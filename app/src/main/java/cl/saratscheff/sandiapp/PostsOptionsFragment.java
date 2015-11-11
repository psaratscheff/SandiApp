package cl.saratscheff.sandiapp;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.client.Firebase;


public class PostsOptionsFragment extends DialogFragment {

    Button btDeletePost;
    Button btWatchPost;
    String postID = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_posts_options, container);
        btDeletePost = (Button) view.findViewById(R.id.buttonDeletePost);
        btWatchPost = (Button) view.findViewById(R.id.buttonWatchPost);

        btWatchPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost(postID);
                dismiss();
            }
        });
        getDialog().setTitle("Opciones");

        return view;
    }

    public void setPostID(String id){
        this.postID = id;
    }

    public void deletePost(String id){
        Firebase mFire;
        mFire = new Firebase("https://sizzling-heat-8397.firebaseio.com/markers/"+id);
        mFire.setValue(null);
        mFire = new Firebase("https://sizzling-heat-8397.firebaseio.com/images/"+id);
        mFire.setValue(null);
        mFire = new Firebase("https://sizzling-heat-8397.firebaseio.com/hd-images/"+id);
        mFire.setValue(null);
    }

}
