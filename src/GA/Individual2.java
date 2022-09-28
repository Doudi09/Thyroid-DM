package GA;

import Data.Data;
import Data.Thyroid;

import java.util.ArrayList;
import java.util.Random;

public class Individual2 {

    ArrayList<Thyroid> solution = new ArrayList<>();
    double fitness;

    public Individual2(ArrayList<Thyroid> solution){
        this.solution = solution;
    }

    public Individual2(int k, Data dataset){

        Random rand = new Random();

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
            solution.add(centoid);
        }
        calculFitness();
    }

    public void calculFitness(){
        //here the fitness is the distance between the centroids
        //we maximize it, to obtain hight intra-class similarity
        double dist = 0;
        for(int i=0; i<solution.size(); i++){
            for (int j=i+1; j<solution.size()-1; j++){
                //calculate the dist :
                dist = dist + solution.get(i).euclideinDistance(solution.get(j));
            }
        }
        fitness = dist;
    }

    public ArrayList<Individual2> crossover(Individual2 solution1){
        //return two results for example : sol1 [c11,c12,c13] sol2 [c21, c22, c23]
        // res1 [c11, c22, c13]    res2[c21, c12, c23]

        ArrayList<Thyroid> result1 = new ArrayList<>();
        ArrayList<Thyroid> result2 = new ArrayList<>();
        int crossoverpoint = (int) (solution.size()/2);

        for(int i=0; i<solution.size(); i++){
            if (i%2 == 0){
                result1.add(solution1.solution.get(i));
                result2.add(solution.get(i));
            }
            else{
                result2.add(solution1.solution.get(i));
                result1.add(solution.get(i));
            }
        }
        ArrayList<Individual2> finalResult = new ArrayList<>();
        finalResult.add(new Individual2(result1));
        finalResult.add(new Individual2(result2));
        return finalResult;
    }

    public void mutation (double mutationRate, Data dataset){
        //the mutation process consist of removing randomly a centroid and replacing it with an random centroid
        // mutation rate == % of mutation in [0,1]
        Random rand = new Random();
        ArrayList<Thyroid> mutated = new ArrayList<>();
        int i = 0;
        for(Thyroid centroid : solution){

            if(rand.nextBoolean() && i < mutationRate * solution.size()/100){
                //generate a random thyroid :
                Random random = new Random();

                Thyroid max = new Thyroid(-1, dataset.max(Data.T3), dataset.max(Data.TYS),
                        dataset.max(Data.TRI), dataset.max(Data.TSH), dataset.max(Data.DTSH));

                Thyroid min = new Thyroid(-1, dataset.min(Data.T3), dataset.min(Data.TYS),
                        dataset.min(Data.TRI), dataset.min(Data.TSH), dataset.min(Data.DTSH));

                mutated.add(new Thyroid(-1, rand.nextFloat() * (max.getT3() - min.getT3()) + min.getT3(),
                        rand.nextFloat() * (max.getTys() - min.getTys()) + min.getTys(),
                        rand.nextFloat() * (max.getTri() - min.getTri()) + min.getTri(),
                        rand.nextFloat() * (max.getTsh() - min.getTsh()) + min.getTsh(),
                        rand.nextFloat() * (max.getDtsh() - min.getDtsh()) + min.getDtsh()));
            }else
                mutated.add(centroid);
        }
        solution = mutated;
    }

    public ArrayList<Thyroid> getArray(){
        return solution;
    }

    public String toString(){
        return solution.toString() + " \nfitness = "+fitness;
    }






}
