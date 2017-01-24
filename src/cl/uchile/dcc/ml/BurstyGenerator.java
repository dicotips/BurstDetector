package cl.uchile.dcc.ml;

import cl.uchile.dcc.utils.PropertiesTD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This Class implements the use of the Bursty Terms Algorithm (TopK).
 * Based on SOURCE:   https://github.com/Aluxian/Market-Basket-Recommendation-FP-Growth/blob/master/src/main/java/com/aluxian/mba/Main.java
 * WebSite SOURCE:    https://algorithmia.com/algorithms/Aluxian/AffinityAnalysisFPGrowth
 * Dataset:           https://github.com/stedy/Machine-Learning-with-R-datasets/blob/master/groceries.csv
 * 
 * @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
 * @version     1.0                 
 * @since       2016-08-17
 */
public class BurstyGenerator {
  
  /**
   * Applies the BurstyTerms into a Set of transactions  algorithm.
   * @param itemsetList List of transactions.
   * @return a Set of Entry(itemset, support).
   */
  public static List<Map.Entry<Set<String>, Double>> frequenItemSets(List<Set<String>> itemsetList, PropertiesTD prop){

    Map<Set<String>, Integer> map_counter = new HashMap<>();

    int count = 0;
    for (Set<String> transaction : itemsetList) {
      for (String item : transaction) {
        Set<String> entry = new HashSet<>();
        entry.add(item);
        if(!map_counter.containsKey(entry)){
          map_counter.put(entry, 1);
        }
        else{
          map_counter.put(entry, map_counter.get(entry)+1);
        }
        count++;
      }
    }
    
    //Algorithm Bursty Terms
    Map<Set<String>, Double> result = new LinkedHashMap<>();
    
    map_counter.entrySet().stream()
                .sorted(Map.Entry.<Set<String>, Integer>comparingByValue().reversed())
                .forEachOrdered(x -> result.put(x.getKey(), x.getValue().doubleValue()));
    
    // Formating result
    List<Map.Entry<Set<String>, Double>> result_filtered = new ArrayList<>();
    
    int count_2 = 0;
    for (Map.Entry<Set<String>, Double> entry : result.entrySet()) {
      if(count_2 >= prop.describer_bursty_n)
        break;
      
      entry.setValue(entry.getValue()/count);
      result_filtered.add(entry);
      
      count_2++;
    }
    
    return result_filtered;
  }
  
}

