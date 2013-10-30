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


public class classifier2 {
	  
	Instances trainData;
	Instances testData;
	StringToWordVector filter;
	  
    public MultiClassClassifier classifier2;

	public void evaluateJ48graft()
    {
    try {
      trainData.setClassIndex(trainData.numAttributes() - 1);
      testData.setClassIndex(testData.numAttributes() - 1);

      filter = new StringToWordVector();
      filter.setAttributeIndices("last");
      
      
      classifier2 = new MultiClassClassifier();
      classifier2.setClassifier(new J48graft());
      Evaluation eval = new Evaluation(trainData);
      eval.crossValidateModel(classifier2, trainData, 4, new Random(1));
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
  public double learnJ48graft() {
    //Instances train = trainData; // from somewhere
    //Instances test = testData; // from somewhere
    //Standardize filter = new Standardize();
    
    classifier2.setClassifier(new J48graft());
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
    //System.out.println(eval.toSummaryString("\nResults\n======\n", false)); // Uncomment to see the
                                                                            // classifier
    return eval.errorRate();
  }

}
