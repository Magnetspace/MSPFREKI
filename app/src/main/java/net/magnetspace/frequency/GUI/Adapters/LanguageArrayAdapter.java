package net.magnetspace.frequency.GUI.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.magnetspace.frequency.R;

import java.util.Locale;

/**
 * Created by Botond on 2015.05.31..
 */
public class LanguageArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public LanguageArrayAdapter(Context context, String[] values)
    {
        super(context, R.layout.row_layout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values[position]);

        String s = values[position];

        if(s.equals("Magyar"))
            imageView.setImageResource(R.mipmap.hu);
        else if(s.equals("English"))
            imageView.setImageResource(R.mipmap.uk);
        else if(s.equals("Slovakian"))
            imageView.setImageResource(R.mipmap.sk);
        else if(s.equals("Greek"))
            imageView.setImageResource(R.mipmap.el);
        else if(s.equals("Romanian"))
            imageView.setImageResource(R.mipmap.ro);
        else if(s.equals("Russian"))
            imageView.setImageResource(R.mipmap.ru);
        else
            imageView.setImageResource(R.mipmap.de);

        return rowView;
    }
}
