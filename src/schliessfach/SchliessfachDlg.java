/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SchliessfachDlg.java
 *
 * Created on 09.09.2011, 10:05:39
 */
package schliessfach;

import java.util.Arrays;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;

/**
 *
 * @author fschuett
 */
public class SchliessfachDlg extends javax.swing.JDialog {

    private EntityManager em;
    private Object[] schrankListe;
    private static final String schraenkeQueryString = "SELECT new schliessfach.Schliessfachschrank(s.schrankId,MIN(s.nr),COUNT(s),MIN(s.ort)) FROM Schliessfach s GROUP BY s.schrankId";

    /** Creates new form SchliessfachDlg */
    public SchliessfachDlg(java.awt.Frame parent) {
        super(parent, true);
        em = SchliessfachApp.getApplication().em;
        Query q = em.createQuery(schraenkeQueryString);
        schrankListe = q.getResultList().toArray();
        Arrays.sort(schrankListe);
        initComponents();
        faecherListe.setListData(schrankListe);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        faecherListe = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        hAnfang = new javax.swing.JFormattedTextField();
        hStandort = new javax.swing.JFormattedTextField();
        faecherHinzufuegen = new javax.swing.JButton();
        hAnzahl = new javax.swing.JSpinner();
        hAutoSchluessel = new javax.swing.JCheckBox();
        dialogSchliessen = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        faecherEntfernen = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        standort_aendern = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        fachSperren = new javax.swing.JButton();
        fachFreigeben = new javax.swing.JButton();
        fachNummer = new javax.swing.JFormattedTextField();
        fachKommentar = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(schliessfach.SchliessfachApp.class).getContext().getResourceMap(SchliessfachDlg.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        faecherListe.setFont(resourceMap.getFont("faecherListe.font")); // NOI18N
        faecherListe.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        faecherListe.setName("faecherListe"); // NOI18N
        jScrollPane1.setViewportView(faecherListe);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        hAnfang.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        hAnfang.setText(resourceMap.getString("hAnfang.text")); // NOI18N
        hAnfang.setName("hAnfang"); // NOI18N

        hStandort.setText(resourceMap.getString("hStandort.text")); // NOI18N
        hStandort.setName("hStandort"); // NOI18N

        faecherHinzufuegen.setText(resourceMap.getString("faecherHinzufuegen.text")); // NOI18N
        faecherHinzufuegen.setName("faecherHinzufuegen"); // NOI18N
        faecherHinzufuegen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                faecherHinzufuegenActionPerformed(evt);
            }
        });

        hAnzahl.setFont(resourceMap.getFont("hAnzahl.font")); // NOI18N
        hAnzahl.setModel(new javax.swing.SpinnerListModel(new String[] {"12", "24", "36", "48"}));
        hAnzahl.setName("hAnzahl"); // NOI18N

        hAutoSchluessel.setSelected(true);
        hAutoSchluessel.setText(resourceMap.getString("hAutoSchluessel.text")); // NOI18N
        hAutoSchluessel.setName("hAutoSchluessel"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hAutoSchluessel)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(hAnzahl, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(hStandort, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                            .addComponent(hAnfang, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)))
                    .addComponent(faecherHinzufuegen, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(hAnfang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(hAnzahl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(hStandort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(hAutoSchluessel)
                .addGap(18, 18, 18)
                .addComponent(faecherHinzufuegen)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(schliessfach.SchliessfachApp.class).getContext().getActionMap(SchliessfachDlg.class, this);
        dialogSchliessen.setAction(actionMap.get("schliessen")); // NOI18N
        dialogSchliessen.setText(resourceMap.getString("dialogSchliessen.text")); // NOI18N
        dialogSchliessen.setName("dialogSchliessen"); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel3.border.title"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        faecherEntfernen.setText(resourceMap.getString("faecherEntfernen.text")); // NOI18N
        faecherEntfernen.setName("faecherEntfernen"); // NOI18N
        faecherEntfernen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                faecherEntfernenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(60, Short.MAX_VALUE)
                .addComponent(faecherEntfernen)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(faecherEntfernen)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel4.border.title"))); // NOI18N
        jPanel4.setName("jPanel4"); // NOI18N

        standort_aendern.setText(resourceMap.getString("standort_aendern.text")); // NOI18N
        standort_aendern.setName("standort_aendern"); // NOI18N
        standort_aendern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                standort_aendernActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(standort_aendern)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(standort_aendern)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanel5.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel5.border.titleFont"))); // NOI18N
        jPanel5.setName("jPanel5"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        fachSperren.setText(resourceMap.getString("fachSperren.text")); // NOI18N
        fachSperren.setToolTipText(resourceMap.getString("fachSperren.toolTipText")); // NOI18N
        fachSperren.setName("fachSperren"); // NOI18N
        fachSperren.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fachSperrenActionPerformed(evt);
            }
        });

        fachFreigeben.setText(resourceMap.getString("fachFreigeben.text")); // NOI18N
        fachFreigeben.setToolTipText(resourceMap.getString("fachFreigeben.toolTipText")); // NOI18N
        fachFreigeben.setName("fachFreigeben"); // NOI18N
        fachFreigeben.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fachFreigebenActionPerformed(evt);
            }
        });

        fachNummer.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        fachNummer.setName("fachNummer"); // NOI18N

        fachKommentar.setText(resourceMap.getString("fachKommentar.text")); // NOI18N
        fachKommentar.setName("fachKommentar"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(fachSperren)
                        .addGap(18, 18, 18)
                        .addComponent(fachFreigeben))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(fachKommentar)
                            .addComponent(fachNummer, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(fachNummer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(fachKommentar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fachSperren)
                    .addComponent(fachFreigeben))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dialogSchliessen)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dialogSchliessen)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void faecherEntfernenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_faecherEntfernenActionPerformed
        Schliessfachschrank schrank = (Schliessfachschrank) faecherListe.getSelectedValue();
        if (schrank == null) {
            return;
        }
        Query q = em.createQuery("SELECT v FROM Vertrag v WHERE v.schliessfach IS NOT NULL AND v.schliessfach.nr BETWEEN :ersteNummer AND :letzteNummer");
        q.setParameter("ersteNummer", schrank.getErsteNummer());
        q.setParameter("letzteNummer", schrank.getLetzteNummer());
        if (!q.getResultList().isEmpty()) {
            JOptionPane.showMessageDialog(this.getParent(), "Der Schliessfachbereich kann nicht entfernt werden!\nEs existiert noch mindestens ein Vertrag für eines der Schließfächer.", "Schließfächer entfernen", JOptionPane.ERROR_MESSAGE);
            return;
        }
        em.getTransaction().begin();
        q = em.createQuery("DELETE FROM Schluessel s WHERE s.schliessfach.nr BETWEEN :ersteNummer AND :letzteNummer");
        q.setParameter("ersteNummer", schrank.getErsteNummer());
        q.setParameter("letzteNummer", schrank.getLetzteNummer());
        q.executeUpdate();
        q = em.createQuery("DELETE FROM Schliessfach s WHERE s.nr BETWEEN :ersteNummer AND :letzteNummer");
        q.setParameter("ersteNummer", schrank.getErsteNummer());
        q.setParameter("letzteNummer", schrank.getLetzteNummer());
        q.executeUpdate();
        em.getTransaction().commit();
        faecherAktualisieren();
    }//GEN-LAST:event_faecherEntfernenActionPerformed

    private void faecherHinzufuegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_faecherHinzufuegenActionPerformed
        int ersteNummer = ((Number) hAnfang.getValue()).intValue();
        int anzahl = Integer.parseInt((String) hAnzahl.getValue());
        // Ist die erste Nummer gültig
        if (ersteNummer <= 0 || ersteNummer % 12 != 1) {
            JOptionPane.showMessageDialog(this.getParent(), "Die erste Zahl ist ungültig.\nSie muss den Anfang eines 12er-Bereiches markieren.", "Erste Nummer ungültig", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Sind die Nummern frei?
        Query q = em.createQuery("SELECT s FROM Schliessfach s WHERE s.nr BETWEEN :ersteNummer AND :letzteNummer");
        q.setParameter("ersteNummer", ersteNummer);
        q.setParameter("letzteNummer", ersteNummer + anzahl - 1);
        if (!q.getResultList().isEmpty()) {
            JOptionPane.showMessageDialog(this.getParent(), "Mindestens eine der Schliessfachnummern des Schrankes ist bereits vergeben.", "Bereich ungültig", JOptionPane.ERROR_MESSAGE);
            return;
        }
        q = em.createQuery("SELECT MAX(s.schrankId) FROM Schliessfach s");
        Long neuerSchrank = (Long) q.getSingleResult();
        if (neuerSchrank == null) {
            neuerSchrank = 1L;
        } else {
            neuerSchrank++;
        }
        em.getTransaction().begin();
        Schliessfach neu;
        for (int i = ersteNummer; i < ersteNummer + anzahl; i++) {
            neu = new Schliessfach(new Long(i), neuerSchrank, hStandort.getText(), Position.getStandardPosition(i));
            em.persist(neu);
            if (hAutoSchluessel.isSelected()) {
                Schluessel s1 = new Schluessel(1L, neu, false, null, null), s2 = new Schluessel(2L, neu, false, null, null);
                neu.getSchluessel().add(s1);
                neu.getSchluessel().add(s2);
            }
        }
        em.getTransaction().commit();
        faecherAktualisieren();
    }//GEN-LAST:event_faecherHinzufuegenActionPerformed

    private void standort_aendernActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_standort_aendernActionPerformed
        Schliessfachschrank schrank = (Schliessfachschrank) faecherListe.getSelectedValue();
        if (schrank == null) {
            return;
        }
        String neuerName = JOptionPane.showInputDialog(this.getParent(), "Bitte geben Sie den neuen Standort ein: ", schrank.getOrt());
        if (neuerName == null || "".equals(neuerName) || schrank.getOrt().equals(neuerName)) {
            return;
        }
        em.getTransaction().begin();
        Query q = em.createQuery("UPDATE Schliessfach s SET s.ort='" + neuerName + "' WHERE s.schrankId=" + schrank.getId());
        q.executeUpdate();
        em.getTransaction().commit();
        faecherAktualisieren();
    }//GEN-LAST:event_standort_aendernActionPerformed

    private void fachSperrenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fachSperrenActionPerformed
        if (fachNummer.getValue() == null) {
            return;
        }
        if (fachKommentar.getText() == null) {
            JOptionPane.showMessageDialog(this.getParent(), "Bitte geben Sie eine Begründung ein.", "Schließfach sperren", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Query q = em.createQuery("SELECT s FROM Schliessfach s WHERE s.nr=" + fachNummer.getValue());
        try {
            Schliessfach s = (Schliessfach) q.getSingleResult();
            if (s.getVertrag() != null) {
                JOptionPane.showMessageDialog(this.getParent(), "Das Schließfach ist an den Vertrag " + s.getVertrag() + " gebunden.", "Schließfach sperren", JOptionPane.ERROR_MESSAGE);
                return;
            }
            s.setKommentar((String) fachKommentar.getText());
            JOptionPane.showMessageDialog(this.getParent(), "Das Schließfach mit der Nummer " + fachNummer.getValue() + " wurde\nmit dem Kommentar " + s.getKommentar() + " gesperrt.", "Schließfach sperren", JOptionPane.INFORMATION_MESSAGE);
            fachKommentar.setValue(null);
            fachNummer.setValue(null);
            faecherAktualisieren();
        } catch (NoResultException e) {
            JOptionPane.showMessageDialog(this.getParent(), "Ein Schließfach mit der Nummer " + fachNummer.getValue() + " existiert nicht.", "Schließfach sperren", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_fachSperrenActionPerformed

    private void fachFreigebenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fachFreigebenActionPerformed
        if (fachNummer.getValue() == null) {
            return;
        }
        Query q = em.createQuery("SELECT s FROM Schliessfach s WHERE s.nr=" + fachNummer.getValue());
        try {
            Schliessfach s = (Schliessfach) q.getSingleResult();
            s.setKommentar(null);
            JOptionPane.showMessageDialog(this.getParent(), "Das Schließfach mit der Nummer " + fachNummer.getValue() + " wurde freigegeben.", "Schließfach freigeben", JOptionPane.INFORMATION_MESSAGE);
            fachNummer.setValue(null);
            fachKommentar.setValue(null);
            faecherAktualisieren();
        } catch (NoResultException e) {
            JOptionPane.showMessageDialog(this.getParent(), "Ein Schließfach mit der Nummer " + fachNummer.getValue() + " existiert nicht.", "Schließfach freigeben", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_fachFreigebenActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                SchliessfachDlg dialog = new SchliessfachDlg(new javax.swing.JFrame());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    @Action
    public void schliessen() {
        dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton dialogSchliessen;
    private javax.swing.JButton fachFreigeben;
    private javax.swing.JFormattedTextField fachKommentar;
    private javax.swing.JFormattedTextField fachNummer;
    private javax.swing.JButton fachSperren;
    private javax.swing.JButton faecherEntfernen;
    private javax.swing.JButton faecherHinzufuegen;
    private javax.swing.JList faecherListe;
    private javax.swing.JFormattedTextField hAnfang;
    private javax.swing.JSpinner hAnzahl;
    private javax.swing.JCheckBox hAutoSchluessel;
    private javax.swing.JFormattedTextField hStandort;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton standort_aendern;
    // End of variables declaration//GEN-END:variables

    private void faecherAktualisieren() {
        Query q = em.createQuery(schraenkeQueryString);
        schrankListe = q.getResultList().toArray();
        Arrays.sort(schrankListe);
        faecherListe.setListData(schrankListe);
    }
}
