package cl.uchile.dcc.ml;

import weka.associations.FPGrowth;
import weka.associations.FPGrowth.AssociationRule;
import weka.associations.FPGrowth.AssociationRule.METRIC_TYPE;
import weka.associations.FPGrowth.BinaryItem;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * This Class implements the use of the FP-Growth Algorithm.
 * Based on SOURCE:   https://github.com/Aluxian/Market-Basket-Recommendation-FP-Growth/blob/master/src/main/java/com/aluxian/mba/Main.java
 * WebSite SOURCE:    https://algorithmia.com/algorithms/Aluxian/AffinityAnalysisFPGrowth
 * Dataset:           https://github.com/stedy/Machine-Learning-with-R-datasets/blob/master/groceries.csv
 * 
 * @author      <a href="mailto:jguzman@dcc.uchile.cl">Jheser Guzman</a>
 * @version     1.0                 
 * @since       2016-08-17
 */
public class FPGrowthGenerator {
  
  /**
   * Applies the FPGrowth into a Set of transactions  algorithm.
   * @param transactions Array of transactions (one string line each).
   * @param delimiter Delimiter characters to split each transaction into items.
   * @return the list of results of the for each item.
   * @throws Exception when the options has an error.
   */
  public List<FPGrowthResult> apply(String[] transactions, String delimiter)
          throws Exception {
    FPGrowth fp = new FPGrowth();
    fp.setOptions("-P 2 -I -1 -N 100 -T 0 -C 0.9 -D 1.0 -U 1.0 -M 0.005".split(" "));
    fp.buildAssociations(extractDataSet(transactions, delimiter));
    return parseRules(fp.getAssociationRules());
  }
  
  /**
   * Applies the FPGrowth into a Set of transactions  algorithm.
   * @param itemsetList set of tokenized transactions.
   * @return the list of results of the for each item.
   * @throws Exception when the apply method throws an error.
   */
  public List<FPGrowthResult> apply(List<Set<String>> itemsetList)
          throws Exception {
    String[] transactions = new String[itemsetList.size()];
    int i = 0;
    for (Set<String> row : itemsetList) {
      transactions[i] = String.join(",", row);
      i++;
    }
    return apply(transactions, ",");
  }
  
  
  /**
   * Applies the FPGrowth into a Set of transactions  algorithm.
   * @param itemsetList List of transactions.
   * @return a Set of Entry(itemset, support).
   */
  public static List<Map.Entry<Set<String>, Double>> frequenItemSets(List<Set<String>> itemsetList){

    List<Map.Entry<Set<String>, Double>> result_filtered = new ArrayList<>();
    List<FPGrowthResult> result_fpgrowth = null;
    
    if(itemsetList.size() == 0)
      return result_filtered;
    
    //Algorithm FPGrowth
    try{
      result_fpgrowth = new FPGrowthGenerator().apply(itemsetList);
    }
    catch(Exception e){
      System.err.println(e.getMessage());
    }
    
    if(result_fpgrowth.size() == 0)
      return result_filtered;

    result_fpgrowth.sort(Collections.reverseOrder());
    
    //Clean Repeated Subsets
    int count = 1;
    HashSet<String> terms = new HashSet<>();
    for (FPGrowthResult result : result_fpgrowth) {
      Set<String> itemset = new HashSet<>();
      itemset.addAll(result.consequence);
      itemset.addAll(result.premise);
      
      if(result_filtered.isEmpty()){
        result_filtered.add(new MyEntry<>(itemset, (1.0*result.premiseSupport/itemsetList.size())));
        terms.addAll(result.premise);
        terms.addAll(result.consequence);
        continue;
      }
      
      boolean flag1 = terms.containsAll(result.premise);
      terms.addAll(result.premise);
      
      boolean flag2 = terms.containsAll(result.consequence);
      terms.addAll(result.consequence);
      
      if(!(flag1 && flag2)){
        if(count < 100){
          result_filtered.add(new MyEntry<>(itemset, (1.0*result.premiseSupport/itemsetList.size())));
          count++;
        }else
          break;
      }
      
    }
    
    return result_filtered;
    
  }

