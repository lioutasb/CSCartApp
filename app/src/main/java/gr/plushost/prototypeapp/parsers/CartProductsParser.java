package gr.plushost.prototypeapp.parsers;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import gr.plushost.prototypeapp.items.CartProductItem;

/**
 * Created by billiout on 12/3/2015.
 */
public class CartProductsParser {
    public List<CartProductItem> parse(JSONArray response){
        List<CartProductItem> list = new ArrayList<>();

        try {
            for(int i = 0; i < response.length(); i++){
                CartProductItem item = new CartProductItem();

                item.setThumbnail_pic_url(response.getJSONObject(i).getString("thumbnail_pic_url"));
                item.setCart_item_id(response.getJSONObject(i).getString("cart_item_id"));
                item.setItem_id(response.getJSONObject(i).getInt("item_id"));
                item.setItem_title(response.getJSONObject(i).getString("item_title"));
                item.setItem_display_id(response.getJSONObject(i).getString("item_display_id"));
                item.setCurrency(response.getJSONObject(i).getString("currency"));
                item.setItem_price(response.getJSONObject(i).getString("item_price"));
                item.setSubtotal_price(response.getJSONObject(i).getString("subtotal_price"));
                item.setQty(response.getJSONObject(i).getInt("qty"));

                JSONArray array = response.getJSONObject(i).getJSONArray("display_attributes");
                List<String> attributes = new ArrayList<>();
                for(int j = 0; j < array.length(); j++)
                    attributes.add(array.getString(j));
                item.setDisplay_attributes(attributes);

                list.add(item);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }
}
