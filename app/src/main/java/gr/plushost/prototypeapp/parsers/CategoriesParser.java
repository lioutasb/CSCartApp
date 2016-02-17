package gr.plushost.prototypeapp.parsers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import gr.plushost.prototypeapp.items.CategoryItem;

/**
 * Created by billiout on 20/2/2015.
 */
public class CategoriesParser {

    public List<CategoryItem> parse(JSONArray response){
        List<CategoryItem> list = new ArrayList<>();

        if(response != null) {
            try {

                for(int i = 0; i < response.length(); i++){
                    CategoryItem item = new CategoryItem();
                    JSONObject obj = response.getJSONObject(i);
                    item.setTitle(obj.getString("name"));
                    item.setID(obj.getInt("cid"));
                    item.setParent_id(obj.getInt("parent_cid"));
                    item.setCount(obj.getInt("count"));
                    item.setIs_parent(obj.getBoolean("is_parent"));
                    item.setPosition(obj.getInt("position"));
                    item.setLevel(obj.getInt("level"));
                    //if(item.getCount() > 0)
                    list.add(item);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Collections.sort(list, new SortByAlpha());
        //Collections.sort(list, new SortByPID());

        return list;
    }

    class SortByAlpha implements Comparator<CategoryItem> {

        @Override
        public int compare(CategoryItem lhs, CategoryItem rhs) {
            if (lhs.getTitle().toLowerCase().compareTo(rhs.getTitle().toLowerCase()) < 0) {
                return -1;
            } else if (lhs.getTitle().toLowerCase().compareTo(rhs.getTitle().toLowerCase()) > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    class SortByPID implements Comparator<CategoryItem> {

        @Override
        public int compare(CategoryItem lhs, CategoryItem rhs) {
            if (lhs.getParent_id() < rhs.getParent_id()) {
                return -1;
            } else if (lhs.getParent_id() > rhs.getParent_id()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
