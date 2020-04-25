package com.example.simple_browser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {

    boolean connection;

    public boolean isConnection() {
        return connection;
    }

    public void setConnection(boolean connection) {
        this.connection = connection;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,
                    false);

            if (noConnectivity){

                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
                setConnection(false);
            }
            else{

                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
                setConnection(true);
            }


        }

    }
}
