# Entregable U2 - Sistema de Transporte (JavaFX + SQLite)

Estructura igual a `poo_2026_2_final-master`: login con sesion (`UserSession`), base de datos en la
carpeta del usuario (`ConexionDB` + `initDatabase`), `modelo`, `interfaces`, `implementacion` (DAO),
`controller`, `utils` (`UtilAlerts`), `test`, `views` y `Data/transporte-backup.sql`.

## Ingreso
Usuario: `admin`  -  Clave: `admin123` (se crea solo la primera vez).
La BD se guarda en `C:\Users\<usuario>\SistemaTransporte\transporte.db`.

## Observaciones atendidas
1. **Fecha y hora**: `Viaje.fechaSalida` y `Viaje.fechaLlegada` son `LocalDateTime`
   (se guardan como `yyyy-MM-dd HH:mm`). La hora de salida sale de la tarifa y la llegada se calcula
   con la duracion, asi un tramo de 2 o 3 horas el mismo dia ya no se confunde.
2. **Sin cruces**: al guardar un viaje se valida (`validateConductor` / `validateVehiculo`) que el
   conductor y el vehiculo no tengan otro viaje cuyo horario se cruce (fecha **y** hora). Se permite
   salir justo a la hora en que llega el viaje anterior.
3. **Tarifario** (menu *Tarifario*): origen, destino, hora de salida, duracion y precio. Viene
   precargado con rutas de la zona (Tarapoto, Moyobamba, Juanjui, Rioja, Nueva Cajamarca, Yurimaguas,
   Tocache). Los precios son **referenciales**: tomados como base de Turismo Selva y portales de venta de
   pasajes; no se hallaron tarifas publicas de Autos Cajamarca. Se pueden editar desde la pantalla.
4. **Pasaje**: se genera un codigo automatico `PAS-<anio>-<id de 5 digitos>` (ej. `PAS-2026-00001`),
   se ve en la tabla y se muestra al registrar. La tabla tiene el **buscador por DNI**.

## Datos iniciales
Ademas del usuario admin y el tarifario, la primera vez que se crea la BD se cargan una flota de
10 vehiculos (autos, combis y buses) y 8 conductores de ejemplo. Solo se cargan si la tabla esta vacia.
Para volver a cargarlos desde cero, cerrar la app y borrar `transporte.db` de la carpeta `SistemaTransporte`.

## Relaciones
- `viaje`  -> `tarifa`, `conductor`, `vehiculo`
- `pasaje` -> `pasajero`, `viaje`, `usuario` (quien vendio el pasaje)
- Un viaje no vende mas pasajes que la capacidad de personas del vehiculo.

## Ejecutar
`mvn clean javafx:run`, o `MainLauncher` / `HelloApplication` desde IntelliJ.
