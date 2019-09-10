
import java.net.InetAddress;
import java.net.MulticastSocket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Memo
 */
public class Anuncio {
    public static void main(String args[]){
        try{
            int pto=7777;
            MulticastSocket s=new MulticastSocket(pto);
            String dir="229.1.2.3";
            InetAddress gpo=InetAddress.getByName(dir);
            s.joinGroup(gpo);
            System.out.println("Servidor iniciado y unido al grupo "+dir);
            s.setReuseAddress(true);
            s.setTimeToLive(255);
        }catch(Exception e){
            
        }
    }
}
