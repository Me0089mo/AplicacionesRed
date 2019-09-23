package buscaminas;

import java.io.Serializable;
/**
 *
 * @author axele
 */
public class ScoreBoard implements Serializable {
    String[] jugadores;
    int[] puntajes;
    
    public ScoreBoard(String[] jugadores, int[] puntajes){
        this.jugadores=jugadores;
        this.puntajes=puntajes;
    }//constructor
    
    String[] getJugadores(){
        return this.jugadores;
    }
    
    int[] getPuntajes(){
        return this.puntajes;
    }
}
