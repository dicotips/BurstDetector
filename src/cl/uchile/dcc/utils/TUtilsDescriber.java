package cl.uchile.dcc.utils;

import cl.uchile.dcc.events.detection.EventBT;
import cl.uchile.dcc.text.RegexAnalyzer;
import cl.uchile.dcc.text.TextAnalyzer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import cl.uchile.main.AgentDBStorer;
import java.util.Arrays;
import java.util.Comparator;

/**
* This class contains staic methods to format the input and stores into de 
* database for the Description methods.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public class TUtilsDescriber {
  
  /**
   * Loads the tweets related to a burst. It adds the RT and Quoted information.
   * @param sql   SQL Statement to retrieve the tweets from the database.
   * @param prop  Parameters of the _setup.txt file.
   * @return A List of Sets of tokens from the database.
   * @throws SQLException when a Database problem happens.
   */
  public static List<Set<String>> LoadDescriber(String sql, PropertiesTD prop){

    TextAnalyzer processor = new RegexAnalyzer(prop);
    
    Connection conn = null;
    Statement st = null;
    ResultSet rs = null;
    List<Set<String>> itemsetList = new ArrayList<>();
    try{
      String str_conn = String.format("jdbc:mysql://%s:%s/%s?characterSetResults=utf8&useUnicode=true&useLegacyDatetimeCode=false&autoReconnect=true&failOverReadOnly=false&maxReconnects=100&jdbcCompliantTruncation=false&useSSL=false&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'",
                          prop.mysql_server_ip,
                          prop.mysql_server_port,
                          prop.mysql_server_database);
      conn = DriverManager.getConnection( str_conn,
                                          prop.mysql_user,
                                          prop.mysql_password);

      st = conn.createStatement(); 
      rs = st.executeQuery(sql);
      int counter = 0;
      while (rs.next()){
        //String id_tweet    = rs.getString("id_tweet");
        String lang_tweet  = rs.getString("lang_tweet");
        String text_tweet  = rs.getString("text_tweet");
        
        String text_rt  = rs.getString("text_rt");
        if(rs.wasNull())
          text_rt = "";
        
        String text_quote  = rs.getString("text_quote");
        if(rs.wasNull())
          text_quote = "";
        
        if(!text_rt.equals("")){
          text_tweet = text_rt;
        }
        
        Map<String, Integer> ht = processor.analyzeText(text_quote +" "+ text_tweet, lang_tweet);
        
        itemsetList.add(new HashSet<>(ht.keySet()));
        counter++;
      }
    }
    catch(SQLException e){
      e.printStackTrace();
      System.err.println(AgentDBStorer.class.getName());
      System.err.println(e.getMessage());
      System.err.println(e.getSQLState());
    }
    catch(Exception e){
      e.printStackTrace();
    }
    finally{
      try{
        if(rs != null)
          rs.close();
      }catch(SQLException e){}
      try{
        if(st != null)
          st.close();
      }catch(SQLException e){}
      try{
        if(conn != null)
          conn.close();
      }catch(SQLException e){}
    }
    
    return itemsetList;
  }
  
  /**
   * Removes the tokens with frequency=1.
   * @param transactions  List of tokenized tweets.
   * @param prop  Parameters of the _setup.txt file.
   */
  public static void cleanZiptTail(List<Set<String>> transactions, PropertiesTD prop){
    Map<String, Integer> ht = new HashMap<>();

    // Compute the frequency per token ~ O(n)
    for (Set<String> itemSet : transactions) {
      for (String item : itemSet) {
        if(ht.containsKey(item))
          ht.put(item, ht.get(item)+1);
        else
          ht.put(item, 1);
      }
    }
    
    // List tokens with Freq <= minim  ~ O(n)
    Set<String> set_1 = new HashSet<>();
    for (String item : ht.keySet()) {
      if(ht.get(item) <= prop.describer_freq_minimum)
       set_1.add(item);
    }
    
    // Remove tokens with low frequency
    for (Set<String> itemSet : transactions) {
      itemSet.removeAll(set_1);
    }
    set_1.clear();  //Distroying set_1
    set_1 = null;
    
    //Remove the elements that excede the Maximum number of items.
    if(prop.describer_freq_maximum > 0 && prop.describer_freq_maximum < ht.entrySet().size()){
      //get the most frequent terms
      Object[] a = ht.entrySet().toArray();
      Arrays.parallelSort(a, new Comparator() {
          public int compare(Object o1, Object o2) {
            return ((Map.Entry<String, Integer>) o2).getValue().compareTo(
                    ((Map.Entry<String, Integer>) o1).getValue());
          }
      });

      Object[] b = Arrays.copyOfRange(a, prop.describer_freq_maximum, a.length);
      Set<String> set_2 = new HashSet<>();
      for (Object obj : b) {
       Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) obj;
       set_2.add(entry.getKey());
      }
      
      //Remove the non top-k elements
      for (Set<String> itemSet : transactions) {
        itemSet.removeAll(set_2);
      }
    }
    
    //Remove elements with size=1 and contains "Ω≈"
    for (Set<String> itemSet : transactions) {
      if(itemSet.size()==1)
        for (String string : itemSet) {
          if(string.contains("≈")){
            itemSet.remove(string);
        }
      } 
    }
    ht.clear();  //Distroying ht
    ht = null;    
    
    //Remove transactions with 0 elements.
    transactions.removeIf(e->e.isEmpty());
    
  }
  
  public static boolean testTable(String table_name, PropertiesTD prop) {
    
    Connection conn = null;
    Statement st = null;
    ResultSet rs = null;
    
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
                          prop.mysql_server_ip,
                          prop.mysql_server_port,
                          prop.mysql_server_database);
    
    try {
      conn = DriverManager.getConnection( str_conn, prop.mysql_user, prop.mysql_password);

      String sql =  "SELECT * " +
                    "FROM INFORMATION_SCHEMA.TABLES " +
                    "WHERE   TABLE_NAME   = '"+table_name+"'" +
                    "    AND TABLE_SCHEMA = '"+prop.mysql_server_database+"'";

      st = conn.createStatement(); 
      rs = st.executeQuery(sql);
      
      //System.out.println(sql);
      
      return rs.next();
    } catch (SQLException ex) {
      ex.printStackTrace();
    } finally {
      try{
        if(st !=null)
          st.close();
      }catch(SQLException ex){}
      
      try{
        if(rs !=null)
          rs.close();
      }catch(SQLException ex){}
      
      try{
        if(conn !=null)
          conn.close();
      }catch(SQLException ex){}
    
    }
    
    
    return false;
  }
  
  /**
   * Loads the tweets related to a KEYWORD bursts.
   * @param event Event reported from the Burst Detection method.
   * @param prop  Parameters of the _setup.txt file.
   * @return A List of tweets from the database.
   * @throws SQLException when a Database problem happens.
   */
  public static List<Set<String>> LoadKeywordDescriber(EventBT event, PropertiesTD prop)
          throws SQLException{
    String table_ini = TUtils.Date_Formatter(event.getDateIni()).substring(0, 8);
    String table_mid = TUtils.Date_Formatter(event.getDateMid()).substring(0, 8);
    String table_end = TUtils.Date_Formatter(event.getDateEnd()).substring(0, 8);

    String date_ini = TUtils.Date_Formatter2(event.getDateIni());
    String date_mid = TUtils.Date_Formatter2(event.getDateMid());
    String date_end = TUtils.Date_Formatter2(event.getDateEnd());
    
    //String test = "EVENT-KEYWORDS("+date_mid+") "+date_ini+" - "+date_end+"";
    //System.out.println(test);
    
    String sql_previous = "";
    if(TUtilsDescriber.testTable(table_ini+"_inverted_index", prop))
      sql_previous =  "SELECT id_tweet, term "
                    + "FROM "+table_ini+"_inverted_index "
                    + "WHERE download_date >= '"+date_ini+"' "  // event_ini
                    + "  AND download_date <  '"+date_end+"' UNION ";  // event_end
    
    String sql  =   "SELECT DISTINCT v_t.id_tweet, v_t.lang_tweet, v_t.text_tweet, v_t.text_rt, v_t.text_quote " +
                    "FROM "+table_mid+"_event_bt as e INNER JOIN ("+sql_previous+" "+
                    "                                               SELECT id_tweet, term FROM "+table_mid+"_inverted_index" +
                    "                                               WHERE download_date >= '"+date_ini+"' "+
                    "                                                 AND download_date <  '"+date_end+"' "+
                    "                                             ) as i ON e.term=i.term " +
                    "                                 INNER JOIN v_"+prop.keywords_name+" v_t ON i.id_tweet=v_t.id_tweet " +
                    "WHERE   v_t.type = 'TWEET' " +
                    "    AND v_t.download_date >= '"+date_ini+"' " +  // event_ini
                    "    AND v_t.download_date <  '"+date_end+"' " +  // event_end
                    "    AND v_t.repeated_user in ("+ ((prop.repeated_user)?"0":"0,1")+") " +
                    ((prop.keywords.isEmpty())?" ":"	   AND v_t.has_keyword = 1 ") +
                    "    AND v_t.blacklisted_tweet = 0 " +
                    "    AND v_t.blacklisted_user = 0 " +
                    "	   AND e.valid = 1 " +
                    "    AND (e.z_score_ln  > "+prop.event_zscore_minimum+" OR " +
                    "         e.z_score_rar_ln > "+prop.event_zscore_minimum+") " +
                    "    AND e.term <> '__sys__ss_neutral' " +
                    "    AND e.var_freq >= "+prop.event_var_freq_minimum+" " +
                    "    AND e.w1__date = '"+date_mid+"' ";   // event_mid
    return LoadDescriber(sql, prop);
  }
  
  /**
   * Loads the tweets related to a SUMMARIZED_KEYWORD burst.
   * @param event Event reported from the Burst Detection method.
   * @param prop  Parameters of the _setup.txt file.
   * @return A List of tweets from the database.
   * @throws SQLException when a Database problem happens.
   */
  public static List<Set<String>> LoadKeywordNameDescriber(EventBT event, PropertiesTD prop) throws SQLException{

    String table_ini = TUtils.Date_Formatter(event.getDateIni()).substring(0, 8);
    String table_mid = TUtils.Date_Formatter(event.getDateMid()).substring(0, 8);
    String table_end = TUtils.Date_Formatter(event.getDateEnd()).substring(0, 8);
    
    String date_ini = TUtils.Date_Formatter2(event.getDateIni());
    String date_mid = TUtils.Date_Formatter2(event.getDateMid());
    String date_end = TUtils.Date_Formatter2(event.getDateEnd());
    
    //String test = "EVENT-EQ("+date_mid+") "+date_ini+" - "+date_end+"";
    //System.out.println(test);
    
    String sql  = "SELECT v_t.id_tweet, v_t.lang_tweet, v_t.text_tweet, v_t.text_rt, v_t.text_quote " +
                  "FROM  v_"+prop.keywords_name+" as v_t " +
                  "WHERE v_t.type = 'TWEET' " +
                  "    AND v_t.download_date >= '"+date_ini+"' " +  // event_ini
                  "    AND v_t.download_date <  '"+date_end+"' " +  // event_end
                  "    AND v_t.repeated_user in ("+ ((prop.repeated_user)?"0":"0,1")+") " +
                  ((prop.keywords.isEmpty())?" ":"	   AND v_t.has_keyword = 1 ") +
                  "    AND v_t.blacklisted_tweet = 0 " +
                  "    AND v_t.blacklisted_user = 0 " +
                  "    AND EXISTS(  SELECT * " +
                  "           			FROM "+table_mid+"_event_bt as e " +
                  "         				WHERE e.valid = 1 " +
                  "                   AND (e.z_score_ln > "+prop.event_zscore_minimum+" OR " +
                  "                        e.z_score_rar_ln > "+prop.event_zscore_minimum+") " +
                  "         					AND e.term = '__sys__"+prop.keywords_name+"' " +
                  "                   AND e.var_freq >= "+prop.event_var_freq_minimum+" " +
                  "                   AND e.w1__date = '"+date_mid+"') ";   // event_mid

    return LoadDescriber(sql, prop);
  }
  
  /**
   * Loads the tweets related to a LANGUAGE burst.
   * @param event Event reported from the Burst Detection method.
   * @param prop  Parameters of the _setup.txt file.
   * @return A List of tweets from the database.
   * @throws SQLException when a Database problem happens.
   */
  public static List<Set<String>> LoadLanguageDescriber(EventBT event, PropertiesTD prop)
          throws SQLException{
    
    String table_ini = TUtils.Date_Formatter(event.getDateIni()).substring(0, 8);
    String table_mid = TUtils.Date_Formatter(event.getDateMid()).substring(0, 8);
    String table_end = TUtils.Date_Formatter(event.getDateEnd()).substring(0, 8);

    String date_ini = TUtils.Date_Formatter2(event.getDateIni());
    String date_mid = TUtils.Date_Formatter2(event.getDateMid());
    String date_end = TUtils.Date_Formatter2(event.getDateEnd());
    
    //String test = "EVENT-LANG("+date_mid+") "+date_ini+" - "+date_end+"";
    //System.out.println(test);
    
    String sql  = "SELECT v_t.id_tweet, v_t.lang_tweet, concat('lang≈',v_t.lang_tweet,' ',v_t.text_tweet) as text_tweet, concat('lang≈',v_t.lang_tweet,' ', v_t.text_rt) as text_rt, v_t.text_quote " +
                  "FROM  v_"+prop.keywords_name+" as v_t " +
                  "WHERE v_t.type = 'TWEET' " +
                  "    AND v_t.download_date >= '"+date_ini+"' " +  // event_ini
                  "    AND v_t.download_date <  '"+date_end+"' " +  // event_end
                  "    AND v_t.repeated_user in ("+ ((prop.repeated_user)?"0":"0,1")+") " +
                  "    AND v_t.blacklisted_tweet = 0 " +
                  "    AND v_t.blacklisted_user = 0 " +
                  "    AND lang_tweet in (  SELECT DISTINCT lower(substring(term,14,3)) " +
                  "                         FROM "+table_mid+"_event_bt as e " +
                  "             						WHERE e.valid = 1 " +
                  "                           AND (e.z_score_ln  > "+prop.event_zscore_minimum+" OR " +
                  "                                e.z_score_rar_ln > "+prop.event_zscore_minimum+") " +
                  "                       		AND e.term like '__sys__lang%'" +
                  "                           AND e.var_freq >= "+prop.event_var_freq_minimum+" " +
                  "                           AND e.w1__date = '"+date_mid+"') ";   // event_mid

    return LoadDescriber(sql, prop);
  }
  
  /**
   * Loads the tweets related to a POSITIVE_SENTIMENT burst.
   * @param event Event reported from the Burst Detection method.
   * @param prop  Parameters of the _setup.txt file.
   * @return A List of tweets from the database.
   * @throws SQLException when a Database problem happens.
   */
  public static List<Set<String>> LoadSSPositiveDescriber(EventBT event, PropertiesTD prop)
          throws SQLException{
    
    String table_ini = TUtils.Date_Formatter(event.getDateIni()).substring(0, 8);
    String table_mid = TUtils.Date_Formatter(event.getDateMid()).substring(0, 8);
    String table_end = TUtils.Date_Formatter(event.getDateEnd()).substring(0, 8);

    String date_ini = TUtils.Date_Formatter2(event.getDateIni());
    String date_mid = TUtils.Date_Formatter2(event.getDateMid());
    String date_end = TUtils.Date_Formatter2(event.getDateEnd());
    
    //String test = "EVENT-SS_POS("+date_mid+") "+date_ini+" - "+date_end+"";
    //System.out.println(test);
    
    String sql  = "SELECT v_t.id_tweet, v_t.lang_tweet, v_t.text_tweet, v_t.text_rt, v_t.text_quote " +
                  "FROM  v_"+prop.keywords_name+" as v_t " +
                  "WHERE v_t.type = 'TWEET' " +
                  "    AND v_t.download_date >= '"+date_ini+"' " +  // event_ini
                  "    AND v_t.download_date <  '"+date_end+"' " +  // event_end
                  "    AND v_t.repeated_user in ("+ ((prop.repeated_user)?"0":"0,1")+") " +
                  "    AND ss_polarity > 0 " +
                  "    AND v_t.blacklisted_tweet = 0 " +
                  "    AND v_t.blacklisted_user = 0 " +
                  "    AND lang_tweet in (  SELECT DISTINCT lower(substring(term,14,2)) " +
                  "                         FROM "+table_mid+"_event_bt as e " +
                  "             						WHERE e.valid = 1 " +
                  "                           AND (e.z_score_ln  > "+prop.event_zscore_minimum+" OR " +
                  "                                e.z_score_rar_ln > "+prop.event_zscore_minimum+") " +
                  "                       		AND e.term = '__sys__ss_positive' " +
                  "                           AND e.var_freq >= "+prop.event_var_freq_minimum+" " +
                  "                           AND e.w1__date = '"+date_mid+"') ";   // event_mid

    return LoadDescriber(sql, prop);
  }
  
  /**
   * Loads the tweets related to a NEGATIVE_SENTIMENT burst.
   * @param event Event reported from the Burst Detection method.
   * @param prop  Parameters of the _setup.txt file.
   * @return A List of tweets from the database.
   * @throws SQLException when a Database problem happens.
   */
  public static List<Set<String>> LoadSSNegativeDescriber(EventBT event, PropertiesTD prop)
          throws SQLException{
    
    String table_ini = TUtils.Date_Formatter(event.getDateIni()).substring(0, 8);
    String table_mid = TUtils.Date_Formatter(event.getDateMid()).substring(0, 8);
    String table_end = TUtils.Date_Formatter(event.getDateEnd()).substring(0, 8);

    String date_ini = TUtils.Date_Formatter2(event.getDateIni());
    String date_mid = TUtils.Date_Formatter2(event.getDateMid());
    String date_end = TUtils.Date_Formatter2(event.getDateEnd());
    
    //String test = "EVENT-SS_NEG("+date_mid+") "+date_ini+" - "+date_end+"";
    //System.out.println(test);
    
    String sql  = "SELECT v_t.id_tweet, v_t.lang_tweet, v_t.text_tweet, v_t.text_rt, v_t.text_quote " +
                  "FROM  v_"+prop.keywords_name+" as v_t " +
                  "WHERE v_t.type = 'TWEET' " +
                  "    AND v_t.download_date >= '"+date_ini+"' " +  // event_ini
                  "    AND v_t.download_date <  '"+date_end+"' " +  // event_end
                  "    AND v_t.repeated_user in ("+ ((prop.repeated_user)?"0":"0,1")+") " +
                  "    AND ss_polarity < 0 " +
                  "    AND v_t.blacklisted_tweet = 0 " +
                  "    AND v_t.blacklisted_user = 0 " +
                  "    AND lang_tweet in (  SELECT DISTINCT lower(substring(term,14,2)) " +
                  "                         FROM "+table_mid+"_event_bt as e " +
                  "             						WHERE e.valid = 1 " +
                  "                           AND (e.z_score_ln  > "+prop.event_zscore_minimum+" OR " +
                  "                                e.z_score_rar_ln > "+prop.event_zscore_minimum+") " +
                  "                       		AND e.term = '__sys__ss_negative' " +
                  "                           AND e.var_freq >= "+prop.event_var_freq_minimum+" " +
                  "                           AND e.w1__date = '"+date_mid+"') ";   // event_mid

    return LoadDescriber(sql, prop);
  }
  
  /**
   * Loads the tweets related to a COUNTRY_in_TEXT burst.
   * @param event Event reported from the Burst Detection method.
   * @param prop  Parameters of the _setup.txt file.
   * @return A List of tweets from the database.
   * @throws SQLException when a Database problem happens.
   */
  public static List<Set<String>> LoadCountryTxtDescriber(EventBT event, PropertiesTD prop)
          throws SQLException{
    
    String table_ini = TUtils.Date_Formatter(event.getDateIni()).substring(0, 8);
    String table_mid = TUtils.Date_Formatter(event.getDateMid()).substring(0, 8);
    String table_end = TUtils.Date_Formatter(event.getDateEnd()).substring(0, 8);

    String date_ini = TUtils.Date_Formatter2(event.getDateIni());
    String date_mid = TUtils.Date_Formatter2(event.getDateMid());
    String date_end = TUtils.Date_Formatter2(event.getDateEnd());
    
    //String test = "EVENT-Country_TXT("+date_mid+") "+date_ini+" - "+date_end+"";
    //System.out.println(test);
    
    //String sql = "SELECT v_t.id_tweet, v_t.lang_tweet, v_t.text_tweet, v_t.text_rt, v_t.text_quote " +
    String sql = "SELECT v_t.id_tweet, v_t.lang_tweet, concat('c≈',v_t.country_text,' ',v_t.text_tweet) as text_tweet, concat('c≈',v_t.country_text,' ', v_t.text_rt) as text_rt, v_t.text_quote " +
                 "FROM   v_"+prop.keywords_name+" as v_t " +
                     "	  INNER JOIN " +
                     "	  ( SELECT DISTINCT lower(substring(term, 21, 100)) as country " +
                     "      FROM "+table_mid+"_event_bt as e " +
                     "      WHERE e.valid = 1 " +
                     "        AND (e.z_score_ln  > "+prop.event_zscore_minimum+" OR " +
                     "             e.z_score_rar_ln > "+prop.event_zscore_minimum+") " +            
                     "        AND e.term like '__sys__country_txt__%' " +
                     "        AND e.var_freq >= "+prop.event_var_freq_minimum+" " +
                     "        AND e.w1__date = '"+date_mid+"' " +
                     "     ) as c ON lower(v_t.country_text) like concat('%', c.country, '%') " +
                 "WHERE v_t.type = 'TWEET' " +
                 "    AND v_t.repeated_user in ("+ ((prop.repeated_user)?"0":"0,1")+") " +
                 "    AND v_t.blacklisted_tweet = 0 " +
                 "    AND v_t.blacklisted_user = 0 " +
                 "    AND v_t.download_date >= '"+date_ini+"' " +  // event_ini
                 "    AND v_t.download_date <  '"+date_end+"' ";  // event_end

    return LoadDescriber(sql, prop);
  }
  
  /**
   * Loads the tweets related to a COUNTRY_in_USER burst.
   * @param event Event reported from the Burst Detection method.
   * @param prop  Parameters of the _setup.txt file.
   * @return A List of tweets from the database.
   * @throws SQLException when a Database problem happens.
   */
  public static List<Set<String>> LoadCountryUserDescriber(EventBT event, PropertiesTD prop)
          throws SQLException{
    
    String table_ini = TUtils.Date_Formatter(event.getDateIni()).substring(0, 8);
    String table_mid = TUtils.Date_Formatter(event.getDateMid()).substring(0, 8);
    String table_end = TUtils.Date_Formatter(event.getDateEnd()).substring(0, 8);

    String date_ini = TUtils.Date_Formatter2(event.getDateIni());
    String date_mid = TUtils.Date_Formatter2(event.getDateMid());
    String date_end = TUtils.Date_Formatter2(event.getDateEnd());
    
    //String test = "EVENT-Country_USER("+date_mid+") "+date_ini+" - "+date_end+"";
    //System.out.println(test);
    
    //String sql = "SELECT v_t.id_tweet, v_t.lang_tweet, v_t.text_tweet, v_t.text_rt, v_t.text_quote " +
    String sql = "SELECT v_t.id_tweet, v_t.lang_tweet, concat('c≈',v_t.country_user,' ',v_t.text_tweet) as text_tweet, concat('c≈',v_t.country_user,' ', v_t.text_rt) as text_rt, v_t.text_quote " +
                 "FROM   v_"+prop.keywords_name+" as v_t " +
                     "	  INNER JOIN " +
                     "	  ( SELECT DISTINCT lower(substring(term, 21, 100)) as country " +
                     "      FROM "+table_mid+"_event_bt as e " +
                     "      WHERE e.valid = 1 " +
                     "        AND (e.z_score_ln  > "+prop.event_zscore_minimum+" OR " +
                     "             e.z_score_rar_ln > "+prop.event_zscore_minimum+") " +
                     "        AND e.term like '__sys__country_usr__%' " +
                     "        AND e.var_freq >= "+prop.event_var_freq_minimum+" " +
                     "        AND e.w1__date = '"+date_mid+"' " +
                     "     ) as c ON lower(v_t.country_user) like concat('%', c.country, '%') " +
                 "WHERE v_t.type = 'TWEET' " +
                 "    AND v_t.repeated_user in ("+ ((prop.repeated_user)?"0":"0,1")+") " +
                 "    AND v_t.blacklisted_tweet = 0 " +
                 "    AND v_t.blacklisted_user = 0 " +
                 "    AND v_t.download_date >= '"+date_ini+"' " +  // event_ini
                 "    AND v_t.download_date <  '"+date_end+"' ";  // event_end

    return LoadDescriber(sql, prop);
  }
  
  /**
   * Loads the tweets related to a COUNTRY_in_GEO burst.
   * @param event Event reported from the Burst Detection method.
   * @param prop  Parameters of the _setup.txt file.
   * @return A List of tweets from the database.
   * @throws SQLException when a Database problem happens.
   */
  public static List<Set<String>> LoadCountryGeoDescriber(EventBT event, PropertiesTD prop)
          throws SQLException{
    
    String table_ini = TUtils.Date_Formatter(event.getDateIni()).substring(0, 8);
    String table_mid = TUtils.Date_Formatter(event.getDateMid()).substring(0, 8);
    String table_end = TUtils.Date_Formatter(event.getDateEnd()).substring(0, 8);

    String date_ini = TUtils.Date_Formatter2(event.getDateIni());
    String date_mid = TUtils.Date_Formatter2(event.getDateMid());
    String date_end = TUtils.Date_Formatter2(event.getDateEnd());
    
    //String test = "EVENT-Country_GEO("+date_mid+") "+date_ini+" - "+date_end+"";
    //System.out.println(test);
    
    //String sql = "SELECT v_t.id_tweet, v_t.lang_tweet, v_t.text_tweet, v_t.text_rt, v_t.text_quote " +
    String sql = "SELECT v_t.id_tweet, v_t.lang_tweet, concat('c≈',v_t.place_country,' ',v_t.text_tweet) as text_tweet, concat('c≈',v_t.place_country,' ', v_t.text_rt) as text_rt, v_t.text_quote " +
                 "FROM   v_"+prop.keywords_name+" as v_t " +
                     "	  INNER JOIN " +
                     "	  ( SELECT DISTINCT lower(substring(term, 21, 100)) as country " +
                     "      FROM "+table_mid+"_event_bt as e " +
                     "      WHERE e.valid = 1 " +
                     "        AND (e.z_score_ln  > "+prop.event_zscore_minimum+" OR " +
                     "             e.z_score_rar_ln > "+prop.event_zscore_minimum+") " +
                     "        AND e.term like '__sys__country_geo__%' " +
                     "        AND e.var_freq >= "+prop.event_var_freq_minimum+" " +
                     "        AND e.w1__date = '"+date_mid+"' " +
                     "     ) as c ON lower(v_t.place_country) like concat('%', c.country, '%') " +
                 "WHERE v_t.type = 'TWEET' " +
                 "    AND v_t.blacklisted_tweet = 0 " +
                 "    AND v_t.blacklisted_user = 0 " +
                 "    AND v_t.repeated_user in ("+ ((prop.repeated_user)?"0":"0,1")+") " +
                 "    AND v_t.download_date >= '"+date_ini+"' " +  // event_ini
                 "    AND v_t.download_date <  '"+date_end+"' ";  // event_end

    return LoadDescriber(sql, prop);
  }

  /**
   * Create table EVENT_DESCRIBER in the database (SQL Statements).
   * @param table_date  Table date in format "YYYYMMDD"
   * @param conn        SQL Connector
   * @throws SQLException when a Database problem happens.
   */
  public static void createTable_reportEvent(String table_date, Connection conn) throws SQLException {
    
    String sqlCreate = 
        "CREATE TABLE IF NOT EXISTS "+table_date+"_event_desc("
        + "  event_ini        TIMESTAMP not NULL,"
        + "  event_mid        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
        + "  event_end        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
        + "  type             VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  rank             INT not NULL,"
        + "  n_terms          INT not NULL,"
        + "  description      TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin not NULL,"
        + "  support          DECIMAL(65, 8)"
        //+ "  INDEX(event_ini) USING BTREE,"
        //+ "  INDEX(event_mid) USING BTREE,"
        //+ "  INDEX(event_end) USING BTREE"
        + ") ENGINE = INNODB;";
    
    Statement stmt = conn.createStatement();
    stmt.execute(sqlCreate);
  }
  
  /**
   * Insertion of the Description of the events to the database (Insert SQL Statements).
   * @param stmt                Insert SQL Statement.
   * @param event               Event to Insert.
   * @param result_kw           List of terms and support ratio for keyword signals.
   * @param result_eq           List of terms and support ratio for summarized_keyword signal.
   * @param result_lang         List of terms and support ratio for language signals.
   * @param result_ss_pos       List of terms and support ratio for positive_sentiment signal.
   * @param result_ss_neg       List of terms and support ratio for negative_sentiment signal.
   * @param result_country_txt  List of terms and support ratio for Countries in TXT signals.
   * @param result_country_usr  List of terms and support ratio for Countries in USER signals.
   * @param result_country_geo  List of terms and support ratio for Countries in GEO signals.
   * @param prop                Parameters of the _setup.txt file.              
   * @throws SQLException when a Database problem happens.
   */
  public static void insertStatement_reportEvent(
          PreparedStatement stmt,
          EventBT event,
          List<Map.Entry<Set<String>, Double>> result_kw,
          List<Map.Entry<Set<String>, Double>> result_eq,
          List<Map.Entry<Set<String>, Double>> result_lang,
          List<Map.Entry<Set<String>, Double>> result_ss_pos,
          List<Map.Entry<Set<String>, Double>> result_ss_neg,
          List<Map.Entry<Set<String>, Double>> result_country_txt,
          List<Map.Entry<Set<String>, Double>> result_country_usr,
          List<Map.Entry<Set<String>, Double>> result_country_geo,
          PropertiesTD prop) throws SQLException {
    
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    
    // Impresion de eventos por diferentes Criterios
    //System.out.println((char)27 + "[33;40mFREQUENT ITEMSETS - KEYWORDS"+ (char)27 + "[0m");
    int i = 1;
    for (Map.Entry<Set<String>, Double> entry : result_kw) {
      
      stmt.setTimestamp(1 , new Timestamp(event.getDateIni().getTime()), cal); //  event_ini
      stmt.setTimestamp(2 , new Timestamp(event.getDateMid().getTime()), cal); //  event_mid
      stmt.setTimestamp(3 , new Timestamp(event.getDateEnd().getTime()), cal); //  event_end
      stmt.setString   (4 , "keywords");                  // type
      stmt.setInt      (5 , i++);                         // rank
      stmt.setInt      (6 , entry.getKey().size());       // n_terms
      stmt.setString   (7 , entry.getKey().toString());   // description
      stmt.setDouble   (8 , entry.getValue());            // support

      stmt.addBatch();
      
      //System.out.printf("%2d: %9s, support: %1.3f\n",i++,entry.getKey(),entry.getValue());
    }

    //System.out.println((char)27 + "[33;40mFREQUENT ITEMSETS - KEYWORD_NAME SIGNAL"+ (char)27 + "[0m");
    i = 1;
    for (Map.Entry<Set<String>, Double> entry : result_eq) {
      
      stmt.setTimestamp(1 , new Timestamp(event.getDateIni().getTime()), cal); //  event_ini
      stmt.setTimestamp(2 , new Timestamp(event.getDateMid().getTime()), cal); //  event_mid
      stmt.setTimestamp(3 , new Timestamp(event.getDateEnd().getTime()), cal); //  event_end
      stmt.setString   (4 , prop.keywords_name);          // type
      stmt.setInt      (5 , i++);                         // rank
      stmt.setInt      (6 , entry.getKey().size());       // n_terms
      stmt.setString   (7 , entry.getKey().toString());   // description
      stmt.setDouble   (8 , entry.getValue());            // support

      stmt.addBatch();
      
      //System.out.printf("%2d: %9s, support: %1.3f\n",i++,entry.getKey(),entry.getValue());
    }

    //System.out.println((char)27 + "[33;40mFREQUENT ITEMSETS - LANGUAGES"+ (char)27 + "[0m");
    i = 1;
    for (Map.Entry<Set<String>, Double> entry : result_lang) {
      
      stmt.setTimestamp(1 , new Timestamp(event.getDateIni().getTime()), cal); //  event_ini
      stmt.setTimestamp(2 , new Timestamp(event.getDateMid().getTime()), cal); //  event_mid
      stmt.setTimestamp(3 , new Timestamp(event.getDateEnd().getTime()), cal); //  event_end
      stmt.setString   (4 , "language_txt");              // type
      stmt.setInt      (5 , i++);                         // rank
      stmt.setInt      (6 , entry.getKey().size());       // n_terms
      stmt.setString   (7 , entry.getKey().toString());   // description
      stmt.setDouble   (8 , entry.getValue());            // support

      stmt.addBatch();
      
      //System.out.printf("%2d: %9s, support: %1.3f\n",i++,entry.getKey(),entry.getValue());
    }

    //System.out.println((char)27 + "[33;40mFREQUENT ITEMSETS - SENTIMENT POSITIVE"+ (char)27 + "[0m");
    i = 1;
    for (Map.Entry<Set<String>, Double> entry : result_ss_pos) {
      
      stmt.setTimestamp(1 , new Timestamp(event.getDateIni().getTime()), cal); //  event_ini
      stmt.setTimestamp(2 , new Timestamp(event.getDateMid().getTime()), cal); //  event_mid
      stmt.setTimestamp(3 , new Timestamp(event.getDateEnd().getTime()), cal); //  event_end
      stmt.setString   (4 , "ss_positive");               // type
      stmt.setInt      (5 , i++);                           // rank
      stmt.setInt      (6 , entry.getKey().size());       // n_terms
      stmt.setString   (7 , entry.getKey().toString());   // description
      stmt.setDouble   (8 , entry.getValue());            // support

      stmt.addBatch();
      
      //System.out.printf("%2d: %9s, support: %1.3f\n",i++,entry.getKey(),entry.getValue());
    }

    //System.out.println((char)27 + "[33;40mFREQUENT ITEMSETS - SENTIMENT NEGATIVE"+ (char)27 + "[0m");
    i = 1;
    for (Map.Entry<Set<String>, Double> entry : result_ss_neg) {
      
      stmt.setTimestamp(1 , new Timestamp(event.getDateIni().getTime()), cal); //  event_ini
      stmt.setTimestamp(2 , new Timestamp(event.getDateMid().getTime()), cal); //  event_mid
      stmt.setTimestamp(3 , new Timestamp(event.getDateEnd().getTime()), cal); //  event_end
      stmt.setString   (4 , "ss_negative");               // type
      stmt.setInt      (5 , i++);                           // rank
      stmt.setInt      (6 , entry.getKey().size());       // n_terms
      stmt.setString   (7 , entry.getKey().toString());   // description
      stmt.setDouble   (8 , entry.getValue());            // support

      stmt.addBatch();
      
      //System.out.printf("%2d: %9s, support: %1.3f\n",i++,entry.getKey(),entry.getValue());
    }

    //System.out.println((char)27 + "[33;40mFREQUENT ITEMSETS - COUNTRY TEXT"+ (char)27 + "[0m");
    i = 1;
    for (Map.Entry<Set<String>, Double> entry : result_country_txt) {
      
      stmt.setTimestamp(1 , new Timestamp(event.getDateIni().getTime()), cal); //  event_ini
      stmt.setTimestamp(2 , new Timestamp(event.getDateMid().getTime()), cal); //  event_mid
      stmt.setTimestamp(3 , new Timestamp(event.getDateEnd().getTime()), cal); //  event_end
      stmt.setString   (4 , "country_text");              // type
      stmt.setInt      (5 , i++);                           // rank
      stmt.setInt      (6 , entry.getKey().size());       // n_terms
      stmt.setString   (7 , entry.getKey().toString());   // description
      stmt.setDouble   (8 , entry.getValue());            // support

      stmt.addBatch();
      
      //System.out.printf("%2d: %9s, support: %1.3f\n",i++,entry.getKey(),entry.getValue());
    }

    //System.out.println((char)27 + "[33;40mFREQUENT ITEMSETS - COUNTRY USER"+ (char)27 + "[0m");
    i = 1;
    for (Map.Entry<Set<String>, Double> entry : result_country_usr) {
      
      stmt.setTimestamp(1 , new Timestamp(event.getDateIni().getTime()), cal); //  event_ini
      stmt.setTimestamp(2 , new Timestamp(event.getDateMid().getTime()), cal); //  event_mid
      stmt.setTimestamp(3 , new Timestamp(event.getDateEnd().getTime()), cal); //  event_end
      stmt.setString   (4 , "country_user");              // type
      stmt.setInt      (5 , i++);                           // rank
      stmt.setInt      (6 , entry.getKey().size());       // n_terms
      stmt.setString   (7 , entry.getKey().toString());   // description
      stmt.setDouble   (8 , entry.getValue());            // support

      stmt.addBatch();
      
      //System.out.printf("%2d: %9s, support: %1.3f\n",i++,entry.getKey(),entry.getValue());
    }

    //System.out.println((char)27 + "[33;40mFREQUENT ITEMSETS - COUNTRY GEO"+ (char)27 + "[0m");
    i = 1;
    for (Map.Entry<Set<String>, Double> entry : result_country_geo) {
      
      stmt.setTimestamp(1 , new Timestamp(event.getDateIni().getTime()), cal); //  event_ini
      stmt.setTimestamp(2 , new Timestamp(event.getDateMid().getTime()), cal); //  event_mid
      stmt.setTimestamp(3 , new Timestamp(event.getDateEnd().getTime()), cal); //  event_end
      stmt.setString   (4 , "country_geo");              // type
      stmt.setInt      (5 , i++);                           // rank
      stmt.setInt      (6 , entry.getKey().size());       // n_terms
      stmt.setString   (7 , entry.getKey().toString());   // description
      stmt.setDouble   (8 , entry.getValue());            // support

      stmt.addBatch();
      
      //System.out.printf("%2d: %9s, support: %1.3f\n",i++,entry.getKey(),entry.getValue());
    }

  }
  
}
