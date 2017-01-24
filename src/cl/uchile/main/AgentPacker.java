package cl.uchile.main;

import cl.uchile.dcc.events.twitter.Tweet;
import cl.uchile.dcc.events.twitter.TweetType;
import cl.uchile.dcc.text.NGramRow;
import cl.uchile.dcc.utils.PropertiesTD;
import cl.uchile.dcc.text.RegexAnalyzer;
import cl.uchile.dcc.utils.TUtils;
import cl.uchile.dcc.text.TextAnalyzer;
import cl.uchile.dcc.utils.InitialState;
import cl.uchile.dcc.utils.NGramsBag;
import cl.uchile.dcc.utils.WordsBag;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
//import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This Class implements an application that gets the Tweets sent from the 
 * AgStorer and creates the signals into bins. It caches each bin to the 
 * AgProcessor.
 *
 * @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
 * @version     1.0                 
 * @since       2016-08-17
 */
public class AgentPacker extends Thread{
  
  private BlockingQueue<Tweet>     Q_IN;
  private BlockingQueue<WordsBag>  Q_OUT_k;
  private BlockingQueue<NGramsBag> Q_OUT_g;

  private List<String> event_languages;
  private PropertiesTD prop;
  
  private long WinTimeSeconds;
  private long WinTimeSeconds_Shift;
  private static Set<String> keyword_name_keywords;
  private int _id_thread;
  private Date IniWinTime;
  private Date EndWinTime;
  private WordsBag  w_bag;
  private NGramsBag g_bag;
  private String keyword_name;
  
  private BlockingQueue<String> keywords = new LinkedBlockingQueue<>();
  private BlockingQueue<NGramRow> ngrams = new LinkedBlockingQueue<>();
  
  private boolean FlagIni = true;
  private boolean store_ngrams;

  private TextAnalyzer processor = null;
  
  /**
   * Constructor.
   * <p>
   * Initializes the AgentPacker with the parameters in _setup.txt file,
   * specifies the input of tweets and sends the bins to the AgProcessor. 
   * @param id_thread   ID of the Thread that runs in parallel.
   * @param QTwIn       Input queue with tweets.
   * @param QBgOut      Output quque with bins of keywords.
   * @param QGQOut      Output quque with bins of NGrams.
   * @param PropTD      Parameters from the _setup.txt file.
   * @param TimeShift   Shift in the timestamp to process the pipeline in parallel.
   */
  public AgentPacker(int id_thread,
                     BlockingQueue<Tweet>     QTwIn,
                     BlockingQueue<WordsBag>  QBgOut, 
                     BlockingQueue<NGramsBag> QGQOut,
                     PropertiesTD PropTD,
                     long TimeShift) {
       
    _id_thread = id_thread;
    processor = new RegexAnalyzer(PropTD);
    
    prop = PropTD;
    Q_IN        = QTwIn;
    Q_OUT_k     = QBgOut;
    Q_OUT_g     = QGQOut;
    
    store_ngrams = PropTD.store_ngrams && (_id_thread == 0);
    WinTimeSeconds = PropTD.event_window_size;
    WinTimeSeconds_Shift = TimeShift;
    keyword_name_keywords = PropTD.keywords;
    keyword_name = prop.keywords_name;
    
    event_languages = Arrays.asList(PropTD.event_languages);
    
    FlagIni = true;
    processor = new RegexAnalyzer(PropTD);

  }

