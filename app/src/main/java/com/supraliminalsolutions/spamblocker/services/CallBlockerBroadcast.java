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

import com.google.i18n.phonenumbers.*;

import android.util.Log;

import org.json.JSONException;

public class CallBlockerBroadcast extends BroadcastReceiver {


    private TelephonyManager m_telephonyManager;
    private ITelephony m_telephonyService;
    private PhoneNumberUtil m_phoneNumberUtil;
    private Phonenumber.PhoneNumber incomingPhoneNumber;

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
        public void onCallStateChanged(int state, String rawIncomingNumber) {
            Toast.makeText(context, rawIncomingNumber, Toast.LENGTH_LONG).show();
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:

                    m_phoneNumberUtil = PhoneNumberUtil.getInstance();

                    String parsedIncomingNumber;

                    try {
                        Phonenumber.PhoneNumber incomingPhoneNumber = m_phoneNumberUtil.parseAndKeepRawInput(rawIncomingNumber.subSequence(0, rawIncomingNumber.length()), "US");
                        if (!m_phoneNumberUtil.isValidNumber(incomingPhoneNumber)) {
                            throw new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, "not a valid phonenumber");
                        }
                        parsedIncomingNumber = m_phoneNumberUtil.format(incomingPhoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164).replace("+","");
                        if (parsedIncomingNumber.length() < 10 || parsedIncomingNumber.length() > 11) {
                            throw new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, "not a valid phonenumber between 10 and 11 chars " + parsedIncomingNumber);
                        }
                    } catch(NumberParseException e) {
                        Log.d("Incoming number error:", e.toString());
                        parsedIncomingNumber = rawIncomingNumber.substring(rawIncomingNumber.length() - 10, rawIncomingNumber.length());
                    }

                    Log.d("Incoming number:", rawIncomingNumber + " - " + parsedIncomingNumber);
                    boolean isBlacklisted = DataManager.getInstance(context).isIncomingBlocked(parsedIncomingNumber);

                    if (isBlacklisted) // if incoming Number need to be blocked
                    {
                        m_audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        m_telephonyService.endCall();
                    }
                    else {
                        boolean isRobotByPhoneNumber = false;
                        try {
                            isRobotByPhoneNumber = TuringAPI.getInstance(context).isRobotByPhoneNumber(rawIncomingNumber, parsedIncomingNumber);
                        } catch (IOException e) {

                        } catch (JSONException e) {

                        }

                        if (isRobotByPhoneNumber) {
                            DataManager.getInstance(context).blacklistIncoming(parsedIncomingNumber);
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
   