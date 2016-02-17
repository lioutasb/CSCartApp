package gr.plushost.prototypeapp.items;

import java.io.Serializable;

/**
 * Created by billiout on 11/3/2015.
 */
public class StoreLanguageItem implements Serializable {
    private String language_id;
    private String language_code;
    private String language_name;
    private int position;

    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getLanguage_code() {
        return language_code;
    }

    public void setLanguage_code(String language_code) {
        this.language_code = language_code;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public void setLanguage_name(String language_name) {
        this.language_name = language_name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
