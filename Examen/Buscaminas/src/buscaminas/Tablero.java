package buscaminas;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicBorders;

/**
 *
 * @author Memo
 */
public class Tablero extends JFrame{
    
    private int width_tab, height_tab;
    private Socket cl;
    private int noMinas, noButton=1, bandCount;
    private int[] minas;
    private JFrame tablero;
    private ImageIcon img_mina, img_bandera;
    
    public Tablero(){}
    public Tablero(Socket cl, int level){
        URL resource = Tablero.class.getResource("/images/mina.jpg");
        ImageIcon img_aux = new ImageIcon(resource);
        img_mina = new ImageIcon(img_aux.getImage().getScaledInstance(15, 15, java.awt.Image.SCALE_DEFAULT));
        resource = Tablero.class.getResource("/images/bandera.jpg");
        img_aux = new ImageIcon(resource);
        img_bandera = new ImageIcon(img_aux.getImage().getScaledInstance(15, 15, java.awt.Image.SCALE_DEFAULT));
        bandCount = 10;
        this.cl = cl;
        if(level==1){
            generateTablero(9, 9, "Dificultad Facil");
            noMinas=10;
            width_tab=9;
            height_tab=9;
        }
        else if(level==2){
            generateTablero(16, 16, "Dificultad Intermedia");
            noMinas=40;
            width_tab=16;
            height_tab=16;
        }
        else{
            generateTablero(30, 16, "Dificultad Experta");
            noMinas=99;
            width_tab=16;
            height_tab=30;
        }
    }
    
