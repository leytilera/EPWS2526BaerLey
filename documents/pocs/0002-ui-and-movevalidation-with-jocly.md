# POC-002 Jocly als Schachbibliothek

## Ziel:
Das Ziel dieses PoCs ist es zu prüfen, ob Jocly als Schachbibliothek zur clientseitigen Anzeige eines User Interfaces und zur client- und serverseitigen Validierung von gespielten Zügen in einer federierten Umgebung zuverlässig funktioniert. Es soll getestet werden, ob sich mit Jocly ein interaktives Schachbrett im Browser anzeigen lässt, ob gespielte Züge korrekt über ActivityPub übertragen und serverseitig validiert werden können und ob gespielte Züge in PostgreSQL gespeichert werden können. 

## Vorbedingungen:
Es wird eine Datenbank benötigt, um Move-Events persist speichern zu können. Wie im Adr definiert, soll für dieses Projekt PostgreSQL als Datenbank verwendet werden.   
Ebenfalls sollte die grundlegende Kommunikation über Activityhub bereits funktionieren (POC-001 sollte erfolgreich durchgeführt worden sein).   
Es müssen minimale Nutzerprofile existieren (können aus POC-001 übernommen werden).  

## Kontext
Um dieses POC durchführen zu können, wird folgendes benötigt:
- 2 Instanzen (A und B)
- 2 Beispielnutzer
- Endpunkte für ActivityPub
- Integration von Jocly (client- und serverseitig)
- Integration von PostgreSQL mit einfachen Tabellen
- minimale Anwendungslogik

## Implementierung:

### PostgreSQL

Für PostgreSQL werden folgende Datenstrukturen benötigt (Vorschlag, muss gff. noch angepasst werden): 
````
-- Spieler/Akteure (kann Actor URI der ActivityPub Node halten)
CREATE TABLE actors (
  actor_id SERIAL PRIMARY KEY,
  actor_uri TEXT NOT NULL UNIQUE,     -- z.B. https://chess.example.org/actor/alice
  display_name TEXT,
  created_at timestamptz DEFAULT now()
);

-- Spiele (Metadaten)
CREATE TABLE games (
  game_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  external_id TEXT,                    -- optional: id aus ActivityPub/Game URI
  host_instance TEXT NOT NULL,         -- Host-Instanz (autoritative Instanz für dieses Spiel)
  white_actor_id INT REFERENCES actors(actor_id),
  black_actor_id INT REFERENCES actors(actor_id),
  initial_fen TEXT,                    -- Startposition (optional; hilft beim Rekonstruieren)
  status TEXT,                         -- 'ongoing' | 'mate' | 'draw' | 'resign' | 'timeout'
  created_at timestamptz DEFAULT now()
);

-- Moves
CREATE TABLE moves (
  move_id BIGSERIAL PRIMARY KEY,
  game_id UUID NOT NULL REFERENCES games(game_id) ON DELETE CASCADE,
  ply INT,                             -- half move number (wird mit jedem Move um eins inkrementiert, ist also 1, 2, 3, ...)
  played_by_actor INT REFERENCES actors(actor_id),
  from_sq INT,                         -- ursprüngliches Feld, auf dem die gezogene Figur vorher gestanden hat
  to_sq INT,                           -- neues Feld, auf das Figur gezogen ist
  capture_sq INT,                      -- Figur, die geschlagen wurde
  promotion CHAR(1),                   -- 'Q','K','R','P','N' or NULL -> Figur
  check_flag BOOLEAN,                  -- true wenn Schach, sonst false
  enpassant BOOLEAN,                   -- true wenn en-passant stattgefunden hat
  raw_json JSONB NOT NULL,             -- originales Jocly move object
  created_at timestamptz DEFAULT now()
);

-- Index für schnelle Suche der letzten Züge / Spielzustand
CREATE INDEX idx_moves_game_ply ON moves(game_id, ply DESC);
````

### ActivityPub
Im Backend (Spring Framework) müssen folgende Endpunkte implemnetiert werden: 

````
POST /users/{username}/inbox
````
````
POST /users/{username}/outbox
````
````
GET users/{username}
````
<br>  
  
Activity zur Repräsentation eines gespielten Schachzuges: 
````
{
  "@context": "https://www.w3.org/ns/activitystreams",
  "id": "{{ACTIVITY_URI}}",               // URI für diese Activity (unique), z.B. "https://chess.example.org/activities/uuid-1234"
  "type": "Create",
  "actor": "{{ACTOR_URI}}",               // Actor der diese Activity erstellt hat, z.B. "https://chess.example.org/actor/alice"
  "published": "{{ISO_TS}}",              // ISO-8601 UTC timestamp, wenn Activity erstellt wurde
  "to": [ "{{RECIPIENT_URI_OR_COLLECTION}}" ],  // Actor, an den diese Activity gesendet werden soll (in dem Fall URI des anderen Spielers) 
  "object": {
    "id": "{{OBJECT_URI}}",               // ID (unique) des Move-Objects
    "type": "ChessMove",              
    "game": "{{GAME_URI}}",               // URI oder interne ID der zugehörigen Partie
    "playedBy": "{{ACTOR_URI}}", 
    "jocly": {{JOCLY_MOVE_OBJECT}},       // originales Jocly-Move-Object 
    "published": "{{ISO_TS}}"             // Timestamp
  }
}
````
``jocly`` enthält das originale JSON, das von Jocly exportiert wurde. Das vermeidet Informationsverlust und stellt sicher, dass es auf dem anderen Client genauso ausgelesen und angezeigt werden kann.  
<br>  

