package cl.uchile.dcc.utils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
* This Class contains the list of phrases and @user_ScreenName blacklisted
* to mark tweets.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-19
*/
public class Blacklist {
  private static ConcurrentHashMap<String, Integer> users;
  private static ConcurrentHashMap<String, Integer> phrases;
  private PropertiesTD prop;
  private Date lastUpdate;
  
  /**
   * Constructor that loads the parameters of the black
   * @param _prop Parameters from the _setup.txt
   */
  public Blacklist(PropertiesTD _prop){
    if(users == null && phrases == null){
      this.prop = _prop;
      users     = loadBL_Users();
      phrases   = loadBL_Phrases();
      lastUpdate = new Date();
      System.out.println("[BlackList] Files created!.");
    }
  }
  
  /**
   * Updates the blacklists from files.
   */
  public void update(){
    
    long time_now  = new Date().getTime()/1000;
    long time_last = lastUpdate.getTime()/1000;
    
    if ((time_now - time_last) > prop.blacklist_time_refresh){
      users     = loadBL_Users();
      phrases   = loadBL_Phrases();
      lastUpdate = new Date();
      System.out.println("[BlackList] Files Updated!.");
    }
  }
  
  /**
   * Loads the content of the blacklist_users into a HashTable Users.
   * @return the blacklist_users in a HashTable.
   */
  private ConcurrentHashMap<String, Integer> loadBL_Users(){
    users = new ConcurrentHashMap<>();
		try (Scanner scanner = new Scanner(new File(prop.blacklist_users_path))) {
			while (scanner.hasNext()){
        users.put(scanner.nextLine().toLowerCase(), Integer.MIN_VALUE);
			}
		} catch (IOException e) {
      System.err.println(e.getMessage());
			//e.printStackTrace();
		}
    return users;
  }
  
  /**
   * Loads the content of the blacklist_phrases into a HashTable Phrases.
   * @return the blacklist_phrases in a HashTable.
   */
  private ConcurrentHashMap<String, Integer> loadBL_Phrases(){
    phrases = new ConcurrentHashMap<>();
    try (Scanner scanner = new Scanner(new File(prop.blacklist_keywords_path))) {
			while (scanner.hasNext()){
        phrases.put(scanner.nextLine().toLowerCase(), Integer.MIN_VALUE);
			}
		} catch (IOException e) {
      System.err.println(e.getMessage());
			//e.printStackTrace();
		}
    return phrases;
  }
  
  /**
   * Tests if the given @user is blacklisted. (lowerCase applied internally).
   * @param user the text to check in blacklist. (no-case sensitive).
   * @return true if @user is blacklisted.
   */
  public boolean containsUser(String user){
    return users.containsKey(user.toLowerCase());
  }
  /**
   * Tests if the given phrase is blacklisted.
   * @param text the text to check in blacklist. (case sensitive).
   * @return true if phrase is blacklisted.
   */
  public boolean containsPhrase(String text){
    for (String bl_phrase : phrases.keySet()) {
      if(text.contains(bl_phrase))
        return true;
    }
    return false;
  }  
  
  /**
   * This is the main method tests the load and use of the USER and PHRASES 
   * blacklists.
   * 
   * @param  args Nothing
   */
  public static void main(String[] args){
    PropertiesTD prop = new PropertiesTD("/Users/dicotips/Dropbox/Research_SourceCode/Twitter_Crawler/_setup.txt");
    Blacklist bl = new Blacklist(prop);
    
    // Testing if BL contains the correct values.
    System.out.println("@usgi_data\t"+ bl.containsUser("@usgi_data"));
    System.out.println("@dicotips \t"+ bl.containsUser("@dicotips"));
    
    System.out.println("\n");
    // test de si contiene "frio"
    System.out.println("calor\t"+           bl.containsPhrase("calor"));
    System.out.println("hace mucho frio\t"+ bl.containsPhrase("hace mucho frio"));
    System.out.println("hace mucho calor\t"+ bl.containsPhrase("me hace calor"));

    System.out.println("\n");
    File file = new File(prop.blacklist_keywords_path);
    System.out.println("Before Format : " + file.lastModified());
    System.out.println("After Format : " + TUtils.Date_Formatter(new Date(file.lastModified())));

  }
}
