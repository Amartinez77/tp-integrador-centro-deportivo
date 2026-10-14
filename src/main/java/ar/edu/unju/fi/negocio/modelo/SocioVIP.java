package ar.edu.unju.fi.negocio.modelo;

public class SocioVIP extends Socio {
    private static final long serialVersionUID = 1L;
    private double recargoZonasExclusivas;

    public SocioVIP(String dni, String nombre, String apellido, double cuotaBase, double recargoZonasExclusivas) {
        super(dni, nombre, apellido, cuotaBase);
        this.recargoZonasExclusivas = recargoZonasExclusivas;
    }

    @Override
    public double calcularCoutaMensual() {
        return getCuotaBase() + recargoZonasExclusivas;
    }

    public double getRecargoZonasExclusivas() {
        return recargoZonasExclusivas;
    }

    public void setRecargoZonasExclusivas(double recargoZonasExclusivas) {
        this.recargoZonasExclusivas = recargoZonasExclusivas;
    }
}
