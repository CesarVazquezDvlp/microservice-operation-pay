package com.microservice.operation.pay.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.operation.pay.rabbit.RabbitMQMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;

import com.microservice.operation.pay.model.EstatusPago;
import com.microservice.operation.pay.model.Pago;
import com.microservice.operation.pay.model.PagoRequest;
import com.microservice.operation.pay.repository.PagoRepository;
import com.microservice.operation.pay.response.Succes;
import com.microservice.operation.pay.response.BadRequest;
import com.microservice.operation.pay.response.ServerError;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pagos")
public class PagosController {
	private static final Logger logger = LoggerFactory.getLogger(PagosController.class);
    private static final String SERVICE_NAME = "PagosService";
    private static final String METHOD_NAME_RECIBIR_PAGO = "recibirPago";
    private static final String METHOD_NAME_CONSULTAR_ESTATUS = "consultarEstatusPago";
    private static final String METHOD_NAME_CAMBIAR_ESTATUS = "actualizaEstatus";
        
    private final PagoRepository pagoRepository;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    @Autowired
    public PagosController(PagoRepository pagoRepository, RabbitMQMessageProducer rabbitMQMessageProducer) {
        this.pagoRepository = pagoRepository;
        this.rabbitMQMessageProducer = rabbitMQMessageProducer;
    }
    
	@PostMapping
	public ResponseEntity<?> recibirPago(@Valid @RequestBody PagoRequest pagoRequest, BindingResult result){
		String trace = UUID.randomUUID().toString().replace("-", "");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        logger.info("({}) {} - {} - Trace: {}. Recibiendo solicitud de pago: {}", folio(trace), SERVICE_NAME, METHOD_NAME_RECIBIR_PAGO, convertToLoggableString(pagoRequest));

        try {
            if (result.hasErrors()) {
                stopWatch.stop();
                FieldError firstError = result.getFieldErrors().get(0);
                String campoFaltante = firstError.getField();
                String mensajeError = firstError.getDefaultMessage();
                logger.warn("({}) {} - {} - Trace: {}. Error de validación: Campo '{}' - {}", folio(trace), SERVICE_NAME, METHOD_NAME_RECIBIR_PAGO, campoFaltante, mensajeError);
                return construirRespuestaBadRequest(mensajeError, folio(trace), String.format("El campo '%s' es inválido o falta.", campoFaltante), trace);
            }

            Pago pago = new Pago(pagoRequest, folio(trace));
            pagoRepository.save(pago);

            stopWatch.stop();
            long processingTime = stopWatch.getTotalTimeMillis();
            logger.info("({}) {} - {} - Trace: {}. Solicitud de pago procesada exitosamente en {} ms.", folio(trace), SERVICE_NAME, METHOD_NAME_RECIBIR_PAGO, processingTime);

            Map<String, String> resultadoExitoso = new HashMap<>();
            resultadoExitoso.put("estatus", "ok");
            return construirRespuestaSucces("Operación Exitosa.", resultadoExitoso, folio(trace), trace);

        } catch (Exception e) {
            stopWatch.stop();
            logger.error("({}) {} - {} - Trace: {}. Error interno durante el procesamiento: {}", folio(trace), SERVICE_NAME, METHOD_NAME_RECIBIR_PAGO, e.getMessage(), e);
            return handleInternalServerError("Ocurrió un error interno al procesar el pago: " + e.getMessage(), trace);
        }
	}
	
