# ADR-0002: Spring Boot als Backend Framework

**Status:** Accepted
**Datum:** 2025-11-28

---

## Kontext
Für das Backend wird ein entsprechendes Framework benötigt.

## Optionen
Als JVM basierte Frameworks stehen Spring Boot, Javalin, Quarkus oder Micronaut
zur Verfügung. Alternativ wäre es möglich ein Framework für eine andere Programmiersprache
zu verwenden.

## Entscheidung
Wir haben uns dafür entschieden, ein JVM basiertes Framework zu verwenden, da beide
Teammitglieder mit JVM kompatiblen Sprachen vertraut sind und sonst je nach Sprache diese
erst gelernt werden muss. Javalin wäre auch ein interessantes Framework, fällt allerding raus,
da wir damit die Datenbankverbindung selbst implementieren müssten, was zusätzlichen Aufwand 
für das Projekt ergeben würde. Aus den drei übrigbleibenden Frameworks haben wir und für 
Spring Boot entschieden, da wir beide damit bereits vertraut sind.

## Folgen
Als Programmiersprache für des Backend ist eine JVM kompatible Sprache nötig.
Mögliche Sprachen wären Java, Haxe, Scala, Kotlin, Groovy und Clojure.