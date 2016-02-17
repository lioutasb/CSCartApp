package gr.plushost.prototypeapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.activities.ProductActivity;
import gr.plushost.prototypeapp.activities.WebViewActivity;
import gr.plushost.prototypeapp.adapters.listviews.OrderProductsListAdapter;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.items.CountryItem;
import gr.plushost.prototypeapp.items.CustomerOrderItem;
import gr.plushost.prototypeapp.items.CustomerOrderPriceInfoItem;
import gr.plushost.prototypeapp.items.PaymentMethodItem;
import gr.plushost.prototypeapp.items.ShippingMethodItem;
import gr.plushost.prototypeapp.items.ZoneItem;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;
import gr.plushost.prototypeapp.parsers.CheckoutDetailsParser;
import gr.plushost.prototypeapp.parsers.CustomerOrderParser;
import gr.plushost.prototypeapp.widgets.NestedListView;

/**
 * Created by user on 17/7/2015.
 */
public class CheckoutStepFourSummary extends Fragment {
    AppCompatActivity act;
    View rootView;
    CustomerOrderItem order = null;
    OrderProductsListAdapter adapter;
    NoNetworkHandler noNetworkHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = (AppCompatActivity) getActivity();
        noNetworkHandler = new NoNetworkHandler(act);
        rootView = inflater.inflate(R.layout.fragment_checkout_step_four_summary, container, false);
        setHasOptionsMenu(true);
        act.getSupportActionBar().setTitle(act.getResources().getString(R.string.step_four_screen_title));

