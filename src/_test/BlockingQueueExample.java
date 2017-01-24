/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package _test;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueExample {

  public static void main(String[] args) throws Exception {

    BlockingQueue<Integer> queue1 = new LinkedBlockingQueue<>();
    for (int i = 0; i < 10; i++) {
      queue1.add(i);
    }
    BlockingQueue<Integer> queue2 = new LinkedBlockingQueue<>();
    for (int i = 20; i < 30; i++) {
      queue2.add(i);
    }
    
    queue1.addAll(queue2);
    
    while(!queue1.isEmpty()){
      int a = queue1.poll();
      System.out.println(a);
    }
    
    /*
    int size = 100_000_00;
    
    BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
    ConcurrentHashMap<Integer, Integer> map1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<Integer, Integer> map2 = new ConcurrentHashMap<>();
    //HashMap<Integer, Integer> map = new HashMap<>();

    
    for (int i = 0; i < size; i++) {
      queue.add(i);
    }
    System.out.println(queue.size());

    Date date_ini1 = new Date();
    queue.stream()
            .forEach(c->{ 
                          int key = c%17;
                          if(map1.containsKey(key))
                            map1.put(key, map1.get(key)+1);
                          else
                            map1.put(key, 1);
                        });
    Date date_end1 = new Date();
    System.out.println(map1.toString());
    System.out.println("Time2: "+ ((date_end1.getTime()-date_ini1.getTime())/1000.0));
    
    //SEQUENTIAL LOOP
    Date date_ini2 = new Date();
    for (Integer key : queue) {
      if(map2.containsKey(key))
        map2.put(key, map2.get(key)+1);
      else
        map2.put(key, 1);
    }
    Date date_end2 = new Date();
    System.out.println("Time2: "+((date_end2.getTime()-date_ini2.getTime()))/1000.0);
    System.out.println(map2.size());
    */
  }
}