# Future Arcana & Heroes (Forge 1.21.4)

Questa è una **base funzionante** di mod (progetto sorgente) per Minecraft **Java Edition** con **Minecraft Forge 1.21.4 (54.1.12)**.

## Cosa aggiunge (MVP)
- **Blocchi futuristici**: `Quantum Console`, `Temporal Anchor`.
- **Tecnologia / Futuro**: `Nano Alloy Ingot`.
- **Viaggio nel tempo**: `Chrono Bracelet` (stile *Quantum Leap*).
- **Magia**: `Arcane Staff` (colpisce il bersaglio nel cono visivo con un "hex bolt").
- **Sovrannaturale**: `Vampiric Elixir`, `Werewolf Elixir`.
- **Supereroi (archetipo)**: `Hero Serum`.

## Come si usa (in gioco)
- **Chrono Bracelet**
  - `Shift + tasto destro`: salva un **Time Marker** (posizione + dimensione + orario).
  - `Tasto destro`: esegue il **leap** e torna al marker (teleport + imposta l'orario salvato).

- **Arcane Staff**
  - `Tasto destro`: lancia un incantesimo sul bersaglio davanti a te (Wither + Lentezza).

## Build della mod (jar)
1. Installa **Java 21**.
2. Apri il terminale nella cartella del progetto.
3. Esegui:
   - Windows: `gradlew.bat build`
   - Linux/Mac: `./gradlew build`
4. Trovi il jar in: `build/libs/`

## Installazione
- Installa **Minecraft Forge 1.21.4**.
- Copia il jar in `.minecraft/mods/`.

---

## Note per lo sviluppo (README di progetto aggiornato)
Questa base è pensata per essere estesa con:
- Strutture (Chrono Lab), dimensioni, mob (vampiri/lupi mannari), abilità da supereroe, gadget avanzati, ecc.

In questo repository è stato avviato un refactor architetturale che introduce un'architettura a "systems":
- `systems/race` — tipi e metadata delle razze (RaceType, RaceData, RaceManager, RaceCapability)
- `systems/player_state` — PlayerArcanaData e PlayerStateSystem per centralizzare lo stato del giocatore
- `systems/ability` — AbilitySystem + CooldownManager per gestire l'uso delle abilità dagli item
- `systems/transformation` — TransformationSystem per orchestrare trasformazioni (vampiro/lycanthrope)
- `systems/corruption` — CorruptionSystem per gestire la corruzione del giocatore

Branch di riferimento per il refactor: `refactor/core-architecture`.

### Cosa manca ancora
- Persistenza e sincronizzazione di `PlayerArcanaData` (va agganciata ai giocatori con capability / CustomData).
- Migrazione della logica presente in `content/effect` e `content/entity` verso i nuovi systems.
- Rifattorizzazione degli item (`ArcaneStaffItem`, `ChronoBraceletItem`, `EssenceElixirItem`) per delegare la logica all'`AbilitySystem`/`TransformationSystem`.

### Consigli per il testing locale
- Lancia Minecraft con Forge e carica la mod compilata tramite `./gradlew build`.
- Controlla i log per `INFO futurearcanaheroes: Registered basic race data and biome.` nella fase di setup.
- Prosegui con test manuali delle funzionalità (Chrono Bracelet, Arcane Staff, elisir).

---

Se vuoi, posso applicare ora la persistenza di `PlayerArcanaData` (attach capability / CustomData), e completare la migrazione della logica di vampirismo e lycanthropy ai nuovi systems. Fammi sapere quale passo vuoi che esegua dopo questa modifica.
