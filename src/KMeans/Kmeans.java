package KMeans;

import Data.*;
import GA.*;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Kmeans {


    public Data dataset;
    public HashMap<Integer, Thyroid> centroids = new HashMap<Integer, Thyroid>();
    public HashMap<Integer, ArrayList<Thyroid>> clusters = new HashMap<>();
    public static final Random rand = new Random();

    public Kmeans(Data dataset) {
        this.dataset = dataset;
    }


    public void randomCentroids(int k) {
        //create max/min thyroid where each att value = max/min
        Thyroid max = new Thyroid(-1, dataset.max(Data.T3), dataset.max(Data.TYS),
                dataset.max(Data.TRI), dataset.max(Data.TSH), dataset.max(Data.DTSH));

        Thyroid min = new Thyroid(-1, dataset.min(Data.T3), dataset.min(Data.TYS),
                dataset.min(Data.TRI), dataset.min(Data.TSH), dataset.min(Data.DTSH));

        //geberating the k centroids
        for (int i = 1; i <= k; i++) {
            //the class is always -1 :
            Thyroid centoid = new Thyroid(-1, rand.nextFloat() * (max.getT3() - min.getT3()) + min.getT3(),
                    rand.nextFloat() * (max.getTys() - min.getTys()) + min.getTys(),
                    rand.nextFloat() * (max.getTri() - min.getTri()) + min.getTri(),
                    rand.nextFloat() * (max.getTsh() - min.getTsh()) + min.getTsh(),
                    rand.nextFloat() * (max.getDtsh() - min.getDtsh()) + min.getDtsh());
            centroids.put(i, centoid);
        }
    }

    public int assignToNearestCentroid(Thyroid thyroid) {
        //this function calculate the dist between each centroid and the thyroid object
        //and return the k (integer of calss) with the smallest distance between the thyroid and the k centroid

        double minimumDistance = Double.MAX_VALUE;
        int classe = 0;
        for (Map.Entry<Integer, Thyroid> centroid : centroids.entrySet()) {
            //calculate the distance :
            double dist = centroid.getValue().euclideinDistance(thyroid);
            if (dist < minimumDistance) {
                classe = centroid.getKey();
                minimumDistance = dist;
            }
        }
        return classe;
    }

    public Thyroid mean(ArrayList<Thyroid> cluster) {
        //the case where no item is assigned to the cluster
        if (cluster == null || cluster.isEmpty())
            return null;

        float mean_t3 = 0;
        float mean_tsh = 0;
        float mean_tys = 0;
        float mean_dtsh = 0;
        float mean_tri = 0;

        for (Thyroid thyroid : cluster) {
            mean_dtsh = mean_dtsh + thyroid.getDtsh();
            mean_t3 = mean_t3 + thyroid.getT3();
            mean_tsh = mean_tsh + thyroid.getTsh();
            mean_tys = mean_tys + thyroid.getTys();
            mean_tri = mean_tri + thyroid.getTri();
        }

        Thyroid mean = new Thyroid(-1, mean_t3 / cluster.size(), mean_tys / cluster.size(),
                mean_tri / cluster.size(), mean_tsh / cluster.size(), mean_dtsh / cluster.size());

        return mean;
    }

    public void newCentroids() {
        //for each cluster assign the mean to the centroid
        for (Map.Entry<Integer, ArrayList<Thyroid>> cluster : clusters.entrySet()) {
            Thyroid newCentroid = mean(cluster.getValue());
            if (newCentroid != null)
                centroids.put(cluster.getKey(), newCentroid);
            //otherwise keep the old one /case where the cluster is empty
        }
    }

    public void cleanClusters() {
        for (Map.Entry<Integer, ArrayList<Thyroid>> cluster : clusters.entrySet()) {
            cluster.setValue(new ArrayList<>());
        }
    }

    public HashMap<Integer, ArrayList<Thyroid>> fit(int maxIter, int k) {

        //generate initial centroid :
        randomCentroids(k);
        boolean stop = false;
        int i = 0;
        while (!stop && i < maxIter) {
            HashMap<Integer, ArrayList<Thyroid>> lastStateClusters = (HashMap) clusters.clone();
            //affecter chaque instance au cluster le plus similaire ;
            cleanClusters();
            for (Thyroid thyroid : dataset.getDataset()) {
                //calculate the nearest centroid class
                int cent = assignToNearestCentroid(thyroid);
                //getting the cluster k
                ArrayList<Thyroid> cluster = new ArrayList<Thyroid>();
                if (clusters.get(cent) != null)
                    cluster = clusters.get(cent);
                //assing the thyroid to cluster
                cluster.add(thyroid);
                //updating the cluster
                clusters.put(cent, cluster);
            }
            //re calculate the new centroids :
            newCentroids();
            //see if the final state has changed : (to stop if its the case !)
            stop = lastStateClusters.equals(clusters);
            i++;
        }
        return clusters;
    }

    //////////////////////////////////////////////////////////////////
    // EVALUATION METRICS
    //////////////////////////////////////////////////////////////
    public double precision(int classe, int cluster) {
        //
        ArrayList<Thyroid> classList = dataset.getThyroidsClass(classe);
        int nij = 0;
        for (Thyroid t : clusters.get(cluster)) {
            if (classList.contains(t))
                nij++;
        }
        return ((double) nij) / clusters.get(cluster).size();
    }

    public double recall(int classe, int cluster) {
        //
        ArrayList<Thyroid> classList = dataset.getThyroidsClass(classe);
        int nij = 0;
        for (Thyroid t : clusters.get(cluster)) {
            if (classList.contains(t))
                nij++;
        }

        return ((double) nij) / classList.size();
    }

    public double fmeasure(int classe, int cluster) {
        double p = precision(classe, cluster);
        double r = recall(classe, cluster);

        if (p + r == 0) return 0;
        return ((double) (2 * p * r)) / (p + r);
    }

    public double maxfmeasure(int classe) {
        double maxf = 0;
        for (Map.Entry<Integer, ArrayList<Thyroid>> cluster : clusters.entrySet()) {
            double f = fmeasure(classe, cluster.getKey());
            if (f > maxf)
                maxf = f;
        }
        return maxf;
    }

    public double fmeasure() {
        //get the  max𝑗(𝐹(𝑖,𝑗))
        double f1 = maxfmeasure(1);
        double f2 = maxfmeasure(2);
        double f3 = maxfmeasure(3);

        return (Data.c1 * f1 + Data.c2 * f2 + Data.c3 * f3) / dataset.getLenght();
    }

    public double emeasure() {
        double dist1 = 0;
        double dist2 = 0;
        double dist3 = 0;
        //Σ𝐷(𝑥𝑛,𝑐𝑘)
        for (Thyroid thyroid : dataset.getDataset()) {

            if (thyroid.getClasse() == 1) {
                dist1 = dist1 + centroids.get(1).euclideinDistance(thyroid);
            }

            if (thyroid.getClasse() == 2) {
                dist2 = dist2 + centroids.get(2).euclideinDistance(thyroid);
            }

            if (centroids.size() == 3 && thyroid.getClasse() == 3) {
                dist3 = dist3 + centroids.get(3).euclideinDistance(thyroid);
            }
        }
        return dist1 + dist2 + dist3;
    }

    public void experiments(int maxK, int maxIter, int nbrOfTries) {

        double bestE = Double.MAX_VALUE;
        int bestKE = 2;
        double currentE = 0;
        ArrayList<String> results = new ArrayList<>();

        double bestF = 0;
        double currentF = 0;
        int bestKF = 2;

        System.out.println("---------------------------------------------------------------");
        System.out.println("Experiments");
        System.out.println("---------------------------------------------------------------");

        for (int k = 2; k <= maxK; k++) {

            for (int tries = 1; tries <= nbrOfTries; tries++) {

                System.out.println("Nomber of clusters : " + k);
                results.add("Nomber of clusters : " + k);

                System.out.println("This the " + tries + " try");
                fit(maxIter, k);
                //measurements :
                currentE = emeasure();
                currentF = fmeasure();

                System.out.println("F measure : " + currentF);
                results.add("F measure : " + currentF);

                System.out.println("E measure : " + currentE);
                results.add("E measure : " + currentE);

                if (bestE > currentE) {
                    bestE = currentE;
                    bestKE = k;
                }

                results.add("\n\n\n");

                if (bestF < currentF) {
                    bestF = currentF;
                    bestKF = k;
                }
                System.out.println("---------------------------------------------------------------");
            }
        }
        System.out.println("---------------------------------------------------------------");
        System.out.println("--------------------- END -------------------------------");
        results.add("\n\n");
        results.add("F measure : " + bestF);
        System.out.println("F measure : " + bestF);
        System.out.println("Obtained with K = " + bestKF);
        results.add("Obtained with K = " + bestKF);
        results.add("E measure : " + bestE);
        results.add("Obtained with K = " + bestKE);
        System.out.println("E measure : " + bestE);
        System.out.println("Obtained with K = " + bestKE);

        //saving the results in the file
        try {

            Path out = Paths.get("KmeansResults.txt");
            Files.write(out, results, Charset.defaultCharset());

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    ///////////////////////////////////////////////////////////////
    // GA TO GENERATE THE INITIAL CENTROIDS
    //////////////////////////////////////////////////////////////

    public void assignIndividualToCentroids(Individual2 gaCentroids) {
        int classe = 1;
        for (Thyroid centroid : gaCentroids.getArray()) {
            centroids.put(classe, centroid);
            classe++;
        }
    }


    public HashMap<Integer, ArrayList<Thyroid>> fit(int maxIter, int k, int populationLength,
                                                    double mutationRate, int maxIterGA) {

        GA2 ga = new GA2(dataset, k, populationLength, mutationRate);
        //generate initial centroid :
        Individual2 gaCentroids = ga.fit(maxIterGA);
        System.out.println("the k "+k);
        System.out.println("the ga centroids lenght is  "+gaCentroids.getArray().size());
        assignIndividualToCentroids(gaCentroids);

        System.out.println("the ga centroids lenght is  "+centroids.size());


        boolean stop = false;
        int i = 0;
        while (!stop && i < maxIter) {
            //while (i < maxIter){
            //System.out.println("iteration "+i);
            HashMap<Integer, ArrayList<Thyroid>> lastStateClusters = (HashMap) clusters.clone();
            //clean the clusters
            cleanClusters();
            //affecter chaque instance au cluster le plus similaire ;
            for (Thyroid thyroid : dataset.getDataset()) {
                //System.out.println("the inner loop ");
                //calculate the nearest centroid class
                int cent = assignToNearestCentroid(thyroid);
                //getting the cluster k
                ArrayList<Thyroid> cluster = new ArrayList<Thyroid>();
                if (clusters.get(cent) != null)
                    cluster = clusters.get(cent);
                //assing the thyroid to cluster
                cluster.add(thyroid);
                //updating the cluster
                clusters.put(cent, cluster);
            }
            //re calculate the new centroids :
            newCentroids();
            System.out.println("centroids lenght "+centroids.size());
            //see if the final state has changed : (to stop if its the case !)
            stop = lastStateClusters.equals(clusters);
            i++;
        }
//        System.out.println("the centroids "+centroids.toString());
//        System.out.println("the clusters  "+clusters.toString());
        return clusters;
    }

    public void experiments(int maxK, int maxIter, int populationLength,
                            double mutationRate, int maxIterGA) {

        double bestE = Double.MAX_VALUE;
        int bestKE = 2;
        double currentE = 0;
        ArrayList<String> results = new ArrayList<>();
        double bestF = 0;
        double currentF = 0;
        int bestKF = 2;

        double t1 = System.currentTimeMillis();
        System.out.println("---------------------------------------------------------------");
        System.out.println("Experiments");
        System.out.println("---------------------------------------------------------------");

        for (int k = 2; k <= maxK; k++) {

            System.out.println("Nomber of clusters : " + k);
            results.add("Nomber of clusters : " + k);
            fit(maxIter, k, populationLength, mutationRate, maxIterGA);
            //measurements :
            currentE = emeasure();
            currentF = fmeasure();

            System.out.println("F measure : " + currentF);
            results.add("F measure : " + currentF);
            System.out.println("E measure : " + currentE);
            results.add("E measure : " + currentE);

            if (bestE > currentE) {
                bestE = currentE;
                bestKE = k;
            }

            if (bestF < currentF) {
                bestF = currentF;
                bestKF = k;
            }
            results.add("\n");
            System.out.println("---------------------------------------------------------------");
        }
        System.out.println("---------------------------------------------------------------");
        System.out.println("--------------------- END -------------------------------");
        double t2 = System.currentTimeMillis();
        results.add("\n\n\n");
        System.out.println("F measure : " + bestF);
        results.add("F measure : " + bestF);
        System.out.println("Obtained with K = " + bestKF);
        results.add("Obtained with K = " + bestKF);
        System.out.println("E measure : " + bestE);
        results.add("E measure : " + bestE);
        System.out.println("Obtained with K = " + bestKE);
        results.add("Obtained with K = " + bestKE);
        System.out.println("Execution time = " + (t2 - t1));
        results.add("Execution time = " + (t2 - t1));

        //saving the results in the file
        try {

            Path out = Paths.get("GAKmeansResults.txt");
            Files.write(out, results, Charset.defaultCharset());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
