package f13_10601.project_dracarus;

import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

import weka.classifiers.bayes.NaiveBayes;


public class NBClassifier {
	  
	Instances trainData;
	Instances testData;
	StringToWordVector filter;
	  
    public Classifier classifier2;

	public void evaluateNB()
    {
    try {
      trainData.setClassIndex(trainData.numAttributes() - 1);
      testData.setClassIndex(testData.numAttributes() - 1);

      
      classifier2 = new NaiveBayes();
      //classifier2.setClassifier(new NaiveBayes());
      //System.out.println("===== Evaluating on filtered (training) dataset done =====");

    } catch (Exception e) {
      System.out.println("Problem found when evaluating");
    }
  }

  /**
   * This method trains the classifier on the loaded dataset.
   */
  public double learnNB() {
    //Instances train = trainData; 
    //Instances test = testData; 
    
    // train classifier
    Classifier classifier2 = new NaiveBayes();
    //MultiClassClassifier classifier2 = new MultiClassClassifier();
    //classifier2.setClassifier(new NaiveBayes());
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
