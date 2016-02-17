package gr.plushost.prototypeapp.parsers;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import gr.plushost.prototypeapp.items.StoreCurrencyItem;

/**
 * Created by billiout on 11/3/2015.
 */
public class CurrenciesParser {
    public List<StoreCurrencyItem> parse(JSONArray response){
        List<StoreCurrencyItem> list = new ArrayList<>();

        try {
            for(int i = 0; i < response.length(); i++){
                StoreCurrencyItem item = new StoreCurrencyItem();

                item.setCurrency_code(response.getJSONObject(i).getString("currency_code"));
                item.setCurrency_symbol(response.getJSONObject(i).getString("currency_symbol"));
                item.setDecimal_places(response.getJSONObject(i).getInt("decimal_places"));
                item.setDecimal_symbol(response.getJSONObject(i).getString("decimal_symbol"));
                item.setDescription(response.getJSONObject(i).getString("description"));
                item.setGroup_symbol(response.getJSONObject(i).getString("group_symbol"));
                item.setIs_default(response.getJSONObject(i).getBoolean("default"));
                item.setCurrency_symbol_right(response.getJSONObject(i).getBoolean("currency_symbol_right"));

                list.add(item);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }
}
