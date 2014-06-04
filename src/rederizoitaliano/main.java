
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rederizoitaliano;

import javax.swing.*;
import java.io.*;
import fileOperations.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        // Definimos un vector con los nombres de los archivos
        // donde se guarda la informacion de los nodos. Este arreglo NO se cambia


        /*
         * VAmos a poner las tablas de Cesar de Caulobacter
         */
        String[] nombresBacter = {
            "0_CtrA", "1_GcrA","2_CcrM",
            "3_SciP", "4_DnaA", "5_CtrAD",
            "6_ChpT", "7_ClpXP-RcdA", "8_DivK",
            "9_PleC", "10_DivJ", "11_DivL",
            "12_CckA", "13_CpdR"
        };

        
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

        String[] nombresCatsper = {
            //las de erizo
            "0_sr",//1
            "1_GC",//2
            "2_cGMP",//3
            "3_KCNG",//4
            "4_dK",//5
            "5_v",//6
            "6_NHE",//7
            "7_NCE",//8
            "8_HCN",//9
            "9_AC",//10
            "10_LVA",
            "11_pH",//11
            "12_dNa",//12
            "13_cAMP",//13
            "14_HVA",
            "16_CaP",//14
            "17_CaCC",//15
            "18_catsper",//16
            "20_dCl",//17
            "22_PDE",//18
            "26_CaKC",//19
            "15_dCa"}
                ;//20

        String[] nombresReducido = {
            "0_sr", "1_cGMP", "2_PDE", "3_v",
            "4_catsper", "5_HCN", "6_pH", "7_dCa",
            "8_CaKC", "9_dK"
        };
        
        String[] nombresCatsperReducidoDic2012 = {
            "0_sr", "1_cGMP", "2_PDE", "3_pH",
            "4_v", "5_catsper", "6_Ca"
        };

//        String[] nombres = {
//            //las de erizo System.out.print(x[m] + "\t");
//            "0_sr",  "2_cGMP", "3_KCNG",
//            "4_pK", "5_v", "6_NHE",
//             "8_HCN",
//            "10_LVA", "11_upH",
//            "12_pNa", "13_cAMP",
//             "17_CaCC",
//            "20_pCl", "22_PDE",
//            "15_pCa",};

//                String[] nombres = {"0_sr","1_cGMP",
//				"2_dK","3_v","4_NHEpH",
//                                "5_HCNdNa",
//				"6_HVA","7_LVA",
//                                "8_ACcAMP",
//				"9_NCECaP",
//				"11_CaCCdCl","12_CaKC","13_PDE",
//                                "10_dCa"};

        //****************************


//        MetodosMayo12.distanciaPuntos();

//        MetodosMayo12.creaDatsCoordsGMaps();

//        String tablasDir = "catsper/26JulPruebas/tablasCatsper26JulModifCaCC/";

//       String tablasDir = "TablasDerridaFinalFinal/";
//               String tablasDir = "TablasAtractores/";
//   String tablasDir = "TablasCoupled/";
        
        /*
         * tablas de Cesar de Caulobacter
         */
        String tablasBacter = "CauloBacterNetwork/";
        
        String tablasDir = "tablasYA/";
//            String tablasC2 = "catsper/tablasCatsper2/";
            String tablasC2 = "catsper/MSB/Twenty/tablas/";
            String tablasCatsper2012 = "catsper/tablasCatsperReducidaCOMPENDIO/";
        // ****************************************************
        // Descomentar para la visualizacion
//        // ****************************************************
//        Red red = new Red(tablasC2, nombresCatsper); 
//        Red red = new Red(tablasCatsper2012, nombresReducido);
        Red red = new Red(tablasDir, nombres);
//        Red red = new Red(tablasBacter, nombresBacter);
        JFdiscreteDyn v = new JFdiscreteDyn(red);
        v.setVisible(true);
        v.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         ****************************************************

        // ****************************************************
        // Descomentar para calcular los mapeos de Derrida
        // ****************************************************
