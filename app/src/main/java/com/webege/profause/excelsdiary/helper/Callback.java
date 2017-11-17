package com.webege.profause.excelsdiary.helper;

import com.webege.profause.excelsdiary.model.Client;
import com.webege.profause.excelsdiary.model.Measurement;

/**
 * Created by Emmanuel Mensah on 8/19/2016.
 */
public class Callback {

    public interface AddNewClientCallback{
        public void onAdded(Client client);
    }

    public interface AddNewMeasurementCallback{
        public void onAdded(Measurement measurement);
    }

    public interface DeleteCallback{
        public void onDelete();
    }

}
