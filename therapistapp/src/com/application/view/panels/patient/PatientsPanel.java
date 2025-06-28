package com.application.view.panels.patient;

import com.application.view.panels.renderers.PatientProfileCellRender;
import com.application.view.panels.renderers.PatientActionsCellRender;
import com.application.controllers.panels.PatientsPanelController;
import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.model.dto.CityDTO;
import com.application.model.dto.PatientDTO;
import com.application.model.enumerations.ViewType;
import com.formdev.flatlaf.FlatClientProperties;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import raven.modal.Toast;
import static raven.modal.Toast.Type.SUCCESS;
import com.application.interfaces.IPanelMessages;
import com.application.interfaces.IPatientDialog;

public class PatientsPanel extends javax.swing.JPanel implements IPanelMessages, IPatientDialog {

    private PatientsPanelController patientsPanelController;
    DefaultTableModel tableModel;
    
    public PatientsPanel() {
        initComponents();
        setStyle();
        initActionsData();
    }
    
    private void initActionsData() {
        IPatientActionsEvent event = new IPatientActionsEvent() {
            @Override
            public void onView(String patientId) {
                callDialogToViewPatient(patientId);
            }

            @Override
            public void onEdit(String patientId) {
                callDialogToUpdatePatient(patientId);
            }

            @Override
            public void onDelete(String patientId) {
                callDialogToDeletePatient(patientId);
            }
        };

        jTableMain.getColumnModel().getColumn(0).setCellRenderer(new PatientProfileCellRender(jTableMain));
        jTableMain.getColumnModel().getColumn(1).setCellRenderer(new PatientActionsCellRender());
        jTableMain.getColumnModel().getColumn(1).setCellEditor(new PatientActionsCellEditor(event));
    }
        
    private void setStyle() {
        TableColumnModel columnModel = jTableMain.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(600);

        jTableMain.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "height:30;"
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background;"
                + "font:bold;");

        jTableMain.putClientProperty(FlatClientProperties.STYLE, ""
                + "rowHeight:100;"
                + "showHorizontalLines:true;"
                + "intercellSpacing:0,1;"
                + "cellFocusColor:$TableHeader.hoverBackground;"
                + "selectionBackground:$TableHeader.hoverBackground;"
                + "selectionForeground:$Table.foreground;");

