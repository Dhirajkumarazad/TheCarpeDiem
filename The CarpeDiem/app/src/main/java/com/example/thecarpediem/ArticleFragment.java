package com.example.thecarpediem;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArticleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArticleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MyAdapter myAdapter;
    List<String> movieList=new ArrayList<>();
    String category;
    ProgressBar progressBar;

    private OnFragmentInteractionListener mListener;

    public ArticleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArticleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArticleFragment newInstance(String param1, String param2) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview=  inflater.inflate(R.layout.fragment_article, container, false);
        progressBar=rootview.findViewById(R.id.progressbar);

        final RecyclerView rv=rootview.findViewById(R.id.recycler_view);
        SharedPreferences preferences= rootview.getContext().getSharedPreferences("tcofile",Context.MODE_PRIVATE);
        int pos=preferences.getInt("clickedpos",0);

        switch (pos){
            case 0: category="inspiration";
                break;
            case 1: category="love";
                break;
            case 2: category="sad";
                break;
            case 3: category="science fiction";
                break;

        }
        rv.setAdapter(myAdapter);
        LinearLayoutManager lm=new LinearLayoutManager(getActivity());
        rv.setLayoutManager(lm);

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("categories").child(category);
        ref.child("article").child("writings").child("english").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                movieList.clear();
                String name1 = "";

                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot x : dataSnapshot.getChildren()) {
                        name1 = x.getValue().toString();

                        movieList.add(name1);
                    }
                    myAdapter = new MyAdapter(movieList);
                    progressBar.setVisibility(View.INVISIBLE);
                    rv.setAdapter(myAdapter);
                    rv.setTop(0);
                }
                else
                {
                    name1="No record found currently! Try again later.";
                    movieList.add(name1);
                    myAdapter=new MyAdapter(movieList);
                    progressBar.setVisibility(View.INVISIBLE);
                    rv.setAdapter(myAdapter);
                }

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        rv.smoothScrollToPosition(0);
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Database error! Try again later",Toast.LENGTH_LONG).show();

            }
        });

        return rootview;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
