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

    private static final String powerOff = "(PWR0)";
    private static final String powerOn = "(PWR1)";
    private static final String powerQuery = "(PWR?)";
    private SerialPort activePort;
    private final int bitRate;
    private final int dataBits;
    private final int flowControl;
    private final int parity;
    private final int stopBits;
    
    public SerialPort getSerialPort() {
        return activePort;
    }

    public void setSerialPort(SerialPort ap) {
        activePort = ap;
    }


    public synchronized boolean readPower() throws UnsupportedEncodingException, IOException, InterruptedException {
        SerialComm sc = activePort.getPort();
        sc.setComPortParameters(bitRate, dataBits, stopBits, parity);
        sc.setFlowControl(flowControl);

        boolean isOpen = sc.openPort();
        sc.getOutputStream().write(powerQuery.getBytes("US-ASCII"));
                    this.wait(100);

        int avail = sc.getInputStream().available();
        byte[] response = new byte[avail];

        int len = sc.getInputStream().read(response, 0, response.length);

        sc.closePort();

        String r2 = new String(response, 0, len, "US-ASCII");
        return "(0-1,1)".equals(r2);
    }

    public void writePower(boolean state) throws IOException {
        SerialComm sc = activePort.getPort();
        sc.setComPortParameters(bitRate, dataBits, stopBits, parity);
        sc.setFlowControl(flowControl);

        sc.openPort();
        String pwrCmd = (state) ? powerOn : powerOff;
        sc.getOutputStream().write(pwrCmd.getBytes("US-ASCII"));
        sc.closePort();
    }

    public Projector(ProjectorTypes pType){
        bitRate = pType.getBitrate();
        dataBits = pType.getDataBits();
        flowControl = pType.getFlowControl();
        parity = pType.getParity();
        stopBits = pType.getStopBits();
    }
}
