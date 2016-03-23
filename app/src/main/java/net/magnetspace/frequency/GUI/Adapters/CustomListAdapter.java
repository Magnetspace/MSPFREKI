package net.magnetspace.frequency.GUI.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.magnetspace.frequency.Core.Element;
import net.magnetspace.frequency.GUI.CreatingListActivity;
import net.magnetspace.frequency.GUI.ListInfoActivity;
import net.magnetspace.frequency.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Botond on 2015.07.07..
 */
public class CustomListAdapter extends ArrayAdapter  {
        private final Activity activity;
        private final List<Element> values;

        public static String listID = "listID";

        public CustomListAdapter(Activity activity, List<Element> values)
        {
            super(activity, R.layout.list_layout, values);
            this.activity = activity;
            this.values = values;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.listlabel);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.listicon);
            ImageView imageView2 = (ImageView) rowView.findViewById(R.id.editicon);

            textView.setText(values.get(position).getText());
            imageView.setImageResource(R.mipmap.ecg_chart);
            imageView2.setImageResource(R.mipmap.patient_chart);

            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, ListInfoActivity.class);
                    intent.putExtra(CustomListAdapter.listID, values.get(position).getId());
                    activity.startActivity(intent);
                    activity.finish();
                }
            });

            return rowView;
        }
    public void launchRingDialog(View view, final int position) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(CustomListAdapter.this.getContext(), "Please wait ...",	"Loading ...", true);
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
}
