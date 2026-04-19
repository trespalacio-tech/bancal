# Agricultura regenerativa ecológica — Base de conocimiento

> Documento interno de referencia. **NO exponer en UI**. Sirve como fuente de verdad
> para decisiones de diseño, mensajes de alertas, recomendaciones, validaciones y seeds.
> Extraído de *Introducción a la huerta regenerativa ecológica — Agricultura proactiva*.

---

## 1. Principios operativos (reglas de oro)

Estas son las reglas que la app debe respetar o reflejar en sus recomendaciones:

1. **Cultivar para sanar**: producir sin dañar, regenerando el ecosistema.
2. **No pisar el bancal**: siempre con pasillo. La estructura del suelo (macro/microporos) es capital.
3. **No enmendar pH drásticamente** (ni cal, ni azufre). Corregir con materia orgánica y tiempo.
4. **Nunca usar estiércol sin compostar**: libera N de golpe, patógenos, semillas de adventicias, nitratos.
5. **Compostaje aerobio** (no putrefacción). Temperatura ideal 60 °C la primera semana; no superar 70 °C.
6. **El compost se aplica en superficie** cuando es semimaduro (3–9 meses), no enterrado.
7. **Rotación funcional**, no mística: rotar exigentes / poco exigentes / nada exigentes, y por sistema radicular. No invertir energía en "qué planta favorece a cuál" salvo cultivos intercalados concretos.
8. **Prevención > corrección**: falsa siembra, compost sin semillas, tarping, abonos verdes.
9. **Cultivos intercalados**: combinar uno de ciclo corto con uno de ciclo largo, plantados a la vez.
10. **Abonos verdes** son la única práctica que *crea* suelo (el compost sólo lo mantiene).

---

## 2. Suelo

### 2.1 Perfiles
- **Horizonte 0**: materia orgánica gruesa.
- **Horizonte A**: MO fina + minerales (arena, arcilla).
- **Horizonte B**: inorgánicos (arena, arcilla, cascajo).
- **Horizonte C**: piedras y rocas.
- **Roca madre**.

### 2.2 Textura (partículas por tamaño)
| Tipo | Tamaño |
|---|---|
| Gravas | >2 mm |
| Arenas | 2–0,05 mm |
| Limos | 0,05–0,002 mm |
| Arcillas | ≤0,002 mm |

Poros: **macroporos** (drenan rápido, airean) / **microporos** (retienen agua).

Clasificación edafológica:
- **Arenoso** >70% arena. Ligero, drena, se calienta rápido, lava nutrientes.
- **Limoso** >80% limo. Suave, retención intermedia, calentamiento progresivo.
- **Arcilloso** >20% arcilla. Pegajoso, retiene agua, fértil por microporos, drena mal en invierno.
- **Franco**: equilibrio ideal. Nuestro objetivo.

Intermedios: francoarenoso, francoarcilloso, arcilloarenoso, limoarcilloso.

### 2.3 pH
Escala: Alcalino >7 / Neutro 7 / Ácido <7.
- Rango útil: **6–7,5** (máxima disponibilidad de nutrientes).
- <5,5 bloquea Ca, Mg, P. >8 bloquea Fe, Mn, Zn.
- **No corregir con enmiendas** (ni cal, ni azufre). Corregir con aportes continuos de MO.

### 2.4 Materia orgánica
- Clave de la fertilidad. No es "resto vegetal", es *energía* para el ecosistema subterráneo.
- Cosechar extrae nutrientes; reponerlos con MO es imprescindible (no con NPK sintético).
- Suelo rico en MO funciona como "fondo de ahorro": amortigua pH, retiene agua y nutrientes.

### 2.5 Análisis de textura (casero)
1. Muestra de suelo, humedecer, amasar bola.
2. Formar cilindro de 1 mm → diagnóstico:
   - Anillo liso: arcilloso.
   - Resquebraja un poco: arcillo-limoso.
   - No baja de 3 mm, se rompe: arenoso.
   - 1–3 mm con resquebrajamiento leve: limo-arenoso / franco-arenoso.
   - 3 mm forma anillo sin romper: **franco** (ideal).

### 2.6 Análisis de pH (casero)
- 2 muestras con agua destilada, pasta húmeda.
- Bicarbonato → si burbujea, ácido.
- Vinagre → si burbujea, básico.
- Sin burbujas: neutro.
- Para precisión: tiras o medidor profesional.

