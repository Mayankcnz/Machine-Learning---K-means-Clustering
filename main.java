import java.io.*;
import java.util.*;

public class main {

  public static void main(String[] args) {

    List<double[]> trainingData = null;

    try {
      if (args.length != 2) {
        throw new Exception("Files missing");
      }
      trainingData = readFile(args[0]);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    cluster[] clusters = cluster.kmeansCluster(3, trainingData);
    System.out.println(clusters[2].features.size());

  }

  private static List<double[]> readFile(String file) throws FileNotFoundException {

    List<double[]> trainingData = new ArrayList<>();

    String inputLine;

    try (BufferedReader bufReader = new BufferedReader(new FileReader(file))) {

      while ((inputLine = bufReader.readLine()) != null && inputLine.length() != 0) {

        String[] data = inputLine.split("  ");
        double[] attributes = new double[]{
                Double.parseDouble(data[0]),
                Double.parseDouble(data[1]),
                Double.parseDouble(data[2]),
                Double.parseDouble(data[3])
        };
        trainingData.add(attributes);
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return trainingData;
  }


  private static final class cluster {

    private List<double[]> features;
    private double[] centroids;

    private cluster(List<double[]> features, double[] centroid){

      this.features = features;
      this.centroids = centroid;
    }

    private static cluster[] kmeansCluster(int kValue, List<double[]> trainingData) {
      // return new cluster[];
      //centroid intialization
      double[] max = findMax(trainingData);
      double[] min = findMin(trainingData);


      cluster[] clusters = new cluster[kValue];

      // intialize k random centroids
      List<double[]> randomCentroids = new ArrayList<>();
      for (int i = 0; i < kValue; i++) {
        double[] randomNumbers = new double[4];
        for (int j = 0; j < 4; j++) {
          double randomNumber = (Math.random() * ((max[j] - min[j]) + 1)) + min[j];
          randomNumbers[j] = randomNumber;
          randomCentroids.add(randomNumbers);
        }
      }

      boolean hasConverged = false;

      // TO CHECK CONVERGENCE
      List<List<double[]>> previous = new ArrayList<>();
      List<List<double[]>> features = new ArrayList<>();

      for(int i = 0; i < kValue; i++){
        previous.add(new ArrayList<>());
        features.add(new ArrayList<>());
      }

      while(!hasConverged) {
        hasConverged = true;
        for (double[] data : trainingData) { // for all the training data
          double minDistance = Double.MAX_VALUE;
          int minCentroid = 0;
          for (int i = 0; i < kValue; i++) {// for each centroid
            double distance = distance(data, randomCentroids.get(i)); // get the disnance to each centroid
            // and update which one is more closer
            if (distance < minDistance) {
              minDistance = distance;
              minCentroid = i; // indicating that this centroid is closer to the daya
            }
          }
          features.get(minCentroid).add(data);
          previous.get(minCentroid).add(data); // add to the list the data corresponding to the centroid


          // test for convergence

          // get previously added data and compare to check convergence
          List<double[]> f1 = previous.get(minCentroid);
          List<double[]> f2 = features.get(minCentroid);

          for (int i = 0; i < data.length; i++) {
            if (f1.get(f1.size() - 1) != f2.get(f2.size() - 1)) { // if the last added data to the selected centroid is not the same then we are indeed sure that the convergence is not achieved yet
                hasConverged = false;
            }
            //The centroids are recomputed as the mean of the data points assigned to the respective cluster.
          }
          if(hasConverged)continue;

          // for each data figure
        }
        // move centroids
        for (int k = 0; k < features.size(); k++) { // iterating over each centroid
          if (!features.get(k).isEmpty()) {
            System.out.println(features.get(1).size());
            randomCentroids.set(k, findAverage(features.get(k)));
          }
        }

        for(List<double[]> f : features){
          f.clear();
        }

        for(List<double[]> p : previous){
          p.clear();
        }
      }
      // extract information from lists and return the clusters to the caller
      for(int i = 0; i < kValue; i++) {
        clusters[i] = new cluster(features.get(i), randomCentroids.get(i));
      }

      // have the 3 centroids
      return clusters;
    }

    /***
     * computes the average of all the data in a centroid and returns it
     * @param features corressponding to that centroid
     * @return
     */
    private static double[] findAverage(List<double[]> features){


      double sum = 0;
      double[] average = new double[features.get(0).length];
      for(double[] data: features) {
        for (int i = 0; i < average.length; i++) {
          average[i] += data[i];
        }
      }

      for(int i = 0; i < average.length; i++) {
        average[i] = average[i]/((double)features.size());
      }

      return average;
    }

    private static double distance(double[] data, double[] centroid) {

      assert data.length == centroid.length;

      double Sum = 0.0;
      for(int i=0;i<data.length;i++) {
        Sum = Sum + Math.pow((data[i]-centroid[i]),2.0);
      }
      return Math.sqrt(Sum);

    }

    private static double[] findMax (List <double[]>trainingData) {

      assert trainingData != null && trainingData.size() > 0;

      double[] max = new double[trainingData.get(0).length];
      for (int i = 0; i < max.length; i++) {
        max[i] = Double.MIN_VALUE;
      }

      for (double[] data : trainingData) {
        for (int i = 0; i < data.length; i++) {
          if (data[i] > max[i]) {
            max[i] = data[i];
          }
        }
      }


      return max;
    }


    private static double[] findMin(List<double[]> trainingData){
      assert trainingData != null && trainingData.size() > 0;

      double[] min = new double[trainingData.get(0).length];
      for(int i = 0; i < min.length; i++){
        min[i] = Double.MAX_VALUE;
      }

      for(double[] data : trainingData){
        for(int i = 0; i < data.length; i++){
          if(data[i] < min[i]){
            min[i] = data[i];
          }
        }
      }

      return min;
    }
  }
}

