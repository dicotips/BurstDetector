package cl.uchile.main;

import cl.uchile.dcc.events.detection.EventBT;
import cl.uchile.dcc.ml.Apriori;
import cl.uchile.dcc.ml.BurstyGenerator;
import cl.uchile.dcc.ml.FPGrowthGenerator;
import cl.uchile.dcc.utils.PropertiesTD;
import cl.uchile.dcc.utils.QueueSerializer;
import cl.uchile.dcc.utils.TUtils;
import cl.uchile.dcc.utils.TUtilsDescriber;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
* The AgentDescriber Agent stores into a database all descriptions with keywords
* generated from the BustDetector. It uses Frequent Itemsets Algorithms.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0
* @since       2016-08-17
*/
public class AgentDescriber extends Thread{
  
  private BlockingQueue<EventBT> EventCacheIN;

  private PropertiesTD prop;
  
  private boolean was_killed;
  
  private String desc_algorithm;

  /** Last time the process started analyzing an event   */
  public Date last_start_run;
  
  /**
  * Constructor.
  * <p>
  * Initializes the AgentDescriber with the parameters of _setu.txt file and 
  * specifies the source of bursts detected.
  * <p>
   * @param QTweetsIN Queue with the bursts detected.
   * @param PropTD Parameters from the _setup.txt file.
   */
  public AgentDescriber(BlockingQueue<EventBT> QTweetsIN,
                        PropertiesTD PropTD,
                        boolean wasKilled) {
    prop = PropTD;
    desc_algorithm = prop.describer_algorithm;
    EventCacheIN  = QTweetsIN;
    was_killed = wasKilled;
    last_start_run = new Date();
    
    if(was_killed)
      desc_algorithm = "bursty";
  }
  
