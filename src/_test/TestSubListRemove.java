/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package _test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dicotips
 */
public class TestSubListRemove {

  public static void main(String[] args){
    List<Integer> list = new ArrayList<>();
    list.add(0);
    list.add(1);
    list.add(2);
    list.add(3);
    list.add(4);
    list.add(5);
    list.add(6);
    list.add(7);
    list.add(4);
    list.add(5);
    list.add(6);
    list.add(7);
    System.out.println(list);
    list.subList(4, list.size()).clear();
    System.out.println(list);
    
  }
}
