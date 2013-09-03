package schliessfach;

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
@Table(name = "KONSTANTEN")
public class Konstanten implements Serializable {

	@Id
	@Column(name = "KENNUNG")
	private String kennung;
	@Column(name = "INHALT")
	private String inhalt;

	public Konstanten() {
		this.kennung = null;
		this.inhalt = null;
	}

	public Konstanten(String kennung, String inhalt) {
		this.kennung = kennung;
		this.inhalt = inhalt;
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
		return  kennung + "," + inhalt;
	}

}
