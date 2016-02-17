package gr.plushost.prototypeapp.adapters.listviews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pkmmte.view.CircularImageView;
import com.todddavies.components.progressbar.ProgressWheel;

import java.util.List;
import java.util.Random;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.activities.ProductActivity;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.items.CartProductItem;

/**
 * Created by billiout on 12/3/2015.
 */
public class CartCardAdapter extends BaseAdapter {
    private Context context;
    private List<CartProductItem> list;
    private static LayoutInflater inflater=null;
    private PopupMenu.OnMenuItemClickListener listener;
    private int item_pressed;

    private enum ColorsBorder{
        ORANGE("1", Color.parseColor("#FF8533")),
        BLUE("2", Color.parseColor("#00CCFF")),
        BLACK("3", Color.parseColor("#000000")),
        RED("4", Color.parseColor("#800000")),
        GREEN("5", Color.parseColor("#006600"));

        private String id;
        private int color;

        ColorsBorder(String id, int color){
            this.id = id;
            this.color = color;
        }

        public static ColorsBorder getColorsBorderFromString(String type) {
            for (ColorsBorder value : ColorsBorder.values()) {
                if (value.id.equals(type)) {
                    return value;
                }
            }
            return ORANGE;
        }

        public int getColour() {
            return color;
        }
    }

    public CartCardAdapter(Context context, List<CartProductItem> list){
        this.context = context;
        this.list = list;
    }

    public int getItem_pressed() {
        return item_pressed;
    }

    public void setItem_pressed(int item_pressed) {
        this.item_pressed = item_pressed;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.listrow_cart, parent, false);

        CircularImageView thumbNail = (CircularImageView) convertView.findViewById(R.id.productImg);
        TextView title = (TextView) convertView.findViewById(R.id.productTitle);
        TextView price = (TextView) convertView.findViewById(R.id.productPrice);
        TextView attributes = (TextView) convertView.findViewById(R.id.productAttributes);
        TextView amount = (TextView) convertView.findViewById(R.id.productAmount);
        TextView total = (TextView) convertView.findViewById(R.id.productTotalPrice);
        TextView id = (TextView) convertView.findViewById(R.id.productId);
        final ImageButton overflow = (ImageButton) convertView.findViewById(R.id.card_header_button_overflow);
        final ProgressWheel spinner = (ProgressWheel) convertView.findViewById(R.id.progressBarVP);

        final CartProductItem item = list.get(position);

        //String x = String.valueOf(new Random().nextInt(5) + 1);
        //thumbNail.setBorderColor(ColorsBorder.getColorsBorderFromString(x).getColour());

        overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, overflow);
                popup.getMenuInflater().inflate(R.menu.menu_cart_item_overflow, popup.getMenu());
                popup.setOnMenuItemClickListener(listener);
                popup.show();
                setItem_pressed(position);
            }
        });

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

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).denyCacheImageMultipleSizesInMemory().build();
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

        title.setText(item.getItem_title());
        id.setText(String.format(context.getResources().getString(R.string.cart_product_code), item.getItem_display_id()));
        price.setText(String.format(context.getResources().getString(R.string.cart_unit_price), StoreApplication.getCurrency_symbol(context) + item.getItem_price()));
        amount.setText(String.format(context.getResources().getString(R.string.cart_qty), item.getQty()));
        total.setText(String.format(context.getResources().getString(R.string.cart_total_price), StoreApplication.getCurrency_symbol(context) + item.getSubtotal_price()));

        String txt = "";
        List<String> attr = item.getDisplay_attributes();
        for(String s : attr){
            txt += s + "\n";
        }
        attributes.setText(txt);

        return convertView;
    }

    public PopupMenu.OnMenuItemClickListener getListener() {
        return listener;
    }

    public void setListener(PopupMenu.OnMenuItemClickListener listener) {
        this.listener = listener;
    }
}
