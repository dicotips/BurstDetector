package cl.uchile.dcc.events.twitter;

/**
* This Class contains the Sentiment information of a Tweet. It will be inserted
* into the database.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public final class TSenti{
  private String ss_lang;
  private byte   ss_positive;
  private byte   ss_negative;
  private byte   ss_neutral;
  private byte   ss_polarity;

  /**
   * Constructor of the TSenti.
   * @param lang      Language ISO code of the tweet.
   * @param pos       Positive Score (range:  1 to 5).
   * @param neg       Negative Score (range:  1 to 5).
   * @param neutral   Neutral  Score (range:  0 to 4).
   * @param polarity  Polarity Score (range: -4 to 4).
   */
  public TSenti(String lang, byte pos, byte neg, byte neutral, byte polarity){
    setLang(lang);
    setPositive(pos);
    setNegative(neg);
    setNeutral(neutral);
    setPolarity(polarity);  
  }

  /**
   * Sets the Language ISO code of the tweet.
   * @param lang Language ISO code of the tweet.
   */
  public void setLang(String lang){
    ss_lang = lang;
  }
  
  /**
   * Sets the Positive Score (range: 1 to 5).
   * @param val Positive Score (range: 1 to 5).
   */
  public void setPositive(byte val){
    ss_positive = val;
  }
  
  /**
   * Sets the Negative Score (range: 1 to 5).
   * @param val Negative Score (range: 1 to 5).
   */
  public void setNegative(byte val){
    ss_negative = val;
  }

  /**
   * Sets the Neutral Score (range: 0 to 4).
   * @param val Neutral Score (range: 0 to 4).
   */
  public void setNeutral(byte val){
    ss_neutral = val;
  }
  
  /**
   * Sets the Polarity Score (range: -4 to 4).
   * @param val Polarity Score (range: -4 to 4).
   */
  public void setPolarity(byte val){
    ss_polarity = val;
  }
  
  /**
   * Gets the Language ISO code of the tweet.
   * @return Returns the Language ISO code of the tweet.
   */
  public String getLang(){
    return ss_lang;
  }
  
  
  /**
   * Gets the Positive Score.
   * @return Returns the Positive Score.
   */
  public byte getPositive(){
    return ss_positive;
  }
  /**
   * Gets the Negative Score.
   * @return Returns the Negative Score.
   */
  public byte getNegative(){
    return ss_negative;
  }
  
  /**
   * Gets the Neutral Score.
   * @return Returns the Neutral Score.
   */
  public byte getNeutral(){
    return ss_neutral;
  }
  
  /**
   * Gets the Polarity Score.
   * @return Returns the Polarity Score.
   */
  public byte getPolarity(){
    return ss_polarity;
  }

  /**
   * Returns a string representation of the EntityHashTag class.
   * Format: "lang   pos   neg   neutral   polarity"
   * 
   * @return Returns the string representation of the classs.
   */
  @Override
  public String toString(){
    String str =  getLang()     +"\t"+ 
                  getPositive() +"\t"+
                  getNegative() +"\t"+
                  getNeutral()  +"\t"+
                  getPolarity();
    return str;
  }
}
