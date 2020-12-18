import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter k : ");
        int k = Integer.parseInt(sc.nextLine());
        ClusteringController.performClustering(k);

        /*
        • You should detect outlier data (if exists).
        • The final output should show k lists of products and show outlier product’s records.
        */
    }
}