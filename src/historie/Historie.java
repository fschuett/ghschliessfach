package historie;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import schliessfach.SchliessfachApp;
import schueler.Schueler;
import schueler.SchuelerStatus;
import vertrag.Vertrag;

@Entity
@Table(name = "HISTORIE")
public class Historie implements Serializable {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    @Column(name="ZEITPUNKT")
    @Temporal(value = TemporalType.DATE)
    private Date zeitpunkt;
	@Column(name = "RUBRIK")
	private Rubrik rubrik;
	@Column(name = "KENNUNG")
	private String kennung;
	@Column(name = "INHALT")
	private String inhalt;

	static transient EntityManager em;
	
	public Historie() {
		this.zeitpunkt = Calendar.getInstance().getTime();
		this.rubrik = null;
		this.kennung = null;
		this.inhalt = null;
	}

	public Historie(Rubrik rubrik, String kennung, String inhalt) {
		this.zeitpunkt = Calendar.getInstance().getTime();
		this.rubrik = rubrik;
		this.kennung = kennung;
		this.inhalt = inhalt;
	}


	public static void anhaengen(Rubrik rubrik, String kennung, String inhalt) {
		if(em == null)
			em = SchliessfachApp.getApplication().em;
		if(em == null){
			Logger.getLogger(SchliessfachApp.class.getName()).log(Level.SEVERE, "Historie: EntityManager nicht gefunden.");
			return;
		}
		Historie h = new Historie(rubrik, kennung, inhalt);
		em.persist(h);
	}
	
	public Rubrik getRubrik()
	{
		return rubrik;
	}
	
	public void setRubrik(Rubrik rubrik)
	{
		this.rubrik = rubrik;
	}
	
	public String getKennung()
	{
		return kennung;
	}
	
	public void setKennung(String kennung)
	{
		this.kennung = kennung;
	}
	
	public String getInhalt()
	{
		return inhalt;
	}
	
	public void setInhalt(String inhalt)
	{
		this.inhalt = inhalt;
	}
	
	@Override
	public String toString() {
		return zeitpunkt + "," + rubrik + " (" + kennung + "," + inhalt + ")";
	}

}
