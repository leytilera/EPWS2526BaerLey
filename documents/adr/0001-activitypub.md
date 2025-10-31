# ADR-0001: ActivityPub als Federation Protokoll

**Status:** Proposed
**Datum:** 2025-10-31  

---

## Kontext
Um die Kommunikation zwischen Instanzen zu ermöglichen, muss ein Federation Protokoll festgelegt werden.
Dabei muss zum einen die Kommunikation währen der Spiele berücksichtigt werden (Spieler spielt einen Zug),
zum anderen auch die Kommunikation außerhalb der Spiele (Spieler schaut sich das Profil eines anderen Spielers an,
Spieler sucht anderen Spieler für ein Spiel).

## Optionen
Neben der Möglichkeit, ein eigenes Federation Protokoll zu spezifizieren, stehen bereits
ActivityPub, Matrix und XMPP als etablierte und erweiterbare Federation Protokolle zur verfügung.

## Entscheidung
Während Matrix und XMPP für die Kommunikation während eines Spiels gut geeignet wären, lässt sich die
Kommunikation außerhalb von Spielen damit nur schlecht umsetzen. Mit einem eigenen Protokoll oder ActivityPub
lässt sich hingegen sowohl die Kommunikation während Spielen als auch die Kommunikation außerhalb von Spielen
gut umsetzen. Ein eigenes Protokoll würde zwar möglicherweise weniger Aufwand bei der Implementation bedeuten,
gleichzeitig allerdings auch mit mehr Aufwand bei der Spezifikation einhergehen. Daher ist der Vorschlag, das
im Federation Bereich bereits gut etablierte ActivityPub als Federation protokoll zu benutzen.

## Folgen
ActivityPub muss in der Anwendung implementiert werden und um die Schachspezifischen `Actors`, `Activities` und `Objects`
erweitert werden. Dabei kann folgende [Anleitung](https://socialhub.activitypub.rocks/pub/guide-for-new-activitypub-implementers)
verwendet werden. Die Verwendung von ActivityPub ermöglicht in Zukunft, Interoperabilität mit anderen
Fediverse Anwendungen herzustellen, falls dies gewünscht ist und sinnvoll eingesetzt werden kann.