package com.teleco.psi.battleship;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
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

import com.google.gson.Gson;

import java.util.Arrays;

public class NewGameActivity extends AppCompatActivity {
    private static int [][][] matrix = new int[10][10][3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        for (int[][] row : matrix) {
            for (int[] column : row) {
                column[0] = 0;
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame);
        final TableLayout table = createBoard();
        frameLayout.addView(table);

        Button startGameButton = (Button) findViewById(R.id.start_game);
        startGameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences("Matrix", 0);
                SharedPreferences.Editor editor = settings.edit();
                Gson gson = new Gson();
                String json = gson.toJson(matrix);
                editor.putString("Matrix", json);
                editor.commit();
                Intent start_game = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(start_game);
            }
        });

        Button infoButton = (Button) findViewById(R.id.info);
        infoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DialogFragment aboutDialog = new NewGameActivity.AlertDialogInfo().newInstance();
                aboutDialog.show(getFragmentManager(), "Alert");
            }
        });
    }

    protected TableLayout createBoard(){
        String[] AJ = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        String[] num = {"\\","1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        TableLayout tableLayout = new TableLayout(this);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams();
        layoutParams.setMargins(50,0,50,0);
        for (int i = 0; i < 11; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            for (int j = 0; j < 11; j++) {
                TextView field = new TextView(this);
                field.setBackgroundResource(R.drawable.cell_shape);
                field.setTag(i + "," + j);
                field.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int row = Integer.parseInt(v.getTag().toString().split(",")[0]);
                        int column = Integer.parseInt(v.getTag().toString().split(",")[1]);
                        int color = Color.TRANSPARENT;
                        Drawable background = v.getBackground();
                        if (background instanceof ColorDrawable) {
                            color = ((ColorDrawable) background).getColor();
                        }
                        if (color == Color.TRANSPARENT) {
                            matrix[row-1][column-1][0] = 1;
                            v.setBackgroundColor(Color.BLACK);
                        } else {
                            matrix[row-1][column-1][0] = 0;
                            v.setBackgroundResource(R.drawable.cell_shape);
                        }
                    }
                });
                String id = String.valueOf(i) + String.valueOf(j);
                field.setId(Integer.parseInt(id));
                if (i == 0) {
                    field.setText(num[j]);
                } else if (j == 0) field.setText(AJ[i - 1]);
                field.setTextSize(15);
                field.setPadding(10,15,0,0);
                field.setGravity(Gravity.CENTER);
                row.addView(field, rowParams);
            }
            tableLayout.addView(row, layoutParams);
        }
        return tableLayout;
    }

    public static class AlertDialogInfo extends DialogFragment {
        public static NewGameActivity.AlertDialogInfo newInstance() {
            return new AlertDialogInfo();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("INFO");
            builder.setMessage("Place your ships on the board as you like. You can place:"
                + "\n1 carrier (5 squares)"
                + "\n1 battleship (4 squares)"
                + "\n2 cruisers (3 squares)"
                + "\n1 destroyer (2 squares)");
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
