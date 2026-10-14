package ar.edu.unju.fi.logica.modelo;

public class SocioEstandar extends Socio {
    private static final long serialVersionUID = 1L;

    public SocioEstandar(String dni, String nombre, String apellido, double cuotaBase) {
        super(dni, nombre, apellido, cuotaBase);
    }

    @Override
    public double calcularCoutaMensual() {
        return getCuotaBase();
    }


}
