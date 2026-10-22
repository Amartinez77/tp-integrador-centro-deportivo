package ar.edu.unju.fi.negocio.modelo;

public class SocioVitalicio extends Socio {
    private static final long serialVersionUID = 1L;
    private double porcentajeDescuento;

    public SocioVitalicio(String dni, String nombre, String apellido, double cuotaBase, double porcentajeDescuento) {
        super(dni, nombre, apellido, cuotaBase);
        this.porcentajeDescuento = porcentajeDescuento;
    }

    @Override
    public double calcularCuotaMensual() {
        return getCuotaBase() * (1.0 - (porcentajeDescuento / 100.0));
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(double porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }
}
