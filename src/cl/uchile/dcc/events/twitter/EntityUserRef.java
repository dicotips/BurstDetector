package cl.uchile.dcc.events.twitter;

/**
* This Class represents an Entity of User References inside of a tweet. This
* entry will be inserted into the database.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public final class EntityUserRef {
  /**
   * Index in the Tweet's text where the UserReference starts.
   */
  private int iStart;
  
  /**
   * Index in the Tweet's text where the UserReference ends.
   */
  private int iEnd;
  
  /**
   * User Reference ID.
   */
  private long uid;
  
  /**
   * User Reference Screen_Name (@).
   */
  private String screen_name;
  
  /**
   * User Reference Name.
   */
  private String name;

  /**
   * 
   * @param iS            UserReference starts index in the tweet.
   * @param iE            UserReference starts index in the tweet.
   * @param vuid          User Reference ID.
   * @param vscreen_name  User Reference Screen_Name (@).
   * @param vname         User Reference Name.
   */
  public EntityUserRef(int iS,int iE,long vuid,String vscreen_name, String vname){
    setStart(iS);
    setEnd(iE);
    setUID(vuid);
    setScreenName(vscreen_name);
    setName(vname);
  }
  
  /**
   * Sets the Start Index of the User Reference in the Tweet.
   * @param S Start Index of the User Reference in the Tweet.  
   */
  public void setStart(int S){
    iStart = S;
  }
  
  /**
   * Sets the End Index of the User Reference in the Tweet.
   * @param E End Index of the User Reference in the Tweet.  
   */
  public void setEnd(int E){
    iEnd = E;
  }
  
  /**
   * Sets the ID of the User Reference in the Tweet.
   * @param id ID of the User Reference in the Tweet.  
   */
  public void setUID(long id){
    uid=id;
  }
  
  /**
   * Sets the ScreenName(@) of the User Reference in the Tweet.
   * @param sn ScreenName(@) of the User Reference in the Tweet.  
   */
  public void setScreenName(String sn){
    screen_name = sn;
  }
  
  /**
   * Sets the Name of the User Reference in the Tweet.
   * @param n Name of the User Reference in the Tweet.  
   */
  public void setName(String n){
    name = n;
  }

  /**
   * Gets the Start Index of the User Reference in the Tweet.
   * @return Returns the Start Index of the User Reference in the Tweet.  
   */
  public int getStart(){
    return iStart;
  }
  
  /**
   * Gets the End Index of the User Reference in the Tweet.
   * @return Returns the End Index of the User Reference in the Tweet.  
   */
  public int getEnd(){
    return iEnd;
  }
  
  /**
   * Gets the ID of the User Reference in the Tweet.
   * @return Returns the ID of the User Reference in the Tweet.  
   */
  public long getUID(){
    return uid;
  }
  
  /**
   * Gets the ScreenName(@) of the User Reference in the Tweet.
   * @return Returns the ScreenName(@) of the User Reference in the Tweet.  
   */
  public String getScreenName(){
    return screen_name;
  }
  
  /**
   * Gets the Name of the User Reference in the Tweet.
   * @return  Returns the Name of the User Reference in the Tweet.  
   */
  public String getName(){
    return name;
  }

  /**
   * Returns a string representation of the EntityHashTag class.
   * Format: "Start_Index    End_Index    User_ID   @ScreenName   User_Name"
   * 
   * @return Returns the string representation of the classs.
   */
  @Override
  public String toString(){
    return "{"+ getStart() +", "+ getEnd() +", "+getUID()+", "+getScreenName()+", "+getName() + "}";
  }
}
