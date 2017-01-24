package _test;

import cl.uchile.dcc.utils.TUtils;
import java.text.ParseException;
import java.util.Date;

public class TestDate {
  private int id = 1;
  
  public static void main(String[] args) throws ParseException{
    Date date1 = new Date();
    Date date2 = new Date(date1.getTime()+1000);
    System.out.println(date1);
    System.out.println(date2);

    Date[] dates = TUtils.getWinBounds(date1, 300, 150);
    System.out.println(TUtils.Date_Formatter2(date1));
    System.out.println(TUtils.Date_Formatter2(dates[0]));
    System.out.println(TUtils.Date_Formatter2(dates[1]));
  }  
}
