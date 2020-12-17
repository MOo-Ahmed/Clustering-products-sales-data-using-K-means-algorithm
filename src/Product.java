class Product {
    String productName ;
    double salesQuantities[] = new double[31] ;

    public Product(){}

    public Product(String name, double sales[]){
        this.productName = name ;
        this.salesQuantities = sales ;
    }

    public void CopyTo(Product p){
        p.productName = productName ;
        p.salesQuantities = salesQuantities ;
    }

    @Override
    public String toString(){
        String output =  productName + ":" ;
        for(int i = 0 ; i < 31 ; i++){
            output = output + salesQuantities[i] ;
            if((i+1) == 31){
                break ;
            }
            else    output = output + "," ;
        }
        return output ;
    }

    public String printMe(){
        String output =  "<" + productName + ">\n" ;
        for(int i = 0 ; i < 31 ; i++){
            output = output + salesQuantities[i] ;
            if((i+1) % 8 == 0 && i != 0){
                output = output + "\n" ;
            }
            else    output = output + "\t" ;
        }
        return output ;
    }
}