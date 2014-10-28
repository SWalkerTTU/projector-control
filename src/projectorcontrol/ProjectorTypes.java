/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectorcontrol;

import j.extensions.comm.SerialComm;

/**
 *
 * @author scott.walker
 */
public enum ProjectorTypes {
    IN2100Series(115200, 8, SerialComm.ONE_STOP_BIT, SerialComm.NO_PARITY, 
            SerialComm.FLOW_CONTROL_DISABLED);
    
    private final int bitrate;
    private final int dataBits;
    private final int flowControl;
    private final int parity;
    private final int stopBits;

    private ProjectorTypes(int br, int data, int stop, int par, int fc) {
        bitrate = br;
        dataBits = data;
        stopBits = stop;
        parity = par;
        flowControl = fc;
    }

    int getBitrate() {
        return bitrate;
    }

    int getDataBits() {
        return dataBits;
    }

    int getFlowControl() {
        return flowControl;
    }

    int getParity() {
        return parity;
    }

    int getStopBits() {
        return stopBits;
    }
    
}
