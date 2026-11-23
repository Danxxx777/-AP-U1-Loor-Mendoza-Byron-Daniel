# AP-U1 — AforoSeguro: desarrollo parcial

**Estudiante:** Loor Mendoza Byron Daniel  
**Asignatura:** Aplicaciones Distribuidas (ISR-701), SPA 2026–2027  
**Docente:** Gleiston C. Guerrero-Ulloa, PhD

El avance implementa comunicación por sockets TCP, autenticación por token, entradas/salidas/consulta del aforo, atención mediante hilos y comparación entre contador protegido y modo inseguro. No implementa latidos, detección de puertas caídas, Lamport ni OrdenTotal. Esta incorporación documental conserva el código y el historial anteriores; no presenta la actividad como completa.

## Archivos

```text
src/Coordinador.java
src/Puerta.java
src/PruebaCarga.java
config.properties
docs/informe.tex
docs/informe.pdf
docs/entrega_SGA.tex
docs/entrega_SGA.pdf
docs/img/
docs/evidencias/carga_normal_transcrita.txt
README.md
```

Las fotografías de `docs/img/` provienen del estudiante. `conexion_coordinador.jpg` y `respuesta_puerta.jpg` corresponden al intercambio inicial de texto, no al escenario completo de aforo. Se conservan los originales; el informe ajusta el encuadre y gira su visualización para ampliar el texto y facilitar la lectura, sin reconstruir mensajes ni resultados. `codigos_sesion.png` contiene los códigos aportados posteriormente. No se han creado capturas de escenarios pendientes.

## Compilación del código

Se necesita **JDK 21**, sin dependencias externas. Ejecute desde la raíz del repositorio, donde está `config.properties`:

```powershell
javac -encoding UTF-8 -d out src\*.java
```

En Linux/macOS: `javac -encoding UTF-8 -d out src/*.java`.

## Ejecución: orden de arranque

En terminales separadas, desde la misma carpeta:

```powershell
java -cp out Coordinador
java -cp out Puerta 1
java -cp out Puerta 2
java -cp out Puerta 3
```

Cada línea se ejecuta en una terminal diferente. El coordinador debe iniciarse primero. Las puertas aceptan `entrar`, `salir`, `estado`, `invalido` y `fin`. `salir` reduce el contador; `fin` termina el proceso. `invalido` envía una consulta con un token incorrecto.

El aforo es 10 y el puerto 5000. Si el puerto ya está ocupado, detenga la instancia anterior del coordinador. Reiniciarlo pone el contador a cero porque no se implementó persistencia.

### Carga normal

Con el contador en cero y sin otras entradas/salidas durante la prueba:

```powershell
java -cp out PruebaCarga
```

La prueba crea treinta hilos: diez solicitudes por identificador de puerta, utilizando `Puerta.solicitar`. No requiere iniciar tres procesos de puerta para generar esa carga. La salida compartida por el estudiante fue: **10 permitidas, 20 rechazadas, 0 errores y contador final 10**. Se conserva una transcripción identificada como tal en `docs/evidencias/`; no reemplaza la captura requerida.

### Comparación insegura

Detenga el coordinador normal y reinícielo:

```powershell
java -cp out Coordinador --inseguro
```

Luego ejecute nuevamente PruebaCarga. En este modo se omite el bloqueo del contador y hay una pausa de 50 ms entre lectura y actualización. Puede haber entradas autorizadas que no coincidan con el contador por actualizaciones perdidas. No se aportó una salida de ejecución de esta comparación: el informe no le atribuye cifras observadas.

## Formato de mensajes

Cada solicitud termina con un salto de línea. Sus campos se separan con coma:

```text
ORDEN,token,id_puerta
```

Por ejemplo, `ENTRAR,practica123,1`. Las órdenes son ENTRAR, SALIR y ESTADO. El coordinador valida el token y el identificador entre 1 y 3. `readLine()` separa los mensajes; el coordinador mantiene un lector por conexión y puede procesar varias líneas consecutivas. La respuesta es una línea de texto con el resultado y, cuando procede, el contador y aforo. Los rechazos se imprimen en consola; no se guardan bitácoras persistentes. El token es autenticación básica en texto claro, sin cifrado.

## Compilar los PDF desde LaTeX

Compilador: **pdfLaTeX**. Archivos principales: `docs/informe.tex` y `docs/entrega_SGA.tex`. Las imágenes están incluidas en `docs/img/`. No hay bibliografía ni marco teórico.

Desde la raíz del repositorio:

```powershell
cd docs
pdflatex -interaction=nonstopmode -halt-on-error informe.tex
pdflatex -interaction=nonstopmode -halt-on-error informe.tex
pdflatex -interaction=nonstopmode -halt-on-error entrega_SGA.tex
pdflatex -interaction=nonstopmode -halt-on-error entrega_SGA.tex
cd ..
```

Paquetes del informe: `inputenc`, `fontenc`, `lmodern`, `babel` (español), `geometry`, `xcolor` con soporte de tablas, `array`, `tabularx`, `graphicx`, `enumitem`, `xurl` e `hyperref`. Deben estar instalados localmente antes de una compilación sin Internet. La página SGA utiliza un subconjunto de esos paquetes.

El informe tiene tres páginas. `docs/entrega_SGA.pdf` tiene una página con identificación y URL completa en una sola línea: es el único PDF indicado para subir al SGA. El informe y sus fuentes se consultan en el repositorio.

## Código reutilizado y asistencia

No se identificó reutilización de código de un repositorio propio anterior. La implementación y la documentación contaron con asistencia de IA generativa. Esta declaración no acredita cumplimiento de la condición sin IA del planteamiento; tampoco se presenta el código como reutilización propia anterior.

| Archivo de este repositorio | Repositorio propio de origen | Archivo y commit de origen | Qué se cambió |
| --- | --- | --- | --- |
| No se declaró reutilización desde un repositorio propio anterior | No aplica | No aplica | No aplica |

## Códigos de sesión y trazabilidad

Datos transcritos de la imagen que el estudiante aportó posteriormente:

- Código 1, inicio: **KD55-TB**.
- Código 2, minuto 60: **662D-M6**.

Los mensajes del historial previo a esta documentación no identificaban esos códigos. Añadirlos ahora en el informe y en este registro no acredita su inclusión en los commits exigidos durante la sesión. Se conservan las fechas y los mensajes anteriores.

La documentación describe el código de `1822ad4`, integrado en `4b9f37a`. El historial anterior contiene seis commits, cuatro con cambios de código. El primer commit local de código (`02e2683`) conserva la identidad configurada en el equipo del laboratorio; los posteriores llevan los datos del estudiante. Este commit documental se realiza posteriormente con su fecha real.

## Pendientes respecto al planteamiento

- Latidos cada 2 segundos, detección a los 6 segundos y recuperación de puertas.
- Estado de actividad de P1, P2 y P3.
- Relojes de Lamport, bitácoras y programa OrdenTotal.
- Evidencia completa de E0 a E6, incluida la comparación insegura y dos pares causales reales.
- Verificación por el docente de las condiciones de sesión, autoría y entrega.

La versión completa propuesta posteriormente en la conversación no forma parte del código de este repositorio. El informe se limita al avance efectivamente publicado.
