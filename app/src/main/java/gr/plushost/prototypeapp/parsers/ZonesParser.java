package gr.plushost.prototypeapp.parsers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gr.plushost.prototypeapp.items.ZoneItem;

/**
 * Created by billiout on 7/3/2015.
 */
public class ZonesParser {
    public List<ZoneItem> parse (JSONArray response){
        List<ZoneItem> list = new ArrayList<>();

        try{
            for(int i = 0; i < response.length(); i++){
                JSONObject country = response.getJSONObject(i);
                ZoneItem item = new ZoneItem();

                item.setCountry_id(country.getString("country_id"));
                item.setZone_id(country.getInt("zone_id"));
                item.setZone_name(country.getString("zone_name"));
                item.setZone_code(country.getString("zone_code"));

                list.add(item);

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return  list;
    }
}
