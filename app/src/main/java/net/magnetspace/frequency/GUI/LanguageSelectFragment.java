package net.magnetspace.frequency.GUI;

import java.util.Locale;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import net.magnetspace.frequency.GUI.Adapters.LanguageArrayAdapter;
import net.magnetspace.frequency.R;
import net.magnetspace.frequency.SQL.DatabaseHandler;

public class LanguageSelectFragment extends Fragment {
    private String selected_item="English";
    private static String languageCode="en";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Locale current = Locale.getDefault();
        Configuration config = new Configuration();
        config.locale = current;
        getActivity().getBaseContext().getResources().updateConfiguration(config,
                getActivity().getBaseContext().getResources().getDisplayMetrics());
        View rootView = inflater.inflate(R.layout.fragment_language_select, container, false);

        languageCode = current.getLanguage().split("_")[0];
        if(languageCode.equals("us")) languageCode = "en";
        if(languageCode.equals(""))
            languageCode = current.getLanguage().split("_")[0];
        String[] values = new String[] {"Magyar", "English", "Greek", "Romanian", "Russian", "German", "Slovakian"};
        LanguageArrayAdapter languageArrayAdapter = new LanguageArrayAdapter(getActivity(), values);
        final ListView temp =(ListView) rootView.findViewById(R.id.languageList);

        temp.setAdapter(languageArrayAdapter);
        temp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                selected_item = (String) (temp.getItemAtPosition(myItemInt));
                setLanguage();
            }
        });

        return rootView;
    }

    private void setLanguage() {
        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity().getBaseContext());
        Locale locale;
        if(selected_item.equals("Magyar"))
            locale = new Locale("hu");
        else if(selected_item.equals("English"))
            locale = new Locale("en");
        else if(selected_item.equals("Slovakian"))
            locale = new Locale("sk");
        else if(selected_item.equals("Greek"))
            locale = new Locale("el");
        else if(selected_item.equals("Romanian"))
            locale = new Locale("ro");
        else if(selected_item.equals("Russian"))
            locale = new Locale("ru");
        else
            locale = new Locale("de");

        databaseHandler.setLocale(locale.getLanguage().split("_")[0]);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;

        getActivity().getBaseContext().getResources().updateConfiguration(config,
                getActivity().getBaseContext().getResources().getDisplayMetrics());
        getActivity().finish();
        getActivity().startActivity(getActivity().getIntent());
    }

    public static String getSelectedLanguage() {
        return languageCode;
    }
}
