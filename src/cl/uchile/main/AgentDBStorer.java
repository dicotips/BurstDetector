package cl.uchile.main;

import cl.uchile.dcc.events.twitter.Tweet;
import cl.uchile.dcc.events.twitter.TweetType;
import cl.uchile.dcc.text.RegexAnalyzer;
import cl.uchile.dcc.text.TextAnalyzer;
import cl.uchile.dcc.utils.PropertiesTD;
import cl.uchile.dcc.utils.TUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
* The AgentDBStorer Agent stores into a database all the data from the Twitter
* Stream gathered from the Listener.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0
* @since       2016-08-17
*/
public class AgentDBStorer extends Thread{
  private static BlockingQueue<Tweet> TweetCacheIN;
  private static List<BlockingQueue<Tweet>>TweetCacheOUT;
  private PropertiesTD prop;

  private String str_conn;
  private Connection conn;
  private static long c_tweet   = 0;
  private static long c_tweet_q = 0;
  private static long c_user    = 0;
  private static long c_user_q  = 0;
  private static long c_e_hashtags    = 0;
  private static long c_e_hashtags_q  = 0;
  private static long c_e_urefs       = 0;
  private static long c_e_urefs_q     = 0;
  private static long c_e_urls        = 0;
  private static long c_e_urls_q      = 0;
  private static long c_e_medias      = 0;
  private static long c_e_medias_q    = 0;
  private static long c_ss      = 0;
  private static long c_ss_q    = 0;
  private static long c_ii      = 0;
  private static long c_ii_q    = 0;
  private static TextAnalyzer processor = null;
  private static List<String> eventLanguages;
  
  /**
   * Constructor.
   * <p>
   * Initializes the parameters of the AgentDBStorer and specifies the source of
   * Tweets and the output to the analysis modules.
   * <p>
   * @param QTwIn Queue with the Tweets gathered from the Listener.
   * @param QTwOut Queue with the Tweets sended to the Analysis pipeline.
   * @param _prop Parameters from the _setup.txt file.
   */
  public AgentDBStorer(BlockingQueue<Tweet> QTwIn, List<BlockingQueue<Tweet>> QTwOut, PropertiesTD _prop) {
    prop = _prop; 
    TweetCacheIN = QTwIn;
    TweetCacheOUT = QTwOut;
    
    eventLanguages = Arrays.asList(_prop.event_languages);
    
    processor = new RegexAnalyzer(_prop);
    
  }
  