    public void generateTablero(int imax, int jmax, String nivel){
        tablero=new JFrame("Buscaminas");
        tablero.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);       
        if(imax == 9)
            tablero.setSize(250, 295);  
        else if(imax == 16)
            tablero.setSize(390, 410);
        else
            tablero.setSize(390, 780);
        tablero.setLayout(null);
        tablero.validate();
        Container containerTab=tablero.getContentPane();
        JLabel label=new JLabel();
        label.setText(nivel);
        label.setBounds(100, 10, 150, 20);
        containerTab.add(label, -1);
        JButton btn;
        for(int i=0; i<imax; i++){
            for(int j=0; j<jmax; j++){
                btn=addButton();
                btn.setBounds(j*20+25, i*20+35, 20, 20);
                containerTab.add(btn, -1);
            }
        }
        tablero.setVisible(true);
    }
    
    public void locateMines(){
        Conection conect=new Conection(cl.getInetAddress().getHostAddress(), cl.getPort());
        Socket cl2 = conect.connect();
        minas = conect.receiveMap(cl2, noMinas);
        conect.disconnect(cl2);
    }
    
    private JButton addButton(){
        JButton btn=new JButton();
        btn.setName(String.valueOf(noButton));
        Insets margin= new Insets(0, 0, 0, 0);
        btn.setMargin(margin);
        MouseListener ml;
        ml = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                //int minasAlrededor=0;
                int casilla = Integer.parseInt(btn.getName());
                if(me.getButton()==1){
                    
                    int[] casillasVisitadas=new int[width_tab*height_tab];
                    for(int i=0; i<casillasVisitadas.length; i++){
                        casillasVisitadas[i] = 0;
                    }
                    openPath(casilla, casillasVisitadas);
                    endGame(0);
                }
                else if(me.getButton()==3){
                    if(btn.getIcon() == img_bandera){
                        btn.setText("");
                        bandCount++;
                    }
                    if(bandCount > 0){
                        if(btn.getIcon() != img_bandera){
                            btn.setIcon(img_bandera);
                            bandCount--;
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent me) {}

            @Override
            public void mouseReleased(MouseEvent me) {}

            @Override
            public void mouseEntered(MouseEvent me) {}

            @Override
            public void mouseExited(MouseEvent me) {}
        };
        btn.addMouseListener(ml);
        noButton++;
        return btn;
    }
    
    public boolean isMine(int casilla){
        for(int j=0; j<noMinas; j++){
            if(casilla==minas[j])
                return true;
        }
        return false;
    }
    
    public void openPath(int casilla, int[] casillasVisitadas){
        JButton btn = (JButton) tablero.getContentPane().getComponent(casilla);
        int minasAlrededor=0;
        if(casillasVisitadas[casilla-1] == 0){
            casillasVisitadas[casilla-1] = -1;
            if(isMine(casilla)){
                for(int i=0; i<noMinas; i++){
                    JButton btnMina = (JButton) tablero.getContentPane().getComponent(minas[i]);
                    btnMina.setEnabled(false);
                    btnMina.setIcon(img_mina);
                }
                endGame(1);
            }
            else{
                btn.setEnabled(false);
                //Primero se valida si la casilla es esquina y qué esquina es
                //Esquina superior izquierda
                if(casilla == 1){
                    if(isMine(casilla+1))
                        minasAlrededor++;
                    if(isMine(casilla+width_tab))
                        minasAlrededor++;
                    if(isMine(casilla+width_tab+1))
                        minasAlrededor++;
                    //Si no hay minas
                    if(minasAlrededor == 0){
                        openPath(casilla+1, casillasVisitadas);
                        openPath(casilla+width_tab+1, casillasVisitadas);
                        openPath(casilla+width_tab, casillasVisitadas);
                    }
                    else
                        btn.setText(String.valueOf(minasAlrededor));
                }
                //Esquiina superior derecha
                else if(casilla == width_tab){
                    if(isMine(casilla-1))
                        minasAlrededor++;
                    if(isMine(casilla+width_tab))
                        minasAlrededor++;
                    if(isMine(casilla+width_tab-1))
                        minasAlrededor++;
                    //Si no hay minas
                    if(minasAlrededor == 0){
                        openPath(casilla-1, casillasVisitadas);
                        openPath(casilla+width_tab-1, casillasVisitadas);
                        openPath(casilla+width_tab, casillasVisitadas);
                    }
                    else
                        btn.setText(String.valueOf(minasAlrededor));
                }
                //Esquina inferior izquierda
                else if(casilla == width_tab*(height_tab-1)+1){
                    if(isMine(casilla+1))
                        minasAlrededor++;
                    if(isMine(casilla-width_tab))
                        minasAlrededor++;
                    if(isMine(casilla-width_tab+1))
                        minasAlrededor++;
                    //Si no hay minas
                    if(minasAlrededor == 0){
                        openPath(casilla+1, casillasVisitadas);
                        openPath(casilla-width_tab+1, casillasVisitadas);
                        openPath(casilla-width_tab, casillasVisitadas);
                    }
                    else
                        btn.setText(String.valueOf(minasAlrededor));
                }
                //Esquina inferior derecha
                else if(casilla == width_tab*height_tab){
                    if(isMine(casilla-1))
                        minasAlrededor++;
                    if(isMine(casilla-width_tab))
                        minasAlrededor++;
                    if(isMine(casilla-width_tab-1))
                        minasAlrededor++;
                    //Si no hay minas
                    if(minasAlrededor == 0){
                        openPath(casilla-1, casillasVisitadas);
                        openPath(casilla-width_tab-1, casillasVisitadas);
                        openPath(casilla-width_tab, casillasVisitadas);
                    }
                    else
                        btn.setText(String.valueOf(minasAlrededor));
                }
                //Se valida si la casilla es un borde
                //Borde izquierdo
                else if(casilla%width_tab == 1){
                    if(isMine(casilla+1))
                        minasAlrededor++;
                    if(isMine(casilla-width_tab))
                        minasAlrededor++;
                    if(isMine(casilla-width_tab+1))
                        minasAlrededor++;
                    if(isMine(casilla+width_tab))
                        minasAlrededor++;
                    if(isMine(casilla+width_tab+1))
                        minasAlrededor++;
                    //Si no hay minas
                    if(minasAlrededor == 0){
                        openPath(casilla+1, casillasVisitadas);
                        openPath(casilla+width_tab+1, casillasVisitadas);
                        openPath(casilla+width_tab, casillasVisitadas);
                        openPath(casilla-width_tab+1, casillasVisitadas);
                        openPath(casilla-width_tab, casillasVisitadas);
                    }
                    else
                        btn.setText(String.valueOf(minasAlrededor));
                }
                //Borde derecho
                else if(casilla%width_tab == 0){
                    if(isMine(casilla-1))
                        minasAlrededor++;
                    if(isMine(casilla-width_tab))
                        minasAlrededor++;
                    if(isMine(casilla-width_tab-1))
                        minasAlrededor++;
                    if(isMine(casilla+width_tab))
                        minasAlrededor++;
                    if(isMine(casilla+width_tab-1))
                        minasAlrededor++;
                    //Si no hay minas
                    if(minasAlrededor == 0){
                        openPath(casilla-1, casillasVisitadas);
                        openPath(casilla+width_tab-1, casillasVisitadas);
                        openPath(casilla+width_tab, casillasVisitadas);
                        openPath(casilla-width_tab-1, casillasVisitadas);
                        openPath(casilla-width_tab, casillasVisitadas);
                    }
                    else
                        btn.setText(String.valueOf(minasAlrededor));
                }
                //Borde superior
                else if(casilla<=width_tab){
                    if(isMine(casilla+1))
                        minasAlrededor++;
                    if(isMine(casilla-1))
                        minasAlrededor++;
                    if(isMine(casilla+width_tab))
                        minasAlrededor++;
                    if(isMine(casilla+width_tab-1))
                        minasAlrededor++;
                    if(isMine(casilla+width_tab+1))
                        minasAlrededor++;
                    //Si no hay minas
                    if(minasAlrededor == 0){
                        openPath(casilla+1, casillasVisitadas);
                        openPath(casilla+width_tab+1, casillasVisitadas);
                        openPath(casilla+width_tab, casillasVisitadas);
                        openPath(casilla+width_tab-1, casillasVisitadas);
                        openPath(casilla-1, casillasVisitadas);
                    }
                    else
                        btn.setText(String.valueOf(minasAlrededor));
                }
                //Borde inferior
                else if(casilla>width_tab*(height_tab-1)){
                    if(isMine(casilla+1))
                        minasAlrededor++;
                    if(isMine(casilla-1))
                        minasAlrededor++;
                    if(isMine(casilla-width_tab))
                        minasAlrededor++;
                    if(isMine(casilla-width_tab-1))
                        minasAlrededor++;
                    if(isMine(casilla-width_tab+1))
                        minasAlrededor++;
                    //Si no hay minas
                    if(minasAlrededor == 0){
                        openPath(casilla+1, casillasVisitadas);
                        openPath(casilla-width_tab+1, casillasVisitadas);
                        openPath(casilla-width_tab, casillasVisitadas);
                        openPath(casilla-width_tab-1, casillasVisitadas);
                        openPath(casilla-1, casillasVisitadas);
                    }
                    else
                        btn.setText(String.valueOf(minasAlrededor));
                }
                //Para las casillas que no son ni bordes ni esquinas
                else{
                    if(isMine(casilla+1))
                        minasAlrededor++;
                    if(isMine(casilla-1))
                        minasAlrededor++;
                    if(isMine(casilla+width_tab))
                        minasAlrededor++;
                    if(isMine(casilla+width_tab-1))
                        minasAlrededor++;
                    if(isMine(casilla+width_tab+1))
                        minasAlrededor++;
                    if(isMine(casilla-width_tab))
                        minasAlrededor++;
                    if(isMine(casilla-width_tab-1))
                        minasAlrededor++;
                    if(isMine(casilla-width_tab+1))
                        minasAlrededor++;
                    //Si no hay minas
                    if(minasAlrededor == 0){
                        openPath(casilla+1, casillasVisitadas);
                        openPath(casilla-1, casillasVisitadas);
                        openPath(casilla+width_tab+1, casillasVisitadas);
                        openPath(casilla+width_tab-1, casillasVisitadas);
                        openPath(casilla+width_tab, casillasVisitadas);
                        openPath(casilla-width_tab+1, casillasVisitadas);
                        openPath(casilla-width_tab-1, casillasVisitadas);
                        openPath(casilla-width_tab, casillasVisitadas);
                    }
                    else
                        btn.setText(String.valueOf(minasAlrededor));
                }
            }            
        }
    }
    
    public void endGame(int perdio){
        int casillasLibres=0;
        int puntaje;
        for(int i=1; i<=width_tab*height_tab; i++){
            JButton btn = (JButton) tablero.getContentPane().getComponent(i);
            if(btn.isEnabled())
                casillasLibres++;
        }
        if(perdio == 0 && casillasLibres-noMinas == 0){
            puntaje=((width_tab*height_tab)-noMinas)*10;
            showPuntuations(puntaje);
        }
        if(perdio == 1){
            puntaje=(width_tab*height_tab)-casillasLibres-noMinas;
            showPuntuations(puntaje);
        } 
    }
    
    public void showPuntuations(int puntaje){
        //tablero.dispose();
        Conection cn=new Conection(cl.getInetAddress().getHostAddress(), cl.getPort());
        Socket cl2 = cn.connect();
        String alias = JOptionPane.showInputDialog("Tu puntaje ha sido "+puntaje+". Ingresa tu alias para ser registrado en la tabla de puntajes");
        ScoreBoard sc = cn.scoreBoard(cl2, alias, puntaje);
        JFrame puntuaciones = new JFrame("Puntuaciones");
        JList lista = new JList();
        String[] marcador=new String[10];
        for(int i=0; i<10; i++){
            if(sc.getJugadores()[i] != null)
                marcador[i] = sc.getJugadores()[i].concat("     ").concat(String.valueOf(sc.getPuntajes()[i]));
            else
                break;
        }
        lista.setListData(marcador);
        puntuaciones.getContentPane().add(lista);
        puntuaciones.setVisible(true);
    }
}
