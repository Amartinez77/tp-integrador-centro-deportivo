package ar.edu.unju.fi.presentacion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

import ar.edu.unju.fi.datos.excepciones.PersistenciaException;
import ar.edu.unju.fi.negocio.ControlAcceso;
import ar.edu.unju.fi.negocio.Logica;
import ar.edu.unju.fi.negocio.Reportes;
import ar.edu.unju.fi.negocio.excepciones.AccesoDenegadoException;
import ar.edu.unju.fi.negocio.excepciones.DatoInvalidoException;
import ar.edu.unju.fi.negocio.excepciones.NegocioException;
import ar.edu.unju.fi.negocio.excepciones.ReservaDuplicadaException;
import ar.edu.unju.fi.negocio.excepciones.SocioDuplicadoException;
import ar.edu.unju.fi.negocio.modelo.ActividadReporte;
import ar.edu.unju.fi.negocio.modelo.Equipamiento;
import ar.edu.unju.fi.negocio.modelo.Socio;
import ar.edu.unju.fi.negocio.modelo.SocioEstandar;
import ar.edu.unju.fi.negocio.modelo.SocioVIP;
import ar.edu.unju.fi.negocio.modelo.SocioVitalicio;

public class MenuPrincipal {
    private final Logica logica;
    private final ControlAcceso controlAcceso;
    private final Scanner scanner;

