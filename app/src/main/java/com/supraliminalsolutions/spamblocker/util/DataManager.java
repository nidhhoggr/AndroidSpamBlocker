package com.supraliminalsolutions.spamblocker.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DataManager {
    static DataManager instance;
    static SQLiteDatabase db;
    static SQLiteOpenHelper helper;
    static Context con;

    private DataManager() {
        // not accessible
    }

    public static DataManager getInstance(Context con) {
        if (instance == null) {
            instance = new DataManager();
        }
        con = con;
        helper = new DBHelper(con, "TrackerActivity", null, 2);
        db = helper.getWritableDatabase();
        return instance;
    }

    public void updateCallBlackListService(Boolean flag) {
        updateCallService(flag, "black");
    }

    public void updateCallWhiteListService(boolean flag) {
        updateCallService(flag, "white");
    }

    public void updateCallService(boolean flag, String list) {
        if (flag) {
            String[] args = new String[1];
            args[0] = "call";
            db.delete("actservice", "ser = ? ", args);
            ContentValues val = new ContentValues();
            val.put("ser", Constant.CALL_BLOCK_SERVICE);
            val.put("stat", list);
            db.insert("actservice", null, val);
        } else {
            String[] args = new String[1];
            args[0] = Constant.CALL_BLOCK_SERVICE;
            db.delete("actservice", "ser = ? ", args);
        }
    }
    
    public String getActiveCallService() {
        String activeService = "";
        Cursor cur = db.query("actservice", null, "ser='call'", null, null, null, null);
        while (cur.moveToNext()) {
            activeService = cur.getString(1);
        }
        cur.close();
        return activeService;
    }


    public boolean isIncomingBlocked(String incoming) {
        String active_service = "";
        Cursor cur = db.query("actservice", null, "ser='call'", null, null, null, null);
        while (cur.moveToNext()) {
            active_service = cur.getString(1);
        }
        cur.close();
        Boolean whichService = false;
        if (active_service.equals("black")) {
            whichService = isBlackListed(incoming);

        } else if (active_service.equals("white")) {
            whichService = isWhiteListed(incoming);
        }
        return whichService;
    }

    public void blacklistIncoming(String incoming) {
        ContentValues val = new ContentValues();
        val.put("ph_no", incoming.toString());
        val.put("ct", "0");
        db.insert("blklst_call", null, val);
    }

    private Boolean isBlackListed(String incomingNumber) {
        Boolean flag = false;
        Cursor cur = db.rawQuery("Select * from blklst_call",null);
        while (cur.moveToNext()) {
            if (incomingNumber.equals(cur.getString(0))) {
                flag = true;
                break;
            }
            cur.close();
        }
        return flag;
    }


    private Boolean isWhiteListed(String incomingNumber) {
        Boolean flag = true;
        Cursor cur = db.rawQuery("Select * from whitlist_call",null);

        while (cur.moveToNext()) {
            if (incomingNumber.equals(cur.getString(0))) {
                flag = false;
                break;
            }
            cur.close();
        }
        return flag;
    }
}
