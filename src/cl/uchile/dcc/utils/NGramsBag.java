package cl.uchile.dcc.utils;

import cl.uchile.dcc.text.NGramRow;

import java.util.Date;
import java.util.Queue;

/**
* This Class is a container of Ngrams in a certain window. This container is 
* enqued  and sent between the Threads.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public class NGramsBag {
  /**
   * Timestamp of the Bag. It contains the NGrams of a certain window.
   */
  private Date TimeStamp;
  
  /**
   * Container of the NGrams.
   */
  private Queue<NGramRow> QNGrams;

  /**
   * Constructor of the NGramsBag container.
   * @param TimeStamp1 Timestamp of the bag.
   * @param QNGrams1 Queue containing the Ngrams.
   */
  public NGramsBag(Date TimeStamp1, Queue<NGramRow> QNGrams1){
      this.TimeStamp = TimeStamp1;
      this.QNGrams = QNGrams1;
  }
  
  /**
   * Sets the timestamp of the NGramBag.
   * @param TimeStamp1 Timestamp of the bag.
   */
  public void setTimeStamp(Date TimeStamp1){
      this.TimeStamp=TimeStamp1;
  }
  
  /**
   * Sets the queue containing the NGram of the bag.
   * @param QNGrams1 Queue containing the NGram of the bag.
   */
  public void setQNgrams(Queue<NGramRow> QNGrams1 ){
      this.QNGrams = QNGrams1;
  }

  /**
   * Gets the timestamp of the NGramBag.
   * @return Returns the timestamp of the bag.
   */
  public Date getTimeStamp(){
      return TimeStamp;
  }
  /**
   * Gets the queue containing the NGram of the bag.
   * @return Returns the queue containing the NGrams of the bag.
   */
  public Queue<NGramRow> getQueueNGrams(){
      return QNGrams;
  }
}
