package de.lambeck.pned.models.data.validation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("javadoc")
public class GettingCurrentDate {
    public static void main(String[] args) {
        /*
         * Date class
         */
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date dateobj = new Date();
        System.out.println(df.format(dateobj));

        /*
         * Calendar class
         */
        Calendar calobj = Calendar.getInstance();
        System.out.println(df.format(calobj.getTime()));
    }
}
