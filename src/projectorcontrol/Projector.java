/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectorcontrol;

import j.extensions.comm.SerialComm;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scott.walker
 */
public class Projector {

    private SerialPort activePort;
    private final int bitRate;
    private final int dataBits;
    private final int flowControl;
    private final int parity;
    private final int stopBits;

    public Projector(ProjectorTypes pType) {
        bitRate = pType.getBitrate();
        dataBits = pType.getDataBits();
        flowControl = pType.getFlowControl();
        parity = pType.getParity();
        stopBits = pType.getStopBits();
    }

    public SerialPort getSerialPort() {
        return activePort;
    }

    public void setSerialPort(SerialPort ap) {
        activePort = ap;
    }

    public boolean getPower() {
        return "(0-1,1)".equals(read(Queries.POWER));
    }

    public void setPower(boolean state) {
        write(Queries.POWER, (state) ? "1" : "0");
    }

    public int getAspect() {
        String r2 = read(Queries.ASPECT);
        return Integer.parseInt(r2.substring(1, r2.length()-1).split(",")[1]);
    }
    
    public void setAspect (int aspectNum) {
        write(Queries.ASPECT, Integer.toString(aspectNum));
    }
    
    private String read(String cmd) {
        SerialComm sc = activePort.getPort();
        sc.setComPortParameters(bitRate, dataBits, stopBits, parity);
        sc.setFlowControl(flowControl);

        byte[] response = null;
        int len = 0;
        try {
            sc.openPort();
            sc.getOutputStream().write(cmd.getBytes("US-ASCII"));
            synchronized (this) {
                this.wait(100);
            }
            int avail = sc.getInputStream().available();
            response = new byte[avail];    
            len = sc.getInputStream().read(response, 0, response.length);
        } catch (IOException | InterruptedException iOException) {
        }


        sc.closePort();

        try {
            return new String(response, 0, len, "US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Projector.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    
    private void write(String cmd, String val) {
                SerialComm sc = activePort.getPort();
        sc.setComPortParameters(bitRate, dataBits, stopBits, parity);
        sc.setFlowControl(flowControl);
        sc.openPort();
        String fullCmd = cmd.replace("?", val);
        try {
            sc.getOutputStream()
                    .write(fullCmd.getBytes(Charset.forName("US-ASCII")));
        } catch (IOException ex) {
            Logger.getLogger(Projector.class.getName()).log(Level.SEVERE, null, ex);
        }
        sc.closePort();        
    }
    
}
