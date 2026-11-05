package ar.edu.unju.fi.presentacion;

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
     * Limpia la terminal de forma universal y segura.
     * Compatible con Git Bash (MinTTY), Linux, macOS, PowerShell e IntelliJ.
     */
    public static void limpiarPantalla() {
        try {
            // Secuencia ANSI extendida:
            // \033[H  -> Mueve cursor a la esquina superior izquierda (0,0)
            // \033[2J -> Limpia toda la pantalla visible
            // \033[3J -> Borra todo el historial de scrollback (evita que el texto quede arriba)
            System.out.print("\033[H\033[2J\033[3J");
            System.out.flush();
        } catch (Exception e) {
            // Fallback seguro: salto de líneas para separar vistas
            System.out.println("\n".repeat(40));
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
}