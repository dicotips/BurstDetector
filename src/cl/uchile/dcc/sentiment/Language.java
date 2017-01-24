package cl.uchile.dcc.sentiment;

/**
* This Enum Class represents the Languages Supported in the analysis. 
* ISO 2-Characters Country Code 
*
* @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
* @version     1.0                 
* @since       2016-08-17
*/
public enum Language {
  /** German  Language */       DE("German"),
  /** English Language */       EN("English"), 
  /** Spanish Language */       ES("Spanish"),
  /** Spanish-Chile Language */ ES_CL("Spanish, Chile"), 
  /** Italian Language */       IT("Italian"),
  /** French  Language */       FR("French"),
  /** Portugese Language */     PT("Portuguese"),
  /** Arabic  Language */       AR("Arabic"),
  /** Welsh   Language */       CY("Welsh"),
  /** Greek   Language */       EL("Greek"),
  /** Farsi   Language */       FA("Farsi"),
  /** Japanese Language */      JA("Japanese"),
  /** Polish  Language */       PL("Polish"),
  /** Swedish Language */       SV("Swedish");
  
  /**
   * Literal name of a language
   */
  String language_name;
  
  /**
   * Constructor tha receives the name of a Language.
   * @param lang Language long name
   */
  private Language(String lang){
    this.language_name = lang;
  }  
}
