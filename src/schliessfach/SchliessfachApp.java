/*
 * SchliessfachApp.java
 */
package schliessfach;

import einstellungen.PrefObjs;
import gebuehren.Gebuehr;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import vertrag.Zahlungsart;

/**
 * The main class of the application.
 */
public class SchliessfachApp extends SingleFrameApplication {

    public static final String PERSISTENCE_UNIT_NAME = "SCHLIESSFACH";
    public static EntityManagerFactory factory;
    public static final boolean TEST = false;
    public EntityManager em;
    public Calendar heute;
    public Map<Zahlungsart, Double> aktuelleGebuehren = new HashMap<Zahlungsart, Double>();
    // Datenstrukturen für den Ausdruck
    public PrintService druckDienst;
    public PrintRequestAttributeSet druckAttribut;
    public static Preferences prefs;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        prefs = Preferences.userNodeForPackage(this.getClass());
        try {
            druckDienst = PrintServiceLookup.lookupDefaultPrintService();
            String druckerName = prefs.get("Drucker", null);
            if (druckerName != null) {
                PrintService[] druckDienste = null;
                druckDienste = PrintServiceLookup.lookupPrintServices(null, null);
                for (PrintService psv : druckDienste) {
                    if (druckerName.equals(psv.getName())) {
                        druckDienst = psv;
                        break;
                    }
                }
            }
            druckAttribut = (PrintRequestAttributeSet) PrefObjs.getObject(prefs, "Druckattribute");
            if (druckAttribut == null) {
                druckAttribut = new HashPrintRequestAttributeSet();
                druckAttribut.add(MediaSizeName.ISO_A4);
            }
        } catch (BackingStoreException ex) {
            Logger.getLogger(SchliessfachApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SchliessfachApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SchliessfachApp.class.getName()).log(Level.SEVERE, null, ex);
        }
       	em = factory.createEntityManager();
        heute = Calendar.getInstance();
        try {
            if (TEST) {
                Testdatenbank.fuelleDatenbank(em);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        String dbversion = getDBVersion();
        ResourceMap resourceMap = getApplication().getInstance(schliessfach.SchliessfachApp.class).getContext().getResourceMap(SchliessfachAboutBox.class);
        String dbversionrequired = resourceMap.getString("dbversion");
        if(!dbversion.equals(dbversionrequired)){
        	System.err.println("Schliessfach-Programm\n"
        			+"===================\n"
        			+"Das Schliessfach-Programm kann nicht weiter ausgeführt werden."
        			+"Gefundene Datenbank-Version: "+dbversion+"\n"
        			+"Erwartete Datenbank-Version: "+dbversionrequired+"\n\n"
        			+"Bitte aktualisieren Sie die Datenbank gemäß den README-Dateien.\n");
        	System.exit(2);
        }
        aktualisiereGebuehren(heute.get(Calendar.YEAR));
        show(new SchliessfachView(this));
    }

    @Override
    protected void shutdown() {
        if (prefs != null) {
            try {
                if (druckDienst != null) {
                    prefs.put("Drucker", druckDienst.getName());
                }
                PrefObjs.putObject(prefs, "Druckattribute", druckAttribut);
            } catch (BackingStoreException ex) {
                Logger.getLogger(SchliessfachApp.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SchliessfachApp.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SchliessfachApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (em != null) {
            em.close();
        }
        if (factory != null) {
            factory.close();
        }

    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of SchliessfachApp
     */
    public static SchliessfachApp getApplication() {
        return Application.getInstance(SchliessfachApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(SchliessfachApp.class, args);
    }

    public String getDBVersion(){
    	if(em == null)
    		return "";
    	Query q = em.createQuery("SELECT k FROM Konstanten k WHERE k.kennung='DBVERSION'");
    	Konstanten k = (Konstanten)q.getSingleResult();
    	if(k != null)
    		return k.getInhalt();
    	else
    		return "";
    }
    
    public void aktualisiereGebuehren(int jahr) {
        aktuelleGebuehren.clear();
        for (Zahlungsart z : EnumSet.allOf(Zahlungsart.class)) {
            Query q = em.createQuery("SELECT g FROM Gebuehr g WHERE g.art=vertrag.Zahlungsart." + z + " AND g.abJahr<="
                    + jahr + " ORDER BY g.abJahr ASC, g.zeitpunkt ASC");
            List<Gebuehr> gebuehren = q.getResultList();
            if (!gebuehren.isEmpty()) {
                aktuelleGebuehren.put(z, gebuehren.get(gebuehren.size() - 1).getBetrag());
            } else {
                aktuelleGebuehren.put(z, new Double(0.0));
            }
        }

    }
}
