package gr.plushost.prototypeapp.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.items.AddressItem;
import gr.plushost.prototypeapp.items.CountryItem;
import gr.plushost.prototypeapp.items.UserAddressItem;
import gr.plushost.prototypeapp.items.ZoneItem;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;
import gr.plushost.prototypeapp.parsers.AddressParser;
import gr.plushost.prototypeapp.viewanimations.Techniques;
import gr.plushost.prototypeapp.viewanimations.YoYo;

/**
 * Created by user on 16/7/2015.
 */
public class CheckoutStepOneAddressesFragment extends Fragment {
    AppCompatActivity act;
    View rootView;
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
    BootstrapEditText s_addressUsername;
    BootstrapEditText s_addressUsersername;
    BootstrapEditText s_addressUserAddress;
    BootstrapEditText s_addressUserCity;
    BootstrapEditText s_addressUserTK;
    BootstrapEditText s_addressUserRegion;
    BootstrapEditText s_addressUserPhone;
    Spinner s_spinnerUserCountry;
    Spinner s_spinnerUserRegion;
    Spinner s_spinnerUserSex;
    UserAddressItem address;
    boolean isRegionSelected = false;
    boolean s_isRegionSelected = false;
    CheckBox shipAddrCheckbox;
    boolean from_edit = false;
    NoNetworkHandler noNetworkHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        act = (AppCompatActivity) getActivity();
        noNetworkHandler = new NoNetworkHandler(act);
        rootView = inflater.inflate(R.layout.fragment_checkout_step_one_addresses, container, false);
        setHasOptionsMenu(true);
        act.getSupportActionBar().setTitle(act.getResources().getString(R.string.step_one_screen_title));

        addressUsername = (BootstrapEditText) rootView.findViewById(R.id.b_addressUsername);
        addressUsersername = (BootstrapEditText) rootView.findViewById(R.id.b_addressUsersername);
        addressUserAddress = (BootstrapEditText) rootView.findViewById(R.id.b_addressUserAddress);
        addressUserCity = (BootstrapEditText) rootView.findViewById(R.id.b_addressUserCity);
        addressUserTK = (BootstrapEditText) rootView.findViewById(R.id.b_addressUserTK);
        addressUserRegion = (BootstrapEditText) rootView.findViewById(R.id.b_addressUserRegion);
        addressUserPhone = (BootstrapEditText) rootView.findViewById(R.id.b_addressUserPhone);
        spinnerUserRegion = (Spinner) rootView.findViewById(R.id.b_spinnerUserRegion);
        spinnerUserCountry = (Spinner) rootView.findViewById(R.id.b_spinnerUserCountry);
        spinnerUserSex = (Spinner) rootView.findViewById(R.id.b_spinnerUserSex);

        /*s_addressUsername = (BootstrapEditText) rootView.findViewById(R.id.s_addressUsername);
        s_addressUsersername = (BootstrapEditText) rootView.findViewById(R.id.s_addressUsersername);
        s_addressUserAddress = (BootstrapEditText) rootView.findViewById(R.id.s_addressUserAddress);
        s_addressUserCity = (BootstrapEditText) rootView.findViewById(R.id.s_addressUserCity);
        s_addressUserTK = (BootstrapEditText) rootView.findViewById(R.id.s_addressUserTK);
        s_addressUserRegion = (BootstrapEditText) rootView.findViewById(R.id.s_addressUserRegion);
        s_addressUserPhone = (BootstrapEditText) rootView.findViewById(R.id.s_addressUserPhone);
        s_spinnerUserRegion = (Spinner) rootView.findViewById(R.id.s_spinnerUserRegion);
        s_spinnerUserCountry = (Spinner) rootView.findViewById(R.id.s_spinnerUserCountry);
        s_spinnerUserSex = (Spinner) rootView.findViewById(R.id.s_spinnerUserSex);*/

        /*rootView.findViewById(R.id.shipAddr).setVisibility(View.GONE);
        shipAddrCheckbox = ((CheckBox) rootView.findViewById(R.id.shipAddrCheckbox));
        shipAddrCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rootView.findViewById(R.id.shipAddr).setVisibility(View.GONE);
                } else {
                    rootView.findViewById(R.id.shipAddr).setVisibility(View.VISIBLE);
                }
            }
        });*/


