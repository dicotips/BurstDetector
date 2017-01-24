package _test;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author dicotips
 */
public class TestFormats {

  public static void main(String[] args) throws InterruptedException{
    short i1 = 10;
    byte  i2 = 10;
    int   i3 = 10;
    long  i4 = 1031423123;
    System.out.println("\n");
    System.out.printf("%d %d %d %d%n", i1, i2, i3, i4);
    
    System.out.println("\n");
    float  f1 = (float) Math.PI;
    double f2 = Math.PI;
    System.out.printf("%1.20f %1.20f%n", f1, f2);
    System.out.printf("%08f %f%n", f1, f2);
    System.out.println(f1);
    System.out.println(f2);
    
    //////////////////////////////////////////////////////////////////////
    System.out.println("\n");
    Calendar c = Calendar.getInstance();
    System.out.format("%tB %td%n", c, c);
    System.out.format("%tB %te, %tY%n", c, c, c); // -->  "May 29, 2006"
    System.out.format("%2tl:%tM %tp%n", c, c, c);  // -->  "2:34 am"
    System.out.format("%tD%n", c);    // -->  "05/29/06"
    
    
    Date d  = new Date();
    System.out.format("%tB %td%n", d, d);
    System.out.format("%tB %te, %tY%n", d, d, d); // -->  "May 29, 2006"
    System.out.format("%2tl:%tM %tp%n", d, d, d);  // -->  "2:34 am"
    System.out.format("%tD%n", d);    // -->  "05/29/06"
    
    
    //////////////////////////////////////////////////////////////////////
    for (int i = 0; i < 100; i++) {
      Thread.sleep(1000);
      System.out.print("\r"+ i);
    }
    
    
  }
}
