package com.application.view.panels.renderers;

import com.application.model.dto.PatientDTO;
import com.application.view.panels.consultation.dialog.ConsultationPatientProfileCell;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ConsultationPatientProfileCellRender implements TableCellRenderer {

    private final TableCellRenderer oldCellRenderer;

    public ConsultationPatientProfileCellRender(JTable table) {
        oldCellRenderer = table.getDefaultRenderer(Object.class);
    }

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object object, boolean bln, boolean bln1, int i, int i1) {
        Component com = oldCellRenderer.getTableCellRendererComponent(jtable, object, bln, bln1, i, i1);
        ConsultationPatientProfileCell cell = new ConsultationPatientProfileCell((PatientDTO) object, com.getFont());
        cell.setBackground(com.getBackground());
        return cell;
    }
}
