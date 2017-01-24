package cl.uchile.dcc.content;

import cl.uchile.dcc.text.RegexAnalyzer;
import cl.uchile.dcc.text.TextAnalyzer;
import cl.uchile.dcc.utils.PropertiesTD;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
* This Class detects countries from a given text in any language.
*
* Source of list of languages:
*   http://linguistics.stackexchange.com/questions/6131/is-there-a-long-list-of-languages-whose-writing-systems-dont-use-spaces
* 
* Language list: Korean (ko), Lao, Burmese (Myanmar), Vietnamese, Tibetan, 
*                Dzongkha, Chinese (zh), Japanese (ja), Thai (), Khmer, 
*                Hidi (hi), tamil  (ta), arabe  (ar)
* 
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0
* @since       2016-08-17
*/
public class CountryDetector {
  
  /**
   * TextAnalyser that holds the class to tokenize text into tokens.
   */
  private TextAnalyzer analyzer;
  
  /**
   * List of coutries in diferent languages. <Source language, English Language>.
   */
  private Map<String, Map<String, String> > map_lang;
  
  /**
   * List of country codes that do not use traditional separation marks.
   */
  public static Set<String> lang_no_spaces = 
          new HashSet<>(Arrays.asList("ja","ta","hi","zh","ko"));
  
  /**
   * Constructor of the class that use the _setup.txt parameters.
   * 
   * @param _prop Parameters from _setup.txt file.
   */
  public CountryDetector(PropertiesTD _prop){
    map_lang = CountriesLoader(_prop);
    analyzer = new RegexAnalyzer(_prop); 
  }
  
  /**
   * Constructor of the class that use the _setup.txt parameters.
   * 
   * @param text Text to be cleaned.
   * @return  Return a LowerCase text with removed accents, ñ and [video].
   */
  private String cleanText(String text){
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
    return text;
  }
  
  /**
   * Load the list of Countries in many languages from file.
   * 
   * @param prop Properties parameters with the path of the list of contries in many languages.
   * @return  List of countries mapped into diferent languages.
   */
  private Map<String, Map<String, String>> CountriesLoader(PropertiesTD prop){
    Map<String, Map<String, String> > map_lang = new HashMap<>();

    String path = prop.country_detect_path;
    File file = new File(path);

    try {
      Scanner sc = new Scanner(file);
      while (sc.hasNextLine()) {
        String[] i = sc.nextLine().split("\t");

        String country_in  = i[2].toLowerCase().replace("-"," ");
        String country_out = i[1];
        String lang = i[3];

        Map<String, String> map_country;
        if(!map_lang.containsKey(lang))
          map_country = new HashMap<>();
        else
          map_country = map_lang.get(lang);

        map_country.put(country_in, country_out);
        map_country.put(cleanText(country_in), country_out);
        map_country.put(i[2], country_out);
        map_country.put(cleanText(i[2]), country_out);
        
        map_lang.put(lang, map_country);

      }
      sc.close();
    } 
    catch (FileNotFoundException e) {
        e.printStackTrace();
    }
    return map_lang;
  }
  
  /**
   * Detet countries into a text.
   * 
   * @param text Text to be analyzed and see if it has mentioned countries inside.
   * @param lang Language of the text.
   * @param flag_all_text TRUE=Detect in tokens, FALSE=Detect in a Substring.
   * @return a Set of countries.
   */
  public Set<String> DetectCountries(String text, String lang, boolean flag_all_text){
    Set<String> resultado = new HashSet<>();
    Set<String> set_text = analyzer.analyzeText(text, lang).keySet();
    
    if(!map_lang.containsKey(lang))
      return resultado;
    
    if(!flag_all_text){
      if(lang_no_spaces.contains(lang)){
        Map<String, String> map_country = map_lang.get(lang);
        for (String country : map_country.keySet()) {
          if(text.contains(country)){
            resultado.add(map_country.get(country));
          }
        }
        
      }
      else{
        Map<String, String> map_country = map_lang.get(lang);
        for (String country : map_country.keySet()) {
          if(set_text.contains(country) || set_text.contains("#"+country)){
            resultado.add(map_country.get(country));
          }
        }

      }
    }
    else{
      for (String language : map_lang.keySet()) {
        for (String country : map_lang.get(language).keySet()) {
          if(set_text.contains(country) || set_text.contains("#"+country)){
            resultado.add(map_lang.get(language).get(country));
          }
        }
      }
      if(text.contains("U.S.A.") || text.contains("USA") || text.contains("United States")){
        resultado.add("USA");
      }
    }
    
    return resultado;
  }
  
  /**
   * This is the main method which tests the detection of languages into texts
   * in many languages. The PropertiesTD must contain the path of the _setup.txt
   * file with the setup parameters.
   * 
   * @param args Path of the _setup.txt file.
   */
  public static void main(String[] args){
    PropertiesTD prop = new PropertiesTD("/Users/dicotips/Dropbox/Research_SourceCode/Twitter_Crawler/_setup.txt");
    CountryDetector ctry = new CountryDetector(prop);
    
    String text  = "Sismo de magnitud torres del paine 中央市 4.3 registrado en Bolivia, Colombia, Ecuador fue sentido en territorio venezolano https://t.co/fX36MW5CYl https://t.co/DWarldGDHK";
    String text1 = "RT @earthquakejapan: futaleufu USA 日本地震リスク - 5月2日と5月3日 - https://t.co/MSA4iP6XRL";
    System.out.println(text);
    System.out.println(text1);
    
    System.out.println(ctry.DetectCountries(text, "en", false));
    System.out.println(ctry.DetectCountries(text1, "ja", false));
    
    System.out.println(ctry.DetectCountries(text, "en", true));
    System.out.println(ctry.DetectCountries(text1, "ja", true));
    
  }  
}
