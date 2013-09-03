package factories;

public class TabellenFactory {

	public static String tabellenAnfang(String[] ueberschrift) {
		return "<html><body><div style=\"font-size:95%\">\n"
				+ "<table rules=\"rows\" border=\"1\">\n"
				+ tabellenKopf(ueberschrift);
	}

	public static String tabellenAnfang(String ueberschrift) {
		return "<html><body><div style=\"font-size:95%\">\n"
				+ "<table rules=\"rows\" border=\"1\">\n"
				+ tabellenKopf(ueberschrift);
	}

	public static String tabellenKopf(String[] ueberschrift) {
		String result = "<tr align=\"left\">";
		for (String s : ueberschrift) {
			result += "<th align=\"left\">" + s + "</th>";
		}
		return result;
	}

	public static String tabellenKopf(String ueberschrift) {
		return "<tr align=\"left\"><th align=\"left\">" + ueberschrift + "</th></tr>\n";
	}

	public static String tabellenZeile(String[] zeile) {
		String result = "<tr align=\"left\">";
		for (String s : zeile) {
			result += "<td align=\"left\">" + s + "</td>";
		}
		return result + "</tr>\n";
	}

	public static String tabellenZeile(String zeile) {
		return "<tr align=\"left\"><td align=\"left\">" + zeile + "</td></tr>\n";
	}

	public static String tabellenEnde() {
		return "</table>\n</div></body></html>";
	}

}
