package co.edu.sena.nikfashion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicacion NikFashion.
 *
 * <p>La anotacion {@code @SpringBootApplication} activa la configuracion
 * automatica de Spring Boot y el escaneo de componentes (controladores,
 * servicios y repositorios) dentro del paquete {@code co.edu.sena.nikfashion}.</p>
 *
 * @author Santiago Torres García
 */
@SpringBootApplication
public class NikFashionApplication {

    /**
     * Punto de entrada: inicia el servidor web embebido y la aplicacion.
     *
     * @param args argumentos de linea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(NikFashionApplication.class, args);
    }
}
