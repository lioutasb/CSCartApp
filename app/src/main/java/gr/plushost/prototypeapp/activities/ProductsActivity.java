package gr.plushost.prototypeapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.adapters.listviews.ProductsListAdapter;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.exceptionhandlers.StoreExceptionHandler;
import gr.plushost.prototypeapp.fragments.NavigationDrawerFragment;
import gr.plushost.prototypeapp.items.FilterItem;
import gr.plushost.prototypeapp.items.FilterRangeItem;
import gr.plushost.prototypeapp.items.MiniProductItem;
import gr.plushost.prototypeapp.items.ProductItem;
import gr.plushost.prototypeapp.listeners.CartMenuItemStuffListener;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;
import gr.plushost.prototypeapp.parsers.FiltersParser;
import gr.plushost.prototypeapp.parsers.ProductsParser;
import gr.plushost.prototypeapp.providers.RecentSuggestionProvider;
import gr.plushost.prototypeapp.quickreturn.enums.QuickReturnViewType;
import gr.plushost.prototypeapp.quickreturn.listeners.QuickReturnListViewOnScrollListener;
import gr.plushost.prototypeapp.widgets.FilterCheckboxesGroupView;
import gr.plushost.prototypeapp.widgets.FilterRangeGroupView;
import gr.plushost.prototypeapp.widgets.FloatingActionButton;
import tr.xip.errorview.ErrorView;

/**
 * Created by billiout on 23/2/2015.
 */
public class ProductsActivity extends DrawerActivity {
    Activity act = this;
    ListView listView;
    RelativeLayout headerView;
    ProgressBar progressBarLoading;
    int categoryID;
    int totalProducts = 0;
    boolean isPaged = false;
    List<MiniProductItem> list = new ArrayList<>();
    ProductsListAdapter adapter;
    ProgressBar progressBarFooter;
    RelativeLayout footerView;
    int currentPage = 0;
    View mHeader;
    FloatingActionButton fabButton;
    ImageView btnView;
    ImageView btnFilters;
    ActionMode actionMode;
    String defaultSort = "";
    Spinner spinner;
    String query = "";
    boolean fromSearch = false;
    int hot_number = 0;
    TextView ui_hot = null;
    View viewFooter;
    boolean firstTime = true;
    Dialog dialogFilters;
    String filterHead = "subcats=Y&features_hash=";
    String filtersHash = "";
    ArrayList<View> filterViews = new ArrayList<>();
    NoNetworkHandler noNetworkHandler;


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        noNetworkHandler = new NoNetworkHandler(this);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        fragmentTransaction.add(R.id.nav_drawer_fragment, fragment);
        fragmentTransaction.commit();
        super.set(toolbar, true, fragment);

