import java.util.ArrayList;

public class Cluster {
    public Product centroid = new Product() ;
    public ArrayList<Product> clusterMembers ;

    public Cluster(){
        clusterMembers = new ArrayList<>();
    }

    public Cluster(Product centroid, ArrayList<Product> members ){
        setClusterCentroid(centroid);
        setClusterMembers(members);
    }

    public void setClusterCentroid(Product centroid){
        centroid.CopyTo(this.centroid) ;
    }

    public void setClusterMembers(ArrayList<Product> members){
        if(this.clusterMembers == null){
            this.clusterMembers = new ArrayList<>();
        }    
        else{
            this.clusterMembers.clear();
        }
        this.clusterMembers.addAll(members);
    }

    @Override
    public String toString(){
        String output = this.centroid.toString() + '|';
        for(int i = 0; i < clusterMembers.size() ; i++){
            output = output + clusterMembers.get(i).productName ;
            if((i+1) != clusterMembers.size()){
                output = output + "," ;
            }
        }
        output = output + "\n" ;
        return output ;
    }

}
