package historie;

import java.awt.Container;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ButtonGroup;

import schliessfach.SchliessfachApp;

public class HistorieDlg extends JDialog {
	private static final boolean DEBUG = true;
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

	public HistorieDlg(JFrame parent) {
		super(parent);
		em = SchliessfachApp.getApplication().em;
		inhalt = new JList();
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
		kennung.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (DEBUG)
					System.out.println("kennung.actionPerformed: selected("
							+ kennung.isSelected() + ")");
				aktualisiereInhalt();
			}
		});

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		scrollPane.setViewportView(inhalt);

	}

	private String historieRubrik() {
		if (rubrik == null || !rubrik.isSelected())
			return "";
		else {
			return "WHERE h.rubrik = historie.Rubrik."
					+ cbRubrik.getSelectedItem();
		}
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
		String s = historieQueryString + " " + historieRubrik() + " "
				+ historieSortierung();
		Query q = em.createQuery(s);
		historieListe = q.getResultList().toArray();
		inhalt.setListData(historieListe);
	}
}