### Jocly
Jocly muss sowohl client- als auch serverseitig eingebunden werden. 
Eine Anleitung ist als README.md im github von Jocly zu finden: https://github.com/aclap-dev/jocly

Anschließend muss eine minimale ``index.html`` Datei angelegt werden, um das Interface von Jocly im Browser anzeigen zu können.  
<br>  

### 2 Nutzer und 2 Instanzen
Es kann Docker Compose verwendet werden, um 2 Instanzen und zwei Nutzer zu erzeugen. 

---

## Durchführung:
Als Erstes müssen die notwendigen Datenstrukturen und Endpunkte implementiert werden (siehe oben).

Anschließend sollen mehrere Funktionalitäten isoliert getestet werden. Es empfiehlt sich, die folgenden Aufgaben nacheinander und iterierend durchzuführen. Es müssen aussagekräftige Logs an sinnvollen Stellen hinzugefügt werden, um die Abläufe nachzuvollziehen und Ergebnisse zu dokumentieren. 

Als Testpartien können folgende Spielzüge gespielt werden:  
weiß: e2 -> e3  
schwarz: e7 -> e5  
weiß: Bishop f1 -> c4  
schwarz: Knight b8 -> c6  
weiß: Queen d1 -> h5  
schwarz: Knight g8 -> f6  
weiß: Queen h5 -> f7 -> Checkmate  
<br>  

### 1. Board Rendering/UI (Clientseitig)
**Ablauf:**
1. Falls noch nicht erfolgt, muss Jocly installiert und eine minimale ``index.html``-Datei angelegt werden, um das Jocly Interface im Browser anzeigen zu können (``<script src="/node_modules/jocly/dist/browser/jocly.js"></script>``)

**Exit-Kriterien:**
- Der Browser lädt die notwendigen .js files und zeigt ein interaktives Schachbrett im Browser an.
- Es kann ein neues Spiel gestartet werden. Beim Klicken auf einzelne Figuren werden alle Felder markiert, auf welche diese Figur legal ziehen kann. Es sind nur zulässige Züge möglich.
- Beim Ziehen wird ein Move-Event erzeugt. 

**Fail-Kriterien:**
- Die benötigten Files können nicht gefunden/geladen werden, wodurch Jocly nicht starten kann. 

**Fallback:**
- Keine.

**Exit-Kriterien:**
1. Die von Jocly erzeugten Move-Events können erfolgreich in der Datenbank gespeichert werden.
<br>  

### 2. Instanzübergreifendes Spielen
**Erläuterung:**
Dieser Abschnitt dient nur dazu zu prüfen, ob gespielte Züge korrekt über ActivityPub zwischen zwei Instanzen übertragen werden können. Serverseitige Move-Validierung wird noch nicht integriert. Züge können lokal gespeichert werden, oder falls sinnvoll kann Aufgabe 3 parallel durchgeführt werden, und Züge können in Postgres persistiert werden (server- und/oder clientseitig). 

**Ablauf:**
1. Falls noch nicht erfolgt, müssen die für Activitypub benötigten Endpunkte implemenentiert werden sowie 2 Beispielnutzer angelegt werden. Desweiteren müssen 2 Instanzen existieren, z.B. indem zwei Services (Instanz-A und Instanz-B) über Docker Compose gestartet werden. 
2. Falls noch nicht erfolgt, muss Anwendungslogik implementiert werden, um jeden gespielten Zug von Jocly zu exportieren und in eine JSON Activity einzubetten.
3. Anschließend kann die Activity signiert werden (Code dafür aus POC-001 übernehmen) und per POST Request an die Outbox mit dem jeweils anderen Nutzer als Ziel gesendet werden.
4. Auf der jeweils anderen Instanz muss (falls verwendet) die Signatur geprüft werden, der gespielte Zug aus der JSON Activity extrahiert werden und korrekt auf dem eigenen Spielfeld adrgestellt werden. Notwendige Anwendungslogik muss dafür minimal implemnetiert werden.

Eine Übersicht über alle enthaltenen Funktionen in Jocly ist hier zu finden: https://github.com/aclap-dev/jocly/wiki/Match-API-Object

