/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package finanzen;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author fschuett
 * 
 * Schuljahre
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name="JAHRE")
public class Jahr implements Serializable {

    @Transient
    private transient static long diesesJahr = Calendar.getInstance().get(Calendar.YEAR);
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="JAHR")
    private Long jahr;
    @Column(name="KOMMENTAR")
    private String kommentar;

    public Jahr(){
        this(diesesJahr, null);
    }

    public Jahr(Long jahr, String kommentar){
        this.jahr = jahr;
        this.kommentar = kommentar;
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
     * @return the jahr
     */
    public Long getJahr() {
        return jahr;
    }

    /**
     * @param jahr the jahr to set
     */
    public void setJahr(Long jahr) {
        this.jahr = jahr;
    }

    /**
     * @return the kommentar
     */
    public String getKommentar() {
        return kommentar;
    }

    /**
     * @param kommentar the kommentar to set
     */
    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    @Override
    public String toString(){
        return jahr.toString();
    }

}
