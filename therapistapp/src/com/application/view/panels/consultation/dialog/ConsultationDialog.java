package com.application.view.panels.consultation.dialog;

import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.interfaces.IConsultationPatientActionsEvent;
import com.application.interfaces.IPanelMessages;
import com.application.model.dto.ConsultationDTO;
import com.application.model.dto.PatientDTO;
import com.application.model.enumerations.ConsultationStatus;
import com.application.model.enumerations.ViewType;
import com.application.view.panels.renderers.ConsultationPatientActionsCellRender;
import com.application.view.panels.renderers.ConsultationPatientProfileCellRender;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import java.awt.Frame;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import raven.datetime.component.time.TimeEvent;
import raven.datetime.component.time.TimeSelectionListener;
import raven.modal.Toast;
import com.application.interfaces.IConsultationDialog;
import com.application.interfaces.IConsultationPatientsDialog;
import com.application.model.dto.ConsultationPatientDTO;
import com.application.view.panels.consultation.patient.dialog.ConsultationPatientsDialog;
import java.util.ArrayList;

public class ConsultationDialog extends javax.swing.JDialog implements IPanelMessages, IConsultationPatientsDialog {
    private final IConsultationDialog listener;
    private final ViewType viewType;
    private final String consultationId;
    private ConsultationDTO consultationDTO;
    private List<ConsultationPatientDTO> consultationPatientsDTO;
    
    private boolean operationSuccess = false;

    /**  
     * Constructor
     * @param owner
     * @param listener
     * @param viewType
     * @param consultationId
     */
    public ConsultationDialog(
            Frame owner, 
            IConsultationDialog listener,
            ViewType viewType, 
            String consultationId) {
        super(owner, "Thera Kairos", true);
        initComponents();
        setStyle();
        initActionsData();
        this.listener = listener;
        this.viewType = viewType;
        this.consultationId = consultationId;
        
        setComponents();
        
        consultationPatientsDTO = new ArrayList<>();
        
        if (viewType == ViewType.INSERT) {
            loadPatientDataForInsertView();
        }
        
        if (viewType == ViewType.UPDATE) {
            loadPatientDataForUpdateView();
        }
        
        if (viewType == ViewType.VIEW) {
            loadPatientDataForView();
        }
       
        setLocationRelativeTo(null);
        
    }
    
    /**  
     * Carga la infomacion del PatientDTO en el formulario
     */  
    private void loadPatientDataForInsertView() {
        
        jLabelMainTitle.setText("Agregar consulta");

        DefaultTableModel tableModel = (DefaultTableModel) jTablePatients.getModel();

        if (jTablePatients.isEditing()) {
            jTablePatients.getCellEditor().stopCellEditing();
        }

        tableModel.setRowCount(0);
        
        jButtonViewNotes.setVisible(false);
        
        jButtonAdd.setText("Agregar");
        
    }
    
    /**  
     * Carga la infomacion del PatientDTO en el formulario
     */  
    private void loadPatientDataForUpdateView() {
        
        jLabelMainTitle.setText("Modificar consulta");

        loadConsultationData();
        
        jButtonAdd.setText("Modificar");
        
    }
    
    /**  
     * Carga la infomacion del PatientDTO en el formulario
     */  
    private void loadPatientDataForView() {
        
        jLabelMainTitle.setText("Ver consulta");
                
        loadConsultationData();
        
        jFormattedTextFieldConsultationDate.setEnabled(false);
        datePickerConsultationDate.setEditorIcon(null);
        jFormattedTextFieldStartTime.setEnabled(false);
        timePickerStartTime.setEditorIcon(null);
        jFormattedTextFieldEndTime.setEnabled(false);
        timePickerEndTime.setEditorIcon(null);
        jTextFieldAmount.setEnabled(false);
        jButtonAddPatient.setEnabled(false);
        jButtonCancel.setVisible(false);
        jButtonAdd.setText("Volver");
        jTablePatients.setEnabled(false);
        
    }
    
