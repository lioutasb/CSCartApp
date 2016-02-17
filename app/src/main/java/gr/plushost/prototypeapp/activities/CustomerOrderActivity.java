package gr.plushost.prototypeapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.adapters.listviews.OrderProductsListAdapter;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.items.CustomerOrderItem;
import gr.plushost.prototypeapp.items.CustomerOrderPriceInfoItem;
import gr.plushost.prototypeapp.items.PaymentMethodItem;
import gr.plushost.prototypeapp.items.ShippingMethodItem;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;
import gr.plushost.prototypeapp.parsers.CustomerOrderParser;
import gr.plushost.prototypeapp.util.Helper;
import gr.plushost.prototypeapp.widgets.ExpandableHeightListView;
import gr.plushost.prototypeapp.widgets.NestedListView;

/**
 * Created by billiout on 23/3/2015.
 */
public class CustomerOrderActivity extends AppCompatActivity {
    Activity act = this;
    String order_id;
    CustomerOrderItem order = null;
    OrderProductsListAdapter adapter;
    NoNetworkHandler noNetworkHandler;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        noNetworkHandler = new NoNetworkHandler(this);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        order_id = getIntent().getExtras().getString("order_id");
        getSupportActionBar().setTitle(String.format(getResources().getString(R.string.order_screen_title), getIntent().getExtras().getString("display_id")));

