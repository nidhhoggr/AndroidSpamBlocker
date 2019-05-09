package com.supraliminalsolutions.spamblocker.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class TuringAPI {

    static Context context;
    static TuringAPI instance;
    static final String url = Constant.SPAM_BLOCKER_API_URL;

    OkHttpClient client;

    public TuringAPI(Context con) {
        client = new OkHttpClient();
        context = con;
    }

    public static TuringAPI getInstance(Context con) {
        if(instance==null) {
            instance = new TuringAPI(con);
        }
        return instance;
    }

    public Boolean isRobotByPhoneNumber(String incoming) throws JSONException, IOException {
        Request request = new Request.Builder()
                .url(url.concat(incoming))
                .build();

        boolean isRobot = false;

        try (Response response = client.newCall(request).execute()) {

            String jsonData = response.body().string();

            try {
                JSONObject Jobject = new JSONObject(jsonData);
                isRobot = Jobject.getInt("score") == 1;
            } catch (JSONException e) {
                throw e;
            }
        }

        return isRobot;
    }

}
