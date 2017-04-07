package gateway;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;

public class IONGate {

public static List<String> exchangeWhiteList = new ArrayList();
public static List<String> exchangeBlackList = new ArrayList();

private List<File> foldersToProcess = new ArrayList();
private Calendar timestamp;
private Connection cnxLondon, cnxRisk;
private Map<String, Integer> monthSequence = new HashMap();

public boolean objectState = true;
public short objectCode = 0;
public String objectError = "";

public List<Statement> st = new ArrayList();

public IONGate(Calendar lastUpdate) {
    String[] monthName = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL",
                            "AUG", "SEP", "OCT", "NOV", "DEC"};
    int[] monthNum = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    for (int i = 0; i<=11; i++)
    monthSequence.put(monthName[i], monthNum[i]);
    if (this.ConnectionOK()) {
        this.GetFolders(lastUpdate.getTime());
        this.Decompose();
    } else {
        objectState = false;
        objectCode = 1;
        objectError = "Failed to connect to the database";
    }
}

// Establishes connection to the database.
private boolean ConnectionOK () {
    boolean r = true;
    String driver = "sun.jdbc.odbc.JdbcOdbcDriver";
    String dbLondon = "jdbc:odbc:London";
    String dbRisk = "jdbc:odbc:Risk";
    try {
        Class.forName(driver);
        cnxLondon = DriverManager.getConnection(dbLondon, "", "");
        cnxRisk = DriverManager.getConnection(dbRisk, "", "");
    } catch (ClassNotFoundException | SQLException ex) {
        r = false;
    }
    return r;
}

// Retrieves the list of the files to process.
private boolean GetFolders(java.util.Date lastFolder) {
    boolean r = true;
    String defPath = "M:\\Risks\\PrimeBrokerage\\Derivatives\\Archive\\";
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    File defDir = new File(defPath);
    File[] dirs = defDir.listFiles();
    for (File dir : dirs) if (dir.isDirectory()) try {
        java.util.Date folder = df.parse(dir.getName());
        if (folder.compareTo(lastFolder) > 0) {
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                return name.matches("cstm511r.txt");
            }});
            if (files.length > 0) foldersToProcess.add(files[0]);
        }
    } catch (ParseException e) {
        System.out.println(e.getMessage());
    }
    return r;
}

private boolean Decompose () {
    boolean r = true;
    boolean accNew, stNew;
    String accName;
    String lineCheck;
    if (foldersToProcess.size() > 0) for (File fl : foldersToProcess)
    try (Scanner scan = new Scanner(fl)) {
        Statement st = new Statement();
        st.source = fl;
        stNew = true;
        accNew = false;
        while (scan.hasNext()) {
            lineCheck = scan.nextLine();
            if (lineCheck.indexOf(0x000C) != -1) {
                for(int i=1; i<=6; i++) lineCheck = scan.nextLine();
                if (stNew) {
                    Account acc = new Account();
                    acc.name = lineCheck.substring(0, 68).trim();
                    st.stDate = this.ConvertDate(lineCheck.substring(68, 79));
                    stNew = false;
                    accNew = true;
                }

                System.out.println(lineCheck);
            }
        }
    } catch (FileNotFoundException e) {
        r = false;
    }
    return r;
}

// Retrieves the statement date in the Calendar format.
private Calendar ConvertDate(String sourceDate) {
    Calendar statementDate = Calendar.getInstance();
    String[] monthName = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL",
                        "AUG", "SEP", "OCT", "NOV", "DEC"};
    String[] splittedDate;
    Integer day = 1;
    Integer year = 1900;
    int month = 0;
    if (sourceDate.contains("/")) {
        splittedDate = sourceDate.split("/");
        day = Integer.valueOf(splittedDate[0]);
        month = Integer.valueOf(splittedDate[1]) - 1;
        year = 2000 + Integer.valueOf(splittedDate[2]);
    }
    for (String mn : monthName) if(sourceDate.contains(mn)) {
        if (sourceDate.contains(" ")) {
            splittedDate = sourceDate.split(" ");
            day = Integer.valueOf(splittedDate[0]);
            month = monthSequence.get(splittedDate[1]);
            year = Integer.valueOf(splittedDate[2]);
        } else if (sourceDate.length() == 7) {
            day = Integer.valueOf(sourceDate.substring(0, 2));
            month = monthSequence.get(sourceDate.substring(2, 5));
            year = 2000 + Integer.valueOf(sourceDate.substring(5));
        }
    }
    statementDate.set(year, month, day, 0, 0, 0);
    return statementDate;
}

/*public void Close() {
    if (!failObject) try {
        cnxLondon.close();
        cnxRisk.close();
    } catch (SQLException ex) {
        System.out.println(ex.getMessage());
        failObject = true;
    }
}*/
    
}