//            Red red1 = new Red(tablasC2, nombresCatsper);
//            Red red2 = new Red(tablasC2, nombresCatsper);
//            Red red1 = new Red(tablasCatsper2012, nombresReducido);
//            Red red2 = new Red(tablasCatsper2012, nombresReducido);
//            
//            Red red1 = new Red(tablasBacter, nombresBacter);
//            Red red2 = new Red(tablasBacter, nombresBacter);
//            String file1 = ("CauloBacterNetwork/Derrida/DerridaBacter.dat");
////        Red red1 = new Red(tablasDir,nombres);
////        Red red2 = new Red(tablasDir,nombres);
//             String file1 = ("catsper/italiaTablasCatsperItalia/derrida/WT.dat");
//            String file1 = ("catsper/CatsperFullChannels/derrida/31Mayo2013/WT2.dat");
//             String file1 = ("catsper/Gordon/TCatsperAD/derrida/wt.dat");
//             String file1 = ("catsper/MSB/FullChannels/hamming/wt.dat");
//             String fileTemp = ("catsper/italiaTablasCatsperItalia/Prueba.dat");
//        MetodosMayo12.derridaMap(red1, red2, file1);
//            for(int n = 0; n < red1.getSize(); ++n){
////                String file = ("CauloBacterNetwork/Derrida/Sin_" + red1.getNodo(n).getName() + ".dat");
//            int n = 17;
//                String file = ("catsper/MSB/FullChannels/hamming/Sin_LVAy_" +red1.getNodo(n).getName() + ".dat");
//        MetodosMayo12.derridaMap2(red1, red2, n, file);
//            }
//        ****************************************************

        // ****************************************************
        // Descomentar para encontrar los atractores.
        // ya sea la red completa, o sampleada o sin nodos
        // ****************************************************
            /*
             * Tablas deCaulobacter
             */
//            Red red = new Red(tablasBacter, nombresBacter);
//        Red red = new Red(tablasDir, nombres);
//            Red red = new Red(tablasC2, nombresCatsper);
//            Red red = new Red(tablasCatsper2012, nombresReducido);
//          int l = MetodosMayo12.findAttUnder(red,100000);
//          MetodosMayo12.AtractorYTransient(red,10);
//          red.saveAttractors("PLoSNFA/AttsNFAact.txt",l);
//          red.saveAttractors("catsper/Gordon/TCatsperAD/atractores/wt.txt",l);
//          red.saveAttractors("catsper/italiaTablasCatsperItalia/atractores/AtrGordon/wt.txt",l);
//          red.saveAttractors("catsper/MSB/FullChannels/attractors/wt.txt",l);          
//           System.out.println("Acabó el wt");
//          red.saveAttractors("catsper/Enero13/Atractores/NFACaKCActivoCada4.txt",l);
//          red.saveAttractors("catsper/pruebaAtractoresReducido29Mayo2013.txt",l);
//            red.saveAttractors("catsper/CatsperFullChannels/atractores/wt.txt",l);
////
//////         int l = MetodosMayo12.findAttAll(red);
//         for(int n = 0; n < red.getSize(); ++n){
//          int ll = MetodosMayo12.findAttUnderSinUno(red,100000,n);
//          System.out.println("Acabó el nodo " + red.getNodo(n).getName());
//          red.saveAttractors("CauloBacterNetwork/atractores/Sin_" + red.getNodo(n).getName()+ ".txt", ll);
//          red.saveAttractors("catsper/Gordon/TCatsperAD/atractores/Sin_" + red.getNodo(n).getName()+ ".txt", ll);
//          red.saveAttractors("catsper/italiaTablasCatsperItalia/atractores/AtrGordon/"+ "Sin_" + red.getNodo(n).getName()+ ".txt",l);
//          red.saveAttractors("catsper/MSB/FullChannels/attractors/sin_" + red.getNodo(n).getName() +".txt",l); 
//         }
//    }

