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
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class MainActivity extends Activity {

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent refresh = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(refresh);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        String language = getSharedPreferences("Language", Context.MODE_PRIVATE).getString("Language", "");
        Locale locale;
        if (language.equals(""))
            locale = Locale.getDefault();
        else
            locale = new Locale(language);
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
        SharedPreferences settings = getSharedPreferences("Language", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Language", language);
        editor.commit();
        setContentView(R.layout.activity_main);

        Button newGameButton = (Button) findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent game_intent = new Intent(getApplicationContext(), NewGameActivity.class);
                startActivity(game_intent);

            }
        });

        Button optionsButton = (Button) findViewById(R.id.optionsButton);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent options_intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(options_intent);
            }
        });

        Button aboutButton = (Button) findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutDialog();
            }
        });
    }

    private void showAboutDialog() {

        DialogFragment aboutDialog = new AlertDialogAbout().newInstance();
        aboutDialog.show(getFragmentManager(), "Alert");

    }

    public static class AlertDialogAbout extends DialogFragment {

        public static AlertDialogAbout newInstance() {
            return new AlertDialogAbout();
        }

        // Build AlertDialog using AlertDialog.Builder
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.about);
            builder.setMessage(R.string.aboutBattleship);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int id) {

                        }
                    });
            return builder.create();
        }
    }
}
