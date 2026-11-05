package ar.edu.unju.fi.presentacion;

import ar.edu.unju.fi.negocio.ControlAcceso;
import ar.edu.unju.fi.negocio.Logica;
import ar.edu.unju.fi.negocio.Reportes;
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
    private final Reportes reportes;
    private final Scanner scanner;

    public MenuConsola(Logica logica, ControlAcceso controlAcceso) {
        this.logica = logica;
        this.controlAcceso = controlAcceso;
        this.reportes = new Reportes();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcion;
        do {
            mostrarMenuPrincipal();
            opcion = leerEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1 -> menuSocios();
                case 2 -> menuEquipamiento();
                case 3 -> menuReservas();
                case 4 -> menuFacturacion();
                case 5 -> probarMolineteAcceso();
                case 6 -> menuReportes();
                case 0 -> System.out.println("\nGuardando datos y saliendo del sistema...");
                default -> System.out.println("Opción no válida. Intente nuevamente.");
            }
        } while (opcion != 0);
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n========================================");
        System.out.println("       SISTEMA CENTRO DEPORTIVO         ");
        System.out.println("========================================");
        System.out.println("1. Gestión de Socios");
        System.out.println("2. Gestión de Equipamiento y Alquileres");
        System.out.println("3. Gestión de Reservas de Actividades");
        System.out.println("4. Módulo de Facturación y Cuotas");
        System.out.println("5. Simulación de Molinete de Acceso");
        System.out.println("6. Reportes y Búsqueda Binaria");
        System.out.println("0. Guardar y Salir");
        System.out.println("========================================");
    }

    // ==========================================
    // 1. SUBMENÚ SOCIOS
    // ==========================================
    private void menuSocios() {
        System.out.println("\n--- GESTIÓN DE SOCIOS ---");
        System.out.println("1. Registrar Socio Estándar");
        System.out.println("2. Registrar Socio VIP");
        System.out.println("3. Registrar Socio Vitalicio");
        System.out.println("4. Buscar Socio por DNI");
        System.out.println("5. Listar todos los Socios");
        int op = leerEntero("Opción: ");

        try {
            switch (op) {
                case 1 -> {
                    String dni = leerTexto("DNI: ");
                    String nombre = leerTexto("Nombre: ");
                    String apellido = leerTexto("Apellido: ");
                    double cuota = leerDouble("Cuota Base: ");
                    logica.registrarSocio(new SocioEstandar(dni, nombre, apellido, cuota));
                    System.out.println(">> Socio estándar registrado correctamente.");
                }
                case 2 -> {
                    String dni = leerTexto("DNI: ");
                    String nombre = leerTexto("Nombre: ");
                    String apellido = leerTexto("Apellido: ");
                    double cuota = leerDouble("Cuota Base: ");
                    double recargo = leerDouble("Recargo Zonas Exclusivas: ");
                    logica.registrarSocio(new SocioVIP(dni, nombre, apellido, cuota, recargo));
                    System.out.println(">> Socio VIP registrado correctamente.");
                }
                case 3 -> {
                    String dni = leerTexto("DNI: ");
                    String nombre = leerTexto("Nombre: ");
                    String apellido = leerTexto("Apellido: ");
                    double cuota = leerDouble("Cuota Base: ");
                    double desc = leerDouble("Porcentaje de Descuento (%): ");
                    logica.registrarSocio(new SocioVitalicio(dni, nombre, apellido, cuota, desc));
                    System.out.println(">> Socio Vitalicio registrado correctamente.");
                }
                case 4 -> {
                    String dni = leerTexto("DNI a buscar: ");
                    Socio s = logica.buscarSocio(dni);
                    if (s != null) {
                        System.out.println("Encontrado: " + s + " | Cuota Mensual: $" + s.calcularCuotaMensual());
                    } else {
                        System.out.println("No se encontró ningún socio con el DNI indicado.");
                    }
                }
                case 5 -> {
                    List<Socio> socios = logica.obtenerTodosLosSocios();
                    if (socios.isEmpty()) {
                        System.out.println("No hay socios registrados.");
                    } else {
                        socios.forEach(s -> System.out.println("- " + s + " [Cuota: $" + s.calcularCuotaMensual() + "]"));
                    }
                }
            }
        } catch (NegocioException e) {
            System.err.println(">> Error de negocio: " + e.getMessage());
        }
    }

    // ==========================================
    // 2. SUBMENÚ EQUIPAMIENTO Y ALQUILERES
    // ==========================================
    private void menuEquipamiento() {
        System.out.println("\n--- GESTIÓN DE EQUIPAMIENTO ---");
        System.out.println("1. Dar de alta Equipamiento");
        System.out.println("2. Registrar Alquiler");
        System.out.println("3. Devolver Equipamiento");
        System.out.println("4. Listar Inventario");
        int op = leerEntero("Opción: ");

        try {
            switch (op) {
                case 1 -> {
                    String cod = leerTexto("Código: ");
                    String nom = leerTexto("Nombre: ");
                    double costo = leerDouble("Costo Alquiler: ");
                    logica.registrarEquipamiento(new Equipamiento(cod, nom, costo));
                    System.out.println(">> Equipamiento registrado con éxito.");
                }
                case 2 -> {
                    String idAlq = leerTexto("ID Alquiler: ");
                    String dni = leerTexto("DNI Socio: ");
                    String cod = leerTexto("Código Equipamiento: ");
                    int dias = leerEntero("Días de préstamo: ");
                    Alquiler alq = logica.alquilarEquipamiento(idAlq, dni, cod, dias);
                    System.out.println(">> Alquiler registrado: " + alq);
                }
                case 3 -> {
                    String idAlq = leerTexto("ID Alquiler a devolver: ");
                    double tarifaMora = leerDouble("Tarifa mora diaria: ");
                    double mora = logica.devolverEquipamiento(idAlq, java.time.LocalDate.now(), tarifaMora);
                    System.out.println(">> Devolución registrada. Recargo por mora liquidado: $" + mora);
                }
                case 4 -> {
                    logica.obtenerTodosLosEquipamientos().forEach(System.out::println);
                }
            }
        } catch (NegocioException e) {
            System.err.println(">> Error de negocio: " + e.getMessage());
        }
    }

    // ==========================================
    // 3. SUBMENÚ RESERVAS (TS2-02)
    // ==========================================
    private void menuReservas() {
        System.out.println("\n--- GESTIÓN DE RESERVAS ---");
        System.out.println("1. Inscribir Socio a una Actividad");
        System.out.println("2. Consultar Reservas de una Actividad");
        int op = leerEntero("Opción: ");

        try {
            switch (op) {
                case 1 -> {
                    String idRes = leerTexto("ID Reserva (ej. RES-01): ");
                    String act = leerTexto("Código Actividad (ej. NATACION, SPINNING): ");
                    String dni = leerTexto("DNI Socio: ");
                    LocalDateTime fechaHora = leerFechaHora("Fecha y hora del turno (dd/MM/yyyy HH:mm) [ej. 10/11/2026 18:00]: ");
                    Reserva r = logica.registrarReserva(idRes, act, dni, fechaHora);
                    System.out.println(">> Reserva completada exitosamente: " + r);
                }
                case 2 -> {
                    String act = leerTexto("Código Actividad: ");
                    Set<Reserva> res = logica.obtenerReservasPorActividad(act);
                    if (res.isEmpty()) {
                        System.out.println("No hay reservas registradas para la actividad: " + act);
                    } else {
                        System.out.println("\nInscriptos en " + act.toUpperCase() + ":");
                        res.forEach(r -> System.out.println("- " + r));
                    }
                }
            }
        } catch (NegocioException e) {
            System.err.println(">> Error de reserva: " + e.getMessage());
        }
    }

    private LocalDateTime leerFechaHora(String mensaje) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return LocalDateTime.parse(texto, dtf);
            } catch (Exception e) {
                System.out.println("Formato de fecha u hora inválido. Debe usar exactamente el formato dd/MM/yyyy HH:mm (ej: 10/11/2026 18:00).");
            }
        }
    }

    // ==========================================
    // 4. SUBMENÚ FACTURACIÓN (TS3-01)
    // ==========================================
    private void menuFacturacion() {
        System.out.println("\n--- FACTURACIÓN Y CUOTAS ---");
        System.out.println("1. Emitir Factura de Cuota Mensual");
        System.out.println("2. Ver Facturas de un Socio por Periodo Fiscal");
        System.out.println("3. Listar todas las facturas emitidas");
        int op = leerEntero("Opción: ");

        try {
            switch (op) {
                case 1 -> {
                    String numFac = leerTexto("Número de Factura: ");
                    String dni = leerTexto("DNI Socio: ");
                    String periodo = leerTexto("Periodo Fiscal (ej. 2026-11): ");
                    Factura f = logica.emitirFacturaCuota(numFac, dni, periodo);
                    System.out.println(">> Factura emitida exitosamente: " + f);
                }
                case 2 -> {
                    String dni = leerTexto("DNI Socio: ");
                    String periodo = leerTexto("Periodo Fiscal: ");
                    List<Factura> facs = logica.obtenerFacturasDeSocioPorPeriodo(dni, periodo);
                    if (facs.isEmpty()) {
                        System.out.println("No registra facturas para el periodo indicado.");
                    } else {
                        facs.forEach(System.out::println);
                    }
                }
                case 3 -> {
                    List<Factura> todas = logica.obtenerTodasLasFacturas();
                    if (todas.isEmpty()) {
                        System.out.println("No hay facturas emitidas.");
                    } else {
                        todas.forEach(System.out::println);
                    }
                }
            }
        } catch (NegocioException e) {
            System.err.println(">> Error de facturación: " + e.getMessage());
        }
    }

    // ==========================================
    // 5. MOLINETE DE ACCESO (FIX S2-03)
    // ==========================================
    private void probarMolineteAcceso() {
        System.out.println("\n--- MOLINETE DE ACCESO ---");
        String dni = leerTexto("Ingrese el DNI del socio frente al molinete: ");
        try {
            if (controlAcceso.validarAcceso(dni)) {
                System.out.println(">> ACCESO CONCEDIDO: El socio está activo y tiene sus cuotas al día.");
            }
        } catch (AccesoDenegadoException e) {
            System.err.println(">> " + e.getMessage());
        }
    }

    // ==========================================
    // 6. REPORTES Y BÚSQUEDA BINARIA (TS3-02)
    // ==========================================
    private void menuReportes() {
        System.out.println("\n--- REPORTES Y BÚSQUEDA BINARIA ---");
        System.out.println("1. Listar Socios Ordenados Alfabéticamente (Comparable)");
        System.out.println("2. Listar Socios Ordenados por Cuota Mensual Descendente (Comparator)");
        System.out.println("3. Ranking de Actividades por Cantidad de Reservas");
        System.out.println("4. Búsqueda Binaria de Socio por DNI (Collections.binarySearch)");
        int op = leerEntero("Opción: ");

        List<Socio> socios = logica.obtenerTodosLosSocios();

        switch (op) {
            case 1 -> {
                List<Socio> ord = reportes.ordenarSociosPorApellido(socios);
                ord.forEach(s -> System.out.println(s.getApellido() + ", " + s.getNombre() + " (DNI: " + s.getDni() + ")"));
            }
            case 2 -> {
                List<Socio> ord = reportes.ordenarSociosPorCuota(socios);
                ord.forEach(s -> System.out.println(s + " -> $" + s.calcularCuotaMensual()));
            }
            case 3 -> {
                List<ActividadReporte> actRep = reportes.ordenarActividades(logica.obtenerTodasLasReservasPorActividad());
                actRep.forEach(System.out::println);
            }
            case 4 -> {
                String dni = leerTexto("DNI a buscar mediante Binary Search: ");
                Socio encontrado = reportes.buscarSocioPorDni(socios, dni);
                if (encontrado != null) {
                    System.out.println(">> Resultado de búsqueda binaria: " + encontrado);
                } else {
                    System.out.println(">> Socio no encontrado con Collections.binarySearch.");
                }
            }
        }
    }

    // Métodos auxiliares de lectura limpia
    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!scanner.hasNextLine()) {
            return "";
        }
        return scanner.nextLine().trim();
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            if (!scanner.hasNextLine()) {
                return 0;
            }
            String linea = scanner.nextLine().trim();
            if (linea.isEmpty()) {
                continue;
            }
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Debe ingresar un número entero.");
            }
        }
    }

    private double leerDouble(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                if (!scanner.hasNextLine()) {
                    return 0.0;
                }
                String linea = scanner.nextLine().trim();
                return Double.parseDouble(linea);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Debe ingresar un número válido (ej. 1500.50).");
            }
        }
    }
}