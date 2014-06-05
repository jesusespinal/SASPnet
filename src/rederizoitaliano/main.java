
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

    public static void main(String[] args) {

        String tablasDir = "tablasYA/";
        Nombres x = new Nombres();
        String[] nombres = new String[21];
        nombres = x.getNombres().toArray(nombres);
        
        for(String a: nombres){
            System.out.println(a);
        }
        
        /*
        Red red = new Red(tablasDir, nombres);
        JFdiscreteDyn v = new JFdiscreteDyn(red);
        v.setVisible(true);
        v.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        */
       
        
    }
}
