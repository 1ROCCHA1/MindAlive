# MindAlive — AI Cognitive Assistant

Aplicación móvil Android para acompañar a personas mayores,
mantener su mente activa y detectar a tiempo posibles cambios
en su estado cognitivo o emocional.

## ¿Qué es?

MindAlive combina un asistente de voz con inteligencia artificial,
ejercicios cognitivos sencillos y recordatorios de medicamentos.
Todo queda registrado y analizado. Si el sistema detecta una bajada
sostenida en el rendimiento o en el estado de ánimo del mayor,
avisa al familiar o cuidador.

## Repositorio

https://github.com/1ROCCHA1/MindAlive

## Estado actual

La base del proyecto está terminada. El backend arranca correctamente,
conecta con MySQL y MongoDB, y las tablas se crean automáticamente.
El modelo de datos cubre todas las entidades del sistema: usuarios,
perfiles, vínculos familiares, medicamentos, recordatorios, alertas,
snapshots cognitivos, conversaciones y registros de ejercicios.

## Stack tecnológico

- **App Android** — Java / Kotlin + Android Speech & TTS
- **Backend** — Spring Boot
- **Base de datos relacional** — MySQL
- **Base de datos documental** — MongoDB
- **IA conversacional** — OpenAI API

## Requisitos para ejecutar

MySQL con una base de datos llamada `mindalive`, MongoDB corriendo
en el puerto 27017 y las credenciales configuradas en `application.properties`.
Al lanzar `BackendApplication.java` el propio Spring genera las tablas.