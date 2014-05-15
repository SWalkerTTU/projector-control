/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectorcontrol;

import j.extensions.comm.SerialComm;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scott.walker
 */
 public abstract class Projector {

    private static final String powerQuery  = "(PWR?)";
    private static final String powerOn     = "(PWR1)";
    private static final String powerOff    = "(PWR0)";
    private SerialPort activePort;
    
    abstract boolean readPower();
    abstract void writePower(boolean state);
    
    private synchronized boolean readPower(SerialComm sc) throws UnsupportedEncodingException, IOException, InterruptedException {
        sc.openPort();
        sc.getOutputStream().write(powerQuery.getBytes("US-ASCII"));
        this.wait(80);
        int avail = sc.getInputStream().available();
        byte[] response = new byte[avail];

        int len = sc.getInputStream().read(response, 0, response.length);
        sc.closePort();

        String r2 = new String(response, 0, len, "US-ASCII");
        return "(0-1,1)".equals(r2);
    }
    
    private void writePower(SerialComm sc, boolean state) throws UnsupportedEncodingException, IOException{
        sc.openPort();
        String pwrCmd = (state) ? powerOn : powerOff;
        sc.getOutputStream().write(pwrCmd.getBytes("US-ASCII"));
        sc.closePort();
    }

    abstract static class ProjectorType extends Projector {

        int bitRate, dataBits, stopBits, parity, flowControl;

        @Override
        public boolean readPower(){
            SerialComm sc = super.getSerialPort().getPort();
            sc.setComPortParameters(bitRate, dataBits, stopBits, parity);
            sc.setFlowControl(flowControl);
            boolean powerState = false;
            try {
                powerState = super.readPower(sc);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(Projector.class.getName()).log(Level.SEVERE, null, ex);
            }
            return powerState;
        }
        
        @Override
        public void writePower(boolean state){
            SerialComm sc = super.getSerialPort().getPort();
            sc.setComPortParameters(bitRate, dataBits, stopBits, parity);
            sc.setFlowControl(flowControl);
            try {
                super.writePower(sc, state);
            } catch (IOException ex) {
                Logger.getLogger(Projector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static final class IN2100Series extends ProjectorType {

        public IN2100Series() {
            bitRate = 115200;
            dataBits = 8;
            stopBits = SerialComm.ONE_STOP_BIT;
            parity = SerialComm.NO_PARITY;
            flowControl = SerialComm.FLOW_CONTROL_DISABLED;
        }
    }

    public void setSerialPort(SerialPort ap) {
        activePort = ap;
    }

    public SerialPort getSerialPort(){
        return activePort;
    }
}
