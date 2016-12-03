package com.teleco.psi.battleship;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends Activity {
    private static double [][] matrixBaseAttack = new double[10][10];
    private static double [][] matrixBaseDefense = new double[10][10];
    private static double alpha = 0.2;
    private static int totalGames;

    private static int [][][] matrixHuman = new int[10][10][3];
    private static int [][][] matrixMachine = new int[10][10][3];
    private static FrameLayout frameLayoutHuman;
    private FrameLayout frameLayoutMachine;

    static int shipsDownIA = 0;
    static int shipsDownHuman = 0;
    private static int vertical = 0;
    private static int horizontal = 0;
    private static boolean lastHit = false;
    private static int row, column;
    private static int pos = 1, sentido = 1;
    private static int sentidosInvertidos =0 ;
    private static int[] lastAction = new int[3];
    private static boolean IATurn = false;
    private static boolean humanTurn = false;


    private static int winner;

    private static final int NUMBER_SHIPS = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
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
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);

        Button startGameButton = (Button) findViewById(R.id.newGameButton);
        startGameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });

        frameLayoutHuman = (FrameLayout) findViewById(R.id.boardHuman);
        frameLayoutMachine = (FrameLayout) findViewById(R.id.boardMachine);
        frameLayoutHuman.addView(createBoard(false));
        frameLayoutMachine.addView(createBoard(true));

        setMatrixMachine();

        //setMatrixHuman()
        SharedPreferences settings = getSharedPreferences("Matrix", 0);
        Gson gson = new Gson();
        String json = settings.getString("Matrix", "");
        matrixHuman = gson.fromJson(json, int[][][].class);
        TableLayout board = (TableLayout) frameLayoutHuman.getChildAt(0);
        for (int i = 1; i <= 10; i++) {
            TableRow row = (TableRow) board.getChildAt(i);
            for (int j = 1; j <= 10; j++) {
                TextView field = (TextView) row.getChildAt(j);
                if (matrixHuman[i-1][j-1][0] == 1 ) {
                    field.setBackgroundColor(Color.BLACK);
                }
            }
        }
    }


    protected TableLayout createBoard(boolean clickable){
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
            for (int j = 0; j < 11; j++) { //i
                TextView field = new TextView(this);
                field.setBackgroundResource(R.drawable.cell_shape);
                if (i == 0) {
                    field.setText(num[j]);
                } else if (j == 0) field.setText(AJ[i - 1]);
                field.setTextSize(15);
                field.setPadding(8,6,0,0);
                field.setGravity(Gravity.CENTER);
                if (clickable) {
                    if (!(i == 0 || j == 0)) {
                        addClickListener(field, i, j);
                    }
                }
                row.addView(field, rowparams);
            }
            tableLayout.addView(row, layoutParams);
        }
        return tableLayout;

    }
    protected void addClickListener(final TextView view, final int i, final int j){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matrixMachine[i - 1][j - 1][0] == 1) {
                    view.setBackgroundColor(Color.RED);
                    return;
                } else {
                    view.setBackgroundColor(Color.BLUE);
                }
                IATurn = true;
                humanTurn = false;
                startAlgorithm();
            }
        });
    }

    public static void setMatrixMachine(){

        Random rand = new Random();
        boolean shipOK = false;

        //Barco de 5
        int line = rand.nextInt(9);
        int direction = rand.nextInt(2); // 0 = horizontal, 1 = vertical

        int from = rand.nextInt(5);
        int to = from + 4;



        System.out.println("SHIP 5 - true: Line: " + (line+1)  + " -- Direction: " + direction + " -- From: " + from + " -- To: " + to);
        setShip(from, to, line, direction, matrixMachine);

        //Barco de 4

        while (!shipOK){

            line = rand.nextInt(9);
            direction = rand.nextInt(2); // 0 = horizontal, 1 = vertical

            from = rand.nextInt(4);
            to = from + 3;

            shipOK = true;
            shipOK = isAShip(from, to, line, direction, matrixMachine);
            System.out.println("SHIP 4 - " + shipOK + ": Line: " + (line+1) + " -- Direction: " + direction + " -- From: " + from + " -- To: " + to);

            if (shipOK) {
                setShip(from, to, line, direction, matrixMachine);
            }

        }

        int shipThree = 0;

        while (shipThree != 2){

            line = rand.nextInt(9);
            direction = rand.nextInt(2); // 0 = horizontal, 1 = vertical

            from = rand.nextInt(3);
            to = from + 2;

            shipOK = true;
            shipOK = isAShip(from, to, line, direction, matrixMachine);

            System.out.println("SHIP 3 - " + shipOK + ": Line: " + (line+1) + " -- Direction: " + direction + " -- From: " + from + " -- To: " + to);

            if (shipOK) {
                setShip(from, to, line, direction, matrixMachine);
                shipThree++;
            }

        }

        shipOK = false;

        while (!shipOK){

            line = rand.nextInt(9);
            direction = rand.nextInt(2); // 0 = horizontal, 1 = vertical

            from = rand.nextInt(2);
            to = from + 1;

            shipOK = true;
            shipOK = isAShip(from, to, line, direction, matrixMachine);

            System.out.println("SHIP 2 - " + shipOK + ": Line: " + (line+1) + " -- Direction: " + direction + " -- From: " + from + " -- To: " + to);

            if (shipOK) {
                setShip(from, to, line, direction, matrixMachine);
            }

        }
    }

    public static boolean isAShip(int from, int to, int line, int direction, int[][][] matrixAux){

        if (direction==0){ //Horizontal
            for (int i=from; i<=to; i++){
                if (matrixAux[line][i][0] == 1) return false;
            }
        } else { //Vertical
            for (int i=from; i<=to; i++){
                if (matrixAux[i][line][0] == 1) return false;
            }
        }

        return true;
    }

    public static void startAlgorithm(){
        while (IATurn){
            if(!lastHit){
                lastAction =  choosePlay();
                lastHit = hitOrMiss(lastAction[0], lastAction[1]);
            }else if(vertical != 0 || horizontal != 0){
                shipFound(lastAction[0], lastAction[1]);
            }else{
                bestAfterHit(lastAction);
            }
        }

        //printMatrix();
    }

    /***
     * This function print the matrix in console.
     * It only prints the boats, and the state of the box.
     */

    private static void printMatrix(){
        System.out.println("\\   A    B    C    D    E    F    G    H    I    J");
        for (int i = 0; i < matrixHuman.length ; i++) {
            System.out.print(i+1);
            for (int j = 0; j < matrixHuman.length ; j++) {
                if(i==9 && j==0) System.out.print(" "+matrixHuman[i][j][0]+"/"+matrixHuman[i][j][1]);

                else System.out.print("  "+matrixHuman[i][j][0]+"/"+matrixHuman[i][j][1]);
            }
            System.out.print("\n");
        }
    }

    /***
     * This function choose the next shot when the only that we know is the matrix probability.
     * The function choose the higher probability value and return it. If all the probabilities are equals,
     * the function generates two randoms numbers to select the nest shot
     *
     * @return bestAction [3] bestAction[0]  row, bestAction[1]  column, bestAction[2]  probability of the shot.
     */

    private static int[] choosePlay(){
        int[] bestAction = new int[3];
        bestAction[0] = -1;
        for (int i = 0; i < matrixHuman.length ; i++) {
            for (int j = 0; j < matrixHuman.length ; j++) {
                if (matrixHuman[i][j][1] == 0 && matrixHuman[i][j][2] > bestAction[2]){
                    bestAction[0] = i;
                    bestAction[1] = j;
                    bestAction[2] = matrixHuman[i][j][2];
                }
            }
        }

        if(bestAction[0] == -1) {
            Random rand = new Random();

            while(true) {
                int x = rand.nextInt(9);
                int y = rand.nextInt(9);

                if(matrixHuman[x][y][1] == 0) {
                    bestAction[0] = x;
                    bestAction[1] = y;
                    bestAction[2] =  matrixHuman[x][y][2];
                    break;
                }
            }
        }
        return bestAction;
    }

    /**
     * This function check if the shot is success or not
     *
     * @param x row of the shot
     * @param y column of the shot
     * @return boolean which indicates hit or not.
     */
    private static boolean hitOrMiss(int x, int y){
        if(matrixHuman[x][y][0]==1) {
            matrixHuman[x][y][1] = 2; //tocado
            System.out.println("JUGADA: fila  " + (x+1) + " columna  " + (y+1) + " TOCADO");
            if(IATurn){
                shipsDownIA++;
            }else{
                shipsDownHuman++;
            }
            drawHitOrMiss(x,y,true);
            checkFinalGame();
            return true;
        }
        matrixHuman[x][y][1] = 1;    //agua
        System.out.println("JUGADA: fila  " + (x+1) + " columna  " + (y+1) + " AGUA");
        drawHitOrMiss(x,y, false);
        IATurn = !IATurn;
        humanTurn = !humanTurn;
        checkFinalGame();
        return false;
    }

    private static void drawHitOrMiss(int x, int y, boolean hit) {
        TableLayout board = (TableLayout) frameLayoutHuman.getChildAt(0);
        TableRow row = (TableRow) board.getChildAt(x+1);
        TextView field = (TextView) row.getChildAt(y+1);
        if (hit)
            field.setBackgroundColor(Color.RED);
        else
            field.setBackgroundColor(Color.BLUE);
    }

    /**
     * This function put the boats in the matrix
     * @param from where the boat starts
     * @param to where the boat finish
     * @param line the line where the boat is
     * @param direction the horientation of the boat, vertical (1) or horizontal (0)
     * @param matrixHuman the board game
     */

    public static void setShip(int from, int to, int line, int direction, int[][][] matrixHuman){
        if (direction==0){ //Horizontal
            for (int i=from; i<=to; i++){
                matrixHuman[line][i][0] = 1;
            }
        } else { //Vertical
            for (int i=from; i<=to; i++){
                matrixHuman[i][line][0] = 1;
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

    public static List<String> findArround(int[] lastAction){
        List<String> possiblePlays = new ArrayList<>();

        if(lastAction[0] == 9){  // buscar hacia arriba o lados
            if(lastAction[1] == 0) { //hacia arriba o derecha
                possiblePlays.add("9-1");
                possiblePlays.add("8-0");
            } else if(lastAction[1] == 9){ //hacia arriba o izquierda
                possiblePlays.add("8-9");
                possiblePlays.add("9-8");
            } else { // buscar mejor jugada proxima a ese barco
                possiblePlays.add("9-" + (lastAction[1] - 1));
                possiblePlays.add("9-" + (lastAction[1] + 1));
                possiblePlays.add("8-" + (lastAction[1]));
            }
        }else if(lastAction[0]==0) { // hacia abajo o los lados
            if(lastAction[1] == 0) { // hacia abajo o derecha
                possiblePlays.add("0-1");
                possiblePlays.add("1-0");
            } else if(lastAction[1] == 9){ //hacia abajo o izquierda
                possiblePlays.add("0-8");
                possiblePlays.add("1-9");
            } else { // buscar mejor jugada proxima a ese barco
                possiblePlays.add("0-"+ (lastAction[1] - 1));
                possiblePlays.add("0-"+ (lastAction[1] + 1));
                possiblePlays.add("1-"+ lastAction[1]);
            }
        } else if(lastAction[1]==0){
            possiblePlays.add(lastAction[0] + "-" + (lastAction[1] + 1));
            possiblePlays.add((lastAction[0]+1) + "-" + lastAction[1]);
            possiblePlays.add((lastAction[0]-1) + "-" + lastAction[1]);
        }else if (lastAction[1] == 9){
            possiblePlays.add(lastAction[0] + "-" + (lastAction[1] - 1));
            possiblePlays.add((lastAction[0]+1) + "-" + lastAction[1]);
            possiblePlays.add((lastAction[0]-1) + "-" + lastAction[1]);
        }else{
            possiblePlays.add((lastAction[0]) + "-" + (lastAction[1]+1));
            possiblePlays.add((lastAction[0]) + "-" + (lastAction[1]-1));
            possiblePlays.add((lastAction[0]+1) + "-" + lastAction[1]);
            possiblePlays.add((lastAction[0]-1) + "-" + lastAction[1]);

        }

        return possiblePlays;
    }

    /**
     * Function that calls when the IA hits a boat two times and try to found
     * the rest of the ship.
     * @param lastAction array of int with the params of the last shot. lastAction[0] row, lastAction[1]  column
     */
    public static void bestAfterHit(int[] lastAction){
        List<String> possiblePlays = findArround(lastAction);
        checkProbablyPos(possiblePlays, lastAction);
    }

    /**
     * This function checks the possible plays arround the last hit, and when find a good play (hit), the function locates
     * int the board the ship and call @function shipFound()
     * @param possiblePlays List with the possible plays where the boat can be
     * @param lastAction array of int with the params of the last shot. lastAction[0]  row, lastAction[1]  column
     */

    public static void checkProbablyPos(List<String> possiblePlays, int[] lastAction){
        boolean hit;
        int[] bestAction =  new int[3];
        int nPosiblePlays = possiblePlays.size();
        int nPlaysTested = 0;

        for (int i = 0; i < possiblePlays.size(); i++) {
            String[] playsStr = possiblePlays.get(i).split("-");
            int row = Integer.parseInt(playsStr[0]);
            int column = Integer.parseInt(playsStr[1]);

            if (matrixHuman[row][column][1] == 0 && matrixHuman[row][column][2] >= bestAction[2]){
                bestAction[0] = row;
                bestAction[1] = column;
                bestAction[2] = matrixHuman[row][column][2];
            }
        }
        nPlaysTested++;
        hit = hitOrMiss(bestAction[0], bestAction[1]);

        if(hit){
            row = bestAction[0];
            column = bestAction[1];

            if(bestAction[0] == lastAction [0]) { //si la fila donde hemos tocado, es la misma que la de la anterior jugada => horizontal
                horizontal = 1; //horizontal
            }else{
                vertical = 1; //vertical
            }
        }else if(nPlaysTested == nPosiblePlays) {
            vertical = 0;
            horizontal = 0;
            lastHit = false;
        }

    }


    /**
     * Function calls after hit a boat again after two hits, and you know the aproximate possition of the boat
     * and if is vertical or horizontal. if is shotting in one direction and fails, them invert the direction of the shots
     * @param row row where the IA knows that there is a boat there
     * @param column column where the IA knows that there is a boat there
     */
    public static void shipFound(int row, int column){
        boolean hit;

        if(horizontal ==  1) {
            while (true) {
                if (sentidosInvertidos == 2) {
                    shipDown();
                    break;
                }

                int upDown = checkLimits(column - pos * sentido);
                if (upDown == sentido) {
                    changeDirection();
                    continue;
                }

                if (matrixHuman[row][column - pos * sentido][1] == 0) {
                    hit = hitOrMiss(row, column - pos * sentido);
                } else {
                    pos++;
                    continue;
                }

                if (hit) continueDirection(hit);
                else changeDirection();

                break;
            }
        }
        if(vertical == 1) {
            while (true) {
                if (sentidosInvertidos == 2) {
                    shipDown();
                    break;
                }

                int upDown = checkLimits(row - pos * sentido);
                if (upDown == sentido) {
                    changeDirection();
                    continue;
                }

                if (matrixHuman[row - pos * sentido][column][1] == 0) {
                    hit = hitOrMiss(row - pos * sentido, column);
                } else {
                    pos++;
                    continue;
                }

                if (hit) continueDirection(hit);
                else changeDirection();

                break;
            }
        }
    }


    private static void shipDown() {
        pos = 1;
        vertical=0;
        horizontal=0;
        sentidosInvertidos = 0;
        lastHit = false;
    }

    private static void changeDirection(){
        pos = 1;
        sentido = sentido * (-1);
        sentidosInvertidos++;
    }

    private static void continueDirection(boolean hit){
        lastHit = hit;
        pos++;
    }
    /**
     * This function check the limits of the board.
     * @param position index of the board
     * @return 0 for the position is good, 1, if your position is under the minimum index, and -1 if your position is over the maximun index
     */
    public static int checkLimits(int position){
        if(position < 0){
            return 1;
        }else if(position > 9){
            return -1;
        }else{
            return 0;
        }
    }

    public static void checkFinalGame(){
        if(shipsDownHuman == NUMBER_SHIPS){
            winner = 1;
        } else if( shipsDownIA == NUMBER_SHIPS){
            winner = 2;
        }
        totalGames++;
    }


    public static void learningAttack(){
        for (int row = 0; row < 10; row++) {
            for (int column  = 0; column < 10; column++) {
                matrixBaseAttack[row][column] = (totalGames * matrixBaseAttack[row][column] - (alpha * matrixHuman[row][column][2])) / (totalGames + 1);
            }
        }
    }

    public static void learningDefense(){
        for (int row = 0; row < 10; row++) {
            for (int column  = 0; column < 10; column++) {
                matrixBaseDefense[row][column] = (totalGames * matrixBaseDefense[row][column] - (alpha * matrixMachine[row][column][2])) / (totalGames + 1);
            }
        }
    }

    public static void inicializeBase (){
        //centrales
        matrixBaseDefense[4][4] = 1;
        matrixBaseDefense[4][5] = 1;
        matrixBaseDefense[5][4] = 1;
        matrixBaseDefense[4][5] = 1;
        //rodeando las centrales
        matrixBaseDefense[4][3] = 0.9;
        matrixBaseDefense[4][6] = 0.9;
        matrixBaseDefense[5][3] = 0.9;
        matrixBaseDefense[5][6] = 0.9;
        matrixBaseDefense[3][4] = 0.9;
        matrixBaseDefense[3][5] = 0.9;
        matrixBaseDefense[6][4] = 0.9;
        matrixBaseDefense[6][5] = 0.9;
        //un nivel mas hacia afuera
        matrixBaseDefense[3][3] = 0.82;
        matrixBaseDefense[3][6] = 0.82;
        matrixBaseDefense[6][3] = 0.82;
        matrixBaseDefense[6][6] = 0.82;
        matrixBaseDefense[4][2] = 0.82;
        matrixBaseDefense[5][2] = 0.82;
        matrixBaseDefense[4][7] = 0.82;
        matrixBaseDefense[5][7] = 0.82;
        matrixBaseDefense[2][4] = 0.82;
        matrixBaseDefense[2][5] = 0.82;
        matrixBaseDefense[7][4] = 0.82;
        matrixBaseDefense[7][5] = 0.82;
        ///
        matrixBaseDefense[3][2] = 0.73;
        matrixBaseDefense[2][3] = 0.73;
        matrixBaseDefense[2][6] = 0.73;
        matrixBaseDefense[3][7] = 0.73;
        matrixBaseDefense[6][2] = 0.73;
        matrixBaseDefense[7][3] = 0.73;
        matrixBaseDefense[6][7] = 0.73;
        matrixBaseDefense[7][6] = 0.73;
        //
        matrixBaseDefense[4][1] = 0.64;
        matrixBaseDefense[5][1] = 0.64;
        matrixBaseDefense[4][8] = 0.64;
        matrixBaseDefense[5][8] = 0.64;
        matrixBaseDefense[8][4] = 0.64;
        matrixBaseDefense[8][5] = 0.64;
        matrixBaseDefense[1][4] = 0.64;
        matrixBaseDefense[1][5] = 0.64;
        matrixBaseDefense[2][2] = 0.64;
        matrixBaseDefense[2][7] = 0.64;
        matrixBaseDefense[7][7] = 0.64;
        matrixBaseDefense[7][2] = 0.64;
        //
        matrixBaseDefense[3][1] = 0.55;
        matrixBaseDefense[1][3] = 0.55;
        matrixBaseDefense[1][6] = 0.55;
        matrixBaseDefense[3][8] = 0.55;
        matrixBaseDefense[6][1] = 0.55;
        matrixBaseDefense[8][3] = 0.55;
        matrixBaseDefense[6][8] = 0.55;
        matrixBaseDefense[8][6] = 0.55;
        //
        matrixBaseDefense[0][4] = 0.46;
        matrixBaseDefense[0][5] = 0.46;
        matrixBaseDefense[9][4] = 0.46;
        matrixBaseDefense[9][5] = 0.46;
        matrixBaseDefense[4][0] = 0.46;
        matrixBaseDefense[5][0] = 0.46;
        matrixBaseDefense[4][9] = 0.46;
        matrixBaseDefense[5][9] = 0.46;
        matrixBaseDefense[1][2] = 0.46;
        matrixBaseDefense[2][1] = 0.46;
        matrixBaseDefense[1][7] = 0.46;
        matrixBaseDefense[2][8] = 0.46;
        matrixBaseDefense[7][1] = 0.46;
        matrixBaseDefense[8][2] = 0.46;
        matrixBaseDefense[8][7] = 0.46;
        matrixBaseDefense[7][8] = 0.46;
        //
        matrixBaseDefense[0][3] = 0.36;
        matrixBaseDefense[3][0] = 0.36;
        matrixBaseDefense[0][6] = 0.36;
        matrixBaseDefense[3][9] = 0.36;
        matrixBaseDefense[1][7] = 0.36;
        matrixBaseDefense[2][8] = 0.36;
        matrixBaseDefense[6][0] = 0.36;
        matrixBaseDefense[9][3] = 0.36;
        matrixBaseDefense[9][6] = 0.36;
        matrixBaseDefense[6][9] = 0.36;
        //
        matrixBaseDefense[2][0] = 0.27;
        matrixBaseDefense[1][1] = 0.27;
        matrixBaseDefense[0][2] = 0.27;
        matrixBaseDefense[0][7] = 0.27;
        matrixBaseDefense[1][8] = 0.27;
        matrixBaseDefense[2][9] = 0.27;
        matrixBaseDefense[7][0] = 0.27;
        matrixBaseDefense[8][1] = 0.27;
        matrixBaseDefense[9][2] = 0.27;
        matrixBaseDefense[9][7] = 0.27;
        matrixBaseDefense[8][8] = 0.27;
        matrixBaseDefense[7][9] = 0.27;
        //
        matrixBaseDefense[0][1] = 0.18;
        matrixBaseDefense[1][0] = 0.18;
        matrixBaseDefense[0][8] = 0.18;
        matrixBaseDefense[1][9] = 0.18;
        matrixBaseDefense[8][0] = 0.18;
        matrixBaseDefense[9][1] = 0.18;
        matrixBaseDefense[8][9] = 0.18;
        matrixBaseDefense[9][8] = 0.18;
        //
        matrixBaseDefense[0][0] = 0;
        matrixBaseDefense[0][9] = 0.;
        matrixBaseDefense[9][0] = 0.;
        matrixBaseDefense[9][9] = 0.;
    }

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

    public void placeCarrier(int[] bestPlace){
        List<String> possiblePlace = placesWithCorrectSize(bestPlace, 5);
        boolean shipFree;

        for (int i = 0; i < possiblePlace.size(); i++) {
            String[] placeStr = possiblePlace.get(i).split("-");
            int row = Integer.parseInt(placeStr[0]);
            int column = Integer.parseInt(placeStr[1]);

            if(bestPlace[0] == row){
                if (bestPlace[1] > column){
                    shipFree = isAShip(bestPlace[1] - 5, bestPlace[1], row , 0);
                }else{
                    shipFree = isAShip(bestPlace[1], bestPlace[1] + 5, row , 0);
                }
            }else{
                if(bestPlace[0] > column){
                    shipFree = isAShip(bestPlace[0] + 5, bestPlace[0], row , 0);
                }else{
                    shipFree = isAShip(bestPlace[0], bestPlace[0] + 5, row , 0);
                }
            }

            if(shipFree){
                //probabilityPlace();
            }
        }
    }

    public List<String> placesWithCorrectSize(int[] bestPlace, int sizeBoat){ //el menos deberia quitarse si los barcos no pueden estar juntos
        List<String> possiblePlays = new ArrayList<>();
        if((bestPlace[0] + sizeBoat-1) < 10){
            possiblePlays.add((bestPlace[0] + 1 ) + "-" + bestPlace[1]);
        }
        if((bestPlace[0] - sizeBoat-1) >= 0){
            possiblePlays.add((bestPlace[0] - 1 ) + "-" + bestPlace[1]);
        }
        if((bestPlace[1] + sizeBoat-1) < 10){
            possiblePlays.add(bestPlace[0] + "-" + (bestPlace[1] + 1));
        }
        if((bestPlace[1] - sizeBoat-1) >= 0){
            possiblePlays.add(bestPlace[0] + "-" + (bestPlace[1] - 1));
        }
        return possiblePlays;
    }

    public static boolean isAShip(int from, int to, int line, int direction){

        if (direction==0){ //Horizontal
            for (int i=from; i<=to; i++){
                if (matrixMachine[line][i][0] == 1) return false;
            }
        } else { //Vertical
            for (int i=from; i<=to; i++){
                if (matrixMachine[i][line][0] == 1) return false;
            }
        }

        return true;
    }
}
