package gr.plushost.prototypeapp.parsers;

import org.json.JSONObject;

import gr.plushost.prototypeapp.items.FreeShippingPromotionItem;

/**
 * Created by user on 5/8/2015.
 */
public class FreeShippingPromotionParser {

    public FreeShippingPromotionItem parse(JSONObject object){
        FreeShippingPromotionItem item = new FreeShippingPromotionItem();

        try {
            item.setHas_free_ship(object.getBoolean("has_free_ship"));

            if(item.isHas_free_ship()){
                item.setName(object.getString("name"));
                item.setConditions_hash(object.getString("conditions_hash"));
                item.setValue(object.getString("value"));
                item.setPromotion_id(object.getString("promotion_id"));
                item.setZone(object.getString("zone"));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return item;
    }
}
