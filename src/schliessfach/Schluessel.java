package schliessfach;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import vertrag.Vertrag;

@Entity
@Table(name = "SCHLUESSEL")
public class Schluessel implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "NR")
    private Long nr;
    @JoinColumn(name = "FACH")
    @ManyToOne
    private Schliessfach schliessfach;
    @JoinColumn(name = "AUSGEG")
    private Boolean ausgegeben;
    @JoinColumn(name = "ANVERTRAG")
    @ManyToOne
    private Vertrag anVertrag;
    @Column(name = "AUSGABE")
    @Temporal(value = TemporalType.DATE)
    private Date ausgabe;
    @Column(name = "KOMMENTAR")
    private String kommentar;

    public Schluessel() {
        this.nr = -1L;
        this.schliessfach = null;
        this.ausgegeben = false;
        this.anVertrag = null;
        this.ausgabe = null;
    }

    public Schluessel(Long nr, Schliessfach schliessfach, boolean ausgegeben, Vertrag anVertrag, Date ausgabe) {
        this.nr = nr;
        this.schliessfach = schliessfach;
        this.ausgegeben = ausgegeben;
        this.anVertrag = anVertrag;
        this.ausgabe = ausgabe;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNr(Long nr) {
        this.nr = nr;
    }

    public Long getNr() {
        return nr;
    }

    public void setSchliessfach(Schliessfach schliessfach) {
        this.schliessfach = schliessfach;
    }

    public Schliessfach getSchliessfach() {
        return schliessfach;
    }

    public void setAusgegeben(Boolean ausgegeben) {
        this.ausgegeben = ausgegeben;
    }

    public Boolean getAusgegeben() {
        return ausgegeben;
    }

    public void setAnVertrag(Vertrag anVertrag) {
        this.anVertrag = anVertrag;
    }

    public Vertrag getAnVertrag() {
        return anVertrag;
    }

    public void setAusgabe(Date ausgabe) {
        this.ausgabe = ausgabe;
    }

    public Date getAusgabe() {
        return ausgabe;
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

    public void ausgeben(Vertrag anVertrag, Date ausgabe) {
        this.ausgegeben = true;
        this.anVertrag = anVertrag;
        this.ausgabe = ausgabe;
    }

    public void einsammeln() {
        this.ausgegeben = false;
        this.anVertrag = null;
        this.ausgabe = null;
    }

    @Override
    public String toString() {
        return "Schlüssel [Fach/Nr(" + schliessfach + "/" + nr + ") "+kommentar+"]";
    }
}
