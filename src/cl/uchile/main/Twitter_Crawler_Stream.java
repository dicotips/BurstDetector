package cl.uchile.main;

import cl.uchile.dcc.events.detection.EventBT;
import cl.uchile.dcc.events.detection.WindowRow;
import cl.uchile.dcc.events.twitter.Tweet;
import cl.uchile.dcc.text.NGramRow;
import cl.uchile.dcc.utils.Blacklist;
import cl.uchile.dcc.utils.InitialState;
import cl.uchile.dcc.utils.MapSerializer;
import cl.uchile.dcc.utils.NGramsBag;
import cl.uchile.dcc.utils.PropertiesTD;
import cl.uchile.dcc.utils.QueueSerializer;
import cl.uchile.dcc.utils.TUtils;
import cl.uchile.dcc.utils.WordsBag;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
* This Class implements an application that
* collect Tweets from the Twitter Stream, stores them into a MySQL database
* and detects Bursty Terms over time. It uses the setup file _setup.txt to get
* the parameters to use in the entire pipeline of the analyis. This work is
* a contribution developed in my PhD Thesis at the University of Chile.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0
* @since       2016-08-17
*/
public class Twitter_Crawler_Stream {
  /** Counter of how many errors the Listerer had in a row. */
  public static int  c_error_listener = 0;
  /** Counter of how many errors the Storer had in a row. */
  public static int  c_error_dbstorer = 0;
  /** Timestamp of the last time when the debug of repeated users was made. */
  public static Date last_debug_date  = new Date();
  /** Timestamp of the last time when the debug of repeated users was made. */
  public static Date init_date  = new Date();
  /** Initiation State. */
  public static InitialState ini_state;

  
  /**
   * This is the main method which monitors all Threads of the application.
   * 
   * @param args Path of the _setup.txt file.
   */
  public static void main(String[] args) throws Exception {
    
    PropertiesTD prop = new PropertiesTD(args[0]);
    System.out.println(prop.listener_src_name);
    int th_time = 30;
    Blacklist bl = new Blacklist(prop);

    /**************************************************************************/
    // VARIABLES TO HANDLE BETWEEN THE AGENTS (Queues)
    BlockingQueue<Tweet> Q_01   = new LinkedBlockingQueue<>();  // Listener cache.
    List<BlockingQueue<Tweet>> Q_02 = new ArrayList<>();        // Tweets enqueued for analysis.
    for (int i = 0; i < prop.event_n_detectors; i++) {
      Q_02.add(new LinkedBlockingQueue<>());
    }
    List<BlockingQueue<WordsBag>>Q_03_k = new ArrayList<>();
    for (int i = 0; i < prop.event_n_detectors; i++) {
      Q_03_k.add(new LinkedBlockingQueue<>());
    }
    BlockingQueue<NGramsBag> Q_03_g = new LinkedBlockingQueue<>();  // NGrams.
    BlockingQueue<EventBT>   Q_04a  = QueueSerializer.loadSerializer(prop);  // EventBT - from Processor.
    
    /*
     * HT_R_Users   HashTable to monitor repeated Users into an interval.
     * HT_g         Storage for the N-Grams.
     * HT_k#         HashTable to handle each signal per term. Each AgPacker/AgProcesor has one.
     */
    ConcurrentHashMap<Long, Date>             HT_R_Users  = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, NGramRow>       HT_g        = new ConcurrentHashMap<>();
    List<ConcurrentHashMap<String,WindowRow>> HT_k        = MapSerializer.loadSerializer(prop);

    //SETING THE INI_STATE
    int ht_k_counter = 0;
    for (ConcurrentHashMap<String, WindowRow> concurrentHashMap : HT_k) {
      ht_k_counter += concurrentHashMap.size();
    }

    if(ht_k_counter > 0){
      //Recovery
      if(prop.listener_src_name.equals("twitter4j"))
        ini_state = InitialState.RECOVERY_STREAM;
      else
        ini_state = InitialState.RECOVERY_DATABASE;
    }
    else{
      //New
      if(prop.listener_src_name.equals("twitter4j"))
        ini_state = InitialState.NEW_STREAM;
      else
        ini_state = InitialState.NEW_DATABASE;
    }
    System.out.println("[SYSTEM] INI_STATE = "+ini_state.name());
    
    /**************************************************************************/
    // CREATION and INITIALIZATION of Agents to process data (Threads).
    AgentListener           agListener;
    AgentListenerDB         agListenerDB;
    AgentDBStorer           agDBStorer;
    List<AgentPacker>       agPacker = new ArrayList<>();
    List<AgentProcessorBD>  agProcessorBD = new ArrayList<>();
    AgentDBNGrams           agDBNGrams;
    AgentDescriber          agDescriber;
    
    // AGENTS INITIALIZATION (THREADS)
    agListener       = new AgentListener   (HT_R_Users, prop, 0, Q_01);
    agDBStorer       = new AgentDBStorer   (Q_01, Q_02, prop);
    for (int i = 0; i < prop.event_n_detectors; i++) {      
      agPacker.add     (new AgentPacker     (i, Q_02.get(i), Q_03_k.get(i), Q_03_g, prop, i*prop.event_window_shift));
      agProcessorBD.add(new AgentProcessorBD(i, Q_03_k.get(i), HT_k.get(i), Q_04a, prop));
    }
    agDescriber      = new AgentDescriber  (Q_04a, prop, true);
    agDBNGrams       = new AgentDBNGrams   (Q_03_g, HT_g, prop);
    agListenerDB     = new AgentListenerDB (prop, Q_01, Q_02, Q_03_k, Q_03_g, Q_04a, HT_k,
                                            agDBStorer, agPacker, agProcessorBD, agDescriber);
    
    /**************************************************************************/
    // START Threads
    agDBStorer.setPriority(Thread.MAX_PRIORITY);     
    switch(prop.listener_src_name){
      case "twitter4j": agListener.start();
                        break;
      case "database":  agListenerDB.start();
                        break;
      default:  throw new Exception("[prop] Argumento no valido en 'listener_src_name'.");
    }
    agDBStorer.start();
    if(prop.store_ngrams)
      agDBNGrams.start();
    if (prop.emerging_event_detection) {
      for (int i = 0; i < prop.event_n_detectors; i++) {
        agPacker.get(i).start();
        agProcessorBD.get(i).start();
      }
      if (prop.emerging_event_description) {
        agDescriber.start();
      }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    /*
     * MONITORING THREADS DURING RUNNING TIME. IF A THREAD FAILS, IT IS STARTED.
     */
    ///////////////////////////////////////////////////////////////////////////

    while(true){
      
      try {
        Thread.sleep(th_time*1000);
        th_time = 10;
        
        //Threads de detection (STATISTICS REPORT )
        String str_pack = "";
        String str_proc = "";
        String str_Q2 = "";
        String str_Q3_k = "";
        String str_ht = "";
        for (int i = 0; i < prop.event_n_detectors; i++) {
          str_pack  += ", Pack"+i+": "+  (agPacker.get(i).isAlive()?(char)27      + "[32;47mtrue":(char)27 + "[31;47mfalse") + (char)27 + "[34;47m";
          str_proc  += ", Proc"+i+": "+  (agProcessorBD.get(i).isAlive()?(char)27 + "[32;47mtrue":(char)27 + "[31;47mfalse") + (char)27 + "[34;47m";
          str_Q2   += ", "+Q_02.get(i).size();
          str_Q3_k += ", "+Q_03_k.get(i).size();
          str_ht   += ", h_t"+i+": " + HT_k.get(i).size();
        }
        str_Q2 = str_Q2.substring(2);
        str_Q3_k = str_Q3_k.substring(2);
        bl.update();  //Update of the Blacklists.
        //Threads (LISTENERS)
        String listener_status;
        switch(prop.listener_src_name){
          case "twitter4j": listener_status = "list("   +  agListener.count_tweets+":"+agListener.count_tweets_total+"): "+ (agListener.isAlive()?(char)27    + "[32;47mtrue":(char)27 + "[31;47mfalse-" +c_error_listener)      + (char)27 + "[34;47m";
                            break;
          case "database":  listener_status = "list: "  + (agListenerDB.isAlive()?(char)27    + "[32;47mtrue":(char)27 + "[31;47mfalse-"+c_error_listener)     + (char)27 + "[34;47m";
                            break;
          default:  throw new Exception("[prop] Argumento no valido en 'listener_src_name'.");
        }
        
        System.out.println( (char)27 + "[34;47m"
                + ">>>STATUS("+ TUtils.Date_Formatter2(new Date()) +"): "
                + "\tQ[q_1: "+Q_01.size()+", q_2: {"+str_Q2+"}, q_3k: {"+str_Q3_k+"}, q_3g: "+Q_03_g.size()+", q_4a: "+Q_04a.size()+"]"
                //+ "\n\t\t\t\t\t"
                + "\tTH["+ listener_status
                + ","
                //+ "\t\t\t\t\t\t"
                + "DB_store: " +  (agDBStorer.isAlive()?(char)27    + "[32;47mtrue":(char)27 + "[31;47mfalse") + (char)27 + "[34;47m"
                + ","
                //+ "\n\t\t\t\t\t\t"
                + str_pack.substring(2)
                + ","
                //+ "\n\t\t\t\t\t\t"
                + str_proc.substring(2)
                + ","
                //+ "\n\t\t\t\t\t\t"
                + "DB_NGRam: " +  (agDBNGrams.isAlive()?(char)27    + "[32;47mtrue":(char)27 + "[31;47mfalse") + (char)27 + "[34;47m"
                + ","
                //+ "\n\t\t\t\t\t\t"
                + "Desc: "     +  (agDescriber.isAlive()?(char)27   + "[32;47mtrue":(char)27 + "[31;47mfalse") + "("+TUtils.Date_Formatter2(agDescriber.last_start_run)+")" + (char)27 + "[34;47m"
                + "]"
                //+ "\n\t\t\t\t\t"
                + "\tHash[h_u: "  + HT_R_Users.size()
                + str_ht
                + ", h_g: "     + HT_g.size()
                +"]" + (char)27 + "[0m");
        
        
        //LISTENER
        if(prop.listener_src_name.equals("twitter4j")){
          if(!agListener.isAlive()){
            c_error_listener++;
            if(c_error_listener == 4 || c_error_listener == 8){
              agListener.twitterStream.shutdown();
              System.err.println(">>>FAIL("+c_error_listener+"):  AgentListener starting ...");
              agListener     = new AgentListener(HT_R_Users, prop, 0, Q_01);
              agListener.start();
            }
            if(c_error_listener == 12){
              agListener.twitterStream.shutdown();
              System.err.println(">>>FAIL("+c_error_listener+"):  AgentListener starting (switch oAuth Account)...");
              agListener     = new AgentListener(HT_R_Users, prop, 1, Q_01);
              agListener.start();
            }
            if(c_error_listener == 16){
              agListener.twitterStream.shutdown();
              System.err.println(">>>SYSTEM ERROR LISTENER");
              System.exit(1);
            }
          }
          else{
            c_error_listener = 0;
            agListener.count_tweets = 0;
          }
        }
        else{
          if(!agListenerDB.isAlive()){
            c_error_listener++;
            if(c_error_listener >= 5 && Q_04a.isEmpty() && agDescriber.getState() != State.TIMED_WAITING){
              System.err.println(">>>("+c_error_listener+"):  Closing AgentListenerDB.");
              System.exit(0);
            }
          }
          else{
            c_error_listener = 0;
            agListenerDB.count_tweets = 0;
          }
        }
        
        
        //STORER
        if(!agDBStorer.isAlive()){
          System.err.println(">>>FAIL:  AgentDBStorer starting ...");
          agDBStorer = new AgentDBStorer(Q_01, Q_02, prop);              
          agDBStorer.setPriority(Thread.MAX_PRIORITY);
          agDBStorer.start();
        }
        
        if (prop.emerging_event_detection) {
          for (int i = 0; i < prop.event_n_detectors; i++) {
            //PACKER-i
            if(!agPacker.get(i).isAlive()){
              System.err.println(">>>FAIL:  AgentTweetPacker-"+i+" starting ...");
              agPacker.set(i, new AgentPacker(i, Q_02.get(i), Q_03_k.get(i), Q_03_g, prop, i*prop.event_window_shift));
              agPacker.get(i).start();
            }
            //PROCESSOR-i (BD)
            if(!agProcessorBD.get(i).isAlive()){
              System.err.println(">>>FAIL:  AgentProcessor-"+i+" starting ...");
              agProcessorBD.set(i, new AgentProcessorBD(i, Q_03_k.get(i), HT_k.get(i), Q_04a, prop));
              agProcessorBD.get(i).start();
            }
          }
        }
        
        //PROCESSOR NGrams
        if(!agDBNGrams.isAlive() && prop.store_ngrams){
          System.err.println(">>>FAIL:  AgentDBNGrams starting ...");
          agDBNGrams    = new AgentDBNGrams(Q_03_g, HT_g, prop);
          agDBNGrams.start();
        }
        
        //DESCRIBER
        if (prop.emerging_event_detection && prop.emerging_event_description) {
          Date date_now = new Date();
          if(
            ((date_now.getTime() - agDescriber.last_start_run.getTime()) > prop.describer_last_start_kill_time*1000 ) 
            && agDescriber.getState().equals(State.RUNNABLE)
            && Q_04a.size() > 0){
            System.err.println(">>>FAIL:  AgentDescriber SLOW ... Stopping !!!");
            agDescriber.stop();
            //agDescriber.interrupt();
          }
          
          if(!agDescriber.isAlive()){
            System.err.println(">>>FAIL:  AgentDescriber starting ...");
            agDescriber    = new AgentDescriber(Q_04a, prop, true);
            agDescriber.start();
          }
        }
        
        //DEBUG Repeated HT_Users
        Date now_time = new Date();   
        if((now_time.getTime() - last_debug_date.getTime()) > prop.repeated_user_debug_time*1000 ){
          List<Long> lst_old_users = new ArrayList<>();
          for (long _user_id: HT_R_Users.keySet()) {
            Date _date = HT_R_Users.get(_user_id);
            
            //TODO:  Reemplazar por el ultimo tweet insertado por Q_01.peek(). Cuidado con Null.
            if((now_time.getTime() - _date.getTime()) > prop.repeated_user_time*1000){
              lst_old_users.add(_user_id);
            }
          }
          //System.err.println(">>>DEBUG HT_USERS"+ lst_old_users);
          if(lst_old_users.size() > 0){
            int u_duplicated = lst_old_users.size();
            for (Long _user : lst_old_users) {
              HT_R_Users.remove(_user);
            }
            System.out.println("[Repeated Users] Cleaning  "+ u_duplicated +" users!!");
            last_debug_date = now_time;
          }
        }
        
      }catch(InterruptedException | Error e) {
        e.printStackTrace();
        System.err.println(">>>SYSTEM ERROR");
        System.exit(1);
      } //END TRY-CATCH 
      
    } //WHILE(true)
    ///////////////////////////////////////////////////////////////////////////
    
  } // MAIN
} // CLASS