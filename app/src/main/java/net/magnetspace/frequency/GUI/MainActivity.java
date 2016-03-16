package net.magnetspace.frequency.GUI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.magnetspace.frequency.GUI.Adapters.CustomListAdapter;
import net.magnetspace.frequency.R;
import net.magnetspace.frequency.SQL.DatabaseHandler;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    private static int selectedListItemID = -1;
    private static Context gContext;
    public static Context getgContext() {
        return gContext;
    }

    public static int getSelectedListItemID() {
        return selectedListItemID;
    }

    public static void setSelectedListItemID(int selectedListItemID) {
        MainActivity.selectedListItemID = selectedListItemID;
    }

    private int count = 0;
    private long startMillis=0;
    private ViewPager viewPager;
    private ActionBar actionBar;
    private TabsPagerAdapter tabsPagerAdapter;


    private String[] tabs;

    public static void hideSystemBar() {
        try {
            //REQUIRES ROOT
            Build.VERSION_CODES vc = new Build.VERSION_CODES();
            Build.VERSION vr = new Build.VERSION();
            String ProcID = "79"; //HONEYCOMB AND OLDER

            //v.RELEASE  //4.0.3
            if (vr.SDK_INT >= vc.ICE_CREAM_SANDWICH) {
                ProcID = "42"; //ICS AND NEWER
            }

            String commandStr = "service call activity " +
                    ProcID + " s16 com.android.systemui";
            runAsRoot(commandStr);
        } catch (Exception e) {
            // something went wrong, deal with it here
        }
    }

    @Override
    protected  void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.main_activity);
        gContext = getBaseContext();
        RootTools.debugMode = true; // debug mode
        hideSystemBar();
        boolean tempB = isDbFileExist();
        if(!isDbFileExist())
            copydatabase();
        else {
            DatabaseHandler db = new DatabaseHandler(this);
            if(!db.checkVersion())
                copydatabase();
        }

        String[] temp = {getResources().getString(R.string.languages), getResources().getString(R.string.main_menu)};
        tabs = temp;
        viewPager = (ViewPager) findViewById((R.id.pager));
        actionBar = getActionBar();
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(tabsPagerAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        for(String tab_name : tabs)
        {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

    }
    public void onCreateNewListBtn(View view)
    {
        launchRingDialog(view);
    }

    public void launchRingDialog(View view) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...",	"Loading ...", true);
        ringProgressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    Intent intent = new Intent(MainActivity.this, CreatingListActivity.class);
                    startActivity(intent);
                    finish();

                } catch (Exception e) {

                }
                ringProgressDialog.dismiss();
            }
        }).start();
    }

    public void onStartBtn(View view)
    {
        if(MainActivity.getSelectedListItemID() != -1) {
            Intent intent = new Intent(this, FrequencyPlayerActivity.class);
            intent.putExtra(CustomListAdapter.listID, -1);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.list_start_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onDeleteBtn(View view)
    {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.delete_title))
                .setMessage(getResources().getString(R.string.delete_info))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        DatabaseHandler db = new DatabaseHandler(getBaseContext());
                        db.deleteAllList(getSelectedListItemID());
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.list_delete),
                                Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public boolean isDbFileExist()
    {
        return getBaseContext().getDatabasePath("db.sqlite").exists();

        //return database.exists();
    }

    public void copydatabase(){
        try{
            InputStream myinput = getAssets().open(DatabaseHandler.DATABASE_NAME);

            OutputStream myoutput = new FileOutputStream("/data/data/net.magnetspace.frequency/databases/db.sqlite");

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myinput.read(buffer))>0) {
                myoutput.write(buffer,0,length);
            }

            myoutput.flush();
            myoutput.close();
            myinput.close();
        } catch(IOException e)
        {

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();
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
                showSystemBar();
            }
            return true;
        }
        return false;
    }

    public static void showSystemBar() {
        String commandStr = "am startservice -n com.android.systemui/.SystemUIService";
        runAsRoot(commandStr);
    }
    private static void runAsRoot(String commandStr) {
        try {
            CommandCapture command = new CommandCapture(0, commandStr);
            RootTools.getShell(true).add(command);
        } catch (Exception e) {
            // something went wrong, deal with it here
        }
    }
}
