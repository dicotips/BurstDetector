package cl.uchile.dcc.events.twitter;

/**
* This Class represents an Entity of HashTag in a tweet,. This entry will be inserted into the database.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public final class EntityHashTag {
  /**
   * Index in the Tweet's text where the HashTags starts.
   */
  private int iStart;
  
  /**
   * Index in the Tweet's text where the HashTags ends.
   */
  private int iEnd;
  
  /**
   * HashTag term in a tweet.
   */
  private String hashtag;

  /**
  * Constructor: Creates an instance of EntityHashTag.
  * @param  iS Start Index of the hashtag in the Tweet.
  * @param  iE End Index of the hashtag in the Tweet.
  * @param  vhashtag HashTag.
  */
  public EntityHashTag(int iS, int iE, String vhashtag){
    setStart(iS);
    setEnd(iE);
    hashtag = vhashtag;
  }
  
  /**
  * Sets the Start Index of the HashTag in the Tweet.
  * @param S Start Index of the hashtag in the Tweet.  
  */
  public void setStart(int S){
    iStart = S;
  }
  
  /**
  * Sets the End Index of the HashTag in the Tweet.
  * @param E Start End of the hashtag in the Tweet.  
  */
  public void setEnd(int E){
    iEnd = E;
  }
  
  /**
  * Sets the HashTag term of the Tweet.
  * @param txt HashTag term.  
  */
  public void setHasTag(String txt){
    hashtag = txt;
  }

  /**
  * Gets the Start Index of the HashTag in the Tweet.
  * @return Start Index of the hashtag in the Tweet.  
  */
  public int getStart(){
    return iStart;
  }
  
  /**
  * Gets the End Index of the HashTag in the Tweet.
  * @return End Index of the hashtag in the Tweet.  
  */
  public int getEnd(){
    return iEnd;
  }
  
  /**
  * Tets the HashTag term of the Tweet.
  * @return HashTag term.
  */
  public String getHashTag(){
    return hashtag;
  }

  /**
   * Returns a string representation of the EntityHashTag class.
   * Format: "Start_Index    End_Index    HashTag"
   * 
   * @return Returns the string representation of the classs.
   */
  @Override
   public String toString(){
    return "{"+getStart() +", "+ getEnd() +", "+ getHashTag() +"}";
   }
}
