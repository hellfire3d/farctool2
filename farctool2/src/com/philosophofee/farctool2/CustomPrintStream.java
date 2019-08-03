package com.philosophofee.farctool2;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomPrintStream extends PrintStream {

    public CustomPrintStream(OutputStream out) {
        super(out);
    }

    
    @Override
    public void println(String string) {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        super.println("[" + df.format(date) + "] " + string);
    }
}