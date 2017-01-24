package cl.uchile.main;

import cl.uchile.dcc.utils.PropertiesTD;
import cl.uchile.dcc.utils.TUtils;
import cl.uchile.dcc.text.NGramRow;
import cl.uchile.dcc.utils.NGramsBag;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
* The AgentDBNGrams Agent stores into a database all NGrams generated prom NGramBagCacheIN.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0
* @since       2016-08-17
*/
public class AgentDBNGrams extends Thread{
  
  private static BlockingQueue<NGramsBag>    NGramBagCacheIN;
  private static ConcurrentHashMap<String, NGramRow> HashTableOUT;

  private String str_conn;
  private Connection conn;
  
  private PropertiesTD prop;

  /**
  * Body of the Thread: Counts terms and stores it into the database.
  * 
  */
  @Override
  public void run(){
    try{
      while(true){
        
        if(NGramBagCacheIN.isEmpty()){
          // LOG reporting info and sleeps for 60 seconds if the input is empty.
          System.out.println("NGram PROCESSOR [SLEEP 3000] ["+ NGramBagCacheIN.size()
                                                         +"|"+ HashTableOUT.size() +"]");
          sleep(60000);
        }
        else{
          
          NGramsBag GBag = NGramBagCacheIN.poll();
          
          //Check if the Store_nGrams Flag is activated.
          if(!prop.store_ngrams)
            continue;
          
          Date BagTimeStamp = GBag.getTimeStamp();
          Queue<NGramRow> NGramBag = GBag.getQueueNGrams();
          
          //Terms counting.
          for(NGramRow ngram : NGramBag){

            if(!HashTableOUT.containsKey(ngram._id)){ // Inserts a new NGram
              HashTableOUT.put(ngram._id, ngram);
            }
            else{
              NGramRow GRow = HashTableOUT.get(ngram._id);
              GRow._freq_term += ngram._freq_term;
              GRow._freq_tweet++;
              HashTableOUT.put(ngram._id, GRow);
            }
          }
          
          // Storing all Ngrams counting stored in the hashtable into a database.
          StoreNGramHashTable(BagTimeStamp, HashTableOUT);
          System.out.println((char)27 + "[36;40mNGram PROCESSOR [CurrentDateTime '"+TUtils.Date_Formatter(new Date())+"'][WindowTime '"+TUtils.Date_Formatter(BagTimeStamp)+"'][HashTableSize " + HashTableOUT.size() + "]"+ (char)27 + "[0m");
        }
      }
    }catch(Exception e){
      
      System.err.println("NGram PROCESSOR ERROR: " + e.toString());
      System.err.println("NGram PROCESSOR ERROR: " + Arrays.toString(e.getStackTrace()));
      
    }
  }

  
  /**
  * Constructor.
  * <p>
  * Loads data of counting terms from the database and stores it in the hashtable
  * <p>
  *
  * @param  QFeaturesIn Queue with bags of ngrams per window.
  * @param  HTOut HashTable that stores the counting and details about the term.
  * @param  PropTD Parameters from the _setup.txt file.
  */
  public AgentDBNGrams(BlockingQueue<NGramsBag> QFeaturesIn, ConcurrentHashMap<String, NGramRow> HTOut, PropertiesTD PropTD) {
    NGramBagCacheIN = QFeaturesIn;
    HashTableOUT = HTOut;
    prop = PropTD;

    try {
      str_conn = String.format("jdbc:mysql://%s:%s/%s?characterSetResults=utf8&useUnicode=true&useLegacyDatetimeCode=false&autoReconnect=true&failOverReadOnly=false&maxReconnects=100&jdbcCompliantTruncation=false&useSSL=false&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'",
                                PropTD.mysql_server_ip,
                                PropTD.mysql_server_port,
                                PropTD.mysql_server_database);
      conn = DriverManager.getConnection( str_conn,
                                          PropTD.mysql_user,
                                          PropTD.mysql_password);
      
      TUtils.createTable_NGrams(conn);
      String query = "SELECT * FROM v_ngrams";
 
      // Load Data from database to HashTable
      System.out.println((char)27 + "[36;40mNGram PROCESSOR START Loading... "+ (char)27 + "[0m");
      Statement st = conn.createStatement(); 
      ResultSet rs = st.executeQuery(query);
      
      while (rs.next())
      { 
        String id    = rs.getString("id");
        String term  = rs.getString("term");
        int    n     = rs.getInt("n");
        boolean rt   = rs.getBoolean("rt");
        String lang_tweet = rs.getString("lang_tweet");
        long freq_term    = rs.getLong("freq_term");
        long freq_tweet   = rs.getLong("freq_tweet");
        
        NGramRow gramRow = new NGramRow(term, n, rt, lang_tweet, freq_term, freq_tweet);
        HashTableOUT.put(term, gramRow);

      }
      st.close();
      System.out.println((char)27 + "[36;40mNGram PROCESSOR FINISHED Loading.  "+ (char)27 + "[0m");
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      System.err.println(AgentDBStorer.class.getName());
      System.err.println(ex.getMessage());
      System.err.println(ex.getSQLState());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally{
      if(conn != null)
        try{ conn.close();} catch(Exception e){ e.printStackTrace(); }
    }

  }
  
  /**
  * Stores Ngram from HashTable into a Database.
  *
  * @param  BagTimeStamp Timestamp of the window containing the n-grams.
  * @param  HashTableOUT HashTable that stores the counting and details about the term.
  */
  public void StoreNGramHashTable(Date BagTimeStamp, ConcurrentHashMap<String, NGramRow> HashTableOUT) {
    
    try{
      str_conn = String.format("jdbc:mysql://%s:%s/%s?characterSetResults=utf8&useUnicode=true&useLegacyDatetimeCode=false&autoReconnect=true&failOverReadOnly=false&maxReconnects=100&jdbcCompliantTruncation=false&useSSL=false&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'",
                                prop.mysql_server_ip,
                                prop.mysql_server_port,
                                prop.mysql_server_database);
      conn = DriverManager.getConnection( str_conn,
                                          prop.mysql_user,
                                          prop.mysql_password);
      
      TUtils.createTable_NGrams(conn);
      
      PreparedStatement stmt = conn.prepareStatement("insert into v_ngrams values(?,?,?,?,?,?,?) "
                             + "ON DUPLICATE KEY UPDATE term=term;");
      
      for (String ngram : HashTableOUT.keySet()) {
        TUtils.insertStatement_NGrams(stmt, HashTableOUT.get(ngram));
        stmt.addBatch();
      }
      stmt.executeBatch();
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      System.err.println(AgentDBStorer.class.getName());
      System.err.println(ex.getMessage());
      System.err.println(ex.getSQLState());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally{
      if(conn != null)
        try{ conn.close(); }catch(Exception e){ e.printStackTrace(); }
    }
    

  }  
  
}