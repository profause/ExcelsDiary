package com.webege.profause.excelsdiary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.webege.profause.excelsdiary.R;
import com.webege.profause.excelsdiary.model.Client;

import java.io.File;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/**
 * Created by Emmanuel Mensah on 8/19/2016.
 */
public class ClientListAdapter extends RecyclerView.Adapter<ClientListAdapter.ClientListViewHolder> {
    ClickListener clickListener;

    LongClickListener longClickListener;

    ImageClickListener imageClickListener;

    Context context;
    LayoutInflater inflater;
    ArrayList<Client> clientsList = new ArrayList<>();
    View v;

    public ClientListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public ArrayList<Client> getClientsList() {
        return clientsList;
    }

    public void setClientsList(ArrayList<Client> clientsList) {
        this.clientsList = clientsList;
        notifyDataSetChanged();
    }

    public void AddClient(Client item) {
        clientsList.add(0, item);
        notifyItemInserted(0);
    }

    public void AddRange(ArrayList<Client> items) {
        try {
            for (Client c : items) {
                AddClient(c);
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ClientListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = inflater.inflate(R.layout.client_list_row, parent, false);
        ClientListViewHolder holder = new ClientListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ClientListViewHolder holder, int position) {
        Client current = clientsList.get(position);
        holder.clientName.setText(current.getName());
        holder.clientTelephone.setText(current.getTelephone());
        holder.clientImage.setImageResource(0);
        //TODO : PICASSO IMAGELOADER
        try {
            Picasso.with(context).load(new File(current.getImageUri()))
                    .error(R.drawable.user)
                    .fit()
                    .placeholder(R.drawable.user)
                    .into(holder.clientImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            holder.imageProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.imageProgress.setVisibility(View.GONE);
                        }
                    });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        return clientsList == null ? 0 : clientsList.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLongClickListener(LongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void setImageClickListener(ImageClickListener imageClickListener) {
        this.imageClickListener = imageClickListener;
    }

    public void Clear() {
        this.clientsList = null;
        notifyDataSetChanged();
    }

    public class ClientListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView clientName, clientTelephone;
        ImageView clientImage;
        ProgressBar imageProgress;

        public ClientListViewHolder(View itemView) {
            super(itemView);
            clientName = (TextView) itemView.findViewById(R.id.clientName);
            clientTelephone = (TextView) itemView.findViewById(R.id.clientTel);
            imageProgress = (ProgressBar) itemView.findViewById(R.id.image_progressBar);

            clientImage = (ImageView) itemView.findViewById(R.id.clientImage);
            clientImage.setOnClickListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.clientImage) {
                if (imageClickListener != null) {
                    imageClickListener.imageClicked(view, getAdapterPosition(),
                            clientsList.get(getAdapterPosition()).getImageUri(),
                            clientsList.get(getAdapterPosition()).getName()
                    );
                }
            } else {
                if (clickListener != null) {
                    clickListener.itemClicked(view, getAdapterPosition(), new Client(
                            clientsList.get(getAdapterPosition()).getName(),
                            clientsList.get(getAdapterPosition()).getTelephone(),
                            clientsList.get(getAdapterPosition()).getGender(),
                            clientsList.get(getAdapterPosition()).getImageUri(),
                            clientsList.get(getAdapterPosition()).getDateAdded()
                    ));
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (longClickListener != null) {
                longClickListener.itemLongClicked(view, getAdapterPosition(),
                        new Client(
                                clientsList.get(getAdapterPosition()).getName(),
                                clientsList.get(getAdapterPosition()).getTelephone(),
                                clientsList.get(getAdapterPosition()).getGender(),
                                clientsList.get(getAdapterPosition()).getImageUri(),
                                clientsList.get(getAdapterPosition()).getDateAdded()
                        )
                );
            }
            return false;
        }
    }

    public interface ClickListener {
        public void itemClicked(View view, int position, Client c);
    }

    public interface LongClickListener {
        public void itemLongClicked(View view, int position, Client c);
    }

    public interface ImageClickListener {
        public void imageClicked(View view, int position, String imageUri, String clientName);
    }
}
