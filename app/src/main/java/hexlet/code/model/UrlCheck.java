package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class UrlCheck {
    private long id;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private long urlId;
    private Timestamp createdAt;

    public UrlCheck(int code, String pageTitle, String pageH1, String pageDescription, long pageUrlId) {
        this.statusCode = code;
        this.title = pageTitle;
        this.h1 = pageH1;
        this.description = pageDescription;
        this.urlId = pageUrlId;
    }
}
