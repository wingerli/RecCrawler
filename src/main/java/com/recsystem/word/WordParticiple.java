package com.recsystem.word;

import java.util.List;


import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.FilterRecognition;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.log4j.Logger;


/**
 * Created by lee on 2016/11/24.
 */
public class WordParticiple {
    static Logger log = Logger.getLogger(WordParticiple.class);

    public static String parse(String input){
        long start = System.currentTimeMillis();
        Result terms = ToAnalysis.parse(input);
        String output = terms.toStringWithOutNature(" ");
        output = output.replaceAll("[\\pP‘’“”]", "");
        long end = System.currentTimeMillis();
        log.debug(end - start);

        return output;
    }

    public static void main(String[] args){
        long start = System.currentTimeMillis();
        FilterRecognition filter = new FilterRecognition();
        filter.insertStopRegex("[\\\\pP‘’“”]"); //支持正则表达式
        String str = "我觉得Ansj中文分词123是一个不错的系统2345!我是王婆!";
        Result terms = ToAnalysis.parse(str).recognition(filter);
        long end = System.currentTimeMillis();
        log.debug(end - start);
        String result = terms.toStringWithOutNature(" ");
        log.debug(result.replaceAll("[\\pP‘’“”]", ""));

    }
}
