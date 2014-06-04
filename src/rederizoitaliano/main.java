
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rederizoitaliano;

import javax.swing.*;
import java.io.*;
import fileOperations.*;
import java.util.*;

/**
 *
 * @author jespinal
 */
public class main {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        // Definimos un vector con los nombres de los archivos
        // donde se guarda la informacion de los nodos. Este arreglo NO se cambia
        
        String[] nombres = {
            //las de erizo
            "0_sr",
            "1_GC",
            "2_cGMP",
            "3_KCNG",
            "4_dK",
            "5_v",
            "6_NHE",
            "7_NCE",
            "8_HCN",
            "9_AC",
            "10_LVA",
            "11_pH",
            "12_dNa",
            "13_cAMP",
            "14_HVA",
            "16_CaP",
            "17_CaCC",
            "18_cAMPCC",
            "20_dCl",
            "22_PDE",
            "26_CaKC",
            "15_dCa"};

        String tablasDir = "tablasYA/";
        Red red = new Red(tablasDir, nombres);
        JFdiscreteDyn v = new JFdiscreteDyn(red);
        v.setVisible(true);
        v.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            
    }
}
