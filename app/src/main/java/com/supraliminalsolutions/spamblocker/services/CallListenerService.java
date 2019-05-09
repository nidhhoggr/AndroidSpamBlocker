package com.supraliminalsolutions.spamblocker.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import com.supraliminalsolutions.spamblocker.util.DataManager;
import com.supraliminalsolutions.spamblocker.util.SMSOps;


public class CallListenerService extends Service {

    TelephonyManager m_telephonyManager;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        m_telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if(DataManager.getInstance(this).checkTrackerService()){
           SMSOps.getInstance(this).checkSimChange();
        }
        super.onStart(intent, startId);
    }



}
