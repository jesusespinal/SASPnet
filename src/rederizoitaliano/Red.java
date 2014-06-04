/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rederizoitaliano;

import java.util.*;
import fileOperations.*;
import java.text.DecimalFormat;

public class Red {

    ArrayList<Attractor> att; // Atractores
    private Nodo[] nn; // Arreglo de nodos
    private int size;   // Tamaño de toda la red
    private int numPresentes; // numero de nodos presentes
    private int numAusentes;  // numero de nodos que no estan presentes
//        private int compara;      // numero que va a comparar con el ifr en evolveBooleanSwitch

    public Red(String dirTablas, String[] nombres) {
        nn = Methods.creaRed(dirTablas, nombres);
        size = nn.length;
        numPresentes = size;
        numAusentes = 0;
        att = new ArrayList<Attractor>();
//                compara = 0;

    }

    public int getNumPresentes() {
        return numPresentes;
    }

    public int getNumAusentes() {
        return numAusentes;
    }

    public int getSize() {
        return size;
    }

    public Nodo getNodo(int i) {
        return nn[i];
    }

    public int[] getStates() {
        int[] s = new int[size];
        for (int n = 0; n < size; ++n) {
            s[n] = nn[n].getS();
        }
        return s;
    }

    public Attractor getAttractor(int l) {
        return att.get(l);
    }

    public int getNumAttractors() {
        return att.size();
    }

    public void addAttractor(Attractor a) {
        att.add(a);
    }

    public void setStates(int[] s) {
        for (int i = 0; i < size; ++i) {
            nn[i].setS(s[i]);
        }
    }

    /**
     * Asigna las etiquetas de presente/ausente a cada nodo
     * dependiendo de lo que se contenga en el arreglo b
     * @param b Arreglo de estados presente/ausente para la nn
     */
    public void setPresentes(boolean[] b) {
        numPresentes = 0;
        for (int i = 0; i < nn.length; ++i) {
            nn[i].setPresente(b[i]);
            if (b[i]) {
                ++numPresentes;
            }
        }
        numAusentes = size - numPresentes;
    }

    /**
     * Resetea la nn para que los nodos tengan sus estados originales
     * con los que dice el Chucho que se debe iniciar
     */
    public void setOriginalStates() {
        for (int i = 0; i < nn.length; ++i) {
            nn[i].setDefaultState();
        }
    }

    /**
     * Resetea las funciones reguladoras de los nodos para que sean las
     * funciones originales con las que el Chucho dice que se debe comenzar
     *
     */
    public void setOriginalFunctions() {
        for (int i = 0; i < nn.length; ++i) {
            nn[i].setDefaultFunction();
        }
    }

    /**
     * Asigna un estado aleatorio a los nodos de la nn
     *
     */
    public void setRandomStates() {
        int x;
        int s;
        for (int i = 0; i < nn.length; ++i) {
            s = (nn[i].isBinary()) ? 2 : 3;
            x = (int) (s * Math.random());
            nn[i].setS(x);
        }
    }

    /**
     * Este metodo evoluciona la red con la dinamica de Kauffman
     *
     */
    public void evolveKauffman() {
        Methods.evolveBoolean(nn);

    }

    /**
     * Evoluciona la red con Dinámica discreta con una función cambiada
     * @param compara, el entero proveniente del ternario del renglon a cambiar
     * @param nodoACambiar el nodo al que se le cambia la función
     */
    public void evolveKauffmanSwitch(int compara, int nodoACambiar) {
        Methods.evolveBooleanSwitch(nn, compara, nodoACambiar);
    }

    public void evolveKauffmanMayoria(Red r1, Red r2, Red r3){
        Nodo[] n1 = r1.nn;
        Nodo[] n2 = r2.nn;
        Nodo[] n3 = r3.nn;
        Methods.evolveReglaMayoria(n1, n2, n3);
    }
    
//    public void evolveKauffmanMayoriaChida(Red[] r, int numNodos){
////        ArrayList<Nodo[]> nodo = new ArrayList<Nodo[]>();
//        Nodo[] nodo = new Nodo[numNodos];
////        for(int n = 0; n < numNodos; ++n){
////            nodo.add(n, nn);
////        }
//        Methods.evolveReglaMayoriaChida(numNodos, nodo);
//    }
    
