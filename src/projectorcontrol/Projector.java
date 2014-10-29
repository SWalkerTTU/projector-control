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

    public SerialPort getSerialPort() {
        return activePort;
    }

    public void setSerialPort(SerialPort ap) {
        activePort = ap;
    }

    public boolean readPower() throws UnsupportedEncodingException,
            IOException, InterruptedException {
        SerialComm sc = activePort.getPort();
        sc.setComPortParameters(bitRate, dataBits, stopBits, parity);
        sc.setFlowControl(flowControl);

        boolean isOpen = sc.openPort();
        sc.getOutputStream().write(Queries.POWER.getBytes("US-ASCII"));
        synchronized (this) {
            this.wait(100);
        }
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

        char pwrCmd = (state) ? '1' : '0';
        sc.getOutputStream().write(Queries.POWER.replace('?', pwrCmd).getBytes(Charset.forName("US-ASCII")));
        sc.closePort();
    }

    public Projector(ProjectorTypes pType) {
        bitRate = pType.getBitrate();
        dataBits = pType.getDataBits();
        flowControl = pType.getFlowControl();
        parity = pType.getParity();
        stopBits = pType.getStopBits();
    }

    int readAspect() throws UnsupportedEncodingException, IOException, InterruptedException {
        SerialComm sc = activePort.getPort();
        sc.setComPortParameters(bitRate, dataBits, stopBits, parity);
        sc.setFlowControl(flowControl);

        boolean isOpen = sc.openPort();
        sc.getOutputStream().write(Queries.ASPECT.getBytes("US-ASCII"));
        synchronized (this) {
            this.wait(100);
        }
        int avail = sc.getInputStream().available();
        byte[] response = new byte[avail];

        int len = sc.getInputStream().read(response, 0, response.length);

        sc.closePort();

        String r2 = new String(response, 0, len, "US-ASCII");

        return Integer.parseInt(r2.substring(1, r2.length()-1).split(",")[1]);
    }

    void writeAspect(int aspectNum) throws IOException {
        SerialComm sc = activePort.getPort();
        sc.setComPortParameters(bitRate, dataBits, stopBits, parity);
        sc.setFlowControl(flowControl);
        sc.openPort();

        sc.getOutputStream().write(Queries.ASPECT.replace('?', Integer.toString(aspectNum).charAt(0)).getBytes(Charset.forName("US-ASCII")));
        sc.closePort();
    }
    
}
