package com.btcdatch.tools;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothCodecStatus;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

@SuppressLint("MissingPermission")
public class BluetoothA2dpTool {

    public final static BluetoothA2dpTool tool = new BluetoothA2dpTool();

    public void getProxy(Context context, A2dpListener pl) {
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context.getApplicationContext(), new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                pl.call((BluetoothA2dp) bluetoothProfile);
            }

            @Override
            public void onServiceDisconnected(int i) {
            }
        }, BluetoothProfile.A2DP);
    }

    public void closeProxy(BluetoothProfile bluetoothProfile) {
        BluetoothAdapter.getDefaultAdapter().closeProfileProxy(BluetoothProfile.A2DP, bluetoothProfile);
    }

    public void setCodecConfig(Context context, BluetoothDevice device, BluetoothCodecConfig config) {
        BluetoothA2dpTool.tool.getProxy(context, proxy -> {
            t(() -> {
                proxy.getClass().getMethod("setCodecConfigPreference", BluetoothDevice.class, BluetoothCodecConfig.class).invoke(proxy, device, config);
            });
            BluetoothA2dpTool.tool.closeProxy(proxy);

        });
    }

    public void getState(Context context, StateListener sl) {
        BluetoothA2dpTool.tool.getProxy(context, proxy -> {
            t(() -> {
                int state = 0;
                if(proxy.getConnectedDevices().size() > 0) {
                    BluetoothDevice device = proxy.getConnectedDevices().get(0);
                    state = proxy.getConnectionState(device);
                }
                sl.call(state);
            });
            BluetoothA2dpTool.tool.closeProxy(proxy);
        });
    }

    public void getCodecStatus(Context context, CodecStatusListener csl) {
        BluetoothA2dpTool.tool.getProxy(context, proxy -> {
            t(() -> {
                BluetoothCodecStatus status = null;
                if(proxy.getConnectedDevices().size() > 0) {
                    BluetoothDevice device = proxy.getConnectedDevices().get(0);
                    status = (BluetoothCodecStatus) proxy.getClass().getMethod("getCodecStatus", BluetoothDevice.class).invoke(proxy, device);
                    csl.call(device, status);
                }
            });
            BluetoothA2dpTool.tool.closeProxy(proxy);
        });

    }

    public BluetoothCodecConfig[] getCodecsSelectableCapabilities(BluetoothCodecStatus status){
        final BluetoothCodecConfig[][] config = new BluetoothCodecConfig[1][];
        t(() -> {
            config[0] = (BluetoothCodecConfig[]) status.getClass().getMethod("getCodecsSelectableCapabilities").invoke(status);
        });
        return config[0];
    }

    public int getBluetoothDeviceBatteryLevel(BluetoothDevice device){
        final int[] level = new int[1];
        level[0] = -1;
        t(() -> {

            level[0] = (int) device.getClass().getDeclaredMethod("getBatteryLevel").invoke(device);
        });
        return level[0];
    }

    private void t(TryContainer tc){
        try {
            tc.t();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public BluetoothCodecConfig newBluetoothCodecConfig(int codec, int priority, int sample_rate, int bits_pet_sample, int channel_mode){
        final BluetoothCodecConfig[] config = new BluetoothCodecConfig[1];
        t(() -> {
            config[0] = BluetoothCodecConfig.class.getConstructor(int.class, int.class, int.class, int.class, int.class, long.class, long.class, long.class, long.class)
                    .newInstance(
                            codec,
                            priority,
                            sample_rate,
                            bits_pet_sample,
                            channel_mode,
                            0, 0,
                            0, 0);
        });
        return config[0];
    }

    public interface A2dpListener{
        public void call(BluetoothA2dp proxy);
    }

    public interface CodecStatusListener{
        public void call(BluetoothDevice device, BluetoothCodecStatus status);
    }

    public interface StateListener{
        public void call(int state);
    }

    private interface TryContainer{
        public void t() throws Exception;
    }
}
