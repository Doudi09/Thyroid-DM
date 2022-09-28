package GA;

import Data.Data;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GA2 {


    public ArrayList<Individual2> population = new ArrayList<>(); //each solution represents the centroid of the cluster
    //the fitness function is the distance between each centroid and its cluster
    Data dataset;
    int populationLenght;
    int k;
    double mutationRate;

    public GA2(Data dataset, int k, int populationLenght, double mutationRate) {
        this.dataset = dataset;
        this.k = k;
        this.mutationRate = mutationRate;
        this.populationLenght = populationLenght;
    }

    public void initialPopulation() {

        for (int i = 0; i < populationLenght; i++) {
            population.add(new Individual2(k, dataset)); // the fitness is calculated here !
        }
    }

    public void sortPopulation() {

        Collections.sort(population, new Comparator<Individual2>() {

            public int compare(Individual2 i1, Individual2 i2) {
                if (i1.fitness < i2.fitness) return 1;
                if (i1.fitness > i2.fitness) return -1;
                return 0;
            }
        });
    }

    public void crossover() {
        ArrayList<Individual2> liste = new ArrayList<>();
        for (int i = 0; i < population.size() - 1; i++) {
            liste.addAll(population.get(i).crossover(population.get(i + 1)));
        }
        //replace the individuals in population with new ones except the first one
        int l = population.size();
        for (int i = 1; i < l; i++) {
            population.remove(i);
            population.add(i, liste.get(i - 1));
        }
    }

    public void mutation() {
        //mutate all individuals except of the first one (the fittest)
        for (int i = 1; i < population.size(); i++) {
            population.get(i).mutation(mutationRate, dataset);
        }
    }

    public void calculFitnessPopulation() {

        for (Individual2 i : population) {
            i.calculFitness();
        }
    }

    public Individual2 fit(int iterMax) {
        //System.out.println("the GA algorithm ");
        initialPopulation();

        for (int i = 0; i < iterMax; i++) {
            //System.out.println("iteration " +i);
            //showPopulation();
            sortPopulation();
            crossover();
            mutation();
            calculFitnessPopulation();
        }
        sortPopulation();
        return population.get(0);
    }


    public ArrayList<Individual2> fit(int iterMax, int nbrOfTries) {
        //System.out.println("the GA algorithm ");
        ArrayList<Individual2> allTheResults = new ArrayList<>();
        for (int tries=0; tries<=nbrOfTries; tries++){
            initialPopulation();

            for (int i = 0; i < iterMax; i++) {
                //System.out.println("iteration " +i);
                //showPopulation();
                sortPopulation();
                crossover();
                mutation();
                calculFitnessPopulation();
            }
            sortPopulation();
            allTheResults.add(population.get(0));
        }
        return allTheResults;
    }

    public void showPopulation() {
        System.out.println("\n\n---------------------------------------------------");
        for (Individual2 i : population) {
            System.out.println(i);
        }
    }


    public void expirements(int maxPopLenght, int iterMax, int nbrOfTries) {

        int bestPopulation = 5;
        double bestMutationRate = 0.1;
        int bestIter = 10;
        double bestFitness = 0;

        ArrayList<String> results =new ArrayList<>();
        double t1 = System.currentTimeMillis();

        for (double mutationRateExp = 0.1; mutationRateExp < 0.9; mutationRateExp = mutationRateExp + 0.2) {
            for (int populationExp = 5; populationExp <= maxPopLenght; populationExp = populationExp+10) {
                //for (int iterExp = 10; iterExp <= iterMax; iterExp = iterExp + 10) {
                for (int iterExp = 10; iterExp <= iterMax; iterExp = iterExp + 50) {
                    for (int tries = 1; tries <= nbrOfTries; tries++) {
                        System.out.println("The mutationrate : " + mutationRateExp);
                        results.add("The mutationrate : " + mutationRateExp);
                        System.out.println("The iteration : " + iterExp);
                        results.add("The iteration : " + iterExp);
                        System.out.println("The population size : " + populationExp);
                        results.add("The population size : " + populationExp);
                        //assign the values
                        populationLenght = populationExp;
                        mutationRate = mutationRateExp;
                        //call the fit
                        Individual2 best = fit(iterExp);
                        System.out.println("the fitness "+best.fitness);
                        results.add("the fitness "+best.fitness);
                        results.add("\n ");
                        if (best.fitness > bestFitness) {
                            bestFitness = best.fitness;
                            bestIter = iterExp;
                            bestMutationRate = mutationRateExp;
                            bestPopulation = populationExp;
                        }
                    }
                }
            }
        }
        // the end
        double t2 = System.currentTimeMillis();
        results.add("\n ");
        results.add("\n ");
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("The best fitness : " + bestFitness);
        results.add("The best fitness : " + bestFitness);
        System.out.println("The best iteration: " + bestIter);
        results.add("The best iteration: " + bestIter);
        System.out.println("The best population size : " + bestPopulation);
        results.add("The best population size : " + bestPopulation);
        System.out.println("The best mutation: " + bestMutationRate);
        results.add("The best mutation: " + bestMutationRate);
        System.out.println("Execution time = " + (t2 - t1));
        results.add("Execution time = " + (t2 - t1));
        //saving the results in the file
        try{

            Path out = Paths.get("GAResults.txt");
            Files.write(out,results , Charset.defaultCharset());

        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
