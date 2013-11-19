package f13_10601.project_dracarus;

import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.Evaluation;

import weka.classifiers.meta.Decorate;
import weka.classifiers.meta.MultiClassClassifier;

public class classifier33_0 {

  Instances trainData;

  Instances testData;

  StringToWordVector filter;

  public MultiClassClassifier classifier3;

  public void evaluateDecorate() {
    try {
      // System.out.println("***********************"+trainData.numAttributes());
      // System.out.println("#######################"+testData.numAttributes());
      trainData.setClassIndex(trainData.numAttributes() - 1);
      testData.setClassIndex(testData.numAttributes() - 1);

      classifier3 = new MultiClassClassifier();
      classifier3.setClassifier(new Decorate());

      // System.out.println(eval.toClassDetailsString());
      // System.out.println("===== Evaluating on filtered (training) dataset done =====");

    } catch (Exception e) {
      System.out.println("Problem found when evaluating");
    }
  }

  public double learnDecorate() throws Exception {

    // train classifier
    classifier3.setClassifier(new Decorate());
    try {
      classifier3.buildClassifier(trainData);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // evaluate classifier and print some statistics
    Evaluation eval = null;
    try {
      eval = new Evaluation(trainData);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try {
      eval.evaluateModel(classifier3, testData);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // System.out.println(eval.toSummaryString("\nResults\n======\n", false));
    return eval.errorRate();

  }

}
