







/*
 * This is the core of subroutines whith which I developed 
 * my PhD studies. ONly thing you need is copy and paste
 * this file and linked with the Main.java file.
 *
 * This file has been developed using NetBeans 7.1. However,
 * it is easy to use it in another versions.
 *
 */
package rederizoitaliano;

import fileOperations.*;
import java.io.*;
//import jsc.*;
import java.util.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import jsc.datastructures.PairedData;
import jsc.regression.PearsonCorrelation;

public class Methods {

    /**
     * En este metodo se genera la red leyendo los datos de los archivos.
     * @param directorio
     * @param nombres. El arreglo con el nombre de los nodos
     * @return red. Regresa un arreglo con los nodos ya inicializados
     */
    public static Nodo[] creaRed(String directorio, String[] nombres) {

        int N = nombres.length;
        Nodo[] red = new Nodo[N];

        for (int n = 0; n < N; ++n) {
            red[n] = new Nodo(nombres[n], nombres, directorio);
        }
        return red;
    }

    /**
     * Este metodo evoluciona la red un paso de tiempo utilizando
     * la dinamica de Glass.
     * @param nn Arreglo de nodos
     * @param dt Paso de integracion para la ecuacion diferencial
     * @param red. La red que vamos a usar.
     */
    public static void evolveGlass(Nodo[] nn, double dt, Red red) {
        int N = red.getSize();
        //voy a darle valor a dt.
        dt = 0.001;

        int rea = 10000;
        String file;
        FileToWrite fw;

        //para hacer el mapeo de derrida completo
        for (int je = 0; je < 21; ++je) {
            file = "Glass/Glass_" + "" + ".dat";
            fw = new FileToWrite(file);



//        int N = nn.length;                              //numero de nodos
            double[] nC = new double[N];                    //arreglo de dobles de tamaño del número de nodos, para guardar variables continuas
            double a, x;
            int[] stR;                                      //este arreglo va a tener el estado de los reguladores de cada nodo
            int j, nr, ifr, st;

            //Con este ciclo for se va a asignar dinámicamente
            //valores para cada nodo, es decir que cada variable
            //arriba definida, tomará valores correspondientes
            //al estado del nodo que le toca

            for (int i = 0; i < N; ++i) {
                nr = nn[i].getNumReg();                     //guarda en nr el # de reguladores de cada uno de los nodos
                stR = new int[nr];                          //stR es un array de tamaño del # de reguladores del nodo i


                for (int m = 0; m < nr; ++m) {                //hasta este momento se le va a poner valor a los reguladores
                    j = nn[i].getReg(m);                    //"j" es el valor del regulador "1-m" del nodo nn[i].
                    //por ejemplo el Calcio tiene 7 reguladores, entonces "j" será el valor de
                    //HVA, luego de LVA, luego de pCa y asi sucesivamente hasta m=7.

                    x = nn[j].getC();                   //"x" ya es el valor contínuo del regulador "j" asignado dde forma aleatoria pero consistente
                    //con el estado que ya leyó
                    if (nn[j].isBinary()) {                   //si el regulador "j" es binario el valor continuo de "x" va a servir para poner el valor
                        for (int k = 0; k < 2; ++k) {         //entero de stR[m] entre 0 y 2. Si es ternario lo pondra entre 0 y 3.
                            if ((nn[j].getThr(k) <= x) && (x <= nn[j].getThr(k + 1))) {
                                stR[m] = k;
                                break;
                            }
                        }
                    } else {
                        for (int k = 0; k < 3; ++k) {
                            if ((nn[j].getThr(k) <= x) && (x <= nn[j].getThr(k + 1))) {
                                stR[m] = k;
                                break;
                            }
                        }
                    }                                               //hasta este momento ya tenemos el conjunto de valores de todos los reguladores
                }                                                   // del nodo "i"
                ifr = ternaryToInt(stR);                            //"ifr" va a guardar la representación decimal del arreglo ternario de stR
                st = nn[i].getFuncVal(ifr);                         //"st" va a guardar el valor de la función reguladora de "ifr", o sea del nodo "i"
                a = nn[i].getAlpha();                               //"a" guardará el coeficiente de relajación alpha del nodo "i"
                x = nn[i].getC();                                   //"x" será el valor contínuo del nodo "i"
                nC[i] = x + (a * (st - x) * dt);                          //el valor contínuo que se va a graficar se obtiene de restar st-x, este resultado
                //lo multiplica por el coeficiente de relajación y por el paso de integración.
                //hecho esto se lo suma a la "x". Esto va a hacer que nC[i] varíe de a poquitos
                // Se asegura de que el valor de la concentracion no se
                // salga de los rangos permitidos.
                if (nn[i].isBinary()) {
                    if (nC[i] > 1) {
                        nC[i] = 1;
                    }
                } else {
                    if (nC[i] > 2) {
                        nC[i] = 2;
                    }
                }
                if (nC[i] < 0) {
                    nC[i] = 0;
                }
            }
            for (int i = 0; i < N; ++i) {                 //este for es para ver que si el nodo está presente se haga todo, si no, se le pone 0 ó 1
                if (nn[1].isPresent()) {                  //dependiendo si es binario o ternario.
                    nn[i].setC(nC[i]);
                } else {
                    if (nn[i].isBinary()) {
                        nn[i].setC(0);
                    } else {
                        nn[i].setC(1);
                    }
                }
            }
        }
    }

    /**
     * Evoluciona la red con la dinamica de Kauffman.
     * @param nn Arreglo de nodos.
     */
    public static void evolveBoolean(Nodo[] nn) {
        int N = nn.length;                          //N tiene el tamaño de la red

        int[] nuevoEstado = new int[N];             //vector de tamaño N donde guarda
        //el estado nuevo (o sea t+1)
        int ifr, i, m, n, nr;
        int[] nt;

        for (n = 0; n < N; ++n) {               //for para cada nodo de la red
            nr = nn[n].getNumReg();            //"nr" guarda el número de reguladores del nodo N
            nt = new int[nr];                  //"nt" vector que guarda el núm d regs. de N

            for (m = 0; m < nr; ++m) {         //ESTE ES DENTRO DEL NODO, SU NUM DE REGULADORES

                //dame el estado del regulador "m" del
                i = nn[n].getReg(m);           //del nodo "n" y guardalo en "i"

                //el regulador n del nodo i va a
                nt[m] = nn[i].getS();          //adquirir el valor designado por getS() que da el
            }                                  //valor del estado que debe tener


            ifr = ternaryToInt(nt);
            nuevoEstado[n] = nn[n].getFuncVal(ifr);
        }
        for (n = 0; n < N; ++n) {
            if (nn[n].isPresent()) {
                nn[n].setS(nuevoEstado[n]);
            } else {
                if (nn[n].isBinary()) {
                    nn[n].setS(0);
                } else if (n == 21) {
                    nn[n].setS(0);
                } else {
                    nn[n].setS(1);
                }
            }
        }
    }

    /**
     * Evoluciona la red con la dinamica de Kauffman cuando todos
     * los nodos son booleanos
     * @param nn Arreglo de nodos.
     * @param compara, el entero que sale del ternario
     * @param nodoACambiar, indice del nodo al que se le cambia la función
     */
    public static void evolveBooleanSwitch(Nodo[] nn, int compara, int nodoACambiar) {
        int N = nn.length;                          //N tiene el tamaño de la red
        int[] nuevoEstado = new int[N];             //vector de tamaño N donde guarda
        int ifr, i, m, n, nr;
        int numRenglones = 1;
        int[] nt;
        for (n = 0; n < N; ++n) {               //for para cada nodo de la red
            nr = nn[n].getNumReg();            //"nr" guarda el número de reguladores del nodo N
            nt = new int[nr];                  //"nt" vector que guarda el núm d regs. de N

            for (m = 0; m < nr; ++m) {         //ESTE ES DENTRO DEL NODO, SU NUM DE REGULADORES
                i = nn[n].getReg(m);           //del nodo "n" y guardalo en "i"                //el regulador n del nodo i va a
                nt[m] = nn[i].getS();          //adquirir el valor designado por getS() que da el
            }                                  //valor del estado que debe tener

            numRenglones = 1;
            ifr = ternaryToInt(nt);
            nuevoEstado[n] = nn[n].getFuncVal(ifr);
            if (ifr == compara & n == nodoACambiar) {
//                System.out.println("nodo " + nn[nodoACambiar].getName()
//                        + " nuevo estado viejo " + nuevoEstado[n] + " y compara es " + compara);
                nuevoEstado[n] = nn[n].getSwitchFuncVal(ifr);
            }
        }

        for (n = 0; n < N; ++n) {
            if (nn[n].isPresent()) {
                nn[n].setS(nuevoEstado[n]);
            } else {
                if (nn[n].isBinary()) {
                    nn[n].setS(0);
                } else if (n == 21) {
                    nn[n].setS(0);
                } else {
                    nn[n].setS(1);
                }
            }
        }
    }

    /**
     * Convierte un arreglo con la representacion en base tres de un
     * numero a su entero en base 10.
     * @param nt
     * @return numero, el entero que se ha convertido
     */
    public static int ternaryToInt(int[] nt) {
        int numero = 0;
        int potencia = 1;

        for (int n = 0; n < nt.length; ++n) {
            numero += (potencia * nt[n]);
            potencia *= 3;
        }
        return numero;
    }

    /**
     * Convierte a ternario un entero dado, guardándolo en un
     * arreglo, inicialmente llenado con 0's
     * @param nt el número entero a convertir
     * @param b el arreglo donde va a guardar el ternnario
     */
    public static void intToTernary(int nt, int[] b) {
        for (int m = 0; m < b.length; ++m) {
            b[m] = 0;
        }
        if (nt > 0) {
            int p = 1;
            int m = 0;
            int res = nt;
            while (p <= nt) {
                p *= 3;
                ++m;
            }
            p /= 3;
            --m;
            while (m >= 0) {
                b[m] = res / p;
                if (p > 0) {
                    res %= p;
                }
                p /= 3;
                --m;
            }
        }
    }

    /**
     * Convierte un número binario guardado en
     * un arreglo "nt", en un entero llamado "numero"
     * @param nt el arreglo que contiene el binario
     * @return numero, el entero ya convertido
     */
    public static int binaryToInt(int[] nt) {
        int numero = 0;
        int potencia = 1;

        for (int n = 0; n < nt.length; ++n) {
            numero += (potencia * nt[n]);
            potencia *= 2;
        }
        return numero;
    }

    /**
     * Convierte a binario un entero dado guardándolo en un
     * arreglo, inicialmente llenado con 0's
     * @param nt el número entero a convertir
     * @param b el arreglo donde va a guardar el binario
     */
    public static void intToBinary(int nt, int[] b) {
        for (int m = 0; m < b.length; ++m) {
            b[m] = 0;
        }
        if (nt > 0) {
            int p = 1;
            int m = 0;
            int res = nt;
            while (p <= nt) {
                p *= 2;
                ++m;
            }
            p /= 2;
            --m;
            while (m >= 0) {
                b[m] = res / p;
                if (p > 0) {
                    res %= p;
                }
                p /= 2;
                --m;
            }
        }
    }

    /**
     * Sepa la chingada que chingaos hace este pinche método jajajaja
     * @param r la Red
     * @param numNodo el número del nodo? jajajajaja
     */
    /*
    public static void ArregloEnteroTabla(Red[] r, Nodo[] numNodo) {

        ArrayList Enteros = new ArrayList();
        int N = r.length;                          //N tiene el tamaño de la red
//System.out.println(compara);
        int[] nuevoEstado = new int[N];             //vector de tamaño N donde guarda
        //el estado nuevo (o sea t+1)
        int ifr, i, m, n, nr;
        int numRenglones = 1;
        int[] nt;

//        for (int IndArrComp = 0; IndArrComp < ArrComparison.length; ++IndArrComp) {

        for (n = 0; n < N; ++n) {               //for para cada nodo de la red
            numRenglones = 1;
//            for(terToCompare=0; terToCompare<(Math.pow(3.0,nn[n].numReg));++terToCompare){
            nr = numNodo[n].getNumReg();            //"nr" guarda el número de reguladores del nodo N
            nt = new int[nr];                  //"nt" vector que guarda el núm d regs. de N

            for (m = 0; m < nr; ++m) {         //ESTE ES DENTRO DEL NODO, SU NUM DE REGULADORES

                //dame el indice del regulador "m" del
                i = numNodo[n].getReg(m);           //del nodo "n" y guardalo en "i"
                if (numNodo[i].binario) {
                    numRenglones *= 2;
                } else {
                    numRenglones *= 3;
                }

                //el regulador n del nodo i va a
                nt[m] = numNodo[i].getS();          //adquirir el valor designado por getS() que da el
            }                                  //valor del estado que debe tener
            ifr = ternaryToInt(nt);

            Enteros.set(1, ifr);
        }
    }
*/
    