    /**
  * Body of the Thread: Stores all the tweets information into the database.
  * 
  */
  @Override
  public void run(){
    
    Tweet tweet = null;
      
    while(true){
      if(TweetCacheIN.isEmpty()){
        if(c_tweet>0){
          System.out.println("[AgDBStorer - Sleep 5000]"
                  + "(t:"+c_tweet+", tq:"+c_tweet_q+"]"
                  + "[u:"+c_user+", uq:"+c_user_q+"]"
                  + "[ht:"+c_e_hashtags+", htq:"+c_e_hashtags_q+"]"
                  + "[ur:"+c_e_urefs+", urq:"+c_e_urefs_q+"]"
                  + "[url:"+c_e_urls+", q:"+c_e_urls_q+"]"
                  + "[med:"+c_e_medias+", q:"+c_e_medias_q+"]"
                  + "[ss:"+c_ss+", q:"+c_ss_q+"]"
                  + "[ii:"+c_ii+", q:"+c_ii_q+")");
        }
        try{  Thread.sleep(5000);  }catch(Exception e){ e.printStackTrace(); }

        c_tweet_q = 0;
        c_user_q = 0;
        c_e_hashtags_q = 0;
        c_e_urefs_q = 0;
        c_e_urls_q = 0;
        c_e_medias_q = 0;
        c_ss_q = 0;
        c_ii_q = 0;
        continue;
      }


      Map<String, PreparedStatement> mp_stmt_tweet    = new HashMap<>();
      Map<String, PreparedStatement> mp_stmt_user     = new HashMap<>();
      Map<String, PreparedStatement> mp_stmt_e_hashtags = new HashMap<>();
      Map<String, PreparedStatement> mp_stmt_e_urls   = new HashMap<>();
      Map<String, PreparedStatement> mp_stmt_e_urefs  = new HashMap<>();
      Map<String, PreparedStatement> mp_stmt_e_medias = new HashMap<>();
      Map<String, PreparedStatement> mp_stmt_senti  = new HashMap<>();
      Map<String, PreparedStatement> mp_stmt_inverted_index = new HashMap<>();
      Map<String, PreparedStatement> mp_stmt_report01 = new HashMap<>();
      
      PreparedStatement stmt_tweet = null;
      PreparedStatement stmt_user  = null;
      PreparedStatement stmt_e_hashtags = null;
      PreparedStatement stmt_e_urls = null;
      PreparedStatement stmt_e_urefs = null;
      PreparedStatement stmt_e_medias = null;
      PreparedStatement stmt_senti = null;
      PreparedStatement stmt_inverted_index = null;
      PreparedStatement stmt_report01 = null;

      try {
        //str_conn = String.format("jdbc:mysql://%s:%s/%s?characterSetResults=utf8&useUnicode=true&useLegacyDatetimeCode=false&autoReconnect=true&failOverReadOnly=false&maxReconnects=100&jdbcCompliantTruncation=false",
        str_conn = String.format("jdbc:mysql://%s:%s/%s?characterSetResults=utf8&useUnicode=true&useLegacyDatetimeCode=false&autoReconnect=true&failOverReadOnly=false&maxReconnects=100&jdbcCompliantTruncation=false&useSSL=false&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'",
                                  prop.mysql_server_ip,
                                  prop.mysql_server_port,
                                  prop.mysql_server_database);
        conn = DriverManager.getConnection( str_conn,
                                            prop.mysql_user,
                                            prop.mysql_password);

        conn.setAutoCommit(false);

        Set<String> set_tables = new HashSet<>();
        
        while(!TweetCacheIN.isEmpty()){

          //Forward the tweet to Output Queue
          tweet = TweetCacheIN.poll();
          if(prop.emerging_event_detection
                  //&& prop.repeated_user 
                  && !tweet.isRepeatedUser() 
                  && !tweet.isBlacklisted()
                  && !tweet.getUser().isBlacklisted()){
            for (int i = 0; i < TweetCacheOUT.size(); i++) {
              TweetCacheOUT.get(i).add(tweet);
            }
          }
          //if(tweet.getType() == TweetType.TEMP)
          //  continue;
          
          // Creating tables 
          String tbl_date = tweet.getDownloadedAt_String().replace("-", "").substring(0, 8);
          set_tables.add(tbl_date);

          //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
          if(!mp_stmt_tweet.containsKey(tbl_date)){
            mp_stmt_tweet.put(tbl_date, conn.prepareStatement("insert into "+ tbl_date +"_tweets "
                    + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE counter = counter + 1;"));
            
            mp_stmt_user.put(tbl_date, conn.prepareStatement("insert into "+ tbl_date +"_users "
                    + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE screen_name=screen_name;"));
            
            mp_stmt_e_hashtags.put(tbl_date, conn.prepareStatement("insert into "+ tbl_date +"_e_hashtags "
                    + "values(?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE idx_start=idx_start;"));
            
            mp_stmt_e_urefs.put(tbl_date, conn.prepareStatement("insert into "+ tbl_date +"_e_urefs "
                    + "values(?,?,?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE idx_start=idx_start;"));
            
            mp_stmt_e_urls.put(tbl_date, conn.prepareStatement("insert into "+ tbl_date +"_e_urls "
                    + "values(?,?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE idx_start=idx_start;"));
            
            mp_stmt_e_medias.put(tbl_date, conn.prepareStatement("insert into "+ tbl_date +"_e_medias "
                    + "values(?,?,?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE type=type;"));
            
            mp_stmt_senti.put(tbl_date, conn.prepareStatement("insert into "+ tbl_date +"_senti "
                    + "values(?,?,?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE ss_polarity=ss_polarity;"));
            
            mp_stmt_report01.put(tbl_date, conn.prepareStatement("insert into v_"+prop.keywords_name+" "
                    + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE counter = counter + 1"));
            
            mp_stmt_inverted_index.put(tbl_date, conn.prepareStatement("insert into "+ tbl_date +"_inverted_index "
                    + "values(?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE term=term"));
            
          }
          
          // Insertion of v_keyword_name into DB
          stmt_report01 = mp_stmt_report01.get(tbl_date);
          TUtils.insertStatement_report01(stmt_report01, tweet);
          stmt_report01.addBatch();
          
          // Insertion of Tweets into DB
          stmt_tweet = mp_stmt_tweet.get(tbl_date);
          TUtils.insertStatement_Tweets(stmt_tweet, tweet);
          stmt_tweet.addBatch();

          // Insertion of Users into DB
          stmt_user = mp_stmt_user.get(tbl_date);
          TUtils.insertStatement_Users(stmt_user, tweet);
          stmt_user.addBatch();
          
          // Insertion of eHashTags into DB
          stmt_e_hashtags = mp_stmt_e_hashtags.get(tbl_date);
          TUtils.insertStatement_eHashTags(stmt_e_hashtags, tweet);
          
          // Insertion of eUserRefs into DB
          stmt_e_urefs = mp_stmt_e_urefs.get(tbl_date);
          TUtils.insertStatemnet_eUserRefs(stmt_e_urefs, tweet);
          
          // Insertion of eURLs into DB
          stmt_e_urls = mp_stmt_e_urls.get(tbl_date);
          TUtils.insertStatement_eURLs(stmt_e_urls, tweet);
          
          // Insertion of eMedias into DB
          stmt_e_medias = mp_stmt_e_medias.get(tbl_date);
          TUtils.insertStatement_eMedias(stmt_e_medias, tweet);          
          
          // Insertion of Senti into DB
          if(tweet.getSentiment() != null){
            stmt_senti = mp_stmt_senti.get(tbl_date);
            TUtils.insertStatement_Senti(stmt_senti, tweet);
            stmt_senti.addBatch();
          }
          
          //Insertion of Inverted_Index
          if(eventLanguages.contains(tweet.getLanguage().toUpperCase()) || eventLanguages.contains("ALL")){
            stmt_inverted_index = mp_stmt_inverted_index.get(tbl_date);
            TUtils.insertStatement_InvertedIndex(stmt_inverted_index, 
                    processor.analyzeText(tweet.getText(), tweet.getLanguage()).keySet(),
                    tweet);
          }
          
          //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        }

        //Create Tables
        for (String tbl_date_item : set_tables) {
          TUtils.createTable_Tweets(tbl_date_item, conn);
          TUtils.createTable_Users(tbl_date_item, conn);
          TUtils.createTable_eHashTags(tbl_date_item, conn);
          TUtils.createTable_eUserRefs(tbl_date_item, conn);
          TUtils.createTable_eURLs    (tbl_date_item, conn);
          TUtils.createTable_eMedias  (tbl_date_item, conn);
          TUtils.createTable_Senti    (tbl_date_item, conn);
          TUtils.createTable_InvertedIndex(tbl_date_item, conn);
          TUtils.createTable_report01(conn, prop);
        }
        set_tables.clear();

        //execute batches
        for (String set_table : mp_stmt_tweet.keySet()) {
          stmt_tweet = mp_stmt_tweet.get(set_table);
          if(stmt_tweet != null){
            int[] inserts = stmt_tweet.executeBatch();
            int count = (int) Arrays.stream(inserts).filter(x -> x >= 0).sum();
            c_tweet   += count;
            c_tweet_q += count;
          }
        }
        //System.out.println("[AgDBStorer - Tweets (1/9)](t:"+c_tweet+", tq:"+c_tweet_q+")");
        
        for (String set_table : mp_stmt_user.keySet()) {
          stmt_user = mp_stmt_user.get(set_table);
          if(stmt_user != null){
            int[] inserts = stmt_user.executeBatch();
            int count = (int) Arrays.stream(inserts).filter(x -> x >= 0).sum();
            c_user   += count;
            c_user_q += count;
          }
        }
        //System.out.println("[AgDBStorer - Users (2/9)](t:"+c_user+", tq:"+c_user_q+")");
        
        for (String set_table : mp_stmt_e_hashtags.keySet()) {
          stmt_e_hashtags = mp_stmt_e_hashtags.get(set_table);
          if(stmt_e_hashtags != null){
            int[] inserts = stmt_e_hashtags.executeBatch();
            int count = (int) Arrays.stream(inserts).filter(x -> x >= 0).sum();
            c_e_hashtags   += count;
            c_e_hashtags_q += count;
          }
        }
        //System.out.println("[AgDBStorer - e_HashTags (3/9)](t:"+c_e_hashtags+", tq:"+c_e_hashtags_q+")");
        
        for (String set_table : mp_stmt_e_urefs.keySet()) {
          stmt_e_urefs = mp_stmt_e_urefs.get(set_table);
          if(stmt_e_urefs != null){
            int[] inserts = stmt_e_urefs.executeBatch();
            int count = (int) Arrays.stream(inserts).filter(x -> x >= 0).sum();
            c_e_urefs   += count;
            c_e_urefs_q += count;
          }
        }
        //System.out.println("[AgDBStorer - e_URefs (4/9)](t:"+c_e_urefs+", tq:"+c_e_urefs_q+")");
        
        for (String set_table : mp_stmt_e_urls.keySet()) {
          stmt_e_urls = mp_stmt_e_urls.get(set_table);
          if(stmt_e_urls != null){
            int[] inserts = stmt_e_urls.executeBatch();
            int count = (int) Arrays.stream(inserts).filter(x -> x >= 0).sum();
            c_e_urls   += count;
            c_e_urls_q += count;
          }
        }
        //System.out.println("[AgDBStorer - e_URLs (5/9)](t:"+c_e_urls+", tq:"+c_e_urls_q+")");
        
        for (String set_table : mp_stmt_e_medias.keySet()) {
          stmt_e_medias = mp_stmt_e_medias.get(set_table);
          if(stmt_e_medias != null){
            int[] inserts = stmt_e_medias.executeBatch();
            int count = (int) Arrays.stream(inserts).filter(x -> x >= 0).sum();
            c_e_medias   += count;
            c_e_medias_q += count;
          }
        }
        //System.out.println("[AgDBStorer - e_Medias (6/9)](t:"+c_e_medias+", tq:"+c_e_medias_q+")");
        
        for (String set_table : mp_stmt_senti.keySet()) {
          stmt_senti = mp_stmt_senti.get(set_table);
          if(stmt_senti != null){
            int[] inserts = stmt_senti.executeBatch();
            int count = (int) Arrays.stream(inserts).filter(x -> x >= 0).sum();
            c_ss   += count;
            c_ss_q += count;
          }
        }
        //System.out.println("[AgDBStorer - Senti (7/9)](t:"+c_ss+", tq:"+c_ss_q+")");
        
        for (String set_table : mp_stmt_inverted_index.keySet()) {
          stmt_inverted_index = mp_stmt_inverted_index.get(set_table);
          if(stmt_inverted_index != null){
            int[] inserts = stmt_inverted_index.executeBatch();
            int count = (int) Arrays.stream(inserts).filter(x -> x >= 0).sum();
            c_ii   += count;
            c_ii_q += count;
          }
        }
        //System.out.println("[AgDBStorer - InvertedIndex (8/9)](t:"+c_ii+", tq:"+c_ii_q+")");
        
        for (String set_table : mp_stmt_report01.keySet()) {
          stmt_report01 = mp_stmt_report01.get(set_table);
          if(stmt_report01 != null){
            stmt_report01.executeBatch();
          }
        }
        //System.out.println("[AgDBStorer - Report01 (9/9)]");
        

        mp_stmt_tweet.clear();
        mp_stmt_user.clear();
        mp_stmt_e_hashtags.clear();
        mp_stmt_e_urls.clear();
        mp_stmt_e_urefs.clear();
        mp_stmt_e_medias.clear();
        mp_stmt_inverted_index.clear();
        mp_stmt_senti.clear();
        mp_stmt_report01.clear();
        
        conn.commit();
        conn.setAutoCommit(true);
        
        System.out.println("[AgDBStorer]"
                  + "(t:"+c_tweet+", tq:"+c_tweet_q+"]"
                  + "[u:"+c_user+", uq:"+c_user_q+"]"
                  + "[ht:"+c_e_hashtags+", htq:"+c_e_hashtags_q+"]"
                  + "[ur:"+c_e_urefs+", urq:"+c_e_urefs_q+"]"
                  + "[url:"+c_e_urls+", q:"+c_e_urls_q+"]"
                  + "[med:"+c_e_medias+", q:"+c_e_medias_q+"]"
                  + "[ss:"+c_ss+", q:"+c_ss_q+"]"
                  + "[ii:"+c_ii+", q:"+c_ii_q+")");

      }
      catch (SQLException ex) {
        ex.printStackTrace();
        System.err.println(AgentDBStorer.class.getName());
        System.err.println(ex.getMessage());
        System.err.println(ex.getSQLState());
      }
      catch (Exception ex) {
        ex.printStackTrace();
        System.err.println(AgentDBStorer.class.getName());
        System.err.println(ex.getMessage());
      }
      finally{
        tweet = null;

        try {
          if(stmt_tweet != null)
             stmt_tweet.close();
          if(stmt_user != null)
             stmt_user.close();
          if(stmt_e_hashtags != null)
             stmt_e_hashtags.close();
          if(stmt_e_urefs != null)
             stmt_e_urefs.close();
          if(stmt_e_urls != null)
             stmt_e_urls.close();
          if(stmt_e_medias != null)
             stmt_e_medias.close();
          if(stmt_inverted_index != null)
             stmt_inverted_index.close();
          if(stmt_senti != null)
             stmt_senti.close();
          if(stmt_report01 != null)
             stmt_report01.close();
          
          for (String stmt_str : mp_stmt_tweet.keySet()) {
            PreparedStatement stmt = mp_stmt_tweet.get(stmt_str);
            if(stmt != null)
              stmt.close();
          }
          for (String stmt_str : mp_stmt_user.keySet()) {
            PreparedStatement stmt = mp_stmt_user.get(stmt_str);
            if(stmt != null)
              stmt.close();
          }
          for (String stmt_str : mp_stmt_e_hashtags.keySet()) {
            PreparedStatement stmt = mp_stmt_e_hashtags.get(stmt_str);
            if(stmt != null)
              stmt.close();
          }
          for (String stmt_str : mp_stmt_e_urefs.keySet()) {
            PreparedStatement stmt = mp_stmt_e_urefs.get(stmt_str);
            if(stmt != null)
              stmt.close();
          }
          for (String stmt_str : mp_stmt_e_urls.keySet()) {
            PreparedStatement stmt = mp_stmt_e_urls.get(stmt_str);
            if(stmt != null)
              stmt.close();
          }
          for (String stmt_str : mp_stmt_e_medias.keySet()) {
            PreparedStatement stmt = mp_stmt_e_medias.get(stmt_str);
            if(stmt != null)
              stmt.close();
          }
          for (String stmt_str : mp_stmt_senti.keySet()) {
            PreparedStatement stmt = mp_stmt_senti.get(stmt_str);
            if(stmt != null)
              stmt.close();
          }
          for (String stmt_str : mp_stmt_inverted_index.keySet()) {
            PreparedStatement stmt = mp_stmt_inverted_index.get(stmt_str);
            if(stmt != null)
              stmt.close();
          }
          for (String stmt_str : mp_stmt_report01.keySet()) {
            PreparedStatement stmt = mp_stmt_report01.get(stmt_str);
            if(stmt != null)
              stmt.close();
          }
          
          if(conn != null)
            conn.close();

        } catch (SQLException ex) {
          ex.printStackTrace();
        }
        mp_stmt_tweet.clear();
        mp_stmt_user.clear();
        mp_stmt_e_hashtags.clear();
        mp_stmt_e_urls.clear();
        mp_stmt_e_urefs.clear();
        mp_stmt_e_medias.clear();
        mp_stmt_inverted_index.clear();
        mp_stmt_senti.clear();
        mp_stmt_report01.clear();
        
      }
    }

  }
}
