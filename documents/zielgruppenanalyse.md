# Zielgruppenanalyse

**Datum:** 29.10.2025  
**Version:** 2.0

# Ziel der Analyse
Diese kompakte Zielgruppenanalyse segmentiert die primären Nutzer unserer förderierten Schachplattform, beschreibt kurz ihre Charakteristika und leitet daraus priorisierte Anforderungen (Jobs-to-be-Done) ab.

# Wichtige Anmerkung
Da wir nicht die Ressourcen zur Verfügung haben, um selbst Umfragen und Interviews durchzuführen, basiert die folgende Analyse größtenteils auf Internet-Recherche, Beiträgen in Communities und Annahmen. Das heißt, Teile der Segmentierung und Anforderungen sind hypothesenbasiert und damit spekulativ. 

## Segmentierung (Übersicht):

1. Schachspieler (Clients) <br>
   1.1 Gelegenheitsnutzer/Hobby-Spieler   
   1.2 Competitive-/Turnierspieler  
   1.3 Self-Host-Affine Spieler  
   1.4 Vereins-/Club-Mitglieder  

3. Instanzbetreiber (Hosts) <br>
   2.1 Hobby Self-hosters (Technikaffine Einzelpersonen)  
   2.2 Vereins-/Organisations-Hosts  
   2.3 Lernende (z.B. Studierende, die Instanzen aus Lernzwecken betreiben)  

## Detaillierte Kurzprofile & zentrale Bedürfnisse

### 1.1 Gelegenheitsnutzer/Hobbyspieler
**Definition:** Änfänger bis erfahrene Spieler, welche sporadisch/regelmäßig Partien im Browser spielen und bereits Plattformen wie z.B. Lichess und chess.com nutzen. Sie spielen primär aus Spaß und für sie stellt Dezentralisation kein zentrales Bedürfnis dar, aber sie sind trotzdem eine potentielle Nutzergruppe und ihre Bedürfnisse sind relevant für uns. 

