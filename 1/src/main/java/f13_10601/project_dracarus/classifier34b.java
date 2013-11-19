package f13_10601.project_dracarus;

import weka.core.Instances;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.meta.MultiClassClassifier;
import weka.classifiers.meta.Stacking;
import weka.classifiers.trees.ADTree;
import weka.classifiers.trees.FT;
import weka.classifiers.trees.J48graft;
import weka.classifiers.trees.RandomForest;

public class classifier34b {

  Instances trainData;

  Instances testData;

  public MultiClassClassifier classifier3;

  public void evaluateDecorate() {
    try {
      // System.out.println("***********************"+trainData.numAttributes());
      // System.out.println("#######################"+testData.numAttributes());
      trainData.setClassIndex(trainData.numAttributes() - 1);
      testData.setClassIndex(testData.numAttributes() - 1);

      ADTree ATClassifier = new ADTree();
      ATClassifier.setNumOfBoostingIterations(50);

      RandomForest RFClassifier = new RandomForest();
      J48graft J48Classifier = new J48graft();
      FT FTClassifier = new FT();

      Classifier[] classifierList = { J48Classifier, ATClassifier, RFClassifier, FTClassifier };

      // Bagging BGClassifier = new Bagging();
      LogitBoost LBClassifier = new LogitBoost();
      LBClassifier.setShrinkage(0.5);
      LBClassifier.setNumIterations(40);
      LBClassifier.setWeightThreshold(95);

      Stacking myClassifier = new Stacking();
      myClassifier.setClassifiers(classifierList);
      myClassifier.setMetaClassifier(LBClassifier);

      /*
       * Decorate myClassifier = new Decorate();
       * 
       * ADTree RFClassifier = new ADTree(); // RandomForest RFClassifier = new RandomForest();
       * myClassifier.setClassifier(RFClassifier); myClassifier.setNumIterations(100);
       * myClassifier.setArtificialSize(1.0); myClassifier.setDesiredSize(15);
       */

      classifier3 = new MultiClassClassifier();
      classifier3.setClassifier(myClassifier);

      // System.out.println(eval.toClassDetailsString());
      // System.out.println("===== Evaluating on filtered (training) dataset done =====");

    } catch (Exception e) {
      System.out.println("Problem found when evaluating");
    }
  }

  public double learnDecorate() throws Exception {

    ADTree ATClassifier = new ADTree();
    ATClassifier.setNumOfBoostingIterations(50);

    RandomForest RFClassifier = new RandomForest();
    J48graft J48Classifier = new J48graft();
    FT FTClassifier = new FT();

    Classifier[] classifierList = { J48Classifier, ATClassifier, RFClassifier, FTClassifier };

    // Bagging BGClassifier = new Bagging();
    LogitBoost LBClassifier = new LogitBoost();
    LBClassifier.setShrinkage(0.5);
    LBClassifier.setNumIterations(40);
    LBClassifier.setWeightThreshold(95);

    Stacking myClassifier = new Stacking();
    myClassifier.setClassifiers(classifierList);
    myClassifier.setMetaClassifier(LBClassifier);

    /*
     * // train classifier Decorate myClassifier = new Decorate();
     * 
     * ADTree RFClassifier = new ADTree(); // RandomForest RFClassifier = new RandomForest();
     * myClassifier.setClassifier(RFClassifier); myClassifier.setNumIterations(100);
     * myClassifier.setArtificialSize(1.0); myClassifier.setDesiredSize(15);
     */

    classifier3 = new MultiClassClassifier();
    classifier3.setClassifier(myClassifier);
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
      /*
       * for (int i=0;i<testData.numInstances();i++) {
       * System.out.println(classifier3.classifyInstance(testData.instance(i))); }
       */
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // System.out.println(eval.toSummaryString("\nResults\n======\n", false));
    return eval.errorRate();

  }
  /*
   * public void loadTrainDataset(String fileName) { try { BufferedReader reader = new
   * BufferedReader(new FileReader(fileName)); ArffReader arff = new ArffReader(reader); trainData =
   * arff.getData(); System.out.println("===== Loaded train dataset: " + fileName + " =====");
   * reader.close(); } catch (IOException e) {
   * System.out.println("Problem found when reading train data: " + fileName); } }
   * 
   * public void loadTestDataset(String fileName) { try { BufferedReader reader = new
   * BufferedReader(new FileReader(fileName)); ArffReader arff = new ArffReader(reader); testData =
   * arff.getData(); System.out.println("===== Loaded test dataset: " + fileName + " =====");
   * reader.close(); } catch (IOException e) {
   * System.out.println("Problem found when reading test data: " + fileName); } }
   * 
   * public static void main(String[] args) throws Exception {
   * 
   * classifier30 cls3 = new classifier30(); File XMLDirectory = new File("./dataout/"); File[]
   * files = XMLDirectory.listFiles(); Arrays.sort(files);
   * 
   * int limit = (files.length);
   * 
   * for (int i = 2; i < limit; i = i + 2) { cls3.loadTestDataset(files[i].getAbsolutePath());
   * cls3.loadTrainDataset(files[i + 1].getAbsolutePath()); cls3.testData = testData; cls3.trainData
   * = trainData;
   * 
   * cls3.evaluateDecorate(); try { double error3 = cls3.learnDecorate(); } catch (Exception e1) {
   * // TODO Auto-generated catch block e1.printStackTrace();
   * 
   * } } }
   */
}
