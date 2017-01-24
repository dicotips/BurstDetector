/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package _test;

import cl.uchile.dcc.ml.Apriori;
import cl.uchile.dcc.ml.AprioriFrequentItemsetGenerator;
import cl.uchile.dcc.ml.BurstyGenerator;
import cl.uchile.dcc.ml.FPGrowthGenerator;
import cl.uchile.dcc.utils.PropertiesTD;
import cl.uchile.dcc.utils.TUtils;
import cl.uchile.dcc.utils.TUtilsDescriber;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dicotips
 */
public class TestBurstyQuick {
  public static void main(String[] args) throws SQLException{
    
    PropertiesTD prop = new PropertiesTD("/Users/dicotips/Dropbox/"
            + "Research_SourceCode/Twitter_Crawler/_setup.txt");
    
    /*  DESCRIBER START Loading Tweets (BURSTY)[20130204 02:25:00 +0000,
                                                20130204 02:30:00 +0000,
                                                20130204 02:35:00 +0000] ... 
        >>|>>(68664)|>>DESCRIBER [CurrentDateTime '2016-09-29 16:45:17']
                      [WindowTime '{2013-02-04 02:25:00,
                                    2013-02-04 02:30:00,
                                    2013-02-04 02:35:00, 300}']
                      (n_load_kw: 68558)
        >>|>>(80374)|>>DESCRIBER [CurrentDateTime '2016-09-29 16:51:40']
                      [WindowTime '{2013-02-04 02:25:00,
                                    2013-02-04 02:30:00,
                                    2013-02-04 02:35:00, 300}']
                      (n_load_eq: 80106)
        >>|>>(75279)|
    */
    String table_ini = "20130204";
    String table_mid = "20130204";
    String table_end = "20130204";

    String date_ini = "2013-02-04 02:25:00";
    String date_mid = "2013-02-04 02:30:00";
    String date_end = "2013-02-04 02:35:00";
    
    String sql_previous = "";
    if(TUtilsDescriber.testTable(table_ini+"_inverted_index", prop))
      sql_previous =  "SELECT id_tweet, term "
                    + "FROM "+table_ini+"_inverted_index "
                    + "WHERE download_date >= '"+date_ini+"' "  // event_ini
                    + "  AND download_date <  '"+date_end+"' UNION ";  // event_end
    
    String sql  =   "SELECT DISTINCT v_t.id_tweet, v_t.lang_tweet, v_t.text_tweet, v_t.text_rt, v_t.text_quote " +
                    "FROM "+table_mid+"_event_bt as e INNER JOIN ("+sql_previous+" "+
                    "                                               SELECT id_tweet, term FROM "+table_end+"_inverted_index" +
                    "                                               WHERE download_date >= '"+date_ini+"' "+
                    "                                                 AND download_date <  '"+date_end+"' "+
                    "                                             ) as i ON e.term=i.term " +
                    "                                 INNER JOIN v_"+prop.keywords_name+" v_t ON i.id_tweet=v_t.id_tweet " +
                    "WHERE   v_t.type = 'TWEET' " +
                    "    AND v_t.download_date >= '"+date_ini+"' " +  // event_ini
                    "    AND v_t.download_date <  '"+date_end+"' " +  // event_end
                    "    AND v_t.repeated_user in ("+ ((prop.repeated_user)?"0":"0,1")+") " +
                    "	   AND v_t.has_keyword = 1" +
                    "    AND v_t.blacklisted_tweet = 0 " +
                    "    AND v_t.blacklisted_user = 0 " +
                    "	   AND e.valid = 1 " +
                    "    AND (e.z_score_ln  > "+prop.event_zscore_minimum+" OR " +
                    "         e.z_score_rar_ln > "+prop.event_zscore_minimum+") " +
                    "    AND e.term <> '__sys__ss_neutral' " +
                    "    AND e.var_freq >= "+prop.event_var_freq_minimum+" " +
                    "    AND e.w1__date = '"+date_mid+"' ";   // event_mid
    List<Set<String>> itemsetList_kw = TUtilsDescriber.LoadDescriber(sql, prop);
    System.out.println("NRO TransaccionesDB: "+ itemsetList_kw.size());
    
    /* PRINT TRANSACTIONS
    int count = 0;
    for (Set<String> set : itemsetList_kw) {
      System.out.println(set);
      count++;
      if (count>3) {
        break;
      }
    }*/
    

    //**************************************************************************
    //TUtilsDescriber.cleanZiptTail(itemsetList_kw, prop);
    List<Set<String>> transactions = itemsetList_kw;

    // Compute the frequency per token ~ O(n)    
    HashMap<String, Integer> ht = new HashMap<>();
    
    System.out.println("\nLimpiando 1("+TUtils.Date_Formatter(new Date())+")");
    for (Set<String> itemSet : transactions) {
      for (String item : itemSet) {
        if(ht.containsKey(item))
          ht.put(item, ht.get(item)+1);
        else
          ht.put(item, 1);
      }
    }
    
    System.out.println("\nLimpiando 2("+TUtils.Date_Formatter(new Date())+")");
    
    // List tokens with Freq <= minim  ~ O(n)
          //ht.entrySet().removeIf(e->e.getValue() > prop.describer_freq_minimum);
    Set<String> list_1 = new HashSet<>();
    for (String item : ht.keySet()) {
      if(ht.get(item) <= prop.describer_freq_minimum)
       list_1.add(item);
    }
    
    System.out.println("\nLimpiando 3("+TUtils.Date_Formatter(new Date())+")");
    // Remove tokens with low frequency
    for (Set<String> itemSet : transactions) {
      //for (String item : ht.keySet()) {
      //  if(itemSet.contains(item)){
      //    itemSet.remove(item);
      //  }
      //}
      itemSet.removeAll(list_1);
    }
    
          //for (Set<String> itemSet : transactions) {
          //  itemSet.removeAll(ht.keySet());
          //}
    
    //Remove the elements that excede the Maximum number of items.
    System.out.println("Limpiando 4("+TUtils.Date_Formatter(new Date())+")");
    if(prop.describer_freq_maximum > 0 && prop.describer_freq_maximum < ht.entrySet().size()){
      //get the most frequent terms
      Object[] a = ht.entrySet().toArray();
      Arrays.parallelSort(a, new Comparator() {
          public int compare(Object o1, Object o2) {
            return ((Map.Entry<String, Integer>) o2).getValue().compareTo(
                    ((Map.Entry<String, Integer>) o1).getValue());
          }
      });
      
      
      System.out.println("Limpiando 4.1("+TUtils.Date_Formatter(new Date())+")");
      Object[] b = Arrays.copyOfRange(a, prop.describer_freq_maximum, a.length);
      Set<String> list_2 = new HashSet<>();
      for (Object obj : b) {
        Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) obj;
        list_2.add(entry.getKey());
      }
      System.out.println("Limpiando 4.2("+TUtils.Date_Formatter(new Date())+")");
      
      //Remove the non top-k elements
      for (Set<String> itemSet : transactions) {
        //for (Object item : b) {
        //  Map.Entry<String, Integer> e = (Map.Entry<String, Integer>) item;
        //  if(itemSet.contains(e.getKey())){
        //    itemSet.remove(e.getKey());
        //  }
        // }
        itemSet.removeAll(list_2);
      }
      System.out.println("Limpiando 4.3("+TUtils.Date_Formatter(new Date())+")");
    }
    