**Zentrale Bedürfnisse:**
- große, aktive Spielgemeinschaft um zuverlässig und schnell passende Gegner finden zu können (auch Spieler mit höherem Rating, damit Spiele herausfordernd sind)
- schnelles Matchmaking: geringe Wartezeiten und Filtermöglichkeiten (z.B. Spielvariante, Zeitbegrenzung, Rating)
- flüssiges Spielerlebnis ohne Verzögerungen und sofortige Rückmeldung nach dem Zug eines Gegners (niedrige Latenz, sichtbare Zugbestätigug)
- breite Auswahl an Spielvarianten und flexiblen Zeitkontrollen um verschiedene Vorlieben abzudecken 
- kostenfreie Grundfunktionen ohne Werbeunterbrechungen, da dies auch schon von Konkurrenzplattformen bereitgestellt wird (z.B. [Lichess](https://lichess.org/de) ist Open-Source und komplett kostenlos)
- einfache, verständliche Post-Game-Analyse (unbegrenzter Zugriff auf Basis-Analyse und PGN-Export)
- Möglichkeiten, Spieler bei denen der Verdacht besteht, dass sie sich nicht regelkonform verhalten oder cheaten zu identifizieren und zu melden
- anpassbares Board-/Figurendesign (Themes/Skins) zur Personalisierung der Spieloberfläche
- Intuitive, stabile UI/UX ohne Abstürze sowie schnelle Reconnect/Recovery-Mechanismen für Verbindungsabbrüche

---

### 1.2 Competitive-/Turnierspieler

**Definition:** Überwiegend erfahrene, regelmäßig spielende Nutzer mit hoher Spielstärke, welche interessiert an Turnieren und Ranglisten sind. Ihre Bedürfnisse überschneiden sich mit den Anforderungen von Gelegenheitsnutzern/Hobbyspielern, trotzdem werden sie hier als seperate Nutzergruppe definiert, da sie potentiell höhere Ansprüche an Fairplay und Integrität haben sowie ein höheres Interesse an Turnieren, Post-Game-Analyse und gleichwertigen Gegnern.

**Zentrale Bedürfnisse:**
- verlässliche, nachvollziehbare und einheitliche Ratings
- robuste Fairplay- und Integritätsmechanismen, da Zuverlässigkeit und Vertrauen in andere Instanzen essentiell ist
- Nachvollziehbarkeit und Revisionsmöglichkeiten sowie Aufzeichnung uns Analyse vergangener Spiele, vor allem bei Turnieren
- Optionen für organisatorische Zusatzmaßnahmen (z.B. Zuschauermodus für Partien anderer Spieler, strengere Matching-Regeln oder Turnier-Moderation) welche bei Bedarf aktiviert werden können

**Relevante Systemanforderungen:**
- einheitlicher Rating-Algorithmus
- Matchingalgoritmus zum Finden passender Gegner, basierend auf Filtermöglichkeiten/Rating anderer Spieler
- Transparenz anderer Instanzen
- Fairness-Policy
- Möglichkeit Manipulation zu reporten
- post-game engine-basierte Spielanalyse
- Export gespielter Partien  

---
  
### 1.3 Self-Host-affine Spieler

**Definition:** Spieler, welche bewusst in einem dezentralen System und auf self-hosted-Instanzen spielen wollen. Für sie sind Datenschutz, Privatsphäre und Kontrolle entscheidende Kriterien. Sie besitzen überwiegend ein hohes Technikinteresse und haben teilweise schon Erfahrungen mit Self-Hosting/Federation, beispielsweise nutzen sie bereits dezentrale Plattformen wie z.B. Mastodon. 

**Zentrale Bedürfnisse:**
- Datenschutz- und Sichtbarkeitseinstellungen (z.B. Sichtbarkeit von Profilinformationen, dem Rating und gespielten Partien), da die Kontrolle über die eigenen Daten eine ausschlaggebende Motivation von Nutzern dezentraler Plattformen ist
- Transparente Informationen über Instanzen, um das Vertrauen der Spieler in andere Instanzen zu stärken und cross-instance-Interaktionen zu fördern

---

### 1.4 Vereins-/Club-Mitglieder

**Definition:** Diese Nutzergruppe umfasst sowohl traditionelle/bereits bestehende Schachgruppen, welche sich bisher in Person treffen und sich gerne um eine Online-Gruppe erweitern wollen; als auch Freundesgruppen oder Menschen welche daran interessiert sind, in einer kleineren Online-Community zu spielen. 

**Zentrale Bedürfnisse:**
- Möglichkeit, vereinsintern/gruppenintern (d.h. innerhalb der eigenen Instanz) Turniere zu veranstalten
- Rangliste für Mitglieder der eigenen Instanz sowie gemeinsames Archiv (vergane Spiele + Ratings) um Leistungen zu dokumentieren
- Funktionen zum Einsehen vergangener Partien anderer Spieler und gemeinsame Analyse abgeschlossener Partien, um sich auszutauschen und gemeinsam zu lernen
- optionale Live-Zuschauerfunktion oder einfache "Live-Feed" Anzeige für Vereins-/Gruppenturniere (nicht MVP, aber als zukünftige Erweiterung denkbar)
- Rollen/Permissions für Mitglieder der eigenen Instanz (z.B. Admin, Moderator, Mitglied) zur einfacheren Verwaltung, Kontrolle und Delegation von Aufgaben 
- Unterstützung verschiedener Spielvarianten und klassischen Zeitkontrollen 
  
---

### 2.1 Hobby Self-hosters (Technikaffine Einzelpersonen)

**Definition:** Technikinteressierte Einzelpersonen, welche aus Spaß oder Überzeugung eine eigene Instanz betreiben wollen. Sie haben überwiegend gute Technikkenntnisse und teilweise bereits Erfahrung mit Self-Hosting, bezitzen eventuell sogar schon die nötige Infrastruktur. Sie möchten experimentieren, Konfigurationen anpassen und sich gff. aktiv an der Entwicklung beteiligen. 

**Zentrale Bedürfnisse:**
- voll konfigurierbare Server-Optionen, um experimentieren zu können und Einstellungen individuell anpassen zu können
- open-source-Code, um Mitentwicklung und Transparenz zu ermöglichen
- keine unnötigen Zusatzkosten, bevorzugte Nutzung bereits vorhandener, etablierter Infrastruktur (z.B. PostgreSQL als Datenbank) zur Reduktion von wartungsaufwand und Kompatibilitätsproblemen
- Hohe Performance und hohe Skalierbarkeit um flüssige Spiele und geringe Latenzen zu ermöglichen

---

### 2.2 Vereins-/Organisations-Hosts
**Definition:** Personen (oft Leiter/Verantwortliche von Vereinen oder Gruppen), welche eine eigene Instanz für ihren Verein/ihre Gruppe betreiben wollen oder Menschen, welche Interesse daran haben eine kleinere Community aufzubauen. Da ihre Technikkenntisse schwanken, sind für sie vor allem eine niederschwellige, einfache Aufsetzung und Verwaltung von Instanzen von Bedeutung. 

**Zentrale Bedürfnisse:**
- einfache, gut dokumentierte Deployments inklusive Schritt-für-Schritt-Anleitungen und/oder Beispiel-Setups, damit auch Personen ohne umfangreiche Technikkenntnisse Instanzen aufsetzen und betreiben können
- intuitive Admintools um Spieler der Instanz (Mitglieder) zu verwalten, Rollen/Zugriffe zu steuern, Turniere zu organisieren und Ranglisten zu führen
- einfache Konfigurationsoptionen für vereinspezifische/gruppenspezifische Vorlieben, die ohne tiefgehende technische Kenntnisse änderbar sind

---

### 2.3 Lernende 
**Definition:** Personen, Kleingruppen oder Organisationen (z.B. Studierende oder (Hoch-)schulen), welche eine eigene Instanz primär aus Lernzwecken betreiben wollen, um Erfahrung mit Self-Hosting und dezentralen Systemen zu sammeln. Sie könnten als eigene Kategorie definiert werden, wir haben uns jedoch dazu entschiedem sie unter Instanzbetreiber einzuordnen, da die Bereitstellung von lernspezifische Materialen (z.B. Tutorials) nicht im Projektziel vorgesehen ist. 

**Zentrale Bedürfnisse**: 
- isolierte Setups und umfangreiche Dokumentation, evt. mit Beispiel-Deployments und Demo-Daten, damit sie ohne Risiko experimentieren und Wissen erwerben können

---

Natürlich kann es auch Überschneidungen zwischen den Nutzergruppen geben und Schachspieler können ebenfalls Instanzbetreiber sein. Ebenso kann es weitere potentielle Nutzergruppen geben, wie z.B. Familien, Freundesgruppen oder Formen von Gelegenheitsgruppen als Schachspieler sowie Organisationen/Bildungseinrichtungen als Instanzbetreiber, aber für das MVP würden wir uns vorerst auf die obigen Nutzergruppen fokussieren.  

--- 

## Mögliche Akzeptanzbarrieren:

1. Vertrauen in fremde Instanzen  
2. Komplexität beim Hosten
3. Manipulation/Betrug
4. Netzwerk/Latenz und Timekeeping bei Matches zwischen unterschiedlichen Instanzen
5. Datenschutz / DSGVO-Bedenken

---

## Quellen  
(Auswahl)

https://www.reddit.com/r/chess/comments/1o8rq1b/the_best_free_chess_resources_that_should_be_more/  
https://www.reddit.com/r/chess/comments/106hfqi/what_is_the_best_chess_platformserver_to_play_on/  

https://www.reddit.com/r/selfhosted/comments/1hc35mn/simple_chess_server/#  
https://forum.solidproject.org/t/decentralized-chess-game/365/15  

https://www.usenix.org/system/files/usenixsecurity23-grober.pdf [analysiert die generelle Motivation von Self-Hostern]
 
Lichess ([website](https://lichess.org/), [github](https://github.com/lichess-org/lila)) bietet durch öffentlichen Quellcode und Git-Logs aufschlussreiche Einblicke in implementierte Funktionalitäten und den Entwicklungsprozess  
Chess.com ([website](https://www.chess.com/)) dient als etablierte, große Schachplattform ebenfalls als Inspiration für relevante Funktionalitäten
