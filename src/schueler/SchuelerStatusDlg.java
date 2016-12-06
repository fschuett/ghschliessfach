/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schueler;

import javax.persistence.EntityManager;
import org.jdesktop.application.Application;
import schliessfach.Schliessfach;
import schliessfach.SchliessfachApp;

/**
 *
 * @author fschuett
 */
public class SchuelerStatusDlg extends javax.swing.JDialog {

    private EntityManager em;
    private Schueler s;
    
    public SchuelerStatusDlg(java.awt.Frame parent, boolean modal, Schueler s){
        super(parent, modal);
        this.s = s == null ? new Schueler() : s;
        em = Application.getInstance(SchliessfachApp.class).em;
        initComponents();    
    }
    /**
     * Creates new form SchuelerStatusDlg
     */
    public SchuelerStatusDlg(java.awt.Frame parent, boolean modal) {
        this(parent, modal, null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        entityManager1 = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory("SCHLIESSFACH").createEntityManager();
        ok = new javax.swing.JButton();
        schuelerPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nummer = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        nachname = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        vorname = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        aktiv = new javax.swing.JCheckBox();
        abbrechen = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        ok.setText("Ok");
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });

        schuelerPanel.setLayout(new java.awt.GridLayout(4, 2));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setLabelFor(nummer);
        jLabel1.setText("Nummer:");
        schuelerPanel.add(jLabel1);

        nummer.setEditable(false);
        nummer.setText("jTextField2");
        nummer.setText(s.getNr().toString());
        schuelerPanel.add(nummer);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("Name:");
        schuelerPanel.add(jLabel2);

        nachname.setEditable(false);
        nachname.setText("jTextField1");
        nachname.setText(s.getNachName());
        schuelerPanel.add(nachname);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("Vorname:");
        schuelerPanel.add(jLabel3);

        vorname.setEditable(false);
        vorname.setText("jTextField3");
        vorname.setText(s.getVorName());
        schuelerPanel.add(vorname);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("Aktiv:");
        schuelerPanel.add(jLabel4);

        aktiv.setSelected(s.getStatus() == SchuelerStatus.AKTIV);
        schuelerPanel.add(aktiv);

        abbrechen.setText("Abbrechen");
        abbrechen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abbrechenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(abbrechen)
                .addGap(18, 18, 18)
                .addComponent(ok)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(schuelerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(schuelerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ok)
                    .addComponent(abbrechen))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
        em.getTransaction().begin();
        s.setStatus(aktiv.isSelected()?SchuelerStatus.AKTIV:SchuelerStatus.INAKTIV);
        em.getTransaction().commit();
        setVisible(false);
        dispose();
    }//GEN-LAST:event_okActionPerformed

    private void abbrechenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abbrechenActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_abbrechenActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SchuelerStatusDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SchuelerStatusDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SchuelerStatusDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SchuelerStatusDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SchuelerStatusDlg dialog = new SchuelerStatusDlg(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton abbrechen;
    private javax.swing.JCheckBox aktiv;
    private javax.persistence.EntityManager entityManager1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField nachname;
    private javax.swing.JTextField nummer;
    private javax.swing.JButton ok;
    private javax.swing.JPanel schuelerPanel;
    private javax.swing.JTextField vorname;
    // End of variables declaration//GEN-END:variables
}
