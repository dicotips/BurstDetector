package cl.uchile.main;

import cl.uchile.dcc.events.detection.EventBT;
import cl.uchile.dcc.events.detection.WindowRow;
import cl.uchile.dcc.events.twitter.TUser;
import cl.uchile.dcc.events.twitter.Tweet;
import cl.uchile.dcc.events.twitter.TweetType;
import cl.uchile.dcc.utils.InitialState;
import cl.uchile.dcc.utils.NGramsBag;
import cl.uchile.dcc.utils.PropertiesTD;
import cl.uchile.dcc.utils.TUtils;
import cl.uchile.dcc.utils.TUtilsListenerDB;
import cl.uchile.dcc.utils.WordsBag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
* The AgentDListenerDB Agent Load tweets from a Database.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0
* @since       2016-09-22
*/
public class AgentListenerDB extends Thread{
  
  private BlockingQueue<Tweet>          Q_01;
  private List<BlockingQueue<Tweet>>    Q_02;
  private List<BlockingQueue<WordsBag>> Q_03_k;
  private BlockingQueue<NGramsBag>      Q_03_g;
  private BlockingQueue<EventBT>        Q_04a;
  private List<ConcurrentHashMap<String,WindowRow>> HT_k;
  private PropertiesTD prop;
  
  private AgentDBStorer           agDBStorer;
  private List<AgentPacker>       agPacker;
  private List<AgentProcessorBD>  agProcessorBD;
  private AgentDescriber          agDescriber;
  