//       int l = 0;
//        int N = red.getSize();
////        for (int n = 0; n < red.getSize(); ++n) {
//            for (int nodoDel = 0; nodoDel < 1; ++nodoDel) {
////                Red red = new Red(tablasDir, nombres);
////                for (int ka = 0; ka < N; ++ka) {
////                    red.getNodo(ka).setPresente(true);
////                }
////                red.getNodo(nodoDel).setPresente(false);
////                red.getNodo(16).setPresente(false);
////                red.getNodo(20).setPresente(false);
//                System.out.println(
////                        red.getNodo(nodoDel).getS() +
//////                        red.getNodo(16).getS() +
//////                        red.getNodo(20).getS() +
////                        "   " +
//                        red.getNodo(nodoDel).getName()
//////                         red.getNodo(16).getName()+
//////                         red.getNodo(20).getName()
//                        );
////            }
//           l = MetodosMayo12.findAttUnderSinUno(red, 10000, nodoDel);
////        }
//    red.saveAttractors("catsper/30julTablasCatsper2/atractoresCatsperActivo/Sin_" +
//            red.getNodo(8).getName() +
//            red.getNodo(16).getName() +
//             red.getNodo(20).getName() +
////            "_chinoCatsperNoActivo_"+
//             "Chino.txt", l);
//        //  VER SI HAY ALGO PROPORCIONAL AL  NUMERO DE CONEXIONES
//        //  PONER UN SCORE DE ALGO
//        //  CALCULAR LA CONECTIVIDAD PROMEDIO Y ESAS COSAS
//            }
//m nb nb nb ggjhv,jhgc,hgc
//         }
        // ********************************************************

        //**********************************
        //Descomentar para encontrar atractores con
        // una función switchada
        //**********************************
        ///creo vectores con los enteros de los ternarios
        //guardados en la carpeta renglones
        //esto es lo que se va a usar para switchar la funcion
//Red red = new Red(tablasC2, nombresCatsper);
////        Red red = new Red(tablasDir, nombres);
//        red.att.clear();
//        int N = red.getSize();
//// System.out.println("Nodo cambiado"+ "\t" +  "Renglon" +"\t" + "Atractor\tPeriodo\tcuenca");
//        for (int n = 0; n < N; ++n) {
//
////            System.out.println("\nEste es el nodo  " + n + ": " + red.getNodo(n).getName());
//
//            // Ahora comenzara a leer los datos del archivo renglones
//            if(n == 10 || n == 14){
//                n++;
//            }
//            String fileName = "catsper/MSB/Twenty/renglones/" + n + ".txt";
//            FileToRead fr = new FileToRead(fileName);
//            while (fr.hasNext()) {
//                int CompRenglon = fr.nextInt();
//                int l = MetodosMayo12.findAttUnderSwitch(red, 10000, CompRenglon, n);
////                int l = MetodosMayo12.findAttAllSwitch(red, CompRenglon, n);
////                System.out.println("voy en el renglon " + CompRenglon);
////                red.saveAttractors("Complete/at/"
////                         red.saveAttractors("Atractores22Sep11/at/"
////                        + n +"/"
////                        + "_" + CompRenglon + ".txt", l);
//        
//
////                        red.saveAttractorsLandscape("catsper/Enero13/Switch/Atractores/at/"
////                       + n +"/"
////                       + "_" + CompRenglon + ".dat", l, CompRenglon, n);  
//                         red.saveAttractorsLandscape("catsper/MSB/Twenty/switch/at/"
//                       + n +"/"
//                       + "_" + CompRenglon + ".dat", l, CompRenglon, n);  
//                        
//            }
//            fr.close();
//        }
        //****************************************************

        // ****************************************************
        // Descomentar para encontrar los calcios rojos.
        // ****************************************************
//         Red red = new Red(tablasDir, nombres);
//           int l = MetodosMayo12.findCalcioRojoUnder2(red,10000);
//           int numRegsNodoActual = MetodosMayo12.findCalcioRojoUnderSinUnNodo(red,10000);
//           int numRegsNodoActual = MetodosMayo12.findCalcioRojoUnderSin2o3(red,10000);
//           int lo = MetodosMayo12.findCalcioRojoUnderHistogram(red,100000);
//           int l = MetodosMayo12.findCalcioRojoUnderSinHVAHistogram(red,10000);
        // ****************************************************


        // ****************************************************
        // Descomentar para encontrar tiempo en llegar al atractor.
        // ****************************************************
//         Red red = new Red(tablasDir, nombres);
//           int l = MetodosMayo12.findTimeToReachAttUnder(red,10000);
//           int l = MetodosMayo12.findTimeToReachAttUnderSin2o3(red,10000);
//           int numRegsNodoActual = MetodosMayo12.findTimeToReachAttUnder(red,10000);
        // ****************************************************


        // ****************************************************
        // Descomentar para encontrar las conf. que llevan a periodo 8.
        // ****************************************************
