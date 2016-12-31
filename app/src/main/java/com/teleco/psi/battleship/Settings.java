package com.teleco.psi.battleship;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;


public class Settings extends Activity {
    private static String original[] = new String[4];
    private final int LEVEL = 0, LANGUAGE = 1, SHIPS = 2, RULES = 3;
    private Spinner spinner_language, spinner_level, spinner_rules;
    private Switch ad_ships;
    private Button backButton;

    @Override
    protected void onResume() {
        String language = getSharedPreferences("Language" , Context.MODE_PRIVATE).getString("Language","");
        if(language.equals("es")){
            ArrayAdapter adap = (ArrayAdapter) spinner_language.getAdapter();
            spinner_language.setSelection(adap.getPosition("Español"));
        }
        SharedPreferences settings_ships = getSharedPreferences("Adyacent_ships" , Context.MODE_PRIVATE);
        settings_ships.getBoolean("checked",false);
        ad_ships.setChecked(settings_ships.getBoolean("checked",false));
        super.onResume();
    }

    private void configLanguage(String language){
        Locale locale = new Locale(language);
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
        onConfigurationChanged(config);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        spinner_level = (Spinner) findViewById(R.id.difficulty_options);
        ArrayAdapter<CharSequence> adap_level = ArrayAdapter.createFromResource(this, R.array.difficulty_options, android.R.layout.simple_spinner_item);
        adap_level.setDropDownViewResource(R.layout.downlevel);
        spinner_level.setAdapter(adap_level);
        spinner_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                System.out.println("Level: " + spinner_level.getSelectedItem().toString());

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner_language = (Spinner) findViewById(R.id.language_options);
        ArrayAdapter<CharSequence> adap_language = ArrayAdapter.createFromResource(this, R.array.language_options, android.R.layout.simple_spinner_item);
        adap_language.setDropDownViewResource(R.layout.downlevel);
        spinner_language.setAdapter(adap_language);
        spinner_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String language;
                if (spinner_language.getSelectedItem().toString().equalsIgnoreCase("English")) {
                    language = "en";
                } else language = "es";
                configLanguage(language);
                System.out.println("Language: " + spinner_language.getSelectedItem().toString());
                SharedPreferences settings = getSharedPreferences("Language" , Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("Language", language);
                editor.commit();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ad_ships = (Switch) findViewById(R.id.switch_ships);
        ad_ships.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences settings = getSharedPreferences("Adyacent_ships" , Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("checked", isChecked);
                editor.commit();
            }
        });

        spinner_rules = (Spinner) findViewById(R.id.rules_options);
        ArrayAdapter<CharSequence> adap_rules = ArrayAdapter.createFromResource(this, R.array.rules_options, android.R.layout.simple_spinner_item);
        adap_rules.setDropDownViewResource(R.layout.downlevel);
        spinner_rules.setAdapter(adap_rules);
        spinner_rules.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                System.out.println("Rules: " + spinner_rules.getSelectedItem().toString());
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        original[LANGUAGE] = spinner_language.getSelectedItem().toString();
        original[RULES] = spinner_rules.getSelectedItem().toString();
        original[LEVEL] = spinner_level.getSelectedItem().toString();
        original[SHIPS] = "" + ad_ships.isChecked();

        backButton = (Button) findViewById(R.id.back_settings);
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

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setTitle("Games modes")
                .setMessage(getResources().getString(R.string.information_rules))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
    }

    /**
     * Cuando cambia la configuración de idioma se llama a este método para refrescar los recursos (cambiar el idioma de los TextView y los Spinner)
     * @param newConfig Configuración a actualizar
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        TextView setting = (TextView) findViewById(R.id.settings_text);
        setting.setText(R.string.settings);
        TextView level = (TextView) findViewById(R.id.difficulty_text);
        level.setText(R.string.difficulty);
        TextView rules = (TextView) findViewById(R.id.rules_text);
        rules.setText(R.string.rules);
        TextView ships = (TextView) findViewById(R.id.ships_text);
        ships.setText(R.string.allow_adjacent_ships);
        TextView language = (TextView) findViewById(R.id.language_text);
        language.setText(R.string.language);
        ArrayAdapter<CharSequence> adap_rules = ArrayAdapter.createFromResource(this, R.array.rules_options, android.R.layout.simple_spinner_item);
        adap_rules.setDropDownViewResource(R.layout.downlevel);
        spinner_rules.setAdapter(adap_rules);
        ArrayAdapter<CharSequence> adap_level = ArrayAdapter.createFromResource(this, R.array.difficulty_options, android.R.layout.simple_spinner_item);
        adap_level.setDropDownViewResource(R.layout.downlevel);
        spinner_level.setAdapter(adap_level);
        backButton.setText(R.string.save);
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Captura el evento de pulsar la tecla atrás del dispositivo, lanzando un diálogo sólo cuando alguno de los parámetros de los ajustes ha cambiado.
     * Se puede escoger entre guardar o no guardar.
     */

    private boolean check_changed(){
        if (!(original[LANGUAGE].equalsIgnoreCase(spinner_language.getSelectedItem().toString())) ||
                !(original[LEVEL].equalsIgnoreCase(spinner_level.getSelectedItem().toString())) ||
                !(original[RULES].equalsIgnoreCase(spinner_rules.getSelectedItem().toString())) ||
                !(original[SHIPS].equalsIgnoreCase("" + ad_ships.isChecked()))){
            return false;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        if(check_changed()) {
            AlertDialog alertbox = new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.back_pressed))
                    .setCancelable(false)
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no_save, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Locale locale = new Locale(original[LANGUAGE]);
                            Resources res = getResources();
                            Configuration config = res.getConfiguration();
                            config.locale = locale;
                            res.updateConfiguration(config, res.getDisplayMetrics());
                            finish();
                            //close();
                        }
                    })
                    .show();
        }else finish();
    }

}
