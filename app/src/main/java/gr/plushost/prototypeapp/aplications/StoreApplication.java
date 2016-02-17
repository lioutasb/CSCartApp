package gr.plushost.prototypeapp.aplications;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.items.BannerItem;
import gr.plushost.prototypeapp.items.CategoryItem;
import gr.plushost.prototypeapp.items.CountryItem;
import gr.plushost.prototypeapp.items.FreeShippingPromotionItem;
import gr.plushost.prototypeapp.items.SortOptionItem;
import gr.plushost.prototypeapp.items.StoreCurrencyItem;
import gr.plushost.prototypeapp.items.StoreLanguageItem;
import gr.plushost.prototypeapp.items.ZoneItem;
import gr.plushost.prototypeapp.network.ExSSLSocketFactory;
import gr.plushost.prototypeapp.network.ServiceHandler;

/**
 * Created by billiout on 5/3/2015.
 */
public class StoreApplication extends Application {
    private static StoreApplication singleton;
    private static OkHttpClient httpClient;
    private static int cartCount = 0;
    private static ServiceHandler serviceHandler = null;

    public static ServiceHandler getServiceHandler(Context context) {
        if(serviceHandler == null)
            serviceHandler = new ServiceHandler();
        return serviceHandler;
    }

    public static int getCartCount() {
        return cartCount;
    }

    public static void setCartCount(int cartCount) {
        StoreApplication.cartCount = cartCount;
    }

    public static String getCurrency_symbol(Context ctx) {
        return ctx.getSharedPreferences("ShopPrefs", 0).getString("store_currency_symbol", "");
    }

