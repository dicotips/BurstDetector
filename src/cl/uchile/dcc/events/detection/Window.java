package cl.uchile.dcc.events.detection;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Date;

/**
* This Class contains the information of a Window and the statitics of it and
* timestamp data.
* 
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public class Window implements /*Serializable,*/ Externalizable{
  /** Start Date of the window. */
  private Date TimeStamp;
  /** End Date of the window. */
  private Date TimeStamp_End;
  
  /** Window size in seconds. */
  private long WindowTime;
  /** Sum of frequencies of all terms in a Window range. */
  private long TotalWindowFreq;
  
  /** Frequency of occurrance of a term in the Window. */
  private long Frequency;
  /** LN of Frequency of occurrance of a term in the Window. */
  private double FrequencyLN;
  /** Arrival Rate of a term. (Frequency/WindowTime) = Speed */
  private double Velocity;
  /** LN of Arrival Rate of a term. LN(Frequency/WindowTime) = LN(Speed) */
  private double VelocityLN;
  /** Probability of occurrance of a term in a window. (Frequency/TotalWindowFreq)  */  
  private double Relevance;
  /** LN of Probability of occurrance of a term in a window. LN(Frequency/TotalWindowFreq)  */  
  private double RelevanceLN;

  
  /**
  * Constructor: Creates an default instance based on CURRENT)TIME and
  * timestamp = 1.
  */
  public Window() {
    this.WindowTime = 1;
    Reset(new Date());
  }
  
  /**
  * Constructor: Creates an instance based on the WindowSize and
  * sets the timestamp.
  * @param  TimeStamp Initial timestamp of the window.
  * @param  win_size Window size in seconds.
  */
  public Window(Date TimeStamp, long win_size) {
    this.WindowTime = win_size;
    Reset(TimeStamp);
  }
  
  /**
  * Constructor: Creates an clone instance based on another Window instance.
  * @param  Win Window to be cloned.
  */
  public Window(Window Win) {
    this.TimeStamp       = Win.getTimeStamp();
    this.TimeStamp_End   = Win.getTimeStamp_End();
    this.WindowTime      = Win.getWindowTime();
    this.TotalWindowFreq = Win.getTotalWindowFreq();
    
    this.Frequency       = Win.getFrequency();
    this.FrequencyLN     = Win.getFrequencyLN();
    this.Velocity        = Win.getVelocity();
    this.VelocityLN      = Win.getVelocityLN();
    this.Relevance       = Win.getRelevance();
    this.RelevanceLN     = Win.getRelevanceLN();
  }
  
  /**
  * Initializes the member variables based on the given timestamp.
  * @param  TimeStamp1 Initial timestamp of the window.
  */
  public void Reset(Date TimeStamp1) {
    this.TimeStamp        = TimeStamp1;
    this.TimeStamp_End    = new Date(this.TimeStamp.getTime() + this.WindowTime * 1000);
    this.Frequency        = 0;
    this.FrequencyLN      = 0;
    this.TotalWindowFreq  = 0;
    this.CalcVelocity();
    this.CalcRelevance();
  }
  
  /**
  * Computes the Arrival Rate (Velocity) based on:  Frequency/Window_size
  */
  public void CalcVelocity() {
    if(this.WindowTime == 0){
      this.Velocity = 0;
      this.VelocityLN = 0;
    }
    else{
      this.Velocity = ((double) this.Frequency)/((double)this.WindowTime);
      this.VelocityLN = ((double) this.FrequencyLN)/((double)this.WindowTime);
    }
  }
  
  /**
  * Computes the Relevance (Probability of occurance of a term in a window)
  * based on:  Frequency/TotalWindowFreq
  */
  public void CalcRelevance() {
    if(this.TotalWindowFreq==0){
      this.Relevance = 0;
      this.RelevanceLN = 0;
    }
    else{
      this.Relevance = ((double) this.Frequency) / ((double)this.TotalWindowFreq);
      this.RelevanceLN = ((double) this.FrequencyLN) / ((double)this.TotalWindowFreq);
    }
  }
  
  /**
  * Adds a value in the actual Frequency.
  * @param value Value to be added in the frequency.
  */
  public void AddFreq(long value) {
    if (value > 0) {
      this.Frequency += value;
      this.FrequencyLN = Math.log(this.Frequency);
      CalcVelocity();
      CalcRelevance();
    }
  }
  
  /**
  * Sets the Frequency member variable in a specific value. It re-computes
  * the Velocity and the Relevance.
  * @param freq Value to be set in the frequency.
  */
  public void setFrequency(long freq) {
    if (freq > 0) {
      this.Frequency = freq;
      this.FrequencyLN = Math.log(this.Frequency);
      CalcVelocity();
      CalcRelevance();
    }
  }
  
  /**
  * Sets the TotalWindowFrequency member variable in a specific value.
  * @param tFreq Value to be set in the TotalWindowFrequency.
  */
  public void setTotalWindowFreq(long tFreq) {
    this.TotalWindowFreq = tFreq;
    CalcRelevance();
  }
  
  /**
  * Sets the window_size.
  * @param time Window size in seconds.
  */
  public void setWindowTime(long time) {   
    this.WindowTime = time;
    CalcVelocity();
  }
  
  /**
  * Sets the initial timestamp of a window. COmputes the End_timestamp of
  * the window.
  * @param time Initial timestamp of the window.
  */
  public void setTimeStamp(Date time) {
    this.TimeStamp      = time;
    this.TimeStamp_End  = new Date(this.TimeStamp.getTime() + this.WindowTime * 1000);
  }
  
  /**
  * Getter that returns the start timestamp of the window.
  * @return Returns the initial Timestamp.
  */
  public Date getTimeStamp(){
    return this.TimeStamp;
  }
  
  /**
  * Getter that returns the end timestamp of the window.
  * @return Returns the End Timestamp.
  */
  public Date getTimeStamp_End(){
    return this.TimeStamp_End;
  }

  /**
  * Getter that returns the Total Frequency of the Window.
  * @return Returns the Total Frequency in a Window.
  */
  public long getTotalWindowFreq(){
    return this.TotalWindowFreq;
  }
  
  /**
  * Getter that returns the window_size in seconds.
  * @return Returns the window_size (seconds).
  */
  public long getWindowTime(){
    return this.WindowTime;
  }
  
  /**
  * Getter that returns the Frequency.
  * @return Returns the Frequency.
  */
  public long getFrequency(){
    return this.Frequency;
  }
  
  /**
  * Getter that returns the LN of the Frequency.
  * @return Returns the Frequency.
  */
  public double getFrequencyLN(){
    return this.FrequencyLN;
  }
  
  /**
  * Getter that returns the Arrival Rate (Velocity).
  * @return Returns the Arrival Rate.
  */
  public double getVelocity(){
    return this.Velocity;
  }
  
  /**
  * Getter that returns the LN of Arrival Rate (VelocityLN).
  * @return Returns the Arrival Rate.
  */
  public double getVelocityLN(){
    return this.VelocityLN;
  }
  
  /**
  * Getter that returns the Arrival Rate (Velocity).
  * @return Returns the Arrival Rate.
  */
  public double getRelevance(){
    return this.Relevance;
  }
  
  /**
  * Getter that returns the Arrival Rate (Velocity).
  * @return Returns the Arrival Rate.
  */
  public double getRelevanceLN(){
    return this.RelevanceLN;
  }

  /**
   * Returns a string representation of the Window class.
   * Format: [ D={_Initial_TimeStamp} F=Frequency, Wt=WindowTime, R=Velocity ]
   * @return Returns the string representation of the class.
   */
  @Override
  public String toString(){
    return "[D={" + TimeStamp.getTime() + "}{Wt=" + WindowTime +"}"
            +   " {F="+ Frequency   +", V="+ Velocity   +", R="+ Relevance   +"}"
            + " LN{F="+ FrequencyLN +", V="+ VelocityLN +", R="+ RelevanceLN +"}"
            + "]";
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(TimeStamp);
    out.writeObject(TimeStamp_End);
    out.writeLong  (WindowTime);
    out.writeLong  (TotalWindowFreq);
    out.writeLong  (Frequency);
    out.writeDouble(FrequencyLN);
    out.writeDouble(Velocity);
    out.writeDouble(VelocityLN);
    out.writeDouble(Relevance);
    out.writeDouble(RelevanceLN);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    TimeStamp     = (Date)   in.readObject();
    TimeStamp_End = (Date)   in.readObject();
    WindowTime    = (long)   in.readLong();
    TotalWindowFreq = (long) in.readLong();
    Frequency     = (long)   in.readLong();
    FrequencyLN   = (double) in.readDouble();
    Velocity      = (double) in.readDouble();
    VelocityLN    = (double) in.readDouble();
    Relevance     = (double) in.readDouble();
    RelevanceLN   = (double) in.readDouble();
  }
}
