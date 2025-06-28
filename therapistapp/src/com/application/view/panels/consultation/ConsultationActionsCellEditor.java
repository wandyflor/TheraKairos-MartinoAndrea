package com.application.view.panels.consultation;

import com.application.interfaces.IConsultationActionsEvent;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

public class ConsultationActionsCellEditor extends DefaultCellEditor {

    private final IConsultationActionsEvent event;

    public ConsultationActionsCellEditor(IConsultationActionsEvent event) {
        super(new JCheckBox());
        this.event = event;
    }

    @Override
    public Component getTableCellEditorComponent(JTable jtable, Object object, boolean isSelected, int row, int column) {
        ConsultationActionsCell action = new ConsultationActionsCell();
        action.initEvent(event, String.valueOf(object));
        action.setBackground(jtable.getSelectionBackground());
        return action;
    }
}
