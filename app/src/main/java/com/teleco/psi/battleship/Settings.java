package com.teleco.psi.battleship;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Settings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Button backButton = (Button) findViewById(R.id.back_settings);
        backButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //Closing SecondScreen Activity
                finish();
            }
        });
    }
}
