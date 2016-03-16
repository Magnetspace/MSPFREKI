package net.magnetspace.frequency.GUI.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.magnetspace.frequency.GUI.CustomExtendedList.Child;
import net.magnetspace.frequency.GUI.CustomExtendedList.Parent;
import net.magnetspace.frequency.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Botond on 2015.06.20..
 */
public class CustomExpandableListAdapter extends BaseExpandableListAdapter implements Filterable {

    private ArrayList<Parent> catList;
    private ArrayList<Parent> backupList;
    private ArrayList<Child> selectedItems;
    private Context ctx;
    private ViewHolder viewHolder;
    private Filter filter;

    public ArrayList<Child> getSelected() {
        return selectedItems;
    }

    public ArrayList<Parent> getCatList() {
        return catList;
    }

    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    // We implement here the filter logic
                    if (constraint == null || constraint.length() == 0) {
                        // No filter implemented we return all the list
                        results.values = catList;
                        results.count = catList.size();
                    }
                    else {
                        // We perform filtering operation
                        ArrayList<Parent> catAList = new ArrayList<Parent>();

                        for (Parent p : backupList) {
                            ArrayList<Child> cList = new ArrayList<Child>();
                            for (Child c : p.getChildList()) {
                                if (c.getName().toString().toUpperCase().contains(constraint.toString().toUpperCase())) {
                                    cList.add(c);
                                }
                            }
                            if(cList.size() != 0)
                                catAList.add(new Parent(p.getId(), p.getName(), cList));
                        }

                        results.values = catAList;
                        results.count = catAList.size();

                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results.count == 0)
                        notifyDataSetInvalidated();
                    else {
                        catList = (ArrayList<Parent>) results.values;
                        notifyDataSetChanged();
                    }
                }
            };
        return filter;
    }

    private class ViewHolder
    {
        TextView textView;
        CheckBox checkBox;
    }

    public CustomExpandableListAdapter(ArrayList<Parent> parents, Context context)
    {
        catList = parents;
        ctx = context;
        selectedItems = new ArrayList<Child>();
        backupList = parents;
    }

    public CustomExpandableListAdapter(ArrayList<Parent> parents, Context context, ArrayList<Child> selected)
    {
        catList = parents;
        ctx = context;
        selectedItems = selected;
        backupList = parents;
    }

    public void removeGroup(int i)
    {
        catList.remove(i);
    }

    public  void removeChild(int i, int ii)
    {
        ((Parent)getGroup(i)).removeChild(ii);
    }

    @Override
    public int getGroupCount() {
        return catList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return catList.get(i).getChildList().size();
    }

    @Override
    public Object getGroup(int i) {
        return catList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return catList.get(i).getChildList().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return catList.get(i).getId();
    }

    @Override
    public long getChildId(int i, int i1) {
        return catList.get(i).getChildList().get(i1).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        View v = view;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.parent_layout, viewGroup, false);
        }

        TextView groupName = (TextView) v.findViewById(R.id.groupName);
        ImageView imageView = (ImageView) v.findViewById(R.id.iconforparent);
        imageView.setImageResource(R.mipmap.medical_record);

        Parent cat = catList.get(i);

        groupName.setText(cat.getName());


        return v;
    }

    @Override
    public View getChildView(final int i, final int i1, boolean b, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.child_layout, viewGroup, false);
            viewHolder = new ViewHolder();

            viewHolder.textView = (TextView) view.findViewById(R.id.itemName);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.itemDescr);
            ImageView imageView = (ImageView) view.findViewById(R.id.iconforchild);
            imageView.setImageResource(R.mipmap.cardiology);

            view.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder)view.getTag();



        final Child det = catList.get(i).getChildList().get(i1);

        viewHolder.textView.setText(det.getName());

        viewHolder.checkBox.setChecked(catList.get(i).getChildList().get(i1).isChecked());
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(((CheckBox)v).isChecked()) {
                    catList.get(i).getChildList().get(i1).setIsChecked(true);
                    selectedItems.add(catList.get(i).getChildList().get(i1));
                }
                else {
                    catList.get(i).getChildList().get(i1).setIsChecked(false);
                    selectedItems.remove(catList.get(i).getChildList().get(i1));
                }

            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}



