package gateway;

import java.io.*;
import java.sql.*;
import java.util.*;

public class IONGate {

private String defPath = "M:\\Risks\\PrimeBrokerage\\Derivatives\\Archive\\";
private File[] foldersToProcess;
private Calendar timestamp;
private Connection cnxLondon, cnxRisk;
private int logID;

public boolean failObject = false;

public IONGate() {
    String driver = "sun.jdbc.odbc.JdbcOdbcDriver";
    String dbLondon = "jdbc:odbc:London";
    String dbRisk = "jdbc:odbc:Risk";
    try {
        Class.forName(driver);
        cnxLondon = DriverManager.getConnection(dbLondon, "", "");
        cnxRisk = DriverManager.getConnection(dbRisk, "", "");
    } catch (ClassNotFoundException | SQLException ex) {
        System.out.println(ex.getMessage());
        failObject = true;
    }
}

public boolean GetFolders() {
    boolean r = true;
    File stDirectory = new File(defPath);
    File[] statements = stDirectory.listFiles();
    for (File st : statements) System.out.println(st.getName());
    String qu = "SELECT neolog_folder FROM neolog " +
           "WHERE neolog_class = 'IONGate' ORDER BY neolog_stamp";
    return r;
}


public boolean GetLogID() {
    boolean r = true;
    String qu;
    Statement st;
    ResultSet rs;
    try {
        qu = "SELECT MAX(neolog_id) FROM neolog";
        st = this.cnxRisk.createStatement
        (ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        rs = st.executeQuery(qu);
        rs.first();
        logID = rs.getInt(1);
        logID++;
        System.out.println("LogID = " + logID);
        rs.close();
        st.close();
        this.GetFolders();
    } catch (SQLException e) {
        System.out.println(e.getMessage());
        r = false;
    }
    return r;
}

public void Close() {
    if (!failObject) try {
        cnxLondon.close();
        cnxRisk.close();
    } catch (SQLException ex) {
        System.out.println(ex.getMessage());
        failObject = true;
    }
}
    
}