  /**
  * Body of the Thread: retrieves the tweets related to the bursts detected and 
  * cached in EventCacheIN and finds the most frequent itemsets of terms.
  */
  @Override
  public void run(){
    try{
      while(true){
        if(EventCacheIN.isEmpty()){
          System.out.println("[AgDescriber - sleep 5000] ["+ EventCacheIN.size() +"]");
          sleep(5000);
          
          last_start_run = new Date();
          if(was_killed){
            was_killed = false;
            desc_algorithm = prop.describer_algorithm;
          }
          continue;
        }
        
          
        //EventBT event = EventCacheIN.poll();
        EventBT event = EventCacheIN.peek();
        last_start_run = new Date();

        System.out.println((char)27 + "[33;40mDESCRIBER START Loading Tweets ("
                + desc_algorithm.toUpperCase()+")["
                + TUtils.Date_Formatter(event.getDateIni()) + ","
                + TUtils.Date_Formatter(event.getDateMid()) + ","
                + TUtils.Date_Formatter(event.getDateEnd()) + "] ... "+ (char)27 + "[0m");

        List<Map.Entry<Set<String>, Double>> result_kw = new ArrayList<>();
        List<Map.Entry<Set<String>, Double>> result_eq = new ArrayList<>();
        List<Map.Entry<Set<String>, Double>> result_lang = new ArrayList<>();
        List<Map.Entry<Set<String>, Double>> result_ss_pos = new ArrayList<>();
        List<Map.Entry<Set<String>, Double>> result_ss_neg = new ArrayList<>();
        List<Map.Entry<Set<String>, Double>> result_country_txt = new ArrayList<>();
        List<Map.Entry<Set<String>, Double>> result_country_geo = new ArrayList<>();
        List<Map.Entry<Set<String>, Double>> result_country_usr = new ArrayList<>();

        //DESCRIBER KEYWORDS
        if (prop.track_signal_bursty_keywords || prop.track_signal_keywords) {
          System.out.print((char)27 +"[33;40m>>|"+ (char)27 + "[0m");
          List<Set<String>> itemsetList_kw = TUtilsDescriber.LoadKeywordDescriber(event, prop); 
          System.out.print((char)27 +"[33;40m>>("+itemsetList_kw.size()+")|"+ (char)27 + "[0m");
          TUtilsDescriber.cleanZiptTail(itemsetList_kw, prop);
          System.out.println((char)27 + "[33;40m>>DESCRIBER [CurrentDateTime '"+TUtils.Date_Formatter2(new Date())+"'][WindowTime '"+event.toString()+"'](n_load_kw: "+itemsetList_kw.size()+")"+ (char)27 + "[0m");
          switch(desc_algorithm){
            case "apriori":   result_kw = Apriori.frequenItemSets(itemsetList_kw);
                              break;
            case "fpgrowth":  result_kw = FPGrowthGenerator.frequenItemSets(itemsetList_kw);
                              break; 
            default:
                result_kw = BurstyGenerator.frequenItemSets(itemsetList_kw, prop);
                break;
          }
        }
        last_start_run = new Date();

        //DESCRIBER KEYWORD_NAME
        if (prop.track_signal_keywords) {
          System.out.print((char)27 +"[33;40m>>|"+ (char)27 + "[0m");
          List<Set<String>> itemsetList_eq = TUtilsDescriber.LoadKeywordNameDescriber(event, prop);
          System.out.print((char)27 +"[33;40m>>("+itemsetList_eq.size()+")|"+ (char)27 + "[0m");
          TUtilsDescriber.cleanZiptTail(itemsetList_eq, prop);
          System.out.println((char)27 + "[33;40m>>DESCRIBER [CurrentDateTime '"+TUtils.Date_Formatter2(new Date())+"'][WindowTime '"+event.toString()+"'](n_load_eq: "+itemsetList_eq.size()+")"+ (char)27 + "[0m");
          switch(desc_algorithm){
            case "apriori":   result_eq = Apriori.frequenItemSets(itemsetList_eq);
                              break;
            case "fpgrowth":  result_eq = FPGrowthGenerator.frequenItemSets(itemsetList_eq);
                              break;
            default:
                result_eq = BurstyGenerator.frequenItemSets(itemsetList_eq, prop);
                break;
          }
        }
        last_start_run = new Date();

        //DESCRIBER LANGUAGE
        if (prop.track_signal_language) {
          System.out.print((char)27 +"[33;40m>>|"+ (char)27 + "[0m");
          List<Set<String>> itemsetList_lang = TUtilsDescriber.LoadLanguageDescriber(event, prop);
          System.out.print((char)27 +"[33;40m>>("+itemsetList_lang.size()+")|"+ (char)27 + "[0m");
          TUtilsDescriber.cleanZiptTail(itemsetList_lang, prop);
          System.out.println((char)27 + "[33;40m>>DESCRIBER [CurrentDateTime '"+TUtils.Date_Formatter2(new Date())+"'][WindowTime '"+event.toString()+"'](n_load_lang: "+itemsetList_lang.size()+")"+ (char)27 + "[0m");
          switch(desc_algorithm){
            case "apriori":   result_lang = Apriori.frequenItemSets(itemsetList_lang);
                              break;
            case "fpgrowth":  result_lang = FPGrowthGenerator.frequenItemSets(itemsetList_lang);
                              break;
            default:
                result_lang = BurstyGenerator.frequenItemSets(itemsetList_lang, prop);
                break;
          }
        }
        last_start_run = new Date();

        //DESCRIBER POSITIVE SENTIMENT
        if (prop.track_signal_sentiment) {
          System.out.print((char)27 +"[33;40m>>|"+ (char)27 + "[0m");
          List<Set<String>> itemsetList_ss_pos = TUtilsDescriber.LoadSSPositiveDescriber(event, prop);
          System.out.print((char)27 +"[33;40m>>("+itemsetList_ss_pos.size()+")|"+ (char)27 + "[0m");
          TUtilsDescriber.cleanZiptTail(itemsetList_ss_pos, prop);
          System.out.println((char)27 + "[33;40m>>DESCRIBER [CurrentDateTime '"+TUtils.Date_Formatter2(new Date())+"'][WindowTime '"+event.toString()+"'](n_load_Spos: "+itemsetList_ss_pos.size()+")"+ (char)27 + "[0m");
          switch(desc_algorithm){
            case "apriori":   result_ss_pos = Apriori.frequenItemSets(itemsetList_ss_pos);
                              break;
            case "fpgrowth":  result_ss_pos = FPGrowthGenerator.frequenItemSets(itemsetList_ss_pos);
                              break;
            default:
                result_ss_pos = BurstyGenerator.frequenItemSets(itemsetList_ss_pos, prop);
                break;
          }
        }
        last_start_run = new Date();

        //DESCRIBER NEGATIVE SENTIMENT
        if (prop.track_signal_sentiment) {
          System.out.print((char)27 +"[33;40m>>|"+ (char)27 + "[0m");
          List<Set<String>> itemsetList_ss_neg = TUtilsDescriber.LoadSSNegativeDescriber(event, prop);
          System.out.print((char)27 +"[33;40m>>("+itemsetList_ss_neg.size()+")|"+ (char)27 + "[0m");
          TUtilsDescriber.cleanZiptTail(itemsetList_ss_neg, prop);
          System.out.println((char)27 + "[33;40m>>DESCRIBER [CurrentDateTime '"+TUtils.Date_Formatter2(new Date())+"'][WindowTime '"+event.toString()+"'](n_load_Sneg: "+itemsetList_ss_neg.size()+")"+ (char)27 + "[0m");
          switch(desc_algorithm){
            case "apriori":   result_ss_neg = Apriori.frequenItemSets(itemsetList_ss_neg);
                              break;
            case "fpgrowth":  result_ss_neg = FPGrowthGenerator.frequenItemSets(itemsetList_ss_neg);
                              break;
            default:
                result_ss_neg = BurstyGenerator.frequenItemSets(itemsetList_ss_neg, prop);
                break;
          }
        }
        last_start_run = new Date();

        //DESCRIBER COUNTRY TXT
        if (prop.track_signal_geo_text) {
          System.out.print((char)27 +"[33;40m>>|"+ (char)27 + "[0m");
          List<Set<String>> itemsetList_country_txt = TUtilsDescriber.LoadCountryTxtDescriber(event, prop);
          System.out.print((char)27 +"[33;40m>>("+itemsetList_country_txt.size()+")|"+ (char)27 + "[0m");
          TUtilsDescriber.cleanZiptTail(itemsetList_country_txt, prop);
          System.out.println((char)27 + "[33;40m>>DESCRIBER [CurrentDateTime '"+TUtils.Date_Formatter2(new Date())+"'][WindowTime '"+event.toString()+"'](n_load_Ctxt: "+itemsetList_country_txt.size()+")"+ (char)27 + "[0m");
          switch(desc_algorithm){
            case "apriori":   result_country_txt = Apriori.frequenItemSets(itemsetList_country_txt);
                              break;
            case "fpgrowth":  result_country_txt = FPGrowthGenerator.frequenItemSets(itemsetList_country_txt);
                              break;
            default:
              result_country_txt = BurstyGenerator.frequenItemSets(itemsetList_country_txt, prop);
              break;
          }
        }
        last_start_run = new Date();

        //DESCRIBER COUNTRY USER
        if (prop.track_signal_geo_user){
          System.out.print((char)27 +"[33;40m>>|"+ (char)27 + "[0m");
          List<Set<String>> itemsetList_country_usr = TUtilsDescriber.LoadCountryUserDescriber(event, prop);
          System.out.print((char)27 +"[33;40m>>("+itemsetList_country_usr.size()+")|"+ (char)27 + "[0m");
          TUtilsDescriber.cleanZiptTail(itemsetList_country_usr, prop);
          System.out.println((char)27 + "[33;40m>>DESCRIBER [CurrentDateTime '"+TUtils.Date_Formatter2(new Date())+"'][WindowTime '"+event.toString()+"'](n_load_Cusr: "+itemsetList_country_usr.size()+")"+ (char)27 + "[0m");
          switch(desc_algorithm){
            case "apriori":   result_country_usr = Apriori.frequenItemSets(itemsetList_country_usr);
                              break;
            case "fpgrowth":  result_country_usr = FPGrowthGenerator.frequenItemSets(itemsetList_country_usr);
                              break;
            default:
                result_country_usr = BurstyGenerator.frequenItemSets(itemsetList_country_usr, prop);
                break;
          }
        }
        last_start_run = new Date();

        //DESCRIBER COUNTRY GEO
        if (prop.track_signal_geo_location) {
          System.out.print((char)27 +"[33;40m>>|"+ (char)27 + "[0m");
          List<Set<String>> itemsetList_country_geo = TUtilsDescriber.LoadCountryGeoDescriber(event, prop);
          System.out.print((char)27 +"[33;40m>>("+itemsetList_country_geo.size()+")|"+ (char)27 + "[0m");
          TUtilsDescriber.cleanZiptTail(itemsetList_country_geo, prop);
          System.out.println((char)27 + "[33;40m>>DESCRIBER [CurrentDateTime '"+TUtils.Date_Formatter2(new Date())+"'][WindowTime '"+event.toString()+"'](n_load_Cgeo: "+itemsetList_country_geo.size()+")"+ (char)27 + "[0m");            
          switch(desc_algorithm){
            case "apriori":   result_country_geo = Apriori.frequenItemSets(itemsetList_country_geo);
                              break;
            case "fpgrowth":  result_country_geo = FPGrowthGenerator.frequenItemSets(itemsetList_country_geo);
                              break;  
            default:
                result_country_geo = BurstyGenerator.frequenItemSets(itemsetList_country_geo, prop);
                break;
          }
        }
        last_start_run = new Date();

        //STORING DATA INTO THE DATABASE
        String str_conn;
        Connection conn = null;
        PreparedStatement stmt = null;

        str_conn = String.format("jdbc:mysql://%s:%s/%s?characterSetResults=utf8&useUnicode=true&useLegacyDatetimeCode=false&autoReconnect=true&failOverReadOnly=false&maxReconnects=100&jdbcCompliantTruncation=false&useSSL=false&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'",
                                  prop.mysql_server_ip,
                                  prop.mysql_server_port,
                                  prop.mysql_server_database);
        conn = DriverManager.getConnection( str_conn,
                                            prop.mysql_user,
                                            prop.mysql_password);

        conn.setAutoCommit(false);
        String table_mid = TUtils.Date_Formatter(event.getDateMid()).substring(0, 8);

        //Create table DESCRIBE EVENT
        TUtilsDescriber.createTable_reportEvent(table_mid, conn);

        //INSERT DATA INTO TABLE
        stmt = conn.prepareStatement("insert into "+table_mid+"_event_desc values(?,?,?,?,?,?,?,?)");
        TUtilsDescriber.insertStatement_reportEvent(stmt, event, result_kw, result_eq, result_lang, result_ss_pos, result_ss_neg, result_country_txt, result_country_usr, result_country_geo, prop);

//        event = null;
//        stmt, event, result_kw, result_eq, result_lang, result_ss_pos, result_ss_neg, result_country_txt, result_country_usr, result_country_geo
        
        
        //COMMIT
        int[] inserts = stmt.executeBatch();

        conn.commit();
        conn.setAutoCommit(true);

        System.out.println((char)27 + "[33;40mDESCRIBER [CurrentDateTime '"+TUtils.Date_Formatter2(new Date())+"'][WindowTime '"+event.toString()+"'](n_desc: "+inserts.length+")"+ (char)27 + "[0m");



        // Removes the head of the Queue.
        EventCacheIN.poll();

        //SERIALIZATION OF THE QUEUE
        if(prop.events_serialize_store){
          String aux_file = prop.events_serialize_path+"_aux";
          String file = prop.events_serialize_path;

          Path aux_file_path  = Paths.get(aux_file);
          Path file_path      = Paths.get(file);

          QueueSerializer.serizalizeQueue(EventCacheIN, aux_file);

          try {
            Files.move(aux_file_path, file_path,
                    StandardCopyOption.REPLACE_EXISTING);
          } catch (IOException e) {
            System.err.println("[Serializer-EventBT] Error saving backup!!");
            //e.printStackTrace();
          }

          // Returning the value when it was killed
          if(was_killed){
            was_killed = false;
            desc_algorithm = prop.describer_algorithm;
          }

        }
        
        //**********************************************************************
        //Garbage collector and releasing variables.
        //Closing DATABASE Statements.
        //event
        result_kw.clear();    result_kw = null;
        result_eq.clear();    result_eq = null;
        result_lang.clear();  result_lang = null;
        result_ss_pos.clear();  result_ss_pos = null;
        result_ss_neg.clear();  result_ss_neg = null;
        result_country_txt.clear(); result_country_txt = null;
        result_country_usr.clear(); result_country_usr = null;
        result_country_geo.clear(); result_country_geo = null;
        
        //Closing DATABASE Connections
        stmt.close();
        stmt = null;
        conn.close();
        conn = null;
        
        
        System.gc();
        
      }// END WHILE (true)
    }catch(Exception e){
      System.err.println("DESCRIBER STOPED");
      System.err.println("DESCRIBER ERROR: " + e.toString());
      System.err.println("DESCRIBER ERROR: " + Arrays.toString(e.getStackTrace()));
      
      Thread.currentThread().interrupt();
      
    }
  }

}