

package rederizoitaliano;

public class Attractor {
    int nSt; // Numero de estados
    int N;   // Numero de nodos en la red
    int[][] st; // Estados del atractor
    int basin;
    int resetBasin;
    int resetnSt;
    int[][] resetst;
    //linea chucho del transitorio
    int trans; //el transitorio del atractor
    /**
     * Construye un atractor inicialmente vacio. Los estados
     * se iran añadiendo despues.
     * @param L Numero de estados del atractor
     * @param N Numero de nodos en la red
     */
    public Attractor(int L, int n){
        nSt = L;
        N = n;
        st = new int[nSt][N];
        basin = 0;
        trans = 0;
    }
    public void addState(int[] s, int l){
        for(int i = 0; i < st[l].length; ++i){
            st[l][i] = s[i];
        }
        ++basin;
    }
    public int[] getState(int l){
        return st[l];
    }
    public int getState(int l, int i){
        return st[l][i];
    }
    //linea chucho
    //cosa que devuelve el transitorio
    public int getTrans(int trans){
        return trans;
    }
    public int getNumStates(){
        return nSt;
    }
    public boolean equals(Attractor a){
        boolean b = false;
        if(nSt != a.getNumStates()){
            return b;
        }
        else{
            for(int l = 0; l < a.getNumStates(); ++l){
                b = MetodosMayo12.arrayEquals(st[0], a.getState(l));
                if(b){
                    return b;
                }
            }
        }
        return b;
    }
    public void ppBasin(){
        ++basin;
    }
    public int getBasin(){
        return basin;
    }

    ///lineas chucho, es para resetear los atractores
    ///
    ///maldita sea

    public int resBasin(){
        resetBasin =0;
        return resetBasin;
    }

    public int resnSt(){
        resetnSt = 0;
        return resetnSt;
    }

    public int resGetState(int l, int i){
        if(st[l][i]!=0){
            return 0;
        }
        else
        return 0;
    }

}
