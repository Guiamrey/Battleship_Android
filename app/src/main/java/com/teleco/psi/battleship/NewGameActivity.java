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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;


public class NewGameActivity extends Activity {
    private static float[][][] matrix = new float[10][10][3];
    /**
     * Matriz donde se guardan las views que componen el tablero
     */
    private static View[][] views = new View[10][10];
    /**
     * - ship: valor asociado al barco que se está colocando en el momento que se guardará en la matriz. Ver wiki para más info.
     * - colocados: numero de casillas marcadas durante la colocación de un barco. Aumenta cada vez que el usuario pulsa una casilla. Disminuye si el usuario pulsa una casilla ya marcada.
     * - casillas: número de casillas que debe de ocupar el barco que se está colocando en el momento.
     * - numfilter: número de views cambiadas a blanco y negro.
     */
    private int ship, colocados, casillas, numfilter;
    private int total_ships = 0, placed[] = new int[7];
    private TextView infoship;
    /**
     * - image: ImageView del barco que se ha pulsado para colocar. Sirve para ponerla en balnco y negro cuando el barco esta colcoado correctamente.
     * - colorfilter: conjunto de ImageView a que se les ha aplicado el filtro de blanco y negro. Al resetear el tablero vuelven al color original.
     */
    private ImageView image, colorfilter[] = new ImageView[5];
    private boolean allow_adjacent_ships;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            System.setErr(new PrintStream(new FileOutputStream(new File("sdcard/log.file"))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (float[][] row : matrix) {
            for (float[] column : row) {
                column[0] = 0;
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        /** Es el botón para resetear el tablero */
        ImageButton delete = (ImageButton) findViewById(R.id.trashicon);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBoard();
            }
        });

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame);
        final TableLayout table = createBoard();
        frameLayout.addView(table);