//         Red red = new Red(tablasDir, nombres);
//          int l = MetodosMayo12.findFirstArrayToPeriod4(red,200000);
//          int l = Mewt3_ValeLVAtodos.findFirstArrayToPeriod8(red,200);
        // ****************************************************

        // ****************************************************
        // Descomentar para encontrar las conf. que llevan a periodo 8.
        // ****************************************************
//        Red red = new Red(tablasDir, nombres);
//       MetodosMayo12.findAttAndInitCondToP8(red);
//           int l = MetodosMayo12.findAllArraysToPeriod8(red);
//        MetodosMayo12.FindInitialConditionsPeriod8(red);
        
        // ****************************************************


        // ****************************************************
        // Descomentar para ver la curva de calcio
        // ****************************************************
//         Red red = new Red(tablasDir, nombres);
//            Red red = new Red(tablasC2, nombresCatsper);
//            Red red = new Red(tablasCatsper2012, nombresReducido);
//// boolean es=false;
//////////////          int l = MetodosMayo12.findFirstArrayToPeriod8(red);
//         for(int n = 1; n <= red.getSize(); ++n){
//          int c = MetodosMayo12.calciumEvolutionCatsper(red,50,1);
// double[][] estacionarios = new double[200][red.getSize()+1];
// for(int i = 1; i < red.getSize(); ++i){
// estacionarios = MetodosMayo12.calciumEvolution(red,1000,1);
// MetodosMayo12.max();
// System.out.println("hola");
//    }
//double c = MetodosMayo12.Correlacion(red, 1000, estacionarios);
//          System.out.println("\n"+ n);
//         }
//         boolean es=false;
        // *********************************************ctrum*******

        //**********************
        // MARKOV
        //**********************
//        Red red = new Red(tablasDir, nombres);
//          int l = MetodosMayo12.markov(red, 100000);

        /*
         * Esta es para evolucionar System.out.print(x[m] + "\t"); acoplando
         * la dinÃ¡mica de Kauffma String[] nombres = {
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
        "15_dCa",};n.
         * Agarramos 10 redes de parÃ¡metro, luego evolucionamos de
         * la 2 a la 9 para no tener broncas con las orillas
         * Las orillas las tratamos dentro del mÃ©todo de evoluciÃ³n.
         */

//        Red r1 = new Red(tablasC2, nombresCatsper);
//        Red r2 = new Red(tablasC2, nombresCatsper);
//        Red r3 = new Red(tablasC2, nombresCatsper);
//        Red r4 = new Red(tablasC2, nombresCatsper);
//        Red r5 = new Red(tablasC2, nombresCatsper);
//        Red r6 = new Red(tablasC2, nombresCatsper);
//        Red r7 = new Red(tablasC2, nombresCatsper);
//        Red r8 = new Red(tablasC2, nombresCatsper);
//        Red r9 = new Red(tablasC2, nombresCatsper);
//        Red r10 = new Red(tablasC2, nombresCatsper);
//        Red r11 = new Red(tablasDir, nombres);
//        Red r12 = new Red(tablasDir, nombres);
//        Red r13 = new Red(tablasDir, nombres);
//        Red r14 = new Red(tablasDir, nombres);
//        Red r15 = new Red(tablasDir, nombres);
//        Red r16 = new Red(tablasDir, nombres);
//        Red r17 = new Red(tablasDir, nombres);
//        Red r18 = new Red(tablasDir, nombres);
//        Red r19 = new Red(tablasDir, nombres);
//        Red r20 = new Red(tablasDir, nombres);
//        Red r21 = new Red(tablasDir, nombres);
//        Red r22 = new Red(tablasDir, nombres);
//        Red r23 = new Red(tablasDir, nombres);
//        Red r24 = new Red(tablasDir, nombres);
//        Red r25 = new Red(tablasDir, nombres);
//        Red r26 = new Red(tablasDir, nombres);
//        Red r27 = new Red(tablasDir, nombres);
//        Red r28 = new Red(tablasDir, nombres);
//        Red r29 = new Red(tablasDir, nombres);
//        Red r30 = new Red(tablasDir, nombres);
//        double eps;
        