    public MenuPrincipal(Logica logica) {
        this.logica = logica;
        this.controlAcceso = new ControlAcceso(logica);
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenu() {
        int opcion = 0;
        do {
            try {
                System.out.println("\n==================================================");
                System.out.println("   CENTRO DE ALTO RENDIMIENTO - MENÚ PRINCIPAL    ");
                System.out.println("==================================================");
                System.out.println("1. Gestión de Socios");
                System.out.println("2. Control de Acceso (Molinete)");
                System.out.println("3. Alquiler de Equipamiento");
                System.out.println("4. Reservas de Actividades");
                System.out.println("5. Facturación y Comprobantes");
                System.out.println("6. Reportes");
                System.out.println("7. Salir");
                System.out.print("Seleccione una opción: ");

                opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        menuSocios();
                        break;
                    case 2:
                        menuMolinete();
                        break;
                    case 3:
                        menuEquipamiento();
                        break;
                    case 4:
                        menuReservas();
                        break;
                    case 5:
                        menuFacturacion();
                        break;
                    case 6:
                        menuReportes();
                        break;

                    case 7:
                        guardarTodo();
                        System.out.println("¡Saliendo del sistema. Hasta luego!");
                        break;
                    default:
                        System.out.println("[Aviso] Opción inválida. Ingrese un número del 1 al 6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[Error] Debe ingresar un número entero válido.");
            } catch (NegocioException | PersistenciaException e) {
                System.out.println("[Aviso del Sistema]: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[Error Inesperado]: " + e.getMessage());
            }
        } while (opcion != 6);
    }

    private void menuSocios() throws PersistenciaException, DatoInvalidoException, SocioDuplicadoException {
        System.out.println("\n--- SUBMENÚ: GESTIÓN DE SOCIOS ---");
        System.out.println("1. Registrar Socio");
        System.out.println("2. Buscar Socio por DNI");
        System.out.println("3. Listar Todos los Socios");
        System.out.print("Elija una opción: ");
        int op = Integer.parseInt(scanner.nextLine());

        if (op == 1) {
            System.out.print("Tipo de Socio (1: Estándar, 2: VIP, 3: Vitalicio): ");
            int tipo = Integer.parseInt(scanner.nextLine());
            System.out.print("DNI: ");
            String dni = scanner.nextLine();
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();
            System.out.print("Apellido: ");
            String apellido = scanner.nextLine();
            System.out.print("Cuota Base: ");
            double cuotaBase = Double.parseDouble(scanner.nextLine());

            Socio socio = null;
            if (tipo == 1) {
                socio = new SocioEstandar(dni, nombre, apellido, cuotaBase);
            } else if (tipo == 2) {
                System.out.print("Cargo Adicional Zona VIP: ");
                double cargo = Double.parseDouble(scanner.nextLine());
                socio = new SocioVIP(dni, nombre, apellido, cuotaBase, cargo);
            } else if (tipo == 3) {
                System.out.print("Porcentaje de Descuento: ");
                double porcentajeDescuento = Double.parseDouble(scanner.nextLine());
                socio = new SocioVitalicio(dni, nombre, apellido, cuotaBase, porcentajeDescuento);
            }

            if (socio != null) {
                logica.registrarSocio(socio);
                logica.guardarSocios();
                System.out.println("¡Socio registrado y guardado exitosamente!");
            }
        } else if (op == 2) {
            System.out.print("Ingrese DNI a buscar: ");
            String dni = scanner.nextLine();
            Socio s = logica.buscarSocio(dni);
            if (s != null) {
                System.out.println("\n[Perfil de Socio Encontrado]");
                System.out.println("DNI: " + s.getDni() + " | Nombre: " + s.getNombre() + " " + s.getApellido());
                System.out.println("Cuota Mensual Polimórfica: $" + s.calcularCuotaMensual());
            } else {
                System.out.println("No se encontró ningún socio con el DNI ingresado.");
            }
        } else if (op == 3) {
            System.out.println("\n--- LISTADO GENERAL DE SOCIOS ---");
            for (Socio s : logica.obtenerTodosLosSocios()) {
                System.out.println("- " + s + " | Cuota: $" + s.calcularCuotaMensual());
            }
        }
    }

    private void menuMolinete() {
        System.out.println("\n--- CONTROL DE ACCESO (MOLINETE) ---");
        System.out.print("Ingrese DNI del socio que intenta ingresar: ");
        String dni = scanner.nextLine();

        try {
            controlAcceso.validarAcceso(dni);
            Socio s = logica.buscarSocio(dni);
            System.out.println("\n========================================");
            System.out.println("   [ACCESO AUTORIZADO - BIENVENIDO/A]   ");
            System.out.println("   Socio: " + s.getNombre() + " " + s.getApellido());
            System.out.println("========================================");
        } catch (AccesoDenegadoException e) {
            System.out.println("\n****************************************");
            System.out.println("   [ACCESO DENEGADO]                    ");
            System.out.println("   Motivo: " + e.getMessage());
            System.out.println("****************************************");
        }
    }

    private void menuEquipamiento() throws PersistenciaException, DatoInvalidoException {
        System.out.println("\n--- SUBMENÚ: EQUIPAMIENTO Y ALQUILERES ---");
        System.out.println("1. Registrar Equipamiento");
        System.out.println("2. Alquilar Equipamiento");
        System.out.println("3. Devolver Equipamiento");
        System.out.println("4. Listar Equipamiento");
        System.out.print("Elija una opción: ");
        int op = Integer.parseInt(scanner.nextLine());

        if (op == 1) {
            System.out.print("Código de equipamiento: ");
            String cod = scanner.nextLine();
            System.out.print("Nombre: ");
            String nom = scanner.nextLine();
            System.out.print("Costo de alquiler diario: ");
            double costo = Double.parseDouble(scanner.nextLine());

            Equipamiento eq = new Equipamiento(cod, nom, costo);
            logica.registrarEquipamiento(eq);
            logica.guardarEquipamientos();
            System.out.println("¡Equipamiento registrado con éxito!");
        } else if (op == 2) {
            System.out.print("ID de Alquiler: ");
            String idAlq = scanner.nextLine();
            System.out.print("DNI del Socio: ");
            String dni = scanner.nextLine();
            System.out.print("Código de Equipamiento: ");
            String codEq = scanner.nextLine();
            System.out.print("Días de préstamo: ");
            int dias = Integer.parseInt(scanner.nextLine());

            logica.alquilarEquipamiento(idAlq, dni, codEq, dias);
            logica.guardarAlquileres();
            System.out.println("¡Alquiler procesado con éxito!");
        } else if (op == 3) {
            System.out.print("ID de Alquiler a devolver: ");
            String idAlq = scanner.nextLine();
            double mora = logica.devolverEquipamiento(idAlq, LocalDate.now(), 150.0);
            logica.guardarAlquileres();
            System.out.println("¡Devolución registrada! Recargo por mora aplicado: $" + mora);
        } else if (op == 4) {
            System.out.println("\n--- LISTADO DE EQUIPAMIENTO ---");
            for (Equipamiento eq : logica.obtenerTodosLosEquipamientos()) {
                System.out.println(eq);
            }
        }
    }

    private void menuReservas() throws PersistenciaException, DatoInvalidoException, ReservaDuplicadaException {
        System.out.println("\n--- SUBMENÚ: RESERVA DE ACTIVIDADES ---");
        System.out.print("ID de Reserva: ");
        String idRes = scanner.nextLine();
        System.out.print("Código de Actividad (ej. TENIS, NATACION): ");
        String codAct = scanner.nextLine();
        System.out.print("DNI del Socio: ");
        String dni = scanner.nextLine();

        logica.registrarReserva(idRes, codAct, dni, LocalDateTime.now().plusDays(1));
        logica.guardarReservas();
        System.out.println("¡Reserva inscripta sin duplicados exitosamente!");
    }

    private void menuFacturacion() {
        System.out.println("\n--- SUBMENÚ: FACTURACIÓN Y COMPROBANTES ---");
        System.out.println("1. Registrar Estado de Cuotas de un Socio");
        System.out.println("2. Imprimir Comprobante de Cuota / Factura");
        System.out.print("Elija una opción: ");
        int op = Integer.parseInt(scanner.nextLine());

        if (op == 1) {
            System.out.print("Ingrese DNI del socio: ");
            String dni = scanner.nextLine();
            System.out.print("¿Tiene cuotas al día? (true/false): ");
            boolean alDia = Boolean.parseBoolean(scanner.nextLine());
            logica.registrarEstadoCuotas(dni, alDia);
            System.out.println("¡Estado de cuotas actualizado correctamente!");
        } else if (op == 2) {
            System.out.print("Ingrese DNI del socio para emitir comprobante: ");
            String dni = scanner.nextLine();
            Socio s = logica.buscarSocio(dni);
            if (s != null) {
                System.out.println("\n==================================================");
                System.out.println("      CENTRO DE ALTO RENDIMIENTO - TICKET         ");
                System.out.println("==================================================");
                System.out.println(" Fecha de Emisión: " + LocalDate.now());
                System.out.println(" Socio: " + s.getApellido() + ", " + s.getNombre());
                System.out.println(" DNI: " + s.getDni());
                System.out.println(" Tipo de Membresía: " + s.getClass().getSimpleName());
                System.out.println(" Cuota Base: $" + s.getCuotaBase());
                System.out.println(" ------------------------------------------------");
                System.out.println(" TOTAL A PAGAR (CON APLICACIÓN POLIMÓRFICA): $" + s.calcularCuotaMensual());
                System.out.println("==================================================");
            } else {
                System.out.println("No se encontró al socio para emitir el comprobante.");
            }
        }
    }

    private void menuReportes() {

        Reportes reportes = new Reportes();

        System.out.println("\n--- MÓDULO DE REPORTES ---");
        System.out.println("1. Socios por apellido y nombre");
        System.out.println("2. Socios por cuota mensual");
        System.out.println("3. Actividades con más reservas");
        System.out.println("4. Buscar socio por DNI");
        System.out.print("Seleccione una opción: ");

        int opcion = Integer.parseInt(scanner.nextLine());

        switch (opcion) {

            case 1:
                for (Socio socio : reportes.ordenarSociosPorApellido(
                        logica.obtenerTodosLosSocios())) {

                    System.out.println(socio.getApellido() + ", "
                            + socio.getNombre());
                }
                break;

            case 2:
                for (Socio socio : reportes.ordenarSociosPorCuota(
                        logica.obtenerTodosLosSocios())) {

                    System.out.println(socio.getApellido() + ", "
                            + socio.getNombre()
                            + " | Cuota: $"
                            + socio.calcularCuotaMensual());
                }
                break;

            case 3:
                for (ActividadReporte actividad : reportes.ordenarActividades(
                        logica.obtenerTodasLasReservasPorActividad())) {

                    System.out.println(actividad);
                }
                break;

            case 4:
                System.out.print("Ingrese DNI: ");
                String dni = scanner.nextLine();

                Socio encontrado = reportes.buscarSocioPorDni(
                        logica.obtenerTodosLosSocios(), dni);

                if (encontrado != null) {
                    System.out.println("Socio encontrado: "
                            + encontrado.getApellido() + ", "
                            + encontrado.getNombre());
                } else {
                    System.out.println("No se encontró el socio.");
                }
                break;

            default:
                System.out.println("Opción inválida.");
        }
    }

    private void guardarTodo() {
        try {
            logica.guardarSocios();
            logica.guardarEquipamientos();
            logica.guardarReservas();
        } catch (PersistenciaException e) {
            System.out.println("[Advertencia de guardado]: " + e.getMessage());
        }
    }
}