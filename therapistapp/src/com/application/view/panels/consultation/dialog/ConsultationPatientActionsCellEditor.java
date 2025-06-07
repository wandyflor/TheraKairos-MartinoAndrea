package com.application.view.panels.consultation.dialog;

import com.application.interfaces.IConsultationPatientActionsEvent;
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
    public Component getTableCellEditorComponent(JTable jtable, Object object, boolean isSelected, int row, int column) {
        ConsultationPatientActionsCell action = new ConsultationPatientActionsCell();
        action.initEvent(event, String.valueOf(object));
        action.setBackground(jtable.getSelectionBackground());
        return action;
    }
}
