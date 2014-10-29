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

    public boolean readPower() throws UnsupportedEncodingException, IOException, InterruptedException {
        return "(0-1,1)".equals(read(Queries.POWER));
    }

    public void writePower(boolean state) throws IOException {
        write(Queries.POWER, (state) ? "1" : "0");
    }

    private String read(String cmd) throws IOException, InterruptedException {
        SerialComm sc = activePort.getPort();
        sc.setComPortParameters(bitRate, dataBits, stopBits, parity);
        sc.setFlowControl(flowControl);

        boolean isOpen = sc.openPort();
        sc.getOutputStream().write(cmd.getBytes("US-ASCII"));
        synchronized (this) {
            this.wait(100);
        }
        int avail = sc.getInputStream().available();
        byte[] response = new byte[avail];

        int len = sc.getInputStream().read(response, 0, response.length);

        sc.closePort();

        return new String(response, 0, len, "US-ASCII");
    }

    int readAspect() throws UnsupportedEncodingException, IOException, InterruptedException {
        String r2 = read(Queries.ASPECT);
        return Integer.parseInt(r2.substring(1, r2.length()-1).split(",")[1]);
    }
    
    void writeAspect (int aspectNum) throws IOException{
        write(Queries.ASPECT, Integer.toString(aspectNum));
    }
    
    private void write(String cmd, String val) throws IOException{
                SerialComm sc = activePort.getPort();
        sc.setComPortParameters(bitRate, dataBits, stopBits, parity);
        sc.setFlowControl(flowControl);
        sc.openPort();
        String fullCmd = cmd.replace("?", val);
        sc.getOutputStream()
                .write(fullCmd.getBytes(Charset.forName("US-ASCII")));
        sc.closePort();        
    }
    
}
