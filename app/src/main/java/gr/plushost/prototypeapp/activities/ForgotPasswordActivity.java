package gr.plushost.prototypeapp.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;

/**
 * Created by billiout on 30/3/2015.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    Activity act = this;
    NoNetworkHandler noNetworkHandler;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        noNetworkHandler = new NoNetworkHandler(this);
        setContentView(R.layout.activity_pass_recovery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.pass_recovery_screen_title));

        final BootstrapEditText email = (BootstrapEditText) findViewById(R.id.email);

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isSpaceChar(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        email.setFilters(new InputFilter[]{filter});

        if (!act.getSharedPreferences("ShopPrefs", 0).getString("user_email", "").equals("")) {
            email.setText(act.getSharedPreferences("ShopPrefs", 0).getString("user_email", ""));
        }

        act.findViewById(R.id.btnRecoveryPass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(noNetworkHandler.showDialog())
                    new RecoverPassword().execute(email.getText().toString());
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_pass_recovery);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    class RecoverPassword extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String response = null;
            try {
                response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_user_password_recovery), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), URLEncoder.encode(strings[0], "UTF-8")), ServiceHandler.GET);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("code").equals("0x0000")) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                SuperToast.create(act, getResources().getString(R.string.pass_recovery_success_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.GREEN, SuperToast.Animations.POPUP)).show();
                act.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            } else {
                SuperToast.create(act, getResources().getString(R.string.pass_recovery_fail_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
            }
        }
    }
}
