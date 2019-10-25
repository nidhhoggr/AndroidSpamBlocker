package com.supraliminalsolutions.spamblocker.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.supraliminalsolutions.spamblocker.R;
import com.supraliminalsolutions.spamblocker.util.DataManager;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.MODIFY_PHONE_STATE;
import static android.Manifest.permission.INTERNET;



public class MainActivity extends AppCompatActivity {
    /**
     * Called when the activity is first created.
     */

    private static final int PERMISSION_REQUEST_CODE = 200;


    ToggleButton toggle_black_list, toggle_white_list;
    private static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        requestPermission();
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

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_PHONE_STATE, MODIFY_PHONE_STATE, INTERNET}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean denied = false;
                    for (int i = 0; i < permissions.length; i++) {
                        String permission = permissions[i];
                        int grantResult = grantResults[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {

                            denied = true;
                            break;
                        }
                    }

                    if (!denied)
                        Toast.makeText(getApplicationContext(), "Permission Granted.", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplicationContext(), "Permission Denied.", Toast.LENGTH_LONG).show();

                }

                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}