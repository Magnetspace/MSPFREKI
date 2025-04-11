package net.magnetspace.frequency.GUI.CustomExtendedList;

/**
 * Created by Botond on 2015.06.20..
 */
import java.util.ArrayList;
import java.util.List;

public class Parent
{
    private long id;
    private String name;
    private List<Child> childList = new ArrayList<Child>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Child> getChildList() {
        return childList;
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
    }

    public Parent()
    {}

    public void removeChild(int i)
    {
        childList.remove(i);
    }
    public Parent(long id, String name, List<Child> childList) {
        this.id = id;
        this.name = name;
        this.childList = childList;
    }

    public Parent(long id, String name) {
        this.id = id;
        this.name = name;
        this.childList = new ArrayList<Child>();
    }
}
