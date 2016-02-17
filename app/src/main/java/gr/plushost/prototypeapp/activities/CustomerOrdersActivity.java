package gr.plushost.prototypeapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.adapters.listviews.OrdersListAdapter;
import gr.plushost.prototypeapp.adapters.tablefix.MatrixTableAdapter;
import gr.plushost.prototypeapp.adapters.tablefix.OrdersTableAdapter;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.exceptionhandlers.StoreExceptionHandler;
import gr.plushost.prototypeapp.items.CustomerOrderItem;
import gr.plushost.prototypeapp.items.CustomerOrderPriceInfoItem;
import gr.plushost.prototypeapp.items.CustomerOrderStatusItem;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;
import gr.plushost.prototypeapp.parsers.CustomerOrdersParser;
import gr.plushost.prototypeapp.tablefixheaders.TableFixHeaders;
import gr.plushost.prototypeapp.util.Helper;
import gr.plushost.prototypeapp.widgets.ExpandableHeightListView;
import gr.plushost.prototypeapp.widgets.NestedListView;

/**
 * Created by billiout on 21/3/2015.
 */
public class CustomerOrdersActivity extends DrawerActivity {
    Activity act = this;
    NestedListView listView;
    List<CustomerOrderItem> list = new ArrayList<>();
    List<CustomerOrderStatusItem> listStatuses = new ArrayList<>();
    List<CheckBox> statusesCheckboxes = new ArrayList<>();
    boolean isFiltersOpen = false;
    OrdersListAdapter adapter;
    boolean firstTime = false;
    NoNetworkHandler noNetworkHandler;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        noNetworkHandler = new NoNetworkHandler(this);
        setContentView(R.layout.activity_orders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        toolbar.setTitle(getResources().getString(R.string.orders_screen_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (NestedListView) findViewById(R.id.ordersListView);
        ((TextView) findViewById(R.id.priceFilterTitle)).setText(String.format(getResources().getString(R.string.orders_search_total), StoreApplication.getCurrency_symbol(this)));
        //listView.setExpanded(true);

        firstTime = true;
        if(noNetworkHandler.showDialog())
            new GetOrdersItems().execute("", "", "", "");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_orders);
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

    public void onFiltersClick(View view) {
        if (isFiltersOpen) {
            RelativeLayout txt = (RelativeLayout) findViewById(R.id.ldesc);
            txt.setVisibility(View.GONE);
            ImageView img = (ImageView) findViewById(R.id.arrow1);
            img.setImageResource(R.drawable.arrow_down_small);
            isFiltersOpen = false;
        } else {
            RelativeLayout txt = (RelativeLayout) findViewById(R.id.ldesc);
            txt.setVisibility(View.VISIBLE);
            ImageView img = (ImageView) findViewById(R.id.arrow1);
            img.setImageResource(R.drawable.arrow_up_small);
            isFiltersOpen = true;
        }
    }

    class GetOrdersItems extends AsyncTask<String, Void, Void> {
        SweetAlertDialog pDialog;
        @Override
        protected void onPreExecute() {
            if (firstTime) {
                pDialog = new SweetAlertDialog(act, SweetAlertDialog.PROGRESS_TYPE, false);
                pDialog.setTitleText(act.getResources().getString(R.string.loadingTxt));
                pDialog.setCancelable(false);
                pDialog.show();
                pDialog.setMultiColorProgressBar(act, true);
                findViewById(R.id.progressBarLoading).setVisibility(View.GONE);
                findViewById(R.id.progressBarRel).setVisibility(View.GONE);
                findViewById(R.id.progressBarLoadingForOrders).setVisibility(View.GONE);
                findViewById(R.id.content_frame).setVisibility(View.GONE);
                act.findViewById(R.id.emptyTxt).setVisibility(View.GONE);
            } else {
                findViewById(R.id.progressBarLoading).setVisibility(View.GONE);
                findViewById(R.id.progressBarRel).setVisibility(View.GONE);
                findViewById(R.id.progressBarLoadingForOrders).setVisibility(View.VISIBLE);
                findViewById(R.id.content_frame).setVisibility(View.VISIBLE);
                act.findViewById(R.id.emptyTxt).setVisibility(View.GONE);
            }
            list.clear();
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (firstTime) {
                    String responseStatuses = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_orders_statuses_get), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", "")), ServiceHandler.GET);

                    JSONObject jsonObject = new JSONObject(responseStatuses);
                    if (jsonObject.getString("code").equals("0x0000")) {
                        JSONArray jsonArray = jsonObject.getJSONObject("info").getJSONArray("statuses");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            CustomerOrderStatusItem statusItem = new CustomerOrderStatusItem();
                            statusItem.setStatus_type(jsonArray.getJSONObject(i).getString("status"));
                            statusItem.setStatus_name(jsonArray.getJSONObject(i).getString("name"));
                            listStatuses.add(statusItem);
                        }
                    }
                }

                String responseOrders = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_orders_get), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), URLEncoder.encode(strings[0], "UTF-8"), URLEncoder.encode(strings[1], "UTF-8"), URLEncoder.encode(strings[2], "UTF-8"), URLEncoder.encode(strings[3], "UTF-8")), ServiceHandler.GET);

                JSONObject jsonObject = new JSONObject(responseOrders);
                if (jsonObject.getString("code").equals("0x0000")) {
                    CustomerOrdersParser parser = new CustomerOrdersParser();
                    list.addAll(parser.parse(jsonObject.getJSONObject("info").getJSONArray("orders")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            findViewById(R.id.progressBarLoading).setVisibility(View.GONE);
            findViewById(R.id.progressBarRel).setVisibility(View.GONE);
            findViewById(R.id.progressBarLoadingForOrders).setVisibility(View.GONE);
            if(pDialog != null && pDialog.isShowing()){
                pDialog.dismissWithAnimation();
                pDialog.setMultiColorProgressBar(act, false);
            }
            findViewById(R.id.content_frame).setVisibility(View.VISIBLE);
            if (listStatuses.size() > 0 && firstTime) {
                LinearLayout statusesLay = (LinearLayout) act.findViewById(R.id.statusFilterBody);
                for (CustomerOrderStatusItem statusItem : listStatuses) {
                    CheckBox checkBox = new CheckBox(act);
                    checkBox.setText(statusItem.getStatus_name());
                    statusesLay.addView(checkBox);
                    statusesCheckboxes.add(checkBox);
                    firstTime = false;
                }
            }


            if (list.size() > 0) {
                act.findViewById(R.id.emptyTxt).setVisibility(View.GONE);
                adapter = new OrdersListAdapter(act, list);
                listView.setAdapter(adapter);
                //Helper.getListViewSize(listView, act);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(act, CustomerOrderActivity.class);
                        intent.putExtra("order_id", list.get(i).getOrder_id());
                        intent.putExtra("display_id", list.get(i).getDisplay_id());
                        act.startActivity(intent);
                        act.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });

                BootstrapButton searchBtn = (BootstrapButton) act.findViewById(R.id.btnSearch);

                searchBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JSONArray statuses = new JSONArray();
                        for (int i = 0; i < statusesCheckboxes.size(); i++) {
                            CheckBox checkBox = statusesCheckboxes.get(i);
                            if (checkBox.isChecked()) {
                                statuses.put(listStatuses.get(i).getStatus_type());
                            }
                        }

                        String from = ((BootstrapEditText) act.findViewById(R.id.total_from)).getText().toString();
                        String to = ((BootstrapEditText) act.findViewById(R.id.total_to)).getText().toString();
                        String id = ((BootstrapEditText) act.findViewById(R.id.order_id)).getText().toString();

                        if(noNetworkHandler.showDialog())
                            new GetOrdersItems().execute(from, to, id, statuses.toString());
                    }
                });
            } else {
                act.findViewById(R.id.emptyTxt).setVisibility(View.VISIBLE);
                ((TextView) act.findViewById(R.id.emptyTxt)).setText(getResources().getString(R.string.orders_no_orders));
            }
        }
    }
}
