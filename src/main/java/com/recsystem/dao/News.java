package com.recsystem.dao;

import com.recsystem.mongo.IdEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Created by lee on 2016/11/21.
 */

@Document(collection="news")
public class News extends IdEntity implements Serializable {
    private String title;
    private String time;
    private String originalhtml;
    private String content;
    private String optime;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOriginalhtml() {
        return originalhtml;
    }

    public void setOriginalhtml(String originalhtml) {
        this.originalhtml = originalhtml;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOptime() {
        return optime;
    }

    public void setOptime(String optime) {
        this.optime = optime;
    }
}
