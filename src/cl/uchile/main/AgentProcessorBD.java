package cl.uchile.main;

import cl.uchile.dcc.events.detection.EventBT;
import cl.uchile.dcc.events.detection.WindowRow;
import cl.uchile.dcc.utils.MapSerializer;
import cl.uchile.dcc.utils.PropertiesTD;
import cl.uchile.dcc.utils.QueueSerializer;
import cl.uchile.dcc.utils.TUtils;
import cl.uchile.dcc.utils.WordsBag;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This Class implements an application that gets the WordBags from the
 * AgPacker and process the keywords into the Hashtables with theirs statistics.
 * It generates the Busrts per signal and store them into the database. 
 *
 * @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
 * @version     1.0                 
 * @since       2016-08-17
 */
public class AgentProcessorBD extends Thread{
  private PropertiesTD prop;
  
  private BlockingQueue<WordsBag> WordsBagCacheIN;
  private ConcurrentHashMap<String, WindowRow> HashTableOUT;
  private BlockingQueue<EventBT> EventsCacheOut;

  private int _id_thread;
  private String str_conn;
  private Connection conn;
  
  private long WinTime;
  
  
  
  private int serialize_counter = 0;

  /**
   * Constructor.
   * <p>
   * Initializes the AgentProcessor with the parameters of _setu.txt file,
   * specifies the source WordBags per window, and sends the detected bursts
   * to the Describer.
   * @param id_thread   ID of the Thread that runs in parallel.
   * @param QFeaturesIn Queue that contains the WordBags per window.
   * @param HTOut       HashTable to process
   * @param QEventsOut  Queue for sending the detected Bursts to the AgDescriber.
   * @param PropTD Parameters from the _setup.txt file.
   */
  public AgentProcessorBD(int id_thread,
                          BlockingQueue<WordsBag> QFeaturesIn,
                          ConcurrentHashMap<String, WindowRow> HTOut, 
                          BlockingQueue<EventBT> QEventsOut, 
                          PropertiesTD PropTD) {
    _id_thread = id_thread;
    WordsBagCacheIN = QFeaturesIn;
    HashTableOUT = HTOut;
    EventsCacheOut = QEventsOut;
    prop = PropTD;
    
    WinTime = PropTD.event_window_size;

    try {
    str_conn = String.format("jdbc:mysql://%s:%s/%s?characterSetResults=utf8&useUnicode=true&useLegacyDatetimeCode=false&autoReconnect=true&failOverReadOnly=false&maxReconnects=100&jdbcCompliantTruncation=false&useSSL=false&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'",
                                PropTD.mysql_server_ip,
                                PropTD.mysql_server_port,
                                PropTD.mysql_server_database);
      conn = DriverManager.getConnection( str_conn,
                                          PropTD.mysql_user,
                                          PropTD.mysql_password);
    }
    catch (SQLException ex) {
      System.err.println(AgentDBStorer.class.getName());
      System.err.println(ex.getMessage());
      System.err.println(ex.getSQLState());
    }
  }
    
