package cl.uchile.dcc.text;

import java.util.Map;
import java.util.Set;

/**
 * Interface that extracts tokens from a given text corpus. It removes non-relevant
 * symbols and it removes stopwords.
 * 
 * @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
 * @version     1.0
 * @since       2016-08-17
 */
public interface TextAnalyzer {

	/**
	 * Generate the Tokenization of a given text. It also computes the frequency
   * of each token.
   * 
	 * @param text The text corpus to tokenize.
   * @param lang The text language.
	 * @return List of terms with their frequencies.
	 */
	public Map<String, Integer> analyzeText(String text, String lang);

  /**
   * Generates a list of n-grams with their countings from a given text.
   * @param text String source to generate n-grams.
   * @return N-Grams with their frequencies.
   */
  public Map<String, Integer> analyzeText_NGram(String text);
  
  /**
   * Generates the PowerSets of list of keywords.
   * 
   * @param originalSet String list to generate PowerSets.
   * @return Set of PowerSets of given keywords.
   */
  public Set<Set<String>> powerSet(Set<String> originalSet);
  
}
