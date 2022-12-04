package com.btcdatch;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.btcdatch.tools.BluetoothA2dpTool;
import com.btcdatch.tools.StorageService;

public class BluetoothDeciveConnectionReciver extends BroadcastReceiver {

    private final static String TAG = BluetoothDeciveConnectionReciver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0);
        if (state == BluetoothA2dp.STATE_CONNECTED) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            BluetoothCodecConfig config = new StorageService(context).getBluetoothCodecConfig(device.getAddress());
            if(config != null)
                BluetoothA2dpTool.tool.setCodecConfig(context, device, config);

        }
    }
}