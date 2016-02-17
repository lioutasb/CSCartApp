package gr.plushost.prototypeapp.items;

import java.util.List;

/**
 * Created by billiout on 6/3/2015.
 */
public class MiniProductItem {
    private int item_id;
    private String display_id;
    private List<Integer> cids;
    private String item_title;
    private String thumbnail_pic_url;
    private Boolean post_free;
    private double shipping_fee;
    private String price;
    private String item_url;
    private Boolean allow_add_to_cart;
    private int qty;
    private String item_status;
    private int rating_count;
    private int rating_score;
    private String stuff_status;
    private boolean virtual_flag;
    private String currency;
    private String initial_price;
    private long discount;
    private List<ProductSpecificationItem> features;
    private String short_desc = "";
    private String long_desc = "";
    private int attributes_length = 0;
    private String free_shipping = "";

    public String getFree_shipping() {
        return free_shipping;
    }

    public void setFree_shipping(String free_shipping) {
        this.free_shipping = free_shipping;
    }

    public int getAttributes_length() {
        return attributes_length;
    }

    public void setAttributes_length(int attributes_length) {
        this.attributes_length = attributes_length;
    }

    public List<ProductSpecificationItem> getFeatures() {
        return features;
    }

    public void setFeatures(List<ProductSpecificationItem> features) {
        this.features = features;
    }

    public String getLong_desc() {
        return long_desc;
    }

    public void setLong_desc(String long_desc) {
        this.long_desc = long_desc;
    }

    public String getShort_desc() {
        return short_desc;
    }

    public void setShort_desc(String short_desc) {
        this.short_desc = short_desc;
    }

    public String getInitial_price() {
        return initial_price;
    }

    public void setInitial_price(String initial_price) {
        this.initial_price = initial_price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isVirtual_flag() {
        return virtual_flag;
    }

    public void setVirtual_flag(boolean virtual_flag) {
        this.virtual_flag = virtual_flag;
    }

    public String getStuff_status() {
        return stuff_status;
    }

    public void setStuff_status(String stuff_status) {
        this.stuff_status = stuff_status;
    }

    public int getRating_score() {
        return rating_score;
    }

    public void setRating_score(int rating_score) {
        this.rating_score = rating_score;
    }

    public int getRating_count() {
        return rating_count;
    }

    public void setRating_count(int rating_count) {
        this.rating_count = rating_count;
    }

    public String getItem_status() {
        return item_status;
    }

    public void setItem_status(String item_status) {
        this.item_status = item_status;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public Boolean getAllow_add_to_cart() {
        return allow_add_to_cart;
    }

    public void setAllow_add_to_cart(Boolean allow_add_to_cart) {
        this.allow_add_to_cart = allow_add_to_cart;
    }

    public String getItem_url() {
        return item_url;
    }

    public void setItem_url(String item_url) {
        this.item_url = item_url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public double getShipping_fee() {
        return shipping_fee;
    }

    public void setShipping_fee(double shipping_fee) {
        this.shipping_fee = shipping_fee;
    }

    public Boolean getPost_free() {
        return post_free;
    }

    public void setPost_free(Boolean post_free) {
        this.post_free = post_free;
    }

    public String getThumbnail_pic_url() {
        return thumbnail_pic_url;
    }

    public void setThumbnail_pic_url(String thumbnail_pic_url) {
        this.thumbnail_pic_url = thumbnail_pic_url;
    }

    public String getItem_title() {
        return item_title;
    }

    public void setItem_title(String item_title) {
        this.item_title = item_title;
    }

    public List<Integer> getCids() {
        return cids;
    }

    public void setCids(List<Integer> cids) {
        this.cids = cids;
    }

    public String getDisplay_id() {
        return display_id;
    }

    public void setDisplay_id(String display_id) {
        this.display_id = display_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public long getDiscount() {
        return discount;
    }

    public void setDiscount(long discount) {
        this.discount = discount;
    }
}
