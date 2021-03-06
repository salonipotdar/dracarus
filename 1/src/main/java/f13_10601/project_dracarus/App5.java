package f13_10601.project_dracarus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.Dagging;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.meta.MultiClassClassifier;
import weka.classifiers.meta.Vote;
import weka.classifiers.trees.FT;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ArffLoader.ArffReader;

public class App5 {

  static int index;

  static Instances trainData;

  static Instances testData;

  static MultiClassClassifier classifier1_App;

  static MultiClassClassifier classifier2_App;

  static MultiClassClassifier classifier3_App;

  static MultiClassClassifier NBClassifier_App;

  public void loadTrainDataset(String fileName) throws Exception {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(fileName));
      ArffReader arff = new ArffReader(reader);
      trainData = arff.getData();

      if (fileName.contains("mfeat")) {
        // give the number of splits
        int noOfSplits = 5;
        // split the data
        Instances[] trainDataSampled = partitionDataSetToSamples(trainData, noOfSplits);
        // use only one of them to train
        index = randInt(1, 4);
        trainData = trainDataSampled[index];
      }

      System.out.println("===== Loaded train dataset: " + fileName + " =====");
      reader.close();
    } catch (IOException e) {
      System.out.println("Problem found when reading train data: " + fileName);
    }
  }

  public void loadTestDataset(String fileName) throws Exception {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(fileName));
      ArffReader arff = new ArffReader(reader);
      testData = arff.getData();
      // System.out.println("\nTest: dont modify it as it is the test data");
      System.out.println("===== Loaded test dataset: " + fileName + " =====");
      reader.close();
    } catch (IOException e) {
      System.out.println("Problem found when reading test data: " + fileName);
    }
  }

  public static void main(String[] args) throws Exception {

    MultiClassClassifier classifier1_App = new MultiClassClassifier();
    MultiClassClassifier classifier2_App = new MultiClassClassifier();
    MultiClassClassifier classifier3_App = new MultiClassClassifier();
    MultiClassClassifier NBClassifier_App = new MultiClassClassifier();

    classifier15 cls1 = new classifier15();
    classifier25 cls2 = new classifier25();
    classifier35 cls3 = new classifier35();
    NBClassifier0 clsnb = new NBClassifier0();

    App5 app_obj = new App5();

    File XMLDirectory = new File("./dataout/");
    File[] files = XMLDirectory.listFiles();
    Arrays.sort(files);

    int limit = (files.length);
    PrintWriter writer = null;
    try {
      writer = new PrintWriter("./result.txt", "UTF-8");
    } catch (FileNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (UnsupportedEncodingException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    ArrayList<Double> errorC_errorNB = new ArrayList<Double>();
    ArrayList<Double> errorC1 = new ArrayList<Double>();
    ArrayList<Double> errorC2 = new ArrayList<Double>();
    ArrayList<Double> errorC3 = new ArrayList<Double>();
    ArrayList<Double> errorNB = new ArrayList<Double>();

    for (int i = 1; i < limit; i = i + 2) {
      app_obj.loadTestDataset(files[i].getAbsolutePath());
      app_obj.loadTrainDataset(files[i + 1].getAbsolutePath());

      cls1.classifier1 = classifier1_App;
      cls2.classifier2 = classifier2_App;
      cls3.classifier3 = classifier3_App;
      clsnb.classifier2 = NBClassifier_App;

      cls1.testData = testData;
      cls1.trainData = trainData;

      cls2.testData = testData;
      cls2.trainData = trainData;

      cls3.testData = testData;
      cls3.trainData = trainData;

      clsnb.testData = testData;
      clsnb.trainData = trainData;

      double error1, error2, error3;

      clsnb.evaluateNB();
      double error_nb = clsnb.learnNB();

      cls1.evaluateADTree();
      error1 = cls1.learnADTree();
      errorC1.add(error1);

      cls2.evaluateJ48graft();
      error2 = cls2.learnJ48graft();
      errorC2.add(error2);

      cls3.evaluateDecorate();
      try {
        error3 = cls3.learnDecorate();
        errorC3.add(error3);
      } catch (Exception e1) {
        // TODO Auto-generated catch block
        // e1.printStackTrace();
        error3 = 1.0;
      }

      if (Double.isNaN(error3)) {
        error3 = 1.0;
      }

      System.out.println("\n\n*********** Untuned RandomForest Error: " + error1);
      System.out.println("*********** Tuned LogitBoost Error: " + error2);
      System.out.println("*********** Tuned Vote Error: " + error3);
      System.out.println("*********** NaiveBayes Error: " + error_nb);

      errorNB.add(error_nb);

    }

    ArrayList<Double> errorC1_original = (ArrayList<Double>) errorC1.clone();
    System.out.println("errorc1:");
    System.out.println(errorC1_original);
    ArrayList<Double> errorC2_original = (ArrayList<Double>) errorC2.clone();
    System.out.println("errorc2:");
    System.out.println(errorC2_original);
    ArrayList<Double> errorC3_original = (ArrayList<Double>) errorC3.clone();
    System.out.println("errorc3:");
    System.out.println(errorC3_original);
    Collections.sort(errorC1);
    Collections.sort(errorC2);
    Collections.sort(errorC3);

    double sumErrorC1 = 0, sumErrorC2 = 0, sumErrorC3 = 0;
    for (int i = 0; i < errorC1.size(); i++) {
      sumErrorC1 += errorC1.get(i);
      sumErrorC2 += errorC2.get(i);
      sumErrorC3 += errorC3.get(i);
    }

    double avgErrorC1 = sumErrorC1 / errorC1.size();
    double avgErrorC2 = sumErrorC2 / errorC2.size();
    double avgErrorC3 = sumErrorC3 / errorC3.size();

    double maxErrorC1 = errorC1.get(errorC1.size() - 1);
    double maxErrorC2 = errorC2.get(errorC2.size() - 1);
    double maxErrorC3 = errorC3.get(errorC3.size() - 1);

    // double maxMinusAvgErrorC1 = maxErrorC1 - avgErrorC1;
    // double maxMinusAvgErrorC2 = maxErrorC2 - avgErrorC2;
    // double maxMinusAvgErrorC3 = maxErrorC3 - avgErrorC3;

    // double f1ScoreC1=(2*maxMinusAvgErrorC1*avgErrorC1)/(maxMinusAvgErrorC1+avgErrorC1);
    // double f1ScoreC2=(2*maxMinusAvgErrorC2*avgErrorC2)/(maxMinusAvgErrorC2+avgErrorC2);
    // double f1ScoreC3=(2*maxMinusAvgErrorC3*avgErrorC3)/(maxMinusAvgErrorC3+avgErrorC3);

    double f1ScoreC1 = avgErrorC1;
    double f1ScoreC2 = avgErrorC2;
    double f1ScoreC3 = avgErrorC3;

    double errorC1_errorNB = 0.0;
    double errorC2_errorNB = 0.0;
    double errorC3_errorNB = 0.0;

    for (int k = 0; k < errorC1.size(); k++) {
      errorC1_errorNB += (errorC1_original.get(k) / errorNB.get(k));
      errorC2_errorNB += (errorC2_original.get(k) / errorNB.get(k));
      errorC3_errorNB += (errorC3_original.get(k) / errorNB.get(k));
    }

    System.out.println("\n========= Average Errors Ratio ==========\n");
    System.out.println("Untuned RandomForest: " + (errorC1_errorNB / errorC1.size())
            + "\tTuned LogitBoost: " + (errorC2_errorNB / errorC2.size()) + "\tTuned Vote: "
            + errorC3_errorNB / errorC3.size());

    /*
     * System.out.println("\n========= Max Errors Ratio ==========\n");
     * System.out.println("ADTree: " + maxErrorC1 + "\tJ48graft: " + maxErrorC2 + "\tStacking: " +
     * maxErrorC3);
     */
    System.out.println("\n=======================================\n");

    System.out.println("\n========= Average Errors ==========\n");
    System.out.println("Untuned RandomForest: " + f1ScoreC1 + "\tTuned LogitBoost: " + f1ScoreC2
            + "\tTuned Vote: " + f1ScoreC3);
    System.out.println("\n========= Max Errors ==========\n");
    System.out.println("Untuned RandomForest: " + maxErrorC1 + "\tTuned LogitBoost: " + maxErrorC2
            + "\tTuned Vote: " + maxErrorC3);

    double leastF1Score = Math.min(f1ScoreC1, Math.min(f1ScoreC2 + 1, f1ScoreC3 + 1));
    if (leastF1Score == f1ScoreC1) {
      // output all models for this one
      String folderName;
      folderName = ("./model/");
      File file = new File(folderName);
      file.mkdir();
      for (int i = 1; i < limit; i = i + 2) {

        /*
         * AdaBoostM1 myClassifier = new AdaBoostM1(); J48 J48Classifier = new J48();
         * J48Classifier.setConfidenceFactor((float) 0.50);
         * myClassifier.setClassifier(J48Classifier);
         */

        RandomForest myClassifier = new RandomForest();
        /*
         * ADTree myClassifier = new ADTree(); myClassifier.setNumOfBoostingIterations(61);
         */

        MultiClassClassifier cls = new MultiClassClassifier();
        cls.setClassifier(myClassifier);

        // train
        Instances inst = null;
        Instances inst1 = null;
        try {
          inst = new Instances(new BufferedReader(new FileReader(files[i + 1].getAbsolutePath())));
          inst1 = new Instances(new BufferedReader(new FileReader(files[i].getAbsolutePath())));
        } catch (FileNotFoundException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        inst.setClassIndex(inst.numAttributes() - 1);
        inst1.setClassIndex(inst1.numAttributes() - 1);

        if (files[i + 1].getAbsolutePath().contains("splice")) {
          // give the number of splits
          int noOfSplits = 5;
          // split the data
          Instances[] trainDataSampled = partitionDataSetToSamples(inst, noOfSplits);
          // use only one of them to train
          // int index = randInt(1, 5);
          inst = trainDataSampled[index];
        }

        try {
          cls.buildClassifier(inst);
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }

        try {
          Evaluation eval = null;
          eval = new Evaluation(inst);
          eval.evaluateModel(cls, inst1);
          PrintWriter writer1 = new PrintWriter(folderName
                  + files[i].getName().toString().replace("_test.arff", "") + "-LB.predict",
                  "UTF-8");
          for (int i1 = 0; i1 < inst1.numInstances(); i1++) {
            long prediction = (long) cls.classifyInstance(inst1.instance(i1));
            writer1.write(Long.toString(prediction) + "\n");
          }
          writer1.close();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }
      for (int k = 0; k < errorC1.size(); k++) {
        System.out.println("=============== Best Error: " + errorC1_original.get(k)
                + " from Untuned RandomForest ===============\n\n");
        writer.println(errorC1_original.get(k) / errorNB.get(k));
        errorC_errorNB.add(errorC1_original.get(k) / errorNB.get(k));
      }
    } else if (leastF1Score == f1ScoreC2) {
      String folderName;
      folderName = ("./model/");
      File file = new File(folderName);
      file.mkdir();
      // output all models for this one
      for (int i = 1; i < limit; i = i + 2) {

        // Bagging myClassifier = new Bagging();

        LogitBoost myClassifier = new LogitBoost();
        myClassifier.setShrinkage(0.5);
        myClassifier.setNumIterations(40);
        myClassifier.setWeightThreshold(96);

        // ADTree myClassifier = new ADTree();
        // myClassifier.setNumOfBoostingIterations(61);

        /*
         * J48graft myClassifier = new J48graft(); myClassifier.setUnpruned(false);
         * myClassifier.setConfidenceFactor((float) 0.45); myClassifier.setMinNumObj(2);
         * myClassifier.setBinarySplits(false); myClassifier.setUseLaplace(true);
         * myClassifier.setRelabel(true);
         */

        MultiClassClassifier cls = new MultiClassClassifier();
        cls.setClassifier(myClassifier);

        // train
        Instances inst = null;
        Instances inst1 = null;
        try {
          inst = new Instances(new BufferedReader(new FileReader(files[i + 1].getAbsolutePath())));
          inst1 = new Instances(new BufferedReader(new FileReader(files[i].getAbsolutePath())));
        } catch (FileNotFoundException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }

        inst.setClassIndex(inst.numAttributes() - 1);
        inst1.setClassIndex(inst1.numAttributes() - 1);

        try {
          cls.buildClassifier(inst);
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }

        try {
          Evaluation eval = null;
          eval = new Evaluation(inst);
          eval.evaluateModel(cls, inst1);
          PrintWriter writer1 = new PrintWriter(folderName
                  + files[i].getName().toString().replace("_test.arff", "") + ".predict", "UTF-8");
          for (int i1 = 0; i1 < inst1.numInstances(); i1++) {
            long prediction = (long) cls.classifyInstance(inst1.instance(i1));
            writer1.write(Long.toString(prediction) + "\n");
          }
          writer1.close();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }
      for (int k = 0; k < errorC2.size(); k++) {
        System.out.println("=============== Best Error: " + errorC2_original.get(k)
                + " from Tuned LogitBoost ===============\n\n");
        writer.println(errorC2_original.get(k) / errorNB.get(k));
        errorC_errorNB.add(errorC2_original.get(k) / errorNB.get(k));
      }
    } else if (leastF1Score == f1ScoreC3) {
      // output all models for this one
      String folderName;
      folderName = ("./model/");
      File file = new File(folderName);
      file.mkdir();
      for (int i = 1; i < limit; i = i + 2) {

        /*
         * STACKING ADTree ATClassifier = new ADTree(); ATClassifier.setNumOfBoostingIterations(35);
         * 
         * J48graft J48Classifier = new J48graft(); RandomForest RFClassifier = new RandomForest();
         * 
         * FT FTClassifier = new FT(); //FTClassifier.setMinNumInstances(12);
         * //FTClassifier.setWeightTrimBeta(0.06);
         * 
         * DecisionTable DTClassifier = new DecisionTable(); DTClassifier.setCrossVal(2);
         * 
         * NaiveBayes NBClassifier = new NaiveBayes(); Logistic LGClassifier = new Logistic();
         * 
         * SMO SMOClassifier = new SMO(); // SMOClassifier.setC(16.0); //
         * SMOClassifier.setEpsilon(0.0000000000005);
         * 
         * Classifier[] classifierList = { ATClassifier, RFClassifier, FTClassifier, DTClassifier,
         * NBClassifier, SMOClassifier, LGClassifier, J48Classifier };
         * 
         * // Bagging BGClassifier = new Bagging(); LogitBoost LBClassifier = new LogitBoost();
         * LBClassifier.setShrinkage(0.5); LBClassifier.setNumIterations(40);
         * LBClassifier.setWeightThreshold(95);
         * 
         * Stacking myClassifier = new Stacking(); myClassifier.setClassifiers(classifierList);
         * myClassifier.setMetaClassifier(LBClassifier);
         */

        Vote myClassifier = new Vote();

        FT FTClassifier = new FT();
        FTClassifier.setMinNumInstances(12);
        FTClassifier.setWeightTrimBeta(0.06);
        FTClassifier.setModelType(new SelectedTag(FTClassifier.MODEL_FTLeaves,
                FTClassifier.TAGS_MODEL));

        RandomForest RFClassifier = new RandomForest();
        RFClassifier.setNumTrees(25);

        AdaBoostM1 ABClassifier = new AdaBoostM1();
        J48 J48Classifier = new J48();
        J48Classifier.setConfidenceFactor((float) 0.50);
        ABClassifier.setClassifier(J48Classifier);
        // ABClassifier.setWeightThreshold(4);
        // ABClassifier.setNumIterations(100);

        /*
         * SMO SMOClassifier = new SMO(); SMOClassifier.setC(2.75); SMOClassifier.setFilterType(new
         * SelectedTag(SMOClassifier.FILTER_NORMALIZE, SMOClassifier.TAGS_FILTER));
         */

        Bagging BGClassifier = new Bagging();

        /*
         * LMT LMTClassifier = new LMT(); LMTClassifier.setWeightTrimBeta(0.45);
         * LMTClassifier.setNumBoostingIterations(-1);
         * 
         * BGClassifier.setClassifier(LMTClassifier);
         */

        Dagging DGClassifier = new Dagging();
        DGClassifier.setNumFolds(1);
        // DGClassifier.setClassifier(SMOClassifier);

        LogitBoost LBClassifier = new LogitBoost();
        LBClassifier.setShrinkage(0.5);
        LBClassifier.setNumIterations(40);
        LBClassifier.setWeightThreshold(96);

        Classifier[] classifierList = { RFClassifier, DGClassifier, LBClassifier, ABClassifier,
            BGClassifier };
        myClassifier.setClassifiers(classifierList);
        // myClassifier.setCombinationRule(new SelectedTag(myClassifier.MIN_RULE,
        // myClassifier.TAGS_RULES));

        /*
         * DECORATE Decorate myClassifier = new Decorate(); RandomForest RFClassifier = new
         * RandomForest(); myClassifier.setClassifier(RFClassifier);
         * myClassifier.setNumIterations(50); myClassifier.setArtificialSize(1.0);
         * myClassifier.setDesiredSize(15);
         */

        MultiClassClassifier cls = new MultiClassClassifier();
        cls.setClassifier(myClassifier);

        // train
        Instances inst = null;
        Instances inst1 = null;
        try {
          inst = new Instances(new BufferedReader(new FileReader(files[i + 1].getAbsolutePath())));
          inst1 = new Instances(new BufferedReader(new FileReader(files[i].getAbsolutePath())));
        } catch (FileNotFoundException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        inst.setClassIndex(inst.numAttributes() - 1);
        inst1.setClassIndex(inst1.numAttributes() - 1);

        if (files[i + 1].getAbsolutePath().contains("splice")) {
          // give the number of splits
          int noOfSplits = 5;
          // split the data
          Instances[] trainDataSampled = partitionDataSetToSamples(inst, noOfSplits);
          // use only one of them to train
          // int index = randInt(1, 5);
          inst = trainDataSampled[index];
        }

        try {
          cls.buildClassifier(inst);
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }

        try {
          Evaluation eval = null;
          eval = new Evaluation(inst);
          eval.evaluateModel(cls, inst1);
          PrintWriter writer1 = new PrintWriter(folderName
                  + files[i].getName().toString().replace("_test.arff", "") + "-L5.predict",
                  "UTF-8");
          for (int i1 = 0; i1 < inst1.numInstances(); i1++) {
            long prediction = (long) cls.classifyInstance(inst1.instance(i1));
            writer1.write(Long.toString(prediction) + "\n");
          }
          writer1.close();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }
      for (int k = 0; k < errorC3_original.size(); k++) {
        System.out.println("=============== Best Error: " + errorC3_original.get(k)
                + " from Tuned Vote ===============\n\n");
        writer.println(errorC3_original.get(k) / errorNB.get(k));
        errorC_errorNB.add(errorC3_original.get(k) / errorNB.get(k));
      }

    }

    double average = 0.0;
    for (int k = 0; k < errorC_errorNB.size(); k++) {
      average += errorC_errorNB.get(k);
    }
    writer.println(average / errorC_errorNB.size());
    Collections.sort(errorC_errorNB);
    writer.print(errorC_errorNB.get(errorC_errorNB.size() - 1));
    writer.close();
  }

  public static Instances[] partitionDataSetToSamples(Instances totalSet, int samples)
          throws Exception {
    Instances[] resampledInstances = new Instances[samples];
    // User has provided a random number seed.
    totalSet.randomize(new Random(1));
    totalSet.setClassIndex(totalSet.numAttributes() - 1);
    // Select out a fold
    totalSet.stratify(samples);
    for (int i = 0; i < samples; i++) {
      resampledInstances[i] = totalSet.testCV(samples, i);
    }
    return resampledInstances;
  }

  public static int randInt(int min, int max) {
    // Usually this can be a field rather than a method variable
    Random rand = new Random();

    // nextInt is normally exclusive of the top value,
    // so add 1 to make it inclusive
    int randomNum = rand.nextInt((max - min) + 1) + min;

    return randomNum;
  }

}