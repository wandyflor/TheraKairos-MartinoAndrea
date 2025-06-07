package com.application.view.panels.patient;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

public class PatientActionsCellEditor extends DefaultCellEditor {

    private final IPatientActionsEvent event;

    public PatientActionsCellEditor(IPatientActionsEvent event) {
        super(new JCheckBox());
        this.event = event;
    }

    @Override
    public Component getTableCellEditorComponent(JTable jtable, Object object, boolean isSelected, int row, int column) {
        PatientActionsCell action = new PatientActionsCell();
        action.initEvent(event, String.valueOf(object));
        action.setBackground(jtable.getSelectionBackground());
        return action;
    }
}
