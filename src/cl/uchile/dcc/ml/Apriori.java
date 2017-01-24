package cl.uchile.dcc.ml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class SizeComarator implements Comparator<Set<String>> {
  public FrequentItemsetData<String> _data;

  public SizeComarator(FrequentItemsetData<String> data){
    _data = data;
  }

  @Override
  public int compare(Set<String> o1, Set<String> o2) {
    int compare_support = Double.compare(_data.getSupport(o2), (_data.getSupport(o1)));
    if(compare_support != 0)
      return compare_support;
    return Integer.valueOf(o2.size()).compareTo(o1.size());
  }
}


final class MyEntry<K, V> implements Map.Entry<K, V> {
  private final K key;
  private V value;

  public MyEntry(K key, V value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public K getKey() {
    return key;
  }

  @Override
  public V getValue() {
    return value;
  }

  @Override
  public V setValue(V value) {
    V old = this.value;
    this.value = value;
    return old;
  }
  
  @Override
  public String toString(){
    return getKey()+"="+getValue();
  }
}


public class Apriori {

  /*
   * SOURCE: http://codereview.stackexchange.com/questions/104637/apriori-algorithm-for-frequent-itemset-generation-in-java
  */
  public static List<Map.Entry<Set<String>, Double>> frequenItemSets(List<Set<String>> itemsetList){
    AprioriFrequentItemsetGenerator<String> generator = new AprioriFrequentItemsetGenerator<>();
    FrequentItemsetData<String> data = null;
    List<Map.Entry<Set<String>, Double>> result_filtered = new ArrayList<>();
    if(itemsetList.size() == 0)
      return result_filtered;

    //Algorithm APriori
    double support = 1.0;
    ArrayList<Set<String>> result_apriori;
    do{
      data = generator.generate(itemsetList, support);
      result_apriori = (ArrayList<Set<String>>) data.getFrequentItemsetList();
      support /= 2.0;
    }while(result_apriori.size() < 15 && support >= 0.005);

    if(result_apriori.size() == 0)
      return result_filtered;
      
    Collections.sort(result_apriori, new SizeComarator(data));

    //Clean Repeated Subsets
    int count = 1;
    for (Set<String> itemset : result_apriori) {
      if(result_filtered.isEmpty()){
        result_filtered.add(new MyEntry<>(itemset, data.getSupport(itemset)));
        continue;
      }

      boolean flag = false;
      
      for (Map.Entry<Set<String>, Double> entry : result_filtered) {
          flag = flag || (entry.getKey().containsAll(itemset) && (data.getSupport(itemset) >= entry.getValue()));
      }
      
      if(!flag){
        if(count < 100){
          result_filtered.add(new MyEntry<>(itemset, data.getSupport(itemset)));
          count++;
        }
        else
          break;

      }

    }

    return result_filtered;
  }
  
}
