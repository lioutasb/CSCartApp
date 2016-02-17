package gr.plushost.prototypeapp.adapters.listviews;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.items.CustomerOrderItem;
import gr.plushost.prototypeapp.items.CustomerOrderPriceInfoItem;

/**
 * Created by billiout on 22/3/2015.
 */
public class OrdersListAdapter extends BaseAdapter {
    Context context;
    List<CustomerOrderItem> list;
    private static LayoutInflater inflater=null;

    public OrdersListAdapter(Context context, List<CustomerOrderItem> list){
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
            view = inflater.inflate(R.layout.listrow_orders, viewGroup, false);

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView status = (TextView) view.findViewById(R.id.status);
        TextView customer = (TextView) view.findViewById(R.id.customer);
        TextView total_price = (TextView) view.findViewById(R.id.total_price);

        title.setText(String.format(context.getResources().getString(R.string.orders_title), list.get(i).getDisplay_id(), list.get(i).getDate_added()));//"ΠΑΡΑΓΓΕΛΙΑ " + list.get(i).getDisplay_id() + "\n(" + list.get(i).getDate_added() + ")");
        if (!list.get(i).getOrder_status().getStatus_color().equals(""))
            status.setTextColor(Color.parseColor(list.get(i).getOrder_status().getStatus_color()));
        status.setText(list.get(i).getOrder_status().getStatus_name());
        customer.setText(list.get(i).getFirstname() + " " + list.get(i).getLastname() + "\n(" + list.get(i).getEmail() + ")");
        String price = "";
        for(CustomerOrderPriceInfoItem priceInfoItem : list.get(i).getPrice_infos()){
            if(priceInfoItem.getType().equals("total")){
                price = priceInfoItem.getPrice();
            }
        }
        total_price.setText(String.format(context.getResources().getString(R.string.orders_total_price),StoreApplication.getCurrency_symbol(context) + price));

        return view;
    }
}
