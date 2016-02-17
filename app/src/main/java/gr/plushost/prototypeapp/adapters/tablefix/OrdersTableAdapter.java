package gr.plushost.prototypeapp.adapters.tablefix;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.tablefixheaders.adapters.BaseTableAdapter;

public class OrdersTableAdapter<T> extends BaseTableAdapter {

    private final int[] widths = {
            60,
            100,
            150,
            80,
            100
    };
    private final float density;

    private final Context context;

	private T[][] table;

	public OrdersTableAdapter(Context context) {
		this(context, null);
	}

	public OrdersTableAdapter(Context context, T[][] table) {
		this.context = context;

        density = context.getResources().getDisplayMetrics().density;

		setInformation(table);
	}

	public void setInformation(T[][] table) {
		this.table = table;
	}

	@Override
	public int getRowCount() {
		return table.length - 1;
	}

	@Override
	public int getColumnCount() {
		return table[0].length - 1;
	}

	@Override
	public View getView(int row, int column, View convertView, ViewGroup parent) {
		switch (row+1){
            case 0:{
               // if (convertView == null) {
                   convertView = ((Activity)context).getLayoutInflater().inflate(R.layout.item_table_header_orders, parent, false);
               // }
                ((TextView) convertView.findViewById(android.R.id.text1)).setText(table[0][column+1].toString());
                return convertView;
            }
            default:{
                //if (convertView == null) {
                    convertView = ((Activity)context).getLayoutInflater().inflate(R.layout.item_table_body_orders, parent, false);
                    convertView.setBackgroundColor(row % 2 == 0 ? Color.parseColor("#66FFFF") : Color.parseColor("#0066FFFF"));
                //}
                ((TextView) convertView.findViewById(android.R.id.text1)).setText(table[row+1][column+1].toString());
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                return convertView;
            }
        }
        //return convertView;
	}

	@Override
	public int getHeight(int row) {
		return Math.round(60 * density);
	}

	@Override
	public int getWidth(int column) {
	    return Math.round(widths[column + 1] * density);
	}

	@Override
	public int getItemViewType(int row, int column) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}
}
