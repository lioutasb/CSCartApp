package gr.plushost.prototypeapp.items;

import java.io.Serializable;

/**
 * Created by billiout on 7/3/2015.
 */
public class CountryItem implements Serializable {
    private String country_id;
    private String country_name;
    private String country_iso_code_2;
    private String country_iso_code_3;

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getCountry_iso_code_2() {
        return country_iso_code_2;
    }

    public void setCountry_iso_code_2(String country_iso_code_2) {
        this.country_iso_code_2 = country_iso_code_2;
    }

    public String getCountry_iso_code_3() {
        return country_iso_code_3;
    }

    public void setCountry_iso_code_3(String country_iso_code_3) {
        this.country_iso_code_3 = country_iso_code_3;
    }
}
