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
import android.graphics.PorterDuff;
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

import java.io.File;
import java.io.FileInputStream;


public class Settings extends Activity {
    private static String original[] = new String[4];
    private final int LEVEL = 0, LANGUAGE = 1, SHIPS = 2, RULES = 3;
    private Spinner spinner_language, spinner_level, spinner_rules;
    private Switch ad_ships;
    private Button backButton;

    @Override
    protected void onResume() {
        String language = getResources().getConfiguration().locale.getDisplayLanguage();
        System.out.println("-------- Language"+language);
        if(language.equalsIgnoreCase("español") || language.equalsIgnoreCase("spanish")){
            ArrayAdapter adap = (ArrayAdapter) spinner_language.getAdapter();
            spinner_language.setSelection(adap.getPosition("Español"));
        }

        super.onResume();
    }

    private void setPreferences(){
        ArrayAdapter adap = (ArrayAdapter) spinner_level.getAdapter();
        spinner_level.setSelection(adap.getPosition(getSharedPreferences("Level", Context.MODE_PRIVATE).getString("Level", "")), true);

        adap = (ArrayAdapter) spinner_rules.getAdapter();
        spinner_rules.setSelection(adap.getPosition(getSharedPreferences("Rules", Context.MODE_PRIVATE).getString("Rules", "")));

        ad_ships.setChecked(getSharedPreferences("Adyacent_ships" , Context.MODE_PRIVATE).getBoolean("checked",false));
    }

    private void configLanguage(String language){
        Locale locale = new Locale(language);
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
        onConfigurationChanged(config);
    }
    private void savePreferences(String key, String name, String value_string){
        SharedPreferences settings = getSharedPreferences(key , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value_string);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        spinner_language = (Spinner) findViewById(R.id.language_options);
        spinner_language.getBackground().setColorFilter(getResources().getColor(R.color.ColorWhite), PorterDuff.Mode.SRC_ATOP);
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
                ((TextView) view).setTextColor(getResources().getColor(R.color.ColorWhite));
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner_level = (Spinner) findViewById(R.id.difficulty_options);
        spinner_level.getBackground().setColorFilter(getResources().getColor(R.color.ColorWhite), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<CharSequence> adap_level = ArrayAdapter.createFromResource(this, R.array.difficulty_options, android.R.layout.simple_spinner_item);
        adap_level.setDropDownViewResource(R.layout.downlevel);
        spinner_level.setAdapter(adap_level);
        spinner_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.ColorWhite));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ad_ships = (Switch) findViewById(R.id.switch_ships);

        spinner_rules = (Spinner) findViewById(R.id.rules_options);
        spinner_rules.getBackground().setColorFilter(getResources().getColor(R.color.ColorWhite), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<CharSequence> adap_rules = ArrayAdapter.createFromResource(this, R.array.rules_options, android.R.layout.simple_spinner_item);
        adap_rules.setDropDownViewResource(R.layout.downlevel);
        spinner_rules.setAdapter(adap_rules);
        spinner_rules.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.ColorWhite));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        original[LANGUAGE] = getSharedPreferences("Language" , Context.MODE_PRIVATE).getString("Language","");
        original[RULES] = getSharedPreferences("Rules", Context.MODE_PRIVATE).getString("Rules", "");
        original[LEVEL] = getSharedPreferences("Level", Context.MODE_PRIVATE).getString("Level", "");
        original[SHIPS] = "" + getSharedPreferences("Adyacent_ships" , Context.MODE_PRIVATE).getBoolean("checked",false);

        backButton = (Button) findViewById(R.id.back_settings);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveAll();
                //Closing Screen Activity
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

        ImageButton log = (ImageButton) findViewById(R.id.log);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogDialog();
            }
        });
    }

    private void saveAll(){
        SharedPreferences settings = getSharedPreferences("Adyacent_ships" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("checked", ad_ships.isChecked());
        editor.commit();
        savePreferences("Level", "Level", spinner_level.getSelectedItem().toString());

        String language;
        if (spinner_language.getSelectedItem().toString().equalsIgnoreCase("English")) {
            language = "en";
        } else language = "es";
        savePreferences("Language", "Language", language);

        savePreferences("Rules", "Rules", spinner_rules.getSelectedItem().toString());
    }

    private void showInfoDialog() {

        AlertDialog infodialog = new AlertDialog.Builder(this)
                .setTitle(R.string.gamesmodes)
                .setMessage(getResources().getString(R.string.information_rules))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
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
        setPreferences();
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Captura el evento de pulsar la tecla atrás del dispositivo, lanzando un diálogo sólo cuando alguno de los parámetros de los ajustes ha cambiado.
     * Se puede escoger entre guardar o no guardar.
     */

    private boolean check_changed(){
        String lan;
        if(original[LANGUAGE].equalsIgnoreCase("es"))
            lan = "Español";
        else lan = "English";
        return !(lan.equalsIgnoreCase(spinner_language.getSelectedItem().toString())) ||
                !(original[LEVEL].equalsIgnoreCase(spinner_level.getSelectedItem().toString())) ||
                !(original[RULES].equalsIgnoreCase(spinner_rules.getSelectedItem().toString())) ||
                !(original[SHIPS].equalsIgnoreCase("" + ad_ships.isChecked()));
    }

    /**
     * Se llama cuando se pulsa el botón de atrás del dispositivo, se comprueba si ha cambiado algún parametro de los ajustes para guardar o no los cambios.
     */
    @Override
    public void onBackPressed() {
        if(check_changed()) {
            AlertDialog alertbox = new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.back_pressed))
                    .setCancelable(false)
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            saveAll();
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
                        }
                    })
                    .show();
        }else finish();
    }

    private void showLogDialog() {

        DialogFragment logDialog = new LogDialogInfo().newInstance();
        logDialog.show(getFragmentManager(), "Log file");

    }

    public static class LogDialogInfo extends DialogFragment {
        public static LogDialogInfo newInstance() {
            return new LogDialogInfo();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Log file");
            File file = new File("sdcard/log.file");
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fileInputStream.read(data);
                fileInputStream.close();
                builder.setMessage(new String(data, "UTF-8"));
            } catch (Exception e) {
                builder.setMessage(null);
            }
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
