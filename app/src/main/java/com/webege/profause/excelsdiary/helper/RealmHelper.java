package com.webege.profause.excelsdiary.helper;

import android.content.Context;
import android.widget.Toast;

import com.webege.profause.excelsdiary.model.Client;
import com.webege.profause.excelsdiary.model.Measurement;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

/**
 * Created by Emmanuel Mensah on 8/20/2016.
 */
public class RealmHelper {
    Realm realm;
    Context context;
    RealmResults<Client> clientsResults;
    RealmList<Measurement> measurementsResult;


    boolean isSaved;

    public RealmHelper(Realm realm, Context context) {
        this.realm = realm;
        this.context = context;
    }

    public boolean Save(final Client c, final Measurement measurement) {
        if (measurement == null) {
            isSaved = false;
        } else {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        Client client = realm.where(Client.class)
                                .equalTo("name",c.getName()).findFirst();
                        client.getMeasurements().add(measurement);
                    } catch (RealmException e) {
                        e.printStackTrace();
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    isSaved = true;
                    Toast.makeText(context, "saved successful", Toast.LENGTH_SHORT).show();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Toast.makeText(context, "failed to save", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return isSaved;
    }

    public boolean Save(final Client client) {
        if (client == null) {
            isSaved = false;
        } else {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        Client c = realm.copyToRealm(client);
                        for(Measurement m:Measurement.getTempMeasure(c.getGender())){
                            c.getMeasurements().add(m);
                        }
                    } catch (RealmException e) {
                        e.printStackTrace();
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    isSaved = true;
                    Toast.makeText(context, "saved successful", Toast.LENGTH_SHORT).show();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Toast.makeText(context, "failed to save", Toast.LENGTH_LONG).show();
                }
            });
        }
        return isSaved;
    }

    public boolean Update(final String name, final Client client) {
        if (client == null) {
            isSaved = false;
        } else {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        Client c = realm.where(Client.class).equalTo("name", name).findFirst();
                        c.setName(client.getName());
                        c.setTelephone(client.getTelephone());
                        c.setDateAdded(client.getDateAdded());
                        c.setImageUri(client.getImageUri());
                        c.setGender(client.getGender());
                        //gender
                    } catch (RealmException e) {
                        e.printStackTrace();
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    isSaved = true;
                    Toast.makeText(context, "saved successful", Toast.LENGTH_SHORT).show();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    isSaved = false;
                    Toast.makeText(context, "failed to save", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return isSaved;
    }

    public boolean Update(final String clientName,final String where, final Measurement measurement) {
        if (measurement == null) {
            isSaved = false;
        } else {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        Client c =realm.where(Client.class).equalTo("name",clientName).findFirst();
                        Measurement m =c.getMeasurements().where().equalTo("name",where).findFirst();
                        m.setName(measurement.getName());
                        m.setValue(measurement.getValue());
                        m.setUnit(measurement.getUnit());
                        //gender
                    } catch (RealmException e) {
                        e.printStackTrace();
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    isSaved = true;
                    Toast.makeText(context, "saved successful", Toast.LENGTH_SHORT).show();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    isSaved = false;
                    Toast.makeText(context, "failed to save", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return isSaved;
    }

    public boolean DeleteMeasurement(final String clientName, final String where) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    Client c =realm.where(Client.class).equalTo("name",clientName).findFirst();
                    Measurement m =c.getMeasurements().where().equalTo("name",where).findFirst();
                    m.deleteFromRealm();
                    //Toast.makeText(context, m.getName()+"", Toast.LENGTH_SHORT).show();
                } catch (RealmException e) {
                    e.printStackTrace();
                }

            }
        });
        return isSaved;
    }


    public void RetrieveClientRealm() {
        clientsResults = realm.where(Client.class).findAll();
       // clientsResults=clientsResults.sort("name", Sort.ASCENDING);
    }

    public void RetrieveMeasurementRealm(String where) {

        try {
            measurementsResult = realm.where(Client.class)
                    .equalTo("name", where).findFirst().getMeasurements();
            //realmResults=realmResults.sort("name", Sort.ASCENDING);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Client> clientsRefresh() {
        ArrayList<Client> l = new ArrayList<>();
        for (Client c : clientsResults) {
            l.add(c);
        }
        return l;
    }

    public ArrayList<Measurement> measurementsRefresh() {
        ArrayList<Measurement> l = new ArrayList<>();

        try {
            for (Measurement m : measurementsResult) {
                l.add(m);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return l;
    }

    public boolean DeleteClient(final String where) {
        final File[] f = new File[1];
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    Client c = realm.where(Client.class).equalTo("name", where).findFirst();
                    f[0] = new File(App.dirPath + File.separator + c.getName()
                            + "_" + c.getTelephone()+ ".jpg");
                   c.deleteFromRealm();
                } catch (RealmException e) {
                    e.printStackTrace();
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                isSaved = true;
                Toast.makeText(context, "Delete successful", Toast.LENGTH_SHORT).show();
                if(f[0].exists()){
                    f[0].delete();
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                isSaved = false;
                Toast.makeText(context, "failed to delete", Toast.LENGTH_SHORT).show();
            }
        });
        return isSaved;
    }
}
