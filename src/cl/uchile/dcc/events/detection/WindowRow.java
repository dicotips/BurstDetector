package cl.uchile.dcc.events.detection;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
* This Class implements a Row of Windows to be analyzed. It represents the
* signal per term to be tracked. It tracks the statistics of the "absolute
* variation" and the "Relative variation" of the Relevance, Velocity and the 
* Relative Arrival Rate.
* 
*   W1 new, W2 old, stats
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public class WindowRow implements Comparable, /*Serializable,*/ Externalizable{
  
  ////////////////////   WINDOWS   ////////////////////
  /** Window 1 to track. It has the newer information. */
  private Window W1;
  
  /** Window 2 to track. It has the previous window. */
  private Window W2;
  
  /** TimeStamp of the Row. It Matches with the W1. */
  private Date TimeStamp;
  
  /** Window size in seconds. */
  private long WindowTime;
  
  ////////////////////   STATS - VARIATIONS   ////////////////////
  /** Absolute variation of the Frequencies between W1 and W2  Freq(W1)-Freq(W2) */
  private long Var_Freq;
  /** Absolute variation of the FrequenciesLN between W1 and W2  Freq_LN(W1)-Freq_LN(W2) */
  private double Var_Freq_LN;
  
  /** Absolute variation of the Arrival_Rate between W1 and W2:  AR(W1)-AR(W2) */
  private double Var_Vel;
  /** Absolute variation of the Arrival_Rate between W1 and W2:  AR_LN(W1)-AR_LN(W2) */
  private double Var_Vel_LN;
  
  /** Absolute variation of the Arrival_Rate between W1 and W2:  Rel(W1)-Rel(W2) */
  private double Var_Rel;
  /** Absolute variation of the Arrival_Rate between W1 and W2:  Rel_LN(W1)-Rel_LN(W2) */
  private double Var_Rel_LN;
  
  /** Relative Variation of the Frequencies between W1 and W2  (Freq(W1)-Freq(W2))/Freq(W2) */
  private double VarRel_Freq;
  /** Relative Variation of the Frequencies between W1 and W2  (Freq_LN(W1)-Freq_LN(W2))/Freq_LN(W2) */
  private double VarRel_Freq_LN;
  
  /** Relative Variation of the Arrival Rate between W1 and W2  (AR(W1)-AR(W2))/AR(W2) */
  private double VarRel_Vel;
  /** Relative Variation of the Arrival Rate between W1 and W2  (AR_LN(W1)-AR_LN(W2))/AR_LN(W2) */
  private double VarRel_Vel_LN;
  
  /** Relative Variation of the Relevance between W1 and W2  (Rel(W1)-Rel(W2))/Rel(W2) */
  private double VarRel_Rel;
  /** Relative Variation of the Relevance between W1 and W2  (Rel_LN(W1)-Rel_LN(W2))/Rel_LN(W2) */
  private double VarRel_Rel_LN;
  
  ////////////////////   STATS - EXTRA    ////////////////////
  /**
  * Number of Windows processed so far while the Twitter_Crawler was running. 
  * (includes Freq=0 windows.
  */
  private long n_w0;      // N windows (include freq=0)
  
  /**
  * Number of non-empty Windows processed so far while the Twitter_Crawler was
  * running. (Does not includes Freq=0 windows.
  */
  private long n_w;       // N windows that has the term.
  
  /**
  * Accumulative sum of the frequency of the Windows processed so far while 
  * the Twitter_Crawler was running.
  */
  private long   sum_x;
  private double sum_x_ln;
  private double sum_x_vel;
  private double sum_x_vel_ln;
  private double sum_x_rel;
  private double sum_x_rel_ln;
  private double sum_x_var_vel;
  private double sum_x_rel_var_vel;
  private double sum_x_var_rel;
  private double sum_x_rel_var_rel;
  
  /**
  * Accumulative sum of the frequency^2 of the Windows processed so far while 
  * the Twitter_Crawler was running.
  */
  private long   sum_x2;
  private double sum_x2_ln;
  private double sum_x2_vel;
  private double sum_x2_vel_ln;
  private double sum_x2_rel;
  private double sum_x2_rel_ln;
  private double sum_x2_var_vel;
  private double sum_x2_rel_var_vel;
  private double sum_x2_var_rel;
  private double sum_x2_rel_var_rel;
  
  /**
  * Actual MEAN of the frequencies of the Windows processed so far while 
  * the Twitter_Crawler was running.
  */
  private double mean;
  private double mean_ln;
  private double mean_vel;
  private double mean_vel_ln;
  private double mean_rel;
  private double mean_rel_ln;
  private double mean_var_vel;
  private double mean_rel_var_vel;
  private double mean_var_rel;
  private double mean_rel_var_rel;  
  
  /**
  * Actual VARIANCE of the frequencies of the Windows processed so far while 
  * the Twitter_Crawler was running.
  * Source: Variance of König-Huygens formula
  * Link:   (https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance)
  * 
  *     V(X) = stdev^2 = E(X^2)-E(X)^2
  * 
  */
  private double variance;
  private double variance_ln;
  private double variance_vel;
  private double variance_vel_ln;
  private double variance_rel;
  private double variance_rel_ln;
  private double variance_var_vel;
  private double variance_rel_var_vel;
  private double variance_var_rel;
  private double variance_rel_var_rel;
  
  /**
  * Actual STDEV of the frequencies of the Windows processed so far while 
  * the Twitter_Crawler was running.
  * 
  *     sqrt(V(X))
  * 
  */
  private double stdev;
  private double stdev_ln;
  private double stdev_vel;
  private double stdev_vel_ln;
  private double stdev_rel;
  private double stdev_rel_ln;
  private double stdev_var_vel;
  private double stdev_rel_var_vel;
  private double stdev_var_rel;
  private double stdev_rel_var_rel;
  
  /**
  * Actual ZScore of the frequencies of the Windows processed so far while 
  * the Twitter_Crawler was running. (Based on MEAN and VARIANCE)
  */
  private double z_score;
  private double z_score_ln;
  private double z_score_vel;
  private double z_score_vel_ln;
  private double z_score_rel;
  private double z_score_rel_ln;
  private double z_score_var_vel;
  private double z_score_rel_var_vel;
  private double z_score_var_rel;
  private double z_score_rel_var_rel;
  
  /**
  * Actual TF-IDF Score.
  */
  private double tf_idf;

  
  /**
  * Constructor: Creates an instance of WindowRow based on the WindowSize and
  * sets the timestamp. Creates new Window W1 and W2.
  */
  public WindowRow() {
    this.TimeStamp  = new Date();
    this.WindowTime = 1;
    Var_Freq    = 0;
    Var_Freq_LN = 0.0;
    Var_Vel     = 0.0;
    Var_Vel_LN  = 0.0;
    Var_Rel     = 0.0;
    Var_Rel_LN  = 0.0;
    VarRel_Freq = 0.0;
    VarRel_Freq_LN = 0.0;
    VarRel_Vel     = 0.0;
    VarRel_Vel_LN  = 0.0;
    VarRel_Rel     = 0.0;
    VarRel_Rel_LN  = 0.0;
    
    W1 = new Window(TimeStamp, WindowTime);
    W2 = new Window(TimeStamp, WindowTime);
    
    // STATS-EXTRA
    n_w0   = 0;   
    n_w    = 0;  
    
    sum_x        = 0;
    sum_x_ln     = 0.0;
    sum_x_vel    = 0.0;
    sum_x_vel_ln = 0.0;
    sum_x_rel    = 0.0;
    sum_x_rel_ln = 0.0;
    sum_x_var_vel     = 0.0;
    sum_x_rel_var_vel = 0.0;
    sum_x_var_rel     = 0.0;
    sum_x_rel_var_rel = 0.0;
    
    sum_x2        = 0;
    sum_x2_ln     = 0.0;
    sum_x2_vel    = 0.0;
    sum_x2_vel_ln = 0.0;
    sum_x2_rel    = 0.0;
    sum_x2_rel_ln = 0.0;
    sum_x2_var_vel     = 0.0;
    sum_x2_rel_var_vel = 0.0;
    sum_x2_var_rel     = 0.0;
    sum_x2_rel_var_rel = 0.0;
    
    mean        = 0.0;
    mean_ln     = 0.0;
    mean_vel    = 0.0;
    mean_vel_ln = 0.0;
    mean_rel    = 0.0;
    mean_rel_ln = 0.0;
    mean_var_vel     = 0.0;
    mean_rel_var_vel = 0.0;
    mean_var_rel     = 0.0;
    mean_rel_var_rel = 0.0;
    
    variance        = 0.0;
    variance_ln     = 0.0;
    variance_vel    = 0.0;
    variance_vel_ln = 0.0;
    variance_rel    = 0.0;
    variance_rel_ln = 0.0;
    variance_var_vel     = 0.0;
    variance_rel_var_vel = 0.0;
    variance_var_rel     = 0.0;
    variance_rel_var_rel = 0.0;
    
    stdev        = 0.0;
    stdev_ln     = 0.0;
    stdev_vel    = 0.0;
    stdev_vel_ln = 0.0;
    stdev_rel    = 0.0;
    stdev_rel_ln = 0.0;
    stdev_var_vel     = 0.0;
    stdev_rel_var_vel = 0.0;
    stdev_var_rel     = 0.0;
    stdev_rel_var_rel = 0.0;
    
    z_score        = 0.0;
    z_score_ln     = 0.0;
    z_score_vel    = 0.0;
    z_score_vel_ln = 0.0;
    z_score_rel    = 0.0;
    z_score_rel_ln = 0.0;
    z_score_var_vel     = 0.0;
    z_score_rel_var_vel = 0.0;
    z_score_var_rel     = 0.0;
    z_score_rel_var_rel = 0.0;
    
    tf_idf = 0.0;

  }
  
  /**
  * Constructor: Creates an instance of WindowRow based on the WindowSize and
  * sets the timestamp. Creates new Window W1 and W2.
  * @param  window_size Window size in seconds.
  * @param  ini_timestamp Initial timestamp of the window.
  */
  public WindowRow(long window_size, Date ini_timestamp) {
    this.TimeStamp  = ini_timestamp;
    this.WindowTime = window_size;
    Var_Freq    = 0;
    Var_Freq_LN = 0.0;
    Var_Vel     = 0.0;
    Var_Vel_LN  = 0.0;
    Var_Rel     = 0.0;
    Var_Rel_LN  = 0.0;
    VarRel_Freq = 0.0;
    VarRel_Freq_LN = 0.0;
    VarRel_Vel     = 0.0;
    VarRel_Vel_LN  = 0.0;
    VarRel_Rel     = 0.0;
    VarRel_Rel_LN  = 0.0;
    
    W1 = new Window(TimeStamp, WindowTime);
    W2 = new Window(TimeStamp, WindowTime);
    
    // STATS-EXTRA
    n_w0   = 0;   
    n_w    = 0;  
    
    sum_x        = 0;
    sum_x_ln     = 0.0;
    sum_x_vel    = 0.0;
    sum_x_vel_ln = 0.0;
    sum_x_rel    = 0.0;
    sum_x_rel_ln = 0.0;
    sum_x_var_vel     = 0.0;
    sum_x_rel_var_vel = 0.0;
    sum_x_var_rel     = 0.0;
    sum_x_rel_var_rel = 0.0;
    
    sum_x2        = 0;
    sum_x2_ln     = 0.0;
    sum_x2_vel    = 0.0;
    sum_x2_vel_ln = 0.0;
    sum_x2_rel    = 0.0;
    sum_x2_rel_ln = 0.0;
    sum_x2_var_vel     = 0.0;
    sum_x2_rel_var_vel = 0.0;
    sum_x2_var_rel     = 0.0;
    sum_x2_rel_var_rel = 0.0;
    
    mean        = 0.0;
    mean_ln     = 0.0;
    mean_vel    = 0.0;
    mean_vel_ln = 0.0;
    mean_rel    = 0.0;
    mean_rel_ln = 0.0;
    mean_var_vel     = 0.0;
    mean_rel_var_vel = 0.0;
    mean_var_rel     = 0.0;
    mean_rel_var_rel = 0.0;
    
    variance        = 0.0;
    variance_ln     = 0.0;
    variance_vel    = 0.0;
    variance_vel_ln = 0.0;
    variance_rel    = 0.0;
    variance_rel_ln = 0.0;
    variance_var_vel     = 0.0;
    variance_rel_var_vel = 0.0;
    variance_var_rel     = 0.0;
    variance_rel_var_rel = 0.0;
    
    stdev        = 0.0;
    stdev_ln     = 0.0;
    stdev_vel    = 0.0;
    stdev_vel_ln = 0.0;
    stdev_rel    = 0.0;
    stdev_rel_ln = 0.0;
    stdev_var_vel     = 0.0;
    stdev_rel_var_vel = 0.0;
    stdev_var_rel     = 0.0;
    stdev_rel_var_rel = 0.0;
    
    z_score        = 0.0;
    z_score_ln     = 0.0;
    z_score_vel    = 0.0;
    z_score_vel_ln = 0.0;
    z_score_rel    = 0.0;
    z_score_rel_ln = 0.0;
    z_score_var_vel     = 0.0;
    z_score_rel_var_vel = 0.0;
    z_score_var_rel     = 0.0;
    z_score_rel_var_rel = 0.0;
    
    tf_idf = 0.0;

  }
  
  /**
  * Constructor: Creates a clone of WindowRow based on provided WindowRow.
  * @param WR WindowRow to be cloned.
  */
  public WindowRow(WindowRow WR) {
    this.TimeStamp  = WR.getTimeStamp();
    this.WindowTime = WR.getWindowTime();
    this.Var_Freq   = WR.getVar_Freq();
    this.Var_Freq_LN  = WR.getVar_Freq_LN();
    this.Var_Vel      = WR.getVar_Vel();
    this.Var_Vel_LN   = WR.getVar_Vel_LN();
    this.Var_Rel      = WR.getVar_Rel();
    this.Var_Rel_LN   = WR.getVar_Rel_LN();
    this.VarRel_Freq    = WR.getVarRel_Freq();
    this.VarRel_Freq_LN = WR.getVarRel_Freq_LN();
    this.VarRel_Vel     = WR.getVarRel_Vel();
    this.VarRel_Vel_LN  = WR.getVarRel_Vel_LN();
    this.VarRel_Rel     = WR.getVarRel_Rel();
    this.VarRel_Rel_LN  = WR.getVarRel_Rel_LN();
    W1 = new Window(WR.getW1());
    W2 = new Window(WR.getW2());
    this.n_w0   = WR.getNW0();
    this.n_w    = WR.getNW();
    this.sum_x        = WR.getSumX();
    this.sum_x_ln     = WR.getSumX_LN();
    this.sum_x_vel    = WR.getSumXVel();
    this.sum_x_vel_ln = WR.getSumXVel_LN();
    this.sum_x_rel    = WR.getSumXRel();
    this.sum_x_rel_ln = WR.getSumXRel_LN();
    this.sum_x_var_vel     = WR.getSumXVarVel();
    this.sum_x_rel_var_vel = WR.getSumXRelVarVel();
    this.sum_x_var_rel     = WR.getSumXVarRel();
    this.sum_x_rel_var_rel = WR.getSumXRelVarRel();

    this.sum_x2        = WR.getSumX2();
    this.sum_x2_ln     = WR.getSumX2_LN();
    this.sum_x2_vel    = WR.getSumX2Vel();
    this.sum_x2_vel_ln = WR.getSumX2Vel_LN();
    this.sum_x2_rel    = WR.getSumX2Rel();
    this.sum_x2_rel_ln = WR.getSumX2Rel_LN();
    this.sum_x2_var_vel     = WR.getSumX2VarVel();
    this.sum_x2_rel_var_vel = WR.getSumX2RelVarVel();
    this.sum_x2_var_rel     = WR.getSumX2VarRel();
    this.sum_x2_rel_var_rel = WR.getSumX2RelVarRel();

    this.mean        = WR.getMean();
    this.mean_ln     = WR.getMean_LN();
    this.mean_vel    = WR.getMeanVel();
    this.mean_vel_ln = WR.getMeanVel_LN();
    this.mean_rel    = WR.getMeanRel();
    this.mean_rel_ln = WR.getMeanRel_LN();
    this.mean_var_vel     = WR.getMeanVarVel();
    this.mean_rel_var_vel = WR.getMeanRelVarVel();
    this.mean_var_rel     = WR.getMeanVarRel();
    this.mean_rel_var_rel = WR.getMeanRelVarRel();

    this.variance        = WR.getVar();
    this.variance_ln     = WR.getVar_LN();
    this.variance_vel    = WR.getVarVel();
    this.variance_vel_ln = WR.getVarVel_LN();
    this.variance_rel    = WR.getVarRel();
    this.variance_rel_ln = WR.getVarRel_LN();
    this.variance_var_vel     = WR.getVarVarVel();
    this.variance_rel_var_vel = WR.getVarRelVarVel();
    this.variance_var_rel     = WR.getVarVarRel();
    this.variance_rel_var_rel = WR.getVarRelVarRel();

    this.stdev        = WR.getStdev();
    this.stdev_ln     = WR.getStdev_LN();
    this.stdev_vel    = WR.getStdevVel();
    this.stdev_vel_ln = WR.getStdevVel_LN();
    this.stdev_rel    = WR.getStdevRel();
    this.stdev_rel_ln = WR.getStdevRel_LN();
    this.stdev_var_vel     = WR.getStdevVarVel();
    this.stdev_rel_var_vel = WR.getStdevRelVarVel();
    this.stdev_var_rel     = WR.getStdevVarRel();
    this.stdev_rel_var_rel = WR.getStdevRelVarRel();

    this.z_score        = WR.getZScore();
    this.z_score_ln     = WR.getZScore_LN();
    this.z_score_vel    = WR.getZScoreVel();
    this.z_score_vel_ln = WR.getZScoreVel_LN();
    this.z_score_rel    = WR.getZScoreRel();
    this.z_score_rel_ln = WR.getZScoreRel_LN();
    this.z_score_var_vel     = WR.getZScoreVarVel();
    this.z_score_rel_var_vel = WR.getZScoreRelVarVel();
    this.z_score_var_rel     = WR.getZScoreVarRel();
    this.z_score_rel_var_rel = WR.getZScoreRelVarRel();
    
    this.tf_idf = WR.getTF_IDF();
  }
  
  /**
  * Comparator of to WindowRow against another. 
  *   Priority of comparison: Var_Rel, Var_Freq, Var_Vel, VarRel_Freq, VarRel_Vel, VarRel_Rel
  * @param WR_i Object to be compared. 
  * @return  -1 or 1.
  */
  @Override
  public int compareTo(Object WR_i) {
    if (!(WR_i instanceof WindowRow))
      throw new ClassCastException("A WindowRow object expected.");
    
    WindowRow WR = ((WindowRow) WR_i);
    
    if(this.VarRel_Rel < WR.VarRel_Rel)
      return -1;
    if(this.VarRel_Rel > WR.VarRel_Rel)
      return  1;
    
    if(this.VarRel_Freq < WR.VarRel_Freq)
      return -1;
    if(this.VarRel_Freq > WR.VarRel_Freq)
      return  1;

    if(this.VarRel_Vel < WR.VarRel_Vel)
      return -1;
    if(this.VarRel_Vel > WR.VarRel_Vel)
      return  1;
    
    if(this.Var_Rel < WR.Var_Rel)
      return -1;
    if(this.Var_Rel > WR.Var_Rel)
      return  1;
    
    if(this.Var_Freq < WR.Var_Freq)
      return -1;
    if(this.Var_Freq > WR.Var_Freq)
      return  1;
    
    if(this.Var_Vel < WR.Var_Vel)
      return -1;
    if(this.Var_Vel > WR.Var_Vel)
      return  1;
    
    
    
    //TODO: add LN variables in the comparison.
    
    return 0;
  }

  /**
  * Validates if the Frequency of W1 and W2 are greater than 0, and if the
  * @return 1=Valid; 0=Initial (same timestamps); -1=One Freq in 0.
  */
  public int isValid() {
    if(W1.getFrequency() == 0 && W2.getFrequency() == 0)
      return -1;
    
    if(W1.getTimeStamp().equals(W2.getTimeStamp()))
      return 0;
    
    return 1;
  }

  /**
  * Computes the Absolute_Variation and Relative_Variation of Freq, Vel, Rel.
  * If division by zero occures, then it sets 0. 
  */
  public void CalcRates() {
    // Absolute Variations
    Var_Freq    = W1.getFrequency()   - W2.getFrequency();
    Var_Freq_LN = W1.getFrequencyLN() - W2.getFrequencyLN();
    Var_Vel     = W1.getVelocity()    - W2.getVelocity();
    Var_Vel_LN  = W1.getVelocityLN()  - W2.getVelocityLN();
    Var_Rel     = W1.getRelevance()   - W2.getRelevance();
    Var_Rel_LN  = W1.getRelevanceLN() - W2.getRelevanceLN();
    
    //Relative Variations
    if(W2.getFrequency() == 0){
      VarRel_Freq = 0.0;
    }
    else{
      VarRel_Freq = Var_Freq*100.0/W2.getFrequency();
    }
    if(W2.getFrequencyLN() == 0){
      VarRel_Freq_LN = 0.0;
    }
    else{
      VarRel_Freq_LN = Var_Freq_LN*100.0/W2.getFrequencyLN();
    }

    if(W2.getVelocity() == 0.0){
      VarRel_Vel = 0.0;
    }
    else{
      VarRel_Vel = Var_Vel / W2.getVelocity() * 100.0;
    }
    if(W2.getVelocityLN() == 0.0){
      VarRel_Vel_LN = 0.0;
    }
    else{
      VarRel_Vel_LN = Var_Vel_LN / W2.getVelocityLN() * 100.0;
    }

    if(W2.getRelevance()==0){
      VarRel_Rel=0.0;
    }
    else{
      VarRel_Rel= Var_Rel*100.0/W2.getRelevance();
    }
    if(W2.getRelevanceLN()==0){
      VarRel_Rel_LN=0.0;
    }
    else{
      VarRel_Rel_LN= Var_Rel_LN*100.0/W2.getRelevanceLN();
    }

  }
  
  /**
  * Computes the MEAN, VARIANCE, ZScore and the variables that supports them.
  * @param BagTimeStamp Timestamp of the Bag of keywords.
  */
  public void CalcRatesVAR(Date BagTimeStamp) {
    n_w0++;
    if(W1.getFrequency() > 0 && W1.getTimeStamp().equals(BagTimeStamp))
      n_w++;

    sum_x         += W1.getFrequency();
    sum_x_ln      += W1.getFrequencyLN();
    sum_x_vel     += W1.getVelocity();
    sum_x_vel_ln  += W1.getVelocityLN();
    sum_x_rel     += W1.getRelevance();
    sum_x_rel_ln  += W1.getRelevanceLN();
    sum_x_var_vel     += this.getVar_Vel();
    sum_x_rel_var_vel += this.getVarRel_Vel();
    sum_x_var_rel     += this.getVar_Vel();
    sum_x_rel_var_rel += this.getVarRel_Vel();

    
    sum_x2        += W1.getFrequency()   * W1.getFrequency();
    sum_x2_ln     += W1.getFrequencyLN() * W1.getFrequencyLN();
    sum_x2_vel    += W1.getVelocity()    * W1.getVelocity();
    sum_x2_vel_ln += W1.getVelocityLN()  * W1.getVelocityLN();
    sum_x2_rel    += W1.getRelevance()   * W1.getRelevance();
    sum_x2_rel_ln += W1.getRelevanceLN() * W1.getRelevanceLN();
    sum_x2_var_vel     += this.getVar_Vel()     * this.getVar_Vel();
    sum_x2_rel_var_vel += this.getVarRel_Vel()  * this.getVarRel_Vel();
    sum_x2_var_rel     += this.getVar_Vel()     * this.getVar_Rel();
    sum_x2_rel_var_rel += this.getVarRel_Vel()  * this.getVarRel_Rel();
    
    mean        = (double) sum_x        / n_w0;
    mean_ln     = (double) sum_x_ln     / n_w0;
    mean_vel    = (double) sum_x_vel    / n_w0;
    mean_vel_ln = (double) sum_x_vel_ln / n_w0;
    mean_rel    = (double) sum_x_rel    / n_w0;
    mean_rel_ln = (double) sum_x_rel_ln / n_w0;
    mean_var_vel      = (double) sum_x_var_vel      / n_w0;
    mean_rel_var_vel  = (double) sum_x_rel_var_vel  / n_w0;
    mean_var_rel      = (double) sum_x_var_rel      / n_w0;
    mean_rel_var_rel  = (double) sum_x_rel_var_rel  / n_w0;
    
    // Variance of König-Huygens
    variance        = 0.0;
    variance_ln     = 0.0;
    variance_vel    = 0.0;
    variance_vel_ln = 0.0;
    variance_rel    = 0.0;
    variance_rel_ln = 0.0;
    if(n_w0 > 1){
      variance        = (sum_x2         - (sum_x        * sum_x)        / (double) n_w0) / (n_w0 - 1);
      variance_ln     = (sum_x2_ln      - (sum_x_ln     * sum_x_ln)     / (double) n_w0) / (n_w0 - 1);
      variance_vel    = (sum_x2_vel     - (sum_x_vel    * sum_x_vel)    / (double) n_w0) / (n_w0 - 1);
      variance_vel_ln = (sum_x2_vel_ln  - (sum_x_vel_ln * sum_x_vel_ln) / (double) n_w0) / (n_w0 - 1);
      variance_rel    = (sum_x2_rel     - (sum_x_rel    * sum_x_rel)    / (double) n_w0) / (n_w0 - 1);
      variance_rel_ln = (sum_x2_rel_ln  - (sum_x_rel_ln * sum_x_rel_ln) / (double) n_w0) / (n_w0 - 1);
      variance_var_vel      = (sum_x2_var_vel     - (sum_x_var_vel      * sum_x_var_vel)      / (double) n_w0) / (n_w0 - 1);
      variance_rel_var_vel  = (sum_x2_rel_var_vel - (sum_x_rel_var_vel  * sum_x_rel_var_vel)  / (double) n_w0) / (n_w0 - 1);
      variance_var_rel      = (sum_x2_var_rel     - (sum_x_var_rel      * sum_x_var_rel)      / (double) n_w0) / (n_w0 - 1);
      variance_rel_var_rel  = (sum_x2_rel_var_rel - (sum_x_rel_var_rel  * sum_x_rel_var_rel)  / (double) n_w0) / (n_w0 - 1);
    }
    stdev         = Math.sqrt((variance         > 0)?  variance        : 0.0);
    stdev_ln      = Math.sqrt((variance_ln      > 0)?  variance_ln     : 0.0);
    stdev_vel     = Math.sqrt((variance_vel     > 0)?  variance_vel    : 0.0);
    stdev_vel_ln  = Math.sqrt((variance_vel_ln  > 0)?  variance_vel_ln : 0.0);
    stdev_rel     = Math.sqrt((variance_rel     > 0)?  variance_rel    : 0.0);
    stdev_rel_ln  = Math.sqrt((variance_rel_ln  > 0)?  variance_rel_ln : 0.0);
    stdev_var_vel     = Math.sqrt((variance_var_vel     > 0)?     variance_var_vel      : 0.0);
    stdev_rel_var_vel = Math.sqrt((variance_rel_var_vel > 0)?     variance_rel_var_vel  : 0.0);
    stdev_var_rel     = Math.sqrt((variance_var_rel     > 0)?     variance_var_rel      : 0.0);
    stdev_rel_var_rel = Math.sqrt((variance_rel_var_rel > 0)?     variance_rel_var_rel  : 0.0);
    
    z_score         = (stdev        > 0.0)? (W1.getFrequency()    - mean        )/stdev         : 0.0;
    z_score_ln      = (stdev_ln     > 0.0)? (W1.getFrequencyLN()  - mean_ln     )/stdev_ln      : 0.0;
    z_score_vel     = (stdev_vel    > 0.0)? (W1.getVelocity()     - mean_vel    )/stdev_vel     : 0.0;
    z_score_vel_ln  = (stdev_vel_ln > 0.0)? (W1.getVelocityLN()   - mean_vel_ln )/stdev_vel_ln  : 0.0;
    z_score_rel     = (stdev_rel    > 0.0)? (W1.getRelevance()    - mean_rel    )/stdev_rel     : 0.0;
    z_score_rel_ln  = (stdev_rel_ln > 0.0)? (W1.getRelevanceLN()  - mean_rel_ln )/stdev_rel_ln  : 0.0;
    z_score_var_vel     = (stdev_var_vel      > 0.0)? (this.getVarVel()     - mean_var_vel      )/stdev_var_vel     : 0.0;
    z_score_rel_var_vel = (stdev_rel_var_vel  > 0.0)? (this.getVarRel_Vel() - mean_rel_var_vel  )/stdev_rel_var_vel : 0.0;
    z_score_var_rel     = (stdev_var_rel      > 0.0)? (this.getVarRel()     - mean_var_rel      )/stdev_var_rel     : 0.0;
    z_score_rel_var_rel = (stdev_rel_var_rel  > 0.0)? (this.getVarRel_Rel() - mean_rel_var_rel  )/stdev_rel_var_rel : 0.0;
  }
  
  /**
  * Computes the TF-IDF:  tf_idf = Freq(W1) * log10(n_w0 / n_w);
  */
  public void CalcRates_TF_IDF() {
    tf_idf = 0.0;
    if(n_w > 0)// && W1.getFrequency() > 0)
      tf_idf = W1.getFrequency() * Math.log10((double) n_w0 / n_w);
  }

  /**
  * Adds Elem in the frequency of thd W1.
  * @param Elem Value to add in the frequency of W1
  */
  public void AddW1(long Elem){
    // TODO: Test of W1.AddFrequency(Elem) works.
    W1.setFrequency(W1.getFrequency() + Elem);
  }
  
  /**
  * Adds Elem in the frequency of thd W2.
  * @param Elem Value to add in the frequency of W2.
  */
  public void AddW2(long Elem){
    // TODO: Test of W2.AddFrequency(Elem) works.
    W2.setFrequency(W2.getFrequency() + Elem);
  }
  
  /**
  * Swaps W1 and W2 and initializes W1 to be filled with the new data.
  * @param newTimeStamp New Timestamp for the new Window1
  */
  public void MoveWindow(Date newTimeStamp){
    Window WAux;
    WAux = W1;
    W1 = W2;
    W2 = WAux;
    W1.Reset(newTimeStamp);
  }
  
  /**
  * @return Returns the Timestamp of the W1 (newer Window).
  */
  public Date getTimeSTampW1(){
    return W1.getTimeStamp();
  }
  
  /**
  * @return Returns the Timestamp of the W2 (older Window).
  */
  public Date getTimeSTampW2(){
    return W2.getTimeStamp();
  }

  /**
  * @return Returns W1 (newer Window).
  */
  public Window getW1(){
    return W1;
  }
  
  /**
  * @return Returns W2 (older Window).
  */
  public Window getW2(){
    return W2;
  }
  
  /**
  * @return Returns the timestamp of the Row (matches with the newest W1).
  */
  public Date getTimeStamp(){
    return TimeStamp;
  }
  
  /**
  * @return Returns the window_size in seconds.
  */
  public long getWindowTime(){
    return WindowTime;
  }
  
  /**
  * @return Returns the Absolute Variation of the Frequencies between W1 and W2:  Freq(W1)-Freq(W2).
  */
  public long getVar_Freq(){
    return Var_Freq;
  }
  
  /**
  * @return Returns the Absolute Variation of the LN Frequencies between W1 and W2:  FreqLN(W1)-FreqLN(W2).
  */
  public double getVar_Freq_LN(){
    return Var_Freq_LN;
  }
  
  /**
   * @return Returns the Absolute Variation of the Arrival Rate between W1 and W2:  AR(W1)-AR(W2)
   */
  public double getVar_Vel(){
    return Var_Vel;
  }
  
  /**
   * @return Returns the Absolute Variation of the LN Arrival Rate between W1 and W2:  AR_LN(W1)-AR_LN(W2)
   */
  public double getVar_Vel_LN(){
    return Var_Vel_LN;
  }

  /**
   * @return Returns the Absolute Variation of the Relevance between W1 and W2:  Rel(W1)-Rel(W2).
   */
  public double getVar_Rel(){
    return Var_Rel;
  }
  
  /**
   * @return Returns the Absolute Variation of the LN Relevance between W1 and W2:  Rel_LN(W1)-Rel_LN(W2).
   */
  public double getVar_Rel_LN(){
    return Var_Rel_LN;
  }
  
  /**
   * @return Returns the Relative Variation of the Frequencies between W1 and W2:  (Freq(W1)-Freq(W2))/Freq(W2)
   */
  public double getVarRel_Freq(){
    return VarRel_Freq;
  }
  
  /**
   * @return Returns the Relative Variation of the LN Frequencies between W1 and W2:  (FreqLN(W1)-FreqLN(W2))/FreqLN(W2)
   */
  public double getVarRel_Freq_LN(){
    return VarRel_Freq_LN;
  }
  
  /**
   * @return Returns the Relative Variation of the Arrival Rate between W1 and W2:  (AR(W1)-AR(W2))/AR(W2)
   */
  public double getVarRel_Vel(){
    return VarRel_Vel;
  }
  
  /**
   * @return Returns the Relative Variation of the LN Arrival Rate between W1 and W2:  (AR_LN(W1)-AR_LN(W2))/AR_LN(W2)
   */
  public double getVarRel_Vel_LN(){
    return VarRel_Vel_LN;
  }
  
  /**
   * @return Returns the Relative Variation of the Relevance between W1 and W2:  (Rel(W1)-Rel(W2))/Rel(W2)
   */
  public double getVarRel_Rel(){
    return VarRel_Rel;
  }
  
  /**
   * @return Returns the Relative Variation of the LN Relevance between W1 and W2:  (Rel_LN(W1)-Rel_LN(W2))/Rel_LN(W2)
   */
  public double getVarRel_Rel_LN(){
    return VarRel_Rel_LN;
  }
  
  /**
   * @return Returns the Number of Windows processed so far. Exncludes empty ones.
   */
  public long getNW(){
    return n_w;
  }
  
  /**
   * @return Returns the Number of Windows processed so far. Includes empty ones.
   */
  public long getNW0(){
    return n_w0;
  }

  /**
   * @return Returns the accumulative frequencies.
   */
  public long getSumX(){
    return sum_x;
  }
  
  /**
   * @return Returns the accumulative LN frequencies.
   */
  public double getSumX_LN(){
    return sum_x_ln;
  }
  
  /**
   * @return Returns the accumulative velocities.
   */
  public double getSumXVel(){
    return sum_x_vel;
  }
  
  /**
   * @return Returns the accumulative LN velocities.
   */
  public double getSumXVel_LN(){
    return sum_x_vel_ln;
  }
  
    /**
   * @return Returns the accumulative relevance.
   */
  public double getSumXRel(){
    return sum_x_rel;
  }
  
  /**
   * @return Returns the accumulative LN relevance.
   */
  public double getSumXRel_LN(){
    return sum_x_rel_ln;
  }
  
  /**
   * @return Returns the accumulative variation of the velocity.
   */
  public double getSumXVarVel(){
    return sum_x_var_vel;
  }
  
  /**
   * @return Returns the accumulative relative_variation of the velocity.
   */
  public double getSumXRelVarVel(){
    return sum_x_rel_var_vel;
  }
  
  /**
   * @return Returns the accumulative variation of the relevance.
   */
  public double getSumXVarRel(){
    return sum_x_var_rel;
  }
  
  /**
   * @return Returns the accumulative relative_variation of the relevance.
   */
  public double getSumXRelVarRel(){
    return sum_x_rel_var_rel;
  }

  /**
   * @return Returns the accumulative frequencies^2.
   */
  public long getSumX2(){
    return sum_x2;
  }
  
  /**
   * @return Returns the accumulative LN_frequencies^2.
   */
  public double getSumX2_LN(){
    return sum_x2_ln;
  }
  
  /**
   * @return Returns the accumulative velocity^2.
   */
  public double getSumX2Vel(){
    return sum_x2_vel;
  }
  
  /**
   * @return Returns the accumulative LN_velocity^2.
   */
  public double getSumX2Vel_LN(){
    return sum_x2_vel_ln;
  }
  
  /**
   * @return Returns the accumulative relevance^2.
   */
  public double getSumX2Rel(){
    return sum_x2_rel;
  }
  
  /**
   * @return Returns the accumulative LN_relevance^2.
   */
  public double getSumX2Rel_LN(){
    return sum_x2_rel_ln;
  }
  
  /**
   * @return Returns the accumulative variation of the velocity^2.
   */
  public double getSumX2VarVel(){
    return sum_x2_var_vel;
  }
  
  /**
   * @return Returns the accumulative relative_variation of the velocity^2.
   */
  public double getSumX2RelVarVel(){
    return sum_x2_rel_var_vel;
  }
  
  /**
   * @return Returns the accumulative variation of the relevance^2.
   */
  public double getSumX2VarRel(){
    return sum_x2_var_rel;
  }
  
  /**
   * @return Returns the accumulative relative_variation of the relevance^2.
   */
  public double getSumX2RelVarRel(){
    return sum_x2_rel_var_rel;
  }

  /**
   * @return Returns the MEAN of frequencies.
   */
  public double getMean(){
    return mean;
  }
  
  /**
   * @return Returns the MEAN of LN_frequencies.
   */
  public double getMean_LN(){
    return mean_ln;
  }
  
  /**
   * @return Returns the MEAN of Velocity.
   */
  public double getMeanVel(){
    return mean_vel;
  }
  
  /**
   * @return Returns the MEAN of LN_Velocity.
   */
  public double getMeanVel_LN(){
    return mean_vel_ln;
  }
  
  /**
   * @return Returns the MEAN of Relevance.
   */
  public double getMeanRel(){
    return mean_rel;
  }
  
  /**
   * @return Returns the MEAN of LN_Relevance.
   */
  public double getMeanRel_LN(){
    return mean_rel_ln;
  }
  
  /**
   * @return Returns the MEAN of the Variation of the Velocity.
   */
  public double getMeanVarVel(){
    return mean_var_vel;
  }
  
  /**
   * @return Returns the MEAN of the Relative Variation of the Velocity.
   */
  public double getMeanRelVarVel(){
    return mean_rel_var_vel;
  }
  
  /**
   * @return Returns the MEAN of the Variation of the Relevance.
   */
  public double getMeanVarRel(){
    return mean_var_rel;
  }
  
  /**
   * @return Returns the MEAN of the Relative Variation of the Relevance.
   */
  public double getMeanRelVarRel(){
    return mean_rel_var_rel;
  }
  
  /**
   * @return Returns the VARIANCE of frequencies.
   */
  public double getVar(){
    return variance;
  }
  
  /**
   * @return Returns the VARIANCE of LN_frequencies.
   */
  public double getVar_LN(){
    return variance_ln;
  }
  
  /**
   * @return Returns the VARIANCE of Velocity.
   */
  public double getVarVel(){
    return variance_vel;
  }
  
  /**
   * @return Returns the VARIANCE of LN_Velocity.
   */
  public double getVarVel_LN(){
    return variance_vel_ln;
  }
  
  /**
   * @return Returns the VARIANCE of Relevance.
   */
  public double getVarRel(){
    return variance_rel;
  }
  
  /**
   * @return Returns the VARIANCE of LN_Relevance.
   */
  public double getVarRel_LN(){
    return variance_rel_ln;
  }
  
  /**
   * @return Returns the VARIANCE of Variation of the Velocity.
   */
  public double getVarVarVel(){
    return variance_var_vel;
  }
  
  /**
   * @return Returns the VARIANCE of Relative_Variation of the Velocity.
   */
  public double getVarRelVarVel(){
    return variance_rel_var_vel;
  }
  
  /**
   * @return Returns the VARIANCE of Variation of the Relevance.
   */
  public double getVarVarRel(){
    return variance_var_rel;
  }
  
  /**
   * @return Returns the VARIANCE of Relative_Variation of the Relevance.
   */
  public double getVarRelVarRel(){
    return variance_rel_var_rel;
  }
  
  /**
   * @return Returns the Sandard-Deviation of frequencies.
   */
  public double getStdev(){
    return stdev;
  }
  
  /**
   * @return Returns the Sandard-Deviation of LN_frequencies.
   */
  public double getStdev_LN(){
    return stdev_ln;
  }
  
  /**
   * @return Returns the Sandard-Deviation of Velocity.
   */
  public double getStdevVel(){
    return stdev_vel;
  }
  
  /**
   * @return Returns the Sandard-Deviation of LN_Velocity.
   */
  public double getStdevVel_LN(){
    return stdev_vel_ln;
  }
  
  /**
   * @return Returns the Sandard-Deviation of Relevance.
   */
  public double getStdevRel(){
    return stdev_rel;
  }
  
  /**
   * @return Returns the Sandard-Deviation of LN_Relevance.
   */
  public double getStdevRel_LN(){
    return stdev_rel_ln;
  }
  
  /**
   * @return Returns the Sandard-Deviation of Variation of the Velocity.
   */
  public double getStdevVarVel(){
    return stdev_var_vel;
  }
  
  /**
   * @return Returns the Sandard-Deviation of Relative Variation of the Velocity.
   */
  public double getStdevRelVarVel(){
    return stdev_rel_var_vel;
  }
  
  /**
   * @return Returns the Sandard-Deviation of Variation of the Relevance.
   */
  public double getStdevVarRel(){
    return stdev_var_rel;
  }
  
  /**
   * @return Returns the Sandard-Deviation of Relative Variation of the Relevance.
   */
  public double getStdevRelVarRel(){
    return stdev_rel_var_rel;
  }
  
  /**
   * @return Returns the ZScore of frequencies.
   */
  public double getZScore(){
    return z_score;
  }
  
  /**
   * @return Returns the ZScore of LN_frequencies.
   */
  public double getZScore_LN(){
    return z_score_ln;
  }
  
  /**
   * @return Returns the ZScore of Velocity.
   */
  public double getZScoreVel(){
    return z_score_vel;
  }
  
  /**
   * @return Returns the ZScore of LN_Velocity.
   */
  public double getZScoreVel_LN(){
    return z_score_vel_ln;
  }
  
  /**
   * @return Returns the ZScore of Relevance.
   */
  public double getZScoreRel(){
    return z_score_rel;
  }
  
  /**
   * @return Returns the ZScore of LN_Relevance.
   */
  public double getZScoreRel_LN(){
    return z_score_rel_ln;
  }
  
  /**
   * @return Returns the ZScore of Variation of the Velocity.
   */
  public double getZScoreVarVel(){
    return z_score_var_vel;
  }
  
  /**
   * @return Returns the ZScore of Variation of the Velocity.
   */
  public double getZScoreRelVarVel(){
    return z_score_rel_var_vel;
  }
  
  /**
   * @return Returns the ZScore of Variation of the Relevance.
   */
  public double getZScoreVarRel(){
    return z_score_var_rel;
  }
  
  /**
   * @return Returns the ZScore of Variation of the Relevance.
   */
  public double getZScoreRelVarRel(){
    return z_score_rel_var_rel;
  }
  
  /**
   * @return Returns the TF-IDF.
   */
  public double getTF_IDF(){
    return tf_idf;
  }

  /**
   * Returns a string representation of the WindowRow class.
   * Format: "{ WindowTime, [ W1 data ][ W2 data ],variation_stats}
   *          {n_w, n_w0, sum_x2, sum_x, mean, variance, stdev, z_score, tf_idf}"
   * @return Returns the string representation of the class.
   */
  @Override
  public String toString(){
    //DecimalFormat twoDForm = new DecimalFormat("#.########");
    DecimalFormat twoDForm = new DecimalFormat("#.##");
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss Z");
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

    long VF     = Var_Freq;
    double VF_LN  = Double.valueOf(twoDForm.format(Var_Freq_LN));
    double VV     = Double.valueOf(twoDForm.format(Var_Vel));
    double VV_LN  = Double.valueOf(twoDForm.format(Var_Vel_LN));
    double VR     = Double.valueOf(twoDForm.format(Var_Rel));
    double VR_LN  = Double.valueOf(twoDForm.format(Var_Rel_LN));
    double VRF    = Double.valueOf(twoDForm.format(VarRel_Freq));
    double VRF_LN = Double.valueOf(twoDForm.format(VarRel_Freq_LN));
    double VRV    = Double.valueOf(twoDForm.format(VarRel_Vel));
    double VRV_LN = Double.valueOf(twoDForm.format(VarRel_Vel_LN));
    double VRR    = Double.valueOf(twoDForm.format(VarRel_Rel));
    double VRR_LN = Double.valueOf(twoDForm.format(VarRel_Rel_LN));
    
    double vt_mean        = Double.valueOf(twoDForm.format(mean));
    double vt_mean_ln     = Double.valueOf(twoDForm.format(mean_ln));
    double vt_mean_vel    = Double.valueOf(twoDForm.format(mean_vel));
    double vt_mean_vel_ln = Double.valueOf(twoDForm.format(mean_vel_ln));
    double vt_mean_rel    = Double.valueOf(twoDForm.format(mean_rel));
    double vt_mean_rel_ln = Double.valueOf(twoDForm.format(mean_rel_ln));
    double vt_mean_var_vel      = Double.valueOf(twoDForm.format(mean_var_vel));
    double vt_mean_rel_var_vel  = Double.valueOf(twoDForm.format(mean_rel_var_vel));
    double vt_mean_var_rel      = Double.valueOf(twoDForm.format(mean_var_rel));
    double vt_mean_rel_var_rel  = Double.valueOf(twoDForm.format(mean_rel_var_rel));
    
    double vt_variance        = Double.valueOf(twoDForm.format(variance));
    double vt_variance_ln     = Double.valueOf(twoDForm.format(variance_ln));
    double vt_variance_vel    = Double.valueOf(twoDForm.format(variance_vel));
    double vt_variance_vel_ln = Double.valueOf(twoDForm.format(variance_vel_ln));
    double vt_variance_rel    = Double.valueOf(twoDForm.format(variance_rel));
    double vt_variance_rel_ln = Double.valueOf(twoDForm.format(variance_rel_ln));
    double vt_variance_var_vel      = Double.valueOf(twoDForm.format(variance_var_vel));
    double vt_variance_rel_var_vel  = Double.valueOf(twoDForm.format(variance_rel_var_vel));
    double vt_variance_var_rel      = Double.valueOf(twoDForm.format(variance_var_rel));
    double vt_variance_rel_var_rel  = Double.valueOf(twoDForm.format(variance_rel_var_rel));
    
    double vt_stdev           = Double.valueOf(twoDForm.format(stdev));
    double vt_stdev_ln        = Double.valueOf(twoDForm.format(stdev_ln));
    double vt_stdev_vel       = Double.valueOf(twoDForm.format(stdev_vel));
    double vt_stdev_vel_ln    = Double.valueOf(twoDForm.format(stdev_vel_ln));
    double vt_stdev_rel       = Double.valueOf(twoDForm.format(stdev_rel));
    double vt_stdev_rel_ln    = Double.valueOf(twoDForm.format(stdev_rel_ln));
    double vt_stdev_var_vel     = Double.valueOf(twoDForm.format(stdev_var_vel));
    double vt_stdev_rel_var_vel = Double.valueOf(twoDForm.format(stdev_rel_var_vel));
    double vt_stdev_var_rel     = Double.valueOf(twoDForm.format(stdev_var_rel));
    double vt_stdev_rel_var_rel = Double.valueOf(twoDForm.format(stdev_rel_var_rel));
    
    double vt_z_score         = Double.valueOf(twoDForm.format(z_score));
    double vt_z_score_ln      = Double.valueOf(twoDForm.format(z_score_ln));
    double vt_z_score_vel     = Double.valueOf(twoDForm.format(z_score_vel));
    double vt_z_score_vel_ln  = Double.valueOf(twoDForm.format(z_score_vel_ln));
    double vt_z_score_rel     = Double.valueOf(twoDForm.format(z_score_rel));
    double vt_z_score_rel_ln  = Double.valueOf(twoDForm.format(z_score_rel_ln));
    double vt_z_score_var_vel     = Double.valueOf(twoDForm.format(z_score_var_vel));
    double vt_z_score_rel_var_vel = Double.valueOf(twoDForm.format(z_score_rel_var_vel));
    double vt_z_score_var_rel     = Double.valueOf(twoDForm.format(z_score_var_rel));
    double vt_z_score_rel_var_rel = Double.valueOf(twoDForm.format(z_score_rel_var_rel));
    

    return "{" + WindowTime + ",\t"
            + "W1[" + dateFormat.format(W1.getTimeStamp()) + ", " 
            + W1.getTotalWindowFreq() + ",\t"
            + W1.getFrequency()   + ", " 
            + twoDForm.format(W1.getFrequencyLN()) + ", " 
            + twoDForm.format(W1.getVelocity())    + ", "
            + twoDForm.format(W1.getVelocityLN())  + ", "
            + twoDForm.format(W1.getRelevance())   + ", "
            + twoDForm.format(W1.getRelevanceLN()) 
            + "]\n\t\t"
            
            + "W2["+ dateFormat.format(W2.getTimeStamp()) + ", "
            + W2.getTotalWindowFreq() + ",\t"
            + W2.getFrequency() + ", "
            + twoDForm.format(W2.getFrequencyLN()) + ", "
            + twoDForm.format(W2.getVelocity())    + ", "
            + twoDForm.format(W2.getVelocityLN())  + ", "
            + twoDForm.format(W2.getRelevance())   + ", "
            + twoDForm.format(W2.getRelevanceLN()) 
            + "],\n\t\t\t\t\t"
            
            + "Var_ABS\t{"            
            + VF      + ", "
            + VF_LN   + ", "
            + VV      + ", " 
            + VV_LN   + ", " 
            + VR      + ", "
            + VR_LN   +
            "}\n\t\t\t\t\t"
            
            + "Var_REL\t{"
            + VRF     + ", " 
            + VRF_LN  + ", " 
            + VRV     + ", " 
            + VRV_LN  + ", " 
            + VRR     + ", " 
            + VRR_LN  
            + "}\n\t\t\t\t\t"
            
            + "COUNT\t{"
            + n_w         + ", "
            + n_w0        
            + "}\n\t\t\t\t\t"
            
            + "SUM\t{"
            + sum_x       + ", "
            + twoDForm.format(sum_x_ln)       + ", "
            + twoDForm.format(sum_x_vel)      + ", "
            + twoDForm.format(sum_x_vel_ln)   + ", "
            + twoDForm.format(sum_x_rel)      + ", "
            + twoDForm.format(sum_x_rel_ln)   + ", "
            + twoDForm.format(sum_x_var_vel)      + ", "
            + twoDForm.format(sum_x_rel_var_vel)  + ", "
            + twoDForm.format(sum_x_var_rel)      + ", "
            + twoDForm.format(sum_x_rel_var_rel)  
            + "}\n\t\t\t\t\t"
            
            + "SUM2\t{"            
            + sum_x2      + ", "
            + twoDForm.format(sum_x2_ln)      + ", "
            + twoDForm.format(sum_x2_vel)     + ", "
            + twoDForm.format(sum_x2_vel_ln)  + ", "
            + twoDForm.format(sum_x2_rel)     + ", "
            + twoDForm.format(sum_x2_rel_ln)  + ", "
            + twoDForm.format(sum_x2_var_vel)      + ", "
            + twoDForm.format(sum_x2_rel_var_vel)  + ", "
            + twoDForm.format(sum_x2_var_rel)      + ", "
            + twoDForm.format(sum_x2_rel_var_rel)  
            + "}\n\t\t\t\t\t"
            
            + "MEAN\t{"
            + vt_mean         + ", "
            + vt_mean_ln      + ", "
            + vt_mean_vel     + ", "
            + vt_mean_vel_ln  + ", "
            + vt_mean_rel     + ", "
            + vt_mean_rel_ln  + ", "
            + vt_mean_var_vel     + ", "
            + vt_mean_rel_var_vel + ", "
            + vt_mean_var_rel     + ", "
            + vt_mean_rel_var_rel
            + "}\n\t\t\t\t\t"
            
            + "VAR\t{"
            + vt_variance         + ", "
            + vt_variance_ln      + ", "
            + vt_variance_vel     + ", "
            + vt_variance_vel_ln  + ", "
            + vt_variance_rel     + ", "
            + vt_variance_rel_ln  + ", "
            + vt_variance_var_vel     + ", "
            + vt_variance_rel_var_vel + ", "
            + vt_variance_var_rel     + ", "
            + vt_variance_rel_var_rel
            + "}\n\t\t\t\t\t"
            
            + "STDEV\t{"            
            + vt_stdev        + ", "
            + vt_stdev_ln     + ", "
            + vt_stdev_vel    + ", "
            + vt_stdev_vel_ln + ", "
            + vt_stdev_rel    + ", "
            + vt_stdev_rel_ln + ", "
            + vt_stdev_var_vel      + ", "
            + vt_stdev_rel_var_vel  + ", "
            + vt_stdev_var_rel      + ", "
            + vt_stdev_rel_var_rel
            + "}\n\t\t\t\t\t"
            
            + "Z_SCORE\t{"
            + vt_z_score      + ", "
            + vt_z_score_ln   + ", "
            + vt_z_score_vel      + ", "
            + vt_z_score_vel_ln   + ", "
            + vt_z_score_rel      + ", "
            + vt_z_score_rel_ln   + ", "
            + vt_z_score_var_vel      + ", "
            + vt_z_score_rel_var_vel  + ", "
            + vt_z_score_var_rel      + ", "
            + vt_z_score_rel_var_rel
            + "}\n\t\t\t\t\t"
            
            + "TF-IDF\t{"
            + twoDForm.format(tf_idf)  
            + "}";
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(W1);
    out.writeObject(W2);
    out.writeObject(TimeStamp);
    out.writeLong  (WindowTime);
    out.writeLong  (Var_Freq);
    out.writeDouble(Var_Freq_LN);
    out.writeDouble(Var_Vel);
    out.writeDouble(Var_Vel_LN);
    out.writeDouble(Var_Rel);
    out.writeDouble(Var_Rel_LN);
    out.writeDouble(VarRel_Freq);
    out.writeDouble(VarRel_Freq_LN);
    out.writeDouble(VarRel_Vel);
    out.writeDouble(VarRel_Vel_LN);
    out.writeDouble(VarRel_Rel);
    out.writeDouble(VarRel_Rel_LN);
    out.writeLong  (n_w0);
    out.writeLong  (n_w);
    out.writeDouble(sum_x);
    out.writeDouble(sum_x_ln);
    out.writeDouble(sum_x_vel);
    out.writeDouble(sum_x_vel_ln);
    out.writeDouble(sum_x_rel);
    out.writeDouble(sum_x_rel_ln);
    out.writeDouble(sum_x_var_vel);
    out.writeDouble(sum_x_rel_var_vel);
    out.writeDouble(sum_x_var_rel);
    out.writeDouble(sum_x_rel_var_rel);
    out.writeLong  (sum_x2);
    out.writeDouble(sum_x2_ln);
    out.writeDouble(sum_x2_vel);
    out.writeDouble(sum_x2_vel_ln);
    out.writeDouble(sum_x2_rel);
    out.writeDouble(sum_x2_rel_ln);
    out.writeDouble(sum_x2_var_vel);
    out.writeDouble(sum_x2_rel_var_vel);
    out.writeDouble(sum_x2_var_rel);
    out.writeDouble(sum_x2_rel_var_rel);
    
    out.writeDouble(mean);
    out.writeDouble(mean_ln);
    out.writeDouble(mean_vel);
    out.writeDouble(mean_vel_ln);
    out.writeDouble(mean_rel);
    out.writeDouble(mean_rel_ln);
    out.writeDouble(mean_var_vel);
    out.writeDouble(mean_rel_var_vel);
    out.writeDouble(mean_var_rel);
    out.writeDouble(mean_rel_var_rel);  
    out.writeDouble(variance);
    out.writeDouble(variance_ln);
    out.writeDouble(variance_vel);
    out.writeDouble(variance_vel_ln);
    out.writeDouble(variance_rel);
    out.writeDouble(variance_rel_ln);
    out.writeDouble(variance_var_vel);
    out.writeDouble(variance_rel_var_vel);
    out.writeDouble(variance_var_rel);
    out.writeDouble(variance_rel_var_rel);
    out.writeDouble(stdev);
    out.writeDouble(stdev_ln);
    out.writeDouble(stdev_vel);
    out.writeDouble(stdev_vel_ln);
    out.writeDouble(stdev_rel);
    out.writeDouble(stdev_rel_ln);
    out.writeDouble(stdev_var_vel);
    out.writeDouble(stdev_rel_var_vel);
    out.writeDouble(stdev_var_rel);
    out.writeDouble(stdev_rel_var_rel);
    out.writeDouble(z_score);
    out.writeDouble(z_score_ln);
    out.writeDouble(z_score_vel);
    out.writeDouble(z_score_vel_ln);
    out.writeDouble(z_score_rel);
    out.writeDouble(z_score_rel_ln);
    out.writeDouble(z_score_var_vel);
    out.writeDouble(z_score_rel_var_vel);
    out.writeDouble(z_score_var_rel);
    out.writeDouble(z_score_rel_var_rel);
    out.writeDouble(tf_idf);
    

  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    W1                   = (Window) in.readObject();
    W2                   = (Window) in.readObject();
    TimeStamp            = (Date)   in.readObject();
    WindowTime           = (long)   in.readLong  ();
    Var_Freq             = (long)   in.readLong  ();
    Var_Freq_LN          = (double) in.readDouble();
    Var_Vel              = (double) in.readDouble();
    Var_Vel_LN           = (double) in.readDouble();
    Var_Rel              = (double) in.readDouble();
    Var_Rel_LN           = (double) in.readDouble();
    VarRel_Freq          = (double) in.readDouble();
    VarRel_Freq_LN       = (double) in.readDouble();
    VarRel_Vel           = (double) in.readDouble();
    VarRel_Vel_LN        = (double) in.readDouble();
    VarRel_Rel           = (double) in.readDouble();
    VarRel_Rel_LN        = (double) in.readDouble();
    n_w0                 = (long)   in.readLong  ();
    n_w                  = (long)   in.readLong  ();
    sum_x                = (long)   in.readLong  ();
    sum_x_ln             = (double) in.readDouble();
    sum_x_vel            = (double) in.readDouble();
    sum_x_vel_ln         = (double) in.readDouble();
    sum_x_rel            = (double) in.readDouble();
    sum_x_rel_ln         = (double) in.readDouble();
    sum_x_var_vel        = (double) in.readDouble();
    sum_x_rel_var_vel    = (double) in.readDouble();
    sum_x_var_rel        = (double) in.readDouble();
    sum_x_rel_var_rel    = (double) in.readDouble();
    sum_x2               = (long)   in.readLong  ();
    sum_x2_ln            = (double) in.readDouble();
    sum_x2_vel           = (double) in.readDouble();
    sum_x2_vel_ln        = (double) in.readDouble();
    sum_x2_rel           = (double) in.readDouble();
    sum_x2_rel_ln        = (double) in.readDouble();
    sum_x2_var_vel       = (double) in.readDouble();
    sum_x2_rel_var_vel   = (double) in.readDouble();
    sum_x2_var_rel       = (double) in.readDouble();
    sum_x2_rel_var_rel   = (double) in.readDouble();
    
    mean                 = (double) in.readDouble();
    mean_ln              = (double) in.readDouble();
    mean_vel             = (double) in.readDouble();
    mean_vel_ln          = (double) in.readDouble();
    mean_rel             = (double) in.readDouble();
    mean_rel_ln          = (double) in.readDouble();
    mean_var_vel         = (double) in.readDouble();
    mean_rel_var_vel     = (double) in.readDouble();
    mean_var_rel         = (double) in.readDouble();
    mean_rel_var_rel     = (double) in.readDouble(); 
    variance             = (double) in.readDouble();
    variance_ln          = (double) in.readDouble();
    variance_vel         = (double) in.readDouble();
    variance_vel_ln      = (double) in.readDouble();
    variance_rel         = (double) in.readDouble();
    variance_rel_ln      = (double) in.readDouble();
    variance_var_vel     = (double) in.readDouble();
    variance_rel_var_vel = (double) in.readDouble();
    variance_var_rel     = (double) in.readDouble();
    variance_rel_var_rel = (double) in.readDouble();
    stdev                = (double) in.readDouble();
    stdev_ln             = (double) in.readDouble();
    stdev_vel            = (double) in.readDouble();
    stdev_vel_ln         = (double) in.readDouble();
    stdev_rel            = (double) in.readDouble();
    stdev_rel_ln         = (double) in.readDouble();
    stdev_var_vel        = (double) in.readDouble();
    stdev_rel_var_vel    = (double) in.readDouble();
    stdev_var_rel        = (double) in.readDouble();
    stdev_rel_var_rel    = (double) in.readDouble();
    z_score              = (double) in.readDouble();
    z_score_ln           = (double) in.readDouble();
    z_score_vel          = (double) in.readDouble();
    z_score_vel_ln       = (double) in.readDouble();
    z_score_rel          = (double) in.readDouble();
    z_score_rel_ln       = (double) in.readDouble();
    z_score_var_vel      = (double) in.readDouble();
    z_score_rel_var_vel  = (double) in.readDouble();
    z_score_var_rel      = (double) in.readDouble();
    z_score_rel_var_rel  = (double) in.readDouble();
    tf_idf               = (double) in.readDouble();




  }

}
