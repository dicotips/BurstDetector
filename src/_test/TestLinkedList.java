/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package _test;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author dicotips
 */
public class TestLinkedList {
  public static void main(String[] args){
    //List<Integer> list1 = new LinkedList<>();
    List<Integer> list2 = new ArrayList<>();
    int n = 1_000_000;
    
    
    //long d_1 = new Date().getTime();
    //for (int i = 0; i < n; i++) {
      //list1.add(i);
    //}
    long  d_2 = new Date().getTime();
    for (int i = 0; i < n; i++) {
      list2.add(0, i);
    }
    long  d_3 = new Date().getTime();
    
    //System.out.println((d_2-d_1)/1000);
    System.out.println((d_3-d_2)/1000);
  }
}
