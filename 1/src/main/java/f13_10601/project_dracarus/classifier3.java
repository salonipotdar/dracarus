package f13_10601.project_dracarus;

import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.Evaluation;

import weka.classifiers.meta.Decorate;
import weka.classifiers.meta.MultiClassClassifier;


public class classifier3 {

  Instances trainData;
  Instances testData;
  StringToWordVector filter;

  
  public MultiClassClassifier classifier3;

    public void evaluateDecorate() {
    	try 
    	{
    		//System.out.println("***********************"+trainData.numAttributes());
    		//System.out.println("#######################"+testData.numAttributes());
        	trainData.setClassIndex(trainData.numAttributes()-1);
            testData.setClassIndex(testData.numAttributes()-1);
            //System.out.println("1hhh");

            /*try1 to get rid of the atrribute
            Remove rm = new Remove();
            filter.setOptions( weka.core.Utils.splitOptions("-R last"));
            filter.setInputFormat(trainData);   
            trainData = Filter.useFilter(trainData, filter);
            testData = Filter.useFilter(testData, filter);
            rm.setAttributeIndices("f");  // remove 1st attribute
            */
            
            
            classifier3 = new MultiClassClassifier();
            //System.out.println("3hhh");
            //classifier3.setFilter(filter);
            //System.out.println("4hhh");
            classifier3.setClassifier(new Decorate());
            //System.out.println("5hhh");
            //Evaluation eval = new Evaluation(trainData);
            //System.out.println("6hhh");
            //System.out.println("7hhh");
            //System.out.println("8hhh");
            //System.out.println(eval.toClassDetailsString());
            //System.out.println("9hhh");
            //System.out.println("===== Evaluating on filtered (training) dataset done =====");

        } 
    	catch (Exception e) 
    	{
        	System.out.println("Problem found when evaluating");
        }
  }

  public double learnDecorate() throws Exception 
  {
    //Instances train = trainData; // from somewhere
    //Instances test = testData; // from somewhere
    
    /*try 2 to get rid of the attribute
    Standardize filter = new Standardize();
    StringToWordVector filter1 = new StringToWordVector();
    	filter.setOptions( weka.core.Utils.splitOptions("SubsetByExpression -E \"(ATT26< 2)\""));
	
    try {
		filter1.setInputFormat(trainData);
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}   
    try {
		trainData = Filter.useFilter(trainData, filter1);
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
    try {
		testData = Filter.useFilter(testData, filter1);
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

    try {
      filter.setInputFormat(train);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } // initializing the filter once with training set
    */
    
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
    //System.out.println(eval.toSummaryString("\nResults\n======\n", false)); // Uncomment to see the
     return eval.errorRate();                                                                        // classifier

  }
  
}
