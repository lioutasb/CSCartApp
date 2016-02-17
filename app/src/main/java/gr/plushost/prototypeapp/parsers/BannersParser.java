package gr.plushost.prototypeapp.parsers;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import gr.plushost.prototypeapp.items.BannerItem;

/**
 * Created by billiout on 10/3/2015.
 */
public class BannersParser {

    public List<BannerItem> parse(JSONArray response){
        List<BannerItem> list = new ArrayList<>();

        try {
            for (int i = 0; i < response.length(); i++) {
                BannerItem item = new BannerItem();
                item.setTitle(response.getJSONObject(i).getString("title"));
                item.setUrl(response.getJSONObject(i).getString("url"));
                item.setImg_url(response.getJSONObject(i).getString("img_url"));
                list.add(item);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }
}
