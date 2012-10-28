/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package schliessfach;

/**
 *
 * @author fschuett
 */
public enum Position {
    oben, mitteOben, mitteUnten, unten;

    public static Position getStandardPosition(int i){
        switch(i%4){
            case 1:
                return oben;
            case 2:
                return mitteOben;
            case 3:
                return mitteUnten;
            default:
                return unten;
        }
    }
}
