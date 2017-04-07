package gateway;

import java.io.*;
import java.util.*;

public class Statement {

public File source;
public Calendar stDate = Calendar.getInstance();
public List<Account> account = new ArrayList();
public List<FXRate> fxRate = new ArrayList();
public List<Instrument> instrument = new ArrayList();
}
