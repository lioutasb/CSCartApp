package gr.plushost.prototypeapp.adapters.listviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.items.CustomerOrderProductItem;

/**
 * Created by billiout on 26/3/2015.
 */
public class OrderProductsListAdapter extends BaseAdapter {
    Context context;
    List<CustomerOrderProductItem> list;
    private static LayoutInflater inflater=null;

    public OrderProductsListAdapter(Context context, List<CustomerOrderProductItem> list){
        this.context = context;
        this.list = list;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.listrow_order_product, viewGroup, false);

        ImageView thumbNail = (ImageView) view.findViewById(R.id.productImg);
        TextView title = (TextView) view.findViewById(R.id.productTitle);
        TextView price = (TextView) view.findViewById(R.id.productPrice);
        TextView attributes = (TextView) view.findViewById(R.id.productAttributes);
        TextView amount = (TextView) view.findViewById(R.id.productAmount);
        TextView total = (TextView) view.findViewById(R.id.productTotalPrice);
        TextView id = (TextView) view.findViewById(R.id.productId);

        CustomerOrderProductItem item = list.get(i);

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

            imageLoader.displayImage(item.getThumbnail_pic_url(), thumbNail, options);
        }
        else {
            thumbNail.setImageResource(R.drawable.no_image);
        }

        title.setText(item.getItem_title());
        id.setText(String.format(context.getResources().getString(R.string.order_product_code), item.getItem_display_id()));
        price.setText(String.format(context.getResources().getString(R.string.order_unit_price), StoreApplication.getCurrency_symbol(context) + item.getPrice()));
        amount.setText(String.format(context.getResources().getString(R.string.order_qty), item.getQty()));
        total.setText(String.format(context.getResources().getString(R.string.order_total_price), StoreApplication.getCurrency_symbol(context) + item.getFinal_price()));
        attributes.setText(Html.fromHtml(item.getDisplay_attributes()));

        return view;
    }
}