	@GetMapping("/estatus/{folio}")
    public ResponseEntity<?> consultarEstatusPago(@PathVariable("folio") String folio) {
		String trace = UUID.randomUUID().toString().replace("-", "");
	    StopWatch stopWatchGeneral = new StopWatch();
	    stopWatchGeneral.start();
	    logger.info("({}) {} - {} - Trace: {}. Recibida petición para consultar estatus del folio: {}", folio, SERVICE_NAME, METHOD_NAME_CONSULTAR_ESTATUS, trace, folio);
	    
	    try {
	    	ResponseEntity<?> errorResponse = validarFolio(folio, trace);
	        if (errorResponse != null) {
	            stopWatchGeneral.stop();
	            return errorResponse;
	        }
		    
		    logger.info("({}) {} - {} - Trace: {}. Buscando pago con folio: {}", folio, SERVICE_NAME, METHOD_NAME_CONSULTAR_ESTATUS, trace, folio);
		    StopWatch stopWatchConsulta = new StopWatch();
		    stopWatchConsulta.start();
		    Optional<Pago> pagoOptional = pagoRepository.findByFolio(folio);
		    stopWatchConsulta.stop();
		    long tiempoConsulta = stopWatchConsulta.getTotalTimeMillis();
		    logger.info("({}) {} - {} - Trace: {}. Tiempo de consulta a la base de datos: {} ms.", folio, SERVICE_NAME, METHOD_NAME_CONSULTAR_ESTATUS, trace, tiempoConsulta);
	
		    ResponseEntity<?> responseEntity;
		    if (pagoOptional.isPresent()) {
                responseEntity = construirRespuestaSucces("Operación Exitosa.", Map.of("estatusPago", pagoOptional.get().getEstatusPago()), folio, trace);
            } else {
                responseEntity = construirRespuestaNotFound("No se encontró pago con el folio proporcionado.", folio, "No se encontró ningún registro de pago con el folio proporcionado en la base de datos.", trace);
            }
	
		    stopWatchGeneral.stop();
		    long tiempoTotal = stopWatchGeneral.getTotalTimeMillis();
		    logger.info("({}) {} - {} - Trace: {}. Tiempo total de procesamiento de la consulta: {} ms.", folio, SERVICE_NAME, METHOD_NAME_CONSULTAR_ESTATUS, trace, tiempoTotal);
	
		    return responseEntity;
	    } catch (Exception e) {	
	    	stopWatchGeneral.stop();
            logger.error("({}) {} - {} - Trace: {}. Error inesperado al consultar el estatus del folio {}: {}", folio, SERVICE_NAME, METHOD_NAME_CONSULTAR_ESTATUS, trace, folio, e.getMessage(), e);
            return handleInternalServerError("Error inesperado al consultar el estatus del pago.", trace, e.getMessage());
	    }
	}
	
