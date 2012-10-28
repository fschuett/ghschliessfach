package gebuehren;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import vertrag.Zahlungsart;

@Entity
@Table(name="GEBUEHR")
public class Gebuehr implements Serializable {
	@Id
        @Column(name="ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
        @Column(name="ZEITPUNKT")
	@Temporal(value = TemporalType.DATE)
	private Date zeitpunkt;
        @Column(name="ART")
	private Zahlungsart art;
        @Column(name="ABJAHR")
	private Integer abJahr;
        @Column(name="BETRAG")
	private Double betrag;

        public Gebuehr(){
            this.zeitpunkt = null;
            this.art = Zahlungsart.Miete;
            this.abJahr = -1;
            this.betrag = 0.0;
        }

        public Gebuehr(Date zeitpunkt, Zahlungsart art, Integer abJahr, Double betrag){
            this.zeitpunkt = zeitpunkt;
            this.art = art;
            this.abJahr = abJahr;
            this.betrag = betrag;
        }
        
	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setZeitpunkt(Date zeitpunkt) {
		this.zeitpunkt = zeitpunkt;
	}

	public Date getZeitpunkt() {
		return zeitpunkt;
	}

	public void setArt(Zahlungsart art) {
		this.art = art;
	}

	public Zahlungsart getArt() {
		return art;
	}

	public void setAbJahr(Integer abJahr) {
		this.abJahr = abJahr;
	}

	public Integer getAbJahr() {
		return abJahr;
	}

	public void setBetrag(Double betrag) {
		this.betrag = betrag;
	}

	public Double getBetrag() {
		return betrag;
	}

	@Override
	public String toString() {
		return "Gebühr [ID=" + id + " Zeitpunkt=" + zeitpunkt + " Art=" + art
				+ " ab Jahr=" + abJahr + " Betrag=" + betrag + "]";
	}
}
