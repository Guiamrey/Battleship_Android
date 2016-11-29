package com.teleco.psi.battleship;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class GameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_game);
        FrameLayout v = (FrameLayout) findViewById(R.id.vieew);
        setContentView(v);
        //LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View v = vi.inflate(R.layout.activity_game, null);
        TableLayout board = (TableLayout) findViewById(R.id.board);
        TableLayout.LayoutParams params = new TableLayout.LayoutParams();
        params.width = 260;
        board.setBackgroundColor(Color.BLACK);
        for (int i = 0; i < 10; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams rowparams = new TableRow.LayoutParams();
            rowparams.width = TableRow.LayoutParams.MATCH_PARENT;
           // row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            for (int j = 0; j < 10; j++) {
                TextView field = new TextView(this);
                field.setBackgroundColor(Color.GRAY);
                row.addView(field,rowparams);
            }
            board.addView(row, params);
        }
        v.addView(board);
       // ViewGroup insertPoint = (ViewGroup) findViewById(R.id.board);
       // insertPoint.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
    }
}
