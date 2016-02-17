package gr.plushost.prototypeapp.items;

/**
 * Created by user on 5/8/2015.
 */
public class FreeShippingPromotionItem {
    private boolean has_free_ship = false;
    private String promotion_id = "";
    private String zone = "";
    private String conditions_hash = "";
    private String name = "";
    private String value = "";

    public boolean isHas_free_ship() {
        return has_free_ship;
    }

    public void setHas_free_ship(boolean has_free_ship) {
        this.has_free_ship = has_free_ship;
    }

    public String getPromotion_id() {
        return promotion_id;
    }

    public void setPromotion_id(String promotion_id) {
        this.promotion_id = promotion_id;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getConditions_hash() {
        return conditions_hash;
    }

    public void setConditions_hash(String conditions_hash) {
        this.conditions_hash = conditions_hash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
