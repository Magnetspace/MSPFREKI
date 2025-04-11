package net.magnetspace.frequency.Core;

/**
 * Created by Botond on 2015.07.02..
 */
public class Frequency {
    private String name;
    private double freq;

    public Frequency(String name, double freq)
    {
        this.name = name;
        this.freq = freq;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setFreq(double freq) {
        this.freq = freq;
    }

    public double getFreq() {
        return freq;
    }
}