//        double div =1;
//        int CI = 10000;
//        double rounder2 =0.666;
//        double rounder = 0.5;
//        int CouplerNode = 0;
        
        
        
//////        
//////  /*
//////                 * 13 de enero
//////                 * voy a hacer una corrida de 0.1 en 0.1 para ver si el redondeo afecta al acoplamiento
//////                 */
//////                for ( rounder = 0; rounder <= 1; rounder += 0.05){
//////        FileToWrite fw13enero = new FileToWrite("/home/chucho/Coupled/AcopleMayo12/DeTablasCatsper2/Isopotencial/TodoAcoplado/Transients.dat");
////      
//////        switch(CouplerNode){
//////            case 0:{
////        System.out.println("calcio" + nombres[nombres.length-1]);
////        System.out.println("epsilon\tcalcio\tflagelo");
//////        for (div = 1; div <= 2; div += .1) {
////
        
        
//            for (double eps = 0; eps <= 1; eps += 0.01) {
//                double[] l = new double[2];
//                 l = MetodosMayo12.todoCoupledItalia(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10,  CI, eps, div, rounder, rounder2, CouplerNode);
//                 System.out.println(eps  + "\t"+ l[0]+ "\t" + l[1]);
//                        }
            
            
            
//////                
//////                int l = MetodosMayo12.todoCoupledItalia(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10,  CI, eps, div);
////
//              double[] transitoriosCayTotal = new double[2];
//               transitoriosCayTotal = MetodosMayo12.todoCoupled(r1, r2, r3, 
//                      r4,
//                      r5, 
//                      r6,
//                      r7, r8, r9, r10,
//                      CI, eps, div, rounder2, rounder, nombres.length-1);
//              transitoriosCayTotal = MetodosMayo12.todoCoupledMayoria(r1, r2, r3, 
//                      r4,
//                      r5, 
//                      r6,
//                      r7, r8, r9, r10,
//                      100);
              
