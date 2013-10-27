package f13_10601.project_dracarus;

/**
 * Hello world!
 *
 */
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Standardize;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.Evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.classifiers.meta.MultiClassClassifier;
import weka.classifiers.trees.ADTree;
import weka.core.converters.ArffLoader.ArffReader;

import java.io.*;

/**
 * This class implements a simple text learner in Java using WEKA. It loads a text dataset written
 * in ARFF format, evaluates a classifier on it, and saves the learnt model for further use.
 * 
 * @author Jose Maria Gomez Hidalgo - http://www.esp.uem.es/jmgomez
 * @see MyFilteredClassifier
 */

public class classifier1 {

  /**
   * Objects that stores training and testing data.
   */
  Instances trainData;

  Instances testData;

  /**
   * Object that stores the filter
   */
  StringToWordVector filter;

  /**
   * Object that stores the classifier
   */
  MultiClassClassifier classifier;

  /**
   * This method loads a dataset in ARFF format. If the file does not exist, or it has a wrong
   * format, the attribute trainData is null.
   * 
   * @param fileName
   *          The name of the file that stores the dataset.
   */
  public void loadTrainDataset(String fileName) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(fileName));
      ArffReader arff = new ArffReader(reader);
      trainData = arff.getData();
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

  public void evaluate() {
    try {
      trainData.setClassIndex(trainData.numAttributes() - 1);
      testData.setClassIndex(trainData.numAttributes() - 1);

      filter = new StringToWordVector();
      filter.setAttributeIndices("last");
      classifier = new MultiClassClassifier();
      classifier.setClassifier(new ADTree());
      Evaluation eval = new Evaluation(trainData);
      eval.crossValidateModel(classifier, trainData, 4, new Random(1));
      System.out.println(eval.toSummaryString());
      System.out.println(eval.toClassDetailsString());
      System.out.println("===== Evaluating on filtered (training) dataset done =====");

    } catch (Exception e) {
      System.out.println("Problem found when evaluating");
    }
  }

  /**
   * This method trains the classifier on the loaded dataset.
   */
  public double learn() {
    Instances train = trainData; // from somewhere
    Instances test = testData; // from somewhere
    Standardize filter = new Standardize();
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
      // train classifier
    MultiClassClassifier cls = new MultiClassClassifier();
    cls.setClassifier(new ADTree());
    try {
      cls.buildClassifier(newTrain);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // evaluate classifier and print some statistics
    Evaluation eval = null;
    try {
      eval = new Evaluation(newTrain);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try {
      eval.evaluateModel(cls, newTest);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println(eval.toSummaryString("\nResults\n======\n", false)); // Uncomment to see the
                                                                            // classifier
    return eval.errorRate();
  }

  /**
   * This method saves the trained model into a file. This is done by simple serialization of the
   * classifier object.
   * 
   * @param fileName
   *          The name of the file that will store the trained model.
   */
  public void saveModel(String fileName) {
    try {
      weka.core.SerializationHelper.write(fileName, classifier);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Main method. It is an example of the usage of this class.
   * 
   * @param args
   *          Command-line arguments: fileData and fileModel.
   */
  public static List<Double> main(String[] args) {

    classifier1 learner;
    learner = new classifier1();

    File[] files = new File("C:/Users/user/git/dracarus/1/dataout/").listFiles();
    // System.out.println(files.length);

    int limit = (files.length);
    List<Double> errorList = new ArrayList<Double>();
    for (int i = 2; i < limit; i = i + 2) {
      // System.out.println(files[i].getAbsolutePath());
      learner.loadTrainDataset(files[i].getAbsolutePath());
      learner.loadTestDataset(files[i + 1].getAbsolutePath());

      // Evaluation must be done before training
      // More info in: http://weka.wikispaces.com/Use+WEKA+in+your+Java+code
      learner.evaluate();
      double error = learner.learn();
      errorList.add(error);
      
      String folderName = ("C:/Users/user/git/dracarus/1/model/"
              + files[i].getName().toString().replace("_test.arff", "") + "/");
      learner.saveModel(folderName + "ADTree_"
              + files[i].getName().toString().replace("_test.arff", "") + ".model");
    }
    return errorList;
  }

}
