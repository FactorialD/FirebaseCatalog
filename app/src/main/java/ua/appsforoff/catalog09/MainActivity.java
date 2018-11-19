package ua.appsforoff.catalog09;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    final String ATTRIBUTE_NAME_IMAGE = "Image";
    final String ATTRIBUTE_NAME_TEXT1 = "Text1";
    final String ATTRIBUTE_NAME_TEXT2 = "Text2";
    final String ATTRIBUTE_NAME_BUTTON = "Button";
    final String ATTRIBUTE_NAME_RATE = "Rate";
    final String ATTRIBUTE_NAME_REF = "Ref";

    ListView catalog;

    ArrayList<CatalogListItem> catalogList;
    ArrayList<CatalogListItem> sortedRateCatalogList;

    String header;

    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().hide();
        }

        buyCheck();

        //setPrivacyButton();

        database = FirebaseDatabase.getInstance();

        loadHeader();

        catalogList = new ArrayList<>();
        sortedRateCatalogList = new ArrayList<>();

        //сбор данных с БД
        DatabaseReference myRef = database.getReference("CatalogItems");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    //Log.v("Dima","enter loop in evListonDataChange");

                    catalogList.add(new CatalogListItem(
                            userSnapshot.child("Image").getValue().toString(),
                            userSnapshot.child("Text1").getValue().toString(),
                            userSnapshot.child("Text2").getValue().toString(),
                            userSnapshot.child("Button").getValue().toString(),
                            userSnapshot.child("Rate").getValue().toString(),
                            userSnapshot.child("Ref").getValue().toString()
                    ));//image, String text1, String text2, String button, String rate, String ref

                    //catalogList.get(catalogList.size()-1).printLog();
                }

                 Comparator<CatalogListItem> compareByRate = new Comparator<CatalogListItem>() {
                    @Override
                    public int compare(CatalogListItem lhs, CatalogListItem rhs) {
                        return parseInt(lhs.Rate) - parseInt(rhs.Rate);
                    }
                };
                Collections.sort(catalogList, Collections.reverseOrder(compareByRate));


                // РАБОТА С КАТАЛОГОМ
                Log.v("Dima", "Catalog list contains" + catalogList.size() + "elements:");
                for (CatalogListItem i: catalogList
                        ) {
                    i.printLog();
                }

                // упаковываем данные в понятную для адаптера структуру
                ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(catalogList.size());
                Map<String, Object> m;

                Log.v("Dima", "before loop");
                for (int i = 0; i < catalogList.size(); i++) {
                    Log.v( "Dima","loop" + i + " begin");

                    m = new HashMap<String, Object>();
                    m.put(ATTRIBUTE_NAME_IMAGE, catalogList.get(i).Image);
                    m.put(ATTRIBUTE_NAME_TEXT1, catalogList.get(i).Text1);
                    m.put(ATTRIBUTE_NAME_TEXT2, catalogList.get(i).Text2);
                    m.put(ATTRIBUTE_NAME_BUTTON, catalogList.get(i).Button);
                    m.put(ATTRIBUTE_NAME_RATE, getResources().getIdentifier("star" + catalogList.get(i).Rate, "drawable", getPackageName()));
                    m.put(ATTRIBUTE_NAME_REF, catalogList.get(i).Ref);

                    data.add(m);

                    Log.v("Dima" ,"loop" + i + " end");
                }
                Log.v( "Dima", "after loop");

                // массив имен атрибутов, из которых будут читаться данные
                String[] from = { ATTRIBUTE_NAME_IMAGE, ATTRIBUTE_NAME_TEXT1, ATTRIBUTE_NAME_TEXT2, ATTRIBUTE_NAME_RATE, ATTRIBUTE_NAME_REF };
                // массив ID View-компонентов, в которые будут вставлять данные
                int[] to = { R.id.listCatalogImage, R.id.mainText, R.id.secondText, R.id.rateProgressBar, R.id.listCatalogButton};

                // создаем адаптер
                MyAdapter sAdapter = new MyAdapter(getBaseContext(), data, R.layout.list_item,
                        from, to);

                // определяем список и присваиваем ему адаптер
                catalog = (ListView) findViewById(R.id.catalogList);

                View footer = getLayoutInflater().inflate(R.layout.footer, null);
                ((Button)footer.findViewById(R.id.button2)).setOnClickListener(e ->{
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://telegra.ph/Privacy-Policy-10-01-2"));
            e.getContext().startActivity(browserIntent);
                });
                //View footer = (View) findViewById(R.id.footer_constr);
                //View footer = getLayoutInflater().inflate(R.layout.footer, null);

                //View footer = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer, null, false);
                //View footer = (TextView) ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_view, null, false);
//                View footer = findViewById(R.id.footer_layout);
                catalog.addFooterView(footer);
                catalog.setAdapter(sAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            // Failed to read value
                Log.w("Dima", "Failed to read value.", databaseError.toException());
            }
        });

