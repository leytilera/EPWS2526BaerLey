# User Stories

## Spieler*innen/

### Grundfunktionen

**US-001 - Spiel starten**  
Als angemeldeter Spieler möchte ich ein neues Spiel gegen einen anderen Spieler (lokal auf der gleichen Instanz oder cross-instance) mit wählbaren Parametern (Zeitbegrenzung, Variante, Rated/Unrated) starten können, damit ich sofort spielen kann. 

**US-002 - Züge & Validierung**  
Als Spieler möchte ich, dass nur vom Server validierte und regelkonforme Züge akzeptiert werden, damit die Spielregeln eingehalten werden und Integrität sichergestellt wird.

**US-003 - Timecontrol & Timekeeping**  
Als Spieler in einer Partie mit Zeitbeschränkung möchte ich eine verlässliche, von der Instanz koordinierte Zeitkontrolle haben (inklusive Berücksichtigung der Latenz), die nach jedem Zug korrekt reduziert wird, damit Fairplay gewährleistet ist.

**US-004 - Spiel Speichern & PGN-Export**  
Als Spieler möchte ich eine beendete Partie speichern und als PGN (Portable Game Notation) exportieren können, damit ich sie analysieren und teilen kann. 

**US-005 - Profil & Sichtbarkeit**  
Als Spieler möchte ich ein öffentliches Profil mit wählbaren Datenschutz-Optionen (öffentlich/nur eigene Instanz/privat) haben, damit ich selbst bestimmen kann, wer meine Spiele/Statistiken sehen kann.

**US-006 - Freunde**  
Als Spieler möchte ich andere Nutzer als Freunde hinzufügen können (lokal und cross-instance), damit ich ohne Aufwand ihre Profile einsehen und ihnen Spielanfragen schicken kann.


### Matching

**US-010 - Suche und Queue**  
Als Spieler möchte ich verfügbare Gegner nach Kriterien (z.B Rating, Spielvariante, Instanzfilter) per automatischem Matching oder manueller Suche finden, damit ich schnell einen passenden Gegner finden kann. 

**US-011 - gezielte Herausforderung**  
Als Spieler möchte ich gezielt einen anderen Spieler (von der gleichen oder einer anderen Instanz) herausfordern können, damit ich gemeinsam mit Freunden spielen kann.

**US-012 - Einladungen erhalten**   
Als Spieler möchte ich Einladungen für Spiele von anderen Spielern erhalten und akzeptieren/ablehnen können. 


### Analyse und Training

**US-020 - Post-Game-Analyse**  
Als Spieler möchte ich nach einem Spiel eine engine-basierte Analyse ausführen und diese als Download (PGN mit Annotationen) exportieren können, damit ich Fehler und Verbesserungen erkennen kann.

**US-021 - Schrittweise Widergabe von Partien**  
Als Spieler möchte ich vergangene Partien Zug-für-Zug widergeben können, um entscheidende Momente zu wiederholen.
 

### Rating und Fairness

**US-030 - Rating aktualisieren**  
Als Spieler möchte ich, dass nach jeder gewerteten Partie das Rating gemäß spezifizierter Algorithmusversion aktualisiert wird, damit Ratings konsistent, fair und vergleichbar bleiben.

**US-031 - Reporting**  
Als Spieler möchte ich einen anderen Spieler melden können (z.B. bei Verdacht auf Regelverstoß/Manipulation), damit der zuständige Betreiber dessen Instanz den Fall prüfen kann. 


## Instanzbetreiber*innen

**US-100 - Basis Konfiguration**  
Als Instanzbetreiber möchte ich beim Setup grundlegende Konfigurationsmöglichkeiten haben (Instanzname, Federation-Policy, Rating-Algorithmus-Version), damit meine Instanz korrekt im federierten Netzwerk sichtbar ist. 

**US-101 - Deployment**  
Als Instanzbetreiber möchte ich die Instanz via Docker-Compose mit minimalem Aufwand deployen können, damit ich auch ohne umfangreiches technisches Wissen eine Instanz betreiben kann.

**US-102 - Administration**  
Als Instanzbetreiber möchte ich Metrics (Anzahl aktiver Nutzer, Anzahl gespielter Spiele, Federation-Errors, etc.) einsehen können, damit ich den Betrieb überwachen und Reports prüfen kann. 


// weitere User-Stories für Instanzbetreiber werden noch ergänzt
 


