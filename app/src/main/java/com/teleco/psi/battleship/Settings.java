package com.teleco.psi.battleship;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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

        ImageButton info = (ImageButton) findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog();
            }
        });

    }

    private void showInfoDialog() {

        DialogFragment infoDialog = new AlertDialogInfo().newInstance();
        infoDialog.show(getFragmentManager(), "Alert");

    }

    public static class AlertDialogInfo extends DialogFragment {
        public static AlertDialogInfo newInstance() {
            return new AlertDialogInfo();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Games modes");
            builder.setMessage(getResources().getString(R.string.information_rules));
            builder.setCancelable(false);
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            return builder.create();
        }
    }
}
