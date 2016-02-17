package gr.plushost.prototypeapp.parsers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gr.plushost.prototypeapp.items.CountryItem;

/**
 * Created by billiout on 7/3/2015.
 */
public class CountriesParser {
    public List<CountryItem> parse (JSONArray response){
        List<CountryItem> list = new ArrayList<>();

        try{
            for(int i = 0; i < response.length(); i++){
                JSONObject country = response.getJSONObject(i);
                CountryItem item = new CountryItem();

                item.setCountry_id(country.getString("country_id"));
                item.setCountry_name(country.getString("country_name"));
                item.setCountry_iso_code_2(country.getString("country_iso_code_2"));
                item.setCountry_iso_code_3(country.getString("country_iso_code_3"));

                list.add(item);

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return  list;
    }
}
