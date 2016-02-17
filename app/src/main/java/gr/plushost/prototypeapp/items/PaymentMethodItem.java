package gr.plushost.prototypeapp.items;

/**
 * Created by billiout on 25/3/2015.
 */
public class PaymentMethodItem {
    private String pm_id;
    private String pm_description;
    private String pm_img_url;
    private String pm_title;
    private String pm_instructions;

    public String getPm_id() {
        return pm_id;
    }

    public void setPm_id(String pm_id) {
        this.pm_id = pm_id;
    }

    public String getPm_description() {
        return pm_description;
    }

    public void setPm_description(String pm_description) {
        this.pm_description = pm_description;
    }

    public String getPm_img_url() {
        return pm_img_url;
    }

    public void setPm_img_url(String pm_img_url) {
        this.pm_img_url = pm_img_url;
    }

    public String getPm_title() {
        return pm_title;
    }

    public void setPm_title(String pm_title) {
        this.pm_title = pm_title;
    }

    public String getPm_instructions() {
        return pm_instructions;
    }

    public void setPm_instructions(String pm_instructions) {
        this.pm_instructions = pm_instructions;
    }
}
