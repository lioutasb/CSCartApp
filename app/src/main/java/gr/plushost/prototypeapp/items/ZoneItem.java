package gr.plushost.prototypeapp.items;

import java.io.Serializable;

/**
 * Created by billiout on 7/3/2015.
 */
public class ZoneItem implements Serializable {
    private int zone_id;
    private String country_id;
    private String zone_name;
    private String zone_code;

    public int getZone_id() {
        return zone_id;
    }

    public void setZone_id(int zone_id) {
        this.zone_id = zone_id;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getZone_name() {
        return zone_name;
    }

    public void setZone_name(String zone_name) {
        this.zone_name = zone_name;
    }

    public String getZone_code() {
        return zone_code;
    }

    public void setZone_code(String zone_code) {
        this.zone_code = zone_code;
    }
}
