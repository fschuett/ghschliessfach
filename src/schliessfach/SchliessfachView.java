/*
 * SchliessfachView.java
 */
package schliessfach;

import drucken.DruckVorschauDlg;
import drucken.DruckerEinstellung;
import finanzen.Jahr;
import finanzen.JahrVerwaltungDlg;
import finanzen.ZahlungDlg;
import gebuehren.GebuehrenDlg;
import historie.Historie;
import historie.Rubrik;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.swing.DefaultListModel;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.jdesktop.beansbinding.Binding.SyncFailure;
import schliessfach.dialoge.SchliessfachAuswahlDlg;
import schliessfach.dialoge.SuchDialog;
import schueler.Schueler;
import schueler.SchuelerImportDlg;
import schueler.SchuelereingabeDlg;
import schueler.SchuelernummernImportDlg;
import vertrag.Vertrag;
import vertrag.VertragDlg;
import vertrag.Zahlung;
import vertrag.Zahlungsart;
import javax.swing.JSeparator;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;

/**
 * The application's main frame.
 */
public class SchliessfachView extends FrameView {

	public Vector<String> klasseModel = new Vector<String>();
	public boolean klassenlisteIstAktuell = false;
	public DefaultListModel schuelerModel = new DefaultListModel();
	public boolean schuelerlisteIstAktuell = false;
	public Schueler aktuellerSchueler = null;
	private boolean isInitializing = false;

