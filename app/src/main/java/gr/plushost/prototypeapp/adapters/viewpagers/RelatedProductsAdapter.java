package gr.plushost.prototypeapp.adapters.viewpagers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.todddavies.components.progressbar.ProgressWheel;

import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.activities.ProductActivity;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.items.MiniProductItem;
import gr.plushost.prototypeapp.widgets.LabelView;

/**
 * Created by user on 28/7/2015.
 */
public class RelatedProductsAdapter extends PagerAdapter {
    Context context;
    List<MiniProductItem> products;

    public RelatedProductsAdapter(Context context, List<MiniProductItem> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.viewpager_related_products, container, false);

        final ImageView mImageView = (ImageView) view
                .findViewById(R.id.image_display);

        final ProgressWheel spinner = (ProgressWheel) view.findViewById(R.id.progressBarVP);

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

        ((TextView) view.findViewById(R.id.productTitle)).setText(products.get(position).getItem_title());
        ((TextView) view.findViewById(R.id.productPrice)).setText(StoreApplication.getCurrency_symbol(context) + products.get(position).getPrice());

        if(products.get(position).getDiscount() > 0) {
            LabelView label = new LabelView(context, R.drawable.view_line_dotted_red);
            label.setText(String.valueOf(-products.get(position).getDiscount()) + "%");
            label.setTextSize(15);
            if(!context.getResources().getBoolean(R.bool.isTablet))
                label.setTargetView(mImageView, 10, LabelView.Gravity.LEFT_TOP);
            else
                label.setTargetView(mImageView, 15, LabelView.Gravity.LEFT_TOP);

            view.findViewById(R.id.productDiscPrice).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.productDiscPrice)).setText(StoreApplication.getCurrency_symbol(context) + products.get(position).getInitial_price());
            ((TextView) view.findViewById(R.id.productDiscPrice)).setPaintFlags(((TextView) view.findViewById(R.id.productDiscPrice)).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            view.findViewById(R.id.productDiscPrice).setVisibility(View.GONE);
        }

        view.setOnClickListener(new View.OnClickListener() {
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

        container.addView(view);
        return view;
    }

    @Override
    public float getPageWidth(int position) {
        if(products.size() > 1)
            if(context.getResources().getBoolean(R.bool.isTablet))
                return 0.45f;
            else
                return 0.49f;
        return  1.0f;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
