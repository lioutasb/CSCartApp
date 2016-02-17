package gr.plushost.prototypeapp.items;

import java.util.List;

/**
 * Created by billiout on 23/2/2015.
 */
public class ProductItem {
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
    private double rating_score;
    private String stuff_status;
    private boolean virtual_flag;
    private String currency;
    private String initial_price;
    private long discount;
    private List<ProductAttributeItem> attributes;
    private String out_of_stock_actions;
    private String promo_text;
    private List<ProductTierPriceItem> tier_prices;
    private String short_description;
    private String detail_description;
    private List<String> item_imgs;
    private String avail_since;
    private List<ProductSpecificationItem> specifications;
    private String discussion_type;
    private List<MiniProductItem> related_items;
    private String categoriesNames = "";
    private String free_shipping = "";

    public String getFree_shipping() {
        return free_shipping;
    }

    public void setFree_shipping(String free_shipping) {
        this.free_shipping = free_shipping;
    }

    public String getCategoriesNames() {
        return categoriesNames;
    }

    public void setCategoriesNames(String categoriesNames) {
        this.categoriesNames = categoriesNames;
    }

    public List<MiniProductItem> getRelated_items() {
        return related_items;
    }

    public void setRelated_items(List<MiniProductItem> related_items) {
        this.related_items = related_items;
    }

    public String getDiscussion_type() {
        return discussion_type;
    }

    public void setDiscussion_type(String discussion_type) {
        this.discussion_type = discussion_type;
    }

    public String getAvail_since() {
        return avail_since;
    }

    public void setAvail_since(String avail_since) {
        this.avail_since = avail_since;
    }

    public List<ProductAttributeItem> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ProductAttributeItem> attributes) {
        this.attributes = attributes;
    }

    public String getOut_of_stock_actions() {
        return out_of_stock_actions;
    }

    public void setOut_of_stock_actions(String out_of_stock_actions) {
        this.out_of_stock_actions = out_of_stock_actions;
    }

    public String getPromo_text() {
        return promo_text;
    }

    public void setPromo_text(String promo_text) {
        this.promo_text = promo_text;
    }

    public List<ProductTierPriceItem> getTier_prices() {
        return tier_prices;
    }

    public void setTier_prices(List<ProductTierPriceItem> tier_prices) {
        this.tier_prices = tier_prices;
    }

    public String getDetail_description() {
        return detail_description;
    }

    public void setDetail_description(String detail_description) {
        this.detail_description = detail_description;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public List<ProductSpecificationItem> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<ProductSpecificationItem> specifications) {
        this.specifications = specifications;
    }

    public List<String> getItem_imgs() {
        return item_imgs;
    }

    public void setItem_imgs(List<String> item_imgs) {
        this.item_imgs = item_imgs;
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

    public double getRating_score() {
        return rating_score;
    }

    public void setRating_score(double rating_score) {
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
