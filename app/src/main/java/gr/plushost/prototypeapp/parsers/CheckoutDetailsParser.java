package gr.plushost.prototypeapp.parsers;

import android.text.Html;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gr.plushost.prototypeapp.items.AddressItem;
import gr.plushost.prototypeapp.items.CustomerOrderItem;
import gr.plushost.prototypeapp.items.CustomerOrderPriceInfoItem;
import gr.plushost.prototypeapp.items.CustomerOrderProductItem;
import gr.plushost.prototypeapp.items.CustomerOrderStatusItem;
import gr.plushost.prototypeapp.items.PaymentMethodItem;
import gr.plushost.prototypeapp.items.ShippingMethodItem;
import gr.plushost.prototypeapp.items.UserAddressItem;

/**
 * Created by billiout on 25/3/2015.
 */
public class CheckoutDetailsParser {
    public CustomerOrderItem parse(JSONObject orderJ){
        CustomerOrderItem item = new CustomerOrderItem();

        try {

            List<CustomerOrderPriceInfoItem> prices = new ArrayList<>();
            JSONArray pricesArray = orderJ.getJSONArray("price_infos");
            for (int j = 0; j < pricesArray.length(); j++) {
                CustomerOrderPriceInfoItem priceItem = new CustomerOrderPriceInfoItem();

                priceItem.setPosition(pricesArray.getJSONObject(j).getInt("position"));
                priceItem.setCurrency(pricesArray.getJSONObject(j).getString("currency"));
                priceItem.setPrice(pricesArray.getJSONObject(j).getString("price"));
                priceItem.setTitle(pricesArray.getJSONObject(j).getString("title"));
                priceItem.setType(pricesArray.getJSONObject(j).getString("type"));

                prices.add(priceItem);
            }
            item.setPrice_infos(prices);

            List<CustomerOrderProductItem> productItems = new ArrayList<>();
            JSONArray products = orderJ.getJSONArray("review_orders").getJSONObject(0).getJSONArray("cart_items");
            for(int i = 0; i < products.length(); i++) {
                CustomerOrderProductItem product = new CustomerOrderProductItem();

                product.setPrice(products.getJSONObject(i).getString("item_price"));
                product.setThumbnail_pic_url(products.getJSONObject(i).getString("thumbnail_pic_url"));
                JSONArray array = products.getJSONObject(i).getJSONArray("display_attributes");
                String attributes = "";
                for (int j = 0; j < array.length(); j++){
                    if (j < array.length() - 1) {
                        attributes += array.getString(j) + "<br>";
                    } else{
                        attributes += array.getString(j);
                    }
                }
                product.setDisplay_attributes(attributes);
                product.setFinal_price(products.getJSONObject(i).getString("subtotal_price"));
                product.setItem_id(products.getJSONObject(i).getString("item_id"));
                product.setItem_display_id(products.getJSONObject(i).getString("item_display_id"));
                product.setItem_title(products.getJSONObject(i).getString("item_title"));
                product.setQty(products.getJSONObject(i).getString("qty"));

                productItems.add(product);
            }
            item.setOrder_items(productItems);

            item.setNotes(orderJ.getString("notes"));

            /*AddressItem shippingAddress = new AddressItem();
            JSONObject shipping_address = orderJ.getJSONObject("shipping_address");
            shippingAddress.setAddress1(shipping_address.getString("address1"));
            shippingAddress.setAddress2(shipping_address.getString("address2"));
            shippingAddress.setAddress_book_id(shipping_address.getString("address_book_id"));
            shippingAddress.setCity(shipping_address.getString("city"));
            //shippingAddress.setCountry_code(shipping_address.getString("country_code"));
            shippingAddress.setCountry_id(shipping_address.getString("country_id"));
            shippingAddress.setCountry_name(Html.fromHtml(shipping_address.getString("country_name")).toString());
            shippingAddress.setFirstname(shipping_address.getString("firstname"));
            shippingAddress.setGender(shipping_address.getString("gender"));
            shippingAddress.setLastname(shipping_address.getString("lastname"));
            shippingAddress.setPostcode(shipping_address.getString("postcode"));
            //shippingAddress.setState(shipping_address.getString("state"));
            //shippingAddress.setTax_code(shipping_address.getString("tax_code"));
            shippingAddress.setTelephone(shipping_address.getString("telephone"));
            shippingAddress.setZone_code(shipping_address.getString("zone_code"));
            shippingAddress.setZone_id(shipping_address.getString("zone_id"));
            shippingAddress.setZone_name(shipping_address.getString("zone_name"));
            item.setShipping_address(shippingAddress);

            AddressItem billingAddress = new AddressItem();
            JSONObject billing_address = orderJ.getJSONObject("billing_address");
            billingAddress.setAddress1(billing_address.getString("address1"));
            billingAddress.setAddress2(billing_address.getString("address2"));
            billingAddress.setAddress_book_id(billing_address.getString("address_book_id"));
            billingAddress.setAddress_type(billing_address.getString("address_type"));
            billingAddress.setCity(billing_address.getString("city"));
            billingAddress.setCountry_id(billing_address.getString("country_id"));
            billingAddress.setCountry_name(Html.fromHtml(shipping_address.getString(billing_address.getString("country_name"))).toString());
            billingAddress.setFirstname(billing_address.getString("firstname"));
            billingAddress.setGender(billing_address.getString("gender"));
            billingAddress.setLastname(billing_address.getString("lastname"));
            billingAddress.setPostcode(billing_address.getString("postcode"));
            //billingAddress.setState(billing_address.getString("state"));
            //billingAddress.setTax_code(billing_address.getString("tax_code"));
            billingAddress.setTelephone(billing_address.getString("telephone"));
            billingAddress.setZone_code(billing_address.getString("zone_code"));
            billingAddress.setZone_id(billing_address.getString("zone_id"));
            billingAddress.setZone_name(billing_address.getString("zone_name"));
            item.setBilling_address(billingAddress);*/

            UserAddressItem shippingAdress = new AddressParser().parse(orderJ.getJSONObject("shipping_address"));
            UserAddressItem billingAdress = new AddressParser().parse(orderJ.getJSONObject("billing_address"));

            item.setBilling_address_2(billingAdress);
            item.setShipping_address_2(shippingAdress);

            PaymentMethodItem paymentMethod = new PaymentMethodItem();
            JSONObject payment_method = orderJ.getJSONObject("payment_method");
            paymentMethod.setPm_description(payment_method.getString("pm_description"));
            paymentMethod.setPm_id(payment_method.getString("pm_id"));
            //paymentMethod.setPm_img_url(payment_method.getString("pm_img_url"));
            paymentMethod.setPm_title(payment_method.getString("pm_title"));
            item.setPayment_method(paymentMethod);

            ShippingMethodItem shippingMethod = new ShippingMethodItem();
            JSONObject shipping_method = orderJ.getJSONArray("review_orders").getJSONObject(0).getJSONObject("shipping_method");
            shippingMethod.setTitle(shipping_method.getString("title"));
            shippingMethod.setPrice(shipping_method.getString("price"));
            //shippingMethod.setCurrency(shipping_method.getString("currency"));
            shippingMethod.setDescription(shipping_method.getString("description"));
            shippingMethod.setSm_code(shipping_method.getString("sm_code"));
            shippingMethod.setSm_id(shipping_method.getString("sm_id"));
            item.setShipping_method(shippingMethod);

            List<String> msgs = new ArrayList<>();
            if(orderJ.get("messages") instanceof JSONObject){
                for(Iterator iterator = orderJ.getJSONObject("messages").keys(); iterator.hasNext();) {
                    String key = (String) iterator.next();
                    msgs.add(orderJ.getJSONObject("messages").getJSONObject(key).getString("title") + " - " + orderJ.getJSONObject("messages").getJSONObject(key).getString("message"));
                }
            }
            item.setMessages(msgs);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return item;
    }
}