//        catalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                Intent intent;
////                intent = new Intent(AllHeroesActivity.this, HeroMainActivity.class);
////
////                TextView name = (TextView) findViewById(R.id.hero_name);
////                //TextView tv = (TextView) parent.findViewById(R.id.list_item_hero_name);
////                //String name = tv.getText().toString();
////                //Log.v("df", name.getText().toString());
////
////                ////parent.getItemAtPosition(position)
////
////                //intent.putExtra("name", name.getText().toString());
////                intent.putExtra("position", position);
////
////                startActivity(intent); //Запускаем активность
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void buyCheck(){
        //проверка на оплату

        //Log.v("Dima", "in buyCheck");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("OffKey");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.v("Dima", "in onDataChange");
                //Log.v("Dima", dataSnapshot.getValue() + " " + "13");
                //Log.v("Dima", String.valueOf(dataSnapshot.getValue().equals(13)));

                String val = dataSnapshot.getValue().toString();
                if(!val.equals("1357111317")){
                    Log.v("Dima", "in onDataChange->serialWrong");


                    TextView tv = (TextView) findViewById(R.id.headerTextView);
                    tv.setText("Ой!");
                    TextView tv2 = (TextView) findViewById(R.id.headerTextView2);
                    tv2.setText("Похоже, что кто-то не доплатил разработчику или все поломал.");
                    ArrayList<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
                    HashMap<String, Object> m = new HashMap();
                    m.put("Error","Ай-яй-яй");
                    m.put("Err", "мммммммм");
                    arr.add(m);
                    String[] from = {"Error", "Err"};
                    int[] to = {R.id.mainText, R.id.secondText};
                    SimpleAdapter ad = new SimpleAdapter(getApplicationContext(), arr, R.layout.list_item, from, to );
                    ListView lv = (ListView) findViewById(R.id.catalogList);
                    lv.setAdapter(ad);

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("Dima", "in onCancelled->value not exist");
                Log.w("Dima", "Failed to read value.", databaseError.toException());

                TextView tv = (TextView) findViewById(R.id.headerTextView);
                tv.setText("Ой!");
                TextView tv2 = (TextView) findViewById(R.id.headerTextView2);
                tv2.setText("Похоже, что кто-то не доплатил разработчику или все поломал.");
                ArrayList<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
                HashMap<String, Object> m = new HashMap();
                m.put("Error","Ай-яй-яй");
                m.put("Err", "мммммммм");
                arr.add(m);
                String[] from = {"Error", "Err"};
                int[] to = {R.id.mainText, R.id.secondText};
                SimpleAdapter ad = new SimpleAdapter(getApplicationContext(), arr, R.layout.list_item, from, to );
                ListView lv = (ListView) findViewById(R.id.catalogList);
                lv.setAdapter(ad);

            }
        });

    }

//    public void setPrivacyButton(){
//        //privacy policy
//        privacyButton = (Button) findViewById(R.id.privacyButton);
////        privacyButton.setOnClickListener(e -> {
////            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
////            builder.setTitle("Privacy policy!")
////                    .setMessage("This is the privacy policy. This app not use any content protected by copyright. Have fun!")
////                    //.setIcon(R.drawable.ic_android_cat)
////                    .setCancelable(false)
////                    .setNegativeButton("Got it",
////                            new DialogInterface.OnClickListener() {
////                                public void onClick(DialogInterface dialog, int id) {
////                                    dialog.cancel();
////                                }
////                            });
////            AlertDialog alert = builder.create();
////            alert.show();
////        });
//        privacyButton.setOnClickListener(e -> {
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("https://telegra.ph/Privacy-Policy-10-01-2"));
//            e.getContext().startActivity(browserIntent);
//        });
//    }

    public void loadHeader(){
        //РАБОТА С ШАПКОЙ

        ImageView imageBackground = (ImageView) findViewById(R.id.imageBackground);
        imageBackground.setBackgroundColor(Color.BLACK);
        ImageView imageHeader = (ImageView) findViewById(R.id.headerImageView);


        DatabaseReference headerRef = database.getReference("Header");
        headerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                TextView t1 = (TextView) findViewById(R.id.headerTextView);
                t1.setText(dataSnapshot.child("Text1").getValue().toString());
                TextView t2 = (TextView) findViewById(R.id.headerTextView2);
                t2.setText(dataSnapshot.child("Text2").getValue().toString());
                header = dataSnapshot.child("HeaderImage").getValue().toString();

                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                StorageReference headRef = mStorageRef.child(header);

                headRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getApplicationContext()).load(uri).into(imageHeader);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.v("Dima","DownloadFileFailure");
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Dima", "Failed to read value.", error.toException());
            }
        });
    }
}

