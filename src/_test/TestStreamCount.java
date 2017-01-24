package _test;

import java.util.Arrays;

/**
 *
 * @author dicotips
 */
public class TestStreamCount {

  public static void main(String[] args){
    int[] arr = {1,0,0,1,2,3,1,2,2};
    int count = (int) Arrays.stream(arr).filter(x -> x == 1).count();
    
    System.out.println(count+"\t"+Arrays.toString(arr));
  }
}
