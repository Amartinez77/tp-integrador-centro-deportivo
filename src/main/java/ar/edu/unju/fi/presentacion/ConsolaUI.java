package ar.edu.unju.fi.presentacion;

import ar.edu.unju.fi.negocio.modelo.Alquiler;
import ar.edu.unju.fi.negocio.modelo.Factura;
import ar.edu.unju.fi.negocio.modelo.Socio;

public class ConsolaUI {

    // Códigos ANSI estándar
    public static final String RESET = "\u001B[0m";
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    public static final String NEGRITA = "\u001B[1m";
    public static final String TITILAR = "\u001B[5m";

    /**
     * Limpieza universal de pantalla compatible con:
     * - IntelliJ IDEA (botón Run / terminal interna)
     * - VS Code (terminal integrada bash/powershell)
     * - Git Bash / Linux / macOS (soporte ANSI nativo)
     * - Windows CMD tradicional (fallback por scroll)
     */
    public static void limpiarPantalla() {
        try {
            // 1. Enviamos secuencia ANSI (para terminales reales: Git Bash, VS Code, Linux, macOS)
            System.out.print("\033[H\033[2J\033[3J");
            System.out.flush();

            // 2. Fallback de cortesía: 30 saltos de línea para visores planos tipo Run de IntelliJ
            // En consolas sin emulación ANSI esto simula perfectamente el borrado.
            System.out.print("\n".repeat(30));
            System.out.flush();
        } catch (Exception e) {
            // Protección ante entornos restringidos
            System.out.println("\n".repeat(25));
        }
    }

    public static void imprimirFichaSocio(Socio socio, boolean cuotasAlDia) {
        String estadoCuota = cuotasAlDia
                ? VERDE + NEGRITA + "[ AL DÍA ]" + RESET
                : ROJO + NEGRITA + TITILAR + "[ ADEUDA CUOTAS ]" + RESET;

        String estadoSocio = socio.isActivo()
                ? VERDE + "ACTIVO" + RESET
                : ROJO + "INACTIVO" + RESET;

        System.out.println(CYAN + "┌────────────────────────────────────────────────────────┐" + RESET);
        System.out.printf(CYAN + "│" + RESET + " %-20s : %-31s " + CYAN + "│\n" + RESET, "DNI", socio.getDni());
        System.out.printf(CYAN + "│" + RESET + " %-20s : %-31s " + CYAN + "│\n" + RESET, "Nombre Completo", socio.getApellido() + ", " + socio.getNombre());
        System.out.printf(CYAN + "│" + RESET + " %-20s : %-31s " + CYAN + "│\n" + RESET, "Categoría", socio.getClass().getSimpleName().replace("Socio", "Socio "));
        System.out.printf(CYAN + "│" + RESET + " %-20s : %-40s " + CYAN + "│\n" + RESET, "Estado Membresía", estadoSocio);
        System.out.printf(CYAN + "│" + RESET + " %-20s : $%-30.2f " + CYAN + "│\n" + RESET, "Cuota Mensual", socio.calcularCuotaMensual());
        System.out.printf(CYAN + "│" + RESET + " %-20s : %-49s " + CYAN + "│\n" + RESET, "Situación Fiscal", estadoCuota);
        System.out.println(CYAN + "└────────────────────────────────────────────────────────┘" + RESET);
    }

    public static void imprimirFichaFactura(Factura fac) {
        System.out.println(AMARILLO + "┌────────────────────────────────────────────────────────┐" + RESET);
        System.out.printf(AMARILLO + "│" + RESET + " %-18s : %-33s " + AMARILLO + "│\n" + RESET, "Factura N°", fac.getNumeroFactura());
        System.out.printf(AMARILLO + "│" + RESET + " %-18s : %-33s " + AMARILLO + "│\n" + RESET, "DNI Socio", fac.getDniSocio());
        System.out.printf(AMARILLO + "│" + RESET + " %-18s : %-33s " + AMARILLO + "│\n" + RESET, "Periodo Fiscal", fac.getPeriodoFiscal());
        System.out.printf(AMARILLO + "│" + RESET + " %-18s : %-33s " + AMARILLO + "│\n" + RESET, "Concepto", fac.getConcepto());
        System.out.printf(AMARILLO + "│" + RESET + " %-18s : $%-32.2f " + AMARILLO + "│\n" + RESET, "Monto Abonado", fac.getMontoTotal());
        System.out.println(AMARILLO + "└────────────────────────────────────────────────────────┘" + RESET);
    }

    public static void imprimirFichaAlquiler(Alquiler alq) {
        String estado = alq.isDevuelto()
                ? VERDE + "DEVUELTO" + RESET
                : ROJO + NEGRITA + "EN PRÉSTAMO / ACTIVO" + RESET;

        System.out.println(AZUL + "┌────────────────────────────────────────────────────────┐" + RESET);
        System.out.printf(AZUL + "│" + RESET + " %-18s : %-33s " + AZUL + "│\n" + RESET, "ID Alquiler", alq.getIdAlquiler());
        System.out.printf(AZUL + "│" + RESET + " %-18s : %-33s " + AZUL + "│\n" + RESET, "Socio DNI", alq.getSocio().getDni() + " (" + alq.getSocio().getApellido() + ")");
        System.out.printf(AZUL + "│" + RESET + " %-18s : %-33s " + AZUL + "│\n" + RESET, "Equipo Código", alq.getEquipamiento().getCodigo() + " (" + alq.getEquipamiento().getNombre() + ")");
        System.out.printf(AZUL + "│" + RESET + " %-18s : %-33s " + AZUL + "│\n" + RESET, "Fecha Préstamo", alq.getFechaAlquiler());
        System.out.printf(AZUL + "│" + RESET + " %-18s : %-33s " + AZUL + "│\n" + RESET, "Límite Devolución", alq.getFechaLimiteDevolucion());
        System.out.printf(AZUL + "│" + RESET + " %-18s : %-42s " + AZUL + "│\n" + RESET, "Estado", estado);
        if (alq.isDevuelto()) {
            System.out.printf(AZUL + "│" + RESET + " %-18s : $%-32.2f " + AZUL + "│\n" + RESET, "Mora Recargada", alq.getRecargoPorMora());
        }
        System.out.println(AZUL + "└────────────────────────────────────────────────────────┘" + RESET);
    }
}