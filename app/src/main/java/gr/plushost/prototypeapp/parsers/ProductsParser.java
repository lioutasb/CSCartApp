package gr.plushost.prototypeapp.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gr.plushost.prototypeapp.items.MiniProductItem;
import gr.plushost.prototypeapp.items.ProductSpecificationItem;

/**
 * Created by billiout on 6/3/2015.
 */
public class ProductsParser {

    public List<MiniProductItem> parse(JSONArray response){
        List<MiniProductItem> list = new ArrayList<>();

        if(response != null){
            try {

                for(int i = 0; i < response.length(); i++){
                    JSONObject product = response.getJSONObject(i);
                    MiniProductItem item = new MiniProductItem();

                    item.setItem_id(product.getInt("item_id"));
                    item.setDisplay_id(product.getString("display_id"));
                    item.setThumbnail_pic_url(product.getString("thumbnail_pic_url"));
                    item.setItem_title(product.getString("item_title"));
                    item.setPost_free(product.getBoolean("post_free"));
                    item.setShipping_fee(product.getDouble("shipping_fee"));
                    item.setPrice(product.getString("price"));
                    item.setItem_url(product.getString("item_url"));
                    item.setAllow_add_to_cart(product.getBoolean("allow_add_to_cart"));
                    item.setQty(product.getInt("qty"));
                    item.setItem_status(product.getString("item_status"));
                    //item.setRating_count(product.getInt("rating_count"));
                    item.setAttributes_length(product.getInt("attributes_length"));
                    item.setStuff_status(product.getString("stuff_status"));
                    item.setVirtual_flag(product.getBoolean("virtual_flag"));
                    item.setCurrency(product.getString("currency"));
                    item.setFree_shipping(product.getString("free_shipping"));
                    item.setInitial_price(product.getString("initial_price"));
                    if(Double.valueOf(item.getInitial_price().replace(",","")) > 0 && Double.valueOf(item.getInitial_price().replace(",","")).equals(Double.valueOf(item.getPrice().replace(",",""))))
                        item.setInitial_price("0");
                    item.setLong_desc(product.getString("detail_description").replace("background-color: #ffffff;", ""));
                    item.setShort_desc(product.getString("short_description").replace("background-color: #ffffff;", ""));

                    if (Double.valueOf(item.getInitial_price().replace(",","")) > 0 && Double.valueOf(item.getPrice().replace(",","")) < Double.valueOf(item.getInitial_price().replace(",",""))) {
                        double res = ((Double.valueOf(item.getInitial_price().replace(",","")) - Double.valueOf(item.getPrice().replace(",","")))/Double.valueOf(item.getInitial_price().replace(",","")))*100;
                        item.setDiscount(Math.round(res));
                    } else {
                        item.setDiscount(0);
                    }

                    /*Object obj = product.get("rating_score");
                    if(obj instanceof Integer){
                        item.setRating_score(product.getInt("rating_score"));
                    }
                    else{
                        item.setRating_score(-1);
                    }*/

                    JSONArray cidsArray = product.getJSONArray("cid");
                    List<Integer> cids = new ArrayList<>();
                    for(int  j = 0; j < cidsArray.length(); j++){
                        cids.add(cidsArray.getInt(j));
                    }
                    item.setCids(cids);

                    JSONArray specifications = product.getJSONArray("specifications");
                    List<ProductSpecificationItem> specificationItemList = new ArrayList<>();
                    for(int j = 0; j < specifications.length(); j++){
                        ProductSpecificationItem specificationItem = new ProductSpecificationItem();

                        specificationItem.setName(specifications.getJSONObject(j).getString("name"));
                        specificationItem.setValue(specifications.getJSONObject(j).getString("value"));

                        specificationItemList.add(specificationItem);
                    }
                    item.setFeatures(specificationItemList);

                    if(Double.valueOf(item.getPrice().replace(",", "")) > 0 && item.getAllow_add_to_cart())
                        list.add(item);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
