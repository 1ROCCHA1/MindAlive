package com.mindalive.backend.servicio;

import com.mindalive.backend.modelo.RegistroEjercicio;
import com.mindalive.backend.repositorio.RegistroEjercicioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ServicioJuegos {

    @Autowired
    private RegistroEjercicioRepositorio registroEjercicioRepositorio;

    public int obtenerNivel(Long mayorId, String tipoJuego) {
        List<RegistroEjercicio> registros = registroEjercicioRepositorio
                .findByMayorIdAndTipoJuegoOrderByFechaDesc(mayorId, tipoJuego);

        if (registros.isEmpty()) return 1;

        return registros.get(0).getNivel();
    }

    private int calcularNivelSiguiente(Long mayorId, String tipoJuego, int nivelActual, boolean correcto) {
        List<RegistroEjercicio> registros = registroEjercicioRepositorio
                .findByMayorIdAndTipoJuegoOrderByFechaDesc(mayorId, tipoJuego);

        List<RegistroEjercicio> ultimasEnNivel = new ArrayList<>();
        for (RegistroEjercicio r : registros) {
            if (r.getNivel() == nivelActual) {
                ultimasEnNivel.add(r);
            }
            if (ultimasEnNivel.size() == 3) break;
        }

        if (correcto && ultimasEnNivel.size() >= 2) {
            long victoriasRecientes = ultimasEnNivel.stream()
                    .limit(2)
                    .filter(r -> r.getAciertos() > 0 && r.getErrores() == 0)
                    .count();
            if (victoriasRecientes == 2 && nivelActual < 6) return nivelActual + 1;
        }

        if (!correcto && ultimasEnNivel.size() >= 2) {
            long derrotasRecientes = ultimasEnNivel.stream()
                    .limit(2)
                    .filter(r -> r.getErrores() > 0)
                    .count();
            if (derrotasRecientes == 2 && nivelActual > 1) return nivelActual - 1;
        }

        return nivelActual;
    }

    public Map<String, Object> generarSimon(Long mayorId) {
        int nivel = obtenerNivel(mayorId, "SIMON");

        List<String> coloresDisponibles;
        if (nivel <= 2) {
            coloresDisponibles = Arrays.asList("ROJO", "AZUL", "VERDE", "AMARILLO");
        } else if (nivel <= 4) {
            coloresDisponibles = Arrays.asList("ROJO", "AZUL", "VERDE", "AMARILLO", "NARANJA");
        } else {
            coloresDisponibles = Arrays.asList("ROJO", "AZUL", "VERDE", "AMARILLO", "NARANJA", "MORADO");
        }

        int longitudSecuencia = nivel + 2;

        int velocidad;
        if (nivel == 1) velocidad = 2000;
        else if (nivel == 2) velocidad = 1800;
        else if (nivel == 3) velocidad = 1300;
        else if (nivel == 4) velocidad = 1000;
        else if (nivel == 5) velocidad = 800;
        else velocidad = 1100;

        Random random = new Random();
        List<String> secuencia = new ArrayList<>();
        for (int i = 0; i < longitudSecuencia; i++) {
            secuencia.add(coloresDisponibles.get(random.nextInt(coloresDisponibles.size())));
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("nivel", nivel);
        resultado.put("secuencia", secuencia);
        resultado.put("velocidad", velocidad);
        resultado.put("coloresDisponibles", coloresDisponibles);

        return resultado;
    }

    public Map<String, Object> validarSimon(Long mayorId, List<String> secuenciaCorrecta,
                                            List<String> respuestaUsuario, double tiempoMedio) {
        int nivel = obtenerNivel(mayorId, "SIMON");

        boolean correcto = secuenciaCorrecta.equals(respuestaUsuario);
        int aciertos = correcto ? secuenciaCorrecta.size() : 0;

        if (!correcto) {
            for (int i = 0; i < Math.min(secuenciaCorrecta.size(), respuestaUsuario.size()); i++) {
                if (secuenciaCorrecta.get(i).equals(respuestaUsuario.get(i))) {
                    aciertos++;
                } else {
                    break;
                }
            }
        }

        int errores = secuenciaCorrecta.size() - aciertos;

        RegistroEjercicio registro = new RegistroEjercicio();
        registro.setMayorId(mayorId);
        registro.setTipoJuego("SIMON");
        registro.setNivel(nivel);
        registro.setAciertos(aciertos);
        registro.setErrores(errores);
        registro.setTiempoMedioRespuesta(tiempoMedio);
        registro.setFecha(LocalDateTime.now());
        registro.setSubioPorNivel(false);
        registro.setBajoPorNivel(false);
        registroEjercicioRepositorio.save(registro);

        int nivelNuevo = calcularNivelSiguiente(mayorId, "SIMON", nivel, correcto);
        registro.setSubioPorNivel(nivelNuevo > nivel);
        registro.setBajoPorNivel(nivelNuevo < nivel);
        registro.setNivel(nivelNuevo);
        registroEjercicioRepositorio.save(registro);

        String mensaje;
        if (correcto && nivelNuevo > nivel) {
            mensaje = "¡Excelente! Subiste al nivel " + nivelNuevo;
        } else if (!correcto && nivelNuevo < nivel) {
            mensaje = "Sin presión, bajamos al nivel " + nivelNuevo;
        } else if (correcto) {
            mensaje = "¡Muy bien! Sigue así";
        } else {
            mensaje = "No pasa nada, inténtalo de nuevo";
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("correcto", correcto);
        resultado.put("aciertos", aciertos);
        resultado.put("errores", errores);
        resultado.put("nivelAnterior", nivel);
        resultado.put("nivelNuevo", nivelNuevo);
        resultado.put("mensaje", mensaje);

        return resultado;
    }
}