package com.application.view.panels.renderers;

import com.application.model.dto.ConsultationPatientDTO;
import com.application.view.panels.consultation.dialog.ConsultationPatientActionsCell;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ConsultationPatientActionsCellRender extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object object, boolean isSelected, boolean hasFocus, int row, int column) {
        ConsultationPatientDTO consultationPatient = (ConsultationPatientDTO) object;
        ConsultationPatientActionsCell action = new ConsultationPatientActionsCell();
        action.setIsPaid(Boolean.parseBoolean(consultationPatient.getIsPaid()));
        action.setBackground(jtable.getBackground());
        return action;
    }
}
