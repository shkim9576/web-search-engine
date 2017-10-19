package model;

/**
 * Created by skim on 2/1/17.
 */
public class All {

    public enum STATUS {
        OK, N_OK;
    }

    private String url;
    private STATUS status;

    public All(String url, STATUS status) {
        if (url != null) url = url.replaceAll(",", "-");
        this.url = url;
        this.status = status;
    }

    public STATUS getStatus() {
        return status;
    }

    public String getUrl() {
        return url;
    }

}
