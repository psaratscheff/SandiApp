package cl.saratscheff.sandiapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class PopUpMapMenu extends DialogFragment {
    //private TextView mDescription = null;
    private Button mBtnPost;
    private ImageView mImgPost = null;
    private String markerID = "";
    private String title = "";
    private String description = "";
    private String date = "";
    private String creator = "";
    private String image = "";
    private FrameLayout previewLayout = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pop_up_map_menu, container);
        mBtnPost = (Button) view.findViewById(R.id.btn_view_post);
        //mDescription = (TextView) view.findViewById(R.id.lbl_description);
        mImgPost = (ImageView) view.findViewById(R.id.image_post);
        previewLayout = (FrameLayout) view.findViewById(R.id.popup_preview_layout);
        //view.setBackgroundColor(Color.LTGRAY);
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SandiApp/");

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("SandiApp", "failed to create directory");
        }

        mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SandiApp/" + markerID + ".jpg");

        String path = mediaStorageDir.getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        if (bitmap != null)
        {
            mImgPost.setImageBitmap(bitmap);
            updatePopUpView();
        }else {

            if (image != "" | markerID != "") {
                setImage(image, markerID);
                updatePopUpView();
            }

        }

            /*
            Bitmap img = decode(image);
            if (img != null) {
                mImgPost.setImageBitmap(img);
                FileOutputStream out = null;
                String strMyImagePath = mediaStorageDir.getPath();
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mediaStorageDir);
                    img.compress(Bitmap.CompressFormat.PNG, 70, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }else
            {
                String error = "";

            }
        }*/


        //Se abre el ComplaintActivity al presionar el boton o Imagen
        mImgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Abrir vista del post
                Intent myIntent = new Intent(v.getContext(), ComplaintActivity.class);
                myIntent.putExtra("markerID", markerID); // No puedo pasar la imagen por su tamaño!!
                myIntent.putExtra("title", title); // Paso el titulo para colocar en el "Header"
                PopUpMapMenu.this.startActivity(myIntent);
            }
        });
        mBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Abrir vista del post
                Intent myIntent = new Intent(v.getContext(), ComplaintActivity.class);
                myIntent.putExtra("markerID", markerID); // No puedo pasar la imagen por su tamaño!!
                myIntent.putExtra("title", title); // Paso el titulo para colocar en el "Header"
                PopUpMapMenu.this.startActivity(myIntent);
            }
        });
        //getDialog().setTitle(title);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }




    public void setMarkerID(String ID){
        this.markerID = ID;
    }

    public void setTitle(String Title){
        this.title = Title;
    }

    public void setDescription(String Description){
        this.description = Description;
        //updateDescription();
    }

    public void setDate(String Date){
        this.date = Date;
        //updateDescription();
    }

    public void setCreator(String Creator) {
        this.creator = Creator;
        //updateDescription();
    }

    public void setImage(String Image, String markerID) {
        this.image = Image;
        if(mImgPost != null) {

            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SandiApp/" + markerID + ".jpg");
            String path = mediaStorageDir.getAbsolutePath();
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                mImgPost.setImageBitmap(bitmap);
                updatePopUpView();
            } else {
                Bitmap img = decode(image);
                mImgPost.setImageBitmap(img);
                updatePopUpView();

                FileOutputStream out = null;
                String strMyImagePath = mediaStorageDir.getAbsolutePath();
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mediaStorageDir);
                    img.compress(Bitmap.CompressFormat.PNG, 70, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap decode(String imageFile)
    {
        try {
            byte[] imageAsBytes = Base64.decode(imageFile, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            return bmp;
        }catch (Exception e)
        {
            return null;
        }
    }

    private void updatePopUpView(){
        mImgPost.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        mBtnPost.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        previewLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                700));

    }
    /*
    private void updateDescription(){
        if(mDescription != null){
            //mDescription.setText(date + " por " + creator + "\n\n" + description);
            mDescription.setText(creator);
        }
    }
    */
}
