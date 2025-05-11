package com.microservice.operation.pay.response;

import java.util.List;
import java.util.UUID;

public class ServerError {
	private String codigo;
    private String mensaje;
    private String folio;
    private String info;
    private List<String> detalles;

    public ServerError(String mensaje, List<String> detalles) {
        this.codigo = "500.banco-base-operaciones-pagos.0001";
        this.mensaje = "Problemas al procesar su solicitud, favor de contactar a su administrador.";
        this.folio = UUID.randomUUID().toString().replace("-", "");
        this.info = "https://banco.base.mx/info#500.banco-base-operaciones-pagos.0001";
        this.detalles = detalles;
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

	public List<String> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<String> detalles) {
		this.detalles = detalles;
	}
}
