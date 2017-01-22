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
        builder.setTitle(R.string.endgame);
        if(GameActivity.getWinner() == 1){
            builder.setMessage(R.string.youwin);
        }else{
            builder.setMessage(R.string.youlose);
        }
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                    }
                });
        return builder.create();
    }
}