        if(noNetworkHandler.showDialog())
            new GetOrderInfo().execute();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_order);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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

    class GetOrderInfo extends AsyncTask<Void, Void, Void> {
        SweetAlertDialog pDialog;
        @Override
        protected void onPreExecute() {
            pDialog = new SweetAlertDialog(act, SweetAlertDialog.PROGRESS_TYPE, false);
            pDialog.setTitleText(act.getResources().getString(R.string.loadingTxt));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setMultiColorProgressBar(act, true);
            act.findViewById(R.id.progressBarRel).setVisibility(View.GONE);
            act.findViewById(R.id.content_frame).setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_order_get), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), URLEncoder.encode(order_id, "UTF-8")), ServiceHandler.GET);

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("code").equals("0x0000")) {
                    CustomerOrderParser parser = new CustomerOrderParser();
                    order = parser.parse(jsonObject.getJSONObject("info").getJSONObject("order"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(pDialog != null && pDialog.isShowing()){
                pDialog.dismissWithAnimation();
                pDialog.setMultiColorProgressBar(act, false);
            }
            act.findViewById(R.id.progressBarRel).setVisibility(View.GONE);
            if (order != null) {
                ((TextView) act.findViewById(R.id.orderTitle)).setText(String.format(getResources().getString(R.string.order_title), order.getDisplay_id(),order.getDate_added()));

                String status = getResources().getString(R.string.order_status_title) + " " + order.getOrder_status().getStatus_name();
                SpannableString sa = new SpannableString(status);
                sa.setSpan(new ForegroundColorSpan(Color.parseColor(order.getOrder_status().getStatus_color())), getResources().getString(R.string.order_status_title).length(), status.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((TextView) act.findViewById(R.id.statusTitle)).setText(sa);

                if (!order.getShipping_address().getFirstname().equals("") || !order.getShipping_address().getFirstname().equals("")) {
                    (act.findViewById(R.id.s_name)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.s_name)).setText(order.getShipping_address().getFirstname() + " " + order.getShipping_address().getLastname());
                } else {
                    (act.findViewById(R.id.s_name)).setVisibility(View.GONE);
                }

                if (!order.getEmail().equals("")) {
                    (act.findViewById(R.id.s_email)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.s_email)).setText(order.getEmail());
                } else {
                    (act.findViewById(R.id.s_email)).setVisibility(View.GONE);
                }

                if (!order.getShipping_address().getTelephone().equals("")) {
                    (act.findViewById(R.id.s_phone)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.s_phone)).setText(order.getShipping_address().getTelephone());
                } else {
                    (act.findViewById(R.id.s_phone)).setVisibility(View.GONE);
                }

                if (!order.getShipping_address().getCity().equals("")) {
                    (act.findViewById(R.id.s_city)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.s_city)).setText(order.getShipping_address().getCity());
                } else {
                    (act.findViewById(R.id.s_city)).setVisibility(View.GONE);
                }

                if (!order.getShipping_address().getCountry_name().equals("")) {
                    (act.findViewById(R.id.s_country)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.s_country)).setText(order.getShipping_address().getCountry_name());
                } else {
                    (act.findViewById(R.id.s_country)).setVisibility(View.GONE);
                }

                if (!order.getShipping_address().getAddress1().equals("")) {
                    (act.findViewById(R.id.s_address)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.s_address)).setText(order.getShipping_address().getAddress1());
                } else {
                    (act.findViewById(R.id.s_address)).setVisibility(View.GONE);
                }

                if (!order.getShipping_address().getZone_name().equals("")) {
                    (act.findViewById(R.id.s_region)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.s_region)).setText(order.getShipping_address().getZone_name());
                } else {
                    (act.findViewById(R.id.s_region)).setVisibility(View.GONE);
                }

                if (!order.getShipping_address().getPostcode().equals("")) {
                    (act.findViewById(R.id.s_zipcode)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.s_zipcode)).setText(order.getShipping_address().getPostcode());
                } else {
                    (act.findViewById(R.id.s_zipcode)).setVisibility(View.GONE);
                }


                if (!order.getBilling_address().getFirstname().equals("") || !order.getBilling_address().getFirstname().equals("")) {
                    (act.findViewById(R.id.b_name)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.b_name)).setText(order.getBilling_address().getFirstname() + " " + order.getBilling_address().getLastname());
                } else {
                    (act.findViewById(R.id.b_name)).setVisibility(View.GONE);
                }

                if (!order.getEmail().equals("")) {
                    (act.findViewById(R.id.b_email)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.b_email)).setText(order.getEmail());
                } else {
                    (act.findViewById(R.id.b_email)).setVisibility(View.GONE);
                }

                if (!order.getBilling_address().getTelephone().equals("")) {
                    (act.findViewById(R.id.b_phone)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.b_phone)).setText(order.getBilling_address().getTelephone());
                } else {
                    (act.findViewById(R.id.b_phone)).setVisibility(View.GONE);
                }

                if (!order.getBilling_address().getCity().equals("")) {
                    (act.findViewById(R.id.b_city)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.b_city)).setText(order.getBilling_address().getCity());
                } else {
                    (act.findViewById(R.id.b_city)).setVisibility(View.GONE);
                }

                if (!order.getBilling_address().getCountry_name().equals("")) {
                    (act.findViewById(R.id.b_country)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.b_country)).setText(order.getBilling_address().getCountry_name());
                } else {
                    (act.findViewById(R.id.b_country)).setVisibility(View.GONE);
                }

                if (!order.getBilling_address().getAddress1().equals("")) {
                    (act.findViewById(R.id.b_address)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.b_address)).setText(order.getBilling_address().getAddress1());
                } else {
                    (act.findViewById(R.id.b_address)).setVisibility(View.GONE);
                }

                if (!order.getBilling_address().getZone_name().equals("")) {
                    (act.findViewById(R.id.b_region)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.b_region)).setText(order.getBilling_address().getZone_name());
                } else {
                    (act.findViewById(R.id.b_region)).setVisibility(View.GONE);
                }

                if (!order.getBilling_address().getPostcode().equals("")) {
                    (act.findViewById(R.id.b_zipcode)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.b_zipcode)).setText(order.getBilling_address().getPostcode());
                } else {
                    (act.findViewById(R.id.b_zipcode)).setVisibility(View.GONE);
                }


                if (!order.getOrder_status().getComments().equals("")) {
                    (act.findViewById(R.id.commentsTxt)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.commentsTxt)).setText(order.getOrder_status().getComments());
                } else {
                    (act.findViewById(R.id.commentsTxt)).setVisibility(View.VISIBLE);
                    ((TextView) act.findViewById(R.id.commentsTxt)).setText(getResources().getString(R.string.order_no_comments));
                }


                if (!order.getBarcode_img_url().equals("")) {
                    act.findViewById(R.id.barcodeImg).setVisibility(View.VISIBLE);
                    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(act).denyCacheImageMultipleSizesInMemory().build();
                    if (!ImageLoader.getInstance().isInited()) {
                        ImageLoader.getInstance().init(config);
                    }
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(order.getBarcode_img_url(), (ImageView) act.findViewById(R.id.barcodeImg));
                } else {
                    act.findViewById(R.id.barcodeImg).setVisibility(View.GONE);
                }

                LinearLayout summaryLay = (LinearLayout) act.findViewById(R.id.summaryBody);

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
                        textView.setText(Html.fromHtml("<b>" + priceInfoItems.get(i).getTitle() + ":</b> " + StoreApplication.getCurrency_symbol(act) + priceInfoItems.get(i).getPrice()));
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
                ((NestedListView) act.findViewById(R.id.productListView)).setAdapter(adapter);
                //Helper.getListViewSize(((ExpandableHeightListView) act.findViewById(R.id.productListView)), act);
                ((NestedListView) act.findViewById(R.id.productListView)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                });

                SpannableString str = new SpannableString(getResources().getString(R.string.order_shipping_info));
                str.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((TextView) findViewById(R.id.s_address_title)).setText(str);

                str = new SpannableString(getResources().getString(R.string.order_billing_info));
                str.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((TextView) findViewById(R.id.b_address_title)).setText(str);

                act.findViewById(R.id.content_frame).setVisibility(View.VISIBLE);
            }
        }
    }
}
