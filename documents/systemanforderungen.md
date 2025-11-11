# Systemanforderungen

**Datum:** 11.11.2025  
**Version:** 1.0

## Ziel  
Diese kompakte Tabelle definiert die Anforderungen an unser System, welche aus den Bedürfnissen der in der Zielgruppenanalyse segmentierten Nutzergruppen abgeleitet sind. Technische Details sind noch nicht final und können im weiteren Verlauf des Projekts noch angepasst werden.

---

| ID | Kurzbeschreibung | Nutzergruppen | Detaillierte Beschreibung | Priorität | 
| --- | --- | --- | --- | --- |
| REQ-001 | Realtime-Moves & Zugbestätigung 	| alle | Bidirektionale, persistente Verbindung (z.B. Websockets) für die Übertragung von Spielzügen, Zug muss beim Gegner innerhalb akzeptabler UI-Latenz angezeigt werden	| MUST |
| REQ-002 | Server-authorative Move-Validierung		| alle | Moves müssen serverseitig validiert werden, der Server entscheidet bei Konflikten, optional: Clients zeigen sofortiges UI-Feedback nach Zug, aber müssen anschließend auf serverbeständigen Zustand synchronisieren (d.h. falls Server Zug ablehnt, muss der Client korrigieren)	| MUST |
| REQ-003 | PGN-Export und standardkonformes Persistieren	| TS, TAS, VS | Jede Partie wird als standardkonformes PGN gespeichert (= Metadaten + Züge) und ist exportierbar/importierbar 	| MUST |  
| REQ-004 | Persistente Audit-Logs & Prüfprotokolle 	| TS, VS, VB | Vollständige Historie (gespielte Partien als PGN, Session/Connection-Metadaten (gff. anonymisiert), Server-Entscheidungen (z.B. Validierungsentscheidungen), Admin-Eingriffe (z.B. Reports, Bans, ...), Aufbewahrungs- & Retention-Policy | COULD |
| REQ-005 | Basis-Fairplay 	| alle | Automatische Prüfung von Reaktionsmustern und Post-Game-Analyse über Stockfish, automatischer Report bei ungewöhnlichem Spielverhalten | COULD |
| REQ-006 | PGN-basierte Post-Game-Analyse (Stockfish)		| alle | Unbegrenzte Post-Game Analyse mit Stockfish über UCI, exportierbar | SHOULD | 
| REQ-007 | Präzise Zeitkontrolle & Timeout-Konsens 	| alle | Deterministische Zeitkontrolle (Server-Timestamps) und klare Time-Out-Regeln | MUST |
| REQ-008 | Matching 	| HS, TS, TAS | automatisiertes Matching basierend auf Rating, Spielvariante, Zeitbegrenzungen; Fallback-Rules falls kein passendes Match gefunden | MUST |
| REQ-009 | Reconnect & State-Synchronisierung 	| alle | Robuste Reconnect-Mechanismen falls Verbindung abbricht: automatische & korrekte Resynchronisierung von Board-Zustand & Zeitkontrolle (Abfrage über aktuellen Spielzustand vom Server und automatische Widerherstellung von lokalem Zustand) | MUST |
| REQ-010 | Transparente Instanz Metadaten 	| TS, TAS | Transparente, öffentliche Informationen über Instanzen (z.B. Instanzname, Betreiberkontakt, Terms/Moderation-Policy/Code of Conduct, Blocklist, Software-Version/Last-Updated/Uptime-Statistic, erwartete Zielgruppe, Anzahl aktiver Nutzer) für Nutzervertrauen | MUST |
| REQ-011 | Groups/Clubs 	| VS, VB | Möglichkeit, private Gruppen zu erstellen & zu verwalten | COULD |
| REQ-012 | Turniere	| TS, VB | Möglichkeit, Turniere zu organisieren und zu verwalten (sowohl geschlossen über Einladungen als öffentlich beitretbar) | COULD |
| REQ-013 | Ratings 	| alle | ELO-System zum Messen der Spielstärke von Spielern, Matchfindung und Ranglisten | SHOULD |
| REQ-014 | Rollenzuweisung & Verwaltung (Admin-Dashboard) 	| VB, SAB | Minimales UI-Dashboard mit Metriken, Rollenzuweisungen (Admin, Moderator, Mitglied), Rechtezuweisung, Mitgliederverwaltung (einladen, akzeptieren, bannen) | MUST/SHOULD |
| REQ-015 | Einfaches Deployment über Docker Compose | VB, TAB, L | Bereitstellung von allen fürs Deployment benötigten Dateien, README mit Installationsanleitung und Hinweisen, evt. externe Dokumentation | MUST | 

---

**Abkürzungen (Nutzergruppen aus der Zielgruppenanalyse):**  
HS = Hobbyspieler/Gelegenheitsspieler   
TS = Turnierspieler/Competitive-Spieler  
TAS = Technikaffine Spieler  
VS = Vereinsspieler/Spieler aus Schachgruppen  
TAB = Technikaffine Instanzbetreiber  
VB = Instanzbetreiber von Vereinen/Schachgruppen  
L = Lernende (Instanzbetreiber aus Lernzwecken)

