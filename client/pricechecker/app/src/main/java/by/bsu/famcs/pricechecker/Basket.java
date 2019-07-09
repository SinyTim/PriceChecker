package by.bsu.famcs.pricechecker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Basket {
    private List<ProductInfoData> listProducts;

    public Basket(){
        listProducts = new ArrayList<>();
    }

    public List<ProductInfoData> getListProducts() {
        return listProducts;
    }

    public void addProduct(ProductInfoData add){
        listProducts.add(add);
    }

    public void notifySums(){
        float totalSum = 0.0f;
        for (ProductInfoData elem: listProducts){
            totalSum += elem.getAmount() * elem.getPrice();
        }
        if(ClassesRef.basketActivity != null) {
            ClassesRef.basketActivity.changeTotalSum("Итоговая сумма: " + new DecimalFormat("#0.00").format(totalSum));
        }
        if(ClassesRef.productInfoActivity != null) {
            ClassesRef.productInfoActivity.changeTotalSum("Итоговая сумма: " + new DecimalFormat("#0.00").format(totalSum));
        }
    }
}