### 2.7 Análisis de MO (casero)
- Agua oxigenada sobre muestra humedecida.
- Mucha efervescencia = muy orgánico. Poca/nula = pobre.

---

## 3. Vida en el suelo

### 3.1 Microorganismos (<visibles)
Bacterias, hongos, actinomicetos, algas, protozoos. Cada suelo tiene comunidad única. Equilibrio = simbiosis; desequilibrio = patógenos dominan.

Intervenciones:
- Rápida: **SMN** (microorganismos locales).
- Lenta/natural: compost.

### 3.2 Macroorganismos (visibles)
- **Colémbolos** (<1 mm): bichos blancos, trocean MO.
- **Ácaros** (1–3 mm): arañitas, descomponedores.
- **Lombrices** (>3 mm): fase avanzada de descomposición.
  - Más rojizas = restos frescos. Menos rojizas = MO en descomposición. Grises = minerales.
- **Otras larvas**: positivas, pero cribar compost para no meter lepidópteros que comerán plántulas.

---

## 4. Bancales

### 4.1 Definición y tipos
Zona de cultivo delimitada, **más larga que ancha**. No se pisa. Pasillo obligatorio.

Tipos:
- **Simple** (a nivel o acaballonado).
- **Profundo** (biointensivo, doble cavado).
- **Aterrazado** (pendientes pronunciadas).

Solo usar elevados (madera/ladrillo) si hay roca a poca profundidad.

### 4.2 Dimensiones
- **Ancho**: 75–90 cm (la app usa 75 cm por defecto). Regla: < 2× brazos estirados.
- **Largo**: 10–20 m recomendado (la app usa 10 m por defecto).
- **Pasillo**: largo de un pie, ajustable.
- **Bloques** de bancales del mismo tamaño para sistematizar.

### 4.3 Elección
- Pluviometría alta → acaballonado.
- Pluviometría baja → simple a nivel.
- Clima árido/muy soleado → excavados + acolchado de piedras (estilo Canarias).
- Pendiente suave → simple. Pronunciada → aterrazado.

### 4.4 Ventajas del sistema
- Sin compactación (estructura ideal macro/microporos).
- Esponja hídrica (mejor con pluviometría alta si acaballonado).
- Superficie siempre mullida → trasplante inmediato tras cosecha.
- Menos labores → tiempo y dinero.
- Cada temporada gana profundidad al añadir compost.
- Reduce erosión, enfermedades fúngicas radiculares, y banco de semillas de adventicias.
- Flexibilidad: se arranca un bancal sin afectar al resto.

### 4.5 Herramientas
- **Ahuecar**: pala plana, bieldo (caña alta), horcas de doble mango (35–40 cm profundidad, del ancho del bancal), pala normal.
- **Elevar**: biciazada con aporcador, pala.
- **Desmenuzar**: azada, rototill, biciazada, Gardena con desmenuzador.
- **Toque final**: rastrillo y rodillo.

### 4.6 Formación en suelos degradados (doble cavado biointensivo)
1. Replanteo con ferrallas + cuerda.
2. Retirar capa superficial de vegetación.
3. Por extremos, 1 m² cada vez: pala plana 20–30 cm, tierra a carretilla.
4. Dentro del m², bieldo/horca de doble mango: balancear sin levantar.
   - Suelo duro: separación 10–15 cm.
   - Suelo blando: 20–25 cm.
5. Añadir compost semimaduro + opcional humus de lombriz / harina de roca / SMN.
6. Pasar al siguiente m², tierra extraída va sobre el anterior.
7. Último m² se tapa con la tierra de la carretilla del primer m².
8. Romper terrones superficiales con azada o desterronador.

**No hacer doble cavado sin aportar MO + microorganismos**: no compensa.

### 4.7 Formación en suelos en buen estado
1. Subsolar si posible (muy recomendable, no imprescindible).
2. Ahuecar con grada (toda la superficie) o con bieldo/horca (solo el bancal, sin pisarlo).
3. Labrado superficial (rotovator, rototill).
4. Delimitar y acaballonar si procede.

Mecanizar las primeras fases si hay mucha superficie.

