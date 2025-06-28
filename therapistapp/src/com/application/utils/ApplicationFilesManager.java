package com.application.utils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

/**
 * Gestiona creación, validación y modificaciones de las carpetas base de la aplicación.
 */
public class ApplicationFilesManager {

    private final Path baseDir;
    private final Path patientsDir;
    private final Path appDataDir;

    /**
     * Inicializa el manager con la carpeta base dentro de “Documentos/therapistapp”.
     */
    public ApplicationFilesManager() {
        String userHome = System.getProperty("user.home");
        this.baseDir     = Paths.get(userHome, "Documents", "therapistapp");
        this.patientsDir = baseDir.resolve("patients");
        this.appDataDir  = baseDir.resolve("appdata");
    }

    /** Crea todas las carpetas base si no existen. */
    public void ensureBaseFolders() throws IOException {
        Files.createDirectories(patientsDir);
        Files.createDirectories(appDataDir);
    }

    /** Devuelve true si la carpeta base y sus subdirectorios existen. */
    public boolean areFoldersReady() {
        return Files.isDirectory(patientsDir) && Files.isDirectory(appDataDir);
    }

    /**
     * Crea un subdirectorio bajo la carpeta base.
     * @param name nombre del nuevo directorio (p.ej. "logs")
     * @throws IOException si falla la creación
     */
    public Path createSubfolder(String name) throws IOException {
        Path sub = baseDir.resolve(name);
        return Files.createDirectories(sub);
    }

    /**
     * Elimina un subdirectorio (y todo su contenido) si existe.
     * @param name nombre del directorio a borrar
     * @throws IOException si falla el borrado
     */
    public void deleteSubfolder(String name) throws IOException {
        Path sub = baseDir.resolve(name);
        if (Files.exists(sub)) {
            // borra todo recursivamente
            Files.walk(sub)
                 .sorted(Comparator.reverseOrder())
                 .forEach(p -> {
                     try { Files.delete(p); }
                     catch (IOException e) { /* maneja o relanza */ }
                 });
        }
    }

    /** Ejemplo de uso rápido: */
    public static void main(String[] args) {
        ApplicationFilesManager mgr = new ApplicationFilesManager();
        try {
            if (!mgr.areFoldersReady()) {
                mgr.ensureBaseFolders();
                System.out.println("Directorios base creados.");
            } else {
                System.out.println("Los directorios ya existen.");
            }
            // mgr.createSubfolder("logs");
            // mgr.deleteSubfolder("appdata");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
