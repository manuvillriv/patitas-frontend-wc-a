package pe.edu.cibertec.patitas_frontend_wc_a.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.patitas_frontend_wc_a.client.AutenticacionClient;
import pe.edu.cibertec.patitas_frontend_wc_a.dto.LogoutRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc_a.dto.LogoutResponseDTO;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/logout")
@CrossOrigin("http://localhost:5173/")
public class LogoutControllerFeign {

    @Autowired
    AutenticacionClient autenticacionClient;

    @PostMapping("/salir-feign")
    public ResponseEntity<LogoutResponseDTO> salir(@RequestBody LogoutRequestDTO logoutRequestDTO) {

        System.out.println("Consumiendo con Feign!!");

        try {
            // Consumir servicio de autenticación con Feign Client
            ResponseEntity<LogoutResponseDTO> responseEntity = Mono.just(autenticacionClient.logout(logoutRequestDTO)).block();

            // Validar respuesta del servicio
            if (responseEntity.getStatusCode().is2xxSuccessful()) {

                LogoutResponseDTO logoutResponseDTO = responseEntity.getBody();

                if (logoutResponseDTO.codigo().equals("00")) {
                    return ResponseEntity.ok(logoutResponseDTO);
                } else {
                    return ResponseEntity.status(400).body(new LogoutResponseDTO("02", "Error: No se pudo cerrar la sesión"));
                }
            } else {
                return ResponseEntity.status(500).body(new LogoutResponseDTO("99", "Error: Ocurrió un problema http"));
            }
        } catch (Exception e) {

            System.out.println("Exception: " + e.getMessage());
            return ResponseEntity.status(500).body(new LogoutResponseDTO("99", "Error: Ocurrió un problema"));

        }

    }

}