    public void evolveKauffmanCoupled(Red r1, Red r2, Red r3, double eps, double div, int pos, double rounder2, double rounder, int NodoAcoplador) {
        Nodo[] nodo1 = r1.nn;
        Nodo[] nodo2 = r2.nn;
        Nodo[] nodo3 = r3.nn;
        Methods.evolveCoupled(nodo1, nodo2, nodo3, eps, div, pos, rounder2, rounder, NodoAcoplador);
    }

    public void evolveKauffmanCola(Red r1, Red r2, double eps, double div, int pos) {
        Nodo[] nodo1 = r1.nn;
        Nodo[] nodo2 = r2.nn;
        Methods.evolveCola(nodo1, nodo2, eps, div, pos);
    }

    public void evolveKauffmanCuello(Red r1, Red r2, double eps, double div, int pos) {
        Nodo[] nodo1 = r1.nn;
        Nodo[] nodo2 = r2.nn;
        Methods.evolveCuello(nodo1, nodo2, eps, div, pos);
    }

    /**
     * Este metodo evoluciona la red con la dinamica de Glass
     */
    public void evolveGlass() {
    }

    public void saveAttractors(String fileName, int lt) {
        FileToWrite fw = new FileToWrite(fileName);
        fw.writeLine("Long transients = " + lt);
        Attractor a = null;
        int s;
        String numS;
        StringTokenizer stk;
        int numI;

        for (int na = 0; na < att.size(); ++na) {
            a = att.get(na);
            fw.writeLine("\nAttractor " + na + "; Length: " + a.getNumStates());
            fw.writeLine("Basin size = " + a.getBasin());
            fw.writeLine("\n*************************************************");
            for (int n = 0; n < size; ++n) {
                stk = new StringTokenizer(nn[n].getName(), "_");
                numS = stk.nextToken();
                numI = Integer.parseInt(numS);
                if (numI < 10) {
                    numS = "0" + numS;
                }
                fw.writeString(numS + "  ");
            }
            fw.writeLine("\n*************************************************");
            for (int l = 0; l < a.getNumStates(); ++l) {
                for (int n = 0; n < size; ++n) {
                    s = a.getState(l, n);
                    fw.writeString(s + "   ");
                }
                fw.writeLine();
            }
            fw.writeLine();
//        System.out.println("tamaño del viejo viejo  atractor: " + att.size());
        }

        att.removeAll(att);
//        System.out.println("tamaño del nuevo atractor: " + att.size());
        fw.close();
    }

    /*
     * VAMOS A HACER UN PAISAJE DISTINTO DE ATRACTORES
     * VAMOS A SYTEM.OUT.PRINTEAR EN PANTALLA LA
     * REPRESENTACION DECIMAL DE LOS BINARIO, TERNARIOS
     * Y EN EL EJE Z LA CUENCA
     */
    public void saveAttractorsLandscape(String fileName, int lt, int renglonComp, int NodoChange) {
        FileToWrite fw = new FileToWrite(fileName);
//        FileToWrite fw2 = new FileToWrite("catsper/Enero13/Switch/Atractores/at/Landscape.dat");
        fw.writeLine("Long transients = " + lt);
        Attractor a = null;
        int s;
        double t;
        String numS;
        StringTokenizer stk;
        int numI;
        double ternario = 0;
        int binario = 0;
        int ejeZ = 0;

        for (int na = 0; na < att.size(); ++na) {
            a = att.get(na);
//            System.out.println("Atract\tPeriodo\tCuenca\tBinario\tTernario");
            fw.writeLine("\nAttractor " + na + "; Length: " + a.getNumStates());
            fw.writeLine("Basin size = " + a.getBasin());
            fw.writeLine("\n*************************************************");
            for (int n = 0; n < size; ++n) {
                stk = new StringTokenizer(nn[n].getName(), "_");
                numS = stk.nextToken();
                numI = Integer.parseInt(numS);
                if (numI < 10) {
                    numS = "0" + numS;
                }
                fw.writeString(numS + "  ");
            }
            fw.writeLine("\n*************************************************");
            for (int l = 0; l < a.getNumStates(); ++l) {
                for (int n = 0; n < size; ++n) {
                    s = a.getState(l, n);
                    t = s;
//                    System.out.print("  " + s );
                    fw.writeString(s + "   ");
//                     if(nn[n].binario==false){
                    ternario += (t * (Math.pow(3, n)));
//                     }

                    binario += (s * Math.pow(2, n));


//                     }

                }
                s = 0;
                t = 0;
                        DecimalFormat d = new DecimalFormat("0000000000.0");
              System.out.println(//NodoChange + "\t" + renglonComp + "\t" + na + "\t" + a.getNumStates() + "\t" + a.getBasin() + "\t" + binario + "\t" +
                      d.format(ternario));
               
//fw2.writeLine("" +
//        ternario);
                               binario = 0;
                ternario = 0;
                fw.writeLine();
            }
            fw.writeLine();
//        System.out.println("tamaño del viejo viejo  atractor: " + att.size());
        }

        
//        System.out.println("tamaño del nuevo atractor: " + att.size());
        fw.close();
        att.removeAll(att);
//        fw2.close();
    }

