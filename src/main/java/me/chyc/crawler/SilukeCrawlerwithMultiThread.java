package me.chyc.crawler;

import me.chyc.entity.Pair;
import me.chyc.http.CrawlerProxyGetter;
import me.chyc.http.WebPageGetter;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chyc on 7/28/14.
 */
public class SilukeCrawlerWithMultiThread {
    private List<String> novelList;
    private List<Pair<String, Integer>> proxyList;
    private String baseDir;
    private String outDir;

    public SilukeCrawlerWithMultiThread(String baseDir, String novelFileName, String outDir, String proxyURL) throws Exception {
        this.baseDir = baseDir;
        this.outDir = outDir;
        this.novelList = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(baseDir + novelFileName))));
        String line;
        int index = 0;
        while ((line = br.readLine()) != null) {
//            System.out.println("No." + (index++) + "\t" + line);
            JSONObject novel = new JSONObject(line);
            File outfile = new File(baseDir + outDir + novel.getString("id") + ".novel");
            if (!outfile.exists()) {
                novel.put("file", outfile.getAbsolutePath());
                SilukeCrawler.save(novel, new File(novel.getString("file")));
            }
            novelList.add(novel.getString("id"));
        }
        this.proxyList = CrawlerProxyGetter.Getter(proxyURL);
    }

    public void start() {
        if (this.novelList == null || this.novelList.size() == 0 || this.proxyList == null || this.proxyList.size() < 0)
            return;
        System.out.println("Novel List:\t" + this.novelList.size());
        System.out.println("Proxy List:\t" + this.proxyList.size());
        for (int i = 0; i < this.proxyList.size(); i++) {
            Pair<String, Integer> proxy = this.proxyList.get(i);
            (new Crawler(i, proxy)).start();
        }
    }

    synchronized String getNovelID() {
        if (this.novelList == null || this.novelList.size() == 0)
            return null;
        else
            return this.novelList.remove(0);
    }

    class Crawler extends Thread {
        private int no;
        private Pair<String, Integer> proxy;

        Crawler(int no, Pair<String, Integer> proxy) {
            this.no = no;
            this.proxy = proxy;
            System.out.println("Crawler " + no + ":" + this.proxy.toString() + "run");
        }

        @Override
        public void run() {
            String novelID;
            while ((novelID = getNovelID()) != null) {
                System.out.println("No." + no + ":\t" + novelID + "$ " + "start");
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                File novelFile = new File(baseDir + outDir + novelID + ".novel");
                try {
                    JSONObject novel = SilukeCrawler.load(novelFile);
                    if (!novel.getString("file").equalsIgnoreCase(novelFile.getAbsolutePath()))
                        novel.put("file", novelFile.getAbsolutePath());

                    String html = WebPageGetter.getWebPagewithProxy(novel.getString("link"), proxy.value1, proxy.value2, "GBK");
                    if (html == null)
                        html = "";
                    html = html.replace("\\u201c", "“").replace("\\u201d", "”");
                    JSONObject contents = new JSONObject();
                    if (novel.has("contents"))
                        contents = novel.getJSONObject("contents");

                    JSONObject chapters = SilukeCrawler.getNovelChapters(html);

                    int origin = contents.length();
                    System.err.println("No." + no + ":\t" + novelID + "$ " + "Contents:\t" + contents.length());
                    System.err.flush();

                    if (chapters != null) {
                        boolean update = false;
                        for (String chapterId : chapters.keySet()) {
                            if (contents.has(chapterId)) {
                                String content = contents.getJSONObject(chapterId).getString("content");
                                if (content == null || content.length() == 0)
                                    update = true;
                            } else
                                update = true;

                            if (update) {
                                JSONObject chapter = chapters.getJSONObject(chapterId);
                                String url = novel.getString("link") + chapter.get("link");
                                Element cc = Jsoup.parse(WebPageGetter.getWebPagewithProxy(url, proxy.value1, proxy.value2, "GBK"));
                                Element text = cc.select("div#content").first();
                                if (text == null)
                                    chapter.put("content", "");
                                else
                                    chapter.put("content", text.text());
                                contents.put(chapterId, chapter);
                                System.err.println("No." + no + ":\t" + novelID + "$ " +"\t"+contents.length()+ "\t" + chapter.getString("id") + ":\t" + chapter.getString("title"));
                                System.err.flush();

                                try {
                                    Thread.sleep(System.currentTimeMillis() % 5 * 100 + 100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            if ((contents.length() - origin) != 0 && (contents.length() - origin) % 10 == 0) {
                                System.out.println("No." + no + ":\t" + novelID + "$" + "Contents:\t" + contents.length());
                                System.out.flush();
                                novel.put("contents", contents);
                                SilukeCrawler.save(novel, novelFile);
                            }
                        }
                        System.out.println("Contents:\t" + contents.length());
                        System.out.flush();
                        novel.put("contents", contents);
                        SilukeCrawler.save(novel, novelFile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("No." + no + ":\t" + novelID + "$ " + "end");

            }
            System.out.println("Crawler " + no + ":" + this.proxy.toString() + " close");
        }
    }

    public static void main(String args[]) throws Exception {
//        List<Pair<String, Integer>> proxies = CrawlerProxyGetter.Getter("http://pachong.org");
//        for (Pair<String, Integer> proxy : proxies) {
//            System.out.println(proxy.toString());
//        }
        String baseDir = "/Users/chyc/Workspaces/Mine/";
        String novelFileName = "crawler/data/siluke2.info";
        String outDir = "crawler/data/out2/";
        String proxyURL = "http://pachong.org";
//        System.setErr(new PrintStream(baseDir + "crawler/data/err.log"));
//        System.setOut(new PrintStream(baseDir + "crawler/data/out.log"));

        SilukeCrawlerWithMultiThread silukeCrawlerWithMultiThread = new SilukeCrawlerWithMultiThread(baseDir, novelFileName, outDir, proxyURL);
        silukeCrawlerWithMultiThread.start();
//        System.err.close();
//        System.out.close();
//        String html = WebPageGetter.getWebPagewithRandomProxy("http://pachong.org");
//        System.out.println(html);
    }
}
