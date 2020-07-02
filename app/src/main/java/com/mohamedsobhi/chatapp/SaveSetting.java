package com.mohamedsobhi.chatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class SaveSetting {

    Context context;
    SharedPreferences sharedPreferences;
    public static final String MyPreferences="MyPref";


    public SaveSetting(Context context) {
        this.context = context;
        sharedPreferences =context.getSharedPreferences(MyPreferences,Context.MODE_PRIVATE);

    }

    public void saveData(String key,String value){

        try {

            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putString(key,String.valueOf(value));

            editor.apply();

        }catch (Exception e)
        {
            Log.e("saveData error",e.getMessage());
        }

    }

    public String loadData(String key)
    {

        String userId =sharedPreferences.getString(key,"empty");

        return userId;

    }
}