    public void saveAttractorsFor(String fileName) {
        FileToWrite fw = new FileToWrite(fileName);
        fw.writeLine("Time");
        Attractor a;
        int s;
        String numS;
        StringTokenizer stk;
        int numI;

        for (int na = 0; na < att.size(); ++na) {
            a = att.get(na);
            fw.writeLine("\nAttractor " + na + "; Length: " + a.getNumStates());
            fw.writeLine("Basin size = " + a.getBasin());
            fw.writeLine("\n*************************************************");
            for (int n = 0; n < size; ++n) {
                stk = new StringTokenizer(nn[n].getName(), "_");
                numS = stk.nextToken();
                numI = Integer.parseInt(numS);
                if (numI < 10) {
                    numS = "0" + numS;
                }
                fw.writeString(numS + "  ");
            }
            fw.writeLine("\n*************************************************");
            for (int l = 0; l < a.getNumStates(); ++l) {
                for (int n = 0; n < size; ++n) {
                    s = a.getState(l, n);
                    fw.writeString(s + "   ");

                }
                fw.writeLine();
            }
            fw.writeLine();
        }
        fw.close();
    }

    /**
     * imprime numero periodo y cuenca con phase locking
     * @param lt long transient
     */
    public int printAttractors(int lt) {
        int[] atrs = new int[att.size()];
        int[] atrs2 = new int[atrs.length];

//        System.out.println("#Att\t\tPer\t\tBasin\t\tlongTrans: " + lt);
        Attractor a = null;
        int comparador = 1;
        int b = 0;
        for (int na = 0; na < att.size(); ++na) {
            a = att.get(na);
            atrs[na] = a.getNumStates();
//            atrs2[na] = atrs[na];
//            System.out.println(na + "\t\t" + atrs[na] + "\t\t" + a.getBasin());

        }
        Arrays.sort(atrs);

        int PhLock = 1;
        int cont = 0;
        atrs2[0] = atrs[0];
        for (int an = 1; an < atrs.length; ++an) {
            for (int or = 0; or < atrs.length; ++or) {

                if (atrs[or] < atrs[an]) {
                    atrs2[an] = atrs[an];
                    break;
                } else {
                    cont++;
                    break;
                }
            }
        }

        Arrays.sort(atrs2);
        int num = (atrs.length) - cont;
        int[] arrVdd = new int[num];
        for (int al = 0; al < num; ++al) {

            arrVdd[al] = atrs2[atrs2.length - 1 - al];
        }
        for (int i = 0; i < arrVdd.length; ++i) {
            PhLock *= arrVdd[i];
        }
//        System.out.println("========  " + PhLock + "\t\tEste es PhLock ???????????????????");

//        System.out.println(comparador + " <-- vale comparador");
//        Arrays.sort(atrs);

//        System.out.println("*************************************************\n");
//        att.removeAll(att);

        return PhLock;
    }
}
