package gr.plushost.prototypeapp.adapters.listviews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
import gr.plushost.prototypeapp.activities.ProductActivity;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.items.MiniProductItem;
import gr.plushost.prototypeapp.items.ProductSpecificationItem;
import gr.plushost.prototypeapp.network.NoNetworkHandler;
import gr.plushost.prototypeapp.network.ServiceHandler;
import gr.plushost.prototypeapp.widgets.LabelView;

/**
 * Created by billiout on 27/2/2015.
 */
public class PopularItemsMainListAdapter extends BaseAdapter {
    Context context;
    List<MiniProductItem> products;
    private static LayoutInflater inflater=null;

    public PopularItemsMainListAdapter(Context context, List<MiniProductItem> products){
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.listrow_popular_items_main, parent, false);

        final ImageView mImageView = (ImageView) convertView
                .findViewById(R.id.image_display);

        final ProgressWheel spinner = (ProgressWheel) convertView.findViewById(R.id.progressBarVP);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageOnFail(R.drawable.no_image)
                .bitmapConfig(Bitmap.Config.RGB_565)
                        //.displayer(new FadeInBitmapDisplayer(500))
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context.getApplicationContext()).build();
        if (! ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(config);
        }
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(products.get(position).getThumbnail_pic_url(), mImageView, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                spinner.setVisibility(View.VISIBLE);
                mImageView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                spinner.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                spinner.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
            }
        }, new ImageLoadingProgressListener(){

            @Override
            public void onProgressUpdate(String s, View view, int current, int total) {
                spinner.setProgress(Math.round(360.0f * current / total));
            }
        });

        ((TextView) convertView.findViewById(R.id.productTitle)).setText(products.get(position).getItem_title());
        ((TextView) convertView.findViewById(R.id.productPrice)).setText(StoreApplication.getCurrency_symbol(context) + products.get(position).getPrice());

        if(products.get(position).getDiscount() > 0) {
            LabelView label = new LabelView(context, R.drawable.view_line_dotted_red);
            label.setText(String.valueOf(-products.get(position).getDiscount()) + "%");
            label.setTextSize(15);
            if(!context.getResources().getBoolean(R.bool.isTablet))
                label.setTargetView(mImageView, 10, LabelView.Gravity.LEFT_TOP);
            else
                label.setTargetView(mImageView, 15, LabelView.Gravity.LEFT_TOP);

            convertView.findViewById(R.id.productDiscPrice).setVisibility(View.VISIBLE);
            ((TextView) convertView.findViewById(R.id.productDiscPrice)).setText(StoreApplication.getCurrency_symbol(context) + products.get(position).getInitial_price());
            ((TextView) convertView.findViewById(R.id.productDiscPrice)).setPaintFlags(((TextView) convertView.findViewById(R.id.productDiscPrice)).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            convertView.findViewById(R.id.productDiscPrice).setVisibility(View.GONE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProductActivity.class);
                i.putExtra("need_download", true);
                i.putExtra("product_id", products.get(position).getItem_id());
                i.putExtra("product_name", products.get(position).getItem_title());
                context.startActivity(i);
                ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //((Activity) context).finish();
            }
        });

        return convertView;
    }
}