    /**
     * Hace la matriz de transferencia de atractores de periodo
     * a periodo 8 y viceversa
     * @param r la Red
     * @param iC Condiciones iniciales
     * @return 0
     */
    public static int markov(Red r, int iC) {
        int N = r.getSize();
        double[][] sum = new double[N][N];
        double[] VectSum = new double[N];
        for (int n = 0; n < N; ++n) {
            for (int i = 0; i < iC; i++) {
                r.setRandomStates();       //t = 0;
                r.getNodo(n).stInt = 1; //inicializo el valor de mi nodo target en 1
                r.evolveKauffman();     //evoluciono UN SOLO PASO la red
                for (int m = 0; m < N; m++) {
                    if (r.getNodo(m).isBinary()) {
                        sum[n][m] += r.getNodo(m).stInt; //la casilla [n][m] adquiere el
                    } //valor de la evolucion de la red
                    else {
                        sum[n][m] += r.getNodo(m).stInt / 1.5;
                    }
                }
            }
        }
        System.out.print("\t");
        for (int fila = 0; fila < N; ++fila) {
            System.out.print(fila + "\t\t\t");
        }
        System.out.println();
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {

                sum[i][j] /= iC;

//                VectSum[i] += sum[i][j];

            }
//             System.out.println();


            System.out.print(i + "\t");
            for (int ii = 0; ii < N; ++ii) {

//                sum[i][ii] /= VectSum[i];

                System.out.print((sum[i][ii]) + "\t\t\t");
            }
            System.out.println();
        }
//        }
        return 0;
    }

    /**
     * Este metodo calcula el mapeo de Derrida sin un nodo.
     * Este mapeo calcula la distancia de Hamming que hay entre 2 redes
     * a un tiempo "t+1", partiendo de una distancia "d", a un tiempo "t"
     * Se generan 2 redes, las cuales evolucionan con la misma dinámica
     * de Kauffman, pero la segunda red, tiene un nodo menos.
     * Por lo tanto, la red1 evoluciona normal, mientras la red2 evoluciona
     * sin un nodo.
     * Se fija la distancia "d" inicial, la dinámica corre un paso y se
     * recalcula la distancia "d" final. Esos puntos se grafican.
     * @param red1, la red original
     * @param red2 la red que se va a mover una distancia "d"
     */
    public static void derridaMap2(Red red1, Red red2, int del, String file) {
        int N = red1.getSize();
        double d1, d2;
        int rea = 10000;
        FileToWrite fw;
//        for (del = 0; del < N; ++del) {
        System.out.println("Vamos con el nodo " + del + " = " + red1.getNodo(del).getName());
        for (int n = 0; n < N; ++n) {
            red1.getNodo(n).setPresente(true);
            red2.getNodo(n).setPresente(true);
        }
        red1.getNodo(del).setPresente(false);
        red1.getNodo(14).setPresente(false);
//            red2.getNodo(del).setPresente(false);
//            file = "DerridaTotNuevasTablas" + red1.getNodo(del).getName()
//                    +  ".dat";
//            file = "/home/chucho/Desktop/ModeloCatsper/Hamming/Sin_" + red1.getNodo(del).getName() + ".dat";
        fw = new FileToWrite(file);
        for (int d = 0; d <= (2 * red1.getSize() / 3); ++d) {
            d1 = 0;
            d2 = 0;
            for (int r = 0; r < rea; ++r) {
                red1.setRandomStates();
                setDistance2(red1, red2, d, del);
//                    setDistance(red1, red2, d);
                d1 += hmDistance(red1, red2);
                red1.evolveKauffman();
                red2.evolveKauffman();
                d2 += hmDistance(red1, red2);
            }
            d1 /= rea;
            d2 /= rea;
            fw.writeLine(d1 + "\t\t" + (d2 - d1));
        }
        fw.close();
//        }
    }

    /**
     * setDistance ORIGINAL
     * Recibe dos redes, y pone los estados de la segunda red
     * iguales a los estados de la primera red con una probabilidad "d".
     * Esto hace que las dos redes tengan una distancia hamming d.
     * @param r1 La primera red. Esta no cambia.
     * @param r2 La segunda red. Los estados seran iguales a r1 con prob. "d"
     * @param d  La distancia Hamming que se quiere tener entre las redes.
     */
    public static void setDistance(Red r1, Red r2, int d) {
        int N = r1.getSize();
        int M = N;
        int s1, s2, i, n;
        int[] st = new int[N];
        int[] rst = new int[d];

        for (n = 0; n < N; ++n) {
            st[n] = n;
            s1 = r1.getNodo(n).getS();
            r2.getNodo(n).setS(s1);
        }

        --M;
        for (n = 0; n < d; ++n) {
            i = (int) (M * Math.random());
            rst[n] = st[i];
            st[i] = st[M - 1];
            --M;
        }

        for (n = 0; n < d; ++n) {
            i = rst[n];
            s1 = r1.getNodo(i).getS();
            if (r1.getNodo(i).isBinary()) {
                s2 = 1 - s1;
            } else {
                if (s1 == 0) {
                    s2 = (Math.random() < 0.5) ? 1 : 2;
                } else {
                    if (s1 == 1) {
                        s2 = (Math.random() < 0.5) ? 0 : 2;
                    } else {
                        s2 = (Math.random() < 0.5) ? 0 : 1;
                    }
                }
            }
            r2.getNodo(i).setS(s2);
        }

    }

    /**
     * Este metodo calcula el mapeo de Derrida ORIGINAL a muchos pasos.
     * @param red1 la red original
     * @param red2 la red que se va a comparar
     */
    public static void derridaMap(Red red1, Red red2, String file) {
        int N = red1.getSize();
        double d1, d2;
        int rea = 10000;
//        String file;
        FileToWrite fw;
        System.out.println("Vamos con el nodo wt");
//        for (int ink = 0; ink < N; ++ink) {
//            System.out.println("Vamos con el nodo " + ink + " = " + red1.getNodo(ink).getName());
        for (int n = 0; n < N; ++n) {
            red1.getNodo(n).setPresente(true);
            red2.getNodo(n).setPresente(true);
        }
//            red1.getNodo(ink).setPresente(false);
//            red2.getNodo(ink).setPresente(false);


//            file = "/home/chucho/Desktop/ModeloCatsper/Hamming/Derrida.dat";
        fw = new FileToWrite(file);
//            for (int ic = 1; ic < N; ++ic) {
//                for (int r = 0; r < rea; ++r) {
//                    red1.setRandomStates();
//                    setDistance(red1, red2, ink);
//                    red1.evolveKauffman();
//                    red2.evolveKauffman();
        //S += hmDistance(red1,red2);
//                    red1.setRandomStates();
//                    setDistance(red1, red2, ink);
//                    for (int derrs = 0; derrs < 3; ++derrs) {
        //para hacer el mapeo de derrida completo
//                        file = "Hamming_DerMod08nov11_" + "DerridaTot" + derrs + ".dat";
//                        fw = new FileToWrite(file);
        d1 = 0;
        d2 = 0;
        for (int d = 0; d <= (2 * red1.getSize() / 3); ++d) {
            d1 = 0;
            d2 = 0;
            for (int r = 0; r < rea; ++r) {
//                            for (int rr = 0; rr < 50; ++rr) {
                red1.setRandomStates();
                setDistance(red1, red2, d);
                d1 += hmDistance(red1, red2);
                //voy a comprobar que el mapeo de derrida es correcto.
                //primero dejo evolucionar mucho tiempo las dos redes.
                //luego obtengo el mapeo
                //luego hago eso muchas veces. Saco el promedio
                //repito ese paso muchas veces y saco el promedio
                //si es cercano a 0 el metodo esta bien. si no ni modo
                red1.evolveKauffman();
                red2.evolveKauffman();
                d2 += hmDistance(red1, red2);
            }
            d1 /= rea;
            d2 /= rea;
            fw.writeLine(d1 + "\t\t" + (d2));
        }
        fw.writeLine();
        fw.close();
//        }
//                }
//            }
//        }
    }

    /**
     * Este metodo calcula el mapeo de Derrida Realmente GRANDE.
     * Lo puse aqui para quitarlo del Main.
     */
    public static void derridaMapXinf(Red red1, Red red2) {
        int N = red1.getSize();
        double S, xinf, xit;


        int rea = 10000;

        String file, file2, file3;
        FileToWrite fw, fw2, fw3;
        double[] ht = new double[150];
        double[] ht2 = new double[150];
        for (int i = 0; i < 150; i++) {
            ht[i] = 0;
            ht2[i] = 0;
        }
        //para hacer el mapeo de derrida completo

        file = "Hamming/Hamming_" + "DerridaXinf" + ".dat";
        file2 = "Hamming/Hammingtd2" + ".dat";
        file3 = "Hamming/Hammingtd1" + ".dat";
        fw = new FileToWrite(file);
        fw2 = new FileToWrite(file2);
        fw3 = new FileToWrite(file3);
        for (int ic = 1; ic < N / 2; ++ic) {
            S = 0;
            xinf = 0;

            for (int r = 0; r < rea; ++r) {
                xit = 0;
                red1.setRandomStates();
                setDistance(red1, red2, 1);
                red1.evolveKauffman();
                red2.evolveKauffman();
                S += hmDistance(red1, red2);

                red1.setRandomStates();
                setDistance(red1, red2, ic);


                //voy a comprobar que el mapeo de derrida es correcto.
                //primero dejo evolucionar mucho tiempo las dos redes.
                //luego obtengo el mapeo
                //luego hago eso muchas veces. Saco el promedio
                //repito ese paso muchas veces y saco el promedio
                //si es cercano a 0 el metodo esta bien. si no ni modo
                int tCor = 100;
                for (int corr = 0; corr < tCor; ++corr) {
                    if (ic == 2) {
                        ht[corr] += hmDistance(red1, red2);
                    }
                    if (ic == 1) {
                        ht2[corr] += hmDistance(red1, red2);
                    }
                    red1.evolveKauffman();
                    red2.evolveKauffman();

                }
                for (int corr = 0; corr < 50; ++corr) {
                    if (ic == 2) {
                        ht[corr + 100] += hmDistance(red1, red2);
                    }
                    if (ic == 1) {
                        ht2[corr + 100] += hmDistance(red1, red2);
                    }
                    red1.evolveKauffman();
                    red2.evolveKauffman();
                    xit += hmDistance(red1, red2);

                }
                xit /= 50;
                xinf += xit;
            }

            xinf /= rea;
            S /= rea;
            S *= N;
            fw.writeLine(ic + "\t\t" + S + "\t\t" + xinf);
        }

        for (int i = 0; i < 150; i++) {
            ht[i] /= rea;
            ht2[i] /= rea;
            fw2.writeLine(i + "\t\t" + ht[i]);
            fw3.writeLine(i + "\t\t" + ht2[i]);
        }


        fw.writeLine();
        fw.close();
        fw2.close();
        fw3.close();
    }

    /**
     * Recibe dos redes, y pone los estados de la segunda red
     * iguales a los estados de la primera red con una probabilidad "d".
     * Esto hace que las dos redes tengan una distancia hammin d.
     * @param r1 La primera red. Esta no cambia.
     * @param r2 La segunda red. Los estados seran iguales a r1 con prob. "d"
     * @param d  La distancia Hamming que se quiere tener entre las redes.
     * @param ink el índice del nodo que va a utilizar
     */
    public static void setDistance2(Red r1, Red r2, int d, int ink) {
        int N = r1.getSize();
        int M = N;
        int s1, s2, i, n;
        int[] st = new int[N];
        int[] rst = new int[d];      //d es el orden de los puntos del mapeo de Derrida

        for (n = 0; n < N; ++n) {
            st[n] = n;

            s1 = r1.getNodo(n).getS();//s1 toma un valor aleatorio dependiendo de si es binario o ternario

            r2.getNodo(n).setS(s1);
        }

        st[ink] = M - 1; //no entiendo por que st[ink] es 23


        --M;
        for (n = 0; n < d; ++n) {


            i = (int) (M * Math.random());//con este metodo te aseguras de llenar de modo
            rst[n] = st[i];              //aleatorio las casillas de rst[] sin repetir
            st[i] = st[M - 1];               //
            --M;                            //

        }

        for (n = 0; n < d; ++n) {
            i = rst[n];         // i va a tomar valores entre 0 y N
            s1 = r1.getNodo(i).getS();  //s1 va a tomar el valor que hay en el nodo i de la red.
            if (r1.getNodo(i).isBinary()) {
                s2 = 1 - s1;      //si es binario o ternario s2 tendra el valor contrario a s1.
            } //  Con este for se establece la distancia de Hamming d para todos los nodos
            else {
                if (s1 == 0) {
                    s2 = (Math.random() < 0.5) ? 1 : 2;
                } else {
                    if (s1 == 1) {
                        s2 = (Math.random() < 0.5) ? 0 : 2;
                    } else {
                        s2 = (Math.random() < 0.5) ? 0 : 1;
                    }
                }
            }
//            System.out.print(s1+"\t"+s2);
//             System.out.println();

            r2.getNodo(i).setS(s2);
        }  //System.out.println();
        if (r1.getNodo(ink).isBinary()) {   //aqui es cuando elige si el nodo de r1 ink es binario le pone 1 y a r2 0;
            r1.getNodo(ink).setS(1);      // si es ternario le pone 2 ó 0 para que a la red r2 le ponga 1 y asi entre
            r2.getNodo(ink).setS(0);        //nodos la distancia siempre sea 1.
        } else {
            s1 = (Math.random() < 0.5) ? 0 : 2;
            r1.getNodo(ink).setS(s1);
            r2.getNodo(ink).setS(1);
        }

    }

    /**
     * Este es el bueno, arriba es el Derrida sin muchos
     * @param red1
     * @param red2
     */
    public static void derridaMapQ(Red red1, Red red2) {
        int N = red1.getSize();
        double d1, d2;
        double S, xinf, xit;
        int rea = 1000;
        String file;
        FileToWrite fw;


        for (int ink = 0; ink < N; ++ink) {
            System.out.println("Vamos con el nodo " + ink + " = " + red1.getNodo(ink).getName());
            for (int n = 0; n < N; ++n) {
                red1.getNodo(n).setPresente(true);
                red2.getNodo(n).setPresente(true);
            }

            red2.getNodo(ink).setPresente(false);
            file = "catsper/Hamming/DerridaSin_" + red1.getNodo(ink).getName() + ".dat";
            fw = new FileToWrite(file);
            for (int ic = 1; ic < N; ++ic) {
                S = 0;
                xinf = 0;

                for (int r = 0; r < rea; ++r) {
                    xit = 0;
                    red1.setRandomStates();
                    setDistance(red1, red2, 1);
                    red1.evolveKauffman();
                    red2.evolveKauffman();
                    S += hmDistance(red1, red2);

                    red1.setRandomStates();
                    setDistance(red1, red2, ic);


                    //voy a comprobar que el mapeo de derrida es correcto.
                    //primero dejo evolucionar mucho tiempo las dos redes.
                    //luego obtengo el mapeo
                    //luego hago eso muchas veces. Saco el promedio
                    //repito ese paso muchas veces y saco el promedio
                    //si es cercano a 0 el metodo esta bien. si no ni modo
//                    int tCor = 100;
//                    for (int corr = 0; corr < tCor; ++corr) {
//                        red1.evolveKauffman();
//                        red2.evolveKauffman();
//                    }
//                    for (int corr = 0; corr < 50; ++corr) {
//                        red1.evolveKauffman();
//                        red2.evolveKauffman();
//                        xit += hmDistance(red1, red2);
//                    }
//                    xit /= 50;
//                    xinf += xit;
                }

                xinf /= rea;
                S /= rea;
                S *= N;
                fw.writeLine(ic + "\t\t" + S + "\t\t" + xinf);

            }
            fw.close();
        }

    }

    /**
     * Calcula la distancia Hamming entre dos redes
     * @param r1 La primera red
     * @param r2 La segunda red
     * @return Regresa la distancia Hamming
     */
    public static double hmDistance(Red r1, Red r2) {
        double d = 0;
        int s1, s2;
        for (int i = 0; i < r1.getSize(); ++i) {
            s1 = r1.getNodo(i).getS();
            s2 = r2.getNodo(i).getS();
//System.out.println(Math.abs(s1-s2) + "  " + s2 + s1);
//System.out.println();
            if (s1 != s2) {
//                System.out.println(Math.abs(s1-s2) + "  " + s2 + s1);
                ++d;
            }
        }
        d /= r2.getSize();
        return d;
    }

    /**
     * Compara dos arreglos de enteros para saber si son inguales.
     * @param a1 Primer arreglo a comparar
     * @param a2 Segudo arreglo a comparar
     * @return Regresa true si a1 = a2; Regresa false si a1 != a2.
     */
    public static boolean arrayEquals(int[] a1, int[] a2) {
        boolean b = true;
        if (a1.length != a2.length) {
            b = false;
            return b;
        } else {
            for (int n = 0; n < a1.length; ++n) {
                if (a1[n] != a2[n]) {
                    b = false;
                    return b;
                }
            }
        }
        return b;
    }

    /**
     * Encuentra los atractores de la red recorriendo todos los
     * estados dinamicos.
     * @param r La red a la que le va a encontrar los atractores
     * @return longTrans número de transitorios largos
     */
    public static int findAttAll(Red r) {
        int nb;
        int nt;
        int N = r.getSize();                    //N = tamaño de la red
        int Nb = r.getSize() - 1;                //Nb = tamaño de la red -1
        int Cb = (int) (Math.pow(2, Nb) + 0.1);   //Cb = 2 a la tamaño de red
        int[] stb = new int[Nb];                //arreglo de tamaño de la red -1
        int[] s = new int[N];                   //arreglo tamaño de la red
        int tMax = 100;                         // tiempo maximo de evolucion
        int[][] trj;                            //matriz trj
        int cuenta = 0;
        int l, k, m, n, t;
        int longTrans = 0;
        boolean busca, ya;
        Attractor a;                            //objeto tipo atractor

        for (nt = 0; nt < 3; ++nt) {              //FORSOTE
            for (nb = 0; nb < Cb; ++nb) {         //de 0 a Cb (2 a la red.size)
                ++cuenta;
                if (cuenta == 100001) {
                    System.out.println("Condición inicial: " + nb);
                    cuenta = 0;
                }
                intToBinary(nb, stb);            //convierte a binario TODAS las
                m = 0;                          //posibilidades, guardando cada
                //una en stb. O sea que va a llenar
                //2^21 arreglos con el numero binario correspondiente
                //a su entero para hacer la dinámica con ese arreglo
                for (n = 0; n < r.getSize(); ++n) {
                    if (r.getNodo(n).getType() == 2) {
                        s[n] = stb[m];
                        ++m;
                    } else {
                        s[n] = nt;
                    }
                }
                r.setStates(s);
                trj = new int[tMax][N];
                for (t = 0; t < tMax; ++t) {
                    r.evolveKauffman();
                    trj[t] = r.getStates();
                }
                t = tMax - 2;
                busca = true;
                l = 0;
                while ((busca) && (t > 0)) {
                    if (arrayEquals(trj[t], trj[tMax - 1])) {
                        busca = false;
                        l = (tMax - 1) - t;
                    } else {
                        --t;
                    }
                }
                if (l > 0) {
                    a = new Attractor(l, N);
                    for (k = 0; k < l; ++k) {
                        t = tMax - 1 - l + k;
                        a.addState(trj[t], k);
                    }
                    if (r.getNumAttractors() == 0) {
                        r.addAttractor(a);
                    } else {
                        ya = false;
                        for (k = 0; k < r.getNumAttractors(); ++k) {
                            if (r.getAttractor(k).equals(a)) {
                                r.getAttractor(k).ppBasin();
                                ya = true;
                                break;
                            }
                        }
                        if (!ya) {
                            r.addAttractor(a);
                        }
                    }
                } else {
                    ++longTrans;
                }
            }
        }
        return longTrans;
    }

    /**
     * Encuentra los atractores de la red solamente sampleando los estados
     * dinamicos. Por eso se llama "Under" porque "Undersamplea".
     * @param r La red a la que le va a encontrar los atractores
     * @param iC El número de condiciones iniciales
     *
     * Adicionalmente, puede encontrar los atractores sin uno o mas nodos
     */
    public static int findAttUnder(Red r, int iC) {
        int N = r.getSize();
        int tMax = 100;
        int[][] trj;
        int l, k, t;
        int longTrans = 0;
        boolean busca, ya;
        Attractor a;
        int cuenta = 0;
//        for (int nodoDel = 0; nodoDel < N; ++nodoDel) {
        for (int ka = 0; ka < N; ++ka) {
            r.getNodo(ka).setPresente(true);
        }
        r.getNodo(8).setPresente(false);
        r.getNodo(16).setPresente(false);
//        r.getNodo(20).setPresente(false);
//            System.out.println(r.getNodo(10).getName() + "   " + r.getNodo(14).getName()+ "   " + r.getNodo(18).getName());
        for (int ic = 0; ic < iC; ++ic) {
            ++cuenta;
            if (cuenta > 10000) {
                System.out.println("Voy en el estado " + ic);
                cuenta = 0;
            }
            r.setRandomStates();

            trj = new int[tMax][N];
            for (t = 0; t < tMax; ++t) { 
//                r.getNodo(20).setS(1);
                r.evolveKauffman();
                r.getNodo(20).setS(1);
//                if(t%5 != 0){
               
//                }
//                r.getNodo(18).setS(1);
                trj[t] = r.getStates();
            }
            t = tMax - 2;
            busca = true;
            l = 0;
            while ((busca) && (t > 0)) {
                if (arrayEquals(trj[t], trj[tMax - 1])) {
                    busca = false;
                    l = (tMax - 1) - t;
                } else {
                    --t;
                }
            }
            if (l > 0) {
                a = new Attractor(l, N);
                for (k = 0; k < l; ++k) {
                    t = tMax - 1 - l + k;
                    a.addState(trj[t], k);
                }
                if (r.getNumAttractors() == 0) {
                    r.addAttractor(a);
                } else {
                    ya = false;
                    for (k = 0; k < r.getNumAttractors(); ++k) {
                        if (r.getAttractor(k).equals(a)) {
                            r.getAttractor(k).ppBasin();
                            ya = true;
                            break;
                        }
                    }
                    if (!ya) {
                        r.addAttractor(a);
                    }
                }
            } else {
                ++longTrans;
            }
        }
        return longTrans;
    }

    /**
     * Encuentra los atractores con UNA FUNCION CAMBIADA EN UN SOLO NODO
     * EN UNA SOLA ENTRADA, BARRIENDO TODAS LAS CONDICIONES INICIALES
     * de la red solamente sampleando los estados
     * dinamicos. Por eso se llama "Under" porque "Undersamplea".
     * @param r La red a la que le va a encontrar los atractores
     * @param iC el número de condiciones iniciales que hay
     * @param renglon, el renglon al que se le cambia la función
     * @param ChangedNode el nodo al que se le cambia la función.
     */
    public static int findAttUnderSwitch(Red r, int iC, int renglon, int ChangedNode) {

        FileToWrite fw = null;
//FileToWrite igual = null;
//FileToWrite CasiIgual = null;
//FileToWrite igual = new FileToWrite("AnalisisAtsFull/igualitos.txt");
//FileToWrite CasiIgual = new FileToWrite ("AnalisisAtsFull/IgualMod4.txt");
        int N = r.getSize();
        int tMax = 150;
        int[][] trj;

        int l = 0;
        int k, t;
        int longTrans = 0;
        boolean busca, ya;
        Attractor a = null;
        int cuenta = 0;
        for (int ic = 0; ic < iC; ++ic) {
            r.setOriginalFunctions();
//            r.setOriginalStates();
            r.setRandomStates();
//            r.evolveKauffman();
            trj = new int[tMax][N];

            for (t = 0; t < tMax; ++t) {
//                        r.evolveKauffman();

                r.evolveKauffmanSwitch(renglon, ChangedNode);
                trj[t] = r.getStates();
//                for (int m =0; m<N; ++m){
//                    System.out.print(" " + trj[t][m]);
//                }
//                System.out.println();
            }
            t = tMax - 2;
            busca = true;
            l = 0;                      //Periodo del atractor
            while ((busca) && (t > 0)) {
                if (arrayEquals(trj[t], trj[tMax - 1])) {
                    busca = false;
                    l = (tMax - 1) - t;
                } else {
                    --t;
                }
            }
            if (l > 0) {

                //aca veo que pedo con el atractor



                a = new Attractor(l, N);
                for (k = 0; k < l; ++k) {
                    t = tMax - 1 - l + k;
                    a.addState(trj[t], k);
                }
//                System.out.println("pasos de este " + a.getNumStates());
                if (r.getNumAttractors() == 0) {
                    r.addAttractor(a);
                } else {
                    ya = false;
                    for (k = 0; k < r.getNumAttractors(); ++k) {
                        if (r.getAttractor(k).equals(a)) {
                            r.getAttractor(k).ppBasin();
                            ya = true;
                            break;
                        }
                    }
                    if (!ya) {
                        r.addAttractor(a);
                    }
                }
            } else {
                ++longTrans;
            }
            if (longTrans > 0) {
//                System.out.println("Nodo cambiado " + ChangedNode + "   renglon " + renglon
//                        + " transitorios largos " + longTrans);
            }
        }
        Attractor atr = null;
        int prodPeriod = 1;  //producto de los periodos
//        for (int reng = 1; reng <= 40; ++reng) {
//          if (r.getNumAttractors() > 5 || r.getNumAttractors() < 3){
//            if (r.getNumAttractors() == reng) {
//                fw = new FileToWrite("Atractores22Sep11/"
//                        + "NumAts_" + r.getNumAttractors() + "_ChNode_" + ChangedNode + "_Reng_" + renglon + ".txt");
        int m = r.getNumAttractors();
//               System.out.println("\nnumero de atractores: " +m );
//                fw.writeLine("\nnumero de atractores: " + m);
//                fw.writeLine("Att\t\tPeriodo\t\tCuenca\t\t");
        for (int b = 0; b < r.att.size(); ++b) {

            atr = r.att.get(b);
            int cuenca = r.att.get(b).basin;

//                    int cuenca2 = a.getBasin();
            prodPeriod *= atr.getNumStates();


//                    fw.writeLine(b + "\t\t" + atr.getNumStates() + "\t\t" + cuenca);

        }

//                fw.close();
//System.out.println("Producto de los periodos: " + prodPeriod );
        if (prodPeriod == 2048) {
//                      igual.writeLine("nodo cambiado "+" renglon ");
//                      System.out.println("producto de los periodos " + prodPeriod + " nodo cambiado "
//                              +ChangedNode + " renglon " + renglon);
        } else if (prodPeriod % 4 != 0) {
//
//                CasiIgual.writeLine("numero de atractores: " +m + "\t" +
//                        "Nodo cambiado: " +ChangedNode + "\t" +
//                        "renglon cambiado: " + renglon + "\t");
//                CasiIgual.close();
//                    System.out.println("Atractores " + m + "\t"
//                            + "Nodo cambiado: " + ChangedNode + "\t"
//                            + "renglon cambiado: " + renglon + "\t" + "periodo " + atr.getNumStates());
        } else;

//                   fw.close();
//               }//fin for b
//            }

//                }

//        }

        return longTrans;
    }

    /**
     * Encuentra los atractores de la red PARTIENDO DE
     * CONDICIONES INICIALES QUE TE LLEVAN A LOS
     * ATRACTORES DE PERIODO 8 solamente sampleando los estados
     * dinamicos. Por eso se llama "Under" porque "Undersamplea".
     * @param r La red a la que le va a encontrar los atractores
     * @param iC el número de condiciones iniciales a jugar
     */
    public static int findAttUnderSolo8(Red r, int iC) {

        String file;
        //agarra los arreglos que llevan a periodo 8 y que a partir de esos corra
        //la dinamica, a ver a donde chingaos llega con todos y sin uno

//        Metodos.findFirstArrayToPeriod8(r);

        int N = r.getSize();
        int tMax = 80;
        int[][] trj;

        int l, k, t;
        int longTrans = 0;
        boolean busca, ya;
        Attractor a;
        int cuenta = 0;
        for (int ic = 0; ic < iC; ++ic) {
//            ++cuenta;
//            if (cuenta > 10000) {
//                System.out.println("Voy en el estado " + ic);
//                cuenta = 0;
//            }
            r.setRandomStates();

            trj = new int[tMax][N];
            for (t = 0; t < tMax; ++t) {
                r.evolveKauffman();
                trj[t] = r.getStates();
            }
            t = tMax - 2;
            busca = true;
            l = 0;
            while ((busca) && (t > 0)) {
                if (arrayEquals(trj[t], trj[tMax - 1])) {
                    busca = false;
                    l = (tMax - 1) - t;
                } else {
                    --t;
                }
            }
            if (l > 0) {
                a = new Attractor(l, N);
                for (k = 0; k < l; ++k) {
                    t = tMax - 1 - l + k;
                    a.addState(trj[t], k);
                }
                if (r.getNumAttractors() == 0) {
                    r.addAttractor(a);
                } else {
                    ya = false;
                    for (k = 0; k < r.getNumAttractors(); ++k) {
                        if (r.getAttractor(k).equals(a)) {
                            r.getAttractor(k).ppBasin();
                            ya = true;
                            break;
                        }
                    }
                    if (!ya) {
                        r.addAttractor(a);

                    }
                }
            } else {
                ++longTrans;
            }
        }
        return longTrans;
    }

    /**
     * Encuentra los atractores de la red QUITANDO UN NODO A LA RED
     * solamente sampleando los estados
     * dinamicos. Por eso se llama "Under" porque "Undersamplea".
     * @param r La red a la que le va a encontrar los atractores
     */
    public static int findAttUnderSinUno(Red r, int iC, int del) {
        int N = r.getSize();
        int tMax = 100;
        int[][] trj;
        int l, k, t;
        int longTrans = 0;
        boolean busca, ya;
        int cuenta = 0;
        Attractor a;
        for (int an = 0; an < N; ++an) {
            r.getNodo(an).setPresente(true);
        }
        r.getNodo(del).setPresente(false);
        for (int ic = 0; ic < iC; ++ic) {
//                 ++cuenta;
//            if (cuenta > 10000) {
//                System.out.println("Voy en el estado " + ic);
//                cuenta = 0;
//            }
            r.setRandomStates();
            trj = new int[tMax][N];
            for (t = 0; t < tMax; ++t) {
//                r.evolveKauffmanSwitch(2, 8);
//                r.getNodo(17).setS(1);
                r.evolveKauffman();
//                if (t % 2 == 1) {
//                    r.getNodo(17).setS(1);
//                    r.getNodo(20).setS(1);
//                }
                trj[t] = r.getStates();
            }
            t = tMax - 2;
            busca = true;
            l = 0;
            while ((busca) && (t > 0)) {
                if (arrayEquals(trj[t], trj[tMax - 1])) {
                    busca = false;
                    l = (tMax - 1) - t;
                } else {
                    --t;
                }
            }
            if (l > 0) {
                a = new Attractor(l, N);
                for (k = 0; k < l; ++k) {
                    t = tMax - 1 - l + k;
                    a.addState(trj[t], k);
                }
                if (r.getNumAttractors() == 0) {
                    r.addAttractor(a);
                } else {
                    ya = false;
                    for (k = 0; k < r.getNumAttractors(); ++k) {
                        if (r.getAttractor(k).equals(a)) {
                            r.getAttractor(k).ppBasin();
                            ya = true;
                            break;
                        }
                    }
                    if (!ya) {
                        r.addAttractor(a);
                    }
                }
            } else {
                ++longTrans;
            }
        }
        return longTrans;
    }

    /**
     * Encuentra los atractores de la red recorriendo todos los
     * estados dinamicos.
     * @param r La red a la que le va a encontrar los atractores
     */
    public static int findAttAllSwitch(Red r, int renglon, int ChangedNode) {
        FileToWrite fw = null;
        int nb;
        int nt;
        int N = r.getSize();                    //N = tamaño de la red
        int Nb = r.getSize() - 1;                //Nb = tamaño de la red -1
        int Cb = (int) (Math.pow(2, Nb) + 0.1);   //Cb = 2 a la tamaño de red
        int[] stb = new int[Nb];                //arreglo de tamaño de la red -1
        int[] s = new int[N];                   //arreglo tamaño de la red
        int tMax = 200;                         // tiempo maximo de evolucion
        int[][] trj;                            //matriz trj

        int cuenta = 0;
        int l, k, m, n, t;
        int longTrans = 0;
        boolean busca, ya;
        Attractor a;                            //objeto tipo atractor
        for (nt = 0; nt < 3; ++nt) {              //FORSOTE
            for (nb = 0; nb < Cb; ++nb) {         //de 0 a Cb (2 a la red.size)

                ++cuenta;
//                if (cuenta == 100001) {
//                    System.out.println("Condición inicial: " + nb);
//                    cuenta = 0;
//                }
                intToBinary(nb, stb);            //convierte a binario TODAS las
                m = 0;                          //posibilidades, guardando cada
                //una en stb. O sea que va a llenar
                //2^21 arreglos con el numero binario correspondiente
                //a su entero para hacer la dinámica con ese arreglo
                for (n = 0; n < r.getSize(); ++n) {
                    if (r.getNodo(n).getType() == 2) {
                        s[n] = stb[m];
                        ++m;
                    } else {
                        s[n] = nt;
                    }
                }
                r.setStates(s);
                trj = new int[tMax][N];
                for (t = 0; t < tMax; ++t) {
                    r.evolveKauffmanSwitch(renglon, ChangedNode);
                    trj[t] = r.getStates();
                }
                t = tMax - 2;
                busca = true;
                l = 0;
                while ((busca) && (t > 0)) {
                    if (arrayEquals(trj[t], trj[tMax - 1])) {
                        busca = false;
                        l = (tMax - 1) - t;
                    } else {
                        --t;
                    }
                }
                if (l > 0) {
                    a = new Attractor(l, N);
                    for (k = 0; k < l; ++k) {
                        t = tMax - 1 - l + k;
                        a.addState(trj[t], k);
                    }
                    if (r.getNumAttractors() == 0) {
                        r.addAttractor(a);
                    } else {
                        ya = false;
                        for (k = 0; k < r.getNumAttractors(); ++k) {
                            if (r.getAttractor(k).equals(a)) {
                                r.getAttractor(k).ppBasin();
                                ya = true;
                                break;
                            }
                        }
                        if (!ya) {
                            r.addAttractor(a);
                        }
                    }
                } else {
                    ++longTrans;
                }
            }

            return longTrans;
        }



        //5 DE aBRIL
        //ESTAS LINEAS LAS QUITE
//        Attractor atr = null;
//        int prodPeriod = 1;  //producto de los periodos
//        for (int reng = 1; reng <= 20; ++reng) {
////          if (r.getNumAttractors() > 5 || r.getNumAttractors() < 3){
//            if (r.getNumAttractors() == reng) {
////                fw = new FileToWrite("Atractores22Sep11/"
////                        + "NumAts_" + reng + "_ChNode_" + ChangedNode + "_Reng_" + renglon + ".txt");
//                int mm = r.getNumAttractors();
//               System.out.println("\nnumero de atractores: " +mm );
////                fw.writeLine("\nnumero de atractores: " + mm);
////                fw.writeLine("Att\t\tPeriodo\t\tCuenca\t\t");
//                for (int b = 0; b < r.att.size(); ++b) {
//
//                    atr = r.att.get(b);
//                    int cuenca = r.att.get(b).basin;
//
//                    prodPeriod *= atr.getNumStates();
//
//
////                    fw.writeLine(b + "\t\t" + atr.getNumStates() + "\t\t" + cuenca);
//
//                    System.out.println(ChangedNode + "'\t\t" +renglon+ "\t\t" +b + "\t\t" + atr.getNumStates() + "\t\t" + cuenca);
//                }
////                fw.close();
////System.out.println("Producto de los periodos: " + prodPeriod );
////                if (prodPeriod == 2048) {
//////                      igual.writeLine("nodo cambiado "+" renglon ");
//////                      System.out.println("producto de los periodos " + prodPeriod + " nodo cambiado "
//////                              +ChangedNode + " renglon " + renglon);
////                } else if (prodPeriod % 4 != 0) {
//////
//////                CasiIgual.writeLine("numero de atractores: " +m + "\t" +
//////                        "Nodo cambiado: " +ChangedNode + "\t" +
//////                        "renglon cambiado: " + renglon + "\t");
//////                CasiIgual.close();
////
////                    System.out.println("Atractores " + mm + "\t"
////                            + "Nodo cambiado: " + ChangedNode + "\t"
////                            + "renglon cambiado: " + renglon + "\t" + "periodo " + atr.getNumStates());
////                } else;
//
////                   fw.close();
////               }//fin for b
//            }
//
//
//
//        }

        return longTrans;
    }

    /**
     * Encuentra el tiempo que tarda en aparecer el primer
     * calcio rojo en la red solamente sampleando los estados
     * dinamicos.
     * @param r La red
     * @param iC número de condiciones iniciales
     */
    public static int findCalcioRojoUnder2(Red r, int iC) { //nombre y parametros
        int N = r.getSize();                //N    --da el tamaño de la red
        int tMax = 30;                      //tMax --Numero maximo de corridas
        int[][] trj;                        //trj  --matriz donde se
        int t;
        double PromDouble = 0;
        int Prom = 0;
        String File;
        File = "TimeToReachCa" + ".dat";
        FileToWrite fw;
        fw = new FileToWrite(File);
        int PromCaRojo = 0;
        int cuenta = 0;
        for (int ic = 0; ic < iC; ++ic) {     //iC asignada = 10,000   Para visualizar la cuenta
            ++cuenta;                       //de lo que se está haciendo
            if (cuenta > 1000) {
                System.out.println("Voy en el estado " + ic);
                cuenta = 0;
            }
            //Aca viene lo bueno
            r.setRandomStates();            //Elige una condición aleatoria
            trj = new int[tMax][N];         //la Matriz trj se le asigna tamaño 50*RedSize
            for (t = 0; t < tMax; ++t) {      //de 0 a 50 evoluciona la red, guardando cada paso
                r.evolveKauffman();         //de evolución en trj. Tenemos un arreglo de N*50
                trj[t] = r.getStates();
                if (trj[t][N - 1] == 2) {        //si el nodo correspondiente a Ca es igual a 2
                    PromCaRojo += t;            //PromCaRojo suma el numero de t
                    break;
                }
            }
        }
        PromDouble = (PromCaRojo / 10000.0);
        fw.writeLine(PromDouble + "");
        System.out.println("El promedio de tiempo pa llegar al Calcio rojo\n"
                + "con todos los nodos presentes es: " + (PromDouble) + "\n"
                + " y la suma de los calcios rojos iniciales es: " + PromCaRojo);
        fw.close();
        return Prom;
    }

    /**
     * Encuentra el tiempo que tarda en aparecer el primer
     * calcio rojo, lo guarda en un arreglote y hace un histograma
     * de frecuencias para ver en que tiempo es mas probable
     * llegar al primer calcio rojo
     * @param r La red a la que le va a encontrar los atractores
     */
    public static int findCalcioRojoUnderHistogram(Red r, int iC) { //nombre y parametros
        int N = r.getSize();                //N    --da el tamaño de la red
        int tMax = 30;                      //tMax --Numero maximo de corridas
        int[][] trj;                        //trj  --matriz donde se
        int t;
        double PromDouble = 0;
        int Prom = 0;
//        String File;
//        FileToWrite fw;
//        File = "TimeToReachCaHistNoHVA" + ".dat";
//        fw = new FileToWrite(File);
//        FileToWrite f = new FileToWrite("TimeToCaRedNoHVA.dat");

//        r.getNodo(14).setPresente(false);



//        fw.writeLine("t\t\t" + "p(t)");
        int[] p = new int[1000];
        for (int k = 0; k < 1000; k++) {
            p[k] = 0;
        }

//        for (int l = 0; l < 10; ++l) {

        int PromCaRojo = 0;
        int cuenta = 0;
        for (int ic = 0; ic < iC; ++ic) {     //iC asignada = 10,000   Para visualizar la cuenta
            ++cuenta;                       //de lo que se está haciendo
            if (cuenta > 1000) {
//                System.out.println("Voy en el estado " + ic);
                cuenta = 0;
            }

            //Aca viene lo bueno
            r.setRandomStates();            //Elige una condición aleatoria
            trj = new int[tMax][N];         //la Matriz trj se le asigna tamaño 50*RedSize
            for (t = 0; t < tMax; ++t) {      //de 0 a 50 evoluciona la red, guardando cada paso
                r.evolveKauffman();         //de evolución en trj. Tenemos un arreglo de N*50
                trj[t] = r.getStates();
                if (trj[t][N - 1] == 2) {        //si el nodo correspondiente a Ca es igual a 2
                    PromCaRojo += t;            //PromCaRojo suma el numero de t
                    p[t]++;
//                        f.writeLine(ic + "\t\t" + t);
                    break;
                }
//                    fw.writeLine(t + "\t\t" + p[t]);
            }
        }
        PromDouble = (PromCaRojo / 100000.0);

//        fw.writeLine(PromDouble +"");
        for (int h = 0; h < tMax; ++h) {
//            fw.writeLine(h + "\t\t" + p[h]);
        }
        System.out.println("El promedio de tiempo pa llegar al Calcio rojo\n" + "con todos los nodos presentes es: " + (PromDouble) + "\n"
                + " y la suma de los calcios rojos iniciales es: " + PromCaRojo);
//        }
//        fw.close();
//        f.close();
        return Prom;
    }

    /**
     * Da el tiempo que tarda en aparecer por primera vez
     * los calcios rojos de la red, solamente sampleando los estados
     * dinamicos quitándole uno o más nodos
     * @param r La red a la que le va a encontrar los atractores
     * @iC el numero de condiciones iniciales que muestrea
     */
    public static int findCalcioRojoUnderSinUnNodo(Red r, int iC) { //nombre y parametros
        String File;
        FileToWrite fw;
        File = "TimeToReachCaRojoSin.dat";
        fw = new FileToWrite(File);
        System.out.println("Nodo\t\t" + "time to Reach Attractor");
        int Prom = 0;
        for (int k = 0; k < r.getSize(); k++) {
            for (int n = 0; n < r.getSize(); ++n) {
                r.getNodo(n).setPresente(true);
            }
            r.getNodo(k).setPresente(false);
            int N = r.getSize();                //N    --da el tamaño de la red
            int tMax = 30;                      //tMax --Numero maximo de corridas
            int[][] trj;                        //trj  --matriz donde se
            int t;
            double PromDouble = 0;
            int PromCaRojo = 0;
            int cuenta = 0;
            for (int ic = 0; ic < iC; ++ic) {     //iC asignada = 10,000   Para visualizar la cuenta
                ++cuenta;                       //de lo que se está haciendo
                if (cuenta > 1000) {
                    System.out.println("Voy en el estado " + ic);
                    cuenta = 0;
                }
                //Aca viene lo bueno
                r.setRandomStates();            //Elige una condición aleatoria
                trj = new int[tMax][N];         //la Matriz trj se le asigna tamaño 50*RedSize
                for (t = 0; t < tMax; ++t) {      //de 0 a 50 evoluciona la red, guardando cada paso
                    r.evolveKauffman();         //de evolución en trj. Tenemos un arreglo de N*50
                    trj[t] = r.getStates();
                    if (trj[t][N - 1] == 2) {        //si el nodo correspondiente a Ca es igual a 2
                        PromCaRojo += t;        //se guarda en PromCaRojo
                        break;
                    }
                }
            }
            PromDouble = (PromCaRojo / 10000.0);
            System.out.println("El promedio de tiempo pa llegar al Calcio rojo\n"
                    + "con todos los nodos presentes SIN "
                    + r.getNodo(k).getName() + " es: " + (PromDouble) + "\n"
                    + " y la suma de los calcios rojos iniciales es: " + PromCaRojo);
            fw.writeLine(k + "\t\t" + PromDouble);
        }
        fw.close();
        return Prom;
    }

    /**
     * Encuentra los atractores de la red solamente sampleando los estados
     * dinamicos. Por eso se llama "Under" porque "Undersamplea".
     * @param r La red a la que le va a encontrar los atractores
     */
    public static int findCalcioRojoUnderSinHVAHistogram(Red r, int iC) { //nombre y parametros
        r.getNodo(14).setPresente(false);
        int N = r.getSize();                //N    --da el tamaño de la red
        int tMax = 30;                      //tMax --Numero maximo de corridas
        int[][] trj;                        //trj  --matriz donde se
        int t;
        double PromDouble = 0;
        int Prom = 0;
        String File;
        FileToWrite fw;
        File = "TimeToReachCaHistHVA" + ".dat";
        fw = new FileToWrite(File);




        fw.writeLine("t\t\t" + "p(t)");
        int[] p = new int[1000];
        for (int k = 0; k < 1000; k++) {
            p[k] = 0;
        }

        for (int l = 0; l < 10; ++l) {

            int PromCaRojo = 0;
            int cuenta = 0;
            for (int ic = 0; ic < iC; ++ic) {     //iC asignada = 10,000   Para visualizar la cuenta
                ++cuenta;                       //de lo que se está haciendo
                if (cuenta > 1000) {
                    System.out.println("Voy en el estado " + ic);
                    cuenta = 0;
                }

                //Aca viene lo bueno
                r.setRandomStates();            //Elige una condición aleatoria
                trj = new int[tMax][N];         //la Matriz trj se le asigna tamaño 50*RedSize
                for (t = 0; t < tMax; ++t) {      //de 0 a 50 evoluciona la red, guardando cada paso
                    r.evolveKauffman();         //de evolución en trj. Tenemos un arreglo de N*50
                    trj[t] = r.getStates();
                    if (trj[t][N - 1] == 2) {        //si el nodo correspondiente a Ca es igual a 2
                        PromCaRojo += t;            //PromCaRojo suma el numero de t
                        p[t]++;
                        fw.writeLine(t + "\t\t" + p[t]);
                        break;
                    }
                }
            }
            PromDouble = (PromCaRojo / 10000.0);

//        fw.writeLine(PromDouble +"");

            System.out.println("El promedio de tiempo pa llegar al Calcio rojo\n"
                    + "con todos los nodos presentes es: " + (PromDouble) + "\n"
                    + " y la suma de los calcios rojos iniciales es: " + PromCaRojo);
        }
        fw.close();
        return Prom;
    }

    /**
     * Da el tiempo que tarda en aparecer por primera vez
     * los calcios rojos de la red, solamente sampleando los estados
     * dinamicos. Por eso se llama "Under" porque "Undersamplea".
     * @param r La red a la que le va a encontrar los atractores
     * @iC el numero de condiciones iniciales que muestrea
     */
    public static int findCalcioRojoUnderSin2o3(Red r, int iC) { //nombre y parametros

//        String File;
//        FileToWrite fw;
//        File = "TimeToReachCaRojoSin2o3.dat";
//        fw = new FileToWrite(File);
        System.out.println("Nodo\t\t" + "time to Reach Attractor");
        int Prom = 0;
//        for (int k = 0; k < r.getSize(); k++) {
        for (int n = 0; n < r.getSize(); ++n) {
            r.getNodo(n).setPresente(true);
        }
        r.getNodo(8).setPresente(false);
        r.getNodo(16).setPresente(false);
//            r.getNodo(20).setPresente(false);
        int N = r.getSize();                //N    --da el tamaño de la red
        int tMax = 30;                      //tMax --Numero maximo de corridas
        int[][] trj;                        //trj  --matriz donde se
        int t;
        double PromDouble = 0;
        int PromCaRojo = 0;
        int cuenta = 0;

        for (int ic = 0; ic < iC; ++ic) {     //iC asignada = 10,000   Para visualizar la cuenta
            ++cuenta;                       //de lo que se está haciendo
            if (cuenta > 1000) {
                System.out.println("Voy en el estado " + ic);
                cuenta = 0;
            }
            //Aca viene lo bueno
            r.setRandomStates();            //Elige una condición aleatoria
            trj = new int[tMax][N];         //la Matriz trj se le asigna tamaño 50*RedSize
            for (t = 0; t < tMax; ++t) {      //de 0 a 50 evoluciona la red, guardando cada paso
                r.evolveKauffman();         //de evolución en trj. Tenemos un arreglo de N*50
                trj[t] = r.getStates();
                if (trj[t][N - 1] == 2) {        //si el nodo correspondiente a Ca es igual a 2
                    PromCaRojo += t;        //se guarda en PromCaRojo
                    break;
                }
            }
        }
        PromDouble = (PromCaRojo / 10000.0);
        System.out.println("El promedio de tiempo pa llegar al Calcio rojo\n"
                + "con todos los nodos presentes SIN "
                + r.getNodo(8).getName()
                + r.getNodo(16).getName()
                + //                    r.getNodo(20).getName() +
                " es: " + (PromDouble) + "\n"
                + " y la suma de los calcios rojos iniciales es: " + PromCaRojo);
//            fw.writeLine(k + "\t\t" + PromDouble);
//        }
//        fw.close();
        return Prom;
    }

    /**
     * Encuentra el tiempo que tarda en llegar al atractor con todos los nodos
     * presentes undersampleando.
     * @param r La red a la que le va a encontrar los atractores
     * @param iC el número de condiciones iniciales que muestrea
     */
    public static int findTimeToReachAttUnder(Red r, int iC) {
        int N = r.getSize();                                        //tamaño de la red
        int tMax = 80;                                             // numero maximo de corridas
        int[][] trj;                                                //matriz que guarda la evolucion
        int t;
        int sumota = 0;
        int longTrans = 0;
        int cuenta = 0;

        for (int ic = 0; ic < iC; ++ic) {                             //lo de siempre, de 0 a 10000
            stop:
            {      //Esta te saca del break rotulado
                ++cuenta;                                                 //imprime cada 1000 que ahi va
                if (cuenta > 1000) {                                      // y regresa "cuenta" a 0
                    System.out.println("Voy en la iteración " + ic);
                    cuenta = 0;
                }
                r.setRandomStates();                                    //elige una red aleatoria
                trj = new int[tMax][N];
                for (t = 0; t < tMax; ++t) {
                    r.evolveKauffman();
                    trj[t] = r.getStates();
                }
                t = 0;
                for (t = 0; t < tMax - 1; ++t) {
//                    System.out.println("Esta es la evolucion " + t);
                    for (int i = t + 1; i < tMax; i++) {
                        if (arrayEquals(trj[t], trj[i])) {
                            System.out.println(i);
                            
                            sumota += i;
                            break stop;
                        }
                    }
                }
            }
        }
        System.out.println("El promedio de tiempo en llegar al atractor\n"
                + "con todos los nodos presentes es: " + sumota / 10000.0 + " iteraciones.");
        return longTrans;
    }

    /**
     * Encuentra la iteración en la que se llega al atractor
     * SWICHANDO LA FUNCION
     * @param r La red a la que le va a encontrar los atractores
     * @param iC el número de condiciones iniciales que muestrea.
     */
    public static int findTimeToReachAttUnderSinUnNodo(Red r, int iC) {
        String File;
        FileToWrite fw;
        File = "TimeToReachAttSwitch.dat";
        fw = new FileToWrite(File);
        int longTrans = 0;
        System.out.println("Nodo\t\t" + "time to Reach Attractor");

//        for (int k = 0; k < r.getSize(); k++) {
        for (int n = 0; n < r.getSize(); ++n) {
            r.getNodo(n).setPresente(true);
        }
//            r.getNodo(k).setPresente(false);
//            r.getNodo(k).setPresente(false);
        int N = r.getSize();                                        //tamaño de la red
        int tMax = 200;                                             // numero maximo de corridas
        int[][] trj;                                                //matriz que guarda la evolucion
        int t, sumota;
        sumota = 0;
        for (int ic = 0; ic < iC; ++ic) {                             //lo de siempre, de 0 a 10000
            stop:
            {      //Este ROTULO te saca del break rotulado
                r.setRandomStates();                                    //elige una red aleatoria
                trj = new int[tMax][N];
                for (t = 0; t < tMax; ++t) {
//                        r.evolveKauffman();
                    r.evolveKauffmanSwitch(2, 8);
                    trj[t] = r.getStates();
                }
                t = 0;
                for (t = 0; t < tMax - 1; ++t) {
                    for (int i = t + 1; i < tMax; i++) {
                        if (arrayEquals(trj[t], trj[i])) {
                            sumota += i;
                            break stop;
                        }
                    }
                }
            }
        }
        System.out.println(
                r.getNodo(8).getName() + "\t\t" + sumota / 10000.0);
//            fw.writeLine(k + "\t\t" + sumota / 10000.0);
        fw.writeLine("\t\t" + sumota / 10000.0);
//        }
        fw.close();
        return longTrans;
    }

    /**
     * Guarda en un archivo TODOS los arreglos que te
     * llevan a un atractor de periodo 8, recorriendo todos los
     * estados dinamicos.
     * @param r La red a la que le va a encontrar los atractores
     * @return longTrans, nomás pa ver si hay transitorios largos
     */
    public static int findFirstArrayToPeriod8(Red r) {
        String file4;
        FileToWrite fw4;
        file4 = "TransientToPeriod4All" + ".dat";
        fw4 = new FileToWrite(file4);
        String file8;
        FileToWrite fw8;
        file8 = "TransientToPeriod8All" + ".dat";
        fw8 = new FileToWrite(file8);
        int nb;
        int nt;
        int N = r.getSize();                    //N = tamaño de la red
        int Nb = r.getSize() - 1;                //Nb = tamaño de la red -1
        int Cb = (int) (Math.pow(2, Nb) + 0.1);   //Cb = 2 a la tamaño de red
        int[] stb = new int[Nb];                //arreglo de tamaño de la red -1
        int[] s = new int[N];                   //arreglo tamaño de la red
        int tMax = 60;                         // tiempo maximo de evolucion
        int[][] trj;                            //matriz trj

        int cuenta = 0;
        int l, k, m, n, t;
        int longTrans = 0;
        boolean busca, ya;
        Attractor a;                            //objeto tipo atractor
        for (nt = 0; nt < 3; ++nt) {              //FORSOTE
            for (nb = 0; nb < Cb; ++nb) {         //de 0 a Cb (2 a la red.size)

                ++cuenta;
                if (cuenta == 100001) {
                    System.out.println("Condición inicial: " + nb);
                    cuenta = 0;
                }
                intToBinary(nb, stb);            //convierte a binario TODAS las
                m = 0;                          //posibilidades, guardando cada
                //una en stb. O sea que va a llenar
                //2^21 arreglos con el numero binario correspondiente
                //a su entero para hacer la dinámica con ese arreglo
                for (n = 0; n < r.getSize(); ++n) {
                    if (r.getNodo(n).getType() == 2) {
                        s[n] = stb[m];
                        ++m;
                    } else {
                        s[n] = nt;
                    }
                }
                r.setStates(s);
                trj = new int[tMax][N];
                for (t = 0; t < tMax; ++t) {
                    r.evolveKauffman();
                    trj[t] = r.getStates();
                }
                t = tMax - 2;
                busca = true;
                l = 0;
                while ((busca) && (t > 0)) {
                    if (arrayEquals(trj[t], trj[tMax - 1])) {
                        busca = false;
                        l = (tMax - 1) - t;
                    } else {
                        --t;
                    }
                }
                if (l > 4) {
                    for (int arrNumb = 0; arrNumb < r.getSize(); ++arrNumb) {
                        fw8.writeLine(trj[0][arrNumb] + " ");
                    }
                } else {
                    for (int arrNumb = 0; arrNumb < r.getSize(); ++arrNumb) {
                        fw4.writeLine(trj[0][arrNumb] + " ");
                    }
                }

//                if (l > 0) {
//                    a = new Attractor(l, N);
//                    for (k = 0; k < l; ++k) {
//                        t = tMax - 1 - l + k;
//                        a.addState(trj[t], k);
//                    }
//                    if (r.getNumAttractors() == 0) {
//                        r.addAttractor(a);
//                    } else {
//                        ya = false;
//                        for (k = 0; k < r.getNumAttractors(); ++k) {
//                            if (r.getAttractor(k).equals(a)) {
//                                r.getAttractor(k).ppBasin();
//                                ya = true;
//                                break;
//                            }
//                        }
//                        if (!ya) {
//                            r.addAttractor(a);
//                        }
//                    }
//                } else {
//                    ++longTrans;
//                }
            }
        }
        fw4.close();
        fw8.close();
        return longTrans;
    }

    /**
     * Guarda en un archivo TODOS los arreglos que te
     * llevan a un atractor de periodo 8, recorriendo todos los
     * estados dinamicos.
     * @param r La red a la que le va a encontrar los atractores
     * @return longTrans, nomás pa ver si hay transitorios largos
     */
    public static int findFirstArrayToPeriod4(Red r, int iC) {
        String file;
        FileToWrite fw;
        file = "ArrayToReachPeriod4" + ".dat";
        fw = new FileToWrite(file);
        String numS;
        StringTokenizer stk;
        int numI;
        int N = r.getSize();                    //N = tamaño de la red
        int Nb = r.getSize() - 1;                //Nb = tamaño de la red -1
        int Cb = (int) (Math.pow(2, Nb) + 0.1);   //Cb = 2 a la tamaño de red
        int tMax = 60;                         // tiempo maximo de evolucion
        int[][] trj;                            //matriz trj
        int sumota = 0;
        int[] TotalSumaNodo = new int[N];
        int[] TotalCeros = new int[N];
        int[] TotalUnos = new int[N];
        int[] TotalDos = new int[N];
        int t;
        int longTrans = 0;

        for (int ic = 0; ic < iC; ic++) {
            stoppy:
            {
                r.setRandomStates();

                trj = new int[tMax][N];
                for (t = 0; t < tMax; ++t) {
                    r.evolveKauffman();
                    trj[t] = r.getStates();
                }
                t = 0;

                for (t = 0; t < tMax - 1; ++t) {
                    for (int i = t + 1; i < tMax; i++) {
                        if (arrayEquals(trj[t], trj[i])) {
                            sumota += i;
                            if (i < (t + 6) && i > 0) {
                                for (int arrNumb = 0; arrNumb < r.getSize(); ++arrNumb) {
                                    stk = new StringTokenizer(trj[0][arrNumb] + " ");
                                    numS = stk.nextToken();
                                    numI = Integer.parseInt(numS);
                                    if (numI == 0) {
                                        TotalCeros[arrNumb]++;
                                    } else if (numI == 1) {
                                        TotalUnos[arrNumb]++;
                                    } else {
                                        TotalDos[arrNumb]++;
                                    }
                                    TotalSumaNodo[arrNumb] += numI;
                                    fw.writeString(numS + "   ");
                                }
                                fw.writeLine();
                                break stoppy;
                            }
                            sumota += i;
                            break stoppy;
                        }
                    }
                }
            }

        }
        fw.close();
        for (int ki = 0; ki < r.getSize(); ++ki) {
            System.out.print(TotalCeros[ki] + "  ");
        }
        System.out.println();

        for (int ki = 0; ki < r.getSize(); ++ki) {
            System.out.print(TotalUnos[ki] + "  ");
        }
        System.out.println();

        for (int ki = 0; ki < r.getSize(); ++ki) {
            System.out.print(TotalDos[ki] + "  ");
        }
        System.out.println();
        return longTrans;
    }

    /**
     * Encuentra Todas las condiciones iniciales
     * que te llevan a un atractor de periodo 8 recorriendo todos los
     * estados dinamicos.
     * @param r La red a la que le va a encontrar los atractores
     */
    public static int findAllArraysToPeriod8(Red r) {
        String file;
        FileToWrite fw;
        file = "AllArraysToReachPeriod8" + ".dat";
        fw = new FileToWrite(file);
        int nb;
        int nt;
        int N = r.getSize();                    //N = tamaño de la red
        int Nb = r.getSize() - 1;                //Nb = tamaño de la red -1
        int Cb = (int) (Math.pow(2, Nb) + 0.1);   //Cb = 2 a la tamaño de red
        int[] stb = new int[Nb];                //arreglo de tamaño de la red -1
        int[] s = new int[N];                   //arreglo tamaño de la red
        int tMax = 30;                         // tiempo maximo de evolucion
        int[][] trj;                            //matriz trj

        StringTokenizer stk;
        String numS;
        int numI;
        int[] TotalSumaNodo = new int[N];
        int[] TotalCeros = new int[N];
        int[] TotalUnos = new int[N];
        int[] TotalDos = new int[N];

        int cuenta = 0;
        int l, k, m, n, t;
        int longTrans = 0;
        boolean busca, ya;
        Attractor a;                            //objeto tipo atractor
        for (nt = 0; nt < 3; ++nt) {              //FORSOTE
            for (nb = 0; nb < Cb; ++nb) {         //de 0 a Cb (2 a la red.size)
                intToBinary(nb, stb);            //convierte a binario TODAS las
                m = 0;                          //posibilidades, guardando cada
                //una en stb. O sea que va a llenar
                //2^21 arreglos con el numero binario correspondiente
                //a su entero para hacer la dinámica con ese arreglo
                cuenta++;
                for (n = 0; n < r.getSize(); ++n) {
                    if (r.getNodo(n).isBinary()) {
                        s[n] = stb[m];
                        ++m;
                    } else {
                        s[n] = nt;
                    }
                }
                r.setStates(s);
                trj = new int[tMax][N];
                for (t = 0; t < tMax; ++t) {
                    r.evolveKauffman();
                    trj[t] = r.getStates();
                }

                ++cuenta;
                if (cuenta > 100000) {
                    System.out.println("Voy en la Condicion inicial " + Math.pow(nb, nt) + " o " + nb);
                    cuenta = 0;
                }

                for (t = 0; t < tMax - 1; ++t) {
                    for (int i = t + 1; i < tMax; i++) {
                        if (arrayEquals(trj[t], trj[i])) {
//                            sumota += i;
                            if (i > (t + 6)) {
                                for (int arrNumb = 0; arrNumb < r.getSize(); ++arrNumb) {
                                    stk = new StringTokenizer(trj[0][arrNumb] + " ");
                                    numS = stk.nextToken();
                                    numI = Integer.parseInt(numS);
                                    if (numI == 0) {
                                        TotalCeros[arrNumb]++;
                                    } else if (numI == 1) {
                                        TotalUnos[arrNumb]++;
                                    } else {
                                        TotalDos[arrNumb]++;
                                    }
                                    TotalSumaNodo[arrNumb] += numI;
                                    fw.writeString(numS + "   ");
                                }
                                fw.writeLine();

                            }

                        }
                    }
                }



                t = tMax - 2;
                busca = true;
                l = 0;
                while ((busca) && (t > 0)) {
                    if (arrayEquals(trj[t], trj[tMax - 1])) {
                        busca = false;
                        l = (tMax - 1) - t;
                    } else {
                        --t;
                    }
                }
                if (l > 0) {
                    a = new Attractor(l, N);

                    for (k = 0; k < l; ++k) {
                        t = tMax - 1 - l + k;
                        a.addState(trj[t], k);
                    }
                    if (r.getNumAttractors() == 0) {
                        r.addAttractor(a);
                    } else {
                        ya = false;
                        for (k = 0; k < r.getNumAttractors(); ++k) {
                            if (r.getAttractor(k).equals(a)) {
                                r.getAttractor(k).ppBasin();
                                ya = true;
                                break;
                            }
                        }
                        if (!ya) {
                            r.addAttractor(a);
                        }
                    }
                } else {
                    ++longTrans;
                }
            }                                       //FIN DEL FOR GIGANTE DE 2^21
        }
        fw.close();
        return longTrans;
    }

    /**
     * Encuentra la iteración en la que se llega al atractor
     * undersampleando, sin los nodos NFA sensibles
     * @param r La red a la que le va a encontrar los atractores
     * @param iC el número de condiciones iniciales que muestrea.
     */
    public static int findTimeToReachAttUnderSin2o3(Red r, int iC) {
        String File;
        FileToWrite fw;
        File = "TimeToReachAttSin2o3.dat";
        fw = new FileToWrite(File);
        int longTrans = 0;
        System.out.println("Nodo\t\t" + "time to Reach Attractor");

//        for (int k = 0; k < r.getSize(); k++) {
        for (int n = 0; n < r.getSize(); ++n) {
            r.getNodo(n).setPresente(true);
        }
//            r.getNodo(8).setPresente(false);
        r.getNodo(16).setPresente(false);
        r.getNodo(20).setPresente(false);
        int N = r.getSize();                                        //tamaño de la red
        int tMax = 80;                                             // numero maximo de corridas
        int[][] trj;                                                //matriz que guarda la evolucion
        int t, sumota;
        sumota = 0;
        for (int ic = 0; ic < iC; ++ic) {                             //lo de siempre, de 0 a 10000
            stop:
            {      //Este ROTULO te saca del break rotulado
                r.setRandomStates();                                    //elige una red aleatoria
                trj = new int[tMax][N];
                for (t = 0; t < tMax; ++t) {
                    r.evolveKauffman();
                    trj[t] = r.getStates();
                }
                t = 0;
                for (t = 0; t < tMax - 1; ++t) {
                    for (int i = t + 1; i < tMax; i++) {
                        if (arrayEquals(trj[t], trj[i])) {
                            sumota += i;
                            break stop;
                        }
                    }
                }
            }
        }
        System.out.println(
                r.getNodo(8).getName() + r.getNodo(20).getName() + r.getNodo(16).getName() + "\t\t" + sumota / 10000.0);
        fw.writeLine("8,16,20" + "\t\t" + sumota / 10000.0);
//        }
        fw.close();
        return longTrans;
    }

    /**
     * Genera un patrón temporal de la evolución del calcio
     * mediante un promedio de muchas configuraciones iniciales,
     * y corriendo 10 pasos de tiempo la ventana para suavizar
     * la señal. Con todos los nodos presentes, quitando 1 nodo y
     * quitando 2 nodos.
     * @param r1 La red que se va a evolucionar
     * @param iC las condiciones iniciales a promediar
     * @return P el arreglo de la evolucion
     */
    public static double[][] calciumEvolution(Red r1, int iC, int speract) {
        double[][] Q = new double[200][r1.getSize() + 1];
        String fileca;
        FileToWrite fwc;
//        fileca = "catsper/Enero13/SperactTemporal/curvas/SperactTiempoDependiente_" + speract+".dat";
//        fileca = "catsper/Gordon/TCatsperAD/curvas/wtPRUEBAAGOSTO2013.dat";
//        fileca = "catsper/MSB/FullChannels/curves/NFA.dat";//Sus tablas son sacadas de /catsper/tablasCatsperItalia.zip NO MODIFIQUES ESA
        fileca = "PLoSNFA/NFA11DicPrueba.dat";
        fwc = new FileToWrite(fileca);
        int T = 1200;
        int[][] cals = new int[T][iC];
        int[][] trjs = new int[iC][T];
        double[] P = new double[T];
        double[] S = new double[T];
        for (int m = 0; m < r1.getSize(); ++m) {
            r1.getNodo(m).setPresente(true);
//            if(Math.random()<0.9){
//            r1.getNodo(m).setS(0);
//            }
        }
        r1.getNodo(8).setPresente(false);
        r1.getNodo(16).setPresente(false);
//        r1.getNodo(16).setPresente(false);
        // <editor-fold defaultstate="collapsed" desc="Te oculte tu codigo, puto">
        for (int r = 0; r < iC; ++r) {
            r1.setRandomStates();
            for (int t = 0; t < T; ++t) {
//                trjs[r][t] += r1.getNodo(r1.getSize()-1).getS();
//                r1.evolveKauffman();
//                cals[t][r] = r1.getNodo(r1.getSize()-1).getS();
//                r1.getNodo(10).setPresente(false);
//        r1.getNodo(14).setPresente(false);
               
//                if(Math.random() > (Math.exp(speract/(t+1)))){
//                r1.getNodo(0).setS(0);
//                }
//                else{r1.getNodo(0).setS(1);}
                
//                if(t%4 != 0){
//                r1.getNodo(17).setS(1);
//                r1.getNodo(20).setS(1);
//                }
//                r1.evolveKauffman();
                trjs[r][t] += r1.getNodo(r1.getSize()-1).getS();
                r1.evolveKauffman(); r1.getNodo(20).setS(1);
                cals[t][r] = r1.getNodo(r1.getSize()-1).getS();
            }
        }
        // </editor-fold>
        for (int t = 0; t < T; ++t) {
            for (int r = 0; r < iC; ++r) {
                P[t] += trjs[r][t];
            }
            P[t] /= (1.0 * iC);
        }
        for (int t = 0; t < T; ++t) {
            fwc.writeLine(t + "\t\t" + P[t]);
//            System.out.println(t + "\t\t" + Math.exp(-((t+1))));
        }
        fwc.close();
        for (int i = 800; i < 1000; ++i) {
            Q[i - 800][0] = P[i];
        }
        System.out.println( "acabe de escribir archivo del nfa " + r1.getNodo(17).getName());
        /*
         * Hasta aqui fue solo para el calcio en wt.
         * En adelante se quitan los nodos
         */

//        for (int delete = 0; delete < r1.getSize(); delete++) {
//            for (int a = 0; a < iC; ++a) {
//                for (int b = 0; b < T; ++b) {
//                    trjs[a][b] = 0;
//                }
//            }
//            String file;
//            FileToWrite fw;
////            file = "catsper/Enero13/Curvas/CaKCCambiado_Sin" + r1.getNodo(delete).getName() + ".dat";
////            file = "catsper/Gordon/TCatsperAD/curvas/sin"+ r1.getNodo(delete).getName()+".dat";
////            file = "catsper/MSB/FullChannels/curvesNoHVA/sin" + r1.getNodo(delete).getName()+".dat";
//            file = "PLoSNFA/sin" + r1.getNodo(delete).getName() + ".dat";
//            fw = new FileToWrite(file);
//
//            for (int m = 0; m < r1.getSize(); ++m) {
//                r1.getNodo(m).setPresente(true);
//            }
//            System.out.println("voy en el nodo " + r1.getNodo(delete).getName());
//
//            for (int r = 0; r < iC; ++r) {
//                r1.getNodo(delete).setPresente(false);
////                r1.getNodo(14).setPresente(false);
//                r1.setRandomStates();
//                for (int t = 0; t < T; ++t) {
//                    trjs[r][t] += r1.getNodo(r1.getSize()-1).getS();
//                    r1.evolveKauffman();
//                    cals[t][r] = r1.getNodo(r1.getSize()-1).getS();
//                }
//            }
//            for (int t = 0; t < T; ++t) {
//                for (int r = 0; r < iC; ++r) {
//                    P[t] += trjs[r][t];
//                }
//                P[t] /= (1.0 * iC);
////            for (int r = 0; r < iC; ++r) {
////                S[t] += ((trjs[r][t] - P[t]) * (trjs[r][t] - P[t]));
////            }
////            S[t] /= (1.0 * iC);
////            S[t] = Math.sqrt(S[t]);
//            }
//
//
//            DecimalFormat df = new DecimalFormat("0.000000000000000");
//            for (int t = 0; t < T; ++t) {
//                fw.writeLine(t + "\t\t" + df.format(P[t]));
//            }
//            fw.close();
//            System.out.println("acabe de escribir el archivo del nodo deleteado" + r1.getNodo(delete).getName() + " y catsper es " + r1.getNodo(17).nombre);
//            for (int i = 800; i < 1000; ++i) {
//                Q[i - 800][delete + 1] = P[i];
//            }
//        }


        System.out.println("termina el metodo calciumEvolution     " + speract);
        return Q;
    }

    /**
     * Genera un patrón temporal de la evolución del calcio
     * mediante un promedio de muchas configuraciones iniciales,
     * y corriendo 10 pasos de tiempo la ventana para suavizar
     * la señal. Con todos los nodos presentes, quitando 1 nodo y
     * quitando 2 nodos.
     * @param r1 La red que se va a evolucionar
     * @param iC las condiciones iniciales a promediar
     */
    public static int todosEvolution(Red r1, int iC) {

        String file;
        FileToWrite fw;

        //        int R = 10000;
        int T = 300;
//        for (int cur = 21; cur < r1.getSize(); ++cur) {//
//            System.out.println("" + r1.getNodo(cur).getName());
//        for (int n = 0; n < r1.getSize(); ++n) {
//            for (int m = 0; m < r1.getSize(); ++m) {
//                r1.getNodo(m).setPresente(true);
//            }
//        r1.getNodo(cur).setPresente(false);
//        r1.getNodo(16).setPresente(false);
//        r1.getNodo(20).setPresente(false);

        int[][] trjs = new int[iC][T];
        double[] P = new double[T];
        double[] S = new double[T];

        int s;

        for (int r = 0; r < iC; ++r) {
            r1.setRandomStates();
            s = (Math.random() < 0.5) ? 1 : 1;
            r1.getNodo(21).setS(s);
//System.out.println(""+ r1.getNodo(21).getName());

            for (int t = 0; t < T; ++t) {
                trjs[r][t] += r1.getNodo(21).getS();
                r1.evolveKauffman();
            }
        }
        for (int t = 0; t < T; ++t) {
            for (int r = 0; r < iC; ++r) {
                P[t] += trjs[r][t];
            }
            P[t] /= (1.0 * iC);
            for (int r = 0; r < iC; ++r) {
                S[t] += ((trjs[r][t] - P[t]) * (trjs[r][t] - P[t]));
            }
            S[t] /= (1.0 * iC);
            S[t] = Math.sqrt(S[t]);
        }
//        double x, y;
//        int V = 10;
//            for (int t = 0; t < (T - V); ++t) {
//                x = 0;
//                y = 0;
//                for (int tv = 0; tv < V; ++tv) {
//                    x += P[t + tv];
//                    y += S[t + tv];
//                }
//                P[t] = x / V;
//                S[t] = y / V;
//            }
//            file = "Curvas/curvaSin_" + r1.getNodo(n).getName() + ".dat";
//            file = "Curvas/" + r1.getNodo(cur).getName() + "_12sep.dat";

        file = "CurvasNoAtenuadas/CurvaCon_"
                + "_" + r1.getNodo(21).getName()
                + //                "_" + r1.getNodo(16).getName() +
                //                "_" + r1.getNodo(20).getName() +
                ".dat";
        fw = new FileToWrite(file);
        for (int t = 0; t < T; ++t) {
            fw.writeLine(t + "\t\t" + P[t] + "\t\t" + S[t]);
        }
        fw.close();
//        }
//        }
        return T;

    }

    /**
     * Evoluciona la dinÃ¡mica de Kauffman con 3 redes.
     * Antes de generar el vector de estados dinÃ¡micos discretos
     * las variables acoplantes (Calcio) se guardarÃ¡n en un vector
     * temporal de tamaÃ±o 3, donde se harÃ¡ una especie de promedio, donde el nodo del centro
     * va a "hacer caso" a sus vecinos con cierta probabilidad, dependiendo de la
     * "distancia" que estÃ¡ un cluster de otro.
     * Purpuratus va a estar mÃ¡s lejos que pictus.
     * Esto serÃ¡ determinado por el epsilon.
     * IMPORTANTE: EPSILON >= 3 GENERA TRANSITORIOS MAYORES A 55 PASOS
     * @param n1 nodo 1 que harÃ¡ la dinÃ¡mica por la izquierda
     * @param n2 nodo 2, Ã©ste es el principal
     * @param n3 nodo 3, el de la derecha.
     * @param epsilon, el double reductor del coupling
     */
    public static void evolveCoupled(Nodo[] n1, Nodo[] n2, Nodo[] n3, double epsilon, double divisor, int posicion,
            double Rounder2, double Rounder, int NodoAcoplador) {
        int N = n1.length;                          //N tiene el tamaÃ±o de la red
        int[] nuevoEstado1 = new int[N];             //vector de tamaÃ±o N donde guarda
        int[] nuevoEstado2 = new int[N];             //para cada nodo
        int[] nuevoEstado3 = new int[N];             //el estado nuevo (o sea t+1)
        int ifr, ifr2, ifr3;
        int i, m, n, nr, nr2, nr3;
        int[] nt;
        int[] nt2;
        int[] nt3;

        //Creamos el vector donde se almacena temporalmente
        //y se juega con los valores del calcio
        int[] tempNode = new int[3];
        int[] tempCa = new int[3];
        int[] tempcAMP = new int[3];
        int[] tempcGMP = new int[3];
        for (n = 0; n < N; ++n) {               //for para cada nodo de la red
            nr = n1[n].getNumReg();            //"nr" guarda el nÃºmero de reguladores del nodo N
            nr2 = n2[n].getNumReg();           // dependiendo de la red que va a evolucionar
            nr3 = n3[n].getNumReg();           //En este caso evolucionan 3 redes y se acoplan
            nt = new int[nr];                  //"nt" vector que guarda el nÃºm d regs. de N
            nt2 = new int[nr2];
            nt3 = new int[nr3];

            //Para cada uno de los 3 nodos
            //obtenemos el estado de sus reguladores
            for (m = 0; m < nr; ++m) {
                i = n1[n].getReg(m);
                nt[m] = n1[i].getS();
            }
            for (m = 0; m < nr2; ++m) {
                i = n2[n].getReg(m);
                nt2[m] = n2[i].getS();
            }
            for (m = 0; m < nr3; ++m) {
                i = n3[n].getReg(m);
                nt3[m] = n3[i].getS();
            }

            //para cada nodo obtenemos la
            //representaciÃ³n entera de su ternario
            ifr = ternaryToInt(nt);
            ifr2 = ternaryToInt(nt2);
            ifr3 = ternaryToInt(nt3);

            //Ahora, asignamos el valor de cada estado dinÃ¡mico
            //al vector nuevoEstado que habÃ­amos creado
            //con el valor de la funciÃ³n leÃ­da de la tabla
            //de acuerdo con el renglÃ³n ternario.
            nuevoEstado1[n] = n1[n].getFuncVal(ifr);
            nuevoEstado2[n] = n2[n].getFuncVal(ifr2);
            nuevoEstado3[n] = n3[n].getFuncVal(ifr3);
        }

        tempNode[0] = nuevoEstado1[NodoAcoplador];
        tempNode[1] = nuevoEstado2[NodoAcoplador];
        tempNode[2] = nuevoEstado3[NodoAcoplador];

        //Asignamos el valor del calcio obtenido de la dinÃ¡mica
        //al vector tempCa para cada nodo del acoplamiento
        tempCa[0] = nuevoEstado1[N - 1];
        tempCa[1] = nuevoEstado2[N - 1];
        tempCa[2] = nuevoEstado3[N - 1];

        //Acoplando cGMP
        tempcGMP[0] = nuevoEstado1[2];
        tempcGMP[1] = nuevoEstado2[2];
        tempcGMP[2] = nuevoEstado3[2];

        //Acoplando cAMP
        tempcAMP[0] = nuevoEstado1[13];
        tempcAMP[1] = nuevoEstado2[13];
        tempcAMP[2] = nuevoEstado3[13];
        /*
         *Creo una variable llamada CalcioPos_m1
         * y otra llamada                  CalcioPos_M1
         * para hacer mi cuenta chingona.... o sea, voy a hacer estas
         * dos variables, valores dependientes de la posicion del flagelo
         * en la que se encuentren y con eso voy a crear una exponencial
         * negativa para ver cómo se comporta así el acoplamiento
         */

//        double CalcioPos_m1 = tempCa[0] * Math.exp(-epsilon * (posicion - 1));
//        double CalcioPos_M1 = tempCa[2] * Math.exp(-epsilon * (posicion + 1));

        /*Creamos  una variable "sumaTemp" con la que vamos a jugar para acoplar
         * el valor del calcio del nodo central, junto con los de los lados.
         * para el caso de los nodos de las orillas,
         * se hace un mÃ©todo evolveCola y evolveCuello para no tener problemas
         * de "frontera"
         */
        double sumaTemp = ((tempNode[0] + tempNode[2]) / 2);

        double sumaTempCa = ((tempCa[0] + tempCa[2]) / 2);
        double sumaTempcAMP = ((tempcAMP[0] + tempcAMP[2]) / 2);
        double sumaTempcGMP = ((tempcGMP[0] + tempcGMP[2]) / 2);

        //Nos aseguramos de que si el nodo del centro es menor
        //que nuestro promedio de los lados, le haga caso a ese valor
        // con una probabilidad del 75%, de
        //otra suerte, promedia su valor con el de ellos.
        //Si de plano el calcio del centro vale mÃ¡s, se queda con su valor

        /*
         * 11 de enero de 2012
         * primer parche del año
         * Voy a definir tempCa[] sin usar el Math.round, sino que en un primer caso voy
         * a decir que sea .3 y .6 las cotas superior e inferior
         */
//        double Redondeador;

        double noRoundTemp = (sumaTemp * epsilon) + (tempNode[1] * (1 - epsilon));

//        if (n2[NodoAcoplador].isBinary() == false) {
//            System.out.println("Este es ternario");
//            if (noRoundTemp <= Rounder2) {
//                tempCa[1] = 0;
//            } else if (noRoundTemp > Rounder2 && noRoundTemp <= (2 * Rounder2)) {
//                tempCa[1] = 1;
//            } else {
//                tempCa[1] = 2;
//            }
//        } else {
//            if (noRoundTemp < Rounder) {
//                tempcGMP[1] = 0;
//            } else {
//                tempcGMP[1] = 1;
//            }
//        }

        double noRoundCa = (sumaTempCa * epsilon) + (tempCa[1] * (1 - epsilon));
        if (noRoundCa <= Rounder2) {
            tempCa[1] = 0;
        } else if (noRoundCa > Rounder2 && noRoundCa <= (2 * Rounder2)) {
            tempCa[1] = 1;
        } else {
            tempCa[1] = 2;
        }


        double noRoundcAMP = (sumaTempcAMP * epsilon) + (tempcAMP[1] * (1 - epsilon));
//        if (noRoundcAMP <= Rounder) {
//            tempCa[1] = 0;
//        } else if (noRoundcAMP > Rounder && noRoundcAMP <= (2 * Rounder)) {
//            tempCa[1] = 1;
//        } else {
//            tempCa[1] = 2;
//        }


        double noRoundcGMP = (sumaTempcGMP * epsilon) + (tempcGMP[1] * (1 - epsilon));
//        if (noRoundcGMP <= Rounder) {
//            tempCa[1] = 0;
//        } else if (noRoundcGMP > Rounder && noRoundcGMP <= (2* Rounder)) {
//            tempCa[1] = 1;
//        } else {
//            tempCa[1] = 2;
//        }

        /*
         * 5 de mayo 2012
         * ecoplando cGMP
         */
        if (noRoundcGMP < Rounder) {
            tempcGMP[1] = 0;
        } else {
            tempcGMP[1] = 1;
        }


        /*
         * 5 de mayo 2012
         * ecoplando cAMP
         */
        if (noRoundcAMP < Rounder) {
            tempcAMP[1] = 0;
        } else {
            tempcAMP[1] = 1;
        }

        /*
         * 13 de Julio:
         * La forma correcta de acoplar es suponiendo que el factor de homogeneización
         * depende también de epsilon, por lo tanto, en lugar de usar la variable
         * "divisor", voy a usar (1+epsilon), ya que cuando epsilon tiende a 0, regresamos
         * a la dinámica desacoplada.
         */

//        tempCa[1] = (int) Math.round((sumaTemp * epsilon) + (tempCa[1] / divisor));
//        tempCa[1] = (int) Math.round((sumaTemp * epsilon) + (tempCa[1] / (1 + epsilon)));
//   tempCa[1] = (int) Math.round((sumaTemp * epsilon) + (tempCa[1] * (1 - epsilon)));   //Difusivo
//        tempCa[1] = (int) ((int) Math.round(epsilon * tempCa[0]) + ((1 - epsilon) * tempCa[2]) + (divisor * tempCa[1]));//Jor
//        tempCa[1] = (int) Math.round((tempCa[1] * divisor) + CalcioPos_m1 - CalcioPos_M1);   //El buenisimo 24 de agosto




        if (tempCa[1] > 2) {
            tempCa[1] = 2;
        }
        if (tempCa[1] < 0) {
            tempCa[1] = 0;
        }
        /*
         * Ahora sí vamos a asignar el valor obtenido de la dinámica
         * para todos los nodos, excepto para el calcio.
         * El calcio se va a asignar despuÃ©s con el valor de tempCa
         */
        for (n = 0; n < N; ++n) {
            if (n2[n].isPresent()) {
                n2[n].setS(nuevoEstado2[n]);
            } else {
                if (n2[n].isBinary()) {
                    n2[n].setS(0);
                } else {
                    n2[n].setS(1);
                }
            }
        }

        //ponemos el valor del calcio dependiendo del acoplamiento
//        n2[NodoAcoplador].setS(tempNode[1]);
//        n2[2].setS(tempcGMP[1]);
//        n2[13].setS(tempcAMP[1]);
        n2[N - 1].setS(tempCa[1]);
//        System.out.println("cAMP es " +n2[13].getS());
    }

    /**
     * Este es igual al de evolveCoupled pero solo con
     * 2 nodos, el de la cola y el de junto.
     * En este mÃ©todo se hace lo mismo que arriba pero con 2
     * @param n1 nodo de la cola
     * @param n2 nodo de junto
     * @param epsilon, el tÃ©rmino que disminuye el coupling.
     */
    public static void evolveCola(Nodo[] n1, Nodo[] n2, double epsilon, double divisor, int posicion) {
        int N = n1.length;                          //N tiene el tamaÃ±o de la red
        int[] nuevoEstado1 = new int[N];             //vector de tamaÃ±o N "O SEA 22" donde guarda
        int[] nuevoEstado2 = new int[N];             //para cada nodo
        int ifr, ifr2;
        int i, m, n, nr, nr2;
        int[] nt;
        int[] nt2;
        int[] tempCa = new int[2];

        for (n = 0; n < N; ++n) {               //for para cada nodo de la red
            nr = n1[n].getNumReg();            //"nr" guarda el nÃºmero de reguladores del nodo N
            nr2 = n2[n].getNumReg();           // dependiendo de la red que va a evolucionar
            nt = new int[nr];                  //"nt" vector que guarda el nÃºm d regs. de N
            nt2 = new int[nr2];

            //Para cada uno de los 3 nodos
            //obtenemos el estado de sus reguladores
            for (m = 0; m < nr; ++m) {
                i = n1[n].getReg(m);
                nt[m] = n1[i].getS();
            }

            for (m = 0; m < nr2; ++m) {
                i = n2[n].getReg(m);
                nt2[m] = n2[i].getS();
            }

            ifr = ternaryToInt(nt);
            ifr2 = ternaryToInt(nt2);

            nuevoEstado1[n] = n1[n].getFuncVal(ifr);
            nuevoEstado2[n] = n2[n].getFuncVal(ifr2);
        }

        tempCa[0] = nuevoEstado1[N - 1];
        tempCa[1] = nuevoEstado2[N - 1];

        /*
         *Creo una variable llamada CalcioPos_m1
         * y otra llamada                  CalcioPos_M1
         * para hacer mi cuenta chingona.... o sea, voy a hacer estas
         * dos variables, valores dependientes de la posicion del flagelo
         * en la que se encuentren y con eso voy a crear una exponencial
         * negativa para ver cómo se comporta así el acoplamiento
         */

        double CalcioPos_m1 = Math.round(tempCa[0] * Math.exp(-epsilon * (posicion - 1)));


        double sumaTemp = epsilon * tempCa[1];

        /*
         * 13 de Julio: Mismo cambio de "divisor" por (1 + epsilon)
         */
//        tempCa[0] = (int) Math.round((sumaTemp) + (tempCa[0] / divisor));
//         tempCa[0] = (int) Math.round((sumaTemp) + (tempCa[0] / (1 + epsilon) ));
        tempCa[0] = (int) Math.round((sumaTemp * epsilon) + (tempCa[0] * (1 - epsilon)));      //Difusivo
//        tempCa[1] = (int) ((int) Math.round ((1 - epsilon) * tempCa[0]) + (divisor * tempCa[1])); //de Jorge
//        tempCa[1] = (int) Math.round(CalcioPos_m1 - tempCa[1] / divisor);   //El buenisimo 24 de agosto


        if (tempCa[1] > 2) {
            tempCa[1] = 2;
        }

        if (tempCa[1] < 0) {
            tempCa[1] = 0;
        }

        /*
         * Ahora sÃ­ vamos a asignar el valor obtenido de la dinÃ¡mica
         * para todos los nodos, excepto para el calcio.
         * El calcio se va a asignar despuÃ©s con el valor de tempCa
         */
        for (n = 0; n < N - 1; ++n) {
            if (n1[n].isPresent()) {
                n1[n].setS(nuevoEstado1[n]);
            } else {
                if (n1[n].isBinary()) {
                    n1[n].setS(0);
                } else {
                    n1[n].setS(1);
                }
            }
        }
        //ponemos el valor del calcio en tempCa[1] dependiendo del acoplamiento
        n1[N - 1].setS((tempCa[0]));
    }

    /**
     * Este es igual al de evolveCoupled pero solo con
     * 2 nodos, el de la cola y el de junto.
     * En este mÃ©todo se hace lo mismo que arriba pero con 2
     * @param n1 nodo de la cola
     * @param n2 nodo de junto
     * @param epsilon, el tÃ©rmino que disminuye el coupling.
     */
    public static void evolveCuello(Nodo[] n1, Nodo[] n2, double epsilon, double divisor, int posicion) {
        int N = n1.length;                          //N tiene el tamaÃ±o de la red
        int[] nuevoEstado1 = new int[N];             //vector de tamaÃ±o N "O SEA 22" donde guarda
        int[] nuevoEstado2 = new int[N];             //para cada nodo
        int ifr, ifr2;
        int i, m, n, nr, nr2;
        int[] nt;
        int[] nt2;
        int[] tempCa = new int[2];

        for (n = 0; n < N; ++n) {               //for para cada nodo de la red
            nr = n1[n].getNumReg();            //"nr" guarda el nÃºmero de reguladores del nodo N
            nr2 = n2[n].getNumReg();           // dependiendo de la red que va a evolucionar
            nt = new int[nr];                  //"nt" vector que guarda el nÃºm d regs. de N
            nt2 = new int[nr2];

            //Para cada uno de los 3 nodos
            //obtenemos el estado de sus reguladores
            for (m = 0; m < nr; ++m) {
                i = n1[n].getReg(m);
                nt[m] = n1[i].getS();
            }

            for (m = 0; m < nr2; ++m) {
                i = n2[n].getReg(m);
                nt2[m] = n2[i].getS();
            }

            ifr = ternaryToInt(nt);
            ifr2 = ternaryToInt(nt2);

            nuevoEstado1[n] = n1[n].getFuncVal(ifr);
            nuevoEstado2[n] = n2[n].getFuncVal(ifr2);
        }

        tempCa[0] = nuevoEstado1[N - 1];
        tempCa[1] = nuevoEstado2[N - 1];

        /*
         *Creo una variable llamada CalcioPos_m1
         * y otra llamada                  CalcioPos_M1
         * para hacer mi cuenta chingona.... o sea, voy a hacer estas
         * dos variables, valores dependientes de la posicion del flagelo
         * en la que se encuentren y con eso voy a crear una exponencial
         * negativa para ver cómo se comporta así el acoplamiento
         */


        double CalcioPos_M1 = tempCa[1] * Math.exp(-epsilon * (posicion + 1));

//        System.out.println("exponente es " + CalcioPos_M1 );

        double sumaTemp = epsilon * tempCa[1];

        /*
         * 13 de Julio: Mismo cambio de "divisor" por (1 + epsilon)
         */
//        tempCa[0] = (int) Math.round((sumaTemp) + (tempCa[0] / divisor));
//         tempCa[0] = (int) Math.round((sumaTemp) + (tempCa[0] / (1 + epsilon) ));
//         tempCa[1] = (int) Math.round((sumaTemp * epsilon) + (tempCa[0] * (1 - epsilon)));      //Difusivo
//        tempCa[1] = (int) ((int) Math.round ((1 - epsilon) * tempCa[0]) + (divisor * tempCa[1])); //de Jorge
        tempCa[1] = (int) Math.round((tempCa[0] * divisor) - CalcioPos_M1);   //El buenisimo 24 de agosto


        if (tempCa[1] > 2) {
            tempCa[1] = 2;
        }
        if (tempCa[1] < 0) {
            tempCa[1] = 0;
        }

        /*
         * Ahora sÃ­ vamos a asignar el valor obtenido de la dinÃ¡mica
         * para todos los nodos, excepto para el calcio.
         * El calcio se va a asignar despuÃ©s con el valor de tempCa
         */
        for (n = 0; n < N - 1; ++n) {
            if (n1[n].isPresent()) {
                n1[n].setS(nuevoEstado1[n]);
            } else {
                if (n1[n].isBinary()) {
                    n1[n].setS(0);
                } else {
                    n1[n].setS(1);
                }
            }
        }
        //ponemos el valor del calcio dependiendo del acoplamiento
        n1[N - 1].setS((tempCa[0]));
    }

    /**
     * Método para acoplar redes, obtener los atractores
     * y curvas características, así como transitorios
     * pero SOLAMENTE EL NODO CORRESPONDIENTE AL CALCIO
     * porque si no va a ser un monton de números que
     * nomás estorban
     * Los 10 primeros parámteros son las redes a evolucionar
     * @param r
     * @param r2
     * @param r3
     * @param r4
     * @param r5
     * @param r6
     * @param r7
     * @param r8
     * @param r9
     * @param r10
     * @param iC    El nÃºmero mÃ¡ximo de condiciones iniciales (< 100 000 )
     * @param eps   El epsilon que multiplica el tÃ©rmino de acoplamiento
     * @param div  Mi sigma o el término de perdida de calcio en el flagelo
     * @return      longTrans pa ver cuanto tarda en llegar al atractor o transitorio promedio
     *
     * NOTA NO MENTAL: TODO LO HECHO EN ESTE ARCHIVO, PARTICULARMENTE EN ESTE METODO
     * SE HIZO EN FLORENCIA, RECORDARLO POR SI HAY ERRORES
     */
    public static double[] todoCoupled(Red r, Red r2, Red r3, Red r4, Red r5,
            Red r6,
            Red r7, Red r8, Red r9, Red r10,
            int iC, double eps, double div, double rounder2, double rounder, int nodoAcoplador) {
        ArrayList<Attractor> atractorGde = new ArrayList<Attractor>();
        int N = r.getSize();
        int tMax = 200;
        int[][] trj;
        int[][] trj2;
        int[][] trj3;
        int[][] trj4;
        int[][] trj5;
        int[][] trj6;
        int[][] trj7;
        int[][] trj8;
        int[][] trj9;
        int[][] trj10;
        int[][] flagelo;
        int[][] calcio;
        int[][] vol;
        int[][] calcioProm;
        double[] calciote = new double[tMax];
        trj = new int[tMax][N];
        trj2 = new int[tMax][N];
        trj3 = new int[tMax][N];
        trj4 = new int[tMax][N];
        trj5 = new int[tMax][N];
        trj6 = new int[tMax][N];
        trj7 = new int[tMax][N];
        trj8 = new int[tMax][N];
        trj9 = new int[tMax][N];
        trj10 = new int[tMax][N];
        double epsilon;
        epsilon = eps;
        double[] sumota = new double[2];
        int sumota2 = 0;
        Attractor a = null;
        flagelo = new int[tMax][10 * N];
        calcio = new int[tMax][10];
        vol = new int[tMax][10];
        calcioProm = new int[tMax][10];
        int l = 0;
        int k, t;
        int longTrans = 0;
        boolean busca, ya;

        int[] transi = new int[2];
        int cuenta = 0;

//        FileToWrite fw1 = new FileToWrite("ItalianCoupling/NumRedesVSTransitorio/PruebaCouplingIsOK/10Redes_TransCalcio_" + epsilon
//                + "_.dat");
//
//
//        FileToWrite fw2 = new FileToWrite("ItalianCoupling/NumRedesVSTransitorio/PruebaCouplingIsOK/10Redes_TransFlagelo_" + epsilon
//                + "_.dat");

        FileToWrite fw3 = new FileToWrite("ItalianCoupling/GoodCoupling/10redesCalcio_Eps_" + epsilon + "_.dat");
//         FileToWrite fw4 = new FileToWrite("ItalianCoupling/CalcioEnCola/Derivada_" + epsilon + "_.dat");
//        FileToWrite fileCa = new FileToWrite("Enero12/Red5_ArrayCa_" + eps +"_div_"+ div + "_.txt");


        for (int ic = 0; ic < iC; ++ic) {
//            FileToWrite fw3 = new FileToWrite("/home/chucho/Coupled//ic_" + iC + "periodo_eps_" + epsilon + "_div" + div + ".dat");
//            FileToWrite fw4 = new FileToWrite("/home/chucho/Coupled//ic_" + iC + "cuenca_eps_" + epsilon + "_div" + div + ".dat");
//            FileToWrite fileCa = new FileToWrite("/home/chucho/Coupled/AcopleMayo12/DeTablasCatsper2/Isopotencial/CaAcoplado/Calcio_Epsilon_" + eps + "_ic_" + ic + "_.txt");
//            fw3.writeLine("atractor\t" + "periodo");
//            fw4.writeLine("atractor\t" + "cuenca" + "\t");
            //*******************************
            //17 de agosto comentado
//System.out.println();
//            ++cuenta;
//            if (cuenta > 100) {
//                System.out.println("*********************************************Condicion inicial  " + ic + "\n");
//                cuenta = 0;
//            }

//            stop:
//            {
                /*
             * Iniciamos las 10 redes aleatorias y
             * guardamos los valores en su arreglo de trayectorias y en
             * el arreglo gigante del Flagelo.
             * NOTA IMPORTANTE
             * CREO UN VECTOR LLAMADO "calcio"
             * EN ESTE ARREGLO GUARDO SOLO EL CALCIO DE
             * CADA RED, PARA CALCULAR LOS ATRACTORES
             * CON ESTE ARREGLO, NO CON FLAGELO, PARA VER SI SE PUEDE HACER
             * EL MUESTREO CON MAS COND. INICIALES
             * A VER QUE PASA
            /
             * 
             */
            r.setRandomStates();
            r2.setRandomStates();
            r3.setRandomStates();
            r4.setRandomStates();
            r5.setRandomStates();
            r6.setRandomStates();
            r7.setRandomStates();
            r8.setRandomStates();
            r9.setRandomStates();
            r10.setRandomStates();

            calcio[0][0] = trj[0][N - 1];
            calcio[0][1] = trj2[0][N - 1];
            calcio[0][2] = trj3[0][N - 1];
            calcio[0][3] = trj4[0][N - 1];
            calcio[0][4] = trj5[0][N - 1];
            calcio[0][5] = trj6[0][N - 1];
            calcio[0][6] = trj7[0][N - 1];
            calcio[0][7] = trj8[0][N - 1];
            calcio[0][8] = trj9[0][N - 1];
            calcio[0][9] = trj10[0][N - 1];

//            Aqui ya tenemos el estado inicial de las redes chicas y la redsota

            /*
             * Evolucionamos las redes y llenamos los arreglos
             * de las trayectorias y el flagelo con los métodos
             * de evolución acoplando redes.
             * TAMBIEN "calcio" SE VA A LLENAR CON EL VALOR
             * DEL CALCIO DE CADA PASO DE EVOLUCION DE LA RED
             */
            for (t = 1; t < tMax; ++t) {

                r.evolveKauffmanCoupled(r10, r, r2, epsilon, div, 1, rounder2, rounder, nodoAcoplador);
                r2.evolveKauffmanCoupled(r, r2, r3, epsilon, div, 2, rounder2, rounder, nodoAcoplador);
                r3.evolveKauffmanCoupled(r2, r3, r4, epsilon, div, 3, rounder2, rounder, nodoAcoplador);
                r4.evolveKauffmanCoupled(r3, r4, r5, epsilon, div, 4, rounder2, rounder, nodoAcoplador);
                r5.evolveKauffmanCoupled(r4, r5, r6, epsilon, div, 5, rounder2, rounder, nodoAcoplador);
                r6.evolveKauffmanCoupled(r5, r6, r7, epsilon, div, 6, rounder2, rounder, nodoAcoplador);
                r7.evolveKauffmanCoupled(r6, r7, r8, epsilon, div, 7, rounder2, rounder, nodoAcoplador);
                r8.evolveKauffmanCoupled(r7, r8, r9, epsilon, div, 8, rounder2, rounder, nodoAcoplador);
                r9.evolveKauffmanCoupled(r8, r9, r10, epsilon, div, 9, rounder2, rounder, nodoAcoplador);
                r10.evolveKauffmanCoupled(r9, r10, r, epsilon, div, 10, rounder2, rounder, nodoAcoplador);

                /*
                 * 6 de mayo de 2012;
                 * *********************
                 * VOLTAJE ISOPOTENCIAL
                 * *********************
                 *
                 * Vamos a promediar el valor de los voltajes de las 10
                 * redes y lo vamos a asignar ESE UNICO VALOR DEL VOLTAJE
                 * a todas las redes.
                 *
                 */
                int voltEfectivo = 0;
                double volt = (r.getNodo(5).getS()
                        + r2.getNodo(5).getS()
                        + r3.getNodo(5).getS()
                        + r4.getNodo(5).getS()
                        + r5.getNodo(5).getS()
                        + r6.getNodo(5).getS()
                        + r7.getNodo(5).getS()
                        + r8.getNodo(5).getS()
                        + r9.getNodo(5).getS()
                        + r10.getNodo(5).getS()) / 10.0;

                if (volt < 0.66) {
                    voltEfectivo = 0;
                } else if (volt >= 0.66 && volt < 1.33) {
                    voltEfectivo = 1;
                } else {
                    voltEfectivo = 2;
                }

                r.getNodo(5).setS(voltEfectivo);
                r2.getNodo(5).setS(voltEfectivo);
                r3.getNodo(5).setS(voltEfectivo);
                r4.getNodo(5).setS(voltEfectivo);
                r5.getNodo(5).setS(voltEfectivo);
                r6.getNodo(5).setS(voltEfectivo);
                r7.getNodo(5).setS(voltEfectivo);
                r8.getNodo(5).setS(voltEfectivo);
                r9.getNodo(5).setS(voltEfectivo);
                r10.getNodo(5).setS(voltEfectivo);

                /*
                 * Aqui acaba el rollo del voltaje isopotencial
                 */

                trj[t] = r.getStates();
                trj2[t] = r2.getStates();
                trj3[t] = r3.getStates();
                trj4[t] = r4.getStates();
                trj5[t] = r5.getStates();
                trj6[t] = r6.getStates();
                trj7[t] = r7.getStates();
                trj8[t] = r8.getStates();
                trj9[t] = r9.getStates();
                trj10[t] = r10.getStates();

                calcio[t][0] = trj[t][N - 1];
                calcio[t][1] = trj2[t][N - 1];
                calcio[t][2] = trj3[t][N - 1];
                calcio[t][3] = trj4[t][N - 1];
                calcio[t][4] = trj5[t][N - 1];
                calcio[t][5] = trj6[t][N - 1];
                calcio[t][6] = trj7[t][N - 1];
                calcio[t][7] = trj8[t][N - 1];
                calcio[t][8] = trj9[t][N - 1];
                calcio[t][9] = trj10[t][N - 1];

                /*
                 * 29 octubre 2012
                 * Quiero ver el calcio como si fuera el flagelo
                 */

//                for(int fl = 0; fl < 10; ++fl){
//                    System.out.print(calcio[t][fl] + " ");
//                }
//                System.out.println();


                /*
                 * 7 de noviembre de 2012
                 * Voy a sumar los calcios y graficarlos
                 */

                for (int i = 0; i < 10; i++) {
                    calciote[t] += calcio[t][i];
                }


                /*
                 * Aca vemos cuánto tarda el transitorio
                 * Comentarlo si vamos a encontrar los
                 * atractores y guardarlos en el archivo
                 */
                //************************
                //*********17 de agosto
                stop:
                for (int m = 0; m < tMax - 1; ++m) {
                    for (int h = m + 1; h < tMax; ++h) {

                        if (arrayEquals(calcio[m], calcio[h])) {
//                            sumota[0] += h;
                            transi[0] = h;
                            m = tMax;
                            continue stop;
                        }
                    }
                }

                stop2:
                for (int m = 0; m < tMax - 1; ++m) {
                    for (int h = m + 1; h < tMax; ++h) {
                        if (arrayEquals(trj[m], trj[h])) {
//                            sumota[1] += h;
                            transi[1] = h;
                            m = tMax;
                            continue stop2;
                        }
                    }
                }
            }
//            System.out.println(eps + "\t" + transi[0] 
//                  +  "\t" + transi[1]
//                    );

//            fw1.writeLine(eps + "\t" + transi[0]);
//
//            fw2.writeLine(eps + "\t" + transi[1]);
//        fw2.close();
//        fileCa.close();
            sumota[0] += transi[0];
            sumota[1] += transi[1];
        }
        for (int i = 0; i < tMax - 1; ++i) {
//        calciote[i] /= iC;
            for (int j = 0; j < 10; ++j) {
                fw3.writeString((calcio[i][j]) + "\t");
            }
            fw3.writeLine();
//        fw3.writeLine(i + "\t" + (calcio[i]/iC));
//        fw4.writeLine(i + "\t" + ((calciote[i+1] - calciote[i])/iC));
        }
//        fw1.close();
//        fw2.close();
        fw3.close();
//        fw4.close();
//        sumota[0] += sumota2;

        sumota[0] = sumota[0] / iC;
        sumota[1] = sumota[1] / iC;
        return sumota;
//        return longTrans;
    }

    /**
     * Método para acoplar redes, obtener los atractores
     * y curvas características, así como transitorios
     * pero SOLAMENTE EL NODO CORRESPONDIENTE AL CALCIO
     * porque si no va a ser un monton de números que
     * nomás estorban, vamos a hacerla con distribucion exponencial
     * de los canales de calcio y el KCNG, xq los demas son del feedback
     * Los 10 primeros parámteros son las redes a evolucionar
     * @param r
     * @param r2
     * @param r3
     * @param r4
     * @param r5
     * @param r6
     * @param r7
     * @param r8
     * @param r9
     * @param r10
     * @param iC    El nÃºmero mÃ¡ximo de condiciones iniciales (< 100 000 )
     * @param eps   El epsilon que multiplica el tÃ©rmino de acoplamiento
     * @param div  Mi sigma o el término de perdida de calcio en el flagelo
     * @return      longTrans pa ver cuanto tarda en llegar al atractor o transitorio promedio
     */
    public static int todoCoupledExp(Red r, Red r2, Red r3, Red r4,
            Red r5, Red r6, Red r7, Red r8, Red r9, Red r10, int iC, double eps, double div, int nodoAcoplador) {
        double rounder = 0;
        double rounder2 = 0;
        ArrayList<Attractor> atractorGde = new ArrayList<Attractor>();
        int N = r.getSize();
        int tMax = 15;
        int[][] trj;
        int[][] trj2;
        int[][] trj3;
        int[][] trj4;
        int[][] trj5;
        int[][] trj6;
        int[][] trj7;
        int[][] trj8;
        int[][] trj9;
        int[][] trj10;

        int[][] flagelo;
        int[][] calcio;
        int[][] calcioProm;
        trj = new int[tMax][N];
        trj2 = new int[tMax][N];
        trj3 = new int[tMax][N];
        trj4 = new int[tMax][N];
        trj5 = new int[tMax][N];
        trj6 = new int[tMax][N];
        trj7 = new int[tMax][N];
        trj8 = new int[tMax][N];
        trj9 = new int[tMax][N];
        trj10 = new int[tMax][N];

        double epsilon;
        epsilon = eps;
        int sumota = 0;
        int sumota2 = 0;
        flagelo = new int[tMax][10 * N];
        calcio = new int[tMax][10];
        calcioProm = new int[tMax][10];
        int l = 0;
        int k, t;



        for (int ic = 0; ic < iC; ++ic) {
            /*
             * Iniciamos las 10 redes aleatorias y
             * guardamos los valores en su arreglo de trayectorias y en
             * el arreglo gigante del Flagelo.
             * NOTA IMPORTANTE
             * CREO UN VECTOR LLAMADO "calcio"
             * EN ESTE ARREGLO GUARDO SOLO EL CALCIO DE
             * CADA RED, PARA CALCULAR LOS ATRACTORES
             * CON ESTE ARREGLO, NO CON FLAGELO, PARA VER SI SE PUEDE HACER
             * EL MUESTREO CON MAS COND. INICIALES
             * A VER QUE PASA
             */

            /*
             * SEGUNDA PARTE: DISTRIBUCION DE LOS NODOS
             * Nuestros nodos van a seguir una distribución exponencial
             * dependiente de la posición en el flagelo, es decir, en el orden
             * de las redes.
             * NODOS A CAMBIAR:
             * HVA----->14
             * LVA------>10
             * cAMPCC->17
             * KCNG---->3
             * un double llamado probabExistencia que determine si
             * estuvo o no el nodo
             */
            double probabExistencia = 0;

            r.setRandomStates();
            probabExistencia = Math.random();
            if (probabExistencia > Math.exp(-epsilon)) {
                r.getNodo(3).setPresente(false);
                r.getNodo(10).setPresente(false);
                r.getNodo(14).setPresente(false);
                r.getNodo(17).setPresente(false);
            }
            trj[0] = r.getStates();
            for (int j = 0; j < N; ++j) {
                flagelo[0][j] = trj[0][j];
            }

            r2.setRandomStates();
            probabExistencia = Math.random();
            if (probabExistencia > Math.exp(-epsilon * 2)) {
                r2.getNodo(3).setPresente(false);
                r2.getNodo(10).setPresente(false);
                r2.getNodo(14).setPresente(false);
                r2.getNodo(17).setPresente(false);
            }
            trj2[0] = r2.getStates();
            for (int j = N; j < 2 * N; ++j) {
                flagelo[0][j] = trj2[0][j - (N)];
            }

            r3.setRandomStates();
            probabExistencia = Math.random();
            if (probabExistencia > Math.exp(-epsilon * 3)) {
                r3.getNodo(3).setPresente(false);
                r3.getNodo(10).setPresente(false);
                r3.getNodo(14).setPresente(false);
                r3.getNodo(17).setPresente(false);
            }
            trj3[0] = r3.getStates();
            for (int j = (2 * N); j < 3 * N; ++j) {
                flagelo[0][j] = trj3[0][j - ((2 * N))];
            }

            r4.setRandomStates();
            probabExistencia = Math.random();
            if (probabExistencia > Math.exp(-epsilon * 4)) {
                r4.getNodo(3).setPresente(false);
                r4.getNodo(10).setPresente(false);
                r4.getNodo(14).setPresente(false);
                r4.getNodo(17).setPresente(false);
            }
            trj4[0] = r4.getStates();
            for (int j = (3 * N); j < 4 * N; ++j) {
                flagelo[0][j] = trj4[0][j - (3 * N)];
            }

            r5.setRandomStates();
            probabExistencia = Math.random();
            if (probabExistencia > Math.exp(-epsilon * 5)) {
                r5.getNodo(3).setPresente(false);
                r5.getNodo(10).setPresente(false);
                r5.getNodo(14).setPresente(false);
                r5.getNodo(17).setPresente(false);
            }
            trj5[0] = r5.getStates();
            for (int j = 4 * N; j < 5 * N; ++j) {
                flagelo[0][j] = trj5[0][j - (4 * N)];
            }

            r6.setRandomStates();
            probabExistencia = Math.random();
            if (probabExistencia > Math.exp(-epsilon * 6)) {
                r6.getNodo(3).setPresente(false);
                r6.getNodo(10).setPresente(false);
                r6.getNodo(14).setPresente(false);
                r6.getNodo(17).setPresente(false);
            }
            trj6[0] = r6.getStates();
            for (int j = (5 * N); j < 6 * N; ++j) {
                flagelo[0][j] = trj6[0][j - ((5 * N))];
            }

            r7.setRandomStates();
            probabExistencia = Math.random();
            if (probabExistencia > Math.exp(-epsilon * 7)) {
                r7.getNodo(3).setPresente(false);
                r7.getNodo(10).setPresente(false);
                r7.getNodo(14).setPresente(false);
                r7.getNodo(17).setPresente(false);
            }
            trj7[0] = r7.getStates();
            for (int j = (6 * N); j < 7 * N; ++j) {
                flagelo[0][j] = trj7[0][j - ((6 * N))];
            }

            r8.setRandomStates();
            probabExistencia = Math.random();
            if (probabExistencia > Math.exp(-epsilon * 8)) {
                r8.getNodo(3).setPresente(false);
                r8.getNodo(10).setPresente(false);
                r8.getNodo(14).setPresente(false);
                r8.getNodo(17).setPresente(false);
            }
            trj8[0] = r8.getStates();
            for (int j = (7 * N); j < 8 * N; ++j) {
                flagelo[0][j] = trj8[0][j - (7 * N)];
            }

            r9.setRandomStates();
            probabExistencia = Math.random();
            if (probabExistencia > Math.exp(-epsilon * 9)) {
                r9.getNodo(3).setPresente(false);
                r9.getNodo(10).setPresente(false);
                r9.getNodo(14).setPresente(false);
                r9.getNodo(17).setPresente(false);
            }
            trj9[0] = r9.getStates();
            for (int j = 8 * N; j < 9 * N; ++j) {
                flagelo[0][j] = trj9[0][j - (8 * N)];
            }

            r10.setRandomStates();
            probabExistencia = Math.random();
            if (probabExistencia > Math.exp(-epsilon * 10)) {
                r10.getNodo(3).setPresente(false);
                r10.getNodo(10).setPresente(false);
                r10.getNodo(14).setPresente(false);
                r10.getNodo(17).setPresente(false);
            }
            trj10[0] = r10.getStates();
            for (int j = (9 * N); j < 10 * N; ++j) {
                flagelo[0][j] = trj10[0][j - ((9 * N))];
            }


            for (int cal = 0; cal < 10; cal++) {
                calcio[0][cal] = flagelo[0][(cal + 1) * (N - 1)];
            }
//            Aqui ya tenemos el estado inicial de las redes chicas y la redsota
//            con las modificaciones de la distribución de los  nodos.


            /*
             * Evolucionamos las redes y llenamos los arreglos
             * de las trayectorias y el flagelo con los métodos
             * de evolución acoplando redes usando la distribución
             * diferente de los nodos de calcio y KCNG
             */
            for (t = 1; t < tMax; ++t) {
                r.evolveKauffmanCuello(r, r2, epsilon, div, 1);
                r2.evolveKauffmanCoupled(r, r2, r3, epsilon, div, 2, rounder2, rounder, nodoAcoplador);
                r3.evolveKauffmanCoupled(r2, r3, r4, epsilon, div, 3, rounder2, rounder, nodoAcoplador);
                r4.evolveKauffmanCoupled(r3, r4, r5, epsilon, div, 4, rounder2, rounder, nodoAcoplador);
                r5.evolveKauffmanCoupled(r4, r5, r6, epsilon, div, 5, rounder2, rounder, nodoAcoplador);
                r6.evolveKauffmanCoupled(r5, r6, r7, epsilon, div, 6, rounder2, rounder, nodoAcoplador);
                r7.evolveKauffmanCoupled(r6, r7, r8, epsilon, div, 7, rounder2, rounder, nodoAcoplador);
                r8.evolveKauffmanCoupled(r7, r8, r9, epsilon, div, 8, rounder2, rounder, nodoAcoplador);
                r9.evolveKauffmanCoupled(r8, r9, r10, epsilon, div, 9, rounder2, rounder, nodoAcoplador);
                r10.evolveKauffmanCuello(r10, r9, epsilon, div, 10); //esta es la buena del de Jorge
//                r10.evolveKauffmanCola(r10, r9, epsilon, div); //esta es la buena del difusivo

                trj[t] = r.getStates();
                for (int j = 0; j < N; ++j) {
                    flagelo[t][j] = trj[t][j];
                    calcio[t][0] = trj[t][N - 1];
                }
                calcioProm[t][0] += calcio[t][0];

                trj2[t] = r2.getStates();
                for (int j = N; j < 2 * N; ++j) {
                    flagelo[t][j] = trj2[t][j - N];
                    calcio[t][1] = trj2[t][N - 1];
                }
                calcioProm[t][1] += calcio[t][1];

                trj3[t] = r3.getStates();
                for (int j = 2 * N; j < 3 * N; ++j) {
                    flagelo[t][j] = trj3[t][j - (2 * (N))];
                    calcio[t][2] = trj3[t][N - 1];
                }
                calcioProm[t][2] += calcio[t][2];

                trj4[t] = r4.getStates();
                for (int j = 3 * N; j < 4 * N; ++j) {
                    flagelo[t][j] = trj4[t][j - (3 * N)];
                    calcio[t][3] = trj4[t][N - 1];
                }
                calcioProm[t][3] += calcio[t][3];

                trj5[t] = r5.getStates();
                for (int j = 4 * N; j < 5 * N; ++j) {
                    flagelo[t][j] = trj5[t][j - (4 * N)];
                    calcio[t][4] = trj5[t][N - 1];
                }
                calcioProm[t][4] += calcio[t][4];

                trj6[t] = r6.getStates();
                for (int j = 5 * N; j < 6 * N; ++j) {
                    flagelo[t][j] = trj6[t][j - (5 * N)];
                    calcio[t][5] = trj6[t][N - 1];
                }
                calcioProm[t][5] += calcio[t][5];

                trj7[t] = r7.getStates();
                for (int j = 6 * N; j < 7 * N; ++j) {
                    flagelo[t][j] = trj7[t][j - (6 * N)];
                    calcio[t][6] = trj7[t][N - 1];
                }
                calcioProm[t][6] += calcio[t][6];

                trj8[t] = r8.getStates();
                for (int j = 7 * N; j < 8 * N; ++j) {
                    flagelo[t][j] = trj8[t][j - (7 * N)];
                    calcio[t][7] = trj8[t][N - 1];
                }
                calcioProm[t][7] += calcio[t][7];

                trj9[t] = r9.getStates();
                for (int j = 8 * N; j < 9 * N; ++j) {
                    flagelo[t][j] = trj9[t][j - (8 * N)];
                    calcio[t][8] = trj9[t][N - 1];
                }
                calcioProm[t][8] += calcio[t][8];

                trj10[t] = r10.getStates();
                for (int j = 9 * N; j < 10 * N; ++j) {
                    flagelo[t][j] = trj10[t][j - (9 * N)];
                    calcio[t][9] = trj10[t][N - 1];
                }
                calcioProm[t][9] += calcio[t][9];
            }
//          Aquí ya están llenos nuestros arreglos
        }

        /*
         * Ahora vamos a ver la evolución tipo
         * onda, o sea que se pasa de uno al otro
         * como una ola y asi se ve bien padre.
         *
         * La forma va a ser promediar cada casilla flagelo[t][N]
         * sobre todas las condiciones iniciales para que el calcio
         * en la posición 1, o sea el cuello, tenga su propio
         * promedio, la casilla 2, o sea, pegada al cuello, tenga
         * su promedio y asi... el chiste es que al graficar
         * calcio1 promedio contra tiempo, calcio2 promedio contra tiempo,
         * etc, tenga la misma forma que en la gráfica de las
         * celulas nadando cuando se salen de foco ("PruebaDifusion.dat").
         */
        FileToWrite fw = new FileToWrite("AgostoCoupled/24AgostoExponencial/DistCanales_eps_" + eps + "_div_" + div + ".txt");
        for (int tiempo = 0; tiempo < tMax; ++tiempo) {
            fw.writeString(tiempo + "  ");
            for (int caos = 0; caos < 10; ++caos) {
                double ca = (calcioProm[tiempo][caos]);
                fw.writeString(" " + ca);
            }
            fw.writeLine();
        }
        fw.close();
        sumota += sumota2;
        sumota = sumota / iC;
        return sumota;
    }

    public static void Mapeo() {
        int N = 300;
        int tMax = 5000;
        double a = 0.7692307685468465323;
        double b = 0.0;
        double epsilon = 1.3;
        double[] initialConditions = new double[N];
        double[] x = new double[N];

        long idum = -878946545;
        DecimalFormat df = new DecimalFormat("0.0000000000000000");

        FileToWrite fw = new FileToWrite("CML.dat");

        for (int i = 0; i < N; i++) {
            x[i] = initialConditions[i] = Math.random();
            fw.writeString(df.format(x[i]) + "\t");
//		System.out.print("\t"+df.format( x[i]));
        }
        fw.writeLine();
//System.out.println();

        for (int i = 0; i < tMax; i++) {
            F(a, b, epsilon, x, N);
            for (int j = 0; j < N; j++) {
                fw.writeString(df.format(x[j]) + "\t");
//			System.out.print("\t"+ df.format(x[j]));
            }
            fw.writeLine();
//		System.out.println();
        }
    }

    public static void F(double a, double b, double eps, double[] x, int N) {
        // Take care of frontier conditions first
        double ip;
        if (x[0] < a) {
            x[0] = ((x[0] + (eps / 2) * (x[1] - x[N - 1])) / a) % 1;
        } else {
            x[0] = a + b * (((x[0] + (eps / 2) * (x[1] - x[N - 1])) - a) % 1);
        }
        if (x[N - 1] < a) {
            x[N - 1] = ((x[N - 1] + (eps / 2) * (x[0] - x[N - 2])) / a) % 1;
        } else {
            x[N - 1] = a + b * (((x[N - 1] + (eps / 2) * (x[0] - x[N - 2])) - a) % 1);
        }
        // Then compute the rest of the mapping
        for (int i = 1; i < N - 1; i++) {
            if (x[i] < a) {
                x[i] = ((x[i] + (eps / 2) * (x[i + 1] - x[i - 1])) / a) % 1;
            } else {
                x[i] = a + b * (((x[i] + (eps / 2) * (x[i + 1] - x[i - 1])) - a) % 1);
            }
        }

    }

    /**
     * Encuentra las ciudades que están a un radio menor
     * que el determinado por el usuario
     * @param d distancia entre puntos
     */
    public static void radioMenor(String estado, String mpio, String edoTarget, String mpioTarget, double d, double radio, FileToWrite fwr, int muertos) {

        double RadioMenor1 = 0;
        if (d < radio) {
            RadioMenor1 = d;

            fwr.writeLine(edoTarget + "\t" + mpioTarget + "\t" + RadioMenor1 + "\t" + muertos);
        }



//        return RadioMenor;

    }

    /**
     * Lee los archivos de las coordenadas ".dat" pero feos
     * y a partir de ellos crea unos ".dat"
     * unicamente con dos columnas de la coordenada X y la Y
     */
    public static void creaDatsCoordsGMaps() {
        String file = ("Hernan/dats/opio/Gro.dat");
        String file2 = ("Hernan/dats/opio/Nay.dat");
        String file3 = ("Hernan/dats/opio/Oax.dat");
        String file4 = ("Hernan/dats/opio/RutaOpio.dat");
        String file5 = ("Hernan/dats/opio/Sin.dat");

        FileToRead fr = new FileToRead(file);
        FileToRead fr2 = new FileToRead(file2);
        FileToRead fr3 = new FileToRead(file3);
        FileToRead fr4 = new FileToRead(file4);
        FileToRead fr5 = new FileToRead(file5);

        FileToWrite fw = new FileToWrite("/home/chucho/Desktop/Hernan/Fotos/RegionesOpio/datsLimpios/Gro.dat");
        FileToWrite fw2 = new FileToWrite("/home/chucho/Desktop/Hernan/Fotos/RegionesOpio/datsLimpios/Nay.dat");
        FileToWrite fw3 = new FileToWrite("/home/chucho/Desktop/Hernan/Fotos/RegionesOpio/datsLimpios/Oax.dat");
        FileToWrite fw4 = new FileToWrite("/home/chucho/Desktop/Hernan/Fotos/RegionesOpio/datsLimpios/RutaOpio.dat");
        FileToWrite fw5 = new FileToWrite("/home/chucho/Desktop/Hernan/Fotos/RegionesOpio/datsLimpios/Sin.dat");

        String linea = fr.nextLine() + "";
        String linea2 = fr2.nextLine() + "";
        String linea3 = fr3.nextLine() + "";
        String linea4 = fr4.nextLine() + "";
        String linea5 = fr5.nextLine() + "";

        StringTokenizer cosa = new StringTokenizer(linea);
        StringTokenizer cosa2 = new StringTokenizer(linea2);
        StringTokenizer cosa3 = new StringTokenizer(linea3);
        StringTokenizer cosa4 = new StringTokenizer(linea4);
        StringTokenizer cosa5 = new StringTokenizer(linea5);

        while (cosa.hasMoreTokens()) {
            String x = cosa.nextToken(",");
            String y = cosa.nextToken(",");
            String cero = cosa.nextToken(" ");
            fw.writeLine(x + "\t" + y);
        }
        while (cosa2.hasMoreTokens()) {
            String x = cosa2.nextToken(",");
            String y = cosa2.nextToken(",");
            String cero = cosa2.nextToken(" ");
            fw2.writeLine(x + "\t" + y);
        }
        while (cosa3.hasMoreTokens()) {
            String x = cosa3.nextToken(",");
            String y = cosa3.nextToken(",");
            String cero = cosa3.nextToken(" ");
            fw3.writeLine(x + "\t" + y);
        }
        while (cosa4.hasMoreTokens()) {
            String x = cosa4.nextToken(",");
            String y = cosa4.nextToken(",");
            String cero = cosa4.nextToken(" ");
            fw4.writeLine(x + "\t" + y);
        }
        while (cosa5.hasMoreTokens()) {
            String x = cosa5.nextToken(",");
            String y = cosa5.nextToken(",");
            String cero = cosa5.nextToken(" ");
            fw5.writeLine(x + "\t" + y);
        }

        fr.close();
        fw.close();

        fr2.close();
        fw2.close();

        fr3.close();
        fw3.close();

        fr4.close();
        fw4.close();

        fr5.close();
        fw5.close();

    }

    /**
     * Genera un patrón temporal de la evolución del calcio
     * mediante un promedio de muchas configuraciones iniciales,
     * y corriendo 10 pasos de tiempo la ventana para suavizar
     * la señal. Con todos los nodos presentes, quitando 1 nodo y
     * quitando 2 nodos.
     * La parte nueva es que ahora catsper se activa independientemente, simulando
     * el efecto del NFA al sobreactivarlo.
     * Se activa dependiendo del módulo del entero pasoAct
     * @param r1 La red que se va a evolucionar
     * @param iC las condiciones iniciales a promediar
     * @param file es el nombre del archivo que guarda la dinamica de calcio
     */
    public static double[] calciumEvolutionCatsper(Red r1, int iC, String file) {
        FileToWrite fw;
        int T = 1200;
        for (int m = 0; m < r1.getSize(); ++m) {
            r1.getNodo(m).setPresente(true);
        }
        int[][] trjs = new int[iC][T];
        double[] P = new double[T];
        for (int r = 0; r < iC; ++r) {
            r1.setRandomStates();
            for (int t = 0; t < T; ++t) {
                trjs[r][t] += r1.getNodo(21).getS();
                r1.evolveKauffman();
            }
        }
        for (int t = 0; t < T; ++t) {
            for (int r = 0; r < iC; ++r) {
                P[t] += trjs[r][t];
            }
            P[t] /= (1.0 * iC);
        }
        fw = new FileToWrite(file);
        for (int t = 0; t < T; ++t) {
            fw.writeLine(t + "\t\t" + P[t]);
        }
        fw.close();

        double[] Q = new double[200];
        for (int i = 1000; i < 1200; ++i) {
            Q[i - 1000] = P[i];
        }
        return Q;
    }

    /**
     * Crea archivos con el tiempo transitorio y
     * con la configuración con la que llegan al atractor:
     *      Tenemos el atractor 0 2 1 1
     *      pero se puede llegar a él de 4 formas distintas: 02, 21, 11 y 10
     * Este metodo calcula el tiempo transitorio y en cuál de esas 4 configuraciones inicia
     * el atractor.
     *
     * Posteriormente se hará un hstograma de frecuencias para versi está cargado
     * haacia uno de estas configuraciones, para poder explicar por qué el calcio
     * no se mantiene totalmente en 1, sino entre 0.85 y 1.15
     * @param r la red que se usa
     * @param iC el número de condiciones iniciales
     */
    public static void AtractorYTransient(Red r, int iC) {
        //leemos todas las condiciones que te llevan a periodo 4 con este archivo
        FileToRead fr = new FileToRead("/home/chucho/Desktop/ArchivosNetBeansDropboxErizo/ArrayToReachPeriod4.dat");
        FileToWrite fw02 = new FileToWrite("ceroDos.dat");
        FileToWrite fw21 = new FileToWrite("dosUno.dat");
        FileToWrite fw11 = new FileToWrite("unoUno.dat");
        FileToWrite fw10 = new FileToWrite("unoCero.dat");
        FileToWrite fw = new FileToWrite("Raro.dat");
        int N = r.getSize();
        int tMax = 100;
        int[][] trj;
        int TransProm = 0;
        int l, k, t;
        int longTrans = 0;
        boolean busca, ya;
        Attractor a;
        int cuenta = 0;
        for (int ka = 0; ka < N; ++ka) {
            r.getNodo(ka).setPresente(true);
        }
//        for (int ic = 0; ic < 10; ++ic) {
        while (fr.hasNextInt()) {
//            r.setRandomStates();
            /*
             * Asignamos a cada nodo el valor leído en el archivo,
             * de este modo, cada configurtación que te lleva al
             * atractor de periodo 4, será la condición inicial
             * de nuestra red.
             */
            r.getNodo(0).setS(fr.nextInt());
            r.getNodo(1).setS(fr.nextInt());
            r.getNodo(2).setS(fr.nextInt());
            r.getNodo(3).setS(fr.nextInt());
            r.getNodo(4).setS(fr.nextInt());
            r.getNodo(5).setS(fr.nextInt());
            r.getNodo(6).setS(fr.nextInt());
            r.getNodo(7).setS(fr.nextInt());
            r.getNodo(8).setS(fr.nextInt());
            r.getNodo(9).setS(fr.nextInt());
            r.getNodo(10).setS(fr.nextInt());
            r.getNodo(11).setS(fr.nextInt());
            r.getNodo(12).setS(fr.nextInt());
            r.getNodo(13).setS(fr.nextInt());
            r.getNodo(14).setS(fr.nextInt());
            r.getNodo(15).setS(fr.nextInt());
            r.getNodo(16).setS(fr.nextInt());
            r.getNodo(17).setS(fr.nextInt());
            r.getNodo(18).setS(fr.nextInt());
            r.getNodo(19).setS(fr.nextInt());
            r.getNodo(20).setS(fr.nextInt());
            r.getNodo(21).setS(fr.nextInt());
            trj = new int[tMax][N];
            for (t = 0; t < tMax; ++t) {
                r.evolveKauffman();
                trj[t] = r.getStates();
            }
            System.out.print("  " + trj[20][N - 1]);
//            for (int y = 0; y < iC; ++y) {
//                System.out.print("  " + trj[20][N - 1]);
//            }
//            System.out.println();
            t = tMax - 2;
            busca = true;
            l = 0;
            int Trans = 0;      //este lo voy a usar pa calcular el transitorio

            /*
             *Ahora vamos a ver el tiempo que le lleva a la red
             * generar una oscilacion periodica de calcio, no el atractor,
             * sino el tiempo que le lleva encontrar el periodo de calcio,
             * debe ser muy similar al transitorio de toda la red
             */

            /*
             * En el momento en que son desiguales
             * los valores del calcio
             * se toma el valor de t
             */
            while (t > 6) {
                if ((trj[t][N - 1] == trj[t - 4][N - 1])
                        && (trj[t - 1][N - 1] == trj[t - 1 - 4][N - 1])
                        && (trj[t - 2][N - 1] == trj[t - 2 - 4][N - 1])
                        && (trj[t - 3][N - 1] == trj[t - 3 - 4][N - 1]) //                        ||
                        //                        (trj[t][r.getNodo(21).getS()]==trj[t-8][r.getNodo(21).getS()])
                        //                        &&
                        //                        (trj[t-1][r.getNodo(21).getS()]==trj[t-1-8][r.getNodo(21).getS()])
                        //                        &&
                        //                        (trj[t-2][r.getNodo(21).getS()]==trj[t-2-8][r.getNodo(21).getS()])
                        //                        &&
                        //                        (trj[t-3][r.getNodo(21).getS()]==trj[t-1-3-8][r.getNodo(21).getS()])
                        //                        &&
                        //                        (trj[t-4][r.getNodo(21).getS()]==trj[t-8][r.getNodo(21).getS()])
                        //                        &&
                        //                        (trj[t-5][r.getNodo(21).getS()]==trj[t-1-8][r.getNodo(21).getS()])
                        //                        &&
                        //                        (trj[t-6][r.getNodo(21).getS()]==trj[t-2-8][r.getNodo(21).getS()])
                        //                        &&
                        //                        (trj[t-7][r.getNodo(21).getS()]==trj[t-1-3-8][r.getNodo(21).getS()])
                        ) {
                    t--;
                } else {
                    /*
                     * Si el atractor inicia en la condición 02
                     */
                    if (trj[t - 6][N - 1] == 0 && trj[t - 5][N - 1] == 2) {
                        fw02.writeString(t - 6 + "\n");
                    } /*
                     * Si el atractor inicia en la condición 11
                     */ else if (trj[t - 6][N - 1] == 1 && trj[t - 5][N - 1] == 1) {
                        fw11.writeString(t - 6 + "\n");
                    } /*
                     * Si el atractor inicia en la condición 21
                     */ else if (trj[t - 6][N - 1] == 2 && trj[t - 5][N - 1] == 1) {
                        fw21.writeString(t - 6 + "\n");
                    } /*
                     * Si el atractor inicia en la condición 10
                     */ else if (trj[t - 6][N - 1] == 1 && trj[t - 5][N - 1] == 0) {
                        fw10.writeString(t - 6 + "\n");
                    } else {
                        fw.writeString(" " + (t - 6) + "\t" + (t - 5));
                    }
//                    System.out.println(t-4 + " caso raro: " );
                    Trans = t - 4;
                    break;
                }
            }
            TransProm += Trans;
            fr.nextLine();
        }
        fr.close();
        fw02.close();
        fw21.close();
        fw11.close();
        fw10.close();
        fw.close();
        System.out.println("\n\n" + TransProm / iC);
    }

    /**
     * Genera un patrón temporal de la evolución del calcio
     * mediante un promedio de muchas configuraciones iniciales,
     * y corriendo 10 pasos de tiempo la ventana para suavizar
     * la señal. Con todos los nodos presentes, quitando 1 nodo y
     * quitando 2 nodos.
     * La parte nueva es que ahora catsper se activa independientemente, simulando
     * el efecto del NFA al sobreactivarlo.
     * Se activa dependiendo del módulo del entero pasoAct
     * @param r1 La red que se va a evolucionar
     * @param iC las condiciones iniciales a promediar
     * @param pasoAct Entero que indica cada cuántos pasos se activa catsper
     */
    public static int calciumEvolutionCatsper26jul(Red r1, int iC, int pasoAct, boolean es) {

        String file;
        FileToWrite fw;

        //        int R = 10000;
        int T = 1200;
//for (int cur = 0; cur < r1.getSize(); ++cur) {
//        for (int n = 0; n < r1.getSize(); ++n) {
        for (int m = 0; m < r1.getSize(); ++m) {
            r1.getNodo(m).setPresente(true);
        }
        System.out.println("voy en el nodo " + r1.getNodo(20).getName());
        r1.getNodo(8).setPresente(false);
        r1.getNodo(16).setPresente(false);
//        r1.getNodo(20).setPresente(false);

        int[][] cals = new int[T][iC];
        int[][] trjs = new int[iC][T];
        double[] P = new double[T];
        double[] S = new double[T];

        int s;

        for (int r = 0; r < iC; ++r) {
            r1.setRandomStates();
//            s = (Math.random() < 0.5) ? 1 : 0;
//            r1.getNodo(21).setS(s);


            for (int t = 0; t < T; ++t) {
                trjs[r][t] += r1.getNodo(21).getS();

                r1.evolveKauffman();
//                if (t % pasoAct == 1) {
//                    r1.getNodo(17).setS(1);
//                }
//                r1.getNodo(20).setS(1);
//                if(t%pasoAct==1){
//                r1.getNodo(17).setS(0);
//                r1.getNodo(20).setS(0);
//                }
                if (es == true) {
                    s = (Math.random() < (pasoAct / 10.0)) ? r1.getNodo(17).getS() : 1;
                    r1.getNodo(17).setS(s);
                    int ti = (Math.random() < (pasoAct / 10.0)) ? r1.getNodo(20).getS() : 1;
                    r1.getNodo(20).setS(ti);
                }


                cals[t][r] = r1.getNodo(21).getS();
                System.out.print(" " + cals[t][r]);
            }
            System.out.println();
        }
        for (int t = 0; t < T; ++t) {
            for (int r = 0; r < iC; ++r) {
                P[t] += trjs[r][t];
            }
            P[t] /= (1.0 * iC);
            for (int r = 0; r < iC; ++r) {
                S[t] += ((trjs[r][t] - P[t]) * (trjs[r][t] - P[t]));
            }
            S[t] /= (1.0 * iC);
            S[t] = Math.sqrt(S[t]);
        }
//        double x, y;
//        int V = 4;
//            for (int t = 0; t < (T - V); ++t) {
//                x = 0;
//                y = 0;
//                for (int tv = 0; tv < V; ++tv) {
//                    x += P[t + tv];
//                    y += S[t + tv];
//                }
//                P[t] = x / V;
//                S[t] = y / V;
//            }
//            file = "Curvas/curvaSin_" + r1.getNodo(n).getName() + ".dat";

        //       file = "CurvasTodosWT/" + r1.getNodo(cur).getName() + ".dat";

        file = "catsper/26JulPruebas/ActivaCatsper/catsperActivo90_"
                //                + pasoAct
                //                //        file = "CurvasNoAtenuadas/wt3_ValeLVA"
                + "_" + r1.getNodo(8).getName()
                + "_" + r1.getNodo(16).getName()
                + "_" + r1.getNodo(20).getName()
                + "CHINO.dat";
        fw = new FileToWrite(file);
        for (int t = 0; t < T; ++t) {
//            fw.writeLine(t + "\t\t" + P[t] + "\t\t" + S[t]);
            fw.writeLine(t + "\t\t" + P[t]);
        }
        fw.close();
//        }
//}
        return T;

    }

    /**
     * Este metodo calcula la mayoría de parametros que se buscan
     * del modelo del erizo. Se calcula de una sola vez todo lo que
     * se puede:
     * Media,pico, amplitud y frecuencia de la dinamica del calcio.
     * También calcula los valores del análisis de atractores.
     * Todo lo imprime en pantalla pero puede imprimir en archivo
     * @param r1 La red
     * @param iC Número de condiciones iniciales
     * @param nod8 Valor que indica si HCN estará o no presente (1 = presente)
     * @param nod16 Valor que indica si CaCC estará o no presente
     * @param nod20 Valor que indica si CaKC estará o no presente
     * @param catsperAct Valor que indica si se sobreactiva o no catsper
     * @param CaKCact Valor que indica si se sobreactiva o no CaKC
     * @param pasoAct Valordel porcentaje de sobreactivacion
     * @param es Booleano que indica si se sobreexpresa al 100% o no algun nodo.
     *           Si no se sobreexpresa, con una probabilidad expresa su valor de la tabla (false = 100%)
     * @return
     */
    public static int TodoDeTodo(Red r1, int iC, int nod8, int nod16, int nod20, int catsperAct, int CaKCact, int pasoAct, boolean es) {
        String file;
        FileToWrite fw;
        int T = 1200;
        /*
         * Primero preguntamos los nodos que se eliminan
         */
        for (int m = 0; m < r1.getSize(); ++m) {
            r1.getNodo(m).setPresente(true);
        }
//        if(nod8+nod16+nod20 ==3){
//            System.out.print("WT\t");
//        }
        if (nod8 == 0) {
            r1.getNodo(8).setPresente(false);
//            System.out.println(r1.getNodo(8).getName() + " ");
        }
        if (nod16 == 0) {
            r1.getNodo(16).setPresente(false);
//            System.out.println(r1.getNodo(16).getName() + " ");
        }
        if (nod20 == 0) {
            r1.getNodo(20).setPresente(false);
//            System.out.println(r1.getNodo(20).getName() + " ");
        }




        int[][] trjs = new int[iC][T];
        int[][] trjRed = new int[T][r1.getSize()];
        double[] P = new double[T];
        double[] S = new double[T];

        int s;

        int N = r1.getSize();
        int l, k, t;
        int longTrans = 0;
        boolean busca, ya;
        Attractor a;
        /*
         * Evolucionamos la red, preguntando si
         * hay nodos que se sobreexpresan
         */
        for (int r = 0; r < iC; ++r) {
            r1.setRandomStates();
            r1.getNodo(21).setS(0);
            for (t = 0; t < T; ++t) {
                trjs[r][t] += r1.getNodo(21).getS();

                r1.evolveKauffman();

                if (catsperAct == 0) {
                    if (es == true) {
                        s = (Math.random() < (pasoAct / 10.0)) ? r1.getNodo(17).getS() : 1;
                        r1.getNodo(17).setS(s);
                    } else {
                        r1.getNodo(17).setS(1);
                    }
                }

                if (CaKCact == 0) {

                    if (es == true) {
                        int ti = (Math.random() < (pasoAct / 10.0)) ? r1.getNodo(20).getS() : 1;
                        r1.getNodo(20).setS(ti);
                    } else {
                        r1.getNodo(20).setS(1);
                    }
                }
                trjRed[t] = r1.getStates();
            }

            t = T - 2;
            busca = true;
            l = 0;
            while ((busca) && (t > 0)) {
                if (arrayEquals(trjRed[t], trjRed[T - 1])) {
                    busca = false;
                    l = (T - 1) - t;
                } else {
                    --t;
                }
            }
            if (l > 0) {
                a = new Attractor(l, N);
                for (k = 0; k < l; ++k) {
                    t = T - 1 - l + k;
                    a.addState(trjRed[t], k);
                }
                if (r1.getNumAttractors() == 0) {
                    r1.addAttractor(a);
                } else {
                    ya = false;
                    for (k = 0; k < r1.getNumAttractors(); ++k) {
                        if (r1.getAttractor(k).equals(a)) {
                            r1.getAttractor(k).ppBasin();
                            ya = true;
                            break;
                        }
                    }
                    if (!ya) {
                        r1.addAttractor(a);
                    }
                }
            } else {
                ++longTrans;
            }

        }



        /*
         * Ahora obtenemos los valores de:
         * media, pico, amplitud y frecuencia
         */
        double media = 0;
        double pico = 0;
        double amp = 0;
        double maxAmp = 0;
        double minAmp = 0;
        double freq = 0;
        double mean = 0;

        for (t = 500; t < T; ++t) {
            for (int r = 0; r < iC; ++r) {
                P[t] += trjs[r][t];
                media += trjs[r][t];
            }

            P[t] /= (1.0 * iC);
            for (int r = 0; r < iC; ++r) {
                S[t] += ((trjs[r][t] - P[t]) * (trjs[r][t] - P[t]));
            }
            S[t] /= (1.0 * iC);
            S[t] = Math.sqrt(S[t]);
            mean = media / (700 * iC);
        }

        /*
         * Para el pico, dentro del atractor, ordenamos un
         * arreglo, el mayor de ese arreglo será el pico
         * máximo. Tal vez valga la pena hacer un +-
         */
        for (int i = 1199; i > 500; --i) {
            for (int j = i - 1; j > 500; --j) {



                /*
                 * para el pico máximo y la amplitud
                 */
                if (P[i] == P[j]) {
                    double[] sorted = new double[i - j + 1];
                    double[] amps = new double[i - j + 1];



                    for (int m = 0; m < sorted.length; ++m) {
                        amps[m] = Math.abs(P[i - m] - P[i - m - 1]);
                        amp += amps[m];
                        sorted[m] = P[1199 - m];
                    }
                    Arrays.sort(sorted);
                    Arrays.sort(amps);
                    minAmp = amps[0];
                    maxAmp = amps[amps.length - 1];
                    amp /= amps.length;
                    pico = sorted[sorted.length - 1];
                    freq = i - j;
                }
            }
        }
        DecimalFormat df = new DecimalFormat("0.0000");
        System.out.print(df.format(mean) + "\t\t" + df.format(pico) + "\t\t" + df.format(amp)
                + "\t\t" + df.format(maxAmp) + "\t\t" + df.format(minAmp) + "\t\t" + (int) freq + "\t\t");
        return longTrans;
    }

    public static double[] ordenacionShell(double[] v) {
        final int N = v.length;
        int incremento = N;
        do {
            incremento = incremento / 2;
            for (int k = 0; k < incremento; k++) {
                for (int i = incremento + k; i < N; i += incremento) {
                    int j = i;
                    while (j - incremento >= 0 && v[j] < v[j - incremento]) {
                        double tmp = v[j];
                        v[j] = v[j - incremento];
                        v[j - incremento] = tmp;
                        j -= incremento;
                    }
                }
            }
        } while (incremento > 1);
//        for(int a = 0; a < v.length; ++a){
//        System.out.println("" + v[a]);
//        }
        return v;
    }

    public static double[] calciumEvolBlocking(Red r1, int iC, int block, String file) {
        FileToWrite fw;
        int T = 1200;
        for (int m = 0; m < r1.getSize(); ++m) {
            r1.getNodo(m).setPresente(true);
        }
        r1.getNodo(block).setPresente(false);
        int[][] trjs = new int[iC][T];
        double[] P = new double[T];
        double[] S = new double[T];
        for (int r = 0; r < iC; ++r) {
            r1.setRandomStates();
            for (int t = 0; t < T; ++t) {
                trjs[r][t] += r1.getNodo(21).getS();
                r1.evolveKauffman();
            }
        }
        for (int t = 0; t < T; ++t) {
            for (int r = 0; r < iC; ++r) {
                P[t] += trjs[r][t];
            }
            P[t] /= (1.0 * iC);
            for (int r = 0; r < iC; ++r) {
                S[t] += ((trjs[r][t] - P[t]) * (trjs[r][t] - P[t]));
            }
            S[t] /= (1.0 * iC);
            S[t] = Math.sqrt(S[t]);
        }
        fw = new FileToWrite(file);
        for (int t = 1000; t < T; ++t) {
            fw.writeLine(t + "\t\t" + P[t]);
        }
        fw.close();
        double[] Q = new double[200];
        for (int i = 1000; i < 1200; ++i) {
            Q[i - 1000] = P[i];
        }
        return Q;
    }

    /**
     * De algún modo mágico hace la correlación de pearson o sperman
     * con la librería jsc.*
     * @param x Arreglo con la dinamica de calcio
     * @param y Otro arreglo de la dinamica
     * @return El indice de correlacion o R
     */
    public static double Correlacion(Red r, int iC, double SteadyStates[][]) {
//        FileToWrite fw = new FileToWrite("catsper/Enero13/CaKCCambiado_CorrelacionesMatriz3D.dat");
//        FileToWrite fw = new FileToWrite("catsper/CatsperFullChannels/CorrelacionesMatriz3D.dat");
        FileToWrite fw = new FileToWrite("catsper/Gordon/TCatsperAD/CorrelacionesMatriz.dat");
//        SteadyStates = calciumEvolution(r, iC);
        double[][] st =  new double[r.getSize()+1][200];
        FileToRead fr0 =  new FileToRead("catsper/Gordon/TCatsperAD/curvas/wt.dat");
        for(int n = 0; n < 800; ++n){
            fr0.nextLine();
        }
        for(int m = 0; m < 200; ++m){
                fr0.nextInt();
                    st[0][m] = fr0.nextDouble();
        }
            
            fr0.close();
        
            for(int n = 1; n <=r.getSize(); ++n){
                
                
            FileToRead fr =  new FileToRead("catsper/Gordon/TCatsperAD/curvas/sin" + r.getNodo(n-1).getName() + ".dat");
            System.out.println("nodo "+ (n) + " es "  + r.getNodo(n-1).getName());
            
            for(int nn = 0; nn < 800; ++nn){
            fr.nextLine();
        }
            for(int m = 0; m < 200; ++m){
            
            
            fr.nextInt();
            st[n][m] = fr.nextDouble(); 
        }
            
            fr.close();
        }
            DecimalFormat df = new DecimalFormat("0.000000000000000");
        double[] x = new double[200];
        double[] y = new double[200];
        for (int n = 0; n < r.getSize() + 1; ++n) {
            for (int m = 0; m < r.getSize() + 1; ++m) {
//                x = SteadyStates[n];
//                y = SteadyStates[m];
                x = st[n];
                y = st[m];
                PairedData xy = new PairedData(x, y);
                PearsonCorrelation sd = new PearsonCorrelation(xy);
               

                    fw.writeLine(n + "\t" + m + "\t" + df.format(sd.getR()) );
                
//                System.out.println("correlacion entre " + n + " y " + m + " es: " + sd.getR());
           
            
            }
//            fw.writeLine();
        }
        fw.close();
        return 0;
    }

    public static int TodoCatsper(Red r1, Red r2, int iC, boolean todos, int nod8, int nod16, int nod20,
            int catsperAct, int CaKCact, int pasoAct, boolean es, int renglon, int chNode, String aGuardar, int numTablas) {
        int lt = 0;
        double[] calcioWT = new double[200];
        double[][] sinUno = new double[r1.getSize()][200];
        double[] pearsons = new double[r1.getSize()];
        if (todos == true) {
            System.out.println("NumTablas es " + numTablas + " y todos es " + todos);
            aGuardar = ("/home/chucho/Desktop/ModeloCatsper/deTablas" + numTablas + "/Curvas/WT.dat");
            String ats = new String("/home/chucho/Desktop/ModeloCatsper/deTablas" + numTablas + "/Atractores/WT.dat");
            String derr = new String("/home/chucho/Desktop/ModeloCatsper/deTablas" + numTablas + "/Hamming/WT.dat");
//            System.out.println("Evoluciono calcio");
            calcioWT = calciumEvolutionCatsper(r1, iC, aGuardar);
//            System.out.println("encuentro y guardo los atractores");
            lt = findAttUnder(r1, iC);
            r1.saveAttractors(ats, lt);
//            System.out.println("Hago el mapeo de derrida\n\n");
            derridaMap(r1, r2, derr);
        } else {
            System.out.println("NumTablas es " + numTablas + " y todos es " + todos);
            for (int b = 0; b < r1.getSize(); ++b) {
//                System.out.println("Quito el nodo " + r1.getNodo(b).getName());
                aGuardar = ("/home/chucho/Desktop/ModeloCatsper/deTablas" + numTablas + "/Curvas/Sin_" + r1.getNodo(b).getName() + ".dat");
                String atsSin = new String("/home/chucho/Desktop/ModeloCatsper/deTablas" + numTablas + "/Atractores/Sin_" + r1.getNodo(b).getName() + ".dat");
                String derrSin = new String("/home/chucho/Desktop/ModeloCatsper/deTablas" + numTablas + "/Hamming/Sin_" + r1.getNodo(b).getName() + ".dat");
//                System.out.println("Evoluciono calcio");
                sinUno[b] = calciumEvolBlocking(r1, iC, b, aGuardar);
//                System.out.println("encuentro y guardo los atractores");
                lt = findAttUnderSinUno(r1, iC, b);
                r1.saveAttractors(atsSin, lt);
//                System.out.println("Hago el mapeo de derrida\n\n");
                derridaMap2(r1, r2, b, derrSin);
//                pearsons[b] = Correlacion(calcioWT, sinUno[b]);
//                System.out.print(pearsons[b]);
            }
//            System.out.println();
        }

        /*
         * Ora esto es para encontrar la correlacion
         * entre curvas con un nodo eliminado, se hace la
         * correlacion de pearson entre esas curvas
         */
//        FileToWrite fw = new FileToWrite("/home/chucho/Desktop/ModeloCatsper/PearsonEntreBloqueados.dat");
//        for(int i = 0; i < r1.getSize(); ++i ){
//            for(int j = i + 1; j < r1.getSize(); ++j ){
//                fw.writeLine(r1.getNodo(i).getName()+"\t"+r1.getNodo(j).getName()+"\t"+Correlacion(sinUno[i], sinUno[j]));
//                System.out.println("Correlacion entre " +r1.getNodo(i).getName()+"\t"+r1.getNodo(j).getName()+"\t" + Correlacion(sinUno[i], sinUno[j]));
//            }
//        }
//        fw.close();


        /*
         * Pal Switch
         */
        int N = r1.getSize();
        for (int n = 0; n < N; ++n) {
//            System.out.println("\nEste es el nodo  " + n + ": " + r1.getNodo(n).getName());
//
//            // Ahora comenzara a leer los datos del archivo renglones
            String fileName = "catsper/renglonesCatsper/" + n + ".txt";
            FileToRead fr = new FileToRead(fileName);
            while (fr.hasNext()) {
                int CompRenglon = fr.nextInt();
                int l = findAttUnderSwitch(r1, iC, CompRenglon, n);
//                int l = MetodosMayo12.findAttAllSwitch(red, CompRenglon, n);
//                System.out.println("voy en el renglon " + CompRenglon);
                System.out.println();
////                red.saveAttractors("Complete/at/"
////                         red.saveAttractors("Atractores22Sep11/at/"
////                        + n +"/"
////                        + "_" + CompRenglon + ".txt", l);


                r1.saveAttractorsLandscape("/home/chucho/Desktop/ModeloCatsper/Atractores/Switch/at/"
                        + n + "/"
                        + "_" + CompRenglon + ".dat", l, CompRenglon, n);

            }
            fr.close();
        }

        return lt;
    }

    public static int[][] MapeoChistoso(Red r1, int iC) {
        int N = r1.getSize();
        int tMax = 300;
        int[][] trj;
        int t;
        int[][] ternario = new int[tMax][2];
        for (int ka = 0; ka < N; ++ka) {
            r1.getNodo(ka).setPresente(true);
        }
        for (int ic = 0; ic < iC; ++ic) {
            FileToWrite fw = new FileToWrite("map_" + iC);
            r1.setRandomStates();
            trj = new int[tMax][N];
            for (t = 0; t < tMax; ++t) {
                r1.evolveKauffman();
                trj[t] = r1.getStates();
                for (int i = 0; i < r1.getSize(); ++i) {
                    ternario[t][0] += (int) (trj[t][i] * Math.pow(3, i));
                }
                if (t > 0) {
                    for (int i = 0; i < r1.getSize(); ++i) {
                        ternario[t][1] += (int) (trj[t - 1][i] * Math.pow(3, i));
                    }
                }

            }
            ternario[tMax - 1][0] = ternario[tMax - 1][1] = 0;
            for (int i = 0; i < tMax; ++i) {
                fw.writeLine(ternario[i][0] + "\t" + ternario[i][1]);
            }
        }
        return ternario;
    }

    public static double[] todoCoupledItalia(
            Red r, Red r2, Red r3,
            Red r4,
            Red r5,
            Red r6,
            Red r7,
            Red r8,
            Red r9,
            Red r10,
            //            Red r11, 
            //            Red r12, 
            //            Red r13, 
            //            Red r14, 
            //            Red r15, 
            //            Red r16,
            //            Red r17, 
            //            Red r18, 
            //            Red r19, 
            //            Red r20,
            //             Red r21, 
            //            Red r22, 
            //            Red r23, 
            //            Red r24, 
            //            Red r25, 
            //            Red r26,
            //            Red r27, 
            //            Red r28, 
            //            Red r29, 
            //            Red r30,
            int iC, double eps, double div, double rounder2, double rounder, int nodoAcoplador) {
        int N = r.getSize();
        int tMax = 50;
        int[][] GranTrj = new int[tMax][N * 30];
        double epsilon;
        epsilon = eps;
        double[] calciote = new double[tMax];
        int[][] calcio;
        double[] sumota = new double[2];
        calcio = new int[tMax][30];
        int t;
        int[] transi = new int[2];
//        FileToWrite fw1 = new FileToWrite("ItalianCoupling/NumRedesVSTransitorio/PruebaCouplingIsOK/Italia_10Redes_TransCalcio_" + epsilon + "_.dat");
//        FileToWrite fw2 = new FileToWrite("ItalianCoupling/NumRedesVSTransitorio/PruebaCouplingIsOK/Italia_10Redes_TransFlagelo_" + epsilon + "_.dat");

        int[][] trj;
        int[][] trj2;
        int[][] trj3;
        int[][] trj4;
        int[][] trj5;
        int[][] trj6;
        int[][] trj7;
        int[][] trj8;
        int[][] trj9;
        int[][] trj10;
        int[][] trj11;
        int[][] trj12;
        int[][] trj13;
        int[][] trj14;
        int[][] trj15;
        int[][] trj16;
        int[][] trj17;
        int[][] trj18;
        int[][] trj19;
        int[][] trj20;
        int[][] trj21;
        int[][] trj22;
        int[][] trj23;
        int[][] trj24;
        int[][] trj25;
        int[][] trj26;
        int[][] trj27;
        int[][] trj28;
        int[][] trj29;
        int[][] trj30;
        trj = new int[tMax][N];
        trj2 = new int[tMax][N];
        trj3 = new int[tMax][N];
        trj4 = new int[tMax][N];
        trj5 = new int[tMax][N];
        trj6 = new int[tMax][N];
        trj7 = new int[tMax][N];
        trj8 = new int[tMax][N];
        trj9 = new int[tMax][N];
        trj10 = new int[tMax][N];
        trj11 = new int[tMax][N];
        trj12 = new int[tMax][N];
        trj13 = new int[tMax][N];
        trj14 = new int[tMax][N];
        trj15 = new int[tMax][N];
        trj16 = new int[tMax][N];
        trj17 = new int[tMax][N];
        trj18 = new int[tMax][N];
        trj19 = new int[tMax][N];
        trj20 = new int[tMax][N];
        trj21 = new int[tMax][N];
        trj22 = new int[tMax][N];
        trj23 = new int[tMax][N];
        trj24 = new int[tMax][N];
        trj25 = new int[tMax][N];
        trj26 = new int[tMax][N];
        trj27 = new int[tMax][N];
        trj28 = new int[tMax][N];
        trj29 = new int[tMax][N];
        trj30 = new int[tMax][N];

        for (int ic = 0; ic < iC; ++ic) {
            r.setRandomStates();
            r2.setRandomStates();
            r3.setRandomStates();
            r4.setRandomStates();
            r5.setRandomStates();
            r6.setRandomStates();
            r7.setRandomStates();
            r8.setRandomStates();
            r9.setRandomStates();
            r10.setRandomStates();
//            r11.setRandomStates();
//            r12.setRandomStates();
//            r13.setRandomStates();
//            r14.setRandomStates();
//            r15.setRandomStates();
//            r16.setRandomStates();
//            r17.setRandomStates();
//            r18.setRandomStates();
//            r19.setRandomStates();
//            r20.setRandomStates();
//            r21.setRandomStates();
//            r22.setRandomStates();
//            r23.setRandomStates();
//            r24.setRandomStates();
//            r25.setRandomStates();
//            r26.setRandomStates();
//            r27.setRandomStates();
//            r28.setRandomStates();
//            r29.setRandomStates();
//            r30.setRandomStates();

            calcio[0][0] = trj[0][N - 1];
            calcio[0][1] = trj2[0][N - 1];
            calcio[0][2] = trj3[0][N - 1];
            calcio[0][3] = trj4[0][N - 1];
            calcio[0][4] = trj5[0][N - 1];
            calcio[0][5] = trj6[0][N - 1];
            calcio[0][6] = trj7[0][N - 1];
            calcio[0][7] = trj8[0][N - 1];
            calcio[0][8] = trj9[0][N - 1];
            calcio[0][9] = trj10[0][N - 1];
            calcio[0][10] = trj11[0][N - 1];
            calcio[0][11] = trj12[0][N - 1];
            calcio[0][12] = trj13[0][N - 1];
            calcio[0][13] = trj14[0][N - 1];
            calcio[0][14] = trj15[0][N - 1];
            calcio[0][15] = trj16[0][N - 1];
            calcio[0][16] = trj17[0][N - 1];
            calcio[0][17] = trj18[0][N - 1];
            calcio[0][18] = trj19[0][N - 1];
            calcio[0][19] = trj20[0][N - 1];
            calcio[0][20] = trj21[0][N - 1];
            calcio[0][21] = trj22[0][N - 1];
            calcio[0][22] = trj23[0][N - 1];
            calcio[0][23] = trj24[0][N - 1];
            calcio[0][24] = trj25[0][N - 1];
            calcio[0][25] = trj26[0][N - 1];
            calcio[0][26] = trj27[0][N - 1];
            calcio[0][27] = trj28[0][N - 1];
            calcio[0][28] = trj29[0][N - 1];
            calcio[0][29] = trj30[0][N - 1];

            for (t = 1; t < tMax; ++t) {

                r.evolveKauffmanCoupled(r10, r, r2, epsilon, div, 1, rounder2, rounder, nodoAcoplador);
                r2.evolveKauffmanCoupled(r, r2, r3, epsilon, div, 2, rounder2, rounder, nodoAcoplador);
                r3.evolveKauffmanCoupled(r2, r3, r4, epsilon, div, 3, rounder2, rounder, nodoAcoplador);
                r4.evolveKauffmanCoupled(r3, r4, r5, epsilon, div, 4, rounder2, rounder, nodoAcoplador);
                r5.evolveKauffmanCoupled(r4, r5, r6, epsilon, div, 5, rounder2, rounder, nodoAcoplador);
                r6.evolveKauffmanCoupled(r5, r6, r7, epsilon, div, 6, rounder2, rounder, nodoAcoplador);
                r7.evolveKauffmanCoupled(r6, r7, r8, epsilon, div, 7, rounder2, rounder, nodoAcoplador);
                r8.evolveKauffmanCoupled(r7, r8, r9, epsilon, div, 8, rounder2, rounder, nodoAcoplador);
                r9.evolveKauffmanCoupled(r8, r9, r10, epsilon, div, 9, rounder2, rounder, nodoAcoplador);
                r10.evolveKauffmanCoupled(r9, r10, r, epsilon, div, 10, rounder2, rounder, nodoAcoplador);
//                r11.evolveKauffmanCoupled(r10, r11, r12, epsilon, div, 1, rounder2, rounder, nodoAcoplador);
//                r12.evolveKauffmanCoupled(r11, r12, r13, epsilon, div, 2, rounder2, rounder, nodoAcoplador);
//                r13.evolveKauffmanCoupled(r12, r13, r14, epsilon, div, 3, rounder2, rounder, nodoAcoplador);
//                r14.evolveKauffmanCoupled(r13, r14, r15, epsilon, div, 4, rounder2, rounder, nodoAcoplador);
//                r15.evolveKauffmanCoupled(r14, r15, r16, epsilon, div, 5, rounder2, rounder, nodoAcoplador);
//                r16.evolveKauffmanCoupled(r15, r16, r17, epsilon, div, 6, rounder2, rounder, nodoAcoplador);
//                r17.evolveKauffmanCoupled(r16, r17, r18, epsilon, div, 7, rounder2, rounder, nodoAcoplador);
//                r18.evolveKauffmanCoupled(r17, r18, r19, epsilon, div, 8, rounder2, rounder, nodoAcoplador);
//                r19.evolveKauffmanCoupled(r18, r19, r20, epsilon, div, 9, rounder2, rounder, nodoAcoplador);
//                r20.evolveKauffmanCoupled(r19, r20, r21, epsilon, div, 10, rounder2, rounder, nodoAcoplador);
//                r21.evolveKauffmanCoupled(r20, r21, r22, epsilon, div, 1, rounder2, rounder, nodoAcoplador);
//                r22.evolveKauffmanCoupled(r21, r22, r23, epsilon, div, 2, rounder2, rounder, nodoAcoplador);
//                r23.evolveKauffmanCoupled(r22, r23, r24, epsilon, div, 3, rounder2, rounder, nodoAcoplador);
//                r24.evolveKauffmanCoupled(r23, r24, r25, epsilon, div, 4, rounder2, rounder, nodoAcoplador);
//                r25.evolveKauffmanCoupled(r24, r25, r26, epsilon, div, 5, rounder2, rounder, nodoAcoplador);
//                r26.evolveKauffmanCoupled(r25, r26, r27, epsilon, div, 6, rounder2, rounder, nodoAcoplador);
//                r27.evolveKauffmanCoupled(r26, r27, r28, epsilon, div, 7, rounder2, rounder, nodoAcoplador);
//                r28.evolveKauffmanCoupled(r27, r28, r29, epsilon, div, 8, rounder2, rounder, nodoAcoplador);
//                r29.evolveKauffmanCoupled(r28, r29, r30, epsilon, div, 9, rounder2, rounder, nodoAcoplador);
//                r30.evolveKauffmanCoupled(r29, r30, r, epsilon, div, 10, rounder2, rounder, nodoAcoplador);

                int voltEfectivo = 0;
                double volt = (r.getNodo(5).getS()
                        + r2.getNodo(5).getS()
                        + r3.getNodo(5).getS()
                        + r4.getNodo(5).getS()
                        + r5.getNodo(5).getS()
                        + r6.getNodo(5).getS()
                        + r7.getNodo(5).getS()
                        + r8.getNodo(5).getS()
                        + r9.getNodo(5).getS()
                        + r10.getNodo(5).getS() / 10.0);
//                        + r11.getNodo(5).getS()
//                        + r12.getNodo(5).getS()
//                        + r13.getNodo(5).getS()
//                        + r14.getNodo(5).getS()
//                        + r15.getNodo(5).getS()
//                        + r16.getNodo(5).getS()
//                        + r17.getNodo(5).getS()
//                        + r18.getNodo(5).getS()
//                        + r19.getNodo(5).getS()
//                        + r20.getNodo(5).getS()
//                        + r21.getNodo(5).getS()
//                        + r22.getNodo(5).getS()
//                        + r23.getNodo(5).getS()
//                        + r24.getNodo(5).getS()
//                        + r25.getNodo(5).getS()
//                        + r26.getNodo(5).getS()
//                        + r27.getNodo(5).getS()
//                        + r28.getNodo(5).getS()
//                        + r29.getNodo(5).getS()
//                        + r30.getNodo(5).getS() 

                if (volt < 0.66) {
                    voltEfectivo = 0;
                } else if (volt >= 0.66 && volt < 1.33) {
                    voltEfectivo = 1;
                } else {
                    voltEfectivo = 2;
                }

                r.getNodo(5).setS(voltEfectivo);
                r2.getNodo(5).setS(voltEfectivo);
                r3.getNodo(5).setS(voltEfectivo);
                r4.getNodo(5).setS(voltEfectivo);
                r5.getNodo(5).setS(voltEfectivo);
                r6.getNodo(5).setS(voltEfectivo);
                r7.getNodo(5).setS(voltEfectivo);
                r8.getNodo(5).setS(voltEfectivo);
                r9.getNodo(5).setS(voltEfectivo);
                r10.getNodo(5).setS(voltEfectivo);
//                r11.getNodo(5).setS(voltEfectivo);
//                r12.getNodo(5).setS(voltEfectivo);
//                r13.getNodo(5).setS(voltEfectivo);
//                r14.getNodo(5).setS(voltEfectivo);
//                r15.getNodo(5).setS(voltEfectivo);
//                r16.getNodo(5).setS(voltEfectivo);
//                r17.getNodo(5).setS(voltEfectivo);
//                r18.getNodo(5).setS(voltEfectivo);
//                r19.getNodo(5).setS(voltEfectivo);
//                r20.getNodo(5).setS(voltEfectivo);
//                r21.getNodo(5).setS(voltEfectivo);
//                r22.getNodo(5).setS(voltEfectivo);
//                r23.getNodo(5).setS(voltEfectivo);
//                r24.getNodo(5).setS(voltEfectivo);
//                r25.getNodo(5).setS(voltEfectivo);
//                r26.getNodo(5).setS(voltEfectivo);
//                r27.getNodo(5).setS(voltEfectivo);
//                r28.getNodo(5).setS(voltEfectivo);
//                r29.getNodo(5).setS(voltEfectivo);
//                r30.getNodo(5).setS(voltEfectivo);

                trj[t] = r.getStates();
                trj2[t] = r2.getStates();
                trj3[t] = r3.getStates();
                trj4[t] = r4.getStates();
                trj5[t] = r5.getStates();
                trj6[t] = r6.getStates();
                trj7[t] = r7.getStates();
                trj8[t] = r8.getStates();
                trj9[t] = r9.getStates();
                trj10[t] = r10.getStates();
//                trj11[t] = r11.getStates();
//                trj12[t] = r12.getStates();
//                trj13[t] = r13.getStates();
//                trj14[t] = r14.getStates();
//                trj15[t] = r15.getStates();
//                trj16[t] = r16.getStates();
//                trj17[t] = r17.getStates();
//                trj18[t] = r18.getStates();
//                trj19[t] = r19.getStates();
//                trj20[t] = r20.getStates();
//                trj21[t] = r21.getStates();
//                trj22[t] = r22.getStates();
//                trj23[t] = r23.getStates();
//                trj24[t] = r24.getStates();
//                trj25[t] = r25.getStates();
//                trj26[t] = r26.getStates();
//                trj27[t] = r27.getStates();
//                trj28[t] = r28.getStates();
//                trj29[t] = r29.getStates();
//                trj30[t] = r30.getStates();

                for (int i = 0; i < N; ++i) {
                    GranTrj[t][(i)] = trj[t][i];
                }
                for (int i = 0; i < N; ++i) {
                    GranTrj[t][(N + i)] = trj2[t][i];
                }
                for (int i = 0; i < N; ++i) {
                    GranTrj[t][(2 * N) + i] = trj3[t][i];
                }
                for (int i = 0; i < N; ++i) {
                    GranTrj[t][(3 * N) + i] = trj4[t][i];
                }
                for (int i = 0; i < N; ++i) {
                    GranTrj[t][(4 * N) + i] = trj5[t][i];
                }
                for (int i = 0; i < N; ++i) {
                    GranTrj[t][(5 * N) + i] = trj6[t][i];
                }
                for (int i = 0; i < N; ++i) {
                    GranTrj[t][(6 * N) + i] = trj7[t][i];
                }
                for (int i = 0; i < N; ++i) {
                    GranTrj[t][(7 * N) + i] = trj8[t][i];
                }
                for (int i = 0; i < N; ++i) {
                    GranTrj[t][(8 * N) + i] = trj9[t][i];
                }
                for (int i = 0; i < N; ++i) {
                    GranTrj[t][(9 * N) + i] = trj10[t][i];
                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(10*N)+i]= trj11[t][i];
//                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(11*N)+i]= trj12[t][i];
//                }
//                 for(int i=0; i < N; ++i){
//                    GranTrj[t][(12*N)+i]= trj13[t][i];
//                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(13*N)+i]= trj14[t][i];
//                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(14*N)+i]= trj15[t][i];
//                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(15*N)+i]= trj16[t][i];
//                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(16*N)+i]= trj17[t][i];
//                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(17*N)+i]= trj18[t][i];
//                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(18*N)+i]= trj19[t][i];
//                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(19*N)+i]= trj20[t][i];
//                }for(int i=0; i < N; ++i){
//                    GranTrj[t][(20*N)+i]= trj21[t][i];
//                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(21*N)+i]= trj22[t][i];
//                }
//                 for(int i=0; i < N; ++i){
//                    GranTrj[t][(22*N)+i]= trj23[t][i];
//                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(23*N)+i]= trj24[t][i];
//                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(24*N)+i]= trj25[t][i];
//                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(25*N)+i]= trj26[t][i];
//                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(26*N)+i]= trj27[t][i];
//                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(27*N)+i]= trj28[t][i];
//                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(28*N)+i]= trj29[t][i];
//                }
//                for(int i=0; i < N; ++i){
//                    GranTrj[t][(29*N)+i]= trj20[t][i];
//                }



                calcio[t][0] = trj[t][N - 1];
                calcio[t][1] = trj2[t][N - 1];
                calcio[t][2] = trj3[t][N - 1];
                calcio[t][3] = trj4[t][N - 1];
                calcio[t][4] = trj5[t][N - 1];
                calcio[t][5] = trj6[t][N - 1];
                calcio[t][6] = trj7[t][N - 1];
                calcio[t][7] = trj8[t][N - 1];
                calcio[t][8] = trj9[t][N - 1];
                calcio[t][9] = trj10[t][N - 1];
//                calcio[t][10] = trj11[t][N - 1];
//                calcio[t][11] = trj12[t][N - 1];
//                calcio[t][12] = trj13[t][N - 1];
//                calcio[t][13] = trj14[t][N - 1];
//                calcio[t][14] = trj15[t][N - 1];
//                calcio[t][15] = trj16[t][N - 1];
//                calcio[t][16] = trj17[t][N - 1];
//                calcio[t][17] = trj18[t][N - 1];
//                calcio[t][18] = trj19[t][N - 1];
//                calcio[t][19] = trj20[t][N - 1]; 
//                calcio[t][20] = trj21[t][N - 1];
//                calcio[t][21] = trj22[t][N - 1];
//                calcio[t][22] = trj23[t][N - 1];
//                calcio[t][23] = trj24[t][N - 1];
//                calcio[t][24] = trj25[t][N - 1];
//                calcio[t][25] = trj26[t][N - 1];
//                calcio[t][26] = trj27[t][N - 1];
//                calcio[t][27] = trj28[t][N - 1];
//                calcio[t][28] = trj29[t][N - 1];
//                calcio[t][29] = trj30[t][N - 1];
                for (int i = 0; i < 10; i++) {
                    calciote[t] += calcio[t][i];
                }
                stop:
                for (int m = 0; m < tMax - 1; ++m) {
                    for (int h = m + 1; h < tMax; ++h) {
                        if (arrayEquals(calcio[m], calcio[h])) {
                            transi[0] = h;
                            m = tMax;
                            continue stop;
                        }
                    }
                }

                stop2:
                for (int m = 0; m < tMax - 1; ++m) {
                    for (int h = m + 1; h < tMax; ++h) {
                        if (arrayEquals(GranTrj[m], GranTrj[h])) {
//                            for(int a = 0; a<GranTrj[m].length; ++a){
//                                System.out.print("" + GranTrj[m][a]);
//                            }
//                            System.out.println();
                            transi[1] = h;
                            m = tMax;
                            continue stop2;
                        }
                    }
                }
            }
//            fw1.writeLine(eps + "\t" + transi[0]);
//            fw2.writeLine(eps + "\t" + transi[1]);
            sumota[0] += transi[0];
            sumota[1] += transi[1];
        }
//        fw1.close();
//        fw2.close();
        sumota[0] = sumota[0] / iC;
        sumota[1] = sumota[1] / iC;
        return sumota;
    }

    /**
     * Da el valor del calcio acoplando "N" numero de redes
     * decidiendo el valor del calcio por criterio de
     * regla de la mayoria. Si más de la mitad de los
     * nodos vecinos tienen el mismo valor, entonces el nodo
     * central adquiere ese valor, o al menos se acerca en 
     * una unidad, si es que estaba a 2 del valor de la mayoria.
     * @param n1 nodo que lee sus reglas
     * @param n2 el nodo central
     * @param n3 nodo de la derecha
     */
    public static void evolveReglaMayoria(Nodo[] n1, Nodo[] n2, Nodo[] n3) {

        int N = n1.length;                          //N tiene el tamaÃ±o de la red
        int[] nuevoEstado1 = new int[N];             //vector de tamaÃ±o N donde guarda
        int[] nuevoEstado2 = new int[N];             //para cada nodo
        int[] nuevoEstado3 = new int[N];             //el estado nuevo (o sea t+1)
        int ifr, ifr2, ifr3;
        int i, m, n, nr, nr2, nr3;
        int[] nt;
        int[] nt2;
        int[] nt3;
        int numNodos = 3;

        int M = 0;
        ArrayList<Nodo[]> nodo = new ArrayList<Nodo[]>();
        for (int j = 0; j < numNodos; ++j) {
            nodo.add(j, n1);
            M = nodo.get(j).length;
        }
        int[][] nuevosEstados = new int[numNodos][M];



        //Creamos el vector donde se almacena temporalmente
        //y se juega con los valores del calcio
        int[] tempCa = new int[3];
        for (n = 0; n < N; ++n) {               //for para cada nodo de la red
            nr = n1[n].getNumReg();            //"nr" guarda el nÃºmero de reguladores del nodo N
            nr2 = n2[n].getNumReg();           // dependiendo de la red que va a evolucionar
            nr3 = n3[n].getNumReg();           //En este caso evolucionan 3 redes y se acoplan
            nt = new int[nr];                  //"nt" vector que guarda el nÃºm d regs. de N
            nt2 = new int[nr2];
            nt3 = new int[nr3];

            //Para cada uno de los 3 nodos
            //obtenemos el estado de sus reguladores
            for (m = 0; m < nr; ++m) {
                i = n1[n].getReg(m);
                nt[m] = n1[i].getS();
            }
            for (m = 0; m < nr2; ++m) {
                i = n2[n].getReg(m);
                nt2[m] = n2[i].getS();
            }
            for (m = 0; m < nr3; ++m) {
                i = n3[n].getReg(m);
                nt3[m] = n3[i].getS();
            }

            //para cada nodo obtenemos la
            //representaciÃ³n entera de su ternario
            ifr = ternaryToInt(nt);
            ifr2 = ternaryToInt(nt2);
            ifr3 = ternaryToInt(nt3);
            nuevoEstado1[n] = n1[n].getFuncVal(ifr);
            nuevoEstado2[n] = n2[n].getFuncVal(ifr2);
            nuevoEstado3[n] = n3[n].getFuncVal(ifr3);
        }
        tempCa[0] = nuevoEstado1[N - 1];
        tempCa[1] = nuevoEstado2[N - 1];
        tempCa[2] = nuevoEstado3[N - 1];
        int cuenta0 = 0;
        int cuenta1 = 0;
        int cuenta2 = 0;
        for (int a = 0; a < numNodos; ++a) {

            if (tempCa[a] == 0) {
                cuenta0++;
            } else if (tempCa[a] == 1) {
                cuenta1++;
            } else {
                cuenta2++;
            }

        }
        /*
         * caso mayoria 0
         */
        if ((cuenta0 > (numNodos / 2)) && tempCa[1] != 2) {
            tempCa[1] = 0;
        } else if ((cuenta0 > (numNodos / 2)) && tempCa[1] == 2) {
            tempCa[1] = 0;
        }

        /*
         * Caso mayoria 1
         */
        if (cuenta1 > (numNodos / 2)) {
            tempCa[1] = 1;
        } /*
         * caso mayoria 2
         */ else if ((cuenta2 > (numNodos / 2)) && tempCa[1] != 0) {
            tempCa[1] = 2;
        } else if ((cuenta2 > (numNodos / 2)) && tempCa[1] == 0) {
            tempCa[1] = 1;
        } else {
            tempCa[1] = tempCa[1];
        }
        for (n = 0; n < N; ++n) {
            if (n2[n].isPresent()) {
                n2[n].setS(nuevoEstado2[n]);
            } else {
                if (n2[n].isBinary()) {
                    n2[n].setS(0);
                } else {
                    n2[n].setS(1);
                }
            }
        }
        n2[N - 1].setS(tempCa[1]);
    }

    public static double[] todoCoupledMayoria(Red r, Red r2, Red r3, Red r4, Red r5,
            Red r6,
            Red r7, Red r8, Red r9, Red r10,
            int iC) {
        int N = r.getSize();
        int tMax = 1000;
        int[][] trj;
        int[][] trj2;
        int[][] trj3;
        int[][] trj4;
        int[][] trj5;
        int[][] trj6;
        int[][] trj7;
        int[][] trj8;
        int[][] trj9;
        int[][] trj10;
        int[][] calcio;
        double[] calciote = new double[tMax];
        trj = new int[tMax][N];
        trj2 = new int[tMax][N];
        trj3 = new int[tMax][N];
        trj4 = new int[tMax][N];
        trj5 = new int[tMax][N];
        trj6 = new int[tMax][N];
        trj7 = new int[tMax][N];
        trj8 = new int[tMax][N];
        trj9 = new int[tMax][N];
        trj10 = new int[tMax][N];

        double[] sumota = new double[2];
        calcio = new int[tMax][10];
        int t;

        int[] transi = new int[2];

        FileToWrite fw3 = new FileToWrite("ItalianCoupling/GoodCoupling/mayoria/10redesCalcios.dat");
        FileToWrite fw = new FileToWrite("ItalianCoupling/GoodCoupling/mayoria/10redesTransitorios.dat");
        for (int ic = 0; ic < iC; ++ic) {

            r.setRandomStates();
            r2.setRandomStates();
            r3.setRandomStates();
            r4.setRandomStates();
            r5.setRandomStates();
            r6.setRandomStates();
            r7.setRandomStates();
            r8.setRandomStates();
            r9.setRandomStates();
            r10.setRandomStates();

            calcio[0][0] = trj[0][N - 1];
            calcio[0][1] = trj2[0][N - 1];
            calcio[0][2] = trj3[0][N - 1];
            calcio[0][3] = trj4[0][N - 1];
            calcio[0][4] = trj5[0][N - 1];
            calcio[0][5] = trj6[0][N - 1];
            calcio[0][6] = trj7[0][N - 1];
            calcio[0][7] = trj8[0][N - 1];
            calcio[0][8] = trj9[0][N - 1];
            calcio[0][9] = trj10[0][N - 1];

            for (t = 1; t < tMax; ++t) {
                r.evolveKauffmanMayoria(r10, r, r2);
                r2.evolveKauffmanMayoria(r, r2, r3);
                r3.evolveKauffmanMayoria(r2, r3, r4);
                r4.evolveKauffmanMayoria(r3, r4, r5);
                r5.evolveKauffmanMayoria(r4, r5, r6);
                r6.evolveKauffmanMayoria(r5, r6, r7);
                r7.evolveKauffmanMayoria(r6, r7, r8);
                r8.evolveKauffmanMayoria(r7, r8, r9);
                r9.evolveKauffmanMayoria(r8, r9, r10);
                r10.evolveKauffmanMayoria(r9, r10, r);

                int voltEfectivo = 0;
                double volt = (r.getNodo(5).getS()
                        + r2.getNodo(5).getS()
                        + r3.getNodo(5).getS()
                        + r4.getNodo(5).getS()
                        + r5.getNodo(5).getS()
                        + r6.getNodo(5).getS()
                        + r7.getNodo(5).getS()
                        + r8.getNodo(5).getS()
                        + r9.getNodo(5).getS()
                        + r10.getNodo(5).getS()) / 10.0;

                if (volt < 0.66) {
                    voltEfectivo = 0;
                } else if (volt >= 0.66 && volt < 1.33) {
                    voltEfectivo = 1;
                } else {
                    voltEfectivo = 2;
                }

                r.getNodo(5).setS(voltEfectivo);
                r2.getNodo(5).setS(voltEfectivo);
                r3.getNodo(5).setS(voltEfectivo);
                r4.getNodo(5).setS(voltEfectivo);
                r5.getNodo(5).setS(voltEfectivo);
                r6.getNodo(5).setS(voltEfectivo);
                r7.getNodo(5).setS(voltEfectivo);
                r8.getNodo(5).setS(voltEfectivo);
                r9.getNodo(5).setS(voltEfectivo);
                r10.getNodo(5).setS(voltEfectivo);

                /*
                 * Aqui acaba el rollo del voltaje isopotencial
                 */

                trj[t] = r.getStates();
                trj2[t] = r2.getStates();
                trj3[t] = r3.getStates();
                trj4[t] = r4.getStates();
                trj5[t] = r5.getStates();
                trj6[t] = r6.getStates();
                trj7[t] = r7.getStates();
                trj8[t] = r8.getStates();
                trj9[t] = r9.getStates();
                trj10[t] = r10.getStates();

                calcio[t][0] = trj[t][N - 1];
                calcio[t][1] = trj2[t][N - 1];
                calcio[t][2] = trj3[t][N - 1];
                calcio[t][3] = trj4[t][N - 1];
                calcio[t][4] = trj5[t][N - 1];
                calcio[t][5] = trj6[t][N - 1];
                calcio[t][6] = trj7[t][N - 1];
                calcio[t][7] = trj8[t][N - 1];
                calcio[t][8] = trj9[t][N - 1];
                calcio[t][9] = trj10[t][N - 1];

                for (int i = 0; i < 10; i++) {
                    calciote[t] += calcio[t][i];
                }

                stop:
                for (int m = 0; m < tMax - 1; ++m) {
                    for (int h = m + 1; h < tMax; ++h) {

                        if (arrayEquals(calcio[m], calcio[h])) {
                            transi[0] = h;
                            m = tMax;
                            continue stop;
                        }
                    }
                }

                stop2:
                for (int m = 0; m < tMax - 1; ++m) {
                    for (int h = m + 1; h < tMax; ++h) {
                        if (arrayEquals(trj[m], trj[h])) {
                            transi[1] = h;
                            m = tMax;
                            continue stop2;
                        }
                    }
                }
            }
            fw.writeLine(ic + "\t" + transi[0]);
            sumota[0] += transi[0];
            sumota[1] += transi[1];
        }
        for (int i = 0; i < tMax - 1; ++i) {
            for (int j = 0; j < 10; ++j) {
                fw3.writeString((calcio[i][j]) + "\t");
            }
            fw3.writeLine();
        }
        fw3.close();
        fw.close();
        sumota[0] = sumota[0] / iC;
        sumota[1] = sumota[1] / iC;
        return sumota;
    }

