package com.microservice.operation.pay.response;


import java.util.Map;

public class Succes {
	private String mensaje;
    private String folio;
    private Map<String, ?> resultado;
    
    public Succes(String mensaje, Map<String, ?> data) {
        this.mensaje = mensaje;
        this.resultado = data;
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

	public Map<String, ?> getResultado() {
        return resultado;
    }

	public void setResultado(Map<String, String> resultado) {
		this.resultado = resultado;
	}
}
