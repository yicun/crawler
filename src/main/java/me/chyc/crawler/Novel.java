package me.chyc.crawler;

import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yicun.chen on 9/26/14.
 */
public class Novel {
    private static String ID = "id";
    private static String NAME = "name";
    private static String LINK = "link";
    private static String FILE = "file";
    private static String CONTENTS = "contents";

    // {"file":"src/me/chyc/out/9881.novel","contents":{},"name":"完美世界","link":"http://www.siluke.info/0/9/9881/","id":"9881"}
    private String id;
    private String name;
    private String link;
    private String file;
    private List<Chapter> contents;

    public Novel(String id, String name, String link, String file, List<Chapter> contents) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.file = file;
        this.contents = contents;
    }

    public static Novel readNovelfromJsonFile(File jsonFile) throws IOException {
        JSONObject jsonObject = SilukeCrawler.load(jsonFile);
        String id = jsonObject.getString(ID);
        String name = jsonObject.getString(NAME);
        String link = jsonObject.getString(LINK);
        String file = jsonObject.getString(FILE);
        JSONObject contents = jsonObject.getJSONObject(CONTENTS);

        List<Chapter> chapters = new ArrayList<Chapter>();

        for (String key : contents.keySet())
            chapters.add(new Chapter(contents.getJSONObject(key)));
        Collections.sort(chapters);
        return new Novel(id, name, link, file, chapters);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Chapter> getContents() {
        return contents;
    }

    public void setContents(List<Chapter> contents) {
        this.contents = contents;
    }

    public void saveToTxt(File txtFile) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txtFile)));
        bw.write(ID.toUpperCase() + ":" + this.id + "\n");
        bw.write(NAME.toUpperCase()+ ":" + this.name + "\n");
        bw.write(LINK.toUpperCase() + ":" + this.link+"\n");
        bw.write(FILE.toUpperCase() +":" + this.file + "\n");
        bw.write(CONTENTS.toUpperCase() + ":\n");
        for (Chapter chapter: this.contents) {
            bw.write(chapter.getTitle() +"\n");
            bw.write(chapter.getContent()+"\n");
            bw.flush();
        }
        bw.close();
    }
    public static void main(String args[]) throws IOException {
        String baseDir = "/Users/chyc/Workspaces/IDEAProjects/Mine/";
        String id = "7149";
        File jsonFile = new File(baseDir + "crawler/data/out/"+id+".novel");
        File txtFile = new File(baseDir + "crawler/data/"+id+".txt");
        Novel novel = Novel.readNovelfromJsonFile(jsonFile);
        System.out.println(novel.getId() + "\t" + novel.getName());
        novel.saveToTxt(txtFile);
    }
}
