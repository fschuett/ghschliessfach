package test;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import vertrag.Zahlung;
import vertrag.Zahlungsart;

public class Abfrage {

    public static final String PERSISTENCE_UNIT_NAME = "SCHLIESSFACH";
    public static EntityManagerFactory factory;
    public static final boolean TEST = false;
    public static EntityManager em;
    public static Calendar heute;
    public static Map<Zahlungsart, Double> aktuelleGebuehren = new HashMap<Zahlungsart, Double>();
    
	/**
	 * @param args
	 */
    public static void main(String[] args) {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
        heute = Calendar.getInstance();
        // Testabfragen
        Query q = em.createQuery("SELECT z FROM Zahlung z JOIN z.vertrag v WHERE z.leihJahr IS NULL");
        List<Object> daten = q.getResultList();
        em.getTransaction().begin();
        for(Object o : daten){
        	Zahlung z = (Zahlung) o;
        	z.setLeihJahr(z.getZeitpunkt().getYear());
        }
        em.getTransaction().commit();
        if (em != null) {
            em.close();
        }
        if (factory != null) {
            factory.close();
        }

	}

}
