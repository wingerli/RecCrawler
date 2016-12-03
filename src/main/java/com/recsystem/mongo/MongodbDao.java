package com.recsystem.mongo;

import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * mongodb 基础操作类
 * @author chenpengye 2015年12月21日 下午11:33:06
 * @param <T>
 */
@Repository
public class MongodbDao<T extends IdEntity> {

    private static final Logger logger = LoggerFactory.getLogger(MongodbDao.class);

    private static final int DEFAULT_SKIP = 0;
    private static final int DEFAULT_LIMIT = 200;

    @Autowired
    protected MongoTemplate mongoTemplate;

    // ＝＝＝＝＝＝＝ 增 ＝＝＝＝＝＝＝＝＝＝＝＝
    public void save(T t) {
        mongoTemplate.save(t);
        logger.debug("save entity: {}", t);
    }

    public void insertAll(List<T> list) {
        mongoTemplate.insertAll(list);
    }

    // ＝＝＝＝＝＝＝ 删 ＝＝＝＝＝＝＝＝＝＝＝＝
    /**
     * 删除对象
     *
     * @param t
     */
    public void delete(T t) {
        mongoTemplate.remove(t);
    }

    /**
     * 根据id 删除对象
     *
     * @param id
     */
    public void deleteById(String id) {
        Criteria criteria = Criteria.where("id").is(id);
        Query query = new Query(criteria);
        mongoTemplate.remove(query, this.getEntityClass());
    }

    /**
     * 根据条件删除
     */
    public void delete(Query query) {
        mongoTemplate.remove(query, this.getEntityClass());
    }

    /**
     * 删除该collection 的所有的数据
     */
    public void deleteAll() {
        mongoTemplate.dropCollection(this.getEntityClass());
    }

    // ＝＝＝＝＝＝＝ 改 ＝＝＝＝＝＝＝＝＝＝＝＝
    public void update(Query query, Update update) {
        mongoTemplate.findAndModify(query, update, this.getEntityClass());
    }

    // ＝＝＝＝＝＝＝ 查 ＝＝＝＝＝＝＝＝＝＝＝＝
    public List<T> findAll(){
        return mongoTemplate.findAll(this.getEntityClass());
    }
    /**
     * 根据查询query 查找list
     *
     * @param query
     * @return
     */
    public List<T> find(Query query) {
        return mongoTemplate.find(query, this.getEntityClass());
    }

    /**
     * 按照字段排序 － 顺序  <br/>
     * @param query        查询条件  <br/>
     * @param properties   排序字段  <br/>
     * @return
     */
    public List<T> findWithOrderAsc(Query query, String... properties){
        Sort sort = new Sort(Direction.ASC, properties);
        query.with(sort);
        return mongoTemplate.find(query, this.getEntityClass());
    }


    /**
     * 按照字段排序 － 逆序 <br/>
     * @param query        查询条件  <br/>
     * @param properties   排序字段  <br/>
     * @return
     */
    public List<T> findWithOrderDesc(Query query, String... properties){
        Sort sort = new Sort(Direction.DESC, properties);
        query.with(sort);
        return mongoTemplate.find(query, this.getEntityClass());
    }


    /**
     * 根据查询query 查找一个对象
     *
     * @param query
     * @return
     */
    public T findOne(Query query) {
        return mongoTemplate.findOne(query, this.getEntityClass());
    }

    /**
     * 根据 id 查询对象
     *
     * @param id
     * @return
     */
    public T findById(String id) {
        return mongoTemplate.findById(id, this.getEntityClass());
    }

    /**
     * 根据id 和 集合名字查询对象
     *
     * @param id
     * @param collectionName
     * @return
     */
    public T findById(String id, String collectionName) {
        return mongoTemplate.findById(id, this.getEntityClass(), collectionName);
    }

    /**
     * 查询分页  tips：［不要skip 太多的页数，如果跳过太多会严重影响效率。最大不要skip 20000页］
     * @param page
     * @param query
     * @return
     */
    public PageInfo<T> findPage(PageInfo<T> page, Query query) {
        long count = this.count(query);
        page.setTotal(count);
        int pageNumber = page.getPageNum();
        int pageSize = page.getPageSize();
        query.skip((pageNumber - 1) * pageSize).limit(pageSize);
        List<T> list = this.find(query);
        page.setList(list);
        return page;
    }

    public long count(Query query) {
        return mongoTemplate.count(query, this.getEntityClass());
    }

    /**
     * 获取需要操作的实体类class <br/>
     * 例如: StudentScoreDao extends MongodbDao <b>&lt;StudentScore&gt;</b> <br/>
     * 返回的是 <b>StudentScore</b> 的Class
     *
     * @return
     */
    private Class<T> getEntityClass() {
        return ReflectionUtils.getSuperClassGenricType(getClass());
    }

    /**
     * 获取collection的名字，默认是dao范型T的名字 <br/>
     * 例如: StudentScoreDao extends MongodbDao <b>&lt;StudentScore&gt;</b> <br/>
     * 则返回的名字是：<b>StudentScore</b>
     *
     * @return
     */
    private String getCollectionName() {
        return getEntityClass().getSimpleName();
    }

}