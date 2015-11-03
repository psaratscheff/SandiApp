package cl.saratscheff.sandiapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class PopUpMapMenu extends DialogFragment {
    private TextView mDescription;
    private Button mBtnPost;
    private ImageView mImgPost;
    private String title = "";
    private String description = "";
    private String date = "";
    private String creator = "";
    private String image = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        System.out.println(title);
        System.out.println(description);
        System.out.println(date);
        System.out.println(creator);
        System.out.println(image);
        View view = inflater.inflate(R.layout.fragment_pop_up_map_menu, container);
        mBtnPost = (Button) view.findViewById(R.id.btn_view_post);
        mDescription = (TextView) view.findViewById(R.id.lbl_description);
        mImgPost = (ImageView) view.findViewById(R.id.image_post);

        mBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Abrir vista del post
            }
        });
        getDialog().setTitle(title);
        //Bitmap img = decode(image);
        //mImgPost.setImageBitmap(img);
        mDescription.setText(date + ": " + creator + "\n" + description);

        return view;
    }

    public void setTitle(String Title){
        this.title = Title;
    }

    public void setDescription(String Description){
        this.description = Description;
    }

    public void setDate(String Date){
        this.date = Date;
    }

    public void setCreator(String Creator) {
        this.creator = Creator;
    }

    public void setImage(String Image) {
        this.image = Image;
    }

    private Bitmap decode(String imageFile)
    {
        byte[] imageAsBytes = Base64.decode(imageFile, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        return  bmp;
    }
}
