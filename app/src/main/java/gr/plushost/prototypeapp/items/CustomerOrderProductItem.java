package gr.plushost.prototypeapp.items;

/**
 * Created by billiout on 25/3/2015.
 */
public class CustomerOrderProductItem {
    private String display_attributes;
    private String final_price;
    private String item_id;
    private String item_title;
    private String order_item_id;
    private String item_display_id;
    private String price;
    private String qty;
    private String thumbnail_pic_url;
    private String virtual_flag;

    public String getItem_display_id() {
        return item_display_id;
    }

    public void setItem_display_id(String item_display_id) {
        this.item_display_id = item_display_id;
    }

    public String getDisplay_attributes() {
        return display_attributes;
    }

    public void setDisplay_attributes(String display_attributes) {
        this.display_attributes = display_attributes;
    }

    public String getFinal_price() {
        return final_price;
    }

    public void setFinal_price(String final_price) {
        this.final_price = final_price;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_title() {
        return item_title;
    }

    public void setItem_title(String item_title) {
        this.item_title = item_title;
    }

    public String getOrder_item_id() {
        return order_item_id;
    }

    public void setOrder_item_id(String order_item_id) {
        this.order_item_id = order_item_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getThumbnail_pic_url() {
        return thumbnail_pic_url;
    }

    public void setThumbnail_pic_url(String thumbnail_pic_url) {
        this.thumbnail_pic_url = thumbnail_pic_url;
    }

    public String getVirtual_flag() {
        return virtual_flag;
    }

    public void setVirtual_flag(String virtual_flag) {
        this.virtual_flag = virtual_flag;
    }
}
