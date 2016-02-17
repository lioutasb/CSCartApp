package gr.plushost.prototypeapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.adapters.listviews.CategoriesListAdapter;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.exceptionhandlers.StoreExceptionHandler;
import gr.plushost.prototypeapp.items.BannerItem;
import gr.plushost.prototypeapp.items.CategoryItem;
import gr.plushost.prototypeapp.items.FreeShippingPromotionItem;
import gr.plushost.prototypeapp.items.SortOptionItem;
import gr.plushost.prototypeapp.items.StoreCurrencyItem;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;
import gr.plushost.prototypeapp.parsers.BannersParser;
import gr.plushost.prototypeapp.parsers.CategoriesParser;
import gr.plushost.prototypeapp.parsers.CountriesParser;
import gr.plushost.prototypeapp.parsers.CurrenciesParser;
import gr.plushost.prototypeapp.parsers.FreeShippingPromotionParser;
import gr.plushost.prototypeapp.parsers.LanguagesParser;
import gr.plushost.prototypeapp.parsers.ZonesParser;

/**
 * Created by billiout on 20/2/2015.
 */
public class SplashScreenActivity extends AppCompatActivity {
    Activity act = this;
    TextView txtInfo;
    List<CategoryItem> list = new ArrayList<>();
    NoNetworkHandler noNetworkHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        noNetworkHandler = new NoNetworkHandler(this);

        txtInfo = (TextView) findViewById(R.id.txtInfo);
        txtInfo.setText(String.format(getResources().getString(R.string.splash_info_msg), Calendar.getInstance().get(Calendar.YEAR) > 2013 ? Calendar.getInstance().get(Calendar.YEAR) : "2014", getResources().getString(R.string.store_site)));

        if (noNetworkHandler.isNetworkAvailable(this)) {
            new GetInfoForStart().execute();
        } else {
            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.zoom_out, R.anim.zoom_in);
            System.gc();
            finish();
            System.gc();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_splash);
    }

    class GetInfoForStart extends AsyncTask<Void, Void, Void> {
        List<BannerItem> bannerItemList = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... params) {
            String responseCategories = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_categories), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), "0"), ServiceHandler.GET);

            try {
                JSONObject initialObj = new JSONObject(responseCategories);
                if (initialObj.getString("result").equals("success")) {
                    CategoriesParser parser = new CategoriesParser();
                    list = parser.parse(initialObj.getJSONObject("info").getJSONArray("item_cats"));
                    StoreApplication.getInstance().setCategoryItemList(list);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String responseTestLogin = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_get_user_name), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", "")), ServiceHandler.GET);

            try {
                JSONObject initialObj = new JSONObject(responseTestLogin);
                if (!initialObj.getString("result").equals("success")) {
                    act.getSharedPreferences("ShopPrefs", 0).edit().putBoolean("is_connected", false).apply();
                } else {
                    act.getSharedPreferences("ShopPrefs", 0).edit().putString("user_name", initialObj.getJSONObject("info").getString("firstname") + " " + initialObj.getJSONObject("info").getString("lastname")).apply();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String responseStoreInfo = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_store_info), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", "")), ServiceHandler.GET);

            try {
                JSONObject initialObj = new JSONObject(responseStoreInfo);
                if (initialObj.getString("result").equals("success")) {
                    CountriesParser countriesParser = new CountriesParser();
                    ZonesParser zonesParser = new ZonesParser();
                    LanguagesParser languagesParser = new LanguagesParser();
                    CurrenciesParser currenciesParser = new CurrenciesParser();

                    StoreApplication.getInstance().setCountryItemList(countriesParser.parse(initialObj.getJSONObject("info").getJSONObject("store_info").getJSONArray("countries")));
                    StoreApplication.getInstance().setZoneItemList(zonesParser.parse(initialObj.getJSONObject("info").getJSONObject("store_info").getJSONArray("zones")));
                    StoreApplication.getInstance().setLanguageItemList(languagesParser.parse(initialObj.getJSONObject("info").getJSONObject("store_info").getJSONArray("languages")));
                    StoreApplication.getInstance().setCurrencyItemList(currenciesParser.parse(initialObj.getJSONObject("info").getJSONObject("store_info").getJSONArray("currencies")));

                    List<StoreCurrencyItem> currencyItemList = StoreApplication.getInstance().getCurrencyItemList();
                    for (StoreCurrencyItem item : currencyItemList) {
                        if (item.getCurrency_code().equals(act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""))) {
                            StoreApplication.setCurrency_symbol(act, item.getCurrency_symbol());
                        }
                    }
                }

                //JSONObject initialObj = new JSONObject(responseStoreInfo);
                if (initialObj.getString("result").equals("success")) {
                    BannersParser parser = new BannersParser();
                    bannerItemList = parser.parse(initialObj.getJSONObject("info").getJSONArray("banners"));
                    StoreApplication.getInstance().setBannerItemList(bannerItemList);
                }

                //JSONObject initialObj = new JSONObject(responseStoreInfo);
                if (initialObj.getString("result").equals("success")) {
                    List<SortOptionItem> sorts = new ArrayList<>();

                    JSONArray array = initialObj.getJSONObject("info").getJSONObject("store_info").getJSONArray("category_sort_options");
                    for(int i = 0; i < array.length(); i++){
                        SortOptionItem optionItem = new SortOptionItem();
                        optionItem.setTitle(array.getJSONObject(i).getString("title"));
                        String[] tmp = array.getJSONObject(i).getString("code").split(":");
                        optionItem.setSort_by(tmp[0]);
                        optionItem.setSort_order(tmp[1]);
                        sorts.add(optionItem);
                    }
                    StoreApplication.getInstance().setSortsItemList(sorts);
                }
                //JSONObject initialObj = new JSONObject(responseStoreInfo);
                if (initialObj.getString("result").equals("success")) {
                    FreeShippingPromotionParser parser = new FreeShippingPromotionParser();
                    FreeShippingPromotionItem freeShippingPromotionItem = parser.parse(initialObj.getJSONObject("info").getJSONObject("free_shipping"));
                    StoreApplication.getInstance().setFreeShippingPromotionItem(freeShippingPromotionItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(act.getSharedPreferences("ShopPrefs", 0).getBoolean("first_time", true)){
                Intent i = new Intent(SplashScreenActivity.this, IntroActivity.class);
                //i.putExtra("categories_list", new Gson().toJson(list));
                //i.putExtra("banners_list", new Gson().toJson(bannerItemList));
                startActivity(i);
                overridePendingTransition(R.anim.zoom_out, R.anim.zoom_in);
                System.gc();
                finish();
                System.gc();
            }
            else {
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                //i.putExtra("categories_list", new Gson().toJson(list));
                //i.putExtra("banners_list", new Gson().toJson(bannerItemList));
                startActivity(i);
                overridePendingTransition(R.anim.zoom_out, R.anim.zoom_in);
                System.gc();
                finish();
                System.gc();
            }
        }
    }
}
