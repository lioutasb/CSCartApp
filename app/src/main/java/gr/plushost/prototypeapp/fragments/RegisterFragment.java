package gr.plushost.prototypeapp.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;
import gr.plushost.prototypeapp.viewanimations.Techniques;
import gr.plushost.prototypeapp.viewanimations.YoYo;

/**
 * Created by billiout on 28/3/2015.
 */
public class RegisterFragment extends Fragment {
    Activity act;
    View rootView;
    NoNetworkHandler noNetworkHandler;
    BootstrapButton btnRegister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        act = getActivity();
        noNetworkHandler = new NoNetworkHandler(act);
        rootView = inflater.inflate(R.layout.fragment_register, container, false);
        setHasOptionsMenu(true);

        final BootstrapEditText email = (BootstrapEditText) rootView.findViewById(R.id.email);
        final BootstrapEditText password1 = (BootstrapEditText) rootView.findViewById(R.id.password1);
        final BootstrapEditText password2 = (BootstrapEditText) rootView.findViewById(R.id.password2);
        final BootstrapEditText firstname = (BootstrapEditText) rootView.findViewById(R.id.firstname);
        final BootstrapEditText lastname = (BootstrapEditText) rootView.findViewById(R.id.lastname);
        final BootstrapEditText phone = (BootstrapEditText) rootView.findViewById(R.id.phone);
        btnRegister = (BootstrapButton) rootView.findViewById(R.id.btnRegister);

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
        password1.setFilters(new InputFilter[]{filter});
        password2.setFilters(new InputFilter[]{filter});

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string_email = email.getText().toString();
                String string_password1 = password1.getText().toString();
                String string_password2 = password2.getText().toString();
                String string_firstname = firstname.getText().toString();
                String string_lastname = lastname.getText().toString();
                String string_phone = phone.getText().toString();

                boolean valid = true;
                if(string_email.equals("")){
                    SuperToast.create(act, getResources().getString(R.string.register_empty_email_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                    YoYo.with(Techniques.Shake).duration(700).playOn(rootView.findViewById(R.id.edittexts_rel));
                    return;
                }

                if(string_password1.equals("")){
                    SuperToast.create(act, getResources().getString(R.string.register_empty_password1_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                    YoYo.with(Techniques.Shake).duration(700).playOn(rootView.findViewById(R.id.edittexts_rel));
                    return;
                }

                if(string_password2.equals("")){
                    SuperToast.create(act, getResources().getString(R.string.register_empty_password2_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                    YoYo.with(Techniques.Shake).duration(700).playOn(rootView.findViewById(R.id.edittexts_rel));
                    return ;
                }

                if(isValidEmail(string_email)){
                    if(string_password1.equals(string_password2)){
                        if(isValidPasswordLength(string_password1)){
                            if(isValidPasswordCharacters(string_password1)){
                                if(noNetworkHandler.showDialog())
                                    new CheckIfUserExists(string_email, string_password1, string_firstname, string_lastname, string_phone).execute();
                            }
                            else {
                                SuperToast.create(act, getResources().getString(R.string.register_password_invalid_chars_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                                YoYo.with(Techniques.Shake).duration(700).playOn(rootView.findViewById(R.id.edittexts_rel));
                            }
                        }
                        else {
                            SuperToast.create(act, getResources().getString(R.string.register_password_invalid_length_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                            YoYo.with(Techniques.Shake).duration(700).playOn(rootView.findViewById(R.id.edittexts_rel));
                        }
                    }
                    else {
                        SuperToast.create(act, getResources().getString(R.string.register_passwords_not_matching_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                        YoYo.with(Techniques.Shake).duration(700).playOn(rootView.findViewById(R.id.edittexts_rel));
                    }
                }
                else {
                    SuperToast.create(act, getResources().getString(R.string.register_email_invalid_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                    YoYo.with(Techniques.Shake).duration(700).playOn(rootView.findViewById(R.id.edittexts_rel));
                }
            }
        });

        return rootView;
    }

    class CheckIfUserExists extends AsyncTask<Void, Void, Boolean>{
        String email;
        String password;
        String firstname;
        String lastname;
        String phone;

        public CheckIfUserExists(String email, String password, String firstname, String lastname, String phone){
            this.email = email;
            this.password = password;
            this.firstname = firstname;
            this.lastname = lastname;
            this.phone = phone;
            btnRegister.setEnabled(false);
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
                btnRegister.setEnabled(true);
                SuperToast.create(act, getResources().getString(R.string.register_email_already_used_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                YoYo.with(Techniques.Shake).duration(700).playOn(rootView.findViewById(R.id.edittexts_rel));
            }
            else {
                if(noNetworkHandler.showDialog())
                    new RegisterUser(email, password, firstname, lastname, phone).execute();
            }
        }
    }

    class RegisterUser extends AsyncTask<Void, Void, JSONObject> {
        String email;
        String password;
        String firstname;
        String lastname;
        String phone;

        public RegisterUser(String email, String password, String firstname, String lastname, String phone){
            this.email = email;
            this.password = password;
            this.firstname = firstname;
            this.lastname = lastname;
            this.phone = phone;
            btnRegister.setEnabled(false);
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            ServiceHandler sh = StoreApplication.getServiceHandler(act);

            String response = null;
            try {
                response = sh.makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_register_user), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), URLEncoder.encode(email, "UTF-8"), URLEncoder.encode(password, "UTF-8"), URLEncoder.encode(firstname, "UTF-8"), URLEncoder.encode(lastname, "UTF-8"), URLEncoder.encode(phone, "UTF-8")), ServiceHandler.GET);
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
            btnRegister.setEnabled(true);
            if(response != null) {
                try {
                    act.getSharedPreferences("ShopPrefs", 0).edit().putBoolean("is_connected", true).apply();
                    act.getSharedPreferences("ShopPrefs", 0).edit().putInt("user_id", response.getInt("user_id")).apply();
                    act.getSharedPreferences("ShopPrefs", 0).edit().putString("user_name", response.getString("user_name")).apply();
                    act.getSharedPreferences("ShopPrefs", 0).edit().putString("user_email", response.getString("email")).apply();
                    SuperToast.create(act, getResources().getString(R.string.register_success_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.GREEN, SuperToast.Animations.POPUP)).show();
                    act.finish();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{
                SuperToast.create(act, String.format(getResources().getString(R.string.register_fail_msg)), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                YoYo.with(Techniques.Shake).duration(700).playOn(rootView.findViewById(R.id.edittexts_rel));
            }
        }
    }

    public boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean isValidPasswordLength(String str){
        return str.length() >= 6;
    }

    public boolean isValidPasswordCharacters(String str){
        System.out.println(str);
        return !str.matches(".*[^A-Za-z0-9!+@#$%^&*()_].*");
    }
}
