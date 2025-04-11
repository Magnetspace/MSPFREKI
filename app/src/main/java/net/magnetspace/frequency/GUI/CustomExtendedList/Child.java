package net.magnetspace.frequency.GUI.CustomExtendedList;

import android.text.Spanned;

/**
 * Created by Botond on 2015.06.20..
 */
public class Child
{
    private long id;
    private Spanned name;
    private boolean isChecked;
    private int nth;

    public int getNth() {
        return nth;
    }

    public void setNth(int nth) {
        this.nth = nth;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Spanned getName() {
        return name;
    }

    public void setName(Spanned name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public Child()
    {}

    public Child(long id, Spanned name, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.isChecked = isChecked;
    }
}