### 4.8 Preparación previa a plantación (según ventana)
- **Septiembre → marzo/abril**: cartones + hierba segada + restos de cocina.
- **1–2 meses de antelación**: tarping (lona sin capa de compost superficial).
- **Natural**: desbrozar lindes, dejar secar, esparcir sobre bancal. Al plantar, retirar y labrar superficial.
- **Inmediato**: no requiere técnica previa.

---

## 5. Fertilidad — compost

### 5.1 Definición
Compost = "poner juntos". Abono compuesto de MO que fermenta hasta humificarse.

### 5.2 Relación C/N ideal
**25–30 / 1 de partida** para obtener mezcla final **15 / 1**.

| Material | C/N |
|---|---|
| Aserrín | 150/1 |
| Paja (cereales) | 75–150/1 |
| Pasto seco | 80/1 |
| Hojas secas | 20–60/1 |
| Estiércol de caballo | 30/1 |
| Estiércol de vaca | 20–25/1 |
| Heno de leguminosas | 12–24/1 |
| Estiércol de oveja | 15–20/1 |
| Restos de cocina | 15–20/1 |
| Estiércol de aves | 10–15/1 |
| Residuos vegetales (yuyos) | 12/1 |
| Orina | 0,8/1 |

Cultivos para carbono: centeno, avena, trigo, maíz.
Cultivos para nitrógeno: habas, veza, alfalfa.

Proporción: **66,6% seco (C) / 33,3% verde (N)**.

### 5.3 Dimensiones pila doméstica
1,5 m × 1,5 m × 1,5 m.

### 5.4 Construcción
1. Rozar vegetación y clavar bieldo para soltar tierra.
2. Cama fina de ramas pequeñas (podas finas, restos de tomate/pimiento secos) → evita compactación/encharque.
3. Capas: **4 baldes seco + 3 baldes verde + 1/3 balde suelo/compost anterior**. Repetir hasta 1,5 m.
4. Trozos leñosos a 3–4 cm.
5. Alternativa sencilla: cilindro de malla metálica 1,5 × 1,5 m, abrible por lateral.

### 5.5 Proceso (temperatura)
- Semana 1: alcanzar **60 °C** (esteriliza patógenos y semillas).
- >70 °C: mueren microorganismos beneficiosos (demasiado estiércol).
- Semana 2+: baja progresivamente.
- 1 mes: temperatura ambiente.
- Volumen final: **50% del inicial**.

### 5.6 Humedad
- Humedecer materiales antes de añadirlos (sobre todo paja, serrín, podas).
- Regar con agua sin cloro, poco y a menudo.
- Exceso → anaerobio → putrefacción y nitratos.

### 5.7 Aireación
- Fermentación **aerobia**.
- La paja da porosidad natural.
- Ramas grandes = demasiado aire (sin contacto).
- **Voltear a los 20–30 días**: bordes al centro, centro a bordes. Debe volver a subir a 55/60 °C.

### 5.8 Cobertura
- Frío/sombra/lluvia alta → plástico.
- Lluvia baja → rafia o malla antihierbas.
- Clima templado, lluvias esporádicas → paja.

### 5.9 Maduración
- 2 meses: aparecen macroorganismos (bichos bola, ciempiés, ácaros, colémbolos, lombrices).
- 3 meses: color oscuro, olor a tierra fresca → compost terminado.

### 5.10 Incorporación — clasificación por edad
- **<3 meses**: no enterrar, muy activo. Acolchar y tapar con paja. Terminar en el bancal sin cultivo.
- **3–9 meses ("mantillo")**: **el que usamos**. Humifica en el bancal.
- **>9 meses**: rápida liberación, muy soluble. Guardar tapado. Solo para semilleros y base de siembra directa.

### 5.11 Por qué no estiércol sin compostar
- Libera N de golpe → crecimiento débil + nitratos (toxicidad).
- Amoníaco fresco inhibe germinación.
- No desinfectado → E. coli, Listeria.
- Semillas de adventicias viables.

### 5.12 Aplicación superficial — beneficios
- Favorece enraizamiento.
- Capa gruesa reduce germinación de adventicias.
- Amortigua impacto de lluvia.
- Se incorpora con el tiempo, suelta la tierra.

### 5.13 Dosis (bancal de 75 cm × 10 m = 7,5 m²)

**Volumen base (capa 2 cm)**: 0,75 × 10 × 0,02 = **0,15 m³ = 150 L/bancal**.

