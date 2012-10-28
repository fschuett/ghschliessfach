/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package einstellungen;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author fschuett
 */
public class PrefObjs {

    private static int pieceLength = (int) (Preferences.MAX_VALUE_LENGTH * 0.75);

    static private byte[] object2Bytes(Object o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        return baos.toByteArray();
    }

    static private Object bytes2Object(byte raw[])
            throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(raw);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();
        return o;
    }

    static private byte[][] breakIntoPieces(byte raw[]) {
        int numPieces = (raw.length + pieceLength - 1) / pieceLength;
        byte pieces[][] = new byte[numPieces][];
        for (int i = 0; i < numPieces; ++i) {
            int startByte = i * pieceLength;
            int endByte = startByte + pieceLength;
            if (endByte > raw.length) {
                endByte = raw.length;
            }
            int length = endByte - startByte;
            pieces[i] = new byte[length];
            System.arraycopy(raw, startByte, pieces[i], 0, length);
        }
        return pieces;
    }

    static private byte[] combinePieces(byte pieces[][]) {
        int length = 0;
        for (int i = 0; i < pieces.length; ++i) {
            length += pieces[i].length;
        }
        byte raw[] = new byte[length];
        int cursor = 0;
        for (int i = 0; i < pieces.length; ++i) {
            System.arraycopy(pieces[i], 0, raw, cursor, pieces[i].length);
            cursor += pieces[i].length;
        }
        return raw;
    }

    static public void putObject(Preferences prefs, String key, Object o)
            throws IOException, BackingStoreException, ClassNotFoundException {
        byte raw[] = object2Bytes(o);
        byte pieces[][] = breakIntoPieces(raw);
        writePieces(prefs, key, pieces);
    }

    static public Object getObject(Preferences prefs, String key)
            throws IOException, BackingStoreException, ClassNotFoundException {
        byte pieces[][] = readPieces(prefs, key);
        if(pieces == null)
            return null;
        byte raw[] = combinePieces(pieces);
        Object o = bytes2Object(raw);
        return o;
    }

    static private void writePieces(Preferences prefs, String key, byte[][] pieces) {
        Preferences child = prefs.node(key);
        for (int i = 0; i < pieces.length; ++i) {
            child.putByteArray("" + i, pieces[i]);
        }
        child.putInt("count", pieces.length);
    }

    static private byte[][] readPieces(Preferences prefs, String key) throws BackingStoreException {
        Preferences child = prefs.node(key);
        int numPieces = child.getInt("count", -1);
        if(numPieces == -1)
            return null;
        byte[][] pieces = new byte[numPieces][];
        for (int i = 0; i < pieces.length; ++i) {
            pieces[i] = child.getByteArray("" + i, null);
        }
        return pieces;
    }
}
