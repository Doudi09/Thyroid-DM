import Data.*;
import GA.GA2;
import KMeans.Kmeans;
import java.util.ArrayList;
import java.util.HashMap;


public class Main {


    public static void main(String[] args) {

        /*
        //GA --- expirements to get the best parameters
        Data dataset = new Data();
        GA2 ga = new GA2(dataset, 3, 4, 0.2);
        double t1 = System.currentTimeMillis();
        ga.expirements(50, 200, 3);
        double t2 = System.currentTimeMillis();
        System.out.println("\n\n------------------------------------------");
        System.out.println("the time exec : "+(t2-t1)/1000+" sec ");
        */




        // These are the best parameters of GA =
        // Nombre d’itérations :160
        //•	Taille de la population : 6
        //•	Taux de mutation : 0.5
//        HashMap<Integer, ArrayList<Thyroid>> clusters = kmeans.fit(500, 5, 6,
//                0.5, 160);

        ///////////////////////////////////////////////////////////////////////////////////////////
        //  GA + Kmeans
        Data dataset = new Data();
        Kmeans kmeans = new Kmeans(dataset);
        kmeans.experiments(20, 500, 1);



        //
//        // calling fit kmeans with no GA :
//        HashMap<Integer, ArrayList<Thyroid>> clusters = kmeans.fit(500, 3);
//
//        for (int k=1; k<= kmeans.centroids.size(); k++){
//            System.out.println("-------------------------- CLUSTER ----------------------------");
//            System.out.println(k);
//            System.out.println("Centroid : "+kmeans.centroids.get(k).toString());
//            if (clusters.containsKey(k)){
//                String members = String.join(", ", kmeans.clusters.get(k).toString());
//                System.out.println("the cluster size = "+kmeans.clusters.get(k).size());
//                System.out.print(members);
//            }else
//                System.out.print("Empty cluster !");
//
//
//            System.out.println();
//            System.out.println();
//        }
//
//
//        System.out.println(kmeans.fmeasure());
//        System.out.println(kmeans.emeasure());

//        clusters.forEach((key, value) -> {
//            System.out.println("-------------------------- CLUSTER ----------------------------");
//            // Sorting the coordinates to see the most significant tags first.
//            System.out.println(key);
//            String members = String.join(", ", value.toString());
//            System.out.println("the cluster size = "+value.size());
//            System.out.print(members);
//
//            System.out.println();
//            System.out.println();
//        });


        /*
        ///////////////////////////////////////////
        // RUN Kmeans without GA :
        Kmeans kmeans = new Kmeans(dataset);

        HashMap<Integer, ArrayList<Thyroid>> clusters = kmeans.fit(500, 3);
        clusters.forEach((key, value) -> {
            System.out.println("-------------------------- CLUSTER ----------------------------");
            // Sorting the coordinates to see the most significant tags first.
            System.out.println(key);
            String members = String.join(", ", value.toString());
            System.out.println("the cluster size = "+value.size());
            System.out.print(members);

            System.out.println();
            System.out.println();
        });

        System.out.println(kmeans.fmeasure());
        System.out.println(kmeans.emeasure());

         */

    }

}
