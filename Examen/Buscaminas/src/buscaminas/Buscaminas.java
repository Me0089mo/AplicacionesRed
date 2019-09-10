/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buscaminas;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.Socket;

/**
 *
 * @author Memo
 */
public class Buscaminas {
    
    String host="127.0.0.1";
    String pto;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        SetDifficulty sd=new SetDifficulty();
        int difficulty = sd.getDificulty();
    }
    
    public static void send(){
        try{
            Socket cl=new Socket(host, pto);
            System.out.println("Conexión con servidor establecida, se enviará un archivo...");
            String nombre=f.getName();
            long tam=f.length();
            String ruta=f.getAbsolutePath();
            DataOutputStream dos=new DataOutputStream(cl.getOutputStream());
            DataInputStream dis=new DataInputStream(new FileInputStream(ruta));
            dos.writeUTF(nombre);
            dos.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
