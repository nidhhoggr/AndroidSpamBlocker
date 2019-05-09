package com.supraliminalsolutions.spamblocker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.supraliminalsolutions.spamblocker.R;
import com.supraliminalsolutions.spamblocker.util.DataManager;

public class CallBlockerActivity extends AppCompatActivity {
    ToggleButton toggle_black_list, toggle_white_list;
    private static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_block);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Call Blocker");

        String active_service = DataManager.getInstance(this).getActiveCallService();
        toggle_black_list = (ToggleButton) findViewById(R.id.toggleButton1);
        toggle_white_list = (ToggleButton) findViewById(R.id.toggleButton2);

        if (active_service.equals("black")) {
            toggle_black_list.setChecked(true);
            toggle_white_list.setChecked(false);
        } else if (active_service.equals("white")) {
            toggle_white_list.setChecked(true);
            toggle_black_list.setChecked(false);
        } else {
            toggle_white_list.setChecked(false);
            toggle_black_list.setChecked(false);
        }

        context = getApplicationContext();
    }

    public void editblk(View v) {
        Intent i = new Intent(this, EditBlackListActivity.class);
        startActivity(i);
    }

    public void whitelist(View v) {
        Intent i = new Intent(this, EditWhiteListActivity.class);
        startActivity(i);
    }

    public void setBlacklist(View v) {
        DataManager.getInstance(context).updateCallBlackListService(toggle_black_list.isChecked());
        if (toggle_black_list.isChecked())
        {
            toggle_white_list.setChecked(false);
            Toast.makeText(context, "Black List is on !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Black List off !", Toast.LENGTH_SHORT).show();
        }
    }

    public void setWhitelist(View v) {
        DataManager.getInstance(context).updateCallWhiteListService(toggle_white_list.isChecked());
        if (toggle_white_list.isChecked()) {
            toggle_black_list.setChecked(false);
            Toast.makeText(context, "white List is on !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "white List off !", Toast.LENGTH_SHORT).show();
        }
    }

}
