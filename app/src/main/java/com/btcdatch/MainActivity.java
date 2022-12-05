package com.btcdatch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothCodecStatus;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.btcdatch.tools.BluetoothA2dpTool;
import com.btcdatch.tools.BluetoothCodecConfigTool;
import com.btcdatch.tools.StorageService;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private final static String ITEM_NAME = "ITEM_NAME";
    private final static String ITEM_DATA = "ITEM_DATA";
    private final static String TAG = MainActivity.class.getName();

    private class Menu {
        protected final static int DEVICE = 0;
        protected final static int CODEC = 1;
        protected final static int SAMPLE_RATE = 2;
        protected final static int BIT_PER_SAMPLE = 3;
        protected final static int CHANNEL_MODE = 4;
    }

    private MainBCastReciver reciver = new MainBCastReciver();

    private String[] codec_names;
    private String[] sample_rate_names;
    private String[] bits_per_sample_names;
    private String[] channel_mode_names;

    private Button set_bt;
    private ProgressBar bar;
    private TextView dnc;
    private ListView menu;

    private BluetoothDevice bdevice;
    private BluetoothCodecStatus bstatus;
    private BluetoothCodecConfig bconfig;
    private BluetoothCodecConfig bconfig_a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initResurces();
        set_bt = findViewById(R.id.set_but);
        bar = findViewById(R.id.bar);
        dnc = findViewById(R.id.device_no_connectet);
        menu = findViewById(R.id.menu);
        set_bt.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, R.string.trying_to_set, Toast.LENGTH_SHORT).show();
            BluetoothA2dpTool.tool.setCodecConfig(this, bdevice, bconfig);
            new StorageService(this).putBluetoothCodecConfig(bdevice.getAddress(), bconfig).save();
        });
        menu.setOnItemClickListener(new MenuClickListener());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(reciver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        update(0);
        BluetoothA2dpTool.tool.getState(this, state -> update(state));
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction("android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED");
        registerReceiver(reciver, filter);
    }

    private  void initResurces() {
        codec_names = getResources().getStringArray(R.array.codec_names);
        sample_rate_names = getResources().getStringArray(R.array.sample_rate_names);
        bits_per_sample_names = getResources().getStringArray(R.array.bits_per_sample_names);
        channel_mode_names = getResources().getStringArray(R.array.channel_mode_names);
    }

    private void updateMenu(){
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        HashMap<String, String> item;
        item = new HashMap() {{
            put(ITEM_NAME, getString(R.string.device));
            put(ITEM_DATA, bdevice.getName());
        }};
        arrayList.add(item);

        item = new HashMap() {{
            put(ITEM_NAME, getString(R.string.codec));
            put(ITEM_DATA, bconfig.getCodecType() > 5 ? getString(R.string.unknown) : codec_names[bconfig.getCodecType()]);
        }};
        arrayList.add(item);

        item = new HashMap() {{
            put(ITEM_NAME, getString(R.string.sample_rate));
            put(ITEM_DATA, sample_rate_names[bitPosition(bconfig.getSampleRate())]);
        }};
        arrayList.add(item);

        item = new HashMap() {{
            put(ITEM_NAME, getString(R.string.bits_per_sample));
            put(ITEM_DATA, bits_per_sample_names[bitPosition(bconfig.getBitsPerSample())]);
        }};
        arrayList.add(item);

        item = new HashMap() {{
            put(ITEM_NAME, getString(R.string.channel_mode));
            put(ITEM_DATA, channel_mode_names[bitPosition(bconfig.getChannelMode())]);
        }};
        arrayList.add(item);
        SimpleAdapter adapter = new SimpleAdapter(this, arrayList, android.R.layout.simple_list_item_2,
                new String[]{ITEM_NAME, ITEM_DATA},
                new int[]{android.R.id.text1, android.R.id.text2});
        menu.setAdapter(adapter);
    }

    private int bitPosition(int x){
        if(x < 1)
            return 0;
        return (int) Math.round(Math.log(x) / Math.log(2)) + 1;
    }

    private void load(){
        BluetoothA2dpTool.tool.getCodecStatus(this,(device, status) -> {
            bdevice = device;
            bstatus = status;
            bconfig = status.getCodecConfig();
            bconfig_a = BluetoothA2dpTool.tool.getCodecsSelectableCapabilities(status)[0];
            BluetoothCodecConfig config = new StorageService(this).getBluetoothCodecConfig(device.getAddress());
            if(!bconfig.equals(config)) {
                BluetoothA2dpTool.tool.setCodecConfig(this, device, config);
            }
            updateMenu();
        });

    }

    private void hideAll(){
        set_bt.setVisibility(View.GONE);
        bar.setVisibility(View.GONE);
        dnc.setVisibility(View.GONE);
        menu.setVisibility(View.GONE);
    }

    private void update(int state){
        hideAll();
        switch (state){
            case BluetoothA2dp.STATE_DISCONNECTED:
                dnc.setVisibility(View.VISIBLE);
                break;
            case BluetoothA2dp.STATE_CONNECTING:
                bar.setVisibility(View.VISIBLE);
                break;
            case BluetoothA2dp.STATE_CONNECTED:
                load();
                menu.setVisibility(View.VISIBLE);
                set_bt.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void makeDialog(int title ,String[] parametrs, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setItems(parametrs, listener)
                .show();
    }

    private class MenuClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String names[];
            switch (position) {
                case Menu.CODEC:
                    BluetoothCodecConfig[] configs = BluetoothA2dpTool.tool.getCodecsSelectableCapabilities(bstatus);
                    names = BluetoothCodecConfigTool.tool.getCodecsNames(configs, codec_names);
                    MainActivity.this.makeDialog(R.string.codec, names, (dialog, c) -> {
                        bconfig_a = configs[c];
                        bconfig = BluetoothCodecConfigTool.tool.getBluetoothCodecConfig(
                                bconfig_a,
                                configs[c].getCodecType(),
                                bconfig.getCodecPriority() + 1000);
                        MainActivity.this.updateMenu();
                    });
                    break;
                case Menu.SAMPLE_RATE:
                    names = BluetoothCodecConfigTool.tool.avalibleConfiguratuinsNames(bconfig_a.getSampleRate(), sample_rate_names);
                    MainActivity.this.makeDialog(R.string.sample_rate, names, (dialog, c) -> {
                        bconfig = BluetoothCodecConfigTool.tool.setSampleRate(
                                bconfig,
                                BluetoothCodecConfigTool.tool.avalibleConfiguratuins(bconfig_a.getSampleRate())[c]);
                        MainActivity.this.updateMenu();
                    });
                    break;
                case Menu.BIT_PER_SAMPLE:
                    names = BluetoothCodecConfigTool.tool.avalibleConfiguratuinsNames(bconfig_a.getBitsPerSample(), bits_per_sample_names);
                    MainActivity.this.makeDialog(R.string.bits_per_sample, names, (dialog, c) -> {
                        bconfig = BluetoothCodecConfigTool.tool.setBitsPerSample(
                                bconfig,
                                BluetoothCodecConfigTool.tool.avalibleConfiguratuins(bconfig_a.getBitsPerSample())[c]);
                        MainActivity.this.updateMenu();
                    });
                    break;
                case Menu.CHANNEL_MODE:
                    names = BluetoothCodecConfigTool.tool.avalibleConfiguratuinsNames(bconfig_a.getChannelMode(), channel_mode_names);
                    MainActivity.this.makeDialog(R.string.channel_mode, names, (dialog, c) -> {
                        bconfig = BluetoothCodecConfigTool.tool.setChannelMode(
                                bconfig,
                                BluetoothCodecConfigTool.tool.avalibleConfiguratuins(bconfig_a.getChannelMode())[c]);
                        MainActivity.this.updateMenu();
                    });
                    break;

            }
        }
    }

    private class MainBCastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothCodecStatus status = intent.getParcelableExtra(BluetoothCodecStatus.EXTRA_CODEC_STATUS);
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(status != null){
                if(!status.equals(bstatus) && device != null && device.equals(bdevice)) {
                    bstatus = status;
                    bconfig = status.getCodecConfig();
                    update(BluetoothA2dp.STATE_CONNECTED);
                    Toast.makeText(MainActivity.this, R.string.codec_setted, Toast.LENGTH_LONG).show();
                }
            } else {
                int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0);
                int blevel = BluetoothA2dpTool.tool.getBluetoothDeviceBatteryLevel(device);
                if (blevel == -1)
                    state = 0;
                update(state);
            }

        }
    }
}