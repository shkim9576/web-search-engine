package model;

/**
 * Created by skim on 2/1/17.
 */
public class Fetch {

    private String url;
    private int status;

    public Fetch(String url, int status) {
        if (url != null) url = url.replaceAll(",", "-");
        this.url = url;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getUrl() {
        return url;
    }

}
