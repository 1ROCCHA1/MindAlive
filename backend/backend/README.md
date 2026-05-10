# MindAlive — Backend

API REST desarrollada con Spring Boot para dar soporte a la app Android MindAlive.

## ¿Qué hace?

Gestiona la autenticación de usuarios, los ejercicios cognitivos, 
el asistente de voz con IA, las alarmas y recordatorios, el perfil 
del mayor y el centro de control del cuidador.

## Stack tecnológico

- **Backend** — Spring Boot (Java 21)
- **Base de datos relacional** — MySQL 8.0
- **Base de datos documental** — MongoDB 7.0
- **IA conversacional** — Gemini 2.5 Flash API

## Modelo de datos

MySQL: usuarios, vinculos_familiares, registro_ejercicio, alarmas  
MongoDB: conversaciones, perfiles_mayor

## Requisitos para ejecutar

- Java 21
- MySQL 8.0 con una base de datos llamada `mindalive`
- MongoDB corriendo en el puerto 27017
- Clave de API de Gemini configurada en `application.properties`

Al lanzar `BackendApplication.java` Spring genera las tablas automáticamente.

## Repositorio frontend

https://github.com/1ROCCHA1/MindAlive-Front
