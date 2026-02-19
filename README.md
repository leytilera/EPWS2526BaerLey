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

## Dokumente

- [Expose](./documents/expose.md)
- [Zielgruppenanalyse](./documents/zielgruppenanalyse.md)
- [ADRs](./documents/adr/)
- [PoC](./documents/poc.md)
- [Probleme und Risiken](./documents/risiken.md)
- [Elo-Berechnung](./documents/elo-berechnung.md)
- [UseCases](./documents/usecases/)
- [Glossar](./documents/glossar/)
