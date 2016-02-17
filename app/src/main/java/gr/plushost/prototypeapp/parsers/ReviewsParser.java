package gr.plushost.prototypeapp.parsers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gr.plushost.prototypeapp.items.ReviewItem;

/**
 * Created by billiout on 9/3/2015.
 */
public class ReviewsParser {
    public List<ReviewItem> parse(JSONArray initialArray){
        List<ReviewItem> list = new ArrayList<>();

        try {
            for (int i = 0; i < initialArray.length(); i++) {
                JSONObject object = initialArray.getJSONObject(i);
                ReviewItem item = new ReviewItem();

                item.setItem_id(object.getInt("item_id"));
                if(!object.isNull("rate_score")) {
                    item.setRate_score(object.getInt("rate_score"));
                }
                else {
                    item.setRate_score(-1);
                }

                if(!object.isNull("rate_content")) {
                    item.setRate_content(object.getString("rate_content"));
                }
                else {
                    item.setRate_content("");
                }
                item.setUname(object.getString("uname"));
                item.setRate_date(object.getString("rate_date"));

                list.add(item);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }
}
