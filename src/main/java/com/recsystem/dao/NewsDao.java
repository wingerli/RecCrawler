package com.recsystem.dao;

import com.recsystem.mongo.MongodbDao;
import org.springframework.stereotype.Repository;

/**
 * Created by lee on 2016/11/21.
 */
@Repository
public class NewsDao extends MongodbDao<News> {
}
