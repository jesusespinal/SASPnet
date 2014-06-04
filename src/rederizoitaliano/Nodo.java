package rederizoitaliano;

import fileOperations.*;
import java.util.*;

public class Nodo {

    String nombre;    // Nombre del nodo
    int stInt;        // Estado entero para dinamica discreta
    double stDou;     // Estado double para dinamica Glass
    double alpha;     // Coeficiente de relajacion
    int stInitial;    // Valor inicial del estado que se lee de las tablas. Sirve para resetear la red
    double[] thr;     // Valor del threshold para la dinamica de Glass.
    double maxThr;    // Valor maximo del threshold (1 para binarios, 2 para ternarios)
    int numReg;       // Numero de reguladores
    int[] regInt;     // Arreglo con el valor entero del indice de los reguladores
    int[] funReg;     // Arreglo con la funcion reguladora
    int[] funRegIni;  // Arreglo con la funcion reguladora inicial
    boolean presente; // Indica si el nodo esta o no esta en la red
    boolean binario;  // Indica si el nodo es binario (true) o ternario (false)

    int tipo;         //determina si el nodo es binario, ternario etc
    int reng;         //CHUCHO RENGLON QUE VA A GUARDAR EL VALOR ENTERO DEL RENGLON EN CUESTION
    int tabla[];  //crea una tabla solo con los valores de las reguladores (Sin FUNCION)
    ArrayList<Integer> table = new ArrayList<Integer>();//linea Chucho crea ArrayList para guardar los ternarios

    public Nodo(String nom, String[] diccionario, String directorio) {
        int i, n, m;

        // Asigna el nombre
        nombre = nom;

        // Inicialmente, todos los nodos estan presentes
        presente = true;

        // Asigna el valor del coeficiente de relajacion
        alpha = 1;

        // Ahora comenzara a leer los datos del archivo
        String fileName = directorio + nombre + ".txt";
        FileToRead fr = new FileToRead(fileName);

        // Lo primero que lee es el estado
        stInt = fr.nextInt();
        stInitial = stInt;

        // Asigna un valor a la concentracion (double) de forma aleatoria
        // pero consistente con el estado entero que acaba de leer.
        for (n = 0; n < (thr.length - 1); ++n) {
            if ((thr[n] <= stInt) && (stInt <= thr[n + 1])) {
                stDou = thr[n] + ((thr[n + 1] - thr[n]) * Math.random());
                break;
            }
        }

        // Lee el numero de reguladores y despues lee los nombres de los
        // reguladores extrayendolos como tokens de una linea
        numReg = fr.nextInt();
        String[] regNom = new String[numReg];
        regInt = new int[numReg];

        String linea = fr.nextLine();
        linea = fr.nextLine(); // Hay que leer dos veces porque la primera vez lee el resto de la linea
        // donde se quedo antes

        // Esta es la linea que contiene el nombre de los reguladores
        StringTokenizer t = new StringTokenizer(linea);
        for (n = 0; n < numReg; ++n) {
            regNom[n] = t.nextToken();
            i = buscaIndice(regNom[n], diccionario);
            if (i != -1) {
                regInt[n] = i;
            } else {
                System.out.println("No encontro el nombre en " +
                        "la lista para el regulador " + n + ": " + regNom[n]);
                System.out.println("este pertenece al gen " + nombre);
                System.exit(0);
            }
        }

        // Ahora va a leer las funciones logicas guardandolas como
        // si todas fueran funciones ternarias. Solo la funcion del
        // voltaje (y el calcio en su caso y los HVA y LVA) es ternaria.
        //A todas las demas funciones las considera
        // como ternarias pero las entradas que no existen las llena con -1's
         int numConf = (int) (Math.pow(3, numReg));
//        int numConf = (int) (Math.pow(2, numReg));
        funReg = new int[numConf];
        funRegIni = new int[numConf];
        for (n = 0; n < funReg.length; ++n) {
            funReg[n] = -1;
        }

        int[] nt = new int[numReg];
        int contadorRenglones = 0;
        while (fr.hasNextInt()) {
            contadorRenglones++;
            for (n = 0; n < numReg; ++n) {
                nt[n] = fr.nextInt();              
            }

            Integer M;
            m = Methods.ternaryToInt(nt);
            M=m;

            reng = M;
             table.add(new Integer(reng));//linea chucho

             funReg[reng] = fr.nextInt();  //linea chucho sustituye la de arriba
        }

       
        fr.close();
        for (n = 0; n < funReg.length; ++n) {
            funRegIni[n] = funReg[n];
        }
          // Ahora determina el tipo de nodo, leyendo los valores de la fucion booleana.
        tipo = 0;
        for(n = 0; n < funReg.length; ++n){
            if(funReg[n] > tipo){
                tipo = funReg[n];
            }
        }
        maxThr = tipo; // El maximo threshold es el maximo valor de la funcion booleana
        ++tipo;
        // Ahora la variable "tipo" vale 2 si la funcion es booleana,
        // o vale 3 si la funcion es ternaria. Con esto podemos contruir los
        // thresholds, que se asignaran de manera equitativa.
        thr = new double[tipo + 1];
        if(tipo == 2){
            thr[0] = 0;
            thr[1] = 0.5;
            thr[2] = 1;
        }
        else{
            thr[0] = 0;
            thr[1] = 0.666;
        }
    }

