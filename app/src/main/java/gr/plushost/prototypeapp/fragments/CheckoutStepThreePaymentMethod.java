package gr.plushost.prototypeapp.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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
import gr.plushost.prototypeapp.items.PaymentMethodItem;
import gr.plushost.prototypeapp.items.ShippingMethodItem;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;
import gr.plushost.prototypeapp.widgets.RadioGroup;

/**
 * Created by user on 17/7/2015.
 */
public class CheckoutStepThreePaymentMethod extends Fragment {
    AppCompatActivity act;
    View rootView;
    RadioGroup radioGroupShips;
    List<RadioButton> pays;
    List<PaymentMethodItem> paysItems;
    String id = "";
    NoNetworkHandler noNetworkHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        act = (AppCompatActivity) getActivity();
        noNetworkHandler = new NoNetworkHandler(act);
        rootView = inflater.inflate(R.layout.fragment_checkout_step_three_payments, container, false);
        setHasOptionsMenu(true);
        act.getSupportActionBar().setTitle(act.getResources().getString(R.string.step_three_screen_title));

        radioGroupShips = new RadioGroup(act);
        ((LinearLayout) rootView.findViewById(R.id.linShips)).addView(radioGroupShips);
        rootView.findViewById(R.id.btnGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noNetworkHandler.showDialog())
                    new UpdatePaymentMethod().execute();

            }
        });

        if(noNetworkHandler.showDialog())
            new GetPaymentMethods().execute();
        return rootView;
    }

    class GetPaymentMethods extends AsyncTask<Void, Void, List<PaymentMethodItem>> {
        String idp = "";

        @Override
        protected void onPreExecute(){
            rootView.findViewById(R.id.scrollView).setVisibility(View.GONE);
            rootView.findViewById(R.id.progressBarVP).setVisibility(View.VISIBLE);
            ((ProgressWheel) rootView.findViewById(R.id.progressBarVP)).spin();
        }

        @Override
        protected List<PaymentMethodItem> doInBackground(Void... params) {
            pays = new ArrayList<>();
            ServiceHandler sh = StoreApplication.getServiceHandler(act);

            List<PaymentMethodItem> paymentMethodItems = new ArrayList<>();
            String response = sh.makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_checkout_get_payment_methods), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", "")), ServiceHandler.GET);
            //System.out.println(response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("code").equals("0x0000")) {

                    JSONArray methods = jsonObject.getJSONObject("info").getJSONArray("methods");
                    for(int i = 0; i < methods.length(); i++){
                        PaymentMethodItem paymentMethodItem = new PaymentMethodItem();
                        paymentMethodItem.setPm_id(methods.getJSONObject(i).getString("pm_id"));
                        paymentMethodItem.setPm_description(methods.getJSONObject(i).getString("pm_description"));
                        paymentMethodItem.setPm_title(methods.getJSONObject(i).getString("pm_title"));
                        paymentMethodItem.setPm_instructions(methods.getJSONObject(i).getString("pm_instructions"));
                        paymentMethodItems.add(paymentMethodItem);
                    }
                    idp = jsonObject.getJSONObject("info").getString("selected_method");
                    return paymentMethodItems;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return paymentMethodItems;
        }

        @Override
        protected void onPostExecute(List<PaymentMethodItem> methods) {
            rootView.findViewById(R.id.progressBarVP).setVisibility(View.GONE);
            if (methods.size() > 0) {
                rootView.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                paysItems = new ArrayList<>(methods);
                for (int i = 0; i < methods.size(); i++) {
                    if (!idp.equals("null")) {
                        if (methods.get(i).getPm_id().equals(idp)) {
                            SpannableString colouredString = new SpannableString("\n" + methods.get(i).getPm_title() + "\n" + methods.get(i).getPm_description() + "\n");
                            colouredString.setSpan(new ForegroundColorSpan(Color.parseColor("#3D5C5C")), methods.get(i).getPm_title().length() + 1, colouredString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            colouredString.setSpan(new StyleSpan(Typeface.ITALIC), methods.get(i).getPm_title().length() + 1, colouredString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            RadioButton rd = radioGroupShips.addItem(colouredString, String.valueOf(i), true);
                            if(!paysItems.get(i).getPm_instructions().equals("")) {
                                ((TextView) rootView.findViewById(R.id.additionalInfo)).setText(Html.fromHtml(paysItems.get(i).getPm_instructions()));
                                rootView.findViewById(R.id.additionalInfo).setVisibility(View.VISIBLE);
                            }
                            id = paysItems.get(i).getPm_id();
                            rd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        if(!paysItems.get(Integer.valueOf((String) buttonView.getTag())).getPm_instructions().equals("")) {
                                            ((TextView) rootView.findViewById(R.id.additionalInfo)).setText(Html.fromHtml(paysItems.get(Integer.valueOf((String) buttonView.getTag())).getPm_instructions()));
                                            rootView.findViewById(R.id.additionalInfo).setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            rootView.findViewById(R.id.additionalInfo).setVisibility(View.GONE);
                                        }
                                        id = paysItems.get(Integer.valueOf((String) buttonView.getTag())).getPm_id();
                                    }
                                }
                            });
                        } else {
                            SpannableString colouredString = new SpannableString("\n" + methods.get(i).getPm_title() + "\n" + methods.get(i).getPm_description() + "\n");
                            colouredString.setSpan(new ForegroundColorSpan(Color.parseColor("#3D5C5C")), methods.get(i).getPm_title().length() + 1, colouredString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            colouredString.setSpan(new StyleSpan(Typeface.ITALIC), methods.get(i).getPm_title().length() + 1, colouredString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            RadioButton rd = radioGroupShips.addItem(colouredString, String.valueOf(i), false);
                            rd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        if(!paysItems.get(Integer.valueOf((String) buttonView.getTag())).getPm_instructions().equals("")) {
                                            ((TextView) rootView.findViewById(R.id.additionalInfo)).setText(Html.fromHtml(paysItems.get(Integer.valueOf((String) buttonView.getTag())).getPm_instructions()));
                                            rootView.findViewById(R.id.additionalInfo).setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            rootView.findViewById(R.id.additionalInfo).setVisibility(View.GONE);
                                        }
                                        id = paysItems.get(Integer.valueOf((String) buttonView.getTag())).getPm_id();
                                    }
                                }
                            });
                        }
                    } else {
                        if (i == 0) {
                            SpannableString colouredString = new SpannableString("\n" + methods.get(i).getPm_title() + "\n" + methods.get(i).getPm_description() + "\n");
                            colouredString.setSpan(new ForegroundColorSpan(Color.parseColor("#3D5C5C")), methods.get(i).getPm_title().length() + 1, colouredString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            colouredString.setSpan(new StyleSpan(Typeface.ITALIC), methods.get(i).getPm_title().length() + 1, colouredString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            RadioButton rd = radioGroupShips.addItem(colouredString, String.valueOf(i), true);
                            if(!paysItems.get(i).getPm_instructions().equals("")) {
                                ((TextView) rootView.findViewById(R.id.additionalInfo)).setText(Html.fromHtml(paysItems.get(i).getPm_instructions()));
                                rootView.findViewById(R.id.additionalInfo).setVisibility(View.VISIBLE);
                            }
                            id = paysItems.get(i).getPm_id();
                            rd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        if(!paysItems.get(Integer.valueOf((String) buttonView.getTag())).getPm_instructions().equals("")) {
                                            ((TextView) rootView.findViewById(R.id.additionalInfo)).setText(Html.fromHtml(paysItems.get(Integer.valueOf((String) buttonView.getTag())).getPm_instructions()));
                                            rootView.findViewById(R.id.additionalInfo).setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            rootView.findViewById(R.id.additionalInfo).setVisibility(View.GONE);
                                        }
                                        id = paysItems.get(Integer.valueOf((String) buttonView.getTag())).getPm_id();
                                    }
                                }
                            });
                        } else {
                            SpannableString colouredString = new SpannableString("\n" + methods.get(i).getPm_title() + "\n" + methods.get(i).getPm_description() + "\n");
                            colouredString.setSpan(new ForegroundColorSpan(Color.parseColor("#3D5C5C")), methods.get(i).getPm_title().length() + 1, colouredString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            colouredString.setSpan(new StyleSpan(Typeface.ITALIC), methods.get(i).getPm_title().length() + 1, colouredString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            RadioButton rd = radioGroupShips.addItem(colouredString, String.valueOf(i), false);
                            rd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        if(!paysItems.get(Integer.valueOf((String) buttonView.getTag())).getPm_instructions().equals("")) {
                                            ((TextView) rootView.findViewById(R.id.additionalInfo)).setText(Html.fromHtml(paysItems.get(Integer.valueOf((String) buttonView.getTag())).getPm_instructions()));
                                            rootView.findViewById(R.id.additionalInfo).setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            rootView.findViewById(R.id.additionalInfo).setVisibility(View.GONE);
                                        }
                                        id = paysItems.get(Integer.valueOf((String) buttonView.getTag())).getPm_id();
                                    }
                                }
                            });
                        }
                    }
                }
            }
            else{
                SuperToast.create(act, getResources().getString(R.string.btn_logout_fail), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                act.finish();
            }
        }
    }

    class UpdatePaymentMethod extends AsyncTask<Void, Void, Boolean>{
        @Override
        protected void onPreExecute(){
            rootView.findViewById(R.id.btnGo).setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ServiceHandler sh = StoreApplication.getServiceHandler(act);
            try {
                String response = sh.makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_checkout_update_payment_methods), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), id), ServiceHandler.GET);
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
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.content_fragment, new CheckoutStepFourSummary()).addToBackStack("stepThree").commit();
            }
            else{
                SuperToast.create(act, getResources().getString(R.string.btn_logout_fail), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                act.finish();
            }
        }
    }

}
