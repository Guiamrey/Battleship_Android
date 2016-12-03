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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;


public class NewGameActivity extends AppCompatActivity {
    private static int[][][] matrix = new int[10][10][3];
    private int _xDelta;
    private int _yDelta;
    private boolean move = false;

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
        startGameButton.setOnClickListener(new View.OnClickListener() {
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
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment aboutDialog = new NewGameActivity.AlertDialogInfo().newInstance();
                aboutDialog.show(getFragmentManager(), "Alert");
            }
        });
        ImageView ship5 = (ImageView) findViewById(R.id.ship5);
        onTouchListener(ship5);
        ImageView ship4_2 = (ImageView) findViewById(R.id.ship4_2);
        onTouchListener(ship4_2);
        ImageView ship4_1 = (ImageView) findViewById(R.id.ship4_1);
        onTouchListener(ship4_1);
        ImageView ship3_1 = (ImageView) findViewById(R.id.ship3_1);
        onTouchListener(ship3_1);
        ImageView ship3_2 = (ImageView) findViewById(R.id.ship3_2);
        onTouchListener(ship3_2);
        ImageView ship2 = (ImageView) findViewById(R.id.ship2);
        onTouchListener(ship2);
    }

    protected TableLayout createBoard() {
        String[] AJ = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        String[] num = {"\\", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        TableLayout tableLayout = new TableLayout(this);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams();
        layoutParams.setMargins(50, 0, 50, 0);
        int i, j;
        for (i = 0; i < 11; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            for (j = 0; j < 11; j++) {
                TextView field = new TextView(this);
                field.setBackgroundResource(R.drawable.cell_shape);
                if (!(i == 0 || j == 0)) {
                    addClickListener(field, i, j);
                }
                if (i == 0) {
                    field.setText(num[j]);
                } else if (j == 0) {
                    field.setText(AJ[i - 1]);
                }
                field.setTextSize(15);
                field.setPadding(10, 15, 0, 0);
                field.setGravity(Gravity.CENTER);
                row.addView(field, rowParams);
            }
            tableLayout.addView(row, layoutParams);
        }
        return tableLayout;
    }

    private void addClickListener(final TextView view, final int i, final int j) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = Color.TRANSPARENT;
                Drawable background = v.getBackground();
                if (background instanceof ColorDrawable) {
                    color = ((ColorDrawable) background).getColor();
                }
                if (color == Color.TRANSPARENT) {
                    matrix[i - 1][j - 1][0] = 1;
                    v.setBackgroundColor(Color.BLACK);
                } else {
                    matrix[i - 1][j - 1][0] = 0;
                    v.setBackgroundResource(R.drawable.cell_shape);
                }
            }
        });
    }

    private void onTouchListener(ImageView v) {

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Recogemos las coordenadas del dedo
                final int X = (int) event.getRawX();
                final int Y = (int) event.getRawY();
                //Dependiendo de la accion recogida..
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    //Al tocar la pantalla
                    case MotionEvent.ACTION_DOWN:
                        move = false;
                        //Recogemos los parametros de la imagen que hemo tocado
                        RelativeLayout.LayoutParams Params = (RelativeLayout.LayoutParams) v.getLayoutParams();
                        _xDelta = X - Params.leftMargin;
                        _yDelta = Y - Params.topMargin;
                        System.out.println(X + " " + Y + " / " + _xDelta + " " + _yDelta + " / " + Params.leftMargin + " " + Params.topMargin);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(!move){
                            if(Float.compare(v.getRotation(), (float) 0.0) < 0) {
                                v.setRotation(v.getRotation() + 90);
                            }else{
                                v.setRotation(v.getRotation() - 90);
                            }
                        }
                        //Al levantar el dedo simplemento mostramos un mensaje
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        //No hace falta utilizarlo
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        //No hace falta utilizarlo
                        break;
                    case MotionEvent.ACTION_MOVE:
                        move = true;
                        //Al mover el dedo vamos actualizando los margenes de la imagen para crear efecto de arrastrado
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                        layoutParams.leftMargin = X - _xDelta;
                        layoutParams.topMargin = Y - _yDelta;
                        /*int top = layoutParams.topMargin;
                        int left = layoutParams.leftMargin;
                        if ((left < 140) || (top < 678) || (top > 1438))*/
                        v.setLayoutParams(layoutParams);
                        break;
                }
                //Se podría decir que 'dibujamos' la posición de la imagen en el marco.
                v.invalidate();
                return true;
            }
        });
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