//              double[] mayorias = new double[2];
//              Red[][] gran = new Red[10][3];
//              for(int a = 0; a < gran.length; ++a){
//                  for(int b = 0; b < gran[a].length; ++b){
//                      gran[a][b] = new Red(tablasDir, nombres);
//                  }
//              }
//              System.out.println("empiezan las mayorias");
//              mayorias = MetodosMayo12.todoCoupledMayoriaChida(1,10,3,gran);
//              System.out.println("esto es de regla mayoria chida " + mayorias[0] );
////              
//              transitoriosCayTotal = MetodosMayo12.todoCoupled(r1, r2, r3, 
//                      r4,
//                      r5, 
//                      r6,
//                      r7,
//                      r8,
//                      r9,
//                      r10,
//                      r11, 
//                      r12, 
//                      r13, 
//                      r14,
//                      r15, 
//                      r16,
//                      r17,
//                      r18,
//                      r19,
//                      r20,
//                      r21, 
//                      r22, 
//                      r23, 
//                      r24,
//                      r25, 
//                      r26,
//                      r27,
//                      r28,
//                      r29,
//                      r30,
//                      CI, eps, div, rounder2, rounder, nombres.length-1);
//////              
//                 System.out.println(eps + "\t" + transitoriosCayTotal[0] + "\t" + transitoriosCayTotal[1]);    
//////                int l = MetodosMayo12.todoCoupled(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10,  CI, eps, div, rounder2, rounder);
//////                System.out.println("10 redes: Rounder: " + rounder + " Condiciones iniciales: " + CI + ": epsilon en " + eps +" y sigma en " + div);
//////                 fw.writeLine("epsilon: " +eps + " Condicion inicial: "+ CI + "\t transitorio promedio: " + l);
//////                fw13enero.writeLine(" " + eps + "   "+ "  " + l);
//////            }
////                }
//            }
//                
//                case 1:{
//        System.out.println("cAMP");
//        System.out.println("epsilon\tcalcio\tflagelo");
////        for (div = 1; div <= 2; div += .1) {
//
//            for (eps =0; eps <= 0.8; eps += 0.1) {
////                for(int CI = 1; CI <= 1000; CI*=10){
////                System.out.println("epsilon en " + eps + "   y sigma " + div);
////                int l = MetodosMayo12.todoCoupled(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10,  CI, eps, div);
//
//              double[] transitoriosCayTotal = new double[2];
//              transitoriosCayTotal = MetodosMayo12.todoCoupled(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, 
//                      CI, eps, div, rounder2, 
//                      rounder, 13);
//                 System.out.println(eps + "\t" + transitoriosCayTotal[0] + "\t" + transitoriosCayTotal[1]);    
////                int l = MetodosMayo12.todoCoupled(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10,  CI, eps, div, rounder2, rounder);
////                System.out.println("10 redes: Rounder: " + rounder + " Condiciones iniciales: " + CI + ": epsilon en " + eps +" y sigma en " + div);
////                 fw.writeLine("epsilon: " +eps + " Condicion inicial: "+ CI + "\t transitorio promedio: " + l);
////                fw13enero.writeLine(" " + eps + "   "+ "  " + l);
////            }
//                }
////            }
////                    case 2:{
//        System.out.println("Calcio");
//        System.out.println("epsilon\tcalcio\tflagelo");
////        for (div = 1; div <= 2; div += .1) {
//
//            for (eps =0; eps <= 0.8; eps += 0.1) {
////                for(int CI = 1; CI <= 1000; CI*=10){
////                System.out.println("epsilon en " + eps + "   y sigma " + div);
////                int l = MetodosMayo12.todoCoupled(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10,  CI, eps, div);
//
//              double[] transitoriosCayTotal = new double[2];
//              transitoriosCayTotal = MetodosMayo12.todoCoupled(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10,  
//                      CI, eps, div, rounder2, rounder, nombres.length-1);
//                 System.out.println(eps + "\t" + transitoriosCayTotal[0] + "\t" + transitoriosCayTotal[1]);    
////                int l = MetodosMayo12.todoCoupled(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10,  CI, eps, div, rounder2, rounder);
////                System.out.println("10 redes: Rounder: " + rounder + " Condiciones iniciales: " + CI + ": epsilon en " + eps +" y sigma en " + div);
////                 fw.writeLine("epsilon: " +eps + " Condicion inicial: "+ CI + "\t transitorio promedio: " + l);
////                fw13enero.writeLine(" " + eps + "   "+ "  " + l);
////            }
//                }
//            }

//    }
//             fw13enero.close();

        /*
         * Para evaluar todas las tablas y todas las combinaciones
         * de nodos eliminados
         */



