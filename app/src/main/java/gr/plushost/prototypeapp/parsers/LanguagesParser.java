package gr.plushost.prototypeapp.parsers;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import gr.plushost.prototypeapp.items.StoreLanguageItem;

/**
 * Created by billiout on 11/3/2015.
 */
public class LanguagesParser {
    public List<StoreLanguageItem> parse(JSONArray response){
        List<StoreLanguageItem> list = new ArrayList<>();

        try {
            for(int i = 0; i < response.length(); i++){
                StoreLanguageItem item = new StoreLanguageItem();

                item.setLanguage_code(response.getJSONObject(i).getString("language_code"));
                item.setLanguage_id(response.getJSONObject(i).getString("language_id"));
                item.setLanguage_name(response.getJSONObject(i).getString("language_name"));
                item.setPosition(response.getJSONObject(i).getInt("position"));

                list.add(item);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }
}
