package _test;

import cl.uchile.dcc.events.detection.WindowRow;
import cl.uchile.dcc.utils.MapSerializer;

import java.util.concurrent.ConcurrentHashMap;

public class TestPrunning {
  public static void main(String[] args) throws InterruptedException{
    
    String str = "/Users/dicotips/Desktop/_stats_hashtables.ser0";
    int prunning_min_windows = 30;
    double prunning_threshold = 0.03;
    
    System.out.println("[Serializer] Loading Stats HashTables ...");
    
    ConcurrentHashMap<String,WindowRow> HT = MapSerializer.deserizalizeMap(str);
    System.out.println("HT: "+ HT.size());

    long lower  = 0;
    long higher = 0;

    for (String key : HT.keySet()) {
      WindowRow row = HT.get(key);

      double ratio = (double) row.getNW() / row.getNW0();
      
      if (    row.getNW0() > prunning_min_windows &&
              !key.contains("__sys__") &&
              (ratio < prunning_threshold ||
              row.getMean() < prunning_threshold*100)
          ) {
        //System.out.println(key +"\t"+ HT.get(key));
        lower++;
      }
      else{
        //System.out.println(key +"\t"+ HT.get(key));
        higher++;
      }

    }
    
    System.out.println("L: "+ lower + "\tH: "+ higher +"\tR: "+ ((double) lower / HT.size()));
    
    //HT.entrySet().removeIf(entry -> (
    //        entry.getValue().getNW0() > prunning_min_windows  && 
    //        ((double) entry.getValue().getNW() / entry.getValue().getNW0()) < prunning_threshold &&
    //        !entry.getKey().contains("__sys__") &&
    //        entry.getValue().getSumX() < 3 
    //));
    
    /*
    HT.entrySet().clear();
    Runtime.getRuntime().runFinalization();
    Runtime.getRuntime().gc();
    System.gc();
    Thread.sleep(20000);
    
    System.out.println("HT: "+ HT.size());
    HT = MapSerializer.deserizalizeMap(str);
    System.out.println("HT: "+ HT.size());
    
    HT = new ConcurrentHashMap<>();

    System.out.println("HT: "+ HT.size());
    
    System.out.println("[Serializer] Loaded");
    */
  }
}
