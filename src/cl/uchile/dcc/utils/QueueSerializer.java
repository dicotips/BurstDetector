package cl.uchile.dcc.utils;

import cl.uchile.dcc.events.detection.EventBT;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
* This Class stores a Queue(EventBT) into a file using serialization. It 
* creates a backup and loads it back in orther to store the queue of events.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-09-03
*/
public class QueueSerializer {
  
  //Load Queue of events
  /**
   * Loader of the Queue of events from files. If its a 
   * problem in the deserialization, if will create new Queue.
   * @param prop Parapeters stored in Properties from the _setup.txt file.
   * @return Returns the Queue of EventBT.
   */
  public static BlockingQueue<EventBT> loadSerializer(PropertiesTD prop){
    System.out.println("[Serializer-EventBT] Loading Events Queue ...");
    BlockingQueue<EventBT> queue = null;
    
    if(prop.events_serialize_store)
      queue = QueueSerializer.deserizalizeQueue(prop.events_serialize_path);
    
    if(queue == null)
      queue = new LinkedBlockingQueue<>();
    
    return queue;
  }
  
  /**
   * Serializer that stores the Queue(EventBT) into a file.
   * @param queue_source Queue containing the Events.
   * @param file_path Path of the file to store the data.
   */
  public static void serizalizeQueue(BlockingQueue<EventBT> queue_source, String file_path){
    //SERIALIZE
    try{
      FileOutputStream fos = new FileOutputStream(file_path);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(queue_source);
      oos.close();
      fos.close();
    }catch(Exception e){
      System.err.println("[Serializer-EventBT] ");
      e.printStackTrace();
    }    
  }
  
  /**
   * De-Serializer that loads the content of a file to a
   * Queue(EventBT). If it presents an error in the process, returns an 
   * empty Queue.
   * @param file_path Path of the file that contain the data for the Queue.
   * @return Returns the Queuecontaining the information of the events.
   */
  public static BlockingQueue<EventBT> deserizalizeQueue( String file_path){
    BlockingQueue<EventBT> queue_destiny = null;
    try{
       FileInputStream fis = new FileInputStream(file_path);
       ObjectInputStream ois = new ObjectInputStream(fis);
       queue_destiny = (LinkedBlockingQueue<EventBT>) ois.readObject();
       ois.close();
       fis.close();
       return queue_destiny;
    }catch(Exception e){
      System.err.println("[Serializer-EventBT] "+e.getMessage());
    }
    return null;
  }
  
  
  /**
   * This is the main method tests the use of the the Serialization and 
   * de-serialization of the Queue if Events.
   * 
   * @param  args Nothing
   */
  public static void main(String[] args){
    PropertiesTD prop = new PropertiesTD("/Users/dicotips/Dropbox/Research_SourceCode/Twitter_Crawler/_setup.txt");
    
    // Creating the data structure to store the data
    BlockingQueue<EventBT> queue = new LinkedBlockingQueue<>();
    
    EventBT e1 = new EventBT(new Date(), 300);
    EventBT e2 = new EventBT(new Date(), 300);
    EventBT e3 = new EventBT(new Date(), 300);
    EventBT e4 = new EventBT(new Date(), 300);
    
    queue.add(e1);
    queue.add(e2);
    queue.add(e3);
    queue.add(e4);
    
    // Prints the content of the map before the serialization.
    for (EventBT ev : queue) {
      System.out.println(ev);
    }
    System.out.println("\n");
    
    // SERIALIZE MAP
    String path = prop.events_serialize_path;
    serizalizeQueue(queue, path);
    System.out.println();
    
    // DESERIALIZE MAP
    BlockingQueue<EventBT> queue_destiny = null;
    queue_destiny = deserizalizeQueue(path);
    for (EventBT ev : queue_destiny) {
      System.out.println(ev);
    }
    
  }
}