	public SchliessfachView(SingleFrameApplication app) {
		super(app);
		isInitializing = true;
		initComponents();
		isInitializing = false;

		schluesselTabelle.getModel().addTableModelListener(
				new TableModelListener() {

					@Override
					public void tableChanged(TableModelEvent evt) {
						if (evt.getColumn() == 3 && evt.getFirstRow() > -1) {
							TableModel model = (TableModel) evt.getSource();
							Boolean b = (Boolean) model.getValueAt(
									evt.getFirstRow(), 3);
							Iterator<Vertrag> it = vertragListe.iterator();
							Vertrag v = null;
							Long sfNr = (Long) schluesselTabelle.getModel()
									.getValueAt(evt.getFirstRow(), 0);
							Long snr = (Long) schluesselTabelle.getModel()
									.getValueAt(evt.getFirstRow(), 1);
							while (it.hasNext()) {
								if ((v = it.next()).getSchliessfach().getNr() == sfNr) {
									break;
								} else {
									v = null;
								}
							}
							if (b != null && b && v != null) {
								String[] auswahlWerte = new String[] {
										"Schlüsselkaution",
										"Zweitschlüsselkaution",
										"Keine Kaution" };
								Object auswahl = JOptionPane.showInputDialog(
										schluesselTabelle.getParent(),
										"Bitte geben Sie den gewünschten Kautionsbetrag ein.",
										"Schlüsselkaution",
										JOptionPane.QUESTION_MESSAGE, null,
										auswahlWerte, auswahlWerte[0]);
								if (auswahl != null
										&& auswahl != auswahlWerte[2]) {
									Zahlung z = new Zahlung();
									if (auswahl == auswahlWerte[0]) {
										z.setArt(Zahlungsart.Kaution);
									} else {
										z.setArt(Zahlungsart.KautionZweitschluessel);
									}
									z.setBetrag(SchliessfachApp
											.getApplication().aktuelleGebuehren
											.get(z.getArt()));
									z.setZeitpunkt(SchliessfachApp
											.getApplication().heute.getTime());
									z.setKommentar(z.getArt().getKommentar());
									em.getTransaction().begin();
									em.persist(z);
									z.setVertrag(v);
									Historie.anhaengen(Rubrik.ZAHLUNG, z
											.getVertrag().toString(),
											"hinzugefügt: " + z.toString());
									v.einzahlen(z);
									em.getTransaction().commit();
									schluesselTabelle.getModel().setValueAt(
											z.getKommentar(),
											evt.getFirstRow(), 4);
									aktualisiereBetraege();
								}
								Query q = em
										.createQuery("SELECT s FROM Schluessel s WHERE s.schliessfach.nr="
												+ sfNr + " AND s.nr=" + snr);
								try {
									Schluessel s = (Schluessel) q
											.getSingleResult();
									em.getTransaction().begin();
									s.setAnVertrag(v);
									em.getTransaction().commit();
									aktualisiereKautionenliste();
								} catch (NoResultException ex) {
									ex.printStackTrace();
									JOptionPane.showMessageDialog(
											schluesselTabelle.getParent(),
											"Der Schlüssel wurde nicht gefunden.",
											"Schlüssel ausgeben",
											JOptionPane.ERROR_MESSAGE);
								}
								schluesselTabelle.getModel().setValueAt(
										SchliessfachApp.getApplication().heute
												.getTime(), evt.getFirstRow(),
										2);
							} else if (b != null && !b && v != null) {
								String[] auswahlWerte = new String[] {
										"Schlüsselkaution zurück",
										"Zweitschlüsselkaution zurück",
										"Keine Kaution zurück" };
								Object auswahl = JOptionPane.showInputDialog(
										schluesselTabelle.getParent(),
										"Bitte geben Sie den zurückgezahlten Kautionsbetrag ein.",
										"Schlüsselkaution zurück",
										JOptionPane.QUESTION_MESSAGE, null,
										auswahlWerte, auswahlWerte[0]);
								if (auswahl != null
										&& auswahl != auswahlWerte[2]) {
									Zahlung z = new Zahlung();
									if (auswahl == auswahlWerte[0]) {
										z.setArt(Zahlungsart.KautionZurueck);
									} else {
										z.setArt(Zahlungsart.KautionZweitschluesselZurueck);
									}
									z.setBetrag(SchliessfachApp
											.getApplication().aktuelleGebuehren
											.get(z.getArt()));
									z.setZeitpunkt(SchliessfachApp
											.getApplication().heute.getTime());
									z.setKommentar(z.getArt().getKommentar());
									em.getTransaction().begin();
									em.persist(z);
									z.setVertrag(v);
									Historie.anhaengen(Rubrik.ZAHLUNG, z
											.getVertrag().toString(),
											"hinzugefügt: " + z.toString());
									v.einzahlen(z);
									em.getTransaction().commit();
									aktualisiereBetraege();
								}
								Query q = em
										.createQuery("UPDATE Schluessel s SET s.anVertrag="
												+ null
												+ " WHERE s.schliessfach.nr="
												+ sfNr + " AND s.nr=" + snr);
								em.getTransaction().begin();
								q.executeUpdate();
								em.getTransaction().commit();
								aktualisiereKautionenliste();
								schluesselTabelle.getModel().setValueAt(null,
										evt.getFirstRow(), 2);
								schluesselTabelle.getModel().setValueAt(null,
										evt.getFirstRow(), 4);
							}
						}
					}
				});
		// status bar initialization - message timeout, idle icon and busy
		// animation, etc
		ResourceMap resourceMap = getResourceMap();
		int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
		messageTimer = new Timer(messageTimeout, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				statusMessageLabel.setText("");
			}
		});
		messageTimer.setRepeats(false);
		int busyAnimationRate = resourceMap
				.getInteger("StatusBar.busyAnimationRate");
		for (int i = 0; i < busyIcons.length; i++) {
			busyIcons[i] = resourceMap
					.getIcon("StatusBar.busyIcons[" + i + "]");
		}
		busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
				statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
			}
		});
		idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
		statusAnimationLabel.setIcon(idleIcon);
		progressBar.setVisible(false);

		// connecting action tasks to status bar via TaskMonitor
		TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
		taskMonitor
				.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

					@Override
					public void propertyChange(
							java.beans.PropertyChangeEvent evt) {
						String propertyName = evt.getPropertyName();
						if ("started".equals(propertyName)) {
							if (!busyIconTimer.isRunning()) {
								statusAnimationLabel.setIcon(busyIcons[0]);
								busyIconIndex = 0;
								busyIconTimer.start();
							}
							progressBar.setVisible(true);
							progressBar.setIndeterminate(true);
						} else if ("done".equals(propertyName)) {
							busyIconTimer.stop();
							statusAnimationLabel.setIcon(idleIcon);
							progressBar.setVisible(false);
							progressBar.setValue(0);
						} else if ("message".equals(propertyName)) {
							String text = (String) (evt.getNewValue());
							statusMessageLabel.setText((text == null) ? ""
									: text);
							messageTimer.restart();
						} else if ("progress".equals(propertyName)) {
							int value = (Integer) (evt.getNewValue());
							progressBar.setVisible(true);
							progressBar.setIndeterminate(false);
							progressBar.setValue(value);
						}
					}
				});
		aktualisiereJahrliste();
	}

	@Action
	public void showAboutBox() {
		if (aboutBox == null) {
			JFrame mainFrame = SchliessfachApp.getApplication().getMainFrame();
			aboutBox = new SchliessfachAboutBox(mainFrame);
			aboutBox.setLocationRelativeTo(mainFrame);
		}
		aboutBox.setVisible(true);
	}

	public void showHinweiseBox() {
		try {
			Scanner in = new Scanner(new File("Hinweise.txt"));
			String s = in.nextLine() + "\n";
			while (in.hasNext())
				s = s + in.nextLine() + "\n";
			JTextArea inhalt = new JTextArea(s, 20, 60);
			inhalt.setEditable(false);
			JScrollPane scroller = new JScrollPane(inhalt,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			javax.swing.JOptionPane.showMessageDialog(SchliessfachApp
					.getApplication().getMainFrame(), scroller);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {
		bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

		mainPanel = new javax.swing.JPanel();
		jPanel1 = new javax.swing.JPanel();
		auswahlKlasse = new javax.swing.JComboBox();
		auswahlJahr = new javax.swing.JComboBox();
		jahrVerwalten = new javax.swing.JButton();
		jLabel12 = new javax.swing.JLabel();
		jLabel13 = new javax.swing.JLabel();
		jLabel16 = new javax.swing.JLabel();
		anzahlFrei = new javax.swing.JFormattedTextField();
		jLabel17 = new javax.swing.JLabel();
		anzahlGesamt = new javax.swing.JFormattedTextField();
		jLabel18 = new javax.swing.JLabel();
		jSplitPane1 = new javax.swing.JSplitPane();
		jPanel2 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		klassenListe = new javax.swing.JList();
		jPanel3 = new javax.swing.JPanel();
		jPanel4 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		nachName = new javax.swing.JFormattedTextField();
		vorName = new javax.swing.JFormattedTextField();
		nr = new javax.swing.JFormattedTextField();
		gebuehrenBetrag = new javax.swing.JFormattedTextField();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		klasse = new javax.swing.JFormattedTextField();
		lehrer = new javax.swing.JFormattedTextField();
		jLabel7 = new javax.swing.JLabel();
		geburt = new javax.swing.JFormattedTextField();
		jLabel11 = new javax.swing.JLabel();
		kautionBetrag = new javax.swing.JFormattedTextField();
		sAktionenPanel = new javax.swing.JPanel();
		einAusZahlung = new javax.swing.JButton();
		jLabel10 = new javax.swing.JLabel();
		detailsTabs = new javax.swing.JTabbedPane();
		vertragSchluesselPanel = new javax.swing.JPanel();
		jPanel5 = new javax.swing.JPanel();
		jScrollPane2 = new javax.swing.JScrollPane();
		vertragTabelle = new javax.swing.JTable();
		jPanel7 = new javax.swing.JPanel();
		jScrollPane3 = new javax.swing.JScrollPane();
		schluesselTabelle = new javax.swing.JTable();
		vAktionenPanel = new javax.swing.JPanel();
		jLabel8 = new javax.swing.JLabel();
		vertragNeu = new javax.swing.JButton();
		vertragEntfernen = new javax.swing.JButton();
		vertragBearbeiten = new javax.swing.JButton();
		jLabel9 = new javax.swing.JLabel();
		schluesselNeu = new javax.swing.JButton();
		schluesselEntfernen = new javax.swing.JButton();
		vertragZuordnen = new javax.swing.JButton();
		vertragFreigeben = new javax.swing.JButton();
		zahlungenPanel = new javax.swing.JPanel();
		zAktionenPanel = new javax.swing.JPanel();
		jLabel14 = new javax.swing.JLabel();
		kautionNeu = new javax.swing.JButton();
		kautionEntfernen = new javax.swing.JButton();
		jLabel15 = new javax.swing.JLabel();
		mieteNeu = new javax.swing.JButton();
		mieteEntfernen = new javax.swing.JButton();
		zKautionenPanel = new javax.swing.JPanel();
		jScrollPane4 = new javax.swing.JScrollPane();
		kautionenTabelle = new javax.swing.JTable();
		zMietenPanel = new javax.swing.JPanel();
		jScrollPane5 = new javax.swing.JScrollPane();
		mietenTabelle = new javax.swing.JTable();
		menuBar = new javax.swing.JMenuBar();
		javax.swing.JMenu dateiMenu = new javax.swing.JMenu();
		miImportieren = new javax.swing.JMenuItem();
		miNummernImportieren = new javax.swing.JMenuItem();
		miDrucker = new javax.swing.JMenuItem();
		javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
		suchenMenu = new javax.swing.JMenu();
		sucheSchueler = new javax.swing.JMenuItem();
		sucheSchluessel = new javax.swing.JMenuItem();
		verwaltungMenu = new javax.swing.JMenu();
		verwaltungFach = new javax.swing.JMenuItem();
		verwaltungSchluessel = new javax.swing.JMenuItem();
		verwaltungGebuehren = new javax.swing.JMenuItem();
		verwaltungSchueler = new javax.swing.JMenuItem();
		jahrVerwaltung = new javax.swing.JMenuItem();
		listenMenu = new javax.swing.JMenu();
		miKlassenliste = new javax.swing.JMenuItem();
		kautionsListe = new javax.swing.JMenuItem();
		schluesselFehlliste = new javax.swing.JMenuItem();
		offenePostenListe = new JMenuItem();
		ausgegebeneSchluesselListe = new JMenuItem();
		javax.swing.JMenu helpMenu = new javax.swing.JMenu();
		javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
		statusPanel = new javax.swing.JPanel();
		javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
		statusMessageLabel = new javax.swing.JLabel();
		statusAnimationLabel = new javax.swing.JLabel();
		progressBar = new javax.swing.JProgressBar();
		em = java.beans.Beans.isDesignTime() ? null : SchliessfachApp
				.getApplication().em;
		schuelerVertraegeQuery = java.beans.Beans.isDesignTime() ? null
				: em.createQuery(
						org.jdesktop.application.Application
								.getInstance(schliessfach.SchliessfachApp.class)
								.getContext()
								.getResourceMap(SchliessfachView.class)
								.getString("schuelerVertraegeQuery.query"))
						.setParameter("schuelerId", -1);
		vertragListe = java.beans.Beans.isDesignTime() ? java.util.Collections
				.emptyList()
				: org.jdesktop.observablecollections.ObservableCollections
						.observableList(schuelerVertraegeQuery.getResultList());
		gebuehrQuery = java.beans.Beans.isDesignTime() ? null
				: em.createQuery("SELECT SUM(z.betrag) FROM Zahlung z JOIN z.vertrag v JOIN v.schueler s WHERE "
						+ Zahlungsart.gebuehrenQS("s.id=:schuelerId AND z.art"));
		schluesselQuery = java.beans.Beans.isDesignTime() ? null
				: em.createQuery(
						org.jdesktop.application.Application
								.getInstance(schliessfach.SchliessfachApp.class)
								.getContext()
								.getResourceMap(SchliessfachView.class)
								.getString("schluesselQuery.query"))
						.setParameter("schuelerId", -1);
		schluesselListe = java.beans.Beans.isDesignTime() ? java.util.Collections
				.emptyList()
				: org.jdesktop.observablecollections.ObservableCollections
						.observableList(new java.util.LinkedList(
								schluesselQuery.getResultList()));
		org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
				.getInstance(schliessfach.SchliessfachApp.class).getContext()
				.getResourceMap(SchliessfachView.class);
		jahrQuery = java.beans.Beans.isDesignTime() ? null : em
				.createQuery(resourceMap.getString("jahrQuery.query")); // NOI18N
		jahrListe = java.beans.Beans.isDesignTime() ? java.util.Collections
				.emptyList()
				: org.jdesktop.observablecollections.ObservableCollections
						.observableList(new java.util.LinkedList(jahrQuery
								.getResultList()));
		kautionQuery = java.beans.Beans.isDesignTime() ? null
				: em.createQuery("SELECT SUM(z.betrag) FROM Zahlung z JOIN z.vertrag v JOIN v.schueler s WHERE "
						+ Zahlungsart.kautionenQS("s.id=:schuelerId AND z.art"));
		kautionenQuery = java.beans.Beans.isDesignTime() ? null
				: em.createQuery(
						"SELECT z FROM Zahlung z JOIN z.vertrag v WHERE "
								+ Zahlungsart
										.kautionenQS("v.schueler.id=:schuelerId AND z.art"))
						.setParameter("schuelerId", -1);
		kautionenListe = java.beans.Beans.isDesignTime() ? java.util.Collections
				.emptyList()
				: org.jdesktop.observablecollections.ObservableCollections
						.observableList(kautionenQuery.getResultList());
		mietenQuery = java.beans.Beans.isDesignTime() ? null
				: em.createQuery(
						"SELECT z FROM Zahlung z JOIN z.vertrag v WHERE "
								+ Zahlungsart
										.mietenQS("v.schueler.id=:schuelerId1 AND z.art")
								+ " OR "
								+ Zahlungsart
										.gebuehrenQS("v.schueler.id=:schuelerId2 AND z.art"))
						.setParameter("schuelerId1", -1)
						.setParameter("schuelerId2", -1);
		mietenListe = java.beans.Beans.isDesignTime() ? java.util.Collections
				.emptyList()
				: org.jdesktop.observablecollections.ObservableCollections
						.observableList(mietenQuery.getResultList());
		anzahlFreiQuery = java.beans.Beans.isDesignTime() ? null : em
				.createQuery(resourceMap.getString("anzahlFreiQuery.query")); // NOI18N
		anzahlGesamtQuery = java.beans.Beans.isDesignTime() ? null : em
				.createQuery(resourceMap.getString("anzahlGesamtQuery.query")); // NOI18N

		mainPanel.setName("mainPanel"); // NOI18N

		jPanel1.setName("jPanel1"); // NOI18N

		auswahlKlasse.setName("auswahlKlasse"); // NOI18N
		auswahlKlasse.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				auswahlKlasseActionPerformed(evt);
			}
		});
		auswahlKlasse.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusGained(java.awt.event.FocusEvent evt) {
				auswahlKlasseFocusGained(evt);
			}
		});

		auswahlJahr.setName("auswahlJahr"); // NOI18N

		org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings
				.createJComboBoxBinding(
						org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
						jahrListe, auswahlJahr);
		bindingGroup.addBinding(jComboBoxBinding);

		jahrVerwalten.setFont(resourceMap.getFont("jahrVerwalten.font")); // NOI18N
		jahrVerwalten.setText(resourceMap.getString("jahrVerwalten.text")); // NOI18N
		jahrVerwalten.setName("jahrVerwalten"); // NOI18N
		jahrVerwalten.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jahrVerwaltenActionPerformed(evt);
			}
		});

		jLabel12.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
		jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
		jLabel12.setName("jLabel12"); // NOI18N

		jLabel13.setFont(resourceMap.getFont("jLabel13.font")); // NOI18N
		jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
		jLabel13.setName("jLabel13"); // NOI18N

		jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
		jLabel16.setName("jLabel16"); // NOI18N

		anzahlFrei.setColumns(4);
		anzahlFrei.setEditable(false);
		anzahlFrei
				.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
						new javax.swing.text.NumberFormatter(
								new java.text.DecimalFormat("#0"))));
		anzahlFrei.setText(resourceMap.getString("anzahlFrei.text")); // NOI18N
		anzahlFrei.setToolTipText(resourceMap
				.getString("anzahlFrei.toolTipText")); // NOI18N
		anzahlFrei.setName("anzahlFrei"); // NOI18N

		org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings
				.createAutoBinding(
						org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
						anzahlFreiQuery, org.jdesktop.beansbinding.ELProperty
								.create("${singleResult}"), anzahlFrei,
						org.jdesktop.beansbinding.BeanProperty.create("value"),
						"anzahlFreiBindung"); // NOI18N
		bindingGroup.addBinding(binding);

		jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
		jLabel17.setName("jLabel17"); // NOI18N

		anzahlGesamt.setColumns(4);
		anzahlGesamt.setEditable(false);
		anzahlGesamt
				.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
						new javax.swing.text.NumberFormatter(
								new java.text.DecimalFormat("#0"))));
		anzahlGesamt.setText(resourceMap.getString("anzahlGesamt.text")); // NOI18N
		anzahlGesamt.setToolTipText(resourceMap
				.getString("anzahlGesamt.toolTipText")); // NOI18N
		anzahlGesamt.setName("anzahlGesamt"); // NOI18N

		binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
				org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
				anzahlGesamtQuery,
				org.jdesktop.beansbinding.ELProperty.create("${singleResult}"),
				anzahlGesamt,
				org.jdesktop.beansbinding.BeanProperty.create("value"),
				"anzahlGesamtBindung"); // NOI18N
		bindingGroup.addBinding(binding);

		jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
		jLabel18.setName("jLabel18"); // NOI18N

		javax.swing.GroupLayout gl_jPanel1 = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(gl_jPanel1);
		gl_jPanel1
				.setHorizontalGroup(gl_jPanel1
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								gl_jPanel1
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jLabel13)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												auswahlKlasse,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												111,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jLabel16)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												anzahlFrei,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel17)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												anzahlGesamt,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel18)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												173, Short.MAX_VALUE)
										.addComponent(
												jLabel12,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												88,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												auswahlJahr,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												109,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jahrVerwalten)
										.addGap(3, 3, 3)));
		gl_jPanel1
				.setVerticalGroup(gl_jPanel1
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_jPanel1
										.createSequentialGroup()
										.addGroup(
												gl_jPanel1
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																jahrVerwalten)
														.addComponent(
																auswahlJahr,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel12)
														.addComponent(jLabel13)
														.addComponent(
																auswahlKlasse,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel16)
														.addComponent(
																anzahlFrei,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel17)
														.addComponent(
																anzahlGesamt,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel18))
										.addContainerGap(14, Short.MAX_VALUE)));

		jSplitPane1.setDividerLocation(200);
		jSplitPane1.setName("jSplitPane1"); // NOI18N

		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				resourceMap.getString("jPanel2.border.title"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				resourceMap.getFont("jPanel2.border.titleFont"))); // NOI18N
		jPanel2.setName("jPanel2"); // NOI18N

		jScrollPane1.setName("jScrollPane1"); // NOI18N

		klassenListe.setFont(resourceMap.getFont("klassenListe.font")); // NOI18N
		klassenListe.setModel(schuelerModel);
		klassenListe
				.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		klassenListe.setName("klassenListe"); // NOI18N
		klassenListe
				.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(
							javax.swing.event.ListSelectionEvent evt) {
						klassenListeValueChanged(evt);
					}
				});
		jScrollPane1.setViewportView(klassenListe);

		javax.swing.GroupLayout gl_jPanel2 = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(gl_jPanel2);
		gl_jPanel2.setHorizontalGroup(gl_jPanel2.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				gl_jPanel2
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(jScrollPane1,
								javax.swing.GroupLayout.DEFAULT_SIZE, 165,
								Short.MAX_VALUE).addContainerGap()));
		gl_jPanel2.setVerticalGroup(gl_jPanel2.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				gl_jPanel2
						.createSequentialGroup()
						.addComponent(jScrollPane1,
								javax.swing.GroupLayout.DEFAULT_SIZE, 487,
								Short.MAX_VALUE).addContainerGap()));

		jSplitPane1.setLeftComponent(jPanel2);

		jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				resourceMap.getString("jPanel3.border.title"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				resourceMap.getFont("jPanel3.border.titleFont"))); // NOI18N
		jPanel3.setName("jPanel3"); // NOI18N

		jPanel4.setName("jPanel4"); // NOI18N

		jLabel1.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
		jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
		jLabel1.setName("jLabel1"); // NOI18N

		jLabel2.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
		jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
		jLabel2.setName("jLabel2"); // NOI18N

		jLabel3.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
		jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
		jLabel3.setName("jLabel3"); // NOI18N

		jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
		jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
		jLabel4.setName("jLabel4"); // NOI18N

		nachName.setEditable(false);
		nachName.setText(resourceMap.getString("nachName.text")); // NOI18N
		nachName.setName("nachName"); // NOI18N

		vorName.setEditable(false);
		vorName.setText(resourceMap.getString("vorName.text")); // NOI18N
		vorName.setName("vorName"); // NOI18N

		nr.setEditable(false);
		nr.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
				new javax.swing.text.NumberFormatter(
						new java.text.DecimalFormat("#0"))));
		nr.setText(resourceMap.getString("nr.text")); // NOI18N
		nr.setName("nr"); // NOI18N

		gebuehrenBetrag.setEditable(false);
		gebuehrenBetrag
				.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
						new javax.swing.text.NumberFormatter(
								java.text.NumberFormat.getCurrencyInstance())));
		gebuehrenBetrag.setText(resourceMap.getString("gebuehrenBetrag.text")); // NOI18N
		gebuehrenBetrag.setName("gebuehrenBetrag"); // NOI18N

		jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
		jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
		jLabel5.setName("jLabel5"); // NOI18N

		jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
		jLabel6.setText("Lehrer:"); // NOI18N
		jLabel6.setName("jLabel6"); // NOI18N

		klasse.setColumns(10);
		klasse.setEditable(false);
		klasse.setText(resourceMap.getString("klasse.text")); // NOI18N
		klasse.setName("klasse"); // NOI18N

		lehrer.setColumns(10);
		lehrer.setEditable(false);
		lehrer.setText(resourceMap.getString("lehrer.text")); // NOI18N
		lehrer.setName("lehrer"); // NOI18N

		jLabel7.setFont(new java.awt.Font("Dialog", 0, 12));
		jLabel7.setText("Geburt:"); // NOI18N
		jLabel7.setName("jLabel7"); // NOI18N

		geburt.setEditable(false);
		geburt.setName("geburt"); // NOI18N

		jLabel11.setFont(resourceMap.getFont("jLabel11.font")); // NOI18N
		jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
		jLabel11.setName("jLabel11"); // NOI18N

		kautionBetrag.setEditable(false);
		kautionBetrag
				.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
						new javax.swing.text.NumberFormatter(
								java.text.NumberFormat.getCurrencyInstance())));
		kautionBetrag.setText(resourceMap.getString("kautionBetrag.text")); // NOI18N
		kautionBetrag.setName("kautionBetrag"); // NOI18N

		javax.swing.GroupLayout gl_jPanel4 = new javax.swing.GroupLayout(
				jPanel4);
		jPanel4.setLayout(gl_jPanel4);
		gl_jPanel4
				.setHorizontalGroup(gl_jPanel4
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_jPanel4
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_jPanel4
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jLabel1)
														.addComponent(jLabel2)
														.addComponent(jLabel3)
														.addComponent(jLabel7))
										.addGap(16, 16, 16)
										.addGroup(
												gl_jPanel4
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addComponent(geburt)
														.addComponent(nr)
														.addComponent(vorName)
														.addComponent(
																nachName,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																151,
																Short.MAX_VALUE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												101, Short.MAX_VALUE)
										.addGroup(
												gl_jPanel4
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addGroup(
																gl_jPanel4
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(
																				jLabel5)
																		.addComponent(
																				jLabel6))
														.addComponent(jLabel11)
														.addComponent(jLabel4))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												gl_jPanel4
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																false)
														.addComponent(
																gebuehrenBetrag)
														.addComponent(
																kautionBetrag)
														.addComponent(lehrer)
														.addComponent(klasse))
										.addContainerGap()));
		gl_jPanel4
				.setVerticalGroup(gl_jPanel4
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_jPanel4
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_jPanel4
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																gl_jPanel4
																		.createSequentialGroup()
																		.addGroup(
																				gl_jPanel4
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jLabel5)
																						.addComponent(
																								klasse,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				gl_jPanel4
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jLabel6)
																						.addComponent(
																								lehrer,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				gl_jPanel4
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								kautionBetrag,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								jLabel11)))
														.addGroup(
																gl_jPanel4
																		.createSequentialGroup()
																		.addGroup(
																				gl_jPanel4
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jLabel1)
																						.addComponent(
																								nachName,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				gl_jPanel4
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jLabel2)
																						.addComponent(
																								vorName,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				gl_jPanel4
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								jLabel3)
																						.addComponent(
																								nr,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE))))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												gl_jPanel4
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																gl_jPanel4
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(
																				gebuehrenBetrag,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(
																				jLabel4))
														.addComponent(jLabel7)
														.addComponent(
																geburt,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		sAktionenPanel.setName("sAktionenPanel"); // NOI18N

		einAusZahlung.setFont(resourceMap.getFont("einAusZahlung.font")); // NOI18N
		einAusZahlung.setText("Zahlung"); // NOI18N
		einAusZahlung.setName("einAusZahlung"); // NOI18N
		einAusZahlung.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				einAusZahlungActionPerformed(evt);
			}
		});

		jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel10.setText("Schüler"); // NOI18N
		jLabel10.setName("jLabel10"); // NOI18N

		javax.swing.GroupLayout gl_sAktionenPanel = new javax.swing.GroupLayout(
				sAktionenPanel);
		sAktionenPanel.setLayout(gl_sAktionenPanel);
		gl_sAktionenPanel
				.setHorizontalGroup(gl_sAktionenPanel
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_sAktionenPanel
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_sAktionenPanel
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jLabel10,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																113,
																Short.MAX_VALUE)
														.addComponent(
																einAusZahlung,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																113,
																Short.MAX_VALUE))
										.addContainerGap()));
		gl_sAktionenPanel
				.setVerticalGroup(gl_sAktionenPanel
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_sAktionenPanel
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jLabel10)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(einAusZahlung)
										.addContainerGap(60, Short.MAX_VALUE)));

		detailsTabs.setName("detailsTabs"); // NOI18N
		detailsTabs.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				detailsTabsStateChanged(evt);
			}
		});

		vertragSchluesselPanel.setName("vertragSchluesselPanel"); // NOI18N

		jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				resourceMap.getString("jPanel5.border.title"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				resourceMap.getFont("jPanel5.border.titleFont"))); // NOI18N
		jPanel5.setName("jPanel5"); // NOI18N

		jScrollPane2.setName("jScrollPane2"); // NOI18N

		vertragTabelle.setColumnSelectionAllowed(true);
		vertragTabelle.setName("vertragTabelle"); // NOI18N
		vertragTabelle.getTableHeader().setReorderingAllowed(false);

		org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings
				.createJTableBinding(
						org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
						vertragListe, vertragTabelle);
		org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${id}"));
		columnBinding.setColumnName("Id");
		columnBinding.setColumnClass(Long.class);
		columnBinding.setEditable(false);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${schliessfach.nr}"));
		columnBinding.setColumnName("Schliessfach.nr");
		columnBinding.setColumnClass(Long.class);
		columnBinding.setEditable(false);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${beginnJahr}"));
		columnBinding.setColumnName("Beginn Jahr");
		columnBinding.setColumnClass(Integer.class);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${beginnSommer}"));
		columnBinding.setColumnName("Beginn Sommer");
		columnBinding.setColumnClass(Boolean.class);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${endeJahr}"));
		columnBinding.setColumnName("Ende Jahr");
		columnBinding.setColumnClass(Integer.class);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${endeSommer}"));
		columnBinding.setColumnName("Ende Sommer");
		columnBinding.setColumnClass(Boolean.class);
		bindingGroup.addBinding(jTableBinding);
		jTableBinding.bind();
		jScrollPane2.setViewportView(vertragTabelle);
		vertragTabelle
				.getColumnModel()
				.getSelectionModel()
				.setSelectionMode(
						javax.swing.ListSelectionModel.SINGLE_SELECTION);
		vertragTabelle.getColumnModel().getColumn(0).setResizable(false);
		vertragTabelle.getColumnModel().getColumn(0).setPreferredWidth(70);
		vertragTabelle
				.getColumnModel()
				.getColumn(0)
				.setHeaderValue(
						resourceMap
								.getString("vertragTabelle.columnModel.title0")); // NOI18N
		vertragTabelle.getColumnModel().getColumn(1).setResizable(false);
		vertragTabelle.getColumnModel().getColumn(1).setPreferredWidth(70);
		vertragTabelle
				.getColumnModel()
				.getColumn(1)
				.setHeaderValue(
						resourceMap
								.getString("vertragTabelle.columnModel.title1")); // NOI18N
		vertragTabelle.getColumnModel().getColumn(2).setResizable(false);
		vertragTabelle
				.getColumnModel()
				.getColumn(2)
				.setHeaderValue(
						resourceMap
								.getString("vertragTabelle.columnModel.title2")); // NOI18N
		vertragTabelle.getColumnModel().getColumn(3).setResizable(false);
		vertragTabelle.getColumnModel().getColumn(3).setPreferredWidth(35);
		vertragTabelle
				.getColumnModel()
				.getColumn(3)
				.setHeaderValue(
						resourceMap
								.getString("vertragTabelle.columnModel.title3")); // NOI18N
		vertragTabelle.getColumnModel().getColumn(4).setResizable(false);
		vertragTabelle
				.getColumnModel()
				.getColumn(4)
				.setHeaderValue(
						resourceMap
								.getString("vertragTabelle.columnModel.title4")); // NOI18N
		vertragTabelle.getColumnModel().getColumn(5).setResizable(false);
		vertragTabelle.getColumnModel().getColumn(5).setPreferredWidth(35);
		vertragTabelle
				.getColumnModel()
				.getColumn(5)
				.setHeaderValue(
						resourceMap
								.getString("vertragTabelle.columnModel.title5")); // NOI18N

		javax.swing.GroupLayout gl_jPanel5 = new javax.swing.GroupLayout(
				jPanel5);
		jPanel5.setLayout(gl_jPanel5);
		gl_jPanel5.setHorizontalGroup(gl_jPanel5.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				gl_jPanel5
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(jScrollPane2,
								javax.swing.GroupLayout.DEFAULT_SIZE, 495,
								Short.MAX_VALUE).addContainerGap()));
		gl_jPanel5.setVerticalGroup(gl_jPanel5.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				gl_jPanel5
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(jScrollPane2,
								javax.swing.GroupLayout.DEFAULT_SIZE, 94,
								Short.MAX_VALUE).addContainerGap()));

		jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				resourceMap.getString("jPanel7.border.title"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				resourceMap.getFont("jPanel7.border.titleFont"))); // NOI18N
		jPanel7.setName("jPanel7"); // NOI18N

		jScrollPane3.setName("jScrollPane3"); // NOI18N

		schluesselTabelle.setColumnSelectionAllowed(true);
		schluesselTabelle.setName("schluesselTabelle"); // NOI18N
		schluesselTabelle
				.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		schluesselTabelle.getTableHeader().setReorderingAllowed(false);

		jTableBinding = org.jdesktop.swingbinding.SwingBindings
				.createJTableBinding(
						org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
						schluesselListe, schluesselTabelle);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${schliessfach.nr}"));
		columnBinding.setColumnName("Schliessfach.nr");
		columnBinding.setColumnClass(Long.class);
		columnBinding.setEditable(false);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${nr}"));
		columnBinding.setColumnName("Nr");
		columnBinding.setColumnClass(Long.class);
		columnBinding.setEditable(false);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${ausgabe}"));
		columnBinding.setColumnName("Ausgabe");
		columnBinding.setColumnClass(java.util.Date.class);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${ausgegeben}"));
		columnBinding.setColumnName("Ausgegeben");
		columnBinding.setColumnClass(Boolean.class);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${kommentar}"));
		columnBinding.setColumnName("Kommentar");
		columnBinding.setColumnClass(String.class);
		bindingGroup.addBinding(jTableBinding);
		jTableBinding.bind();
		jScrollPane3.setViewportView(schluesselTabelle);
		schluesselTabelle
				.getColumnModel()
				.getSelectionModel()
				.setSelectionMode(
						javax.swing.ListSelectionModel.SINGLE_SELECTION);
		schluesselTabelle.getColumnModel().getColumn(0).setResizable(false);
		schluesselTabelle.getColumnModel().getColumn(0).setPreferredWidth(70);
		schluesselTabelle
				.getColumnModel()
				.getColumn(0)
				.setHeaderValue(
						resourceMap
								.getString("schluesselTabelle.columnModel.title0")); // NOI18N
		schluesselTabelle.getColumnModel().getColumn(1).setResizable(false);
		schluesselTabelle.getColumnModel().getColumn(1).setPreferredWidth(70);
		schluesselTabelle
				.getColumnModel()
				.getColumn(1)
				.setHeaderValue(
						resourceMap
								.getString("schluesselTabelle.columnModel.title3")); // NOI18N
		schluesselTabelle.getColumnModel().getColumn(2).setResizable(false);
		schluesselTabelle.getColumnModel().getColumn(2).setPreferredWidth(120);
		schluesselTabelle
				.getColumnModel()
				.getColumn(2)
				.setHeaderValue(
						resourceMap
								.getString("schluesselTabelle.columnModel.title1")); // NOI18N
		schluesselTabelle.getColumnModel().getColumn(2)
				.setCellEditor(new factories.DefaultFactory.DateEditor());
		schluesselTabelle.getColumnModel().getColumn(2)
				.setCellRenderer(new factories.DefaultFactory.DateRenderer());
		schluesselTabelle.getColumnModel().getColumn(3).setResizable(false);
		schluesselTabelle.getColumnModel().getColumn(3).setPreferredWidth(80);
		schluesselTabelle
				.getColumnModel()
				.getColumn(3)
				.setHeaderValue(
						resourceMap
								.getString("schluesselTabelle.columnModel.title2")); // NOI18N
		schluesselTabelle.getColumnModel().getColumn(4).setResizable(false);
		schluesselTabelle
				.getColumnModel()
				.getColumn(4)
				.setHeaderValue(
						resourceMap
								.getString("schluesselTabelle.columnModel.title4")); // NOI18N

		javax.swing.GroupLayout gl_jPanel7 = new javax.swing.GroupLayout(
				jPanel7);
		jPanel7.setLayout(gl_jPanel7);
		gl_jPanel7.setHorizontalGroup(gl_jPanel7.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				gl_jPanel7
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(jScrollPane3,
								javax.swing.GroupLayout.DEFAULT_SIZE, 495,
								Short.MAX_VALUE).addContainerGap()));
		gl_jPanel7.setVerticalGroup(gl_jPanel7.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				gl_jPanel7
						.createSequentialGroup()
						.addComponent(jScrollPane3,
								javax.swing.GroupLayout.DEFAULT_SIZE, 115,
								Short.MAX_VALUE).addContainerGap()));

		vAktionenPanel.setName("vAktionenPanel"); // NOI18N

		jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel8.setText("Vertrag"); // NOI18N
		jLabel8.setName("jLabel8"); // NOI18N

		vertragNeu.setFont(resourceMap.getFont("vertragNeu.font")); // NOI18N
		vertragNeu.setText("Neu"); // NOI18N
		vertragNeu.setName("vertragNeu"); // NOI18N
		vertragNeu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				vertragNeuActionPerformed(evt);
			}
		});

		vertragEntfernen.setFont(resourceMap.getFont("vertragEntfernen.font")); // NOI18N
		vertragEntfernen.setText("Entfernen"); // NOI18N
		vertragEntfernen.setName("vertragEntfernen"); // NOI18N
		vertragEntfernen.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				vertragEntfernenActionPerformed(evt);
			}
		});

		vertragBearbeiten
				.setFont(resourceMap.getFont("vertragBearbeiten.font")); // NOI18N
		vertragBearbeiten.setText("Bearbeiten"); // NOI18N
		vertragBearbeiten.setName("vertragBearbeiten"); // NOI18N
		vertragBearbeiten
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						vertragBearbeitenActionPerformed(evt);
					}
				});

		jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel9.setText("Schlüssel"); // NOI18N
		jLabel9.setName("jLabel9"); // NOI18N

		schluesselNeu.setFont(resourceMap.getFont("schluesselNeu.font")); // NOI18N
		schluesselNeu.setText("Neu"); // NOI18N
		schluesselNeu.setName("schluesselNeu"); // NOI18N
		schluesselNeu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				schluesselNeuActionPerformed(evt);
			}
		});

		schluesselEntfernen.setFont(resourceMap
				.getFont("schluesselEntfernen.font")); // NOI18N
		schluesselEntfernen.setText("Entfernen"); // NOI18N
		schluesselEntfernen.setName("schluesselEntfernen"); // NOI18N
		schluesselEntfernen
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						schluesselEntfernenActionPerformed(evt);
					}
				});

		vertragZuordnen.setFont(resourceMap.getFont("vertragZuordnen.font")); // NOI18N
		vertragZuordnen.setText(resourceMap.getString("vertragZuordnen.text")); // NOI18N
		vertragZuordnen.setName("vertragZuordnen"); // NOI18N
		vertragZuordnen.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				vertragZuordnenActionPerformed(evt);
			}
		});

		vertragFreigeben.setFont(resourceMap.getFont("vertragFreigeben.font")); // NOI18N
		vertragFreigeben
				.setText(resourceMap.getString("vertragFreigeben.text")); // NOI18N
		vertragFreigeben.setName("vertragFreigeben"); // NOI18N
		vertragFreigeben.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				vertragFreigebenActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout gl_vAktionenPanel = new javax.swing.GroupLayout(
				vAktionenPanel);
		vAktionenPanel.setLayout(gl_vAktionenPanel);
		gl_vAktionenPanel
				.setHorizontalGroup(gl_vAktionenPanel
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_vAktionenPanel
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_vAktionenPanel
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jLabel8,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																113,
																Short.MAX_VALUE)
														.addComponent(
																vertragNeu,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																113,
																Short.MAX_VALUE)
														.addComponent(
																vertragEntfernen,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																113,
																Short.MAX_VALUE)
														.addComponent(
																vertragBearbeiten,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																113,
																Short.MAX_VALUE)
														.addComponent(
																vertragZuordnen,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																113,
																Short.MAX_VALUE)
														.addComponent(
																jLabel9,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																113,
																Short.MAX_VALUE)
														.addComponent(
																schluesselNeu,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																113,
																Short.MAX_VALUE)
														.addComponent(
																schluesselEntfernen,
																javax.swing.GroupLayout.Alignment.TRAILING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																113,
																Short.MAX_VALUE)
														.addComponent(
																vertragFreigeben,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																113,
																Short.MAX_VALUE))
										.addContainerGap()));
		gl_vAktionenPanel
				.setVerticalGroup(gl_vAktionenPanel
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_vAktionenPanel
										.createSequentialGroup()
										.addComponent(jLabel8)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(vertragNeu)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(vertragEntfernen)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(vertragBearbeiten)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(vertragZuordnen)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(vertragFreigeben)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												23, Short.MAX_VALUE)
										.addComponent(jLabel9)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(schluesselNeu)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(schluesselEntfernen)
										.addGap(42, 42, 42)));

		javax.swing.GroupLayout gl_vertragSchluesselPanel = new javax.swing.GroupLayout(
				vertragSchluesselPanel);
		vertragSchluesselPanel.setLayout(gl_vertragSchluesselPanel);
		gl_vertragSchluesselPanel
				.setHorizontalGroup(gl_vertragSchluesselPanel
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_vertragSchluesselPanel
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_vertragSchluesselPanel
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jPanel7,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																jPanel5,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												vAktionenPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap()));
		gl_vertragSchluesselPanel
				.setVerticalGroup(gl_vertragSchluesselPanel
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								gl_vertragSchluesselPanel
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_vertragSchluesselPanel
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																vAktionenPanel,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																gl_vertragSchluesselPanel
																		.createSequentialGroup()
																		.addComponent(
																				jPanel5,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jPanel7,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));

		detailsTabs.addTab(resourceMap
				.getString("vertragSchluesselPanel.TabConstraints.tabTitle"),
				vertragSchluesselPanel); // NOI18N

		zahlungenPanel.setName("zahlungenPanel"); // NOI18N

		zAktionenPanel.setName("zAktionenPanel"); // NOI18N

		jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
		jLabel14.setName("jLabel14"); // NOI18N

		kautionNeu.setFont(resourceMap.getFont("kautionNeu.font")); // NOI18N
		kautionNeu.setText(resourceMap.getString("kautionNeu.text")); // NOI18N
		kautionNeu.setEnabled(false);
		kautionNeu.setName("kautionNeu"); // NOI18N

		kautionEntfernen.setFont(resourceMap.getFont("kautionEntfernen.font")); // NOI18N
		kautionEntfernen
				.setText(resourceMap.getString("kautionEntfernen.text")); // NOI18N
		kautionEntfernen.setName("kautionEntfernen"); // NOI18N
		kautionEntfernen.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				kautionEntfernenActionPerformed(evt);
			}
		});

		jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
		jLabel15.setName("jLabel15"); // NOI18N

		mieteNeu.setFont(resourceMap.getFont("mieteNeu.font")); // NOI18N
		mieteNeu.setText(resourceMap.getString("mieteNeu.text")); // NOI18N
		mieteNeu.setEnabled(false);
		mieteNeu.setName("mieteNeu"); // NOI18N

		mieteEntfernen.setFont(resourceMap.getFont("mieteEntfernen.font")); // NOI18N
		mieteEntfernen.setText(resourceMap.getString("mieteEntfernen.text")); // NOI18N
		mieteEntfernen.setName("mieteEntfernen"); // NOI18N
		mieteEntfernen.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mieteEntfernenActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout gl_zAktionenPanel = new javax.swing.GroupLayout(
				zAktionenPanel);
		zAktionenPanel.setLayout(gl_zAktionenPanel);
		gl_zAktionenPanel
				.setHorizontalGroup(gl_zAktionenPanel
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_zAktionenPanel
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_zAktionenPanel
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																kautionNeu,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																94,
																Short.MAX_VALUE)
														.addComponent(
																jLabel14,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																94,
																Short.MAX_VALUE)
														.addComponent(
																kautionEntfernen,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																94,
																Short.MAX_VALUE)
														.addComponent(
																jLabel15,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																94,
																Short.MAX_VALUE)
														.addComponent(
																mieteNeu,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																94,
																Short.MAX_VALUE)
														.addComponent(
																mieteEntfernen,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																94,
																Short.MAX_VALUE))
										.addContainerGap()));
		gl_zAktionenPanel
				.setVerticalGroup(gl_zAktionenPanel
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_zAktionenPanel
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jLabel14)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(kautionNeu)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(kautionEntfernen)
										.addGap(73, 73, 73)
										.addComponent(jLabel15)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(mieteNeu)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(mieteEntfernen)
										.addContainerGap(73, Short.MAX_VALUE)));

		zKautionenPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, resourceMap.getString("zKautionenPanel.border.title"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				resourceMap.getFont("zKautionenPanel.border.titleFont"))); // NOI18N
		zKautionenPanel.setName("zKautionenPanel"); // NOI18N

		jScrollPane4.setName("jScrollPane4"); // NOI18N

		kautionenTabelle.setColumnSelectionAllowed(true);
		kautionenTabelle.setName("kautionenTabelle"); // NOI18N
		kautionenTabelle
				.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		kautionenTabelle.getTableHeader().setReorderingAllowed(false);

		jTableBinding = org.jdesktop.swingbinding.SwingBindings
				.createJTableBinding(
						org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
						kautionenListe, kautionenTabelle);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${id}"));
		columnBinding.setColumnName("Id");
		columnBinding.setColumnClass(Long.class);
		columnBinding.setEditable(false);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${vertrag.id}"));
		columnBinding.setColumnName("Vertrag.id");
		columnBinding.setColumnClass(Long.class);
		columnBinding.setEditable(false);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${kommentar}"));
		columnBinding.setColumnName("Kommentar");
		columnBinding.setColumnClass(String.class);
		columnBinding.setEditable(false);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${zeitpunkt}"));
		columnBinding.setColumnName("Zeitpunkt");
		columnBinding.setColumnClass(java.util.Date.class);
		columnBinding.setEditable(false);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${betrag}"));
		columnBinding.setColumnName("Betrag");
		columnBinding.setColumnClass(Double.class);
		columnBinding.setEditable(false);
		bindingGroup.addBinding(jTableBinding);
		jTableBinding.bind();
		jScrollPane4.setViewportView(kautionenTabelle);
		kautionenTabelle
				.getColumnModel()
				.getSelectionModel()
				.setSelectionMode(
						javax.swing.ListSelectionModel.SINGLE_SELECTION);
		kautionenTabelle.getColumnModel().getColumn(0).setResizable(false);
		kautionenTabelle.getColumnModel().getColumn(0).setPreferredWidth(30);
		kautionenTabelle
				.getColumnModel()
				.getColumn(0)
				.setHeaderValue(
						resourceMap
								.getString("kautionenTabelle.columnModel.title4")); // NOI18N
		kautionenTabelle.getColumnModel().getColumn(1).setResizable(false);
		kautionenTabelle.getColumnModel().getColumn(1).setPreferredWidth(30);
		kautionenTabelle
				.getColumnModel()
				.getColumn(1)
				.setHeaderValue(
						resourceMap
								.getString("kautionenTabelle.columnModel.title4")); // NOI18N
		kautionenTabelle.getColumnModel().getColumn(2).setResizable(false);
		kautionenTabelle
				.getColumnModel()
				.getColumn(2)
				.setHeaderValue(
						resourceMap
								.getString("kautionenTabelle.columnModel.title1")); // NOI18N
		kautionenTabelle.getColumnModel().getColumn(3).setResizable(false);
		kautionenTabelle
				.getColumnModel()
				.getColumn(3)
				.setHeaderValue(
						resourceMap
								.getString("kautionenTabelle.columnModel.title2")); // NOI18N
		kautionenTabelle.getColumnModel().getColumn(4).setResizable(false);
		kautionenTabelle
				.getColumnModel()
				.getColumn(4)
				.setHeaderValue(
						resourceMap
								.getString("kautionenTabelle.columnModel.title3")); // NOI18N

		javax.swing.GroupLayout gl_zKautionenPanel = new javax.swing.GroupLayout(
				zKautionenPanel);
		zKautionenPanel.setLayout(gl_zKautionenPanel);
		gl_zKautionenPanel
				.setHorizontalGroup(gl_zKautionenPanel
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_zKautionenPanel
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												jScrollPane4,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												514, Short.MAX_VALUE)
										.addContainerGap()));
		gl_zKautionenPanel.setVerticalGroup(gl_zKautionenPanel
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						gl_zKautionenPanel
								.createSequentialGroup()
								.addComponent(jScrollPane4,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										111,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));

		zMietenPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, resourceMap.getString("zMietenPanel.border.title"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				resourceMap.getFont("zMietenPanel.border.titleFont"))); // NOI18N
		zMietenPanel.setName("zMietenPanel"); // NOI18N

		jScrollPane5.setName("jScrollPane5"); // NOI18N

		mietenTabelle.setName("mietenTabelle"); // NOI18N
		mietenTabelle.getTableHeader().setReorderingAllowed(false);

		jTableBinding = org.jdesktop.swingbinding.SwingBindings
				.createJTableBinding(
						org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
						mietenListe, mietenTabelle);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${id}"));
		columnBinding.setColumnName("Id");
		columnBinding.setColumnClass(Long.class);
		columnBinding.setEditable(false);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${vertrag.id}"));
		columnBinding.setColumnName("Vertrag.id");
		columnBinding.setColumnClass(Long.class);
		columnBinding.setEditable(false);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${kommentar}"));
		columnBinding.setColumnName("Kommentar");
		columnBinding.setColumnClass(String.class);
		columnBinding.setEditable(false);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${zeitpunkt}"));
		columnBinding.setColumnName("Zeitpunkt");
		columnBinding.setColumnClass(java.util.Date.class);
		columnBinding.setEditable(false);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${leihJahr}"));
		columnBinding.setColumnName("Leih Jahr");
		columnBinding.setColumnClass(Integer.class);
		columnBinding.setEditable(false);
		columnBinding = jTableBinding
				.addColumnBinding(org.jdesktop.beansbinding.ELProperty
						.create("${betrag}"));
		columnBinding.setColumnName("Betrag");
		columnBinding.setColumnClass(Double.class);
		columnBinding.setEditable(false);
		bindingGroup.addBinding(jTableBinding);
		jTableBinding.bind();
		jScrollPane5.setViewportView(mietenTabelle);
		mietenTabelle.getColumnModel().getColumn(0).setResizable(false);
		mietenTabelle.getColumnModel().getColumn(0).setPreferredWidth(30);
		mietenTabelle
				.getColumnModel()
				.getColumn(0)
				.setHeaderValue(
						resourceMap
								.getString("mietenTabelle.columnModel.title5")); // NOI18N
		mietenTabelle.getColumnModel().getColumn(1).setResizable(false);
		mietenTabelle.getColumnModel().getColumn(1).setPreferredWidth(30);
		mietenTabelle
				.getColumnModel()
				.getColumn(1)
				.setHeaderValue(
						resourceMap
								.getString("mietenTabelle.columnModel.title5")); // NOI18N
		mietenTabelle.getColumnModel().getColumn(2).setResizable(false);
		mietenTabelle
				.getColumnModel()
				.getColumn(2)
				.setHeaderValue(
						resourceMap
								.getString("mietenTabelle.columnModel.title1")); // NOI18N
		mietenTabelle.getColumnModel().getColumn(3).setResizable(false);
		mietenTabelle
				.getColumnModel()
				.getColumn(3)
				.setHeaderValue(
						resourceMap
								.getString("mietenTabelle.columnModel.title2")); // NOI18N
		mietenTabelle.getColumnModel().getColumn(4).setResizable(false);
		mietenTabelle
				.getColumnModel()
				.getColumn(4)
				.setHeaderValue(
						resourceMap
								.getString("mietenTabelle.columnModel.title3")); // NOI18N
		mietenTabelle.getColumnModel().getColumn(5).setResizable(false);
		mietenTabelle
				.getColumnModel()
				.getColumn(5)
				.setHeaderValue(
						resourceMap
								.getString("mietenTabelle.columnModel.title4")); // NOI18N

		javax.swing.GroupLayout gl_zMietenPanel = new javax.swing.GroupLayout(
				zMietenPanel);
		zMietenPanel.setLayout(gl_zMietenPanel);
		gl_zMietenPanel.setHorizontalGroup(gl_zMietenPanel.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				gl_zMietenPanel
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(jScrollPane5,
								javax.swing.GroupLayout.DEFAULT_SIZE, 514,
								Short.MAX_VALUE).addContainerGap()));
		gl_zMietenPanel.setVerticalGroup(gl_zMietenPanel.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				gl_zMietenPanel
						.createSequentialGroup()
						.addComponent(jScrollPane5,
								javax.swing.GroupLayout.DEFAULT_SIZE, 121,
								Short.MAX_VALUE).addContainerGap()));

		javax.swing.GroupLayout gl_zahlungenPanel = new javax.swing.GroupLayout(
				zahlungenPanel);
		zahlungenPanel.setLayout(gl_zahlungenPanel);
		gl_zahlungenPanel
				.setHorizontalGroup(gl_zahlungenPanel
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								gl_zahlungenPanel
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_zahlungenPanel
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																zMietenPanel,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																zKautionenPanel,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												zAktionenPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap()));
		gl_zahlungenPanel
				.setVerticalGroup(gl_zahlungenPanel
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_zahlungenPanel
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_zahlungenPanel
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																zAktionenPanel,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addGroup(
																gl_zahlungenPanel
																		.createSequentialGroup()
																		.addComponent(
																				zKautionenPanel,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				zMietenPanel,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)))
										.addContainerGap()));

		detailsTabs
				.addTab(resourceMap
						.getString("zahlungenPanel.TabConstraints.tabTitle"),
						zahlungenPanel); // NOI18N

		javax.swing.GroupLayout gl_jPanel3 = new javax.swing.GroupLayout(
				jPanel3);
		jPanel3.setLayout(gl_jPanel3);
		gl_jPanel3
				.setHorizontalGroup(gl_jPanel3
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								gl_jPanel3
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_jPanel3
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																detailsTabs,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																701,
																Short.MAX_VALUE)
														.addGroup(
																gl_jPanel3
																		.createSequentialGroup()
																		.addComponent(
																				jPanel4,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				sAktionenPanel,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));
		gl_jPanel3
				.setVerticalGroup(gl_jPanel3
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_jPanel3
										.createSequentialGroup()
										.addGroup(
												gl_jPanel3
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																false)
														.addComponent(
																sAktionenPanel,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																jPanel4,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												detailsTabs,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												363,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		jSplitPane1.setRightComponent(jPanel3);

		javax.swing.GroupLayout gl_mainPanel = new javax.swing.GroupLayout(
				mainPanel);
		mainPanel.setLayout(gl_mainPanel);
		gl_mainPanel
				.setHorizontalGroup(gl_mainPanel
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_mainPanel
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_mainPanel
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jPanel1,
																javax.swing.GroupLayout.Alignment.TRAILING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																jSplitPane1,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																946,
																Short.MAX_VALUE))
										.addContainerGap()));
		gl_mainPanel.setVerticalGroup(gl_mainPanel.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				gl_mainPanel
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(jPanel1,
								javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(jSplitPane1,
								javax.swing.GroupLayout.DEFAULT_SIZE, 526,
								Short.MAX_VALUE).addContainerGap()));

		menuBar.setName("menuBar"); // NOI18N

		dateiMenu.setText(resourceMap.getString("dateiMenu.text")); // NOI18N
		dateiMenu.setName("dateiMenu"); // NOI18N

		miImportieren.setText(resourceMap.getString("miImportieren.text")); // NOI18N
		miImportieren.setName("miImportieren"); // NOI18N
		miImportieren.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				miImportierenActionPerformed(evt);
			}
		});
		dateiMenu.add(miImportieren);

		miNummernImportieren.setText(resourceMap
				.getString("miNummernImportieren.text"));
		miNummernImportieren
				.setToolTipText("Mit diesem Dialog können bei einem Wechsel des Schulverwaltungsprogramms für bestehende Schüler neue Schülernummern importiert werden.");
		miNummernImportieren.setName("miNummernImportieren");
		miNummernImportieren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				miNummernImportierenActionPerformed(e);
			}
		});
		dateiMenu.add(miNummernImportieren);

		javax.swing.ActionMap actionMap = org.jdesktop.application.Application
				.getInstance(schliessfach.SchliessfachApp.class).getContext()
				.getActionMap(SchliessfachView.class, this);

		miDrucker.setAction(actionMap.get("druckerEinstellen")); // NOI18N
		miDrucker.setText(resourceMap.getString("miDrucker.text")); // NOI18N
		miDrucker.setName("miDrucker"); // NOI18N
		dateiMenu.add(miDrucker);

		exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
		exitMenuItem.setText(resourceMap.getString("exitMenuItem.text")); // NOI18N
		exitMenuItem.setName("exitMenuItem"); // NOI18N
		exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exitMenuItemActionPerformed(evt);
			}
		});
		dateiMenu.add(exitMenuItem);

		menuBar.add(dateiMenu);

		suchenMenu.setText(resourceMap.getString("suchenMenu.text")); // NOI18N
		suchenMenu.setName("suchenMenu"); // NOI18N

		sucheSchueler.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_F,
				java.awt.event.InputEvent.CTRL_MASK));
		sucheSchueler.setText(resourceMap.getString("sucheSchueler.text")); // NOI18N
		sucheSchueler.setName("sucheSchueler"); // NOI18N
		sucheSchueler.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sucheSchuelerActionPerformed(evt);
			}
		});
		suchenMenu.add(sucheSchueler);

		sucheSchluessel.setText(resourceMap.getString("sucheSchluessel.text")); // NOI18N
		sucheSchluessel.setName("sucheSchluessel"); // NOI18N
		sucheSchluessel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sucheSchluesselActionPerformed(evt);
			}
		});
		suchenMenu.add(sucheSchluessel);

		menuBar.add(suchenMenu);

		verwaltungMenu.setText("Verwaltung"); // NOI18N
		verwaltungMenu.setName("verwaltungMenu"); // NOI18N

		verwaltungFach.setText(resourceMap.getString("verwaltungFach.text")); // NOI18N
		verwaltungFach.setName("verwaltungFach"); // NOI18N
		verwaltungFach.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				verwaltungFachActionPerformed(evt);
			}
		});
		verwaltungMenu.add(verwaltungFach);

		verwaltungSchluessel.setText("Schlüssel"); // NOI18N
		verwaltungSchluessel.setName("verwaltungSchluessel"); // NOI18N
		verwaltungMenu.add(verwaltungSchluessel);

		verwaltungGebuehren.setAction(actionMap.get("gebuehrenVerwaltung")); // NOI18N
		verwaltungGebuehren.setName("verwaltungGebuehren"); // NOI18N
		verwaltungMenu.add(verwaltungGebuehren);

		verwaltungSchueler.setText(resourceMap
				.getString("verwaltungSchueler.text")); // NOI18N
		verwaltungSchueler.setName("verwaltungSchueler"); // NOI18N
		verwaltungSchueler
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						verwaltungSchuelerActionPerformed(evt);
					}
				});
		verwaltungMenu.add(verwaltungSchueler);

		jahrVerwaltung.setText(resourceMap.getString("jahrVerwaltung.text")); // NOI18N
		jahrVerwaltung.setName("jahrVerwaltung"); // NOI18N
		jahrVerwaltung.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jahrVerwaltungActionPerformed(evt);
			}
		});
		verwaltungMenu.add(jahrVerwaltung);

		menuBar.add(verwaltungMenu);

		listenMenu.setText(resourceMap.getString("listenMenu.text")); // NOI18N
		listenMenu.setName("listenMenu"); // NOI18N

		miKlassenliste.setAction(actionMap.get("mietListe")); // NOI18N
		miKlassenliste.setText(resourceMap.getString("miKlassenliste.text")); // NOI18N
		miKlassenliste.setName("miKlassenliste"); // NOI18N
		listenMenu.add(miKlassenliste);

		kautionsListe.setAction(actionMap.get("kautionsListe")); // NOI18N
		kautionsListe.setText(resourceMap.getString("kautionsListe.text"));
		kautionsListe.setName("kautionsListe"); // NOI18N
		listenMenu.add(kautionsListe);

		schluesselFehlliste.setAction(actionMap.get("schluesselFehlliste")); // NOI18N
		schluesselFehlliste.setText(resourceMap
				.getString("schluesselFehlliste.text")); // NOI18N
		schluesselFehlliste.setName("schluesselFehlliste"); // NOI18N
		listenMenu.add(schluesselFehlliste);

		menuBar.add(listenMenu);

		JSeparator separator = new JSeparator();
		listenMenu.add(separator);

		offenePostenListe.setAction(actionMap.get("offenePostenListe"));
		offenePostenListe.setText(resourceMap
				.getString("offenePostenListe.Action.text"));
		offenePostenListe.setName("offenePostenListe");
		listenMenu.add(offenePostenListe);

		ausgegebeneSchluesselListe.setAction(actionMap
				.get("ausgegebeneSchluesselListe"));
		ausgegebeneSchluesselListe.setText(resourceMap
				.getString("ausgegebeneSchluesselListe.Action.text"));
		ausgegebeneSchluesselListe.setName("ausgegebeneSchluesselListe");
		listenMenu.add(ausgegebeneSchluesselListe);

		helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
		helpMenu.setName("helpMenu"); // NOI18N

		hinweiseMenuItem = new JMenuItem("Hinweise");
		hinweiseMenuItem.setName("hinweiseMenuItem");
		hinweiseMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				showHinweiseBox();
			}
		});
		helpMenu.add(hinweiseMenuItem);

		aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
		aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
		aboutMenuItem.setName("aboutMenuItem"); // NOI18N
		helpMenu.add(aboutMenuItem);

		menuBar.add(helpMenu);

		statusPanel.setName("statusPanel"); // NOI18N

		statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

		statusMessageLabel.setName("statusMessageLabel"); // NOI18N

		statusAnimationLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

		progressBar.setName("progressBar"); // NOI18N

		javax.swing.GroupLayout gl_statusPanel = new javax.swing.GroupLayout(
				statusPanel);
		statusPanel.setLayout(gl_statusPanel);
		gl_statusPanel
				.setHorizontalGroup(gl_statusPanel
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(statusPanelSeparator,
								javax.swing.GroupLayout.DEFAULT_SIZE, 970,
								Short.MAX_VALUE)
						.addGroup(
								gl_statusPanel
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(statusMessageLabel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												786, Short.MAX_VALUE)
										.addComponent(
												progressBar,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(statusAnimationLabel)
										.addContainerGap()));
		gl_statusPanel
				.setVerticalGroup(gl_statusPanel
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								gl_statusPanel
										.createSequentialGroup()
										.addComponent(
												statusPanelSeparator,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												2,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addGroup(
												gl_statusPanel
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																statusMessageLabel)
														.addComponent(
																statusAnimationLabel)
														.addComponent(
																progressBar,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(3, 3, 3)));

		setComponent(mainPanel);
		setMenuBar(menuBar);
		setStatusBar(statusPanel);

		bindingGroup.bind();
	}// </editor-fold>//GEN-END:initComponents

	private void auswahlKlasseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_auswahlKlasseActionPerformed
		aktualisiereSchuelerliste();
	}// GEN-LAST:event_auswahlKlasseActionPerformed

	private void auswahlKlasseFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_auswahlKlasseFocusGained
		if (!klassenlisteIstAktuell && em != null) {
			aktualisiereKlassenliste();
		}
	}// GEN-LAST:event_auswahlKlasseFocusGained

	private void klassenListeValueChanged(
			javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_klassenListeValueChanged
		if (evt.getValueIsAdjusting()) {
			return;
		}
		waehleSchueler((String) klassenListe.getSelectedValue());
	}// GEN-LAST:event_klassenListeValueChanged

	private void sucheSchluesselActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_sucheSchluesselActionPerformed
		Query q = em
				.createQuery("SELECT s.nr FROM Schliessfach s ORDER BY s.nr ASC");
		String[] ar = longList2StringArray(q.getResultList());
		SuchDialog dlg = new SuchDialog(SchliessfachApp.getApplication()
				.getMainFrame(), ar);
		dlg.setTitle("Schlüssel suchen");
		dlg.setLocationRelativeTo(SchliessfachApp.getApplication()
				.getMainFrame());
		dlg.setVisible(true);
		if (!dlg.istAbgebrochen()) {
			String schliessfach = dlg.getResult();
			System.out.println("[SUCHE-SCHLÜSSEL] - Resultat :" + schliessfach);
			q = em.createQuery("SELECT s FROM Schueler s JOIN s.vertraege v WHERE NOT s.status=schueler.SchuelerStatus.INAKTIV AND v.schliessfach.nr=:schliessfachNr");
			q.setParameter("schliessfachNr", Long.parseLong(schliessfach));
			try {
				Schueler schueler = (Schueler) q.getSingleResult();
				auswahlKlasse.setSelectedItem(schueler.getKlasse());
				klassenListe.setSelectedValue(schueler.getNachName() + ","
						+ schueler.getVorName(), true);
			} catch (NoResultException ex) {
				JOptionPane.showMessageDialog(this.getComponent(),
						"Zu dem Schlüssel für das \nSchliessfach "
								+ schliessfach + " gibt es \nkeinen Schüler!",
						"Schlüsselsuche", JOptionPane.ERROR_MESSAGE);
			}
		}
		dlg.dispose();
	}// GEN-LAST:event_sucheSchluesselActionPerformed

	private void sucheSchuelerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_sucheSchuelerActionPerformed
		Query q = em
				.createQuery("SELECT s.nachName,s.vorName FROM Schueler s WHERE NOT s.status=schueler.SchuelerStatus.INAKTIV ORDER BY s.nachName ASC,s.vorName ASC");
		String[] ar = namenList2StringArray(q.getResultList());
		SuchDialog dlg = new SuchDialog(SchliessfachApp.getApplication()
				.getMainFrame(), ar);
		dlg.setTitle("Schüler suchen");
		dlg.setLocationRelativeTo(SchliessfachApp.getApplication()
				.getMainFrame());
		dlg.setVisible(true);
		if (!dlg.istAbgebrochen()) {
			String name = dlg.getResult();
			System.out.println("[SUCHE-SCHÜLER] - Resultat :" + name);
			q = em.createQuery("SELECT s FROM Schueler s WHERE s.nachName=:nachName AND s.vorName=:vorName");
			int pos = name.indexOf(",");
			q.setParameter("vorName", name.substring(pos + 1));
			q.setParameter("nachName", name.substring(0, pos));
			try {
				Schueler schueler = (Schueler) q.getSingleResult();
				auswahlKlasse.setSelectedItem(schueler.getKlasse());
				klassenListe.setSelectedValue(schueler.getNachName() + ","
						+ schueler.getVorName(), true);
			} catch (NonUniqueResultException ex) {
				JOptionPane
						.showMessageDialog(
								this.getComponent(),
								"Vorsicht: Für den Schüler "
										+ name
										+ " existieren mindestens "
										+ "zwei Datensätze. Das sollte nicht passieren. Ich verwende den ersten davn.",
								"Schülersuche", JOptionPane.ERROR_MESSAGE);
				Schueler schueler = (Schueler) q.getResultList().iterator()
						.next();
				auswahlKlasse.setSelectedItem(schueler.getKlasse());
				klassenListe.setSelectedValue(schueler.getNachName() + ","
						+ schueler.getVorName(), true);
			} catch (NoResultException ex) {
				JOptionPane.showMessageDialog(this.getComponent(),
						"Der Schüler " + name + " wurde nicht gefunden!",
						"Schülersuche", JOptionPane.ERROR_MESSAGE);
			}
		}
		dlg.dispose();

	}// GEN-LAST:event_sucheSchuelerActionPerformed

	private void miImportierenActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miImportierenActionPerformed
		SchuelerImportDlg dlg = new SchuelerImportDlg(SchliessfachApp
				.getApplication().getMainFrame());
		dlg.setLocationRelativeTo(SchliessfachApp.getApplication()
				.getMainFrame());
		dlg.setVisible(true);
		aktualisiereKlassenliste();
	}// GEN-LAST:event_miImportierenActionPerformed

	private void miNummernImportierenActionPerformed(
			java.awt.event.ActionEvent evt) {
		SchuelernummernImportDlg dlg = new SchuelernummernImportDlg(
				SchliessfachApp.getApplication().getMainFrame());
		dlg.setLocationRelativeTo(SchliessfachApp.getApplication()
				.getMainFrame());
		dlg.setVisible(true);
		aktualisiereKlassenliste();
	}

	private void vertragNeuActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_vertragNeuActionPerformed
		if (aktuellerSchueler == null) {
			return;
		}
		Long jahr = ((Jahr) auswahlJahr.getSelectedItem()).getJahr();
		if (jahr == null) {
			jahr = new Long(
					SchliessfachApp.getApplication().heute.get(Calendar.YEAR));
		}
		Vertrag neu = new Vertrag(aktuellerSchueler, jahr.intValue(), false);
		em.getTransaction().begin();
		em.persist(neu);
		Historie.anhaengen(Rubrik.VERTRAG,
				neu.getSchueler().getNr().toString(),
				"hinzugefügt: " + neu.toString());
		aktuellerSchueler.getVertraege().add(neu);
		Zahlung z = new Zahlung(neu, jahr.intValue(),
				-SchliessfachApp.getApplication().aktuelleGebuehren
						.get(Zahlungsart.Miete));
		em.persist(z);
		neu.getZahlungen().add(z);
		em.getTransaction().commit();
		aktualisiereVertragliste();
		aktualisiereBetraege();
		aktualisiereMietenliste();
	}// GEN-LAST:event_vertragNeuActionPerformed

	private void vertragBearbeitenActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_vertragBearbeitenActionPerformed
		int zeile;
		if ((zeile = vertragTabelle.getSelectedRow()) == -1) {
			JOptionPane
					.showMessageDialog(
							this.getFrame(),
							"Sie müssen zuerst einen Vertrag zur Bearbeitung auswählen.",
							"Vertrag bearbeiten", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Object id = vertragTabelle.getModel().getValueAt(zeile, 0);
		Vertrag vertrag = em.find(Vertrag.class, id);
		if (vertrag != null) {
			em.getTransaction().begin();
			VertragDlg dlg = new VertragDlg(SchliessfachApp.getApplication()
					.getMainFrame(), vertrag);
			dlg.setLocationRelativeTo(this.getFrame());
			dlg.setVisible(true);
			em.getTransaction().commit();
		}
		aktualisiereVertragliste();
	}// GEN-LAST:event_vertragBearbeitenActionPerformed

	private void vertragEntfernenActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_vertragEntfernenActionPerformed
		int zeile;
		if ((zeile = vertragTabelle.getSelectedRow()) == -1) {
			JOptionPane
					.showMessageDialog(
							this.getFrame(),
							"Sie müssen zuerste einen Vertrag zur Entfernung auswählen.",
							"Vertrag entfernen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Object id = vertragTabelle.getModel().getValueAt(zeile, 0);
		Vertrag vertrag = em.find(Vertrag.class, id);
		if (vertrag != null) {
			Query q = em
					.createQuery("SELECT SUM(z.betrag) FROM Zahlung z WHERE z.vertrag.id="
							+ vertrag.getId());
			try {
				Double betrag = (Double) q.getSingleResult();
				if (betrag != null && betrag != 0.0) {
					JOptionPane
							.showMessageDialog(
									this.getFrame(),
									String.format(
											"Es sind noch Zahlungen für den Vertrag offen. Der Betrag ist %4.2f EUR.\nDer Vertrag kann nicht entfernt werden.",
											betrag), "Vertrag entfernen",
									JOptionPane.ERROR_MESSAGE);
					return;
				}
			} catch (NoResultException ex) {
				// Es gibt keine Zahlungen für diesen Vertrag, der Vertrag kann
				// entfernt werden.
				System.out
						.println("INFO: [vertragEntfernen] Es sind keine Zahlungen mehr für den Vertrag "
								+ vertrag
								+ " erforderlich. Der Vertrag kann entfernt werden.");
			}
			boolean ausgegeben = false;
			if (vertrag.getSchliessfach() != null) {
				Schliessfach schliessfach = vertrag.getSchliessfach();
				for (Schluessel sl : schliessfach.getSchluessel()) {
					ausgegeben = ausgegeben || sl.getAusgegeben();
				}
			}
			if (ausgegeben) {
				JOptionPane
						.showMessageDialog(
								this.getFrame(),
								"Es ist noch mindestens ein Schlüssel für diesen Vertrag ausgegeben.\nDer Vertrag kann nicht entfernt werden.",
								"Vertrag entfernen", JOptionPane.ERROR_MESSAGE);
				return;
			}
			em.getTransaction().begin();
			q = em.createQuery("DELETE FROM Zahlung z WHERE z.vertrag.id="
					+ vertrag.getId());
			q.executeUpdate();
			if (vertrag.getSchliessfach() != null) {
				vertrag.getSchliessfach().setVertrag(null);
			}
			if (vertrag.getSchueler() != null) {
				vertrag.getSchueler().getVertraege().remove(vertrag);
			}
			em.remove(vertrag);
			em.getTransaction().commit();
		}
		aktualisiereSchuelerDetails();
	}// GEN-LAST:event_vertragEntfernenActionPerformed

	private void schluesselNeuActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_schluesselNeuActionPerformed
		int zeile;
		if ((zeile = vertragTabelle.getSelectedRow()) == -1) {
			JOptionPane
					.showMessageDialog(
							this.getFrame(),
							"Sie müssen zuerste einen Vertrag mit Schließfach auswählen.",
							"Neuer Schlüssel", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Object id = vertragTabelle.getModel().getValueAt(zeile, 0);
		Vertrag vertrag = em.find(Vertrag.class, id);
		if (vertrag != null && vertrag.getSchliessfach() != null) {
			em.getTransaction().begin();
			Schliessfach schliessfach = vertrag.getSchliessfach();
			Query q = em
					.createQuery("SELECT MAX(s.nr) FROM Schluessel s WHERE s.schliessfach.nr="
							+ schliessfach.getNr());
			Long l = (Long) q.getSingleResult();
			if (l == null) {
				l = 1L;
			} else {
				l++;
			}
			Schluessel neu = new Schluessel(l, schliessfach, false, null, null);
			schliessfach.getSchluessel().add(neu);
			em.getTransaction().commit();
		} else {
			JOptionPane
					.showMessageDialog(
							this.getFrame(),
							"Sie müssen zuerste einen Vertrag mit Schließfach auswählen.",
							"Neuer Schlüssel", JOptionPane.ERROR_MESSAGE);
			return;
		}
		aktualisiereSchluesselliste();
	}// GEN-LAST:event_schluesselNeuActionPerformed

	private void schluesselEntfernenActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_schluesselEntfernenActionPerformed
		int zeile;
		if ((zeile = schluesselTabelle.getSelectedRow()) == -1) {
			JOptionPane.showMessageDialog(this.getFrame(),
					"Sie müssen zuerste einen Schlüssel auswählen.",
					"Schlüssel entfernen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Long fachNr = (Long) schluesselTabelle.getModel().getValueAt(zeile, 0);
		Long schluesselNr = (Long) schluesselTabelle.getModel().getValueAt(
				zeile, 1);
		try {
			Query q = em
					.createQuery("SELECT s FROM Schluessel s WHERE s.schliessfach.nr="
							+ fachNr + " AND s.nr=" + schluesselNr);
			Schluessel schluessel = (Schluessel) q.getSingleResult();
			boolean gebuehrFaellig;
			double gebuehr = SchliessfachApp.getApplication().aktuelleGebuehren
					.get(Zahlungsart.Ersatzschluessel);
			if (schluessel.getAnVertrag() != null) {
				gebuehrFaellig = (JOptionPane.YES_OPTION == JOptionPane
						.showConfirmDialog(this.getFrame(),
								"Es wird eine Gebühr von " + gebuehr
										+ " EUR fällig.", "Schlüsselverlust",
								JOptionPane.YES_NO_OPTION));
			} else {
				gebuehrFaellig = false;
			}
			em.getTransaction().begin();
			if (gebuehrFaellig) {
				Vertrag v = schluessel.getAnVertrag();
				Zahlung z = new Zahlung(v, Zahlungsart.Ersatzschluessel,
						-gebuehr);
				v.getZahlungen().add(z);
				em.persist(z);
				Historie.anhaengen(Rubrik.ZAHLUNG, z.getVertrag().toString(),
						"hinzugefügt: " + z.toString());
			}
			schluessel.getSchliessfach().getSchluessel().remove(schluessel);
			em.remove(schluessel);
			em.getTransaction().commit();
		} catch (NoResultException e) {
			System.out.println("[schluesselEntfernen] Schlüssel Nr("
					+ schluesselNr + ") für Schließfach(" + fachNr
					+ ") nicht gefunden.");
			e.printStackTrace();
		}
		aktualisiereSchluesselliste();
	}// GEN-LAST:event_schluesselEntfernenActionPerformed

	private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_exitMenuItemActionPerformed
		if (em.getTransaction().isActive()) {
			em.getTransaction().commit();
		}
		SchliessfachApp.getApplication().exit();
	}// GEN-LAST:event_exitMenuItemActionPerformed

	private void verwaltungSchuelerActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_verwaltungSchuelerActionPerformed
		SchuelereingabeDlg dlg = new SchuelereingabeDlg(this.getFrame());
		dlg.setLocationRelativeTo(this.getFrame());
		dlg.setVisible(true);
		aktualisiereSchuelerliste();
	}// GEN-LAST:event_verwaltungSchuelerActionPerformed

	private void jahrVerwaltenActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jahrVerwaltenActionPerformed
		JahrVerwaltungDlg dlg = new JahrVerwaltungDlg(this.getFrame(), true);
		dlg.setLocationRelativeTo(this.getFrame());
		dlg.setVisible(true);
		aktualisiereJahrliste();
		aktualisiereSchuelerDetails();
	}// GEN-LAST:event_jahrVerwaltenActionPerformed

	private void jahrVerwaltungActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jahrVerwaltungActionPerformed
		jahrVerwaltenActionPerformed(evt);
	}// GEN-LAST:event_jahrVerwaltungActionPerformed

	private void verwaltungFachActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_verwaltungFachActionPerformed
		SchliessfachDlg dlg = new SchliessfachDlg(SchliessfachApp
				.getApplication().getMainFrame());
		dlg.setLocationRelativeTo(SchliessfachApp.getApplication()
				.getMainFrame());
		dlg.setVisible(true);
		aktualisiereFächer();
	}// GEN-LAST:event_verwaltungFachActionPerformed

	private void einAusZahlungActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_einAusZahlungActionPerformed
		int zeile = vertragTabelle.getSelectedRow();
		if (zeile == -1) {
			JOptionPane
					.showMessageDialog(
							this.getFrame(),
							"Es ist kein Vertrag ausgewählt.\nWählen Sie den Vertrag aus, für den die Zahlung bestimmt ist.",
							"Zahlung durchführen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Vertrag v = em.find(Vertrag.class, vertragTabelle.getValueAt(zeile, 0));
		if (v == null) {
			JOptionPane.showMessageDialog(
					this.getFrame(),
					"Zu der Vertragsnummer "
							+ vertragTabelle.getValueAt(zeile, 0)
							+ "\nkonnte kein Vertrag gefunden werden.",
					"Zahlung durchführen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		ZahlungDlg dlg = new ZahlungDlg(SchliessfachApp.getApplication()
				.getMainFrame(), v);
		dlg.setLocationRelativeTo(SchliessfachApp.getApplication()
				.getMainFrame());
		dlg.setVisible(true);
		aktualisiereBetraege();
		aktualisiereKautionenliste();
		aktualisiereMietenliste();
	}// GEN-LAST:event_einAusZahlungActionPerformed

	private void detailsTabsStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_detailsTabsStateChanged
		einAusZahlung.setEnabled(detailsTabs.getSelectedIndex() == 0);
	}// GEN-LAST:event_detailsTabsStateChanged

	private void mieteEntfernenActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mieteEntfernenActionPerformed
		int zeile;
		if ((zeile = mietenTabelle.getSelectedRow()) == -1) {
			JOptionPane.showMessageDialog(this.getFrame(),
					"Sie müssen zuerste einen Eintrag auswählen.",
					"Miete entfernen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Long id = (Long) mietenTabelle.getModel().getValueAt(zeile, 0);
		Zahlung z = em.find(Zahlung.class, id);
		if (z == null) {
			JOptionPane.showMessageDialog(this.getFrame(),
					"Die Zahlung mit der Nummer " + id
							+ " wurde nicht gefunden.", "Miete entfernen",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		em.getTransaction().begin();
		z.getVertrag().getZahlungen().remove(z);
		em.remove(z);
		em.getTransaction().commit();
		aktualisiereMietenliste();
		aktualisiereBetraege();
	}// GEN-LAST:event_mieteEntfernenActionPerformed

	private void kautionEntfernenActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_kautionEntfernenActionPerformed
		int zeile;
		if ((zeile = kautionenTabelle.getSelectedRow()) == -1) {
			JOptionPane.showMessageDialog(this.getFrame(),
					"Sie müssen zuerste einen Eintrag auswählen.",
					"Kaution entfernen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Long id = (Long) kautionenTabelle.getModel().getValueAt(zeile, 0);
		Zahlung z = em.find(Zahlung.class, id);
		if (z == null) {
			JOptionPane.showMessageDialog(this.getFrame(),
					"Die Zahlung mit der Nummer " + id
							+ " wurde nicht gefunden.", "Kaution entfernen",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		em.getTransaction().begin();
		z.getVertrag().getZahlungen().remove(z);
		em.remove(z);
		em.getTransaction().commit();
		aktualisiereKautionenliste();
		aktualisiereBetraege();
	}// GEN-LAST:event_kautionEntfernenActionPerformed

	private void vertragZuordnenActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_vertragZuordnenActionPerformed
		int zeile = vertragTabelle.getSelectedRow();
		if (zeile == -1) {
			JOptionPane.showMessageDialog(this.getFrame(),
					"Sie müssen zuerst einen Vertrag auswählen.",
					"Schließfach zuordnen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Long id = (Long) vertragTabelle.getModel().getValueAt(zeile, 0);
		Vertrag vertrag = em.find(Vertrag.class, id);
		if (vertrag == null) {
			JOptionPane.showMessageDialog(this.getFrame(),
					"Der Vertrag mit der Nummer " + id
							+ " wurde nicht gefunden.", "Schließfach zuordnen",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (vertrag.getSchliessfach() != null) {
			JOptionPane
					.showMessageDialog(
							this.getFrame(),
							"Es it bereits ein Schliessfach zugeordnet.\nGeben Sie dieses zunächst frei.",
							"Schließfach zuordnen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		em.getTransaction().begin();
		SchliessfachAuswahlDlg dlg = new SchliessfachAuswahlDlg(SchliessfachApp
				.getApplication().getMainFrame(), vertrag);
		dlg.setLocationRelativeTo(this.getFrame());
		dlg.setVisible(true);
		em.getTransaction().commit();
		aktualisiereFächer();
		aktualisiereVertragliste();
	}// GEN-LAST:event_vertragZuordnenActionPerformed

	private void vertragFreigebenActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_vertragFreigebenActionPerformed
		int zeile = vertragTabelle.getSelectedRow();
		if (zeile == -1) {
			JOptionPane.showMessageDialog(this.getFrame(),
					"Sie müssen zuerst einen Vertrag auswählen.",
					"Schließfach freigeben", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Long id = (Long) vertragTabelle.getModel().getValueAt(zeile, 0);
		Vertrag vertrag = em.find(Vertrag.class, id);
		if (vertrag == null) {
			JOptionPane.showMessageDialog(this.getFrame(),
					"Der Vertrag mit der Nummer " + id
							+ " wurde nicht gefunden.",
					"Schließfach freigeben", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Ist noch ein Schlüssel ausgeteilt?
		if (vertrag.getSchliessfach() == null) {
			return;
		}
		Query q = em
				.createQuery("SELECT s FROM Schluessel s WHERE s.schliessfach.nr="
						+ vertrag.getSchliessfach().getNr()
						+ " AND s.ausgegeben=true");
		List l = q.getResultList();
		if (l != null && l.size() > 0) {
			JOptionPane
					.showMessageDialog(
							this.getFrame(),
							"Das Schließfach kann nicht freigegeben werden.\nEs ist noch mindestens ein Schlüssel ausgegeben.",
							"Schließfach freigeben", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Schliessfach s = vertrag.getSchliessfach();
		em.getTransaction().begin();
		vertrag.setSchliessfach(null);
		s.setVertrag(null);
		em.getTransaction().commit();
		aktualisiereFächer();
		aktualisiereVertragliste();
	}// GEN-LAST:event_vertragFreigebenActionPerformed
		// Variables declaration - do not modify//GEN-BEGIN:variables

	private javax.swing.JFormattedTextField anzahlFrei;
	private javax.persistence.Query anzahlFreiQuery;
	private javax.swing.JFormattedTextField anzahlGesamt;
	private javax.persistence.Query anzahlGesamtQuery;
	private javax.swing.JComboBox auswahlJahr;
	private javax.swing.JComboBox auswahlKlasse;
	private javax.swing.JTabbedPane detailsTabs;
	private javax.swing.JButton einAusZahlung;
	private javax.persistence.EntityManager em;
	private javax.persistence.Query gebuehrQuery;
	private javax.swing.JFormattedTextField gebuehrenBetrag;
	private javax.swing.JFormattedTextField geburt;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel11;
	private javax.swing.JLabel jLabel12;
	private javax.swing.JLabel jLabel13;
	private javax.swing.JLabel jLabel14;
	private javax.swing.JLabel jLabel15;
	private javax.swing.JLabel jLabel16;
	private javax.swing.JLabel jLabel17;
	private javax.swing.JLabel jLabel18;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JMenuItem kautionsListe;
	private javax.swing.JMenuItem schluesselFehlliste;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JScrollPane jScrollPane4;
	private javax.swing.JScrollPane jScrollPane5;
	private javax.swing.JSplitPane jSplitPane1;
	private java.util.List<Long> jahrListe;
	private javax.persistence.Query jahrQuery;
	private javax.swing.JButton jahrVerwalten;
	private javax.swing.JMenuItem jahrVerwaltung;
	private javax.swing.JFormattedTextField kautionBetrag;
	private javax.swing.JButton kautionEntfernen;
	private javax.swing.JButton kautionNeu;
	private javax.persistence.Query kautionQuery;
	private java.util.List<Zahlung> kautionenListe;
	private javax.persistence.Query kautionenQuery;
	private javax.swing.JTable kautionenTabelle;
	private javax.swing.JFormattedTextField klasse;
	private javax.swing.JList klassenListe;
	private javax.swing.JFormattedTextField lehrer;
	private javax.swing.JMenu listenMenu;
	private javax.swing.JPanel mainPanel;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JMenuItem miDrucker;
	private javax.swing.JMenuItem miImportieren;
	private javax.swing.JMenuItem miNummernImportieren;
	private javax.swing.JMenuItem miKlassenliste;
	private javax.swing.JButton mieteEntfernen;
	private javax.swing.JButton mieteNeu;
	private java.util.List<Zahlung> mietenListe;
	private javax.persistence.Query mietenQuery;
	private javax.swing.JTable mietenTabelle;
	private javax.swing.JFormattedTextField nachName;
	private javax.swing.JFormattedTextField nr;
	private javax.swing.JProgressBar progressBar;
	private javax.swing.JPanel sAktionenPanel;
	private javax.swing.JButton schluesselEntfernen;
	private java.util.List<Schluessel> schluesselListe;
	private javax.swing.JButton schluesselNeu;
	private javax.persistence.Query schluesselQuery;
	private javax.swing.JTable schluesselTabelle;
	private javax.persistence.Query schuelerVertraegeQuery;
	private javax.swing.JLabel statusAnimationLabel;
	private javax.swing.JLabel statusMessageLabel;
	private javax.swing.JPanel statusPanel;
	private javax.swing.JMenuItem sucheSchluessel;
	private javax.swing.JMenuItem sucheSchueler;
	private javax.swing.JMenu suchenMenu;
	private javax.swing.JPanel vAktionenPanel;
	private javax.swing.JButton vertragBearbeiten;
	private javax.swing.JButton vertragEntfernen;
	private javax.swing.JButton vertragFreigeben;
	private java.util.List<Vertrag> vertragListe;
	private javax.swing.JButton vertragNeu;
	private javax.swing.JPanel vertragSchluesselPanel;
	private javax.swing.JTable vertragTabelle;
	private javax.swing.JButton vertragZuordnen;
	private javax.swing.JMenuItem verwaltungFach;
	private javax.swing.JMenuItem verwaltungGebuehren;
	private javax.swing.JMenu verwaltungMenu;
	private javax.swing.JMenuItem verwaltungSchluessel;
	private javax.swing.JMenuItem verwaltungSchueler;
	private javax.swing.JFormattedTextField vorName;
	private javax.swing.JPanel zAktionenPanel;
	private javax.swing.JPanel zKautionenPanel;
	private javax.swing.JPanel zMietenPanel;
	private javax.swing.JPanel zahlungenPanel;
	private JMenuItem offenePostenListe;
	private JMenuItem ausgegebeneSchluesselListe;
	private org.jdesktop.beansbinding.BindingGroup bindingGroup;

	// End of variables declaration//GEN-END:variables
	private final Timer messageTimer;
	private final Timer busyIconTimer;
	private final Icon idleIcon;
	private final Icon[] busyIcons = new Icon[15];
	private int busyIconIndex = 0;
	private JDialog aboutBox;
	private JMenuItem hinweiseMenuItem;

	public void aktualisiereKlassenliste() {
		if (em == null) {
			return;
		}
		Query q = em
				.createQuery("SELECT DISTINCT s.klasse FROM Schueler s WHERE NOT s.status=schueler.SchuelerStatus.INAKTIV ORDER BY s.klasse ASC");
		@SuppressWarnings("unchecked")
		List<String> klassen = q.getResultList();
		MutableComboBoxModel mcbm = (MutableComboBoxModel) auswahlKlasse
				.getModel();
		int alt = klasseModel.size();
		int neu = klassen.size();
		int set = Math.min(alt, neu);
		for (int i = 0; i < set; i++) {
			if (!klassen.get(i).equals(klasseModel.get(i))) {
				mcbm.removeElementAt(i);
				mcbm.insertElementAt(klassen.get(i), i);
			}
		}
		for (int i = neu; i < alt; i++) {
			mcbm.removeElementAt(neu);
		}
		for (int i = alt; i < neu; i++) {
			mcbm.addElement(klassen.get(i));
		}
		klassenlisteIstAktuell = true;
	}

	public void aktualisiereSchuelerliste() {
		schuelerModel.clear();
		if (em == null || auswahlKlasse.getSelectedItem() == null) {
			return;
		}
		Query q = em
				.createQuery("SELECT s.nachName, s.vorName FROM Schueler s WHERE s.klasse=:klasse AND NOT s.status=schueler.SchuelerStatus.INAKTIV ORDER BY s.nachName ASC,s.vorName ASC");
		q.setParameter("klasse", auswahlKlasse.getSelectedItem());
		@SuppressWarnings("unchecked")
		Iterator<String[]> schueler = q.getResultList().iterator();
		while (schueler.hasNext()) {
			Object[] t = schueler.next();
			String[] a = Arrays.copyOf(t, t.length, String[].class);
			schuelerModel.addElement(a[0] + "," + a[1]);
		}
		aktuellerSchueler = null;
		aktualisiereSchuelerDetails();
	}

	public void aktualisiereJahrliste() {
		if (em == null) {
			return;
		}
		jahrListe.clear();
		jahrListe.addAll(new java.util.LinkedList(jahrQuery.getResultList()));
		if (jahrListe.size() > 0) {
			auswahlJahr.getModel().setSelectedItem(
					jahrListe.get(jahrListe.size() - 1));
		}
	}

	public void waehleSchueler(String name) {
		if (em == null || name == null) {
			return;
		}
		Query q = em
				.createQuery("SELECT s FROM Schueler s WHERE s.klasse=:klasse AND s.nachName=:nachname AND s.vorName=:vorname");
		q.setParameter("klasse", auswahlKlasse.getSelectedItem());
		int pos = name.indexOf(",");
		q.setParameter("vorname", name.substring(pos + 1));
		q.setParameter("nachname", name.substring(0, pos));
		aktuellerSchueler = (Schueler) q.getSingleResult();
		aktualisiereSchuelerDetails();
	}

	private void aktualisiereFächer() {
		// FIXME: Die Aktualisierung der Bindung funktioniert nicht.
		try {
			SyncFailure result;
			result = bindingGroup.getBinding("anzahlFreiBindung")
					.refreshAndNotify();
			if (result != null) {
				throw new NullPointerException("Sync Fehler anzahlFreiBindung");
			}
			result = bindingGroup.getBinding("anzahlGesamtBindung")
					.refreshAndNotify();
			if (result != null) {
				throw new NullPointerException(
						"Sync Fehler anzahlGesamtBindung");
			}
		} catch (NullPointerException ex) {
			Logger.getLogger(SchliessfachView.class.getName()).log(
					Level.SEVERE, ex.getMessage());
		}
	}

	private void aktualisiereVertragliste() {
		if (vertragListe != null) {
			vertragListe.clear();
			if (aktuellerSchueler != null && schuelerVertraegeQuery != null) {
				schuelerVertraegeQuery.setParameter("schuelerId",
						aktuellerSchueler.getId());
				vertragListe.addAll(schuelerVertraegeQuery.getResultList());
			}
		}
		aktualisiereSchluesselliste();
	}

	private void aktualisiereSchluesselliste() {
		if (schluesselListe != null) {
			schluesselListe.clear();
			if (aktuellerSchueler != null && schluesselQuery != null) {
				schluesselQuery.setParameter("schuelerId",
						aktuellerSchueler.getId());
				schluesselListe.addAll(schluesselQuery.getResultList());
			}
		}
	}

	private void aktualisiereKautionenliste() {
		if (kautionenListe != null) {
			kautionenListe.clear();
			if (aktuellerSchueler != null && kautionenQuery != null) {
				kautionenQuery.setParameter("schuelerId",
						aktuellerSchueler.getId());
				kautionenListe.addAll(kautionenQuery.getResultList());
			}
		}
	}

	private void aktualisiereMietenliste() {
		if (mietenListe != null) {
			mietenListe.clear();
			if (aktuellerSchueler != null && mietenQuery != null) {
				mietenQuery.setParameter("schuelerId1",
						aktuellerSchueler.getId());
				mietenQuery.setParameter("schuelerId2",
						aktuellerSchueler.getId());
				mietenListe.addAll(mietenQuery.getResultList());
			}
		}
	}

	private void aktualisiereBetraege() {
		if (aktuellerSchueler != null && gebuehrQuery != null) {
			gebuehrQuery.setParameter("schuelerId", aktuellerSchueler.getId());
			gebuehrenBetrag.setValue((Double) gebuehrQuery.getSingleResult());
			kautionQuery.setParameter("schuelerId", aktuellerSchueler.getId());
			kautionBetrag.setValue((Double) kautionQuery.getSingleResult());
		} else {
			gebuehrenBetrag.setValue((Double) null);
			kautionBetrag.setValue((Double) null);
		}
	}

	public void aktualisiereSchuelerDetails() {
		if (isInitializing) {
			return;
		}
		nachName.setValue(aktuellerSchueler == null ? "" : aktuellerSchueler
				.getNachName());
		vorName.setValue(aktuellerSchueler == null ? "" : aktuellerSchueler
				.getVorName());
		nr.setValue(aktuellerSchueler == null ? 0L : aktuellerSchueler.getNr());
		geburt.setValue(aktuellerSchueler == null ? null : aktuellerSchueler
				.getGeburt());
		klasse.setValue(aktuellerSchueler == null ? "" : aktuellerSchueler
				.getKlasse());
		lehrer.setValue(aktuellerSchueler == null ? "" : aktuellerSchueler
				.getLehrer());
		if (em == null) {
			return;
		}
		aktualisiereVertragliste();
		aktualisiereBetraege();
		aktualisiereKautionenliste();
		aktualisiereMietenliste();
	}

	private String[] longList2StringArray(List<Long> l) {
		if (l == null) {
			return new String[] {};
		}
		String[] result = new String[l.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = String.format("%04d", l.get(i));
		}
		return result;
	}

	private String[] namenList2StringArray(List<Object[]> l) {
		if (l == null) {
			return new String[] {};
		}
		String[] result = new String[l.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = l.get(i)[0] + "," + l.get(i)[1];
		}
		return result;
	}

	@Action
	public void druckerEinstellen() {
		SchliessfachApp app = SchliessfachApp.getApplication();
		DruckerEinstellung dlg = new DruckerEinstellung(app.druckDienst,
				app.druckAttribut);
		if (dlg.zeigeDialog()) {
			app.druckDienst = dlg.getDruckDienst();
			app.druckAttribut = dlg.getDruckAttribut();
		}
	}

	private String tabellenAnfang(String[] ueberschrift) {
		return "<html><body><div style=\"font-size:95%\">\n"
				+ "<table rules=\"rows\" border=\"1\">\n"
				+ tabellenKopf(ueberschrift);
	}

	private String tabellenKopf(String[] ueberschrift) {
		String result = "<tr align=\"left\">";
		for (String s : ueberschrift) {
			result += "<th align=\"left\">" + s + "</th>";
		}
		return result;
	}

	private String tabellenZeile(String[] zeile) {
		String result = "<tr align=\"left\">";
		for (String s : zeile) {
			result += "<td align=\"left\">" + s + "</td>";
		}
		return result + "</tr>\n";
	}

	private String tabellenEnde() {
		return "</table>\n</div></body></html>";
	}

	public void listeKlasse(boolean mieten) {
		String sKlasse = (String) auswahlKlasse.getSelectedItem();
		if (sKlasse == null) {
			return;
		}
		String sBedingung = mieten ? Zahlungsart.gebuehrenQS("s.klasse='"
				+ sKlasse + "'  AND z.art") : Zahlungsart
				.kautionenQS("s.klasse='" + sKlasse + "'  AND z.art");
		Query q = em
				.createQuery("SELECT s,v,SUM(z.betrag) FROM Schueler s LEFT JOIN s.vertraege v LEFT JOIN v.zahlungen z WHERE "
						+ sBedingung
						+ " GROUP BY s,v ORDER BY s.nachName,s.vorName,v.id");
		String inhalt = tabellenAnfang(new String[] { "Name", "Nr", "Beginn",
				mieten ? "Mietkonto" : "Kautionskonto", "Ende" });
		Iterator<Object[]> zeile = q.getResultList().iterator();
		Object[] o;
		Schueler s;
		Vertrag v;
		Double geb;
		while (zeile.hasNext()) {
			o = zeile.next();
			s = (Schueler) o[0];
			v = (Vertrag) o[1];
			geb = (Double) o[2];
			// geschlossene und ausgeglichene Miet-/Kautionskonten sollen nicht
			// angezeigt werden.
			if (v != null && v.getEndeJahr() != null && geb != null
					&& geb == 0.0)
				continue;
			if (v != null && geb != null) {
				inhalt += tabellenZeile(new String[] {
						s.getNachName() + "," + s.getVorName(),
						v.getId().toString(),
						v.getBeginnJahr().toString(),
						geb.toString(),
						v.getEndeJahr() == null ? "" : v.getEndeJahr()
								.toString() });
			} else {
				inhalt += tabellenZeile(new String[] {
						s.getNachName() + "," + s.getVorName(), "", "", "", "" });
			}
		}
		inhalt += tabellenEnde();
		DruckVorschauDlg dlg = new DruckVorschauDlg(this.getFrame(), inhalt,
				"Klassenliste " + sKlasse);
		dlg.setLocationRelativeTo(this.getFrame());
		dlg.setVisible(true);
	}

	@Action
	public void gebuehrenVerwaltung() {
		Jahr diesJahr = (Jahr) auswahlJahr.getSelectedItem();
		if (diesJahr == null) {
			String s = JOptionPane
					.showInputDialog(
							this.getFrame(),
							"Bitte geben Sie das Jahr ein, für das Sie die Gebühren bearbeiten wollen.",
							"Gebühren bearbeiten", JOptionPane.QUESTION_MESSAGE);
			if (s == null) {
				return;
			}
			try {
				diesJahr = new Jahr(Long.parseLong(s), "");
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this.getFrame(),
						"Das eingegebene Jahr " + s + " ist ungültig.",
						"Gebühren bearbeiten", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		GebuehrenDlg dlg = new GebuehrenDlg(this.getFrame(), diesJahr);
		dlg.setLocationRelativeTo(this.getFrame());
		dlg.setVisible(true);
		diesJahr = (Jahr) auswahlJahr.getSelectedItem();
		if (diesJahr == null) {
			SchliessfachApp.getApplication().aktualisiereGebuehren(
					diesJahr.getJahr().intValue());
		}
	}

	@Action
	public void kautionsListe() {
		listeKlasse(false);
	}

	@Action
	public void mietListe() {
		listeKlasse(true);
	}

	@Action
	public void schluesselFehlliste() {
		Query q = em
				.createQuery("SELECT sf,COUNT(sl) FROM Schliessfach sf LEFT JOIN sf.schluessel sl WHERE (SELECT COUNT(sls) FROM sf.schluessel sls) < 2 GROUP BY sf ORDER BY sf.nr");
		String inhalt = tabellenAnfang(new String[] { "Schließfach", "Anzahl" });
		Iterator<Object[]> zeile = q.getResultList().iterator();
		Object[] o;
		Schliessfach sf;
		Long slanzahl;
		while (zeile.hasNext()) {
			o = zeile.next();
			sf = (Schliessfach) o[0];
			slanzahl = (Long) o[1];
			inhalt += tabellenZeile(new String[] { sf.getNr().toString(),
					slanzahl.toString() });
		}
		inhalt += tabellenEnde();
		DruckVorschauDlg dlg = new DruckVorschauDlg(this.getFrame(), inhalt,
				"Liste aller Schließfächer mit weniger als 2 Schlüsseln");
		dlg.setLocationRelativeTo(this.getFrame());
		dlg.setVisible(true);
	}

	@Action
	public void offenePostenListe() {
		// Liste aller nicht ausgeglichenen Mietkonten aller Klassen geordnet
		// nach Klasse, Schüler, Mietkontostand
		Query q = em
				.createQuery("SELECT s,v,SUM(z.betrag) FROM Schueler s LEFT JOIN s.vertraege v LEFT JOIN v.zahlungen z WHERE "
						+ Zahlungsart.mietenQS("z.art")
						+ " GROUP BY s,v ORDER BY s.klasse,s.nachName,s.vorName,v.id");
		String inhalt = tabellenAnfang(new String[] { "Klasse", "Name", "Nr",
				"Beginn", "Mietkonto", "Ende" });
		Iterator<Object[]> zeile = q.getResultList().iterator();
		Object[] o;
		Schueler s;
		Vertrag v;
		Double geb;
		while (zeile.hasNext()) {
			o = zeile.next();
			s = (Schueler) o[0];
			v = (Vertrag) o[1];
			geb = (Double) o[2];
			if (geb == null || (geb != null && geb == 0.0))
				continue;
			if (v != null && geb != null) {
				inhalt += tabellenZeile(new String[] {
						s.getKlasse(),
						s.getNachName() + "," + s.getVorName(),
						v.getId().toString(),
						v.getBeginnJahr().toString(),
						geb.toString(),
						v.getEndeJahr() == null ? "" : v.getEndeJahr()
								.toString() });
			} else {
				inhalt += tabellenZeile(new String[] { s.getKlasse(),
						s.getNachName() + "," + s.getVorName(), "", "", "", "" });
			}
		}
		inhalt += tabellenEnde();
		DruckVorschauDlg dlg = new DruckVorschauDlg(this.getFrame(), inhalt,
				"Gesamtliste: Offene Posten");
		dlg.setLocationRelativeTo(this.getFrame());
		dlg.setVisible(true);
	}

	@Action
	public void ausgegebeneSchluesselListe() {
		// Liste aller ausgegebenen Schlüssel geordnet nach Schließfach,
		// Schlüssel, Schüler, Klasse
		Query q = em
				.createQuery("SELECT sl,sf,v,s FROM Schluessel sl INNER JOIN sl.schliessfach sf INNER JOIN sl.anVertrag v INNER JOIN  v.schueler s WHERE "
						+ " sl.ausgegeben='TRUE' "
						+ " ORDER BY sf.nr,sl.nr,s.nachName,s.vorName");
		String inhalt = tabellenAnfang(new String[] { "Schließfach",
				"Schlüssel", "Vertrag", "Schüler", "Klasse" });
		Iterator<Object[]> zeile = q.getResultList().iterator();
		Object[] o;
		Schueler s;
		Vertrag v;
		Schluessel sl;
		Schliessfach sf;
		while (zeile.hasNext()) {
			o = zeile.next();
			sl = (Schluessel) o[0];
			sf = (Schliessfach) o[1];
			v = (Vertrag) o[2];
			s = (Schueler) o[3];
			inhalt += tabellenZeile(new String[] { sf.getNr().toString(),
					sl.getNr().toString(), v.getId().toString(),
					s.getNachName() + "," + s.getVorName(), s.getKlasse() });

		}
		inhalt += tabellenEnde();
		DruckVorschauDlg dlg = new DruckVorschauDlg(this.getFrame(), inhalt,
				"Gesamtliste: Ausgegebene Schlüssel");
		dlg.setLocationRelativeTo(this.getFrame());
		dlg.setVisible(true);
	}

	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
		}
	}
}