  /**
   * Convert the transactions into Instances object to feed into FP-Growth
   * @param transactions Array of transactions (one string line each).
   * @param delimiter Delimiter characters to split each transaction into items.
   * @return an Instances object containing the tokenized transacionts for Weka.
   * @throws Exception 
   */
  private Instances extractDataSet(String[] transactions, String delimiter)
          throws Exception {
    List<Attribute> attributes = extractAttributes(transactions, delimiter);
    Instances data = new Instances("affinity", convertToFastVector(attributes), transactions.length);
    
    for (String transaction : transactions) {
      List<String> items = Arrays.asList(transaction.split(delimiter));
      Instance instance = new Instance(attributes.size());
      for (Attribute attribute : attributes) {
        instance.setValue(attribute, items.contains(attribute.name()) ? 1 : Double.NaN);
      }
      data.add(instance);
    }
    return data;
  }

  /**
   * Extract all the attributes that appear in the transactions. String line 
   * into items.
   * @param transactions Array of transactions (one string line each).
   * @param delimiter Delimiter characters to split each transaction into items.
   * @return a List with the list of attributes.
   */
  private List<Attribute> extractAttributes(String[] transactions, String delimiter) {
    return Arrays.asList(transactions).stream()
            .map(transaction -> transaction.split(delimiter))
            .map(Arrays::asList)
            .flatMap(List::stream)
            .distinct()
            .map(item -> {
              FastVector vector = new FastVector(1);
              vector.addElement("");
              return new Attribute(item, vector);
            })
            .collect(Collectors.toList());
  }

  /**
   * Convert a list of objects to FastVector.
   * @param items List of objects.
   * @return a FaseVector with the list of Objects.
   */
  private FastVector convertToFastVector(Collection<?> items) {
    FastVector vector = new FastVector(items.size());
    items.forEach(vector::addElement);
    return vector;
  }

  /**
   * Parse the FP-Growth output
   * @param rules Association Rules.
   * @return a List of formated association rules.
   */
  private List<FPGrowthResult> parseRules(List<AssociationRule> rules) {
    return rules.stream().map(rule -> {
      FPGrowthResult result = new FPGrowthResult();

      result.premiseSupport = rule.getPremiseSupport();
      result.consequenceSupport = rule.getConsequenceSupport();

      result.confidence = getMetric(rule, METRIC_TYPE.CONFIDENCE);
      result.lift = getMetric(rule, METRIC_TYPE.LIFT);
      result.leverage = getMetric(rule, METRIC_TYPE.LEVERAGE);
      result.conviction = getMetric(rule, METRIC_TYPE.CONVICTION);

      Function<BinaryItem, String> getName = item -> item.getAttribute().name();

      result.premise = rule.getPremise().stream().map(getName).collect(Collectors.toList());
      result.consequence = rule.getConsequence().stream().map(getName).collect(Collectors.toList());

      return result;
    }).collect(Collectors.toList());
  }

  /**
   * Extract a metric value from an AssociationRule.
   * @param rule the Association Rule.
   * @param metric value from the association rule.
   * @return an array of metrcis of the association rules.
   */
  private double getMetric(AssociationRule rule, METRIC_TYPE metric) {
    try {
      Field metricTypeField = AssociationRule.class.getDeclaredField("m_metricType");
      metricTypeField.setAccessible(true);
      metricTypeField.set(rule, metric);
      return rule.getMetricValue();
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  
  /**
   * This is the main method tests the use of the The FP-Growth Algorithm using
   * text mesages in different languages.
   * 
   * @param  args Nothing
   * @throws Exception when the algorithm has an error.
   */
  public static void main(String[] args) throws Exception {
    String path_dataset = "/Users/dicotips/Desktop/groceries.csv";
    
    Scanner scanner = new Scanner(new File( path_dataset));
    List<String> lines = new ArrayList<>();

    while (scanner.hasNextLine()) {
      lines.add(scanner.nextLine());
    }

    String delimiter = path_dataset.endsWith(".csv") ? "," : " ";
    String[] transactions = lines.toArray(new String[lines.size()]);
    List<FPGrowthResult> results = new FPGrowthGenerator().apply(transactions, delimiter);
    for (FPGrowthResult result : results) {
      //System.out.println(new Gson().toJson(result));
      System.out.println(result.toString());
    }
    //System.out.println(new Gson().toJson(results));

  }  
}