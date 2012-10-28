package vertrag;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.swing.table.AbstractTableModel;

import schliessfach.Schliessfach;
import schueler.Schueler;

@SuppressWarnings("serial")
@Entity
@Table(name="VERTRAG")
public class Vertrag implements Serializable {

    @Column(name="ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name="SCHUELER")
    @ManyToOne(cascade=CascadeType.PERSIST)
    private Schueler schueler;
    @JoinColumn(name="FACH")
    @OneToOne(cascade=CascadeType.PERSIST)
    private Schliessfach schliessfach;
    @Column(name="BEGINNJ")
    private Integer beginnJahr;
    @Column(name="BEGINNS")
    private boolean beginnSommer;
    @Column(name="ENDEJ")
    private Integer endeJahr;
    @Column(name="ENDES")
    private boolean endeSommer;
    @JoinColumn(name="ZAHLUNGEN")
    @OneToMany(mappedBy = "vertrag")
    private final List<Zahlung> zahlungen = new ArrayList<Zahlung>();

    public Vertrag() {
        this(null,null,-1,true);
    }

    public Vertrag(Schueler schueler, Integer beginnJahr, boolean beginnSommer){
        this(schueler,null,beginnJahr,beginnSommer);
    }

    public Vertrag(Schueler schueler, Schliessfach schliessfach, Integer beginnJahr, boolean beginnSommer){
        this.schueler = schueler;
        this.schliessfach = schliessfach;
        this.beginnJahr = beginnJahr;
        this.beginnSommer = beginnSommer;
        this.endeJahr = null;
        this.endeSommer = true;
    }

    public Vertrag(Schueler schueler, Schliessfach schliessfach, Zahlung zahlung, Integer beginnJahr, boolean beginnSommer){
        this.schueler = schueler;
        this.schliessfach = schliessfach;
        this.zahlungen.add(zahlung);
        this.beginnJahr = beginnJahr;
        this.beginnSommer = beginnSommer;
        this.endeJahr = null;
        this.endeSommer = true;
    }

    public void einzahlen(Zahlung zahlung){
        this.zahlungen.add(zahlung);
    }

    public void einzahlen(Zahlung[] zahlungen){
        if(zahlungen != null)
            for(Zahlung z: zahlungen)
                this.zahlungen.add(z);
    }

    public void vertragBeenden(Integer endeJahr, boolean endeSommer){
        this.endeJahr = endeJahr;
        this.endeSommer = endeSommer;
        this.schliessfach = null;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setSchueler(Schueler schueler) {
        this.schueler = schueler;
    }

    public Schueler getSchueler() {
        return schueler;
    }

    public void setSchliessfach(Schliessfach schliessfach) {
        this.schliessfach = schliessfach;
    }

    public Schliessfach getSchliessfach() {
        return schliessfach;
    }

    public void setBeginnJahr(Integer beginnJahr) {
        this.beginnJahr = beginnJahr;
    }

    public Integer getBeginnJahr() {
        return beginnJahr;
    }

    public boolean isBeginnSommer() {
        return beginnSommer;
    }

    public void setBeginnSommer(boolean beginnSommer) {
        this.beginnSommer = beginnSommer;
    }

    public void setEndeJahr(Integer endeJahr) {
        this.endeJahr = endeJahr;
    }

    public Integer getEndeJahr() {
        return endeJahr;
    }

    public boolean isEndeSommer() {
        return endeSommer;
    }

    public void setEndeSommer(boolean endeSommer) {
        this.endeSommer = endeSommer;
    }

    public List<Zahlung> getZahlungen() {
        return zahlungen;
    }

    public ZahlungenTableModel getZahlungenTableModel(){
        return new ZahlungenTableModel(zahlungen);
    }

    public class ZahlungenTableModel extends AbstractTableModel {
        private List<Zahlung> zahlungen;

        public ZahlungenTableModel(List<Zahlung> zahlungen){
            super();
            this.zahlungen = zahlungen;
        }

        @Override
        public String getColumnName(int col){
            switch(col){
                case 0:
                    return "Id";
                case 1:
                    return "Zeitpunkt";
                case 2:
                    return "Vertrag";
                case 3:
                    return "Art";
                case 4:
                    return "Leihjahr";
                default:
                    return "Betrag";
            }
        }
        
        @Override
        public int getRowCount() {
            return zahlungen.size();
        }

        @Override
        public int getColumnCount() {
            return 6;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch(columnIndex){
                case 0:
                    return zahlungen.get(rowIndex).getId();
                case 1:
                    return zahlungen.get(rowIndex).getZeitpunkt();
                case 2:
                    return zahlungen.get(rowIndex).getVertrag();
                case 3:
                    return zahlungen.get(rowIndex).getArt();
                case 4:
                    return zahlungen.get(rowIndex).getLeihJahr();
                default:
                    return zahlungen.get(rowIndex).getBetrag();
            }
        }

    }

    @Override
    public String toString() {
        return "Vertrag [ID=" + id + " Schueler=" + schueler + " Schliessfach"
                + schliessfach + " BeginnJahr=" + beginnJahr + " EndeJahr="
                + endeJahr + "]";
    }
}
