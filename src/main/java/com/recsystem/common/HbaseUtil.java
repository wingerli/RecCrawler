package com.recsystem.common;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by lee on 2016/11/24.
 */
public class HbaseUtil {
    private static Connection conn;
    private HbaseUtil(){
        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.property.clientPort","2181");
        conf.set("hbase.zookeeper.quorum", "sparkmaster");
        conf.set("hadoop.home.dir","");
        try {
            conn =  ConnectionFactory.createConnection(conf);
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private static HbaseUtil hbaseutil = new HbaseUtil();

    public static HbaseUtil getHbaseutil(){
        return hbaseutil;
    }

    public void writeHbase(String key, String content){
        try {
            Table news = conn.getTable(TableName.valueOf("news"));
            Put put = new Put(key.getBytes());
            put.addColumn("content".getBytes(),"words".getBytes(),URLEncoder.encode(content, "UTF-8").getBytes());
            news.put(put);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public String readHbase(String rowkey, String family, String qulifer){
        String rtn = "";
        try {
            Table news = conn.getTable(TableName.valueOf("news"));
            Get get = new Get(rowkey.getBytes());
            Result r = news.get(get);
            rtn = Bytes.toString(r.getValue(family.getBytes(), qulifer.getBytes()));
            rtn = URLDecoder.decode(rtn,"UTF-8");
        }catch(IOException e){
            e.printStackTrace();
        }
        return rtn;
    }

    public static void main(String[] args) throws IOException {
        HbaseUtil hbase = HbaseUtil.getHbaseutil();
        //hbase.writeHbase("aaaaaaacc", "我是 中国 人民");
        String str = hbase.readHbase("5836c04c2ad47b31188feb51","content","words");
        System.out.println(str);
    }

}
