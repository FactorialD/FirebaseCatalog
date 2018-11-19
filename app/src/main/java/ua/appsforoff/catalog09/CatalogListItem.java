package ua.appsforoff.catalog09;

import android.util.Log;

/**
 * Created by Dmitriy on 24.09.2018.
 */

public class CatalogListItem {

    String Image;
    String Text1;
    String Text2;
    String Button;
    String Rate;
    String Ref;

    public CatalogListItem(String image, String text1, String text2, String button, String rate, String ref) {
        this.Image = image;
        this.Text1 = text1;
        this.Text2 = text2;
        this.Button = button;
        this.Rate = rate;
        this.Ref = ref;
    }
    public CatalogListItem(){}

    public void printLog(){
        Log.v("BD",": " + "CatalogListItem:");
        Log.v("BD",": " + Image);
        Log.v("BD",": " + Text1);
        Log.v("BD",": " + Text2);
        Log.v("BD",": " + Button);
        Log.v("BD",": " + Rate);
        Log.v("BD",": " + Ref);
    }

}
