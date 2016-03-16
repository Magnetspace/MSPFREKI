package net.magnetspace.frequency.Core;

import net.magnetspace.frequency.SQL.DatabaseHandler;

import java.util.ArrayList;

/**
 * Created by Botond on 2015.06.22..
 */
public class PlayList {
    private ArrayList<Frequency> playList;

    public PlayList()
    {
        playList = new ArrayList<Frequency>();
    }

    public void add(Frequency f) {
        playList.add(f);
    }

    public int size() {
        return playList.size();
    }

    public Frequency get(int i) {
        return playList.get(i);
    }
}
