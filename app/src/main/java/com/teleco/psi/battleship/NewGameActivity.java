package com.teleco.psi.battleship;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class NewGameActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ship_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame);
        final TableLayout table = createBoard();
        frameLayout.addView(table);

        Button startGameButton = (Button) findViewById(R.id.start_game);
        startGameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent start_game = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(start_game);
            }
        });



    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = ((String) parent.getItemAtPosition(position)).split(" ")[0];
        int length;
        switch (item) {
            case "Carrier":
                length = 5;
                break;
            case "Battleship":
                length = 4;
                break;
            case "Cruiser":
            case "Submarine":
                length = 3;
                break;
            case "Destroyer":
                length = 2;
                break;
            default:
                length = 0;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    protected TableLayout createBoard(){
        String[] AJ = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        String[] num = {"\\","1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        TableLayout tableLayout = new TableLayout(this);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams();
     //   layoutParams.height = 260;
        layoutParams.setMargins(50,0,50,0);
        for (int i = 0; i < 11; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams rowparams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            for (int j = 0; j < 11; j++) {
                TextView field = new TextView(this);
                field.setBackgroundResource(R.drawable.cell_shape);
                if (i == 0) {
                    field.setText(num[j]);
                } else if (j == 0) field.setText(AJ[i - 1]);
                field.setTextSize(15);
                field.setPadding(10,15,0,0);
                field.setGravity(Gravity.CENTER);
                row.addView(field, rowparams);
            }
            tableLayout.addView(row, layoutParams);
        }
        return tableLayout;

    }
}
