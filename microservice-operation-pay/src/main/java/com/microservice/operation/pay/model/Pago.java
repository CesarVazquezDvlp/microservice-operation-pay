package com.microservice.operation.pay.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection= "pagos")
public class Pago {
	@Id
    private String id;
	private String idCliente;
    private double monto;
    private String concepto;
    private int cantidadProductos;
    private String emisor;
    private String receptor;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaProcesamiento;
    private EstatusPago estatusPago;
    private String folio;
    
    public Pago() {
        this.fechaCreacion = LocalDateTime.now();
        this.estatusPago = EstatusPago.PENDIENTE;
    }
    
    public Pago(PagoRequest pagoRequest, String folio) {
        this.monto = pagoRequest.getMonto();
        this.idCliente = pagoRequest.getIdCliente();
        this.concepto = pagoRequest.getConcepto();
        this.emisor = pagoRequest.getEmisor();
        this.receptor = pagoRequest.getReceptor();
        this.idCliente = pagoRequest.getIdCliente();
        this.cantidadProductos = pagoRequest.getCantidadProductos();
        this.fechaCreacion = pagoRequest.getFechaCreacion();
        this.estatusPago = EstatusPago.PENDIENTE;
        this.folio = folio;
    }
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(String idCliente) {
		this.idCliente = idCliente;
	}
	public double getMonto() {
		return monto;
	}
	public void setMonto(double monto) {
		this.monto = monto;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public int getCantidadProductos() {
		return cantidadProductos;
	}
	public void setCantidadProductos(int cantidadProductos) {
		this.cantidadProductos = cantidadProductos;
	}
	public String getEmisor() {
		return emisor;
	}
	public void setEmisor(String emisor) {
		this.emisor = emisor;
	}
	public String getReceptor() {
		return receptor;
	}
	public void setReceptor(String receptor) {
		this.receptor = receptor;
	}
	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	public LocalDateTime getFechaProcesamiento() {
		return fechaProcesamiento;
	}
	public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) {
		this.fechaProcesamiento = fechaProcesamiento;
	}
	public EstatusPago getEstatusPago() {
        return estatusPago;
    }
    public void setEstatusPago(EstatusPago estatusPago) {
        this.estatusPago = estatusPago;
    }
	public String getFolio() {
		return folio;
	}
	public void setFolio(String folio) {
		this.folio = folio;
	}
}
