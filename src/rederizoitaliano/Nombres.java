package rederizoitaliano;

import java.util.ArrayList;

public class Nombres {
    
    //Variables nativas de la clase
    
    protected ArrayList<String> nombres;
    
    //Constructor
    public Nombres(){
        nombres = new ArrayList<String>();
        nombres.add("0_sr");
        nombres.add("1_GC");
        nombres.add("2_cGMP");
        nombres.add("3_KCNG");
        nombres.add("4_dK");
        nombres.add("5_v");
        nombres.add("6_NHE");
        nombres.add("7_NCE");
        nombres.add("8_HCN");
        nombres.add("9_AC");
        nombres.add("10_LVA");
        nombres.add("11_pH");
        nombres.add("12_dNa");
        nombres.add("13_cAMP");
        nombres.add("14_HVA");
        nombres.add("16_CaP");
        nombres.add("17_CaCC");
        nombres.add("18_cAMPCC");
        nombres.add("20_dCl");
        nombres.add("22_PDE");
        nombres.add("26_CaKC");
        nombres.add("15_dCa");
    }
    
    //Getters y Setters
    public ArrayList<String> getNombres() {
        return this.nombres;
    }

    public void setNombres(ArrayList<String> nombres) {
        this.nombres = nombres;
    }
    
}
