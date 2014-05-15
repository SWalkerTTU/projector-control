/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectorcontrol;

import j.extensions.comm.SerialComm;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;

/**
 *
 * @author scott.walker
 */
public class ProjectorControl extends javax.swing.JFrame {

    /**
     * Creates new form ProjectorControlFrame
     */
    public ProjectorControl() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ctrlPanel = new projectorcontrol.ProjectorControlPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jMenuBar1 = new javax.swing.JMenuBar();
        modelMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        portMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Projector Control");

        modelMenu.setText("Model");

        jMenuItem1.setText("IN2100 Series");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        modelMenu.add(jMenuItem1);

        jMenuBar1.add(modelMenu);

        portMenu.setText("Port");
        portMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                portMenuMenuSelected(evt);
            }
        });
        jMenuBar1.add(portMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(ctrlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(ctrlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void portMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_portMenuMenuSelected
        portMenu.removeAll();
        SerialComm[] ports = SerialComm.getCommPorts();
        ArrayList<SerialPort> portList = new ArrayList<>();
        for (SerialComm port : ports) {
            portList.add(0, new SerialPort(port));
        }
        for (final SerialPort sp : portList) {
            JMenuItem jmi = new JMenuItem(sp.toString());
            jmi.addActionListener(new ActionListener() {
                SerialPort sp2 = sp;

                @Override
                public void actionPerformed(ActionEvent e) {
                    activePort = sp2;
                    System.out.println(activePort.toString());
                    boolean powerOn;
                    try {
                        powerOn = getPowerState(activePort);
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(ProjectorControl.class.getName()).log(Level.SEVERE, null, ex);
                        powerOn = false;
                    } catch (IOException | InterruptedException ex) {
                        Logger.getLogger(ProjectorControl.class.getName()).log(Level.SEVERE, null, ex);
                        powerOn = false;
                    }
                    
                    Component[] panelParts = ctrlPanel.getComponents();
                    javax.swing.JToggleButton pb;
                    for (Component cmp : panelParts) {
                        if (cmp instanceof javax.swing.JToggleButton && "Power".equals(cmp.getName())) {
                            pb = (JToggleButton) cmp;
                            pb.setSelected(powerOn);
                            break;
                        }
                    }
                }

                private synchronized boolean getPowerState(SerialPort ap) throws UnsupportedEncodingException, IOException, InterruptedException {
                    SerialComm sc = ap.getPort();

                    sc.setComPortParameters(115200, 8, SerialComm.ONE_STOP_BIT, SerialComm.NO_PARITY);
                    sc.setFlowControl(SerialComm.FLOW_CONTROL_DISABLED);
                    sc.openPort();
                    sc.getOutputStream().write("(PWR?)".getBytes("US-ASCII"));

                    this.wait(80);
                    int avail = sc.getInputStream().available();
                    byte[] response = new byte[avail];

                    int len = sc.getInputStream().read(response, 0, response.length);
                    sc.closePort();

                    String r2 = new String(response, "US-ASCII");
                    System.out.println(r2);
                    return "(0-1,1)".equals(r2);
                }
            });
            portMenu.add(jmi);
        }
    }//GEN-LAST:event_portMenuMenuSelected

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProjectorControl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProjectorControl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProjectorControl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProjectorControl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ProjectorControl().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private projectorcontrol.ProjectorControlPanel ctrlPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenu modelMenu;
    private javax.swing.JMenu portMenu;
    // End of variables declaration//GEN-END:variables
    SerialPort activePort;
}
