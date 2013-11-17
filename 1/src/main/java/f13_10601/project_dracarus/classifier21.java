package f13_10601.project_dracarus;

/**
 * Hello world!
 *
 */
import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.Evaluation;

import java.util.Random;

import weka.classifiers.meta.MultiClassClassifier;
import weka.classifiers.trees.J48graft;

public class classifier21 {

  Instances trainData;

  Instances testData;

  StringToWordVector filter;

  public MultiClassClassifier classifier2;

  public void evaluateJ48graft() {
    try {
      trainData.setClassIndex(trainData.numAttributes() - 1);
      testData.setClassIndex(testData.numAttributes() - 1);

      filter = new StringToWordVector();
      filter.setAttributeIndices("last");

      J48graft myClassifier = new J48graft();
      myClassifier.setUnpruned(false);
      myClassifier.setConfidenceFactor((float) 0.45);
      myClassifier.setMinNumObj(2);
      myClassifier.setBinarySplits(false);
      // myClassifier.setSubtreeRaising(false);
      myClassifier.setUseLaplace(true);
      myClassifier.setRelabel(true);

      classifier2 = new MultiClassClassifier();
      classifier2.setClassifier(myClassifier);

      Evaluation eval = new Evaluation(trainData);
      eval.crossValidateModel(classifier2, trainData, 4, new Random(1));
      // System.out.println(eval.toSummaryString());
      // System.out.println(eval.toClassDetailsString());
      // System.out.println("===== Evaluating on filtered (training) dataset done =====");

    } catch (Exception e) {
      System.out.println("Problem found when evaluating");
      e.printStackTrace();
    }
  }

  /**
   * This method trains the classifier on the loaded dataset.
   * 
   * @throws Exception
   */
  public double learnJ48graft() throws Exception {

    J48graft myClassifier = new J48graft();
    myClassifier.setUnpruned(false);
    myClassifier.setConfidenceFactor((float) 0.45);
    myClassifier.setMinNumObj(2);
    myClassifier.setBinarySplits(false);
    // myClassifier.setSubtreeRaising(false);
    myClassifier.setUseLaplace(true);
    myClassifier.setRelabel(true);

    classifier2 = new MultiClassClassifier();
    classifier2.setClassifier(myClassifier);

    try {
      classifier2.buildClassifier(trainData);
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
      eval.evaluateModel(classifier2, testData);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // System.out.println(eval.toSummaryString("\nResults\n======\n", false));
    return eval.errorRate();
  }

}
