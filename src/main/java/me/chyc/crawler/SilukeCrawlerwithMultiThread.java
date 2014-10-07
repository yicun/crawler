package me.chyc.crawler;

import me.chyc.utils.CrawlerProxyGetter;
import me.chyc.utils.Pair;

import me.chyc.utils.WebPageGetter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

/**
 * Created by chyc on 7/28/14.
 */
public class SilukeCrawlerwithMultiThread {


    public static void main(String args[]) throws Exception {
        List<Pair<String, Integer>> proxies = CrawlerProxyGetter.Getter("http://pachong.org");
        for (Pair<String,Integer> proxy: proxies){
//            System.out.println(proxy.toString());
        }

        String html= WebPageGetter.getWebPagewithRandomProxy("http://pachong.org");
        System.out.println(html);
    }
}
