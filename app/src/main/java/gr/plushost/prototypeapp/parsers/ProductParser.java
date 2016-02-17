package gr.plushost.prototypeapp.parsers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.items.*;

/**
 * Created by billiout on 4/3/2015.
 */
public class ProductParser {

    public ProductItem parse(JSONObject response){
        ProductItem item = new ProductItem();

        if(response != null) {
            try {

                item.setItem_id(response.getInt("item_id"));
                item.setDisplay_id(response.getString("display_id"));
                item.setThumbnail_pic_url(response.getString("thumbnail_pic_url"));
                item.setItem_title(response.getString("item_title"));
                item.setPost_free(response.getBoolean("post_free"));
                item.setShipping_fee(response.getDouble("shipping_fee"));
                item.setPrice(response.getString("price"));
                item.setItem_url(response.getString("item_url"));
                item.setAllow_add_to_cart(response.getBoolean("allow_add_to_cart"));
                item.setQty(response.getInt("qty"));
                item.setItem_status(response.getString("item_status"));
                item.setCategoriesNames(response.getString("categories_names"));
                item.setFree_shipping(response.getString("free_shipping"));
                if(response.has("rating_count")) {
                    item.setRating_count(response.getInt("rating_count"));
                }
                else{
                    item.setRating_count(-1);
                }
                item.setStuff_status(response.getString("stuff_status"));
                item.setVirtual_flag(response.getBoolean("virtual_flag"));
                item.setCurrency(response.getString("currency"));
                item.setInitial_price(response.getString("initial_price"));
                if(Double.valueOf(item.getInitial_price().replace(",","")) > 0 && Double.valueOf(item.getInitial_price().replace(",","")).equals(Double.valueOf(item.getPrice().replace(",",""))))
                    item.setInitial_price("0");
                item.setDetail_description(response.getString("detail_description").replace("background-color: #ffffff;", ""));
                item.setShort_description(response.getString("short_description").replace("background-color: #ffffff;", ""));
                item.setOut_of_stock_actions(response.getString("out_of_stock_actions"));
                item.setPromo_text(response.getString("promo_text"));
                item.setAvail_since(response.getString("avail_since"));
                item.setDiscussion_type(response.getString("discussion_type"));

                if (Double.valueOf(item.getInitial_price().replace(",","")) > 0 && Double.valueOf(item.getPrice().replace(",","")) < Double.valueOf(item.getInitial_price().replace(",",""))) {
                    double res = ((Double.valueOf(item.getInitial_price().replace(",","")) - Double.valueOf(item.getPrice().replace(",","")))/Double.valueOf(item.getInitial_price().replace(",","")))*100;
                    item.setDiscount(Math.round(res));
                } else {
                    item.setDiscount(0);
                }

                if(response.has("rating_score")) {
                    Object obj = response.get("rating_score");
                    if (obj instanceof Boolean) {
                        item.setRating_score(-1);
                    } else {
                        item.setRating_score(response.getDouble("rating_score"));
                    }
                }
                else{
                    item.setRating_score(-1);
                }

                JSONArray tierPrices = response.getJSONObject("prices").getJSONArray("tier_prices");
                List<ProductTierPriceItem> tierPriceItemList = new ArrayList<>();
                for(int j = 0; j < tierPrices.length(); j++){
                    ProductTierPriceItem tierPriceItem = new ProductTierPriceItem();
                    tierPriceItem.setMin_qty(tierPrices.getJSONObject(j).getInt("min_qty"));
                    tierPriceItem.setPrice(tierPrices.getJSONObject(j).getString("price"));

                    tierPriceItemList.add(tierPriceItem);
                }
                item.setTier_prices(tierPriceItemList);

                JSONArray cidsArray = response.getJSONArray("cid");
                List<Integer> cids = new ArrayList<>();
                for(int  j = 0; j < cidsArray.length(); j++){
                    cids.add(cidsArray.getInt(j));
                }
                item.setCids(cids);

                JSONArray imgsArray = response.getJSONArray("item_imgs");
                List<String> imgs = new ArrayList<>();
                for(int  j = 0; j < imgsArray.length(); j++){
                    imgs.add(imgsArray.getJSONObject(j).getString("img_url"));
                }
                item.setItem_imgs(imgs);

                JSONArray options = response.getJSONArray("attributes");
                List<ProductAttributeItem> attributeItemList = new ArrayList<>();
                for(int i = 0; i < options.length(); i++){
                    JSONObject jsonObject = options.getJSONObject(i);
                    ProductAttributeItem attributeItem = new ProductAttributeItem();
                    attributeItem.setAttribute_id(jsonObject.getInt("attribute_id"));
                    attributeItem.setCustom_name(jsonObject.getString("custom_name"));
                    attributeItem.setCustom_text(jsonObject.getString("custom_text"));
                    attributeItem.setInput(jsonObject.getString("input"));
                    attributeItem.setRequired(jsonObject.getBoolean("required"));
                    attributeItem.setTitle(jsonObject.getString("title"));
                    attributeItem.setIncorrect_message(jsonObject.getString("incorrect_message"));
                    attributeItem.setInner_hint(jsonObject.getString("inner_hint"));
                    attributeItem.setRegex(jsonObject.getString("regexp"));

                    List<ProductAttributeOptionItem> valuesOptions = new ArrayList<>();
                    if(jsonObject.has("options")) {
                        JSONArray values = jsonObject.getJSONArray("options");
                        for(int j = 0; j < values.length(); j++){
                            JSONObject jsonObject2 = values.getJSONObject(j);
                            ProductAttributeOptionItem valueItem = new ProductAttributeOptionItem();
                            valueItem.setAttribute_id(jsonObject2.getInt("attribute_id"));
                            valueItem.setTitle(jsonObject2.getString("title"));
                            valueItem.setOption_id(jsonObject2.getInt("option_id"));
                            valueItem.setValue(jsonObject2.getString("value"));
                            valueItem.setType(jsonObject2.getString("type"));
                            valueItem.setImg_url(jsonObject2.getString("img_url"));

                            valuesOptions.add(valueItem);
                        }
                    }
                    attributeItem.setOptions(valuesOptions);

                    attributeItemList.add(attributeItem);
                }
                item.setAttributes(attributeItemList);

                JSONArray specifications = response.getJSONArray("specifications");
                List<ProductSpecificationItem> specificationItemList = new ArrayList<>();
                for(int i = 0; i < specifications.length(); i++){
                    ProductSpecificationItem specificationItem = new ProductSpecificationItem();

                    specificationItem.setName(specifications.getJSONObject(i).getString("name"));
                    specificationItem.setValue(specifications.getJSONObject(i).getString("value"));

                    specificationItemList.add(specificationItem);
                }
                item.setSpecifications(specificationItemList);

                List<MiniProductItem> relatedItemsList = new ArrayList<>();
                JSONArray relatedItamesArray = response.getJSONObject("related_items").getJSONArray("items");
                for(int i = 0; i < relatedItamesArray.length(); i++){
                    MiniProductItem miniProductItem = new MiniProductItem();
                    miniProductItem.setItem_id(relatedItamesArray.getJSONObject(i).getInt("item_id"));
                    miniProductItem.setItem_title(relatedItamesArray.getJSONObject(i).getString("item_title"));
                    miniProductItem.setInitial_price(relatedItamesArray.getJSONObject(i).getString("initial_price"));
                    miniProductItem.setPrice(relatedItamesArray.getJSONObject(i).getString("price"));
                    miniProductItem.setItem_url(relatedItamesArray.getJSONObject(i).getString("item_url"));
                    miniProductItem.setThumbnail_pic_url(relatedItamesArray.getJSONObject(i).getString("thumbnail_pic_url"));
                    miniProductItem.setDisplay_id(relatedItamesArray.getJSONObject(i).getString("display_id"));
                    miniProductItem.setDiscount(relatedItamesArray.getJSONObject(i).getLong("discount"));
                    relatedItemsList.add(miniProductItem);
                }
                item.setRelated_items(relatedItemsList);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        return item;
    }
}
