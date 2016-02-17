package gr.plushost.prototypeapp.parsers;

import org.json.JSONObject;

import gr.plushost.prototypeapp.items.UserAddressItem;

/**
 * Created by billiout on 7/3/2015.
 */
public class AddressParser {

    public UserAddressItem parse(JSONObject response){
        UserAddressItem item = new UserAddressItem();

        try{
            item.setAddress_book_id(response.getInt("address_book_id"));
            item.setFirstname(response.getString("firstname"));
            item.setLastname(response.getString("lastname"));
            item.setAddress1(response.getString("address1"));
            item.setAddress2(response.getString("address2"));
            item.setCity(response.getString("city"));
            item.setCountry_id(response.getString("country_id"));
            item.setGender(response.getString("gender"));
            item.setPostcode(response.getString("postcode"));
            if(response.has("state"))
                item.setState(response.getString("state"));

            if(response.has("zone_id")) {
                item.setZone_id(!response.getString("zone_id").equals("") ? response.getInt("zone_id") : 0 );
            }
            item.setTelephone(response.getString("telephone"));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return item;
    }
}
