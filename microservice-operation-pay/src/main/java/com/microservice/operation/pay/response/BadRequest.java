package com.microservice.operation.pay.response;


import java.util.UUID;

public class BadRequest {
	private String codigo;
    private String mensaje;
    private String folio;
    private String detalle;
    private String info;

    public BadRequest(String mensaje, String folio, String detalle) {
        this.codigo = "400.banco-base-operaciones-pagos.4000"; 
        this.mensaje = mensaje;
        this.folio = folio;
        this.detalle = detalle;
        this.info = "https://banco.base.mx/info#400.banco-base-operaciones-pagos.4000";
    }

    public BadRequest(String campoFaltante, String folio) {
        this.codigo = "400.banco-base-operaciones-pagos.4004";
        this.mensaje = String.format("Falta campo obligatorio '%s'", campoFaltante);
        this.folio = folio;
        this.detalle = String.format("El campo '%s' es obligatorio y no fue proporcionado.", campoFaltante);
        this.info = "https://banco.base.mx/info#400.banco-base-operaciones-pagos.4004"; 
    }

    public BadRequest(String mensaje, String folio, boolean folioNoEncontrado) {
        this.codigo = "400.banco-base-operaciones-pagos.4006";
        this.mensaje = mensaje;
        this.folio = folio;
        this.detalle = mensaje;
        this.info = "https://banco.base.mx/info#400.banco-base-operaciones-pagos.4006";
    }

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getFolio() {
		return folio;
	}

	public void setFolio(String folio) {
		this.folio = folio;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}
}
