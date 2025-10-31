# Zielgruppenanalyse

**Datum:** 29.10.2025  
**Version:** 1.0

# Ziel der Analyse
Diese kompakte Zielgruppenanalyse segmentiert die primären Nutzer unserer förderierten Schachplattform, beschreibt kurz ihre Charakteristika und leitet daraus priorisierte Anforderungen (Jobs-to-be-Done) ab.


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
**Definition:** spielt sporadisch/regelmäßig Partien im Browser, Anfänger bis erfahrene Spieler

**Zentrale Bedürfnisse:**
- Schnell und ohne Hürden ein Spiel starten (einfache Anmeldung, schnelles Starten eines Spiels)
- Kurze Wartezeiten beim Finden eines passenden Gegners
- Intuitive UI

- 

---

### 1.2 Competitive-/Turnierspieler

**Definition:** erfahrene Spieler, welche regelmäßig Partien spielen

**Zentrale Bedürfnisse:**
- verlässliche, nachvollziehbare und einheitliche Ratings, geringe Toleranz gegenüber Unstimmigkeiten
- Fairplay, Zuverlässigkeit und Vertraulichkeit anderer Instanzen  
- geringe Latenz, präzise Zeitkontrollen
- nachträgliche Spiel-Analyse
  
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

**Definition:** Spieler, welche viel Wert auf Datenschutz, Privatsphäre und Kontrolle legen; überwiegend hohe Technikaffinität 

**Zentrale Bedürfnisse:**
- Transparente Sichtbarkeit/Datenschutz-Einstellungen
- Vetraulichkeit anderer Instanzen

---

### 1.4 Vereins-/Club-Mitglieder

**Definition:** Schachvereine oder Schachgruppen, welche innerhalb ihrer eigenen Community spielen wollen 

---

### 2.1 Hobby Self-hosters (Technikaffine Einzelpersonen)

**Definition:** Technikinteressierte Einzelpersonen, welche gerne ihre eigene Instanz betreiben wollen; mittlere bis hohe Technikaffinität 

**Zentrale Bedürfnisse:**
- Einfaches Deployment
- klare Dokumentation
- umfassende Konfigurationsmöglichkeiten

---

### 2.2 Vereins-/Organisations-Hosts
**Definition:** Hosts von Schachvereinen/Communities, welche ihr eigenes Netzwerk betreiben wollen; Technikkentnisse schwanken  

**Zentrale Bedürfnisse:**
- umfangreiche Dokumentation
- einfaches Aufsetzen von Instanzen, da nicht unbedingt gute Technikenntnisse 
- Verwaltung aller Spieler der Instanz (Mitglieder)
- Möglichkeit, Turniere zu organisieren oder Ranglisten zu führen
- gff. Konfigurationsmöglichkeiten um Einstellungen an individuelle Bedürfnisse anzupassen
- DSGVO-Konformität

---

### 2.3 Lernende 
**Definition:** Personen, die eine eigene Instanz primär aus Lernzwecken betreiben wollen und nicht zwangsläufig am Schachspielen interessiert sind   

**Zentrale Bedürfnisse**: 
- umfangreiche Dokumentation, evt. Beispiel-setup
- umfangreiche Konfigurationsmöglichkeiten 

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

