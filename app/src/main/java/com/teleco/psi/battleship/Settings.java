package com.teleco.psi.battleship;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


public class Settings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Spinner spinner_level = (Spinner) findViewById(R.id.difficulty_options);
        ArrayAdapter<CharSequence> adap_level = ArrayAdapter.createFromResource(this, R.array.difficulty_options, android.R.layout.simple_spinner_item);

        adap_level.setDropDownViewResource(R.layout.downlevel);
        spinner_level.setAdapter(adap_level);

        spinner_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner spinner_language = (Spinner) findViewById(R.id.language_options);
        ArrayAdapter<CharSequence> adap_language = ArrayAdapter.createFromResource(this, R.array.language_options, android.R.layout.simple_spinner_item);

        adap_language.setDropDownViewResource(R.layout.downlevel);
        spinner_language.setAdapter(adap_language);

        spinner_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner spinner_rules = (Spinner) findViewById(R.id.rules_options);
        ArrayAdapter<CharSequence> adap_rules = ArrayAdapter.createFromResource(this, R.array.rules_options, android.R.layout.simple_spinner_item);

        adap_rules.setDropDownViewResource(R.layout.downlevel);
        spinner_rules.setAdapter(adap_rules);

        spinner_rules.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button backButton = (Button) findViewById(R.id.back_settings);
        backButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //Closing SecondScreen Activity
                finish();
            }
        });

    }
}
