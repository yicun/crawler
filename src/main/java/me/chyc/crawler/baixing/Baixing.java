package me.chyc.crawler.baixing;

import me.chyc.http.WebPageGetter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


/**
 * Created by yicun.chen on 10/20/14.
 */
public class Baixing {

    public static String getPhoneNum(String url) throws Exception {
        String html = WebPageGetter.getWebPage(url, "utf-8");
        Document document = Jsoup.parse(html);
        Element el_num = document.getElementsByAttributeValue("id", "num").first();
        Element el_num2 = document.getElementsByAttributeValue("class", "show-contact button button-grey").first();
        try {
            return el_num.text().replace("*", "") + el_num2.attr("data-contact");
        } catch (NullPointerException e) {
            return "";
        }
    }

    public static void main(String args[]) throws Exception {
        if (args == null || args.length != 2) {
            System.err.println("Usage: city outfile");
            System.exit(1);
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1])));
        for (int i = 0; i < 50; i++) {
            String list_url = "http://" + args[0] + ".baixing.com/chushi/?page=" + i;
            System.out.print(list_url);
            int index = 0;
            String html = WebPageGetter.getWebPage(list_url, "utf-8");
            Document document = Jsoup.parse(html);
            Elements el_list = document.getElementsByAttributeValue("class", "table-view-item clearfix  item-pinned");
            for (Element el : el_list) {
                Element el_a = el.select("a[href]").first();
                String href = el_a.attr("href");
                String phonenum = getPhoneNum(href);

                Element el_c = el.getElementsByAttributeValue("class", "table-view-block").first();
                String shopname = el_c.text();
//                System.out.println(phonenum + "\t" + shopname);
                bw.write(phonenum + "\t" + shopname);
                index++;
                bw.newLine();
                bw.flush();
            }
            Elements el_list2 = document.getElementsByAttributeValue("class", "table-view-item clearfix  item-regular");
            for (Element el : el_list2) {
                Element el_a = el.select("a[href]").first();
                String href = el_a.attr("href");
                String phonenum = getPhoneNum(href);
                Element el_c = el.getElementsByAttributeValue("class", "table-view-block").first();
                String shopname = el_c.text();
//                System.out.println(phonenum + "\t" + shopname);
                bw.write(phonenum + "\t" + shopname);
                bw.newLine();
                bw.flush();
                index++;
            }
            System.out.println(" ...... " + index);
        }
        bw.close();
    }
}
