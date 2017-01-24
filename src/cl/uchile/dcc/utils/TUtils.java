package cl.uchile.dcc.utils;

import cl.uchile.dcc.events.detection.WindowRow;
import cl.uchile.dcc.events.twitter.EntityHashTag;
import cl.uchile.dcc.events.twitter.EntityMedia;
import cl.uchile.dcc.events.twitter.EntityURL;
import cl.uchile.dcc.events.twitter.EntityUserRef;
import cl.uchile.dcc.events.twitter.TSenti;
import cl.uchile.dcc.events.twitter.TUser;
import cl.uchile.dcc.events.twitter.Tweet;
import cl.uchile.dcc.text.NGramRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import cl.uchile.main.AgentDBStorer;

/**
* This class contains static methods to format text removing special characters. It 
* also prepares the Creation of table and insert statements for sending the 
* data into the database.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public class TUtils {
  
  /**
   * Removes carry return and tabulation marks in a given text.
   * @param str Text to process.
   * @return Text with removed carry return and tab characters.
   */
  public static String Text_Formatter(String str){
    if (str == null)
      str = "";
    str = str.replaceAll("\n"," ");
    str = str.replaceAll("\r"," ");
    str = str.replaceAll("\t"," ");
    str = str.replaceAll("[\n\r]"," ");
    str = str.replaceAll("\\s+", " ");
    return str.trim();
  }
  
  /**
   * Date formated to "YYYYMMDD HH:mm:ss Z" in UTC-0.
   * @param date Local date.
   * @return String in format UTC0: "YYYYMMDD HH:mm:ss Z"
   */
  public static String Date_Formatter(Date date){
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss Z");
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    return dateFormat.format(date);
  }
  
  /**
   * Date formated to "YYYY-MM-DD HH:mm:ss" in UTC-0.
   * @param date Local date.
   * @return String in format UTC0: "YYYY-MM-DD HH:mm:ss"
   */
  public static String Date_Formatter2(Date date){
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    return dateFormat.format(date);
  }

  /**
   * Generates the Start and End Dates from a given date. These dates contain
   * the bounds and ranges of the dates that handles the date into a window.
   * @param now Actual timestamp.
   * @param prop_window_size  Window_Size in seconds.
   * @param shift             Shift in the timestamps of the window (in seconds).
   * @return Array with Start and End dates.
   * @throws ParseException when the parsing cannot be done.
   */
  public static Date[] getWinBounds(Date now, long prop_window_size, long shift)
          throws ParseException{
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss Z");
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

    long window_size = prop_window_size * 1000;  //miliseconds
    
    //Init of the Day in UTC  ==>>  Date ini
    String now_str = TUtils.Date_Formatter(now);
    String ini_str = now_str.substring(0, 8)+" 00:00:00 +0000";
    //System.err.println(ini_str);
    Date ini = dateFormat.parse(ini_str);

    //Window INI $ END
    long diff = now.getTime()-ini.getTime();
    long win_ini_l = (long) (window_size * Math.floor((double) diff/window_size));
    long win_end_l = win_ini_l + window_size;
    Date win_ini = new Date(ini.getTime() + win_ini_l + shift*1000);
    Date win_end = new Date(ini.getTime() + win_end_l + shift*1000);
    
    Date[] win_bound = {win_ini, win_end};
    
    return win_bound;
  }
  
  // 
  /**
   * Sorting algorithm Java8 Lambda and Parallel Stream.
   * @param <K> Key of the Map.
   * @param <V>
   * @param map
   * @return 
   */
  /*public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map ){
    Map<K,V> result = new LinkedHashMap<>();
    Stream <Entry<K,V>> st = map.entrySet().stream();

    st.sorted(Comparator.comparing(e -> e.getValue()))
            .forEachOrdered(e ->result.put(e.getKey(),e.getValue()));

     return result;
  }*/  
  
  /**
   * Create table TWEETS in the database (SQL Statements).
   * @param tbl_date  Table date in format "YYYYMMDD"
   * @param conn      SQL Connector
   * @throws SQLException When cration error occurs.
   */
  public static void createTable_Tweets(String tbl_date, Connection conn) throws SQLException {
    
    String sqlCreate = 
        "CREATE TABLE IF NOT EXISTS "+tbl_date+"_tweets("
        + "  sys_partition     INT not null,"
        + "  download_date     TIMESTAMP not NULL DEFAULT CURRENT_TIMESTAMP,"
        + "  creation_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
        + "  id_tweet          BIGINT not NULL,"
        + "  blacklisted_tweet TINYINT not null," 
        + "  id_user           BIGINT not NULL,"
        + "  repeated_user     TINYINT not null," 
        + "  favorited         TINYINT not null,"
        + "  truncated         TINYINT not null,"
        + "  type              VARCHAR(10) not NULL,"
        + "  lang_tweet        VARCHAR(10) not NULL,"
        + "  text_tweet        TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin not NULL,"
        + "  rt                TINYINT not null,"
        + "  rt_count          BIGINT,"
        + "  rt_id             BIGINT,"
        + "  text_rt           TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  quote             TINYINT not NULL,"
        + "  quote_id          BIGINT,"
        + "  text_quote        TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  has_keyword       TINYINT,"
        + "  src_href          VARCHAR(200),"
        + "  src_rel           VARCHAR(50),"
        + "  src_text          VARCHAR(100),"
        + "  in_reply_to_status_id   BIGINT,"
        + "  in_reply_to_user_id     BIGINT,"
        + "  in_reply_to_screen_name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  countries_text    VARCHAR(200),"
        + "  geo_located       TINYINT not null,"
        + "  geo_latitude      DECIMAL(65, 8),"
        + "  geo_longitude     DECIMAL(65, 8),"
        + "  place_id          VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  place_name        VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  place_country_code  VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  place_country     VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  place_fullname    VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  place_street_address  VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  place_url         VARCHAR(2048),"
        + "  place_place_type  VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  counter           INT not NULL,"
        + "  PRIMARY KEY(id_tweet, sys_partition) USING HASH,"
        //+ "  INDEX(creation_date) USING BTREE,"
        + "  INDEX(download_date) USING BTREE"
        + ") ENGINE = INNODB PARTITION BY HASH(sys_partition) PARTITIONS 24;";
    
    Statement stmt = conn.createStatement();
    stmt.execute(sqlCreate);
  }
  
  /**
   * Create table USERS in the database (SQL Statements).
   * @param tbl_date  Table date in format "YYYYMMDD"
   * @param conn      SQL Connector
   * @throws SQLException When cration error occurs.
   */
  public static void createTable_Users(String tbl_date, Connection conn) throws SQLException {
    
    String sqlCreate = 
        "CREATE TABLE IF NOT EXISTS "+tbl_date+"_users("
        + "  sys_partition     INT not null,"
        + "  download_date     TIMESTAMP not NULL DEFAULT CURRENT_TIMESTAMP,"
        + "  creation_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
        + "  id_tweet          BIGINT not NULL,"
        + "  id_user           BIGINT not NULL,"
        + "  name              VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  screen_name       VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  blacklisted_user  TINYINT not null,"
        + "  description       TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  statuses_count    BIGINT,"
        + "  favorites_count   BIGINT,"
        + "  followers_count   BIGINT,"
        + "  friends_count     BIGINT,"
        + "  listed_count      BIGINT,"
        + "  translator        TINYINT,"
        + "  geo_enabled       TINYINT,"
        + "  lang_user         VARCHAR(10),"
        + "  location          TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  countries_user    VARCHAR(200),"
        + "  timezone          VARCHAR(30),"
        + "  utc_offset_mins       INTEGER,"
        + "  follow_request_sent   TINYINT,"
        + "  uprotected            TINYINT,"
        + "  verified              TINYINT,"
        + "  contribution_enable   TINYINT,"
        + "  show_all_inline_media TINYINT,"
        + "  url                   TEXT,"
        + "  profile_bkg_color     VARCHAR(6),"
        + "  profile_bkg_img_url   TEXT,"
        + "  profile_img_url       TEXT,"
        + "  profile_link_color    VARCHAR(6),"
        + "  profile_sidebar_border_color VARCHAR(6),"
        + "  profile_sidebar_color VARCHAR(6),"
        + "  profile_text_color    VARCHAR(6),"
        + "  PRIMARY KEY(id_tweet, id_user, sys_partition) USING HASH"
        //+ "  FOREIGN KEY(id_tweet) REFERENCES "+tbl_date+"_tweets(id_tweet) ON DELETE RESTRICT ON UPDATE CASCADE"
        + ") ENGINE = INNODB PARTITION BY HASH(sys_partition) PARTITIONS 24;";
    
    Statement stmt = conn.createStatement();
    stmt.execute(sqlCreate);
  }
  
  /**
   * Create table e_HASHTAGS in the database (SQL Statements).
   * @param tbl_date  Table date in format "YYYYMMDD"
   * @param conn      SQL Connector
   * @throws SQLException When cration error occurs.
   */
  public static void createTable_eHashTags(String tbl_date, Connection conn) throws SQLException {
    
    String sqlCreate = 
        "CREATE TABLE IF NOT EXISTS "+tbl_date+"_e_hashtags("
        + "  sys_partition     INT not null,"
        + "  download_date     TIMESTAMP not NULL DEFAULT CURRENT_TIMESTAMP,"
        + "  id_tweet          BIGINT not NULL,"
        + "  idx_start         INTEGER not NULL,"
        + "  idx_end           INTEGER not NULL,"
        + "  hashtag           TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin not NULL,"
        + "  PRIMARY KEY (id_tweet,idx_start,idx_end,sys_partition) USING HASH,"
        //+ "  FOREIGN KEY (id_tweet) REFERENCES "+tbl_date+"_tweets (id_tweet) ON DELETE RESTRICT ON UPDATE CASCADE,"
        + "  INDEX(download_date) USING BTREE"
        + ") ENGINE = INNODB PARTITION BY HASH(sys_partition) PARTITIONS 24;";
    
    Statement stmt = conn.createStatement();
    stmt.execute(sqlCreate);
  }
  
  /**
   * Create table e_USERREFS in the database (SQL Statements).
   * @param tbl_date  Table date in format "YYYYMMDD"
   * @param conn      SQL Connector
   * @throws SQLException When cration error occurs.
   */
  public static void createTable_eUserRefs(String tbl_date, Connection conn) throws SQLException {
    
    String sqlCreate = 
        "CREATE TABLE IF NOT EXISTS "+tbl_date+"_e_urefs("
        + "  sys_partition     INT not null,"
        + "  download_date     TIMESTAMP not NULL DEFAULT CURRENT_TIMESTAMP,"
        + "  id_tweet          BIGINT not NULL,"
        + "  idx_start         INTEGER not NULL,"
        + "  idx_end           INTEGER not NULL,"
        + "  id_user           BIGINT not NULL,"
        + "  user_name         VARCHAR(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin not NULL DEFAULT '',"
        + "  user_screen_name  VARCHAR(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin not NULL DEFAULT '',"
        + "  PRIMARY KEY (id_tweet,idx_start,idx_end,sys_partition) USING HASH,"
        //+ "  FOREIGN KEY (id_tweet) REFERENCES "+tbl_date+"_tweets (id_tweet) ON DELETE RESTRICT ON UPDATE CASCADE,"
        + "  INDEX(download_date) USING BTREE"
        + ") ENGINE = INNODB PARTITION BY HASH(sys_partition) PARTITIONS 24;";
    
    Statement stmt = conn.createStatement();
    stmt.execute(sqlCreate);
  }
  
  /**
   * Create table e_URLS in the database (SQL Statements).
   * @param tbl_date  Table date in format "YYYYMMDD"
   * @param conn      SQL Connector
   * @throws SQLException When cration error occurs.
   */
  public static void createTable_eURLs(String tbl_date, Connection conn) throws SQLException {
    
    String sqlCreate = 
        "CREATE TABLE IF NOT EXISTS "+tbl_date+"_e_urls("
        + "  sys_partition     INT not null,"
        + "  download_date     TIMESTAMP not NULL DEFAULT CURRENT_TIMESTAMP,"
        + "  id_tweet          BIGINT not NULL,"
        + "  idx_start         INTEGER not NULL,"
        + "  idx_end           INTEGER not NULL,"
        + "  url_short         TEXT not NULL,"
        + "  url_long          TEXT not NULL,"
        + "  PRIMARY KEY (id_tweet,idx_start,idx_end,sys_partition) USING HASH,"
        //+ "  FOREIGN KEY (id_tweet) REFERENCES "+tbl_date+"_tweets (id_tweet) ON DELETE RESTRICT ON UPDATE CASCADE,"
        + "  INDEX(download_date) USING BTREE"
        + ") ENGINE = INNODB PARTITION BY HASH(sys_partition) PARTITIONS 24;";
    
    Statement stmt = conn.createStatement();
    stmt.execute(sqlCreate);
  }
  
  /**
   * Create table e_MEDIAS in the database (SQL Statements).
   * @param tbl_date  Table date in format "YYYYMMDD"
   * @param conn      SQL Connector
   * @throws SQLException When cration error occurs.
   */
  public static void createTable_eMedias(String tbl_date, Connection conn) throws SQLException {
    
    String sqlCreate = 
        "CREATE TABLE IF NOT EXISTS "+tbl_date+"_e_medias("
        + "  sys_partition     INT not null,"
        + "  download_date     TIMESTAMP not NULL DEFAULT CURRENT_TIMESTAMP,"
        + "  id_tweet          BIGINT not NULL,"
        + "  id_media          BIGINT not NULL,"
        + "  url               TEXT not NULL,"
        + "  url_https         TEXT not NULL,"
        + "  type              VARCHAR(50)   not NULL,"    
        + "  sizes             TEXT not NULL,"
        + "  PRIMARY KEY (id_tweet,id_media,sys_partition) USING HASH,"
        //+ "  FOREIGN KEY (id_tweet) REFERENCES "+tbl_date+"_tweets (id_tweet) ON DELETE RESTRICT ON UPDATE CASCADE,"
        + "  INDEX(download_date) USING BTREE"
        + ") ENGINE = INNODB PARTITION BY HASH(sys_partition) PARTITIONS 24;";
    
    Statement stmt = conn.createStatement();
    stmt.execute(sqlCreate);
  }
  
  /**
   * Create table SENTI in the database (SQL Statements).
   * @param tbl_date  Table date in format "YYYYMMDD"
   * @param conn      SQL Connector
   * @throws SQLException When cration error occurs.
   */
  public static void createTable_Senti(String tbl_date, Connection conn) throws SQLException {
    
    String sqlCreate = 
        "CREATE TABLE IF NOT EXISTS "+tbl_date+"_senti("
        + "  sys_partition     INT not null,"
        + "  download_date     TIMESTAMP not NULL DEFAULT CURRENT_TIMESTAMP,"
        + "  id_tweet          BIGINT not NULL,"
        + "  ss_lang           VARCHAR(10) not NULL,"
        + "  ss_positive       TINYINT not NULL,"
        + "  ss_negative       TINYINT not NULL,"
        + "  ss_neutral        TINYINT not NULL,"
        + "  ss_polarity       TINYINT not NULL,"
        + "  PRIMARY KEY (id_tweet,sys_partition) USING HASH"
        //+ "  FOREIGN KEY (id_tweet) REFERENCES "+tbl_date+"_tweets (id_tweet) ON DELETE RESTRICT ON UPDATE CASCADE"
        + ") ENGINE = INNODB PARTITION BY HASH(sys_partition) PARTITIONS 24;";
    
    Statement stmt = conn.createStatement();
    stmt.execute(sqlCreate);
  }
  
  /**
   * Create table NGrams in the database (SQL Statements).
   * @param conn      SQL Connector
   * @throws SQLException When cration error occurs.
   */
  public static void createTable_NGrams(Connection conn) throws SQLException {

    String sqlCreate = 
          "CREATE TABLE IF NOT EXISTS v_ngrams("
        + "  id          VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin not NULL,"
        + "  term        VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin not NULL,"
        + "  n           TINYINT not null,"
        + "  rt          TINYINT not null,"
        + "  lang_tweet  VARCHAR(10) not NULL,"
        + "  freq_term   BIGINT not NULL,"
        + "  freq_tweet  BIGINT not NULL,"
        + "  PRIMARY KEY (id) USING HASH"
        + ") ENGINE = INNODB;";
    
    Statement stmt = conn.createStatement();
    stmt.execute(sqlCreate);
  }
  
  /**
   * Create table EEvents in the database (SQL Statements).
   * @param tbl_date  Table date in format "YYYYMMDD"
   * @param conn      SQL Connector
   * @throws SQLException When cration error occurs.
   */
  public static void createTable_EEventBD(String tbl_date, Connection conn) throws SQLException {

    String sqlCreate = 
        "CREATE TABLE IF NOT EXISTS "+tbl_date+"_event_bt("
        + "  event_date        TIMESTAMP not NULL DEFAULT CURRENT_TIMESTAMP,"
        + "  rank              INT not NULL,"
        + "  term              VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin not NULL,"
        + "  window_size_seg   BIGINT not NULL,"
        
        + "  w1__date          TIMESTAMP not NULL DEFAULT CURRENT_TIMESTAMP,"       //5
        + "  w1__freq_t        BIGINT not NULL,"
        + "  w1__freq          BIGINT not NULL,"
        + "  w1__freq_ln       DECIMAL(65, 8) not NULL,"
        + "  w1__ar            DECIMAL(65, 8) not NULL,"
        + "  w1__ar_ln         DECIMAL(65, 8) not NULL,"
        + "  w1__rar           DECIMAL(65, 8) not NULL,"
        + "  w1__rar_ln        DECIMAL(65, 8) not NULL,"
        
        + "  w2__date          TIMESTAMP NULL,"           //13
        + "  w2__freq_t        BIGINT NULL,"
        + "  w2__freq          BIGINT NULL,"
        + "  w2__freq_ln       DECIMAL(65, 8) NULL,"
        + "  w2__ar            DECIMAL(65, 8) NULL,"
        + "  w2__ar_ln         DECIMAL(65, 8) NULL,"
        + "  w2__rar           DECIMAL(65, 8) NULL,"
        + "  w2__rar_ln        DECIMAL(65, 8) NULL,"
            
        + "  var_freq          BIGINT not NULL,"          //21
        + "  var_freq_ln       DECIMAL(65, 8) not NULL,"
        + "  var_ar            DECIMAL(65, 8) not NULL,"
        + "  var_ar_ln         DECIMAL(65, 8) not NULL,"
        + "  var_rar           DECIMAL(65, 8) not NULL,"
        + "  var_rar_ln        DECIMAL(65, 8) not NULL,"
        
        + "  rel_var_freq      DECIMAL(65, 8) not NULL,"  //27
        + "  rel_var_freq_ln   DECIMAL(65, 8) not NULL,"
        + "  rel_var_ar        DECIMAL(65, 8) not NULL,"
        + "  rel_var_ar_ln     DECIMAL(65, 8) not NULL,"
        + "  rel_var_rar       DECIMAL(65, 8) not NULL,"
        + "  rel_var_rar_ln    DECIMAL(65, 8) not NULL,"
        + "  valid             INT not NULL,"
        + "  n_win_term        BIGINT not NULL,"
        + "  vt_nro_windows    BIGINT not NULL,"
        
        + "  vt_sum_x          BIGINT not NULL,"          //36
        + "  vt_sum_x_ln       DECIMAL(65, 8) not NULL,"
        + "  vt_sum_x_ar       DECIMAL(65, 8) not NULL,"
        + "  vt_sum_x_ar_ln    DECIMAL(65, 8) not NULL,"
        + "  vt_sum_x_rar      DECIMAL(65, 8) not NULL,"
        + "  vt_sum_x_rar_ln   DECIMAL(65, 8) not NULL,"
        + "  vt_sum_x_var_ar   DECIMAL(65, 8) not NULL,"
        + "  vt_sum_x_rvar_ar  DECIMAL(65, 8) not NULL,"
        + "  vt_sum_x_var_rar  DECIMAL(65, 8) not NULL,"
        + "  vt_sum_x_rvar_rar DECIMAL(65, 8) not NULL,"
            
        + "  vt_sum_x2         BIGINT not NULL,"          //46
        + "  vt_sum_x2_ln      DECIMAL(65, 8) not NULL,"
        + "  vt_sum_x2_ar      DECIMAL(65, 8) not NULL,"
        + "  vt_sum_x2_ar_ln   DECIMAL(65, 8) not NULL,"
        + "  vt_sum_x2_rar     DECIMAL(65, 8) not NULL,"
        + "  vt_sum_x2_rar_ln  DECIMAL(65, 8) not NULL,"
        + "  vt_sum_x2_var_ar   DECIMAL(65, 8) not NULL,"
        + "  vt_sum_x2_rvar_ar  DECIMAL(65, 8) not NULL,"
        + "  vt_sum_x2_var_rar  DECIMAL(65, 8) not NULL,"
        + "  vt_sum_x2_rvar_rar DECIMAL(65, 8) not NULL,"
            
        + "  vt_mean           DECIMAL(65, 8) not NULL,"  //56
        + "  vt_mean_ln        DECIMAL(65, 8) not NULL,"
        + "  vt_mean_ar        DECIMAL(65, 8) not NULL,"
        + "  vt_mean_ar_ln     DECIMAL(65, 8) not NULL,"
        + "  vt_mean_rar       DECIMAL(65, 8) not NULL,"
        + "  vt_mean_rar_ln    DECIMAL(65, 8) not NULL,"
        + "  vt_mean_var_ar    DECIMAL(65, 8) not NULL,"
        + "  vt_mean_rvar_ar   DECIMAL(65, 8) not NULL,"
        + "  vt_mean_var_rar   DECIMAL(65, 8) not NULL,"
        + "  vt_mean_rvar_rar  DECIMAL(65, 8) not NULL,"
        
        + "  vt_variance       DECIMAL(65, 8) not NULL,"  //66
        + "  vt_variance_ln    DECIMAL(65, 8) not NULL,"
        + "  vt_variance_ar    DECIMAL(65, 8) not NULL,"
        + "  vt_variance_ar_ln DECIMAL(65, 8) not NULL,"
        + "  vt_variance_rar   DECIMAL(65, 8) not NULL,"
        + "  vt_variance_rar_ln   DECIMAL(65, 8) not NULL,"
        + "  vt_variance_var_ar   DECIMAL(65, 8) not NULL,"
        + "  vt_variance_rvar_ar  DECIMAL(65, 8) not NULL,"
        + "  vt_variance_var_rar  DECIMAL(65, 8) not NULL,"
        + "  vt_variance_rvar_rar DECIMAL(65, 8) not NULL,"
        
        + "  vt_stdev          DECIMAL(65, 8) not NULL,"  //76
        + "  vt_stdev_ln       DECIMAL(65, 8) not NULL,"
        + "  vt_stdev_ar       DECIMAL(65, 8) not NULL,"
        + "  vt_stdev_ar_ln    DECIMAL(65, 8) not NULL,"
        + "  vt_stdev_rar      DECIMAL(65, 8) not NULL,"
        + "  vt_stdev_rar_ln   DECIMAL(65, 8) not NULL," 
        + "  vt_stdev_var_ar   DECIMAL(65, 8) not NULL,"
        + "  vt_stdev_rvar_ar  DECIMAL(65, 8) not NULL,"
        + "  vt_stdev_var_rar  DECIMAL(65, 8) not NULL,"
        + "  vt_stdev_rvar_rar DECIMAL(65, 8) not NULL,"

        + "  z_score           DECIMAL(65, 8) not NULL,"  //86
        + "  z_score_ln        DECIMAL(65, 8) not NULL,"
        + "  z_score_ar        DECIMAL(65, 8) not NULL,"
        + "  z_score_ar_ln     DECIMAL(65, 8) not NULL,"
        + "  z_score_rar       DECIMAL(65, 8) not NULL,"
        + "  z_score_rar_ln    DECIMAL(65, 8) not NULL,"
        + "  z_score_var_ar    DECIMAL(65, 8) not NULL,"
        + "  z_score_rvar_ar   DECIMAL(65, 8) not NULL,"
        + "  z_score_var_rar   DECIMAL(65, 8) not NULL,"
        + "  z_score_rvar_rar  DECIMAL(65, 8) not NULL,"
            
        + "  tf_idf            DECIMAL(65, 8) not NULL,"  //96
        + "  INDEX(term) USING HASH"
        + ") ENGINE = INNODB";
    
    Statement stmt = conn.createStatement();
    stmt.execute(sqlCreate);
    
    stmt.close();
  }
  
  /**
   * Create table INVERTED_INDEX in the database (SQL Statements).
   * @param tbl_date  Table date in format "YYYYMMDD"
   * @param conn      SQL Connector
   * @throws SQLException When cration error occurs.
   */
  public static void createTable_InvertedIndex(String tbl_date, Connection conn) throws SQLException {
    
    String sqlCreate = 
        "CREATE TABLE IF NOT EXISTS "+tbl_date+"_inverted_index("
        + "  sys_partition    INT not null,"
        + "  download_date    TIMESTAMP not NULL DEFAULT CURRENT_TIMESTAMP,"
        + "  term             VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin not NULL,"
        + "  id_tweet         BIGINT not NULL,"
        + "  INDEX(download_date) USING BTREE,"
        + "  INDEX(id_tweet)  USING HASH"
        + ") ENGINE = INNODB PARTITION BY HASH(sys_partition) PARTITIONS 24;";
    
    Statement stmt = conn.createStatement();
    stmt.execute(sqlCreate);
  }
  
  /**
   * Create table REPORT_01 in the database (SQL Statements). It contains varios
   * fields from the other tables of the last 24 hours.
   * @param conn  SQL Connector.
   * @param prop  Parameters from the _setup.txt file.
   * @throws SQLException When cration error occurs.
   */
  public static void createTable_report01(Connection conn, PropertiesTD prop) throws SQLException {
    
    String sqlCreate = 
        "CREATE TABLE IF NOT EXISTS v_"+prop.keywords_name+"("
        + "  sys_partition    INT not null,"
        + "  id_tweet         BIGINT not NULL,"
        + "  blacklisted_tweet TINYINT not null," 
        + "  download_date    TIMESTAMP not NULL DEFAULT CURRENT_TIMESTAMP,"
        + "  creation_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
        + "  type             VARCHAR(10) not NULL,"
        + "  lang_tweet       VARCHAR(10) not NULL,"
        + "  text_tweet       TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin not NULL,"
        + "  text_rt          TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  text_quote       TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  has_keyword      TINYINT,"
        + "  country_text     VARCHAR(200),"
        + "  id_user          BIGINT not NULL,"
        + "  repeated_user    TINYINT not null,"
        + "  blacklisted_user TINYINT not null,"
        + "  user_name              VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '',"
        + "  user_screen_name       VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '',"
        + "  user_profile_img_url   TEXT,"
        + "  usr_geo_enabled  TINYINT,"
        + "  usr_location     TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  country_user     VARCHAR(200),"
        + "  geo_located      TINYINT not null,"
        + "  geo_latitude     DECIMAL(65, 8),"
        + "  geo_longitude    DECIMAL(65, 8),"
        + "  place_id            VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  place_name          VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  place_country_code  VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  place_country       VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  place_fullname      VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  place_street_address  VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  place_url           TEXT,"
        + "  place_place_type    VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,"
        + "  ss_positive      TINYINT,"
        + "  ss_negative      TINYINT,"
        + "  ss_neutral       TINYINT,"
        + "  ss_polarity      TINYINT,"
        + "  count_urls       INT not NULL,"
        + "  counter          INT not NULL,"
        + "  PRIMARY KEY(id_tweet,sys_partition) USING HASH,"
        + "  INDEX(download_date) USING BTREE"
        + ") ENGINE = INNODB PARTITION BY HASH(sys_partition) PARTITIONS 24;";
    
    Statement stmt = conn.createStatement();
    stmt.execute(sqlCreate);
  }

  /**
   * Prepares the insert statement for a TWEET for a database (Insert SQL Statements).
   * @param stmt  Insert SQL Statement.
   * @param tweet Tweet instance.
   * @throws SQLException When the setting of fields are not correct.
   */
  public static void insertStatement_Tweets(PreparedStatement stmt, Tweet tweet) throws SQLException {
    
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
   //tweet.getDownloadedAt().getHours()
    stmt.setInt      (1,  Integer.valueOf(TUtils.Date_Formatter(tweet.getDownloadedAt()).substring(9, 11)));       //  sys_partition (Hour)
    stmt.setTimestamp(2 , new Timestamp(tweet.getDownloadedAt().getTime()), cal); //  download_date
    stmt.setTimestamp(3 , new Timestamp(tweet.getCreatedAt().getTime()), cal);    //  creation_date
    stmt.setLong     (4 , tweet.getId());           //  id_tweet
    stmt.setBoolean  (5 , tweet.isBlacklisted());   //  blacklisted_tweet
    stmt.setLong     (6 , tweet.getUser().getId()); //  id_user
    stmt.setBoolean  (7 , tweet.isRepeatedUser());  //  repeated_user
    stmt.setBoolean  (8 , tweet.isFavorited());     //  favorited
    stmt.setBoolean  (9 , tweet.isTruncated());     //  truncated
    stmt.setString   (10, tweet.getType().name());  //  type 
    stmt.setString   (11, tweet.getLanguage());     //  lang_tweet
    stmt.setString   (12, tweet.getText());         //  text_tweet
    stmt.setBoolean  (13, tweet.isRetweet());       //  rt
    stmt.setLong     (14, tweet.getRTCount());      //  rt_count
    if(tweet.isRetweet()){
      stmt.setLong   (15, tweet.getIdRT());          //  rt_id
      stmt.setString (16, tweet.getRTText());        //  text_rt
    }
    else{
      stmt.setNull   (15, java.sql.Types.BIGINT);    //  rt_id
      stmt.setNull   (16, java.sql.Types.VARCHAR);   //  text_rt
    }
    stmt.setBoolean  (17, tweet.isQuote());          //  quote
    if(tweet.isQuote()){
      stmt.setLong   (18, tweet.getIdQuoted());      //  quote_id
      stmt.setString (19, tweet.getTextQuoted());    //  text_quote
    }
    else{
      stmt.setNull   (18, java.sql.Types.BIGINT);    //  quote_id
      stmt.setNull   (19, java.sql.Types.VARCHAR);   //  text_quote
    }
    stmt.setBoolean  (20, tweet.hasKeyword());     //  has_keyword
    stmt.setString   (21, tweet.getSrcHref());     //  src_href
    
    stmt.setString   (22, tweet.getSrcRel());      //  src_rel
    stmt.setString   (23, tweet.getSrcText());     //  src_text
    if(tweet.getIn_reply_to_status_id() != -1)
      stmt.setLong   (24, tweet.getIn_reply_to_status_id());    //  in_reply_to_status_id
    else
      stmt.setNull   (24, java.sql.Types.BIGINT);    //  in_reply_to_status_id
    
    if(tweet.getIn_reply_to_user_id() != -1)
      stmt.setLong   (25, tweet.getIn_reply_to_user_id());      //  in_reply_to_user_id
    else
      stmt.setNull   (25, java.sql.Types.BIGINT);    //  in_reply_to_user_id
    stmt.setString   (26, tweet.getIn_reply_to_screen_name());  //  in_reply_to_screen_name
    if (tweet.getCountriesText() == null){
      stmt.setNull   (27, java.sql.Types.VARCHAR);
    }
    else if(tweet.getCountriesText().isEmpty()) {
      stmt.setNull   (27, java.sql.Types.VARCHAR);
    }
    else{
      String ctries = tweet.getCountriesText().toString();
      stmt.setString (27, ctries.substring(1, ctries.length()-1));   // countries_text
    }
    
    stmt.setBoolean  (28, tweet.isGeolocated());     //  geo_located
    if(tweet.isGeolocated()){
      stmt.setDouble (29, tweet.getGeoLatitude());   //  geo_latitude
      stmt.setDouble (30, tweet.getGeoLongitude());  //  geo_longitude
    }
    else{
      stmt.setNull   (29, java.sql.Types.DECIMAL);    //  geo_latitude
      stmt.setNull   (30, java.sql.Types.DECIMAL);    //  geo_longitude
    }
    
    if(tweet.getPlace() != null){
      stmt.setString (31 , tweet.getPlace().getId());             //  place_id
      stmt.setString (32 , tweet.getPlace().getName());           //  place_name
      stmt.setString (33 , tweet.getPlace().getCountryCode());    //  place_country_code
      stmt.setString (34 , tweet.getPlace().getCountry());        //  place_country
      stmt.setString (35 , tweet.getPlace().getFullName());       //  place_fullname
      stmt.setString (36 , tweet.getPlace().getStreetAddress());  //  place_street_address
      stmt.setString (37 , tweet.getPlace().getUrl());            //  place_url
      stmt.setString (38 , tweet.getPlace().getPlaceType());      //  place_place_type 
    }
    else{
      stmt.setNull   (31 , java.sql.Types.VARCHAR); //  place_id
      stmt.setNull   (32 , java.sql.Types.VARCHAR); //  place_name
      stmt.setNull   (33 , java.sql.Types.VARCHAR); //  place_country_code
      stmt.setNull   (34 , java.sql.Types.VARCHAR); //  place_country
      stmt.setNull   (35 , java.sql.Types.VARCHAR); //  place_fullname
      stmt.setNull   (36 , java.sql.Types.VARCHAR); //  place_street_address
      stmt.setNull   (37 , java.sql.Types.VARCHAR); //  place_url
      stmt.setNull   (38 , java.sql.Types.VARCHAR); //  place_place_type 
    }
    stmt.setInt      (39, 1);

  }
  
  /**
   * Prepares the insert statement for a USER for a database (Insert SQL Statements).
   * @param stmt  Insert SQL Statement.
   * @param tweet Tweet instance.
   * @throws SQLException When the setting of fields are not correct.
   */
  public static void insertStatement_Users(PreparedStatement stmt, Tweet tweet) throws SQLException {
    
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    TUser user = tweet.getUser();

    stmt.setInt      (1 , Integer.valueOf(TUtils.Date_Formatter(tweet.getDownloadedAt()).substring(9, 11)));       //  sys_partition
    stmt.setTimestamp(2 , new Timestamp(tweet.getDownloadedAt().getTime()), cal); //  download_date
    stmt.setTimestamp(3 , new Timestamp(user.getCreated_at().getTime()), cal);    //  creation_date
    stmt.setLong     (4 , tweet.getId());             //  id_tweet
    stmt.setLong     (5 , user.getId());              //  id_user
    stmt.setString   (6 , user.getName());            //  name
    stmt.setString   (7 , user.getScreenName());      //  screen_name
    stmt.setBoolean  (8 , user.isBlacklisted());      //  blacklisted_user
    stmt.setString   (9 , user.getDescription());     //  description
    stmt.setLong     (10, user.getStatusesCount());   //  statuses_count
    stmt.setLong     (11, user.getFavoritesCount()); //  favorites_count
    stmt.setLong     (12, user.getFollowersCount());  //  followers_count
    stmt.setLong     (13, user.getFriendsCount());    //  friends_count
    stmt.setLong     (14, user.getListedCount());     //  listed_count
    stmt.setBoolean  (15, user.isTranslator());       //  translator
    stmt.setBoolean  (16, user.isGeoEnabled());       //  geo_enabled
    stmt.setString   (17, user.getLang());            //  lang_user
    stmt.setString   (18, user.getLocation());        //  location
    if (user.getCountryUser() == null){               //  country_user
      stmt.setNull   (19, java.sql.Types.VARCHAR);
    }
    else if(user.getCountryUser().isEmpty()) {
      stmt.setNull   (19, java.sql.Types.VARCHAR);
    }
    else{
      String ctries = user.getCountryUser().toString();
      stmt.setString (19, ctries.substring(1, ctries.length()-1));   // countries_user
    }
    stmt.setString   (20, user.getTimeZone());        //  timezone
    stmt.setInt      (21, user.getUtcOffset());       //  utc_offset
    stmt.setBoolean  (22, user.isFollowRequestSent());    //  follow_request_sent
    stmt.setBoolean  (23, user.isUProtected());           //  uprotected
    stmt.setBoolean  (24, user.isVerified());             //  verified
    stmt.setBoolean  (25, user.isContributorsEnable());   //  contribution_enable
    stmt.setBoolean  (26, user.isShowAllInlineMedia());   //  show_all_inline_media
    stmt.setString   (27, user.getURL());                 //  url
    stmt.setString   (28, user.getProfileBkgColor());     //  profile_bkg_color
    stmt.setString   (29, user.getProfileBkgImgURL());    //  profile_bkg_img_url
    stmt.setString   (30, user.getProfileImgURL());       //  profile_img_url
    stmt.setString   (31, user.getProfileLinkColor());    //  profile_link_color
    stmt.setString   (32, user.getProfileSidebarBorderColor());   //  profile_sidebar_border_color
    stmt.setString   (33, user.getProfileSidebarColor()); //  profile_sidebar_color
    stmt.setString   (34, user.getProfileTextColor());    //  profile_text_color

  }
  
  /**
   * Prepares the insert statement for a eHashTags for a database (Insert SQL Statements).
   * @param stmt  Insert SQL Statement.
   * @param tweet Tweet instance.
   * @throws SQLException When the setting of fields are not correct.
   */
  public static void insertStatement_eHashTags(PreparedStatement stmt, Tweet tweet) throws SQLException {
    
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    
    for (EntityHashTag ht : tweet.getHashTags()) {

      stmt.setInt      (1 , Integer.valueOf(TUtils.Date_Formatter(tweet.getDownloadedAt()).substring(9, 11)));       //  sys_partition (Hour)
      stmt.setTimestamp(2 , new Timestamp(tweet.getDownloadedAt().getTime()), cal); //  download_date
      stmt.setLong     (3 , tweet.getId());   //  id_tweet
      stmt.setInt      (4 , ht.getStart());   //  idx_start
      stmt.setInt      (5 , ht.getEnd());     //  idx_end
      stmt.setString   (6 , ht.getHashTag()); //  hashtag
      stmt.addBatch();
    }
  }
  
  /** 
   * Prepares the insert statement for a eUserRefs for a database (Insert SQL Statements).
   * @param stmt  Insert SQL Statement.
   * @param tweet Tweet instance.
   * @throws SQLException When the setting of fields are not correct.
   */
  public static void insertStatemnet_eUserRefs(PreparedStatement stmt, Tweet tweet) throws SQLException {

    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    
    for (EntityUserRef ur : tweet.getUserRefs()) {      

      if(ur.getName() == null)
        continue;
      
      stmt.setInt      (1 , Integer.valueOf(TUtils.Date_Formatter(tweet.getDownloadedAt()).substring(9, 11)));       //  sys_partition (Hour)
      stmt.setTimestamp(2 , new Timestamp(tweet.getDownloadedAt().getTime()), cal); //  download_date
      stmt.setLong     (3 , tweet.getId());       //  id_tweet
      stmt.setInt      (4 , ur.getStart());       //  idx_start
      stmt.setInt      (5 , ur.getEnd());         //  idx_end
      stmt.setLong     (6 , ur.getUID());         //  id_user
      stmt.setString   (7 , ur.getName());        //  user_name
      stmt.setString   (8 , ur.getScreenName());  //  user_screen_name

      stmt.addBatch();
    }
  }
  
  /** 
   * Prepares the insert statement for a eURLs for a database (Insert SQL Statements).
   * @param stmt  Insert SQL Statement.
   * @param tweet Tweet instance.
   * @throws SQLException When the setting of fields are not correct.
   */
  public static void insertStatement_eURLs(PreparedStatement stmt, Tweet tweet) throws SQLException {

    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    
    for (EntityURL url : tweet.getURLs()) {

      stmt.setInt      (1 , Integer.valueOf(TUtils.Date_Formatter(tweet.getDownloadedAt()).substring(9, 11)));       //  sys_partition (Hour)
      stmt.setTimestamp(2 , new Timestamp(tweet.getDownloadedAt().getTime()), cal); //  download_date
      stmt.setLong     (3 , tweet.getId());         //  id_tweet
      stmt.setInt      (4 , url.getStart());        //  idx_start
      stmt.setInt      (5 , url.getEnd());          //  idx_end
      stmt.setString   (6 , url.getURL());          //  url_short
      stmt.setString   (7 , url.getExpandedURL());  //  url_long
      
      stmt.addBatch();
    }
  }
  
  /** 
   * Prepares the insert statement for a eMedias for a database (Insert SQL Statements).
   * @param stmt  Insert SQL Statement.
   * @param tweet Tweet instance.
   * @throws SQLException When the setting of fields are not correct.
   */
  public static void insertStatement_eMedias(PreparedStatement stmt, Tweet tweet) throws SQLException {

    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    
    for (EntityMedia me : tweet.getMedias()) {

      stmt.setInt      (1 , Integer.valueOf(TUtils.Date_Formatter(tweet.getDownloadedAt()).substring(9, 11)));       //  sys_partition (Hour)
      stmt.setTimestamp(2 , new Timestamp(tweet.getDownloadedAt().getTime()), cal); //  download_date
      stmt.setLong     (3 , tweet.getId());         //  id_tweet
      stmt.setLong     (4 , me.getId());            //  id_media
      stmt.setString   (5 , me.getMediaURL());      //  url
      stmt.setString   (6 , me.getMediaURLHttps()); //  url_https
      stmt.setString   (7 , me.getType());          //  type
      stmt.setString   (8 , me.getSizes());         //  sizes

      stmt.addBatch();
    }
    
  }
  
  /** 
   * Prepares the insert statement for a SENTI for a database (Insert SQL Statements).
   * @param stmt  Insert SQL Statement.
   * @param tweet Tweet instance.
   * @throws SQLException When the setting of fields are not correct.
   */
  public static void insertStatement_Senti(PreparedStatement stmt, Tweet tweet) throws SQLException {
        
    TSenti ss = tweet.getSentiment();
    
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    stmt.setInt      (1 , Integer.valueOf(TUtils.Date_Formatter(tweet.getDownloadedAt()).substring(9, 11)));       //  sys_partition (Hour)
    stmt.setTimestamp(2 , new Timestamp(tweet.getDownloadedAt().getTime()), cal); //  download_date
    stmt.setLong     (3 , tweet.getId());     //  id_tweet
    stmt.setString   (4 , ss.getLang());      //  ss_lang
    stmt.setByte     (5 , ss.getPositive());  //  ss_positive
    stmt.setByte     (6 , ss.getNegative());   //  ss_negative
    stmt.setByte     (7 , ss.getNeutral());   //  ss_neutral
    stmt.setByte     (8 , ss.getPolarity());  //  ss_polarity

  }
  
  /** 
   * Prepares the insert statement for a NGrams for a database (Insert SQL Statements).
   * @param stmt  Insert SQL Statement.
   * @param ngram Ngram to insert.
   * @throws SQLException When the setting of fields are not correct.
   */
  public static void insertStatement_NGrams(PreparedStatement stmt, NGramRow ngram) throws SQLException {
    
    stmt.setString   (1 , ngram._id);         //  id
    stmt.setString   (2 , ngram._term);       //  term
    stmt.setInt      (3 , ngram._n);          //  n
    stmt.setBoolean  (4 , ngram._rt);         //  rt
    stmt.setString   (5 , ngram._lang_tweet); //  lang_tweet
    stmt.setLong     (6 , ngram._freq_term);  //  freq_term
    stmt.setLong     (7 , ngram._freq_tweet); //  freq_tweet
    
  }

  /**
   * Prepares the insert statement for a EventBT for a database (Insert SQL Statements).
   * @param tbl_date      Table date in YYYYMMDD.
   * @param BagTimeStamp  Timestamp of the WindowRow.
   * @param rank          Position Rank in the window.
   * @param term          Term name of the signal.
   * @param w_row         WindowRow to insert.
   * @param isValid       If the WindowRow is valid:  0=New  1=valid -1=zero_freq
   * @param conn  SQL Connector.
   * @return The number of rows inserted.
   */
  public static int insertStatement_EEventBD(String tbl_date, Date BagTimeStamp, int rank, String term, WindowRow w_row, int isValid, Connection conn) {
    PreparedStatement stmt = null;
        
    try{
      stmt = conn.prepareStatement("insert into "+ tbl_date +"_event_bt "
           + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                   +"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                   +"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                   +"?,?,?,?,?,?)"
           + "ON DUPLICATE KEY UPDATE rank=rank;");
      Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      
      stmt.setTimestamp(1 , new Timestamp(w_row.getW1().getTimeStamp_End().getTime()));  //  window_date
      stmt.setInt      (2 , rank);                    //  rank
      stmt.setString   (3 , term);                    //  term
      stmt.setLong     (4 , w_row.getWindowTime());   //  window_size_seg
      
      stmt.setTimestamp(5 , new Timestamp(w_row.getW1().getTimeStamp().getTime()), cal);  //  w1__date
      stmt.setLong     (6 , w_row.getW1().getTotalWindowFreq());  //  w1__freq_t
      stmt.setLong     (7 , w_row.getW1().getFrequency());        //  w1__freq
      stmt.setDouble   (8 , w_row.getW1().getFrequencyLN());      //  w1__freq_ln
      stmt.setDouble   (9 , w_row.getW1().getVelocity());         //  w1__ar
      stmt.setDouble   (10, w_row.getW1().getVelocityLN());       //  w1__ar_ln
      stmt.setDouble   (11, w_row.getW1().getRelevance());        //  w1__rar
      stmt.setDouble   (12, w_row.getW1().getRelevanceLN());      //  w1__rar_ln
      
      
      stmt.setTimestamp(13, new Timestamp(w_row.getW2().getTimeStamp().getTime()), cal);  //  w2__date
      stmt.setLong     (14, w_row.getW2().getTotalWindowFreq()); //  w2__freq_t
      stmt.setLong     (15, w_row.getW2().getFrequency());       //  w2__freq
      stmt.setDouble   (16, w_row.getW2().getFrequencyLN());     //  w2__freq_ln
      stmt.setDouble   (17, w_row.getW2().getVelocity());        //  w2__ar
      stmt.setDouble   (18, w_row.getW2().getVelocityLN());      //  w2__ar_ln
      stmt.setDouble   (19, w_row.getW2().getRelevance());       //  w2__rar
      stmt.setDouble   (20, w_row.getW2().getRelevanceLN());     //  w2__rar_ln

      stmt.setLong     (21, w_row.getVar_Freq());       //  var_freq
      stmt.setDouble   (22, w_row.getVar_Freq_LN());    //  var_freq_ln
      stmt.setDouble   (23, w_row.getVar_Vel());        //  var_ar
      stmt.setDouble   (24, w_row.getVar_Vel_LN());     //  var_ar_ln
      stmt.setDouble   (25, w_row.getVar_Rel());        //  var_rar
      stmt.setDouble   (26, w_row.getVar_Rel_LN());     //  var_rar_ln
      
      stmt.setDouble   (27, w_row.getVarRel_Freq());    //  rel_var_freq
      stmt.setDouble   (28, w_row.getVarRel_Freq_LN()); //  rel_var_freq_ln
      stmt.setDouble   (29, w_row.getVarRel_Vel());     //  rel_var_ar
      stmt.setDouble   (30, w_row.getVarRel_Vel_LN());  //  rel_var_ar_ln
      stmt.setDouble   (31, w_row.getVarRel_Rel());     //  rel_var_rar
      stmt.setDouble   (32, w_row.getVarRel_Rel_LN());  //  rel_var_rar_ln
      stmt.setInt      (33, isValid);                   //  valid
      stmt.setLong     (34, w_row.getNW());             //  n_win_term
      stmt.setLong     (35, w_row.getNW0());            //  vt_nro_windows
      
      stmt.setLong     (36, w_row.getSumX());           //  vt_sum_x
      stmt.setDouble   (37, w_row.getSumX_LN());        //  vt_sum_x_ln
      stmt.setDouble   (38, w_row.getSumXVel());        //  vt_sum_x_ar
      stmt.setDouble   (39, w_row.getSumXVel_LN());     //  vt_sum_x_ar_ln
      stmt.setDouble   (40, w_row.getSumXRel());        //  vt_sum_x_rar_ln
      stmt.setDouble   (41, w_row.getSumXRel_LN());     //  vt_sum_x_rar_ln
      stmt.setDouble   (42, w_row.getSumXVarVel());     //  vt_sum_x_var_ar
      stmt.setDouble   (43, w_row.getSumXRelVarVel());  //  vt_sum_x_rel_var_rar
      stmt.setDouble   (44, w_row.getSumXVarRel());     //  vt_sum_x_var_ar
      stmt.setDouble   (45, w_row.getSumXRelVarRel());  //  vt_sum_x_rel_var_rar
      
      stmt.setLong     (46, w_row.getSumX2());          //  vt_sum_x2
      stmt.setDouble   (47, w_row.getSumX2_LN());       //  vt_sum_x2_ln
      stmt.setDouble   (48, w_row.getSumX2Vel());       //  vt_sum_x2_ar
      stmt.setDouble   (49, w_row.getSumX2Vel_LN());    //  vt_sum_x2_ar_ln
      stmt.setDouble   (50, w_row.getSumX2Rel());       //  vt_sum_x2_rar
      stmt.setDouble   (51, w_row.getSumX2Rel_LN());    //  vt_sum_x2_rar_ln
      stmt.setDouble   (52, w_row.getSumX2VarVel());    //  vt_sum_x2_var_ar
      stmt.setDouble   (53, w_row.getSumX2RelVarVel()); //  vt_sum_x2_rel_var_rar
      stmt.setDouble   (54, w_row.getSumX2VarRel());    //  vt_sum_x2_var_ar
      stmt.setDouble   (55, w_row.getSumX2RelVarRel()); //  vt_sum_x2_rel_var_rar
      
      stmt.setDouble   (56, w_row.getMean());           //  vt_mean
      stmt.setDouble   (57, w_row.getMean_LN());        //  vt_mean_ln
      stmt.setDouble   (58, w_row.getMeanVel());        //  vt_mean_ar
      stmt.setDouble   (59, w_row.getMeanVel_LN());     //  vt_mean_ar_ln
      stmt.setDouble   (60, w_row.getMeanRel());        //  vt_mean_rar
      stmt.setDouble   (61, w_row.getMeanRel_LN());     //  vt_mean_rar_ln
      stmt.setDouble   (62, w_row.getMeanVarVel());     //  vt_mean_var_ar
      stmt.setDouble   (63, w_row.getMeanRelVarVel());  //  vt_mean_rel_var_rar
      stmt.setDouble   (64, w_row.getMeanVarRel());     //  vt_mean_var_ar
      stmt.setDouble   (65, w_row.getMeanRelVarRel());  //  vt_mean_rel_var_rar
      
      stmt.setDouble   (66, w_row.getVar());            //  vt_variance
      stmt.setDouble   (67, w_row.getVar_LN());         //  vt_variance_ln
      stmt.setDouble   (68, w_row.getVarVel());         //  vt_variance_ar
      stmt.setDouble   (69, w_row.getVarVel_LN());      //  vt_variance_ar_ln
      stmt.setDouble   (70, w_row.getVarRel());         //  vt_variance_rar
      stmt.setDouble   (71, w_row.getVarRel_LN());      //  vt_variance_rar_ln
      stmt.setDouble   (72, w_row.getVarVarVel());      //  vt_variance_var_ar
      stmt.setDouble   (73, w_row.getVarRelVarVel());   //  vt_variance_rel_var_rar
      stmt.setDouble   (74, w_row.getVarVarRel());      //  vt_variance_var_ar
      stmt.setDouble   (75, w_row.getVarRelVarRel());   //  vt_variance_rel_var_rar
      
      stmt.setDouble   (76, w_row.getStdev());          //  vt_stddev
      stmt.setDouble   (77, w_row.getStdev_LN());       //  vt_stddev_ln
      stmt.setDouble   (78, w_row.getStdevVel());       //  vt_stddev_ar
      stmt.setDouble   (79, w_row.getStdevVel_LN());    //  vt_stddev_ar_ln
      stmt.setDouble   (80, w_row.getStdevRel());       //  vt_stddev_rar
      stmt.setDouble   (81, w_row.getStdevRel_LN());    //  vt_stddev_rar_ln
      stmt.setDouble   (82, w_row.getStdevVarVel());    //  vt_stddev_var_ar
      stmt.setDouble   (83, w_row.getStdevRelVarVel()); //  vt_stddev_rel_var_rar
      stmt.setDouble   (84, w_row.getStdevVarRel());    //  vt_stddev_var_ar
      stmt.setDouble   (85, w_row.getStdevRelVarRel()); //  vt_stddev_rel_var_rar
      
      stmt.setDouble   (86, w_row.getZScore());          //  vt_ZScore
      stmt.setDouble   (87, w_row.getZScore_LN());       //  vt_ZScore_ln
      stmt.setDouble   (88, w_row.getZScoreVel());       //  vt_ZScore_ar
      stmt.setDouble   (89, w_row.getZScoreVel_LN());    //  vt_ZScore_ar_ln
      stmt.setDouble   (90, w_row.getZScoreRel());       //  vt_ZScore_rar
      stmt.setDouble   (91, w_row.getZScoreRel_LN());    //  vt_ZScore_rar_ln
      stmt.setDouble   (92, w_row.getZScoreVarVel());    //  vt_ZScore_var_ar
      stmt.setDouble   (93, w_row.getZScoreRelVarVel()); //  vt_ZScore_rel_var_rar
      stmt.setDouble   (94, w_row.getZScoreVarRel());    //  vt_ZScore_var_ar
      stmt.setDouble   (95, w_row.getZScoreRelVarRel()); //  vt_ZScore_rel_var_rar
      
      stmt.setDouble   (96, w_row.getTF_IDF());      //  vt_TF_IDF
      
      stmt.executeUpdate();
      return 1;
    }
    catch (SQLException se) {
      se.printStackTrace();
      System.err.println(stmt.toString());
      System.err.println(AgentDBStorer.class.getName());
      System.err.println(se.getMessage());
      System.err.println(se.getSQLState());
    }
    finally{
      try{
        if(stmt != null)
          stmt.close();
      }
      catch(SQLException e){}
        
    }
    return 0;
  }

  /**
   * Prepares the insert statement for a InvertedIndex for a database (Insert SQL Statements).
   * @param stmt  Insert SQL Statement.
   * @param keywords  Set of keywords to insert.
   * @param tweet Tweet instance.
   * @throws SQLException When the setting of fields are not correct.
   */
  public static void insertStatement_InvertedIndex(PreparedStatement stmt, Set<String> keywords, Tweet tweet) throws SQLException {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    
    for (String term : keywords){  
      stmt.setInt      (1 , Integer.valueOf(TUtils.Date_Formatter(tweet.getDownloadedAt()).substring(9, 11)));       //  sys_partition (Hour)
      stmt.setTimestamp(2 , new Timestamp(tweet.getDownloadedAt().getTime()), cal); //  download_date
      stmt.setString   (3 , term);      //  term
      stmt.setLong     (4 , tweet.getId());  //  id_tweet
      
      stmt.addBatch();
    }
  }
  
    /** 
   * Prepares the insert of the V_... report into the database (Insert SQL Statements).
   * @param stmt  Insert SQL Statement.
   * @param tweet Tweet to insert.
   * @throws SQLException When the setting of fields are not correct.
   */
  public static void insertStatement_report01(PreparedStatement stmt, Tweet tweet) throws SQLException {
    
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    
    stmt.setInt      (1 , Integer.valueOf(TUtils.Date_Formatter(tweet.getDownloadedAt()).substring(9, 11)));       //  sys_partition (Hour)
    stmt.setLong     (2 , tweet.getId());          //  id_tweet
    stmt.setBoolean  (3 , tweet.isBlacklisted());   //  blacklisted_tweet
    stmt.setTimestamp(4 , new Timestamp(tweet.getDownloadedAt().getTime()), cal); //  download_date
    stmt.setTimestamp(5 , new Timestamp(tweet.getCreatedAt().getTime()), cal);    //  creation_date
    stmt.setString   (6 , tweet.getType().name()); //  type
    stmt.setString   (7 , tweet.getLanguage());    //  lang_tweet
    stmt.setString   (8 , tweet.getText());        //  text_tweet
    if(tweet.isRetweet()){                         // text_rt
      stmt.setString (9 , tweet.getRTText());
    }
    else{
      stmt.setNull   (9 , java.sql.Types.VARCHAR);
    }
    if(tweet.isQuote()){                           // text_quote
      stmt.setString (10, tweet.getTextQuoted());
    }
    else{
      stmt.setNull   (10, java.sql.Types.VARCHAR);
    }
    stmt.setBoolean  (11, tweet.hasKeyword());     //  has_keyword
    if (tweet.getCountriesText() == null){
      stmt.setNull   (12, java.sql.Types.VARCHAR);
    }
    else if(tweet.getCountriesText().isEmpty()) {
      stmt.setNull   (12, java.sql.Types.VARCHAR);
    }
    else{
      String ctries = tweet.getCountriesText().toString();
      stmt.setString (12, ctries.substring(1, ctries.length()-1));   // countries_user
    } 
    stmt.setLong     (13, tweet.getUser().getId());            // id_user
    stmt.setBoolean  (14, tweet.isRepeatedUser());             // repeated_user
    stmt.setBoolean  (15, tweet.getUser().isBlacklisted());    // blacklisted_user
    stmt.setString   (16, tweet.getUser().getName());          // name
    stmt.setString   (17, tweet.getUser().getScreenName());    // screen_name
    stmt.setString   (18, tweet.getUser().getProfileImgURL()); // profile_img_url
    stmt.setBoolean  (19, tweet.getUser().isGeoEnabled());     // usr_geo_enabled
    stmt.setString   (20, tweet.getUser().getLocation());      // usr_location
    if (tweet.getUser().getCountryUser() == null){
      stmt.setNull   (21, java.sql.Types.VARCHAR);
    }
    else if(tweet.getUser().getCountryUser().isEmpty()) {
      stmt.setNull   (21, java.sql.Types.VARCHAR);
    }
    else{
      String ctries = tweet.getUser().getCountryUser().toString();
      stmt.setString (21, ctries.substring(1, ctries.length()-1));   // countries_user
    }
    stmt.setBoolean  (22, tweet.isGeolocated());    //  geo_located
    if(tweet.isGeolocated()){
      stmt.setDouble (23, tweet.getGeoLatitude());   //  geo_latitude
      stmt.setDouble (24, tweet.getGeoLongitude());  //  geo_longitude
    }
    else{
      stmt.setNull   (23, java.sql.Types.DOUBLE);    //  geo_latitude
      stmt.setNull   (24, java.sql.Types.DOUBLE);    //  geo_longitude
    }
    
    if(tweet.getPlace() != null){
      stmt.setString (25, tweet.getPlace().getId());             //  place_id
      stmt.setString (26, tweet.getPlace().getName());           //  place_name
      stmt.setString (27, tweet.getPlace().getCountryCode());    //  place_country_code
      stmt.setString (28, tweet.getPlace().getCountry());        //  place_country
      stmt.setString (29, tweet.getPlace().getFullName());       //  place_fullname
      stmt.setString (30, tweet.getPlace().getStreetAddress());  //  place_street_address
      stmt.setString (31, tweet.getPlace().getUrl());            //  place_url
      stmt.setString (32, tweet.getPlace().getPlaceType());      //  place_place_type 
    }
    else{
      stmt.setNull   (25, java.sql.Types.VARCHAR); //  place_id
      stmt.setNull   (26, java.sql.Types.VARCHAR); //  place_name
      stmt.setNull   (27, java.sql.Types.VARCHAR); //  place_country_code
      stmt.setNull   (28, java.sql.Types.VARCHAR); //  place_country
      stmt.setNull   (29, java.sql.Types.VARCHAR); //  place_fullname
      stmt.setNull   (30, java.sql.Types.VARCHAR); //  place_street_address
      stmt.setNull   (31, java.sql.Types.VARCHAR); //  place_url
      stmt.setNull   (32, java.sql.Types.VARCHAR); //  place_place_type 
    }
    if(tweet.getSentiment() != null){
      stmt.setByte   (33, tweet.getSentiment().getPositive());   //  ss_positive
      stmt.setByte   (34, tweet.getSentiment().getNegative());   //  ss_negative
      stmt.setByte   (35, tweet.getSentiment().getNeutral());    //  ss_neutral
      stmt.setByte   (36, tweet.getSentiment().getPolarity());   //  ss_polarity
    }
    else{
      stmt.setNull   (33, java.sql.Types.SMALLINT); //  ss_positive
      stmt.setNull   (34, java.sql.Types.SMALLINT); //  ss_negative
      stmt.setNull   (35, java.sql.Types.SMALLINT); //  ss_neutral
      stmt.setNull   (36, java.sql.Types.SMALLINT); //  ss_polarity
    }
    stmt.setInt      (37, tweet.getURLs().size());  // count_urls
    stmt.setInt      (38, 1);   // counter
    
  }
}
