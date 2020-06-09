package com.example.assignment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.Adapter.CustomAdapter;
import com.example.Model.EventList;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainPage extends Fragment {

    private Button btn_addEvent;
    private FragmentManager fragmentManager;
    private FirebaseAuth auth;
    private Context context;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private FirebaseHelper helper;
    private CustomAdapter adapter;
    private ListView listView;
    private long back_pressed;
    private ShimmerFrameLayout shimmer_view;
    private ArrayList<EventList> eventLists = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        fragmentManager = getFragmentManager();
        listView = view.findViewById(R.id.listView);
        shimmer_view = view.findViewById(R.id.shimmer_view);
        btn_addEvent = view.findViewById(R.id.btn_addEvent);
        context = getContext();
        FirebaseApp.initializeApp(context);
        auth = FirebaseAuth.getInstance();

        //show hidden toolbar
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();

        //Initialize FireBase DB
        helper = new FirebaseHelper(db);

        //set data in listView
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Events");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                EventList event = dataSnapshot.getValue(EventList.class);
                eventLists.add(event);
                adapter = new CustomAdapter(getContext(), android.R.layout.simple_list_item_1, eventLists);
                listView.setAdapter(adapter);
                shimmer_view.stopShimmerAnimation();
                shimmer_view.setVisibility(View.GONE);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //checked current user loggedIn or not
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    fragmentManager.beginTransaction().replace(R.id.content_frame,new LoginFragment()).addToBackStack("main").commit();
                    }
            }
        };

        //Add event details button
        btn_addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog();
            }
        });//method closed

        //perform onBackPressed to exit from the application
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        back_pressed = System.currentTimeMillis();// Backpressed initialized //
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (back_pressed++ > System.currentTimeMillis()) {
                    Log.d("Time backpressed..if", "" + back_pressed);
                    Log.d("Time system..if", "" + System.currentTimeMillis());
                getActivity().finish();
                }else {
                back_pressed = (System.currentTimeMillis()+2000);
                Log.d("Time backpressed..else", "" + back_pressed);
                Log.d("Time system..else", "" + System.currentTimeMillis());
            }
                return true;
        }
        return false;
  }
});

        return view;
    }
    private void displayDialog(){
        final Dialog dialog = new Dialog(getActivity(),R.style.CustomAlertDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_event_dialog);

        final EditText edt_addEvent = dialog.findViewById(R.id.edt_addEvent);
        final EditText edt_addName = dialog.findViewById(R.id.edt_addName);
        final Button btn_save = dialog.findViewById(R.id.btn_save);
        final Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = edt_addName.getText().toString().trim();
                final String event = edt_addEvent.getText().toString().trim();
                if (event.isEmpty()) {
                    edt_addEvent.setError(getString(R.string.empty));
                }
                else if(name.isEmpty()){
                    edt_addName.setError(getString(R.string.name));
                }
                else {
                    //SET DATA
                    EventList s = new EventList();
                    s.setName(name);
                    s.setDescription(event);

                    if (helper.save(s)) {
                        //IF SAVED CLEAR EDITXT
                        edt_addName.setText("");
                        edt_addEvent.setText("");

                        adapter = new CustomAdapter(getContext(), android.R.layout.simple_list_item_1, helper.retrieve());
                        listView.setAdapter(adapter);

                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmer_view.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        shimmer_view.stopShimmerAnimation();
        super.onPause();
    }
}