//    public static void evolveReglaMayoriaChida(int numNodos, Nodo[][] nodo) {
//
//        
//        nodo = new Nodo[numNodos][];
//        int N = nodo.length;                          //N tiene el tamaÃ±o de la red
////        nodo = new ArrayList<Nodo[]>();
//        int[][] nuevoEstado = new int[numNodos][N];
//        int[] nrs = new int[numNodos];      //nrs: cada array de nodos tiene un numero de reguladores. nrs los guarda
//        //antes, nr era un solo entero, nrs ora es arreglo
//
////        for (int n = 0; n < numNodos; ++n) {
////            nodo.add(n,nodo.get(n));
////        }
//        /*
//         * se supone que aqui ya tengo hechos
//         * mis arreglos de nodos
//         */
//        numNodos = 3;
//        int[] ifr = new int[numNodos];
//        int[][] nt;     //nt es la matriz donde se guarda en cada fila
//        // el numero de reguladores de cada nodo 
//
//
//
//        //Creamos el vector donde se almacena temporalmente
//        //y se juega con los valores del calcio
//        int[] tempCa = new int[numNodos];
//        for (int ns = 0; ns < numNodos; ++ns) {          //for para cada nodo adicionado del ArrayList
//            for (int n = 0; n < N; ++n) {               //for para cada nodo de la red
//                nrs[ns] = nodo[n].getNumReg();
//                nt = new int[numNodos][nrs[ns]]; //"nt"era el vector que guarda el num d regs. de N
//                //ora es matriz, dependiendo del numNodos
//
//                /*
//                 * Ora aqui obtenemos el estado
//                 * de los reguladores de cada nodo
//                 * dentro de cada array de nodos
//                 */
//                for (int m = 0; m < nrs[ns]; ++m) {
//                    int i = nodo[n].getReg(m);
//                    nt[ns][m] = nodo[i].getS();
//                }
//
//                /*
//                 * aqui se convierten a enteros los
//                 * arreglos ternarios y se guarda el
//                 * estado de cada nodo de la red, para cada
//                 * array de nodos
//                 */
//                ifr[ns] = ternaryToInt(nt[ns]);
//                nuevoEstado[ns][n] = nodo[n].getFuncVal(ifr[ns]);
//
//            }
//        }
//        /*
//         * se supone que en este momento, ya estan
//         * todos los arreglos de nodos de todos los
//         * arrays de nodos creados. O sea, todo de todo.
//         * A partir de aqui se juega con los valores del
//         * calcio y se hace la regla de la mayoria
//         */
//        
//
//        int cuenta0 = 0;
//        int cuenta1 = 0;
//        int cuenta2 = 0;
//        /*
//         * empezamos por asignar a tempCa los valores
//         * del calcio obtenidos de las tablas y
//         * activamos los contadores pa la regla de la
//         * mayoria
//         */
//        for (int a = 0; a < numNodos; ++a) {
//            tempCa[a] = nuevoEstado[a][N - 1];
//            if (tempCa[a] == 0) {
//                cuenta0++;
//            } else if (tempCa[a] == 1) {
//                cuenta1++;
//            } else {
//                cuenta2++;
//            }
//        }
//        
//        /*
//         * INICIA REGLA DE LA MAYORIA
//         * ALGUNAS CONSIDERACIONES
//         * el numero de redes siempre sera impar
//         * para que el nodo del centro vea un numero
//         * igual a la derecha y a la izquierda.
//         * 
//         * caso mayoria 0
//         */
//        if ((cuenta0 > (numNodos / 2)) && tempCa[(int)(numNodos / 2) + 1] != 2) {
//            tempCa[(int)(numNodos / 2) + 1] = 0;
//        } else if ((cuenta0 > (numNodos / 2)) && tempCa[(int)(numNodos / 2) + 1] == 2) {
//            tempCa[(int)(numNodos / 2) + 1] = 0;
//        }
//
//        /*
//         * Caso mayoria 1
//         */
//        if (cuenta1 > (numNodos / 2)) {
//            tempCa[(int)(numNodos / 2) + 1] = 1;
//        } /*
//         * caso mayoria 2
//         */ else if ((cuenta2 > (numNodos / 2)) && tempCa[(int)(numNodos / 2) + 1] != 0) {
//            tempCa[(int)(numNodos / 2) + 1] = 2;
//        } else if ((cuenta2 > (numNodos / 2)) && tempCa[(int)(numNodos / 2) + 1] == 0) {
//            tempCa[(int)(numNodos / 2) + 1] = 1;
//        }
//        /*
//         * si no encuentra ninguna de estas reglas, se queda
//         * con su valor
//         */
//        else {
//            tempCa[(int)(numNodos / 2) + 1] = tempCa[(int)(numNodos / 2) + 1];
//        }
//        
//        /*
//         * ya esta parte es la verdadera asignacion de valores
//         * De aqui sale el metodo con el valor del calcio "mayoreado"
//         * de "ns" calcios. Ademas todos los nodos con sus valores respectivos
//         */
//        for(int ns = 0; ns < numNodos; ++ns){
//        for (int n = 0; n < N; ++n) {
//            if (nodo[ns].isPresent()) {
//                nodo[ns].setS(nuevoEstado[(int)(numNodos / 2) + 1][n]);
//            } else {
//                if (nodo[ns].isBinary()) {
//                    nodo[ns].setS(0);
//                } else {
//                    nodo[ns].setS(1);
//                }
//            }
//        }
//        }
//        nodo[(int)(numNodos / 2) + 1].setS(tempCa[(int)(numNodos / 2) + 1]);
//        
//        
//
//    }

