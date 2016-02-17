package gr.plushost.prototypeapp.adapters.listviews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Parcel;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.activities.LoginActivity;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.items.MiniProductItem;
import gr.plushost.prototypeapp.items.ProductItem;
import gr.plushost.prototypeapp.items.ProductSpecificationItem;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;

/**
 * Created by billiout on 27/2/2015.
 */
public class CompareListAdapter extends BaseAdapter {
    Context context;
    List<MiniProductItem> list;
    private static LayoutInflater inflater=null;

    public CompareListAdapter(Context context, List<MiniProductItem> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.listrow_compare, parent, false);

        final ProgressWheel spinner = (ProgressWheel) convertView.findViewById(R.id.progressBarImgProdcuts);
        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.txtTitle);
        TextView features = (TextView) convertView.findViewById(R.id.txtFeatures);
        TextView price = (TextView) convertView.findViewById(R.id.txtPrice);
        TextView discount = (TextView) convertView.findViewById(R.id.txtDiscount);
        TextView shortDesc = (TextView) convertView.findViewById(R.id.txtShortDesc);
        BootstrapButton btnAddToCart = (BootstrapButton) convertView.findViewById(R.id.btnAddToCart);

        final MiniProductItem item = list.get(position);

        if(!item.getThumbnail_pic_url().equals("")) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .showImageOnFail(R.drawable.no_image)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .resetViewBeforeLoading(true)
                    .displayer(new FadeInBitmapDisplayer(500))
                    .build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context.getApplicationContext()).denyCacheImageMultipleSizesInMemory().build();
            if (!ImageLoader.getInstance().isInited()) {
                ImageLoader.getInstance().init(config);
            }
            ImageLoader imageLoader = ImageLoader.getInstance();

            imageLoader.displayImage(item.getThumbnail_pic_url(), thumbNail, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    super.onLoadingStarted(imageUri, view);
                    spinner.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    super.onLoadingFailed(imageUri, view, failReason);
                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    spinner.setVisibility(View.GONE);
                }
            }, new ImageLoadingProgressListener(){

                @Override
                public void onProgressUpdate(String s, View view, int current, int total) {
                    spinner.setProgress(Math.round(360.0f * current / total));
                }
            });
        }
        else{
            spinner.setVisibility(View.GONE);
            thumbNail.setImageResource(R.drawable.no_image);
        }

        SpannableString content = new SpannableString(item.getItem_title());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        title.setText(content);

        List<ProductSpecificationItem> specificationItemList = item.getFeatures();
        String specifications = "";
        for (ProductSpecificationItem specificationItem : specificationItemList) {
            specifications += "<b>" + specificationItem.getName() + ": </b>";
            specifications += specificationItem.getValue() + "<br>";
        }
        if (specifications.equals(""))
            features.setText(context.getResources().getString(R.string.txt_no_available_features));
        else
            features.setText(Html.fromHtml(specifications));



        if(Double.valueOf(item.getInitial_price().replace(",", "")) > 0){
            SpannableString priceRet = new SpannableString(StoreApplication.getCurrency_symbol(context) + item.getPrice() + "  " + StoreApplication.getCurrency_symbol(context) + item.getInitial_price());
            priceRet.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.orange)), 0, item.getPrice().length() + StoreApplication.getCurrency_symbol(context).length(), 0);
            priceRet.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.grey_font)), item.getPrice().length() + 2 + StoreApplication.getCurrency_symbol(context).length(), priceRet.length(), 0);
            priceRet.setSpan(new StrikethroughSpan(), item.getPrice().length() + 2 + StoreApplication.getCurrency_symbol(context).length(), priceRet.length(), 0);
            price.setText(priceRet);
            discount.setVisibility(View.VISIBLE);
            discount.setText(String.valueOf(-item.getDiscount()) + "%");
        }
        else {
            SpannableString priceRet = new SpannableString(StoreApplication.getCurrency_symbol(context) + item.getPrice());
            priceRet.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.orange)), 0, priceRet.length(), 0);
            price.setText(priceRet);
            discount.setVisibility(View.GONE);
        }

        if(!item.getShort_desc().equals("")){
            shortDesc.setVisibility(View.VISIBLE);
            shortDesc.setText(Html.fromHtml(item.getShort_desc()));
        }
        else if(!item.getLong_desc().equals("")){
            shortDesc.setVisibility(View.VISIBLE);
            shortDesc.setText(Html.fromHtml(item.getLong_desc()));
        }
        else {
            shortDesc.setVisibility(View.GONE);
        }

        if(item.getAttributes_length() == 0) {
            btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context.getSharedPreferences("ShopPrefs", 0).getBoolean("is_connected", false)) {

                        if(new NoNetworkHandler(context).showDialog())
                            new AddProductToCart().execute(String.valueOf(item.getItem_id()), "1", "");
                    } else {
                        Intent i = new Intent(context, LoginActivity.class);
                        i.putExtra("page", 0);
                        context.startActivity(i);
                    }
                }
            });
        }
        else{
            btnAddToCart.setEnabled(false);
        }

        return convertView;
    }

    class AddProductToCart extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                String response = StoreApplication.getServiceHandler(context).makeServiceCall(context, true, String.format(context.getResources().getString(R.string.url_add_product_to_cart), context.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), context.getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""), Integer.valueOf(strings[0]), Integer.valueOf(strings[1]), URLEncoder.encode(strings[2], "UTF-8")), ServiceHandler.GET);

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
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(new NoNetworkHandler(context).showDialog())
                new GetCartCount().execute();
            if(result){
                SuperToast.create(context, String.format(context.getResources().getString(R.string.txt_add_to_cart_success)), SuperToast.Duration.SHORT, Style.getStyle(Style.PURPLE, SuperToast.Animations.POPUP)).show();
            }
            else {
                SuperToast.create(context, String.format(context.getResources().getString(R.string.txt_add_to_cart_fail)), SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
            }
        }
    }

    public class GetCartCount extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            String response = StoreApplication.getServiceHandler(context).makeServiceCall(context, true, String.format(context.getResources().getString(R.string.url_cart_count), context.getSharedPreferences("ShopPrefs", 0).getString("store_language", ""), context.getSharedPreferences("ShopPrefs", 0).getString("store_currency", "")), ServiceHandler.GET);

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
        }
    }
}
