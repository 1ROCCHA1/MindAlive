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
            if (ultimasEnNivel.size() == 4) break;
        }

        if (correcto && ultimasEnNivel.size() >= 1) {
            long victoriasRecientes = ultimasEnNivel.stream()
                    .limit(1)
                    .filter(r -> r.getErrores() == 0)
                    .count();
            if (victoriasRecientes == 1 && nivelActual < 6) return nivelActual + 1;
        }

        if (!correcto && ultimasEnNivel.size() >= 3) {
            long derrotasRecientes = ultimasEnNivel.stream()
                    .limit(3)
                    .filter(r -> r.getErrores() > 0)
                    .count();
            if (derrotasRecientes == 3 && nivelActual > 1) return nivelActual - 1;
        }

        return nivelActual;
    }

    public Map<String, Object> generarSimon(Long mayorId) {
        int nivel = obtenerNivel(mayorId, "SIMON");

        List<String> coloresDisponibles;
        if (nivel <= 2) {
            coloresDisponibles = Arrays.asList("ROJO", "AZUL", "AMARILLO","VERDE" );
        } else if (nivel <= 4) {
            coloresDisponibles = Arrays.asList("ROJO", "AZUL", "AMARILLO","VERDE" , "NARANJA");
        } else {
            coloresDisponibles = Arrays.asList("ROJO", "AZUL", "AMARILLO","VERDE" , "NARANJA", "MORADO");
        }

        int longitudSecuencia = nivel + 2;

        int velocidad;
        if (nivel == 1) velocidad = 1500;
        else if (nivel == 2) velocidad = 1200;
        else if (nivel == 3) velocidad = 1000;
        else if (nivel == 4) velocidad = 800;
        else if (nivel == 5) velocidad = 600;
        else velocidad = 450;

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
        if (correcto && nivelNuevo > nivel) mensaje = "¡Excelente! Subiste al nivel " + nivelNuevo;
        else if (!correcto && nivelNuevo < nivel) mensaje = "Sin presión, bajamos al nivel " + nivelNuevo;
        else if (correcto) mensaje = "¡Muy bien! Sigue así";
        else mensaje = "No pasa nada, inténtalo de nuevo";

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("correcto", correcto);
        resultado.put("aciertos", aciertos);
        resultado.put("errores", errores);
        resultado.put("nivelAnterior", nivel);
        resultado.put("nivelNuevo", nivelNuevo);
        resultado.put("mensaje", mensaje);
        return resultado;
    }

    public Map<String, Object> generarMemory(Long mayorId) {
        int nivel = obtenerNivel(mayorId, "MEMORY");

        int parejas;
        if (nivel == 1) parejas = 4;
        else if (nivel == 2) parejas = 6;
        else if (nivel == 3) parejas = 8;
        else if (nivel == 4) parejas = 10;
        else if (nivel == 5) parejas = 12;
        else parejas = 15;

        int tiempoLimite;
        if (nivel == 1) tiempoLimite = 90;
        else if (nivel == 2) tiempoLimite = 100;
        else if (nivel == 3) tiempoLimite = 110;
        else if (nivel == 4) tiempoLimite = 120;
        else if (nivel == 5) tiempoLimite = 130;
        else tiempoLimite = 150;

        List<String> todasFiguras = Arrays.asList(
                "🌞", "🌙", "⭐", "🌈", "🌊", "🌸",
                "🍎", "🍋", "🍇", "🍓", "🎵", "🏠",
                "🐶", "🐱", "🐦", "🦋", "🌺", "🍀",
                "🎯", "🎲", "🎪", "🎨", "🔵", "🟣"
        );

        List<String> figuras = new ArrayList<>(todasFiguras.subList(0, Math.min(parejas, todasFiguras.size())));
        List<String> tablero = new ArrayList<>();
        for (String figura : figuras) {
            tablero.add(figura);
            tablero.add(figura);
        }
        Collections.shuffle(tablero);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("nivel", nivel);
        resultado.put("tablero", tablero);
        resultado.put("parejas", parejas);
        resultado.put("tiempoLimite", tiempoLimite);
        return resultado;
    }

    public Map<String, Object> validarMemory(Long mayorId, int parejasAcertadas,
                                             int parejasTotal, double tiempoMedio,
                                             int erroresIntermedios, boolean sinTiempo) {
        int nivel = obtenerNivel(mayorId, "MEMORY");

        // Si jugó sin tiempo, no afecta al nivel
        if (sinTiempo) {
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("correcto", true);
            resultado.put("nivelAnterior", nivel);
            resultado.put("nivelNuevo", nivel);
            resultado.put("mensaje", "¡Bien hecho! Modo sin tiempo, el nivel no cambia");
            return resultado;
        }

        // Victoria: completa el tablero con menos de 3 errores intermedios
        boolean correcto = parejasAcertadas == parejasTotal && erroresIntermedios < 3;
        int errores = correcto ? 0 : 1;

        RegistroEjercicio registro = new RegistroEjercicio();
        registro.setMayorId(mayorId);
        registro.setTipoJuego("MEMORY");
        registro.setNivel(nivel);
        registro.setAciertos(parejasAcertadas);
        registro.setErrores(errores);
        registro.setTiempoMedioRespuesta(tiempoMedio);
        registro.setFecha(LocalDateTime.now());
        registro.setSubioPorNivel(false);
        registro.setBajoPorNivel(false);
        registroEjercicioRepositorio.save(registro);

        int nivelNuevo = calcularNivelSiguiente(mayorId, "MEMORY", nivel, correcto);
        registro.setSubioPorNivel(nivelNuevo > nivel);
        registro.setBajoPorNivel(nivelNuevo < nivel);
        registro.setNivel(nivelNuevo);
        registroEjercicioRepositorio.save(registro);

        String mensaje;
        if (correcto && nivelNuevo > nivel) mensaje = "¡Excelente! Subiste al nivel " + nivelNuevo;
        else if (!correcto && nivelNuevo < nivel) mensaje = "Sin presión, bajamos al nivel " + nivelNuevo;
        else if (correcto) mensaje = "¡Muy bien! Sigue así";
        else mensaje = "No pasa nada, inténtalo de nuevo";

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("correcto", correcto);
        resultado.put("nivelAnterior", nivel);
        resultado.put("nivelNuevo", nivelNuevo);
        resultado.put("mensaje", mensaje);
        return resultado;
    }
    public Map<String, Object> generarOperacion(Long mayorId) {
        int nivel = obtenerNivel(mayorId, "OPERACIONES");

        Random random = new Random();
        int a, b, tiempoLimite;
        String simbolo;
        int resultado;

        if (nivel == 1) {
            // Sumas simples hasta 10
            a = random.nextInt(10) + 1;
            b = random.nextInt(10) + 1;
            simbolo = "+";
            resultado = a + b;
            tiempoLimite = 40;
        } else if (nivel == 2) {
            // Sumas y restas hasta 20, resultado siempre positivo
            a = random.nextInt(20) + 1;
            b = random.nextInt(a) + 1; // b siempre menor que a para evitar negativos
            simbolo = random.nextBoolean() ? "+" : "-";
            resultado = simbolo.equals("+") ? a + b : a - b;
            tiempoLimite = 30;
        } else if (nivel == 3) {
            // Sumas y restas hasta 100, puede haber negativos
            a = random.nextInt(100) + 1;
            b = random.nextInt(100) + 1;
            simbolo = random.nextBoolean() ? "+" : "-";
            resultado = simbolo.equals("+") ? a + b : a - b;
            tiempoLimite = 30;
        } else if (nivel == 4) {
            // Multiplicaciones tabla hasta el 10
            a = random.nextInt(10) + 1;
            b = random.nextInt(10) + 1;
            simbolo = "×";
            resultado = a * b;
            tiempoLimite = 25;
        } else if (nivel == 5) {
            // Mezcla de todo con números medianos
            a = random.nextInt(30) + 1;
            b = random.nextInt(30) + 1;
            int op = random.nextInt(3);
            if (op == 0) { simbolo = "+"; resultado = a + b; }
            else if (op == 1) { simbolo = "-"; resultado = a - b; }
            else { simbolo = "×"; resultado = a * b; }
            tiempoLimite = 20;
        } else {
            // Nivel 6: mezcla con números grandes y poco tiempo
            a = random.nextInt(50) + 10;
            b = random.nextInt(50) + 10;
            int op = random.nextInt(3);
            if (op == 0) { simbolo = "+"; resultado = a + b; }
            else if (op == 1) { simbolo = "-"; resultado = a - b; }
            else {
                // Multiplicaciones más manejables en nivel 6
                a = random.nextInt(15) + 1;
                b = random.nextInt(15) + 1;
                simbolo = "×";
                resultado = a * b;
            }
            tiempoLimite = 15;
        }

        Map<String, Object> res = new HashMap<>();
        res.put("nivel", nivel);
        res.put("operacion", a + " " + simbolo + " " + b + " = ?");
        res.put("resultado", resultado);
        res.put("tiempoLimite", tiempoLimite);
        return res;
    }

    public Map<String, Object> validarOperaciones(Long mayorId, int aciertos, int errores, double tiempoMedio) {
        int nivel = obtenerNivel(mayorId, "OPERACIONES");
        boolean correcto = aciertos >= 4;

        RegistroEjercicio registro = new RegistroEjercicio();
        registro.setMayorId(mayorId);
        registro.setTipoJuego("OPERACIONES");
        registro.setNivel(nivel);
        registro.setAciertos(aciertos);
        registro.setErrores(errores);
        registro.setTiempoMedioRespuesta(tiempoMedio);
        registro.setFecha(LocalDateTime.now());
        registro.setSubioPorNivel(false);
        registro.setBajoPorNivel(false);
        registroEjercicioRepositorio.save(registro);

        int nivelNuevo = calcularNivelSiguiente(mayorId, "OPERACIONES", nivel, correcto);
        registro.setSubioPorNivel(nivelNuevo > nivel);
        registro.setBajoPorNivel(nivelNuevo < nivel);
        registro.setNivel(nivelNuevo);
        registroEjercicioRepositorio.save(registro);

        String mensaje;
        if (correcto && nivelNuevo > nivel) mensaje = "¡Excelente! Subiste al nivel " + nivelNuevo;
        else if (!correcto && nivelNuevo < nivel) mensaje = "Sin presión, bajamos al nivel " + nivelNuevo;
        else if (correcto) mensaje = "¡Muy bien! Sigue así";
        else mensaje = "No pasa nada, inténtalo de nuevo";

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("correcto", correcto);
        resultado.put("nivelAnterior", nivel);
        resultado.put("nivelNuevo", nivelNuevo);
        resultado.put("mensaje", mensaje);
        return resultado;
    }
    public Map<String, Object> generarRaro(Long mayorId) {
        int nivel = obtenerNivel(mayorId, "RARO");
        Random random = new Random();

        List<String> elementos = new ArrayList<>();
        String elementoRaro = "";
        String pista = "";

        if (nivel == 1) {
            List<String[]> grupos = Arrays.asList(
                    new String[]{"🔴", "🔴", "🔴", "🔵"},
                    new String[]{"🟡", "🟡", "🟡", "🟢"},
                    new String[]{"⬛", "⬛", "⬛", "⬜"},
                    new String[]{"🟣", "🟣", "🟣", "🟠"},
                    new String[]{"🔵", "🔵", "🔵", "🔴"},
                    new String[]{"🟢", "🟢", "🟢", "🟡"},
                    new String[]{"🟠", "🟠", "🟠", "⬛"},
                    new String[]{"⬜", "⬜", "⬜", "🟣"},
                    new String[]{"🔴", "🔴", "🔴", "🟡"},
                    new String[]{"🔵", "🔵", "🔵", "🟢"},
                    new String[]{"🟣", "🟣", "🟣", "⬛"},
                    new String[]{"🟠", "🟠", "🟠", "🔵"}
            );
            String[] grupo = grupos.get(random.nextInt(grupos.size()));
            elementoRaro = grupo[3];
            for (int i = 0; i < 3; i++) elementos.add(grupo[0]);
            elementos.add(grupo[3]);
            Collections.shuffle(elementos);
            pista = "¿Cuál es de color diferente?";

        } else if (nivel == 2) {
            List<String[]> grupos = Arrays.asList(
                    new String[]{"🐶", "🐱", "🐦", "🚗"},
                    new String[]{"🐘", "🦁", "🐯", "🏠"},
                    new String[]{"🐟", "🐬", "🦈", "🌵"},
                    new String[]{"🐔", "🦆", "🦅", "🍕"},
                    new String[]{"🐰", "🐻", "🦊", "✈️"},
                    new String[]{"🐸", "🦎", "🐢", "🎸"},
                    new String[]{"🐺", "🦌", "🐗", "🚀"},
                    new String[]{"🦋", "🐝", "🐛", "🏆"}
            );
            String[] grupo = grupos.get(random.nextInt(grupos.size()));
            elementoRaro = grupo[3];
            for (int i = 0; i < 3; i++) elementos.add(grupo[i]);
            elementos.add(grupo[3]);
            Collections.shuffle(elementos);
            pista = "¿Cuál no es un animal?";

        } else if (nivel == 3) {
            List<String[]> grupos = Arrays.asList(
                    new String[]{"🍎", "🍊", "🍋", "🥦"},
                    new String[]{"🍇", "🍓", "🍑", "🥕"},
                    new String[]{"🍌", "🍉", "🍒", "🧅"},
                    new String[]{"🥝", "🍍", "🥭", "🌽"},
                    new String[]{"🍐", "🍈", "🫐", "🥬"},
                    new String[]{"🍏", "🍅", "🍆", "🥒"},
                    new String[]{"🫒", "🍋", "🍇", "🥔"},
                    new String[]{"🍑", "🍒", "🍓", "🧄"}
            );
            String[] grupo = grupos.get(random.nextInt(grupos.size()));
            elementoRaro = grupo[3];
            for (int i = 0; i < 3; i++) elementos.add(grupo[i]);
            elementos.add(grupo[3]);
            Collections.shuffle(elementos);
            pista = "¿Cuál no es una fruta?";

        } else if (nivel == 4) {
            boolean parMayoria = random.nextBoolean();
            List<Integer> pool = new ArrayList<>();
            List<Integer> contrarios = new ArrayList<>();
            if (parMayoria) {
                for (int i = 2; i <= 20; i += 2) pool.add(i);
                for (int i = 1; i <= 19; i += 2) contrarios.add(i);
            } else {
                for (int i = 1; i <= 19; i += 2) pool.add(i);
                for (int i = 2; i <= 20; i += 2) contrarios.add(i);
            }
            Collections.shuffle(pool);
            Collections.shuffle(contrarios);
            for (int i = 0; i < 3; i++) elementos.add(String.valueOf(pool.get(i)));
            elementoRaro = String.valueOf(contrarios.get(0));
            elementos.add(elementoRaro);
            Collections.shuffle(elementos);
            pista = parMayoria ? "¿Cuál no es par?" : "¿Cuál no es impar?";

        } else if (nivel == 5) {
            List<String[]> grupos = Arrays.asList(
                    new String[]{"Mesa", "Silla", "Sofá", "Perro"},
                    new String[]{"Rojo", "Azul", "Verde", "Coche"},
                    new String[]{"Madrid", "París", "Roma", "Manzana"},
                    new String[]{"Lunes", "Martes", "Miércoles", "Enero"}
            );
            String[] grupo = grupos.get(random.nextInt(grupos.size()));
            elementoRaro = grupo[3];
            for (int i = 0; i < 3; i++) elementos.add(grupo[i]);
            elementos.add(grupo[3]);
            Collections.shuffle(elementos);
            pista = "¿Cuál no pertenece al grupo?";

        } else {
            List<String[]> grupos = Arrays.asList(
                    new String[]{"2", "4", "8", "9"},
                    new String[]{"3", "6", "9", "10"},
                    new String[]{"Río", "Mar", "Lago", "Montaña"},
                    new String[]{"Piano", "Guitarra", "Violín", "Pintura"}
            );
            String[] grupo = grupos.get(random.nextInt(grupos.size()));
            elementoRaro = grupo[3];
            for (int i = 0; i < 3; i++) elementos.add(grupo[i]);
            elementos.add(grupo[3]);
            Collections.shuffle(elementos);
            pista = "¿Cuál no encaja?";
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("nivel", nivel);
        resultado.put("elementos", elementos);
        resultado.put("elementoRaro", elementoRaro);
        resultado.put("pista", pista);
        return resultado;
    }

    public Map<String, Object> validarRaro(Long mayorId, int aciertos, int errores, double tiempoMedio) {
        int nivel = obtenerNivel(mayorId, "RARO");
        boolean correcto = aciertos >= 4;

        RegistroEjercicio registro = new RegistroEjercicio();
        registro.setMayorId(mayorId);
        registro.setTipoJuego("RARO");
        registro.setNivel(nivel);
        registro.setAciertos(aciertos);
        registro.setErrores(errores);
        registro.setTiempoMedioRespuesta(tiempoMedio);
        registro.setFecha(LocalDateTime.now());
        registro.setSubioPorNivel(false);
        registro.setBajoPorNivel(false);
        registroEjercicioRepositorio.save(registro);

        int nivelNuevo = calcularNivelSiguiente(mayorId, "RARO", nivel, correcto);
        registro.setSubioPorNivel(nivelNuevo > nivel);
        registro.setBajoPorNivel(nivelNuevo < nivel);
        registro.setNivel(nivelNuevo);
        registroEjercicioRepositorio.save(registro);

        String mensaje;
        if (correcto && nivelNuevo > nivel) mensaje = "¡Excelente! Subiste al nivel " + nivelNuevo;
        else if (!correcto && nivelNuevo < nivel) mensaje = "Sin presión, bajamos al nivel " + nivelNuevo;
        else if (correcto) mensaje = "¡Muy bien! Sigue así";
        else mensaje = "No pasa nada, inténtalo de nuevo";

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("correcto", correcto);
        resultado.put("nivelAnterior", nivel);
        resultado.put("nivelNuevo", nivelNuevo);
        resultado.put("mensaje", mensaje);
        return resultado;
    }
}