  /**
   * Body of the Thread: retrieves the keywords, computes the statistics per 
   * signal into the HashTable, detects bursts, stores them and send them to the 
   * cached to the AgDescbriber.
   */
  @Override
  public void run(){
    try{
      while(true){
        if(WordsBagCacheIN.isEmpty()){
          System.out.println("PROCESSOR_BD"+_id_thread+" [SLEEP 3000] ["+ WordsBagCacheIN.size()
                                                   +"|"+ HashTableOUT.size() +"]");
          sleep(20000);
          continue;
        }
        
        WordsBag WBag = WordsBagCacheIN.poll();
        //COUNT WORDS IN THS ACTUAL WINDOW.
        Date BagTimeStamp = WBag.getTimeStamp();
        //System.out.println(TUtils.Date_Formatter2(BagTimeStamp));
        System.out.println((char)27 + "[32;40mPROCESSOR_BD"+_id_thread
                +" START [CurrentDateTime '"+TUtils.Date_Formatter(new Date())
                +"'][WindowTime '"+TUtils.Date_Formatter(BagTimeStamp)
                +"'][Serialize: "+ (serialize_counter+1) +"/"+prop.stats_serialize_win_freq+")"+ (char)27 + "[0m");
        
        Queue<String> WordsBag = WBag.getQueueWords();
        
        WordsBag.stream().forEach(KWord->{
          WindowRow WRow;
          if(!HashTableOUT.containsKey(KWord)){ // insert a new KWord
            WRow = new WindowRow(WinTime, BagTimeStamp);
            HashTableOUT.put(KWord, WRow);
          }
          else{
            WRow = HashTableOUT.get(KWord);
            if(WRow.getTimeSTampW1() != BagTimeStamp){
              WRow.MoveWindow(BagTimeStamp);
            }
          }
          
          //Set TotalWindow Frequency per type of signal.
          if(KWord.startsWith("__sys__ss_"))
            WRow.getW1().setTotalWindowFreq(WBag.c_ss);
          else if(KWord.startsWith("__sys__lang__"))
            WRow.getW1().setTotalWindowFreq(WBag.c_tweets);
          else if(KWord.startsWith("__sys__country_txt__"))
            WRow.getW1().setTotalWindowFreq(WBag.c_tweets);
          else if(KWord.startsWith("__sys__country_usr__"))
            WRow.getW1().setTotalWindowFreq(WBag.c_tweets);
          else if(KWord.startsWith("__sys__country_geo__"))
            WRow.getW1().setTotalWindowFreq(WBag.c_tweets);
          else if(KWord.startsWith("__sys__"+prop.keywords_name))
            WRow.getW1().setTotalWindowFreq(WBag.c_tweets);
          else
            WRow.getW1().setTotalWindowFreq(WBag.c_kw);
          
          WRow.AddW1(1);        
        });

        // STORE THE HT INTO THE DATABASE
        StoreHashTable(BagTimeStamp, HashTableOUT);
        
        //Prunning of the HashTable of the signals
        boolean flag_cleaning = true;//n_processes % (prop.prunning_min_windows / 3) == 0;
        if( flag_cleaning ){
          HashTableOUT.entrySet()
              .removeIf(
                      entry -> (
                      entry.getValue().getNW0() > prop.prunning_min_windows  && 
                      !entry.getKey().contains("__sys__") &&
                      (((double) entry.getValue().getNW() / entry.getValue().getNW0()) < prop.prunning_threshold ||
                      entry.getValue().getMean() < prop.prunning_threshold*100))
              );
          
          
        }
        System.gc();
        
        //SERIALIZATION OF THE HT
        
        if(prop.stats_serialize_store && flag_cleaning && (++serialize_counter % prop.stats_serialize_win_freq == 0)){
          
          System.out.println((char)27 + "[32;40mPROCESSOR_BD"+_id_thread
                +" [CurrentDateTime '"+TUtils.Date_Formatter(new Date())
                +"'] Serializing Stats..."+ (char)27 + "[0m");
        
          String aux_file = prop.stats_serialize_path+_id_thread+"_aux";
          String file = prop.stats_serialize_path+_id_thread;
          
          Path aux_file_path  = Paths.get(aux_file);
          Path file_path      = Paths.get(file);
          
          MapSerializer.serizalizeMap(HashTableOUT, aux_file, prop);

          try {
            Files.move(aux_file_path, file_path,
                    StandardCopyOption.REPLACE_EXISTING);
          } catch (IOException e) {
            System.err.println("[Serializer] Error saving backup!!");
            //e.printStackTrace();
          }
          
          serialize_counter = 0;
          
        } // end SERIALIZATION STATS
        
        System.out.println((char)27 + "[32;40mPROCESSOR_BD"+_id_thread
                +" [CurrentDateTime '"+TUtils.Date_Formatter(new Date())
                +"'][WindowTime '"+TUtils.Date_Formatter(BagTimeStamp)
                +"'][HashTableSize " + HashTableOUT.size()
                +"]"+ (char)27 + "[0m");

        //Message EnQueued to the AgDescriber
        if(prop.emerging_event_description){
          EventsCacheOut.add(new EventBT(BagTimeStamp, WinTime));
        
          //SERIALIZATION OF THE QUEUE
          if(prop.events_serialize_store){
            String aux_file = prop.events_serialize_path+"_aux";
            String file = prop.events_serialize_path;

            Path aux_file_path  = Paths.get(aux_file);
            Path file_path      = Paths.get(file);

            QueueSerializer.serizalizeQueue(EventsCacheOut, aux_file);

            try {
              Files.move(aux_file_path, file_path,
                      StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
              System.err.println("[Serializer-EventBT] Error saving backup!!");
              //e.printStackTrace();
            }

          }
        }
        
        System.gc();
        
      }
    }catch(Exception e){  
      System.err.println("PROCESSOR_BD"+_id_thread+" STOPED");
      System.err.println("PROCESSOR_BD"+_id_thread+" ERROR: " + e.toString());
      System.err.println("PROCESSOR_BD"+_id_thread+" ERROR: "
              + Arrays.toString(e.getStackTrace()));
      
      Thread.currentThread().interrupt();
      
    } // end TRY
  } // end RUN
  
  
  
  /**
   * Method that detect the bursts, sorts them and stores them into the database.
   * @param BagTimeStamp  Timestamp of the window processed.
   * @param HashTableOUT  Hashtable storing the signals per keyword.
   */
  public void StoreHashTable(Date BagTimeStamp, ConcurrentHashMap<String,WindowRow> HashTableOUT){
    String tbl_date = TUtils.Date_Formatter(BagTimeStamp).substring(0, 8);

    // Limpieza de la tabla (fechas que no son correctas)
    Set<String> keysList_valid = new HashSet<>();
    MapDebugger(keysList_valid, BagTimeStamp);
    System.gc();
    
    List<String> keysList_sorted = new ArrayList<>();
    if(keysList_valid.size() > 0){
      //SORTING WITH PARALLEL STREAMS
      HashTableOUT.entrySet().stream()
          .sorted(Map.Entry.comparingByValue())
          .forEachOrdered(e -> keysList_sorted.add(((Map.Entry<String, WindowRow>) e).getKey()));
    }
    
    try{
      //Storing Bursts into the database
      TUtils.createTable_EEventBD(tbl_date, conn);
      
      int temp_e_bt = 1;
      //for (String kl : keysList_sorted) {
      for (int i = keysList_sorted.size()-1; i >= 0; i--) {
        String kl = keysList_sorted.get(i);
        if(!keysList_valid.contains(kl))
          continue;
                
        int value_r = TUtils.insertStatement_EEventBD(tbl_date, BagTimeStamp, 
                      kl.startsWith("__sys__")?0:temp_e_bt, 
                      kl, 
                      HashTableOUT.get(kl), 
                      HashTableOUT.get(kl).isValid(), 
                      conn);
        temp_e_bt += kl.startsWith("__sys__")?0:value_r;
      }
    }
    catch (Exception se) {
      System.err.println(AgentDBStorer.class.getName());
      se.printStackTrace();
    }
    
  }
  
  /**
   * Debugger and statistics compute in WindowRows.
   * @param keysList     List of valid keywords
   * @param BagTimeStamp Timestamp of the processed window.
   */
  private void MapDebugger(Set<String> keysList, Date BagTimeStamp){
    
    HashTableOUT.entrySet().stream()
            .forEach(entry -> 
            {   WindowRow WR_HT = entry.getValue();
                if(WR_HT.getW1().getTimeStamp() != BagTimeStamp){
                  WR_HT.MoveWindow(BagTimeStamp);
                }
                WR_HT.CalcRates();
                WR_HT.CalcRatesVAR(BagTimeStamp);
                WR_HT.CalcRates_TF_IDF();

                if(WR_HT.isValid() >= 0
                        && WR_HT.getVar_Vel() > 0 
                        && WR_HT.getW1().getFrequency() > 1)
                  keysList.add(entry.getKey());

                if(entry.getKey().startsWith("__sys__"))
                  keysList.add(entry.getKey());
            });
  }
  
}