  /**
   * Body of the Thread: retrieves the tweets, packs them into bins based on the
   * timestamp and windows size (+shifts), and sends them to the AgProcessor. It 
   * also creates the NGrams and stores them into NGramsBags to send them to the 
   * AgDBNgrams to be stored into the database.
   */
  @Override
  public void run(){
        
    while(true){
      try {         
        // La cola de entrada esta VACIA
        if(Q_IN.isEmpty()){
          
          int sleep_time = 7000;
          System.out.println("[AgPacker"+_id_thread+" - Sleep "+sleep_time+"]"
                  + "(in: "   + Q_IN.size()    +"]"
                  + "[out_k: "+ Q_OUT_k.size() +"][k: "+keywords.size()   +"]"
                  + "[out_g: "+ Q_OUT_g.size() +"][g: "+ngrams.size()     +")");
          this.sleep(sleep_time);
          continue;
        }
        
        Tweet tweet = Q_IN.poll();
        
        // Primer tweet del stream
        //System.err.println(FlagIni +"\t"+ tweet.getId() +"\t"+ tweet.getDownloadedAt().getTime() +"\t"+ EndWinTime.getTime());
        if(FlagIni || tweet.getDownloadedAt().getTime() >= EndWinTime.getTime()){
          FlagIni = false;
          
          //Cerrar Bags antiguos y enviar a AgProcessor
          if(w_bag != null)
            Q_OUT_k.add(w_bag);
          if(store_ngrams && g_bag != null)
            Q_OUT_g.add(g_bag);
          
          //Crear Bolsa nueva y llenar tweets.
          Date[] win_date = TUtils.getWinBounds(tweet.getDownloadedAt(), WinTimeSeconds, WinTimeSeconds_Shift);
          IniWinTime = win_date[0];
          EndWinTime = win_date[1];
          
          //IF the time is not complete, does not add a bag.
          //System.out.println(">>PACKER: "+IniWinTime+"\t"+
          //        Twitter_Crawler_Stream.init_date+"\t"+
          //        IniWinTime.compareTo(Twitter_Crawler_Stream.init_date));
          if(Twitter_Crawler_Stream.ini_state == InitialState.RECOVERY_DATABASE && IniWinTime.compareTo(Twitter_Crawler_Stream.init_date) < 0){
            FlagIni = true;
            continue;
          }
          
          keywords   = new LinkedBlockingQueue<>();
          ngrams     = new LinkedBlockingQueue<>();
          w_bag = new WordsBag (IniWinTime, keywords);
          g_bag = new NGramsBag(IniWinTime, ngrams);
          
          System.out.println((char)27 + "[35;40mPACKER"+_id_thread+" [BAG ADDED][WindowTime "+TUtils.Date_Formatter2(IniWinTime)+"  -  "+ TUtils.Date_Formatter2(EndWinTime) +"]" + (char)27 + "[0m");
        }
        
        //Discard RT Root and Quoted for the Burst Analysis.
        if(tweet.getType() != TweetType.TWEET)
          continue;
        w_bag.c_tweets++;
          
        //KeyWords (Tokenize, filtered Stopwords)
        String tweet_txt = tweet.getText();
        if(tweet.getRTText() != null)
          tweet_txt = tweet.getRTText();
        if(tweet.getTextQuoted() != null)
          tweet_txt = tweet.getTextQuoted() +" "+ tweet_txt;

        Map<String,Integer> ht = processor.analyzeText(tweet_txt, tweet.getLanguage());
        if(ht.size() > 0 && (event_languages.contains(tweet.getLanguage().toUpperCase()) 
                || event_languages.contains("ALL"))){

          for(String sval : ht.keySet()){
            for(int i=0; i<ht.get(sval);i++){
              if (prop.track_signal_bursty_keywords) {
                keywords.add(sval);
                w_bag.c_kw++;
              }
              else if(prop.track_signal_keywords) {
                for (String str: prop.keywords) {
                  if(sval.equals(str)){
                    keywords.add(sval);
                    w_bag.c_kw++;
                  }
                }
              }
            }
          }
        }
               
        // Keyword_name Signal
        for (String e_word : keyword_name_keywords) {
          if(tweet.getText().toLowerCase().contains(e_word.toLowerCase())){
            keywords.add("__sys__" + keyword_name);
            w_bag.c_eq++;
            break;
          }
        }
        
        // Sentiment values Signal
        if(tweet.getSentiment() != null && prop.track_signal_sentiment){
          if(tweet.getSentiment().getPolarity() > 1){
            keywords.add("__sys__ss_positive");
            w_bag.c_ss++;
          }
          else if(tweet.getSentiment().getPolarity() < -1){ 
            keywords.add("__sys__ss_negative");
            w_bag.c_ss++;
          }
          else{
            keywords.add("__sys__ss_neutral");
            w_bag.c_ss++;
          }
        }
        
        // Languages Signal
        if (prop.track_signal_language) {
          String lang_kw = "__sys__lang__"+ tweet.getLanguage().toUpperCase();
          keywords.add(lang_kw);
          w_bag.c_lang++;
        }
        
        // Country in Text Signal
        if (prop.track_signal_geo_text) {
          for (String country : tweet.getCountriesText()) {
            keywords.add("__sys__country_txt__" + country.toUpperCase());
            w_bag.c_Ctxt++;
          }
        }
        
        // Country in User Signal
        if(tweet.getUser().getCountryUser() != null && prop.track_signal_geo_user){
          for (String country : tweet.getUser().getCountryUser()) {
            keywords.add("__sys__country_usr__" + country.toUpperCase());
            w_bag.c_Cusr++;
          }
        }
          
        // Country in Geo Signal
        if(tweet.getPlace() != null && prop.track_signal_geo_location){
          for (String country : tweet.getUser().getCountryUser()) {
            keywords.add("__sys__country_geo__" + country.toUpperCase());
            w_bag.c_Cgeo++;
          }
        }
        
        // NGrams enqued Signal
        if(store_ngrams){
          String text = tweet.getText();
          if(tweet.isRetweet()){
            text = tweet.getRTText();
            if(tweet.isQuote())
              text = tweet.getTextQuoted() +" "+ text;
          }

          Map<String, Integer> tokens = processor.analyzeText_NGram(text);

          for (String token : tokens.keySet()) {
            NGramRow ngram = new NGramRow(token, token.length(), tweet.isRetweet(), tweet.getLanguage(), tokens.get(token), 1);
            ngrams.add(ngram);
          }
        }
        
      } catch (Exception e) {
        e.printStackTrace();
        if(e.toString().trim().equals("java.lang.InterruptedException: sleep interrupted"))
          System.err.println("AgPacker"+_id_thread+" STOPPED");
        else{
          System.err.println("AgPacker"+_id_thread+" ERROR: " + e.toString());
          System.err.println("AgPacker"+_id_thread+" ERROR: " + Arrays.toString(e.getStackTrace()));
        }
      }
    }
  } 
}
