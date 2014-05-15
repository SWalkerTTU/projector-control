/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectorcontrol;

import j.extensions.comm.SerialComm;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author scott.walker
 */
public class Projector {

    private static final String powerQuery = "(PWR?)";
    private static final String powerOn = "(PWR1)";
    private static final String powerOff = "(PWR0)";
    protected SerialPort activePort;

    protected synchronized boolean readPower(SerialComm sc) throws UnsupportedEncodingException, IOException, InterruptedException {
        sc.openPort();
        sc.getOutputStream().write(powerQuery.getBytes("US-ASCII"));
        this.wait(80);
        int avail = sc.getInputStream().available();
        byte[] response = new byte[avail];

        int len = sc.getInputStream().read(response, 0, response.length);
        sc.closePort();

        String r2 = new String(response, "US-ASCII");
        System.out.println(r2);
        return "(0-1,1)".equals(r2);
    }
    
    protected void writePower(SerialComm sc, boolean state) throws UnsupportedEncodingException, IOException{
        sc.openPort();
        String pwrCmd = (state) ? powerOn : powerOff;
        sc.getOutputStream().write(pwrCmd.getBytes("US-ASCII"));
    }

    abstract static class ProjectorType extends Projector {

        int bitRate, dataBits, stopBits, parity, flowControl;

        public boolean readPower() throws IOException, UnsupportedEncodingException, InterruptedException {
            SerialComm sc = activePort.getPort();
            sc.setComPortParameters(bitRate, dataBits, stopBits, parity);
            sc.setFlowControl(flowControl);
            return super.readPower(sc);
        }
        
        public void writePower(boolean state) throws IOException{
            SerialComm sc = activePort.getPort();
            sc.setComPortParameters(bitRate, dataBits, stopBits, parity);
            sc.setFlowControl(flowControl);
            super.writePower(sc, state);
        }
    }

    public static class IN2100Series extends ProjectorType {

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

}
