# Expose

## Projektthema

Die Projektidee ist, eine dezentrale/föderierte Platform zu entwickeln, auf der Nutzer miteinander Schach spielen können. Dabei sollen sowohl zwei Nutzer 
derselben Instanz, als auch zwei Nutzer von unterschiedlichen Instanzen miteinander spielen können. Der Fokus liegt dabei auf der Dezentralität der Platform.

## Features

- Spieler sollen mit anderen Spielern der gleichen oder einer anderen Instanz Schach spielen können
- Gespielte Spiele werden persistent gespeichert und können sich im nachhinein von anderen Nutzern angeschaut werden
- Nutzer können sich die Profile von anderen Nutzern über Instanzen hinweg anschauen
- Spieler können direkt einen anderen Spieler zu einem Spiel herausfordern
- Spieler können andere Spieler im dezentralen Netzwerk finden, die aktuell auch spielen wollen
- Spieler haben ein Rating, welches sich aus den bereits gespielten Spielen berechnet wird
- Das Rating wird beim Finden eines anderen Spielers berücksichtigt
- Es gibt die Möglichkeit, Spiele mit Zeitbegrenzung zu spielen

## Nutzen

- Jeder kann sich eine eigene Instanz aufsetzen, wodurch es keine Abhängigkeit auf eine zentrale Stelle gibt
- Nutzer können mit Nutzern auf anderen Instanzen spielen, ohne sich auf einer anderen Instanz anzumelden

## Probleme und Risiken

Für die Zeitbegrenzung bei Spielen muss festgelegt werden, zu welchen Zeitpunkt ein Zug als gespielt gilt. Dabei muss es einen
Konsenz zwischen beiden beteiligten Instanzen geben, damit kein Spieler zeitlich benachteiligt wird.

Für das Rating ist es nötig, dass alle Instanzen den gleichen Algorithmus zur berechnung nutzen und sich über das Rating eines
Spielers einig sind.

Wie wird damit umgegangen, wenn sich eine Instanz nicht regelkonform verhält (zum Beispiel ungültige Züge zulässt)?

## Nutzergruppen

### Schachspieler

Personen, die gerne Schach mit anderen Spielern spielen wollen, ohne auf eine zentrale Instanz angewiesen zu sein.

### Instanzbetreiber

Personen, die einen Server zum Schachspielen betreiben wollen und dabei Teil eines dezentralen Netzwerks sein möchten.