  /** Counter of tweet since the last check. */
  public long count_tweets = 0;
  /** Counter of tweet in total. */
  public long count_tweets_total = 0;
  
  
  /**
   * Constructor.
   * <p>
   * Connects to the Database of Tweets to load tweets into the application, 
   * cleans them, encapsulates them and adds meta-data (GeoLocation, Sentiment).
   * <p>
   * @param HT_R_Users  HashTable with the users that posted in the last 5 mins.
   * @param PropTD      Parameters from the _setup.txt file.
   * @param TwQueue     Queue to push Tweets into. Sends the tweets to the DBStorer.
   */
  AgentListenerDB(
          PropertiesTD PropTD, 
          BlockingQueue<Tweet>          _Q_01,
          List<BlockingQueue<Tweet>>    _Q_02,
          List<BlockingQueue<WordsBag>> _Q_03_k,
          BlockingQueue<NGramsBag>      _Q_03_g,
          BlockingQueue<EventBT>        _Q_04a,
          List<ConcurrentHashMap<String,WindowRow>> _HT_k,
          AgentDBStorer                 _agDBStorer,
          List<AgentPacker>             _agPacker,
          List<AgentProcessorBD>        _agProcessorBD,
          AgentDescriber                _agDescriber ) {
    
    prop      = PropTD;
    Q_01      = _Q_01;
    Q_02      = _Q_02;
    Q_03_k    = _Q_03_k;
    Q_03_g    = _Q_03_g;
    Q_04a     = _Q_04a;
    HT_k      = _HT_k;
    
    agDBStorer    = _agDBStorer;
    agPacker      = _agPacker;
    agProcessorBD = _agProcessorBD;
    agDescriber   = _agDescriber;
    
  }
  
  
  /**
   * Body of the Thread: Starts to Download tweets from the Twitter Stream.
   * 
   * TWITTER LISTENER - Database
   */
  @Override
  public void run(){
    
    Date recovery_date = new Date();

    //CLEANS crash data from a database.
    if(Twitter_Crawler_Stream.ini_state == InitialState.RECOVERY_DATABASE){
      
      recovery_date = TUtilsListenerDB.getInitialDate(HT_k, prop);
      TUtilsListenerDB.cleanCrashDB(recovery_date, prop);
      Twitter_Crawler_Stream.init_date = recovery_date;
      
      System.out.println((char)27 +"[36;40m[SYSTEM] Init-Date: " +
              TUtils.Date_Formatter2(Twitter_Crawler_Stream.init_date)+ 
              (char)27 + "[0m");
    }

    List<Date> dates_aux = TUtilsListenerDB.generateTableDates(prop);
    List<Date> dates = new ArrayList<>();

    if(Twitter_Crawler_Stream.ini_state == InitialState.NEW_DATABASE){
      recovery_date = new Date(Long.MIN_VALUE);
    }

    //Select the dates from the list that not processed already.
    for (Date date : dates_aux) {
      if(date.compareTo(recovery_date) >= 0){
        dates.add(date);
      }
    }
      
    //Remove the hours that are already processed
    recovery_date = TUtilsListenerDB.getMinDateTable(
            TUtils.Date_Formatter(dates_aux.get(0)).substring(0,8),
            prop);
      final Date recovery_date1 = 
              new Date(recovery_date.getTime() - prop.event_window_size*1000);
      dates.removeIf(e->e.compareTo(recovery_date1) < 0);
      Twitter_Crawler_Stream.init_date = recovery_date;

      System.out.println((char)27 +"[36;40m[Listener DB] Loading from Database: "+
              prop.mysql_src_server_database + (char)27 + "[0m");
      
    try {
      for (Date date : dates) {
        String table_date   = TUtils.Date_Formatter(date).substring(0, 8);
        String date_ini     = TUtils.Date_Formatter2(date);
        String date_end     = TUtils.Date_Formatter2(new Date(date.getTime() + prop.event_window_size*1000));

        //Load Tweets retrieved from the Database into the pipeline of the Crawler.
        System.out.println((char)27 +"[36;40m[Listener DB - "+
                TUtils.Date_Formatter(new Date())  + "]("+
                table_date +", "+ 
                date_ini +", "+ 
                date_end +") Loading Window..."+ (char)27 + "[0m");
        
        String stats;
        if(!prop.mysql_src_load_complete_batch){
          stats = TUtilsListenerDB.LoadDBTweet(prop, table_date, date_ini, date_end, Q_01);
        }
        else{
          BlockingQueue<Tweet> Q_temp = new LinkedBlockingQueue<>();
          stats = TUtilsListenerDB.LoadDBTweet(prop, table_date, date_ini, date_end, Q_temp);
          Q_temp.drainTo(Q_01);
        }
        
        System.out.println((char)27 +"[36;40m[Listener DB - "+
                TUtils.Date_Formatter(new Date())  + "]("+
                table_date +", "+ 
                date_ini +", "+ 
                date_end  +", "+ 
                stats  +")"+ (char)27 + "[0m");

        //Wait until all the leaded tweets are processed and Waiting.
        while(true){
          boolean var  = true;
          boolean var2 = true;
          String var2_str = "";
          for (BlockingQueue<Tweet> q_tweet : Q_02) {
            var = var && (q_tweet.isEmpty());
          }
          for (BlockingQueue<WordsBag> q_wbag : Q_03_k) {
            var = var && (q_wbag.isEmpty());
          }
          
          var2 = var2 && (agDBStorer.getState()    == State.TIMED_WAITING && agDBStorer.isAlive());
          var2_str = var2_str + "aDB:"+ agDBStorer.getState() +" ";
          for ( AgentPacker th : agPacker ) {
            var2 = var2 && (th.getState() == State.TIMED_WAITING && th.isAlive());
            var2_str = var2_str + "aPack:"+ th.getState() +" ";
          }
          for ( AgentProcessorBD th : agProcessorBD ) {
            var2 = var2 && (th.getState()  == State.TIMED_WAITING && th.isAlive());
            var2_str = var2_str + "aProc:"+ th.getState() +" ";
          }
          var2 = var2 && (agDescriber.getState()   == State.TIMED_WAITING && agDescriber.isAlive());
          var2_str = var2_str + "aDesc:"+ agDescriber.getState() +" ";
          
          if(var //&& var2
                 && (Q_01.isEmpty()) 
                 //&& (Q_03_g.isEmpty()) 
                 //&& (Q_04a.isEmpty())
                  ){
            break;
          }
          
          System.out.println((char)27 +"[36;40m[Listener DB - SLEEP 5000] "
                  + var2_str                  
                  + (char)27 + "[0m");
          Thread.sleep(5000);    // 10 seconds
        }// end While

      }// end FOR of dates
    } catch (Exception ex) {
      ex.printStackTrace();
    }
      
    //ADD MOCK TWEET WITH LARGE DATE
    Tweet tweet = new Tweet(prop);
          tweet.setDownloadedAt(new Date(Long.MAX_VALUE));
          tweet.setUser(new TUser());
          tweet.setType(TweetType.TEMP);


    //ENQUEUE the LAST TWEET.
    Q_01.add(tweet);
    System.out.println((char)27 +"[36;40m[Listener DB] Loading from Database finished!!!"+ (char)27 + "[0m");
      
   
  }
}
