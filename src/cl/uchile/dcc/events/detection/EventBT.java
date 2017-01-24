package cl.uchile.dcc.events.detection;

import cl.uchile.dcc.utils.TUtils;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;

/**
* This Class contains the information of the Busty terms detected. It contains
* information about the burst and its range of time when it was detected. It is 
* created by the AgentProcessor and enqued in Q04 for the Description task.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public class EventBT implements java.io.Serializable{
  
  /**
   * Start Timestamp of the first_window when the burst was detected.
   */
  private Date _range_ini;
  
  /**
   * Middle Timestamp of the window when the burst was detected. This is the
   * timestamp of the window that has the burst.
   */
  private Date _event_time;
  
  /**
   * End Timestamp of the second_window when the burst was detected.
   */
  private Date _range_end;
  
  /**
   * Window size in seconds.
   */
  private long _winTime;
  
  /**
   * Constructor of the Event.
   * @param mid Timestamp when the event happens. It is the middle Date of the 2 adyacent windows.
   * @param winTime Window Size in seconds.
   */
  public EventBT(Date mid, long winTime){
    _range_end  = new Date(mid.getTime()+winTime*1000);
    _event_time = mid;
    _range_ini  = new Date(mid.getTime()-winTime*1000);
    _winTime    = winTime;
  }
  /**
   * Constructor of the Event (Cloner).
   * @param e EventBT to be cloned.
   */
  public EventBT(EventBT e){
    _range_end  = e.getDateEnd();
    _event_time = e.getDateMid();
    _range_ini  = e.getDateIni();
    _winTime    = e.getWinTime();
  }
  
  /**
   * Getter of the start timestamp of the first_window.
   * @return Returns the Timestamp of the first window.
   */
  public Date getDateIni(){
    return _range_ini;
  }
  
  /**
   * Getter of the middle timestamp of the two adyancent windows.
   * @return Returns the Timestamp of the middle window. (Event Time)
   */
  public Date getDateMid(){
    return _event_time;
  }
  
  /**
   * Getter of the end timestamp of the second_window.
   * @return Returns the end timestamp of the second window.
   */
  public Date getDateEnd(){
    return _range_end;
  }
  
  /**
   * Getter of the window_size in seconds.
   * @return Returns the window_size in seconds.
   */
  public long getWinTime(){
    return _winTime;
  }

  
  /**
   * String representation of the EventBT class.
   * Format: {_range_ini, _event_time, _range_end, _winTime, _terms};
   * 
   * @return Returns the string representation of the class.
   */
  @Override
  public String toString(){
    return "{"+ TUtils.Date_Formatter2(_range_ini)  +", "+
                TUtils.Date_Formatter2(_event_time) +", "+ 
                TUtils.Date_Formatter2(_range_end) +", "+ 
                _winTime +"}";
  }
  
  /**
   * This is the main method which tests the methods: Constructors and toString().
   * @param args Nothing.
   */
  public static void main(String[] args){
    EventBT event1 = new EventBT(new Date(), 300);
    System.out.println(event1);
    
    EventBT event2 = new EventBT(event1);
    System.out.println(event2);
  }

}
