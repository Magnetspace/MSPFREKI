package net.magnetspace.frequency.Core;

/**
 * Created by Botond on 2015.07.02..
 */
public class Element {
    private String text;
    private int id;

    public Element(String text, int id) {
        this.text = text;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