        rootView.findViewById(R.id.btnGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noNetworkHandler.showDialog())
                    new UpdateUserAddress().execute();

            }
        });

        from_edit = this.getArguments().getBoolean("from_edit");

        if(noNetworkHandler.showDialog())
            new GetUserAddress().execute();

        return rootView;
    }

    class GetUserAddress extends AsyncTask<Void, Void, UserAddressItem> {
        @Override
        protected void onPreExecute(){
            rootView.findViewById(R.id.scrollView).setVisibility(View.GONE);
            rootView.findViewById(R.id.progressBarVP).setVisibility(View.VISIBLE);
            ((ProgressWheel) rootView.findViewById(R.id.progressBarVP)).spin();
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

            if (result != null) {
                address = result;

                if(!from_edit) {
                    if (!address.getAddress1().equals("") && !address.getLastname().equals("") && !address.getFirstname().equals("") && !address.getCity().equals("") && !address.getCountry_id().equals("")
                            && !address.getPostcode().equals("") && !address.getTelephone().equals("") && (!address.getState().equals("") || address.getZone_id() > -1)) {
                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.content_fragment, new CheckoutStepTwoShippingMethodsFragment()).commit();
                    } else {
                        Handler handler = new Handler();
                        Runnable r = new Runnable() {
                            public void run() {
                                updateBillLayout();
                                rootView.findViewById(R.id.progressBarVP).setVisibility(View.GONE);
                                rootView.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                            }
                        };
                        handler.post(r);
                    }
                }
                else{
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        public void run() {
                            updateBillLayout();
                            rootView.findViewById(R.id.progressBarVP).setVisibility(View.GONE);
                            rootView.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                        }
                    };
                    handler.post(r);
                }


                //updateShipLayout();
            }
            else{
                rootView.findViewById(R.id.progressBarVP).setVisibility(View.GONE);
                rootView.findViewById(R.id.scrollView).setVisibility(View.GONE);
                SuperToast.create(act, getResources().getString(R.string.btn_logout_fail), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                act.finish();
            }
        }
    }

    private void updateBillLayout() {
        addressUsername.setText(address.getFirstname());
        addressUsersername.setText(address.getLastname());
        addressUserAddress.setText(address.getAddress1());
        addressUserCity.setText(address.getCity());
        addressUserTK.setText(address.getPostcode());
        addressUserPhone.setText(address.getTelephone());

        List<String> sexes = new ArrayList<>();
        sexes.add("Male");
        sexes.add("Female");
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

    private void updateShipLayout() {
        s_addressUsername.setText(address.getFirstname());
        s_addressUsersername.setText(address.getLastname());
        s_addressUserAddress.setText(address.getAddress1());
        s_addressUserCity.setText(address.getCity());
        s_addressUserTK.setText(address.getPostcode());
        s_addressUserPhone.setText(address.getTelephone());

        List<String> sexes = new ArrayList<>();
        sexes.add("Male");
        sexes.add("Female");
        ArrayAdapter<String> sexesAdapter = new ArrayAdapter<>(act, android.R.layout.simple_spinner_item, sexes);
        sexesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_spinnerUserSex.setAdapter(sexesAdapter);
        s_spinnerUserSex.setSelection(address.getGender().equals("f") ? 1 : 0);

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
        s_spinnerUserCountry.setAdapter(countryAdapter);
        s_spinnerUserCountry.setSelection(user_country_selection, false);
        final HashMap<String, String> map = new HashMap<>();
        for (CountryItem item : countryItemList) {
            map.put(item.getCountry_name(), item.getCountry_id());
        }
        s_spinnerUserCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    s_addressUserRegion.setVisibility(View.GONE);
                    s_spinnerUserRegion.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> zonesAdapter = new ArrayAdapter<>(act, android.R.layout.simple_spinner_item, listZones);
                    zonesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s_spinnerUserRegion.setAdapter(zonesAdapter);
                    s_isRegionSelected = true;
                } else {
                    s_addressUserRegion.setVisibility(View.VISIBLE);
                    s_spinnerUserRegion.setVisibility(View.GONE);
                    s_isRegionSelected = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (address.getZone_id() > -1) {
            s_addressUserRegion.setVisibility(View.GONE);
            s_spinnerUserRegion.setVisibility(View.VISIBLE);
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
            s_spinnerUserRegion.setAdapter(zonesAdapter);
            s_spinnerUserRegion.setSelection(user_zone_selection, false);
            s_isRegionSelected = true;
        } else {
            s_addressUserRegion.setVisibility(View.VISIBLE);
            s_spinnerUserRegion.setVisibility(View.GONE);
            s_addressUserRegion.setText(address.getState());
            s_isRegionSelected = false;
        }
    }

    class UpdateUserAddress extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute(){
            //rootView.findViewById(R.id.scrollView).setVisibility(View.GONE);
            //rootView.findViewById(R.id.progressBarLoading).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.btnGo).setEnabled(false);
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            ServiceHandler sh = StoreApplication.getServiceHandler(act);
            try {
                int address_book_id = address != null ? address.getAddress_book_id():-1;
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
                            YoYo.with(Techniques.Shake).duration(700).playOn(act.findViewById(R.id.b_addressUsersername));
                        }
                    });
                    return false;
                }

                if (addressUsername.getText().toString().trim().equals("")) {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_firstname_fail_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                            YoYo.with(Techniques.Shake).duration(700).playOn(act.findViewById(R.id.b_addressUsername));
                        }
                    });
                    return false;
                }

                if (addressUserPhone.getText().toString().trim().equals("")) {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_phone_fail_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                            YoYo.with(Techniques.Shake).duration(700).playOn(act.findViewById(R.id.b_addressUserPhone));
                        }
                    });
                    return false;
                }

                if (addressUserTK.getText().toString().trim().equals("")) {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_zipcode_fail_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                            YoYo.with(Techniques.Shake).duration(700).playOn(act.findViewById(R.id.b_addressUserTK));
                        }
                    });
                    return false;
                }

                if (!addressUserTK.getText().toString().trim().equals("") && !isValidPostalCode(addressUserTK.getText().toString().trim(), country_code)) {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_zipcode_not_valid_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                            YoYo.with(Techniques.Shake).duration(700).playOn(act.findViewById(R.id.b_addressUserTK));
                        }
                    });
                    return false;
                }

                if (addressUserCity.getText().toString().trim().equals("")) {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_city_fail_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                            YoYo.with(Techniques.Shake).duration(700).playOn(act.findViewById(R.id.b_addressUserCity));
                        }
                    });
                    return false;
                }

                if (addressUserAddress.getText().toString().trim().equals("")) {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_address_fail_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                            YoYo.with(Techniques.Shake).duration(700).playOn(act.findViewById(R.id.b_addressUserAddress));
                        }
                    });
                    return false;
                }

                if (addressUserRegion.getText().toString().trim().equals("") && !isRegionSelected) {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            SuperToast.create(act, getResources().getString(R.string.profile_region_fail_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                            YoYo.with(Techniques.Shake).duration(700).playOn(act.findViewById(R.id.b_addressUserRegion));
                        }
                    });
                    return false;
                }

                //if(shipAddrCheckbox.isChecked()) {

                    String response = sh.makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_update_user_address), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), address_book_id, gender, firstname, lastname, telephone, postcode, city, address1, country_code, zone_code, state, address2), ServiceHandler.GET);
                    //System.out.println(response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("result").equals("success")) {
                        act.getSharedPreferences("ShopPrefs", 0).edit().putString("user_name", firstname + " " + lastname).apply();
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                rootView.findViewById(R.id.btnGo).setEnabled(true);
                                //Toast.makeText(act, getResources().getString(R.string.profile_btn_save_success), Toast.LENGTH_SHORT).show();
                                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.content_fragment, new CheckoutStepTwoShippingMethodsFragment()).addToBackStack("stepOne").commit();
                            }
                        });
                        return true;
                    } else {
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                rootView.findViewById(R.id.btnGo).setEnabled(true);
                                SuperToast.create(act, getResources().getString(R.string.btn_logout_fail), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                                act.finish();
                            }
                        });
                        return false;
                    }
                //}
            } catch (Exception e) {
                e.printStackTrace();
            }

            act.runOnUiThread(new Runnable() {
                public void run() {
                    rootView.findViewById(R.id.btnGo).setEnabled(true);
                    SuperToast.create(act, getResources().getString(R.string.profile_btn_save_fail), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                }
            });
            return false;
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
