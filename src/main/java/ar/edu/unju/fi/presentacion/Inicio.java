package ar.edu.unju.fi.presentacion;

import ar.edu.unju.fi.datos.AccesoDatos;
import ar.edu.unju.fi.datos.excepciones.PersistenciaException;
import ar.edu.unju.fi.negocio.ControlAcceso;
import ar.edu.unju.fi.negocio.Logica;

import javax.swing.*;
import java.util.Scanner;

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

        // 3. Recuperación en frío de los 4 archivos canónicos en orden de dependencia
        cargarDatosIniciales(logica);

        // 4. Mecanismo de persistencia garantizada ante cierre forzado (Shutdown Hook)
        registrarGanchoDeCierre(logica);

        // 5. Arranque de la interfaz de consola
        int opcionInterfaz = seleccionarModoInterfaz();

        if (opcionInterfaz == 1) {
            // MODO CONSOLA
            System.out.println("\n>> Iniciando interfaz de Consola...");
            MenuConsola menu = new MenuConsola(logica, controlAcceso);
            menu.iniciar();
        } else {
            // MODO GRÁFICO (SWING)
            System.out.println("\n>> Iniciando interfaz Gráfica (Swing)...");
            SwingUtilities.invokeLater(() -> {
                VentanaPrincipal ventana = new VentanaPrincipal(logica, controlAcceso);
                ventana.setVisible(true);
            });
        }
    }

    private static int seleccionarModoInterfaz() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n========================================");
        System.out.println("  SELECCIONE EL MODO DE INTERFAZ");
        System.out.println("========================================");
        System.out.println("1. Modo Consola (Interactiva)");
        System.out.println("2. Modo Gráfico (GUI Swing)");
        System.out.print("Opción (1 o 2) [Defecto: 1]: ");

        String entrada = scanner.nextLine().trim();
        if ("2".equals(entrada)) {
            return 2;
        }
        return 1;
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

    /**
     * Shutdown Hook: Hilo ejecutado por la JVM cuando el proceso finaliza normalmente
     * o se interrumpe abruptamente (Ctrl+C, kill, cierre de terminal).
     */
    private static void registrarGanchoDeCierre(Logica logica) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SISTEMA] Persistiendo datos en disco antes de cerrar...");
            try {
                logica.guardarSocios();
                logica.guardarEquipamientos();
                logica.guardarReservas();
                logica.guardarFacturas();
                System.out.println("[SISTEMA] Persistencia integral completada con éxito (Socios, Equipamiento, Reservas, Facturas).");
            } catch (PersistenciaException e) {
                System.err.println("[SISTEMA] Error crítico al persistir datos en cierre: " + e.getMessage());
            }
        }));
    }
}