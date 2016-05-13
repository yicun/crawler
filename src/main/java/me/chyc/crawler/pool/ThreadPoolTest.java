package me.chyc.crawler.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

/**
 * Created by yicun.chen on 10/20/14.
 */
public class ThreadPoolTest {
    private List<Integer> queue;
    private int n = 0;
    private boolean close = false;
//    synchronized void push(int n){
//        if (queue==null)
//            queue = new ArrayList<Integer>();
//        queue.add(n);
//        notifyAll();
//    }
//    synchronized int pop() throws InterruptedException {
//        if (queue!=null && queue.size() > 0)
//            return queue.remove(0);
//        else
//            wait();
//    }

    public void test() {
        queue = new ArrayList<Integer>();
        ExecutorService pool = Executors.newSingleThreadExecutor();
        OutputThread out0 = new OutputThread(0);
//        OutputThread out1 = new OutputThread(1);
//        OutputThread out2 = new OutputThread(2);
//        InputThread in0 = new InputThread(0);
        InputThread in1 = new InputThread(1);
        pool.execute(out0);
//        pool.execute(out1);
//        pool.execute(out2);
//        pool.execute(in0);
        pool.execute(in1);



    }

    class OutputThread implements Runnable {
        int no;

        public OutputThread(int n) {
            no = n;
        }

        public void run() {
            while (!close || queue.size() != 0) {
                try {
                    int n;
                    synchronized (queue) {
                        if (queue.size() == 0) {
                            System.out.println("Output " +no + ":\t" + "wait!");
                            queue.wait();
                        }
                        n = queue.remove(0);
                    }

                    System.out.println("Output " +no + ":\t" + "output " + n);
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class InputThread implements Runnable {
        int no;

        InputThread(int n) {
            no = n;
        }

        public void run() {
            while (n < 100) {
                synchronized (queue) {
                    queue.add(n++);
                    System.out.println("Input " +no + ":\t" + "input " + n);
                    queue.notify();
                }
                try {
                    System.out.println("Input " +no + ":\t" + "sleep!");
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            close = true;
        }
    }


    public static void main(String args[]) {
        ThreadPoolTest test = new ThreadPoolTest();
        test.test();
    }
}
