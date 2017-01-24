package cl.uchile.dcc.text;

/**
* This class stores the data of an NGram to be stored into a database.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public class NGramRow {
  /**  Surrogate ID of a the n_gram:   term + rt + lang  */
  public String  _id = "";
  
  /**  Ngram term (token).  */
  public String  _term = "";
  
  /**  Number of characters (n-gram size)  */
  public int     _n = 0;
  
  /**  Flag if it is a ReTweet  */
  public boolean _rt = false;
  
  /**  Language of the tweet where the term was extracted  */
  public String  _lang_tweet = "";
  
  /**  Frequency of occurrance of the Term  */
  public long    _freq_term = 0;
  
  /**  Number of tweets that contains the term in the specific Language*/
  public long    _freq_tweet = 0;
  
  /**
   * Constructor of a Ngram Row an its internal.
   * @param term Token.
   * @param n    Number of characters (n-gram size).
   * @param rt  Is a ReTweet?.
   * @param lang_tweet  Language of the Tweet where the term was extracted.
   * @param freq_term   Frequency of occurrance of a term in the language.
   * @param freq_tweet  Number of Tweets that contains the term in the language specified.
   */
  public NGramRow(String term, int n, boolean rt, String lang_tweet, long freq_term, long freq_tweet){
    _id         = term +"_"+ (rt?"_1":"_0") +"_"+ lang_tweet.toUpperCase();
    _term       = term;
    _n          = n;
    _rt         = rt;
    _lang_tweet = lang_tweet;
    _freq_term  = freq_term;
    _freq_tweet = freq_tweet;
  }

}