    private int buscaIndice(String nomReg, String[] dic) {
        int i = -1;

        for (int n = 0; n < dic.length; ++n) {
            if (nomReg.equals(dic[n])) {
                i = n;
                break;
            }
        }
        return i;
    }

    // *************** INTERFACE PUBLICA ****************
     public int getType(){
        return tipo;
    }
    public int getS() {
        return stInt;
    }

    public double getC() {
        return stDou;
    }

    public double getThr(int k) {
        return thr[k];
    }

    public double getMaxThr() {
        return maxThr;
    }

    public double getAlpha() {
        return alpha;
    }

    public int getNumReg() {
        return numReg;
    }

    public int getReg(int n) {
        return regInt[n];
    }

    public boolean isPresent() {
        return presente;
    }

    public boolean isBinary() {
        return binario;
    }

  

    public int[] getFunc() {
        return funReg;
    }

    public String getName() {
        return nombre;
    }

    /**
     * Asigna el valor discreto al estado del nodo. Se asegura de que
     * el valor este dentro del rango.
     * @param s
     */
    public void setS(int s) {
        if ((0 <= s) && (s <= 2)) {
            stInt = s;
        } else {
            System.out.println("El valor del nodo " + nombre
                    + " no esta en el rango " + stInt * 2);
            System.exit(0);
        }
    }

    /**
     * Asigna el valor continuo a la concentracion del nodo.
     * Se asegura de que el valor este dentro del rango.
     * @param c
     */
    public void setC(double c) {
        if (c < 0) {
            stDou = 0;
        } else {
            if (c > maxThr) {
                stDou = maxThr;
            } else {
                stDou = c;
            }
        }
    }

    public void setPresente(boolean b) {
        presente = b;
    }

    public void setDefaultState() {
        stInt = stInitial;
    }

    public void setDefaultFunction() {
        for (int n = 0; n < funRegIni.length; ++n) {
            funReg[n] = funRegIni[n];
        }
    }

    public void setFuncReg(int k, int f) {
        funReg[k] = f;
    }

      public int getFuncVal(int i) {
        return funReg[i];
    }

/**
 * Cambia el valor de la función discreta de
 * un nodo, si es binario no hay bronca, pero si
 * es ternario no es tan exacta.
 * @param n el índice del nodo a suichear
 */
    public int getSwitchFuncVal(int n) {
//        for (n = 0; n < funReg.length; ++n) {
            if (binario) {
                funReg[n] = (funReg[n] + 1) % 2;
            }
            else {
                funReg[n] = (funReg[n] + 1) % 3;
            }
//        }
        return funReg[n];
    }
 public int getSwitchFuncVal2(int n) {
//        for (n = 0; n < funReg.length; ++n) {
            if (binario) {
                if( funReg[n] ==0 )
                    funReg[n] =1;
                else funReg[n] =0;
            }
            else {
               if (funReg[n] == 0){
                   funReg[n]=2;}
               else if(funReg[n] == 1){
                   funReg[n]=0;}
               else funReg[n] = 0;

            }
//        }
        return funReg[n];
    }

  public int getNoiseFuncVal(int n) {
      double r = Math.random();
        if (r<0.1) {
            if (binario) {
                funReg[n] = (funReg[n] + 1) % 2;
            }
            else {
                funReg[n] = (funReg[n] + 1) % 3;
            }
        }
        return funReg[n];
    }

}
