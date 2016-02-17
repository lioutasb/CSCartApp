package gr.plushost.prototypeapp.items;

import java.io.Serializable;

/**
 * Created by billiout on 11/3/2015.
 */
public class StoreCurrencyItem implements Serializable {
    private String currency_code;
    private boolean is_default;
    private String currency_symbol;
    private String decimal_symbol;
    private String group_symbol;
    private int decimal_places;
    private String description;
    private boolean currency_symbol_right;

    public boolean isCurrency_symbol_right() {
        return currency_symbol_right;
    }

    public void setCurrency_symbol_right(boolean currency_symbol_right) {
        this.currency_symbol_right = currency_symbol_right;
    }

    public String getCurrency_symbol() {
        return currency_symbol;
    }

    public void setCurrency_symbol(String currency_symbol) {
        this.currency_symbol = currency_symbol;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getDecimal_symbol() {
        return decimal_symbol;
    }

    public void setDecimal_symbol(String decimal_symbol) {
        this.decimal_symbol = decimal_symbol;
    }

    public int getDecimal_places() {
        return decimal_places;
    }

    public void setDecimal_places(int decimal_places) {
        this.decimal_places = decimal_places;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroup_symbol() {
        return group_symbol;
    }

    public void setGroup_symbol(String group_symbol) {
        this.group_symbol = group_symbol;
    }
}
