# Microservicio de Pagos
Este proyecto es un microservicio de Spring Boot que gestiona el procesamiento y el seguimiento del estado de los pagos. 
Utiliza RabbitMQ para la comunicación asíncrona con otros servicios.

# Funcionalidades
Registro de Pagos: Recibe solicitudes de pago y las persiste en la base de datos.
Consulta de Estado: Permite consultar el estado de un pago específico mediante su folio.
Actualización de Estado: Permite actualizar el estado de un pago existente.
Comunicación Asíncrona: Envía eventos a través de RabbitMQ cuando el estado de un pago cambia.

# Tecnologías Utilizadas
Java 17
Spring Boot
Spring Data JPA
MongoDB
RabbitMQ
Gradle
Docker

# Comentarios
Opté por dejar toda la lógica en el controlador debido al alcance limitado del proyecto. 
Si bien es una mejor práctica ubicar esta lógica en un package/clases "service", en este caso prioricé optimizar los tiempos de desarrollo, considerando la simplicidad del escenario.
