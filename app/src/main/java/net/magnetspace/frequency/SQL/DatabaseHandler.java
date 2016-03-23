package net.magnetspace.frequency.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import net.magnetspace.frequency.Core.Frequency;
import net.magnetspace.frequency.Core.PlayList;
import net.magnetspace.frequency.GUI.LanguageSelectFragment;
import net.magnetspace.frequency.GUI.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Botond on 2015.06.20..
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "db.sqlite";

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;

        //copydatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long addListName(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        return db.insert("ListName", null, contentValues);
    }

    public void setLocale(String code)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update settings set locale='"+code+"';");
        //ContentValues contentValues = new ContentValues();
        //contentValues.put("locale", code);
        //long ret = db.update("android_metadata", contentValues, "id=1", null);
        //return ret;
    }

    public String getLocale()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select locale from settings", null);
        res.moveToFirst();
        String ret = res.getString(res.getColumnIndex("locale"));
        return ret;
    }

    public long addToList(int listID, int therapyID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("listID", listID);
        contentValues.put("therapyID", therapyID);
        return db.insert("ListFreq", null, contentValues);
    }

    public int deleteList(int listID) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete("ListFreq", "listID=" + listID, null);
    }

    public int deleteAllList(int listID) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete("ListFreq", "listID="+listID, null) +
                db.delete("ListName", "id="+listID, null);
    }

    public Map<Integer, String> getCategories()
    {
        Map<Integer, String> ret = new LinkedHashMap<Integer, String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select id, name_"+LanguageSelectFragment.getSelectedLanguage()+
                " from Cats order by name_"+LanguageSelectFragment.getSelectedLanguage()+" asc", null);
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            ret.put(res.getInt(res.getColumnIndex("id")), res.getString(res.getColumnIndex("name_"+LanguageSelectFragment.getSelectedLanguage())).trim());
                    res.moveToNext();
        }
        ret = sortByValue(ret);
        return ret;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }



    public Map<Integer, Pair<String, Integer>> getItems(int catId)
    {
        Map<Integer, Pair<String, Integer>>  ret = new LinkedHashMap<Integer, Pair<String, Integer>>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select _id, name_"+LanguageSelectFragment.getSelectedLanguage()+", count_freq from Therapy where cats_id="+catId+
                        " ORDER BY name_"+LanguageSelectFragment.getSelectedLanguage()+" ASC", null);
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            ret.put(res.getInt(res.getColumnIndex("_id")), new Pair<String, Integer>(res.getString(res.getColumnIndex("name_"+LanguageSelectFragment.getSelectedLanguage())).trim(),
                    res.getInt(res.getColumnIndex("count_freq"))));
            res.moveToNext();
        }
        //ret = sortByValue(ret);
        return ret;
    }

    public ArrayList<Integer> getListFreq(int listID) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select therapyID from ListFreq where listID="+listID, null);
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            ret.add(res.getInt(res.getColumnIndex("therapyID")));
            res.moveToNext();
        }
        return ret;
    }

    public boolean checkVersion() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from settings", null);
        res.moveToFirst();
        int darab = res.getColumnCount();
        if(darab > 1)
            return true;
        else
            return false;
    }

    public Map<Integer, String> getListNames()
    {
        Map<Integer, String> ret = new HashMap<Integer, String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select id, name from ListName", null);
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            ret.put(res.getInt(res.getColumnIndex("id")), res.getString(res.getColumnIndex("name")));
            res.moveToNext();
        }
        return ret;
    }

    public PlayList getFrequencies() {
        PlayList playList = new PlayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select Therapy.name_"+LanguageSelectFragment.getSelectedLanguage()+", freq_therapy.freq  "+
                "from ListName inner join ListFreq on ListName.id=ListFreq.listID " +
                "inner join Therapy on Therapy._id=ListFreq.therapyID " +
                "inner join freq_therapy on freq_therapy.therapy_id=Therapy._id " +
                "where ListName.id="+MainActivity.getSelectedListItemID(), null);
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            playList.add(new Frequency(res.getString(res.getColumnIndex("name_" + LanguageSelectFragment.getSelectedLanguage())), res.getDouble(res.getColumnIndex("freq"))));
            res.moveToNext();
        }
        return playList;
    }

    public ArrayList<String> getSelectedFreqNames(int id) {
        ArrayList<String> ret = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select Therapy.name_"+LanguageSelectFragment.getSelectedLanguage()+
                " from Therapy join ListFreq on Therapy._id=ListFreq.therapyID where ListFreq.listID="+
                id, null);

        res.moveToFirst();

        while(res.isAfterLast() == false) {
            ret.add(res.getString(res.getColumnIndex("name_" + LanguageSelectFragment.getSelectedLanguage())));
            res.moveToNext();
        }
            return ret;
    }

}
