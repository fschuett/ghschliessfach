package vertrag;

import java.util.EnumSet;
import java.util.Set;

public enum Zahlungsart {
	Miete("Miete"),
        MieteZurueck("Mietrückzahlung"),
        MieteZweitschluessel("Miete Zweitschlüssel"),
        MieteZweitschluesselZurueck("Miete Zweitschlüssel zurück"),
        Kaution("Kaution"),
        KautionZurueck("Rückzahlung der Kaution"),
        KautionZweitschluessel("Kaution für einen Zweitschlüssel"),
        KautionZweitschluesselZurueck("Rückzahlung der Zweitschlüsselkaution"),
        MieteHalbjahr("Miete für ein halbes Jahr"),
        MieteZurueckHalbjahr("Mietrückzahlung für ein halbes Jahr"),
        MieteZweitschluesselHalbjahr("Miete Zweitschlüssel für ein halbes Jahr"),
        MieteZweitschluesselHalbjahrZurueck("Mietrückzahlung Zweitschlüssel für ein halbes Jahr"),
        Sonder,
        SonderZurueck,
        Ersatzschluessel("Gebühr für einen Ersatzschlüssel");

        private String kommentar;

        private Zahlungsart(){
            kommentar = "";
        }

        private Zahlungsart(String kommentar){
            this.kommentar = kommentar;
        }

        public String getKommentar(){
            return kommentar;
        }

        public static Set<Zahlungsart> mieten;
        public static Set<Zahlungsart> kautionen;
        public static Set<Zahlungsart> gebuehren;
        public static Set<Zahlungsart> sonderfall;
        public static Set<Zahlungsart> einzahlung;
        public static Set<Zahlungsart> auszahlung;

        public static String mietenQS(String var){
            return " "+var+"=vertrag.Zahlungsart.Miete OR "+var+"=vertrag.Zahlungsart.MieteZurueck OR "
                    +var+"=vertrag.Zahlungsart.MieteZweitschluessel OR "+var+"=vertrag.Zahlungsart.MieteZweitschluesselZurueck OR "
                    +var+"=vertrag.Zahlungsart.MieteHalbjahr OR "+var+"=vertrag.Zahlungsart.MieteZurueckHalbjahr OR "
                    +var+"=vertrag.Zahlungsart.MieteZweitschluesselHalbjahr OR "
                    +var+"=vertrag.Zahlungsart.MieteZweitschluesselHalbjahrZurueck ";
        }

        public static String kautionenQS(String var){
            return " "+var+"=vertrag.Zahlungsart.Kaution OR "+var+"=vertrag.Zahlungsart.KautionZweitschluessel OR "
                    +var+"=vertrag.Zahlungsart.KautionZurueck OR "+var+"=vertrag.Zahlungsart.KautionZweitschluesselZurueck ";
        }

        public static String gebuehrenQS(String var){
            return mietenQS(var)+"OR "+var+"=vertrag.Zahlungsart.Ersatzschluessel OR "
                    +var+"=vertrag.Zahlungsart.Sonder OR "+var+"=vertrag.Zahlungsart.SonderZurueck ";
        }
        
        static {
            mieten = EnumSet.of(Miete, MieteZurueck, MieteZweitschluessel, MieteZweitschluesselZurueck, MieteHalbjahr, MieteZurueckHalbjahr,
                    MieteZweitschluesselHalbjahr, MieteZweitschluesselHalbjahrZurueck);
            kautionen = EnumSet.of(Kaution, KautionZweitschluessel, KautionZurueck, KautionZweitschluesselZurueck);
            gebuehren = EnumSet.of(Miete, MieteZurueck, MieteZweitschluessel, MieteZweitschluesselZurueck, 
                    MieteHalbjahr, MieteZurueckHalbjahr, MieteZweitschluesselHalbjahr, MieteZweitschluesselHalbjahrZurueck,
                    Ersatzschluessel, Sonder, SonderZurueck);
            sonderfall = EnumSet.of(Sonder, SonderZurueck);
            einzahlung = EnumSet.of(Miete, MieteZweitschluessel, MieteHalbjahr, MieteZweitschluesselHalbjahr, Kaution,
                    KautionZweitschluessel, Ersatzschluessel, Sonder);
            auszahlung = EnumSet.of(MieteZurueck, MieteZweitschluesselZurueck, MieteZurueckHalbjahr, MieteZweitschluesselHalbjahrZurueck,
                    KautionZurueck, KautionZweitschluesselZurueck, SonderZurueck);
        }
}
