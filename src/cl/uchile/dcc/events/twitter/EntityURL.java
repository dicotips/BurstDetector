package cl.uchile.dcc.events.twitter;

/**
* This Class represents an Entity of URLS inside of a tweet. This entry will 
* be inserted into the database.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public final class EntityURL{
  /**
   * Index in the Tweet's text where the URLS starts.
   */
  private int iStart;
  
  /**
   * Index in the Tweet's text where the URLS Ends.
   */
  private int iEnd;
  
  /**
   * Short URL address in a tweet.
   */
  private String url;
  
  /**
   * Extended URL address in a tweet.
   */
  private String expanded_url;

  /**
   * Constructor: Creates an instance of EntityURL.
   * @param iS    Start index of the URL in the Tweet.
   * @param iE    End index of the URL in the Tweet.
   * @param vurl  Short URL address in a tweet.
   * @param vexpanded_url Extended URL address in a tweet.
   */
  public EntityURL(int iS,int iE,String vurl,String vexpanded_url){
    setStart(iS);
    setEnd(iE);
    setURL(vurl);
    setExpandedURL(vexpanded_url);
  }
  
  /**
   * Sets the Start Index of the URL in the Tweet.
   * @param S Start Index of the URL in the Tweet.  
   */
  public void setStart(int S){
    iStart = S;
  }
  
  /**
   * Sets the End Index of the URL in the Tweet.
   * @param E End Index of the URL in the Tweet.  
   */
  public void setEnd(int E){
    iEnd = E;
  }
  
  /**
  * Sets the Short URL address of the Tweet.
  * @param U Short URL address.  
  */
  public void setURL(String U){
    url = U;
  }
  
  /**
  * Sets the Extended URL address of the Tweet.
  * @param eU HashTag address.  
  */
  public void setExpandedURL(String eU){
    expanded_url = eU;
  }

  /**
   * Gets the Start Index of the URL in the Tweet.
   * @return Returns the Start Index of the URL in the Tweet.  
   */
  public int getStart(){
    return iStart;
  }
  
  /**
   * Gets the End Index of the URL in the Tweet.
   * @return Returns the End Index of the URL in the Tweet.  
   */
  public int getEnd(){
    return iEnd;
  }
  
  /**
  * Gets the Short URL address of the Tweet.
  * @return Returns the Short URL address.  
  */
  public String getURL(){
    return url;
  }
  
  /**
  * Gets the Extended URL address of the Tweet.
  * @return Returns the Extended URL address.  
  */
  public String getExpandedURL(){
    return expanded_url;
  }

  /**
   * Returns a string representation of the EntityHashTag class.
   * Format: "Start_Index    End_Index    Short_URL   Extended_URL"
   * 
   * @return Returns the string representation of the classs.
   */
  @Override
  public String toString(){
    String str =    "{"+
                    getStart()  +", "+ 
                    getEnd()    +", "+
                    getURL()    +", "+
                    getExpandedURL()
                    +"}";
    return str;
  }
}
