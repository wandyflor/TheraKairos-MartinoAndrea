package com.application.view.panels.consultation;

import com.application.view.panels.consultation.dialog.ConsultationDialog;
import com.application.view.panels.consultation.calendar.ModelDate;
import com.application.controllers.panels.ConsultationsPanelController;
import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.interfaces.IConsultationActionsEvent;
import com.application.model.dto.CityDTO;
import com.application.model.dto.ConsultationDTO;
import com.application.model.dto.PatientDTO;
import com.application.model.enumerations.ViewType;
import com.application.view.panels.patient.PatientDialog;
import com.application.view.panels.renderers.ConsultationActionsCellRender;
import com.application.view.panels.renderers.ConsultationProfileCellRender;
import com.application.view.panels.renderers.ConsultationTimeCellRender;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import raven.modal.Toast;
import static raven.modal.Toast.Type.SUCCESS;
import com.application.interfaces.IPanelMessages;
import com.application.interfaces.IConsultationDialog;
import com.application.interfaces.IPatientDialog;
import com.application.model.dto.ConsultationPatientDTO;

public class ConsultationsPanel extends javax.swing.JPanel implements IPanelMessages, IConsultationDialog, IPatientDialog {

    private ConsultationsPanelController consultationsPanelController;
    
    ModelDate actualSelectedDate = null;
    
    public ConsultationsPanel() {
        initComponents();
        setStyle();
        
        this.actualSelectedDate = new ModelDate();
        
        calendar.addCalendarSelectedListener((MouseEvent evt, ModelDate date) -> {
            actualSelectedDate = date;
            jLabelSelectedDate.setText(String.valueOf(date.getDay()) + "/" + date.getMonth()+ "/" + date.getYear());
            loadTableData(date);    
        });
        
        initActionsData();
        
    }
    
    private void initActionsData() {
        IConsultationActionsEvent event = new IConsultationActionsEvent() {
            @Override
            public void onView(String consultationId) {
                callDialogToViewConsultation(consultationId);
            }
            @Override
            public void onEdit(String consultationId) {
                callDialogToUpdateConsultation(consultationId);
            }
            @Override
            public void onDelete(String consultationId) {
                callDialogToDeleteConsultation(consultationId);
            }  
        };
        
        jTableMain.getColumnModel().getColumn(0).setCellRenderer(new ConsultationTimeCellRender(jTableMain));
        jTableMain.getColumnModel().getColumn(1).setCellRenderer(new ConsultationProfileCellRender(jTableMain));
        jTableMain.getColumnModel().getColumn(2).setCellRenderer(new ConsultationActionsCellRender());
        jTableMain.getColumnModel().getColumn(2).setCellEditor(new ConsultationActionsCellEditor(event));
    }
            
