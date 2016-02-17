package gr.plushost.prototypeapp.adapters.listviews;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.todddavies.components.progressbar.ProgressWheel;

import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.items.MiniProductItem;
import gr.plushost.prototypeapp.items.ProductItem;

public class ProductsListAdapter extends BaseAdapter {
	private Activity activity;
    private List<MiniProductItem> data;
    private static LayoutInflater inflater=null;
    private int layout;

    public ProductsListAdapter(Activity activity, List<MiniProductItem> movieItems, int layout) {
        this.activity = activity;
        this.data = movieItems;
        this.layout = layout;
    }
 
    @Override
    public int getCount() {
        return data.size();
    }
 
    @Override
    public Object getItem(int location) {
        return data.get(location);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public  boolean hasStableIds(){
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
 
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(layout, parent, false);

        ImageView thumbNail = (ImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.titleProd);
        TextView orinPrice = (TextView) convertView.findViewById(R.id.orinPrice);
        TextView retPrice = (TextView) convertView.findViewById(R.id.retPrice);
        TextView disc = (TextView) convertView.findViewById(R.id.discount);
        TextView freeship = (TextView) convertView.findViewById(R.id.freeshipText);

        MiniProductItem item = data.get(position);

        final ProgressWheel spinner = (ProgressWheel) convertView.findViewById(R.id.progressBarImgProdcuts);

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

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(activity.getApplicationContext()).build();
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
         

        orinPrice.setText(StoreApplication.getCurrency_symbol(activity)+item.getPrice() + "  ");
        if(Double.valueOf(item.getInitial_price().replace(",","")) > 0){
            retPrice.setVisibility(View.VISIBLE);
            retPrice.setText(StoreApplication.getCurrency_symbol(activity)+item.getInitial_price());
            retPrice.setPaintFlags(retPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            disc.setVisibility(View.VISIBLE);
            if(layout==R.layout.listrow_products_big)
                disc.setText(String.format(activity.getResources().getString(R.string.txt_discount_product), String.format("%d", -item.getDiscount())));
            else if(layout==R.layout.listrow_products_small)
                disc.setText("-"+String.valueOf(item.getDiscount())+"%");
        }
        else{
            retPrice.setVisibility(View.GONE);
            disc.setVisibility(View.GONE);
        }

        if (StoreApplication.getInstance().getFreeShippingPromotionItem().isHas_free_ship() && Double.valueOf(item.getPrice().replace(",", "")) > Double.valueOf(StoreApplication.getInstance().getFreeShippingPromotionItem().getValue()) || item.getFree_shipping().equals("Y")) {
            freeship.setVisibility(View.VISIBLE);
        }
        else{
            freeship.setVisibility(View.GONE);
        }

        return convertView;
    }
}