    private void setComponents() {
        datePickerConsultationDate.setCloseAfterSelected(true);
        datePickerConsultationDate.setEditor(jFormattedTextFieldConsultationDate);
        
        timePickerStartTime.set24HourView(true);
        timePickerStartTime.setOrientation(SwingConstants.HORIZONTAL);
        timePickerStartTime.addTimeSelectionListener((TimeEvent te) -> {
            if (timePickerStartTime.isTimeSelected()) {
                DateTimeFormatter df = DateTimeFormatter.ofPattern("hh:mm a");
            }
        });
        timePickerStartTime.setEditor(jFormattedTextFieldStartTime);
        
        timePickerEndTime.set24HourView(true);
        timePickerEndTime.setOrientation(SwingConstants.HORIZONTAL);
        timePickerEndTime.addTimeSelectionListener((TimeEvent te) -> {
            if (timePickerEndTime.isTimeSelected()) {
                DateTimeFormatter df = DateTimeFormatter.ofPattern("hh:mm a");
            }
        });
        timePickerEndTime.setEditor(jFormattedTextFieldEndTime);
    }
    
    private void initActionsData() {
        IConsultationPatientActionsEvent event = new IConsultationPatientActionsEvent() {
            @Override
            public void onIsPaid(String patientId) {
                setConsultationPatientPaid(patientId);
            }
            @Override
            public void onDelete(String patientId) {
                callDialogToDeleteConsultationPatient(patientId);
            }  
        };
        
        jTablePatients.getColumnModel().getColumn(0).setCellRenderer(new ConsultationPatientProfileCellRender(jTablePatients));
        jTablePatients.getColumnModel().getColumn(1).setCellRenderer(new ConsultationPatientActionsCellRender());
        jTablePatients.getColumnModel().getColumn(1).setCellEditor(new ConsultationPatientActionsCellEditor(event));
    }
    
    private void setStyle() {
        TableColumnModel columnModel = jTablePatients.getColumnModel();
        
        jTablePatients.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "height:30;"
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background;"
                + "font:bold;");

        jTablePatients.putClientProperty(FlatClientProperties.STYLE, ""
                + "rowHeight:100;"
                + "showHorizontalLines:true;"
                + "intercellSpacing:0,1;"
                + "cellFocusColor:$TableHeader.hoverBackground;"
                + "selectionBackground:$TableHeader.hoverBackground;"
                + "selectionForeground:$Table.foreground;");

