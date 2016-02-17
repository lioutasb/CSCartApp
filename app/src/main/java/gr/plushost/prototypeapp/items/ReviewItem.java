package gr.plushost.prototypeapp.items;

/**
 * Created by billiout on 9/3/2015.
 */
public class ReviewItem {
    private String uname;
    private int item_id;
    private int rate_score;
    private String rate_content;
    private String rate_date;

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getRate_score() {
        return rate_score;
    }

    public void setRate_score(int rate_score) {
        this.rate_score = rate_score;
    }

    public String getRate_content() {
        return rate_content;
    }

    public void setRate_content(String rate_content) {
        this.rate_content = rate_content;
    }

    public String getRate_date() {
        return rate_date;
    }

    public void setRate_date(String rate_date) {
        this.rate_date = rate_date;
    }
}
