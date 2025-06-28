package com.application.view.panels.consultation.dialog;

import com.application.interfaces.IConsultationPatientActionsEvent;
import com.application.model.dto.ConsultationPatientDTO;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

public class ConsultationPatientActionsCellEditor extends DefaultCellEditor {

    private final IConsultationPatientActionsEvent event;

    public ConsultationPatientActionsCellEditor(IConsultationPatientActionsEvent event) {
        super(new JCheckBox());
        this.event = event;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        ConsultationPatientDTO consultationPatient = (ConsultationPatientDTO) value;
        ConsultationPatientActionsCell cell = new ConsultationPatientActionsCell();
        cell.initEvent(event, consultationPatient.getPatientId());
        cell.setIsPaid(Boolean.parseBoolean(consultationPatient.getIsPaid()));
        return cell;
    }
}
