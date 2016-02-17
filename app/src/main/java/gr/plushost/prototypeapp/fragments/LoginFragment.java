package gr.plushost.prototypeapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.activities.ForgotPasswordActivity;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;
import gr.plushost.prototypeapp.viewanimations.Techniques;
import gr.plushost.prototypeapp.viewanimations.YoYo;

/**
 * Created by billiout on 5/3/2015.
 */
public class LoginFragment extends Fragment {
    Activity act;
    View rootView;
    NoNetworkHandler noNetworkHandler;
    BootstrapButton btnLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        act = getActivity();
        noNetworkHandler = new NoNetworkHandler(act);
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        setHasOptionsMenu(true);

        final BootstrapEditText email = (BootstrapEditText) rootView.findViewById(R.id.email);
        final BootstrapEditText password = (BootstrapEditText) rootView.findViewById(R.id.password);
        btnLogin = (BootstrapButton) rootView.findViewById(R.id.btnLogin);
        CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.showPass);

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
        password.setFilters(new InputFilter[]{filter});

        if(!act.getSharedPreferences("ShopPrefs", 0).getString("user_email", "").equals("")){
            email.setText(act.getSharedPreferences("ShopPrefs", 0).getString("user_email", ""));
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string_email = email.getText().toString();
                String string_password = password.getText().toString();
                if(isValidEmail(string_email) && !string_password.trim().equals("")){
                    if(noNetworkHandler.showDialog())
                        new CheckIfUserExists(string_email, string_password).execute();
                }
                else if(!isValidEmail(string_email)){
                    SuperToast.create(act, getResources().getString(R.string.login_invalid_email_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.BLUE, SuperToast.Animations.POPUP)).show();
                    YoYo.with(Techniques.Shake).duration(700).playOn(rootView.findViewById(R.id.edittexts_rel));
                }
                else {
                    SuperToast.create(act, getResources().getString(R.string.login_password_empty_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.BLUE, SuperToast.Animations.POPUP)).show();
                    YoYo.with(Techniques.Shake).duration(700).playOn(rootView.findViewById(R.id.edittexts_rel));
                }
            }
        });

        rootView.findViewById(R.id.passRecovery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, ForgotPasswordActivity.class);
                act.startActivity(intent);
                act.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        return rootView;
    }

    class CheckIfUserExists extends AsyncTask<Void, Void, Boolean>{
        String email;
        String password;
        public CheckIfUserExists(String email, String password){
            this.email = email;
            this.password = password;
            btnLogin.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ServiceHandler sh = StoreApplication.getServiceHandler(act);

            String response = null;
            try {
                response = sh.makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_check_if_user_exists), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), URLEncoder.encode(email, "UTF-8")), ServiceHandler.GET);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("code").equals("0x0000")){
                    return jsonObject.getJSONObject("info").getBoolean("uname_is_exist");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
           if(result){
               if(noNetworkHandler.showDialog())
                new LoginUser(email, password).execute();
           }
           else {
               btnLogin.setEnabled(true);
               SuperToast.create(act, getResources().getString(R.string.login_fail_msg), SuperToast.Duration.LONG, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
               YoYo.with(Techniques.Shake).duration(700).playOn(rootView.findViewById(R.id.edittexts_rel));
           }
        }
    }

    class LoginUser extends AsyncTask<Void, Void, JSONObject>{
        String email;
        String password;
        public LoginUser(String email, String password){
            this.email = email;
            this.password = password;
            btnLogin.setEnabled(false);
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            ServiceHandler sh = StoreApplication.getServiceHandler(act);

            String response = null;
            try {
                response = sh.makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_login_user), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), URLEncoder.encode(email, "UTF-8"), URLEncoder.encode(password, "UTF-8")), ServiceHandler.GET);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("code").equals("0x0000")){
                    return jsonObject.getJSONObject("info");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response){
            btnLogin.setEnabled(true);
            if(response != null) {
                try {
                    act.getSharedPreferences("ShopPrefs", 0).edit().putBoolean("is_connected", true).apply();
                    act.getSharedPreferences("ShopPrefs", 0).edit().putInt("user_id", response.getInt("user_id")).apply();
                    act.getSharedPreferences("ShopPrefs", 0).edit().putString("user_name", response.getString("user_name")).apply();
                    act.getSharedPreferences("ShopPrefs", 0).edit().putString("user_email", response.getString("email")).apply();
                    SuperToast.create(act, getResources().getString(R.string.login_success_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.GREEN, SuperToast.Animations.POPUP)).show();
                    act.finish();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{
                SuperToast.create(act, getResources().getString(R.string.login_fail_msg), SuperToast.Duration.LONG, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                YoYo.with(Techniques.Shake).duration(700).playOn(rootView.findViewById(R.id.edittexts_rel));
            }
        }
    }

    public boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