Equivalencias útiles:
- 3 cubos de 20 L = 60 L ≈ 1 carretilla ≈ capa 2 cm en 4 m de bancal.
- 7,5 cubos de 20 L = 150 L = 1 bancal completo a 2 cm.

**Clasificación por exigencia**:

| Exigencia | Familia típica | Dosis |
|---|---|---|
| **Muy exigentes** | Solanáceas, cucurbitáceas, brasicas de ciclo largo (>2 meses) | Fondo: compost maduro enterrado 15 cm (gallinaza 120 L/bancal o pellets 1 kg) + superficial 7,5 cubos de 20 L |
| **Poco exigentes** | El resto | 150 L/bancal, 2 cm superficial (75 kg aprox) |
| **Nada exigentes** | Leguminosas | No necesitan. Opcional 3 cubos de 20 L en filas con compost bajo en N |

En suelos muy degradados: **5 cm superficiales** hasta revertir la situación.

En floración de muy exigentes: refuerzo con compost menos nitrogenado o pellets tipo Ecofem (3-5-6).

Compost con cada nueva plantación en poco exigentes, salvo que ya se haya abonado en cultivo previo o abonos verdes.

### 5.14 Fertilización adicional
- **Té de compost**: para suelos en transición, plantas estresadas, o cuando falta compost. Cada 7–14 días hojas, cada 15–30 días suelo.
- **Algas**: minerales y micronutrientes. Verde oscuro, resistencia a plagas.
- **Harinas de roca**: sistemáticas en método biointensivo. No necesarias en uso doméstico.
- Profesional: partir de analítica y corregir.
- **Menos es más**. No inventar preparados de más.

### 5.15 Receta té de compost aireado (ACT)
**Ingredientes**:
- 20 L agua sin cloro.
- 1 L compost de alta calidad o lombricompost.
- 15–30 mL alimento microbiano (melaza / harina de pescado / extracto de algas).

**Pasos**:
1. Llenar bidón con agua sin cloro (si tiene aireador, airear 30–60 min).
2. Compost en bolsa porosa (medias de nylon), holgado.
3. Añadir alimento microbiano al agua.
4. Airear **12–24 h a 15–25 °C**.
5. Retirar malla, filtrar.
6. Usar inmediato. No almacenar (si hay que, añadir melaza).

**Mezcla mochila 15 L para 30 m²**:
- 150 mL ACT + 14,85 L agua sin cloro. No se diluye el té, el agua es solo vehículo.
- **Dosis fija: 50 L ACT por hectárea**.

**Aplicación foliar preventiva**:
- Cada 7–14 días.
- Amanecer o atardecer. No con sol fuerte ni antes de lluvia.
- Boquillas >400 micrones. Presión 20–80 psi. Agitar suavemente.

**Aplicación al suelo (inocular rizosfera)**:
- Cada 15–30 días en suelos pobres o al inicio de cultivo.
- Al pie / línea de siembra. Tras riego o suelo húmedo.
- Puede mezclarse con riego si es de aplicación inmediata.

### 5.16 Bidón aireador DIY
Cubo 20 L + piedras difusoras fijadas con cinta + bomba acuario (≥60 L/min para lotes grandes) + mangueras silicona. Sin piedras: T en el tubo aireador.

---

## 6. Plantación y siembra

### 6.1 Intercalado (cultivos asociados)
Combinar ciclo corto + ciclo largo, **plantados a la vez**.

Ejemplos recomendados:
- Tomate + Lechuga + Cebolleta/Remolacha
- Lechugas + Cebolleta/Remolacha
- Leguminosas de enrame + Rabanitos/Cebolleta/Lechuga

**Perennes**: intercalar solo con otras perennes (flora auxiliar, reservorio de fauna auxiliar). No con anuales (alteraría el hábitat).

### 6.2 Tabla de marcos de plantación (fuente de verdad para seed)

Formato: Cultivo | Líneas por bancal | Distancia entre plantas (cm) | Sem. hasta cosecha | Sem. cosechando | Siembra directa | Plantel

