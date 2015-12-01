package cl.saratscheff.sandiapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class PostFragment extends Fragment implements ListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Firebase mFire;
    private ArrayList<String> myPosts = new ArrayList<>();
    private Context context;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;


    // TODO: Rename and change types of parameters
    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PostFragment() {
    }

    public PostFragment setContext(Context cont){
        this.context = cont;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mFire = new Firebase("https://sizzling-heat-8397.firebaseio.com/markers");
        mFire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                myPosts = new ArrayList<String>();
                if (snapshot.hasChildren()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Post newPost = new Post();

                        if(child.child("latitude").exists() && child.child("longitude").exists()){
                            newPost.latitude = child.child("latitude").getValue().toString();
                            newPost.longitude = child.child("longitude").getValue().toString();
                        }

                        if(child.child("title").exists()){
                            newPost.title = child.child("title").getValue().toString();
                        }

                        if(child.child("description").exists()){
                            newPost.description = child.child("description").getValue().toString();
                        }

                        if(child.child("date").exists()){
                            newPost.date = child.child("date").getValue().toString();
                        }

                        if(child.child("creator").exists()){
                            newPost.creator = child.child("creator").getValue().toString();
                        }

                        newPost.id = child.getKey();

                        if(newPost.creator.equals(LoginActivity.userID)) {
                            //((PostsAdapter) mListView.getAdapter()).add(newPost);
                            //mListView.setAdapter((PostsAdapter)mListView.getAdapter());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        mFire.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post newPost = new Post();

                if(dataSnapshot.child("latitude").exists() && dataSnapshot.child("longitude").exists()){
                    newPost.latitude = dataSnapshot.child("latitude").getValue().toString();
                    newPost.longitude = dataSnapshot.child("longitude").getValue().toString();
                }

                if(dataSnapshot.child("title").exists()){
                    newPost.title = dataSnapshot.child("title").getValue().toString();
                }

                if(dataSnapshot.child("description").exists()){
                    newPost.description = dataSnapshot.child("description").getValue().toString();
                }

                if(dataSnapshot.child("date").exists()){
                    newPost.date = dataSnapshot.child("date").getValue().toString();
                }

                if(dataSnapshot.child("creator").exists()){
                    newPost.creator = dataSnapshot.child("creator").getValue().toString();
                }

                newPost.id = dataSnapshot.getKey();

                if(newPost.creator.equals(LoginActivity.userID)) {
                    ((PostsAdapter) mListView.getAdapter()).add(newPost);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Post newPost = new Post();

                if(dataSnapshot.child("latitude").exists() && dataSnapshot.child("longitude").exists()){
                    newPost.latitude = dataSnapshot.child("latitude").getValue().toString();
                    newPost.longitude = dataSnapshot.child("longitude").getValue().toString();
                }

                if(dataSnapshot.child("title").exists()){
                    newPost.title = dataSnapshot.child("title").getValue().toString();
                }

                if(dataSnapshot.child("description").exists()){
                    newPost.description = dataSnapshot.child("description").getValue().toString();
                }

                if(dataSnapshot.child("date").exists()){
                    newPost.date = dataSnapshot.child("date").getValue().toString();
                }

                if(dataSnapshot.child("creator").exists()){
                    newPost.creator = dataSnapshot.child("creator").getValue().toString();
                }

                newPost.id = dataSnapshot.getKey();

                if(newPost.creator.equals(LoginActivity.userID)) {
                    for(int i=0; i<((PostsAdapter) mListView.getAdapter()).getCount(); i++){
                        Post p = (Post)(((PostsAdapter) mListView.getAdapter()).getItem(i));
                        if(p.id.equals(newPost.id)){
                            ((PostsAdapter) mListView.getAdapter()).remove(p.id);
                            ((PostsAdapter) mListView.getAdapter()).add(newPost);
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String id = dataSnapshot.getKey();
                ((PostsAdapter) mListView.getAdapter()).remove(id);
                mListView.setAdapter((PostsAdapter)mListView.getAdapter());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(new PostsAdapter(context));

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {

            PostsOptionsFragment pof = new PostsOptionsFragment();
            Post pos = (Post)((PostsAdapter) mListView.getAdapter()).getItem(position);
            pof.setPostID(pos.id);
            pof.setLocation(Double.parseDouble(pos.latitude), Double.parseDouble(pos.longitude));
            pof.setParent(this);
            pof.show(getFragmentManager(), "fragment_posts_options");
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }


}





class PostsAdapter extends BaseAdapter{

    Context context;
    ArrayList<Post> data;
    private static LayoutInflater inflater = null;

    public PostsAdapter(Context context) {
        this.context = context;
        data = new ArrayList<Post>();
        if(context!= null){
            inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }
    }

    public void add(Post post) {
        boolean shouldAdd = true;
        for(Post p:data){
            if(p.id == post.id){
                shouldAdd = false;
                break;
            }
        }
        if(shouldAdd){
            data.add(0, post);
            notifyDataSetChanged();
        }
    }

    public void remove(String id){
        for(Post p:data){
            if(p.id.equals(id)){
                data.remove(p);
                notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row_mypost, parent, false);
        TextView textViewTitle = (TextView) vi.findViewById(R.id.textViewPostTitle);
        TextView textViewDate = (TextView) vi.findViewById(R.id.textViewPostDate);
        ImageView roundImage = (ImageView) vi.findViewById(R.id.post_round_image);

        if(position<data.size()){
            textViewTitle.setText(data.get(position).title);
            textViewDate.setText(data.get(position).date);
        }

        return vi;
    }
}

class Post{
    public String title;
    public String description;
    public String date;
    public String latitude;
    public String longitude;
    public String creator;
    public String id;

    public Post() { }
}
