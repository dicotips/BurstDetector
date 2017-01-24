package cl.uchile.dcc.text;

import cl.uchile.dcc.utils.PropertiesTD;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

/**
 * This class cleans text, tokenizes it and generate NGrams. It also discards 
 * stopwords.
 * 
 * @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
 * @version     1.0
 * @since       2016-08-17
 */
public class RegexAnalyzer implements TextAnalyzer {
  private static LanguagesAnalizer nlp;
  
  private Set<String> stopwords;
  private StringTokenizer symTokenizer;

  /**
   * Constructor that initializes the instance using the parameters of
   * the _setup.txt file.
   * @param prop Parameters of the _setup.txt file.
   */
  public RegexAnalyzer(PropertiesTD prop) {
    
    if (nlp == null){
      nlp = new LanguagesAnalizer(prop);
    }
    //Carga stopwords (Conjunto)
    stopwords = new HashSet<>();
    File dir = new File(prop.StopWords_Data);

    String[] children = dir.list();
    if (children == null) {
      // Either dir does not exist or is not a directory
    } 
    else {
      for (int i = 0; i < children.length; i++) {
        // Get filename of file or directory
        if (children[i].endsWith(".txt")) {
          BufferedReader br = null;
          try {
            String line = null;
            br = new BufferedReader(new FileReader(new File(prop.StopWords_Data +"/"+ children[i])));
            while ((line = br.readLine()) != null) {
              stopwords.add(line.trim().toLowerCase());
            }
            br.close();
          } 
          catch (FileNotFoundException ex) {
            ex.printStackTrace();
          } 
          catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      }
    }
  }

  /**
   * Method that cleans the text replacing accents and other add-symbols. It
   * tokenizes the given text using the specified language.
   * @param text  Text to process and tokenize.
   * @param lang  Language of the text.
   * @return Map with tokens and their frequencies.
   */
  @Override
  public Map<String, Integer> analyzeText(String text, String lang) {
    //Extrae URLS
    text = removeUrls(text);

    text = text.toLowerCase();
    text = text.replace("[video]","");
    text = text.replace('á', 'a');
    text = text.replace('é', 'e');
    text = text.replace('í', 'i');
    text = text.replace('ó', 'o');
    text = text.replace('ú', 'u');
    text = text.replace('ä', 'a');
    text = text.replace('ë', 'e');
    text = text.replace('ï', 'i');
    text = text.replace('ö', 'o');
    text = text.replace('ü', 'u');
    text = text.replace('à', 'a');
    text = text.replace('è', 'e');
    text = text.replace('ì', 'i');
    text = text.replace('ò', 'o');
    text = text.replace('ù', 'u');
    text = text.replace('â', 'a');
    text = text.replace('ê', 'e');
    text = text.replace('î', 'i');
    text = text.replace('ô', 'o');
    text = text.replace('û', 'u');
    text = text.replace('ã', 'a');
    text = text.replace('å', 'a');
    text = text.replace('õ', 'o');
    text = text.replace('ç', 'c');
    text = text.replace('ý', 'y');
    text = text.replace('ỳ', 'y');
    text = text.replace('ŷ', 'y');
    text = text.replace('ÿ', 'y');
    text = text.replace("æ", "ae");
    text = text.replace("ñ", "n");
    //text = text.replaceAll("[^A-Za-z0-9#@_-]", " ");

    Map<String, Integer> terms = new HashMap<>();
    
    if(nlp.containsLanguage(lang)){
      Map<String, Integer>  nlp_tokens = nlp.analyzeText(text, lang);
      
      for (String nlp_token : nlp_tokens.keySet()) {
        String token = nlp_token.replaceAll("[［］『』〈〉《》〔〕〖〗（）【】，：；？！　 \\ªº!|\"·$%&¬/()=?¡¿\\[^+*\\]{},;.:-_“~‘°”\t\n<>\r'。、「」,﹁﹂…]", "").trim();
        
        if (token.length() > 1 && !token.matches("\\s+") && !stopwords.contains(token) && !isNumeric(token)) {
          if (terms.containsKey(token)) {
            terms.put(token, terms.get(token) + 1);
          } else {
            terms.put(token, 1);
          }
        }
      }
    }
    else{
      //Tokenizazinyt JA language
      if(lang == "ja"){
        Tokenizer tokenizer = new Tokenizer() ;
        List<Token> tokens = tokenizer.tokenize(text);
        String aux = "";
        for (Token token : tokens) {
          aux += " " + token.getSurface();
        }
        text = aux.trim();
      }
      
      //Tokenizacion process
      symTokenizer = new StringTokenizer(text, "［］『』〈〉《》〔〕〖〗（）【】，：；？！　 \\ªº!|\"·$%&¬/()=?¡¿[^+*]{},;.:-_“~‘°”\t\n<>\r'。、「」,﹁﹂…");

      while (symTokenizer.hasMoreTokens()) {
        String token = symTokenizer.nextToken();
        token = removeDigits(token);

        if (token.length() > 1 && !token.matches("\\s+") && !stopwords.contains(token) && !isNumeric(token)) {
          if (terms.containsKey(token)) {
            terms.put(token, terms.get(token) + 1);
          } else {
            terms.put(token, 1);
          }
        }
      }
    }
    return terms;
  }
  
  /**
   * Verifies if the string is a number.
   * @param str text to analyze.
   * @return TRUE=Is number; FALSE= Is not a number.
   */
  private boolean isNumeric(String str)
  {  
    try  
    {  
      double d = Double.parseDouble(str);  
    }  
    catch(NumberFormatException nfe)  
    {  
      return false;  
    }  
    return true;  
  }
  
  /**
   * Remove digits from the given text.
   * @param token Text to process removing digits.
   * @return Text without digits.
   */
  private String removeDigits(String token) {
    StringBuilder filteredString = new StringBuilder();

    for (int i = 0; i < token.length(); i++) {
      char c = token.charAt(i);
      if (Character.isLetter(c) || Character.isDigit(c) || c=='#' || c=='@') {
        filteredString.append(token.substring(i));
        break;
      }
    }

    return filteredString.toString();
  } 
  
  /**
   * Remove URLS from the given text.
   * @param  text Text to process removing URLs.
   * @return Text without URLS.
   */
  private String removeUrls(String text) {
    Pattern pattern = Pattern.compile("(https?|ftp|gopher|telnet|file|Unsure|http)://[\\S]*", Pattern.CASE_INSENSITIVE);
    String newtext = pattern.matcher(text).replaceAll("");
    return newtext;
  }
  
  /**
   * NGrams generator from 1 to n_gram number. It generates NGrams per character.
   * @param n_gram  Up to ngrams size.
   * @param text    Text to generate NGrams per characters.
   * @return List of Ngrams.
   */
  private List<String> nGram( int n_gram, String text){
    List<String> grams = new ArrayList<>();
    
    for (int i = 0; i < text.length()-n_gram+1; i++) {
      String token = text.substring(i, i+n_gram).trim();
      if(token.length() != n_gram)
        continue;
      
      grams.add(token);
    }
    return grams;
  }
  
  /**
   * NGrams generator per tokens.
   * @param text Text to analyze.
   * @return List of Ngrams based on tokens, and their frequencies.
   */
  @Override
  public Map<String, Integer> analyzeText_NGram(String text) {
    //Extrae URLS
    text = removeUrls(text);

    text = text.toLowerCase();
    text = text.replace("[video]","");
    text = text.replace('á', 'a');
    text = text.replace('é', 'e');
    text = text.replace('í', 'i');
    text = text.replace('ó', 'o');
    text = text.replace('ú', 'u');
    text = text.replace('ä', 'a');
    text = text.replace('ë', 'e');
    text = text.replace('ï', 'i');
    text = text.replace('ö', 'o');
    text = text.replace('ü', 'u');
    text = text.replace('à', 'a');
    text = text.replace('è', 'e');
    text = text.replace('ì', 'i');
    text = text.replace('ò', 'o');
    text = text.replace('ù', 'u');
    text = text.replace('â', 'a');
    text = text.replace('ê', 'e');
    text = text.replace('î', 'i');
    text = text.replace('ô', 'o');
    text = text.replace('û', 'u');
    text = text.replace('ã', 'a');
    text = text.replace('å', 'a');
    text = text.replace('õ', 'o');
    //text = text.replace('ç', 'c');
    text = text.replace('ý', 'y');
    text = text.replace('ỳ', 'y');
    text = text.replace('ŷ', 'y');
    text = text.replace('ÿ', 'y');
    //text = text.replace("æ", "ae");
    //text = text.replace("ñ", "n");
    //text = text.replaceAll("[^A-Za-z0-9#@_-]", " ");
    text = text.replaceAll("\\t\\n<>\\r", " ");

    Map<String, Integer> terms = new HashMap<>();
    
    for (String token : nGram(1, text)) {
      if (terms.containsKey(token)) {
        terms.put(token, terms.get(token) + 1);
      } else {
        terms.put(token, 1);
      }
    }
    
    for (String token : nGram(2, text)) {
      if (terms.containsKey(token)) {
        terms.put(token, terms.get(token) + 1);
      } else {
        terms.put(token, 1);
      }
    }
    
    return terms;
  }

  /**
   * PowerSet Generator per tokens
   * @param originalSet List of tokens.
   * @return List of combinations of tokens grouped from 1 to ...
   */
  @Override
  public Set<Set<String>> powerSet(Set<String> originalSet) {
    Set<Set<String>> sets = new HashSet<>();
    if (originalSet.isEmpty()) {
    	sets.add(new HashSet<String>());
    	return sets;
    }
    List<String> list = new ArrayList<String>(originalSet);
    String head = list.get(0);
    Set<String> rest = new HashSet<String>(list.subList(1, list.size())); 
    for (Set<String> set : powerSet(rest)) {
    	Set<String> newSet = new HashSet<String>();
    	newSet.add(head);
    	newSet.addAll(set);
    	sets.add(newSet);
    	sets.add(set);
    }		
    return sets;
  }
  
  /**
   * This is the main method tests the use of the RegexAnalyzer for 
   * tokenizing text in diferent languages and their NGrams.
   * 
   * @param  args Nothing
   */
  public static void main(String args[]) {
    PropertiesTD PropTD = new PropertiesTD("/Users/dicotips/Dropbox/Research_SourceCode/Twitter_Crawler/_setup.txt");
    TextAnalyzer analyzer = new RegexAnalyzer(PropTD);

    String str1  = "[Video] Llamarada solar amenaza @Jheser #filio con interrumpir comunicaciones satelitales http://t.co/SUaxNBB";
    String str2  = "Revisa la @Sección #Opinión 49ers revisa: Perdona-vidas...a propósito de los dichos de mi amigo Enrique Correa, por Luis Alvarado";
    String str3  = ":)https://ow.ly/i/d0JK. ..http://ow.ly/i/d0JK http";
    String str4  = "Ganti nick aja deh ehehehe ƪ(˘⌣˘ )┐ƪ(˘⌣˘)ʃ┌( ˘⌣˘)ʃ";
    String str5  = "Meeeeeee ! RT @missLOVElace_: who wants my 80,000 tweet ?";
    String str6  = "@croqui9 おはようございます！";    
    String str7  = "@5HVoteStats HARMOS GO CRAZY 2016 #harmonizers #iheartawards #BestFanArmy";
    String str8  = "应有尽有的丰富选择定将为您的旅程增添无数的赏心乐事";
    String str9  = "本姑娘不爽，［aloha］aloha『aloha』〈aloha〉《aloha》〔aloha〕〖aloha〗（aloha）【aloha】所以決定攤牌!!!!!!! 簡單的交班人選可以拖了2.3個禮拜無法決定是在搞什麼鬼???!!! https://t.co/Qy78jCWmis";
    String str10 = "お寿司が食べたい。";
    
    List<String> str = new ArrayList<>();
    str.add("rt @yien093x: ⌁⁞ ╻ᐟ｡ 🕸ᐢ˖恩-段宜, 93° 🔭˟╻¯⌇ °˖ͯ♡̷˖04.09%📽⸗❮rapper🌪❯ ․ ╷󾟗·‛@˟jypcuteboys ⑈❪󾠀❫˖⌁╷");
    str.add("⌁⁞ ╻ᐟ｡ 🕸ᐢ˖恩-段宜, 93° 🔭˟╻¯⌇ °˖ͯ♡̷˖04.09%📽⸗❮rapper🌪❯ ․ ╷󾟗·‛@˟jypcuteboys ⑈❪󾠀❫˖⌁╷");
    str.add("吊帶短裙↬ 󾓕󾓑󾓕󾓑󾓕󾓑󾓕󾓑󾓕󾓑󾓕󾓑󾓕 ↱↱ 怎麼穿怎麼q ↰↰ 歡迎美美的妳們和我們分享妳的 ☾☾funkylook穿搭☽☽ 穿搭分享： 張芳瑜");
    str.add("󾔗󾔗免運免費索取體驗包󾔗󾔗 󾭹󾭹󾭹索取方式說明󾭹󾭹󾭹 ✅直接點選加入footpure鞋蜜粉官方line>> ✅請於line留言告知寄送以下資料：...");
    str.add("#集邮# 近日收到：1、2、6、襄阳邮友杨女士在南漳县长坪镇寄来的“相思鸟”原地封&极限片以及“奥运”首日封；3、随州邮友周先生在襄阳“相思鸟”原地封；4、5、襄阳邮友刘先生在“相思邮局”寄来的“相思鸟”首日封；7、昆山邮友施先生寄来的“奥运”首日封。～一并致谢󾌸");
    str.add("󾓶【latoja身体乳】󾓶一次决定成败❗❗❗82.7cm到79.8cm󾭚");
    str.add("160815 高英培 instagram 更新 喔嗚呀~~~#都知道󾍕 花籃：宇宙的中心 soran mbc 藍夜dj鐘鉉*製作 cr hello_金钟铉中文网");
    str.add("肝臟健康真的好重要。雖說它是沈默的器官，但還是有跡可循。像是口臭、黃疸、覺得累...等。再忙還是要好好照顧小心肝 󾌸󾌸");
    
    //System.out.println(str7 +"\n"+ analyzer.analyzeText(str7, "en"));
    //System.out.println(str8 +"\n"+ analyzer.analyzeText(str8, "zh"));
    //System.out.println(str9  +"\n"+ analyzer.analyzeText(str9 , "zh"));
    System.out.println(str10 +"\n"+ analyzer.analyzeText(str10, "ja"));
    
    //Set<Set<String>> pw_ngrams = analyzer.powerSet(new HashSet<>(analyzer.analyzeText(str7,"en").keySet()));
    //System.out.println(pw_ngrams.size());
    /*
    System.out.println("\nNGRAMS\n");
    for (Set<String> pw_ngram : pw_ngrams) {
      System.out.println(pw_ngram);
    }
    */
    
  }
  
}
