/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package schliessfach;

import finanzen.Jahr;
import gebuehren.Gebuehr;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import schueler.Schueler;
import schueler.SchuelerStatus;
import vertrag.Vertrag;
import vertrag.Zahlung;
import vertrag.Zahlungsart;

/**
 *
 * @author fschuett
 */
public class Testdatenbank {

    public static void fuelleDatenbank(EntityManager em) throws Exception {
        em.getTransaction().begin();
        Query q = em.createQuery("Select s from Schueler s");
        if (q.getResultList().size() != 0) {
            throw new Exception("TEST: Die Datenbank ist nicht leer!");
        }
        for (int i=0;i<schliessfaecher.length;i++) {
            schliessfaecher[i].setSchrankId(i<4?0L:1L);
            em.persist(schliessfaecher[i]);
            Schluessel sl = new Schluessel(1L, schliessfaecher[i], false, null, null);
            schliessfaecher[i].getSchluessel().add(sl);
            sl = new Schluessel(2L, schliessfaecher[i], false, null, null);
            schliessfaecher[i].getSchluessel().add(sl);
        }
        for(Jahr j : jahre)
            em.persist(j);
        for (Gebuehr g : gebuehren) {
            em.persist(g);
        }
        for (int i = 0; i < schueler.length; i++) {
            Schueler s = schueler[i];
            em.persist(s);
            if (i == 0) {
                schueler[i].setStatus(SchuelerStatus.INAKTIV);
            }
            else {
                Vertrag v = new Vertrag(schueler[i], 2010, i % 2 == 0);
                schueler[i].getVertraege().add(v);
                if (i > 1) {
                    v.setSchliessfach(schliessfaecher[2 * i]);
                    schliessfaecher[2 * i].setVertrag(v);
                    if (i > 2) {
                        v.getSchliessfach().getSchluessel().get(0).ausgeben(v, new Date());
                        Zahlung z = new Zahlung(v, Zahlungsart.Kaution, 20.0);
                        v.einzahlen(z);
                        em.persist(z);
                        z = new Zahlung(v, Zahlungsart.Miete, 2010, 20.0);
                        v.einzahlen(z);
                        em.persist(z);
                        z = new Zahlung(v, Zahlungsart.Miete, 2011, 20.0);
                        v.einzahlen(z);
                        em.persist(z);
                        z = new Zahlung(v, Zahlungsart.Miete, 2012, 25.0);
                        v.einzahlen(z);
                        em.persist(z);
                    }
                }
                em.persist(v);
            }
        }

        em.getTransaction().commit();
    }

    private static Schueler[] schueler = new Schueler[]{
        new Schueler(1L, SchuelerStatus.NEU, "Duck", "Donald", "1.1.00", "7a", "DDD"),
        new Schueler(1001L, SchuelerStatus.NEU, "Duck", "Daisy", "30.12.95", "7a", "DDD"),
        new Schueler(100001L, SchuelerStatus.NEU, "Wichtig", "Willi", "30.1.01", "11ab", "WOW"),
        new Schueler(1199L, SchuelerStatus.NEU, "Mustermann", "Karl", "1.3.01", "12a", "MMM")
    };
    private static Schliessfach[] schliessfaecher = new Schliessfach[]{
        new Schliessfach(1L, 0L, "Erdgeschoss", Position.oben),
        new Schliessfach(2L, 0L, "Erdgeschoss", Position.mitteOben),
        new Schliessfach(3L, 0L, "Erdgeschoss", Position.mitteUnten),
        new Schliessfach(4L, 0L, "Erdgeschoss", Position.unten),
        new Schliessfach(5L, 0L, "Musik gegenüber", Position.oben),
        new Schliessfach(6L, 0L, "Musik gegenüber", Position.mitteOben),
        new Schliessfach(7L, 0L, "Musik gegenüber", Position.mitteUnten),
        new Schliessfach(8L, 0L, "Musik gegenüber", Position.unten)
    };
    private static Gebuehr[] gebuehren = new Gebuehr[]{
        new Gebuehr(new Date(), Zahlungsart.Miete, 2000, 20.0),
        new Gebuehr(new Date(), Zahlungsart.Kaution, 2000, 20.0),
        new Gebuehr(new Date(), Zahlungsart.KautionZweitschluessel, 2000, 10.0),
        new Gebuehr(new Date(), Zahlungsart.Ersatzschluessel, 2000, 10.0),
        new Gebuehr(new Date(), Zahlungsart.KautionZurueck, 2000, -20.0),
        new Gebuehr(new Date(), Zahlungsart.KautionZweitschluesselZurueck, 2000, -10.0),
        new Gebuehr(new Date(), Zahlungsart.MieteZurueck, 2000, -10.0),
        new Gebuehr(new Date(), Zahlungsart.MieteZurueckHalbjahr, 2000, -10.0),
        new Gebuehr(new Date(), Zahlungsart.Miete, 2012, 25.0)
    };
    private static Jahr[] jahre = new Jahr[]{
        new Jahr(2010L,"erstes Jahr"),
        new Jahr(2011L,null),
        new Jahr(2012L,null)
    };
}