//  tablasCatsper0 son las tablasYA normales
//        int iC = 10000;
//        boolean es = false;
//        for (int tablasCond = 0; tablasCond <= 3; ++tablasCond) {
//            for (int catsper = 1; catsper >= 0; --catsper) {
//                for (int chino = 1; chino >= 0; --chino) {
//
//                    String tablas = new String("catsper/tablasCatsper" + tablasCond + "/");
//
//                    System.out.println(tablas + "\t*******************************************************************");
//                    if (catsper != 1) {
//                        System.out.println("Catsper act\t");
//                    }
//                    if (chino != 1) {
//                        System.out.println("caKC act");
//                    }
//                    System.out.println("media\t\tpico\t\tamp\t\tmaxAmp\t\tminAmp\t\tfreq\t\tPhLock\t\tcaso");
//
//                    int algo = 0;
//
//                    String tablasDir = tablas;
//                    if (tablasCond == 0) {
//                        nombres[17] = "18_cAMPCC";
//                    } else {
//                        nombres[17] = "18_catsper";
//                    }
//
//                    switch (algo) {
////                    /*
////                     * Caso WT
////                     */
//                        case 0: {
//                            Red red = new Red(tablasDir, nombres);
//                            int l = MetodosMayo12.TodoDeTodo(red, iC, 1, 1, 1, catsper, chino, 1, es);
//                            int tt= red.printAttractors(l);
//                            System.out.println(tt+"\t\tWT");
////                    red.printAttractors(l);
//                        }
//
//                        /*
//                         * Caso HCN
//                         */
//                        case 1: {
//                            Red red = new Red(tablasDir, nombres);
//                            int l = MetodosMayo12.TodoDeTodo(red, iC, 0, 1, 1, catsper, chino, 1, es);
//                            int tt= red.printAttractors(l);
//                            System.out.println(tt+"\t\tHCN");
////                    red.printAttractors(l);
//                        }
//
//                        /*
//                         * Caso CaCC
//                         */
//                        case 2: {
////                    System.out.println("caso CaCC:");
////                    if(catsper !=1){
////                    System.out.print("Catsper act\t");
////                    }if(chino !=1){
////                    System.out.println("caKC act");
////                    }
//                            Red red = new Red(tablasDir, nombres);
//                            int l = MetodosMayo12.TodoDeTodo(red, iC, 1, 0, 1, catsper, chino, 1, es);
//                            int tt= red.printAttractors(l);
//                            System.out.println(tt+"\t\tCaCC");
////                    red.printAttractors(l);
//                        }
//
//                        /*
//                         * Caso CaKC mio
//                         */
//                        case 3: {
////                    System.out.println("caso CaKC original:");
////                    if(catsper !=1){
////                    System.out.print("Catsper act\t");
////                    }if(chino !=1){
////                    System.out.println("caKC act");
////                    }
//                            Red red = new Red(tablasDir, nombres);
//                            int l = MetodosMayo12.TodoDeTodo(red, iC, 1, 1, 0, catsper, chino, 1, es);
//                            int tt= red.printAttractors(l);
//                            System.out.println(tt+"\t\tCaKC original");
////                    red.printAttractors(l);
//                        }
//
//                        /*
//                         * HCN-CaCC
//                         */
//                        case 4: {
////                    System.out.println("caso HCN-CaCC:");
////                    if(catsper !=1){
////                    System.out.print("Catsper act\t");
////                    }if(chino !=1){
////                    System.out.println("caKC act");
////                    }
//                            Red red = new Red(tablasDir, nombres);
//                            int l = MetodosMayo12.TodoDeTodo(red, iC, 0, 0, 1, catsper, chino, 1, es);
//                            int tt= red.printAttractors(l);
//                            System.out.println(tt+"\t\tHCN-CaCC");
////                    red.printAttractors(l);
//                        }
//
//                        /*
//                         * caso HCN-CaKC
//                         */
//                        case 5: {
////                    System.out.println("caso HCN-CaKC:");
////                    if(catsper !=1){
////                    System.out.print("Catsper act\t");
////                    }if(chino !=1){
////                    System.out.println("caKC act");
////                    }
//                            Red red = new Red(tablasDir, nombres);
//                            int l = MetodosMayo12.TodoDeTodo(red, iC, 0, 1, 0, catsper, chino, 1, es);
//                            int tt= red.printAttractors(l);
//                            System.out.println(tt+"\t\tHCN-CaKC");
////                    red.printAttractors(l);
//                        }
//                        /*
//                         * Caso CaCC-CaKC
//                         */
//                        case 6: {
////                    System.out.println("caso CaCC_CaKC:");
////                    if(catsper !=1){
////                    System.out.print("Catsper activo\t");
////                    }if(chino !=1){
////                    System.out.println("caKC activo");
////                    }
//                            Red red = new Red(tablasDir, nombres);
//                            int l = MetodosMayo12.TodoDeTodo(red, iC, 1, 0, 0, catsper, chino, 1, es);
//                           int tt= red.printAttractors(l);
//                            System.out.println(tt+"\t\tCaCC_CaKC");
////                    red.printAttractors(l);
//                        }
//
//                        /*
//                         * Caso NFA
//                         */
//                        case 7: {
////                    System.out.println("caso NFA:");
////                    if(catsper !=1){
////                    System.out.print("Catsper act\t");
////                    }if(chino !=1){
////                    System.out.println("caKC act");
////                    }
//                            Red red = new Red(tablasDir, nombres);
//                            int l = MetodosMayo12.TodoDeTodo(red, iC, 0, 0, 0, catsper, chino, 1, es);
//                            int tt= red.printAttractors(l);
//                            System.out.println(tt+"\t\tNFA");
////                    red.printAttractors(l);
//                        }
//                    }
//                }
//            }
//        }


