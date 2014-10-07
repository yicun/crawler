package me.chyc.crawler;

import me.chyc.utils.WebPageGetter;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

import static org.jsoup.Jsoup.parse;

/**
 * Created by chyc on 7/28/14.
 */
public class SilukeCrawler {

    public static JSONObject getNovelList(String html) {
        Element element = parse(html);
        if (element == null)
            return null;
        JSONObject lists = new JSONObject();
        Elements trs = element.select("tr");
        for (Element tr : trs) {
            Elements links = tr.select("a[href]");
            if (links == null || links.size() == 0)
                continue;
            String name = links.get(0).text();
            String href = links.get(0).attr("href");
            String id = href.split("/")[href.split("/").length - 1];
            JSONObject novel = new JSONObject();
            novel.put("id", id);
            novel.put("name", name);
            novel.put("link", href);
            lists.put(id, novel);
        }
        return lists;
    }

    public static JSONObject updateNovel(JSONObject novel) throws Exception {
        String html = WebPageGetter.getWebPage(novel.getString("link"));
        JSONObject contents = new JSONObject();
        if (novel.has("contents"))
            contents = novel.getJSONObject("contents");

        int origin = contents.length();

        JSONObject chapters = getNovelChapters(html);
        if (chapters != null) {
            boolean update = false;
            for (String chapterId : chapters.keySet()) {
                if (contents.has(chapterId)){
                    String content = contents.getJSONObject(chapterId).getString("content");
                    if (content == null || content.length() == 0)
                        update = true;
                }else
                    update = true;

                if (update) {
                    JSONObject chapter = chapters.getJSONObject(chapterId);
                    String url = novel.getString("link") + chapter.get("link");
                    Element cc = Jsoup.parse(WebPageGetter.getWebPage(url));
                    Element text = cc.select("div#content").first();
                    if (text == null)
                        chapter.put("content", "");
                    else
                        chapter.put("content", text.text());
                    System.out.println("\t" + chapter.getString("id") + ":\t" + chapter.getString("title"));
                    contents.put(chapterId, chapter);
                    try {
                        Thread.sleep(System.currentTimeMillis() % 5 * 100 + 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if ((contents.length() - origin) != 0 && (contents.length() - origin) % 20 == 0) {
                    System.out.println("Contents:\t" + contents.length());
                    novel.put("contents", contents);
                    save(novel, new File(novel.getString("file")));
                }
            }
        }

        System.out.println("Contents:\t" + contents.length());
        novel.put("contents", contents);
        return novel;
    }

    public static void updateNovel(File novelFile) throws Exception {
        JSONObject novel = load(novelFile);
        if (!novel.getString("file").equalsIgnoreCase(novelFile.getAbsolutePath()))
            novel.put("file", novelFile.getAbsolutePath());

        String html = WebPageGetter.getWebPage(novel.getString("link"));

        JSONObject contents = new JSONObject();
        if (novel.has("contents"))
            contents = novel.getJSONObject("contents");

        JSONObject chapters = getNovelChapters(html);

        int origin = contents.length();
        System.out.println("Contents:\t" + contents.length());

        if (chapters != null) {
            boolean update = false;
            for (String chapterId : chapters.keySet()) {
                if (contents.has(chapterId)){
                    String content = contents.getJSONObject(chapterId).getString("content");
                    if (content == null || content.length() == 0)
                        update = true;
                }else
                    update = true;

                if (update) {
                    JSONObject chapter = chapters.getJSONObject(chapterId);
                    String url = novel.getString("link") + chapter.get("link");
                    Element cc = Jsoup.parse(WebPageGetter.getWebPage(url));
                    Element text = cc.select("div#content").first();
                    if (text == null)
                        chapter.put("content", "");
                    else
                        chapter.put("content", text.text());
                    System.out.println("\t" + chapter.getString("id") + ":\t" + chapter.getString("title"));
                    contents.put(chapterId, chapter);
                    try {
                        Thread.sleep(System.currentTimeMillis() % 5 * 100 + 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if ((contents.length() - origin) != 0 && (contents.length() - origin) % 20 == 0) {
                    System.out.println("Contents:\t" + contents.length());
                    novel.put("contents", contents);
                    save(novel, novelFile);
                }
            }
            System.out.println("Contents:\t" + contents.length());
            novel.put("contents", contents);
            save(novel, novelFile);
        }
    }

    public static JSONObject getNovelChapters(String html) {
        Element doc = parse(html);
        Elements dds = doc.select("dd");
        if (dds == null || dds.size() == 0)
            return null;
        JSONObject chapters = new JSONObject();
        for (int i = 0; i < dds.size(); i++) {
            Element dd = dds.get(i);
            Element a = dd.children().first();
            if (a != null) {
                String link = a.attr("href");
                String title = a.attr("title");
                String id = link.substring(0, link.indexOf("."));
                JSONObject chapter = new JSONObject();
                chapter.put("id", id);
                chapter.put("title", title);
                chapter.put("link", link);
                chapters.put(id, chapter);
            }
        }

        return chapters;
    }

    public static void save(JSONObject novel, File file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        bw.write(novel.toString());
        bw.newLine();
        bw.flush();
        bw.close();
    }

    public static JSONObject load(File file) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null)
            sb.append(line);
        return new JSONObject(sb.toString());
    }

    public static void main(String args[]) throws Exception {
        String baseDir = "/Users/chyc/Workspaces/IDEAProjects/Mine/";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(baseDir + "crawler/data/siluke.info"))));
        String line;
        int index = 0;
        while ((line = br.readLine()) != null) {
            System.out.println("No." + (index++) + "\t" + line);
            JSONObject novel = new JSONObject(line);
//            if (!novel.get("id").equals("8937"))
//                continue;
            File outfile = new File(baseDir + "crawler/data/out/" + novel.getString("id") + ".novel");
            if (outfile.exists()) {
                updateNovel(outfile);
            } else {
                System.out.println("Contents:\t" + 0);
                novel.put("file", outfile.getAbsolutePath());
                novel = updateNovel(novel);
                save(novel, new File(novel.getString("file")));
            }
        }
    }
}
