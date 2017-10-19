package model;

import org.apache.tika.Tika;

import java.io.IOException;
import java.net.URL;

/**
 * Created by skim on 2/1/17.
 */
public class Visit {

    private String url;
    private int fileSize;
    private int outLinkCount;
    private String contentType;
    Tika tika = new Tika();

    public Visit(String url, int fileSize, int outLinkCount, String contentType) {
        if (url != null) url = url.replaceAll(",", "-");
        this.url = url;
        this.fileSize = fileSize;
        this.outLinkCount = outLinkCount;
        try {
            this.contentType = (contentType == null || contentType.isEmpty()) ? tika.detect(new URL(url)) : contentType;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    public int getFileSize() {
        return fileSize;
    }

    public int getOutLinkCount() {
        return outLinkCount;
    }

    public String getContentType() {
        return contentType;
    }

}
