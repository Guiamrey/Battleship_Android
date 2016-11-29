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
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class GameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] AJ = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        String[] num = {"\\","1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        TableLayout tableLayout = new TableLayout(this);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams();
        layoutParams.setMargins(30,15,30,0);
        layoutParams.height = 260;
        for (int i = 0; i < 11; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams rowparams = new TableRow.LayoutParams();
            rowparams.width = TableRow.LayoutParams.MATCH_PARENT;
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            for (int j = 0; j < 11; j++) {
                TextView field = new TextView(this);
                if (i == 0) {
                    field.setText(num[j]);
                } else if (j == 0) field.setText(AJ[i - 1]);
                field.setTextSize(15);
                field.setBackgroundColor(Color.GRAY);
                row.addView(field, rowparams);
            }
            tableLayout.addView(row, layoutParams);
        }
        setContentView(tableLayout);
    }
}
