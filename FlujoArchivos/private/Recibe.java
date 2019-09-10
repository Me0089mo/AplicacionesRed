import java.net.*;
import java.io.*;
import javax.swing.JFileChooser;

public class Recibe{
    public static int actual_tree_depth=-1;
    public static String actual_path="C:\\Users\\Memo\\Documents\\Redes2\\ServerTests";
    public static void main(String args[]){
        try{
            int pto=9000;
            ServerSocket s=new ServerSocket(pto);
            System.out.println("Servicio iniciado... esperando clientes...");
            
            for(;;){
                Socket cl=s.accept();
                System.out.println("Cliente conectado...");
                DataInputStream dis=new DataInputStream(cl.getInputStream());
                boolean directory=dis.readBoolean();
                int dir_depth=0;
                if(directory == true)
                    dir_depth=dis.readInt();
                String nombre=dis.readUTF();
                long tam=0;
                System.out.println(actual_path);
                if(directory != true)
                    tam=dis.readLong();
                //System.out.println("Preparado para recibir el archivo"+nombre+"de"+tam+"bytes desde"+cl.getInetAddress()+":"+cl.getPort()); 
                if(directory == true){
                    if(dir_depth == actual_tree_depth){
                        File f=new File(nombre);
                        f.mkdir();
                        f.setWritable(true);
                        actual_path=f.getAbsolutePath();
                    }
                    else if(actual_tree_depth < dir_depth){
                        actual_tree_depth++;
                        File f=new File(actual_path+"\\"+nombre);
                        f.mkdir();
                        f.setWritable(true);
                        System.out.println(f.getAbsolutePath());
                        actual_path=f.getAbsolutePath();
                    }
                    else{
                        File aux=null;
                        while(actual_tree_depth > dir_depth){
                            actual_tree_depth--;
                            File f=new File(actual_path);
                            aux=new File(f.getParent());
                            actual_path=aux.getAbsolutePath();
                        }
                        aux.mkdir();
                        aux.setWritable(true);
                    }
                    System.out.println(actual_tree_depth);
                }
                
                if(directory == false){
                    DataOutputStream dos=new DataOutputStream(new FileOutputStream(actual_path+"\\"+nombre));
                    long recibidos=0;
                    int n=0, porcentaje=0;
              
                    while(recibidos<tam){
                        byte[] b=new byte[2000];
                        n=dis.read(b);
                        recibidos=recibidos+n;
                        dos.write(b, 0, n);
                        dos.flush();
                        porcentaje=(int)((porcentaje*100)/tam);
                    }
                    dos.close();
                }
                System.out.println("Archivo "+nombre+" recibido\n");
                dis.close();
                cl.close();
            }
            
        }catch(Exception e){
                e.printStackTrace();
        }
    }
}	