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
- Authentifizierung/Autorisierung: mindestens lokale Accounts; falls genügend Zeit & Ressourcen vorhanden, kann später 0Auth hinzugefügt werden 

## 3. Technologien 
- **Frontend:** HTML + JavaScript, Jocly für das Board, alternativ cm-chessboard oder chessboard.js
- **Client-Side Spiellogik:** chess.js (JS) für Move-Validierung, FEN/PGN-Handling; alternativ selbst Logik implementieren 
- **Backend:** Kotlin (JVM) für REST/WebSocket API — Backend ist server-authoritative; Move-Validierung kann serverseitig per Node-Microservice (chess.js) oder python-chess erfolgen.
- **Analyse-Engine:** Stockfish (separater Prozess) für Post-Game Analysen (UCI) 
- **Persistence:** PostgreSQL (Games, Users, Moves, Meta), PGN als persistentes Feld + Storage für Audit/Logs
- **Messaging / Realtime:** WebSocket zwischen Client ↔ Server; Federation muss noch festgelegt werden
- **Deployment (PoC):** Docker Compose zur lokalen Reproduktion

