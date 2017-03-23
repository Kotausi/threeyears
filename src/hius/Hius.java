package hius;

import gateway.*;

public class Hius {

public static void main(String[] args) {
    IONGate ion = new IONGate();
    if (!ion.failObject) {
        ion.GetLogID();
        ion.Close();
    }
}
    
}