| Cultivo | Líneas | Dist cm | Sem cosecha | Sem cosechando | Directa | Plantel |
|---|---|---|---|---|---|---|
| Acelga | 3 | 30 | 8 | 12 | No | Sí |
| Acelga baby | 5 | 15 | 5 | 8 | Sí | Sí |
| Ajo | 4 | 15 | 30 | 1 | Sí | No |
| Ajo tierno | 5 | 15 | 8 | 8 | Sí | No |
| Albahaca | 4 | 40 | 6 | 8 | No | Sí |
| Apio | 3 | 40 | 12 | 12 | No | Sí |
| Apionabo | 4 | 40 | 10 | 4 | No | Sí |
| Berenjena | 2 | 50 | 10 | 12 | No | Sí |
| Boniato | 2 | 30 | 16 | 1 | Sí | No |
| Borraja | 2 | 25 | 6 | 8 | Sí | Sí |
| Brócoli | 2 | 40 | 10 | 3 | No | Sí |
| Calabacín | 1 | 80 | 9 | 10 | No | Sí |
| Calabaza pequeña | 1 | 100 | 12 | 8 | Sí | Sí |
| Calabaza grande | 1 | 100 | 16 | 8 | Sí | Sí |
| Canónigo | 8 | 15 | 6 | 8 | Sí | Sí |
| Cebolla fresca | 5 | 25 | 10 | 8 | No | Sí |
| Cebolla guardar | 4 | 25 | 16 | 1 | No | Sí |
| Cebolla japonesa | 5 | 25 | 8 | 8 | No | Sí |
| Chalota | 5 | 25 | 14 | 1 | Sí | No |
| Cebollino | 3 | 40 | 8 | 30 | No | Sí |
| Cilantro | 3 | 40 | 8 | 20 | No | Sí |
| Col de Bruselas | 2 | 40 | 16 | 12 | No | Sí |
| Col lisa | 2 | 40 | 9 | 6 | No | Sí |
| Col pico | 2 | 40 | 10 | 6 | No | Sí |
| Col rizada | 2 | 40 | 12 | 8 | No | Sí |
| Coliflor temprana | 2 | 40 | 12 | 3 | No | Sí |
| Coliflor tardía | 2 | 40 | 19 | 4 | No | Sí |
| Colinabo | 3 | 40 | 10 | 2 | No | Sí |
| Colirábano | 3 | 40 | 10 | 1 | No | Sí |
| Eneldo | 4 | 30 | 8 | 3 | No | Sí |
| Escarola | 3 | 30 | 10 | 6 | No | Sí |
| Espárrago | 1 | 40 | 52 | 8 | No | Sí |
| Espinaca | 4 | 30 | 6 | 8 | No | Sí |
| Espinaca baby | 4 | 30 | 6 | 8 | Sí | Sí |
| Guisante | 2 | 10 | 12 | 2 | Sí | Sí |
| Haba | 3 | 15 | 12 | 2 | Sí | Sí |
| Hinojo | 3 | 40 | 10 | 3 | No | Sí |
| Judía alta | 2 | 10 | 9 | 6 | Sí | Sí |
| Judía baja | 3 | 20 | 8 | 5 | Sí | Sí |
| Kale | 2 | 40 | 9 | 16 | No | Sí |
| Lechuga | 3 | 30 | 6 | 4 | No | Sí |
| Lechuga cogollo | 4 | 30 | 5 | 3 | No | Sí |
| Lechuga mix | 5 | 20 | 6 | 8 | Sí | No |
| Maíz | 2 | 30 | 12 | 4 | Sí | No |
| Melón | 1 | 80 | 12 | 4 | No | Sí |
| Mizuna | 5 | 30 | 5 | 8 | No | Sí |
| Mostaza | 5 | 45 | 5 | 8 | No | Sí |
| Nabiza/Grelos | 3 | 30 | 6 | 12 | Sí | No |
| Orégano | 3 | 40 | 16 | 30 | No | Sí |
| Pak choi | 3 | 30 | 5 | 4 | No | Sí |
| Patata | 1 | 40 | 13 | 1 | Sí | No |
| Pepino | 2 | 40 | 6 | 4 | No | Sí |
| Perejil | 3 | 40 | 8 | 20 | No | Sí |
| Pet sai | 3 | 30 | 5 | 4 | No | Sí |
| Pimiento | 2 | 40 | 13 | 12 | No | Sí |
| Pimiento gordo | 2 | 40 | 15 | 8 | No | Sí |
| Pimiento pequeño | 2 | 40 | 10 | 12 | No | Sí |
| Pimiento picante | 2 | 40 | 10 | 8 | No | Sí |
| Puerro | 2 | 15 | 10 | 12 | No | Sí |
| Rabanito | 5 | 10 | 3 | 4 | Sí | No |
| Rábano invierno | 4 | 15 | 5 | 8 | Sí | Sí |
| Remolacha | 4 | 15 | 5 | 4 | No | Sí |
| Remolacha granel | 3 | 15 | 9 | 8 | No | Sí |
| Romanesco | 2 | 40 | 11 | 3 | No | Sí |
| Rúcula | 5 | 10 | 5 | 8 | No | Sí |
| Ruibarbo | 1 | 50 | 13 | 8 | No | Sí |
| Sandía | 1 | 80 | 12 | 8 | No | Sí |
| Tatsoi | 5 | 30 | 6 | 4 | No | Sí |
| Tokyo bekana | 3 | 30 | 6 | 4 | No | Sí |
| Tomate cherry | 1 | 75 | 12 | 10 | No | Sí |
| Tomate F1 | 1 | 50 | 14 | 10 | No | Sí |
| Tomate tradicional | 1 | 50 | 14 | 10 | No | Sí |
| Tomate determinado | 1 | 50 | 12 | 8 | No | Sí |
| Tomillo | 3 | 40 | 16 | 30 | No | No |
| Wasabino | 5 | 30 | 6 | 4 | No | Sí |
| Zanahoria | 4 | 15 | 11 | 4 | Sí | No |
| Zanahoria granel | 3 | 15 | 16 | 1 | Sí | No |

