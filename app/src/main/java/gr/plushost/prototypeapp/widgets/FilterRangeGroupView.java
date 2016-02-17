package gr.plushost.prototypeapp.widgets;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.items.FilterItem;

/**
 * Created by user on 3/8/2015.
 */
public class FilterRangeGroupView extends RelativeLayout {

    TextView title;
    boolean isFiltersOpen = false;
    FilterItem filter;

    public FilterRangeGroupView(Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_filters_range, this);

        title = (TextView) findViewById(R.id.title);

        /*findViewById(R.id.ldescH).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFiltersOpen) {
                    RelativeLayout txt = (RelativeLayout) findViewById(R.id.ldesc);
                    txt.setVisibility(View.GONE);
                    ImageView img = (ImageView) findViewById(R.id.arrow1);
                    img.setImageResource(R.drawable.arrow_down_small);
                    isFiltersOpen = false;
                } else {
                    RelativeLayout txt = (RelativeLayout) findViewById(R.id.ldesc);
                    txt.setVisibility(View.VISIBLE);
                    ImageView img = (ImageView) findViewById(R.id.arrow1);
                    img.setImageResource(R.drawable.arrow_up_small);
                    isFiltersOpen = true;
                }
            }
        });*/
    }

    public FilterRangeGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_filters_range, this);

        title = (TextView) findViewById(R.id.title);
    }

    public FilterRangeGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_filters_range, this);

        title = (TextView) findViewById(R.id.title);
    }

    public void addTitle(final CharSequence title){
        this.title.setText(title);
    }

    public void setMinHint(String s){
        InputFilterMinMax filterT = new InputFilterMinMax(filter.getMin(), filter.getMax()) {};
        ((TextView) findViewById(R.id.min_e)).setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(5),
                DigitsKeyListener.getInstance()});
        ((TextView) findViewById(R.id.min_e)).setKeyListener(DigitsKeyListener.getInstance());
        ((TextView) findViewById(R.id.min_e)).setHint(s);
        ((TextView) findViewById(R.id.min_e)).setText(String.valueOf(filter.getMin()));
    }

    public void setMaxHint(String s){
        InputFilterMinMax filterT = new InputFilterMinMax(filter.getMin(), filter.getMax()) {};
        ((TextView) findViewById(R.id.max_e)).setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(5),
                DigitsKeyListener.getInstance()});
        ((TextView) findViewById(R.id.max_e)).setKeyListener(DigitsKeyListener.getInstance());
        ((TextView) findViewById(R.id.max_e)).setHint(s);
        ((TextView) findViewById(R.id.max_e)).setText(String.valueOf(filter.getMax()));
    }

    public String getMin(){
        if(((TextView) findViewById(R.id.min_e)).getText().toString().equals(""))
            return String.valueOf((filter.getMin()));
        else if(Integer.valueOf(((TextView) findViewById(R.id.min_e)).getText().toString()) < filter.getMin()){
            ((TextView) findViewById(R.id.min_e)).setText(String.valueOf(filter.getMin()));
            return String.valueOf((filter.getMin()));
        }
        else if(Integer.valueOf(((TextView) findViewById(R.id.min_e)).getText().toString()) > filter.getMax()){
            ((TextView) findViewById(R.id.min_e)).setText(String.valueOf(filter.getMax()));
            return String.valueOf((filter.getMax()));
        }
        else {
            return ((TextView) findViewById(R.id.min_e)).getText().toString();
        }
    }

    public String getMax(){
        if(((TextView) findViewById(R.id.max_e)).getText().toString().equals(""))
            return String.valueOf((filter.getMax()));
        else if(Integer.valueOf(((TextView) findViewById(R.id.max_e)).getText().toString()) < filter.getMin()){
            ((TextView) findViewById(R.id.max_e)).setText(String.valueOf(filter.getMin()));
            return String.valueOf((filter.getMin()));
        }
        else if(Integer.valueOf(((TextView) findViewById(R.id.max_e)).getText().toString()) > filter.getMax()){
            ((TextView) findViewById(R.id.max_e)).setText(String.valueOf(filter.getMax()));
            return String.valueOf((filter.getMax()));
        }
        else {
            return ((TextView) findViewById(R.id.max_e)).getText().toString();
        }
    }

    public String getFeatureHash(){
        return filter.getField_type() + getMin() + "-" + getMax() + "-" + getContext().getSharedPreferences("ShopPrefs", 0).getString("store_currency", "");
    }

    public void setFilter(FilterItem filter) {
        this.filter = filter;
    }

    public class InputFilterMinMax implements InputFilter {

        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}
