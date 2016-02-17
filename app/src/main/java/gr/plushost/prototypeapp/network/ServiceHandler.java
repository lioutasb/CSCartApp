package gr.plushost.prototypeapp.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.webkit.CookieSyncManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.activities.LoginActivity;
import gr.plushost.prototypeapp.activities.MainActivity;
import gr.plushost.prototypeapp.activities.SplashScreenActivity;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.items.CategoryItem;
import gr.plushost.prototypeapp.network.ExSSLSocketFactory;

/**
 * Created by Billiout on 11/8/2014.
 */
public class ServiceHandler {
    static String responseStr = null;
    public final static int GET = 1;
    public final static int POST = 2;
    //public AlertDialog dialog = null;

    /*public ServiceHandler(final Context con){
        if(dialog == null){
            //((Activity) con).runOnUiThread(new Runnable() {
                //public void run() {
                    //if (!((Activity) con).isFinishing()) {
                        dialog = new AlertDialog.Builder(con)
                                .setTitle(con.getResources().getString(R.string.not_internet_title))
                                .setMessage(con.getResources().getString(R.string.not_internet_msg))
                                .setCancelable(false)
                                .setIcon(R.drawable.sing)
                                .setPositiveButton(con.getResources().getString(R.string.not_internet_ok_btn), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        android.os.Process.killProcess(android.os.Process.myPid());
                                        System.exit(0);
                                    }
                                })
                                .setNegativeButton(con.getResources().getString(R.string.not_internet_settings_btn), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //((Activity) con).startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                        System.exit(1);
                                    }
                                }).create();

                   // }
                //}
           // });
        }
    }*/

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */
    public String makeServiceCall(Context con, boolean needAuthHost, String url, int method) {
        return this.makeServiceCall(con, needAuthHost, url, method, null);
    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    public String makeServiceCall(final Context con, boolean needAuthHost, String url, int method,
                                  List<NameValuePair> params) {
        try {

            /*if(dialog == null){
                ((Activity) con).runOnUiThread(new Runnable() {
                    public void run() {
                        if (!((Activity) con).isFinishing()) {
                            dialog = new AlertDialog.Builder(con)
                                .setTitle(con.getResources().getString(R.string.not_internet_title))
                                .setMessage(con.getResources().getString(R.string.not_internet_msg))
                                .setCancelable(false)
                                .setIcon(R.drawable.sing)
                                .setPositiveButton(con.getResources().getString(R.string.not_internet_ok_btn), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        android.os.Process.killProcess(android.os.Process.myPid());
                                        System.exit(0);
                                    }
                                })
                                .setNegativeButton(con.getResources().getString(R.string.not_internet_settings_btn), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ((Activity) con).startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                        System.exit(1);
                                    }
                                }).create();

                        }
                    }
                });
            }*/



            OkHttpClient httpClient = StoreApplication.getInstance().getHttpClient();
            // http client
            httpClient.setCookieHandler(new CookieManager(new OkHttpPersistentCookieStore(con), CookiePolicy.ACCEPT_ALL));
            //Resources res = con.getResources();
            //httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(auth_username, auth_api_code));
            //new AuthScope(res.getString(R.string.auth_host), AuthScope.ANY_PORT)
            /*HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            if(needAuthHost)
                url = con.getResources().getString(R.string.auth_host) + url;

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }
                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);
                httpResponse = httpClient.execute(httpGet);
            }
            //assert httpResponse != null;
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);*/

            Response response = null;

            if(needAuthHost)
                url = con.getResources().getString(R.string.auth_host) + url + "&app_key=" + URLEncoder.encode(con.getResources().getString(R.string.api_code), "UTF-8");

            if (method == GET) {

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                response = httpClient.newCall(request).execute();
            }
            /*else if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                response = httpClient.newCall(request).execute();
            }*/

            assert response != null;
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            responseStr = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }



        return responseStr;

    }
}
