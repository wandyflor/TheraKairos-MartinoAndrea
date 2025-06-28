package com.application.view.panels.renderers;

import com.application.model.dto.PatientDTO;
import com.application.view.panels.consultation.ConsultationProfileCell;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ConsultationProfileCellRender implements TableCellRenderer {

    private final TableCellRenderer oldCellRenderer;

    public ConsultationProfileCellRender(JTable table) {
        oldCellRenderer = table.getDefaultRenderer(Object.class);
    }

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object object, boolean bln, boolean bln1, int i, int i1) {
        Component com = oldCellRenderer.getTableCellRendererComponent(jtable, object, bln, bln1, i, i1);
        ConsultationProfileCell cell = new ConsultationProfileCell((PatientDTO) object, com.getFont());
        cell.setBackground(com.getBackground());
        return cell;
    }
}
