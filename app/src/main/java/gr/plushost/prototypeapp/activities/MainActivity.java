package gr.plushost.prototypeapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.utils.AutoResizeTextView;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.adapters.listviews.PopularItemsMainListAdapter;
import gr.plushost.prototypeapp.adapters.viewpagers.BannersImageSlideAdapter;
import gr.plushost.prototypeapp.adapters.listviews.CategoriesListAdapter;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.exceptionhandlers.StoreExceptionHandler;
import gr.plushost.prototypeapp.fragments.NavigationDrawerFragment;
import gr.plushost.prototypeapp.indicators.CirclePageIndicator;
import gr.plushost.prototypeapp.items.BannerItem;
import gr.plushost.prototypeapp.items.CategoryItem;
import gr.plushost.prototypeapp.items.MiniProductItem;
import gr.plushost.prototypeapp.listeners.CartMenuItemStuffListener;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;
import gr.plushost.prototypeapp.util.Helper;
import gr.plushost.prototypeapp.widgets.ExpandableHeightGridView;
import gr.plushost.prototypeapp.widgets.ExpandableHeightListView;
import gr.plushost.prototypeapp.widgets.HorizontalListView;
import gr.plushost.prototypeapp.widgets.LockableScrollView;
import gr.plushost.prototypeapp.widgets.NestedGridView;
import gr.plushost.prototypeapp.widgets.NestedListView;
import gr.plushost.prototypeapp.widgets.SmartViewPager;


public class MainActivity extends DrawerActivity {

    Activity act = this;
    List<CategoryItem> categoriesList;
    ArrayList<String> bannerImgs;
    NestedListView listView;
    CategoriesListAdapter categoriesListAdapter;
    SmartViewPager viewPagerImg;
    CirclePageIndicator circlePageIndicator;
    TextView searchTextView;
    boolean doubleBackToExitPressedOnce;
    LockableScrollView lockableScrollView;
    List<BannerItem> bannersList;
    int hot_number = 0;
    TextView ui_hot = null;
    NoNetworkHandler noNetworkHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            //overridePendingTransition(0, R.anim.slide_out_bottom);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
        noNetworkHandler = new NoNetworkHandler(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        fragmentTransaction.add(R.id.nav_drawer_fragment, fragment);
        fragmentTransaction.commit();
        super.set(toolbar, true, fragment);

        ((TextView) findViewById(R.id.phonetxt)).setText(getResources().getString(R.string.txt_phone_title) +  " " + getResources().getString(R.string.store_phone));

        searchTextView = (TextView) findViewById(R.id.searchTextView);
        searchTextView.setHint(String.format(getResources().getString(R.string.search_text_on_box), getResources().getString(R.string.store_name)));

        /*if (!ServiceHandler.isNetworkAvailable(this)) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.not_internet_title));
            alertDialogBuilder
                    .setMessage(getResources().getString(R.string.not_internet_msg))
                    .setCancelable(false)
                    .setIcon(R.drawable.sing)
                    .setPositiveButton(getResources().getString(R.string.not_internet_ok_btn), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.not_internet_settings_btn), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }*/

        listView = (NestedListView) findViewById(R.id.list_categories);
        viewPagerImg = (SmartViewPager) findViewById(R.id.viewPagerImg);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicatorImg);
        lockableScrollView = (LockableScrollView) findViewById(R.id.mainScrollView);

        //String jsonMyObject;
        /*Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("categories_list");
            if (new Gson().fromJson(jsonMyObject, new TypeToken<List<CategoryItem>>() {
            }.getType()) instanceof ArrayList) {*/
        categoriesList = StoreApplication.getInstance().getCategoryItemList(); //(ArrayList<CategoryItem>) new Gson().fromJson(jsonMyObject, new TypeToken<List<CategoryItem>>() {
                /*}.getType());
            }

            jsonMyObject = extras.getString("banners_list");
            if (new Gson().fromJson(jsonMyObject, new TypeToken<List<BannerItem>>() {
            }.getType()) instanceof ArrayList) {*/
        bannersList = StoreApplication.getInstance().getBannerItemList();//(ArrayList<BannerItem>) new Gson().fromJson(jsonMyObject, new TypeToken<List<BannerItem>>() {
                /*}.getType());
            }
        }*/

