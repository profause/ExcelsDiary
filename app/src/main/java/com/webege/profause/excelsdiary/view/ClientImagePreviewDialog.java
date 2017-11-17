package com.webege.profause.excelsdiary.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.webege.profause.excelsdiary.R;

import java.io.File;

/**
 * Created by Emmanuel Mensah on 8/19/2016.
 */
public class ClientImagePreviewDialog extends DialogFragment {
    AlertDialog.Builder aBuilder;
    LayoutInflater inflater;
    View v;
    ImageView clientImage;
    TextView clientName;
    ProgressBar imageProgress;
    String name;
    String imageUri;

    static ClientImagePreviewDialog newInstance(String name,String imageUri){
        ClientImagePreviewDialog dialog = new ClientImagePreviewDialog();
        Bundle args = new Bundle();
        args.putString("name",name);
        args.putString("imageUri",imageUri);
        dialog.setArguments(args);
        return dialog;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        name = getArguments().getString("name");
        imageUri = getArguments().getString("imageUri");

        aBuilder = new AlertDialog.Builder(getActivity());

        inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.client_image_preview_dialog_layout, null);
        clientImage= (ImageView) v.findViewById(R.id.clientImage);
        clientName= (TextView) v.findViewById(R.id.clientName);
        imageProgress= (ProgressBar) v.findViewById(R.id.progressBar);

        aBuilder.setView(v);
        aBuilder.setCancelable(true);
        Preview();


        Dialog dialog = aBuilder.create();
        return dialog;
    }

    private void Preview(){
    clientName.setText(name);
        try {
            Picasso.with(getContext()).load(new File(imageUri==""||imageUri==null?"":imageUri))
                    .error(R.drawable.user)
                    .fit()
                    .placeholder(R.drawable.user)
                    .into(clientImage,new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            imageProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            imageProgress.setVisibility(View.GONE);
                        }
                    });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
