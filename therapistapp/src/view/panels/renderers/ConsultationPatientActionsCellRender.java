package com.application.view.panels.renderers;

import com.application.view.panels.consultation.dialog.ConsultationPatientActionsCell;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ConsultationPatientActionsCellRender extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object object, boolean isSeleted, boolean bln1, int row, int column) {
        Component com = super.getTableCellRendererComponent(jtable, object, isSeleted, bln1, row, column);
        ConsultationPatientActionsCell action = new ConsultationPatientActionsCell();
        action.setBackground(com.getBackground());
        return action;
    }
}
