package by.bsu.famcs.pricechecker;

// класс костыль

public class ClassesRef {
    protected static MainActivity mainActivity;
    protected static ProductInfoActivity productInfoActivity;
    protected static BasketActivity basketActivity;
    protected static Basket basket;

    public static String getFilePath(){
        return ClassesRef.mainActivity.getFilesDir().getPath() + "/basket.json";
    }
}
