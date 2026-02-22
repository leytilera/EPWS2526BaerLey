# Probleme und Risiken

**Version:** 2.0

## Architekturelle Risiken

### 1. Federation funktioniert instanzübergreifend nicht stabil (Activitypub)
**Risiko:**
- Federation-Ereignisse werden zu langsam, unvollständig oder fehlerhaft synchronisiert
- Security-Aspekte (gefälschte Anfragen, nicht-vertrauenswürde Clients)
<br>  

### 3. State-Synchronisation bei Verbindungsabbrüchen
Bei einem Verbindungsabbruch müssen Brettzustand), Zeitkontrolle und Spielstatus in wenigen Millisekunden korrekt rekonstruiert werden  

**Risiko:** Spieler A sieht einen anderen Zustand als Spieler B  
<br>  

## Risiken in der Kommunikation

### 1. Konflikte zwischen Client-seitiger und Server-seitiger Zugvalidierung
Jocly validiert lokal, der Server validiert zentral (allerdings ebenfalls über Jocly). 

**Risiko:** Client sagt "gültig", Server sagt "ungültig" → eventuell UX-Brüche und logischer Konflikt  
<br>  

### 2. Uneinigkeiten von Instanzen
Wenn zwei Spieler von unterschiedlichen Instanzen miteinander spielen wollen, müssen sich beide Instanzen einig sein, wie Move-Validierung und Timekeeping gehandhabt wird.  
Eine Möglichkeit wäre, eine autorative Instanz als Single-Source-of Truth zu bestimmen. Die andere Instanz kann selber noch eine lokale Uhr führen und Partien protokollieren, aber nur die autorative Instanz darf Züge und Zeitänderungen offiziell bestätigen. Dementsprechend muss festgelegt werden, wie die Entscheidung getroffen wird, welche Instanz die Rolle der autorativen Instanz einnimmt.   

Überlegungen haben bisher folgende Möglichkeiten ergeben:
- Automatische, deterministische Zuweisung der authorativen Instanz, beispielsweise ist immer die Instanz des Spielers, der die Partie anfragt, automatisch Single-Source-Of-Truth. 
- Zwei-Seiten-Agreement, das heißt beide Instanzen einigen sich manuell bei Spielstart darüber, welche Instanz die Autorität ist. 
- Verteilter Konsens/externer Server als Autorität: das Spiel wird über mehrere Server repliziert und Zeitänderungen 

**Risiko:** 
- Instanzen können sich nicht darauf einigen, wer die autorative Instanz ist bzw. wie Move-Validierung und Timekeeping gehandhabt werden oder sind nicht einverstanden mit automatischen Entscheidungen
- Instanzen haben unterschiedliche Systemzeiten (es sollte unbedingt dafür gesorgt werden, dass alle Server ihre Systemzeit einheitlich, bestätigt korrekt und stabil halten (Network Time Protokol (RFC 5905) ist Standard für die Zeitsynchronisation im Internet)
<br>  

## Technische Risiken

### 1. Persistenz & Datenkonsistenz
- Das Standardformat für die Dokumentation von Schachpartien/gespielten Zügen ist die PGN (Portable Game Notation). Jocly verwendet allerdings eine numerische Notation für die Identifikation der einzelnen Spielfelder auf dem Brett. 
- Eine Konvertierung von Spielzügen in unterschiedliche Datenformate muss korrekt und verlustfrei erfolgen, sodass Partien korrekt in der Datenbank gespeichert und beim Abrufen vom Client korrekt rekonstruiert werden können bzw. es muss eine einheitliche Notation verwendet werden.
  
**Risiko:** inkompatible Notationen, verlorene Züge, fehlerhafte Historie, fehlerhafte Datenbankzustände und Rekonstruktion nicht möglich  
<br>  

### 2. Jocly als veraltete Bibliothek
Der letzte Commit in Jocly ist zwei Jahre alt.  

**Risiko:** Mögliche Bugs bleiben ungelöst  
<br>  

## Kompetenzbezogene Risiken

### 1. Unerfahrenheit mit ActivityPub
Wir beide (oder zumindest eine von uns beiden) haben/hat noch keine Erfahrung mit Federation.  

**Risiko:** Fehlende Erfahrung kann zu Unsicherheiten, längerer Entwicklungszeit und fehleranfälliger Entwicklung führen.  
<br>  

### 2. Falsche Zeitplanung
Wir schätzen unseren zeitlichen Rahmen bzw. den Entwicklungsaufwand falsch ein, nehmen uns zu viel vor oder setzen Prioritäten falsch

**Risiko:** Wir schaffen die Entwicklung zeitlich nicht



