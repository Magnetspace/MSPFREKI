package net.magnetspace.frequency.GUI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import net.magnetspace.frequency.GUI.Adapters.CustomExpandableListAdapter;
import net.magnetspace.frequency.GUI.Adapters.CustomListAdapter;
import net.magnetspace.frequency.GUI.CustomExtendedList.Child;
import net.magnetspace.frequency.GUI.CustomExtendedList.Parent;
import net.magnetspace.frequency.R;
import net.magnetspace.frequency.SQL.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import android.os.Handler;
public class CreatingListActivity extends Activity {

    private ArrayList<Parent> parents;
    private ArrayList<Child> selected;
    private DatabaseHandler dbHandler;
    private CustomExpandableListAdapter customExpandableListAdapter;
    private int listID;
    private SearchView searchView;
    ProgressDialog barProgressDialog;
    Handler updateBarHandler;

    public ArrayList<Parent> getParents() {
        return parents;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_list);
        getActionBar().hide();
        updateBarHandler = new Handler();

        dbHandler = new DatabaseHandler(this);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                CreatingListActivity.this.launchRingDialog(searchView);
                searchView.setIconified(true);
                searchView.onActionViewCollapsed();
                searchView.setQuery(query, false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                customExpandableListAdapter.getFilter().filter(searchView.getQuery().toString());
                if(newText.isEmpty())
                    searchView.setIconified(true);
                return false;
            }
        });

        listID = getIntent().getIntExtra(CustomListAdapter.listID, -1);
        if(listID == -1) {
            listCreating();
        } else {
            listEditing();
            Button btn = (Button) findViewById(R.id.button4);
            btn.setText(getResources().getString(R.string.update));
        }
    }

    public void launchRingDialog(View view) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(CreatingListActivity.this, "Várjon...",	"Betöltés folyamatban ...", true);
        ringProgressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(2000);
                } catch (Exception e) {

                }
                ringProgressDialog.dismiss();
            }
        }).start();
    }

    public void launchBarDialog(View view) {
        barProgressDialog = new ProgressDialog(CreatingListActivity.this);

        barProgressDialog.setTitle("Loading list");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(20);
        barProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    // Here you should write your time consuming task...
                    while (barProgressDialog.getProgress() <= barProgressDialog.getMax()) {

                        Thread.sleep(2000);

                        updateBarHandler.post(new Runnable() {

                            public void run() {

                                barProgressDialog.incrementProgressBy(2);

                            }

                        });

                        if (barProgressDialog.getProgress() == barProgressDialog.getMax()) {

                            barProgressDialog.dismiss();

                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();
    }

    private void listCreating() {
        parents = setupCreatingList();

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        customExpandableListAdapter = new CustomExpandableListAdapter(parents, this);
        expandableListView.setIndicatorBounds(0, 20);
        expandableListView.setAdapter(customExpandableListAdapter);
       /* expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(i, i1));
                expandableListView.setItemChecked(index, true);

                return true;
            }
        });*/
    }

    private void listEditing() {
        parents = setupEditingList();

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        customExpandableListAdapter = new CustomExpandableListAdapter(parents, this, selected);

        expandableListView.setIndicatorBounds(0, 20);
        expandableListView.setAdapter(customExpandableListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_creating_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Parent> setupCreatingList()
    {
        final ArrayList<Parent> list = new ArrayList<Parent>();
        Map<Integer, String> cats = dbHandler.getCategories();
        //TreeMap<Integer, String> catsOrder = new TreeMap<Integer, String>(cats);

        for (Integer i : cats.keySet())
        {
            final Parent parent = new Parent();

            parent.setName(cats.get(i));
            parent.setId((long) i);
            parent.setChildList(new ArrayList<Child>());

            Map<Integer, Pair<String, Integer>> items = dbHandler.getItems(i);
            //TreeMap<Integer, Pair<String, Integer>> itemsOrder = new TreeMap<Integer, Pair<String, Integer>>(items);

            for(Integer ii : items.keySet()) {
                final Child child = new Child();

                //child.setName(Html.fromHtml("<b><font size=\"14\">"+items.get(ii).first+"</font></b>"+"<br>"+getString(R.string.length)+" "+items.get(ii).second+".0 min<br>"));
                child.setName(Html.fromHtml(items.get(ii).first + "<br>" + getString(R.string.length) + " " + items.get(ii).second + ".0 min\n"));
                child.setId(ii);

                parent.getChildList().add(child);
            }

            list.add(parent);
        }
        return list;
    }

    private ArrayList<Parent> setupEditingList() {
        selected = new ArrayList<Child>();

        ArrayList<Integer> selectedIDs = dbHandler.getListFreq(listID);
        final ArrayList<Parent> list = new ArrayList<Parent>();
        Map<Integer, String> cats = dbHandler.getCategories();
        //TreeMap<Integer, String> catsOrder = new TreeMap<Integer, String>(cats);

        for (Integer i : cats.keySet())
        {
            final Parent parent = new Parent();

            parent.setName(cats.get(i));
            parent.setId((long) i);
            parent.setChildList(new ArrayList<Child>());

            Map<Integer, Pair<String, Integer>> items = dbHandler.getItems(i);
            //TreeMap<Integer, Pair<String, Integer>> itemsOrder = new TreeMap<Integer, Pair<String, Integer>>(items);

            for(Integer ii : items.keySet()) {
                final Child child = new Child();
                //child.setName(Html.fromHtml("<b><font size=\"14\">"+items.get(ii).first+"</font></b>"+"<br>"+getString(R.string.length)+" "+items.get(ii).second+".0 min<br>"));
                child.setName(Html.fromHtml(items.get(ii).first + "<br>" + getString(R.string.length) + " " + items.get(ii).second + ".0 min\n"));
                child.setId(ii);

                if(selectedIDs.contains(ii)) {
                    child.setIsChecked(true);
                }
                else {
                    child.setIsChecked(false);
                }

                parent.getChildList().add(child);
            }

            list.add(parent);
        }

        for(int i = 0; i < selectedIDs.size(); i++)
        {
            for (Integer j : cats.keySet())
            {
                Map<Integer, Pair<String, Integer>> items = dbHandler.getItems(j);

                for(Integer ii : items.keySet()) {
                    final Child child = new Child();
                    //child.setName(Html.fromHtml("<b><font size=\"14\">"+items.get(ii).first+"</font></b>"+"<br>"+getString(R.string.length)+" "+items.get(ii).second+".0 min<br>"));
                    child.setName(Html.fromHtml(items.get(ii).first + "<br>" + getString(R.string.length) + " " + items.get(ii).second + ".0 min\n"));
                    child.setId(ii);

                    if(selectedIDs.contains(ii)) {
                        child.setIsChecked(true);
                    }
                    else {
                        child.setIsChecked(false);
                    }
                    selected.add(child);
                }
            }
        }
        return list;
    }

    public void onCreateBtn(View view)
    {
        if(listID == -1) {
            if (addListToDb()) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.list_add),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.list_add_error),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            if (updateListInDb()) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.list_update),
                        Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.list_update_error),
                        Toast.LENGTH_SHORT).show();
        }
    }

    private void back()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onBackBtn(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean addListToDb()
    {
        ArrayList<Child> temp = customExpandableListAdapter.getSelected();
        EditText listName = (EditText) findViewById(R.id.editText);
        if(!listName.getText().toString().isEmpty()) {
            long id = dbHandler.addListName(listName.getText().toString());
            if(id == -1) return false;
            //for (Parent p : temp) {
                for (Child c : temp) {
                    if (c.isChecked()) {
                        if(dbHandler.addToList((int)id, (int)c.getId()) == -1) return false;
                    }
                }
            //}
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean updateListInDb()
    {
        dbHandler.deleteList(listID);
        ArrayList<Parent> temp = customExpandableListAdapter.getCatList();
            for (Parent p : temp) {
                for (Child c : p.getChildList()) {
                    if (c.isChecked()) {
                        if(dbHandler.addToList(listID, (int)c.getId()) == -1) return false;
                    }
                }
            }
            return true;

    }
}