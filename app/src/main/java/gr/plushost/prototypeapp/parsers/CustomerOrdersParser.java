package gr.plushost.prototypeapp.parsers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gr.plushost.prototypeapp.items.CustomerOrderItem;
import gr.plushost.prototypeapp.items.CustomerOrderPriceInfoItem;
import gr.plushost.prototypeapp.items.CustomerOrderStatusItem;

/**
 * Created by billiout on 21/3/2015.
 */
public class CustomerOrdersParser {
    public List<CustomerOrderItem> parse(JSONArray response){
        List<CustomerOrderItem> list = new ArrayList<>();

        if(response != null){
            try {
                for(int i = 0; i < response.length(); i++){
                    CustomerOrderItem item = new CustomerOrderItem();
                    JSONObject jsonObject = response.getJSONObject(i);

                    item.setOrder_id(jsonObject.getString("order_id"));
                    item.setCurrency(jsonObject.getString("currency"));
                    item.setDate_added(jsonObject.getString("date_added"));
                    item.setDisplay_id(jsonObject.getString("display_id"));
                    item.setEmail(jsonObject.getString("email"));
                    item.setFirstname(jsonObject.getString("firstname"));
                    item.setLastname(jsonObject.getString("lastname"));

                    CustomerOrderStatusItem statusItem = new CustomerOrderStatusItem();
                    statusItem.setComments(jsonObject.getJSONObject("order_status").getString("comments"));
                    statusItem.setPosition(jsonObject.getJSONObject("order_status").getInt("position"));
                    statusItem.setStatus_id(jsonObject.getJSONObject("order_status").getString("status_id"));
                    statusItem.setStatus_name(jsonObject.getJSONObject("order_status").getString("status_name"));
                    statusItem.setStatus_type(jsonObject.getJSONObject("order_status").getString("status_type"));
                    statusItem.setStatus_color(jsonObject.getJSONObject("order_status").getString("status_color"));
                    item.setOrder_status(statusItem);

                    List<CustomerOrderPriceInfoItem> prices = new ArrayList<>();
                    JSONArray pricesArray = jsonObject.getJSONArray("price_infos");
                    for(int j = 0; j < pricesArray.length(); j++){
                        CustomerOrderPriceInfoItem priceItem = new CustomerOrderPriceInfoItem();

                        priceItem.setPosition(pricesArray.getJSONObject(j).getInt("position"));
                        priceItem.setCurrency(pricesArray.getJSONObject(j).getString("currency"));
                        priceItem.setPrice(pricesArray.getJSONObject(j).getString("price"));
                        priceItem.setTitle(pricesArray.getJSONObject(j).getString("title"));
                        priceItem.setType(pricesArray.getJSONObject(j).getString("type"));

                        prices.add(priceItem);
                    }
                    item.setPrice_infos(prices);

                    list.add(item);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        return list;
    }
}
