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

public class classifier1 {

  Instances trainData;
  Instances testData;
  StringToWordVector filter;
  public MultiClassClassifier classifier1;

  public void evaluateADTree() {
    try 
    {
      trainData.setClassIndex(trainData.numAttributes() - 1);
      testData.setClassIndex(testData.numAttributes() - 1);

      /*
      filter = new StringToWordVector();
      filter.setAttributeIndices("last");
      */
      classifier1 = new MultiClassClassifier();
      classifier1.setClassifier(new ADTree());
      Evaluation eval = new Evaluation(trainData);
      eval.crossValidateModel(classifier1, trainData, 4, new Random(1));
      //System.out.println(eval.toSummaryString());
      //System.out.println(eval.toClassDetailsString());
      //System.out.println("===== Evaluating on filtered (training) dataset done =====");

    } catch (Exception e) {
      System.out.println("Problem found when evaluating");
    }
  }

  /**
   * This method trains the classifier on the loaded dataset.
   */
  public double learnADTree() {
    //Instances train = trainData; 
    //Instances test = testData; 
    //Standardize filter = new Standardize();
    /*
     try {
    
      filter.setInputFormat(train);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } // initializing the filter once with training set
    Instances newTrain = null;
    try {
      newTrain = Filter.useFilter(train, filter);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } // configures the Filter based on train instances and returns filtered instances
    Instances newTest = null;
    try {
      newTest = Filter.useFilter(test, filter);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } // create new test set
    */
    
    // train classifier
    MultiClassClassifier classifier1 = new MultiClassClassifier();
    classifier1.setClassifier(new ADTree());
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
    //System.out.println(eval.toSummaryString("\nResults\n======\n", false)); // Uncomment to see the
                                                                            // classifier
    return eval.errorRate();
  }

}

