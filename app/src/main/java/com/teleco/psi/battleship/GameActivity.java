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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.WorkSource;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class GameActivity extends Activity {
    private boolean supershots;
    /// CONSTANT
    private static final int FIRST_POS = 0;
    private static final int LAST_POS = 9;
    private static final int MATRIX_SIZE = 10;
    private static final int ROW = 0;
    private static final int COLUMN = 1;
    private static final int VALUE = 2;
    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    private static final int SHIPS = 0;
    private static final int GAME_STATE = 1;
    private static final int PROBABILITY = 2;
    private static final int TOUCHED = 2;
    private static final int WATER = 1;
    private static final int UNKNOWN = 0;

    ////

    private static float alpha = 0.2f;
    private static int totalGames;
    private FrameLayout light;
    private float[][][] matrixHuman = new float[MATRIX_SIZE][MATRIX_SIZE][3];
    private static float[][][] matrixMachine = new float[MATRIX_SIZE][MATRIX_SIZE][3];
    private static View[][] viewsHuman = new View[MATRIX_SIZE][MATRIX_SIZE];
    private static View[][] viewsMachine = new View[MATRIX_SIZE][MATRIX_SIZE];

    private static float[][] matrixBaseAttack = new float[MATRIX_SIZE][MATRIX_SIZE];
    private static float[][] matrixBaseDefense = new float[MATRIX_SIZE][MATRIX_SIZE];

    private FrameLayout frameLayoutHuman;
    private boolean stopUserInteractions = false;
    private static int shipsDownIA = 0;
    private static int shipsDownHuman = 0;
    private Handler wait = new Handler();
    private boolean vertical = false;
    private boolean horizontal = false;
    private boolean lastHit = false;
    private int row, column;
    private int pos = 1, orientation = 1;
    private int invertCounter = 0;
    private float[] lastAction = new float[3];
    private boolean IATurn = false;
    private boolean humanTurn = false;
    private static int winner;
    private static final int NUMBER_SHIPS = 17;
    private static int step = 1;
    private boolean allow_adjacent_ships;
    private int level = 0;
    private static final int EASY = 0;
    private static final int MEDIUM = 1;
    private static final int HARD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Comprobar el modo de juego: Clásico o superdisparos
         *  Clásico y Classic contienen 'sic'. Si lo guardado en ajustes no lo contiene entonces el modo de juego es Supershots
         */
        supershots = !getSharedPreferences("Rules", Context.MODE_PRIVATE).getString("Rules", "Classic").contains("sic");
        log("Modo de juego--> "+getSharedPreferences("Rules", Context.MODE_PRIVATE).getString("Rules", "Classic"));
        log("Idioma --> "+getSharedPreferences("Language" , Context.MODE_PRIVATE).getString("Language","en"));
        log("Barcos adyacentes --> "+getSharedPreferences("Adyacent_ships" , Context.MODE_PRIVATE).getBoolean("checked",false));
        log("Dificultad --> "+getSharedPreferences("Level", Context.MODE_PRIVATE).getString("Level", "Easy"));

        log("Default language --> "+Locale.getDefault().getDisplayLanguage());
        String lev = getSharedPreferences("Level", Context.MODE_PRIVATE).getString("Level", "Easy");
        if(lev.equalsIgnoreCase("easy") || lev.equalsIgnoreCase("facil")){
            level = EASY;
        }else if(lev.equalsIgnoreCase("medium") || lev.equalsIgnoreCase("Medio")){
            level = MEDIUM;
        }else level = HARD;

        allow_adjacent_ships = getSharedPreferences("Adyacent_ships", Context.MODE_PRIVATE).getBoolean("checked", false);

        setContentView(R.layout.game_activity);
        shipsDownIA = 0;
        shipsDownHuman = 0;
        step = 1;
        invertCounter = 0;
        vertical = false;
        horizontal = false;
        lastHit = false;
        orientation = 1;
        pos = 1;
        light = (FrameLayout) findViewById(R.id.semaforo);
        light.setBackgroundResource(R.drawable.verde);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent start_game = new Intent(getApplicationContext(), NewGameActivity.class);
                        startActivity(start_game);
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.resetinggame).setMessage(R.string.resetgamesure).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton("No", dialogClickListener);

        Button startGameButton = (Button) findViewById(R.id.newGameButton);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });
        frameLayoutHuman = (FrameLayout) findViewById(R.id.boardHuman);
        FrameLayout frameLayoutMachine = (FrameLayout) findViewById(R.id.boardMachine);
        frameLayoutHuman.addView(createBoard(false));
        frameLayoutMachine.addView(createBoard(true));

        setMatrixMachine();

        SharedPreferences settings = getSharedPreferences("Matrix", 0);
        Gson gson = new Gson();
        String json = settings.getString("Matrix", "");
        matrixHuman = gson.fromJson(json, float[][][].class);
        TableLayout board = (TableLayout) frameLayoutHuman.getChildAt(0);
        for (int row = 1; row <= MATRIX_SIZE; row++) {
            TableRow rowTable = (TableRow) board.getChildAt(row);
            for (int column = 1; column <= MATRIX_SIZE; column++) {
                TextView field = (TextView) rowTable.getChildAt(column);
                if (matrixHuman[row - 1][column - 1][SHIPS] != 0) {
                    field.setBackgroundColor(Color.BLACK);
                    viewsHuman[row - 1][column - 1] = field;
                }
            }
        }

        inicializeBase();
    }

    private TableLayout createBoard(boolean clickable) {
        String[] AJ = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        String[] num = {"\\", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setPadding(0, 15, 0, 0);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams();
        layoutParams.setMargins(100, 0, 100, 0);
        layoutParams.height = 260;
        for (int i = 0; i <= MATRIX_SIZE; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams rowparams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            for (int j = 0; j <= MATRIX_SIZE; j++) { //i
                TextView field = new TextView(this);
                field.setBackgroundResource(R.drawable.cell_shape);
                if (i == 0) {
                    field.setText(num[j]);
                } else if (j == 0) field.setText(AJ[i - 1]);
                field.setTextSize(15);
                field.setPadding(8, 6, 0, 0);
                field.setGravity(Gravity.CENTER);
                if (clickable) {
                    if (!(i == 0 || j == 0)) {
                        viewsMachine[i - 1][j - 1] = field;
                        addClickListener(field, i, j);
                    }
                }
                row.addView(field, rowparams);
            }
            tableLayout.addView(row, layoutParams);
        }
        return tableLayout;

    }

    private void addClickListener(final TextView view, final int row, final int column) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matrixMachine[row - 1][column - 1][SHIPS] != 0) {
                    if (supershots) {
                        supershot((int) matrixMachine[row - 1][column - 1][SHIPS], false);
                    } else {
                        view.setBackgroundColor(Color.RED);
                        shipsDownHuman++;
                        matrixMachine = updateMatrixValues(matrixMachine, row, column, true);
                    }
                    checkFinalGame();
                    return;
                } else {
                    light.setBackgroundResource(R.drawable.rojo);
                    view.setBackgroundColor(Color.BLUE);
                    view.setOnClickListener(null);
                    matrixMachine = updateMatrixValues(matrixMachine, row, column, false);

                }
                IATurn = true;
                humanTurn = false;
                checkFinalGame();
                startAlgorithm();
            }
        });
    }

    private void supershot(int ship, boolean human) {
        if (human) {
            for (int fila = 0; fila < MATRIX_SIZE; fila++) {
                for (int columna = 0; columna < MATRIX_SIZE; columna++) {
                    if (matrixHuman[fila][columna][SHIPS] == ship) {
                        matrixHuman[fila][columna][GAME_STATE] = TOUCHED;
                        viewsHuman[fila][columna].setBackgroundColor(Color.RED);
                        matrixHuman = updateMatrixValues(matrixHuman, fila, columna, true);
                        shipsDownIA++;
                    }
                }
            }
        } else {
            for (int fila = 0; fila < MATRIX_SIZE; fila++) {
                for (int columna = 0; columna < MATRIX_SIZE; columna++) {
                    if (matrixMachine[fila][columna][SHIPS] == ship) {
                        matrixMachine[fila][columna][GAME_STATE] = TOUCHED;
                        viewsMachine[fila][columna].setBackgroundColor(Color.RED);
                        matrixMachine = updateMatrixValues(matrixMachine, fila, columna, true);
                        shipsDownHuman++;
                        viewsMachine[fila][columna].setOnClickListener(null);
                    }
                }
            }
        }
    }

    private void setMatrixMachine() {
        Random rand = new Random();
        boolean shipOK = false;

        //Barco de 5
        int line = rand.nextInt(MATRIX_SIZE);
        int direction = rand.nextInt(2); // 0 = horizontal, 1 = vertical

        int from = rand.nextInt(5);
        int to = from + 4;

        int CASILLAS5 = 6, CASILLAS4 = 5, CASILLAS3_1 = 4, CASILLAS3_2 = 3, CASILLAS2 = 2;

        log("SHIP 5 - true: Line: " + (line + 1) + " -- Direction: " + direction + " -- From: " + from + " -- To: " + to);
        setShip(from, to, line, direction, matrixMachine, CASILLAS5);

        //Barco de 4

        while (!shipOK) {
            line = rand.nextInt(MATRIX_SIZE);
            direction = rand.nextInt(2); // 0 = horizontal, 1 = vertical

            from = rand.nextInt(6);
            to = from + 3;

            shipOK = isAShip(from, to, line, direction, matrixMachine);
            log("SHIP 4 - " + shipOK + ": Line: " + (line + 1) + " -- Direction: " + direction + " -- From: " + from + " -- To: " + to);

            if (shipOK) {
                setShip(from, to, line, direction, matrixMachine, CASILLAS4);
            }

        }

        int shipThree = 0;
        int type = CASILLAS3_1;
        while (shipThree != 2) {

            line = rand.nextInt(MATRIX_SIZE);
            direction = rand.nextInt(2); // 0 = horizontal, 1 = vertical

            from = rand.nextInt(7);
            to = from + 2;

            shipOK = isAShip(from, to, line, direction, matrixMachine);

            log("SHIP 3 - " + shipOK + ": Line: " + (line + 1) + " -- Direction: " + direction + " -- From: " + from + " -- To: " + to);

            if (shipOK) {
                setShip(from, to, line, direction, matrixMachine, type);
                shipThree++;
                type = CASILLAS3_2;
            }
        }

        shipOK = false;

        while (!shipOK) {

            line = rand.nextInt(MATRIX_SIZE);
            direction = rand.nextInt(2); // 0 = horizontal, 1 = vertical

            from = rand.nextInt(8);
            to = from + 1;

            shipOK = isAShip(from, to, line, direction, matrixMachine);

            log("SHIP 2 - " + shipOK + ": Line: " + (line + 1) + " -- Direction: " + direction + " -- From: " + from + " -- To: " + to);

            if (shipOK) {
                setShip(from, to, line, direction, matrixMachine, CASILLAS2);
            }
        }
    }

    public static boolean isAShipTogether(int from, int to, int line, int direction, int[][][] matrixAux) {
        if (direction == 0) { //Horizontal
            for (int column = from; column <= to; column++) {
                if (matrixAux[line][column][SHIPS] != 0) return false;
            }
        } else { //Vertical
            for (int row = from; row <= to; row++) {
                if (matrixAux[row][line][SHIPS] != 0) return false;
            }
        }
        return true;
    }

    public static boolean isAShip(int from, int to, int line, int direction, float[][][] matrixAux) {
        if (direction == 0) { //Horizontal
            for (int i = from; i <= to; i++) {
                if (line > 0) {
                    if (matrixAux[line - 1][i][0] != 0) return false;
                }

                if ((i != 0) && (i != 9)) {

                    if (line > 0) {
                        if (matrixAux[line - 1][i - 1][0] != 0) return false;
                        if (matrixAux[line - 1][i + 1][0] != 0) return false;
                    }

                    if (matrixAux[line][i - 1][0] != 0) return false;
                    if (matrixAux[line][i + 1][0] != 0) return false;

                    if (line < 9) {
                        if (matrixAux[line + 1][i - 1][0] != 0) return false;
                        if (matrixAux[line + 1][i + 1][0] != 0) return false;
                    }

                }

                if (matrixAux[line][i][0] != 0) return false;

                if (line < 9) {
                    if (matrixAux[line + 1][i][0] != 0) return false;
                }
            }
        } else { //Vertical
            for (int i = from; i <= to; i++) {
                if (line > 0) {
                    if (matrixAux[i][line - 1][0] != 0) return false;
                }

                if (i != 0) {

                    if (line > 0) {
                        if (matrixAux[i - 1][line - 1][0] != 0) return false;
                        if (matrixAux[i + 1][line - 1][0] != 0) return false;
                    }

                    if (matrixAux[i - 1][line][0] != 0) return false;
                    if (matrixAux[i + 1][line][0] != 0) return false;

                    if (line < 9) {
                        if (matrixAux[i - 1][line + 1][0] != 0) return false;
                        if (matrixAux[i + 1][line + 1][0] != 0) return false;
                    }

                }

                if (matrixAux[i][line][0] != 0) return false;

                if (line < 9) {
                    if (matrixAux[i][line + 1][0] != 0) return false;
                }
            }
        }

        return true;
    }

    public void startAlgorithm() {
        stopUserInteractions = true;
        while (IATurn) {
            if (step == 1) {
                lastAction = choosePlay();
                lastHit = hitOrMiss((int) lastAction[ROW], (int) lastAction[COLUMN]);

                if (lastHit) {
                    step = 2;
                }
            } else if (supershots) {
                step = 1;
            } else if (step == 2) {
                bestAfterHit(lastAction);
            } else if (step == 3) {
                shipFound((int) lastAction[ROW], (int) lastAction[COLUMN]);
            }
        }
        //printMatrix();
    }

    /***
     * This function prints the matrix in console.
     * It only prints the boats, and the state of the box.
     */
    private void printMatrix() {
        log("\\   A    B    C    D    E    F    G    H    I    J");
        for (int row = 0; row < MATRIX_SIZE; row++) {
            log(Integer.toString(row + 1));
            for (int column = 0; column < MATRIX_SIZE; column++) {
                if (row == LAST_POS && column == FIRST_POS)
                    log(" " + matrixHuman[row][column][SHIPS] + "/" + matrixHuman[row][column][GAME_STATE]);
                else
                    log("  " + matrixHuman[row][column][SHIPS] + "/" + matrixHuman[row][column][GAME_STATE]);
            }
            log("\n");
        }
    }

    /***
     * This function choose the next shot when the only that we know is the matrix probability.
     * The function choose the higher probability value and return it. If all the probabilities are equals,
     * the function generates two randoms numbers to select the nest shot
     *
     * @return bestAction [3] bestAction[0]  row, bestAction[1]  column, bestAction[2]  probability of the shot.
     */
    private float[] choosePlay() {
        float[] bestAction = new float[3];
        bestAction[ROW] = -1;
        for (int row = 0; row < MATRIX_SIZE; row++) {
            for (int column = 0; column < MATRIX_SIZE; column++) {
                if (matrixHuman[row][column][GAME_STATE] == UNKNOWN && matrixHuman[row][column][PROBABILITY] > bestAction[PROBABILITY]) {
                    bestAction[ROW] = row;
                    bestAction[COLUMN] = column;
                    bestAction[VALUE] = matrixHuman[row][column][VALUE];
                }
            }
        }

        if (bestAction[ROW] == -1) {
            Random rand = new Random();

            while (true) {
                int column = rand.nextInt(MATRIX_SIZE);
                int row = rand.nextInt(MATRIX_SIZE);

                if (matrixHuman[row][column][GAME_STATE] == UNKNOWN) {
                    bestAction[ROW] = row;
                    bestAction[COLUMN] = column;
                    bestAction[VALUE] = matrixHuman[row][column][VALUE];
                    break;
                }
            }
        }
        return bestAction;
    }

    /**
     * This function check if the shot is success or not
     *
     * @param row    row of the shot
     * @param column column of the shot
     * @return boolean which indicates hit or not.
     */
    private boolean hitOrMiss(int row, int column) {
        final int X = row;
        final int Y = column;
        if (matrixHuman[row][column][SHIPS] != 0) {
            if (supershots) {
                supershot((int) matrixHuman[row][column][SHIPS], true);
            } else {
                matrixHuman[row][column][GAME_STATE] = TOUCHED; //tocado
                log("JUGADA: fila  " + (row + 1) + " columna  " + (column + 1) + " TOCADO");

                wait.postDelayed(new Runnable() {
                    public void run() {
                        drawHitOrMiss(X, Y, true);
                    }
                }, 2000);
                matrixHuman = updateMatrixValues(matrixHuman, row, column, true);
                shipsDownIA++;
            }
            checkFinalGame();
            return true;
        }

        matrixHuman[row][column][GAME_STATE] = WATER;    //agua
        log("JUGADA: fila  " + (row + 1) + " columna  " + (column + 1) + " AGUA");

        wait.postDelayed(new Runnable() {
            public void run() {
                drawHitOrMiss(X, Y, false);
                stopUserInteractions = false;
                light.setBackgroundResource(R.drawable.verde);
            }
        }, 2000);

        IATurn = !IATurn;
        humanTurn = !humanTurn;
        matrixHuman = updateMatrixValues(matrixHuman, row, column, false);
        checkFinalGame();
        return false;
    }

    private void drawHitOrMiss(int row, int column, boolean hit) {
        TableLayout board = (TableLayout) frameLayoutHuman.getChildAt(0);
        TableRow rowTable = (TableRow) board.getChildAt(row + 1);
        TextView field = (TextView) rowTable.getChildAt(column + 1);
        if (hit)
            field.setBackgroundColor(Color.RED);
        else
            field.setBackgroundColor(Color.BLUE);
    }

    /**
     * This function put the boats in the matrix
     *
     * @param from        where the boat starts
     * @param to          where the boat finish
     * @param line        the line where the boat is
     * @param direction   the horientation of the boat, vertical (1) or horizontal (0)
     * @param matrixHuman the board game
     * @param type        the number to identify each ship separately
     */
    private static void setShip(int from, int to, int line, int direction, float[][][] matrixHuman, int type) {
        if (direction == HORIZONTAL) { //Horizontal
            for (int column = from; column <= to; column++) {
                matrixHuman[line][column][SHIPS] = type;
            }
        } else { //Vertical
            for (int row = from; row <= to; row++) {
                matrixHuman[row][line][SHIPS] = type;
            }
        }
    }

    /**
     * Function that finds the possible plays around your last hit, it calls after you hit for
     * first time a boat, the function keep in mind if you are in the corners of the board or in the middle.. etc
     *
     * @param lastAction array of int with the params of the last shot. lastAction[0]  row, lastAction[1]  column
     * @return A list with the possible plays.
     */
    private List<String> findArround(float[] lastAction) {
        List<String> possiblePlays = new ArrayList<>();

        if (lastAction[ROW] == LAST_POS) {  // buscar hacia arriba o lados
            if (lastAction[COLUMN] == FIRST_POS) { //hacia arriba o derecha
                possiblePlays.add("9-1");
                possiblePlays.add("8-0");
            } else if (lastAction[COLUMN] == LAST_POS) { //hacia arriba o izquierda
                possiblePlays.add("8-9");
                possiblePlays.add("9-8");
            } else { // buscar mejor jugada proxima a ese barco
                possiblePlays.add("9-" + (lastAction[COLUMN] - 1));
                possiblePlays.add("9-" + (lastAction[COLUMN] + 1));
                possiblePlays.add("8-" + (lastAction[COLUMN]));
            }
        } else if (lastAction[ROW] == FIRST_POS) { // hacia abajo o los lados
            if (lastAction[COLUMN] == FIRST_POS) { // hacia abajo o derecha
                possiblePlays.add("0-1");
                possiblePlays.add("1-0");
            } else if (lastAction[COLUMN] == LAST_POS) { //hacia abajo o izquierda
                possiblePlays.add("0-8");
                possiblePlays.add("1-9");
            } else { // buscar mejor jugada proxima a ese barco
                possiblePlays.add("0-" + (lastAction[COLUMN] - 1));
                possiblePlays.add("0-" + (lastAction[COLUMN] + 1));
                possiblePlays.add("1-" + lastAction[COLUMN]);
            }
        } else if (lastAction[COLUMN] == FIRST_POS) {
            possiblePlays.add(lastAction[ROW] + "-" + (lastAction[COLUMN] + 1));
            possiblePlays.add((lastAction[ROW] + 1) + "-" + lastAction[COLUMN]);
            possiblePlays.add((lastAction[ROW] - 1) + "-" + lastAction[COLUMN]);
        } else if (lastAction[COLUMN] == LAST_POS) {
            possiblePlays.add(lastAction[ROW] + "-" + (lastAction[COLUMN] - 1));
            possiblePlays.add((lastAction[ROW] + 1) + "-" + lastAction[COLUMN]);
            possiblePlays.add((lastAction[ROW] - 1) + "-" + lastAction[COLUMN]);
        } else {
            possiblePlays.add((lastAction[ROW]) + "-" + (lastAction[COLUMN] + 1));
            possiblePlays.add((lastAction[ROW]) + "-" + (lastAction[COLUMN] - 1));
            possiblePlays.add((lastAction[ROW] + 1) + "-" + lastAction[COLUMN]);
            possiblePlays.add((lastAction[ROW] - 1) + "-" + lastAction[COLUMN]);
        }
        return possiblePlays;
    }

    /**
     * Function that calls when the IA hits a boat two times and try to found
     * the rest of the ship.
     * @param lastAction array of int with the params of the last shot. lastAction[0] row, lastAction[1]  column
     */
    private void bestAfterHit(float[] lastAction) {
        List<String> possiblePlays = findArround(lastAction);
        checkProbablyPos(possiblePlays, lastAction);
    }

    /**
     * This function checks the possible plays arround the last hit, and when find a good play (hit), the function locates
     * int the board the ship and call @function shipFound()
     * @param possiblePlays List with the possible plays where the boat can be
     * @param lastAction    array of int with the params of the last shot. lastAction[0]  row, lastAction[1]  column
     */
    private void checkProbablyPos(List<String> possiblePlays, float[] lastAction) {
        boolean hit;
        float[] bestAction = new float[3];

        for (String possiblePlay : possiblePlays) {
            String[] playsStr = possiblePlay.split("-");
            int row = Integer.parseInt(playsStr[ROW]);
            int column = Integer.parseInt(playsStr[COLUMN]);

            if (matrixHuman[row][column][GAME_STATE] == UNKNOWN && matrixHuman[row][column][PROBABILITY] >= bestAction[VALUE]) {
                bestAction[ROW] = row;
                bestAction[COLUMN] = column;
                bestAction[VALUE] = matrixHuman[row][column][PROBABILITY];
            }
        }

        hit = hitOrMiss((int) bestAction[ROW], (int) bestAction[COLUMN]);

        if (hit) {
            if (bestAction[ROW] == lastAction[ROW])  //si la fila donde hemos tocado, es la misma que la de la anterior jugada => horizontal
                horizontal = true;
            else
                vertical = true;

            step = 3;
        }
    }

    /**
     * Function calls after hit a boat again after two hits, and you know the aproximate possition of the boat
     * and if is vertical or horizontal. if is shotting in one direction and fails, them invert the direction of the shots
     * @param row    row where the IA knows that there is a boat there
     * @param column column where the IA knows that there is a boat there
     */
    private void shipFound(int row, int column) {
        boolean hit = false;
        int upDown = 0;
        int movement = 0;

        boolean keepOnTrying = true;

        while (keepOnTrying) {
            if (invertCounter == 2) {
                shipDown();
                break;
            }

            if (horizontal) {
                upDown = checkLimits(column - pos * orientation);
            } else if (vertical) {
                upDown = checkLimits(row - pos * orientation);
            }

            if (upDown == orientation) {
                changeDirection();
                continue;
            }

            //asignamos nuestra jugada
            if (horizontal) {
                movement = (int) matrixHuman[row][column - pos * orientation][GAME_STATE];
            } else if (vertical) {
                movement = (int) matrixHuman[row - pos * orientation][column][GAME_STATE];
            }


            switch (movement) {

                case UNKNOWN: { //si no se ha tirado, lo hacemos
                    if (horizontal) hit = hitOrMiss(row, column - pos * orientation);
                    else if (vertical) hit = hitOrMiss(row - pos * orientation, column);

                    if (hit) continueDirection();
                    else changeDirection();
                    keepOnTrying = false;
                    break;
                }

                case WATER: { //si es agua, cambiamos de direccion y seguimos
                    changeDirection();
                    break;
                }

                case TOUCHED: { //si hemos tocado, seguimos en esa direccion
                    pos++;
                    break;
                }
            }
        }
    }

    private void shipDown() {
        pos = 1;
        vertical = false;
        horizontal = false;
        invertCounter = 0;
        lastHit = false;
        step = 1;
    }

    private void changeDirection() {
        pos = 1;
        orientation = orientation * (-1);
        invertCounter++;
    }

    private void continueDirection() {
        lastHit = true;
        pos++;
    }

    /**
     * This function check the limits of the board.
     *
     * @param position index of the board
     * @return 0 for the position is good, 1, if your position is under the minimum index, and -1 if your position is over the maximun index
     */
    private int checkLimits(int position) {
        if (position < FIRST_POS) {
            return 1;
        } else if (position > LAST_POS) {
            return -1;
        } else {
            return 0;
        }
    }

    static int getWinner() {
        return winner;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return stopUserInteractions || super.dispatchTouchEvent(ev);
    }

    private void checkFinalGame() {
        if (shipsDownHuman == NUMBER_SHIPS) {
            winner = 1;
            DialogFragment endGameDialog = new AlertDialogEndGame().newInstance();
            endGameDialog.show(getFragmentManager(), "Alert");
        } else if (shipsDownIA == NUMBER_SHIPS) {
            winner = 2;
            DialogFragment endGameDialog = new AlertDialogEndGame().newInstance();
            endGameDialog.show(getFragmentManager(), "Alert");
        }
        totalGames++;
    }

    public void learningAttack() {
        for (int row = 0; row < MATRIX_SIZE; row++) {
            for (int column = 0; column < MATRIX_SIZE; column++) {
                matrixBaseAttack[row][column] = ((totalGames * matrixBaseAttack[row][column] - (alpha * matrixHuman[row][column][2])) / (totalGames + 1));
            }
        }
    }

    public static void learningDefense() {
        for (int row = 0; row < 10; row++) {
            for (int column = 0; column < 10; column++) {
                matrixBaseDefense[row][column] = (totalGames * matrixBaseDefense[row][column] - (alpha * matrixMachine[row][column][2])) / (totalGames + 1);
            }
        }
    }

    public void inicializeBase() {
        //centrales
        matrixHuman[4][4][2] = 100;
        matrixHuman[4][5][2] = 100;
        matrixHuman[5][4][2] = 100;
        matrixHuman[4][5][2] = 100;
        //rodeando las centrales
        matrixHuman[4][3][2] = 90;
        matrixHuman[4][6][2] = 90;
        matrixHuman[5][3][2] = 90;
        matrixHuman[5][6][2] = 90;
        matrixHuman[3][4][2] = 90;
        matrixHuman[3][5][2] = 90;
        matrixHuman[6][4][2] = 90;
        matrixHuman[6][5][2] = 90;
        //un nivel mas hacia afuera
        matrixHuman[3][3][2] = 82;
        matrixHuman[3][6][2] = 82;
        matrixHuman[6][3][2] = 82;
        matrixHuman[6][6][2] = 82;
        matrixHuman[4][2][2] = 82;
        matrixHuman[5][2][2] = 82;
        matrixHuman[4][7][2] = 82;
        matrixHuman[5][7][2] = 82;
        matrixHuman[2][4][2] = 82;
        matrixHuman[2][5][2] = 82;
        matrixHuman[7][4][2] = 82;
        matrixHuman[7][5][2] = 82;
        ///
        matrixHuman[3][2][2] = 73;
        matrixHuman[2][3][2] = 73;
        matrixHuman[2][6][2] = 73;
        matrixHuman[3][7][2] = 73;
        matrixHuman[6][2][2] = 73;
        matrixHuman[7][3][2] = 73;
        matrixHuman[6][7][2] = 73;
        matrixHuman[7][6][2] = 73;
        //
        matrixHuman[4][1][2] = 64;
        matrixHuman[5][1][2] = 64;
        matrixHuman[4][8][2] = 64;
        matrixHuman[5][8][2] = 64;
        matrixHuman[8][4][2] = 64;
        matrixHuman[8][5][2] = 64;
        matrixHuman[1][4][2] = 64;
        matrixHuman[1][5][2] = 64;
        matrixHuman[2][2][2] = 64;
        matrixHuman[2][7][2] = 64;
        matrixHuman[7][7][2] = 64;
        matrixHuman[7][2][2] = 64;
        //
        matrixHuman[3][1][2] = 55;
        matrixHuman[1][3][2] = 55;
        matrixHuman[1][6][2] = 55;
        matrixHuman[3][8][2] = 55;
        matrixHuman[6][1][2] = 55;
        matrixHuman[8][3][2] = 55;
        matrixHuman[6][8][2] = 55;
        matrixHuman[8][6][2] = 55;
        //
        matrixHuman[0][4][2] = 46;
        matrixHuman[0][5][2] = 46;
        matrixHuman[9][4][2] = 46;
        matrixHuman[9][5][2] = 46;
        matrixHuman[4][0][2] = 46;
        matrixHuman[5][0][2] = 46;
        matrixHuman[4][9][2] = 46;
        matrixHuman[5][9][2] = 46;
        matrixHuman[1][2][2] = 46;
        matrixHuman[2][1][2] = 46;
        matrixHuman[1][7][2] = 46;
        matrixHuman[2][8][2] = 46;
        matrixHuman[7][1][2] = 46;
        matrixHuman[8][2][2] = 46;
        matrixHuman[8][7][2] = 46;
        matrixHuman[7][8][2] = 46;
        //
        matrixHuman[0][3][2] = 36;
        matrixHuman[3][0][2] = 36;
        matrixHuman[0][6][2] = 36;
        matrixHuman[3][9][2] = 36;
        matrixHuman[1][7][2] = 36;
        matrixHuman[2][8][2] = 36;
        matrixHuman[6][0][2] = 36;
        matrixHuman[9][3][2] = 36;
        matrixHuman[9][6][2] = 36;
        matrixHuman[6][9][2] = 36;
        //
        matrixHuman[2][0][2] = 27;
        matrixHuman[1][1][2] = 27;
        matrixHuman[0][2][2] = 27;
        matrixHuman[0][7][2] = 27;
        matrixHuman[1][8][2] = 27;
        matrixHuman[2][9][2] = 27;
        matrixHuman[7][0][2] = 27;
        matrixHuman[8][1][2] = 27;
        matrixHuman[9][2][2] = 27;
        matrixHuman[9][7][2] = 27;
        matrixHuman[8][8][2] = 27;
        matrixHuman[7][9][2] = 27;
        //
        matrixHuman[0][1][2] = 18;
        matrixHuman[1][0][2] = 18;
        matrixHuman[0][8][2] = 18;
        matrixHuman[1][9][2] = 18;
        matrixHuman[8][0][2] = 18;
        matrixHuman[9][1][2] = 18;
        matrixHuman[8][9][2] = 18;
        matrixHuman[9][8][2] = 18;
        //
        matrixHuman[0][0][2] = 0;
        matrixHuman[0][9][2] = 0;
        matrixHuman[9][0][2] = 0;
        matrixHuman[9][9][2] = 0;
    }

    /*
        public void placeShipsIA(){
            int[] bestPlace = new int[3];
            bestPlace[0] = -1;
            for (int i = 0; i < matrixMachine.length ; i++) {
                for (int j = 0; j < matrixMachine.length ; j++) {
                    if (matrixMachine[i][j][0] == 0 && matrixMachine[i][j][2] > bestPlace[2]){
                        bestPlace[0] = i;
                        bestPlace[1] = j;
                        bestPlace[2] = matrixMachine[i][j][2];
                    }
                }
            }

            if(bestPlace[0] == -1) {
                Random rand = new Random();
                while(true) {
                    int x = rand.nextInt(9);
                    int y = rand.nextInt(9);

                    if(matrixMachine[x][y][0] == 0) {
                        bestPlace[0] = x;
                        bestPlace[1] = y;
                        bestPlace[2] =  matrixMachine[x][y][2];
                        break;
                    }
                }
            }
            placeCarrier(bestPlace);
        }
    /*
        public double probabilityPlace(int from, int to, int line, int direction){
            double sum = 0;

            if (direction==0){ //Horizontal
                for (int i=from; i<=to; i++){
                    sum += matrixHuman[line][i][2];
                }
            } else { //Vertical
                for (int i=from; i<=to; i++){
                    sum += matrixHuman[i][line][2];
                }
            }
            return sum/(to-from);
        }
    /*
        private void placeCarrier(int[] bestPlace){
            List<String> possiblePlace = placesWithCorrectSize(bestPlace, 5);
            boolean shipFree;

            for (String aPossiblePlace : possiblePlace) {
                String[] placeStr = aPossiblePlace.split("-");
                int row = Integer.parseInt(placeStr[0]);
                int column = Integer.parseInt(placeStr[1]);

                if (bestPlace[0] == row) {
                    if (bestPlace[1] > column) {
                        shipFree = isAShip(bestPlace[1] - 5, bestPlace[1], row, 0);
                    } else {
                        shipFree = isAShip(bestPlace[1], bestPlace[1] + 5, row, 0);
                    }
                } else {
                    if (bestPlace[0] > column) {
                        shipFree = isAShip(bestPlace[0] + 5, bestPlace[0], row, 0);
                    } else {
                        shipFree = isAShip(bestPlace[0], bestPlace[0] + 5, row, 0);
                    }
                }

                if (shipFree) {
                    //probabilityPlace();
                }
            }
        }
    */
    private List<String> placesWithCorrectSize(int[] bestPlace, int sizeBoat) { //el menos deberia quitarse si los barcos no pueden estar juntos
        List<String> possiblePlays = new ArrayList<>();
        if ((bestPlace[0] + sizeBoat - 1) < 10) {
            possiblePlays.add((bestPlace[0] + 1) + "-" + bestPlace[1]);
        }
        if ((bestPlace[0] - sizeBoat - 1) >= 0) {
            possiblePlays.add((bestPlace[0] - 1) + "-" + bestPlace[1]);
        }
        if ((bestPlace[1] + sizeBoat - 1) < 10) {
            possiblePlays.add(bestPlace[0] + "-" + (bestPlace[1] + 1));
        }
        if ((bestPlace[1] - sizeBoat - 1) >= 0) {
            possiblePlays.add(bestPlace[0] + "-" + (bestPlace[1] - 1));
        }
        return possiblePlays;
    }

    private static boolean isAShip(int from, int to, int line, int direction) {

        if (direction == HORIZONTAL) { //Horizontal
            for (int column = from; column <= to; column++) {
                if (matrixMachine[line][column][SHIPS] != 0) return false;
            }
        } else { //Vertical
            for (int row = from; row <= to; row++) {
                if (matrixMachine[row][line][SHIPS] != 0) return false;
            }
        }
        return true;
    }

    public void log(String text) {
        File logFile = new File("sdcard/log.file");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Se llama cuando se pulsa el botón de atrás del dispositivo, para que el jugador confirme si quiere salir de la partida en juego.
     */
    @Override
    public void onBackPressed() {
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage(R.string.leave_game)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
    }


    public float[][][] updateMatrixValues(float matrix[][][], int row, int column, boolean hit) {
        System.out.println("ANTES");

        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                if (i == LAST_POS && j == FIRST_POS)
                    System.out.print(matrix[i][j][PROBABILITY] + " ");
                else System.out.print(matrix[i][j][PROBABILITY] + " ");
            }
            System.out.println("\n");
        }
        System.out.println("DESPUES");

        matrix[row][column][PROBABILITY] = updatePos(matrix[row][column][PROBABILITY], hit, 1);
        List<String> pos = getAdjacentPos(matrix, row, column);
        for (String posUpdate : pos) {
            String[] posStr = posUpdate.split("-");
            int rowUptdate = Integer.parseInt(posStr[ROW]);
            int columnUpdate = Integer.parseInt(posStr[COLUMN]);
            matrix[rowUptdate][columnUpdate][PROBABILITY] = updatePos(matrix[rowUptdate][columnUpdate][PROBABILITY], hit, 2);
        }
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                if (i == LAST_POS && j == FIRST_POS)
                    System.out.print(matrix[i][j][PROBABILITY] + " ");
                else System.out.print(matrix[i][j][PROBABILITY] + " ");
            }
            System.out.println("\n");
        }
        System.out.println("---------------------");

        return matrix;
    }

    public float updatePos(float target, boolean hit, int level) {
        if (hit) {
            if (target + 20 / level > 100) target = 100;
            else target += 20 / level;
        } else {
            if (target - 20 / level < 0) target = 0;
            else target -= 20 / level;
        }
        return target;
    }

    public List<String> getAdjacentPos(float matrix[][][], int row, int column) {
        List<String> possiblePlays = new ArrayList<>();

        if (row == LAST_POS) {  // buscar hacia arriba o lados
            if (column == FIRST_POS) { //hacia arriba o derecha
                possiblePlays.add("9-1");
                possiblePlays.add("8-0");
            } else if (column == LAST_POS) { //hacia arriba o izquierda
                possiblePlays.add("8-9");
                possiblePlays.add("9-8");
            } else { // buscar mejor jugada proxima a ese barco
                possiblePlays.add("9-" + (column - 1));
                possiblePlays.add("9-" + (column + 1));
                possiblePlays.add("8-" + (column));
            }
        } else if (row == FIRST_POS) { // hacia abajo o los lados
            if (column == FIRST_POS) { // hacia abajo o derecha
                possiblePlays.add("0-1");
                possiblePlays.add("1-0");
            } else if (column == LAST_POS) { //hacia abajo o izquierda
                possiblePlays.add("0-8");
                possiblePlays.add("1-9");
            } else { // buscar mejor jugada proxima a ese barco
                possiblePlays.add("0-" + (column - 1));
                possiblePlays.add("0-" + (column + 1));
                possiblePlays.add("1-" + column);
            }
        } else if (column == FIRST_POS) {
            possiblePlays.add(row + "-" + (column + 1));
            possiblePlays.add((row + 1) + "-" + column);
            possiblePlays.add((row - 1) + "-" + column);
        } else if (column == LAST_POS) {
            possiblePlays.add(row + "-" + (column - 1));
            possiblePlays.add((row + 1) + "-" + column);
            possiblePlays.add((row - 1) + "-" + column);
        } else {
            possiblePlays.add(row + "-" + (column + 1));
            possiblePlays.add(row + "-" + (column - 1));
            possiblePlays.add((row + 1) + "-" + column);
            possiblePlays.add((row - 1) + "-" + column);
        }

        return possiblePlays;
    }
}