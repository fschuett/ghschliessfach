/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drucken;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

/**
 *
 * @author fschuett
 */
public class DruckerEinstellung {

    private PrintService druckDienst;
    private PrintRequestAttributeSet druckAttribut;

    public DruckerEinstellung(PrintService druckDienst, PrintRequestAttributeSet druckAttribut) {
        this.druckDienst = druckDienst;
        this.druckAttribut = druckAttribut == null ? new HashPrintRequestAttributeSet() : druckAttribut;
    }

    public boolean zeigeDialog() {
        //FIXME: Der Druckdialog zeigt als Schaltfläche "Drucken".
        PrintService[] druckDienste = PrintServiceLookup.lookupPrintServices(null, null);
        druckDienst = ServiceUI.printDialog(null, 200, 200, druckDienste, getDruckDienst(), null, druckAttribut);
        return druckDienst != null;
    }

    /**
     * @return the druckDienst
     */
    public PrintService getDruckDienst() {
        return druckDienst;
    }

    /**
     * @param druckDienst the druckDienst to set
     */
    public void setDruckDienst(PrintService druckDienst) {
        this.druckDienst = druckDienst;
    }

    /**
     * @return the druckAttribut
     */
    public PrintRequestAttributeSet getDruckAttribut() {
        return druckAttribut;
    }

    /**
     * @param druckAttribut the druckAttribut to set
     */
    public void setDruckAttribut(PrintRequestAttributeSet druckAttribut) {
        this.druckAttribut = druckAttribut;
    }
}
