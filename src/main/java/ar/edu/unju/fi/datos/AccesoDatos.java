package ar.edu.unju.fi.datos;


import ar.edu.unju.fi.datos.excepciones.PersistenciaException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AccesoDatos {

    // ruta por defecto donde se guardaran los archivos serializados
    private static final String DIRECTORIO_DATOS = "datos";
    public static final String ARCHIVO_SOCIOS = DIRECTORIO_DATOS + File.separator + "Socios.txt";
    public static final String ARCHIVO_EQUIPAMIENTO = DIRECTORIO_DATOS + File.separator + "Equipamiento.txt";
    public static final String ARCHIVO_ALQUILERES = DIRECTORIO_DATOS + File.separator + "Alquileres.txt";

    public AccesoDatos(){

        crearDirectorioSiNoExiste();

    }

    /**
     * Asegura que el directorio 'datos/' exista antes de intentar leer o escribir
     */
    private void crearDirectorioSiNoExiste(){
        try {
            Path path = Paths.get(DIRECTORIO_DATOS);
            if (!Files.exists(path)){
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("Advertencia: No se pudo verificar o crear el directorio de datos: " + e.getMessage());
        }
    }

    /**
     * Guarda una lista genérica de objetos serializables en un archivo.
     * Utiliza try-with-resources para garantizar el cierre seguro de los streams.
     *
     * @param rutaArchivo Ruta o nombre del archivo .txt a escribir.
     * @param elementos   Lista de objetos a persistir (deben implementar Serializable).
     * @param <T>         Tipo del elemento que implementa Serializable.
     * @throws PersistenciaException Si ocurre un error de Entrada/Salida.
     */
    public <T extends Serializable> void guardar(String rutaArchivo, List<T> elementos) throws PersistenciaException {

        if (rutaArchivo == null || rutaArchivo.isBlank()){
            throw new IllegalArgumentException("La ruta del archivo no puede ser nula.");
        }
        if (elementos == null){
            throw new IllegalArgumentException("La lista de elementos no puede ser nula.");
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))){

            oos.writeObject(elementos);
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar datos en el archivo: " + rutaArchivo, e);
        }

    }

    /**
     * Recupera una lista genérica de objetos desde un archivo serializado.
     * Si el archivo no existe o está vacío, retorna una lista vacía para permitir
     * el arranque limpio del sistema.
     *
     * @param rutaArchivo Ruta o nombre del archivo .txt a leer.
     * @param <T>         Tipo esperado de los elementos contenidos en la lista.
     * @return Lista con los objetos recuperados, o lista vacía si no hay registros.
     * @throws PersistenciaException Si el archivo está corrupto o la clase no es compatible.
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> List<T> leer(String rutaArchivo) throws PersistenciaException{
        if (rutaArchivo == null || rutaArchivo.isBlank()){
            throw new IllegalArgumentException("La ruta del archivo no puede ser nula.");
        }

        File archivo = new File(rutaArchivo);
        if (!archivo.exists() || archivo.length() == 0){
            // el archivo aún no existe (primera ejecución): retornamos lista vacía sin romper
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))){
            Object obj = ois.readObject();
            if (obj instanceof List<?>){
                return (List<T>) obj;
            }else {
                throw new PersistenciaException("Formato no válido: el archivo no contiene una lista serializada.");
            }
        }catch (ClassNotFoundException e){

            throw new PersistenciaException("Error de compatibilidad: clase no encontrada al deserializar en " + rutaArchivo, e);

        }catch (IOException e){
            throw new PersistenciaException("Error de E/S al leer los datos desde: " + rutaArchivo, e);
        }

    }

}

