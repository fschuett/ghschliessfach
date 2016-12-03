/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package factories;

import java.awt.Color;
import java.awt.Component;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author fschuett
 */
public class DefaultFactory {

    @SuppressWarnings("serial")
	public static class DateRenderer extends DefaultTableCellRenderer {

        DateFormat formatter;

        public DateRenderer() {
            super();
        }

        @Override
        public void setValue(Object value) {
            if (formatter == null) {
                formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN);
            }
            setText((value == null) ? "" : formatter.format(value));
        }
    }

    public static class DateEditor extends DefaultCellEditor {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		DateFormat formatter;

        public DateEditor() {
            super(new JTextField());
            formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN);
        }
        @Override
        public boolean stopCellEditing() {
            String value = ((JTextField)getComponent()).getText();
            if(!value.equals("")) {
                try {
                    formatter.parse(value);
                } catch (ParseException e) {
                    ((JComponent)getComponent()).setBorder(new LineBorder(Color.red));
                    return false;
                }
            }
            return super.stopCellEditing();
        }
        @Override
        public Component getTableCellEditorComponent(final JTable table, final Object value,
        final boolean isSelected, final int row, final int column) {
            JTextField tf =((JTextField)getComponent());
            tf.setBorder(new LineBorder(Color.black));
            try {
                tf.setText(formatter.format(value));
            } catch (Exception e) {
                tf.setText("");
            }
            return tf;
        }
        @Override
        public Object getCellEditorValue() {
            try {
                Date value = formatter.parse(((JTextField)getComponent()).getText());
                return value;
            } catch (ParseException ex) {
                return null;
            }
        }
    }
}