//    public static double[] todoCoupledMayoriaChida(
//            int iC, int numRedes, int numNodos, Red[][] r1) {
//        int N = r1[0][0].getSize();
//        int tMax = 1000;
//        int[][][] trjs = new int[numRedes][tMax][N];
////        int[][] trj;
////        int[][] trj2;
////        int[][] trj3;
////        int[][] trj4;
////        int[][] trj5;
////        int[][] trj6;
////        int[][] trj7;
////        int[][] trj8;
////        int[][] trj9;
////        int[][] trj10;
//        int[][] calcio;
//        
////        trj = new int[tMax][N];
////        trj2 = new int[tMax][N];
////        trj3 = new int[tMax][N];
////        trj4 = new int[tMax][N];
////        trj5 = new int[tMax][N];
////        trj6 = new int[tMax][N];
////        trj7 = new int[tMax][N];
////        trj8 = new int[tMax][N];
////        trj9 = new int[tMax][N];
////        trj10 = new int[tMax][N];
//        /*
//         * ya me los chingue
//         */
//
//        double[] sumota = new double[2];
//        calcio = new int[tMax][numRedes];
//        double[] calciote = new double[tMax];
//        int t;
//
//        int[] transi = new int[2];
//
//        FileToWrite fw3 = new FileToWrite("ItalianCoupling/GoodCoupling/mayoria/10redeschida.dat");
//        FileToWrite fw = new FileToWrite("ItalianCoupling/GoodCoupling/mayoria/10redesTransitorioschida.dat");
//        System.out.println("abre archivos");
//        /*
//         * tienes que juntar unas subredes
//         * para jugar al acople
//         */
//       
//        
//        for (int ic = 0; ic < iC; ++ic) {
//            
//for(int rs = 0; rs < numRedes; ++rs){
//    for(int ss = 0; ss < numNodos; ++ss){
//                r1[rs][ss].setRandomStates();
//            System.out.println("genera estados iniciales aleatorios");
////            r.setRandomStates();
////            r2.setRandomStates();
////            r3.setRandomStates();
////            r4.setRandomStates();
////            r5.setRandomStates();
////            r6.setRandomStates();
////            r7.setRandomStates();
////            r8.setRandomStates();
////            r9.setRandomStates();
////            r10.setRandomStates();
//
//            /*
//             * ya me los chingue
//             */
//            calcio[0][numRedes-1] = trjs[numRedes-1][0][N-1];
////            calcio[0][0] = trj[0][N - 1];
////            calcio[0][1] = trj2[0][N - 1];
////            calcio[0][2] = trj3[0][N - 1];
////            calcio[0][3] = trj4[0][N - 1];
////            calcio[0][4] = trj5[0][N - 1];
////            calcio[0][5] = trj6[0][N - 1];
////            calcio[0][6] = trj7[0][N - 1];
////            calcio[0][7] = trj8[0][N - 1];
////            calcio[0][8] = trj9[0][N - 1];
////            calcio[0][9] = trj10[0][N - 1];
//            /*
//             * ya me los chingue
//             */
//
//            
//            for (t = 1; t < tMax; ++t) {
//                r1[rs][ss].evolveKauffmanMayoriaChida(r1[rs], numNodos);
//                System.out.println("evolucion numero " + t);
////                r.evolveKauffmanMayoria(r10, r, r2);
////                r2.evolveKauffmanMayoria(r, r2, r3);
////                r3.evolveKauffmanMayoria(r2, r3, r4);
////                r4.evolveKauffmanMayoria(r3, r4, r5);
////                r5.evolveKauffmanMayoria(r4, r5, r6);
////                r6.evolveKauffmanMayoria(r5, r6, r7);
////                r7.evolveKauffmanMayoria(r6, r7, r8);
////                r8.evolveKauffmanMayoria(r7, r8, r9);
////                r9.evolveKauffmanMayoria(r8, r9, r10);
////                r10.evolveKauffmanMayoria(r9, r10, r);
//
//                int voltEfectivo = 0;
//                double volt = 0;
////                double volt = (r.getNodo(5).getS()
////                        + r2.getNodo(5).getS()
////                        + r3.getNodo(5).getS()
////                        + r4.getNodo(5).getS()
////                        + r5.getNodo(5).getS()
////                        + r6.getNodo(5).getS()
////                        + r7.getNodo(5).getS()
////                        + r8.getNodo(5).getS()
////                        + r9.getNodo(5).getS()
////                        + r10.getNodo(5).getS()) / 10.0;
//                for(int volRed = 0; volRed < numRedes; ++volRed){
//                volt += r1[rs][volRed].getNodo(5).getS();
//                        }
//               volt /=numRedes;
//                /*
//                 * ya me los chingue
//                 */
//
//                if (volt < 0.66) {
//                    voltEfectivo = 0;
//                } else if (volt >= 0.66 && volt < 1.33) {
//                    voltEfectivo = 1;
//                } else {
//                    voltEfectivo = 2;
//                }
//
//                for(int volRed = 0; volRed < numRedes; ++volRed){
//                r1[rs][volRed].getNodo(5).setS(voltEfectivo);
//                        }
////                r.getNodo(5).setS(voltEfectivo);
////                r2.getNodo(5).setS(voltEfectivo);
////                r3.getNodo(5).setS(voltEfectivo);
////                r4.getNodo(5).setS(voltEfectivo);
////                r5.getNodo(5).setS(voltEfectivo);
////                r6.getNodo(5).setS(voltEfectivo);
////                r7.getNodo(5).setS(voltEfectivo);
////                r8.getNodo(5).setS(voltEfectivo);
////                r9.getNodo(5).setS(voltEfectivo);
////                r10.getNodo(5).setS(voltEfectivo);
//                /*
//                 * ya me los chingue
//                 */
//
//                /*
//                 * Aqui acaba el rollo del voltaje isopotencial
//                 */
//
//                //numRedes tMax N
//                    trjs[rs][t] = r1[rs][ss].getStates();
//                
////                trj[t] = r.getStates();
////                trj2[t] = r2.getStates();
////                trj3[t] = r3.getStates();
////                trj4[t] = r4.getStates();
////                trj5[t] = r5.getStates();
////                trj6[t] = r6.getStates();
////                trj7[t] = r7.getStates();
////                trj8[t] = r8.getStates();
////                trj9[t] = r9.getStates();
////                trj10[t] = r10.getStates();
//                /*
//                 * ya me los chingue
//                 */
//
//                calcio[t][rs] = trjs[rs][t][N-1];
//                System.out.println("valores del calcio");
////                calcio[t][0] = trj[t][N - 1];
////                calcio[t][1] = trj2[t][N - 1];
////                calcio[t][2] = trj3[t][N - 1];
////                calcio[t][3] = trj4[t][N - 1];
////                calcio[t][4] = trj5[t][N - 1];
////                calcio[t][5] = trj6[t][N - 1];
////                calcio[t][6] = trj7[t][N - 1];
////                calcio[t][7] = trj8[t][N - 1];
////                calcio[t][8] = trj9[t][N - 1];
////                calcio[t][9] = trj10[t][N - 1];
//                /*
//                 * ya me los chingue
//                 */
//
//                for (int i = 0; i < 10; i++) {
//                    calciote[t] += calcio[t][i];
//                }
//            }
//
//                stop:
//                for (int m = 0; m < tMax - 1; ++m) {
//                    for (int h = m + 1; h < tMax; ++h) {
//
//                        if (arrayEquals(calcio[m], calcio[h])) {
//                            transi[0] = h;
//                            m = tMax;
//                            continue stop;
//                        }
//                    }
//                }
//
//                stop2:
//                for (int m = 0; m < tMax - 1; ++m) {
//                    for (int h = m + 1; h < tMax; ++h) {
//                        if (arrayEquals(trjs[rs][m], trjs[rs][h])) {
//                            transi[1] = h;
//                            m = tMax;
//                            continue stop2;
//                        }
//                    }
//                }
//            
//            fw.writeLine(ic + "\t" + transi[0]);
//            sumota[0] += transi[0];
//            sumota[1] += transi[1];
//        }
//}
//    }
//        for (int i = 0; i < tMax - 1; ++i) {
//            for (int j = 0; j < 10; ++j) {
//                fw3.writeString((calcio[i][j]) + "\t");
//            }
//            fw3.writeLine();
//        }
//        fw3.close();
//        fw.close();
//        sumota[0] = sumota[0] / iC;
//        sumota[1] = sumota[1] / iC;
//        return sumota;
//    }

    
   
