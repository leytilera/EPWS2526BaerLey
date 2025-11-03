# PoC: Architektur- und Technologieentscheidung für die dezentrale Schachplattform
 
**Datum:** 30.10.2025  
**Version:** 1.0

## 1. Ziel des PoC
Dieses PoC dokumentiert die technischen Entscheidungen und das konzeptionelle Design für die dezentrale Schachplattform, welche wie im Laufe dieses Projekts entwickeln wollen. Das Ziel ist es, eine nachvollziehbare Basis zu schaffen, auf der Implementierung, Tests und weitere Architektur-Entscheidungen aufbauen.
Das PoC orientiert sich am geplanten MVP:
- Standard-Schach,
- Realtime-Partien (synchron),
- PGN-Export,
- Analyse (post-game)

## 2. Annahmen und Scope (MVP)
- Fokus: Standard Chess (reguläres Schach), optional Chess960 als zusätzliche Spielvariante
- primärer Modus: synchrone (Realtime Partien), asynchrone Partien können im weiteren Verlauf als Erweiterung hinzugefügt werden
- Authentifizierung/Autorisierung: Anmeldung mit OpenID Connect

## 3. Technologien 

### Übersicht
- **Frontend:** HTML + JavaScript, [Jocly](https://github.com/aclap-dev/jocly) für das Board, alternativ [cm-chessboard](https://github.com/shaack/cm-chessboard) oder [chessboard.js](https://github.com/oakmac/chessboardjs/)
- **Client-Side Spiellogik:** [chess.js](https://github.com/jhlywa/chess.js) (JS) für Move-Validierung, FEN/PGN-Handling; alternativ selbst Logik implementieren 
- **Backend:** Kotlin (JVM) für REST/WebSocket API — Backend ist server-authoritative; Move-Validierung kann serverseitig per Node-Microservice (chess.js) oder eigener Logik erfolgen
- **Analyse-Engine:** Stockfish (separater Prozess) für Post-Game Analysen (über UCI) 
- **Persistence:** PostgreSQL (Games, Users, Moves, Meta), PGN als persistentes Feld + Storage für Audit/Logs
- **Messaging / Realtime:** WebSocket zwischen Client ↔ Server; ActivityPub oder eigene Spezifikation als Federation-Protokoll
- **Deployment (PoC):** Docker Compose zur lokalen Reproduktion

### Kurze Beschreibungen der Bibliotheken
- **chess.js:** chess.js ist eine TypeScript-Schachbibliothek zur Generierung und Validierung von Schachzügen, zur Platzierung und Bewegung von Figuren sowie zur Erkennung von Schach, Schachmatt und Patt. (Quelle: [chess.js](https://jhlywa.github.io/chess.js/))
- **ch-chessboard:** cm-chessboard ist ein lightweight, ES6-modulbasiertes, responsive JavaScript-Schachbrett. Züge können per Klick oder DRAG and Drop ausgeführt werden, das Styling kann über CSS angepasst werden und es werden mehrere Figurensätze unterstützt. Für Rendering wird SVG verwendet und es können Erweiterungen hinzugefügt werden. (Quelle: [ch-chessboard](https://www.npmjs.com/package/cm-chessboard))
- **chessboard.js:** chessboard.js ist eine JavaScript-Bibliothek zur Darstellung eines Schachbretts. Es kann zusammen mit der chess.js-Bibliothek verwendet werden, um Spiellogik + Darstellung zu kombinieren. (Quellen: [github](https://github.com/oakmac/chessboardjs/), [Dokumentation](https://chessboardjs.com/docs))
- **Jocly:** Jocly ist eine Bibliothek und ein Toolset um Brettspiele in Web-Umgebungen zu integrieren. Es enthält die Spiellogik sowie 2D und 3D Benutzeroberflächen für eine Vielzahl von Strategiespielen, unter anderem Schach. Ebenfalls enthalten ist KI als Spielgegner, das wird allerdings für unser Projekt aktuell nicht benötigt. (Quelle: [Jocly](https://github.com/aclap-dev/jocly)) 
- **Stockfish:** Stockfish ist eine open-source Chess-Engine, welche durch das Universal Chess Interface (Offenes Protokoll für die Kommunikation zwischen Engines und User Interfaces) verwendet werden kann, um Spielanalysen durchzuführen. Es nutzt ein neuronales Netzwerk, um Positionen auf dem Schachbrett auszuwerten. Stockfish wurde überwiegend in C++ geschrieben, kann aber in Javascript kompiliert werden, um im Browser ausgeführt zu werden. (Quellen: [github](https://github.com/official-stockfish/Stockfish), [offizielle Website](https://stockfishchess.org/about/), [Wikipedia](https://en.wikipedia.org/wiki/Stockfish_(chess)))
