package f13_10601.project_dracarus;

import java.util.List;

public class App {

  public static void main(String[] args) {
    classifier1 cls1 = new classifier1();
    classifier2 cls2 = new classifier2();
    classifier3 cls3 = new classifier3();
    
    List<Double> errorList_cls1 = cls1.main(args);
    List<Double> errorList_cls2 = cls2.main(args);
    List<Double> errorList_cls3 = cls3.main(args);
    
    
  }
}