//        int iC = 10000;
//        boolean todos = true;
//        int nod8, nod16, nod20, catsperAct, CaKCact, pasoAct, renglon, chNode;
//        nod8 = nod16 = nod20 = catsperAct = CaKCact = pasoAct = renglon = chNode = 0;
//        int numTablas;
//        boolean es = true;
//        String write = new String();
//        String ats = new String();
//        
//        
//        
//        for (numTablas = 0; numTablas <= 3; ++numTablas) {
//            String tablas= new String("catsper/tablasCatsper" + numTablas+ "/");
//                                if (numTablas == 0) {
//                        nombres[17] = "18_cAMPCC";
//                    } else {
//                        nombres[17] = "18_catsper";
//                    }
//            Red r1 = new Red(tablas, nombres);
//            Red r2 = new Red(tablas, nombres);
//            
//            int l = MetodosMayo12.TodoCatsper(r1, r2, iC, todos, nod8, nod16, nod20,
//                catsperAct, CaKCact, pasoAct, es, renglon, chNode, write, numTablas);
//            
//            todos = false;
//                String file = new String();
//                int m = MetodosMayo12.TodoCatsper(r1, r2, iC, todos, nod8, nod16, nod20,
//                        catsperAct, CaKCact, pasoAct, es, renglon, chNode, file, numTablas);
//                
//                
//            MetodosMayo12.MapeoChistoso(r1, iC);
//        }
            
            
// ****************************************************
        // Descomentar para calcular los mapeos de Derrida Acoplados
        // ****************************************************
//           Red r1 = new Red(tablasC2, nombresCatsper);
//        Red r2 = new Red(tablasC2, nombresCatsper);
//        Red r3 = new Red(tablasC2, nombresCatsper);
//        Red r4 = new Red(tablasC2, nombresCatsper);
//        Red r5 = new Red(tablasC2, nombresCatsper);
//        Red r6 = new Red(tablasC2, nombresCatsper);
//        Red r7 = new Red(tablasC2, nombresCatsper);
//        Red r8 = new Red(tablasC2, nombresCatsper);
//        Red r9 = new Red(tablasC2, nombresCatsper);
//        Red r10 = new Red(tablasC2, nombresCatsper);
//        Red r11 = new Red(tablasC2, nombresCatsper);
//        Red r12 = new Red(tablasC2, nombresCatsper);
//        Red r13 = new Red(tablasC2, nombresCatsper);
//        Red r14 = new Red(tablasC2, nombresCatsper);
//        Red r15 = new Red(tablasC2, nombresCatsper);
//        Red r16 = new Red(tablasC2, nombresCatsper);
//        Red r17 = new Red(tablasC2, nombresCatsper);
//        Red r18 = new Red(tablasC2, nombresCatsper);
//        Red r19 = new Red(tablasC2, nombresCatsper);
//        Red r20 = new Red(tablasC2, nombresCatsper);
//            
//        int iC=10000;
//        double eps = 0;
//        double div =0;
//        double rounder = 0.5; 
//        double rounder2 = 0.66;
//        int nodoAcoplador=0;
//            
//        for(eps= 0.33; eps <=   0.35; eps += 0.01){
//             String file1 = ("catsper/italiaTablasCatsperItalia/Coupled/derridaCoupled_eps_" + eps + "_.dat");
//        MetodosMayo12.derridaMapCoupled(r1,r2,r3,r4,r5,r6,r7,r8,r9,r10,
//             r11,r12,r13,r14,r15,r16,r17,r18,r19,r20, iC,eps,div,rounder2, rounder, nodoAcoplador, file1);
//        System.out.println("acabe con eps en " + eps);
//        } 
//        ****************************************************

//            double[] prom = new double[19];
//            for(int i = 3; i <= 20; ++i){
//                FileToRead fr  = new FileToRead("ItalianCoupling/NumRedesVSTransitorio/" +i+"Redes_TransCalcio_0.33_.dat");
//                while(fr.hasNext()){
//                fr.nextDouble();
//                prom[i -3] += fr.nextDouble();
//                }
//                prom[i-3]/=1000;
//                System.out.println(i + "\t" +prom[i-3]);
//            }
            
            
         
            
    }
}
