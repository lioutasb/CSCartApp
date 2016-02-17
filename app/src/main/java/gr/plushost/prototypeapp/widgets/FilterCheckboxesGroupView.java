package gr.plushost.prototypeapp.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.items.FilterItem;
import gr.plushost.prototypeapp.items.FilterRangeItem;

/**
 * Created by user on 3/8/2015.
 */
public class FilterCheckboxesGroupView extends LinearLayout {

    private int checkBoxesCount = 0;
    TextView title;
    LinearLayout content;
    boolean isFiltersOpen = true;
    FilterItem filter;
    List<CheckBox> checkboxes = new ArrayList<>();

    public FilterCheckboxesGroupView(Context context) {
        super(context);

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_filters_checkboxes, this);

        title = (TextView) findViewById(R.id.title);
        content = (LinearLayout) findViewById(R.id.content);

        findViewById(R.id.ldescH).setOnClickListener(new OnClickListener() {
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
        });
    }

    public FilterCheckboxesGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_filters_checkboxes, this);

        title = (TextView) findViewById(R.id.title);
        content = (LinearLayout) findViewById(R.id.content);
    }

    public FilterCheckboxesGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_filters_checkboxes, this);

        title = (TextView) findViewById(R.id.title);
        content = (LinearLayout) findViewById(R.id.content);
    }

    public void addTitle(final CharSequence title){
        this.title.setText(title);
    }

    public void setFilter(FilterItem filter) {
        this.filter = filter;
    }

    public CheckBox addCheckBox(final CharSequence text, final Object tag){
        final CheckBox checkBox = new CheckBox(getContext());

        checkBox.setId(tag.hashCode());
        checkBox.setText(text);
        checkBox.setTag(tag);
        content.addView(checkBox);

        checkBoxesCount++;
        checkboxes.add(checkBox);
        return checkBox;
    }

    public String getFeatureHash(){
        List<FilterRangeItem> ranges = filter.getRanges();
        int i = 0;
        String result = "";
        for(FilterRangeItem range : ranges){
            if(checkboxes.get(i).isChecked()){
                result += filter.getField_type() + range.getRange_id() + ".";
            }
            i++;
        }
        return result;
    }
}