    //Remove elements with size=1 and contains "Ω≈"
    System.out.println("Limpiando 5("+TUtils.Date_Formatter(new Date())+")");
    for (Set<String> itemSet : transactions) {
      if(itemSet.size()==1)
        for (String string : itemSet) {
          if(string.contains("≈")){
            itemSet.remove(string);
        }
      } 
    }
    
    //Remove transactions with 0 elements.
    System.out.println("Limpiando 6("+TUtils.Date_Formatter(new Date())+")");
    transactions.removeIf(e->e.isEmpty());
    
    
    System.out.println("Clean Cola: "+ itemsetList_kw.size());

    //*************************************************************************
    List<Map.Entry<Set<String>, Double>> result_kw1 = 
            BurstyGenerator.frequenItemSets(itemsetList_kw, prop);
    System.out.println("\n\nBURSTY("+TUtils.Date_Formatter(new Date())+"): "+ result_kw1.size());
    for (Map.Entry<Set<String>, Double> entry : result_kw1) {
      System.out.println(entry.getKey() +"\t"+ entry.getValue());
    }
    
    List<Map.Entry<Set<String>, Double>> result_kw2 = Apriori.frequenItemSets(itemsetList_kw);
    System.out.println("\n\nAPRIORI("+TUtils.Date_Formatter(new Date())+"): "+ result_kw2.size());
    for (Map.Entry<Set<String>, Double> entry : result_kw2) {
      System.out.println(entry.getKey() +"\t"+ entry.getValue());
    }
    
    List<Map.Entry<Set<String>, Double>> result_kw3 = FPGrowthGenerator.frequenItemSets(itemsetList_kw);
    System.out.println("\n\nFPGROWTH("+TUtils.Date_Formatter(new Date())+"): "+ result_kw3.size());
    for (Map.Entry<Set<String>, Double> entry : result_kw3) {
      System.out.println(entry.getKey() +"\t"+ entry.getValue());
    }
  }          
}
