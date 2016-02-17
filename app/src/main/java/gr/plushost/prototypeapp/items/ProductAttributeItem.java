package gr.plushost.prototypeapp.items;

import java.util.List;

/**
 * Created by billiout on 8/3/2015.
 */
public class ProductAttributeItem {
    private int attribute_id;
    private String title;
    private String custom_name;
    private String custom_text;
    private String input;
    private boolean required;
    private String regex;
    private String inner_hint;
    private String incorrect_message;

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getInner_hint() {
        return inner_hint;
    }

    public void setInner_hint(String inner_hint) {
        this.inner_hint = inner_hint;
    }

    public String getIncorrect_message() {
        return incorrect_message;
    }

    public void setIncorrect_message(String incorrect_message) {
        this.incorrect_message = incorrect_message;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    private List<ProductAttributeOptionItem> options;

    public int getAttribute_id() {
        return attribute_id;
    }

    public void setAttribute_id(int attribute_id) {
        this.attribute_id = attribute_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCustom_name() {
        return custom_name;
    }

    public void setCustom_name(String custom_name) {
        this.custom_name = custom_name;
    }

    public String getCustom_text() {
        return custom_text;
    }

    public void setCustom_text(String custom_text) {
        this.custom_text = custom_text;
    }

    public List<ProductAttributeOptionItem> getOptions() {
        return options;
    }

    public void setOptions(List<ProductAttributeOptionItem> options) {
        this.options = options;
    }
}
