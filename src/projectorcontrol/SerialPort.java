/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectorcontrol;

import j.extensions.comm.SerialComm;

/**
 *
 * @author scott.walker
 */
public class SerialPort {
    
    private final SerialComm port;
    
    public SerialPort(SerialComm sc){
        port = sc;
    }
    
    @Override
    public String toString(){
        return port.getSystemPortName();
    }
    
    public SerialComm getPort(){
        return port;
    }
    
}
