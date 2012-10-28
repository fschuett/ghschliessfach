package schliessfach;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import vertrag.Vertrag;

@Entity
@Table(name="SCHLIESSFACH")
public class Schliessfach implements Serializable {

	@Id
        @Column(name="ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
        @Column(name="SCHRANKID")
        private Long schrankId;
        @Column(name="NR")
	private Long nr;
        @Column(name="ORT")
	private String ort;
        @Column(name="POSITION")
	private Position position;
        @JoinColumn(name="SCHLUESSEL")
	@OneToMany(cascade=CascadeType.ALL, mappedBy="schliessfach")
	private final List<Schluessel> schluessel = new ArrayList<Schluessel>();
        @JoinColumn(name="VERTRAG")
	@OneToOne
	private Vertrag vertrag;
        @Column(name="KOMMENTAR")
        private String kommentar;

        public Schliessfach(){
            this.nr = -1L;
            this.schrankId = -1L;
            this.ort = null;
            this.position = null;
            this.vertrag = null;
        }

        public Schliessfach(Long nr, Long schrankId, String geschoss, Position position){
            this.nr = nr;
            this.schrankId = schrankId;
            this.ort = geschoss;
            this.position = position;
            this.vertrag = null;
        }

        public Long getId() {
		return id;
	}
	public void setNr(Long nr) {
		this.nr = nr;
	}
	public Long getNr() {
		return nr;
	}
	public void setOrt(String ort) {
		this.ort = ort;
	}
	public String getOrt() {
		return ort;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	public Position getPosition() {
		return position;
	}
	public List<Schluessel> getSchluessel(){
		return schluessel;
	}
        public void setVertrag(Vertrag vertrag){
            this.vertrag = vertrag;
        }
	public Vertrag getVertrag(){
		return vertrag;
	}
	
	@Override
	public String toString(){
		return "Nr="+nr+" "+ort+" Position="+position;
	}

    /**
     * @return the schrankId
     */
    public Long getSchrankId() {
        return schrankId;
    }

    /**
     * @param schrankId the schrankId to set
     */
    public void setSchrankId(Long schrankId) {
        this.schrankId = schrankId;
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
}
