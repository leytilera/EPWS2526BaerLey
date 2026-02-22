# Chessfed

## Installationsanleitung

Was wird benötigt:
- Eine Domain
- Einen Linux Server mit öffentlichen Ports `80` und `443`
- Eine PostgreSQL Datenbank (optional)
- Einen OpenID Connect Identity Provider (optional)
- Einen Reverse Proxy (optional)

Folgende Programme müssen auf dem Server installiert sein:
- docker (inklusive docker compose)
- bash
- git
- htpasswd (optional)

Installationsschritte:
- Klonen Sie das Repository auf den Server: `git clone https://github.com/leytilera/EPWS2526BaerLey.git`
- Navigieren Sie in das Repository: `cd EPWS2526BaerLey`
- Starten Sie das `generate_compose` Script: `./generate_compose.sh`
- Folgen Sie den Anweisungen des Scripts. Wenn Sie bereits eine PostgreSQL Datenbank, einen Reverse Proxy oder einen OIDC Provider haben, können Sie dies dort angeben. Ansonsten werden die entsprechenden Dienste durch das Script mitgeneriert.
- Falls Sie nicht bereits einen OIDC Provider haben, können Sie Nutzer für den integrierten IDP erstellen, indem Sie das `create_user` Script ausführen: `./create_user.sh`
- Starten Sie die Anwendung mit Docker Compose: `docker compose up`

## Was ist implementiert

- Spieler können sich über einen OIDC Provider anmelden
- Spieler können andere Spieler zu einem Spiel einladen
- Spieler können gegeneinander Schach spielen
- Züge werden Serverseitig validiert
- Spieler können sich ihr eigenes Profil oder das Profil eines anderen Spielers anschauen
- Laufende und beendete Spiele werden auf den Profilen der entsprechenden Spieler angezeigt
- Beendete Spiele können im nachhinein Zug für Zug angeschaut werden

## Was ist nicht implementiert

- Es gibt keine Zeitbegrenzung bei Spielen
- Spieler können nur gezielt für ein Spiel angefragt werden, eine zufällige/ratingbasierte Spielerfindung ist nicht implementiert
- Es gibt keine Spielerratings
- Es ist kein Algorithmus zur Konfliktbehebung (zum Beispiel bei Zugvalidierung) implementiert
- Es sind keine HTTP-Signaturen für federated requests implementiert

## Repository Struktur

|Pfad|Beschreibung|
|----|------------|
|[CHANGELOG.md](./CHANGELOG.md)|Enthält die Änderungen am Projekt|
|[documents](./documents/)|Enthält die Projektdokumentation|
|[documents/adr](./documents/adr/)|Enthält alle Architecture Decision Records|
|[documents/pocs](./documents/pocs/)|Enthält alle Proof of Concepts|
|[documents/usecases](./documents/usecases/)|Enthält alle UseCases|
|[documents/recherche](./documents/recherche/)|Enthält alle Recherche Dokumente|
|[documents/activitypub.md](./documents/activitypub.md)|Spezifikation für das verwendete ActivityPub Vokabular|
|[documents/websocketSpecification.md](./documents/websocketSpecification.md)|Spezifikation für die WebSocket Nachrichten|
|[documents/expose.md](./documents/expose.md)|Enthält das Exposé für die Projektidee|
|[documents/glossary.md](./documents/glossary.md)|Enthält Erklärungen zu Fachbegriffen|
|[documents/risks.md](./documents/risks.md)|Enthält Probleme und Risiken für das Projekt|
|[documents/requirements.md](./documents/requirements.md)|Enthält die Systemanforderungen|
|[documents/targetAudienceAnalysis.md](./documents/targetAudienceAnalysis.md)|Enthält die Zielgruppenanalyse|
|[documents/eloCalculation.md](./documents/eloCalculation.md)|Enthält den Algorithmus zur Berechnung der Elo-Zahl (siehe Glossar)|
|[documents/Architekturdiagramm-v2.drawio.png](./documents/Architekturdiagramm-v2.drawio.png)|Enthält das Architekturdiagramm|
|[documents/datenmodell.png](./documents/datenmodell.png)|Enthält das Datenmodell|
|[documents/domaenenmodell.png](./documents/domaenenmodell.png)|Enthält das Domänenmodell|
|[src/main/java](./src/main/java/)|Enthält den Quellcode für das Backend|
|[src/main/resources/static](./src/main/resources/static/)|Enthält den Quellcode für das Frontend|