        rootView.findViewById(R.id.checkoutComplete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) rootView.findViewById(R.id.termsAgreeCheckbox)).isChecked())
                    if(noNetworkHandler.showDialog())
                        new CheckoutComplete().execute();
                else
                    SuperToast.create(act, act.getResources().getString(R.string.step_four_terms_fail_txt), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
            }
        });

        rootView.findViewById(R.id.couponsCalcBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                couponsUpdate(v);
            }
        });

        SpannableString content = new SpannableString(act.getResources().getString(R.string.step_four_terms_txt));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        content.setSpan(new ForegroundColorSpan(Color.rgb(51, 51, 204)), 0, content.length(), 0);
        ((TextView) rootView.findViewById(R.id.termsAgreeTxt)).setText(content);

        if(noNetworkHandler.showDialog())
            new GetOrderSummaryInfo().execute();

        rootView.findViewById(R.id.editAddrTxt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++)
                    getActivity().getSupportFragmentManager().popBackStack(getActivity().getSupportFragmentManager().getBackStackEntryAt(i).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

                CheckoutStepOneAddressesFragment fragment = new CheckoutStepOneAddressesFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("from_edit", true);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.content_fragment, fragment).commit();
            }
        });


        return rootView;
    }


    class CheckoutComplete extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute(){
            rootView.findViewById(R.id.checkoutComplete).setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_checkout_complete), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", "")), ServiceHandler.GET);

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("code").equals("0x0000")) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            rootView.findViewById(R.id.checkoutComplete).setEnabled(true);
            if (result) {
                SuperToast.create(act, act.getResources().getString(R.string.step_four_done_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.GREEN, SuperToast.Animations.POPUP)).show();
                act.finish();
            } else {
                SuperToast.create(act, getResources().getString(R.string.btn_logout_fail), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                act.finish();
            }
        }
    }

    class UpdateCoupon extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            rootView.findViewById(R.id.progressBarVP).setVisibility(View.VISIBLE);
            ((ProgressWheel) rootView.findViewById(R.id.progressBarVP)).spin();
            rootView.findViewById(R.id.content_frame).setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_checkout_coupon), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), params[0]), ServiceHandler.GET);

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("code").equals("0x0000")) {
                    CheckoutDetailsParser parser = new CheckoutDetailsParser();
                    order = parser.parse(jsonObject.getJSONObject("info"));
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            rootView.findViewById(R.id.progressBarVP).setVisibility(View.GONE);
            if (order != null) {
                updateLayout();
            } else {
                SuperToast.create(act, getResources().getString(R.string.btn_logout_fail), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                act.finish();
            }
        }
    }

    class GetOrderSummaryInfo extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            rootView.findViewById(R.id.progressBarVP).setVisibility(View.VISIBLE);
            ((ProgressWheel) rootView.findViewById(R.id.progressBarVP)).spin();
            rootView.findViewById(R.id.content_frame).setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_checkout_details), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", "")), ServiceHandler.GET);

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("code").equals("0x0000")) {
                    CheckoutDetailsParser parser = new CheckoutDetailsParser();
                    order = parser.parse(jsonObject.getJSONObject("info"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            rootView.findViewById(R.id.progressBarVP).setVisibility(View.GONE);
            if (order != null) {
                updateLayout();
            } else {
                SuperToast.create(act, getResources().getString(R.string.btn_logout_fail), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                act.finish();
            }

        }
    }

    public void couponsUpdate(View view){
        try {
            if(noNetworkHandler.showDialog())
                new UpdateCoupon().execute(URLEncoder.encode(((BootstrapEditText) act.findViewById(R.id.couponsEditText)).getText().toString().trim(), "UTF-8"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void updateLayout() {
        ((BootstrapEditText) act.findViewById(R.id.couponsEditText)).setText("");

        if (!order.getShipping_address_2().getFirstname().equals("") || !order.getShipping_address_2().getFirstname().equals("")) {
            (rootView.findViewById(R.id.s_name)).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.s_name)).setText(order.getShipping_address_2().getFirstname() + " " + order.getShipping_address_2().getLastname());
        } else {
            (rootView.findViewById(R.id.s_name)).setVisibility(View.GONE);
        }

        if (!act.getSharedPreferences("ShopPrefs", 0).getString("user_email", "").equals("")) {
            (rootView.findViewById(R.id.s_email)).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.s_email)).setText(act.getSharedPreferences("ShopPrefs", 0).getString("user_email", ""));
        } else {
            (rootView.findViewById(R.id.s_email)).setVisibility(View.GONE);
        }

        if (!order.getShipping_address_2().getTelephone().equals("")) {
            (rootView.findViewById(R.id.s_phone)).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.s_phone)).setText(order.getShipping_address_2().getTelephone());
        } else {
            (rootView.findViewById(R.id.s_phone)).setVisibility(View.GONE);
        }

        if (!order.getShipping_address_2().getCity().equals("")) {
            (rootView.findViewById(R.id.s_city)).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.s_city)).setText(order.getShipping_address_2().getCity());
        } else {
            (rootView.findViewById(R.id.s_city)).setVisibility(View.GONE);
        }

        if (!order.getShipping_address_2().getCountry_id().equals("")) {
            (rootView.findViewById(R.id.s_country)).setVisibility(View.VISIBLE);
            List<CountryItem> countryItems = StoreApplication.getInstance().getCountryItemList();
            for (CountryItem item : countryItems)
                if (item.getCountry_id().equals(order.getShipping_address_2().getCountry_id()))
                    ((TextView) rootView.findViewById(R.id.s_country)).setText(item.getCountry_name());
        } else {
            (rootView.findViewById(R.id.s_country)).setVisibility(View.GONE);
        }

        if (!order.getShipping_address_2().getAddress1().equals("")) {
            (rootView.findViewById(R.id.s_address)).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.s_address)).setText(order.getShipping_address_2().getAddress1());
        } else {
            (rootView.findViewById(R.id.s_address)).setVisibility(View.GONE);
        }

        if (order.getShipping_address_2().getZone_id() > 0) {
            (rootView.findViewById(R.id.s_region)).setVisibility(View.VISIBLE);
            List<ZoneItem> countryItems = StoreApplication.getInstance().getZoneItemList();
            for (ZoneItem item : countryItems)
                if (item.getZone_id() == order.getShipping_address_2().getZone_id())
                    ((TextView) rootView.findViewById(R.id.s_region)).setText(item.getZone_name());
        } else if (!order.getShipping_address_2().getState().equals("")) {
            ((TextView) rootView.findViewById(R.id.s_region)).setText(order.getShipping_address_2().getState());

        } else {
            (rootView.findViewById(R.id.s_region)).setVisibility(View.GONE);
        }

        if (!order.getShipping_address_2().getPostcode().equals("")) {
            (rootView.findViewById(R.id.s_zipcode)).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.s_zipcode)).setText(order.getShipping_address_2().getPostcode());
        } else {
            (rootView.findViewById(R.id.s_zipcode)).setVisibility(View.GONE);
        }


        if (!order.getBilling_address_2().getFirstname().equals("") || !order.getBilling_address_2().getFirstname().equals("")) {
            (rootView.findViewById(R.id.b_name)).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.b_name)).setText(order.getBilling_address_2().getFirstname() + " " + order.getBilling_address_2().getLastname());
        } else {
            (rootView.findViewById(R.id.b_name)).setVisibility(View.GONE);
        }

        if (!act.getSharedPreferences("ShopPrefs", 0).getString("user_email", "").equals("")) {
            (rootView.findViewById(R.id.b_email)).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.b_email)).setText(act.getSharedPreferences("ShopPrefs", 0).getString("user_email", ""));
        } else {
            (rootView.findViewById(R.id.b_email)).setVisibility(View.GONE);
        }

        if (!order.getBilling_address_2().getTelephone().equals("")) {
            (rootView.findViewById(R.id.b_phone)).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.b_phone)).setText(order.getBilling_address_2().getTelephone());
        } else {
            (rootView.findViewById(R.id.b_phone)).setVisibility(View.GONE);
        }

        if (!order.getBilling_address_2().getCity().equals("")) {
            (rootView.findViewById(R.id.b_city)).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.b_city)).setText(order.getBilling_address_2().getCity());
        } else {
            (rootView.findViewById(R.id.b_city)).setVisibility(View.GONE);
        }

        if (!order.getBilling_address_2().getCountry_id().equals("")) {
            (rootView.findViewById(R.id.b_country)).setVisibility(View.VISIBLE);
            List<CountryItem> countryItems = StoreApplication.getInstance().getCountryItemList();
            for (CountryItem item : countryItems)
                if (item.getCountry_id().equals(order.getBilling_address_2().getCountry_id()))
                    ((TextView) rootView.findViewById(R.id.b_country)).setText(item.getCountry_name());
        } else {
            (rootView.findViewById(R.id.b_country)).setVisibility(View.GONE);
        }

        if (!order.getBilling_address_2().getAddress1().equals("")) {
            (rootView.findViewById(R.id.b_address)).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.b_address)).setText(order.getBilling_address_2().getAddress1());
        } else {
            (rootView.findViewById(R.id.b_address)).setVisibility(View.GONE);
        }

        if (order.getBilling_address_2().getZone_id() > 0) {
            (rootView.findViewById(R.id.b_region)).setVisibility(View.VISIBLE);
            List<ZoneItem> countryItems = StoreApplication.getInstance().getZoneItemList();
            for (ZoneItem item : countryItems)
                if (item.getZone_id() == order.getBilling_address_2().getZone_id())
                    ((TextView) rootView.findViewById(R.id.b_region)).setText(item.getZone_name());
        } else if (!order.getBilling_address_2().getState().equals("")) {
            ((TextView) rootView.findViewById(R.id.b_region)).setText(order.getBilling_address_2().getState());

        } else {
            (rootView.findViewById(R.id.b_region)).setVisibility(View.GONE);
        }

        if (!order.getBilling_address_2().getPostcode().equals("")) {
            (rootView.findViewById(R.id.b_zipcode)).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.b_zipcode)).setText(order.getBilling_address_2().getPostcode());
        } else {
            (rootView.findViewById(R.id.b_zipcode)).setVisibility(View.GONE);
        }


        if (!order.getNotes().equals("")) {
            (rootView.findViewById(R.id.commentsTxt)).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.commentsTxt)).setText(order.getNotes());
        } else {
            (rootView.findViewById(R.id.commentsTxt)).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.commentsTxt)).setText(getResources().getString(R.string.order_no_comments));
        }

        LinearLayout summaryLay = (LinearLayout) rootView.findViewById(R.id.summaryBody);
        summaryLay.removeAllViews();

        PaymentMethodItem payment = order.getPayment_method();
        if (!payment.getPm_title().equals("")) {
            TextView textView = new TextView(act);
            textView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.order_payment_title) + "</b><br>" + payment.getPm_title() + (!payment.getPm_description().equals("") ? " (" + payment.getPm_description() + ")" : "")));
            textView.setTextSize(12);
            textView.setPadding(0, (int) (getResources().getDisplayMetrics().density * 2 + 0.5f), 0, (int) (getResources().getDisplayMetrics().density * 2 + 0.5f));
            summaryLay.addView(textView);
        }

        ShippingMethodItem shipping = order.getShipping_method();
        if (!shipping.getTitle().equals("")) {
            TextView textView = new TextView(act);
            textView.setText(Html.fromHtml("<b>" + getResources().getString(R.string.order_shipping_title) + "</b><br>" + shipping.getTitle() + (!shipping.getDescription().equals("") ? " (" + shipping.getDescription() + ")" : "")));
            textView.setTextSize(12);
            textView.setPadding(0, (int) (getResources().getDisplayMetrics().density * 2 + 0.5f), 0, (int) (getResources().getDisplayMetrics().density * 2 + 0.5f));
            summaryLay.addView(textView);
        }

        List<CustomerOrderPriceInfoItem> priceInfoItems = order.getPrice_infos();
        for (int i = 0; i < priceInfoItems.size(); i++) {
            if (!priceInfoItems.get(i).getTitle().equals("")) {
                TextView textView = new TextView(act);
                textView.setText(Html.fromHtml("<b>" + priceInfoItems.get(i).getTitle() + ":</b> " + (!priceInfoItems.get(i).getType().equals("info")?StoreApplication.getCurrency_symbol(act):"") + priceInfoItems.get(i).getPrice()));
                textView.setTextSize(12);
                textView.setPadding(0, (int) (getResources().getDisplayMetrics().density * 2 + 0.5f), 0, (int) (getResources().getDisplayMetrics().density * 2 + 0.5f));
                if (priceInfoItems.get(i).getType().equals("total")) {
                    textView.setTypeface(null, Typeface.BOLD_ITALIC);
                    textView.setTextSize(18);
                    textView.setGravity(Gravity.CENTER);
                }
                summaryLay.addView(textView);
            }
        }

        adapter = new OrderProductsListAdapter(act, order.getOrder_items());
        //((ExpandableHeightListView) act.findViewById(R.id.productListView)).setExpanded(true);
        ((NestedListView) rootView.findViewById(R.id.productListView)).setAdapter(adapter);
        //Helper.getListViewSize(((ExpandableHeightListView) rootView.findViewById(R.id.productListView)), act);
                /*((NestedListView) rootView.findViewById(R.id.productListView)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(act, ProductActivity.class);
                        intent.putExtra("need_download", true);
                        intent.putExtra("product_id", Integer.valueOf(order.getOrder_items().get(i).getItem_id()));
                        intent.putExtra("product_name", order.getOrder_items().get(i).getItem_title());
                        act.startActivity(intent);
                        act.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });

                (act.findViewById(R.id.invoicePrint)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(order.getInvoice_pdf_url()));
                        act.startActivity(browserIntent);
                        act.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });*/


        if(order.getMessages().size() > 0){
            for(String s : order.getMessages()){
                SuperToast.create(act, s, SuperToast.Duration.MEDIUM, Style.getStyle(Style.PURPLE, SuperToast.Animations.POPUP)).show();
            }
        }

        SpannableString str = new SpannableString(getResources().getString(R.string.order_shipping_info));
        str.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView) rootView.findViewById(R.id.s_address_title)).setText(str);

        str = new SpannableString(getResources().getString(R.string.order_billing_info));
        str.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView) rootView.findViewById(R.id.b_address_title)).setText(str);

        rootView.findViewById(R.id.content_frame).setVisibility(View.VISIBLE);
    }
}
