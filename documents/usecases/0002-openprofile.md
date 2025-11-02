# UseCase 0002: Profil öffnen

## Akteure

- Nutzer A
- Instanz A
- Instanz B

## Auslöser

Nutzer A sucht anderen Nutzer mit NutzerID

## Vorbedingungen

Nutzer A ist eingeloggt

## Standardablauf

1. Nutzer A gibt NutzerID ein
2. Instanz A sucht Instanz B basierend auf NutzerID
3. Instanz A fragt bei Instanz B nach Profil von Nutzer mit NutzerID
4. Instanz B sendet Profilinformationen von Nutzer an Instanz A
5. Nachbedingung: Instanz A zeigt Nutzer A das Profil von angefragtem Nutzer an

## Alternative Abfolgen

### Instanz B kann nicht erreicht werden

3. Instanz A kann Instanz B nicht finden oder erreichen
4. Nachbedingung: Instanz A informiert Nutzer A, dass Instanz B nicht erreicht werden kann

### Nutzer mit NutzerID existiert nicht

4. Instanz B kennt Nutzer mit NutzerID nicht
5. Instanz B informiert Instanz A, dass Nutzer nicht existiert
6. Nachbedingung: Instanz A informiert Nutzer A, dass Nutzer mit NutzerID nicht existiert