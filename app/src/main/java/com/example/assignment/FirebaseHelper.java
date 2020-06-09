package com.example.assignment;

import com.example.Model.EventList;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class FirebaseHelper {

    private DatabaseReference db;
    private Boolean saved;
    private ArrayList<EventList> eventLists = new ArrayList<>();

    /*
 Pass Database Reference
  */
    public FirebaseHelper(DatabaseReference db) {
        this.db = db;
    }
    public Boolean save(EventList eventList)
    {
        if(eventList==null)
        {
            saved=false;
        }else
        {
            try
            {
                db.child("Events").push().setValue(eventList);
                saved=true;

            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved=false;
            }
        }

        return saved;
    }

    //Implement Fetch Data and fill arrayList
    private void fetchData(DataSnapshot dataSnapshot)
    {
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            EventList event = ds.getValue(EventList.class);
            eventLists.add(event);
        }
    }

    //Retrieve data
    public ArrayList<EventList> retrieve()
    {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
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

        return eventLists;
    }


}