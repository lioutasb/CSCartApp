package gr.plushost.prototypeapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.DigitsKeyListener;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.adapters.listviews.ReviewsListAdapter;
import gr.plushost.prototypeapp.adapters.tablefix.MatrixTableAdapter;
import gr.plushost.prototypeapp.adapters.viewpagers.ImageProductAdapter;
import gr.plushost.prototypeapp.adapters.viewpagers.RelatedProductsAdapter;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.exceptionhandlers.StoreExceptionHandler;
import gr.plushost.prototypeapp.fragments.NavigationDrawerFragment;
import gr.plushost.prototypeapp.indicators.LinePageIndicator;
import gr.plushost.prototypeapp.items.ProductAttributeItem;
import gr.plushost.prototypeapp.items.ProductAttributeOptionItem;
import gr.plushost.prototypeapp.items.ProductItem;
import gr.plushost.prototypeapp.items.ProductSpecificationItem;
import gr.plushost.prototypeapp.items.ProductTierPriceItem;
import gr.plushost.prototypeapp.items.ReviewItem;
import gr.plushost.prototypeapp.listeners.CartMenuItemStuffListener;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;
import gr.plushost.prototypeapp.parsers.ProductParser;
import gr.plushost.prototypeapp.parsers.ReviewsParser;
import gr.plushost.prototypeapp.tablefixheaders.TableFixHeaders;
import gr.plushost.prototypeapp.util.Strings;
import gr.plushost.prototypeapp.viewanimations.Techniques;
import gr.plushost.prototypeapp.viewanimations.YoYo;
import gr.plushost.prototypeapp.widgets.LabelView;
import gr.plushost.prototypeapp.widgets.SmartViewPager;
import gr.plushost.prototypeapp.widgets.VerticalScrollView;

/**
 * Created by billiout on 28/2/2015.
 */
public class ProductActivity extends DrawerActivity {
    Activity act = this;
    boolean need_download = true;
    ProductItem item;
    boolean isDescExp = true;
    boolean isTexnExp = true;
    int product_id;
    VerticalScrollView scrollView;
    ProgressBar progressBarLoading;
    LinearLayout params;
    double price = 0;
    List<View> attributeViews;
    ListView listViewReviews;
    List<ReviewItem> listReviews = new ArrayList<>();
    int current_review_page = 0;
    int total_reviews = 0;
    View reviews_footer_view;
    boolean first_time_on_reviews = true;
    ReviewsListAdapter reviews_adapter;
    int hot_number = 0;
    TextView ui_hot = null;
    WebView asd = null;
    NoNetworkHandler noNetworkHandler;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        noNetworkHandler = new NoNetworkHandler(this);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        fragmentTransaction.add(R.id.nav_drawer_fragment, fragment);
        fragmentTransaction.commit();
        super.set(toolbar, true, fragment);

        scrollView = (VerticalScrollView) findViewById(R.id.scrollViewProd);
        progressBarLoading = (ProgressBar) findViewById(R.id.progressBarLoading);
        params = (LinearLayout) findViewById(R.id.params);
        reviews_footer_view = getLayoutInflater().inflate(R.layout.list_footer_view_products, null);

        Bundle extras = getIntent().getExtras();
        boolean need_download = extras.getBoolean("need_download");

