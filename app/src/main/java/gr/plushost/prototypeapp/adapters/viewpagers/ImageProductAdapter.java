package gr.plushost.prototypeapp.adapters.viewpagers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.todddavies.components.progressbar.ProgressWheel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.activities.FullScreenImageActivity;
import gr.plushost.prototypeapp.items.ProductItem;


/**
 * Created by Billiout on 11/8/2014.
 */
public class ImageProductAdapter extends PagerAdapter {
    private Activity _activity;
    private List<String> _imagePaths;
    private LayoutInflater inflater;


    // constructor
    public ImageProductAdapter(Activity activity,List<String> imagePaths) {
        this._activity = activity;
        this._imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imgDisplay;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_image, container,
                false);

        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgShow);

        final ProgressWheel spinner = (ProgressWheel) viewLayout.findViewById(R.id.progressBarProduct);

        if(!_imagePaths.get(position).equals("")) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .showImageOnFail(R.drawable.no_image)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .displayer(new FadeInBitmapDisplayer(500))
                    .build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(_activity.getApplicationContext()).denyCacheImageMultipleSizesInMemory().build();
            if (!ImageLoader.getInstance().isInited()) {
                ImageLoader.getInstance().init(config);
            }
            ImageLoader imageLoader = ImageLoader.getInstance();

            imageLoader.displayImage(_imagePaths.get(position), imgDisplay, options, new SimpleImageLoadingListener() {
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

            imgDisplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent activity = new Intent(_activity, FullScreenImageActivity.class);
                    activity.putExtra("img_urls", new Gson().toJson(_imagePaths));
                    activity.putExtra("pos", position);
                    _activity.startActivity(activity);
                    _activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }
        else{
            spinner.setVisibility(View.GONE);
            imgDisplay.setImageResource(R.drawable.no_image);
        }

        container.addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
