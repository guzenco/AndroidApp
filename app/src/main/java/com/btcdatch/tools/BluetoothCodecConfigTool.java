package com.btcdatch.tools;

import android.bluetooth.BluetoothCodecConfig;

public class BluetoothCodecConfigTool {

    public final static BluetoothCodecConfigTool tool = new BluetoothCodecConfigTool();

    public String[] getCodecsNames(BluetoothCodecConfig[] configs, String[] codec_names){
        String[] codecs = new String[configs.length];
        for (int i = 0; i < configs.length; i++) {
            codecs[i] = configs[i].getCodecType() <= 5 ? codec_names[configs[i].getCodecType()] : "Unknown";
        }
        return codecs;
    }

    public String[] avalibleConfiguratuinsNames(int p, String[] n){
        String[] ac = new String[Integer.bitCount(p)];
        int b = 0;
        for (int i = 0; i < ac.length; i++) {
            while ((p & (1 << b)) == 0)
                b++;
            ac[i] = n[++b];
        }
        return ac ;
    }

    public int[] avalibleConfiguratuins(int p){
        int[] ac = new int[Integer.bitCount(p)];
        int b = 0;
        for (int i = 0; i < ac.length; i++) {
            while ((p & (1 << b)) == 0)
                b++;
            ac [i] = ++b;
        }
        return ac ;
    }

    public BluetoothCodecConfig getBluetoothCodecConfig(BluetoothCodecConfig config_a, int codec, int priority){
        BluetoothCodecConfig config = BluetoothA2dpTool.tool.newBluetoothCodecConfig(
                codec,
                priority,
                BluetoothCodecConfigTool.tool.avalibleConfiguratuins(config_a.getSampleRate())[0],
                BluetoothCodecConfigTool.tool.avalibleConfiguratuins(config_a.getBitsPerSample())[0],
                BluetoothCodecConfigTool.tool.avalibleConfiguratuins(config_a.getChannelMode())[0]);
        return config;
    }

    public BluetoothCodecConfig setSampleRate(BluetoothCodecConfig config, int sample_rate){
        BluetoothCodecConfig res = BluetoothA2dpTool.tool.newBluetoothCodecConfig(
                config.getCodecType(),
                config.getCodecPriority(),
                sample_rate,
                config.getBitsPerSample(),
                config.getChannelMode());
        return res;
    }

    public BluetoothCodecConfig setBitsPerSample(BluetoothCodecConfig config, int bits_per_sample){
        BluetoothCodecConfig res = BluetoothA2dpTool.tool.newBluetoothCodecConfig(
                config.getCodecType(),
                config.getCodecPriority(),
                config.getSampleRate(),
                bits_per_sample,
                config.getChannelMode());
        return res;
    }

    public BluetoothCodecConfig setChannelMode(BluetoothCodecConfig config, int channel_mode){
        BluetoothCodecConfig res = BluetoothA2dpTool.tool.newBluetoothCodecConfig(
                config.getCodecType(),
                config.getCodecPriority(),
                config.getSampleRate(),
                config.getBitsPerSample(),
                channel_mode);
        return res;
    }

}
