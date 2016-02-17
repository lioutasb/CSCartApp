package gr.plushost.prototypeapp.items;

import java.util.List;

/**
 * Created by user on 3/8/2015.
 */
public class FilterItem {
    private String feature_id = "";
    private String filter_id = "";
    private String filter_name = "";
    private boolean slider = false;
    private String condition_type = "";
    private String feature_type = "";
    private String field_type = "";
    private int min = 0;
    private int max = 0;
    private List<FilterRangeItem> ranges;

    public String getFeature_id() {
        return feature_id;
    }

    public void setFeature_id(String feature_id) {
        this.feature_id = feature_id;
    }

    public String getFilter_name() {
        return filter_name;
    }

    public void setFilter_name(String filter_name) {
        this.filter_name = filter_name;
    }

    public String getFilter_id() {
        return filter_id;
    }

    public void setFilter_id(String filter_id) {
        this.filter_id = filter_id;
    }

    public boolean isSlider() {
        return slider;
    }

    public void setSlider(boolean slider) {
        this.slider = slider;
    }

    public String getCondition_type() {
        return condition_type;
    }

    public void setCondition_type(String condition_type) {
        this.condition_type = condition_type;
    }

    public String getFeature_type() {
        return feature_type;
    }

    public void setFeature_type(String feature_type) {
        this.feature_type = feature_type;
    }

    public String getField_type() {
        return field_type;
    }

    public void setField_type(String field_type) {
        this.field_type = field_type;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public List<FilterRangeItem> getRanges() {
        return ranges;
    }

    public void setRanges(List<FilterRangeItem> ranges) {
        this.ranges = ranges;
    }
}
