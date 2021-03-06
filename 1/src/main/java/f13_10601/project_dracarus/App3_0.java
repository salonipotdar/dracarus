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

import weka.classifiers.meta.Decorate;
import weka.classifiers.meta.MultiClassClassifier;
import weka.classifiers.trees.ADTree;
import weka.classifiers.trees.J48graft;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class App3_0 {

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

      if (fileName.contains("splice")) {
        System.out.println("Train: HAHAHAHAHA");
        // MODIFIED BY SALONI
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

    classifier13_0 cls1 = new classifier13_0();
    classifier23_0 cls2 = new classifier23_0();
    classifier33_0 cls3 = new classifier33_0();
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

      if (files[i].getAbsolutePath().contains("hypothyroid")) {
        for (int m = 0; m < trainData.numAttributes(); m++) {
          if (trainData.attribute(m).name().equals("TBG measured")) {
            trainData.deleteAttributeAt(m);
          }
        }

        for (int m = 0; m < testData.numAttributes(); m++) {
          if (testData.attribute(m).name().equals("TBG measured") == true) {
            testData.deleteAttributeAt(m);
          }
        }

        cls3.testData = testData;
        cls3.trainData = trainData;
      } else {
        cls3.testData = testData;
        cls3.trainData = trainData;
      }

      clsnb.testData = testData;
      clsnb.trainData = trainData;

      double error1, error2, error3, least_error;

      clsnb.evaluateNB();
      double error_nb = clsnb.learnNB();

      cls1.evaluateADTree();
      error1 = cls1.learnADTree();

      cls2.evaluateJ48graft();
      error2 = cls2.learnJ48graft();

      cls3.evaluateDecorate();
      try {
        error3 = cls3.learnDecorate();
      } catch (Exception e1) {
        // TODO Auto-generated catch block
        // e1.printStackTrace();
        error3 = 1.0;
      }

      if (Double.isNaN(error3)) {
        error3 = 1.0;
      }

      least_error = Math.min(error1, Math.min(error2 + 1, error3 + 1));

      System.out.println("\n\n*********** ADTree Error: " + error1);
      System.out.println("*********** J48graft Error: " + error2);
      System.out.println("*********** Decorate Error: " + error3);
      System.out.println("*********** NaiveBayes Error: " + error_nb);

      String folderName;
      folderName = ("./model/");
      File file = new File(folderName);
      file.mkdir();

      if (error1 == least_error) {
        MultiClassClassifier cls = new MultiClassClassifier();
        cls.setClassifier(new ADTree());

        // train
        Instances inst = null;
        try {
          inst = new Instances(new BufferedReader(new FileReader(files[i + 1].getAbsolutePath())));
        } catch (FileNotFoundException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        inst.setClassIndex(inst.numAttributes() - 1);

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
          System.out.println("=============== Best Error: " + error1
                  + " from ADTree ===============\n\n");
          weka.core.SerializationHelper.write(
                  folderName + files[i].getName().toString().replace("_test.arff", "") + "0.model",
                  cls);
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      } else if (error2 == least_error) {
        MultiClassClassifier cls = new MultiClassClassifier();
        cls.setClassifier(new J48graft());

        // train
        Instances inst = null;
        try {
          inst = new Instances(new BufferedReader(new FileReader(files[i + 1].getAbsolutePath())));
        } catch (FileNotFoundException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        inst.setClassIndex(inst.numAttributes() - 1);

        try {
          cls.buildClassifier(inst);
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }

        try {
          System.out.println("=============== Best Error: " + error2
                  + " from J48graft ===============\n\n");
          weka.core.SerializationHelper.write(
                  folderName + files[i].getName().toString().replace("_test.arff", "") + ".model",
                  cls);
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      } else if (error3 == least_error) {
        MultiClassClassifier cls = new MultiClassClassifier();
        cls.setClassifier(new Decorate());

        // train
        Instances inst = null;
        try {
          inst = new Instances(new BufferedReader(new FileReader(files[i + 1].getAbsolutePath())));
        } catch (FileNotFoundException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        inst.setClassIndex(inst.numAttributes() - 1);

        try {
          cls.buildClassifier(inst);
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }

        try {
          System.out.println("=============== Best Error: " + error3
                  + " from Decorate ===============\n\n");
          weka.core.SerializationHelper.write(
                  folderName + files[i].getName().toString().replace("_test.arff", "") + ".model",
                  cls);
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

      writer.println(least_error / error_nb);
      errorC_errorNB.add(least_error / error_nb);
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