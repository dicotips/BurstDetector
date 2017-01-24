package _test;

import cl.uchile.dcc.events.detection.WindowRow;
import java.util.ArrayList;
import static java.util.Collections.reverseOrder;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author dicotips
 */
public class TestHashSort {

  public static void main(String[] args){
    int window_time = 300; // seconds
    
    Date t_w3 = new Date();
    Date t_w2 = new Date(t_w3.getTime()+window_time*1000);
    Date t_w1 = new Date(t_w2.getTime()+window_time*1000);
    
    System.out.println(t_w3);
    System.out.println(t_w2);
    System.out.println(t_w1);
    
    ConcurrentHashMap<String, WindowRow> HT1 = new ConcurrentHashMap<>();

    WindowRow wr1 = new WindowRow(window_time, t_w3);
    wr1.getW1().setTotalWindowFreq(25);
    wr1.AddW1(1);
    wr1.AddW1(2);
    wr1.AddW1(7);
    wr1.CalcRates();
    wr1.CalcRatesVAR(t_w3);
    wr1.CalcRates_TF_IDF();
    
    wr1.MoveWindow(t_w2);
    wr1.getW1().setTotalWindowFreq(60);
    wr1.AddW1(50);
    wr1.CalcRates();
    wr1.CalcRatesVAR(t_w2);
    wr1.CalcRates_TF_IDF();
    
    wr1.MoveWindow(t_w1);
    wr1.getW1().setTotalWindowFreq(70);
    wr1.AddW1(10);
    wr1.AddW1(20);
    wr1.CalcRates();
    wr1.CalcRatesVAR(t_w1);
    wr1.CalcRates_TF_IDF();
    
  
    WindowRow wr2 = new WindowRow(window_time, t_w3);
    wr2.getW1().setTotalWindowFreq(25);
    wr2.AddW1(12);
    wr2.CalcRates();
    wr2.CalcRatesVAR(t_w3);
    wr2.CalcRates_TF_IDF();
    
    wr2.MoveWindow(t_w2);
    wr2.CalcRates();
    wr2.CalcRatesVAR(t_w2);
    wr2.CalcRates_TF_IDF();
    
    wr2.MoveWindow(t_w1);
    wr2.getW1().setTotalWindowFreq(70);
    wr2.AddW1(10);
    wr2.AddW1(10);
    wr2.CalcRates();
    wr2.CalcRatesVAR(t_w1);
    wr2.CalcRates_TF_IDF();
    
    WindowRow wr3 = new WindowRow(window_time, t_w3);
    wr3.getW1().setTotalWindowFreq(25);
    wr3.CalcRates();
    wr3.CalcRatesVAR(t_w3);
    wr3.CalcRates_TF_IDF();
    
    wr3.MoveWindow(t_w2);
    wr3.getW1().setTotalWindowFreq(60);
    wr3.CalcRates();
    wr3.CalcRatesVAR(t_w2);
    wr3.CalcRates_TF_IDF();
    
    wr3.MoveWindow(t_w1);
    wr3.getW1().setTotalWindowFreq(70);
    wr3.AddW1(10);
    wr3.AddW1(20);
    wr3.CalcRates();
    wr3.CalcRatesVAR(t_w1);
    wr3.CalcRates_TF_IDF();
    
    
    WindowRow wr4 = new WindowRow(window_time, t_w3);
    wr4.getW1().setTotalWindowFreq(25);
    wr4.AddW1(2);
    wr4.CalcRates();
    wr4.CalcRatesVAR(t_w3);
    wr4.CalcRates_TF_IDF();
    
    wr4.MoveWindow(t_w2);
    wr4.getW1().setTotalWindowFreq(60);
    wr4.AddW1(3);
    wr4.CalcRates();
    wr4.CalcRatesVAR(t_w2);
    wr4.CalcRates_TF_IDF();
    
    wr4.MoveWindow(t_w1);
    wr4.getW1().setTotalWindowFreq(70);
    wr4.AddW1(1000);
    wr4.CalcRates();
    wr4.CalcRatesVAR(t_w1);
    wr4.CalcRates_TF_IDF();
    
    HT1.put("uno___", wr1);
    HT1.put("dos___", wr2); 
    HT1.put("tres__", wr3);
    HT1.put("cuatro", wr4);
    
    List<Map.Entry<String, WindowRow>> list = new LinkedList<>();
    HT1.entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .forEachOrdered(e -> list.add(0, (Map.Entry<String, WindowRow>) e));
    
    for (Map.Entry<String, WindowRow> entry : list) {
      System.out.println(entry);
    }
  
  }
}
