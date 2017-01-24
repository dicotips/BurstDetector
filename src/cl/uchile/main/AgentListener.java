package cl.uchile.main;

import cl.uchile.dcc.events.twitter.Tweet;
import cl.uchile.dcc.events.twitter.TweetType;
import cl.uchile.dcc.utils.PropertiesTD;
import cl.uchile.dcc.utils.TUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
* The AgentDListener Agent downloads tweets from the Twitter Stream..
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0
* @since       2016-08-17
*/
public class AgentListener {

  private ConcurrentHashMap<Long, Date>  HT_users;
  private BlockingQueue<Tweet> TweetStreamCache;
  private ConfigurationBuilder cb;
  private PropertiesTD prop;
  private boolean state = false;
  private int error_count = 0;
  /** Counter of tweet since the last check. */
  public long count_tweets = 0;
  /** Counter of tweet in total. */
  public long count_tweets_total = 0;
  private int prop_user_index;
  
  /** TwitterStream Listener object */
  public TwitterStream twitterStream;
  private static List<String> tweet_languages;
  
  
  /**
   * Constructor.
   * <p>
   * Connects to the Twitter Stream to download tweets, cleans them, encapsulates them and
   * adds meta-data (GeoLocation, Sentiment).
   * <p>
   * @param HT_R_Users  HashTable with the users that posted in the last 5 mins.
   * @param PropTD      Parameters from the _setup.txt file.
   * @param user_idx    Index of the UserConnection.
   * @param TwQueue     Queue to push Tweets into. Sends the tweets to the DBStorer.
   */
  AgentListener(ConcurrentHashMap<Long, Date>  HT_R_Users, PropertiesTD PropTD, int user_idx, BlockingQueue<Tweet> TwQueue) {
    HT_users = HT_R_Users;
    prop = PropTD;
    prop_user_index = user_idx;
    
    TweetStreamCache = TwQueue;
    cb = new ConfigurationBuilder();
    
    cb.setDebugEnabled(true);
    cb.setOAuthConsumerKey(prop.AuthConsumerKey.get(prop_user_index));
    cb.setOAuthConsumerSecret(prop.AuthConsumerSecret.get(prop_user_index));
    cb.setOAuthAccessToken(prop.AuthAccessToken.get(prop_user_index));
    cb.setOAuthAccessTokenSecret(prop.AuthAccessTokenSecret.get(prop_user_index));
    
    cb.setStreamBaseURL(prop.StreamBaseURL);
    cb.setOAuthRequestTokenURL(prop.RequestTokenURL);
    cb.setOAuthAuthorizationURL(prop.AuthorizationURL);
    cb.setOAuthAccessTokenURL(prop.AccessTokenURL);
    cb.setOAuthAuthenticationURL(prop.AuthenticationURL);
    
    tweet_languages = Arrays.asList(PropTD.tweet_languages);
    
  }
  
  /**
   * Method that checks if there a problem with the Stream.
   * @return TRUE=Working Well;   FALSE=Problems Downloading Tweets
   */
  public boolean isAlive(){
    if(count_tweets == 0)
      state = false;
    
    return state;
  }
  
  /**
   * Body of the Thread: Starts to Download tweets from the Twitter Stream.
   * 
   */
    //TWITTER LISTENER - STREAM
  public void start(){

    System.out.println("[SYSTEM] Init-Date: "+ TUtils.Date_Formatter2(Twitter_Crawler_Stream.init_date));
    
    twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
    
    StatusListener listener = new StatusListener(){
      @Override public void onStatus(Status status) {
        if(tweet_languages.contains(status.getLang().toUpperCase()) 
                || tweet_languages.contains("ALL")){
          Tweet tweet;
          tweet = new Tweet(status, prop);
          Tweet tweet1, tweet2;

          state = true;
          error_count = 0;
          count_tweets++;
          count_tweets_total++;

          if(status.getQuotedStatus() != null && prop.store_qt){
            tweet1 = new Tweet(status.getQuotedStatus(), prop);
            tweet1.setType(TweetType.QUOTE);
            TweetStreamCache.add(tweet1);   // enQUEUE Tweet_RT
          }
          if(status.getRetweetedStatus() != null && prop.store_rt){
            tweet2 = new Tweet(status.getRetweetedStatus(), prop);
            tweet2.setType(TweetType.RT_ROOT);
            TweetStreamCache.add(tweet2);   // enQUEUE Tweet_Quote
          }

          
          // Detect if the user is repeaded in the last minutes.
          if(prop.repeated_user){
            if(HT_users.containsKey(tweet.getUser().getId())){
              if((tweet.getDownloadedAt().getTime() - HT_users.get(tweet.getUser().getId()).getTime()) 
                      < (prop.repeated_user_time * 1000)){
                tweet.setRepeatedUser(true);
              }
              else{
                HT_users.put(tweet.getUser().getId(), tweet.getDownloadedAt());
              }
              
            }
            else{
              HT_users.put(tweet.getUser().getId(), tweet.getDownloadedAt());
            }
            
          }
          
          TweetStreamCache.add(tweet);  // enQUEUE Tweet0
          
        }
      }

      @Override public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
      @Override public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
      @Override public void onException(Exception ex) {
        state = false;
        System.err.println(ex.getMessage());
        //ex.printStackTrace();
      }
      @Override public void onScrubGeo(long userId, long upToStatusId) {}
      @Override public void onStallWarning(StallWarning warning) {
          throw new UnsupportedOperationException("Not supported yet.");
      }
    };
    
    //Filters for the Stream (BoundingBox and Keywords queries).
    FilterQuery filter = new FilterQuery();
    if(!prop.keywords.isEmpty()){
      filter.track((String[]) prop.keywords.toArray(new String[prop.keywords.size()]));
    }
    if(prop.isValidBB()){
      filter.locations(prop.boundingBox);
    }
    
    twitterStream.addListener(listener);
   
    if(prop.keywords != null || prop.isValidBB()){
      twitterStream.filter(filter);
    }
    if(prop.research_sample){
      twitterStream.sample(); //Random Sample of the FIREHOSE for research (representative).
    }
  }
}