	@PutMapping("/estatus/{folio}")
	public ResponseEntity<?> actualizaEstatus(@PathVariable("folio") String folio, @RequestBody Map<String, String> body) {
		String trace = UUID.randomUUID().toString().replace("-", "");
	    StopWatch stopWatchGeneral = new StopWatch();
	    stopWatchGeneral.start();
	    logger.info("({}) {} - {} - Trace: {}. Recibida petición PUT para cambiar estatus del folio: {}", folio, SERVICE_NAME, METHOD_NAME_CAMBIAR_ESTATUS, trace, folio);

	    try {
            ResponseEntity<?> errorResponse = validarFolio(folio, trace);
	        if (errorResponse != null) {
	            stopWatchGeneral.stop();
	            return errorResponse;
	        }

            if (body == null || !body.containsKey("estatusPago")) {
                stopWatchGeneral.stop();
                return construirRespuestaBadRequest("El cuerpo de la petición debe contener el campo 'estatusPago'.", folio, "Cuerpo de petición incompleto.", trace);
            }

            String nuevoEstatusStr = body.get("estatusPago");
            if (nuevoEstatusStr == null || nuevoEstatusStr.trim().isEmpty()) {
                stopWatchGeneral.stop();
                return construirRespuestaBadRequest("El estatus del pago no puede estar vacío.", folio, "El campo 'estatusPago' no puede estar vacío.", trace);
            }
            
            EstatusPago nuevoEstatus;
            try {
                nuevoEstatus = EstatusPago.fromValor(nuevoEstatusStr);
            } catch (IllegalArgumentException e) {
                stopWatchGeneral.stop();
                StringBuilder validValues = new StringBuilder();
                for (EstatusPago estatus : EstatusPago.values()) {
                    validValues.append(estatus.getValor()).append(", ");
                }
                if (validValues.length() > 0) {
                    validValues.delete(validValues.length() - 2, validValues.length());
                }
                return construirRespuestaBadRequest("Estatus de pago no válido: " + nuevoEstatusStr, folio, "El valor proporcionado para 'estatusPago' no es válido. Debe ser uno de: " + validValues.toString(), trace);
            }
            
            logger.info("({}) {} - {} - Trace: {}. Buscando pago con folio: {} para actualizar estatus a: {}", folio, SERVICE_NAME, METHOD_NAME_CAMBIAR_ESTATUS, trace, folio, nuevoEstatus);
            StopWatch stopWatchConsulta = new StopWatch();
            stopWatchConsulta.start();
            Optional<Pago> pagoOptional = pagoRepository.findByFolio(folio);
            stopWatchConsulta.stop();
            long tiempoConsulta = stopWatchConsulta.getTotalTimeMillis();
            logger.info("({}) {} - {} - Trace: {}. Tiempo de consulta a la base de datos: {} ms.", folio, SERVICE_NAME, METHOD_NAME_CAMBIAR_ESTATUS, trace, tiempoConsulta);
            
            if (pagoOptional.isPresent()) {
                Pago pago = pagoOptional.get();
                EstatusPago estatusAnterior = pago.getEstatusPago();
                pago.setEstatusPago(nuevoEstatus);
                StopWatch stopWatchGuardado = new StopWatch();
                stopWatchGuardado.start();
                pagoRepository.save(pago);
                stopWatchGuardado.stop();
                long tiempoGuardado = stopWatchGuardado.getTotalTimeMillis();
                logger.info("({}) {} - {} - Trace: {}. Pago con folio: {} actualizado. Estatus anterior: {}, Nuevo estatus: {}. Tiempo de guardado: {} ms.", folio, SERVICE_NAME, METHOD_NAME_CAMBIAR_ESTATUS, trace, folio, estatusAnterior, nuevoEstatus, tiempoGuardado);

                Map<String, String> message = new HashMap<>();
                message.put("folio", folio);
                message.put("nuevoEstatus", nuevoEstatus.getValor());

                rabbitMQMessageProducer.sendMessage(message);

                Map<String, String> resultado = new HashMap<>();
                resultado.put("folio", folio);
                resultado.put("estatusPago", nuevoEstatus.getValor());
                return construirRespuestaSucces("Estatus del pago actualizado exitosamente.", resultado, folio, trace);
            } else {
                stopWatchGeneral.stop();
                return construirRespuestaNotFound("No se encontró pago con el folio proporcionado.", folio, "No se encontró ningún registro de pago con el folio proporcionado en la base de datos para actualizar el estatus.", trace);
            }
            
	    }catch (Exception e) {
	    	stopWatchGeneral.stop();
            logger.error("({}) {} - {} - Trace: {}. Error inesperado al cambiar el estatus del folio {}: {}", folio, SERVICE_NAME, METHOD_NAME_CAMBIAR_ESTATUS, trace, folio, e.getMessage(), e);
            return handleInternalServerError("Error inesperado al cambiar el estatus del pago.", trace, e.getMessage());
        }
	}
	
	private String convertToLoggableString(PagoRequest pagoRequest) {
        return String.format("Monto: %s, Emisor: %s, ID Cliente: %s, Cantidad: %s",
                pagoRequest.getMonto(),
                pagoRequest.getEmisor(),
                pagoRequest.getIdCliente(),
                pagoRequest.getCantidadProductos());
    }
	