        if (categoriesList.size() > 0) {
            categoriesListAdapter = new CategoriesListAdapter(this, categoriesList, -1);

            listView.setAdapter(categoriesListAdapter);
            Helper.getListViewSize(listView, act);
            //listView.setExpanded(true);
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


            bannerImgs = new ArrayList<>();
            for (BannerItem bannerItem : bannersList) {
                bannerImgs.add(bannerItem.getImg_url());
            }

            viewPagerImg.setAdapter(new BannersImageSlideAdapter(this, bannerImgs));
            viewPagerImg.runnable(bannerImgs.size(), 7000);
            //viewPagerImg.setPageTransformer(true, new ZoomOutSlideTransformer());
            circlePageIndicator.setFillColor(Color.parseColor("#FF6600"));
            circlePageIndicator.setPageColor(Color.parseColor("#AAEEEEEE"));
            circlePageIndicator.setStrokeColor(Color.parseColor("#AAEEEEEE"));
            circlePageIndicator.setViewPager(viewPagerImg);

            listView.setFocusable(false);

            if(StoreApplication.getInstance().getFreeShippingPromotionItem().isHas_free_ship()){
                findViewById(R.id.freeShipRel).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.freeshiptxt2)).setText(String.format(getResources().getString(R.string.txt_freeship_body), StoreApplication.getCurrency_symbol(act) + StoreApplication.getInstance().getFreeShippingPromotionItem().getValue()));
            }
            else{
                findViewById(R.id.freeShipRel).setVisibility(View.GONE);
            }

            if(noNetworkHandler.showDialog())
                new GetCartCount().execute();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        viewPagerImg.setSliding(true);

        if(noNetworkHandler.showDialog())
            new GetCartCount().execute();
    }

    public void onPause() {
        super.onPause();

        viewPagerImg.setSliding(false);
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
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finish();
                overridePendingTransition(0, R.anim.slide_out_bottom);
                /*Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);*/
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            SuperToast.create(this, getResources().getString(R.string.back_toast_warning_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.PURPLE, SuperToast.Animations.POPUP)).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    public void openSearch(View view) {
        Intent i = new Intent(this, SearchActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void openBtn1(View view) {
        Intent browserIntent = new Intent(this, WebViewActivity.class);
        browserIntent.putExtra("title", act.getResources().getString(R.string.btn_txt_1));
        browserIntent.putExtra("url", act.getResources().getString(R.string.btn_url_1));
        browserIntent.putExtra("user_agent", "pc");
        startActivity(browserIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void openBtn2(View view) {
        Intent browserIntent = new Intent(this, WebViewActivity.class);
        browserIntent.putExtra("title", act.getResources().getString(R.string.btn_txt_2));
        browserIntent.putExtra("url", act.getResources().getString(R.string.btn_url_2));
        browserIntent.putExtra("user_agent", "pc");
        startActivity(browserIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void openBtn3(View view) {
        Intent browserIntent = new Intent(this, WebViewActivity.class);
        browserIntent.putExtra("title", act.getResources().getString(R.string.btn_txt_3));
        browserIntent.putExtra("url", act.getResources().getString(R.string.btn_url_3));
        browserIntent.putExtra("user_agent", "pc");
        startActivity(browserIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void openBtn4(View view) {
        Intent browserIntent = new Intent(this, WebViewActivity.class);
        browserIntent.putExtra("title", act.getResources().getString(R.string.btn_txt_4));
        browserIntent.putExtra("url", act.getResources().getString(R.string.btn_url_4));
        browserIntent.putExtra("user_agent", "mobile");
        startActivity(browserIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void openBtn5(View view) {
        Intent browserIntent = new Intent(this, WebViewActivity.class);
        browserIntent.putExtra("title", act.getResources().getString(R.string.btn_txt_5));
        browserIntent.putExtra("url", act.getResources().getString(R.string.btn_url_5));
        browserIntent.putExtra("user_agent", "mobile");
        startActivity(browserIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void openOroi(View view) {
        Intent browserIntent = new Intent(this, WebViewActivity.class);
        browserIntent.putExtra("title", getResources().getString(R.string.txt_btn_terms));
        browserIntent.putExtra("url", act.getResources().getString(R.string.txt_url_terms));
        browserIntent.putExtra("user_agent", "pc");
        startActivity(browserIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void openProsopika(View view) {
        Intent browserIntent = new Intent(this, WebViewActivity.class);
        browserIntent.putExtra("title", getResources().getString(R.string.txt_btn_privacy));
        browserIntent.putExtra("url", act.getResources().getString(R.string.txt_url_privacy));
        browserIntent.putExtra("user_agent", "pc");
        startActivity(browserIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void openStoreSite(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.auth_host)));
        startActivity(browserIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void openSocial1(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.social_url_1)));
        startActivity(browserIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void openSocial2(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.social_url_2)));
        startActivity(browserIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void openSocial3(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.social_url_3)));
        startActivity(browserIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void openSocial4(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.social_url_4)));
        startActivity(browserIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
