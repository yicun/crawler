package me.chyc.crawler;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by yicun.chen on 7/1/15.
 */
public class LinkedinCrawler {
    public static void main(String args[]) throws Exception {
        String url = "http://www.linkedin.com/profile/view?id=2466569";
        String encode = "GBK";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpget);
        StringBuilder sb = new StringBuilder();
        HttpEntity httpEntity = response.getEntity();
        if (httpEntity != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(httpEntity.getContent(), encode));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str + "\n");
            }
            br.close();
        }
        response.close();
        httpClient.close();
        System.out.println(sb.toString());
    }
}
