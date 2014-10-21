package me.chyc.crawler;

/**
 * Created by chyc on 8/13/14.
 */
public class Test {
    public static void main(String args[]) {
//        String link = "http://www.siluke.info/0/25/25450/";
//        String url = "http://pachong.org";
//        String html = SilukeCrawler.getWebSource(url);
//        System.out.println(html);
//        int cat = 4288 + 3247;
//        int worm = 308 + 7437 ^ cat;
//        int duck = 3994 + 7147 ^ worm;
//        int fish = 896 + 7912 ^ duck;
//        int seal = 2458 + 8483 ^ fish;
//        System.out.println(14022 ^ cat + 9);
//        System.out.println(worm);
//        System.out.println(duck);
//        System.out.println(fish);
//        System.out.println(seal);


        int bat = 6398 + 2303;
        int calf = 7553 + 5247 ^ bat;
        int fish = 810 + 4254 ^ calf;
        int dog = 619 + 4055 ^ fish;
        int hen = 878 + 8584 ^ dog;
        System.out.println(bat);
        System.out.println(calf);
        System.out.println(fish);
        System.out.println(dog);
        System.out.println(hen);
        System.out.println((14022^hen) + 9);
        System.out.println((7623^fish) + 419);
        System.out.println((14482^bat) + 3488);





    }
}
