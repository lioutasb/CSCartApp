package gr.plushost.prototypeapp.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.activities.MainActivity;

/**
 * Created by user on 6/8/2015.
 */
public class NoNetworkHandler {
    AlertDialog dialog = null;
    Context context = null;

    public NoNetworkHandler(final Context con){
        context = con;
        dialog = new AlertDialog.Builder(con)
                .setTitle(con.getResources().getString(R.string.not_internet_title))
                .setMessage(con.getResources().getString(R.string.not_internet_msg))
                .setCancelable(false)
                .setIcon(R.drawable.sing)
                .setPositiveButton(con.getResources().getString(R.string.not_internet_ok_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(con.getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("EXIT", true);
                        con.startActivity(intent);
                        //android.os.Process.killProcess(android.os.Process.myPid());
                        //System.exit(0);
                    }
                })
                .setNegativeButton(con.getResources().getString(R.string.not_internet_settings_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ((Activity) con).startActivityForResult(intent, 0);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                }).create();
    }

    public boolean showDialog(){
        if(!isNetworkAvailable(context)){
            if(dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
            return false;
        }
        else{
            return true;
        }
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