**Exit-Kriterien:**
- Gespielte Züge können in eine JSON Activity eingebettet/konvertiert werden und über ActivityPub an eine andere Instanz gesendet werden. 
- Die jeweils andere Instanz kann den gespielten Zug korrekt aus der Activity extrahieren und rekonstruieren. 

**Fail-Kriterien:**
- Die gespielten Züge können nicht in eine JSON-Activity konvertiert werden oder es fehlen Felder/es sind fehlerhafte Felder dabei. 
- Die gespielten Züge können nicht korrekt aus der JSON-Activity extrahiert werden und eine Rekonstruktion schlägt fehl (der Zug wird nicht im Interface des anderen Clients angezeigt) 

**Fallback:**
- Pflichtfunktion, muss funktionieren. Sonst müssen andere Schachbibliotheken in Betracht gezogen werden.
<br>  

### 3. Speicherung von Moves/gespielten Partien
**Ablauf:**
1. Falls noch nicht geschehen, müssen die notwendigen Datenstrukturen für Postgres implementiert werden (siehe oben).
2. Es wird ein neues Spiel gestartet. Jeder Zug wird sowohl auf server- als auch auf clientseite in Postgres gespeichert.

**Exit-Kriterien:**
- Eine Abfrage über alle Spalten in Postgres liefert korrekt alle gespielten Züge in Postgres, inklusive Metainformationen (Nutzer, Timestamp, etc.).

**Fail-Kriterien:**
- Es treten Fehlermeldungen beim Speichern in Postgres auf.
- Eine Abfrage über alle Spalten in Postgres liefert keine oder fehlerhafte Ausgaben.
<br>  

### 4. Serverseitige Move-Validierung
**Erläuterung:**
Auch wenn Jocly im Browser-Interface nur gültige Züge ausführen lässt, d.h. clientseitig nur valide Spielzüge zulässig sind, sollten Clients grundsätzlich als nicht vertrauenswürdig erachtet werden, weshalb eine zusätzliche serverseitige Validierung gespielter Züge erfolgen sollte. Dieser Abschnitt hat das Ziel, auszuprobieren, welche Möglichkeiten es mit Jocly für die serverseitige Move-Validierung gibt. Im folgenden wird ein Implementierungsvorschlag gemacht, aber die Implementierung kann noch abgeändert werden. 
Dieser Schritt basiert auf den vorherigen Schritten.

Implementierungsvorschlag (Pseudo-Code):
````
1. Authentificate Client
2. Parse Activity: extract ``object`` (``chessMove``), ``game``, ``id``, ``jocly`` field
3. Reconstruct game status from database (postgres): ``games`` + ``confirmed moves`` or use
4. Call ``validate()`` with current state + incoming move, use ``getPossibleMoves()``-function from the Jocly library, if
   ``legal == false`` -> respond 400, store evidence
   ``legal == true`` -> insert into ``moves``-table, update games row if needed (e.g. ``fen_after``) 
6. POST the same activity to recipients inbox (resolve by fetching actors json for ``to`` recipient)
7. At other intance: receive activity, verify signature, store at database (postgres) and notify local client
````

**Ablauf:**
1. Es wird ein neues Spiel gestartet und ein Server als Single-Source-Of-Truth bestimmt. Wie diese Entscheidung getroffen wird, muss noch entschieden werden, im Rahmen dieses POCs kann die anfragende Instanz diese Rolle einnehmen. 
2. Jeder Zug, der ausgeführt wurde, wird vom Server geprüft. Dafür wird sowohl die Signatur der Nachricht geprüft als auch die Zulässigkeit des Zuges. 
3. Wenn beide Überprüfungen erfolgreich waren, wird der Zug bestätigt und persistiert.
4. Anschließend wird der Zug als Activity an die Inbox des anderen Nutzers auf der anderen Instanz gesendet.
5. Dort wird der Zug extrahiert und in der Datenbank gespeichert.

Falls möglich kann versucht werden, ungültige Züge an den Server zu senden, indem manuell eine Activity mit einem ungültigen Zug erzeugt wird. 

**Exit-Kriterien:**
- Bei gültigen Zügen sollte die Überprüfung erfolgreich sein und die Züge sollten gespeichert und an die andere instanz gesendet werden.
- Ungültige Züge sollten abgelehnt werden und einen Fehlercode zurückliefern. 

**Fail-Kriterien:**
- Eine serverseitige Validierung der Züge ist nicht möglich oder Jocly kann serverseitig nicht eingebunden werden. 
- Auch ungültige Züge werden zugelassen, gespeichert und weitergeleitet. 

**Fallback:**
- Es wird eine alternative Chess Engine für die serverseitige Validierung verwendet (z.B. Chess.js, da diese auch mit node.js funktioniert). Clientseitig kann weiter Jocly genutzt werden. In diesem Fall müsste ein neuer POC definiert werden. 
- Es wird ein anderer Mechanismus zur Validierung von Zügen verwendet.







