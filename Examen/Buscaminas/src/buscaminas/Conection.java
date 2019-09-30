/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buscaminas;

import static buscaminas.Buscaminas.pto;
import static buscaminas.Buscaminas.server_ip;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 *
 * @author Memo
 */
public class Conection {
    private String server_ip;
    private int pto;
    public Conection(Socket cl){
        server_ip=cl.getInetAddress().getHostAddress();
        pto=cl.getLocalPort();
    }
    public Conection(String server_ip, int pto){
        this.pto=pto;
        this.server_ip=server_ip;
    }
    
    public Socket connect(){
        try{
            Socket cl=new Socket(server_ip, pto);
            return cl;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public void disconnect(Socket cl){
        try{
            cl.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void setDifficulty(Socket cl, int difficulty){
        try{
            DataOutputStream dos=new DataOutputStream(cl.getOutputStream());
            dos.writeInt(difficulty);
            dos.flush();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public int[] receiveMap(Socket cl, int noMinas){
        try{
            int[] minas=new int[500];
            DataInputStream dis=new DataInputStream(cl.getInputStream());
            for(int i=0; i<noMinas; i++)
                minas[i]=dis.readInt();
            dis.close();
            return minas;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public ScoreBoard scoreBoard(Socket cl, String alias, int puntuation){
        try{
            int[] puntajes=new int[10];
            byte[] nombre=new byte[25]; 
            String[] names=new String[10];
            DataOutputStream dos=new DataOutputStream(cl.getOutputStream());
            DataInputStream dis=new DataInputStream(cl.getInputStream());
            dos.writeUTF(alias);
            dos.flush();
            dos.writeInt(puntuation);
            dos.flush();
            int numPuntajes=dis.readInt();
            //System.out.println(numPuntajes);
            for(int i=0; i<numPuntajes; i++){
                dis.read(nombre);
                String aux=new String(nombre);
                names[i] = aux;
                puntajes[i]=dis.readInt();
                nombre = new byte[25];
                //System.out.println(names[i]+"\t"+puntajes[i]);
            }
            dos.close();
            dis.close();
            ScoreBoard sc = new ScoreBoard(names, puntajes);
            return sc;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
