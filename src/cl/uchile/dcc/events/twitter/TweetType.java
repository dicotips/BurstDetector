package cl.uchile.dcc.events.twitter;

/**
* This Enum Class represents the types of a Tweet.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public enum TweetType {
  /** TWEET = Normal Tweet or a RT (Is not generated indirectly). */
  TWEET,
  
  /** QUOTE = Tweet Quoted (referenced as quoted). */
  QUOTE, 
  
  /** RT_ROOT = Tweet referenced by a RT. */
  RT_ROOT,
  
  /** TEMP = Temporal Tweet. */
  TEMP;
}