public static void derridaMapCoupled(Red r1, Red r2,Red r3, Red r4,
                                       Red r5, Red r6,Red r7, Red r8,
                                       Red r9, Red r10,Red r11, Red r12, 
                                       Red r13, Red r14,Red r15, Red r16, 
                                       Red r17, Red r18,Red r19, Red r20,
                                       int iC, double eps, double div, 
                                       double rounder2, double rounder, int nodoAcoplador, 
                                       String file) {
    
        int N = r1.getSize()*10;
        int[] temp1 = new int[N];
        int[] temp2 = new int[N];
        double d1, d2;
        int rea = 10000;
        FileToWrite fw;
        for (int n = 0; n < r1.getSize(); ++n) {
            r1.getNodo(n).setPresente(true);
            r2.getNodo(n).setPresente(true);r3.getNodo(n).setPresente(true);
            r4.getNodo(n).setPresente(true);r5.getNodo(n).setPresente(true);
            r6.getNodo(n).setPresente(true);r7.getNodo(n).setPresente(true);
            r8.getNodo(n).setPresente(true);r9.getNodo(n).setPresente(true);
            r10.getNodo(n).setPresente(true);r11.getNodo(n).setPresente(true);
            r12.getNodo(n).setPresente(true);r13.getNodo(n).setPresente(true);
            r14.getNodo(n).setPresente(true);r15.getNodo(n).setPresente(true);
            r16.getNodo(n).setPresente(true);r17.getNodo(n).setPresente(true);
            r18.getNodo(n).setPresente(true);r19.getNodo(n).setPresente(true);
            r20.getNodo(n).setPresente(true);
        }
        fw = new FileToWrite(file);
        d1 = 0;
        d2 = 0;
        for (int d = 0; d <= 2*N/3; ++d) {
            d1 = 0;
            d2 = 0;
            for (int r = 0; r < rea; ++r) {
                r1.setRandomStates(); r2.setRandomStates();r3.setRandomStates();r4.setRandomStates(); r5.setRandomStates();
                r6.setRandomStates();r7.setRandomStates(); r8.setRandomStates();r9.setRandomStates(); r10.setRandomStates();
                
                
                
                setDistanceCoupled(r1, r2, 
                        r3, r4,r5, r6,r7, 
                        r8,r9, r10,r11, r12,
                        r13, r14,r15,r16, r17,r18, r19, r20,
                        d);
                
                for(int i = 0; i < r1.getSize(); ++i){
                    temp1[i] = r1.getNodo(i).getS();
                    temp1[i+(r1.getSize()*1)] = r2.getNodo(i).getS();
                    temp1[i+(r1.getSize()*2)] = r3.getNodo(i).getS();
                    temp1[i+(r1.getSize()*3)] = r4.getNodo(i).getS();
                    temp1[i+(r1.getSize()*4)] = r5.getNodo(i).getS();
                    temp1[i+(r1.getSize()*5)] = r6.getNodo(i).getS();
                    temp1[i+(r1.getSize()*6)] = r7.getNodo(i).getS();
                    temp1[i+(r1.getSize()*7)] = r8.getNodo(i).getS();
                    temp1[i+(r1.getSize()*8)] = r9.getNodo(i).getS();
                    temp1[i+(r1.getSize()*9)] = r10.getNodo(i).getS();
                }
                
                for(int i = 0; i < r1.getSize(); ++i){
                    temp2[i] = r11.getNodo(i).getS();
                    temp2[i+(r1.getSize()*1)] = r12.getNodo(i).getS();
                    temp2[i+(r1.getSize()*2)] = r13.getNodo(i).getS();
                    temp2[i+(r1.getSize()*3)] = r14.getNodo(i).getS();
                    temp2[i+(r1.getSize()*4)] = r15.getNodo(i).getS();
                    temp2[i+(r1.getSize()*5)] = r16.getNodo(i).getS();
                    temp2[i+(r1.getSize()*6)] = r17.getNodo(i).getS();
                    temp2[i+(r1.getSize()*7)] = r18.getNodo(i).getS();
                    temp2[i+(r1.getSize()*8)] = r19.getNodo(i).getS();
                    temp2[i+(r1.getSize()*9)] = r20.getNodo(i).getS();
                }
                
//                for(int i = 0; i < temp1.length; ++i){
//                System.out.print(temp1[i] + " ");
//                }System.out.println();
//                for(int i = 0; i < temp1.length; ++i){
//                System.out.print(temp2[i] + " ");
//                }System.out.println("*************************");
                
                for(int i  = 0; i < temp1.length; ++i){
                    
                    if(temp1[i] != temp2[i]){
                        d1++;
                    }
                }
//                d1/=temp1.length;
                r1.evolveKauffmanCoupled(r10,r1,r2, eps,0,0,rounder2, rounder,0);
                r2.evolveKauffmanCoupled(r1,r2,r3,eps, 0,0,rounder2, rounder,0);
                r3.evolveKauffmanCoupled(r2,r3,r4,eps, 0,0,rounder2, rounder,0);
                r4.evolveKauffmanCoupled(r3,r4,r5,eps, 0,0,rounder2, rounder,0);
                r5.evolveKauffmanCoupled(r4,r5,r6,eps, 0,0,rounder2, rounder,0);
                r6.evolveKauffmanCoupled(r5,r6,r7,eps, 0,0,rounder2, rounder,0);
                r7.evolveKauffmanCoupled(r6,r7,r8,eps, 0,0,rounder2, rounder,0);
                r8.evolveKauffmanCoupled(r7,r8,r9,eps, 0,0,rounder2, rounder,0);
                r9.evolveKauffmanCoupled(r8,r9,r10,eps, 0,0,rounder2, rounder,0);
                r10.evolveKauffmanCoupled(r9,r10,r1,eps, 0,0,rounder2, rounder,0);
                r11.evolveKauffmanCoupled(r10,r11,r12,eps, 0,0,rounder2, rounder,0);
                r12.evolveKauffmanCoupled(r11,r12,r13,eps, 0,0,rounder2, rounder,0);
                r13.evolveKauffmanCoupled(r12,r13,r14,eps, 0,0,rounder2, rounder,0);
                r14.evolveKauffmanCoupled(r13,r14,r15,eps, 0,0,rounder2, rounder,0);
                r15.evolveKauffmanCoupled(r14,r15,r16,eps, 0,0,rounder2, rounder,0);
                r16.evolveKauffmanCoupled(r15,r16,r17,eps, 0,0,rounder2, rounder,0);
                r17.evolveKauffmanCoupled(r16,r17,r18,eps, 0,0,rounder2, rounder,0);
                r18.evolveKauffmanCoupled(r17,r18,r19,eps, 0,0,rounder2, rounder,0);
                r19.evolveKauffmanCoupled(r18,r19,r20,eps, 0,0,rounder2, rounder,0);
                r20.evolveKauffmanCoupled(r19,r20,r11,eps, 0,0,rounder2, rounder,0);
                
                int voltEfectivo = 0;
                double volt = (r1.getNodo(5).getS()
                        + r2.getNodo(5).getS()
                        + r3.getNodo(5).getS()
                        + r4.getNodo(5).getS()
                        + r5.getNodo(5).getS()
                        + r6.getNodo(5).getS()
                        + r7.getNodo(5).getS()
                        + r8.getNodo(5).getS()
                        + r9.getNodo(5).getS()
                        + r10.getNodo(5).getS() / 10.0);

                if (volt < 0.66) {
                    voltEfectivo = 0;
                } else if (volt >= 0.66 && volt < 1.33) {
                    voltEfectivo = 1;
                } else {
                    voltEfectivo = 2;
                }

//                r1.getNodo(5).setS(voltEfectivo);
//                r2.getNodo(5).setS(voltEfectivo);
//                r3.getNodo(5).setS(voltEfectivo);
//                r4.getNodo(5).setS(voltEfectivo);
//                r5.getNodo(5).setS(voltEfectivo);
//                r6.getNodo(5).setS(voltEfectivo);
//                r7.getNodo(5).setS(voltEfectivo);
//                r8.getNodo(5).setS(voltEfectivo);
//                r9.getNodo(5).setS(voltEfectivo);
//                r10.getNodo(5).setS(voltEfectivo);
                
                int volt2Efectivo = 0;
                double volt2 = (r1.getNodo(5).getS()
                        + r2.getNodo(5).getS()
                        + r3.getNodo(5).getS()
                        + r4.getNodo(5).getS()
                        + r5.getNodo(5).getS()
                        + r6.getNodo(5).getS()
                        + r7.getNodo(5).getS()
                        + r8.getNodo(5).getS()
                        + r9.getNodo(5).getS()
                        + r10.getNodo(5).getS() / 10.0);

                if (volt2 < 0.66) {
                    volt2Efectivo = 0;
                } else if (volt2 >= 0.66 && volt2 < 1.33) {
                    volt2Efectivo = 1;
                } else {
                    volt2Efectivo = 2;
                }

//                r1.getNodo(5).setS(volt2Efectivo);
//                r2.getNodo(5).setS(volt2Efectivo);
//                r3.getNodo(5).setS(volt2Efectivo);
//                r4.getNodo(5).setS(volt2Efectivo);
//                r5.getNodo(5).setS(volt2Efectivo);
//                r6.getNodo(5).setS(volt2Efectivo);
//                r7.getNodo(5).setS(volt2Efectivo);
//                r8.getNodo(5).setS(volt2Efectivo);
//                r9.getNodo(5).setS(volt2Efectivo);
//                r10.getNodo(5).setS(volt2Efectivo);
            
                for(int i = 0; i < r1.getSize(); ++i){
                    temp1[i] = r1.getNodo(i).getS();
                    temp1[i+(r1.getSize()*1)] = r2.getNodo(i).getS();
                    temp1[i+(r1.getSize()*2)] = r3.getNodo(i).getS();
                    temp1[i+(r1.getSize()*3)] = r4.getNodo(i).getS();
                    temp1[i+(r1.getSize()*4)] = r5.getNodo(i).getS();
                    temp1[i+(r1.getSize()*5)] = r6.getNodo(i).getS();
                    temp1[i+(r1.getSize()*6)] = r7.getNodo(i).getS();
                    temp1[i+(r1.getSize()*7)] = r8.getNodo(i).getS();
                    temp1[i+(r1.getSize()*8)] = r9.getNodo(i).getS();
                    temp1[i+(r1.getSize()*9)] = r10.getNodo(i).getS();
                }
                
                for(int i = 0; i < r1.getSize(); ++i){
                    temp2[i] = r11.getNodo(i).getS();
                    temp2[i+(r1.getSize()*1)] = r12.getNodo(i).getS();
                    temp2[i+(r1.getSize()*2)] = r13.getNodo(i).getS();
                    temp2[i+(r1.getSize()*3)] = r14.getNodo(i).getS();
                    temp2[i+(r1.getSize()*4)] = r15.getNodo(i).getS();
                    temp2[i+(r1.getSize()*5)] = r16.getNodo(i).getS();
                    temp2[i+(r1.getSize()*6)] = r17.getNodo(i).getS();
                    temp2[i+(r1.getSize()*7)] = r18.getNodo(i).getS();
                    temp2[i+(r1.getSize()*8)] = r19.getNodo(i).getS();
                    temp2[i+(r1.getSize()*9)] = r20.getNodo(i).getS();
                }
                
                
//                for(int i = 0; i < temp1.length; ++i){
//                System.out.print(temp1[i] + " ");
//                }System.out.println();
//                for(int i = 0; i < temp1.length; ++i){
//                System.out.print(temp2[i] + " ");
//                }System.out.println("\n\n\n");
                
                
                for(int i  = 0; i < temp1.length; ++i){
                    if(temp1[i] != temp2[i]){
                        d2++;
                    }
                }
//                d2/=temp1.length;
            }
            
            d1 /= rea;
            d2 /= rea;
            fw.writeLine(d1 + "\t\t" + (d2));
        }
        fw.writeLine();
        fw.close();
//        }
//                }
//            }
//        }
    }


