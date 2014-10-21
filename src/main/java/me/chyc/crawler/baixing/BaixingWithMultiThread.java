package me.chyc.crawler.baixing;

import me.chyc.http.CrawlerProxyGetter;
import me.chyc.http.ProxyInfo;
import me.chyc.http.WebPageGetter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chyc on 7/28/14.
 */
public class BaixingWithMultiThread {
    private List<String> novelList;
    private List<ProxyInfo> proxyList;
    private List<HireInfo> hireList;
    private String outDir;
    private boolean close;

    public BaixingWithMultiThread(String proxyURL, String outDir) throws Exception {
        this.outDir = outDir;
        this.proxyList = CrawlerProxyGetter.Getter2(proxyURL);
        System.out.println("ProxyInfo List:\t" + proxyList.size());
    }


    public void start() {
        String url = "http://beijing.baixing.com/chushi/";
        ListCrawler listCrawler = new ListCrawler(0, url);
        listCrawler.run();

        for (int i = 0; i < 10; i++)
            (new PageCrawler(i)).run();
    }

    synchronized ProxyInfo getProxy() {
        if (proxyList == null || proxyList.size() == 0)
            return null;
        else
            return proxyList.remove(0);
    }

    synchronized void insertProxy(ProxyInfo proxyInfo) {
        if (proxyList == null)
            proxyList = new ArrayList<ProxyInfo>();
        proxyList.add(proxyInfo);
    }

    synchronized HireInfo getHire() {
        if (hireList == null || hireList.size() == 0)
            return null;
        else
            return hireList.remove(0);
    }

    synchronized void insertHire(HireInfo hireInfo) {
        if (hireList == null)
            hireList = new ArrayList<HireInfo>();
        hireList.add(hireInfo);
        System.out.println("HireInfo List: " + hireList.size());
    }


    class PageCrawler extends Thread {
        private int no;

        PageCrawler(int no) {
            this.no = no;
            System.out.println("PageCrawler " + no + ":\trun");
        }

        public String getPhoneNum(String url, ProxyInfo proxyInfo) throws Exception {
            String html = WebPageGetter.getWebPagewithProxy(url, proxyInfo.getHost(), proxyInfo.getPort(), "utf-8");
            Document document = Jsoup.parse(html);
            Element el_num = document.getElementsByAttributeValue("id", "num").first();
            Element el_num2 = document.getElementsByAttributeValue("class", "show-contact button button-grey").first();
            try {
                return el_num.text().replace("*", "") + el_num2.attr("data-contact");
            } catch (NullPointerException e) {
                return null;
            }
        }

        public void run() {
            while (!close && hireList.size() == 0) {
                ProxyInfo proxyInfo = getProxy();
                while (proxyInfo == null) {
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    proxyInfo = getProxy();
                }
                HireInfo hireInfo = getHire();
                while (hireInfo == null) {
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    hireInfo = getHire();
                }
                try {
                    String phonenum = getPhoneNum(hireInfo.getHref(), proxyInfo);
                    if (phonenum == null)
                        insertHire(hireInfo);
                    else {
                        hireInfo.setPhone(phonenum);
                        System.out.println(no + "\t" + hireInfo.toPhoneCompany());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ListCrawler extends Thread {
        private int no;
        private String url;

        ListCrawler(int no, String url) {
            this.no = no;
            this.url = url;
            System.out.println("ListCrawler " + no + ":\trun");
        }

        @Override
        public void run() {
            for (int i = 1; i <= 50; i++) {
                System.out.println("ListCrawler " + no + ":\tstart");

                String list_url = url + "?page=" + i;
                System.out.println("ListCrawler " + no + ":\t" + list_url);

                ProxyInfo proxyInfo = getProxy();
                while (proxyInfo == null) {
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    proxyInfo = getProxy();
                }
                System.out.println("ListCrawler " + no + ":\t" + proxyInfo.toString());

                String html = null;
                try {
                    html = WebPageGetter.getWebPagewithProxy(url, proxyInfo.getHost(), proxyInfo.getPort(), "utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Document document = Jsoup.parse(html);
                Elements el_list = document.getElementsByAttributeValue("class", "table-view-item clearfix  item-pinned");
                for (Element el : el_list) {
                    Element el_a = el.select("a[href]").first();
                    String href = el_a.attr("href");
                    Element el_c = el.getElementsByAttributeValue("class", "table-view-block").first();
                    String shopname = el_c.text();
                    HireInfo hireInfo = new HireInfo(href, shopname);
                    insertHire(hireInfo);
                }
                Elements el_list2 = document.getElementsByAttributeValue("class", "table-view-item clearfix  item-regular");
                for (Element el : el_list2) {
                    Element el_a = el.select("a[href]").first();
                    String href = el_a.attr("href");
                    Element el_c = el.getElementsByAttributeValue("class", "table-view-block").first();
                    String shopname = el_c.text();
                    HireInfo hireInfo = new HireInfo(href, shopname);
                    insertHire(hireInfo);
                }
            }
            close = false;
        }
    }


    public static void main(String args[]) throws Exception {

        BaixingWithMultiThread com58WithMultiThread = new BaixingWithMultiThread("http://pachong.org/", "./");
        com58WithMultiThread.start();
    }
}

