package cl.uchile.dcc.text;

import cl.uchile.dcc.utils.PropertiesTD;

import edu.stanford.nlp.util.StringUtils;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * This class tokenizes text in diferent languages based on the StanfordNLP
 * API. It is mainly used for the Chinesse language. It only supports EN, ES,
 * FR, DE, ZH.
 * 
 * @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
 * @version     1.0
 * @since       2016-08-17
 */
public class LanguagesAnalizer {
  private Map<String, Properties> prop_map = new HashMap<>();
  private Map<String, StanfordCoreNLP> pipeline_map = new HashMap<>();
  private PropertiesTD prop;
  
  /**
   * Constructor that loads the libraries from StanfordNLP per language.
   * @param prop Setup file with the List of languages to load.
   */
  public LanguagesAnalizer(PropertiesTD _prop){
    prop = _prop;
    String[] languages = _prop.stanford_nlp_languages;
    // this is your print stream, store the reference
    PrintStream err = System.err;
    // now make all writes to the System.err stream silent 
    System.setErr(new PrintStream(new OutputStream() {
        public void write(int b) {
        }
    }));
    //-------------------------------------------------------------------------
    System.out.println("[Libraries] Loading StanfordCoreNLP ...");
    System.out.println("[Libraries] "+ Arrays.toString(languages));
    
    if(Arrays.asList(languages).contains("EN"))
      prop_map.put("en", (Properties) new Properties().setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref"));
    
    if(Arrays.asList(languages).contains("ES"))
      prop_map.put("es", StringUtils.argsToProperties("-props", "StanfordCoreNLP-spanish.properties"));
    
    if(Arrays.asList(languages).contains("FR"))
      prop_map.put("fr", StringUtils.argsToProperties("-props", "StanfordCoreNLP-french.properties"));
    
    if(Arrays.asList(languages).contains("DE"))
      prop_map.put("de", StringUtils.argsToProperties("-props", "StanfordCoreNLP-german.properties"));
    
    if(Arrays.asList(languages).contains("ZH"))
      prop_map.put("zh", StringUtils.argsToProperties("-props", "StanfordCoreNLP-chinese.properties"));
    
    
    for (String key : prop_map.keySet()) {
      System.out.println("[Libraries] Loading Language "+ key.toUpperCase() +"...");
      StanfordCoreNLP pipeline = new StanfordCoreNLP(prop_map.get(key));
      pipeline_map.put(key, pipeline);    
    }
    System.out.println("[Libraries] Load Complete!");
    //-------------------------------------------------------------------------
    // set everything bck to its original state afterwards
    System.setErr(err);
    
  }
  
  /**
   * Verifies if the Language Analyzer has the language library loaded.
   * @param lang Lannguage contained?.
   * @return TRUE if contains the language,; FALSE if not.
   */
  public boolean containsLanguage(String lang){
    return pipeline_map.keySet().contains(lang);
  }
  
  /**
   * Create tokens of the text in its language using StanfordNLP API.
   * @param text  Text to tokenize.
   * @param lang  Language of the text.
   * @return Map with tokens and their frequency.
   */
  public Map<String, Integer> analyzeText(String text, String lang){
    Map<String, Integer> tokens_map = new HashMap<>();
    
    
    Annotation document = new Annotation(text);

    pipeline_map.get(lang).annotate(document);
    List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
    for (CoreLabel token : tokens){
      String word = token.get(TextAnnotation.class);
      //System.out.println(word);

      if(tokens_map.containsKey(word))
        tokens_map.put(word, tokens_map.get(word)+1);
      else
        tokens_map.put(word, 1);
    }
    
    return tokens_map;
  }
  
  /**
   * This is the main method tests the use of the LanguageAnalyzer for 
   * tokenizing textx using StanfordNLP API.
   * 
   * @param  args Nothing
   */
  public static void main(String[] args){
    PropertiesTD PropTD = new PropertiesTD("/Users/dicotips/Dropbox/Research_SourceCode/Twitter_Crawler/_setup.txt");
    String[] prop = {"zh"};
    LanguagesAnalizer nlp = new LanguagesAnalizer(PropTD);
    
    String[] str_array = new String[8];
    str_array[0] = "我在痞客邦 PIXNET 新增了篇文章：捷運中山站美食 新丼 https://t.co/cs2KxfL83k";
    str_array[1] = "有時候不意外迷這對的這麼多 但我眼裡就是阿呆阿瓜XDDDDDD https://t.co/1ABZPLWXz0";
    str_array[2] = "RT @buddhismgirl: 小筆點: 第三世多杰羌佛說法第十五條「以敗業缺福而生煩惱，即是我執魔。」 https://t.co/fug2lnqZqV #第三世多杰羌佛 #小筆點";
    str_array[3] = "本姑娘不爽，所以決定攤牌!!!!!!! 簡單的交班人選可以拖了2.3個禮拜無法決定是在搞什麼鬼???!!! https://t.co/Qy78jCWmis";
    str_array[4] = "'#CityPlaying 郑源 - 为什么相爱的人不能在一起 Request by SMS City Radio Medan 0819888959 #城市爱经典 with DJ　美妡～ Can Live Streaming at https://t.co/5dti7Z1CVp'";
    str_array[5] = "你看看那个傻逼发的新闻链接。。。应该不是珍的吧？ https://t.co/ReCrfDwRHT";
    str_array[6] = "[weibo] #빅스 #VIXX #TheRemix 160811 Cr 爱豆韩流社区 https://t.co/YcoUyyaUAH https://t.co/LfL58KqFEf";
    str_array[7] = "应有尽有的丰富选择定将为您的旅程增添无数的赏心乐事";
   
    for (String str : str_array) {
      System.out.println(str);
      System.out.println(nlp.analyzeText(str, "zh"));
    }
    
  }
}
