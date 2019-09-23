/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buscaminas;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Platform.exit;
import javax.swing.JOptionPane;

/**
 *
 * @author Memo
 */
public class Buscaminas {
    
    public static String host="127.0.0.2", server_ip;
    public static int pto;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        server_ip = JOptionPane.showInputDialog("Ingrese la direcció IP: ");
        pto = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el puerto de conexión: "));
        Conection con=new Conection(server_ip, pto);
        Socket cl = con.connect();
        try {
            cl.setReuseAddress(true);
        } catch (SocketException ex) {
            Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
        }
        //El cliente envia la dificultad al servidor
        if(cl!=null){
            SetDifficulty sd=new SetDifficulty(con, cl);
            sd.setVisible(true);
        }
    }
}
