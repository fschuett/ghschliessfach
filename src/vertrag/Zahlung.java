package vertrag;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.CascadeType;
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

@Entity
@Table(name="ZAHLUNG")
public class Zahlung implements Serializable {

    @Column(name="ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="ZEITPUNKT")
    @Temporal(value = TemporalType.DATE)
    private Date zeitpunkt;
    @JoinColumn(name="VERTRAG")
    @ManyToOne(cascade=CascadeType.PERSIST)
    private Vertrag vertrag;
    @Column(name="ART")
    private Zahlungsart art;
    @Column(name="KOMMENTAR")
    private String kommentar;
    @Column(name="LEIHJAHR")
    private Integer leihJahr;
    @Column(name="BETRAG")
    private Double betrag;

    public Zahlung(){
        this.zeitpunkt = Calendar.getInstance().getTime();
        this.vertrag = null;
        this.art = Zahlungsart.Miete;
        this.kommentar = Zahlungsart.Miete.getKommentar();
        this.leihJahr = -1;
        this.betrag = 0.0;
    }

    public Zahlung(Vertrag vertrag, Zahlungsart art, Double betrag){
        this.zeitpunkt = Calendar.getInstance().getTime();
        this.vertrag = vertrag;
        this.art = art;
        this.kommentar = art.getKommentar();
        this.leihJahr = -1;
        this.betrag = betrag;
    }

    public Zahlung(Vertrag vertrag, Integer leihJahr, Double betrag){
        this.zeitpunkt = Calendar.getInstance().getTime();
        this.vertrag = vertrag;
        this.art = Zahlungsart.Miete;
        this.kommentar = Zahlungsart.Miete.getKommentar();
        this.leihJahr = leihJahr;
        this.betrag = betrag;
    }

    public Zahlung(Vertrag vertrag, Zahlungsart art, Integer leihJahr, Double betrag){
        this.zeitpunkt = Calendar.getInstance().getTime();
        this.vertrag = vertrag;
        this.art = art;
        this.kommentar = art.getKommentar();
        this.leihJahr = leihJahr;
        this.betrag = betrag;
    }

    public Zahlung(Date zeitpunkt, Vertrag vertrag, Zahlungsart art, Integer leihJahr, Double betrag){
        this.zeitpunkt = zeitpunkt;
        this.vertrag = vertrag;
        this.art = art;
        this.kommentar = art.getKommentar();
        this.leihJahr = leihJahr;
        this.betrag = betrag;
    }

    public Long getId() {
        return id;
    }

    public Date getZeitpunkt() {
        return zeitpunkt;
    }

    public void setZeitpunkt(Date zeitpunkt) {
        this.zeitpunkt = zeitpunkt;
    }

    public Vertrag getVertrag() {
        return vertrag;
    }

    public void setVertrag(Vertrag vertrag) {
        this.vertrag = vertrag;
    }

    public Zahlungsart getArt() {
        return art;
    }

    public void setArt(Zahlungsart art) {
        this.art = art;
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

    public Integer getLeihJahr() {
        return leihJahr;
    }

    public void setLeihJahr(Integer leihJahr) {
        this.leihJahr = leihJahr;
    }

    public Double getBetrag() {
        return betrag;
    }

    public void setBetrag(Double betrag) {
        this.betrag = betrag;
    }

    @Override
    public String toString(){
        return "Zahlung ["+zeitpunkt+" ("+art+": "+betrag+") ]";
    }
}