        jScrollPane1.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, ""
                + "trackArc:999;"
                + "trackInsets:3,3,3,3;"
                + "thumbInsets:3,3,3,3;"
                + "background:$Table.background;");
    }
        
    public void setController(PatientsPanelController controller) {
        this.patientsPanelController = controller;
        loadTableData();
    }

    public void loadTableData() {
        tableModel = (DefaultTableModel) jTableMain.getModel();
        if (jTableMain.isEditing()) jTableMain.getCellEditor().stopCellEditing();
        tableModel.setRowCount(0);

        try {
            List<PatientDTO> patientsDTO = patientsPanelController.getAllPatients();
            for (PatientDTO patientDTO : patientsDTO) {
                tableModel.addRow(new Object[]{patientDTO, patientDTO.getPatientDTOId()});
            }
        } catch (Exception ex) {
            showErrorMessage("Error al cargar los pacientes: " + ex.getMessage());
        }
    }

    private void searchData(String patientLastName) {
        tableModel = (DefaultTableModel) jTableMain.getModel();
        if (jTableMain.isEditing()) jTableMain.getCellEditor().stopCellEditing();
        tableModel.setRowCount(0);

        try {
            List<PatientDTO> patientsDTO = patientsPanelController.getPatientsThatMatch(patientLastName);
            for (PatientDTO patientDTO : patientsDTO) {
                tableModel.addRow(new Object[]{patientDTO, patientDTO.getPatientDTOId()});
            }
        } catch (Exception ex) {
            showErrorMessage("Error al buscar pacientes: " + ex.getMessage());
        }
    }
    
    public void callDialogToInsertPatient() {
        try {
            Boolean inserted = PatientDialog.showDialog(this, ViewType.INSERT, "");
            initActionsData();
            loadTableData();
            if(inserted) {
                Toast.show(this, SUCCESS, "Paciente agregado exitosamente");
            }
        } catch (Exception ex) {
            showErrorMessage("Error al agregar paciente: " + ex.getMessage());
        } 
    }
        
    public void callDialogToUpdatePatient(String patientId) {
        try {
            Boolean updated = PatientDialog.showDialog(this, ViewType.UPDATE, patientId);
            initActionsData();
            loadTableData();
            if(updated) {
                Toast.show(this, SUCCESS, "Paciente modificado exitosamente");
            }
        } catch (Exception ex) {
            showErrorMessage("Error al modificar paciente: " + ex.getMessage());
        }
    }
    
    public void callDialogToDeletePatient(String patientId) {
        try {
            Boolean deleted = showConfirmAction("¿Está seguro de eliminar este paciente?");
            if (deleted) {
                patientsPanelController.deletePatient(patientId);
                Toast.show(this, Toast.Type.SUCCESS, "Paciente eliminado exitosamente");
            }          
            initActionsData();
            loadTableData();
        } catch (Exception ex) {
            showErrorMessage("Error al eliminar paciente: " + ex.getMessage());
        } 
    }
    
    public void callDialogToViewPatient(String patientId) {
        try {
            PatientDialog.showDialog(this, ViewType.VIEW, patientId);
            initActionsData();
            loadTableData();
        } catch (Exception ex) {
            showErrorMessage("Error al visualizar paciente: " + ex.getMessage());
        } 
    }
    
    @Override
    public PatientDTO getPatientById(String patientId) {
        return patientsPanelController.getPatientById(patientId);
    }
    
    @Override
    public List<CityDTO> getAllCities() {
        return patientsPanelController.getAllCities();
    }
    
    @Override
    public void insertPatient(PatientDTO patientDTO) throws ValidationException, BusinessException, IOException {
        patientsPanelController.insertPatient(patientDTO);
    }
    
    @Override
    public void updatePatient(PatientDTO patientDTO) throws ValidationException, BusinessException, IOException {
        patientsPanelController.updatePatient(patientDTO);
    }
    
    @Override
    public void showInformationMessage(String message) {
        JOptionPane.showMessageDialog(
                this, 
                message, 
                "Información", 
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                this, 
                message, 
                "Error", 
                JOptionPane.ERROR_MESSAGE
        );
    }
    
    @Override
    public Boolean showConfirmAction(String message) {
        return JOptionPane.showConfirmDialog(
                this, 
                message, 
                "Confirmar acción", 
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;
    }
        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelMain = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableMain = new javax.swing.JTable();
        jLabelMainTitle = new javax.swing.JLabel();
        jTextFieldSearcher = new javax.swing.JTextField();
        jButtonAddPatient = new javax.swing.JButton();

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setFocusable(false);

        jTableMain.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null}
            },
            new String [] {
                "Paciente", "Acciones"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableMain);
        if (jTableMain.getColumnModel().getColumnCount() > 0) {
            jTableMain.getColumnModel().getColumn(0).setResizable(false);
            jTableMain.getColumnModel().getColumn(1).setResizable(false);
        }

        jLabelMainTitle.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabelMainTitle.setText("Pacientes");

        jTextFieldSearcher.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jTextFieldSearcher.setText("buscar...");
        jTextFieldSearcher.setToolTipText("buscar paciente");
        jTextFieldSearcher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldSearcherMouseClicked(evt);
            }
        });
        jTextFieldSearcher.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldSearcherKeyReleased(evt);
            }
        });

        jButtonAddPatient.setBackground(javax.swing.UIManager.getDefaults().getColor("Actions.Green"));
        jButtonAddPatient.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButtonAddPatient.setText("Agregar Paciente");
        jButtonAddPatient.setToolTipText("Agregar paciente");
        jButtonAddPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddPatientActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addComponent(jLabelMainTitle)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMainLayout.createSequentialGroup()
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1235, Short.MAX_VALUE)
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addComponent(jTextFieldSearcher, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonAddPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(24, 24, 24))))
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMainLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabelMainTitle)
                .addGap(18, 18, 18)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldSearcher, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAddPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldSearcherKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldSearcherKeyReleased
        this.searchData(jTextFieldSearcher.getText().trim());
    }//GEN-LAST:event_jTextFieldSearcherKeyReleased

    private void jButtonAddPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddPatientActionPerformed
        this.callDialogToInsertPatient();
    }//GEN-LAST:event_jButtonAddPatientActionPerformed

    private void jTextFieldSearcherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldSearcherMouseClicked
        jTextFieldSearcher.setText("");
    }//GEN-LAST:event_jTextFieldSearcherMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddPatient;
    private javax.swing.JLabel jLabelMainTitle;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableMain;
    private javax.swing.JTextField jTextFieldSearcher;
    // End of variables declaration//GEN-END:variables
}
