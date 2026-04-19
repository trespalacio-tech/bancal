# Bancal — notas para Claude

## Contexto del proyecto
App Android Kotlin + Jetpack Compose + Room para gestionar bancales de huerto
biointensivo regenerativo en Burgos (zona 8b). Arquitectura MVVM + Repository,
100% offline.

## Base de conocimiento agronómico

**Fuente de verdad para decisiones agronómicas, marcos de plantación,
mensajes de alertas y recomendaciones**: [docs/agricultura-regenerativa.md](docs/agricultura-regenerativa.md).

Consultar ese documento antes de:
- Añadir o modificar entradas en `CultivosSeed.kt`.
- Redactar textos de alertas, sugerencias, compartir bancal, o cualquier
  mensaje que contenga consejo agronómico.
- Diseñar nuevas validaciones de plantación (exigencia nutricional,
  intercalados, rotación).
- Escribir datos de dosis (compost, té de compost, abonos verdes).

No exponer el documento directamente al usuario. Es referencia interna.

## Convenciones
- Cultivos en la tabla del PDF (§6.2) cuyos nombres difieren del seed actual:
  - "Col lisa" ↔ "Repollo"
  - "Col pico" ↔ "Col"
  - "Cebolla guardar" ↔ "Cebolla"
  - "Judía alta" ↔ "Judía de enrame"
  Mantenemos los nombres del seed actual (decisión del usuario).
- `diasCosecha` = semanas del PDF × 7.
- Bancal por defecto: 75 cm × 10 m = 7,5 m².
- Dosis base de compost: 150 L por bancal (capa 2 cm).

## Nunca sugerir en textos generados
- Estiércol sin compostar.
- Enmiendas de pH (cal, calizas, dolomita, azufres, sulfato ferroso).
- Paja como acolchado (trae semillas, se vuela, secuestra N).
- NPK sintético.

## Git
- Repo: https://github.com/trespalacio-tech/bancal
- Config local (no global): `user.email=fincatrespalacio@gmail.com`,
  `user.name=Finca Trespalacio`.
- `burebano@gmail.com` NO debe aparecer en commits ni contribuciones.
