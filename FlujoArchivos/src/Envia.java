import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class Envia{
    public static String host="127.0.0.1";
    public static int pto=9000, tree_depth=0;
    
    public static void main(String args[]){
        System.out.println("Seleccione los archivos que desea enviar");
        JFileChooser jf=new JFileChooser();
        jf.setMultiSelectionEnabled(true);
        jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int r=jf.showOpenDialog(null);
        File[] f=jf.getSelectedFiles();
        if(r == JFileChooser.APPROVE_OPTION){
            for(int i=0; i<f.length; i++){
                if(f[i].isDirectory()){
                    sendDirectory(f[i], tree_depth, false);
                    filesInDirectory(f[i]);
                }
                else{
                    if(i-1 == f.length)
                    sendFile(f[i], true);
                else
                    sendFile(f[i], false);
                }
            }
        }
    }
    
    public static void filesInDirectory(File f){
        File[] fc = f.listFiles();
        if(fc.length == 0){
            tree_depth--;
            sendTreeDepth();
        }
        for(int i=0; i<fc.length; i++){
            if(fc[i].isDirectory()){
                tree_depth++;
                sendDirectory(fc[i], tree_depth, false);
                filesInDirectory(fc[i]);
                if(i+1 == fc.length){
                    tree_depth--;
                    sendTreeDepth();
                }
            }
            else{
                if(i+1 == fc.length){
                    sendFile(fc[i], true);
                    tree_depth--;
                }
                else
                    sendFile(fc[i], false);
            }
        }
    }
    
    public static void sendFile(File f, boolean dir_final){
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
            dos.writeBoolean(false);
            dos.flush();
            dos.writeBoolean(dir_final);
            dos.flush();
            dos.writeLong(tam);
            dos.flush();
            long enviados=0;
            int n=0, porcentaje=0;
            while(enviados<tam){
                byte[] b=new byte[2000];
                n=dis.read(b);
                enviados=enviados+n;
                dos.write(b, 0, n);
                dos.flush();
                porcentaje=(int)((enviados*100)/tam);
            }
            System.out.println("Archivo "+nombre+" enviado\n");
            dis.close();
            dos.close();
            cl.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void sendDirectory(File f, int tree_depth, boolean dir_final){
        try{
            Socket cl=new Socket(host, pto);
            System.out.println("Conexión con servidor establecida, se enviará un archivo...");
            String nombre=f.getName();
            DataOutputStream dos=new DataOutputStream(cl.getOutputStream());
            dos.writeUTF(nombre);
            dos.flush();
            dos.writeBoolean(true);
            dos.flush();
            dos.writeInt(tree_depth);
            dos.flush();
            dos.writeBoolean(dir_final);
            dos.flush();
            System.out.println("Directorio "+nombre+" enviado");
            System.out.println(tree_depth+"\n");
            dos.close();
            cl.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
   public static void sendTreeDepth(){
        try {
            Socket cl=new Socket(host, pto);
            System.out.println("Conexión con servidor establecida, se enviará un archivo...");
            String nombre="depth";
            DataOutputStream dos=new DataOutputStream(cl.getOutputStream());
            dos.writeUTF(nombre);
            dos.flush();
            dos.close();
            cl.close();
        } catch (IOException ex) {
            Logger.getLogger(Envia.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
}