**Nombres equivalentes en el seed actual de la app**:
- "Col lisa" = "Repollo" / "Col pico" = "Col" / "Cebolla guardar" = "Cebolla" / "Judía alta" = "Judía de enrame".

### 6.3 Rotación
- No seguir rotaciones míticas complejas. Sí:
  - Rotar por exigencia nutricional (muy exigente → poco exigente → nada exigente).
  - Rotar por sistema radicular (superficial, medio, profundo).
- Modo profesional; para empezar no es necesario.

---

## 7. Protección del suelo

### 7.1 Acolchado y desherbado
- **Agricultura natural** (no desherbar): poco realista, años de prueba-error.
- **Productos**: vinagre mata cultivo igual que adventicia. Descartado.

**Acolchado vegetal** (válido):
- **Restos de siega / césped**: dejar secar 2–3 días, voltear, luego esparcir. Sin contacto directo con plantas. Solo con plantas enraizadas (no tras sembrar).
- **Paja: NO recomendada**. Trae semillas, se vuela, secuestra N.

**Si presión de adventicias alta**: NO acolchar (las hierbas saldrán a través). Solo **compost superficial**, y eliminar hierbas en **nascencia** (color blanquecino, pre-fotosíntesis) con herramientas que apenas muevan tierra. Nunca cortar: rebrotan.

### 7.2 Herramientas de desherbado
- **Delta de alambre**: hierbas muy pequeñas. Superficial, no corta raíces. Cultivos de cualquier tamaño. Suelos sueltos con compost. Pasadas largas.
- **Delta/omega**: hierbas mayores, suelos pesados con costra. Más profunda, corta — cuidado con raíces de cultivos. Pasadas cortas de vaivén.
- **Escardadora de primavera**: muy cerca de la planta sin dañarla.
- **Binador**: cortante. Bordes y costras duras.
- **Cuchilla fina**: entre cultivos muy próximos. Mango largo o de mano.

**No recomendado biciazada** sobre bancales (incómoda). Sí para pasillos.

### 7.3 Tarping
Tapar superficie con material opaco.

Efectos:
- Debilita hierbas (sin fotosíntesis → ahiladas).
- Temperatura estable fomenta germinación débil → aniquilación masiva.
- Protege contra compactación por lluvia y costra superficial.
- Permite tener bancales listos para plantar inmediato.

Materiales:
- Lonas de **ensilaje** (lo habitual, cubre varios bancales).
- Acolchados orgánicos gruesos o cartón debajo (sin luz).
- Climas secos: **malla antihierbas** (conviene que se moje para microbiota).

### 7.4 Abonos verdes / cultivos de cobertera
- "Cobertera": siembra para no tener suelo desnudo (destino flexible).
- "Abono verde": siembra con único objetivo de incorporar al suelo.

**Es la única práctica que CREA suelo**. El compost solo mantiene.