        /** Para comenzar el juego. Se comprueba primero que se hayan colocado todos los barcos */
        Button startGameButton = (Button) findViewById(R.id.start_game);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (total_ships == 5) {
                    System.out.println("total ships "+total_ships);
                    SharedPreferences settings = getSharedPreferences("Matrix", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(matrix);
                    editor.putString("Matrix", json);
                    editor.commit();
                    Intent start_game = new Intent(getApplicationContext(), GameActivity.class);
                    startActivity(start_game);
                    finish();
                } else {
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

        int CASILLAS5 = 6, CASILLAS4 = 5, CASILLAS3_1 = 4, CASILLAS3_2 = 3, CASILLAS2 = 2;

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
        allow_adjacent_ships = getSharedPreferences("Adyacent_ships", Context.MODE_PRIVATE).getBoolean("checked", false);
        for (int i = 2; i < placed.length; i++)
            placed[i] = 0;
    }

    /**
     * Añade el ClickListener a las ImageViews de los barcos.
     *
     * @param v    ImageView del barco.
     * @param tipo Según el barco que sea tiene asociado un valor u otro, para que cada barco esté identificado en la matriz con un número diferente.
     * @param num  Número de casillas que ocupa el barco.
     */
    private void onClickListener(ImageView v, final int tipo, final int num) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (colocados == casillas) {
                    if (placed[tipo] != 1) {
                        infoship.setText(getString(R.string.shipof) + num + getString(R.string.squares));
                        colocados = 0;
                        ship = tipo;
                        casillas = num;
                        image = (ImageView) v;
                        addClickListenerViews();
                    } else {
                        infoship.setText(R.string.another);
                    }
                } else {
                    alertShip();
                }
            }
        });
    }

    /**
     * Se añaden los ClickListener a las Views. Llama a la función 'addClickListener'.
     */
    private void addClickListenerViews() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (matrix[i][j][0] == 0)
                    addClickListener(views[i][j], i + 1, j + 1);
            }
        }
    }

    /**
     * Elimina el ClickListener de todas las Views del tablero, para que no se puedan pulsar si no hay marcado ningún barco para colocar.
     */
    private void removeClickListenerViews() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                views[i][j].setOnClickListener(null);
            }
        }
    }

    /**
     * Crea el tablero para marcar las casillas.
     *
     * @return Devuelve el TableLayout'.
     */
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
                    views[i - 1][j - 1] = field;
                }
                if (i == 0) {
                    field.setText(num[j]);
                    field.setTextColor(getResources().getColor(R.color.ColorWhite));
                } else if (j == 0) {
                    field.setText(AJ[i - 1]);
                    field.setTextColor(getResources().getColor(R.color.ColorWhite));
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

    /**
     * Añade el ClickListener a las casillas de la matriz.
     *
     * @param view View correspondiente a la casilla a añadir el ClickListener
     * @param i    fila de la View para, cuando tocada, marcar en la matriz el valor correspondiente guardado en 'ship' (De 1 a 10).
     * @param j    columna de la View para, cuando tocada, marcar en la matriz el valor correspondiente guardado en 'ship' (De 1 a 10).
     */
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
                    if (colocados == casillas) check_ship(i - 1, j - 1);
                } else {
                    matrix[i - 1][j - 1][0] = 0;
                    colocados--;
                    v.setBackgroundResource(R.drawable.cell_shape);
                }
            }
        });
    }

    /**
     * Comprueba que el barco está bien colocado. Las casillas no están dispersas, el barco no está en L, si se tiene que comprobar que no pueda haber barcos juntos se llama a 'shipsTogether'.
     * Se comprueba primero la orientación del barco.
     *
     * @param i fila de la última casilla colocada del barco (de 0 a 9)
     * @param j columna de la última casilla colocada del barco (de 0 a 9)
     *          <p>
     *          Dentro de la función:
     *          - boolean barco: se establece el valor a true o false si el barco está bien colocado.
     *          - boolean bien: se establece el valor a true o false si en la fila/columna en la que está orientado el barco hay el número de casillas correctas marcadas con el valor asociado al barco.
     *          - int primera: casilla donde empieza el barco.
     *          - int ultima: casilla donde termina el barco.
     *          - int line: fila o columna donde está ubicado el barco.
     */
    private void check_ship(int i, int j) {
        boolean barco, bien;
        boolean horizontal = false;
        int primera = 0, ultima = 0, line = 0;
        if (i == 9) { // si la fila es la última comprobar filas hacia arriba
            if ((matrix[i - 2][j][0] == ship) || (matrix[i - 1][j][0] == ship)) {
                horizontal = false;
                barco = true;
            } else if (j != 0) {
                if ((matrix[i][j + 1][0] == ship) || (matrix[i][j - 1][0] == ship)) {
                    horizontal = true;
                    barco = true;
                } else barco = false;
            } else if ((matrix[i][j + 1][0] == ship) || (matrix[i][j + 2][0] == ship)) {
                horizontal = true;
                barco = true;
            } else barco = false;
        } else if (j == 9) { // si la columna es la ultima comprobar columnas hacia atras
            if ((matrix[i][j - 2][0] == ship) || (matrix[i][j - 1][0] == ship)) {
                horizontal = true;
                barco = true;
            } else if (i != 0) {
                if ((matrix[i + 1][j][0] == ship) || (matrix[i - 1][j][0] == ship)) {
                    horizontal = false;
                    barco = true;
                } else barco = false;
            } else if ((matrix[i + 1][j][0] == ship) || (matrix[i + 2][j][0] == ship)) {
                horizontal = false;
                barco = true;
            } else barco = false;
        } else if (i == 0) { // si la fila es la primera  comprobar filas hacia abajo
            if ((matrix[i + 2][j][0] == ship) || (matrix[i + 1][j][0] == ship)) {
                horizontal = false;
                barco = true;
            } else if (j != 0) {
                if ((matrix[i][j + 1][0] == ship) || (matrix[i][j - 1][0] == ship)) {
                    horizontal = true;
                    barco = true;
                } else barco = false;
            } else if ((matrix[i][j + 1][0] == ship) || (matrix[i][j + 2][0] == ship)) {
                horizontal = true;
                barco = true;
            } else barco = false;
        } else if (j == 0) { // si la columna es la primera comprobar columnas hacia delante
            if ((matrix[i][j + 2][0] == ship) || (matrix[i][j + 1][0] == ship)) {
                horizontal = true;
                barco = true;
            } else if ((matrix[i + 1][j][0] == ship) || (matrix[i - 1][j][0] == ship)) {
                horizontal = false;
                barco = true;
            } else barco = false;
        } else {
            if ((matrix[i + 1][j][0] == ship) || (matrix[i - 1][j][0] == ship)) {
                horizontal = false;
                barco = true;
            } else if ((matrix[i][j + 1][0] == ship) || (matrix[i][j - 1][0] == ship)) {
                horizontal = true;
                barco = true;
            } else barco = false;
        }
        if (barco) {
            /** Si el barco está bien colocado se procede a contar el número de casillas en la posición donde debería estar el barco. */
            int c = 0; /** Se cuentas las casillas con el valor asociado al barco, guardado en la variable global 'ship' */
            if (!horizontal) { //vertical
                for (int k = 0; k <= 9; k++) {
                    if (matrix[k][j][0] == ship) {
                        primera = k;
                        ultima = k + casillas - 1;
                        line = j;
                        for (int x = ultima; x >= k; x--) { /** Desde 'ultima' contamos las casillas marcadas yendo hacia atrás */
                            if (matrix[x][j][0] == ship) c++;
                        }
                        break;
                    }
                }
            } else { //horizontal
                for (int k = 0; k <= 9; k++) {
                    if (matrix[i][k][0] == ship) {
                        primera = k;
                        ultima = k + casillas - 1;
                        line = i;
                        for (int x = ultima; x >= k; x--) {
                            if (matrix[i][x][0] == ship) c++;
                        }
                        break;
                    }
                }
            }
            bien = (c == casillas);
        } else bien = false;
        if (!barco || !bien) {
            /** Si el barco no está bien colocado o no hay el número de casillas correcto se lanza un diálogo para resetear el barco y volver a colocarlo. */
            AlertDialog alert = new AlertDialog.Builder(this)
                    .setMessage(R.string.noship)
                    .setCancelable(false)
                    .setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            for (int i = 0; i < 10; i++) {
                                for (int j = 0; j < 10; j++) {
                                    if (matrix[i][j][0] == ship) {
                                        matrix[i][j][0] = 0;
                                        views[i][j].setBackgroundResource(R.drawable.cell_shape);
                                    }
                                }
                            }
                            colocados = 0;
                        }
                    })
                    .show();
        } else {
            /** Si la opción de barcos adyacentes no está marcada se comprueba que no haya barcos juntos. Si la función 'shipTogether' devuelve false se lanza un diálogo para informar al usuario
             * de la colocación inválida del barco y resetearlo
             */
            if (!allow_adjacent_ships) {
                if (!shipsTogether(primera, ultima, line, horizontal)) {
                    AlertDialog alert = new AlertDialog.Builder(this)
                            .setMessage(R.string.noadjacent)
                            .setCancelable(false)
                            .setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    for (int i = 0; i < 10; i++) {
                                        for (int j = 0; j < 10; j++) {
                                            if (matrix[i][j][0] == ship) {
                                                matrix[i][j][0] = 0;
                                                views[i][j].setBackgroundResource(R.drawable.cell_shape);
                                            }
                                        }
                                    }
                                    colocados = 0;
                                }
                            })
                            .show();
                } else {
                    /** Una vez comprobado que el barco está completamente bien colocado, se marca la imagen del barco tocada en blanco y negro para no dejar tocarla, de aumenta el número de barcos colocados
                     * y se eliminan los ClickListener de las casillas hasta que se vuelva tocar un barco.
                     * Se resetea el TextView que indica el número de casillas a colocar.
                     */
                    ColorMatrix matrix = new ColorMatrix();
                    matrix.setSaturation(0);
                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                    image.setColorFilter(filter);
                    total_ships++;
                    colorfilter[numfilter] = image;
                    numfilter++;
                    removeClickListenerViews();
                    infoship.setText("");
                    placed[ship] = 1;
                }
            } else {
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                image.setColorFilter(filter);
                total_ships++;
                colorfilter[numfilter] = image;
                numfilter++;
                removeClickListenerViews();
                infoship.setText("");
                placed[ship] = 1;
            }
        }
        if (total_ships == 5) {
            infoship.setText(R.string.fuun);
        }
    }

    /**
     * Muestra un diálogo al tocar otro barco para colocarlo sin haber terminado el colocar el anterior para que se termine.
     */
    private void alertShip() {
        AlertDialog alert = new AlertDialog.Builder(this)
                .setMessage(R.string.placeshipfirst)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
    }

    /**
     * Muestra un diálogo al tocar el botón de comenzar juego sin haber colocado todos los barcos.
     * No deja continuar a la pantalla siguiente hasta haber colocado todos los barcos.
     */
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

    /**
     * Muestra un diálogo al tocar el icono de la papelera.
     * Si 'OK', resetea el tablero completo, borrando las casillas marcadas en negro y volviendo las imágenes de los barcos al color original.
     */
    private void resetBoard() {
        AlertDialog alert = new AlertDialog.Builder(this)
                .setMessage(R.string.deleteconf)
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        for (int i = 0; i < 10; i++) {
                            for (int j = 0; j < 10; j++) {
                                matrix[i][j][0] = 0;
                                views[i][j].setBackgroundResource(R.drawable.cell_shape);
                            }
                        }
                        for (ImageView aColorfilter : colorfilter) {
                            if (aColorfilter != null)
                                aColorfilter.setColorFilter(null);
                        }
                        for (int i = 2; i < placed.length; i++) {
                            placed[i] = 0;
                        }
                        total_ships = 0;
                        ship = 0;
                        colocados = 0;
                        casillas = 0;
                        numfilter = 0;
                        infoship.setText("");
                        removeClickListenerViews();
                    }
                })
                .show();

    }

    /**
     * Diálogo que aparece al tocar el botón de INFO en la colocación de los barcos.
     */
    public static class AlertDialogInfo extends DialogFragment {
        public static NewGameActivity.AlertDialogInfo newInstance() {
            return new AlertDialogInfo();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.howto);
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

    /**
     * Comprueba si alrededor del barco ya establecido hay un barco. Solo se llama si la opción de permitir barcos adyacentes está desmarcada en los ajustes.
     *
     * @param primero    casilla donde empieza el barco (empezando desde la casilla 0).
     * @param ultimo     casilla donde termina el barco.
     * @param line       fila o columna donde está ubicado el barco.
     * @param horizontal boolean que indica la orientación del barco.
     * @return retorna true si se puede colocar el barco y false si no.
     */
    private boolean shipsTogether(int primero, int ultimo, int line, boolean horizontal) {

        if (horizontal) { //Horizontal
            for (int i = primero; i <= ultimo; i++) {
                //COMPROBAR ENCIMA: Comprobamos las posiciones que están justo encima del barco (siempre y cuando no estén en el borde del tablero)
                if (line > 0) {
                    if (matrix[line - 1][i][0] != 0) return false;
                }
                //COMPROBAR DEBAJO: Comprobamos las posiciones que están justo debajo del barco (siempre y cuando no estén en el borde del tablero)
                if (line < 9) {
                    if (matrix[line + 1][i][0] != 0) return false;
                }
            }
            //COMPROBAR LATERAL IZQUIERDO: Comprobamos el lateral izquierdo del barco, siempre que estas posiciones no coincidan con los bordes del tablero.
            if (primero != 0) { //Si no coincide con el borde izquierdo...
                //Justo la punta del barco
                if (matrix[line][primero - 1][0] != 0) return false;
                //Esquina superior izquierda (si se puede)
                if (line > 0) {
                    if (matrix[line - 1][primero - 1][0] != 0) return false;
                }
                //Esquina inferior izquierda (si se puede)
                if (line < 9) {
                    if (matrix[line + 1][primero - 1][0] != 0) return false;
                }
            }
            //COMPROBAR LATERAL DERECHO: Comprobamos el lateral derecho del barcho, siempre que estas posiciones no coincidan con los bordes del tablero.
            if (ultimo != 9) { //Si no coincide con el borde derecho...
                //Justo la punta del barco
                if (matrix[line][ultimo + 1][0] != 0) return false;
                //Esquina superior derecha (si se puede)
                if (line > 0) {
                    if (matrix[line - 1][ultimo + 1][0] != 0) return false;
                }
                //Esquina inferior derecha (si se puede)
                if (line < 9) {
                    if (matrix[line + 1][ultimo + 1][0] != 0) return false;
                }
            }
        } else {
            for (int i = primero; i <= ultimo; i++) {
                //COMPROBAR BORDE IZQUIERDO: Comprobamos las posiciones que están justo en el borde izquierdo del barco (siempre y cuando no estén en el borde del tablero)
                if (line > 0) {
                    if (matrix[i][line - 1][0] != 0) return false;
                }
                //COMPROBAR BORDE DERECHO: Comprobamos las posiciones que están justo en el borde derecho del barco (siempre y cuando no estén en el borde del tablero)
                if (line < 9) {
                    if (matrix[i][line + 1][0] != 0) return false;
                }
            }
            //COMPROBAR ENCIMA: Comprobamos las posiciones encima del barco, siempre que estas no coincidan con los bordes del tablero.
            if (primero != 0) { //Si no coincide con el borde izquierdo...
                //Justo la punta del barco
                if (matrix[primero - 1][line][0] != 0) return false;
                //Esquina superior izquierda (si se puede)
                if (line > 0) {
                    if (matrix[primero - 1][line - 1][0] != 0) return false;
                }
                //Esquina inferior izquierda (si se puede)
                if (line < 9) {
                    if (matrix[primero - 1][line + 1][0] != 0) return false;
                }
            }
            //COMPROBAR DEBAJO: Comprobamos las posiciones debajo del barco, siempre que estas no coincidan con los bordes del tablero.
            if (ultimo != 9) { //Si no coincide con el borde derecho...
                //Justo la punta del barco
                if (matrix[ultimo + 1][line][0] != 0) return false;
                //Esquina superior derecha (si se puede)
                if (line > 0) {
                    if (matrix[ultimo + 1][line - 1][0] != 0) return false;
                }
                //Esquina inferior derecha (si se puede)
                if (line < 9) {
                    if (matrix[ultimo + 1][line + 1][0] != 0) return false;
                }
            }
        }
        return true;
    }

}
