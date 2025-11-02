# UseCase 0001: Neues Spiel

## Akteure

- Nutzer A
- Nutzer B
- Instanz A
- Instanz B

## Auslöser

Nutzer A schickt Anfrage für neues Spiel an Nutzer B.

## Vorbedingungen

Nutzer A ist eingeloggt und befindet sich auf Profil von Nutzer B.

## Standardablauf

1. Instanz A schickt die Spielanfrage an Instanz B
2. Instanz B informiert Nutzer B, dass Nutzer A ein neues Spiel starten möchte
3. Nutzer B akzeptiert die Anfrage
4. Instanz B informiert Instanz A, dass Nutzer B die Anfrage angenommen hat
5. Instanz A und Instanz B initiiern das neue Spiel
6. Nachbedingung: Spiel wurde gestartet

## Alternative Abfolgen

### Instanz B ist nicht erreichbar

2. Instanz A kann Instanz B nicht erreichen
3. Instanz A versucht in einem bestimmten Intervall Instanz B erneut zu erreichen
4. (a) Instanz A schafft es, Instanz B bei erneutem Versuch zu erreichen -> weiter mit Standardablauf (2)
5. (b) Instanz A schafft es nicht, Instanz B nach mehreren Versuchen zu erreichen -> Instanz A informiert Nutzer A,
dass die Anfrage fehlgeschlagen ist 
6. Nachbedingung: kein Spiel wurde gestartet

### Nutzer B lehnt Anfrage ab

3. Nutzer B lehnt Anfrage ab
4. Instanz B informiert Instanz A, dass Anfrage abgelehnt wurde
5. Instanz A informiert Nutzer A, dass Nutzer B die Anfrage abgelehnt hat
6. Nachbedingung: kein Spiel wurde gestartet

### Nutzer B reagiert nicht auf Anfrage

3. Nutzer B reagiert nicht auf Anfrage
4. Instanz A wartet bestimmte Zeit ab
5. Sobald Zeit abgelaufen ist, informiert Instanz A Nutzer A und Instanz B, dass die Anfrage abgelaufen ist
6. Nachbedingung: kein Spiel wurde gestartet
