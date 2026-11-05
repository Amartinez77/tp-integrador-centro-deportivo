package ar.edu.unju.fi.presentacion;

import ar.edu.unju.fi.datos.AccesoDatos;
import ar.edu.unju.fi.datos.excepciones.PersistenciaException;
import ar.edu.unju.fi.negocio.ControlAcceso;
import ar.edu.unju.fi.negocio.Logica;


/**
 * Punto de entrada principal del sistema (Capa de Presentación).
 * Coordina la inicialización de las 3 capas, el arranque en frío,
 * y garantiza la persistencia integral de los 4 archivos ante cierres forzados.
 */
public class Inicio {

    public static void main(String[] args) {
        System.out.println("Inicializando componentes del Centro Deportivo...");

        // 1. Capa de datos
        AccesoDatos accesoDatos = new AccesoDatos();

        // 2. Capa de Negocio (Fachada y molinete)
        Logica logica = new Logica(accesoDatos);
        ControlAcceso controlAcceso = new ControlAcceso(logica);

        // 3. Recuperación en frio de los 4 archivos canónicos en orden de dependencia
        cargarDatosIniciales(logica);

        // 4. Mecanismo de persistencia garantizada ante cierre forzado (Shutdown Hook)
        registrarGanchoDeCierre(logica);

        // 5. Arranque de la interfaz de consola
        MenuConsola menu = new MenuConsola(logica, controlAcceso);

        // Guardando limpio al salir voluntariamente
        guardarTodosLosDatos(logica);

    }

    private static void cargarDatosIniciales(Logica logica) {
        try {
            // El orden es crítico: Socios debe cargarse antes que Facturas
            logica.cargarSocios();
            logica.cargarEquipamientos();
            logica.cargarReservas();
            logica.cargarFacturas();
            System.out.println("Persistencia en frío cargada exitosamente desde disco (4 archivos).");
        } catch (PersistenciaException e) {
            System.err.println("Advertencia durante el arranque en frío: " + e.getMessage());
        }
    }

    private static void guardarTodosLosDatos(Logica logica) {
        try {
            logica.guardarSocios();
            logica.guardarEquipamientos();
            logica.guardarReservas();
            logica.guardarFacturas();
            System.out.println("Todos los datos han sido persistidos correctamente en disco.");
        } catch (PersistenciaException e) {
            System.err.println("Error crítico al persistir datos: " + e.getMessage());
        }
    }

    /**
     * Shutdown Hook: Hilo ejecutado por la JVM cuando el proceso se interrumpe
     * abruptamente (Ctrl+C, kill, cierre de terminal de IntelliJ).
     */
    private static void registrarGanchoDeCierre(Logica logica) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\\n[SHUTDOWN-HOOK] Interrupción detectada. Asegurando persistencia de los 4 archivos...");
            try {
                logica.guardarSocios();
                logica.guardarEquipamientos();
                logica.guardarReservas();
                logica.guardarFacturas();
                System.out.println("[SHUTDOWN-HOOK] Persistencia forzada completada con éxito. Archivos consistentes.");
            } catch (PersistenciaException e) {
                System.err.println("[SHUTDOWN-HOOK] Falló la persistencia durante el cierre: " + e.getMessage());
            }
        }));
    }

}