package com.recsystem.mongo;

import java.io.Serializable;

/**
 * mongodb 基础id类，id都是字符串型的
 * @author chenpengye
 * 2015年12月21日 下午4:53:45
 */
public class IdEntity implements Serializable {

    private static final long serialVersionUID = 33633625616087044L;
    private String id;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

}