package com.example.orderingmanager.Util;

import android.app.Activity;
import android.widget.Toast;

public class Utility {
    public Utility(){/* */}

    public static void showToast(Activity activity, String msg){
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

}
