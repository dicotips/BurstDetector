package cl.uchile.dcc.sentiment;

import cl.uchile.dcc.utils.PropertiesTD;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.wlv.sentistrength.SentiStrength;

/**
* This Class create a classifier for sentiment analysis in many languages. It
* uses the API SentiStreight and the dictionaries specifid in the _setup.txt file.
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public class SentiStrengthLang {
  /**
   * Actual classifier per language used. When ot switches the language it 
   * cannot be instanciated separatelly. Py default is used ENGLISH.
   */
  private static Language _actual_language = Language.EN;
  
  /**
   * Map for the Language and its classifier.
   */
  private Map<String, SentiStrength> sentiStrength = new HashMap<>();
  
  /**
   * Properties parameters from _setup.txt file.
   */
  private PropertiesTD prop;

  /**
   * Constructor: Preates an instance with all sentiment classifiers per language.
   * @param _prop Properties parameters from _setup.txt file.
   */
  public SentiStrengthLang(PropertiesTD _prop){
    prop = _prop;
    for (String str : _prop.ss_data.keySet()) {
      String[] param = {"sentidata", _prop.ss_data.get(str), "trinary"};
      SentiStrength ss = new SentiStrength();
      ss.initialise(param);
      
      sentiStrength.put(str, ss);
    }
  }
  
  /**
   * Detects the sentiment category from a given text and its language.
   * @param lang Language of the msg.
   * @param msg  String to be classified into sentiment. 
   * @return  Returns an array of the statistivs of the classification. 
   *          Format:  [ language_name , pos , neg , neutral , pos+neg ]
   */
  public String[] detect(Language lang, String msg){
    SentiStrength ss = sentiStrength.get(lang.name());
    if(lang != _actual_language){
      String[] param = {"sentidata", prop.ss_data.get(lang.name()), "trinary"};
      ss.initialise(param);
      _actual_language = lang;
    }
    String[] value = ss.computeSentimentScores(msg).split(" ");
    
    //System.out.println(Arrays.toString(value));
    
    int scale = Integer.parseInt(value[1]) + Integer.parseInt(value[0]);
    String[] token = {lang.name(), value[0], value[1], value[2], Integer.toString(scale)};
    
    return token;
  }

  /**
   * This is the main method tests the use of the SentiStreigth with 
   * different Languages.
   * 
   * @param  args Nothing
   */
  public static void main(String[] args) {
    SentiStrengthLang sentLang;
    PropertiesTD prop = new PropertiesTD("/Users/dicotips/Dropbox/Research_SourceCode/Twitter_Crawler/_setup.txt");
    sentLang = new SentiStrengthLang(prop);

    /*
    String TweetText = "Te amo mucho";
    String[] sToken = sentLang.detect(Language.ES, TweetText);
    System.out.println(TweetText);
    System.out.println(Arrays.toString(sToken));

    TweetText = "bacán";
    sToken = sentLang.detect(Language.ES, TweetText);
    System.out.println(TweetText);
    System.out.println(Arrays.toString(sToken));

    TweetText = "carajo BaKan";
    sToken = sentLang.detect(Language.ES, TweetText);
    System.out.println(TweetText);
    System.out.println(Arrays.toString(sToken));

    TweetText = "I love you";
    sToken = sentLang.detect(Language.EN, TweetText);
    System.out.println(TweetText);
    System.out.println(Arrays.toString(sToken));
    
    TweetText = "ミス";
    sToken = sentLang.detect(Language.JA, TweetText);
    System.out.println(TweetText);
    System.out.println(Arrays.toString(sToken));
    */
    
    try {
      FileReader fileReader = new FileReader("/Users/dicotips/Desktop/20160830_Sentiment.txt");
      Scanner sc = new Scanner(new BufferedReader(fileReader));
      while(sc.hasNextLine()){
        String[] tokens = sc.nextLine().split("\\t");
        String[] sent   = sentLang.detect(Language.ES, tokens[1]);
        System.out.println(tokens[0] +"\t"+ Arrays.toString(sent) +"\t"+ tokens[1]);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  
}
