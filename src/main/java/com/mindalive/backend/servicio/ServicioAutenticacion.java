package com.mindalive.backend.servicio;

import com.mindalive.backend.modelo.Usuario;
import com.mindalive.backend.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServicioAutenticacion {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    // Codificador de contraseñas, nunca guardamos la contraseña en texto plano
    private BCryptPasswordEncoder codificador = new BCryptPasswordEncoder();

    public Usuario registrar(String nombre, String email, String contrasena, String rol) {
        // Comprobamos que no exista ya un usuario con ese email
        if (usuarioRepositorio.findByEmail(email).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setContrasena(codificador.encode(contrasena));
        usuario.setRol(rol);

        return usuarioRepositorio.save(usuario);
    }

    public Usuario login(String email, String contrasena) {
        Optional<Usuario> resultado = usuarioRepositorio.findByEmail(email);

        if (resultado.isEmpty()) {
            throw new RuntimeException("No existe ningún usuario con ese email");
        }

        Usuario usuario = resultado.get();

        if (!codificador.matches(contrasena, usuario.getContrasena())) {
            throw new RuntimeException("La contraseña no es correcta");
        }

        return usuario;
    }
}