package com.mindalive.backend.servicio;

import com.mindalive.backend.modelo.Usuario;
import com.mindalive.backend.modelo.VinculoFamiliar;
import com.mindalive.backend.repositorio.UsuarioRepositorio;
import com.mindalive.backend.repositorio.VinculoFamiliarRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ServicioRegistro {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private VinculoFamiliarRepositorio vinculoFamiliarRepositorio;

    private BCryptPasswordEncoder codificador = new BCryptPasswordEncoder();

    public Map<String, Object> registrarCuidadorYMayor(
            String nombreCuidador, String emailCuidador, String contrasenaCuidador,
            String nombreMayor) {

        if (usuarioRepositorio.findByEmail(emailCuidador).isPresent()) {
            throw new RuntimeException("Ya existe una cuenta con ese email de cuidador");
        }

        // Creamos el cuidador
        Usuario cuidador = new Usuario();
        cuidador.setNombre(nombreCuidador);
        cuidador.setEmail(emailCuidador);
        cuidador.setContrasena(codificador.encode(contrasenaCuidador));
        cuidador.setRol("CUIDADOR");
        usuarioRepositorio.save(cuidador);

        // Creamos el mayor, solo necesita nombre
        Usuario mayor = new Usuario();
        mayor.setNombre(nombreMayor);
        mayor.setRol("MAYOR");
        usuarioRepositorio.save(mayor);

        // Vinculamos al cuidador con el mayor
        VinculoFamiliar vinculo = new VinculoFamiliar();
        vinculo.setFamiliar(cuidador);
        vinculo.setMayor(mayor);
        vinculo.setPermiso("ESCRITURA");
        vinculoFamiliarRepositorio.save(vinculo);

        return Map.of(
                "cuidadorId", cuidador.getId(),
                "mayorId", mayor.getId(),
                "nombreCuidador", cuidador.getNombre(),
                "nombreMayor", mayor.getNombre(),
                "rol", "CUIDADOR"
        );
    }
}