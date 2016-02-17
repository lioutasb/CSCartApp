package gr.plushost.prototypeapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.support.v7.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.adapters.listviews.CartCardAdapter;
import gr.plushost.prototypeapp.adapters.listviews.ProductsListAdapter;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.exceptionhandlers.StoreExceptionHandler;
import gr.plushost.prototypeapp.items.CartProductItem;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;
import gr.plushost.prototypeapp.parsers.AddressParser;
import gr.plushost.prototypeapp.parsers.CartProductsParser;
import gr.plushost.prototypeapp.util.Helper;
import gr.plushost.prototypeapp.widgets.ExpandableHeightListView;
import gr.plushost.prototypeapp.widgets.NestedListView;

/**
 * Created by billiout on 12/3/2015.
 */
public class ShoppingCartActivity extends AppCompatActivity {
    Activity act = this;
    List<CartProductItem> list = new ArrayList<>();
    NestedListView listView;
    List<PriceInfoItem> priceInfoItems = new ArrayList<>();
    CartCardAdapter adapter;
    int cartCount = 0;
    NoNetworkHandler noNetworkHandler;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        noNetworkHandler = new NoNetworkHandler(this);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.cart_screen_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (NestedListView) findViewById(R.id.cartListView);
        //listView.setExpanded(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(act, ProductActivity.class);
                intent.putExtra("need_download", true);
                intent.putExtra("product_id", list.get(i).getItem_id());
                intent.putExtra("product_name", list.get(i).getItem_title());
                act.startActivity(intent);
                act.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_cart);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_cart_refresh:
                if(noNetworkHandler.showDialog())
                    new GetCartInfo().execute();
                return true;
            case R.id.action_cart_clear:
                if(noNetworkHandler.showDialog())
                    new ClearShoppingCart().execute();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(noNetworkHandler.showDialog())
            new GetCartInfo().execute();
    }

    class PriceInfoItem {
        private String title;
        private String type;
        private String currency;
        private String price;
        private int position;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

    class GetCartInfo extends AsyncTask<Void, Void, Void> {
        SweetAlertDialog pDialog;

        @Override
        protected void onPreExecute() {
            list.clear();
            ((LinearLayout) findViewById(R.id.txtTotals)).removeAllViews();
            pDialog = new SweetAlertDialog(act, SweetAlertDialog.PROGRESS_TYPE, false);
            pDialog.setTitleText(act.getResources().getString(R.string.loadingTxt));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setMultiColorProgressBar(act, true);
            act.findViewById(R.id.progressBarRel).setVisibility(View.GONE);
            act.findViewById(R.id.scrollViewCart).setVisibility(View.GONE);
            act.findViewById(R.id.checkoutRel).setVisibility(View.GONE);
            act.findViewById(R.id.empty_cart_rel).setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServiceHandler sh = StoreApplication.getServiceHandler(act);
            String response = sh.makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_get_cart_info), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", "")), ServiceHandler.GET);

            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("code").equals("0x0000")) {
                    cartCount = jsonObject.getJSONObject("info").getInt("cart_items_count");

                    CartProductsParser parser = new CartProductsParser();
                    list.addAll(parser.parse(jsonObject.getJSONObject("info").getJSONArray("cart_items")));

                    priceInfoItems = new ArrayList<>();
                    JSONArray prices = jsonObject.getJSONObject("info").getJSONArray("price_infos");

                    for (int k = 0; k < prices.length(); k++) {
                        PriceInfoItem infoItem = new PriceInfoItem();
                        infoItem.setCurrency(prices.getJSONObject(k).getString("currency"));
                        infoItem.setPrice(prices.getJSONObject(k).getString("price"));
                        infoItem.setTitle(prices.getJSONObject(k).getString("title"));
                        infoItem.setType(prices.getJSONObject(k).getString("type"));
                        infoItem.setPosition(prices.getJSONObject(k).getInt("position"));

                        priceInfoItems.add(infoItem);
                    }
                }
            } catch (JSONException e) {
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
            SpannableString str = new SpannableString(getResources().getString(R.string.cart_empty_cart));
            str.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((TextView) findViewById(R.id.empty_cart_txt)).setText(str);
            if (list.size() > 0) {
                (act.findViewById(R.id.checkoutRel).findViewById(R.id.checkout_from_cart_btn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(act, CheckoutActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
                if (cartCount != 1) {
                    SpannableString str1 = new SpannableString(String.format(getResources().getString(R.string.cart_count_products_msg), cartCount));
                    str1.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, str1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((TextView) findViewById(R.id.txtTotalItems)).setText(str1);
                }else {
                    SpannableString str1 = new SpannableString(String.format(getResources().getString(R.string.cart_count_product_msg), cartCount));
                    str1.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, str1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((TextView) findViewById(R.id.txtTotalItems)).setText(str1);
                }

                adapter = new CartCardAdapter(act, list);
                PopupMenu.OnMenuItemClickListener listener = new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.qtyChange: {
                                final EditText input = new EditText(act);
                                input.setFilters(new InputFilter[]{
                                        new InputFilter.LengthFilter(5),
                                        DigitsKeyListener.getInstance()
                                });
                                input.setKeyListener(DigitsKeyListener.getInstance());
                                new AlertDialog.Builder(act)
                                        .setTitle(getResources().getString(R.string.txt_amount_promo_2))
                                        .setMessage(getResources().getString(R.string.txt_give_amount))
                                        .setView(input)
                                        .setCancelable(false)
                                        .setPositiveButton(getResources().getString(R.string.txt_ok_btn_qty), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                if (!input.getText().toString().equals("")) {
                                                    String txt = String.valueOf(Integer.valueOf(input.getText().toString()));
                                                    if (Integer.valueOf(txt) > 0)
                                                        if(noNetworkHandler.showDialog())
                                                            new UpdateQtyCartItem().execute(list.get(adapter.getItem_pressed()).getCart_item_id(), txt);
                                                    else
                                                        dialog.dismiss();
                                                } else {
                                                    dialog.dismiss();
                                                }
                                            }
                                        }).setNegativeButton(getResources().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                    }
                                }).show();
                                break;
                            }
                            case R.id.removeItem: {
                                if(noNetworkHandler.showDialog())
                                    new RemoveCartItem().execute(list.get(adapter.getItem_pressed()).getCart_item_id());
                                break;
                            }
                        }
                        return true;
                    }
                };
                adapter.setListener(listener);
                listView.setAdapter(adapter);
                //listView.setExpanded(true);
                //Helper.getListViewSize(listView, act);

                LinearLayout pricesInfoLay = (LinearLayout) findViewById(R.id.txtTotals);

                for (int k = 0; k < priceInfoItems.size(); k++) {
                    TextView txt = new TextView(act);
                    txt.setText(priceInfoItems.get(k).getTitle() + ": " + StoreApplication.getCurrency_symbol(act) + priceInfoItems.get(k).getPrice());
                    if (priceInfoItems.get(k).getType().equals("total")) {
                        txt.setTypeface(null, Typeface.BOLD);
                        txt.setTextSize(20);

                    }
                    txt.setGravity(Gravity.RIGHT);
                    pricesInfoLay.addView(txt);
                }
                act.findViewById(R.id.scrollViewCart).setVisibility(View.VISIBLE);
                act.findViewById(R.id.checkoutRel).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.empty_cart_rel).setVisibility(View.VISIBLE);
                //((TextView) findViewById(R.id.empty_cart_txt)).setTypeface(Typeface.createFromAsset(act.getAssets(), "fonts/comiccity.ttf"), Typeface.BOLD);
            }
        }
    }

    class UpdateQtyCartItem extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                String response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_update_cart_item), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), URLEncoder.encode(strings[0], "UTF-8"), URLEncoder.encode(strings[1], "UTF-8")), ServiceHandler.GET);

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
            if (result) {
                if(noNetworkHandler.showDialog())
                    new GetCartInfo().execute();
            }
        }
    }

    class RemoveCartItem extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                String response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_remove_cart_item), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), URLEncoder.encode(strings[0], "UTF-8")), ServiceHandler.GET);

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
            if (result) {
                if(noNetworkHandler.showDialog())
                    new GetCartInfo().execute();
            }
        }
    }

    class ClearShoppingCart extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_clear_cart), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", "")), ServiceHandler.GET);

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
            if (result) {
                if(noNetworkHandler.showDialog())
                    new GetCartInfo().execute();
            }
        }
    }
}
