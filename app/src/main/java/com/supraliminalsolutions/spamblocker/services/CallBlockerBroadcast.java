package com.supraliminalsolutions.spamblocker.services;

import java.io.IOException;
import java.lang.reflect.Method;


import com.android.internal.telephony.ITelephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.supraliminalsolutions.spamblocker.util.DataManager;
import com.supraliminalsolutions.spamblocker.util.TuringAPI;

import android.util.Log;

import org.json.JSONException;

public class CallBlockerBroadcast extends BroadcastReceiver {


    private TelephonyManager m_telephonyManager;
    private ITelephony m_telephonyService;

    private AudioManager m_audioManager;
    Context context;

    public void onReceive(Context context, Intent intent) {
        this.context = context;
        m_telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = null;
            c = Class.forName(m_telephonyManager.getClass().getName());
            Method m = null;
            m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            m_telephonyService = (ITelephony) m.invoke(m_telephonyManager);
            m_audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            m_telephonyManager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            Toast.makeText(context, incomingNumber, Toast.LENGTH_LONG).show();
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    incomingNumber = incomingNumber.substring(incomingNumber.length() - 10, incomingNumber.length());
                    Log.d("Incoming number:", incomingNumber);
                    boolean isBlacklisted = DataManager.getInstance(context).isIncomingBlocked(incomingNumber);
                    if (isBlacklisted) // if incoming Number need to be blocked
                    {
                        m_audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        m_telephonyService.endCall();
                    }
                    else {
                        boolean isRobotByPhoneNumber = false;
                        try {
                            isRobotByPhoneNumber = TuringAPI.getInstance(context).isRobotByPhoneNumber(incomingNumber);
                        } catch (IOException e) {

                        } catch (JSONException e) {

                        }

                        if (isRobotByPhoneNumber) {
                            DataManager.getInstance(context).blacklistIncoming(incomingNumber);
                            m_audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            m_telephonyService.endCall();
                        }
                    }
                    break;
                default:
                    break;
            }
        }


    }
}
   