package _test;

import cl.uchile.dcc.events.twitter.Tweet;
import cl.uchile.dcc.utils.PropertiesTD;
import cl.uchile.dcc.utils.TUtils;
import cl.uchile.dcc.utils.TUtilsListenerDB;

import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestSecuencia {
  
  /**
   * MAIN method
   * @param args
   * @throws Exception 
   */
  public static void main(String[] args) throws Exception{
    PropertiesTD prop = new PropertiesTD("/Users/dicotips/Dropbox/"
            + "Research_SourceCode/Twitter_Crawler/_setup.txt");
    
    List<Date> dates = TUtilsListenerDB.generateTableDates(prop);
    
    System.out.println((char)27 +"[32;40m[Listener DB] Loading from Database: "+
            prop.mysql_src_server_database + (char)27 + "[0m");
    
    BlockingQueue<Tweet> Q_01   = new LinkedBlockingQueue<>();
    
    for (Date date : dates) {
      String table_date   = TUtils.Date_Formatter(date).substring(0, 8);
      String date_ini     = TUtils.Date_Formatter2(date);
      String date_end     = TUtils.Date_Formatter2(new Date(date.getTime() + prop.event_window_size*1000));
      
      String stats = TUtilsListenerDB.LoadDBTweet(prop, table_date, date_ini, date_end, Q_01);
      
      System.out.println((char)27 +"[32;40m[Listener DB - "+
              TUtils.Date_Formatter(new Date())  + "]("+
              table_date +", "+ 
              date_ini +", "+ 
              date_end  +", "+ 
              stats  +")"+ (char)27 + "[0m");
      
    }
  }

}
