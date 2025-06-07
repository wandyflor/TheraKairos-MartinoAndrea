package com.application.utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Gestiona la creación y movimiento de archivos relacionados a pacientes.
 * Por defecto en Windows usa "%USERPROFILE%\Documents\therapistapp".
 */
public class PatientsFilesManager {
    private final Path baseDir;

    /**
     * Crea un FileManager con directorio base por defecto:
     * %USERPROFILE%\Documents\therapistapp o, si no existe,
     * System.getProperty("user.home")\Documents\therapistapp.
     */
    public PatientsFilesManager() {
        String userProfile = System.getenv("USERPROFILE");
        if (userProfile == null || userProfile.isEmpty()) {
            userProfile = System.getProperty("user.home");
        }
        this.baseDir = Paths.get(userProfile, "Documents", "therapistapp", "patients");
    }

    /**
     * Crea un FileManager con directorio base personalizado.
     * @param baseDirPath ruta al directorio base
     */
    public PatientsFilesManager(String baseDirPath) {
        if (baseDirPath == null || baseDirPath.isEmpty()) {
            String userProfile = System.getenv("USERPROFILE");
            if (userProfile == null || userProfile.isEmpty()) {
                userProfile = System.getProperty("user.home");
            }
            this.baseDir = Paths.get(userProfile, "Documents", "therapistapp");
        } else {
            this.baseDir = Paths.get(baseDirPath);
        }
    }

    /**
     * @return el directorio base donde se almacenan los archivos
     */
    public Path getBaseDir() {
        return baseDir;
    }

    /**
     * Inicializa las carpetas base para un paciente:
     * /photo, /notes, /notes/single, /notes/group.
     * @param patientId
     * @throws java.io.IOException
     */
    public void initPatientFolders(UUID patientId) throws IOException {
        Path root = baseDir.resolve(patientId.toString());
        Files.createDirectories(root.resolve("photo"));
        Files.createDirectories(root.resolve("notes/single"));
        Files.createDirectories(root.resolve("notes/group"));
    }

    /**
     * Copia notas de consulta a la carpeta "consultations" de cada paciente.
     * @param consultationId ID de la consulta
     * @param patientId      ID del paciente
     * @param originalNotePath ruta del archivo de notas fuente
     * @return la ruta al archivo copiado
     * @throws java.io.IOException
     */
    public Path copyConsultationNoteForPatient(
            UUID consultationId,
            UUID patientId,
            Path originalNotePath) throws IOException {
        Path destDir = baseDir.resolve(patientId.toString()).resolve("consultations");
        Files.createDirectories(destDir);
        String fileName = consultationId + getExtension(originalNotePath);
        Path target = destDir.resolve(fileName);
        return Files.copy(originalNotePath, target, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Marca u oculta la carpeta raíz de un paciente en Windows (atributo DOS).
     * @param patientId ID del paciente
     * @param hidden    true=ocultar, false=mostrar
     * @throws java.io.IOException
     */
    public void setHidden(UUID patientId, boolean hidden) throws IOException {
        Path dir = baseDir.resolve(patientId.toString());
        Files.setAttribute(dir, "dos:hidden", hidden);
    }
    
    /**
     * Verifica si el paciente tiene una foto cargada.
     * @param patientId ID del paciente
     * @return true si existe al menos un archivo en la carpeta photo
     * @throws IOException si hay error de E/S
     */
    public boolean hasPatientPhoto(UUID patientId) throws IOException {
        Path photoDir = baseDir.resolve(patientId.toString()).resolve("photo");
        if (!Files.exists(photoDir) || !Files.isDirectory(photoDir)) {
            return false;
        }
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(photoDir)) {
            for (Path entry : ds) {
                if (Files.isRegularFile(entry)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Obtiene la ruta de la foto del paciente si existe.
     * @param patientId ID del paciente
     * @return Path al archivo de la foto, o null si no hay ninguna
     * @throws IOException si hay error de E/S
     */
    public Path getPatientPhoto(UUID patientId) throws IOException {
        Path photoDir = baseDir.resolve(patientId.toString()).resolve("photo");
        if (!Files.exists(photoDir) || !Files.isDirectory(photoDir)) {
            return null;
        }
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(photoDir)) {
            for (Path entry : ds) {
                if (Files.isRegularFile(entry)) {
                    return entry;
                }
            }
        }
        return null;
    }
    
    /**
     * Devuelve el path absoluto de la foto del paciente en formato String.
     * @param patientId ID del paciente
     * @return ruta absoluta como String, o null si no existe
     * @throws IOException si hay error de E/S
     */
    public String getPatientPhotoPath(UUID patientId) throws IOException {
        Path photo = getPatientPhoto(patientId);
        return photo != null ? photo.toAbsolutePath().toString() : "";
    }
    
    /**
     * Copia la foto seleccionada a la carpeta "photo" del paciente,
     * eliminando previamente cualquier imagen existente.
     * @param patientId ID del paciente
     * @param sourcePhotoPath ruta del archivo fuente
     * @return la ruta al archivo copiado
     * @throws IOException si ocurre un error de E/S
     */
    public Path copyPhotoToPatientDir(UUID patientId, Path sourcePhotoPath) throws IOException {
        Path destDir = baseDir.resolve(patientId.toString()).resolve("photo");
        Files.createDirectories(destDir);

        // Eliminar fotos existentes
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(destDir)) {
            for (Path existing : ds) {
                if (Files.isRegularFile(existing)) {
                    Files.delete(existing);
                }
            }
        }

        // Copiar la nueva imagen (dejando el archivo original intacto)
        Path target = destDir.resolve(sourcePhotoPath.getFileName());
        return Files.copy(sourcePhotoPath, target, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Elimina la(s) foto(s) del paciente en la carpeta "photo".
     * @param patientId ID del paciente
     * @return true si se eliminó al menos un archivo, false si no había ninguno
     * @throws IOException si hay error de E/S
     */
    public boolean deletePatientPhoto(UUID patientId) throws IOException {
        Path photoDir = baseDir.resolve(patientId.toString()).resolve("photo");
        if (!Files.exists(photoDir) || !Files.isDirectory(photoDir)) {
            return false;
        }
        boolean deleted = false;
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(photoDir)) {
            for (Path entry : ds) {
                if (Files.isRegularFile(entry)) {
                    Files.delete(entry);
                    deleted = true;
                }
            }
        }
        return deleted;
    }

    /**
     * Extrae la extensión del archivo (ej. ".docx"), o cadena vacía si no tiene.
     */
    private String getExtension(Path file) {
        String name = file.getFileName().toString();
        int idx = name.lastIndexOf('.');
        return (idx == -1) ? "" : name.substring(idx);
    }
}
