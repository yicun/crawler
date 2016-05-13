package me.chyc.crawler;

import org.json.JSONObject;

/**
 * Created by yicun.chen on 9/26/14.
 */
public class Chapter implements Comparable<Chapter> {
    private static String ID = "id";
    private static String TITLE = "title";
    private static String LINK = "link";
    private static String CONTENT = "content";

    //"3521065":{"link":"3521065.html","id":"3521065","title":"第四百九十九章 神秘的妖气","content":"神速记住【思路客】www.siluke.info
    String id;
    String title;
    String link;
    String content;
    String novelId;


    public Chapter(String id, String title, String link, String content, String novelId) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.content = content;
        this.novelId = novelId;
    }

    public Chapter(JSONObject jsonObject) {
        this.id = jsonObject.getString(ID);
        this.title = jsonObject.getString(TITLE);
        this.link = jsonObject.getString(LINK);
        this.content = jsonObject.getString(CONTENT);
        this.novelId = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String toTxt(){
        return id + ":\t" + title + "\n" + content;
    }

    @Override
    public int compareTo(Chapter o) {
        int l = Integer.valueOf(this.id.replaceAll("[^0-9]", ""));
        int r = Integer.valueOf(o.getId().replaceAll("[^0-9]", ""));
        if (l > r)
            return 1;
        else if (l < r)
            return -1;
        else
            return 0;
    }
}
