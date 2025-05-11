package com.microservice.operation.pay.model;

public enum EstatusPago {
    APLICADO("Aplicado"),
    DEVUELTO("Devuelto"),
    EN_PROCESO_DE_DEVOLUCION("En proceso de devolucion"),
    CANCELADO("Cancelado"),
	PENDIENTE("Pendiente");

    private final String valor;

    EstatusPago(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstatusPago fromValor(String valor) {
        for (EstatusPago estatus : values()) {
            if (estatus.valor.equalsIgnoreCase(valor)) {
                return estatus;
            }
        }
        throw new IllegalArgumentException("Estatus de pago no válido: " + valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}