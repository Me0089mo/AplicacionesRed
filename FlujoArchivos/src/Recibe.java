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
                String nombre=dis.readUTF();
                boolean directory=false, dir_end=false;
                int dir_depth=0;
                long tam=0;
                if(nombre.equals("depth")){
                    File aux=new File(actual_path);
                    actual_path = aux.getParentFile().getAbsolutePath();
                    actual_tree_depth--;
                }    
                else{
                    directory=dis.readBoolean();
                    if(directory == true)
                        dir_depth=dis.readInt();
                    dir_end=dis.readBoolean();
                    System.out.println(actual_path);
                    if(directory != true)
                        tam=dis.readLong();
                }
                //System.out.println("Preparado para recibir el archivo"+nombre+"de"+tam+"bytes desde"+cl.getInetAddress()+":"+cl.getPort()); 
                if(directory == true){
                    if(dir_depth == actual_tree_depth){
                        File f=new File(actual_path+"\\"+nombre);
                        f.mkdir();
                        f.setWritable(true);
                        actual_path=f.getAbsolutePath();
                    }
                    else if(actual_tree_depth < dir_depth){
                        actual_tree_depth++;
                        File f=new File(actual_path+"\\"+nombre);
                        f.mkdir();
                        f.setWritable(true);
                        //System.out.println(f.getAbsolutePath());
                        actual_path=f.getAbsolutePath();
                    }
                    else{
                        System.out.println(actual_path);
                        while(actual_tree_depth > dir_depth){
                            actual_tree_depth--;
                            File f=new File(actual_path);
                            actual_path=f.getParentFile().getAbsolutePath();
                        }
                        System.out.println(actual_path);
                        File aux=new File(actual_path+"\\"+nombre);
                        aux.mkdir();
                        aux.setWritable(true);
                        actual_path=aux.getAbsolutePath();
                    }
                    System.out.println(actual_tree_depth);
                }
                
                if(directory == false && !nombre.equals("depth")){
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
                if(dir_end == true && !nombre.equals("depth")){
                    File aux=new File(actual_path);
                    actual_path = aux.getParentFile().getAbsolutePath();
                    actual_tree_depth--;
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