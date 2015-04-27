package com.eobr;

import android.app.Service;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by byung on 4/17/15.
 */
public class BluetoothChecker {
    public static final String BLUETOOTH_CONNECTION = "BLUETOOTH_CONNECTION";
    private Service service;
    private BluetoothA2dp mBluetoothA2dp;
    boolean connected;
    private BluetoothAdapter mBluetoothAdapter;

    public BluetoothChecker(Service service) {
        this.service = service;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connected = false;
        //c = new ConnectionOnBluetooth();
    }

    /**
     * Start listening A2DP profile.
     */
    public void runBluetoothChecker() {
        mBluetoothAdapter.getProfileProxy(service.getBaseContext(), mProfileListener, BluetoothProfile.A2DP);
    }

    /**
     * Listener for A2DP profile.
     */
    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.A2DP) {
                mBluetoothA2dp = (BluetoothA2dp) proxy;
                System.out.println("OnServiceConnected!");
             //   c.start();
                mCountDownTimer.start();
            }
        }
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.A2DP) {
                mBluetoothA2dp = null;
                mCountDownTimer.cancel();
//                c.interrupt();
            }
        }
    };

    /**
     * Thread that checks bluetooth connection infinitely.
     */
//    private ConnectionOnBluetooth c;
//    private class ConnectionOnBluetooth extends Thread {
//        @Override
//        public void run() {
//            System.out.println("ConnectionChecker thread running!");
//            while(true) {
//                if(mBluetoothA2dp != null) {
//                    if (mBluetoothA2dp.getConnectedDevices().size() != 0) {
//                        if(!connected) {
//                            Intent i = new Intent(BLUETOOTH_CONNECTION);
//                            i.putExtra("Connection", 0);
//                            LocalBroadcastManager.getInstance(service).sendBroadcast(i);
//                            connected = true;
//                        }
//                    } else {
//                        if(connected) {
//                            System.out.println("Turning off");
//                            Intent i = new Intent(BLUETOOTH_CONNECTION);
//                            i.putExtra("Connection", 1);
//                            LocalBroadcastManager.getInstance(service).sendBroadcast(i);
//                            connected= false;
//                        }
//                    }
//                }
//            }
//        }
//    }
    private CountDownTimer mCountDownTimer = new CountDownTimer(1000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if(mBluetoothA2dp != null) {
                if (mBluetoothA2dp.getConnectedDevices().size() != 0) {
                    if(!connected) {
                        Intent i = new Intent(BLUETOOTH_CONNECTION);
                        i.putExtra("Connection", 0);
                        LocalBroadcastManager.getInstance(service).sendBroadcast(i);
                        connected = true;
                    }
                } else {
                    if(connected) {
                        System.out.println("Turning off");
                        Intent i = new Intent(BLUETOOTH_CONNECTION);
                        i.putExtra("Connection", 1);
                        LocalBroadcastManager.getInstance(service).sendBroadcast(i);
                        connected= false;
                    }
                }
            }
            mCountDownTimer.start();
        }
    };

    /**
     * Anything that must be turned off when this class is removed.
     */
    public void stopBluetoothChecker() {
        //c.interrupt();
        mCountDownTimer.cancel();
        mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, mBluetoothA2dp);
    }

    /**
     * Below code is used for debugging purpose.
     */
//    public void debugging1() {
//        if(!connected) {
//            Intent i = new Intent(BLUETOOTH_CONNECTION);
//            i.putExtra("Connection", 0);
//            LocalBroadcastManager.getInstance(service).sendBroadcast(i);
//            connected = true;
//            cd.start();
//        }
//    }

//    private CountDownTimer cd = new CountDownTimer(5000,1000) {
//        @Override
//        public void onTick(long millisUntilFinished) {
//            System.out.println("tickTick");
//        }
//
//        @Override
//        public void onFinish() {
//                                        Intent i = new Intent(BLUETOOTH_CONNECTION);
//                            i.putExtra("Connection", 1);
//                            LocalBroadcastManager.getInstance(service).sendBroadcast(i);
//        }
//    };

}
