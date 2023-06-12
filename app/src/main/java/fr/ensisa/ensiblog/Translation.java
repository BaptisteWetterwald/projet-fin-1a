package fr.ensisa.ensiblog;

import java.util.ArrayList;

public class Translation {
    ArrayList<Duo> corresp=new ArrayList<Duo>();
    public void add(String fr, String eng){
        corresp.add(new Duo(fr,eng));
    }
    public void supp(String element){
        int indice;
        for(int i =0;i<= corresp.size();i++) {
            Duo c = corresp.get(i);
            if (element.equals(c.eng)) {
                indice = i;
                corresp.remove(indice);
            }
            if (element.equals(c.fr)) {
                indice = i;
                corresp.remove(indice);
            }
        }
    }
    public String translate(String mot){
        for(int i =0;i<= corresp.size();i++){
            Duo c = corresp.get(i);
            if(mot.equals(c.eng))
                return c.fr;
            if(mot.equals(c.fr))
                return c.eng;
        }
        return null;
    }

    public class Duo{
        public String eng;
        public String fr;
        public Duo(String fr, String eng) {
            this.eng = eng;
            this.fr = fr;
        }
    }
}
