package gr.plushost.prototypeapp.adapters.viewpagers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

/**
 * Created by Billys on 16/9/2014.
 */
public class BannersImageSlideAdapter extends PagerAdapter {
    Activity activity;
    List<String> products;

    public BannersImageSlideAdapter(Activity activity, List<String> products) {
        this.activity = activity;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.viewpager_banner, container, false);

        ImageView mImageView = (ImageView) view
                .findViewById(R.id.image_display);

        final ProgressWheel spinner = (ProgressWheel) view.findViewById(R.id.progressBarVP);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                //.considerExifParams(true)
                //.bitmapConfig(Bitmap.Config.RGB_565)
                //.imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(500))
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(activity.getApplicationContext()).build();
        if (! ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(config);
        }
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(products.get(position), mImageView, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                spinner.setVisibility(View.GONE);
            }
        }, new ImageLoadingProgressListener(){

            @Override
            public void onProgressUpdate(String s, View view, int current, int total) {
                spinner.setProgress(Math.round(360.0f * current / total));
            }
        });

        container.addView(view);
        return view;
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
