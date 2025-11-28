# ADR-0003: Gradle als Build-Tool

**Status:** Accepted
**Datum:** 2025-11-28  

---

## Kontext
Um das JVM Backend Projekt zu verwalten ist ein Build-Tool notwendig.

## Optionen
Als Build-Tools für JVM basierte Anwendungen stehen Maven und Gradle zur Auswahl.

## Entscheidung
Maven und Gradle bieten beide die Möglichkeit Dependencies aus Maven-Repositories einzubinden.
Mit beidem lassen sich Spring Projekte verwalten. Da Gradle als Build-Tool flexibler und 
performanter ist und heutzutage in den meisten JVM Projekten verwendet wird, haben wir uns für
Gradle entschieden.

## Folgen
Keine über die Verwendung des Build-Tools hinausgehende Folgen.