        listView = (ListView) findViewById(R.id.list_products);
        headerView = (RelativeLayout) findViewById(R.id.sticky);
        progressBarLoading = (ProgressBar) findViewById(R.id.progressBarLoading);
        fabButton = (FloatingActionButton) findViewById(R.id.fabButton);
        btnView = (ImageView) findViewById(R.id.btnChangeView);
        btnFilters = (ImageView) findViewById(R.id.btnFilters);
        spinner = (Spinner) findViewById(R.id.sortPros);
        mHeader = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_header_view_products, null);

        LayoutInflater mInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        viewFooter = mInflater.inflate(R.layout.list_footer_view_products, null);
        footerView = (RelativeLayout) viewFooter.findViewById(R.id.footer_layout);
        progressBarFooter = (ProgressBar) viewFooter.findViewById(R.id.progressBarFooter);
        if(listView.getFooterViewsCount() == 0)
            listView.addFooterView(viewFooter, null, false);


        /*findViewById(R.id.filtersFragment).setVisibility(View.VISIBLE);
        Fragment fragmentF = getSupportFragmentManager().findFragmentById(R.id.filtersFragment);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(fragmentF);
        ft.commit();*/

        if(noNetworkHandler.showDialog())
            new GetCartCount().execute();
        defaultSort = "&sort_by="+StoreApplication.getInstance().getSortsItemList().get(0).getSort_by() + "&sort_order=" + StoreApplication.getInstance().getSortsItemList().get(0).getSort_order();
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            this.query = query.trim();
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, RecentSuggestionProvider.AUTHORITY, RecentSuggestionProvider.MODE);
            suggestions.saveRecentQuery(this.query, null);
            getSupportActionBar().setTitle(this.query);

            listView.addHeaderView(mHeader, null, false);
            mHeader.setVisibility(View.GONE);

            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeView(v);
                }
            });

            if (getApplicationContext().getSharedPreferences("ShopPrefs", 0).getInt("viewType", 1) == 1) {
                btnView.setImageResource(R.drawable.changestyle_rows);
            } else if (getApplicationContext().getSharedPreferences("ShopPrefs", 0).getInt("viewType", 1) == 2) {
                btnView.setImageResource(R.drawable.changestyle_big);
            }
            else{
                btnView.setImageResource(R.drawable.changestyle_big);
            }

            String[] items = StoreApplication.getInstance().getSortsNameArray();
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(act, R.layout.spinner_item_header, items);
            spinner.setAdapter(adapterSpinner);

            fromSearch = true;

            btnFilters.setEnabled(false);
            btnFilters.setClickable(false);
            btnFilters.setFocusable(false);
            btnFilters.setImageResource(R.drawable.icon_filters_disabled);

            if(noNetworkHandler.showDialog())
                new GetProductsFromUrl(true, true).execute(1);
            currentPage++;
        } else {
            Bundle extras = getIntent().getExtras();

            getSupportActionBar().setTitle(extras.getString("category_name"));
            categoryID = extras.getInt("category_id");

            listView.addHeaderView(mHeader, null, false);
            mHeader.setVisibility(View.GONE);

            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeView(v);
                }
            });

            if (getApplicationContext().getSharedPreferences("ShopPrefs", 0).getInt("viewType", 1) == 1) {
                btnView.setImageResource(R.drawable.changestyle_rows);
            } else if (getApplicationContext().getSharedPreferences("ShopPrefs", 0).getInt("viewType", 1) == 2) {
                btnView.setImageResource(R.drawable.changestyle_big);
            }
            else{
                btnView.setImageResource(R.drawable.changestyle_big);
            }

            String[] items = StoreApplication.getInstance().getSortsNameArray();
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(act, R.layout.spinner_item_header, items);
            spinner.setAdapter(adapterSpinner);

            fromSearch = false;

            View dialogView = LayoutInflater.from(this)
                    .inflate(R.layout.dialog_filters, null);
            dialogFilters = new AlertDialog.Builder(act)
                    .setView(dialogView)
                    .setIcon(R.drawable.icon_filters)
                    .setTitle(getResources().getString(R.string.txt_filters_title))
                    .setPositiveButton(getResources().getString(R.string.txt_filters_positive), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            filtersHash = "";
                            for(View view : filterViews){
                                if(view instanceof FilterRangeGroupView){
                                    filtersHash += ((FilterRangeGroupView) view).getFeatureHash() + ".";
                                }
                                else if(view instanceof FilterCheckboxesGroupView){
                                    filtersHash += ((FilterCheckboxesGroupView) view).getFeatureHash() + ".";
                                }
                            }
                            isPaged = false;
                            currentPage = 1;
                            if(noNetworkHandler.showDialog())
                                new GetProductsFromUrl(true, false).execute(1);
                        }
                    })
                    .setNeutralButton(getResources().getString(R.string.txt_filters_reset), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ((FiltersFragment) getSupportFragmentManager().findFragmentById(R.id.filtersFragment)).getFiltersExecute();
                            filtersHash = "";
                            isPaged = false;
                            currentPage = 1;
                            if(noNetworkHandler.showDialog())
                                new GetProductsFromUrl(true, false).execute(1);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.txt_filters_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();

            if(noNetworkHandler.showDialog())
                new GetProductsFromUrl(true, false).execute(1);
            currentPage++;
        }
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
            updateHotCount(result);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        /*else if(!getSupportFragmentManager().findFragmentById(R.id.filtersFragment).isHidden()){
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.filtersFragment);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            ft.hide(fragment);
            ft.commit();
        }*/
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    class GetProductsFromUrl extends AsyncTask<Integer, Void, List<MiniProductItem>> {
        boolean firstTime = true;
        boolean fromSearch = false;
        SweetAlertDialog pDialog;

        public GetProductsFromUrl(boolean firstTime, boolean fromSearch) {
            this.firstTime = firstTime;
            this.fromSearch = fromSearch;
        }

        @Override
        protected void onPreExecute() {
            if (firstTime) {
                listView.setVisibility(View.GONE);
                headerView.setVisibility(View.GONE);
                fabButton.setVisibility(View.GONE);
                progressBarFooter.setVisibility(View.GONE);
                viewFooter.setVisibility(View.GONE);
                if(listView.getFooterViewsCount() > 0)
                    listView.removeFooterView(viewFooter);
                progressBarLoading.setVisibility(View.GONE);
                findViewById(R.id.error_view).setVisibility(View.GONE);
                pDialog = new SweetAlertDialog(act, SweetAlertDialog.PROGRESS_TYPE, false);
                pDialog.setTitleText(act.getResources().getString(R.string.loadingTxt));
                pDialog.setCancelable(false);
                pDialog.show();
                pDialog.setMultiColorProgressBar(act, true);

            } else {
                findViewById(R.id.error_view).setVisibility(View.GONE);
                progressBarFooter.setVisibility(View.VISIBLE);
                viewFooter.setVisibility(View.VISIBLE);
                if(listView.getFooterViewsCount() == 0)
                    listView.addFooterView(viewFooter, null, false);
            }
        }

        @Override
        protected List<MiniProductItem> doInBackground(Integer... params) {
            try {
                String response;
                if (!fromSearch)
                    response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_products_of_category) + defaultSort + filterHead + filtersHash, act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), categoryID, params[0]), ServiceHandler.GET);
                else
                    response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_search) + defaultSort, act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), URLEncoder.encode(query, "UTF-8"), params[0]), ServiceHandler.GET);

                JSONObject initialObj = new JSONObject(response);
                if (initialObj.getString("result").equals("success")) {
                    ProductsParser parser = new ProductsParser();
                    List<MiniProductItem> list = parser.parse(initialObj.getJSONObject("info").getJSONArray("items"));
                    totalProducts = initialObj.getJSONObject("info").getInt("total_results");
                    return list;
                } else {
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MiniProductItem> response) {
            listView.setVisibility(View.VISIBLE);
            headerView.setVisibility(View.VISIBLE);
            fabButton.setVisibility(View.VISIBLE);
            progressBarLoading.setVisibility(View.GONE);
            if(pDialog != null && pDialog.isShowing()){
                pDialog.dismissWithAnimation();
                pDialog.setMultiColorProgressBar(act, false);
            }

            updateList(response);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_products);
    }

    public void updateList(List<MiniProductItem> products) {
        progressBarLoading.setVisibility(View.GONE);
        fabButton.setVisibility(View.VISIBLE);
        headerView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
        if (totalProducts > 0) {
            if (!isPaged) {
                btnView.setClickable(true);
                btnView.setEnabled(true);
                btnView.setFocusable(true);
                listView.setVisibility(View.VISIBLE);
                list.clear();
                list.addAll(products);
                if (getApplicationContext().getSharedPreferences("ShopPrefs", 0).getInt("viewType", 1) == 1) {
                    adapter = new ProductsListAdapter(act, list, R.layout.listrow_products_big);
                } else if (getApplicationContext().getSharedPreferences("ShopPrefs", 0).getInt("viewType", 1) == 2) {
                    adapter = new ProductsListAdapter(act, list, R.layout.listrow_products_small);
                }
                listView.setAdapter(adapter);
                int headerHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
                QuickReturnListViewOnScrollListener.Builder builder = new QuickReturnListViewOnScrollListener.Builder(QuickReturnViewType.HEADER)
                        .header(headerView)
                        .minHeaderTranslation(-headerHeight)
                        .isSnappable(true);
                QuickReturnListViewOnScrollListener mQuickReturnListViewOnScrollListener = new QuickReturnListViewOnScrollListener(builder) {
                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                        int totalPages = (int) (totalProducts / 10.0) + 1;
                        totalPages = totalProducts % 10 == 0 ? --totalPages : totalPages;
                        if (totalPages != currentPage) {
                            progressBarFooter.setVisibility(View.VISIBLE);
                            viewFooter.setVisibility(View.VISIBLE);
                            if(listView.getFooterViewsCount() == 0)
                                listView.addFooterView(viewFooter, null, false);
                        }
                    }

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        super.onScrollStateChanged(view, scrollState);
                        if (scrollState == SCROLL_STATE_IDLE) {
                            if (listView.getLastVisiblePosition() >= listView.getCount() - 1) {
                                int totalPages = (int) (totalProducts / 10.0) + 1;
                                totalPages = totalProducts % 10 == 0 ? --totalPages : totalPages;

                                if (totalPages != currentPage) {
                                    currentPage++;
                                    isPaged = true;
                                    if(noNetworkHandler.showDialog())
                                        new GetProductsFromUrl(false, fromSearch).execute(currentPage);
                                } else {
                                    progressBarFooter.setVisibility(View.GONE);
                                    viewFooter.setVisibility(View.GONE);
                                    if(listView.getFooterViewsCount() > 0)
                                        listView.removeFooterView(viewFooter);
                                }
                            }
                        }
                    }

                };
                fabButton.attachToListView(listView, null, mQuickReturnListViewOnScrollListener);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
                listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                    @Override
                    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                        int checkedCount = listView.getCheckedItemCount();
                        if (checkedCount > 1)
                            mode.setTitle(String.format(getResources().getString(R.string.txt_selected_products), checkedCount));
                        else
                            mode.setTitle(String.format(getResources().getString(R.string.txt_selected_product), checkedCount));
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_compare:
                                if (listView.getCheckedItemCount() > 1) {
                                    long[] selected = listView.getCheckedItemIds();
                                    List<MiniProductItem> selectedProducts = new ArrayList<>();

                                    for (long l : selected)
                                        selectedProducts.add(list.get((int) l));
                                    mode.finish();
                                    Intent i = new Intent(act, CompareActivity.class);
                                    i.putExtra("products_list", new Gson().toJson(selectedProducts));
                                    act.startActivity(i);
                                } else {
                                    SuperToast.create(act, act.getResources().getString(R.string.toast_need_more_products_to_compare), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                                }
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mode.getMenuInflater().inflate(R.menu.menu_products_list_select, menu);
                        actionMode = mode;
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        actionMode = null;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position - 1 < list.size()) {
                            Intent i = new Intent(act, ProductActivity.class);
                            //i.putExtra("product_item", new Gson().toJson(list.get(position - 1)));
                            i.putExtra("need_download", true);
                            i.putExtra("product_id", list.get(position - 1).getItem_id());
                            i.putExtra("product_name", list.get(position - 1).getItem_title());
                            act.startActivity(i);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }
                });
            } else {
                if(list != null) {
                    list.addAll(list.size(), products);
                    adapter.notifyDataSetChanged();
                }
            }

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String temp = defaultSort;
                    //if (position == 0) {
                        //defaultSort = "";
                    //}
                    //else {
                        defaultSort = "&sort_by=" + StoreApplication.getInstance().getSortsItemList().get(position).getSort_by() + "&sort_order=" + StoreApplication.getInstance().getSortsItemList().get(position).getSort_order();
                    //}

                    //System.out.println(defaultSort);
                    /*else if (position == 1) {
                        defaultSort = "&sort_by=position&sort_order=asc";
                    } else if (position == 2) {
                        defaultSort = "&sort_by=timestamp&sort_order=desc";
                    } else if (position == 3) {
                        defaultSort = "&sort_by=product&sort_order=asc";
                    } else if (position == 4) {
                        defaultSort = "&sort_by=product&sort_order=desc";
                    } else if (position == 5) {
                        defaultSort = "&sort_by=price&sort_order=asc";
                    } else if (position == 6) {
                        defaultSort = "&sort_by=price&sort_order=desc";
                    } else if (position == 7) {
                        defaultSort = "&sort_by=popularity&sort_order=desc";
                    }*/


                    if (!defaultSort.equals(temp)) {
                        isPaged = false;
                        currentPage = 1;
                        if(noNetworkHandler.showDialog())
                            new GetProductsFromUrl(true, fromSearch).execute(1);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            if(filtersHash.equals("")) {
                listView.setVisibility(View.GONE);
                headerView.setVisibility(View.GONE);
                fabButton.setVisibility(View.GONE);
                findViewById(R.id.error_view).setVisibility(View.VISIBLE);
                if (!fromSearch)
                    ((ErrorView) findViewById(R.id.error_view)).setSubtitle(R.string.txt_no_products);
                    //SuperToast.create(this, getResources().getString(R.string.txt_no_products), SuperToast.Duration.SHORT, Style.getStyle(Style.BLUE, SuperToast.Animations.POPUP)).show();
                else
                    ((ErrorView) findViewById(R.id.error_view)).setSubtitle(R.string.txt_no_results);
                //SuperToast.create(this, getResources().getString(R.string.txt_no_results), SuperToast.Duration.SHORT, Style.getStyle(Style.BLUE, SuperToast.Animations.POPUP)).show();
            }
            else{
                btnView.setClickable(false);
                btnView.setEnabled(false);
                btnView.setFocusable(false);
                listView.setVisibility(View.VISIBLE);
                list.clear();
                list.addAll(products);
                if (getApplicationContext().getSharedPreferences("ShopPrefs", 0).getInt("viewType", 1) == 1) {
                    adapter = new ProductsListAdapter(act, list, R.layout.listrow_products_big);
                } else if (getApplicationContext().getSharedPreferences("ShopPrefs", 0).getInt("viewType", 1) == 2) {
                    adapter = new ProductsListAdapter(act, list, R.layout.listrow_products_small);
                }
                listView.setAdapter(adapter);
                int headerHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
                QuickReturnListViewOnScrollListener.Builder builder = new QuickReturnListViewOnScrollListener.Builder(QuickReturnViewType.HEADER)
                        .header(headerView)
                        .minHeaderTranslation(-headerHeight)
                        .isSnappable(true);
                QuickReturnListViewOnScrollListener mQuickReturnListViewOnScrollListener = new QuickReturnListViewOnScrollListener(builder) {
                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                    }

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        super.onScrollStateChanged(view, scrollState);
                    }

                };
                fabButton.attachToListView(listView, null, mQuickReturnListViewOnScrollListener);
                fabButton.setVisibility(View.GONE);
                findViewById(R.id.error_view).setVisibility(View.VISIBLE);
                ((ErrorView) findViewById(R.id.error_view)).setSubtitle(R.string.txt_no_products);
            }
        }
    }

    public void changeView(View view) {
        if(view.isClickable()) {
            if (actionMode != null)
                actionMode.finish();
            Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_out);
            slide.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_in);
                    listView.startAnimation(slide);
                    if (getApplicationContext().getSharedPreferences("ShopPrefs", 0).getInt("viewType", 1) == 1) {
                        btnView.setImageResource(R.drawable.changestyle_big);
                        listView.setAdapter(new ProductsListAdapter(act, list, R.layout.listrow_products_small));
                        getApplicationContext().getSharedPreferences("ShopPrefs", 0).edit().putInt("viewType", 2).apply();
                    } else if (getApplicationContext().getSharedPreferences("ShopPrefs", 0).getInt("viewType", 1) == 2) {
                        btnView.setImageResource(R.drawable.changestyle_rows);
                        listView.setAdapter(new ProductsListAdapter(act, list, R.layout.listrow_products_big));
                        getApplicationContext().getSharedPreferences("ShopPrefs", 0).edit().putInt("viewType", 1).apply();
                    }

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            listView.startAnimation(slide);
        }
    }

    public void goUpButton(View view) {
        listView.smoothScrollToPosition(0);
    }

    public void openFilters(View view) {
        if(view.isClickable() && !fromSearch) {
        /*Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.filtersFragment);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragment.isHidden()) {
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            ft.show(fragment);
        } else {
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            ft.hide(fragment);
        }
        ft.commit();*/
            dialogFilters.show();
        }
    }

    public static class FiltersFragment extends Fragment {
        Activity act;
        View view;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {

            // Inflate the layout for this fragment
            view =  inflater.inflate(R.layout.fragment_filters,
                    container, false);
            act = getActivity();

            if(((ProductsActivity) act).noNetworkHandler.showDialog())
                new GetProductsFilter().execute();
            return view;
        }


        @Override
        public void onHiddenChanged(final boolean visible) {
            super.onHiddenChanged(visible);
            if(!((ProductsActivity) getActivity()).firstTime){
                if (visible) {
                    //getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    act.findViewById(R.id.fabButton).setVisibility(View.VISIBLE);
                    System.out.println("not_visible");
                } else {
                    /*WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                    lp.dimAmount=0.0f;
                    getActivity().getWindow().setAttributes(lp);
                    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);*/
                    act.findViewById(R.id.fabButton).setVisibility(View.GONE);
                    System.out.println("visible");
                }
            }
            else{
                ((ProductsActivity) getActivity()).firstTime = false;
            }
        }

        public void getFiltersExecute(){
            if(((ProductsActivity) act).noNetworkHandler.showDialog())
                new GetProductsFilter().execute();
        }

        class GetProductsFilter extends AsyncTask<Void, Void, List<FilterItem>>{

            @Override
            protected void onPreExecute(){
                ((LinearLayout) view.findViewById(R.id.f_rel)).removeAllViewsInLayout();
                ((ProductsActivity) act).filterViews.clear();
            }

            @Override
            protected List<FilterItem> doInBackground(Void... params) {
                String response = StoreApplication.getServiceHandler(act).makeServiceCall(act, true, String.format(act.getResources().getString(R.string.url_products_filters), act.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), act.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), String.valueOf(act.getIntent().getExtras().getInt("category_id"))), ServiceHandler.GET);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("code").equals("0x0000")) {
                        return new FiltersParser().parse(jsonObject.getJSONObject("info").getJSONArray("filters"));
                    }
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<FilterItem> result){
                if(result !=null){
                    for(FilterItem filter : result) {
                        System.out.println(filter.getFilter_name());
                        if (filter.getRanges().size() > 0) {
                            System.out.println("mpika");
                            FilterCheckboxesGroupView group = new FilterCheckboxesGroupView(act);
                            group.setFilter(filter);
                            group.addTitle(filter.getFilter_name());
                            for (FilterRangeItem range : filter.getRanges()) {
                                group.addCheckBox(range.getRange_name(), range.getRange_id());
                            }
                            ((LinearLayout) view.findViewById(R.id.f_rel)).addView(group);
                            ((ProductsActivity) act).filterViews.add(group);
                        }
                        else{
                            FilterRangeGroupView group = new FilterRangeGroupView(act);
                            group.setFilter(filter);
                            group.addTitle(filter.getFilter_name());
                            group.setMinHint(StoreApplication.getCurrency_symbol(act) + String.valueOf(filter.getMin()));
                            group.setMaxHint(StoreApplication.getCurrency_symbol(act) + String.valueOf(filter.getMax()));
                            ((LinearLayout) view.findViewById(R.id.f_rel)).addView(group);
                            ((ProductsActivity) act).filterViews.add(group);
                        }
                    }
                }
            }
        }
    }
}
