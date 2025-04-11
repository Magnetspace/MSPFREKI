package net.magnetspace.frequency.GUI;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.magnetspace.frequency.Core.Element;
import net.magnetspace.frequency.GUI.Adapters.CustomListAdapter;
import net.magnetspace.frequency.R;
import net.magnetspace.frequency.SQL.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainMenuFragment extends Fragment {
    private List<Element> customList;
    private Map<Integer, String> names;
    private DatabaseHandler dbHandler;
    private int count = 0;
    private long startMillis=0;

    public MainMenuFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        dbHandler = new DatabaseHandler(getActivity());
        ListView listView = (ListView) view.findViewById(R.id.listView);
        customList = new ArrayList<Element>();
        names = dbHandler.getListNames();

        for(Integer i : names.keySet())
        {
            customList.add(new Element(names.get(i), i));
        }
        //ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, customList);
        CustomListAdapter customListAdapter = new CustomListAdapter(getActivity(), customList);
        listView.setAdapter(customListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity.setSelectedListItemID(customList.get(i).getId());
            }
        });

        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                MainActivity.setSelectedListItemID(customList.get(pos).getId());
               /* Intent intent = new Intent(getActivity(), ListInfoActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();*/
                return true;
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int eventaction = motionEvent.getAction();
                if (eventaction == MotionEvent.ACTION_UP) {

                    //get system current milliseconds
                    long time= System.currentTimeMillis();


                    //if it is the first time, or if it has been more than 3 seconds since the first tap ( so it is like a new try), we reset everything
                    if (startMillis==0 || (time-startMillis> 3000) ) {
                        startMillis=time;
                        count=1;
                    }
                    //it is not the first, and it has been  less than 3 seconds since the first
                    else{ //  time-startMillis< 3000
                        count++;
                    }

                    if (count==15) {
                        MainActivity.showSystemBar();
                    }
                    return true;
                }
                return false;
            }
        });

        return view;
    }

}
