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
        generateIteration1(); // step 2-6

        



        
        

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

    static ArrayList<Integer> generateRandomCentroids(){
        ArrayList<Integer> result = new ArrayList<Integer>();
        int max = 200 , min = 1;
        for(int i = 0 ;  i < K ; i++){
            int random_int = (int)(Math.random() * (max - min + 1) + min);
            result.add(random_int) ;
        }
        return result;
    }

    private static int getManhattanDistance(Product product, Product product2) {
        int distance = 0 ;
        for(int i = 0 ; i < 31 ; i++){
            distance += (int) Math.abs(product.salesQuantities[i] - product2.salesQuantities[i]);
        }
        return distance ;
    }

    private static void generateIteration1() throws IOException {
        ArrayList<Integer> initialCentroids = generateRandomCentroids();
        ArrayList<Cluster> clusters = new ArrayList<>();

        // Make K clusters, add these centroids to them
        for(int i = 0 ; i < K ; i++){
            clusters.add(new Cluster());
            clusters.get(i).setClusterCentroid(productsData.get(initialCentroids.get(i) - 1));
        }

        // Loop through all products and assign them to nearest centroid
        for(int i = 0 ; i < 200; i++){
            int minimumDistance = Integer.MAX_VALUE ;
            int clusterIndex = -1 ;
            for(int j = 0 ; j < K ; j++){
                int ManhattanDistance = getManhattanDistance(productsData.get(initialCentroids.get(j) - 1) , productsData.get(i)) ;
                if(ManhattanDistance < minimumDistance){
                    minimumDistance = ManhattanDistance ;
                    clusterIndex = j ;
                }
            }
            // Add the product to the closest cluster, even if identical to the centroid
            clusters.get(clusterIndex).clusterMembers.add(productsData.get(i));
        }
        writeIterationToFile(1, clusters);

    }

    private static void writeIterationToFile(int iterationNumber, ArrayList<Cluster> clusters) throws IOException {
        /* Till here you prepared iteration 1, you should write it to a file
           One way is to make 2 files for each iteration :
                1- Containing centroids data (cent1:13,3,4,56,6,....)
                2- Containing clusters data (clus1:p1,p2,p3,....)
           
           Another way is like this (cent1:13,3,4,56,6|p1,p2,p3)
        */
        BufferedWriter bw = new BufferedWriter(new FileWriter("iterations/i" + iterationNumber  + ".txt")) ;
        for(int i = 0 ; i < K ; i++){
            bw.write(clusters.get(i).toString());
            System.out.println(clusters.get(i).toString());
        }
        bw.close();
    }

}