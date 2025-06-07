package com.application.view.panels.patient;

import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.interfaces.IPanelMessages;
import com.application.interfaces.IPatientDialogListener;
import com.application.model.dto.CityDTO;
import com.application.model.dto.PatientDTO;
import com.application.model.enumerations.ViewType;
import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PatientDialog extends javax.swing.JDialog implements IPanelMessages {
    private final IPatientDialogListener listener;
    private final ViewType viewType;
    private final String patientId;
    private PatientDTO patientDTO;   
    private String patientPhotoPath = "";
        
    private boolean operationSuccess = false;
    
    /**  
     * Constructor
     * @param owner
     * @param listenter
     * @param viewtype
     * @param patientId
     */
    public PatientDialog(
            Frame owner, 
            IPatientDialogListener listenter, 
            ViewType viewtype, 
            String patientId) {
        super(owner, "Thera Kairos", true);
        initComponents();
        this.listener = listenter;
        this.viewType = viewtype;
        this.patientId = patientId;
        
        datePicker.setCloseAfterSelected(true);
        datePicker.setEditor(jFormattedTextFieldBirthDate);
        
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
        
        jLabelMainTitle.setText("Agregar paciente");
        
        loadComboBoxCitiesData();
        
        jLabelPhotoName.setText("Sin imagen seleccionada");
        jButtonRemovePhoto.setEnabled(false);
        
        jButtonAdd.setText("Agregar");
        
    }
    
    /**  
     * Carga la infomacion del PatientDTO en el formulario
     */  
    private void loadPatientDataForUpdateView() {
        
        jLabelMainTitle.setText("Modificar paciente");
        
        loadPatientData();
        
        jButtonAdd.setText("Modificar");
        
    }
    
    /**  
     * Carga la infomacion del PatientDTO en el formulario
     */  
    private void loadPatientDataForView() {
        
        jLabelMainTitle.setText("Ver paciente");
        
        loadPatientData();
        
        jTextFieldDNI.setEnabled(false);
        jTextFieldName.setEnabled(false);
        jTextFieldLastName.setEnabled(false);
        jFormattedTextFieldBirthDate.setEnabled(false);
        jTextFieldOccupation.setEnabled(false);
        jTextFieldPhone.setEnabled(false);
        jTextFieldEmail.setEnabled(false);
        jComboBoxCities.setEnabled(false);
        jTextFieldAddress.setEnabled(false);
        jTextFieldAddressNumber.setEnabled(false);
        jTextFieldAddressFloor.setEnabled(false); 
        jTextFieldAddressDepartment.setEnabled(false);
        
        jButtonAddPhoto.setVisible(false);
        jButtonRemovePhoto.setVisible(false);
   
        jButtonCancel.setVisible(false);
        jButtonAdd.setText("Volver");
        
    }
         
    /**  
     * Carga lops datos cargados del objeto Paciente
     */
    private void loadPatientData() {
        
        patientDTO = listener.getPatientById(patientId);
        
        jTextFieldDNI.setText(patientDTO.getPatientDTODNI());
        jTextFieldName.setText(patientDTO.getPatientDTOName());
        jTextFieldLastName.setText(patientDTO.getPatientDTOLastName());
        datePicker.setSelectedDate(LocalDate.parse(patientDTO.getPatientDTOBirthDate()));
        jTextFieldOccupation.setText(patientDTO.getPatienDTOOccupation());
        jTextFieldPhone.setText(patientDTO.getPatientDTOPhone());
        jTextFieldEmail.setText(patientDTO.getPatientDTOEmail());
        loadComboBoxCitiesData();
        jTextFieldAddress.setText(patientDTO.getPatientDTOAddress());
        jTextFieldAddressNumber.setText(patientDTO.getPatientDTOAddressNumber());
        jTextFieldAddressFloor.setText(patientDTO.getPatientDTOAddressFloor());
        jTextFieldAddressDepartment.setText(patientDTO.getPatientDTOAddressDepartment());

        if (patientDTO.getPatientDTOPhotoPath() != null && !patientDTO.getPatientDTOPhotoPath().isEmpty()) {
            patientPhotoPath = patientDTO.getPatientDTOPhotoPath();
            jLabelPhotoName.setText(new File(patientDTO.getPatientDTOPhotoPath()).getName());
            jButtonRemovePhoto.setEnabled(true);
        } else {
            jLabelPhotoName.setText("Sin imagen seleccionada");
            jButtonRemovePhoto.setEnabled(false);
        }
        
    }
        
    /**  
     * Carga el objeto jComboBoxCities
     */
    private void loadComboBoxCitiesData() {
        jComboBoxCities.removeAllItems();
        jComboBoxCities.addItem(new CityDTO("", "Seleccione...", ""));

        for (CityDTO city : listener.getAllCities()) {
            jComboBoxCities.addItem(city);
            if ((viewType == ViewType.UPDATE || viewType ==  ViewType.VIEW) && city.getCityId().equals(patientDTO.getCityId())) {
                jComboBoxCities.setSelectedItem(city);
            }
        }
    }
        
    /**  
     * Agrega una foto asociada al paciente  
     */
    private void addPhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar foto del paciente");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Imágenes (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            patientPhotoPath = file.getAbsolutePath().trim();
            jLabelPhotoName.setText(file.getName());
            jButtonAddPhoto.setEnabled(false);
            jButtonRemovePhoto.setEnabled(true);
        }
    }
    
    /**  
     * Elimina la foto cargada  
     */
    private void deletePhoto() {
        patientPhotoPath = "";
        jLabelPhotoName.setText("Sin imagen seleccionada");
        jButtonAddPhoto.setEnabled(true);
        jButtonRemovePhoto.setEnabled(false);
    }
    
    /**  
     * Crea un PatientDTO con los datos cargados en el formulario  
     * @return PatientDTO 
     */
    private PatientDTO getPatientDTO() {
        CityDTO selectedCity = (CityDTO) jComboBoxCities.getSelectedItem();

        return new PatientDTO(
            (viewType == ViewType.UPDATE && patientDTO != null) ? patientDTO.getPatientDTOId() : "",
            jTextFieldDNI.getText().trim(),
            jTextFieldName.getText().trim(),
            jTextFieldLastName.getText().trim(),
            datePicker.isDateSelected() ? Date.valueOf(datePicker.getSelectedDate()).toString() : null,
            jTextFieldOccupation.getText().trim(),
            jTextFieldPhone.getText().trim(),
            jTextFieldEmail.getText().trim(),
            selectedCity != null ? selectedCity.getCityId() : null,
            jTextFieldAddress.getText().trim(),
            jTextFieldAddressNumber.getText().trim(),
            jTextFieldAddressFloor.getText().trim(),
            jTextFieldAddressDepartment.getText().trim(),
            patientPhotoPath
        );
    }
    
    /**  
     * Elige la accion a realizar por el objeto jButtonAdd
     */
    private void saveAction() {
        try {
            
            if (viewType == ViewType.INSERT) {
                listener.insertPatient(getPatientDTO());
            } 
            
            if (viewType == ViewType.UPDATE) {
                listener.updatePatient(getPatientDTO());
            }

            operationSuccess = true;
            dispose(); 

        } catch (ValidationException | BusinessException e) {
            showErrorMessage(e.getMessage());
            operationSuccess = false;
        } catch (IOException e) {
            showErrorMessage("Error al manejar la foto del paciente: " + e.getMessage());
            operationSuccess = false;
        }
    }
    
    /**  
     * Ejecuta la accion de jButtonCancel
     */
    private void cancelAction() {
        operationSuccess = false;
        dispose();
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
     * @param patientId
     * @return true si el guardado fue exitoso, false si canceló o hubo error.  
     */
    public static Boolean showDialog(
            IPatientDialogListener listener, 
            ViewType viewType, 
            String patientId) {
        Frame ownerFrame = JOptionPane.getFrameForComponent((Component) listener);
        PatientDialog dialog = new PatientDialog(ownerFrame, listener, viewType, patientId);
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

        datePicker = new raven.datetime.component.date.DatePicker();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabelMainTitle = new javax.swing.JLabel();
        jTextFieldDNI = new javax.swing.JTextField();
        jTextFieldName = new javax.swing.JTextField();
        jTextFieldLastName = new javax.swing.JTextField();
        jFormattedTextFieldBirthDate = new javax.swing.JFormattedTextField();
        jTextFieldOccupation = new javax.swing.JTextField();
        jTextFieldPhone = new javax.swing.JTextField();
        jTextFieldEmail = new javax.swing.JTextField();
        jComboBoxCities = new javax.swing.JComboBox<>();
        jTextFieldAddress = new javax.swing.JTextField();
        jTextFieldAddressNumber = new javax.swing.JTextField();
        jLabelAddressFloor = new javax.swing.JLabel();
        jTextFieldAddressFloor = new javax.swing.JTextField();
        jLabelAddressDepartment = new javax.swing.JLabel();
        jTextFieldAddressDepartment = new javax.swing.JTextField();
        jPanelPhoto = new javax.swing.JPanel();
        jButtonAddPhoto = new javax.swing.JButton();
        jButtonRemovePhoto = new javax.swing.JButton();
        jLabelPhotoName = new javax.swing.JLabel();
        jLabelDNI = new javax.swing.JLabel();
        jLabelName = new javax.swing.JLabel();
        jLabelLastName = new javax.swing.JLabel();
        jLabelBirthDate = new javax.swing.JLabel();
        jLabelOccupation = new javax.swing.JLabel();
        jLabelPhone = new javax.swing.JLabel();
        jLabelEmail = new javax.swing.JLabel();
        jLabelCityId = new javax.swing.JLabel();
        jLabelAddress = new javax.swing.JLabel();
        jLabelAddressNumber = new javax.swing.JLabel();
        jPanelActions = new javax.swing.JPanel();
        jButtonCancel = new javax.swing.JButton();
        jButtonAdd = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(512, 568));
        setResizable(false);

        jLabelMainTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabelMainTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelMainTitle.setText("Agregar Paciente");
        jLabelMainTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelMainTitle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelMainTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
        );

        jTextFieldDNI.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N

        jTextFieldName.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N

        jTextFieldLastName.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N

        jFormattedTextFieldBirthDate.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N

        jTextFieldOccupation.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N

        jTextFieldPhone.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N

        jTextFieldEmail.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N

        jComboBoxCities.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N

        jTextFieldAddress.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N

        jTextFieldAddressNumber.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N

        jLabelAddressFloor.setText("Piso:");

        jTextFieldAddressFloor.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N

        jLabelAddressDepartment.setText("Dept:");

        jTextFieldAddressDepartment.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N

        jPanelPhoto.setMinimumSize(new java.awt.Dimension(244, 67));

        jButtonAddPhoto.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButtonAddPhoto.setText("Agregar");
        jButtonAddPhoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddPhotoActionPerformed(evt);
            }
        });

        jButtonRemovePhoto.setBackground(javax.swing.UIManager.getDefaults().getColor("Actions.Red"));
        jButtonRemovePhoto.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButtonRemovePhoto.setText("Quitar");
        jButtonRemovePhoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemovePhotoActionPerformed(evt);
            }
        });

        jLabelPhotoName.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabelPhotoName.setText("Sin imagen seleccionada");

        javax.swing.GroupLayout jPanelPhotoLayout = new javax.swing.GroupLayout(jPanelPhoto);
        jPanelPhoto.setLayout(jPanelPhotoLayout);
        jPanelPhotoLayout.setHorizontalGroup(
            jPanelPhotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPhotoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPhotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelPhotoName)
                    .addGroup(jPanelPhotoLayout.createSequentialGroup()
                        .addComponent(jButtonAddPhoto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonRemovePhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelPhotoLayout.setVerticalGroup(
            jPanelPhotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPhotoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPhotoName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addGroup(jPanelPhotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRemovePhoto)
                    .addComponent(jButtonAddPhoto))
                .addContainerGap())
        );

        jLabelDNI.setText("Documento*:");

        jLabelName.setText("Nombre*:");

        jLabelLastName.setText("Apellido*:");

        jLabelBirthDate.setText("Fecha de nacimiento*:");

        jLabelOccupation.setText("Ocupacion*:");

        jLabelPhone.setText("Celular*:");

        jLabelEmail.setText("Email*:");

        jLabelCityId.setText("Ciudad*:");

        jLabelAddress.setText("Direccion*:");

        jLabelAddressNumber.setText("Numero*:");

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
                .addGap(59, 59, 59))
        );
        jPanelActionsLayout.setVerticalGroup(
            jPanelActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelActionsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanelActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(76, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelPhone)
                    .addComponent(jLabelEmail)
                    .addComponent(jLabelCityId)
                    .addComponent(jLabelAddress)
                    .addComponent(jLabelAddressNumber)
                    .addComponent(jLabelName)
                    .addComponent(jLabelDNI)
                    .addComponent(jLabelLastName)
                    .addComponent(jLabelBirthDate)
                    .addComponent(jLabelOccupation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextFieldAddressNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelAddressFloor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldAddressFloor, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelAddressDepartment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldAddressDepartment))
                    .addComponent(jTextFieldPhone)
                    .addComponent(jTextFieldAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addComponent(jTextFieldName, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldDNI, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldLastName)
                    .addComponent(jFormattedTextFieldBirthDate)
                    .addComponent(jTextFieldOccupation)
                    .addComponent(jTextFieldEmail)
                    .addComponent(jComboBoxCities, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelPhoto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(150, 150, 150))
            .addComponent(jPanelActions, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldDNI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDNI))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelLastName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextFieldBirthDate, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelBirthDate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldOccupation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelOccupation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPhone))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelEmail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxCities, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelCityId))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelAddress))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldAddressDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelAddressDepartment)
                        .addComponent(jTextFieldAddressFloor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelAddressFloor))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldAddressNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelAddressNumber)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelPhoto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelActions, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAddPhotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddPhotoActionPerformed
        addPhoto();
    }//GEN-LAST:event_jButtonAddPhotoActionPerformed

    private void jButtonRemovePhotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemovePhotoActionPerformed
        deletePhoto();
    }//GEN-LAST:event_jButtonRemovePhotoActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        cancelAction();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        saveAction();
    }//GEN-LAST:event_jButtonAddActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private raven.datetime.component.date.DatePicker datePicker;
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonAddPhoto;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonRemovePhoto;
    private javax.swing.JComboBox<CityDTO> jComboBoxCities;
    private javax.swing.JFormattedTextField jFormattedTextFieldBirthDate;
    private javax.swing.JLabel jLabelAddress;
    private javax.swing.JLabel jLabelAddressDepartment;
    private javax.swing.JLabel jLabelAddressFloor;
    private javax.swing.JLabel jLabelAddressNumber;
    private javax.swing.JLabel jLabelBirthDate;
    private javax.swing.JLabel jLabelCityId;
    private javax.swing.JLabel jLabelDNI;
    private javax.swing.JLabel jLabelEmail;
    private javax.swing.JLabel jLabelLastName;
    private javax.swing.JLabel jLabelMainTitle;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JLabel jLabelOccupation;
    private javax.swing.JLabel jLabelPhone;
    private javax.swing.JLabel jLabelPhotoName;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelActions;
    private javax.swing.JPanel jPanelPhoto;
    private javax.swing.JTextField jTextFieldAddress;
    private javax.swing.JTextField jTextFieldAddressDepartment;
    private javax.swing.JTextField jTextFieldAddressFloor;
    private javax.swing.JTextField jTextFieldAddressNumber;
    private javax.swing.JTextField jTextFieldDNI;
    private javax.swing.JTextField jTextFieldEmail;
    private javax.swing.JTextField jTextFieldLastName;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldOccupation;
    private javax.swing.JTextField jTextFieldPhone;
    // End of variables declaration//GEN-END:variables
}
