import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class Envia{
    public static String host="127.0.0.1";
    public static int pto=9000;
    
    public static void main(String args[]){
        System.out.println("Seleccione los archivos que desea enviar");
        JFileChooser jf=new JFileChooser();
        jf.setMultiSelectionEnabled(true);
        jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int r=jf.showOpenDialog(null);
        File[] f=jf.getSelectedFiles();
        
        // System.out.println(tr.toString());
        //DefaultTreeModel tree=new DefaultTreeModel(tn);
        if(r == JFileChooser.APPROVE_OPTION){
            for(int i=0; i<f.length; i++){
                if(f[i].isDirectory()){
                    DefaultMutableTreeNode tn=new DefaultMutableTreeNode(f[i].getName());
                    //DefaultTreeModel model=new DefaultTreeModel(tn);
                    filesInDirectory(f[i], tn);
                }
                else
                    sendFile(f[i]);
            }
        }
    }
    
    public static void filesInDirectory(File f, DefaultMutableTreeNode tn){
        File[] fc = f.listFiles();
        for(int i=0; i<fc.length; i++){
            if(fc[i].isDirectory()){
                DefaultMutableTreeNode cn=new DefaultMutableTreeNode(fc[i].getName());
                tn.add(cn);
                System.out.println(tn.getDepth());
                sendDirectory(fc[i], tn.getDepth());
                filesInDirectory(fc[i], cn);
            }
            else{
                DefaultMutableTreeNode cn=new DefaultMutableTreeNode(fc[i].getName());
                tn.add(cn);
                sendFile(fc[i]);
            }
        }
    }
    
    public static void sendFile(File f){
        try{
            Socket cl=new Socket(host, pto);
            System.out.println("Conexión con servidor establecida, se enviará un archivo...\n\n");
            String nombre=f.getName();
            long tam=f.length();
            String ruta=f.getAbsolutePath();
            DataOutputStream dos=new DataOutputStream(cl.getOutputStream());
            DataInputStream dis=new DataInputStream(new FileInputStream(ruta));
            dos.writeBoolean(false);
            dos.flush();
            dos.writeUTF(nombre);
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
                System.out.print("\rEnviado el "+porcentaje+"% del archivo");
            }
            System.out.println("Archivo enviado");
            dis.close();
            dos.close();
            cl.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void sendDirectory(File f, int tree_depth){
        try{
            Socket cl=new Socket(host, pto);
            System.out.println("Conexión con servidor establecida, se enviará un archivo...\n\n");
            String nombre=f.getName();
            long tam=f.length();
            String ruta=f.getAbsolutePath();
            DataOutputStream dos=new DataOutputStream(cl.getOutputStream());
            //DataInputStream dis=new DataInputStream(new FileInputStream(ruta));
            dos.writeBoolean(true);
            dos.flush();
            dos.writeInt(tree_depth);
            dos.flush();
            dos.writeUTF(nombre);
            dos.flush();
            /*dos.writeLong(tam);
            dos.flush();*/
            long enviados=0;
            int n=0, porcentaje=0;
            /*while(enviados<tam){
                byte[] b=new byte[2000];
                n=dis.read(b);
                enviados=enviados+n;
                dos.write(b, 0, n);
                dos.flush();
                porcentaje=(int)((enviados*100)/tam);
                System.out.print("\rEnviado el "+porcentaje+"% del archivo");
            }*/
            System.out.println("Archivo "+nombre+" enviado");
            //dis.close();
            dos.close();
            cl.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
