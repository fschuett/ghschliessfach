package schueler;

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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import vertrag.Vertrag;

@Entity
@Table(name="SCHUELER")
public class Schueler implements Serializable {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="NR")
    private Long nr;
    @Column(name="STATUS")
    private SchuelerStatus status;
    @Column(name="NACHNAME")
    private String nachName;
    @Column(name="VORNAME")
    private String vorName;
    @Temporal(value = TemporalType.DATE)
    @Column(name="GEBURT")
    private Date geburt;
    @Column(name="KLASSE")
    private String klasse;
    @Column(name="LEHRER")
    private String lehrer;
    @JoinColumn(name="VERTRAEGE")
    @OneToMany(mappedBy = "schueler")
    private final List<Vertrag> vertraege = new ArrayList<Vertrag>();
    @Transient
    private static transient Calendar standardGeburt;
    @Transient
    private static transient DateFormat df;

    public Schueler() {
        this.nr = -1L;
        this.status = SchuelerStatus.NEU;
        this.nachName = null;
        this.vorName = null;
        this.geburt = null;
        this.klasse = null;
        this.lehrer = null;
    }

    public Schueler(Long nr, SchuelerStatus status, String nachName, String vorName, String geburtS, String klasse, String lehrer) {
        this.nr = nr;
        this.status = status;
        this.nachName = nachName;
        this.vorName = vorName;
        this.geburt = string2Date(geburtS);
        this.klasse = klasse;
        this.lehrer = lehrer;
    }

    public void setId(Long id) {
        this.id = id;
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

    public SchuelerStatus getStatus() {
        return status;
    }

    public void setStatus(SchuelerStatus status) {
        this.status = status;
    }

    public void setNachName(String nachName) {
        this.nachName = nachName;
    }

    public String getNachName() {
        return nachName;
    }

    public void setVorName(String vorName) {
        this.vorName = vorName;
    }

    public String getVorName() {
        return vorName;
    }

    public void setGeburt(Date geburt) {
        this.geburt = geburt;
    }

    public Date getGeburt() {
        return geburt;
    }

    public void setGeburtString(String geburt) {
        this.geburt = string2Date(geburt);
    }

    public String getGeburtString() {
        return df.format(geburt);
    }

    public void setKlasse(String klasse) {
        this.klasse = klasse;
    }

    public String getKlasse() {
        return klasse;
    }

    public void setLehrer(String lehrer) {
        this.lehrer = lehrer;
    }

    public String getLehrer() {
        return lehrer;
    }

    public List<Vertrag> getVertraege() {
        return vertraege;
    }

    @Override
    public String toString() {
        return nachName + "," + vorName + " (" + klasse + "," + lehrer + ")";
    }

    public static Date string2Date(String geburtS) {
        Calendar kalGeburt = Calendar.getInstance();
        Date result;
        try {
            kalGeburt.setTime(df.parse(geburtS));
            if(kalGeburt.get(Calendar.YEAR)<1900)
                kalGeburt.set(Calendar.YEAR, kalGeburt.get(Calendar.YEAR) + 1900);
            result = kalGeburt.getTime();
        } catch (ParseException ex) {
            Logger.getLogger(Schueler.class.getName()).log(Level.SEVERE, null, ex);
            result = standardGeburt.getTime();
        }
        return result;
    }

    public static String date2String(Date d){
        return df.format(d);
    }
    
    static {
        try {
            df = DateFormat.getDateInstance(DateFormat.SHORT);
            standardGeburt = Calendar.getInstance();
            standardGeburt.setTime(df.parse("01.01.1970"));
        } catch (ParseException ex) {
            Logger.getLogger(Schueler.class.getName()).log(Level.SEVERE, null, ex);
            standardGeburt.set(1990, 1, 1);
        }
    }
}
