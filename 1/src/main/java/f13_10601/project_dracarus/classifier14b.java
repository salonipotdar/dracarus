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
import weka.classifiers.trees.ADTree;

public class classifier14b {

  Instances trainData;

  Instances testData;

  StringToWordVector filter;

  public MultiClassClassifier classifier1;

  public void evaluateADTree() {
    try {
      trainData.setClassIndex(trainData.numAttributes() - 1);
      testData.setClassIndex(testData.numAttributes() - 1);

      ADTree myClassifier = new ADTree();
      myClassifier.setNumOfBoostingIterations(50);

      classifier1 = new MultiClassClassifier();
      classifier1.setClassifier(myClassifier);

      Evaluation eval = new Evaluation(trainData);
      eval.crossValidateModel(classifier1, trainData, 4, new Random(1));
      // System.out.println(eval.toSummaryString());
      // System.out.println(eval.toClassDetailsString());
      // System.out.println("===== Evaluating on filtered (training) dataset done =====");

    } catch (Exception e) {
      System.out.println("Problem found when evaluating");
    }
  }

  /**
   * This method trains the classifier on the loaded dataset.
   */
  public double learnADTree() {

    // train classifier
    ADTree myClassifier = new ADTree();
    myClassifier.setNumOfBoostingIterations(50);

    classifier1 = new MultiClassClassifier();
    classifier1.setClassifier(myClassifier);

    try {
      classifier1.buildClassifier(trainData);
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
      eval.evaluateModel(classifier1, testData);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // System.out.println(eval.toSummaryString("\nResults\n======\n", false));
    return eval.errorRate();
  }

}