public static void setDistanceCoupled(Red r1, Red r2,Red r3, Red r4,
                                       Red r5, Red r6,Red r7, Red r8,
                                       Red r9, Red r10,Red r11, Red r12, 
                                       Red r13, Red r14,Red r15, Red r16, 
                                       Red r17, Red r18,Red r19, Red r20, int d) {
        int N = r1.getSize()*10;
        int M = N;
        int s1, s2,s3,s4,s5,s6,s7,s8,s9,s10,s11,s12,s13,s14,s15,s16,s17,s18,s19,s20, i, n;
        int[] st = new int[N];
        int[] rst = new int[d];

        for (n = 0; n < r1.getSize(); ++n) {
            st[n] = n;
            s1 = r1.getNodo(n).getS();r11.getNodo(n).setS(s1);
            s2 = r2.getNodo(n).getS();r12.getNodo(n).setS(s2);
            s3 = r3.getNodo(n).getS();r13.getNodo(n).setS(s3);
            s4 = r4.getNodo(n).getS();r14.getNodo(n).setS(s4);
            s5 = r5.getNodo(n).getS();r15.getNodo(n).setS(s5);
            s6 = r6.getNodo(n).getS();r16.getNodo(n).setS(s6);
            s7 = r7.getNodo(n).getS();r17.getNodo(n).setS(s7);
            s8 = r8.getNodo(n).getS();r18.getNodo(n).setS(s8);
            s9 = r9.getNodo(n).getS();r19.getNodo(n).setS(s9);
            s10 = r10.getNodo(n).getS();r20.getNodo(n).setS(s10);
        }

        --M;
        for (n = 0; n < d; ++n) {
            i = (int) (M * Math.random());
            rst[n] = st[i];
            st[i] = st[M - 1];
            --M;
        }

        for (n = 0; n < d; ++n) {
            i = rst[n];
            s1 = r1.getNodo(i).getS();s2 = r2.getNodo(i).getS();
            s3 = r3.getNodo(i).getS();s4 = r4.getNodo(i).getS();
            s5 = r5.getNodo(i).getS();s6 = r6.getNodo(i).getS();
            s7 = r7.getNodo(i).getS();s8 = r8.getNodo(i).getS();
            s9 = r9.getNodo(i).getS();s10 = r10.getNodo(i).getS();
            
            if (r1.getNodo(i).isBinary()) {
                s11 = 1 - s1;
            } else {
                if (s1 == 0) {
                    s11 = (Math.random() < 0.5) ? 1 : 2;
                } else {
                    if (s1 == 1) {
                        s11 = (Math.random() < 0.5) ? 0 : 2;
                    } else {
                        s11 = (Math.random() < 0.5) ? 0 : 1;
                    }
                }
            }
            r11.getNodo(i).setS(s11);
            
            if (r2.getNodo(i).isBinary()) {
                s12 = 1 - s2;
            } else {
                if (s2 == 0) {
                    s12 = (Math.random() < 0.5) ? 1 : 2;
                } else {
                    if (s2 == 1) {
                        s12 = (Math.random() < 0.5) ? 0 : 2;
                    } else {
                        s12 = (Math.random() < 0.5) ? 0 : 1;
                    }
                }
            }
            r12.getNodo(i).setS(s12);
            
        if (r3.getNodo(i).isBinary()) {
                s13 = 1 - s3;
            } else {
                if (s3 == 0) {
                    s13 = (Math.random() < 0.5) ? 1 : 2;
                } else {
                    if (s3 == 1) {
                        s13 = (Math.random() < 0.5) ? 0 : 2;
                    } else {
                        s13 = (Math.random() < 0.5) ? 0 : 1;
                    }
                }
            }
            r13.getNodo(i).setS(s13);
        
            
            if (r4.getNodo(i).isBinary()) {
                s14 = 1 - s4;
            } else {
                if (s4 == 0) {
                    s14 = (Math.random() < 0.5) ? 1 : 2;
                } else {
                    if (s4 == 1) {
                        s14 = (Math.random() < 0.5) ? 0 : 2;
                    } else {
                        s14 = (Math.random() < 0.5) ? 0 : 1;
                    }
                }
            }
            r14.getNodo(i).setS(s14);
            
            if (r5.getNodo(i).isBinary()) {
                s15 = 1 - s5;
            } else {
                if (s5 == 0) {
                    s15 = (Math.random() < 0.5) ? 1 : 2;
                } else {
                    if (s5 == 1) {
                        s15 = (Math.random() < 0.5) ? 0 : 2;
                    } else {
                        s15 = (Math.random() < 0.5) ? 0 : 1;
                    }
                }
            }
            r15.getNodo(i).setS(s15);
            
            
            if (r6.getNodo(i).isBinary()) {
                s16 = 1 - s6;
            } else {
                if (s6 == 0) {
                    s16 = (Math.random() < 0.5) ? 1 : 2;
                } else {
                    if (s6 == 1) {
                        s16 = (Math.random() < 0.5) ? 0 : 2;
                    } else {
                        s16 = (Math.random() < 0.5) ? 0 : 1;
                    }
                }
            }
            r16.getNodo(i).setS(s16);
            
            
            if (r7.getNodo(i).isBinary()) {
                s17 = 1 - s7;
            } else {
                if (s7 == 0) {
                    s17 = (Math.random() < 0.5) ? 1 : 2;
                } else {
                    if (s7 == 1) {
                        s17 = (Math.random() < 0.5) ? 0 : 2;
                    } else {
                        s17 = (Math.random() < 0.5) ? 0 : 1;
                    }
                }
            }
            r17.getNodo(i).setS(s17);
            
            if (r8.getNodo(i).isBinary()) {
                s18 = 1 - s8;
            } else {
                if (s8 == 0) {
                    s18 = (Math.random() < 0.5) ? 1 : 2;
                } else {
                    if (s8 == 1) {
                        s18 = (Math.random() < 0.5) ? 0 : 2;
                    } else {
                        s18 = (Math.random() < 0.5) ? 0 : 1;
                    }
                }
            }
            r18.getNodo(i).setS(s18);
            
            if (r9.getNodo(i).isBinary()) {
                s19 = 1 - s9;
            } else {
                if (s9 == 0) {
                    s19 = (Math.random() < 0.5) ? 1 : 2;
                } else {
                    if (s9 == 1) {
                        s19 = (Math.random() < 0.5) ? 0 : 2;
                    } else {
                        s19 = (Math.random() < 0.5) ? 0 : 1;
                    }
                }
            }
            r19.getNodo(i).setS(s19);
            
            if (r10.getNodo(i).isBinary()) {
                s20 = 1 - s10;
            } else {
                if (s10 == 0) {
                    s20 = (Math.random() < 0.5) ? 1 : 2;
                } else {
                    if (s10 == 1) {
                        s20 = (Math.random() < 0.5) ? 0 : 2;
                    } else {
                        s20 = (Math.random() < 0.5) ? 0 : 1;
                    }
                }
            }
            r20.getNodo(i).setS(s20);
        }

      
    }



public static void max(){
    FileToWrite fw = new FileToWrite("cAMPCoupled.dat");
    for(double i = 0; i < 1; i +=0.01){
        double sumotaCa = 0;
        FileToRead fr = new FileToRead("/home/chucho/Coupled/AcopleMayo12/DeTablasYA/Isopotencial/cAMPAcoplado/2Rounder_0.5_trans_eps_" + i + "_div_1.0_.dat");
        for(int n=0; n <1000; ++n){
            sumotaCa += fr.nextInt();
        }
        fr.close();
        sumotaCa/=1000.0;
        fw.writeLine(i + "\t" + sumotaCa);
    }
    fw.close();
}
}
