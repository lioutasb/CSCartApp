package gr.plushost.prototypeapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.adapters.listviews.CategoriesListAdapter;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.exceptionhandlers.StoreExceptionHandler;
import gr.plushost.prototypeapp.fragments.NavigationDrawerFragment;
import gr.plushost.prototypeapp.items.CategoryItem;
import gr.plushost.prototypeapp.listeners.CartMenuItemStuffListener;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;
import gr.plushost.prototypeapp.parsers.CategoriesParser;

/**
 * Created by billiout on 22/2/2015.
 */
public class CategoriesActivity extends DrawerActivity {

    Activity act = this;
    List<CategoryItem> categoriesList;
    ListView listView;
    CategoriesListAdapter categoriesListAdapter;
    int hot_number = 0;
    TextView ui_hot = null;
    int cid = 0;
    String c_name = "";
    NoNetworkHandler noNetworkHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noNetworkHandler = new NoNetworkHandler(this);
        setContentView(R.layout.activity_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        fragmentTransaction.add(R.id.nav_drawer_fragment, fragment);
        fragmentTransaction.commit();
        super.set(toolbar, true, fragment);

        listView = (ListView) findViewById(R.id.list_categories);

        String jsonMyObject;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            /*jsonMyObject = extras.getString("categories_list");
            if (new Gson().fromJson(jsonMyObject, new TypeToken<List<CategoryItem>>() {
            }.getType()) instanceof ArrayList) {
                categoriesList = new Gson().fromJson(jsonMyObject, new TypeToken<List<CategoryItem>>() {
                }.getType());
            }*/
            c_name = extras.getString("category_name");
            cid = extras.getInt("parent_id");
        }
        /*categoriesListAdapter = new CategoriesListAdapter(this, categoriesList, extras != null ? extras.getInt("parent_id") : 0);
        listView.setAdapter(categoriesListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<CategoryItem> itemList = categoriesListAdapter.getList();
                CategoryItem item = itemList.get(position);

                if (item.is_parent()) {
                    Intent i = new Intent(act, CategoriesActivity.class);
                    i.putExtra("categories_list", new Gson().toJson(categoriesList));
                    i.putExtra("parent_id", item.getID());
                    i.putExtra("category_name", item.getTitle());
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Intent i = new Intent(act, ProductsActivity.class);
                    i.putExtra("category_id", item.getID());
                    i.putExtra("category_name", item.getTitle());
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });*/

        getSupportActionBar().setTitle(extras != null ? extras.getString("category_name") : "");

        if(noNetworkHandler.showDialog()) {
            new GetCartCount().execute();
            new GetCategoryInfo().execute();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_categories);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_page_normal, menu);
        menu.findItem(R.id.action_cart).setActionView(R.layout.layout_notification_cart_icon);
        final View menu_hotlist = MenuItemCompat.getActionView(menu.findItem(R.id.action_cart));//.getActionView();
        ui_hot = (TextView) menu_hotlist.findViewById(R.id.hotlist_hot);
        updateHotCount(StoreApplication.getCartCount());
        new CartMenuItemStuffListener(menu_hotlist, getResources().getString(R.string.menu_cart_name)) {
            @Override
            public void onClick(View v) {
                if (getSharedPreferences("ShopPrefs", 0).getBoolean("is_connected", false)) {
                    Intent i = new Intent(act, ShoppingCartActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Intent i = new Intent(act, LoginActivity.class);
                    i.putExtra("page", 0);
                    startActivity(i);
                }
            }
        };
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(act, SettingsActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return true;
        } else if (id == R.id.action_search) {
            Intent i = new Intent(this, SearchActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateHotCount(final int new_hot_number) {
        hot_number = new_hot_number;
        if (ui_hot == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (new_hot_number == 0)
                    ui_hot.setVisibility(View.INVISIBLE);
                else {
                    ui_hot.setVisibility(View.VISIBLE);
                    if (new_hot_number < 10)
                        ui_hot.setText(Integer.toString(new_hot_number));
                    else
                        ui_hot.setText("9+");
                }
            }
        });
    }

    public void onResume() {
        super.onResume();

        if(noNetworkHandler.showDialog()) {
            new GetCartCount().execute();
        }
    }

    class GetCategoryInfo extends AsyncTask<Void, Void, Void> {
        SweetAlertDialog pDialog;

        @Override
        protected void onPreExecute(){
            act.findViewById(R.id.content_frame).setVisibility(View.GONE);
            pDialog = new SweetAlertDialog(act, SweetAlertDialog.PROGRESS_TYPE, false);
            pDialog.setTitleText(act.getResources().getString(R.string.loadingTxt));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setMultiColorProgressBar(act, true);
            categoriesList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String responseCategories = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_categories), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), String.valueOf(cid)), ServiceHandler.GET);

            try {
                JSONObject initialObj = new JSONObject(responseCategories);
                if (initialObj.getString("result").equals("success")) {
                    CategoriesParser parser = new CategoriesParser();
                    categoriesList = parser.parse(initialObj.getJSONObject("info").getJSONArray("item_cats"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if(pDialog != null && pDialog.isShowing()){
                pDialog.dismissWithAnimation();
                pDialog.setMultiColorProgressBar(act, false);
            }
            if(categoriesList.size() > 0) {
                act.findViewById(R.id.content_frame).setVisibility(View.VISIBLE);
                categoriesListAdapter = new CategoriesListAdapter(act, categoriesList, cid);
                listView.setAdapter(categoriesListAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        List<CategoryItem> itemList = categoriesListAdapter.getList();
                        CategoryItem item = itemList.get(position);

                        if (item.is_parent()) {
                            Intent i = new Intent(act, CategoriesActivity.class);
                            //i.putExtra("categories_list", new Gson().toJson(categoriesList));
                            i.putExtra("parent_id", item.getID());
                            i.putExtra("category_name", item.getTitle());
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            Intent i = new Intent(act, ProductsActivity.class);
                            i.putExtra("category_id", item.getID());
                            i.putExtra("category_name", item.getTitle());
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }
                });
            }
        }
    }

    public class GetCartCount extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute(){
            updateHotCount(StoreApplication.getCartCount());
        }

        @Override
        protected Integer doInBackground(Void... voids) {
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
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            StoreApplication.setCartCount(result);
            updateHotCount(result);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}
