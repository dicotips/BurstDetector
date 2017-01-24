package cl.uchile.dcc.utils;

import cl.uchile.dcc.events.detection.WindowRow;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
* This Class stores a Map(String, WindowRow) into a file using serialization. It 
* creates a backup and loads it back in orther to store the Stats of the previous
* analysis.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public class MapSerializer {
  
  //Load HT Keywords for all threads
  /**
   * Loader of all the HT of Keywords from files for all threads. If its a 
   * problem in the deserialization, if will create new HT for the Thread that 
   * gave problems.
   * @param prop Parapeters stored in Properties from the _setup.txt file.
   * @return Returns a List containing the HTs for all the Threads
   */
  public static List<ConcurrentHashMap<String,WindowRow>> loadSerializer(PropertiesTD prop){
    System.out.println("[Serializer] Loading Stats HashTables ...");
    List<ConcurrentHashMap<String,WindowRow>> list_HT = new ArrayList<>();
    for (int i = 0; i < prop.event_n_detectors; i++) {
      ConcurrentHashMap<String,WindowRow> HT = null;
      if(prop.stats_serialize_store)
        HT = MapSerializer.deserizalizeMap(prop.stats_serialize_path+i);
        
      if(HT == null)
        HT = new ConcurrentHashMap<>();
      list_HT.add(HT);
    }
    return list_HT;
  }
  
  /**
   * Serializer that stores the HT(String, WindowRow) into a file.
   * @param hmap_source HashTable containing the signal values.
   * @param file_path Path of the file to store the data.
   */
  public static void serizalizeMap(ConcurrentHashMap<String, WindowRow> hmap_source, String file_path, PropertiesTD prop){
    //SERIALIZE
    try{
      RandomAccessFile raf = new RandomAccessFile(file_path, "rw");
      FileOutputStream fos = new FileOutputStream(raf.getFD());
      //FileOutputStream fos = new FileOutputStream(file_path);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(hmap_source);
      oos.close();
      fos.close();
    }catch(Exception e){
      System.err.println("[Serializer HashMap] "+e.getMessage());
      //e.printStackTrace();
    }    
  }
  
  /**
   * De-Serializer that loads the content of a file to a
   * HT(String, WindowRow). If it presents an error in the process, returns an 
   * empty HashTable.
   * @param file_path Path of the file that contain the data for the HTs.
   * @return Returns the HashTable containing the information of the signal.
   */
  public static ConcurrentHashMap<String, WindowRow> deserizalizeMap( String file_path){
    ConcurrentHashMap<String, WindowRow> hmap_destiny = null;
    try{
      RandomAccessFile raf = new RandomAccessFile(file_path, "r");
      FileInputStream fis = new FileInputStream(raf.getFD());
      //FileInputStream fis = new FileInputStream(file_path);
      if(fis == null)
        return null;
      ObjectInputStream ois = new ObjectInputStream(fis);
      hmap_destiny = (ConcurrentHashMap<String, WindowRow>) ois.readObject();
      ois.close();
      fis.close();
      return hmap_destiny;
    }catch(Exception e){
      System.err.println("[De-serializer HashMap] "+ e.getMessage());
      //e.printStackTrace();
    }
    return null;
  }
  
  
  /**
   * This is the main method tests the use of the the Serialization and 
   * de-serialization of the HashMaps that store the Signals over time.
   * 
   * @param  args Nothing
   */
  public static void main(String[] args){
    // Creating the data structure to store the data
    PropertiesTD prop = new PropertiesTD("/Users/dicotips/Dropbox/"
            + "Research_SourceCode/Twitter_Crawler/_setup.txt");
    
    String path = "/Users/dicotips/Desktop/hashmap.ser";
    
    ConcurrentHashMap<String, WindowRow> HT1 = new ConcurrentHashMap<>();
    WindowRow wr1 = new WindowRow(10, new Date());
    WindowRow wr2 = new WindowRow(10, new Date());
    WindowRow wr3 = new WindowRow(10, new Date());
    
    wr1.AddW1(10);wr1.AddW2(20);wr1.CalcRates();wr1.CalcRatesVAR(new Date());
    wr1.getW1().setTotalWindowFreq(20);
    wr1.getW2().setTotalWindowFreq(25);
    wr2.AddW1(50);wr2.AddW2(10);wr2.CalcRates();wr2.CalcRatesVAR(new Date());
    wr1.getW1().setTotalWindowFreq(20);
    wr1.getW2().setTotalWindowFreq(25);
    wr3.AddW1(30);wr3.AddW2(10);wr3.CalcRates();wr3.CalcRatesVAR(new Date());
    wr1.getW1().setTotalWindowFreq(20);
    wr1.getW2().setTotalWindowFreq(25);
    
    HT1.put("uno",    wr1);
    HT1.put("dos",    wr2);
    HT1.put("tres",   wr3);
    
    /*
    //FUNCIONAMIENTO DE SERIALIZACION
    // Prints the content of the map before the serialization.
    for (String str : HT1.keySet()) {
      System.out.println(str +"\t"+ HT1.get(str));
    }
    System.out.println("\n");
    
    // SERIALIZE MAP
    serizalizeMap(HT1, path, prop);
    System.out.println();
    
    // DESERIALIZE MAP
    ConcurrentHashMap<String, WindowRow> ht_destiny = null;
    ht_destiny = deserizalizeMap(path);
    for (String str : ht_destiny.keySet()) {
      System.out.println(str +"\t"+ ht_destiny.get(str));
    }
    */
    
    
    //SERIALIZATION
    //**************************************************************************
    Date time1 = new Date();
    System.out.println("INIT: "+ TUtils.Date_Formatter(time1));
      int n = 10_000_000;
      for (int i = 0; i < n; i++) {
        HT1.put(Integer.toString(i), wr1);
      }
    //**************************************************************************
    Date time2 = new Date();
    System.out.printf("HT PUT: %s ( %.2f sec)\n", TUtils.Date_Formatter(time2), (time2.getTime()-time1.getTime())/1000.0);
    //**************************************************************************
    Date time3 = new Date();
    System.out.printf("SERIALIZE HT: %s ( %.2f sec)\n", TUtils.Date_Formatter(time3), (time3.getTime()-time2.getTime())/1000.0);
      serizalizeMap(HT1, path, prop);
    Date time4 = new Date();
    System.out.printf("FINISH SERIALIZE: %s ( %.2f sec)\n", TUtils.Date_Formatter(time4), (time4.getTime()-time3.getTime())/1000.0);
    //**************************************************************************
    

    //DE-SERIALIZATION
    //**************************************************************************
    Date time5 = new Date();
    System.out.printf("DE-SERIALIZE INI: %s ( %.2f sec)\n", TUtils.Date_Formatter(time4), (time5.getTime()-time4.getTime())/1000.0);
      ConcurrentHashMap<String, WindowRow> HT2 =  deserizalizeMap(path);
    Date time6 = new Date();
    System.out.printf("FINISH DE-SERIALIZE INI: %s ( %.2f sec)\n", TUtils.Date_Formatter(time4), (time6.getTime()-time5.getTime())/1000.0);
    //**************************************************************************
    
    
    
  }
}
