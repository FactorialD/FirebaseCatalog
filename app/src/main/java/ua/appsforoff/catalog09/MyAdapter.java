package ua.appsforoff.catalog09;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Dmitriy on 25.09.2018.
 */

public class MyAdapter extends SimpleAdapter {


    private LayoutInflater inflater;
    private int layout;
    List<Map<String, Object>> catalog;

    ArrayList<Button> buttons;
    ArrayList<ImageView> ivs;
    ImageView ivrate;

    StorageReference mStorageRef;

    int tempPosition = 0;

    public MyAdapter(Context context, List<? extends Map<String, ?>> data,
                     @LayoutRes int resource, String[] from, @IdRes int[] to){
        super(context, data, resource, from, to);
        catalog = (List<Map<String, Object>>) data;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        buttons = new ArrayList<>();
        ivs = new ArrayList<>();
        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    public View getView(final int position, final View convertView, ViewGroup parent) {

        tempPosition = position;


        View view=inflater.inflate(this.layout, parent, false);

        ImageView iv = (ImageView) view.findViewById(R.id.listCatalogImage);
        ivrate = (ImageView) view.findViewById(R.id.rateProgressBar);
        TextView tv1 = (TextView) view.findViewById(R.id.mainText);
        TextView tv2 = (TextView) view.findViewById(R.id.secondText);
        Button bt = (Button) view.findViewById(R.id.listCatalogButton);
        buttons.add(position,bt);
        ivs.add(position,iv);

        tv1.setText(catalog.get(position).get("Text1").toString());
        tv2.setText(catalog.get(position).get("Text2").toString());
        ivrate.setImageResource((Integer) catalog.get(position).get("Rate"));

//        Log.v("Dima","http://" + catalog.get(tempPosition).get("Ref"));
//        Log.v("Dima", "Position:" + position);

        bt.setText("Подробнее");
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < buttons.size(); i++) {
                    if(buttons.get(i) == v){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://" + catalog.get(i).get("Ref")));
                        v.getContext().startActivity(browserIntent);
                    }
                }
            }
        });

        //Работа с картинками
        if (ivs.get(position).getDrawable() == null) {

            StorageReference itemRef = mStorageRef.child(catalog.get(position).get("Image").toString());
            //Log.v("Dima", catalog.get(position).get("Image").toString());

//            File localFile = null;
//            try {
//                localFile = File.createTempFile("images", "jpg");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            itemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.v("Dima", "DownloadFileSuccess");
                    for (int i = 0; i < catalog.size(); i++) {
                        if (uri.toString().contains(catalog.get(i).get("Image").toString())) {
                            Picasso.with(inflater.getContext()).load(uri).into(ivs.get(i));
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.v("Dima", "DownloadFileFailure");
                }
            });
        }

        iv = ivs.get(position);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < ivs.size(); i++) {
                    if(ivs.get(i) == v){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://" + catalog.get(i).get("Ref")));
                        v.getContext().startActivity(browserIntent);
                    }
                }
            }
        });

        StorageReference rateRef = mStorageRef.child("HeaderImage");
        rateRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                          Picasso.with(inflater.getContext()).load(uri).into(ivrate);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                         Log.v("Dima","DownloadFileFailure");
                     }
        });

        return view;
    }

}
