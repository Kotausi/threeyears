package hius;

import gateway.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Hius {

public static void main(String[] args) throws InterruptedException {
    Calendar lastFolder = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date dt;
    try {
    dt = df.parse("2017-03-29");
    lastFolder.setTime(dt);
    IONGate ion = new IONGate(lastFolder);
    } catch (ParseException e) {
        System.out.println(e.getMessage());
    }
    Calendar test = Calendar.getInstance();
    test.setTimeInMillis(System.currentTimeMillis());
    System.out.println(test.getTimeInMillis());
}
    
}
