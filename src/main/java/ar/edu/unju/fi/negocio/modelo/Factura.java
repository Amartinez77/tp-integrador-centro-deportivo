package ar.edu.unju.fi.negocio.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Factura implements Serializable {
    private static final long serialVersionUID = 1L;

    private String numeroFactura;
    private String dniSocio;
    private String periodoFiscal;
    private LocalDate fechaEmision;
    private String concepto;
    private double montoTotal;

    public Factura(String numeroFactura, String dniSocio, String periodoFiscal, String concepto, double montoTotal) {
        this.numeroFactura = numeroFactura;
        this.dniSocio = dniSocio;
        this.periodoFiscal = periodoFiscal;
        this.fechaEmision = LocalDate.now();
        this.concepto = concepto;
        this.montoTotal = montoTotal;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getDniSocio() {
        return dniSocio;
    }

    public void setDniSocio(String dniSocio) {
        this.dniSocio = dniSocio;
    }

    public String getPeriodoFiscal() {
        return periodoFiscal;
    }

    public void setPeriodoFiscal(String periodoFiscal) {
        this.periodoFiscal = periodoFiscal;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    @Override
    public String toString() {
        return "Factura{" +
                "numeroFactura='" + numeroFactura + '\'' +
                ", dniSocio='" + dniSocio + '\'' +
                ", periodoFiscal='" + periodoFiscal + '\'' +
                ", fechaEmision=" + fechaEmision +
                ", concepto='" + concepto + '\'' +
                ", montoTotal=" + montoTotal +
                '}';
    }
}
