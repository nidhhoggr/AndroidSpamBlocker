package com.supraliminalsolutions.spamblocker.activity;

import java.util.ArrayList;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.Toast;

import com.supraliminalsolutions.spamblocker.R;
import com.supraliminalsolutions.spamblocker.util.DBHelper;

public class EditBlackListActivity extends AppCompatActivity {

    ArrayList<String> no;
    ArrayAdapter<String> lis;
    SQLiteDatabase db;
    ListView sp;
    EditText phno;
    String delitem = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.black_list);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Call Blocker");

        sp = findViewById(R.id.listView1);
        phno = findViewById(R.id.editText1);
        SQLiteOpenHelper helper = new DBHelper(this, "TrackerActivity", null, 2);
        db = helper.getWritableDatabase();
        sp.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                delitem = String.valueOf(sp.getItemAtPosition(arg2));
            }
        });

        no = new ArrayList<String>();
        popup();
    }

    public void popup() {
        no.clear();
        try {
            if (db != null) {
                Cursor c = db.query("blklst_call", null, null, null, null, null, "ph_no desc");
                while (c.moveToNext()) {
                    String num = c.getString(0);
                    no.add(num);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, no);
                    sp.setAdapter(adapter);
                }
                c.close();
            } else {

                Toast.makeText(getApplicationContext(), "blank", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.d("Rohit error", e.getMessage() + e.getStackTrace());
            Toast.makeText(getApplicationContext(), "popup : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void delete(View v) {
        try {
            if (!delitem.equals("")) {
                String[] args = new String[1];
                args[0] = delitem;
                db.delete("blklst_call", "ph_no = ? ", args);
                popup();
                Toast.makeText(getApplicationContext(), "Number deleted !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Select Number frist !", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "del : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void add(View v) {
        try {
            if (!phno.getText().equals("")) {
                ContentValues val = new ContentValues();
                val.put("ph_no", phno.getText().toString());
                val.put("ct", "0");
                db.insert("blklst_call", null, val);
                popup();
                Toast.makeText(getApplicationContext(), "New Number Added !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Enter Number frist !", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "add : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

}
