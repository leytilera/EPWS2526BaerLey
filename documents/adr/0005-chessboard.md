# ADR-0005: Jocly als Chessboard Bibliothek

**Status:** Proposed
**Datum:** 2025-11-28  

---

## Kontext
Für das Frontend wird eine Bibliothek benötigt, mit welcher sich ein interaktives
Schachbrett anzeigen lässt.

## Optionen

### Jocly
Vorteile:
- Jocly unterstützt neben klassischem Schach eine große Bibliothek an Schachvarianten
- Jocly zeigt dem Nutzer an, welche Züge möglich sind
- Eine Zugvalidierung kann zusätzlich bereits auf dem Client stattfinden, damit dem
Server möglichst nur gültige Züge gesendet werden (der Server überprüft die Züge trotzdem)
- Kann als Bibliothek zur Zugvalidierung verwendet werden
- Es lässt sich sowohl eine 2D als auch eine 3D Ansicht nutzen

Nachteile:
- Jocly unterstützt nur klicken als Interaktion. Das "ziehen" von Figuren ist nicht möglich.
- Der letzte Commit ist von vor 2 Jahren

### cm-chessboard
Vorteile:
- cm-chessboard wird regelmäßig weiterentwickelt
- Das "ziehen" von Figuren ist möglich
- Der Nutzer kann nur für sich sichtbare Pfeile und Markierungen auf dem Schachbrett markieren
- Gültige Züge lassen sich anzeigen, wenn eine entsprechende Bibliothek zur Validierung
verwendet wird

Nachteile:
- Für eine Clientseitige Zugvalidierung müsste eine weitere Bibliothek verwendet werden
- Es werden keine klick-basierten Interaktionen unterstützt, sondern nur das "ziehen" von
Figuren

### chessboardjs
Vorteile:
- Sowohl klick-basierte interaktionen als auch das "ziehen" von Figuren ist möglich
- Gültige Züge lassen sich anzeigen, wenn eine entsprechende Bibliothek zur Validierung
verwendet wird

Nachteile:
- Der letzte Commit ist von vor 3 Jahren
- Für eine Clientseitige Zugvalidierung müsste eine weitere Bibliothek verwendet werden

## Entscheidung
Jocly bietet durch die Unterstützung von Schachvarianten, clientseitige Zugvalidierung und
die Möglichkeit einer 3D Ansicht ein gutes Gesamtpaket. Bei Bedarf würde sich später auch
noch die Möglichkeit implementieren lassen, zusätzlich cm-chessboard zu verwenden, falls
der Nutzer eine andere Interaktionsmöglichkeit wünscht oder Markierungen auf den Schachbrett
platzieren möchte.

## Folgen
Der Frontend muss mit einem Tool verwaltet werden, welches NPM Dependencies verwenden kann.