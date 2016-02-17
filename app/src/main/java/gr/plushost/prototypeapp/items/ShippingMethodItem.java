package gr.plushost.prototypeapp.items;

/**
 * Created by billiout on 25/3/2015.
 */
public class ShippingMethodItem {
    private String currency;
    private String description;
    private String price;
    private String sm_code;
    private String sm_id;
    private String title;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSm_code() {
        return sm_code;
    }

    public void setSm_code(String sm_code) {
        this.sm_code = sm_code;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSm_id() {
        return sm_id;
    }

    public void setSm_id(String sm_id) {
        this.sm_id = sm_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