        jScrollPanePatiens.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, ""
                + "trackArc:999;"
                + "trackInsets:3,3,3,3;"
                + "thumbInsets:3,3,3,3;"
                + "background:$Table.background;");
        
    }
    
    /**  
     * Carga los datos cargados del objeto Consulta
     */
    private void loadConsultationData() {
        
        consultationDTO = listener.getConsultationById(consultationId);
        
        datePickerConsultationDate.setSelectedDate(LocalDate.parse(consultationDTO.getConsultationDTODate()));
        timePickerStartTime.setSelectedTime(LocalTime.parse(consultationDTO.getConsultationDTOStartTime()));
        timePickerEndTime.setSelectedTime(LocalTime.parse(consultationDTO.getConsultationDTOEndTime()));
        jTextFieldAmount.setText(consultationDTO.getConsultationDTOAmount());

        loadConsultationPatientsData();
        
    }
    
    /**  
     * Carga los datos cargados de los objetos Paciente asociados con el objeto Consulta
     */
    private void loadConsultationPatientsData() {
        DefaultTableModel tableModel = (DefaultTableModel) jTablePatients.getModel();

        if (jTablePatients.isEditing()) {
            jTablePatients.getCellEditor().stopCellEditing();
        }

        tableModel.setRowCount(0);

        if (consultationId == null) {
            return;
        }

        if(consultationPatientsDTO.isEmpty()) {
            consultationPatientsDTO = listener.getConsultationPatientsByConsultationId(consultationId);
        }        
        
        if (!consultationPatientsDTO.isEmpty()) {
            
            for (ConsultationPatientDTO consultationPatientDTO : consultationPatientsDTO) {
                
                PatientDTO patientDTO = listener.getPatientById(consultationPatientDTO.getPatientId());
                
                tableModel.addRow(new Object[]{
                    patientDTO, 
                    consultationPatientDTO
                });
                
            }
            
        }
    }
    
    /**  
     * Crea un objeto ConsultationDTO con los datos cargados en el formulario  
     * @return ConsultationDTO 
     */
    private ConsultationDTO getConsultationDTO() {       
        return new ConsultationDTO(
               (viewType == ViewType.UPDATE && consultationDTO != null) ? consultationDTO.getConsultationDTOId() : "",
                datePickerConsultationDate.isDateSelected() ? datePickerConsultationDate.getSelectedDate().toString() : null, 
                timePickerStartTime.isTimeSelected() ? timePickerStartTime.getSelectedTime().toString() : null, 
                timePickerEndTime.isTimeSelected() ? timePickerEndTime.getSelectedTime().toString() : null,
                jTextFieldAmount.getText().trim(),
                ConsultationStatus.SCHEDULED.toString()
        );
    }
        
    /**  
     * Elige la accion a realizar por el objeto jButtonAdd
     */
    private void saveAction() {
        try {
            
            if(!consultationPatientsDTO.isEmpty()) {
            
                if (viewType == ViewType.INSERT) {
                    listener.insertConsultationWithPatients(getConsultationDTO(), consultationPatientsDTO);
                } 

                if (viewType == ViewType.UPDATE) {
                    listener.updateConsultationWithPatients(getConsultationDTO(), consultationPatientsDTO);
                }

                operationSuccess = true;
                dispose(); 
            
            } else {
                showErrorMessage("La consulta debe tener como minimo un paciente asociado");
            }

        } catch (ValidationException | BusinessException e) {
            showErrorMessage(e.getMessage());
            operationSuccess = false;
        } catch (IOException e) {
            showErrorMessage("Error al manejar las notas de la consulta: " + e.getMessage());
            operationSuccess = false;
        }
    }

    /**  
     * Elige la accion a realizar por el objeto jButtonCancel
     */
    private void cancelAction() {
        operationSuccess = false;
        dispose();
    }
    
    private void viewConsultationNotes() {
        listener.openConsultationNotesById(consultationId);
    }
    
    /**  
     * Agrega pacientes a la consulta
     */
    private void callDialogToAddConsultationPatients() {
        try {
            Boolean aggregate = ConsultationPatientsDialog.showDialog(this);  
            if (aggregate) {
                Toast.show(this, Toast.Type.SUCCESS, "Pacientes agregados exitosamente");
            }
        } catch (Exception ex) {
            showErrorMessage("Error al agregar pacientes: " + ex.getMessage());
        } 
    }
    
    @Override
    public List<PatientDTO> getAllPatients() {
        return listener.getAllPatients();
    }
    
    @Override
    public void updateConsultationPatientsDTO(List<String> patientsId) {
        List<ConsultationPatientDTO> newConsultationPatientsDTO = createConsultationPatients(patientsId);
        
        consultationPatientsDTO.addAll(newConsultationPatientsDTO);
        
        initActionsData();
        loadConsultationPatientsData();
    }
    
    private List<ConsultationPatientDTO> createConsultationPatients(List<String> patientsId) {
        List<ConsultationPatientDTO> consultationPatientsDTO = new ArrayList<>();
        for(String patientId: patientsId) {
            ConsultationPatientDTO cpdto = new ConsultationPatientDTO(
                    consultationId,
                    patientId,
                    Boolean.FALSE.toString()
            );
            consultationPatientsDTO.add(cpdto);
        }
        return consultationPatientsDTO;
    }
    
    /**  
     * Cambia el estado del pago de la consulta (is_paid = true)
     * @param patientId Identifiador del paciente
     */
    private void setConsultationPatientPaid(String patientId) {
        ConsultationPatientDTO consultationPatientDTO = consultationPatientsDTO
            .stream()
            .filter(cpDTO -> patientId.equals(cpDTO.getPatientId()))
            .findFirst()
            .orElse(null);

        if (consultationPatientDTO != null) {
            consultationPatientDTO.setIsPaid(Boolean.TRUE.toString());
        } 
        
        initActionsData();
        loadConsultationPatientsData();
            
    }
    
    /**  
     * Elimina el paciente de la consulta
     * @param patientId Identifiador del paciente
     */
    private void callDialogToDeleteConsultationPatient(String patientId) {
        try {
            Boolean deleted = showConfirmAction("¿Está seguro de eliminar este paciente?");  
            if (deleted) {
                consultationPatientsDTO.removeIf(consultationPatientDTO -> consultationPatientDTO.getPatientId().equals(patientId));
                Toast.show(this, Toast.Type.SUCCESS, "Paciente eliminado exitosamente");
            }
            initActionsData();
            loadConsultationPatientsData();
        } catch (Exception ex) {
            showErrorMessage("Error al eliminar paciente: " + ex.getMessage());
        } 
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
     * Llama al diálogo y bloquea hasta que se cierre.  
     * @param listener
     * @param viewType
     * @param consultationId
     * @return true si el guardado fue exitoso, false si canceló o hubo error.  
     */
    public static boolean showDialog(IConsultationDialog listener, ViewType viewType, String consultationId) {
        Frame ownerFrame = JOptionPane.getFrameForComponent((Component) listener);
        ConsultationDialog dialog = new ConsultationDialog(ownerFrame, listener, viewType, consultationId);
        dialog.setVisible(true);  
        return dialog.operationSuccess;
    }
        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        datePickerConsultationDate = new raven.datetime.component.date.DatePicker();
        timePickerStartTime = new raven.datetime.component.time.TimePicker();
        timePickerEndTime = new raven.datetime.component.time.TimePicker();
        jPanelMainForm = new javax.swing.JPanel();
        jPanelMainTitle = new javax.swing.JPanel();
        jLabelMainTitle = new javax.swing.JLabel();
        jPanelActions = new javax.swing.JPanel();
        jButtonCancel = new javax.swing.JButton();
        jButtonAdd = new javax.swing.JButton();
        jLabelPatients = new javax.swing.JLabel();
        jScrollPanePatiens = new javax.swing.JScrollPane();
        jTablePatients = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jFormattedTextFieldConsultationDate = new javax.swing.JFormattedTextField();
        jTextFieldAmount = new javax.swing.JTextField();
        jLabelConsultationDate = new javax.swing.JLabel();
        jLabelEndTime = new javax.swing.JLabel();
        jLabelStartTime = new javax.swing.JLabel();
        jLabelAmount = new javax.swing.JLabel();
        jFormattedTextFieldStartTime = new javax.swing.JFormattedTextField();
        jFormattedTextFieldEndTime = new javax.swing.JFormattedTextField();
        jButtonAddPatient = new javax.swing.JButton();
        jButtonViewNotes = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(512, 568));
        setResizable(false);

        jLabelMainTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabelMainTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelMainTitle.setText("Agregar Consulta");
        jLabelMainTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanelMainTitleLayout = new javax.swing.GroupLayout(jPanelMainTitle);
        jPanelMainTitle.setLayout(jPanelMainTitleLayout);
        jPanelMainTitleLayout.setHorizontalGroup(
            jPanelMainTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainTitleLayout.createSequentialGroup()
                .addComponent(jLabelMainTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 639, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelMainTitleLayout.setVerticalGroup(
            jPanelMainTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainTitleLayout.createSequentialGroup()
                .addComponent(jLabelMainTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 9, Short.MAX_VALUE))
        );

        jPanelActions.setMinimumSize(new java.awt.Dimension(195, 100));
        jPanelActions.setName(""); // NOI18N
        jPanelActions.setPreferredSize(new java.awt.Dimension(270, 100));

        jButtonCancel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButtonCancel.setText("Cancelar");
        jButtonCancel.setAlignmentX(0.5F);
        jButtonCancel.setPreferredSize(new java.awt.Dimension(90, 30));
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jButtonAdd.setBackground(javax.swing.UIManager.getDefaults().getColor("Actions.Green"));
        jButtonAdd.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButtonAdd.setText("Agregar");
        jButtonAdd.setPreferredSize(new java.awt.Dimension(90, 30));
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelActionsLayout = new javax.swing.GroupLayout(jPanelActions);
        jPanelActions.setLayout(jPanelActionsLayout);
        jPanelActionsLayout.setHorizontalGroup(
            jPanelActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelActionsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );
        jPanelActionsLayout.setVerticalGroup(
            jPanelActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelActionsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanelActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabelPatients.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabelPatients.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPatients.setText("Paciente/s:");
        jLabelPatients.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jTablePatients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
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
        jScrollPanePatiens.setViewportView(jTablePatients);
        if (jTablePatients.getColumnModel().getColumnCount() > 0) {
            jTablePatients.getColumnModel().getColumn(0).setResizable(false);
            jTablePatients.getColumnModel().getColumn(1).setResizable(false);
        }

        jFormattedTextFieldConsultationDate.setPreferredSize(new java.awt.Dimension(300, 30));

        jTextFieldAmount.setPreferredSize(new java.awt.Dimension(300, 30));

        jLabelConsultationDate.setText("Fecha de consulta:");

        jLabelEndTime.setText("Horario de fin:");

        jLabelStartTime.setText("Horario de inicio:");

        jLabelAmount.setText("Costo:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelConsultationDate)
                            .addComponent(jLabelStartTime))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jFormattedTextFieldConsultationDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jFormattedTextFieldStartTime)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelAmount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelEndTime)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFormattedTextFieldEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(130, 130, 130))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelConsultationDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jFormattedTextFieldConsultationDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelStartTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jFormattedTextFieldStartTime, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelEndTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jFormattedTextFieldEndTime, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldAmount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelAmount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jButtonAddPatient.setBackground(javax.swing.UIManager.getDefaults().getColor("Actions.Green"));
        jButtonAddPatient.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonAddPatient.setText("Agregar Paciente");
        jButtonAddPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddPatientActionPerformed(evt);
            }
        });

        jButtonViewNotes.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonViewNotes.setText("Ver notas");
        jButtonViewNotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonViewNotesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMainFormLayout = new javax.swing.GroupLayout(jPanelMainForm);
        jPanelMainForm.setLayout(jPanelMainFormLayout);
        jPanelMainFormLayout.setHorizontalGroup(
            jPanelMainFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMainTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelMainFormLayout.createSequentialGroup()
                .addContainerGap(40, Short.MAX_VALUE)
                .addGroup(jPanelMainFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanelMainFormLayout.createSequentialGroup()
                        .addComponent(jLabelPatients)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonViewNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAddPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPanePatiens, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(40, Short.MAX_VALUE))
            .addComponent(jPanelActions, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
        );
        jPanelMainFormLayout.setVerticalGroup(
            jPanelMainFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainFormLayout.createSequentialGroup()
                .addComponent(jPanelMainTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAddPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPatients, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonViewNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jScrollPanePatiens, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelActions, javax.swing.GroupLayout.PREFERRED_SIZE, 58, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMainForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMainForm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        saveAction();
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        cancelAction();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonAddPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddPatientActionPerformed
        callDialogToAddConsultationPatients();
    }//GEN-LAST:event_jButtonAddPatientActionPerformed

    private void jButtonViewNotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonViewNotesActionPerformed
        viewConsultationNotes();
    }//GEN-LAST:event_jButtonViewNotesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private raven.datetime.component.date.DatePicker datePickerConsultationDate;
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonAddPatient;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonViewNotes;
    private javax.swing.JFormattedTextField jFormattedTextFieldConsultationDate;
    private javax.swing.JFormattedTextField jFormattedTextFieldEndTime;
    private javax.swing.JFormattedTextField jFormattedTextFieldStartTime;
    private javax.swing.JLabel jLabelAmount;
    private javax.swing.JLabel jLabelConsultationDate;
    private javax.swing.JLabel jLabelEndTime;
    private javax.swing.JLabel jLabelMainTitle;
    private javax.swing.JLabel jLabelPatients;
    private javax.swing.JLabel jLabelStartTime;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelActions;
    private javax.swing.JPanel jPanelMainForm;
    private javax.swing.JPanel jPanelMainTitle;
    private javax.swing.JScrollPane jScrollPanePatiens;
    private javax.swing.JTable jTablePatients;
    private javax.swing.JTextField jTextFieldAmount;
    private raven.datetime.component.time.TimePicker timePickerEndTime;
    private raven.datetime.component.time.TimePicker timePickerStartTime;
    // End of variables declaration//GEN-END:variables
}
