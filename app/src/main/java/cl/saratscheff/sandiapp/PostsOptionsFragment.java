package cl.saratscheff.sandiapp;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.model.LatLng;


public class PostsOptionsFragment extends DialogFragment {

    Button btDeletePost;
    Button btWatchPost;
    String postID = "";
    Double latitude = 0.0;
    Double longitude = 0.0;
    PostFragment parent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_posts_options, container, false);

        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        getDialog().getWindow().setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
        //p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        //p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
        //p.x = 200;
        getDialog().getWindow().setAttributes(p);
        getDialog().setTitle("Opciones");

        btDeletePost = (Button) view.findViewById(R.id.buttonDeletePost);
        btWatchPost = (Button) view.findViewById(R.id.buttonWatchPost);

        btWatchPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parent != null){
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.remove(parent);
                    transaction.commit();
                    getActivity().setTitle(R.string.title_activity_maps);
                    ((MapsActivity)getActivity()).getNavigationView().getMenu().getItem(0).setChecked(true);
                    ((MapsActivity)getActivity()).focusCamera(new LatLng(latitude, longitude), 13);
                    parent = null;
                    dismiss();
                }
            }
        });

        btDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost(postID);
                dismiss();
            }
        });

        return view;
    }

    public void setPostID(String id){
        this.postID = id;
    }

    public void setLocation(Double lat, Double lon){
        latitude = lat;
        longitude = lon;
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

    public void setParent(PostFragment prt){
        this.parent = prt;
    }
}
