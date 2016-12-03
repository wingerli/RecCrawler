package com.recsystem.crawler;

import com.hisign.kafka.KafkaManager;
import com.recsystem.common.HbaseUtil;
import com.recsystem.dao.News;
import com.recsystem.dao.NewsDao;
import com.recsystem.word.WordParticiple;
import org.springframework.context.support.GenericXmlApplicationContext;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lee on 2016/11/21.
 */
public class CrawlerLoL implements PageProcessor {

    private static NewsDao newsdao;
    private static HbaseUtil hbase = HbaseUtil.getHbaseutil();
    private static String kafka_broker = "sparkmaster.cloudera:9092,sparkslave1.cloudera:9093,sparkslave2.cloudera:9094";
    private static KafkaManager kafka = KafkaManager.getInstance("sparkmaster.cloudera:9092,sparkslave1.cloudera:9092,sparkslave2.cloudera:9092");

    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Site site = Site.me().setTimeOut(5000).setSleepTime(1000);

    public Site getSite() {
        return site;
    }

    public void run(){
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.setValidating(false);
        context.load("classpath*:applicationContext*.xml");
        context.refresh();
        newsdao = (NewsDao)context.getBean("newsdao");
        Spider.create(new CrawlerLoL()).addUrl("http://lol.17173.com/list/20140422.shtml")
                .addPipeline(new ConsolePipeline()).run();
    }

    public void process(Page page) {
        //http://lol.17173.com/news/
        if(page.getUrl().regex("http://lol\\.17173\\.com/list/\\d+\\.shtml").match()
                || page.getUrl().regex("http://lol\\.17173\\.com/list/\\d+\\_\\d+\\.shtml").match()) {
            List<String> links = page.getHtml().xpath("//ul[@class='comm-list art-list-txt js-list1']").links().regex("http://lol\\.17173\\.com/news/\\d+/\\d+\\.shtml").all();

            page.addTargetRequests(links);
            List<String> listlinks = page.getHtml().xpath("//div[@class='pagination']").links().all();
            page.addTargetRequests(listlinks);
        }else{
            String originalhtml = page.getHtml().xpath("div[@class='gb-final-mod-article']").toString();
            String content = page.getHtml().xpath("div[@class='gb-final-mod-article']/p/text()").all().toString().replace(",", "").replace(" ","").replace("[","").replace("]","");
            String title = page.getHtml().xpath("h1[@class='gb-final-tit-article']/text()").toString();
            String time = page.getHtml().xpath("div[@class='gb-final-mod-info']/span[@class='gb-final-date']/text()").toString().replace("时间：","");
            //System.out.println(content);
            News news = new News();
            news.setOriginalhtml(originalhtml);

            news.setContent(content);
            news.setTitle(title);
            news.setTime(time);
            news.setOptime(sf.format(new Date()));
            news.setType("lol");
            newsdao.save(news);

            String id = news.getId();
            String split_content = WordParticiple.parse(content);
            hbase.writeHbase(id, split_content);

            kafka.send("rec","lol",id);
        }
    }


    public static void main(String[] args){
        KafkaManager kafka = KafkaManager.getInstance("sparkmaster.cloudera:9092,sparkslave1.cloudera:9093,sparkslave2.cloudera:9094");
        kafka.send("rec","lol","1234567891dsafasdf");
    }
}
