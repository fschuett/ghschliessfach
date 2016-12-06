GHSchliessfach
==============

Installation
------------
Das Programm geht davon aus, das sich eine passende Datenbank im
Programmverzeichnis unter "SchliessfachDB" befindet.
Diese Datenbank muss vor der Verwendung erzeugt werden.


Erzeugen des Programms
----------------------
Es handelt sich um ein ant-Projekt. Tippen Sie

ant

ant create_run_jar

um das JAR-Archiv zu erzeugen.


Erzeugen der Datenbank
----------------------
Es sind die Dateien ij.sh und SchliessfachDB.sql notwendig.

Starten Sie ij.sh: ./ij.sh
 -> Es erscheint der Prompt von ij.
 
Erzeugen Sie die Datenbank durch den ij-Befehl: connect 'jdbc:derby:SchliessfachDB;create=true';
 -> Fertigmeldung

Erzeugen Sie die Tabellen in der Datenbank durch den ij-Befehl: run 'SchliessfachDB.sql';
 -> Einige Warnungen über doppelte Indizes, die ignoriert werden können.

Verlassen Sie ij: exit;

Es ist ein Unterverzeichnis 'SchliessfachDB' mit der leeren Datenbank entstanden.

Aktualisieren der Datenbank
---------------------------
Falls der Programmstart fehlschlägt, ist die Datenbankversion nicht aktuell.

Starten Sie ij.sh: ./ij.sh
 -> Es erscheint der Prompt von ij.
 
Verbinden Sie die Datenbank durch den ij-Befehl: connect 'jdbc:derby:SchliessfachDB';
 -> Fertigmeldung

Erzeugen Sie die neuen Tabellen in der Datenbank durch den ij-Befehl: run 'SchliessfachDB_1.0-2.0.sql';
oder die Datei, die in der Programmfehlermeldung genannt worden ist.

Verlassen Sie ij: exit;


Programmstart
-------------
Starten Sie das Programm durch: java -jar GHSchliessfach.jar


Verwendung
----------
Das Feld "Jahr" bezieht sich immer auf Schuljahre (vom Beginnn des Schuljahres nach den
Sommerferien bis zum Ende des Schuljahres zu Beginn der nächsten Sommerferien).

Ein einjähriger Mietvertrag läuft demnach z.B. vom Beginn-Jahr 2011 bis zum Ende-Jahr 2011.

Schüler werden beim Entfernen nicht aus der Datenbank entfernt, sondern ihr Status wird auf
INAKTIV gesetzt. Auf diese Weise hat man später immer noch Zugriff auf alte Schüler und
Verträge.

Schülerimport
-------------
Die Schülerdaten können aus jedem Schulverwaltungsprogramm importiert werden, das CSV-Daten
im Zeichensatz IBM850 erzeugen kann. Andernfalls gibt es genügend Programme zur Konvertierung
exportierter Dateien.

Das Programm GHSchliessfach erwartet in jeder Zeile:
"<Nummer>";"<Nachname>";"<Vorname>";"<Geburtsdatum>";"<Klasse>";"<Lehrer>"

Die Datumsangabe wird in deutschem Format(tt.mm.jj bzw. tt.mm.jjjj) erwartet.

Es können nur Schüler entfernt werden, für die kein offener Vertrag existiert 
und deren Konten ausgeglichen sind.

Schülernummernimport
--------------------
Bei einem Wechsel des Schulverwaltungsprogramms können sich die Schülernummern ändern.
Dieser Menüpunkt ermöglicht die Ersetzung der alten durch neue Nummern.
Dateiformat:
<Überschrift>
<Nummer_alt>;<Nummer_neu>
...

Hildesheim, 2016
Frank Schütte
fschuett@gymhim.de
