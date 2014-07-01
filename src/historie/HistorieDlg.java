package historie;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import schliessfach.SchliessfachApp;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class HistorieDlg extends JDialog {
	private static final boolean DEBUG = false;
	private JList inhalt;
	private final ButtonGroup auswahl = new ButtonGroup();
	private final ButtonGroup sortierung = new ButtonGroup();
	private EntityManager em;
	private Object[] historieListe;
	private static final String historieQueryString = "SELECT h FROM Historie h";

	private JRadioButton rubrik;
	private JRadioButton alles;
	private JRadioButton datum;
	private JRadioButton kennung;
	private JComboBox cbRubrik;
	private JLabel lblEinschrnkung;
	private JTextField einschraenkung;

	public HistorieDlg(JFrame parent) {
		super(parent);
		em = SchliessfachApp.getApplication().em;
		inhalt = new JList();
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		scrollPane.setViewportView(inhalt);
		aktualisiereInhalt();

		JPanel auswahlPanel = new JPanel();
		getContentPane().add(auswahlPanel, BorderLayout.NORTH);

		alles = new JRadioButton("Alles");
		alles.setSelected(true);
		auswahl.add(alles);
		auswahlPanel.add(alles);
		alles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (DEBUG)
					System.out.println("alles.actionPerformed: selected("
							+ alles.isSelected() + ")");
				cbRubrik.setEnabled(false);
				aktualisiereInhalt();
			}
		});

		rubrik = new JRadioButton("Rubrik:");
		auswahl.add(rubrik);
		auswahlPanel.add(rubrik);
		rubrik.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (DEBUG)
					System.out.println("rubrik.actionPerformed: selected("
							+ rubrik.isSelected() + ")");
				cbRubrik.setEnabled(true);
				aktualisiereInhalt();
			}
		});

		cbRubrik = new JComboBox(Rubrik.values());
		cbRubrik.setSelectedIndex(0);
		cbRubrik.setEnabled(rubrik.isSelected());
		auswahlPanel.add(cbRubrik);
		cbRubrik.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (DEBUG)
					System.out.println("cbRubrik.actionPerformed: selected("
							+ cbRubrik.getSelectedItem() + ")");
				aktualisiereInhalt();
			}
		});

		JLabel lblSortierung = new JLabel("Sortierung:");
		auswahlPanel.add(lblSortierung);

		datum = new JRadioButton("Datum");
		datum.setSelected(true);
		sortierung.add(datum);
		auswahlPanel.add(datum);
		datum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (DEBUG)
					System.out.println("datum.actionPerformed: selected("
							+ datum.isSelected() + ")");
				aktualisiereInhalt();
			}
		});

		kennung = new JRadioButton("Kennung");
		sortierung.add(kennung);
		auswahlPanel.add(kennung);
		
		lblEinschrnkung = new JLabel("Einschränkung:");
		auswahlPanel.add(lblEinschrnkung);
		
		einschraenkung = new JTextField();
		auswahlPanel.add(einschraenkung);
		einschraenkung.setColumns(10);
		einschraenkung.getDocument().addDocumentListener(new DocumentListener(){
			public void removeUpdate(DocumentEvent de) {
				if (DEBUG)
					System.out.println("einschraenkung.removeUpdate: stelle("+de.getOffset()+") anzahl("+de.getLength()+")");
				aktualisiereInhalt();
			}
			public void insertUpdate(DocumentEvent de) {
				if (DEBUG)
					System.out.println("einschraenkung.insertUpdate: stelle("+de.getOffset()+") anzahl("+de.getLength()+")");
				aktualisiereInhalt();
			}
			public void changedUpdate(DocumentEvent de) {
				if (DEBUG)
					System.out.println("einschraenkung.changedUpdate: stelle("+de.getOffset()+") anzahl("+de.getLength()+")");
				aktualisiereInhalt();
			}
		});
		kennung.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (DEBUG)
					System.out.println("kennung.actionPerformed: selected("
							+ kennung.isSelected() + ")");
				aktualisiereInhalt();
			}
		});

		setSize(800, 400);
	}

	private String historieEinschraenkung() {
		if (einschraenkung == null || "".equals(einschraenkung.getText()))
			return null;
		else {
			return "(h.kennung LIKE '%" + einschraenkung.getText() + "%' OR h.inhalt LIKE '%" + einschraenkung.getText() + "%')";
		}
	}
	
	private String historieRubrik() {
		if (rubrik == null || !rubrik.isSelected())
			return null;
		else {
			return "h.rubrik = historie.Rubrik."
					+ cbRubrik.getSelectedItem();
		}
	}

	private String historieWhere() {
		String w1 = historieRubrik();
		String w2 = historieEinschraenkung();
		if(w1 == null && w2 == null)
			return "";
		else if(w1 != null && w2 != null)
			return "WHERE "+w1+" AND "+w2;
		else
			return "WHERE "+(w1 == null? "":w1)+(w2==null?"":w2);
	}
	
	private String historieSortierung() {
		if (datum == null || kennung == null || datum.isSelected())
			return "ORDER BY h.zeitpunkt DESC";
		else
			return "ORDER BY h.kennung, h.zeitpunkt DESC";
	}

	private void aktualisiereInhalt() {
		if (em == null || inhalt == null)
			return;
		String s = historieQueryString + " " + historieWhere() + " "
				+ historieSortierung();
		Query q = em.createQuery(s);
		historieListe = q.getResultList().toArray();
		inhalt.setListData(historieListe);
	}
}