        if(noNetworkHandler.showDialog())
            new GetCartCount().execute();
        if (!need_download) {
            String jsonMyObject = extras.getString("product_item");
            if (new Gson().fromJson(jsonMyObject, new TypeToken<ProductItem>() {
            }.getType()) instanceof ProductItem) {
                item = new Gson().fromJson(jsonMyObject, new TypeToken<ProductItem>() {
                }.getType());
            }

            getSupportActionBar().setTitle(item.getItem_title());
            updateLayout();
        } else {
            product_id = extras.getInt("product_id");
            getSupportActionBar().setTitle(extras.getString("product_name"));
            if(noNetworkHandler.showDialog())
                new GetProductInfo().execute();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_product);
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
        menu.findItem(R.id.action_share).setVisible(true);
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
        } else if (id == R.id.action_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = this.item.getItem_title() + " | " + getResources().getString(R.string.store_name) + " | " + this.item.getItem_url();
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.store_site) + " | " + this.item.getItem_title());
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, String.format(getResources().getString(R.string.txt_share_title),getResources().getString(R.string.store_name))));
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

        if(noNetworkHandler.showDialog())
            new GetCartCount().execute();
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

    @Override
    public void onPause(){
        super.onPause();
        if(asd != null)
            asd.onPause();
    }

    public void nextPage(View view) {
        int currentPage = ((ViewPager) findViewById(R.id.relaredViewPager)).getCurrentItem();
        int totalPages = ((ViewPager) findViewById(R.id.relaredViewPager)).getAdapter().getCount();

        int nextPage = currentPage+1;
        if (nextPage >= totalPages) {
            // We can't go forward anymore.
            // Loop to the first page. If you don't want looping just
            // return here.
            nextPage = 0;
        }

        ((ViewPager) findViewById(R.id.relaredViewPager)).setCurrentItem(nextPage, true);
    }

    public void previousPage(View view) {
        int currentPage = ((ViewPager) findViewById(R.id.relaredViewPager)).getCurrentItem();
        int totalPages = ((ViewPager) findViewById(R.id.relaredViewPager)).getAdapter().getCount();

        int previousPage = currentPage-1;
        if (previousPage < 0) {
            // We can't go back anymore.
            // Loop to the last page. If you don't want looping just
            // return here.
            previousPage = totalPages - 1;
        }

        ((ViewPager) findViewById(R.id.relaredViewPager)).setCurrentItem(previousPage, true);
    }

    public void onDescClick(View view) {
        if (isDescExp) {
            RelativeLayout txt = (RelativeLayout) findViewById(R.id.ldesc);
            txt.setVisibility(View.GONE);
            View v = (View) findViewById(R.id.secEmp);
            v.setVisibility(View.GONE);
            ImageView img = (ImageView) findViewById(R.id.arrow1);
            img.setImageResource(R.drawable.arrow_down_small);
            isDescExp = false;
        } else {
            RelativeLayout txt = (RelativeLayout) findViewById(R.id.ldesc);
            txt.setVisibility(View.VISIBLE);
            View v = (View) findViewById(R.id.secEmp);
            v.setVisibility(View.VISIBLE);
            ImageView img = (ImageView) findViewById(R.id.arrow1);
            img.setImageResource(R.drawable.arrow_up_small);
            isDescExp = true;
        }
    }

    public void onTexnClick(View view) {
        if (isTexnExp) {
            RelativeLayout txt = (RelativeLayout) findViewById(R.id.ltexn);
            txt.setVisibility(View.GONE);
            View v = findViewById(R.id.thirdEmp);
            v.setVisibility(View.GONE);
            ImageView img = (ImageView) findViewById(R.id.arrow2);
            img.setImageResource(R.drawable.arrow_down_small);
            isTexnExp = false;
        } else {
            RelativeLayout txt = (RelativeLayout) findViewById(R.id.ltexn);
            txt.setVisibility(View.VISIBLE);
            View v = findViewById(R.id.thirdEmp);
            v.setVisibility(View.VISIBLE);
            ImageView img = (ImageView) findViewById(R.id.arrow2);
            img.setImageResource(R.drawable.arrow_up_small);
            isTexnExp = true;
        }
    }

    public int convertDip2Pixels(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    public void updateLayout() {
        if (item != null) {
            scrollView.setVisibility(View.VISIBLE);
            LinePageIndicator mIndicator = (LinePageIndicator) findViewById(R.id.indicator);
            ImageProductAdapter adapter = new ImageProductAdapter(ProductActivity.this, item.getItem_imgs());
            final ViewPager viewPager = (ViewPager) findViewById(R.id.pagerImg);
            final VerticalScrollView srollView = (VerticalScrollView) findViewById(R.id.scrollViewProd);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(0);
            viewPager.getParent().requestDisallowInterceptTouchEvent(true);
            viewPager.setOnTouchListener(new View.OnTouchListener() {

                int dragthreshold = 30;
                int downX;
                int downY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downX = (int) event.getRawX();
                            downY = (int) event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int distanceX = Math.abs((int) event.getRawX() - downX);
                            int distanceY = Math.abs((int) event.getRawY() - downY);

                            if (distanceY > distanceX && distanceY > dragthreshold) {
                                viewPager.getParent().requestDisallowInterceptTouchEvent(false);
                                srollView.getParent().requestDisallowInterceptTouchEvent(true);
                            } else if (distanceX > distanceY && distanceX > dragthreshold) {
                                viewPager.getParent().requestDisallowInterceptTouchEvent(true);
                                srollView.getParent().requestDisallowInterceptTouchEvent(false);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            srollView.getParent().requestDisallowInterceptTouchEvent(false);
                            viewPager.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                    return false;
                }
            });
            mIndicator.setSelectedColor(Color.parseColor("#FF6600"));
            mIndicator.setViewPager(viewPager);

            if(item.getRelated_items().size() > 0) {
                ((TextView) findViewById(R.id.relatedTitle)).setTypeface(Typeface.createFromAsset(act.getAssets(), "fonts/gecko.ttf"), Typeface.BOLD);
                ViewPager relatedViewPager = (ViewPager) findViewById(R.id.relaredViewPager);

                RelatedProductsAdapter relatedAdapter = new RelatedProductsAdapter(act, item.getRelated_items());
                relatedViewPager.setAdapter(relatedAdapter);
                //relatedViewPager.setPadding(10, 0, 10, 0);
                relatedViewPager.setClipToPadding(false);
                relatedViewPager.setPageMargin(convertDip2Pixels(act, 5));
                //relatedViewPager.setOn
            }

            if(!item.getCategoriesNames().equals("")){
                findViewById(R.id.textCategoriesNames).setVisibility(View.VISIBLE);
                String[] tmp = item.getCategoriesNames().split(", ");
                if(tmp.length > 1) {
                    SpannableString str = new SpannableString(getResources().getString(R.string.txt_categories) + "\n" + item.getCategoriesNames());
                    str.setSpan(new StyleSpan(Typeface.BOLD), 0, getResources().getString(R.string.txt_categories).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    str.setSpan(new StyleSpan(Typeface.ITALIC), getResources().getString(R.string.txt_categories).length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((TextView) findViewById(R.id.textCategoriesNames)).setText(str);
                }
                else{
                    SpannableString str = new SpannableString(getResources().getString(R.string.txt_category) + "\n" + item.getCategoriesNames());
                    str.setSpan(new StyleSpan(Typeface.BOLD), 0, getResources().getString(R.string.txt_category).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    str.setSpan(new StyleSpan(Typeface.ITALIC), getResources().getString(R.string.txt_category).length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((TextView) findViewById(R.id.textCategoriesNames)).setText(str);
                }
            }
            else{
                findViewById(R.id.textCategoriesNames).setVisibility(View.GONE);
            }

            TextView title = (TextView) findViewById(R.id.textTitle);
            title.setText(item.getItem_title());

            TextView priceret = (TextView) this.findViewById(R.id.textPrice);
            priceret.setVisibility(View.VISIBLE);
            priceret.setText(" " + StoreApplication.getCurrency_symbol(act) + item.getPrice());

            if (StoreApplication.getInstance().getFreeShippingPromotionItem().isHas_free_ship() && Double.valueOf(item.getPrice().replace(",", "")) > Double.valueOf(StoreApplication.getInstance().getFreeShippingPromotionItem().getValue()) || item.getFree_shipping().equals("Y")) {
                priceret = (TextView) this.findViewById(R.id.textFreeShip);
                priceret.setVisibility(View.VISIBLE);
            }
            else{
                this.findViewById(R.id.textFreeShip).setVisibility(View.GONE);
            }

            if (!item.getPromo_text().equals("")) {
                priceret = (TextView) this.findViewById(R.id.txtPromo);
                priceret.setVisibility(View.VISIBLE);
                priceret.setText(Html.fromHtml(item.getPromo_text()));
            }

            if (Double.valueOf(item.getInitial_price().replace(",", "")) > 0) {
                //RelativeLayout tmpRel = (RelativeLayout) findViewById(R.id.discLay);
                //tmpRel.setVisibility(View.VISIBLE);

                priceret = (TextView) this.findViewById(R.id.textPriceOrigin);
                priceret.setPaintFlags(priceret.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                priceret.setText(StoreApplication.getCurrency_symbol(act) + item.getInitial_price());
                priceret.setVisibility(View.VISIBLE);

                //priceret = (TextView) this.findViewById(R.id.textDisc);
                //priceret.setText(String.valueOf(-item.getDiscount()) + "%");
                //priceret.setVisibility(View.VISIBLE);

                LabelView label = new LabelView(this, R.drawable.view_line_dotted);
                label.setText(String.valueOf(-item.getDiscount()) + "%");
                label.setTextSize(20);
                if(!act.getResources().getBoolean(R.bool.isTablet))
                    label.setTargetView(findViewById(R.id.pagerImg), 12, LabelView.Gravity.LEFT_TOP);
                else
                    label.setTargetView(findViewById(R.id.pagerImg), 25, LabelView.Gravity.LEFT_TOP);
            }

            priceret = (TextView) this.findViewById(R.id.textID);
            priceret.setVisibility(View.VISIBLE);
            priceret.setText(String.format(getResources().getString(R.string.txt_product_code), item.getDisplay_id()));

            priceret = (TextView) this.findViewById(R.id.textPhone);
            if (!getResources().getString(R.string.store_phone).equals("")) {

                priceret.setVisibility(View.VISIBLE);
                priceret.setText(getResources().getString(R.string.txt_phone_promo) + getResources().getString(R.string.store_phone));
            } else {
                priceret.setVisibility(View.GONE);
            }

            asd = (WebView) findViewById(R.id.desc);
            asd.setFocusable(false);
            if (item.getDetail_description().equals("")) {
                asd.setBackgroundColor(Color.parseColor("#EEEEEE"));
                asd.getSettings().setJavaScriptEnabled(true);
                asd.loadDataWithBaseURL("", getResources().getString(R.string.txt_no_available_description) + "<br>", "text/html", "UTF-8", "");
            } else {
                asd.setBackgroundColor(Color.parseColor("#EEEEEE"));
                asd.getSettings().setJavaScriptEnabled(true);
                asd.loadDataWithBaseURL("", item.getDetail_description(), "text/html", "UTF-8", "");
            }

            TextView desc = (TextView) findViewById(R.id.texn);
            List<ProductSpecificationItem> specificationItemList = item.getSpecifications();
            String specifications = "";
            for (ProductSpecificationItem specificationItem : specificationItemList) {
                specifications += "<b>" + specificationItem.getName() + ": </b>";
                specifications += specificationItem.getValue() + "<br>";
            }
            if (specifications.equals(""))
                desc.setText(getResources().getString(R.string.txt_no_available_features) + "\n");
            else
                desc.setText(Html.fromHtml(specifications));

            final Spinner edit = (Spinner) this.findViewById(R.id.editTextCount);
            final List<String> list = new ArrayList<>();
            for (int i = 1; i < 100; i++) {
                list.add(String.valueOf(i));
            }
            list.add(getResources().getString(R.string.txt_other));
            final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            edit.setAdapter(dataAdapter);
            edit.setVisibility(View.VISIBLE);
            edit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == dataAdapter.getPosition(getResources().getString(R.string.txt_other))) {
                        final List<Integer> list2 = new ArrayList<>();
                        for (int i = 0; i < list.size() - 1; i++)
                            list2.add(Integer.valueOf(list.get(i)));

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
                                            if (Integer.valueOf(txt) > 99) {
                                                if (!list2.contains(Integer.valueOf(txt)))
                                                    list2.add(Integer.valueOf(txt));
                                                Collections.sort(list2);
                                                ArrayList<String> list3 = new ArrayList<>();
                                                for (int i = 0; i < list2.size(); i++)
                                                    list3.add(String.valueOf(list2.get(i)));
                                                list3.add(getResources().getString(R.string.txt_other));
                                                dataAdapter.clear();
                                                dataAdapter.addAll(list3);
                                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                edit.setAdapter(dataAdapter);
                                                edit.setSelection(dataAdapter.getPosition(txt));
                                            } else if (Integer.valueOf(txt) > 0 && Integer.valueOf(txt) < 100) {
                                                edit.setSelection(dataAdapter.getPosition(txt));
                                            } else {
                                                edit.setSelection(0);
                                            }
                                        } else {
                                            edit.setSelection(0);
                                        }
                                    }
                                }).setNegativeButton(getResources().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                edit.setSelection(0);
                            }
                        }).show();


                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            List<ProductTierPriceItem> tierPriceItemList = item.getTier_prices();
            if (tierPriceItemList.size() > 0) {
                TextView textView = new TextView(act);
                textView.setText(getResources().getString(R.string.txt_price_tier_title));
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                ((LinearLayout) findViewById(R.id.tierPricesRel)).addView(textView);

                TableFixHeaders tableFixHeaders = new TableFixHeaders(act);
                String[][] strings = new String[tierPriceItemList.size() + 1][2];
                strings[0][0] = getResources().getString(R.string.txt_price_tier_qty_title);
                strings[0][1] = getResources().getString(R.string.txt_price_tier_price_title);

                for (int k = 0; k < tierPriceItemList.size(); k++) {
                    strings[k + 1][0] = tierPriceItemList.get(k).getMin_qty() + "+";
                    strings[k + 1][1] = StoreApplication.getCurrency_symbol(act) + tierPriceItemList.get(k).getPrice();
                }

                MatrixTableAdapter<String> matrixTableAdapter = new MatrixTableAdapter<>(this, strings);
                tableFixHeaders.setAdapter(matrixTableAdapter);
                ((LinearLayout) findViewById(R.id.tierPricesRel)).addView(tableFixHeaders);
            }

            if(!item.getAllow_add_to_cart()){
                ((BootstrapButton) findViewById(R.id.buttonBuy)).setBootstrapButtonEnabled(false);
            }

            if (item.getQty() > 0) {
                TextView txtAva = (TextView) findViewById(R.id.textAva);
                txtAva.setText(getResources().getString(R.string.txt_availability_yes));
                BootstrapButton btnCart = (BootstrapButton) findViewById(R.id.buttonBuy);
                btnCart.setBootstrapButtonEnabled(true);
            } else {
                TextView txtAva = (TextView) findViewById(R.id.textAva);
                txtAva.setText(getResources().getString(R.string.txt_availability_no));
                txtAva.setTextColor(Color.RED);
                BootstrapButton btnCart = (BootstrapButton) findViewById(R.id.buttonBuy);
                btnCart.setBootstrapButtonEnabled(false);

                if (item.getOut_of_stock_actions().equals("S")) {
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.availSignup);
                    relativeLayout.setVisibility(View.VISIBLE);

                    CheckBox enable = (CheckBox) findViewById(R.id.checkboxSingupForAva);
                    enable.setChecked(false);
                    enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                RelativeLayout email = (RelativeLayout) act.findViewById(R.id.emailSingupRel);
                                email.setVisibility(View.VISIBLE);
                            } else {
                                RelativeLayout email = (RelativeLayout) act.findViewById(R.id.emailSingupRel);
                                email.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }

            final RatingBar ratingBar = (RatingBar) findViewById(R.id.rating);
            TextView ratingCount = (TextView) findViewById(R.id.rating_count);
            RelativeLayout reviewsRel = (RelativeLayout) findViewById(R.id.reviewsLay);
            TextView writeReview = (TextView) findViewById(R.id.writeReview);
            TextView readReviews = (TextView) findViewById(R.id.readReviews);

            if (item.getDiscussion_type().equals("D")) {
                ratingBar.setVisibility(View.GONE);
                ratingCount.setVisibility(View.GONE);
                reviewsRel.setVisibility(View.GONE);
            } else {
                if (item.getDiscussion_type().equals("B")) {
                    ratingBar.setVisibility(View.VISIBLE);
                    ratingCount.setVisibility(View.VISIBLE);
                    reviewsRel.setVisibility(View.VISIBLE);
                    ratingBar.setRating((float) item.getRating_score());
                    ratingCount.setText("(" + item.getRating_count() + ")");
                    writeReview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final ScrollView scrollView = (ScrollView) getLayoutInflater().inflate(R.layout.layout_write_review_both, null);
                            ((TextView) scrollView.findViewById(R.id.editName)).setText(act.getSharedPreferences("ShopPrefs", 0).getString("user_name", ""));
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);
                            alertDialogBuilder.setTitle(getResources().getString(R.string.product_rating_type_B_write_title));
                            alertDialogBuilder
                                    .setView(scrollView)
                                    .setCancelable(true)
                                    .setIcon(R.drawable.post)
                                    .setPositiveButton(getResources().getString(R.string.product_rating_type_B_write_positive_btn), null)
                                    .setNegativeButton(getResources().getString(R.string.product_rating_type_B_write_negative_btn), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                            final AlertDialog dialogBox = alertDialogBuilder.create();

                            dialogBox.setOnShowListener(new DialogInterface.OnShowListener() {

                                @Override
                                public void onShow(DialogInterface dialog) {

                                    Button b = dialogBox.getButton(AlertDialog.BUTTON_POSITIVE);
                                    b.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {
                                            TextView name = (TextView) scrollView.findViewById(R.id.editName);
                                            RatingBar ratingBar1 = (RatingBar) scrollView.findViewById(R.id.ratingBarPost);
                                            TextView message = (TextView) scrollView.findViewById(R.id.editMessage);
                                            if (name.getText().toString().trim().equals("")) {
                                                YoYo.with(Techniques.Shake).duration(700).playOn(name);
                                            } else if (message.getText().toString().trim().equals("")) {
                                                YoYo.with(Techniques.Shake).duration(700).playOn(message);
                                            }
                                            try {
                                                if(noNetworkHandler.showDialog())
                                                    new PostProductReview(dialogBox, URLEncoder.encode(name.getText().toString().trim(), "UTF-8"), URLEncoder.encode(message.getText().toString().trim(), "UTF-8"), (short) ratingBar1.getRating()).execute();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            });
                            dialogBox.show();
                        }
                    });

                    readReviews.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getRating_count() > 0) {
                                first_time_on_reviews = true;
                                current_review_page = 0;
                                total_reviews = 0;
                                listReviews.clear();
                                final RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.layout_read_reviews, null);
                                listViewReviews = (ListView) view.findViewById(R.id.reviewsList);
                                listViewReviews.addFooterView(reviews_footer_view, null, false);
                                current_review_page++;
                                if(noNetworkHandler.showDialog())
                                    new GetProductReviews().execute(current_review_page);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);
                                alertDialogBuilder.setTitle(getResources().getString(R.string.product_rating_type_B_read_title));
                                alertDialogBuilder
                                        .setView(view)
                                        .setCancelable(true)
                                        .setIcon(R.drawable.review)
                                        .setPositiveButton(getResources().getString(R.string.product_rating_type_B_read_positive_btn), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialogBuilder.create().show();
                            } else {
                                SuperToast.create(act, getResources().getString(R.string.product_rating_type_B_read_no_reviews_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.BLUE, SuperToast.Animations.POPUP)).show();
                            }
                        }
                    });
                } else if (item.getDiscussion_type().equals("C")) {
                    ratingBar.setVisibility(View.GONE);
                    ratingCount.setVisibility(View.VISIBLE);
                    reviewsRel.setVisibility(View.VISIBLE);
                    ratingCount.setText(String.format(getResources().getString(R.string.product_rating_type_C_promo_title), item.getRating_count()));

                    writeReview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final ScrollView scrollView = (ScrollView) getLayoutInflater().inflate(R.layout.layout_write_review_communication, null);
                            ((TextView) scrollView.findViewById(R.id.editName)).setText(act.getSharedPreferences("ShopPrefs", 0).getString("user_name", ""));
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);
                            alertDialogBuilder.setTitle(getResources().getString(R.string.product_rating_type_C_write_title));
                            alertDialogBuilder
                                    .setView(scrollView)
                                    .setCancelable(true)
                                    .setIcon(R.drawable.post)
                                    .setPositiveButton(getResources().getString(R.string.product_rating_type_C_write_positive_btn), null)
                                    .setNegativeButton(getResources().getString(R.string.product_rating_type_C_write_negative_btn), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                            final AlertDialog dialogBox = alertDialogBuilder.create();

                            dialogBox.setOnShowListener(new DialogInterface.OnShowListener() {

                                @Override
                                public void onShow(DialogInterface dialog) {

                                    Button b = dialogBox.getButton(AlertDialog.BUTTON_POSITIVE);
                                    b.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {
                                            TextView name = (TextView) scrollView.findViewById(R.id.editName);
                                            TextView message = (TextView) scrollView.findViewById(R.id.editMessage);
                                            if (name.getText().toString().trim().equals("")) {
                                                YoYo.with(Techniques.Shake).duration(700).playOn(name);
                                            } else if (message.getText().toString().trim().equals("")) {
                                                YoYo.with(Techniques.Shake).duration(700).playOn(message);
                                            }
                                            try {
                                                if(noNetworkHandler.showDialog())
                                                    new PostProductReview(dialogBox, URLEncoder.encode(name.getText().toString().trim(), "UTF-8"), URLEncoder.encode(message.getText().toString().trim(), "UTF-8"), (short) 0).execute();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            });
                            dialogBox.show();
                        }
                    });

                    readReviews.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getRating_count() > 0) {
                                first_time_on_reviews = true;
                                current_review_page = 0;
                                total_reviews = 0;
                                listReviews.clear();
                                final RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.layout_read_reviews, null);
                                listViewReviews = (ListView) view.findViewById(R.id.reviewsList);
                                listViewReviews.addFooterView(reviews_footer_view, null, false);
                                current_review_page++;
                                if(noNetworkHandler.showDialog())
                                    new GetProductReviews().execute(current_review_page);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);
                                alertDialogBuilder.setTitle(getResources().getString(R.string.product_rating_type_C_read_title));
                                alertDialogBuilder
                                        .setView(view)
                                        .setCancelable(true)
                                        .setIcon(R.drawable.review)
                                        .setPositiveButton(getResources().getString(R.string.product_rating_type_C_read_positive_btn), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialogBuilder.create().show();
                            } else {
                                SuperToast.create(act, getResources().getString(R.string.product_rating_type_C_read_no_reviews_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.BLUE, SuperToast.Animations.POPUP)).show();
                            }
                        }
                    });
                } else if (item.getDiscussion_type().equals("R")) {
                    ratingBar.setVisibility(View.VISIBLE);
                    ratingCount.setVisibility(View.VISIBLE);
                    reviewsRel.setVisibility(View.VISIBLE);
                    ratingBar.setRating((float) item.getRating_score());
                    ratingCount.setText("(" + item.getRating_count() + ")");
                    writeReview.setText(getResources().getString(R.string.product_rating_type_R_btn_write_txt));
                    readReviews.setText(getResources().getString(R.string.product_rating_type_R_btn_read_txt));

                    writeReview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final ScrollView scrollView = (ScrollView) getLayoutInflater().inflate(R.layout.layout_write_review_rating, null);
                            ((TextView) scrollView.findViewById(R.id.editName)).setText(act.getSharedPreferences("ShopPrefs", 0).getString("user_name", ""));
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);
                            alertDialogBuilder.setTitle(getResources().getString(R.string.product_rating_type_R_write_title));
                            alertDialogBuilder
                                    .setView(scrollView)
                                    .setCancelable(true)
                                    .setIcon(R.drawable.post)
                                    .setPositiveButton(getResources().getString(R.string.product_rating_type_R_write_positive_btn), null)
                                    .setNegativeButton(getResources().getString(R.string.product_rating_type_R_write_negative_btn), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                            final AlertDialog dialogBox = alertDialogBuilder.create();

                            dialogBox.setOnShowListener(new DialogInterface.OnShowListener() {

                                @Override
                                public void onShow(DialogInterface dialog) {

                                    Button b = dialogBox.getButton(AlertDialog.BUTTON_POSITIVE);
                                    b.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {
                                            TextView name = (TextView) scrollView.findViewById(R.id.editName);
                                            RatingBar ratingBar1 = (RatingBar) scrollView.findViewById(R.id.ratingBarPost);
                                            if (name.getText().toString().trim().equals("")) {
                                                YoYo.with(Techniques.Shake).duration(700).playOn(name);
                                            }
                                            try {
                                                if(noNetworkHandler.showDialog())
                                                    new PostProductReview(dialogBox, URLEncoder.encode(name.getText().toString().trim(), "UTF-8"), "", (short) ratingBar1.getRating()).execute();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            });
                            dialogBox.show();
                        }
                    });

                    readReviews.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getRating_count() > 0) {
                                first_time_on_reviews = true;
                                current_review_page = 0;
                                total_reviews = 0;
                                listReviews.clear();
                                final RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.layout_read_reviews, null);
                                listViewReviews = (ListView) view.findViewById(R.id.reviewsList);
                                listViewReviews.addFooterView(reviews_footer_view, null, false);
                                current_review_page++;
                                if(noNetworkHandler.showDialog())
                                    new GetProductReviews().execute(current_review_page);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);
                                alertDialogBuilder.setTitle(getResources().getString(R.string.product_rating_type_R_read_title));
                                alertDialogBuilder
                                        .setView(view)
                                        .setCancelable(true)
                                        .setIcon(R.drawable.review)
                                        .setPositiveButton(getResources().getString(R.string.product_rating_type_R_read_positive_btn), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialogBuilder.create().show();
                            } else {
                                SuperToast.create(act, getResources().getString(R.string.product_rating_type_R_read_no_reviews_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.BLUE, SuperToast.Animations.POPUP)).show();
                            }
                        }
                    });
                }
            }

            final List<ProductAttributeItem> attributes = item.getAttributes();
            attributeViews = new ArrayList<>();
            if (attributes.size() > 0) {
                for (final ProductAttributeItem attributeItem : attributes) {
                    if (attributeItem.getInput().equals("select")) {
                        RelativeLayout linearLayout = (RelativeLayout) LayoutInflater.from(act).inflate(R.layout.spinner_attributes, null);
                        TextView titleOption = (TextView) linearLayout.findViewById(R.id.attrName);
                        titleOption.setText(attributeItem.getTitle() + (attributeItem.isRequired() ? "*" : "") + ": ");

                        final List<ProductAttributeOptionItem> options = attributeItem.getOptions();
                        Spinner spinner = (Spinner) linearLayout.findViewById(R.id.attrSpinner);
                        List<String> values = new ArrayList<>();
                        for (ProductAttributeOptionItem option : options) {
                            if (option.getType().equals("discount"))
                                values.add(option.getTitle() + " (" + (Double.valueOf(option.getValue().replace(",", "")) > 0 ? "+" : "") + option.getValue() + "%)");
                            else
                                values.add(option.getTitle() + " (" + (Double.valueOf(option.getValue().replace(",", "")) >= 0 ? "+" + StoreApplication.getCurrency_symbol(act) : "-" + StoreApplication.getCurrency_symbol(act)) + option.getValue().replace("-", "") + ")");
                        }
                        ArrayAdapter<String> sexesAdapter = new ArrayAdapter<>(act, android.R.layout.simple_spinner_item, values);
                        sexesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(sexesAdapter);
                        final ImageView optionImg = (ImageView) linearLayout.findViewById(R.id.attrIcon);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                final String img_path = options.get(i).getImg_url();
                                if (!img_path.equals("")) {
                                    optionImg.setVisibility(View.VISIBLE);
                                    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(act.getApplicationContext()).denyCacheImageMultipleSizesInMemory().build();
                                    if (!ImageLoader.getInstance().isInited()) {
                                        ImageLoader.getInstance().init(config);
                                    }
                                    ImageLoader imageLoader = ImageLoader.getInstance();

                                    imageLoader.displayImage(img_path, optionImg);
                                    optionImg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            List<String> imagePaths = new ArrayList<>();
                                            imagePaths.add(img_path);
                                            Intent activity = new Intent(act, FullScreenImageActivity.class);
                                            activity.putExtra("img_urls", new Gson().toJson(imagePaths));
                                            activity.putExtra("pos", 0);
                                            act.startActivity(activity);
                                            act.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        }
                                    });
                                } else {
                                    optionImg.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        params.addView(linearLayout);
                        attributeViews.add(spinner);
                    } else if (attributeItem.getInput().equals("multiselect")) {
                        LinearLayout linearLayout = new LinearLayout(act);
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        linearLayout.setGravity(Gravity.CENTER);
                        TextView titleOption = new TextView(act);
                        titleOption.setText(attributeItem.getTitle() + (attributeItem.isRequired() ? "*" : "") + ": ");
                        linearLayout.addView(titleOption, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

                        final List<ProductAttributeOptionItem> options = attributeItem.getOptions();
                        CheckBox checkBox = new CheckBox(act);
                        checkBox.setTextColor(getResources().getColor(R.color.one_shade_of_the_fifty_shades_of_gray));
                        if (options.get(1).getType().equals("discount"))
                            checkBox.setText(options.get(1).getTitle() + " (" + (Double.valueOf(options.get(1).getValue().replace(",", "")) > 0 ? "+" : "") + options.get(1).getValue() + "%)");
                        else
                            checkBox.setText(options.get(1).getTitle() + " (" + (Double.valueOf(options.get(1).getValue().replace(",", "")) >= 0 ? "+" + StoreApplication.getCurrency_symbol(act) : "-" + StoreApplication.getCurrency_symbol(act)) + options.get(1).getValue().replace("-", "") + ")");
                        linearLayout.addView(checkBox, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                        params.addView(linearLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0, 1.0f));
                        attributeViews.add(checkBox);
                    } else if (attributeItem.getInput().equals("text")) {
                        LinearLayout linearLayout = new LinearLayout(act);
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        linearLayout.setGravity(Gravity.CENTER);
                        TextView titleOption = new TextView(act);
                        titleOption.setText(attributeItem.getTitle() + (attributeItem.isRequired() ? "*" : "") + ": ");
                        linearLayout.addView(titleOption, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                        BootstrapEditText editText = new BootstrapEditText(act);
                        editText.setRoundedCorners(true);
                        editText.setWarning();
                        editText.setSingleLine();
                        editText.setHint(attributeItem.getInner_hint());
                        editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        linearLayout.addView(editText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        params.addView(linearLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        attributeViews.add(editText);
                        editText.clearFocus();
                    }
                }
            }

            BootstrapButton btnBuy = (BootstrapButton) findViewById(R.id.buttonBuy);
            btnBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (act.getSharedPreferences("ShopPrefs", 0).getBoolean("is_connected", false)) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            for (int i = 0; i < attributeViews.size(); i++) {
                                View item = attributeViews.get(i);
                                if (item instanceof CheckBox) {
                                    if (!attributes.get(i).isRequired()) {
                                        short tmp = (short) (((CheckBox) item).isChecked() ? 1 : 0);
                                        jsonObject.put(String.valueOf(attributes.get(i).getAttribute_id()), attributes.get(i).getOptions().get(tmp).getOption_id());
                                    } else {
                                        if (((CheckBox) item).isChecked()) {
                                            short tmp = (short) (((CheckBox) item).isChecked() ? 1 : 0);
                                            jsonObject.put(String.valueOf(attributes.get(i).getAttribute_id()), attributes.get(i).getOptions().get(tmp).getOption_id());
                                        } else {
                                            SuperToast.create(act, String.format(getResources().getString(R.string.txt_btn_cart_required_attr), (attributes.get(i).getTitle())), SuperToast.Duration.SHORT, Style.getStyle(Style.ORANGE, SuperToast.Animations.POPUP)).show();
                                            YoYo.with(Techniques.Shake).duration(700).playOn(item);
                                            return;
                                        }
                                    }
                                } else if (item instanceof Spinner) {
                                    jsonObject.put(String.valueOf(attributes.get(i).getAttribute_id()), attributes.get(i).getOptions().get(((Spinner) item).getSelectedItemPosition()).getOption_id());
                                } else if (item instanceof BootstrapEditText) {
                                    if (!attributes.get(i).isRequired()) {
                                        if (!attributes.get(i).getIncorrect_message().equals("")) {
                                            Pattern p = Pattern.compile(attributes.get(i).getRegex(), Pattern.CASE_INSENSITIVE);
                                            Matcher m = p.matcher(((BootstrapEditText) item).getText().toString().trim());
                                            if (m.find()) {
                                                jsonObject.put(String.valueOf(attributes.get(i).getAttribute_id()), ((BootstrapEditText) item).getText().toString());
                                            } else {
                                                SuperToast.create(act, String.format("%s", (attributes.get(i).getIncorrect_message())), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                                                YoYo.with(Techniques.Shake).duration(700).playOn(item);
                                                return;
                                            }
                                        } else {
                                            jsonObject.put(String.valueOf(attributes.get(i).getAttribute_id()), ((BootstrapEditText) item).getText().toString());
                                        }
                                    } else {
                                        if (((BootstrapEditText) item).getText().toString().trim().equals("")) {
                                            SuperToast.create(act, String.format(getResources().getString(R.string.txt_btn_cart_required_attr), (attributes.get(i).getTitle())), SuperToast.Duration.SHORT, Style.getStyle(Style.ORANGE, SuperToast.Animations.POPUP)).show();
                                            YoYo.with(Techniques.Shake).duration(700).playOn(item);
                                            return;
                                        } else {
                                            if (!attributes.get(i).getIncorrect_message().equals("")) {
                                                Pattern p = Pattern.compile(attributes.get(i).getRegex(), Pattern.CASE_INSENSITIVE);
                                                Matcher m = p.matcher(((BootstrapEditText) item).getText().toString().trim());
                                                if (m.find()) {
                                                    jsonObject.put(String.valueOf(attributes.get(i).getAttribute_id()), ((BootstrapEditText) item).getText().toString());
                                                } else {
                                                    SuperToast.create(act, String.format("%s", (attributes.get(i).getIncorrect_message())), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                                                    YoYo.with(Techniques.Shake).duration(700).playOn(item);
                                                    return;
                                                }
                                            } else {
                                                jsonObject.put(String.valueOf(attributes.get(i).getAttribute_id()), ((BootstrapEditText) item).getText().toString());
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(noNetworkHandler.showDialog())
                            new AddProductToCart().execute(String.valueOf(item.getItem_id()), String.valueOf(edit.getSelectedItem().toString()), jsonObject.toString());
                    } else {
                        Intent i = new Intent(act, LoginActivity.class);
                        i.putExtra("page", 0);
                        startActivity(i);
                    }
                }
            });




        /*long unixTime = System.currentTimeMillis();
        if(unixTime < item.getAvail_since() * (long) 1000){
            TextView avail_since = (TextView) findViewById(R.id.txtAvailableSince);
            avail_since.setVisibility(View.VISIBLE);
            Date date = new Date(item.getAvail_since() * (long) 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            avail_since.setText(String.format(getResources().getString(R.string.txt_availability_time),sdf.format(date)));

            if(item.getOutOfStockAction().equals("B") && item.getAmount() > 0){
                BootstrapButton btnCart = (BootstrapButton) findViewById(R.id.buttonBuy);
                btnCart.setBootstrapButtonEnabled(true);
            }
            else{
                BootstrapButton btnCart = (BootstrapButton) findViewById(R.id.buttonBuy);
                btnCart.setBootstrapButtonEnabled(false);
            }
        }
        else{
            TextView avail_since = (TextView) findViewById(R.id.txtAvailableSince);
            avail_since.setVisibility(View.GONE);
        }*/
        }
    }

    class AddProductToCart extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute(){
            findViewById(R.id.buttonBuy).setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                String response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(getResources().getString(R.string.url_add_product_to_cart), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), Integer.valueOf(strings[0]), Integer.valueOf(strings[1]), URLEncoder.encode(strings[2], "UTF-8")), ServiceHandler.GET);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("code").equals("0x0000")){
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }            return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
            findViewById(R.id.buttonBuy).setEnabled(true);
            if(noNetworkHandler.showDialog())
                new GetCartCount().execute();
            if(result){
                SuperToast.create(act, String.format(getResources().getString(R.string.txt_add_to_cart_success)), SuperToast.Duration.SHORT, Style.getStyle(Style.PURPLE, SuperToast.Animations.POPUP)).show();
            }
            else {
                SuperToast.create(act, String.format(getResources().getString(R.string.txt_add_to_cart_fail)), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
            }
        }
    }

    class PostProductReview extends AsyncTask<Void, Void, Double> {
        String status = "";
        String message = "";
        short rating = 0;
        String name = "";
        AlertDialog dialogBox;
        int totalReviews = 0;

        public PostProductReview(AlertDialog dialogBox, String name, String message, short rating) {
            this.message = message;
            this.rating = rating;
            this.name = name;
            this.dialogBox = dialogBox;
        }

        @Override
        protected Double doInBackground(Void... params) {
            String response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(getResources().getString(R.string.url_post_product_review), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), product_id, name, message, rating), ServiceHandler.GET);

            try {
                JSONObject initialObj = new JSONObject(response);
                if (initialObj.getString("result").equals("success")) {
                    status = initialObj.getJSONObject("info").getString("status");
                    totalReviews = initialObj.getJSONObject("info").getInt("total_reviews");
                    if (!(initialObj.getJSONObject("info").get("average_rating") instanceof Boolean))
                        return initialObj.getJSONObject("info").getDouble("average_rating");
                    else
                        return 0.0;

                } else {
                    return -1.0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1.0;
        }

        @Override
        protected void onPostExecute(Double result) {
            if (result > -1) {
                ((RatingBar) act.findViewById(R.id.rating)).setRating(result.floatValue());
                ((TextView) act.findViewById(R.id.rating_count)).setText("(" + totalReviews + (item.getDiscussion_type().equals("C") ? " κριτικές" : "") + ")");
                if (status.equals("A")) {
                    if(item.getDiscussion_type().equals("B") || item.getDiscussion_type().equals("C"))
                        SuperToast.create(act, getResources().getString(R.string.msg_write_review_success_status_ava), SuperToast.Duration.SHORT, Style.getStyle(Style.GREEN, SuperToast.Animations.POPUP)).show();
                    else if(item.getDiscussion_type().equals("R"))
                        SuperToast.create(act, getResources().getString(R.string.msg_write_review_success_status_ava_R), SuperToast.Duration.SHORT, Style.getStyle(Style.GREEN, SuperToast.Animations.POPUP)).show();
                } else {
                    if(item.getDiscussion_type().equals("B") || item.getDiscussion_type().equals("C"))
                        SuperToast.create(act, getResources().getString(R.string.msg_write_review_success_status_not_ava), SuperToast.Duration.SHORT, Style.getStyle(Style.GREEN, SuperToast.Animations.POPUP)).show();
                    else if(item.getDiscussion_type().equals("R"))
                        SuperToast.create(act, getResources().getString(R.string.msg_write_review_success_status_not_ava_R), SuperToast.Duration.SHORT, Style.getStyle(Style.GREEN, SuperToast.Animations.POPUP)).show();
                }
                dialogBox.dismiss();
            }
        }
    }

    class GetProductReviews extends AsyncTask<Integer, Void, List<ReviewItem>> {
        double avgRating = 0;

        @Override
        protected void onPreExecute() {
            if (first_time_on_reviews) {
                reviews_footer_view.setVisibility(View.GONE);
            } else {
                reviews_footer_view.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<ReviewItem> doInBackground(Integer... params) {
            String response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(getResources().getString(R.string.url_get_product_reviews), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), product_id, 10, params[0]), ServiceHandler.GET);

            try {
                JSONObject initialObj = new JSONObject(response);
                if (initialObj.getString("result").equals("success")) {
                    total_reviews = initialObj.getJSONObject("info").getInt("total_results");
                    if (!(initialObj.getJSONObject("info").get("average_rating") instanceof Boolean))
                        avgRating = initialObj.getJSONObject("info").getDouble("average_rating");
                    return new ReviewsParser().parse(initialObj.getJSONObject("info").getJSONArray("trade_rates"));
                } else {
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<ReviewItem> result) {
            if (result.size() > 0) {
                ((RatingBar) act.findViewById(R.id.rating)).setRating((float) avgRating);
                ((TextView) act.findViewById(R.id.rating_count)).setText("(" + total_reviews + (item.getDiscussion_type().equals("C") ? " κριτικές" : "") + ")");
                if (first_time_on_reviews) {
                    listReviews.addAll(result);
                    first_time_on_reviews = false;
                    reviews_adapter = new ReviewsListAdapter(act, listReviews, item.getDiscussion_type());
                    listViewReviews.setAdapter(reviews_adapter);
                    listViewReviews.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                            if (scrollState == SCROLL_STATE_IDLE) {
                                if (listViewReviews.getLastVisiblePosition() >= listViewReviews.getCount() - 1) {
                                    int totalPages = (int) (total_reviews / 10.0) + 1;
                                    totalPages = total_reviews % 10 == 0 ? --totalPages : totalPages;

                                    if (totalPages != current_review_page) {
                                        current_review_page++;
                                        //isPaged=true;
                                        if(noNetworkHandler.showDialog())
                                            new GetProductReviews().execute(current_review_page);
                                    } else {
                                        reviews_footer_view.setVisibility(View.GONE);
                                        listViewReviews.removeFooterView(reviews_footer_view);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            int totalPages = (int) (total_reviews / 10.0) + 1;
                            totalPages = total_reviews % 10 == 0 ? --totalPages : totalPages;
                            if (totalPages != current_review_page) {
                                reviews_footer_view.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else {
                    listReviews.addAll(listReviews.size(), result);
                    reviews_adapter.notifyDataSetChanged();
                }
            } else {
                reviews_footer_view.setVisibility(View.GONE);
                listViewReviews.removeFooterView(reviews_footer_view);
            }
        }
    }

    public class GetProductInfo extends AsyncTask<Void, Void, ProductItem> {
        SweetAlertDialog pDialog;

        @Override
        protected void onPreExecute() {
            progressBarLoading.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);
            pDialog = new SweetAlertDialog(act, SweetAlertDialog.PROGRESS_TYPE, false);
            pDialog.setTitleText(act.getResources().getString(R.string.loadingTxt));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setMultiColorProgressBar(act, true);
        }

        @Override
        protected ProductItem doInBackground(Void... params) {
            String response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(getResources().getString(R.string.url_product), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), product_id), ServiceHandler.GET);

            ProductParser parser = new ProductParser();
            try {
                JSONObject initialObj = new JSONObject(response);
                if (initialObj.getString("result").equals("success")) {
                    return parser.parse(initialObj.getJSONObject("info").getJSONObject("item"));
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ProductItem result) {
            item = result;
            progressBarLoading.setVisibility(View.GONE);
            if(pDialog != null && pDialog.isShowing()){
                pDialog.dismissWithAnimation();
                pDialog.setMultiColorProgressBar(act, false);
            }

            updateLayout();
        }
    }
}
