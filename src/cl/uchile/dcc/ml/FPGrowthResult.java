package cl.uchile.dcc.ml;

import java.util.List;

/**
   * A class that contains the result information returned by the FPGrowth 
   * algorithm.
   */
public class FPGrowthResult implements Comparable<FPGrowthResult> {

  /**
   * confidence: the proportion of the examples covered by the premise that 
   * are also covered by the consequence. Alternatively, this can be described 
   * as the probability that a rule is correct for a new transaction.
   */
  public double confidence;

  /**
   * lift: confidence divided by the proportion of all examples that are 
   * covered by the consequence. This is a measure of the importance of the 
   * association that is independent of support.
   */
  public double lift;

  /**
   * leverage: the proportion of additional examples covered by both the 
   * premise and consequence above those expected if the premise and 
   * consequence were independent of each other.
   */
  public double leverage;

  /**
   * conviction: another measure of departure from independence.
   */
  public double conviction;

  /**
   * premise: the 'premise' part of the rule.
   */
  public List<String> premise;

  /**
   * consequence: the 'consequence' part of the rule.
   */
  public List<String> consequence;

  /**
   * premiseSupport: the number of transactions (sessions) in the data set 
   * which contain the premise items.
   */
  public int premiseSupport;

  /**
   * consequenceSupport the number of transactions (sessions) in the data 
   * set which contain the consequence items.
   */
  public int consequenceSupport;

  /**
   * Returs the representation of and instance of FPGrowthResult.
   * @return the representation of and instance of FPGrowthResult. 
   */
  @Override
  public String toString(){
    String str =  "confidence: "+           String.format("%.2f", confidence)
                  +"\tlift: "+              String.format("%.2f", lift)
                  +"\tleverage: "+          String.format("%.2f", leverage)
                  +"\tconviction: "+        String.format("%.2f", conviction)
                  +"\tpremiseSupport: "+    premiseSupport
                  +"\tconsequenceSupport: "+  consequenceSupport
                  +"\tpremise: "+           premise.toString()
                  +"\tconsequence: "+       consequence.toString();

    return str;
  }

  /**
   * Implemets comparable methods.
   * @param o Object to compare to.
   * @return a comparition value.
   */
  @Override
  public int compareTo(FPGrowthResult o) {
    return Integer.compare(this.premiseSupport, o.premiseSupport);
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}