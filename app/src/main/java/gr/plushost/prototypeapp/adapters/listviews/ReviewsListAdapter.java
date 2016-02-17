package gr.plushost.prototypeapp.adapters.listviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.items.ReviewItem;

/**
 * Created by billiout on 9/3/2015.
 */
public class ReviewsListAdapter extends BaseAdapter {
    Context context;
    List<ReviewItem> list;
    String discussionType;
    private static LayoutInflater inflater=null;

    public ReviewsListAdapter(Context context, List<ReviewItem> list, String discussionType){
        this.context = context;
        this.list = list;
        this.discussionType = discussionType;
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
            convertView = inflater.inflate(R.layout.listrow_review, parent, false);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
        TextView message = (TextView) convertView.findViewById(R.id.message);

        if(discussionType.equals("B")) {
            name.setText(list.get(position).getUname());
            date.setText(list.get(position).getRate_date());
            ratingBar.setRating(list.get(position).getRate_score());
            message.setText(list.get(position).getRate_content());
        }
        else if(discussionType.equals("C")){
            name.setText(list.get(position).getUname());
            date.setText(list.get(position).getRate_date());
            ratingBar.setVisibility(View.GONE);
            message.setText(list.get(position).getRate_content());
        }
        else if(discussionType.equals("R")){
            name.setText(list.get(position).getUname());
            date.setText(list.get(position).getRate_date());
            ratingBar.setRating(list.get(position).getRate_score());
            message.setVisibility(View.GONE);
        }

        return convertView;
    }
}