	private ResponseEntity<ServerError> handleInternalServerError(String errorMessage, String trace, String... details) {
        logger.error("({}) {} - {} - Trace: {}. Error interno del servidor: {}{}", folio(trace), SERVICE_NAME, METHOD_NAME_RECIBIR_PAGO, trace, errorMessage, (details.length > 0 ? " Detalles: " + String.join(", ", details) : ""));
        ServerError respuestaError = new ServerError("Problemas al procesar su solicitud, favor de contactar a su administrador.", details.length > 0 ? List.of(details) : List.of(errorMessage));
        respuestaError.setFolio(trace);
        return new ResponseEntity<>(respuestaError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> validarFolio(String folio, String trace) {
        if (folio == null || folio.trim().isEmpty()) {
            logger.warn("({}) {} - {} - Trace: {}. Validación fallida: El folio está vacío o nulo.", folio, SERVICE_NAME, METHOD_NAME_CONSULTAR_ESTATUS, trace);
            return construirRespuestaBadRequest("El folio no puede estar vacío.", folio, "El parámetro 'folio' no fue proporcionado o está vacío.", trace);
        }

        if (!folio.matches("^[a-zA-Z0-9]+$")) {
            logger.warn("({}) {} - {} - Trace: {}. Validación fallida: El folio '{}' contiene caracteres inválidos.", folio, SERVICE_NAME, METHOD_NAME_CONSULTAR_ESTATUS, trace, folio);
            return construirRespuestaBadRequest("El folio debe ser alfanumérico.", folio, "El parámetro 'folio' contiene caracteres inválidos.", trace);
        }
        return null;
    }

    private ResponseEntity<Succes> construirRespuestaSucces(String mensaje, Map<String, ?> data, String folio, String trace) {
        Succes respuesta = new Succes(mensaje, data);
        respuesta.setFolio(trace);
        logger.info("({}) {} - {} - Trace: {}. Respuesta Succes (200) enviada. Folio: {}, Data: {}", folio, SERVICE_NAME, (data != null && data.containsKey("estatus") ? METHOD_NAME_CONSULTAR_ESTATUS : METHOD_NAME_RECIBIR_PAGO), trace, folio, data);
        logger.debug("({}) {} - {} - Trace: {}. Respuesta Succes (200) enviada: {}", folio, SERVICE_NAME, (data != null && data.containsKey("estatus") ? METHOD_NAME_CONSULTAR_ESTATUS : METHOD_NAME_RECIBIR_PAGO), trace, respuesta);
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    private ResponseEntity<BadRequest> construirRespuestaBadRequest(String mensaje, String folio, String detalle, String trace) {
        BadRequest respuestaError = new BadRequest(mensaje, folio, detalle);
        respuestaError.setFolio(trace);
        logger.warn("({}) {} - {} - Trace: {}. Respuesta BadRequest (400) enviada. Folio: {}, Detalle: {}", folio, SERVICE_NAME, (detalle != null && detalle.contains("folio") ? METHOD_NAME_CONSULTAR_ESTATUS : METHOD_NAME_RECIBIR_PAGO), trace, folio, detalle);
        logger.debug("({}) {} - {} - Trace: {}. Respuesta BadRequest (400) enviada: {}", folio, SERVICE_NAME, (detalle != null && detalle.contains("folio") ? METHOD_NAME_CONSULTAR_ESTATUS : METHOD_NAME_RECIBIR_PAGO), trace, respuestaError);
        return new ResponseEntity<>(respuestaError, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<BadRequest> construirRespuestaNotFound(String mensaje, String folio, String detalle, String trace) {
        BadRequest respuestaError = new BadRequest(mensaje, folio, detalle);
        respuestaError.setFolio(trace);
        logger.warn("({}) {} - {} - Trace: {}. Respuesta NotFound (404) enviada. Folio: {}, Detalle: {}", folio, SERVICE_NAME, METHOD_NAME_CONSULTAR_ESTATUS, trace, folio, detalle);
        logger.debug("({}) {} - {} - Trace: {}. Respuesta BadRequest (404) enviada: {}", folio, SERVICE_NAME, METHOD_NAME_CONSULTAR_ESTATUS, trace, respuestaError);
        return new ResponseEntity<>(respuestaError, HttpStatus.NOT_FOUND);
    }

    private String folio(String trace) {
        return "[" + trace.substring(0, 8) + "]"; 
    }
}
