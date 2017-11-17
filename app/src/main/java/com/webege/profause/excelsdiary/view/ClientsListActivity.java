package com.webege.profause.excelsdiary.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.webege.profause.excelsdiary.R;
import com.webege.profause.excelsdiary.adapter.ClientListAdapter;
import com.webege.profause.excelsdiary.helper.Callback;
import com.webege.profause.excelsdiary.helper.RealmHelper;
import com.webege.profause.excelsdiary.helper.SimpleDividerItemDecoration;
import com.webege.profause.excelsdiary.model.Client;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class ClientsListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, ClientListAdapter.ClickListener, ClientListAdapter.ImageClickListener,
        ClientListAdapter.LongClickListener {

    private RecyclerView clientList;
    private SwipeRefreshLayout refreshLayout;
    FloatingActionButton fab;
    LinearLayoutManager layoutManager;
    ClientListAdapter clientListAdapter;
    FragmentManager fragmentManager;
    ImageView ivBg;

    Realm realm;
    RealmHelper realmHelper;
    RealmChangeListener realmChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        InitializeViews();
        ChangeBackground(R.drawable.kent);
        fragmentManager = getSupportFragmentManager();
        realm = Realm.getDefaultInstance();
        realmHelper = new RealmHelper(realm,this);



        if (savedInstanceState != null) {
            ArrayList<Client> list = savedInstanceState.getParcelableArrayList("clientsList");
            clientListAdapter.setClientsList(list);
        } else {
           realmHelper.RetrieveClientRealm();
           clientListAdapter.setClientsList(realmHelper.clientsRefresh());
        }

        realmChangeListener = new RealmChangeListener() {

            @Override
            public void onChange(Object element) {
                realmHelper.RetrieveClientRealm();
                clientListAdapter.setClientsList(realmHelper.clientsRefresh());
            }
        };
        realm.addChangeListener(realmChangeListener);
    }

    private void ChangeBackground(int a){
        Picasso.with(ClientsListActivity.this)
                .load(a)
                .fit().centerCrop()
                .into(ivBg);
    }

    private void InitializeViews() {
        ivBg = (ImageView) findViewById(R.id.ivBg);
        clientList = (RecyclerView) findViewById(R.id.clientsList);
        clientListAdapter = new ClientListAdapter(this);
        clientList.setAdapter(clientListAdapter);
        SlideInLeftAnimator animation = new SlideInLeftAnimator();
        animation.setAddDuration(350);
        animation.setRemoveDuration(350);
       /* recyclerView.setItemAnimator(animation);
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(300);
        animator.setRemoveDuration(300);*/
        clientList.setItemAnimator(animation);
        clientList.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        clientList.addItemDecoration(new SimpleDividerItemDecoration(this));
        clientList.setHasFixedSize(true);
        //clientListAdapter.setClientsList(addTempClients());
        clientListAdapter.setImageClickListener(this);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        clientList.setLayoutManager(layoutManager);

//TODO: set adapter here
        clientListAdapter.setClickListener(this);
        clientListAdapter.setLongClickListener(this);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    public ArrayList<Client> addTempClients() {
        ArrayList<Client> list = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            list.add(new Client("mensah", "0543756168", 0,"", "08/19/2016"));
        }
        return list;
    }

    @Override
    public void onRefresh() {
        clientListAdapter.Clear();
        realmHelper.RetrieveClientRealm();
        clientListAdapter.setClientsList(realmHelper.clientsRefresh());
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.fab:
                //TODO: Add new Client
                AddClientDialog dialog = new AddClientDialog();
                dialog.setAddNewClientCallback(new Callback.AddNewClientCallback() {
                    @Override
                    public void onAdded(Client client) {
                        RealmHelper helper = new RealmHelper(realm,ClientsListActivity.this);
                       // client.setMeasurements();
                        if (helper.Save(client)) {
                            //clientListAdapter.AddClient(client);
                            clientList.scrollToPosition(clientListAdapter.getClientsList().size() - 1);
                        }
                       // helper.AddMeasurementTemplate(client);

                    }
                });
                dialog.show(fragmentManager, "addClientDialog");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clients_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(View view, int position, Client c) {
        Intent i =new Intent(this,ClientDetailsActivity.class)
                .putExtra("clientDetails",c);
        startActivity(i);
    }

    @Override
    public void imageClicked(View view, int position, String imageUri, String clientName) {
        ClientImagePreviewDialog dialog = ClientImagePreviewDialog.newInstance(clientName, imageUri);
        dialog.show(fragmentManager, "clientImagePreviewDialog");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("clientsList", clientListAdapter.getClientsList());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realmChangeListener != null) {
            realm.removeChangeListener(realmChangeListener);
        }

    }

    @Override
    public void itemLongClicked(View view, int position, final Client c) {
        ConfirmDeleteDialog deleteDialog = new ConfirmDeleteDialog();
        deleteDialog.setDeleteCallback(new Callback.DeleteCallback() {
            @Override
            public void onDelete() {
                RealmHelper helper = new RealmHelper(realm, ClientsListActivity.this);
                helper.DeleteClient(c.getName());
            }
        });
        deleteDialog.show(fragmentManager, "confirmDelete");
    }
}
