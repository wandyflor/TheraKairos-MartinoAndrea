package com.application.view.panels.renderers;

import com.application.view.panels.patient.PatientActionsCell;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class PatientActionsCellRender extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object object, boolean isSeleted, boolean bln1, int row, int column) {
        Component com = super.getTableCellRendererComponent(jtable, object, isSeleted, bln1, row, column);
        PatientActionsCell action = new PatientActionsCell();
        action.setBackground(com.getBackground());
        return action;
    }
}
