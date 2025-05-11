package com.microservice.operation.pay.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class PagoRequest {
	@NotBlank(message = "El ID del cliente es obligatorio")
	private String idCliente;
	
	private String concepto;
	
	@Min(value = 1, message = "La cantidad de productos debe ser al menos 1")
	private int cantidadProductos;
	
	@NotBlank(message = "El Emisor es obligatorio")
	private String emisor;
	
	@NotBlank(message = "El Receptor es obligatorio")
	private String receptor;
	
	@Min(value = 0, message = "El monto debe ser mayor a 0")
	private Double monto;
	
	private String estatusPago;
	
	private LocalDateTime fechaCreacion = LocalDateTime.now();

	public String getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(String idCliente) {
		this.idCliente = idCliente;
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

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	public String getEstatusPago() {
		return estatusPago;
	}

	public void setEstatusPago(String estatusPago) {
		this.estatusPago = estatusPago;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
}
