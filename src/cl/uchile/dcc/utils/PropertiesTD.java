package cl.uchile.dcc.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
* This Class the parameters to use in all the system and loaded from _setup.txt
* file.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public final class PropertiesTD {

  /////////////////////      TWITTER API CONNECTION     ////////////////////////
  /** Listener Name. In case of multiple streams, it should be different for each one. */
  public String listener_src_name = "";

  /** AuthConsumerKeys generated in the Developer Twitter account. */
  public List<String> AuthConsumerKey = new ArrayList<>();
  /** AuthConsumerSecrets generated in the Developer Twitter account. */
  public List<String> AuthConsumerSecret = new ArrayList<>();
  /** AuthAccessToken generated in the Developer Twitter account. */
  public List<String> AuthAccessToken = new ArrayList<>();
  /** AuthAccessToken generated in the Developer Twitter account. */
  public List<String> AuthAccessTokenSecret = new ArrayList<>();
  
  /** StreamBaseURL address to connect the Listener to the Twitter Stream. */
  public String StreamBaseURL = "";
  /** RequestTokenURL address to connect the Listener to the Twitter Stream. */
  public String RequestTokenURL = "";
  /** AuthorizationURL address to connect the Listener to the Twitter Stream. */
  public String AuthorizationURL = "";
  /** AccessTokenURL address to connect the Listener to the Twitter Stream. */
  public String AccessTokenURL = "";
  /** AuthenticationURL address to connect the Listener to the Twitter Stream. */
  public String AuthenticationURL = "";
  
  /** Listener: List of languages for the downloading tweets (2 ISO Code in Uppercase, ALL to all languages). */
  public String[] tweet_languages;
  /** Listener: FLAG include Research random sample (1% from FireHose). */
  public boolean    research_sample = false;
  /** Listener: Name for the Query per keywords (summarizaed). "No spaces".*/
  public String     keywords_name;
  /** Listener: Query per keywords. */
  public Set<String>  keywords = new HashSet();
  /** Listener: Query per Bounding Box. */
  public double[][] boundingBox  = {{-999,-999},{-999,-999}};
  
  /** FLAG to filter repeated users in a range of time. */
  public boolean repeated_user = false;
  /** Range of time to ignore tweets of repeated users (in seconds). */
  public int repeated_user_time;
  /** Debbug repeated users list (interval time in seconds). */
  public int repeated_user_debug_time;
  
  /////////////////////      MYSQL Database CONNECTION - OUTPUT   //////////////
  /** MySQL Server IP Address. */
  public String mysql_server_ip = "";
  /** MySQL Server port number. */
  public String mysql_server_port = "";
  /** MySQL Database name. */
  public String mysql_server_database = "";
  /** MySQL Server User name. */
  public String mysql_user = "";
  /** MySQL Server User Password. */
  public String mysql_password = "";
  
  /** FLAG to store NGrams per character. */
  public boolean store_ngrams = false;
  /** FLAG to store RT Tweets. */
  public boolean store_rt = false;
  /** FLAG to store QUITED Tweets. */
  public boolean store_qt = false;
  
/////////////////////      MYSQL Database CONNECTION - SOURCE   //////////////
  /** MySQL SRC Server IP Address. */
  public String mysql_src_server_ip = "";
  /** MySQL SRC Server port number. */
  public String mysql_src_server_port = "";
  /** MySQL SRC Database name. */
  public String mysql_src_server_database = "";
  /** MySQL SRC Server User name. */
  public String mysql_src_user = "";
  /** MySQL SRC Server User Password. */
  public String mysql_src_password = "";
  /** MySQL SRC List of dates to process. */
  public String[] mysql_src_date_list;
  /** Database SRC thread wait seconds. */
  public long mysql_src_delay_seconds;
  /** Generate HashTags from text_tweet. */
  public boolean mysql_src_generate_entities_from_text = false;
  /** Load a complete window from a database. */
  public boolean mysql_src_load_complete_batch;
  
  /////////////////////      EXTERNAL FILES     ////////////////////////////////
  /** PATH to the forder with the files with stopwords. */
  public String StopWords_Data;
  /** PATH to the file with the list of countries in many languages. */
  public String country_detect_path;
  /** FLAG to Backup stats of the signals. */
  public boolean stats_serialize_store;
  /** PATH to the file to backup the stats of the signals. */
  public String stats_serialize_path;
  /** Frequency of serizalization */
  public int stats_serialize_win_freq;  
  /** FLAG to Backup EventBT QUEUE. */
  public boolean events_serialize_store;
  /** PATH to the file to backup the EventBT QUEUE. */
  public String events_serialize_path;  
  /** PATH to the subforders with the sentiment dictionaries files. */
  public Map<String, String> ss_data= new HashMap<>();
  /** PATH to the container forder with the sentiment dictionaries folder. */
  public String ss_path = "";
  /** PATH Folder names with the sentiment dictionaries. */
  public String[] ss_data_str;
  /** Time in seconds to refresh the blacklists. */
  public long blacklist_time_refresh;
  /** PATH of Blacklist file of USERS. */
  public String blacklist_users_path;
  /** PATH of Blacklist file of KEYWORDS. */
  public String blacklist_keywords_path;
  /** List of languages to tokenize with StanfordNLP.  (2 ISO Code in Uppercase, ALL to all languages) */
  public String[] stanford_nlp_languages;
 
  ////////////////////////      EVENT DETECTION     ////////////////////////////
  /** FLAG to detect emerging events.  */
  public boolean emerging_event_detection;
  /** FLAG to describe emerging events.  */
  public boolean emerging_event_description;
  /** Minimum Z-score to get bursts.   */
  public double event_zscore_minimum;
  /** Minimum Variation in Frequency to get bursts.   */
  public int event_var_freq_minimum;
  /** Minimum Frequency to get bursts.   */
  public int describer_freq_minimum;
  /** Minimum Frequency to get bursts.   */
  public int describer_freq_maximum;
  /** Name of the algorithm to use (apriori | fpgrowth)  */
  public String describer_algorithm;
  /** Number of Bursty terms to show.   */
  public int describer_bursty_n;
  /** time to wait up to kill the describer process.   */
  public int  describer_last_start_kill_time;
  /** Minimum number of windows processes to apply for prunning.   */
  public int prunning_min_windows = 30;
  /** Prunning threshold to delete (deletes the tail).   */
  public double prunning_threshold = 0.03;
  
  /** List of languages for the event detection (2 ISO Code in Uppercase, ALL to all languages). */
  public String[] event_languages;
  /** Number of processes to detect bursts. */
  public int event_n_detectors;
  /** Window Size in seconds. */
  public long event_window_size;
  /** Window Shift when many processes are used. (window_shift = window_size / n_detectors)  */
  public long event_window_shift;
  
  /** FLAG to track the Signals of Keywords. */
  public boolean track_signal_bursty_keywords;
  /** FLAG to track the Signals of QueryKeyword. */
  public boolean track_signal_keywords;
  /** FLAG to track the Signals of Countries of Users. */
  public boolean track_signal_geo_user;
  /** FLAG to track the Signals of Countries in Text. */
  public boolean track_signal_geo_text;
  /** FLAG to track the Signals of Countries per GeoLocation */
  public boolean track_signal_geo_location;
  /** FLAG to track the Signals of Sentiment Categories. */
  public boolean track_signal_sentiment;
  /** FLAG to track the Signals of Languages. */
  public boolean track_signal_language;
  
  
  /**
   * Load all the paramethers specified in the setup_file.
   * @param setup_file Path of the _setup.txt file.
   */
  public PropertiesTD(String setup_file) {
    try {

      System.out.println("Reading file: " + setup_file);
      File file = new File(setup_file);
      Scanner input = new Scanner(file);

      while (input.hasNextLine()) {
        String line = input.nextLine();
        String[] items = line.split(" : ");
        if(items.length == 2){
          switch(items[0]){
            case "listener_src_name":     listener_src_name  = items[1]; break;
            case "AuthConsumerKey":       AuthConsumerKey.add(items[1]); break;
            case "AuthConsumerSecret":    AuthConsumerSecret.add(items[1]); break;
            case "AuthAccessToken":       AuthAccessToken.add(items[1]); break;
            case "AuthAccessTokenSecret": AuthAccessTokenSecret.add(items[1]); break;
            case "StreamBaseURL":         StreamBaseURL  = items[1]; break;
            case "RequestTokenURL":       RequestTokenURL  = items[1]; break;
            case "AuthorizationURL":      AuthorizationURL  = items[1]; break;
            case "AccessTokenURL":        AccessTokenURL  = items[1]; break;
            case "AuthenticationURL":     AuthenticationURL  = items[1]; break;
            case "mysql_server_ip":       mysql_server_ip  = items[1]; break;
            case "mysql_server_port":     mysql_server_port  = items[1]; break;
            case "mysql_server_database": mysql_server_database  = items[1]; break;
            case "mysql_user":            mysql_user  = items[1]; break;
            case "mysql_password":        mysql_password  = items[1]; break;
            case "mysql_src_server_ip":       mysql_src_server_ip  = items[1]; break;
            case "mysql_src_server_port":     mysql_src_server_port  = items[1]; break;
            case "mysql_src_server_database": mysql_src_server_database  = items[1]; break;
            case "mysql_src_user":            mysql_src_user  = items[1]; break;
            case "mysql_src_password":        mysql_src_password  = items[1]; break;
            case "mysql_src_delay_seconds":   mysql_src_delay_seconds  = Integer.valueOf(items[1]); break;
            case "mysql_src_generate_entities_from_text": mysql_src_generate_entities_from_text =  Boolean.valueOf(items[1]); break;
            case "mysql_src_date_list":       mysql_src_date_list  = items[1].split(","); break;
            case "mysql_src_load_complete_batch": mysql_src_load_complete_batch  = Boolean.valueOf(items[1]); break;
            case "emerging_event_detection":    emerging_event_detection = Boolean.valueOf(items[1]); break;
            case "emerging_event_description":  emerging_event_description = Boolean.valueOf(items[1]); break;
            case "store_rt":              store_rt  = Boolean.valueOf(items[1]); break;
            case "store_qt":              store_qt  = Boolean.valueOf(items[1]); break;
            case "store_ngrams":          store_ngrams  = Boolean.valueOf(items[1]); break;
            case "StopWords_Data":        StopWords_Data  = items[1]; break;
            case "event_zscore_minimum":   event_zscore_minimum  = Double.valueOf(items[1]); break;
            case "event_var_freq_minimum":  event_var_freq_minimum  = Integer.valueOf(items[1]); break;
            case "describer_freq_minimum":  describer_freq_minimum  = Integer.valueOf(items[1]); break;
            case "describer_freq_maximum":  describer_freq_maximum  = Integer.valueOf(items[1]); break;
            case "describer_algorithm":   describer_algorithm = items[1].toLowerCase(); break;
            case "describer_bursty_n":    describer_bursty_n =  Integer.valueOf(items[1]); break;
            case "describer_last_start_kill_time" : describer_last_start_kill_time = Integer.valueOf(items[1]); break;
            case "prunning_min_windows":  prunning_min_windows = Integer.valueOf(items[1]); break;
            case "prunning_threshold":    prunning_threshold = Double.valueOf(items[1]); break;
            case "event_n_detectors":     event_n_detectors = Integer.valueOf(items[1]); break;
            case "event_window_size":     event_window_size  = Long.valueOf(items[1]); break;
            case "event_window_shift":    event_window_shift = Long.valueOf(items[1]); break;
            case "repeated_user":         repeated_user = Boolean.valueOf(items[1]); break;
            case "repeated_user_time":    repeated_user_time = Integer.valueOf(items[1]); break;
            case "repeated_user_debug_time":    repeated_user_debug_time = Integer.valueOf(items[1]); break;
            case "research_sample":       research_sample  = Boolean.valueOf(items[1]); break;
            case "boundingBox_SW_Long":   boundingBox[0][0]  = Double.valueOf(items[1]); break;
            case "boundingBox_SW_Lat":    boundingBox[0][1]  = Double.valueOf(items[1]); break;
            case "boundingBox_NE_Long":   boundingBox[1][0]  = Double.valueOf(items[1]); break;
            case "boundingBox_NE_Lat":    boundingBox[1][1]  = Double.valueOf(items[1]); break;
            case "keywords":              keywords.addAll(Arrays.asList(items[1].split(",")));break;
            case "stanford_nlp_languages" : stanford_nlp_languages = items[1].split(","); break;
            case "event_languages":       event_languages  = items[1].split(","); break;
            case "tweet_languages":       tweet_languages  = items[1].split(","); break;
            case "track_signal_bursty_keywords":  track_signal_bursty_keywords = Boolean.valueOf(items[1]); break;
            case "track_signal_keywords": track_signal_keywords = Boolean.valueOf(items[1]); break;
            case "track_signal_geo_user": track_signal_geo_user = Boolean.valueOf(items[1]); break;
            case "track_signal_geo_text": track_signal_geo_text = Boolean.valueOf(items[1]); break;
            case "track_signal_geo_location": track_signal_geo_location = Boolean.valueOf(items[1]); break;
            case "track_signal_sentiment":  track_signal_sentiment = Boolean.valueOf(items[1]); break;
            case "track_signal_language": track_signal_language = Boolean.valueOf(items[1]); break;
            case "country_detect_path":   country_detect_path = items[1]; break;
            case "blacklist_time_refresh": blacklist_time_refresh = Long.valueOf(items[1]); break;
            case "blacklist_users_path":    blacklist_users_path = items[1]; break;
            case "blacklist_keywords_path": blacklist_keywords_path = items[1]; break;
            case "stats_serialize_store": stats_serialize_store = Boolean.valueOf(items[1]); break;
            case "stats_serialize_path":  stats_serialize_path = items[1]; break;
            case "stats_serialize_win_freq":  stats_serialize_win_freq = Integer.valueOf(items[1]); break;
            case "events_serialize_store": events_serialize_store = Boolean.valueOf(items[1]); break;
            case "events_serialize_path":  events_serialize_path = items[1]; break;
            case "ss_path":               ss_path  = items[1]; break;
            case "keywords_name":         keywords_name = items[1]; break;
            case "SentiStreight_Data":    ss_data_str = items[1].split("\\},");
                                          for (int i = 0; i<ss_data_str.length; i++) {
                                            ss_data_str[i] = ss_data_str[i].replaceAll("\\{","").replaceAll("\\}","");
                                            String[] temp = ss_data_str[i].split(",");
                                            ss_data.put(temp[0], ss_path + temp[1]);
                                          }
                                          break;
            default: System.out.println("No se encuentra el parametro: "+ items[0]); break;
          }
        }
      }
      input.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  
  /**
   * Verifies if the BoundingBox coordinates are valid.
   * @return true if the BoundingBox coordinates are valid.
   */
  public boolean isValidBB(){
    if( boundingBox[0][0] == -999 || boundingBox[0][1] == -999 
     || boundingBox[1][0] == -999 || boundingBox[1][1] == -999)
      return false;
    else
      return true;
  }

  /**
   * Returns a string representation of the PropertiesDT class.
   * @return Returns the string representation of the classs.
   */
  @Override
  public String toString(){
    String str =  listener_src_name     +"\n"+
                  AuthConsumerKey       +"\n"+
                  AuthConsumerSecret    +"\n"+
                  AuthAccessToken       +"\n"+
                  AuthAccessTokenSecret +"\n"+
                  StreamBaseURL         +"\n"+
                  RequestTokenURL       +"\n"+
                  AuthorizationURL      +"\n"+
                  AccessTokenURL        +"\n"+
                  AuthenticationURL     +"\n"+
                  country_detect_path   +"\n"+
                  stats_serialize_store +"\n"+
                  stats_serialize_path  +"\n"+
                  stats_serialize_win_freq  +"\n"+
                  events_serialize_store +"\n"+
                  events_serialize_path  +"\n"+
                  mysql_server_ip       +"\n"+
                  mysql_server_port     +"\n"+
                  mysql_server_database +"\n"+
                  mysql_user            +"\n"+
                  mysql_password        +"\n"+
                  mysql_src_server_ip       +"\n"+
                  mysql_src_server_port     +"\n"+
                  mysql_src_server_database +"\n"+
                  mysql_src_user            +"\n"+
                  mysql_src_password        +"\n"+
                  mysql_src_delay_seconds   +"\n"+
                  mysql_src_generate_entities_from_text +"\n"+
                  mysql_src_load_complete_batch   +"\n"+
                  Arrays.toString(mysql_src_date_list)  +"\n"+
                  emerging_event_detection    +"\n"+
                  emerging_event_description  +"\n"+
                  store_ngrams            +"\n"+
                  event_zscore_minimum    +"\n"+
                  event_var_freq_minimum  +"\n"+
                  describer_freq_maximum  +"\n"+      
                  describer_freq_minimum  +"\n"+
                  describer_algorithm     +"\n"+
                  describer_bursty_n      +"\n"+
                  describer_last_start_kill_time +"\n"+
                  prunning_min_windows  +"\n"+
                  prunning_threshold    +"\n"+
                  event_n_detectors     +"\n"+
                  event_window_size     +"\n"+
                  event_window_shift    +"\n"+
                  repeated_user         +"\n"+
                  repeated_user_time    +"\n"+
                  repeated_user_debug_time +"\n"+
                  Arrays.toString(stanford_nlp_languages) +"\n"+
                  Arrays.toString(event_languages) +"\n"+
                  track_signal_bursty_keywords     +"\n"+
                  track_signal_keywords +"\n"+
                  track_signal_geo_user +"\n"+
                  track_signal_geo_text +"\n"+
                  track_signal_geo_location+"\n"+
                  track_signal_sentiment +"\n"+
                  track_signal_language +"\n"+
                  StopWords_Data        +"\n"+
                  research_sample       +"\n"+
                  blacklist_time_refresh+"\n"+
                  blacklist_users_path  +"\n"+
                  blacklist_keywords_path+"\n"+
                  Arrays.deepToString(boundingBox) +"\n"+
                  keywords              +"\n"+
                  ss_path               +"\n"+
                  ss_data.toString();
 
    return str;
  }   
}
