/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package schliessfach;

/**
 *
 * @author fschuett
 */
public class Schliessfachschrank implements Comparable {
    private Long id;
    private Long ersteNummer;
    private Long anzahl;
    private String ort;

    public Schliessfachschrank(){
        this(-1L,1L,12L,"neuer Standort");
    }
    
    public Schliessfachschrank(Long id, Long ersteNummer, Long anzahl, String ort){
        this.id = id;
        this.ersteNummer = ersteNummer;
        this.anzahl = anzahl;
        this.ort = ort;
    }

    @Override
    public String toString(){
        return ersteNummer+" - "+(ersteNummer+anzahl-1)+" ("+ort+")";
    }


    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the ersteNummer
     */
    public Long getErsteNummer() {
        return ersteNummer;
    }

    /**
     * @param ersteNummer the ersteNummer to set
     */
    public void setErsteNummer(Long ersteNummer) {
        this.ersteNummer = ersteNummer;
    }

    /**
     * @return the letzteNummer
     */
    public Long getLetzteNummer() {
        return ersteNummer+anzahl-1;
    }

    /**
     * @param letzteNummer the letzteNummer to set
     */
    public void setLetzteNummer(Long letzteNummer) {
        this.anzahl = letzteNummer-ersteNummer+1;
    }

    /**
     * @return the anzahl
     */
    public Long getAnzahl() {
        return anzahl;
    }

    /**
     * @param anzahl the anzahl to set
     */
    public void setAnzahl(Long anzahl) {
        this.anzahl = anzahl;
    }

    /**
     * @return the ort
     */
    public String getOrt() {
        return ort;
    }

    /**
     * @param ort the ort to set
     */
    public void setOrt(String ort) {
        this.ort = ort;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Schliessfachschrank){
            Schliessfachschrank s = (Schliessfachschrank)o;
            int result = this.ort.compareTo(s.ort);
            if(result == 0){
                result = (int) Math.signum(this.ersteNummer-s.ersteNummer);
            }
            return result;
        }
        else
            return 0;
    }
}
