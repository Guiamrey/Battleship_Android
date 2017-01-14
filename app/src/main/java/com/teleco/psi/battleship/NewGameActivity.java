package com.teleco.psi.battleship;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;


public class NewGameActivity extends Activity {
    private static int[][][] matrix = new int[10][10][3];
    private static View[][] views = new View[10][10];
    private int ship, colocados, casillas, numfilter;
    private final int CASILLAS5 = 6, CASILLAS4 = 5, CASILLAS3_1 = 4, CASILLAS3_2 = 3, CASILLAS2 = 2;
    private int total_ships = 0;
    private TextView infoship;
    private ImageView image, colorfilter[] = new ImageView[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        for (int[][] row : matrix) {
            for (int[] column : row) {
                column[0] = 0;
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        ImageButton delete = (ImageButton) findViewById(R.id.trashicon);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colocados = 0;
                ship = 0;
                casillas = 0;
                resetBoard();
            }
        });
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame);
        final TableLayout table = createBoard();
        frameLayout.addView(table);

        Button startGameButton = (Button) findViewById(R.id.start_game);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(total_ships == 5) {
                    SharedPreferences settings = getSharedPreferences("Matrix", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(matrix);
                    editor.putString("Matrix", json);
                    editor.commit();
                    Intent start_game = new Intent(getApplicationContext(), GameActivity.class);
                    startActivity(start_game);
                    finish();
                }else{
                    colocarBarcos();
                }
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

        infoship = (TextView) findViewById(R.id.num_fields);

        ImageView ship5 = (ImageView) findViewById(R.id.ship5);
        onClickListener(ship5, CASILLAS5, 5);
        ImageView ship4 = (ImageView) findViewById(R.id.ship4);
        onClickListener(ship4, CASILLAS4, 4);
        ImageView ship3_1 = (ImageView) findViewById(R.id.ship3_1);
        onClickListener(ship3_1, CASILLAS3_1, 3);
        ImageView ship3_2 = (ImageView) findViewById(R.id.ship3_2);
        onClickListener(ship3_2, CASILLAS3_2, 3);
        ImageView ship2 = (ImageView) findViewById(R.id.ship2);
        onClickListener(ship2, CASILLAS2, 2);
        ship = 0;
        colocados = 0;
        casillas = 0;
        numfilter = 0;
        infoship.setText("");
    }

    private void onClickListener(ImageView v, final int tipo, final int num) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (colocados == casillas) {
                    infoship.setText(getString(R.string.shipof) + num + getString(R.string.squares));
                    colocados = 0;
                    ship = tipo;
                    casillas = num;
                    image = (ImageView) v;
                    addClickListenerViews();
                } else {
                    alertShip();
                }
            }
        });
    }

    private void addClickListenerViews() {
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                addClickListener(views[i][j], i+1, j+1);
            }
        }
    }

    private void removeClickListenerViews(){
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                views[i][j].setOnClickListener(null);
            }
        }
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
                    views[i-1][j-1] = field;
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

    private void addClickListener(final View view, final int i, final int j) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = Color.TRANSPARENT;
                Drawable background = v.getBackground();
                if (background instanceof ColorDrawable) {
                    color = ((ColorDrawable) background).getColor();
                }
                if (color == Color.TRANSPARENT) {
                    matrix[i - 1][j - 1][0] = ship;
                    v.setBackgroundColor(Color.BLACK);
                    colocados++;
                    if (colocados == casillas) check_ship(i-1, j-1);
                } else {
                    matrix[i - 1][j - 1][0] = 0;
                    colocados--;
                    v.setBackgroundResource(R.drawable.cell_shape);
                }
            }
        });
    }

    private void check_ship(int i, int j) { //i: fila, j: columna (matrix[fila][colunma] i,j de 0 a 9
        boolean barco, bien; // barco: true si el barco está bien colocado, false si no // bien: si hay el numero de casillas  que deberia tener el barco marcadas en la matriz es true
        boolean horizontal = false;
        if ((matrix[i+1][j][0] == ship) || (matrix[i - 1][j][0] == ship)) {
            horizontal = false;
            barco = true;
        } else if ((matrix[i][j+1][0] == ship) || (matrix[i][j - 1][0] == ship)) {
            horizontal = true;
            barco = true;
        } else barco = false;
        if (barco) {
            int c = 0;
            if (!horizontal) { //vertical
                for (int k = 0; k <= 9; k++) {
                    if (matrix[k][j][0] == ship) {
                        for (int x = k + casillas - 1; x >= k; x--) { //x: ultima casilla donde debería haber barco, desde ahí contamos las casillas marcadas
                            if (matrix[x][j][0] == ship) c++;
                        }
                        break;
                    }
                }
            } else { //horizontal
                for (int k = 0; k <= 9; k++) {
                    if (matrix[i][k][0] == ship) {
                        for (int x = k + casillas - 1; x >= k; x--) {
                            if (matrix[i][x][0] == ship) c++;
                        }
                        break;
                    }
                }
            }
            bien = (c == casillas);
        } else bien = false;
        if (!barco || !bien) {
            AlertDialog alert = new AlertDialog.Builder(this)
                    .setMessage(R.string.noship)
                    .setCancelable(false)
                    .setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            for(int i = 0; i < 10; i++){
                                for (int j = 0; j < 10; j++){
                                    if( matrix[i][j][0] == ship ){
                                        matrix[i][j][0] = 0;
                                        views[i][j].setBackgroundResource(R.drawable.cell_shape);
                                    }
                                }
                            }
                            ship = 0;
                            colocados = 0;
                            casillas = 0;
                            numfilter = 0;
                            infoship.setText("");
                        }
                    })
                    .show();
        } else {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            image.setColorFilter(filter);
            total_ships++;
            colorfilter[numfilter] = image;
            numfilter++;
            removeClickListenerViews();
        }
    }
    
    private void alertShip(){
        AlertDialog alert = new AlertDialog.Builder(this)
                .setMessage(R.string.placeshipfirst)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
    }


    private void colocarBarcos() {
        AlertDialog alert = new AlertDialog.Builder(this)
                .setMessage(R.string.placeallships)
                .setCancelable(false)
                .setPositiveButton(R.string.doit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
    }


    private void resetBoard(){
        AlertDialog alert = new AlertDialog.Builder(this)
                .setMessage(R.string.deleteconf)
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        for(int i = 0; i < 10; i++){
                            for (int j = 0; j < 10; j++){
                                matrix[i][j][0] = 0;
                                views[i][j].setBackgroundResource(R.drawable.cell_shape);
                            }
                        }
                        for(int i = 0; i < numfilter; i++){
                            colorfilter[i].clearColorFilter();
                        }
                        ship = 0;
                        colocados = 0;
                        casillas = 0;
                        numfilter = 0;
                        infoship.setText("");
                    }
                })
                .show();

    }

    public static class AlertDialogInfo extends DialogFragment {
        public static NewGameActivity.AlertDialogInfo newInstance() {
            return new AlertDialogInfo();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.info);
            builder.setMessage(R.string.shipinfo);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            return builder.create();
        }
    }

}
