package gr.plushost.prototypeapp.items;

import java.util.List;

/**
 * Created by billiout on 12/3/2015.
 */
public class CartProductItem {
    private String cart_item_id;
    private int item_id;
    private String item_title;
    private String item_display_id;
    private String thumbnail_pic_url;
    private String currency;
    private String item_price;
    private String subtotal_price;
    private int qty;

    public String getItem_display_id() {
        return item_display_id;
    }

    public void setItem_display_id(String item_display_id) {
        this.item_display_id = item_display_id;
    }

    public String getSubtotal_price() {
        return subtotal_price;
    }

    public void setSubtotal_price(String subtotal_price) {
        this.subtotal_price = subtotal_price;
    }

    private List<String> display_attributes;

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getCart_item_id() {
        return cart_item_id;
    }

    public void setCart_item_id(String cart_item_id) {
        this.cart_item_id = cart_item_id;
    }

    public String getItem_title() {
        return item_title;
    }

    public void setItem_title(String item_title) {
        this.item_title = item_title;
    }

    public String getThumbnail_pic_url() {
        return thumbnail_pic_url;
    }

    public void setThumbnail_pic_url(String thumbnail_pic_url) {
        this.thumbnail_pic_url = thumbnail_pic_url;
    }

    public String getItem_price() {
        return item_price;
    }

    public void setItem_price(String item_price) {
        this.item_price = item_price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public List<String> getDisplay_attributes() {
        return display_attributes;
    }

    public void setDisplay_attributes(List<String> display_attributes) {
        this.display_attributes = display_attributes;
    }
}
