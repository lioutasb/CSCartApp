package gr.plushost.prototypeapp.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.items.ShippingMethodItem;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;
import gr.plushost.prototypeapp.parsers.AddressParser;
import gr.plushost.prototypeapp.util.Strings;
import gr.plushost.prototypeapp.widgets.RadioGroup;

/**
 * Created by user on 16/7/2015.
 */
public class CheckoutStepTwoShippingMethodsFragment extends Fragment {
    AppCompatActivity act;
    View rootView;
    RadioGroup radioGroupShips;
    List<RadioButton> shipps;
    List<ShippingMethodItem> shippsItems;
    BootstrapEditText comments;
    NoNetworkHandler noNetworkHandler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        act = (AppCompatActivity) getActivity();
        noNetworkHandler = new NoNetworkHandler(act);
        rootView = inflater.inflate(R.layout.fragment_checkout_step_two_shippings, container, false);
        setHasOptionsMenu(true);
        act.getSupportActionBar().setTitle(act.getResources().getString(R.string.step_two_screen_title));

        radioGroupShips = new RadioGroup(act);
        ((LinearLayout) rootView.findViewById(R.id.linShips)).addView(radioGroupShips);
        rootView.findViewById(R.id.btnGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noNetworkHandler.showDialog())
                    new UpdateShippingMethod().execute();
            }
        });
        comments = (BootstrapEditText) rootView.findViewById(R.id.commentEditText);

        if(noNetworkHandler.showDialog())
            new GetShippingMethods().execute();
        return rootView;
    }

    class GetShippingMethods extends AsyncTask<Void, Void, List<ShippingMethodItem>>{
        String id = "";
        String notes = "";

        @Override
        protected void onPreExecute(){
            rootView.findViewById(R.id.scrollView).setVisibility(View.GONE);
            rootView.findViewById(R.id.progressBarVP).setVisibility(View.VISIBLE);
            ((ProgressWheel) rootView.findViewById(R.id.progressBarVP)).spin();
        }

        @Override
        protected List<ShippingMethodItem> doInBackground(Void... params) {
            shipps = new ArrayList<>();
            ServiceHandler sh = StoreApplication.getServiceHandler(act);

            List<ShippingMethodItem> shippingMethods = new ArrayList<>();
            String response = sh.makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_checkout_get_shipping_methods), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", "")), ServiceHandler.GET);

            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("code").equals("0x0000")) {

                    JSONArray methods = jsonObject.getJSONObject("info").getJSONArray("shippings");
                    for(int i = 0; i < methods.length(); i++){
                        ShippingMethodItem shippingMethodItem = new ShippingMethodItem();
                        shippingMethodItem.setSm_id(methods.getJSONObject(i).getString("sm_id"));
                        shippingMethodItem.setSm_code(methods.getJSONObject(i).getString("sm_code"));
                        shippingMethodItem.setTitle(methods.getJSONObject(i).getString("title"));
                        shippingMethodItem.setDescription(methods.getJSONObject(i).getString("description"));
                        shippingMethodItem.setPrice(methods.getJSONObject(i).getString("price"));
                        shippingMethods.add(shippingMethodItem);
                    }
                    id = jsonObject.getJSONObject("info").getString("choosed_shipping");
                    notes = jsonObject.getJSONObject("info").getString("notes");


                    return shippingMethods;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return shippingMethods;
        }

        @Override
        protected void onPostExecute(List<ShippingMethodItem> methods){
            if(methods.size() > 0) {
                rootView.findViewById(R.id.progressBarVP).setVisibility(View.GONE);
                rootView.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                shippsItems = new ArrayList<>(methods);
                for (int i = 0; i < methods.size(); i++) {
                    if (methods.get(i).getSm_id().equals(id))
                        radioGroupShips.addItem(methods.get(i).getTitle() + " - " + methods.get(i).getPrice() + StoreApplication.getCurrency_symbol(act), String.valueOf(i), true);
                    else
                        radioGroupShips.addItem(methods.get(i).getTitle() + " - " + methods.get(i).getPrice() + StoreApplication.getCurrency_symbol(act), String.valueOf(i));
                }
                if (!notes.equals("null"))
                    comments.setText(notes);
            }
            else{
                SuperToast.create(act, getResources().getString(R.string.btn_logout_fail), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                act.finish();
            }
        }
    }

    class UpdateShippingMethod extends AsyncTask<Void, Void, Boolean>{
        String tag = "";
        String commentsStr = "";

        @Override
        protected void onPreExecute(){
            tag = radioGroupShips.getCheckedTag();
            commentsStr = comments.getText().toString();
            rootView.findViewById(R.id.btnGo).setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ServiceHandler sh = StoreApplication.getServiceHandler(act);
            try {
                String response = sh.makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_checkout_update_shipping_method), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), shippsItems.get(Integer.valueOf(tag)).getSm_id(), URLEncoder.encode(commentsStr, "UTF-8")), ServiceHandler.GET);
                //System.out.println(response);
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("result").equals("success")) {
                    return true;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
            rootView.findViewById(R.id.btnGo).setEnabled(true);
            if(result){
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.content_fragment, new CheckoutStepThreePaymentMethod()).addToBackStack("stepTwo").commit();
            }
            else{
                SuperToast.create(act, getResources().getString(R.string.btn_logout_fail), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                act.finish();
            }
        }
    }
}
