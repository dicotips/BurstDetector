package cl.uchile.dcc.utils;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

/**
* This Class is a container of keywords of a certain window. This container is 
* enqued and sent between the Threads to be analyzed.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public class WordsBag {
  /** Timestamp of the Bag. It contains the keywords of a certain window. */
  private Date TimeStamp;
  /** Container of the Keywords. */
  private BlockingQueue<String> QKWords;
  
  /** Counter tweets. */
  public long c_tweets = 0;
  /** Counter Keywords Signal. */
  public long c_kw = 0;
  /** Counter Grouped_Keywords Signal. */
  public long c_eq = 0;
  /** Counter Languages Signal. */
  public long c_lang = 0;
  /** Counter Sentiment Signal. */
  public long c_ss = 0;
  /** Counter Country in Text Signal. */
  public long c_Ctxt = 0;
  /** Counter Country in User Signal. */
  public long c_Cusr = 0;
  /** Counter Country in GeoLocation Signal. */
  public long c_Cgeo = 0;

  /**
   * Constructor of the WordsBag container.
   * @param TimeStamp1 Timestamp of the bag.
   * @param QKWords1 Queue containing the Keywords.
   */
  public WordsBag(Date TimeStamp1,BlockingQueue<String> QKWords1){
    this.TimeStamp = TimeStamp1;
    this.QKWords = QKWords1;
  }
  
  /**
   * Sets the timestamp of the WordsBag.
   * @param TimeStamp1 Timestamp of the bag.
   */
  public void setTimeStamp(Date TimeStamp1){
    this.TimeStamp=TimeStamp1;
  }
  
  /**
   * Sets the queue containing the Keywords of the bag.
   * @param QKWords1 Queue containing the Keywords of the bag.
   */
  public void setQKWords(BlockingQueue<String> QKWords1 ){
    this.QKWords = QKWords1;
  }

  /**
   * Gets the timestamp of the WordsBag.
   * @return Returns the timestamp of the bag.
   */
  public Date getTimeStamp(){
    return TimeStamp;
  }
  
  /**
   * Gets the queue containing the Keywords of the bag.
   * @return Returns the Queue containing the Keywords of the bag.
   */
  public BlockingQueue<String> getQueueWords(){
    return QKWords;
  }    
}