    private void setStyle() {
        TableColumnModel columnModel = jTableMain.getColumnModel();
        
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
        
    public void setController(ConsultationsPanelController controller) {
        this.consultationsPanelController = controller;
        loadTableData(actualSelectedDate);
    }
    
    public void loadTableData(ModelDate date) {
        DefaultTableModel tableModel = (DefaultTableModel) jTableMain.getModel();

        if (jTableMain.isEditing()) {
            jTableMain.getCellEditor().stopCellEditing();
        }

        tableModel.setRowCount(0);

        List<ConsultationDTO> consultationsDTO = consultationsPanelController.getConsultationsByDate(date.toFormattedDate());

        if (consultationsDTO.isEmpty()) {
            return;
        }

        for (ConsultationDTO consultationDTO : consultationsDTO) {
            List<ConsultationPatientDTO> patientsDTO = consultationsPanelController.getPatientsByConsultationId(consultationDTO.getConsultationDTOId());

            if (patientsDTO.isEmpty()) continue;

            PatientDTO patient = consultationsPanelController.getPatientById(patientsDTO.get(0).getPatientId());

            tableModel.addRow(new Object[]{
                consultationDTO.getConsultationDTOStartTime(),
                patient, 
                consultationDTO.getConsultationDTOId()
            });
        }
    }
        
    public void callDialogToInsertConsultation() {
        try {
            Boolean inserted = ConsultationDialog.showDialog(this, ViewType.INSERT, "");
            initActionsData();
            loadTableData(actualSelectedDate);
            if(inserted) {
                Toast.show(this, SUCCESS, "Consulta agregada exitosamente");
            }
        } catch (Exception ex) {
            showErrorMessage("Error al agregar consulta: " + ex.getMessage());
        } 
    }
        
    public void callDialogToInsertPatient() {
        try {
            Boolean inserted = PatientDialog.showDialog(this, ViewType.INSERT, "");
            initActionsData();
            if(inserted) {
                Toast.show(this, SUCCESS, "Paciente agregado exitosamente");
            }
        } catch (Exception ex) {
            showErrorMessage("Error al agregar paciente: " + ex.getMessage());
        } 
    }
        
    public void callDialogToUpdateConsultation(String consultationId) {
        try {
            Boolean updated = ConsultationDialog.showDialog(this, ViewType.UPDATE, consultationId);
            initActionsData();
            loadTableData(actualSelectedDate);
            if(updated) {
                Toast.show(this, SUCCESS, "Consulta modificada exitosamente");
            }
        } catch (Exception ex) {
            showErrorMessage("Error al modificar consulta: " + ex.getMessage());
        }
    }
    
    public void callDialogToDeleteConsultation(String consultationId) {
        try {
            Boolean deleted = showConfirmAction("¿Está seguro de eliminar este paciente?");
            if (deleted) {
                consultationsPanelController.deleteConsultation(consultationId);
                Toast.show(this, Toast.Type.SUCCESS, "Consulta eliminada exitosamente");
            }   
            initActionsData();
            loadTableData(actualSelectedDate);
        } catch (Exception ex) {
            showErrorMessage("Error al eliminar consulta: " + ex.getMessage());
        } 
    }
     
    public void callDialogToViewConsultation(String consultationId) {
        try {
            ConsultationDialog.showDialog(this, ViewType.VIEW, consultationId);
            initActionsData();
            loadTableData(actualSelectedDate);
        } catch (Exception ex) {
            showErrorMessage("Error al visualizar la consulta: " + ex.getMessage());
        } 
    }
    
    @Override
    public ConsultationDTO getConsultationById(String consultationId) {
        return consultationsPanelController.getConsultationById(consultationId);
    }
        
    @Override
    public List<ConsultationPatientDTO> getConsultationPatientsByConsultationId(String consultationId) {
        return consultationsPanelController.getPatientsByConsultationId(consultationId);
    }
    
    @Override
    public void insertConsultationWithPatients(
            ConsultationDTO consultationDTO, 
            List<ConsultationPatientDTO> consultationPatientsDTO) throws ValidationException, BusinessException, IOException {
       consultationsPanelController.insertConsultationWithPatients(consultationDTO, consultationPatientsDTO);
    } 
    
    @Override
    public void updateConsultationWithPatients(
            ConsultationDTO consultationDTO, 
            List<ConsultationPatientDTO> consultationPatientsDTO) throws ValidationException, BusinessException, IOException {
       consultationsPanelController.updateConsultationWithPatients(consultationDTO, consultationPatientsDTO);
    }
    
    @Override
    public List<PatientDTO> getAllPatients() {
        return consultationsPanelController.getAllPatients();
    }
        
    @Override
    public PatientDTO getPatientById(String patientId) {
        return consultationsPanelController.getPatientById(patientId);
    }
    
    @Override
    public void openConsultationNotesById(String consultationId) {
        consultationsPanelController.openConsultationNotesById(consultationId);
    }
    
    @Override
    public List<CityDTO> getAllCities() {
        return consultationsPanelController.getAllCities();
    }

    @Override
    public void insertPatient(PatientDTO patientDTO) throws ValidationException, BusinessException, IOException {
        consultationsPanelController.insertPatient(patientDTO);
    }

    @Override
    public void updatePatient(PatientDTO patientDTO) throws ValidationException, BusinessException, IOException {
        consultationsPanelController.updatePatient(patientDTO);
    }

    @Override
    public void showInformationMessage(String message) {
        JOptionPane.showMessageDialog(
                this, 
                message, 
                "Informacion",
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
        jButtonAddConsultation = new javax.swing.JButton();
        calendar = new com.application.view.panels.consultation.calendar.Calendar();
        jButtonAddPatient = new javax.swing.JButton();
        jLabelSelectedDate = new javax.swing.JLabel();

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setFocusable(false);

        jTableMain.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Horario", "Paciente/s", "Acciones"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableMain);
        if (jTableMain.getColumnModel().getColumnCount() > 0) {
            jTableMain.getColumnModel().getColumn(0).setResizable(false);
            jTableMain.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableMain.getColumnModel().getColumn(1).setResizable(false);
            jTableMain.getColumnModel().getColumn(2).setResizable(false);
            jTableMain.getColumnModel().getColumn(2).setPreferredWidth(200);
        }

        jLabelMainTitle.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabelMainTitle.setText("Consultas");

        jButtonAddConsultation.setBackground(javax.swing.UIManager.getDefaults().getColor("Actions.Green"));
        jButtonAddConsultation.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButtonAddConsultation.setText("Agregar Consulta");
        jButtonAddConsultation.setToolTipText("Agregar paciente");
        jButtonAddConsultation.setMaximumSize(new java.awt.Dimension(380, 50));
        jButtonAddConsultation.setMinimumSize(new java.awt.Dimension(380, 50));
        jButtonAddConsultation.setPreferredSize(new java.awt.Dimension(380, 50));
        jButtonAddConsultation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddConsultationActionPerformed(evt);
            }
        });

        jButtonAddPatient.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButtonAddPatient.setLabel("Agregar paciente");
        jButtonAddPatient.setMaximumSize(new java.awt.Dimension(120, 28));
        jButtonAddPatient.setMinimumSize(new java.awt.Dimension(120, 28));
        jButtonAddPatient.setPreferredSize(new java.awt.Dimension(350, 35));
        jButtonAddPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddPatientActionPerformed(evt);
            }
        });

        jLabelSelectedDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSelectedDate.setText("Dia elegido");

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
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(calendar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonAddConsultation, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMainLayout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(jButtonAddPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabelSelectedDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 827, Short.MAX_VALUE)
                        .addGap(24, 24, 24))))
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMainLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabelMainTitle)
                .addGap(18, 18, 18)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addComponent(jLabelSelectedDate, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(calendar, javax.swing.GroupLayout.PREFERRED_SIZE, 381, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonAddConsultation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonAddPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE))
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

    private void jButtonAddConsultationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddConsultationActionPerformed
        callDialogToInsertConsultation();
    }//GEN-LAST:event_jButtonAddConsultationActionPerformed

    private void jButtonAddPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddPatientActionPerformed
        callDialogToInsertPatient();
    }//GEN-LAST:event_jButtonAddPatientActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.application.view.panels.consultation.calendar.Calendar calendar;
    private javax.swing.JButton jButtonAddConsultation;
    private javax.swing.JButton jButtonAddPatient;
    private javax.swing.JLabel jLabelMainTitle;
    private javax.swing.JLabel jLabelSelectedDate;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableMain;
    // End of variables declaration//GEN-END:variables
}
