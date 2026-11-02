package ar.edu.unju.fi.negocio.servicios;

import ar.edu.unju.fi.datos.AccesoDatos;
import ar.edu.unju.fi.datos.excepciones.PersistenciaException;
import ar.edu.unju.fi.negocio.excepciones.DatoInvalidoException;
import ar.edu.unju.fi.negocio.modelo.Factura;
import ar.edu.unju.fi.negocio.modelo.Socio;

import java.util.*;

public class FacturaService {
    private final Map<String, Boolean> estadoCuotas;
    private List<Factura> facturasEmitidas;
    private final AccesoDatos accesoDatos;

    public FacturaService(AccesoDatos accesoDatos) {
        if (accesoDatos == null) {
            throw new IllegalArgumentException("AccesoDatos no puede ser nulo.");
        }
        this.accesoDatos = accesoDatos;
        this.estadoCuotas = new HashMap<>();
        this.facturasEmitidas = new ArrayList<>();
    }

    // metodos del Fix de ControlAcceso
    public void registrarEstadoCuotas(String dni, boolean alDia) {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI no puede ser nulo ni vacio");
        }
        estadoCuotas.put(dni.trim(), alDia);
    }

    public boolean tieneCuotasAlDia(String dni) {
        if (dni == null || dni.isBlank()) return false;
        return Boolean.TRUE.equals(estadoCuotas.get(dni.trim()));
    }

    // metodos TS3-01

    public Factura emitirFacturaCuota(String numeroFactura, Socio socio, String periodoFiscal) throws DatoInvalidoException {
        if (socio == null) {
            throw new DatoInvalidoException("El Socio no puede ser Nulo");
        }
        if (numeroFactura == null || numeroFactura.isBlank()) {
            throw new DatoInvalidoException("El numero de factura es obligatoria");
        }
        double montoCuota = socio.calcularCuotaMensual();
        String concepto = "Cuota Social Mensual - Periodo " + periodoFiscal;

        Factura factura = new Factura(numeroFactura, socio.getDni(), periodoFiscal, concepto, montoCuota);

        socio.agregarFactura(factura);
        facturasEmitidas.add(factura);

        registrarEstadoCuotas(socio.getDni(), true);
        return factura;
    }
    public List<Factura> obtenerTodasLasFacturas(){
        return Collections.unmodifiableList(facturasEmitidas);
    }

    // persistencia
    public void cargarFacturas(Map<String, Socio> mapaSocios) throws PersistenciaException{
        this.facturasEmitidas=accesoDatos.leer(AccesoDatos.ARCHIVO_FACTURAS);
        for(Factura fac : facturasEmitidas){
            Socio soc = mapaSocios.get(fac.getDniSocio());
            if(soc != null){
                soc.agregarFactura(fac);
                registrarEstadoCuotas(soc.getDni(), true);
            }
        }
    }
    public void guardarFacturas() throws PersistenciaException{
        accesoDatos.guardar(AccesoDatos.ARCHIVO_FACTURAS, facturasEmitidas);
    }

}
