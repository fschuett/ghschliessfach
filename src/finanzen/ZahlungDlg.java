/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ZahlungDlg.java
 *
 * Created on 21.09.2011, 17:51:47
 */
package finanzen;

import historie.Historie;
import historie.Rubrik;

import java.util.EnumSet;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import schliessfach.SchliessfachApp;
import vertrag.Vertrag;
import vertrag.Zahlung;
import vertrag.Zahlungsart;

/**
 *
 * @author fschuett
 */
public class ZahlungDlg extends javax.swing.JDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vertrag vertrag;

    /** Creates new form ZahlungDlg */
    public ZahlungDlg(java.awt.Frame parent, Vertrag v) {
        super(parent, true);
        vertrag = v;
        em = SchliessfachApp.getApplication().em;
        initComponents();
        Integer diesJahr = vertrag.getBeginnJahr();
        Query q = em.createQuery("SELECT z.leihJahr FROM Zahlung z JOIN z.vertrag v WHERE v.id=" + vertrag.getId()
                + " AND z.art=vertrag.Zahlungsart.Miete ORDER BY z.leihJahr DESC");
        List<Integer> bisherigeJahre = q.getResultList();
        if (!bisherigeJahre.isEmpty() && bisherigeJahre.get(0) != null) {
            diesJahr = bisherigeJahre.get(0) + 1;
        }
        try {
            q = em.createQuery("SELECT j FROM Jahr j WHERE j.jahr=" + diesJahr);
            Jahr neuesJahr = (Jahr) q.getSingleResult();
            jahr.setSelectedItem(neuesJahr);
        } catch (NoResultException ex) {
            jahr.setSelectedItem(null);
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        em = java.beans.Beans.isDesignTime() ? null : SchliessfachApp.getApplication().em;
        jahrQuery = java.beans.Beans.isDesignTime() ? null : em.createQuery("SELECT j FROM Jahr j ORDER BY j.jahr ASC"); // NOI18N
        jahrListe = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(jahrQuery.getResultList());
        schliesse = new javax.swing.JButton();
        hinzufuegen = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        art = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        kommentar = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        betrag = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jahr = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        vertragFeld = new javax.swing.JFormattedTextField();
        vertragFeld.setValue(vertrag);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Zahlung eintragen");

        schliesse.setFont(new java.awt.Font("Dialog", 0, 12));
        schliesse.setText("Schließen");
        schliesse.setName("schliesse"); // NOI18N
        schliesse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schliesseActionPerformed(evt);
            }
        });

        hinzufuegen.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        hinzufuegen.setText("Hinzufügen");
        hinzufuegen.setName("hinzufuegen"); // NOI18N
        hinzufuegen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hinzufuegenActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Zahlungsart", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        art.setFont(new java.awt.Font("Dialog", 0, 12));
        art.setModel(new DefaultComboBoxModel(EnumSet.allOf(Zahlungsart.class).toArray()));
        art.setSelectedItem(Zahlungsart.Miete);
        art.setName("art"); // NOI18N
        art.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                artActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setText("Kommentar:");
        jLabel1.setName("jLabel1"); // NOI18N

        kommentar.setName("kommentar"); // NOI18N
        kommentar.setValue(Zahlungsart.Miete.getKommentar());

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel2.setText("Betrag:");
        jLabel2.setName("jLabel2"); // NOI18N

        betrag.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        betrag.setName("betrag"); // NOI18N
        betrag.setValue(SchliessfachApp.getApplication().aktuelleGebuehren.get(Zahlungsart.Miete));

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel4.setText("für Jahr:");
        jLabel4.setName("jLabel4"); // NOI18N

        jahr.setFont(new java.awt.Font("Dialog", 0, 12));
        jahr.setName("jahr"); // NOI18N

        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jahrListe, jahr);
        bindingGroup.addBinding(jComboBoxBinding);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(art, 0, 342, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(40, 40, 40)
                        .addComponent(betrag, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jahr, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(kommentar, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(art, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(kommentar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jahr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(betrag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel3.setText("Vertag:");
        jLabel3.setName("jLabel3"); // NOI18N

        vertragFeld.setEditable(false);
        vertragFeld.setName("vertragFeld"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(hinzufuegen)
                        .addGap(18, 18, 18)
                        .addComponent(schliesse))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vertragFeld, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(vertragFeld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(schliesse)
                    .addComponent(hinzufuegen))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void schliesseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schliesseActionPerformed
        dispose();
    }//GEN-LAST:event_schliesseActionPerformed

    private void artActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_artActionPerformed
        if (evt.getSource() instanceof JComboBox) {
            Zahlungsart neu = (Zahlungsart) art.getSelectedItem();
            kommentar.setValue(neu.getKommentar());
            if (Zahlungsart.mieten.contains(neu)) {
                Integer diesJahr = vertrag.getBeginnJahr();
                Query q = em.createQuery("SELECT z.leihJahr FROM Zahlung z JOIN z.vertrag v WHERE v.id=" + vertrag.getId()
                        + " AND " + Zahlungsart.mietenQS("z.art") + " ORDER BY z.leihJahr DESC");
                List<Integer> bisherigeJahre = q.getResultList();
                if (!bisherigeJahre.isEmpty()) {
                    diesJahr = bisherigeJahre.get(0) + 1;
                }
                try {
                    q = em.createQuery("SELECT j FROM Jahr j WHERE j.jahr=" + diesJahr);
                    Jahr neuesJahr = (Jahr) q.getSingleResult();
                    jahr.setSelectedItem(neuesJahr);
                } catch (NoResultException ex) {
                    jahr.setSelectedItem(null);
                }
                jahr.setEnabled(true);
            } else {
                jahr.setSelectedItem(null);
                jahr.setEnabled(false);
            }
            if (Zahlungsart.auszahlung.contains(neu)) {
                betrag.setValue(-SchliessfachApp.getApplication().aktuelleGebuehren.get(neu));
            } else {
                betrag.setValue(SchliessfachApp.getApplication().aktuelleGebuehren.get(neu));
            }
        }
    }//GEN-LAST:event_artActionPerformed

    private void hinzufuegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hinzufuegenActionPerformed
        Zahlung neu = new Zahlung(vertrag, (Zahlungsart) art.getSelectedItem(), ((Number) betrag.getValue()).doubleValue());
        neu.setKommentar((String) kommentar.getValue());
        if (jahr.getSelectedItem() != null) {
            neu.setLeihJahr(((Jahr) jahr.getSelectedItem()).getJahr().intValue());
        } else {
            neu.setLeihJahr(null);
        }
        em.getTransaction().begin();
        vertrag.einzahlen(neu);
        em.persist(neu);
        Historie.anhaengen(Rubrik.ZAHLUNG, neu.getVertrag().toString(), "hinzugefügt: "+neu.toString());
        em.getTransaction().commit();
        dispose();
    }//GEN-LAST:event_hinzufuegenActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                ZahlungDlg dialog = new ZahlungDlg(new javax.swing.JFrame(), new Vertrag());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox art;
    private javax.swing.JFormattedTextField betrag;
    private javax.persistence.EntityManager em;
    private javax.swing.JButton hinzufuegen;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox jahr;
    private java.util.List jahrListe;
    private javax.persistence.Query jahrQuery;
    private javax.swing.JFormattedTextField kommentar;
    private javax.swing.JButton schliesse;
    private javax.swing.JFormattedTextField vertragFeld;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
