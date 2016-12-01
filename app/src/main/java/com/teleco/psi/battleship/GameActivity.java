package com.teleco.psi.battleship;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.gson.Gson;

public class GameActivity extends Activity {
    private static int [][][] matrix = new int[10][10][3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        Button startGameButton = (Button) findViewById(R.id.button3);
        startGameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent start_game = new Intent(getApplicationContext(), NewGameActivity.class);
                startActivity(start_game);
                finish();
            }
        });

        FrameLayout frameLayout2 = (FrameLayout) findViewById(R.id.board2);
        FrameLayout frameLayout1 = (FrameLayout) findViewById(R.id.board1);
        frameLayout1.addView(createBoard());
        frameLayout2.addView(createBoard());

        SharedPreferences settings = getSharedPreferences("Matrix", 0);
        Gson gson = new Gson();
        String json = settings.getString("Matrix", "");
        matrix = gson.fromJson(json, int[][][].class);
        TableLayout board = (TableLayout) frameLayout1.getChildAt(0);
        for (int i = 1; i <= 10; i++) {
            TableRow row = (TableRow) board.getChildAt(i);
            for (int j = 1; j <= 10; j++) {
                TextView field = (TextView) row.getChildAt(j);
                if (matrix[i-1][j-1][0] == 1 ) {
                    field.setBackgroundColor(Color.BLACK);
                }
            }
        }

    }


    protected TableLayout createBoard(){
        String[] AJ = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        String[] num = {"\\","1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setPadding(0,15,0,0);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams();
        layoutParams.setMargins(100,0,100,0);
        layoutParams.height = 260;
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
                field.setPadding(8,6,0,0);
                field.setGravity(Gravity.CENTER);
                addClickListener(field, i, j);
                row.addView(field, rowparams);
            }
            tableLayout.addView(row, layoutParams);
        }
        return tableLayout;

    }
    protected void addClickListener(TextView view, int i, int j){
        final int row = i;
        final int column = j;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int h = v.getHeight();
                int w = v.getWidth();
                if(column==5){
                    v.setBackgroundResource(R.drawable.agua);
                }
                if(column==7){
                    v.setBackgroundResource(R.drawable.fuego);
                }
                System.out.println(row + " " + column + "  " + h + " " + w);
            }
        });
    }


}
