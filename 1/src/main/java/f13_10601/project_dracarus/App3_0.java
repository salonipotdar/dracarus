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

import weka.classifiers.Evaluation;
import weka.classifiers.meta.Decorate;
import weka.classifiers.meta.MultiClassClassifier;
import weka.classifiers.trees.ADTree;
import weka.classifiers.trees.J48graft;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class App3_0 {

  static Instances trainData;

  static Instances testData;

  static MultiClassClassifier classifier1_App;

  static MultiClassClassifier classifier2_App;

  static MultiClassClassifier classifier3_App;

  static MultiClassClassifier NBClassifier_App;

  public void loadTrainDataset(String fileName) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(fileName));
      ArffReader arff = new ArffReader(reader);
      trainData = arff.getData();
      if (fileName.contains("splice")) {
        System.out.println("Train: HAHAHAHAHA");
        for (int m = 1; m < 50; m++) {
          if (trainData.attribute(m).name().equals("Attribute_" + m) == true) {
            trainData.deleteAttributeAt(m);
          }
        }
      }
      System.out.println("===== Loaded train dataset: " + fileName + " =====");
      reader.close();
    } catch (IOException e) {
      System.out.println("Problem found when reading train data: " + fileName);
    }
  }

  public void loadTestDataset(String fileName) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(fileName));
      ArffReader arff = new ArffReader(reader);
      testData = arff.getData();
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

    classifier14b cls1 = new classifier14b();
    classifier24b cls2 = new classifier24b();
    classifier34b cls3 = new classifier34b();
    NBClassifier0 clsnb = new NBClassifier0();

    App3_0 app_obj = new App3_0();

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

    for (int i = 2; i < limit; i = i + 2) {
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

      System.out.println("\n\n*********** ADTree Error: " + error1);
      System.out.println("*********** J48graft Error: " + error2);
      System.out.println("*********** Decorate Error: " + error3);
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

    double maxMinusAvgErrorC1 = maxErrorC1 - avgErrorC1;
    double maxMinusAvgErrorC2 = maxErrorC2 - avgErrorC2;
    double maxMinusAvgErrorC3 = maxErrorC3 - avgErrorC3;

    // double f1ScoreC1=(2*maxMinusAvgErrorC1*avgErrorC1)/(maxMinusAvgErrorC1+avgErrorC1);
    // double f1ScoreC2=(2*maxMinusAvgErrorC2*avgErrorC2)/(maxMinusAvgErrorC2+avgErrorC2);
    // double f1ScoreC3=(2*maxMinusAvgErrorC3*avgErrorC3)/(maxMinusAvgErrorC3+avgErrorC3);

    double f1ScoreC1 = avgErrorC1;
    double f1ScoreC2 = avgErrorC2;
    double f1ScoreC3 = avgErrorC3;

    System.out.println(f1ScoreC1 + "**" + f1ScoreC2 + "**" + f1ScoreC3);
    double leastF1Score = Math.min(f1ScoreC1, Math.min(f1ScoreC2, f1ScoreC3+1));
    if (leastF1Score == f1ScoreC1) {
      // output all models for this one
      String folderName;
      folderName = ("./model/");
      File file = new File(folderName);
      file.mkdir();
      for (int i = 2; i < limit; i = i + 2) {

        ADTree myClassifier = new ADTree();
        
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
      for (int k = 0; k < errorC1.size(); k++) {
        System.out.println("=============== Best Error: " + errorC1_original.get(k)
                + " from ADTree ===============\n\n");
        writer.println(errorC1_original.get(k) / errorNB.get(k));
        errorC_errorNB.add(errorC1_original.get(k) / errorNB.get(k));
      }
    } else if (leastF1Score == f1ScoreC2) {
      String folderName;
      folderName = ("./model/");
      File file = new File(folderName);
      file.mkdir();
      // output all models for this one
      for (int i = 2; i < limit; i = i + 2) {
        J48graft myClassifier = new J48graft();
        myClassifier.setUnpruned(false);
        myClassifier.setConfidenceFactor((float) 0.45);
        myClassifier.setMinNumObj(2);
        myClassifier.setBinarySplits(false);
        myClassifier.setUseLaplace(true);
        myClassifier.setRelabel(true);

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
                + " from J48graft ===============\n\n");
        writer.println(errorC2_original.get(k) / errorNB.get(k));
        errorC_errorNB.add(errorC2_original.get(k) / errorNB.get(k));
      }
    } else if (leastF1Score == f1ScoreC3) {
      // output all models for this one
      String folderName;
      folderName = ("./model/");
      File file = new File(folderName);
      file.mkdir();
      for (int i = 2; i < limit; i = i + 2) {

        Decorate myClassifier = new Decorate();
        RandomForest RFClassifier = new RandomForest();
        myClassifier.setClassifier(RFClassifier);
        myClassifier.setNumIterations(50);
        myClassifier.setArtificialSize(1.0);
        myClassifier.setDesiredSize(15);

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
      for (int k = 0; k < errorC3_original.size(); k++) {
        System.out.println("=============== Best Error: " + errorC3_original.get(k)
                + " from Decorate ===============\n\n");
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
}