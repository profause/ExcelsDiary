package com.webege.profause.excelsdiary.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Emmanuel Mensah on 8/26/2016.
 */
public class Util {

    public static String FromCmToInches(String cm){
        String value = "";
        try {
            double inches = Double.parseDouble(cm) / 2.54;
            value=String.valueOf(round(inches,2))+":inches";
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String FromInchesToCm(String inches){
        String value = "";
        try {
            double cm = Double.parseDouble(inches) *2.54;
            value=String.valueOf(round(cm,2))+":cm";
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
