package _test;

import cl.uchile.dcc.events.detection.Window;
import cl.uchile.dcc.utils.*;
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
public class TestWindowSerializer {
  
  /**
   * Serializer that stores the HT(String, WindowRow) into a file.
   * @param hmap_source HashTable containing the signal values.
   * @param file_path Path of the file to store the data.
   */
  public static void serizalizeWindow(Window win_source, String file_path, PropertiesTD prop){
    //SERIALIZE
    try{
      //RandomAccessFile raf = new RandomAccessFile(file_path, "rw");
      //FileOutputStream fos = new FileOutputStream(raf.getFD());
      FileOutputStream fos = new FileOutputStream(file_path);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(win_source);
      oos.close();
      fos.close();
    }catch(Exception e){
      System.err.println("[Serializer Window] "+e.getMessage());
      e.printStackTrace();
    }    
  }
  
  /**
   * De-Serializer that loads the content of a file to a
   * HT(String, WindowRow). If it presents an error in the process, returns an 
   * empty HashTable.
   * @param file_path Path of the file that contain the data for the HTs.
   * @return Returns the HashTable containing the information of the signal.
   */
  public static Window deserizalizeWindow(String file_path){
    Window hmap_destiny = null;
    try{
      //RandomAccessFile raf = new RandomAccessFile(file_path, "r");
      //FileInputStream fis = new FileInputStream(raf.getFD());
      FileInputStream fis = new FileInputStream(file_path);
      if(fis == null)
        return null;
      ObjectInputStream ois = new ObjectInputStream(fis);
      hmap_destiny = (Window) ois.readObject();
      ois.close();
      fis.close();
      return hmap_destiny;
    }catch(Exception e){
      System.err.println("[De-serializer Window] "+ e.getMessage());
      e.printStackTrace();
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
    
    System.out.println(prop.keywords);
    
    /*
    String path = "/Users/dicotips/Desktop/hashmap.ser";

    Window w1 = new Window(new Date(), 1200);
    System.out.println(w1);
    
    
    // SERIALIZE Window
    serizalizeWindow(w1, path, prop);
    System.out.println();
    
    // DESERIALIZE Window
    Window win_destiny = null;
    win_destiny = deserizalizeWindow(path);
    
    System.out.println(win_destiny);
    */
   
  }
}
