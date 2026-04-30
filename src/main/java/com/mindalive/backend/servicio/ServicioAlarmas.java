package com.mindalive.backend.servicio;

import com.mindalive.backend.modelo.Alarma;
import com.mindalive.backend.repositorio.AlarmaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioAlarmas {

    @Autowired
    private AlarmaRepositorio alarmaRepositorio;

    // Crear alarma
    public Alarma crearAlarma(Alarma alarma) {
        alarma.setFechaCreacion(LocalDateTime.now());
        alarma.setConfirmada(false);
        alarma.setActiva(true);
        return alarmaRepositorio.save(alarma);
    }

    // Obtener todas las alarmas activas de un mayor
    public List<Alarma> obtenerAlarmasActivas(Long mayorId) {
        return alarmaRepositorio.findByMayorIdAndActivaTrue(mayorId);
    }

    // Obtener alarmas por tipo
    public List<Alarma> obtenerAlarmasPorTipo(Long mayorId, String tipo) {
        return alarmaRepositorio.findByMayorIdAndTipoAndActivaTrue(mayorId, tipo);
    }

    // Obtener alarmas del día de hoy
    public List<Alarma> obtenerAlarmasHoy(Long mayorId) {
        LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finDia = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return alarmaRepositorio.findByMayorIdAndFechaHoraBetweenAndActivaTrue(mayorId, inicioDia, finDia);
    }

    // Actualizar alarma
    public Optional<Alarma> actualizarAlarma(Long id, Alarma alarmaActualizada) {
        return alarmaRepositorio.findById(id).map(alarma -> {
            alarma.setTitulo(alarmaActualizada.getTitulo());
            alarma.setDescripcion(alarmaActualizada.getDescripcion());
            alarma.setFechaHora(alarmaActualizada.getFechaHora());
            alarma.setTipo(alarmaActualizada.getTipo());
            alarma.setRepeticion(alarmaActualizada.getRepeticion());
            alarma.setActiva(alarmaActualizada.isActiva());
            return alarmaRepositorio.save(alarma);
        });
    }

    // Confirmar alarma (el mayor pulsa "Tomado")
    public Optional<Alarma> confirmarAlarma(Long id) {
        return alarmaRepositorio.findById(id).map(alarma -> {
            alarma.setConfirmada(true);
            return alarmaRepositorio.save(alarma);
        });
    }

    // Desactivar alarma (borrado lógico)
    public Optional<Alarma> desactivarAlarma(Long id) {
        return alarmaRepositorio.findById(id).map(alarma -> {
            alarma.setActiva(false);
            return alarmaRepositorio.save(alarma);
        });
    }

    // Eliminar alarma (borrado físico)
    public boolean eliminarAlarma(Long id) {
        if (alarmaRepositorio.existsById(id)) {
            alarmaRepositorio.deleteById(id);
            return true;
        }
        return false;
    }
}