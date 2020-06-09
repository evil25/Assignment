package com.example.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Model.EventList;
import com.example.assignment.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<EventList> eventList;
    private int simple_list_item;
    private DatabaseReference databaseReference;

    public CustomAdapter(Context context, int simple_list_item, ArrayList<EventList> eventList) {
        this.context = context;
        this.simple_list_item = simple_list_item;
        this.eventList = eventList;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        if(view==null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.event_list,parent,false);
        }

        TextView nameTxt= view.findViewById(R.id.nameTxt);
        TextView descTxt= view.findViewById(R.id.descTxt);

        final EventList event = (EventList) this.getItem(position);

        nameTxt.setText(event.getName());
        descTxt.setText(event.getDescription());

        //ONITECLICK
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,event.getDescription(),Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}