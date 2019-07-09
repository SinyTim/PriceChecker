package by.bsu.famcs.pricechecker;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

//костыли на notify и запись в файл

public class Basket {
    private List<ProductInfoData> listProducts;

    public Basket(){
        listProducts = new ArrayList<>();
    }

    public void remove(int position){
        listProducts.remove(position);
    }

    public int size(){
        return listProducts.size();
    }

    public void addProduct(ProductInfoData add){
        listProducts.add(add);
    }

    public ProductInfoData get(int index){
        return listProducts.get(index);
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
        writeFile();
    }

    private void writeFile(){
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(ClassesRef.getFilePath()));
            fileOutputStream.write(jsonString.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
