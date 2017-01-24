package _test;

import cl.uchile.dcc.utils.MapSerializer;
import cl.uchile.dcc.utils.PropertiesTD;
import cl.uchile.dcc.utils.TUtils;
import cl.uchile.dcc.utils.TUtilsListenerDB;

import java.util.Date;

public class TestRecoverFromCrash {
  
  /**
   * MAIN method
   * @param args  No used
   * @throws InterruptedException 
   */
  public static void main(String[] args){
    PropertiesTD prop = new PropertiesTD("/Users/dicotips/Dropbox/"
            + "Research_SourceCode/Twitter_Crawler/_setup.txt");

    Date recovery_date = TUtilsListenerDB.getInitialDate(MapSerializer.loadSerializer(prop), prop);
    System.out.println(TUtils.Date_Formatter(recovery_date));
    
    TUtilsListenerDB.cleanCrashDB(recovery_date, prop);
    
  }

  
}

