# ADR-0004: Postgres als Datenbank

**Status:** Accepted
**Datum:** 2025-11-28  

---

## Kontext
Um persistente Daten wie Nutzer oder Spiele zu speichern ist eine Datenbank notwendig.

## Optionen
Als Relationale Datenbanken stehen MySQL, Postgres, SQLite, Oracle und Microsoft SQL Server
zur Auswahl. Alternativ wären auch NoSQL Ansätze wie Redis oder MongoDB, oder das direkte
Speichern in Dateien ohne Datenbank möglich.

## Entscheidung
Das direkte schreiben in Dateien ist komplex zu implementieren und in der Regel weniger
performant als ein bestehendes Datenbanksystem zu benutzen, daher fällt diese Option raus.
Alternative NoSQL Ansätze bieten bei unserer Anwendung keine Vorteile gegenüber SQL Datenbanken.
Da unser Projekt darauf ausgelegt ist, von verschiedenen Self-Hostern betrieben zu werden
fallen properitäre Datenbanksysteme wie Oracle und Microsoft SQL Server ebenfalls weg.
Postgres und MySQL sind beides häufig verwendete Datenbanken, die von vielen Anwendungen
unterstützt werden, allerdings wird im Open Source Bereich heutzutage in der Regel
Postgres verwendet. Aus diesem Grund haben wir uns für Postgres als Datenbank entschieden.


## Folgen
Die `PostgreSQL Driver` Dependency muss zum Spring Boot Projekt hinzugefügt werden.
Außerdem muss beim Deployment eine Postgres Datenbank bereitgestellt werden.