Rutina:
- Siembra **más densa** que para cultivar.
- Solo en bancales (no pasillos).
- Preparar bancal → compactar con rodillo → sembrar a voleo → cubrir con fina capa de compost.
- Si filas: compost solo en la fila.
- Si hay pájaros: tapar con manta térmica o malla antiinsectos hasta nascencia.

**Timing ideal**: otoño (invierno en suelo cubierto) → incorporar antes de primavera → esperar 1,5–2 meses antes de plantar.

**Especies**:
- **Adventicias-repressor**: centeno, espelta, trigo sarraceno.
- **Nitrógeno (leguminosas)**: fijan N del aire (requieren Rhizobium en suelo — inocular semillas si no hay).
- **Cereales** como tutor de leguminosas + raíces profundas.
- **Facelia**: MO + polinizadores.
- **Pasto de Sudán**: MO masiva.
- **Crucíferas (rábanos)**: descompactar / biosolarización.

**Mezcla típica recomendada**: avena-veza, espelta, centeno de invierno.

**Incorporación**:
- Justo antes de floración (no crítico si se pasa).
- **Desbrozadora de martillos** (ideal) o cortacésped: triturado fino.
- Siega tradicional: fibras largas = complicado.
- Tras desbroce: dejar secar hasta cambio de color; motocultor a baja velocidad.
- Alternativa low-disturb: dejar en superficie + compost/SMN + riego + tarping (compostaje superficial + biosolarización).
- Urgente: segar y retirar. Usar en pila de compost o como cobertura.

**Nunca sembrar abono verde entre filas de cultivo existente**: compite, no crece con vigor, no se adelanta nada real.

---

## 8. Riego

Sistemas recomendados:
- **Cintas de goteo**: con manejo ecológico duran años. Ideal cuando hay restricciones de agua.
- **Aspersores de gota fina** (tipo Sumi-samsui, 4–8 m alcance, 2–4 m cada lado): mejor uniformidad.
- **Aspersores**: más longitud pero menos uniformidad (gota mayor).

**Ideal combinado**: goteo uniforme + aspersión refrescante en horas centrales.

Poca lluvia + agua disponible → aspersión (moja toda la superficie para microbiota).
Restricciones → goteo.

Aspersores ayudan además contra pulguilla, araña roja.

---

## 9. Cosecha

- Cuchillo/tijeras **siempre afilados**. Afilador a mano.
- Cajas distribuidas por bancales. Rellenar y mover.
- Calor → mover a zona fresca inmediato. Si no, dejar y mover al final.
- Caja en el lado de la mano dominante. Cuchillo no se suelta.
- Limpiar y afilar al cambiar cultivo.
- Hoja pequeña: cortar todas a 2 cm (no dañar brotes centrales).
- Corte > arranque (cicatrización mejor, menos enfermedad).
- Tras cosecha: pasar rastrillo (quita restos, estimula rebrote al romper costra).

**Materiales**: cuchillos variados, afilador, dedales con cuchilla, carro, gomas para manojos, cajas apilables.

---

## Anclas para la app (cómo aplicar este conocimiento)

- **Marcos de plantación** → tabla §6.2 es la fuente de verdad para `CultivosSeed.kt`.
- **Semanas hasta cosecha** → `diasCosecha = semanas × 7`.
- **Alertas**:
  - Cosecha: `fechaCosechaEstimada - hoy ≤ 7 días` (matemática ya implementada).
  - Tratamiento preventivo: ACT cada 7–14 días foliar / 15–30 días suelo → ver §5.15.
  - Compost de mantenimiento: antes de plantación de cultivos poco exigentes (150 L / bancal).
- **Tarping / abono verde** → ya modelado en `BancalEntity.tarpingActivo` y `abonoVerdeActivo`.
- **Validaciones**:
  - Avisar si intercalado no es del tipo recomendado (corto + largo plantados a la vez).
  - Flag "no pisar" visible en documentación interna cuando se sugieran rutas de mantenimiento.
- **Calendario Burgos**: cruzar la tabla §6.2 (semanas) con ventanas climáticas (última helada ~abril, primera ~octubre) para recomendaciones.
- **Recomendaciones de compost por exigencia** (§5.13) → en `PlantarScreen`, al seleccionar cultivo muy exigente, sugerir compost de fondo + superficial. Para leguminosas, sugerir inóculo Rhizobium.
- **Nunca sugerir estiércol fresco, cal, azufre, ni paja como acolchado** en textos generados por la app.
