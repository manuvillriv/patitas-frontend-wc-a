package pe.edu.cibertec.patitas_frontend_wc_a.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.cibertec.patitas_frontend_wc_a.dto.LoginRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc_a.dto.LoginResponseDTO;
import pe.edu.cibertec.patitas_frontend_wc_a.dto.LogoutRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc_a.dto.LogoutResponseDTO;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:5173")
public class LoginControllerAsync {

    @Autowired
    WebClient webClientAutenticacion;

    @PostMapping("/autenticar-async")
    public Mono<LoginResponseDTO> autenticar(@RequestBody LoginRequestDTO loginRequestDTO) {

        // Validar campos de entrada
        if (loginRequestDTO.tipoDocumento() == null || loginRequestDTO.tipoDocumento().trim().length() == 0 ||
            loginRequestDTO.numeroDocumento() == null || loginRequestDTO.numeroDocumento().trim().length() == 0 ||
            loginRequestDTO.password() == null || loginRequestDTO.password().trim().length() == 0) {

            return Mono.just(new LoginResponseDTO("01", "Error: Debe completar correctamente sus credenciales", "", ""));

        }

        try {

            // consumir servicios de autenticación (Del Backend)
            return webClientAutenticacion.post()
                    .uri("http://localhost:8081/autenticacion/login")
                    .body(Mono.just(loginRequestDTO), LoginRequestDTO.class)
                    .retrieve()
                    .bodyToMono(LoginResponseDTO.class)
                    .flatMap(response -> {

                        if(response.codigo().equals("00")){
                            return Mono.just(new LoginResponseDTO("00", "", response.nombreUsuario(), ""));
                        } else {
                            return  Mono.just(new LoginResponseDTO("02", "Error: Autenticacion fallida", "", ""));
                        }

                    });

        } catch (Exception e){

            System.out.println(e.getMessage());
            return Mono.just(new LoginResponseDTO("99", "Error: Autenticacion fallida", "", ""));

        }

    }

    @PostMapping("/cerrar-sesion")
    public Mono<LogoutResponseDTO> cerrarSesion(@RequestBody LogoutRequestDTO logoutRequestDTO) {

        // Validar campos de entrada
        if (logoutRequestDTO.tipoDocumento() == null || logoutRequestDTO.tipoDocumento().trim().length() == 0 ||
            logoutRequestDTO.numeroDocumento() == null || logoutRequestDTO.numeroDocumento().trim().length() == 0) {

            return Mono.just(new LogoutResponseDTO("01", "Error: credenciales no encotradas"));

        }

        // Registrar en usuarios.txt
        try {
            // consumir servicios de autenticación (Del Backend)
            return webClientAutenticacion.post()
                    .uri("http://localhost:8081/autenticacion/logout")
                    .body(Mono.just(logoutRequestDTO), LoginRequestDTO.class)
                    .retrieve()
                    .bodyToMono(LoginResponseDTO.class)
                    .flatMap(response -> {

                        if(response.codigo().equals("00")){
                            return Mono.just(new LogoutResponseDTO("00", ""));
                        } else {
                            return  Mono.just(new LogoutResponseDTO("02", "Error: No se pudo cerrar la sesión"));
                        }

                    });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Mono.just(new LogoutResponseDTO("99","Error: Ocurrió un problema"));
        }

    }
}
