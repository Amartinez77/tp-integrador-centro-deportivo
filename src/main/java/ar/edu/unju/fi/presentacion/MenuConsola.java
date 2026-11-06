package ar.edu.unju.fi.presentacion;

import ar.edu.unju.fi.negocio.ControlAcceso;
import ar.edu.unju.fi.negocio.Logica;
import ar.edu.unju.fi.negocio.excepciones.AccesoDenegadoException;
import ar.edu.unju.fi.negocio.excepciones.NegocioException;
import ar.edu.unju.fi.negocio.modelo.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class MenuConsola {

    private final Logica logica;
    private final ControlAcceso controlAcceso;
    private final Scanner scanner;

    public MenuConsola(Logica logica, ControlAcceso controlAcceso) {
        this.logica = logica;
        this.controlAcceso = controlAcceso;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        ConsolaUI.limpiarPantalla();
        int opcion;
        do {
            mostrarMenuPrincipal();
            opcion = leerEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1 -> { ConsolaUI.limpiarPantalla(); menuSocios(); }
                case 2 -> { ConsolaUI.limpiarPantalla(); menuEquipamiento(); }
                case 3 -> { ConsolaUI.limpiarPantalla(); menuReservas(); }
                case 4 -> { ConsolaUI.limpiarPantalla(); menuFacturacion(); }
                case 5 -> { ConsolaUI.limpiarPantalla(); probarMolineteAcceso(); }
                case 6 -> { ConsolaUI.limpiarPantalla(); menuReportes(); }
                case 0 -> System.out.println("\nSaliendo del sistema...");
                default -> {
                    System.out.println(ConsolaUI.ROJO + "Opción no válida. Intente nuevamente." + ConsolaUI.RESET);
                    pausar();
                }
            }
        } while (opcion != 0);
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n" + ConsolaUI.AZUL + ConsolaUI.NEGRITA + "========================================" + ConsolaUI.RESET);
        System.out.println(ConsolaUI.AZUL + ConsolaUI.NEGRITA + "       SISTEMA CENTRO DEPORTIVO         " + ConsolaUI.RESET);
        System.out.println(ConsolaUI.AZUL + ConsolaUI.NEGRITA + "========================================" + ConsolaUI.RESET);
        System.out.println("1. Gestión de Socios");
        System.out.println("2. Gestión de Equipamiento y Alquileres");
        System.out.println("3. Gestión de Reservas de Actividades");
        System.out.println("4. Módulo de Facturación y Cuotas");
        System.out.println("5. Simulación de Molinete de Acceso");
        System.out.println("6. Reportes y Búsqueda Binaria");
        System.out.println(ConsolaUI.AMARILLO + "0. Guardar y Salir" + ConsolaUI.RESET);
        System.out.println(ConsolaUI.AZUL + "----------------------------------------" + ConsolaUI.RESET);
    }

    // ==========================================
    // 1. SUBMENÚ SOCIOS
    // ==========================================
    private void menuSocios() {
        int op;
        do {
            System.out.println("\n" + ConsolaUI.CYAN + "--- GESTIÓN DE SOCIOS ---" + ConsolaUI.RESET);
            System.out.println("1. Registrar Socio Estándar");
            System.out.println("2. Registrar Socio VIP");
            System.out.println("3. Registrar Socio Vitalicio");
            System.out.println("4. Buscar Socio por DNI");
            System.out.println("5. Listar todos los Socios");
            System.out.println(ConsolaUI.AMARILLO + "0. Volver al Menú Principal" + ConsolaUI.RESET);
            op = leerEntero("Opción: ");

            try {
                switch (op) {
                    case 1 -> {
                        ConsolaUI.limpiarPantalla();
                        String dni = leerTexto("DNI: ");
                        String nombre = leerTexto("Nombre: ");
                        String apellido = leerTexto("Apellido: ");
                        double cuota = leerDouble("Cuota Base: ");
                        logica.registrarSocio(new SocioEstandar(dni, nombre, apellido, cuota));
                        System.out.println(ConsolaUI.VERDE + ">> Socio estándar registrado correctamente." + ConsolaUI.RESET);
                    }
                    case 2 -> {
                        ConsolaUI.limpiarPantalla();
                        String dni = leerTexto("DNI: ");
                        String nombre = leerTexto("Nombre: ");
                        String apellido = leerTexto("Apellido: ");
                        double cuota = leerDouble("Cuota Base: ");
                        double recargo = leerDouble("Recargo Zonas Exclusivas: ");
                        logica.registrarSocio(new SocioVIP(dni, nombre, apellido, cuota, recargo));
                        System.out.println(ConsolaUI.VERDE + ">> Socio VIP registrado correctamente." + ConsolaUI.RESET);
                    }
                    case 3 -> {
                        ConsolaUI.limpiarPantalla();
                        String dni = leerTexto("DNI: ");
                        String nombre = leerTexto("Nombre: ");
                        String apellido = leerTexto("Apellido: ");
                        double cuota = leerDouble("Cuota Base: ");
                        double desc = leerDouble("Porcentaje de Descuento (%): ");
                        logica.registrarSocio(new SocioVitalicio(dni, nombre, apellido, cuota, desc));
                        System.out.println(ConsolaUI.VERDE + ">> Socio Vitalicio registrado correctamente." + ConsolaUI.RESET);
                    }
                    case 4 -> {
                        ConsolaUI.limpiarPantalla();
                        String dni = leerTexto("DNI a buscar: ");
                        Socio s = logica.buscarSocio(dni);
                        if (s != null) {
                            boolean alDia = logica.tieneCuotasAlDia(s.getDni());
                            ConsolaUI.imprimirFichaSocio(s, alDia);
                        } else {
                            System.out.println(ConsolaUI.ROJO + "No se encontró ningún socio con el DNI indicado." + ConsolaUI.RESET);
                        }
                        pausar();
                    }
                    case 5 -> {
                        ConsolaUI.limpiarPantalla();
                        List<Socio> socios = logica.obtenerTodosLosSocios();
                        if (socios.isEmpty()) {
                            System.out.println("No hay socios registrados.");
                        } else {
                            socios.forEach(s -> ConsolaUI.imprimirFichaSocio(s, logica.tieneCuotasAlDia(s.getDni())));
                        }
                        pausar();
                    }
                    case 0 -> {
                        System.out.println("Regresando...");
                        ConsolaUI.limpiarPantalla();
                    }
                    default -> System.out.println(ConsolaUI.ROJO + "Opción inválida." + ConsolaUI.RESET);
                }
            } catch (NegocioException e) {
                System.err.println(ConsolaUI.ROJO + ">> Error de negocio: " + e.getMessage() + ConsolaUI.RESET);
            }
        } while (op != 0);
    }

    // ==========================================
    // 2. SUBMENÚ EQUIPAMIENTO Y ALQUILERES
    // ==========================================
    private void menuEquipamiento() {
        int op;
        do {
            System.out.println("\n" + ConsolaUI.CYAN + "--- GESTIÓN DE EQUIPAMIENTO ---" + ConsolaUI.RESET);
            System.out.println("1. Dar de alta Equipamiento");
            System.out.println("2. Registrar Alquiler");
            System.out.println("3. Devolver Equipamiento");
            System.out.println("4. Listar Inventario");
            System.out.println(ConsolaUI.AMARILLO + "0. Volver al Menú Principal" + ConsolaUI.RESET);
            op = leerEntero("Opción: ");

            try {
                switch (op) {
                    case 1 -> {
                        ConsolaUI.limpiarPantalla();
                        String cod = leerTexto("Código: ");
                        String nom = leerTexto("Nombre: ");
                        double costo = leerDouble("Costo Alquiler: ");
                        logica.registrarEquipamiento(new Equipamiento(cod, nom, costo));
                        System.out.println(ConsolaUI.VERDE + ">> Equipamiento registrado con éxito." + ConsolaUI.RESET);
                    }
                    case 2 -> {
                        ConsolaUI.limpiarPantalla();
                        String idAlq = leerTexto("ID Alquiler: ");
                        String dni = leerTexto("DNI Socio: ");
                        String cod = leerTexto("Código Equipamiento: ");
                        int dias = leerEntero("Días de préstamo: ");
                        Alquiler alq = logica.alquilarEquipamiento(idAlq, dni, cod, dias);
                        System.out.println(ConsolaUI.VERDE + ">> Alquiler registrado exitosamente." + ConsolaUI.RESET);
                    }
                    case 3 -> {
                        ConsolaUI.limpiarPantalla();
                        String idAlq = leerTexto("ID Alquiler a devolver: ");
                        double tarifaMora = leerDouble("Tarifa mora diaria: ");
                        double mora = logica.devolverEquipamiento(idAlq, java.time.LocalDate.now(), tarifaMora);
                        System.out.printf(ConsolaUI.VERDE + ">> Devolución registrada. Recargo por mora: $%.2f\n" + ConsolaUI.RESET, mora);
                    }
                    case 4 -> {
                        ConsolaUI.limpiarPantalla();
                        List<Equipamiento> lista = logica.obtenerTodosLosEquipamientos();
                        if (lista.isEmpty()) {
                            System.out.println("No hay equipamiento en inventario.");
                        } else {
                            lista.forEach(e -> System.out.printf("• [%s] %-25s | Costo: $%-8.2f | Estado: %s\n",
                                    e.getCodigo(), e.getNombre(), e.getCostoAlquiler(),
                                    (e.isDisponible() ? ConsolaUI.VERDE + "DISPONIBLE" : ConsolaUI.ROJO + "EN USO") + ConsolaUI.RESET));
                        }
                        pausar();
                    }
                    case 0 -> {
                        System.out.println("Regresando...");
                        ConsolaUI.limpiarPantalla();
                    }
                    default -> System.out.println(ConsolaUI.ROJO + "Opción inválida." + ConsolaUI.RESET);
                }
            } catch (NegocioException e) {
                System.err.println(ConsolaUI.ROJO + ">> Error de negocio: " + e.getMessage() + ConsolaUI.RESET);
            }
        } while (op != 0);
    }

    // ==========================================
    // 3. SUBMENÚ RESERVAS
    // ==========================================
    private void menuReservas() {
        int op;
        do {
            System.out.println("\n" + ConsolaUI.CYAN + "--- GESTIÓN DE RESERVAS ---" + ConsolaUI.RESET);
            System.out.println("1. Inscribir Socio a una Actividad");
            System.out.println("2. Consultar Reservas de una Actividad");
            System.out.println(ConsolaUI.AMARILLO + "0. Volver al Menú Principal" + ConsolaUI.RESET);
            op = leerEntero("Opción: ");

            try {
                switch (op) {
                    case 1 -> {
                        ConsolaUI.limpiarPantalla();
                        String idRes = leerTexto("ID Reserva (ej. RES-01): ");
                        String act = leerTexto("Código Actividad (ej. NATACION, SPINNING): ");
                        String dni = leerTexto("DNI Socio: ");
                        LocalDateTime fechaHora = leerFechaHora("Fecha y hora del turno (dd/MM/yyyy HH:mm) [ej. 10/11/2026 18:00]: ");
                        Reserva r = logica.registrarReserva(idRes, act, dni, fechaHora);
                        System.out.println(ConsolaUI.VERDE + ">> Reserva completada: " + r + ConsolaUI.RESET);
                    }
                    case 2 -> {
                        ConsolaUI.limpiarPantalla();
                        String act = leerTexto("Código Actividad: ");
                        Set<Reserva> res = logica.obtenerReservasPorActividad(act);
                        if (res.isEmpty()) {
                            System.out.println("No hay reservas registradas para la actividad: " + act);
                        } else {
                            System.out.println("\nInscriptos en " + ConsolaUI.NEGRITA + act.toUpperCase() + ConsolaUI.RESET + ":");
                            res.forEach(r -> System.out.println(" • " + r));
                        }
                        pausar();
                    }
                    case 0 -> {
                        System.out.println("Regresando...");
                        ConsolaUI.limpiarPantalla();
                    }
                    default -> System.out.println(ConsolaUI.ROJO + "Opción inválida." + ConsolaUI.RESET);
                }
            } catch (NegocioException e) {
                System.err.println(ConsolaUI.ROJO + ">> Error de reserva: " + e.getMessage() + ConsolaUI.RESET);
            }
        } while (op != 0);
    }

    // ==========================================
    // 4. SUBMENÚ FACTURACIÓN
    // ==========================================
    private void menuFacturacion() {
        int op;
        do {
            System.out.println("\n" + ConsolaUI.CYAN + "--- FACTURACIÓN Y CUOTAS ---" + ConsolaUI.RESET);
            System.out.println("1. Emitir Factura de Cuota Mensual");
            System.out.println("2. Ver Facturas de un Socio por Periodo Fiscal");
            System.out.println("3. Listar todas las facturas emitidas");
            System.out.println(ConsolaUI.AMARILLO + "0. Volver al Menú Principal" + ConsolaUI.RESET);
            op = leerEntero("Opción: ");

            try {
                switch (op) {
                    case 1 -> {
                        ConsolaUI.limpiarPantalla();
                        String numFac = leerTexto("Número de Factura: ");
                        String dni = leerTexto("DNI Socio: ");
                        String periodo = leerTexto("Periodo Fiscal (ej. 2026-11): ");
                        Factura f = logica.emitirFacturaCuota(numFac, dni, periodo);
                        System.out.println(ConsolaUI.VERDE + ">> Factura emitida exitosamente:" + ConsolaUI.RESET);
                        ConsolaUI.imprimirFichaFactura(f);
                        pausar();
                    }
                    case 2 -> {
                        ConsolaUI.limpiarPantalla();
                        String dni = leerTexto("DNI Socio: ");
                        String periodo = leerTexto("Periodo Fiscal: ");
                        List<Factura> facs = logica.obtenerFacturasDeSocioPorPeriodo(dni, periodo);
                        if (facs.isEmpty()) {
                            System.out.println("No registra facturas para el periodo indicado.");
                        } else {
                            facs.forEach(ConsolaUI::imprimirFichaFactura);
                        }
                        pausar();
                    }
                    case 3 -> {
                        ConsolaUI.limpiarPantalla();
                        List<Factura> todas = logica.obtenerTodasLasFacturas();
                        if (todas.isEmpty()) {
                            System.out.println("No hay facturas emitidas.");
                        } else {
                            todas.forEach(ConsolaUI::imprimirFichaFactura);
                        }
                        pausar();
                    }
                    case 0 -> {
                        System.out.println("Regresando...");
                        ConsolaUI.limpiarPantalla();
                    }
                    default -> System.out.println(ConsolaUI.ROJO + "Opción inválida." + ConsolaUI.RESET);
                }
            } catch (NegocioException e) {
                System.err.println(ConsolaUI.ROJO + ">> Error de facturación: " + e.getMessage() + ConsolaUI.RESET);
            }
        } while (op != 0);
    }

    // ==========================================
    // 5. MOLINETE DE ACCESO
    // ==========================================
    private void probarMolineteAcceso() {
        ConsolaUI.limpiarPantalla();
        System.out.println("\n" + ConsolaUI.CYAN + "--- SIMULACIÓN DE MOLINETE DE ACCESO ---" + ConsolaUI.RESET);
        String dni = leerTexto("Ingrese el DNI del socio frente al molinete: ");
        try {
            if (controlAcceso.validarAcceso(dni)) {
                System.out.println("\n" + ConsolaUI.VERDE + ConsolaUI.NEGRITA + "========================================");
                System.out.println("  >>> [ACCESO CONCEDIDO] PASE ADELANTE <<<");
                System.out.println("========================================" + ConsolaUI.RESET);
            }
        } catch (AccesoDenegadoException e) {
            System.out.println("\n" + ConsolaUI.ROJO + ConsolaUI.NEGRITA + "========================================");
            System.out.println("  >>> [ACCESO DENEGADO] <<<");
            System.out.println("  Motivo: " + e.getMessage());
            System.out.println("========================================" + ConsolaUI.RESET);
        }
        pausar();
    }

    // ==========================================
    // 6. REPORTES Y BÚSQUEDA BINARIA
    // ==========================================
    private void menuReportes() {
        int op;
        do {
            System.out.println("\n" + ConsolaUI.CYAN + "--- REPORTES Y BÚSQUEDA BINARIA ---" + ConsolaUI.RESET);
            System.out.println("1. Listar Socios Ordenados Alfabéticamente (Comparable)");
            System.out.println("2. Listar Socios Ordenados por Cuota Mensual Descendente (Comparator)");
            System.out.println("3. Ranking de Actividades por Cantidad de Reservas");
            System.out.println("4. Búsqueda Binaria de Socio por DNI (Collections.binarySearch)");
            System.out.println(ConsolaUI.AMARILLO + "0. Volver al Menú Principal" + ConsolaUI.RESET);
            op = leerEntero("Opción: ");

            switch (op) {
                case 1 -> {
                    ConsolaUI.limpiarPantalla();
                    List<Socio> ord = logica.obtenerSociosOrdenadosPorApellido();
                    if (ord.isEmpty()) {
                        System.out.println("No hay socios registrados para generar el reporte.");
                    } else {
                        ord.forEach(s -> System.out.printf("• %-25s | DNI: %-10s | Categoría: %s\n",
                                s.getApellido() + ", " + s.getNombre(), s.getDni(), s.getClass().getSimpleName()));
                    }
                    pausar();
                }
                case 2 -> {
                    ConsolaUI.limpiarPantalla();
                    List<Socio> ord = logica.obtenerSociosOrdenadosPorCuota();
                    if (ord.isEmpty()) {
                        System.out.println("No hay socios registrados para generar el reporte.");
                    } else {
                        ord.forEach(s -> System.out.printf("• %-25s | DNI: %-10s | Cuota Mensual: $%.2f\n",
                                s.getApellido() + ", " + s.getNombre(), s.getDni(), s.calcularCuotaMensual()));
                    }
                    pausar();
                }
                case 3 -> {
                    ConsolaUI.limpiarPantalla();
                    List<ActividadReporte> actRep = logica.obtenerActividadesOrdenadasPorReservas();
                    if (actRep.isEmpty()) {
                        System.out.println("No hay actividades con reservas registradas.");
                    } else {
                        actRep.forEach(a -> System.out.printf("• Actividad: %-15s | Inscriptos: %d\n",
                                a.getCodigoActividad(), a.getCantidadReservas()));
                    }
                    pausar();
                }
                case 4 -> {
                    ConsolaUI.limpiarPantalla();
                    String dni = leerTexto("DNI a buscar mediante Binary Search: ");
                    Socio encontrado = logica.buscarSocioPorDniBinario(dni);
                    if (encontrado != null) {
                        ConsolaUI.imprimirFichaSocio(encontrado, logica.tieneCuotasAlDia(encontrado.getDni()));
                    } else {
                        System.out.println(ConsolaUI.ROJO + "Socio no encontrado con Collections.binarySearch." + ConsolaUI.RESET);
                    }
                    pausar();
                }
                case 0 -> {
                    System.out.println("Regresando...");
                    ConsolaUI.limpiarPantalla();
                }
                default -> System.out.println(ConsolaUI.ROJO + "Opción inválida." + ConsolaUI.RESET);
            }
        } while (op != 0);
    }

    private LocalDateTime leerFechaHora(String mensaje) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return LocalDateTime.parse(texto, dtf);
            } catch (Exception e) {
                System.out.println(ConsolaUI.ROJO + "Formato inválido. Use dd/MM/yyyy HH:mm (ej: 10/11/2026 18:00)." + ConsolaUI.RESET);
            }
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!scanner.hasNextLine()) return "";
        return scanner.nextLine().trim();
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            if (!scanner.hasNextLine()) return 0;
            String linea = scanner.nextLine().trim();
            if (linea.isEmpty()) continue;
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println(ConsolaUI.ROJO + "Entrada inválida. Ingrese un número entero." + ConsolaUI.RESET);
            }
        }
    }

    private double leerDouble(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                if (!scanner.hasNextLine()) return 0.0;
                String linea = scanner.nextLine().trim();
                return Double.parseDouble(linea);
            } catch (NumberFormatException e) {
                System.out.println(ConsolaUI.ROJO + "Entrada inválida. Ingrese un valor numérico (ej. 1500.50)." + ConsolaUI.RESET);
            }
        }
    }

    private void pausar() {
        System.out.print("\nPresione [Enter] para continuar...");
        scanner.nextLine();
        ConsolaUI.limpiarPantalla();
    }

}