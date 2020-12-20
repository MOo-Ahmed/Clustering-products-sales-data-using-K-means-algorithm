import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class ClusteringController {

    private static int K = 0 ;
    static ArrayList<Product> productsData = new ArrayList<Product>();

    public static void performClustering(int k)  throws IOException{
        /*
        1- Read all products from the file
        2- Randomly select k centroids, create k clusters
        3- Loop through all products, pick a product p at a time
        4- Measure Manhattan distnce between p and all centroids
        5- Take the minimum distance and assign it to closest cluster
        6- Write the clusters at current iteration to a file
        7- Calculate the mean for every cluster
        8- Reassign the products based on Manhattan distance again
        9- If the current clusters are the same as previous clusters -> stop
        */

        K = k ;

        // First read the dataset and save it in arraylist
        readProductsFromFile(); // step 1
        ArrayList<Cluster> clusters = generateRandomCentroids();
        clusters = generateNextIteration(1, clusters); // step 2-6

        boolean stop = false ;
        for(int i = 2 ; !stop ; i++){
            // The loop started from 2 because it's preparing iteration 2,3,...
            
            // Make backup of current iteration data
            ArrayList<Cluster> oldClusters = new ArrayList<>();
            for(int a = 0; a < K ; a++){
                oldClusters.add(clusters.get(a).getCopy());
            }
            // Calculate the cluster means
            Product[] clusterMeans = new Product[K];
            for(int j = 0 ; j < K ; j++){
                clusterMeans[j] = calculateClusterMean(clusters.get(j));
                clusterMeans[j].productName = "centroid " + (j+1) ;
                clusterMeans[j].CopyTo(clusters.get(j).centroid);

                clusters.get(j).clusterMembers.clear();
               
                //System.out.println(clusterMeans[j].printMe());
            }
            
            // Reassign the objects 
            clusters = generateNextIteration(i , clusters);
            writeIterationToFile(i, clusters);
            
            // Now compare the current iteration to the previous one

            stop = areIterationsIdentical(clusters, oldClusters);
            if(stop == true){
                System.out.println("The algorithm stopped at iteration " + i);
            }

        }
        showOutliers(clusters);
    }


    static void readProductsFromFile() throws IOException {
        String fileName = "Sales.txt" ;
        int numberOfWeeks = 31, numberOfProducts = 200 ; 
        RandomAccessFile file = new RandomAccessFile(fileName, "r");
        file.seek(0);
        for(int i = 0; i < numberOfProducts ; i++){
            String inputParts[] = file.readLine().split(",");
            String name = inputParts[0] ;
            double members[] = new double[numberOfWeeks];
            for(int j = 1 ; j <= numberOfWeeks ; j++){
                members[j-1] = Integer.parseInt(inputParts[j]);
            }
            Product p = new Product(name, members);
            productsData.add(p); 
        }
        file.close();
    }

    static ArrayList<Cluster> generateRandomCentroids(){
        ArrayList<Integer> result = new ArrayList<Integer>();
        int max = 200 , min = 1;
        for(;  result.size() < K ;){
            int random_int = (int)(Math.random() * (max - min + 1) + min);
            if(result.indexOf(random_int) == -1)    result.add(random_int) ;
        }
        // Make K clusters, add these centroids to them
        ArrayList<Cluster> clusters = new ArrayList<>();
        for(int i = 0 ; i < K ; i++){
            clusters.add(new Cluster());
            clusters.get(i).setClusterCentroid(productsData.get(result.get(i) - 1));
        }
        
        return clusters;
    }

    private static double getManhattanDistance(Product product, Product product2) {
        double distance = 0 ;
        for(int i = 0 ; i < 31 ; i++){
            distance += Math.abs(product.salesQuantities[i] - product2.salesQuantities[i]);
        }
        return distance ;
    }

    private static ArrayList<Cluster> generateNextIteration (int iterationNumber, ArrayList<Cluster> clusters) throws IOException {
        // Loop through all products and assign them to nearest centroid
        for(int i = 0 ; i < 200; i++){
            double minimumDistance = Integer.MAX_VALUE ;
            int clusterIndex = -1 ;
            for(int j = 0 ; j < K ; j++){
                double ManhattanDistance = getManhattanDistance(clusters.get(j).centroid , productsData.get(i)) ;
                if(ManhattanDistance < minimumDistance){
                    minimumDistance = ManhattanDistance ;
                    clusterIndex = j ;
                }
            }
            // Add the product to the closest cluster, even if identical to the centroid
            clusters.get(clusterIndex).clusterMembers.add(productsData.get(i));
        }
        writeIterationToFile(iterationNumber, clusters);
        return clusters;
    }

    private static void writeIterationToFile(int iterationNumber, ArrayList<Cluster> clusters) throws IOException {
        /* Till here you prepared iteration 1, you should write it to a file
           like this (cent1:13,3,4,56,6|p1,p2,p3)
        */
        BufferedWriter bw = new BufferedWriter(new FileWriter("iterations/iteration" + iterationNumber  + ".txt")) ;
        for(int i = 0 ; i < K ; i++){
            bw.write(clusters.get(i).toString());
        }
        bw.close();
        writeIterationSummary(iterationNumber, clusters);
    }

    private static Product calculateClusterMean(Cluster cluster){
        Product mean = new Product();
        
        int n = cluster.clusterMembers.size() ;
        //System.out.println(n);
        for(int i = 0 ; i < n ; i++){
            for(int j = 0 ; j < 31 ; j++){
                //System.out.println("Got here");
                if(i == 0)    mean.salesQuantities[j] = 0 ;
                double value = (cluster.clusterMembers.get(i).salesQuantities[j] * 1.0 / n) ;
                
                mean.salesQuantities[j] +=  value;
            }
        }
        return mean ;
    }

    private static void writeIterationSummary(int iterationNumber, ArrayList<Cluster> clusters) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("iterations/i" + iterationNumber  + ".txt")) ;
        for(int i = 0 ; i < K ; i++){
            String output = "Cluster " + (i+1) + " / " + clusters.get(i).centroid.productName + " / " ;
            for(int j = 0 ; j < clusters.get(i).clusterMembers.size() ; j++)  {
                output += clusters.get(i).clusterMembers.get(j).productName ;
                if((j+1) != clusters.get(i).clusterMembers.size()){
                    output += " , " ;
                }
            }
            bw.write(output + "\n");
        }
        bw.close();
    }

    private static boolean areIterationsIdentical(ArrayList<Cluster> i1, ArrayList<Cluster> i2){
        for(int i = 0 ; i < K ; i++){
            ArrayList<String> C1 = new ArrayList<>(), C2 = new ArrayList<>();
            for(int j = 0 ; j < i1.get(i).clusterMembers.size() ; j++){
                C1.add(i1.get(i).clusterMembers.get(j).productName) ;
            }
            for(int j = 0 ; j < i2.get(i).clusterMembers.size() ; j++){
                C2.add(i2.get(i).clusterMembers.get(j).productName) ;
            }
            if(C1.containsAll(C2) && C2.containsAll(C1) && C1.size() == C2.size()){
                C1.clear();
                C2.clear();
            }
            else {
                return false ;
            }
        }
        return true ;
    }

    private static void showOutliers(ArrayList<Cluster> clusters){
        int secondSmallestIndex = 0 , secondSmallestSize = Integer.MAX_VALUE;
        int SmallestClusterIndex = 0 , smallestSize = Integer.MAX_VALUE;
        for(int i = 0 ; i < clusters.size() ; i++){
            if(clusters.get(i).clusterMembers.size() < smallestSize){
                smallestSize = clusters.get(i).clusterMembers.size();
                SmallestClusterIndex = i ;
            }
            else if(clusters.get(i).clusterMembers.size() < secondSmallestSize ){
                secondSmallestSize = clusters.get(i).clusterMembers.size() ;
                secondSmallestIndex = i ;
            }
        }
        String output = "\nThe outliers can be detected in clusters " 
            + (SmallestClusterIndex+1)  + " and " + (secondSmallestIndex+1);
        System.out.println(output);
        printOutliers(clusters.get(SmallestClusterIndex).clusterMembers, SmallestClusterIndex);
        printOutliers(clusters.get(secondSmallestIndex).clusterMembers, secondSmallestIndex);
        
    }

    private static void printOutliers(ArrayList<Product> members, int idx){
        String output = "\nOutliers in cluster " + (idx+1) + " are : \n" ;
        for(int j = 0 ; j < members.size() ; j++)  {
            output += members.get(j).productName ;
            if((j+1) != members.size()){
                output += "\t" ;
            }
        }
        output += "\n" ;
        for(int j = 0 ; j < members.size() ; j++)  {
            output += members.get(j).printMe() ;
            if((j+1) != members.size()){
                output += "\n" ;
            }
        }
        System.out.println(output + "\n-----------------------------------------------\n");
    }
}