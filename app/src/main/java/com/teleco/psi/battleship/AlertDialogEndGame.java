package com.teleco.psi.battleship;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class AlertDialogEndGame extends DialogFragment{

    public AlertDialogEndGame newInstance() {
        return new AlertDialogEndGame();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("END GAME!");
        if(GameActivity.getWinner() == 1){
            builder.setMessage("You win the game! :)");
        }else{
            builder.setMessage("You lose the game... :(");
        }
        builder.setCancelable(false);
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                    }
                });
        return builder.create();
    }
}
