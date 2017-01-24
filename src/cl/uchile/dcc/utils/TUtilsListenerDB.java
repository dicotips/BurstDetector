package cl.uchile.dcc.utils;

import cl.uchile.dcc.events.detection.WindowRow;
import cl.uchile.dcc.events.twitter.EntityHashTag;
import cl.uchile.dcc.events.twitter.EntityMedia;
import cl.uchile.dcc.events.twitter.EntityURL;
import cl.uchile.dcc.events.twitter.EntityUserRef;
import cl.uchile.dcc.events.twitter.TUser;
import cl.uchile.dcc.events.twitter.Tweet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import twitter4j.MediaEntity;

/**
* This class contains staic methods to format tweets and load them from a 
* database.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public class TUtilsListenerDB {
  
  /**
   * Generate the List of dates and times separated by windods
   * @param prop
   * @return
   * @throws Exception 
   */
  public static List<Date> generateTableDates(PropertiesTD prop) {
    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

    int day_seconds = 24*60*60;
    //prop.event_window_size = 1200;
    List<Date> dates = new ArrayList<>();
    
    try{
      for (String date_e : prop.mysql_src_date_list) {
        Date date = isoFormat.parse(date_e+" 00:00:00");
        for(int i = 0; i < (day_seconds / prop.event_window_size) ; i++){
          dates.add(date);
          date = new Date(date.getTime() + (prop.event_window_size*1000));
        }
      }
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return dates;
  }
  
  
  /**
   * Loader of e_HashTags from a Database. 
   * @param conn  MySQL Database Connection.
   * @param prop  PropertiesTD that contains the _setup.txt parameters.
   * @param table_date  Table date to load the data.
   * @param date_ini  Start date of the range to load.
   * @param date_end  End date of the range to load.
   * @return  A map with <id_tweets, list of EntityHashTag>
   * @throws SQLException 
   */
  public static Map<Long, List<EntityHashTag>> loadEHashTags(
          Connection conn,
          PropertiesTD prop, 
          String table_date,
          String date_ini,
          String date_end) throws SQLException{
    
    Statement st_e_hashtags = null;
    ResultSet rs_e_hashtags = null;
    
    Map<Long, List<EntityHashTag>>  map_e_hashtags  = new HashMap<>();
    
    if(!prop.mysql_src_generate_entities_from_text){
      String sql_hashtags = "SELECT * " +
                            "FROM "+ table_date +"_e_hashtags " +
                            "WHERE 	download_date >= '"+ date_ini +"' " +
                            "   AND	download_date <  '"+ date_end +"' " +
                            "ORDER BY download_date;";

      st_e_hashtags = conn.createStatement(); 
      rs_e_hashtags = st_e_hashtags.executeQuery(sql_hashtags);

      while (rs_e_hashtags.next()){
        long   id_tweet   = rs_e_hashtags.getLong("id_tweet");

        EntityHashTag e_ht = new EntityHashTag(rs_e_hashtags.getInt("idx_start"),
                                  rs_e_hashtags.getInt("idx_end"),
                                  rs_e_hashtags.getString("hashtag"));

        //System.out.println(e_ht);

        List<EntityHashTag> list_e_hashtag;
        if(map_e_hashtags.containsKey(id_tweet)){
          list_e_hashtag = map_e_hashtags.get(id_tweet);
          list_e_hashtag.add(e_ht);
          map_e_hashtags.put(id_tweet, list_e_hashtag);
        }
        else{
          list_e_hashtag = new ArrayList<>();
          list_e_hashtag.add(e_ht);
          map_e_hashtags.put(id_tweet, list_e_hashtag);
        }

      }

    }
    
    return map_e_hashtags;
    
  }
  
  /**
   * Loader of e_URLS from a Database. 
   * @param conn  MySQL Database Connection.
   * @param prop  PropertiesTD that contains the _setup.txt parameters.
   * @param table_date  Table date to load the data.
   * @param date_ini  Start date of the range to load.
   * @param date_end  End date of the range to load.
   * @return  A map with <id_tweets, list of EntityURL>
   * @throws SQLException 
   */
  public static Map<Long, List<EntityURL>> loadEURLs(
          Connection conn,
          PropertiesTD prop, 
          String table_date,
          String date_ini,
          String date_end) throws SQLException{
    
    Statement st_e_urls = null;
    ResultSet rs_e_urls = null;
    
    Map<Long, List<EntityURL>>  map_e_urls = new HashMap<>();

    if(!prop.mysql_src_generate_entities_from_text){
        
      String sql_urls = "SELECT * " +
                        "FROM "+ table_date +"_e_urls " +
                        "WHERE 	download_date >= '"+ date_ini +"' " +
                        "   AND	download_date <  '"+ date_end +"' " +
                        "ORDER BY download_date;";

      st_e_urls = conn.createStatement(); 
      rs_e_urls = st_e_urls.executeQuery(sql_urls);

      while (rs_e_urls.next()){
        long   id_tweet   = rs_e_urls.getLong("id_tweet");

        EntityURL e_url = new EntityURL(rs_e_urls.getInt("idx_start"),
                                  rs_e_urls.getInt("idx_end"),
                                  rs_e_urls.getString("url_short"),
                                  rs_e_urls.getString("url_long"));

        //System.out.println(e_url);

        List<EntityURL> list_e_url;
        if(map_e_urls.containsKey(id_tweet)){
          list_e_url = map_e_urls.get(id_tweet);
          list_e_url.add(e_url);
          map_e_urls.put(id_tweet, list_e_url);
        }
        else{
          list_e_url = new ArrayList<>();
          list_e_url.add(e_url);
          map_e_urls.put(id_tweet, list_e_url);
        }

      }

    }
    
    return map_e_urls;
    
  }
  
  /**
   * Loader of e_UserRefs from a Database. 
   * @param conn  MySQL Database Connection.
   * @param prop  PropertiesTD that contains the _setup.txt parameters.
   * @param table_date  Table date to load the data.
   * @param date_ini  Start date of the range to load.
   * @param date_end  End date of the range to load.
   * @return  A map with <id_tweets, list of EntityUserRef>
   * @throws SQLException 
   */
  public static Map<Long, List<EntityUserRef>> loadEUserRefs(
          Connection conn,
          PropertiesTD prop, 
          String table_date,
          String date_ini,
          String date_end) throws SQLException{
    
    Statement st_e_userrefs = null;
    ResultSet rs_e_userrefs = null;
    
    Map<Long, List<EntityUserRef>>  map_e_userrefs = new HashMap<>();

    if(!prop.mysql_src_generate_entities_from_text){
      String sql_userrefs = "SELECT * " +
                            "FROM "+ table_date +"_e_urefs " +
                            "WHERE 	download_date >= '"+ date_ini +"' " +
                            "   AND	download_date <  '"+ date_end +"' " +
                            "ORDER BY download_date;";

      st_e_userrefs = conn.createStatement(); 
      rs_e_userrefs = st_e_userrefs.executeQuery(sql_userrefs);

      while (rs_e_userrefs.next()){
        long   id_tweet   = rs_e_userrefs.getLong("id_tweet");

        EntityUserRef e_userref = new EntityUserRef(
                rs_e_userrefs.getInt("idx_start"),
                rs_e_userrefs.getInt("idx_end"),
                rs_e_userrefs.getLong("id_user"),
                rs_e_userrefs.getString("user_name"),
                rs_e_userrefs.getString("user_screen_name"));

        //System.out.println(e_userref);

        List<EntityUserRef> list_e_userref;
        if(map_e_userrefs.containsKey(id_tweet)){
          list_e_userref = map_e_userrefs.get(id_tweet);
          list_e_userref.add(e_userref);
          map_e_userrefs.put(id_tweet, list_e_userref);
        }
        else{
          list_e_userref = new ArrayList<>();
          list_e_userref.add(e_userref);
          map_e_userrefs.put(id_tweet, list_e_userref);
        }

      }

    }
    
    return map_e_userrefs;
    
  }
  
  /**
   * Loader of e_Medias from a Database. 
   * @param conn  MySQL Database Connection.
   * @param prop  PropertiesTD that contains the _setup.txt parameters.
   * @param table_date  Table date to load the data.
   * @param date_ini  Start date of the range to load.
   * @param date_end  End date of the range to load.
   * @return  A map with <id_tweets, list of EntityMedias>
   * @throws SQLException 
   */
  public static Map<Long, List<EntityMedia>> loadEMedias(
          Connection conn,
          PropertiesTD prop, 
          String table_date,
          String date_ini,
          String date_end) throws SQLException{
    
    Statement st_e_medias = null;
    ResultSet rs_e_medias = null;
    
    Map<Long, List<EntityMedia>>  map_e_medias = new HashMap<>();

    if(!prop.mysql_src_generate_entities_from_text){
      String sql_medias = "SELECT * " +
                          "FROM "+ table_date +"_e_medias " +
                          "WHERE 	download_date >= '"+ date_ini +"' " +
                          "   AND	download_date <  '"+ date_end +"' " +
                          "ORDER BY download_date;";

      st_e_medias = conn.createStatement(); 
      rs_e_medias = st_e_medias.executeQuery(sql_medias);

      while (rs_e_medias.next()){
        long   id_tweet   = rs_e_medias.getLong("id_tweet");

        EntityMedia e_medias = new EntityMedia(                  
                rs_e_medias.getLong("id_media"),
                rs_e_medias.getString("url"),
                rs_e_medias.getString("url_https"),
                rs_e_medias.getString("type"),
                new HashMap<Integer, MediaEntity.Size>());
        e_medias.setSizes(rs_e_medias.getString("sizes"));

        List<EntityMedia> list_e_medias;
        if(map_e_medias.containsKey(id_tweet)){
          list_e_medias = map_e_medias.get(id_tweet);
          list_e_medias.add(e_medias);
          map_e_medias.put(id_tweet, list_e_medias);
        }
        else{
          list_e_medias = new ArrayList<>();
          list_e_medias.add(e_medias);
          map_e_medias.put(id_tweet, list_e_medias);
        }

      }

    }
    
    return map_e_medias;
    
  }
  
  /**
   * Loader of Users from a Database. 
   * @param conn  MySQL Database Connection.
   * @param prop  PropertiesTD that contains the _setup.txt parameters.
   * @param table_date  Table date to load the data.
   * @param date_ini  Start date of the range to load.
   * @param date_end  End date of the range to load.
   * @return  A map with <id_tweets, list of Users>
   * @throws SQLException 
   */
  public static Map<Long, TUser> loadUsers(
          Connection conn,
          PropertiesTD prop, 
          String table_date,
          String date_ini,
          String date_end) throws SQLException{
    
    Statement st_users = null;
    ResultSet rs_users = null;
    
    Map<Long, TUser>  map_users = new HashMap<>();

    String sql_user = "SELECT * " +
                        "FROM "+ table_date +"_users " +
                        "WHERE 	download_date >= '"+ date_ini +"' " +
                        "   AND	download_date <  '"+ date_end +"' " +
                        "ORDER BY download_date;";
      
    st_users = conn.createStatement(); 
    rs_users = st_users.executeQuery(sql_user);

    while (rs_users.next()){
      long   id_tweet   = rs_users.getLong("id_tweet");

      TUser user = new TUser(
        rs_users.getTimestamp("creation_date"),
        rs_users.getLong    ("id_user"),
        rs_users.getString  ("name"),
        rs_users.getString  ("screen_name"),
        rs_users.getString  ("lang_user"),
        rs_users.getString  ("location"),
        rs_users.getString  ("timezone"),
        rs_users.getInt     ("utc_offset_mins"),
        rs_users.getBoolean ("follow_request_sent"),
        rs_users.getBoolean ("translator"),
        rs_users.getBoolean ("contribution_enable"),
        rs_users.getBoolean ("uprotected"),
        rs_users.getBoolean ("geo_enabled"),
        rs_users.getString  ("profile_bkg_img_url"),
        rs_users.getString  ("description"),
        rs_users.getBoolean ("verified"),
        rs_users.getString  ("url"),
        rs_users.getString  ("profile_img_url"),
        rs_users.getString  ("profile_bkg_color"),
        rs_users.getString  ("profile_text_color"),
        rs_users.getString  ("profile_link_color"),
        rs_users.getString  ("profile_sidebar_border_color"),
        rs_users.getString  ("profile_sidebar_color"),
        rs_users.getString  ("profile_bkg_img_url"),      //REPEATED
        rs_users.getString  ("profile_img_url"),          //COPY profile_img_url->display_url
        rs_users.getBoolean ("show_all_inline_media"),
        rs_users.getLong    ("followers_count"),
        rs_users.getLong    ("friends_count"),
        rs_users.getLong    ("favorites_count"),
        rs_users.getLong    ("listed_count"),
        rs_users.getLong    ("statuses_count"),
        rs_users.getBoolean ("blacklisted_user")
      );

      map_users.put(id_tweet, user);

    }
    
    return map_users;
    
  }
  
  /**
   * Gets the initial date to retrieve tweets based on the serialized hashtable.
   * @param HT_k  List of serialized HashTables.
   * @param prop  Parameters from the _setup file.
   * @return Initial Date of retrieve tweets.
   */
  public static Date getInitialDate(
          List<ConcurrentHashMap<String,WindowRow>> HT_k,
          PropertiesTD prop){
    
    // Initialize dates in the minimum date for each HashTable.
    List<Date> max_dates = new ArrayList<>();
    for (int i = 0 ; i < prop.event_n_detectors ; i++) {
      max_dates.add(new Date(0));
    }
    
    // Getting the MAX date per each processed HashTable.
    int index = 0;
    for (ConcurrentHashMap<String, WindowRow> concurrentHashMap : HT_k) {
      for (String key : concurrentHashMap.keySet()) {
        WindowRow row = concurrentHashMap.get(key);
        
        if(row.getTimeStamp().compareTo(max_dates.get(index)) > 0)
          max_dates.set(index, row.getTimeStamp());
      }
      index++;
    }
    
    // Get the minimum processed date.
    Date min_processed_date = max_dates.stream().min(Date::compareTo).get();
    
    //Compute the Initial date to get Tweets.    
    return new Date(min_processed_date.getTime() + prop.event_window_size*1000);
  }
  
  /**
   * Cleans the tweets inserted into the database that are not valid.
   * @param recovery_date Date of the Crash.
   * @param prop Parameters from PropertiesTD file.
   */
  public static void cleanCrashDB(Date recovery_date, PropertiesTD prop) {
    
    String tableDate = TUtils.Date_Formatter(recovery_date).substring(0,8);
    String recoveryDate_str = TUtils.Date_Formatter2(recovery_date);
    
    String str_conn;
    Connection conn = null;
    Statement stmt_tweets   = null;
    Statement stmt_report01 = null;
    Statement stmt_event_bt = null;
    Statement stmt_event_desc = null;
    
    //DB Connection.
    try{
      str_conn = String.format("jdbc:mysql://%s:%s/%s?"
              + "characterSetResults=utf8&"
              + "useUnicode=true&"
              + "useLegacyDatetimeCode=false&"
              + "autoReconnect=true&"
              + "failOverReadOnly=false"
              + "&maxReconnects=100"
              + "&jdbcCompliantTruncation=false"
              + "&useSSL=false"
              + "&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'",
                                prop.mysql_server_ip,
                                prop.mysql_server_port,
                                prop.mysql_server_database);
      conn = DriverManager.getConnection( str_conn,
                                          prop.mysql_user,
                                          prop.mysql_password);

      //Delete rows from report01 table.
      //System.out.println("[ListenerDB] Deleting rows from table v_"+prop.keywords_name+".");
      String sql = "DELETE "
                 + "FROM v_"+ prop.keywords_name +" "
                 + "WHERE download_date >= '"+recoveryDate_str+"'";
      
      stmt_tweets = conn.createStatement();
      stmt_tweets.executeUpdate(sql);
      
      //Delete rows from Tweets table.
      //System.out.println("[ListenerDB] Deleting rows from table Tweets.");
      sql = "DELETE "
          + "FROM "+ tableDate +"_tweets "
          + "WHERE download_date >= '"+recoveryDate_str+"'";

      stmt_report01 = conn.createStatement();
      stmt_report01.executeUpdate(sql);

      // events_bt
      //Delete rows from Tweets table.
      //System.out.println("[ListenerDB] Deleting rows from table Event_bt.");
      sql = "DELETE "
          + "FROM "+ tableDate +"_event_bt "
          + "WHERE w1__date >= '"+recoveryDate_str+"'";

      if(TUtilsDescriber.testTable(tableDate+"_event_bt", prop)){
        stmt_event_bt = conn.createStatement();
        stmt_event_bt.executeUpdate(sql);
      }

      // events_desc
      //Delete rows from Tweets table.
      //System.out.println("[ListenerDB] Deleting rows from table Event_desc.");
      sql = "DELETE "
          + "FROM "+ tableDate +"_event_desc "
          + "WHERE event_mid >= '"+recoveryDate_str+"'";

      if(TUtilsDescriber.testTable(tableDate+"_event_desc", prop)){
        stmt_event_desc = conn.createStatement();
        stmt_event_desc.executeUpdate(sql);
      }

      // Users:       Updates screen_name (no need to delete).
      // Senti:       Updates polarity (no need to delete).
      // e_HashTags:  Updates idx_start (no need to delete).
      // e_URLs:      Updates idx_start (no need to delete).
      // e_UserRefs:  Updates idx_start (no need to delete).
      // e_Medias:    Updates type (no need to delete).
      // InvertedIndex:  Updates Key (no need to delete).

    }catch(Exception e){
      e.printStackTrace();
    }finally{
      try{
        if(stmt_tweets != null || stmt_report01 != null)
          conn.close();
      }catch(SQLException se){}
      try{
        if(conn!=null)
          conn.close();
      }catch(SQLException se){
        se.printStackTrace();
      }
    }//end try 
  }
  
  
  /**
   * Loader of a window.
   * @param prop 
   */
  public static String LoadDBTweet(PropertiesTD prop, String table_date, String date_ini, String date_end, BlockingQueue<Tweet> queue_out){
    
    Connection conn = null;
    Statement st_tweet = null;
    ResultSet rs_tweet = null;
    
    Map<Long, TUser>                map_users;
    Map<Long, List<EntityHashTag>>  map_e_hashtags;
    Map<Long, List<EntityURL>>      map_e_urls;
    Map<Long, List<EntityMedia>>    map_e_medias;
    Map<Long, List<EntityUserRef>>  map_e_userrefs;
    
    int counter_e_hashtags = 0;
    int counter_e_urls     = 0;
    int counter_e_userrefs = 0;
    int counter_e_medias   = 0;
    int counter_users      = 0;
    int counter_tweets     = 0;
    
    try{
      String str_conn = String.format("jdbc:mysql://%s:%s/%s?"
              + "characterSetResults=utf8"
              + "&useUnicode=true"
              + "&useLegacyDatetimeCode=false"
              + "&autoReconnect=true"
              + "&failOverReadOnly=false"
              + "&maxReconnects=100"
              + "&jdbcCompliantTruncation=false"
              + "&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'",
                          prop.mysql_src_server_ip,
                          prop.mysql_src_server_port,
                          prop.mysql_src_server_database);
      
      conn = DriverManager.getConnection( str_conn,
                                          prop.mysql_src_user,
                                          prop.mysql_src_password);

      /************************************************************************/
      //LOADING eHashTags from Database
      map_e_hashtags = TUtilsListenerDB.loadEHashTags(conn, prop, table_date, date_ini, date_end);
      counter_e_hashtags = map_e_hashtags.size();

      //LOADING eURLS from Database
      map_e_urls = TUtilsListenerDB.loadEURLs(conn, prop, table_date, date_ini, date_end);
      counter_e_urls = map_e_urls.size();

      //LOADING eUserRefs from Database
      map_e_userrefs = TUtilsListenerDB.loadEUserRefs(conn, prop, table_date, date_ini, date_end);
      counter_e_userrefs = map_e_userrefs.size();

      //LOADING eMedias from Database
      map_e_medias = TUtilsListenerDB.loadEMedias(conn, prop, table_date, date_ini, date_end);
      counter_e_medias = map_e_medias.size();
      
      //LOADING USERS from Database
      map_users = TUtilsListenerDB.loadUsers(conn, prop, table_date, date_ini, date_end);
      counter_users = map_users.size();
      
      /************************************************************************/
      //LOADING TWEETS
      String sql_tweet =  "SELECT * " +
                          "FROM "+ table_date +"_tweets " +
                          "WHERE 	download_date >= '"+ date_ini +"' " +
                          "   AND	download_date <  '"+ date_end +"' " +
                          "ORDER BY download_date;";

      //st_tweet = conn.createStatement(); 
      st_tweet = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                      ResultSet.CONCUR_READ_ONLY); 
      
      int cache_size = 1000;
      
      st_tweet.setFetchSize(cache_size);
      //System.err.println("[ListenerDB] FetchSize: "+st_tweet.getFetchSize());
      rs_tweet = st_tweet.executeQuery(sql_tweet);
      
      List<EntityHashTag>  list_e_hashtags; //    = new ArrayList<>();
      List<EntityURL>      list_e_urls;     //    = new ArrayList<>();
      List<EntityUserRef>  list_e_userrefs; //    = new ArrayList<>();
      List<EntityMedia>    list_e_medias;   //    = new ArrayList<>();
      
      BlockingQueue<Tweet> cache = new LinkedBlockingQueue<>();
      while (rs_tweet.next()){

        //TUser                users_default    = new TUser();
        list_e_hashtags  = ( map_e_hashtags.containsKey(rs_tweet.getLong("id_tweet")) )? map_e_hashtags.get(rs_tweet.getLong("id_tweet")) : new ArrayList<>();
        list_e_urls      = ( map_e_urls.containsKey(rs_tweet.getLong("id_tweet"))     )? map_e_urls    .get(rs_tweet.getLong("id_tweet")) : new ArrayList<>();
        list_e_userrefs  = ( map_e_userrefs.containsKey(rs_tweet.getLong("id_tweet")) )? map_e_userrefs.get(rs_tweet.getLong("id_tweet")) : new ArrayList<>();
        list_e_medias    = ( map_e_medias.containsKey(rs_tweet.getLong("id_tweet"))   )? map_e_medias  .get(rs_tweet.getLong("id_tweet")) : new ArrayList<>();
        
         TUser user = map_users.get(rs_tweet.getLong("id_tweet"));;
        if(user == null){
          user = new TUser();
          user.setCreated_at(rs_tweet.getTimestamp ("download_date"));
          user.setCreated_at(rs_tweet.getTimestamp ("download_date"));
          user.setId(rs_tweet.getLong("id_user"));
        }
        
        //Convert to Tweet   
        Tweet tweet = new Tweet(
                rs_tweet.getTimestamp ("download_date"),
                rs_tweet.getTimestamp ("creation_date"),
                rs_tweet.getLong      ("id_tweet"),
                user,
                rs_tweet.getBoolean   ("favorited"),
                rs_tweet.getBoolean   ("truncated"),
                rs_tweet.getString    ("type"),
                rs_tweet.getString    ("lang_tweet"),
                rs_tweet.getString    ("text_tweet"),
                rs_tweet.getBoolean   ("rt"),
                rs_tweet.getLong      ("rt_count"),
                rs_tweet.getLong      ("rt_id"),
                rs_tweet.getString    ("text_rt"),
                rs_tweet.getBoolean   ("quote"),
                rs_tweet.getLong      ("quote_id"),
                rs_tweet.getString    ("text_quote"),
                rs_tweet.getString    ("src_href"),
                rs_tweet.getString    ("src_rel"),
                rs_tweet.getString    ("src_text"),
                rs_tweet.getLong      ("in_reply_to_status_id"),
                rs_tweet.getLong      ("in_reply_to_user_id"),
                rs_tweet.getString    ("in_reply_to_screen_name"),
                rs_tweet.getBoolean   ("geo_located"),
                rs_tweet.getDouble    ("geo_latitude"),
                rs_tweet.getDouble    ("geo_longitude"),
                rs_tweet.getString    ("place_id"),
                rs_tweet.getString    ("place_name"),
                rs_tweet.getString    ("place_country_code"),
                rs_tweet.getString    ("place_country"),
                rs_tweet.getString    ("place_fullname"),
                rs_tweet.getString    ("place_street_address"),
                rs_tweet.getString    ("place_url"),
                rs_tweet.getString    ("place_place_type"),
                list_e_hashtags,
                list_e_urls,
                list_e_userrefs,
                list_e_medias,
                prop
        );
        
        
        cache.add(tweet);
        counter_tweets++;
        
        //ENQUEUE the Tweet into the pipeline of the analysis.
        if(counter_tweets % cache_size == 0){
          cache.drainTo(queue_out);
          counter_tweets = 0;
        }
        
      }
      if(!cache.isEmpty())
        cache.drainTo(queue_out);
      
      if(map_users      != null) map_users.clear();
      if(map_e_hashtags != null) map_e_hashtags.clear();
      if(map_users      != null) map_e_urls.clear();
      if(map_e_urls     != null) map_e_medias.clear();
      if(map_e_userrefs != null) map_e_userrefs.clear();        
    }
    catch(SQLException e){
      e.printStackTrace();
      System.err.println(e.getMessage());
      System.err.println(e.getSQLState());
    }
    catch(Exception e){
      e.printStackTrace();
    }
    finally{
      try{
        if(rs_tweet != null){
          rs_tweet.close();
        }
      }catch(SQLException se){}
      try{
        if(st_tweet != null){
          st_tweet.close();
        }
      }catch(SQLException se){}
      try{
        if(conn != null){
          conn.close();
        }
      }catch(SQLException se){}

    }
    
    
    return "[tw: "+     counter_tweets      +", "+
            "u: "+      counter_users       +", "+
            "eHT: "+    counter_e_hashtags  +", "+
            "eURL: "+   counter_e_urls      +", "+
            "eURef: "+  counter_e_userrefs  +", "+
            "eMed: "+   counter_e_medias    + "]";
  }
  
  public static Date getMinDateTable(String table_date, PropertiesTD prop){
    
    String str_conn = String.format("jdbc:mysql://%s:%s/%s?"
              + "characterSetResults=utf8"
              + "&useUnicode=true"
              + "&useLegacyDatetimeCode=false"
              + "&autoReconnect=true"
              + "&failOverReadOnly=false"
              + "&maxReconnects=100"
              + "&useSSL=false"
              + "&jdbcCompliantTruncation=false"
              + "&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'",
                          prop.mysql_src_server_ip,
                          prop.mysql_src_server_port,
                          prop.mysql_src_server_database);
    
    Date date = new Date(
            Integer.valueOf(table_date.substring(0, 4)),
            Integer.valueOf(table_date.substring(4, 6)),
            Integer.valueOf(table_date.substring(6, 8)),
            0,0,0);
    
    Connection conn = null;
    Statement st = null;
    ResultSet rs = null;
    
    try{
      conn = DriverManager.getConnection( str_conn, prop.mysql_src_user, prop.mysql_src_password);

      String sql =  "SELECT MIN(download_date) min_date " +
                    "FROM "+ table_date+"_tweets; ";
      
      st = conn.createStatement(); 
      rs = st.executeQuery(sql);
  
      while (rs.next()){
        date = rs.getTimestamp("min_date");
      }
    }
    catch(SQLException e){
      e.printStackTrace();
    }finally{
      try{
        if(rs != null){
          rs.close();
        }
      }catch(SQLException se){}
      try{
        if(st != null){
          st.close();
        }
      }catch(SQLException se){}
      try{
        if(conn != null){
          conn.close();
        }
      }catch(SQLException se){}
      
    }//end try 
      
    return date;
    
  }
}
