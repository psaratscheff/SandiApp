package cl.saratscheff.sandiapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.TextView;


public class PopUpMapMenu extends DialogFragment {
    private TextView mDescription;
    private Button mBtnPost;
    private String title = "";
    private String description = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pop_up_map_menu, container);
        mBtnPost = (Button) view.findViewById(R.id.btn_view_post);
        mDescription = (TextView) view.findViewById(R.id.lbl_description);

        mBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Abrir vista del post
            }
        });
        getDialog().setTitle(title);
        mDescription.setText(description);

        return view;
    }

    public void setTitle(String Title){
        this.title = Title;
    }

    public void setDescription(String Description){
        this.description = Description;
    }

}