    public FreeShippingPromotionItem getFreeShippingPromotionItem() {
        FreeShippingPromotionItem freeShippingPromotionItem = new FreeShippingPromotionItem();
        try {
            SharedPreferences mSharedPreference1 = this.getSharedPreferences("FreeShippingPromotionItem", 0);

            freeShippingPromotionItem = new Gson().fromJson(mSharedPreference1.getString("Status", null), FreeShippingPromotionItem.class);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return freeShippingPromotionItem;
    }

    public void setFreeShippingPromotionItem(FreeShippingPromotionItem freeShippingPromotionItem) {
        SharedPreferences sp = this.getSharedPreferences("FreeShippingPromotionItem", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();

        mEdit1.remove("Status");
        mEdit1.putString("Status", new Gson().toJson(freeShippingPromotionItem));

        mEdit1.apply();
    }


    public static void setCurrency_symbol(Context ctx, String currency_symbol) {
        ctx.getSharedPreferences("ShopPrefs", 0).edit().putString("store_currency_symbol", currency_symbol).apply();
    }

    public List<StoreCurrencyItem> getCurrencyItemList() {
        List<StoreCurrencyItem> currencyItemList = new ArrayList<>();
        try {
            SharedPreferences mSharedPreference1 = this.getSharedPreferences("CurrenciesList", 0);
            int size = mSharedPreference1.getInt("Status_size", 0);

            for (int i = 0; i < size; i++) {
                currencyItemList.add(new Gson().fromJson(mSharedPreference1.getString("Status_" + i, null), StoreCurrencyItem.class));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return currencyItemList;
    }

    public void setCurrencyItemList(List<StoreCurrencyItem> currencyItemList) {
        SharedPreferences sp = this.getSharedPreferences("CurrenciesList", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.putInt("Status_size", currencyItemList.size()); /* sKey is an array */

        for(int i=0;i<currencyItemList.size();i++)
        {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, new Gson().toJson(currencyItemList.get(i)));
        }

        mEdit1.apply();
    }

    public List<StoreLanguageItem> getLanguageItemList() {
        List<StoreLanguageItem> languageItemList = new ArrayList<>();
        try {
            SharedPreferences mSharedPreference1 = this.getSharedPreferences("LanguagesList", 0);
            int size = mSharedPreference1.getInt("Status_size", 0);

            for (int i = 0; i < size; i++) {
                languageItemList.add(new Gson().fromJson(mSharedPreference1.getString("Status_" + i, null), StoreLanguageItem.class));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return languageItemList;
    }

    public void setLanguageItemList(List<StoreLanguageItem> languageItemList) {
        SharedPreferences sp = this.getSharedPreferences("LanguagesList", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.putInt("Status_size", languageItemList.size()); /* sKey is an array */

        for(int i=0;i<languageItemList.size();i++)
        {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, new Gson().toJson(languageItemList.get(i)));
        }

        mEdit1.apply();
    }

    public List<ZoneItem> getZoneItemList() {
        List<ZoneItem> zoneItemList = new ArrayList<>();
        try {
            SharedPreferences mSharedPreference1 = this.getSharedPreferences("ZonesList", 0);
            int size = mSharedPreference1.getInt("Status_size", 0);

            for (int i = 0; i < size; i++) {
                zoneItemList.add(new Gson().fromJson(mSharedPreference1.getString("Status_" + i, null), ZoneItem.class));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return zoneItemList;
    }

    public void setZoneItemList(List<ZoneItem> zoneItemList) {
        SharedPreferences sp = this.getSharedPreferences("ZonesList", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.putInt("Status_size", zoneItemList.size()); /* sKey is an array */

        for(int i=0;i<zoneItemList.size();i++)
        {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, new Gson().toJson(zoneItemList.get(i)));
        }

        mEdit1.apply();
    }

    public List<CountryItem> getCountryItemList() {
        List<CountryItem> countryItemList = new ArrayList<>();
        try {
            SharedPreferences mSharedPreference1 = this.getSharedPreferences("CountriesList", 0);
            int size = mSharedPreference1.getInt("Status_size", 0);

            for (int i = 0; i < size; i++) {
                countryItemList.add(new Gson().fromJson(mSharedPreference1.getString("Status_" + i, null), CountryItem.class));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return countryItemList;
    }

    public void setCountryItemList(List<CountryItem> countryItemList) {
        SharedPreferences sp = this.getSharedPreferences("CountriesList", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.putInt("Status_size", countryItemList.size()); /* sKey is an array */

        for(int i=0;i<countryItemList.size();i++)
        {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, new Gson().toJson(countryItemList.get(i)));
        }

        mEdit1.apply();
    }

    public List<CategoryItem> getCategoryItemList() {
        List<CategoryItem> categoryItemList = new ArrayList<>();
        try {
            SharedPreferences mSharedPreference1 = this.getSharedPreferences("CategoryList", 0);
            int size = mSharedPreference1.getInt("Status_size", 0);

            for (int i = 0; i < size; i++) {
                categoryItemList.add(new Gson().fromJson(mSharedPreference1.getString("Status_" + i, null), CategoryItem.class));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return categoryItemList;
    }

    public void setCategoryItemList(List<CategoryItem> categoryItemList) {
        SharedPreferences sp = this.getSharedPreferences("CategoryList", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.putInt("Status_size", categoryItemList.size()); /* sKey is an array */

        for(int i=0;i<categoryItemList.size();i++)
        {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, new Gson().toJson(categoryItemList.get(i)));
        }

        mEdit1.apply();
    }

    public List<BannerItem> getBannerItemList() {
        List<BannerItem> bannerItemList = new ArrayList<>();
        try {
            SharedPreferences mSharedPreference1 = this.getSharedPreferences("BannerList", 0);
            int size = mSharedPreference1.getInt("Status_size", 0);

            for (int i = 0; i < size; i++) {
                bannerItemList.add(new Gson().fromJson(mSharedPreference1.getString("Status_" + i, null), BannerItem.class));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return bannerItemList;
    }

    public void setBannerItemList(List<BannerItem> bannerItemList) {
        SharedPreferences sp = this.getSharedPreferences("BannerList", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.putInt("Status_size", bannerItemList.size()); /* sKey is an array */

        for(int i=0;i<bannerItemList.size();i++)
        {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, new Gson().toJson(bannerItemList.get(i)));
        }

        mEdit1.apply();
    }

    public List<SortOptionItem> getSortsItemList() {
        List<SortOptionItem> sortsItemList = new ArrayList<>();
        try {
            SharedPreferences mSharedPreference1 = this.getSharedPreferences("SortsList", 0);
            int size = mSharedPreference1.getInt("Status_size", 0);

            for (int i = 0; i < size; i++) {
                sortsItemList.add(new Gson().fromJson(mSharedPreference1.getString("Status_" + i, null), SortOptionItem.class));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return sortsItemList;
    }

    public void setSortsItemList(List<SortOptionItem> sortsItemList) {
        SharedPreferences sp = this.getSharedPreferences("SortsList", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.putInt("Status_size", sortsItemList.size()); /* sKey is an array */

        for(int i=0;i<sortsItemList.size();i++)
        {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, new Gson().toJson(sortsItemList.get(i)));
        }

        mEdit1.apply();
    }

    public String[] getSortsNameArray(){
        String[] names = new String[getSortsItemList().size()];
        //names[0] = getResources().getString(R.string.no_sort_txt);
        int i = 0;
        for(SortOptionItem item : getSortsItemList()){
            names[i] = item.getTitle();
            i++;
        }
        return names;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        updateLanguage(this);
        updateCurrency(this);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        updateLanguage(this);
        updateCurrency(this);
        if(serviceHandler == null)
            serviceHandler = new ServiceHandler();
        super.onCreate();
        singleton = this;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static void updateLanguage(Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences("ShopPrefs", 0);
        if(prefs.getString("store_language", "").equals(""))
            prefs.edit().putString("store_language", ctx.getResources().getString(R.string.default_language_code)).apply();
        String lang = prefs.getString("store_language", "");
        updateLanguage(ctx, lang);
    }

    public static void updateLanguage(Context ctx, String lang)
    {
        Configuration cfg = new Configuration();
        if (!TextUtils.isEmpty(lang))
            cfg.locale = new Locale(lang);
        else
            cfg.locale = Locale.getDefault();

        ctx.getResources().updateConfiguration(cfg, null);
        ctx.getSharedPreferences("ShopPrefs", 0).edit().putString("store_language", lang).apply();
    }

    public static void updateCurrency(Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences("ShopPrefs", 0);
        if(prefs.getString("store_currency", "").equals(""))
            prefs.edit().putString("store_currency", ctx.getResources().getString(R.string.default_currency_code)).apply();
        String lang = prefs.getString("store_currency", "");
        updateCurrency(ctx, lang);
    }

    public static void updateCurrency(Context ctx, String curr)
    {
        ctx.getSharedPreferences("ShopPrefs", 0).edit().putString("store_currency", curr).apply();
    }

    public static StoreApplication getInstance(){
        return singleton;
    }

    public OkHttpClient getHttpClient(){
        if(httpClient == null)
            httpClient = new OkHttpClient();
        return httpClient;
    }
}
