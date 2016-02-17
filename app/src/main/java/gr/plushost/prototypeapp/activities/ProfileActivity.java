package gr.plushost.prototypeapp.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.exceptionhandlers.StoreExceptionHandler;
import gr.plushost.prototypeapp.fragments.NavigationDrawerFragment;
import gr.plushost.prototypeapp.items.CountryItem;
import gr.plushost.prototypeapp.items.UserAddressItem;
import gr.plushost.prototypeapp.items.ZoneItem;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;
import gr.plushost.prototypeapp.parsers.AddressParser;
import gr.plushost.prototypeapp.viewanimations.Techniques;
import gr.plushost.prototypeapp.viewanimations.YoYo;

/**
 * Created by billiout on 7/3/2015.
 */
public class ProfileActivity extends AppCompatActivity {
    Activity act = this;
    BootstrapEditText addressUsername;
    BootstrapEditText addressUsersername;
    BootstrapEditText addressUserAddress;
    BootstrapEditText addressUserCity;
    BootstrapEditText addressUserTK;
    BootstrapEditText addressUserRegion;
    BootstrapEditText addressUserPhone;
    Spinner spinnerUserCountry;
    Spinner spinnerUserRegion;
    Spinner spinnerUserSex;
    UserAddressItem address;
    boolean isRegionSelected = false;
    BootstrapButton saveChanges;
    NoNetworkHandler noNetworkHandler;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        noNetworkHandler = new NoNetworkHandler(this);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        toolbar.setTitle(getResources().getString(R.string.profile_screen_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addressUsername = (BootstrapEditText) findViewById(R.id.addressUsername);
        addressUsersername = (BootstrapEditText) findViewById(R.id.addressUsersername);
        addressUserAddress = (BootstrapEditText) findViewById(R.id.addressUserAddress);
        addressUserCity = (BootstrapEditText) findViewById(R.id.addressUserCity);
        addressUserTK = (BootstrapEditText) findViewById(R.id.addressUserTK);
        addressUserRegion = (BootstrapEditText) findViewById(R.id.addressUserRegion);
        addressUserPhone = (BootstrapEditText) findViewById(R.id.addressUserPhone);
        spinnerUserRegion = (Spinner) findViewById(R.id.spinnerUserRegion);
        spinnerUserCountry = (Spinner) findViewById(R.id.spinnerUserCountry);
        spinnerUserSex = (Spinner) findViewById(R.id.spinnerUserSex);
        saveChanges = (BootstrapButton) findViewById(R.id.profileSave);

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noNetworkHandler.showDialog())
                    new UpdateAddress().execute();
            }
        });

        if(noNetworkHandler.showDialog())
            new GetUserAddress().execute();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_profile);
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

    class GetUserAddress extends AsyncTask<Void, Void, UserAddressItem> {
        SweetAlertDialog pDialog;
        @Override
        protected void onPreExecute(){
            pDialog = new SweetAlertDialog(act, SweetAlertDialog.PROGRESS_TYPE, false);
            pDialog.setTitleText(act.getResources().getString(R.string.loadingTxt));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setMultiColorProgressBar(act, true);
            act.findViewById(R.id.scrollView).setVisibility(View.GONE);
            act.findViewById(R.id.progressBarRel).setVisibility(View.GONE);
        }


        @Override
        protected UserAddressItem doInBackground(Void... params) {
            ServiceHandler sh = StoreApplication.getServiceHandler(act);

            String response = sh.makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_get_user_address), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", "")), ServiceHandler.GET);

            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("code").equals("0x0000")) {
                    AddressParser parser = new AddressParser();
                    return parser.parse(jsonObject.getJSONObject("info").getJSONArray("addresses").getJSONObject(0));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(UserAddressItem result) {
            act.findViewById(R.id.progressBarRel).setVisibility(View.GONE);
            if(pDialog != null && pDialog.isShowing()){
                pDialog.dismissWithAnimation();
                pDialog.setMultiColorProgressBar(act, false);
            }
            if (result != null) {
                act.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                address = result;

                updateLayout();
            }
            else{
                act.findViewById(R.id.scrollView).setVisibility(View.GONE);
            }
        }
    }

    class UpdateAddress extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            ServiceHandler sh = StoreApplication.getServiceHandler(act);
            try {
                int address_book_id = address.getAddress_book_id();
                String gender = spinnerUserSex.getSelectedItemPosition() != 1 ? "m" : "f";
                String lastname = URLEncoder.encode(addressUsersername.getText().toString().trim(), "UTF-8");
                String firstname = URLEncoder.encode(addressUsername.getText().toString().trim(), "UTF-8");
                String telephone = URLEncoder.encode(addressUserPhone.getText().toString().trim(), "UTF-8");
                String postcode = URLEncoder.encode(addressUserTK.getText().toString().trim(), "UTF-8");
                String city = URLEncoder.encode(addressUserCity.getText().toString().trim(), "UTF-8");
                String address1 = URLEncoder.encode(addressUserAddress.getText().toString().trim(), "UTF-8");
                String address2 = "";
                String country_code = URLEncoder.encode(StoreApplication.getInstance().getCountryItemList().get((int) spinnerUserCountry.getSelectedItemId()).getCountry_id(), "UTF-8");
                HashMap<String, String> map = new HashMap<>();
                for (ZoneItem item : StoreApplication.getInstance().getZoneItemList()) {
                    map.put(item.getZone_name(), item.getZone_code());
                }
                String zone_code = "";
                String state = "";
                if (isRegionSelected)
                    zone_code = URLEncoder.encode(map.get(spinnerUserRegion.getSelectedItem().toString()), "UTF-8");
                else
                    state = URLEncoder.encode(addressUserRegion.getText().toString().trim(), "UTF-8");

                if (addressUsersername.getText().toString().trim().equals("")) {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_lastname_fail_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                            YoYo.with(Techniques.Shake).duration(700).playOn(act.findViewById(R.id.addressUsersername));
                        }
                    });
                    return false;
                }

                if (addressUsername.getText().toString().trim().equals("")) {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_firstname_fail_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                            YoYo.with(Techniques.Shake).duration(700).playOn(act.findViewById(R.id.addressUsername));
                        }
                    });
                    return false;
                }

                if (addressUserPhone.getText().toString().trim().equals("")) {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_phone_fail_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                            YoYo.with(Techniques.Shake).duration(700).playOn(act.findViewById(R.id.addressUserPhone));
                        }
                    });
                    return false;
                }

                if (addressUserTK.getText().toString().trim().equals("")) {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_zipcode_fail_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                            YoYo.with(Techniques.Shake).duration(700).playOn(act.findViewById(R.id.addressUserTK));
                        }
                    });
                    return false;
                }

                if (!addressUserTK.getText().toString().trim().equals("") && !isValidPostalCode(addressUserTK.getText().toString().trim(), country_code)) {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_zipcode_not_valid_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                            YoYo.with(Techniques.Shake).duration(700).playOn(act.findViewById(R.id.addressUserTK));
                        }
                    });
                    return false;
                }

                if (addressUserCity.getText().toString().trim().equals("")) {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_city_fail_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                            YoYo.with(Techniques.Shake).duration(700).playOn(act.findViewById(R.id.addressUserCity));
                        }
                    });
                    return false;
                }

                if (addressUserAddress.getText().toString().trim().equals("")) {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_address_fail_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                            YoYo.with(Techniques.Shake).duration(700).playOn(act.findViewById(R.id.addressUserAddress));
                        }
                    });
                    return false;
                }

                if (addressUserRegion.getText().toString().trim().equals("") && !isRegionSelected) {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_region_fail_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                            YoYo.with(Techniques.Shake).duration(700).playOn(act.findViewById(R.id.addressUserRegion));
                        }
                    });
                    return false;
                }

                System.out.println(zone_code + " " + state);

                String response = sh.makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_update_user_address), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), address_book_id, gender, firstname, lastname, telephone, postcode, city, address1, country_code, zone_code, state, address2), ServiceHandler.GET);
                System.out.println(response);
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("result").equals("success")) {
                    act.getSharedPreferences("ShopPrefs", 0).edit().putString("user_name", firstname + " " + lastname).apply();
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_btn_save_success), SuperToast.Duration.SHORT, Style.getStyle(Style.GREEN, SuperToast.Animations.POPUP)).show();
                        }
                    });
                    return true;
                } else {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_btn_save_fail), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                        }
                    });
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            act.runOnUiThread(new Runnable() {
                public void run() {
                    SuperToast.create(act, getResources().getString(R.string.profile_btn_save_fail), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                }
            });
            return false;
        }
    }

    private void updateLayout() {
        addressUsername.setText(address.getFirstname());
        addressUsersername.setText(address.getLastname());
        addressUserAddress.setText(address.getAddress1());
        addressUserCity.setText(address.getCity());
        addressUserTK.setText(address.getPostcode());
        addressUserPhone.setText(address.getTelephone());

        List<String> sexes = new ArrayList<>();
        sexes.add("Άνδρας");
        sexes.add("Γυναίκα");
        ArrayAdapter<String> sexesAdapter = new ArrayAdapter<>(act, android.R.layout.simple_spinner_item, sexes);
        sexesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserSex.setAdapter(sexesAdapter);
        spinnerUserSex.setSelection(address.getGender().equals("f") ? 1 : 0);

        final List<String> listCountries = new ArrayList<>();
        List<CountryItem> countryItemList = StoreApplication.getInstance().getCountryItemList();
        int user_country_selection = 0;
        for (int i = 0; i < countryItemList.size(); i++) {
            listCountries.add(countryItemList.get(i).getCountry_name());
            if (address.getCountry_id().equals(countryItemList.get(i).getCountry_id()))
                user_country_selection = i;
        }
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(act, android.R.layout.simple_spinner_item, listCountries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserCountry.setAdapter(countryAdapter);
        spinnerUserCountry.setSelection(user_country_selection, false);
        final HashMap<String, String> map = new HashMap<>();
        for (CountryItem item : countryItemList) {
            map.put(item.getCountry_name(), item.getCountry_id());
        }
        spinnerUserCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<String> listZones = new ArrayList<>();
                List<ZoneItem> zoneItemList = StoreApplication.getInstance().getZoneItemList();

                for (int i = 0; i < zoneItemList.size(); i++) {
                    if (map.get(listCountries.get(position)).equals(zoneItemList.get(i).getCountry_id())) {
                        listZones.add(zoneItemList.get(i).getZone_name());
                    }
                }
                if (listZones.size() > 0) {
                    addressUserRegion.setVisibility(View.GONE);
                    spinnerUserRegion.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> zonesAdapter = new ArrayAdapter<>(act, android.R.layout.simple_spinner_item, listZones);
                    zonesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerUserRegion.setAdapter(zonesAdapter);
                    isRegionSelected = true;
                } else {
                    addressUserRegion.setVisibility(View.VISIBLE);
                    spinnerUserRegion.setVisibility(View.GONE);
                    isRegionSelected = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (address.getZone_id() > -1) {
            addressUserRegion.setVisibility(View.GONE);
            spinnerUserRegion.setVisibility(View.VISIBLE);
            List<String> listZones = new ArrayList<>();
            List<ZoneItem> zoneItemList = StoreApplication.getInstance().getZoneItemList();
            int user_zone_selection = 0;
            for (int i = 0, j = 0; i < zoneItemList.size(); i++) {
                if (address.getCountry_id().equals(zoneItemList.get(i).getCountry_id())) {
                    listZones.add(zoneItemList.get(i).getZone_name());
                    if (address.getZone_id() == zoneItemList.get(i).getZone_id())
                        user_zone_selection = j;
                    j++;
                }
            }
            ArrayAdapter<String> zonesAdapter = new ArrayAdapter<>(act, android.R.layout.simple_spinner_item, listZones);
            zonesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerUserRegion.setAdapter(zonesAdapter);
            spinnerUserRegion.setSelection(user_zone_selection, false);
            isRegionSelected = true;
        } else {
            addressUserRegion.setVisibility(View.VISIBLE);
            spinnerUserRegion.setVisibility(View.GONE);
            addressUserRegion.setText(address.getState());
            isRegionSelected = false;
        }
    }

    private boolean isValidPostalCode(String postalCode, String countryCode) {
        String postalCodeRegex;
        switch (countryCode) {
            case "US":
                postalCodeRegex = "^\\d{5}(-\\d{4})?$";
                break;
            case "CA":
                postalCodeRegex = "^[ABCEGHJKLMNPRSTVXY]{1}\\d{1}[A-Z]{1} *\\d{1}[A-Z]{1}\\d{1}$";
                break;
            case "UK":
                postalCodeRegex = "^([A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]? {1,2}[0-9][ABD-HJLN-UW-Z]{2}|GIR 0AA)$";
                break;
            default:
                postalCodeRegex = "^(?:[A-Z0-9]+([- ]?[A-Z0-9]+)*)?$";
        }
        return postalCode.matches(postalCodeRegex);
    }
}
