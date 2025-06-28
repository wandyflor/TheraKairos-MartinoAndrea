package com.application.view.panels.consultation;

import com.application.model.dto.PatientDTO;
import java.awt.Font;

public class ConsultationProfileCell extends javax.swing.JPanel {

    public ConsultationProfileCell(PatientDTO patientDTO, Font font) {
        initComponents();
        jLabelCompleteNameAndAge.setText(
                patientDTO.getPatientDTOFormattedCompleteName() + " (" + patientDTO.getPatientDTOActualAge() + ")");
        jLabelOccupation.setText(patientDTO.getPatienDTOOccupation());
        if (patientDTO.getPatientDTOPhotoPath() != null) {
            jLabelPhoto.setIcon(patientDTO.getPatientDTOIcon());
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelCompleteNameAndAge = new javax.swing.JLabel();
        jLabelOccupation = new javax.swing.JLabel();
        jLabelPhoto = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(500, 100));

        jLabelCompleteNameAndAge.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabelCompleteNameAndAge.setText("Nombre Completo y edad");

        jLabelOccupation.setText("Ocupacion");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelOccupation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelCompleteNameAndAge, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 22, Short.MAX_VALUE)
                        .addComponent(jLabelCompleteNameAndAge)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelOccupation)
                        .addGap(0, 23, Short.MAX_VALUE))
                    .addComponent(jLabelPhoto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelCompleteNameAndAge;
    private javax.swing.JLabel jLabelOccupation;
    private javax.swing.JLabel jLabelPhoto;
    // End of variables declaration//GEN-END:variables
}
