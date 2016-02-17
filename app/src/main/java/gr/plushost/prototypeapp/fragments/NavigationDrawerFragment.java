package gr.plushost.prototypeapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.activities.CategoriesActivity;
import gr.plushost.prototypeapp.activities.CustomerOrdersActivity;
import gr.plushost.prototypeapp.activities.ForgotPasswordActivity;
import gr.plushost.prototypeapp.activities.LoginActivity;
import gr.plushost.prototypeapp.activities.MainActivity;
import gr.plushost.prototypeapp.activities.ProductActivity;
import gr.plushost.prototypeapp.activities.ProductsActivity;
import gr.plushost.prototypeapp.activities.ProfileActivity;
import gr.plushost.prototypeapp.activities.SettingsActivity;
import gr.plushost.prototypeapp.activities.ShoppingCartActivity;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.PersistentCookieStore;
import gr.plushost.prototypeapp.network.ServiceHandler;

/**
 * Created by billiout on 17/2/2015.
 */
public class NavigationDrawerFragment extends Fragment {
    Activity act;
    RelativeLayout accountLogout;
    RelativeLayout accountLoggedin;
    DrawerLayout drawerLayout;
    View view;
    BootstrapButton btnCart;
    NoNetworkHandler noNetworkHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_nav_drawer, container, false);
        act = getActivity();
        noNetworkHandler = new NoNetworkHandler(act);

        final BootstrapEditText email = (BootstrapEditText) view.findViewById(R.id.editTextEmail);

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

        TextView tmpText = (TextView) view.findViewById(R.id.txtNewsTitle);
        Typeface type = Typeface.createFromAsset(act.getAssets(), "fonts/mistral.ttf");
        tmpText.setTypeface(type, Typeface.BOLD);

        tmpText = (TextView) view.findViewById(R.id.txtAccountTitle);
        type = Typeface.createFromAsset(act.getAssets(), "fonts/mistral.ttf");
        tmpText.setTypeface(type, Typeface.BOLD);

        tmpText = (TextView) view.findViewById(R.id.txtCartTitle);
        type = Typeface.createFromAsset(act.getAssets(), "fonts/mistral.ttf");
        tmpText.setTypeface(type, Typeface.BOLD);

        tmpText = (TextView) view.findViewById(R.id.txtAccountTitle2);
        type = Typeface.createFromAsset(act.getAssets(), "fonts/mistral.ttf");
        tmpText.setTypeface(type, Typeface.BOLD);

        accountLogout = (RelativeLayout) view.findViewById(R.id.accountLay);
        accountLoggedin = (RelativeLayout) view.findViewById(R.id.accountLayLogged);
        btnCart = (BootstrapButton) view.findViewById(R.id.swipeCart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
                if(getActivity().getSharedPreferences("ShopPrefs", 0).getBoolean("is_connected", false)){
                    Intent i = new Intent(act, ShoppingCartActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else{
                    openLogin(view);
                }
            }
        });

        BootstrapButton btnLogin = (BootstrapButton) view.findViewById(R.id.sundesiBtn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin(v);
            }
        });

        BootstrapButton btnRegister = (BootstrapButton) view.findViewById(R.id.newAccountBtn);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister(v);
            }
        });

        if(act.getSharedPreferences("ShopPrefs", 0).getBoolean("is_connected", false)){
            accountLoggedin.setVisibility(View.VISIBLE);
            accountLogout.setVisibility(View.GONE);
            if(!act.getSharedPreferences("ShopPrefs", 0).getString("user_name", "").trim().equals("")) {
                ((TextView) view.findViewById(R.id.userName)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.email)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.userName)).setText(act.getSharedPreferences("ShopPrefs", 0).getString("user_name", ""));
                ((TextView) view.findViewById(R.id.email)).setText(act.getSharedPreferences("ShopPrefs", 0).getString("user_email", ""));
            }
            else {
                ((TextView) view.findViewById(R.id.userName)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.email)).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.userName)).setText(act.getSharedPreferences("ShopPrefs", 0).getString("user_email", ""));
            }
        }
        else {
            accountLoggedin.setVisibility(View.GONE);
            accountLogout.setVisibility(View.VISIBLE);
        }

        if(StoreApplication.getCartCount() > 0){
            if(StoreApplication.getCartCount() == 1)
                btnCart.setText(String.format(getResources().getString(R.string.btn_cart_text_product),StoreApplication.getCartCount()));
            else
                btnCart.setText(String.format(getResources().getString(R.string.btn_cart_text_products),StoreApplication.getCartCount()));
        }
        else {
            btnCart.setText(act.getResources().getString(R.string.btn_cart_text_empty));
        }

        BootstrapButton btnLogout = (BootstrapButton) view.findViewById(R.id.userLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(act.getSharedPreferences("ShopPrefs", 0).getBoolean("is_connected", false)) {
                    drawerLayout.closeDrawers();
                    if(noNetworkHandler.showDialog())
                        new LogoutSession().execute();
                }
                else {
                    SuperToast.create(act, getResources().getString(R.string.btn_logout_fail), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                }
            }
        });

        BootstrapButton btnProfile = (BootstrapButton) view.findViewById(R.id.userProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent i = new Intent(act, ProfileActivity.class);
                startActivity(i);
                act.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        BootstrapButton btnOrders = (BootstrapButton) view.findViewById(R.id.userOrders);
        btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent i = new Intent(act, CustomerOrdersActivity.class);
                startActivity(i);
                act.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        update();

        view.findViewById(R.id.newPassBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
                Intent intent = new Intent(act, ForgotPasswordActivity.class);
                act.startActivity(intent);
                act.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        view.findViewById(R.id.btnNewsletterSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BootstrapEditText editTextEmail = (BootstrapEditText) view.findViewById(R.id.editTextEmail);
                if(!editTextEmail.getText().toString().trim().equals("") || isValidEmail(editTextEmail.getText().toString().trim())){
                    if(noNetworkHandler.showDialog())
                        new NewsletterSignUp().execute(editTextEmail.getText().toString().trim());
                }
                else {
                    SuperToast.create(act, getResources().getString(R.string.newsletter_missing_email_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                }
            }
        });

        view.findViewById(R.id.homeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(getActivity() instanceof MainActivity)) {
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
                else{
                    drawerLayout.closeDrawers();
                }
            }
        });

        return view;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout){
        this.drawerLayout = drawerLayout;
    }

    public void update(){
        if(noNetworkHandler.showDialog())
            new GetCartCount().execute();
        if(getActivity().getSharedPreferences("ShopPrefs", 0).getBoolean("is_connected", false)){
            accountLoggedin.setVisibility(View.VISIBLE);
            accountLogout.setVisibility(View.GONE);
            if(!act.getSharedPreferences("ShopPrefs", 0).getString("user_name", "").trim().equals("")) {
                ((TextView) view.findViewById(R.id.userName)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.email)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.userName)).setText(act.getSharedPreferences("ShopPrefs", 0).getString("user_name", ""));
                ((TextView) view.findViewById(R.id.email)).setText(act.getSharedPreferences("ShopPrefs", 0).getString("user_email", ""));
            }
            else {
                ((TextView) view.findViewById(R.id.userName)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.email)).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.userName)).setText(act.getSharedPreferences("ShopPrefs", 0).getString("user_email", ""));
            }
        }
        else {
            accountLoggedin.setVisibility(View.GONE);
            accountLogout.setVisibility(View.VISIBLE);
        }
    }

    public void openLogin(View view){
        drawerLayout.closeDrawers();
        Intent i = new Intent(act, LoginActivity.class);
        i.putExtra("page", 0);
        startActivity(i);
    }

    public void openRegister(View view){
        drawerLayout.closeDrawers();
        Intent i = new Intent(act, LoginActivity.class);
        i.putExtra("page", 1);
        startActivity(i);
    }

    public boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    class NewsletterSignUp extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_newsletter_signup), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), URLEncoder.encode(params[0], "UTF-8")), ServiceHandler.GET);

                JSONObject jsonObject = new JSONObject(response);
                return jsonObject.getString("code").equals("0x0000");
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
            drawerLayout.closeDrawers();
            if(result){
                SuperToast.create(act, getResources().getString(R.string.newsletter_success_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
            }
            else {
                SuperToast.create(act, getResources().getString(R.string.btn_logout_fail), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
            }
        }
    }

    class GetCartCount extends AsyncTask<Void, Void, Integer>{
        @Override
        protected void onPreExecute(){
            if(isAdded() && getActivity() != null) {
                int result = StoreApplication.getCartCount();
                if (result > 0) {
                    if (result == 1)
                        btnCart.setText(String.format(getResources().getString(R.string.btn_cart_text_product), StoreApplication.getCartCount()));
                    else
                        btnCart.setText(String.format(getResources().getString(R.string.btn_cart_text_products), StoreApplication.getCartCount()));
                } else {
                    btnCart.setText(act.getResources().getString(R.string.btn_cart_text_empty));
                }
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if(isAdded() && getActivity() != null) {
                String response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_cart_count), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", "")), ServiceHandler.GET);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("code").equals("0x0000")) {
                        return jsonObject.getJSONObject("info").getInt("cart_items_count");
                    }
                    return 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result){
            if(isAdded() && getActivity() != null) {
                StoreApplication.setCartCount(result);
                if (result > 0) {
                    if (result == 1)
                        btnCart.setText(String.format(getResources().getString(R.string.btn_cart_text_product), StoreApplication.getCartCount()));
                    else
                        btnCart.setText(String.format(getResources().getString(R.string.btn_cart_text_products), StoreApplication.getCartCount()));
                } else {
                    btnCart.setText(act.getResources().getString(R.string.btn_cart_text_empty));
                }
            }
        }
    }

    class LogoutSession extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            ServiceHandler sh = StoreApplication.getServiceHandler(act);

            String response = sh.makeServiceCall(act, true, String.format(getResources().getString(R.string.url_logout_user), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", "")), ServiceHandler.GET);


            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("code").equals("0x0000")){
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result){
                act.getSharedPreferences("ShopPrefs", 0).edit().putBoolean("is_connected", false).apply();
                SuperToast.create(act, getResources().getString(R.string.btn_logout_success), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                if(noNetworkHandler.showDialog()) {
                    if (act instanceof MainActivity) {
                        ((MainActivity) act).new GetCartCount().execute();
                    } else if (act instanceof CategoriesActivity) {
                        ((CategoriesActivity) act).new GetCartCount().execute();
                    } else if (act instanceof ProductsActivity) {
                        ((ProductsActivity) act).new GetCartCount().execute();
                    } else if (act instanceof ProductActivity) {
                        ((ProductActivity) act).new GetCartCount().execute();
                    }
                }
            }
            else {
                SuperToast.create(act, getResources().getString(R.string.btn_logout_fail), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
            }
        }
    }
}
