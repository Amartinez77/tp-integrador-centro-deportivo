# 🏋️ Sistema Integral de Gestión – Centro Deportivo de Alto Rendimiento

**Materia:** Programación II  
**Docente:** Ing. Fabian Castillo  
**Período:** Octubre – Noviembre 2026  
**Modalidad:** Desarrollo colaborativo en eduScrum (equipos de 4)

---

## 📋 Descripción del Proyecto

Sistema de software integral desarrollado en **Java** para administrar de forma automatizada y centralizada:

- 👥 **Gestión Multicategoría de Socios** (Estándar, VIP, Vitalicio)
- 💰 **Liquidación Automatizada de Cuotas** (con cargos y descuentos polimórficos)
- 🎾 **Alquiler de Equipamiento y Sanciones** por mora
- 📝 **Inscripción a Actividades** con control de cupos
- 🚪 **Control de Acceso / Molinete** (Check-in por DNI)
- 🧾 **Facturación e Historial Auditable** por período fiscal
- 📊 **Módulo de Reportes y Métricas**

---

## 🏗️ Arquitectura

El sistema sigue una **Arquitectura en Tres Capas (3-Tier)**:
src/
├── Presentacion/ → Interfaz de usuario (consola/GUI)
├── LogicaNegocio/ → Modelo de dominio y validaciones
├── Datos/ → Persistencia en archivos serializados
└── Inicio.java → Raíz de Composición (Inyección de Dependencias)

---
