package gr.plushost.prototypeapp.parsers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gr.plushost.prototypeapp.items.FilterItem;
import gr.plushost.prototypeapp.items.FilterRangeItem;

/**
 * Created by user on 3/8/2015.
 */
public class FiltersParser {
    public List<FilterItem> parse(JSONArray response){
        List<FilterItem> list = new ArrayList<>();

        if(response != null){
            try{
                for(int i = 0; i < response.length(); i++){
                    JSONObject item = response.getJSONObject(i);
                    FilterItem filterItem = new FilterItem();

                    filterItem.setFeature_id(item.getString("feature_id"));
                    filterItem.setFilter_id(item.getString("filter_id"));
                    filterItem.setFilter_name(item.getString("filter_name"));
                    filterItem.setSlider(item.getBoolean("slider"));
                    filterItem.setCondition_type(item.getString("condition_type"));
                    filterItem.setField_type(item.getString("feature_type"));
                    filterItem.setField_type(item.getString("field_type"));
                    if(!(item.isNull("range_values"))) {
                        filterItem.setMin(item.getJSONObject("range_values").getInt("min"));
                        filterItem.setMax(item.getJSONObject("range_values").getInt("max"));
                    }

                    List<FilterRangeItem> ranges = new ArrayList<>();
                    for(int j = 0; j < item.getJSONArray("ranges").length(); j++){
                        FilterRangeItem range = new FilterRangeItem();
                        range.setFeature_id(item.getJSONArray("ranges").getJSONObject(j).getString("feature_id"));
                        range.setProducts(item.getJSONArray("ranges").getJSONObject(j).getString("products"));
                        range.setRange_id(item.getJSONArray("ranges").getJSONObject(j).getString("range_id"));
                        range.setRange_name(item.getJSONArray("ranges").getJSONObject(j).getString("range_name"));
                        range.setFeature_type(item.getJSONArray("ranges").getJSONObject(j).getString("feature_type"));
                        range.setFilter_id(item.getJSONArray("ranges").getJSONObject(j).getString("filter_id"));
                        range.setField_type(item.getJSONArray("ranges").getJSONObject(j).getString("field_type"));

                        ranges.add(range);
                    }
                    filterItem.setRanges(ranges);

                    list.add(filterItem);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        return list;
    }
}
