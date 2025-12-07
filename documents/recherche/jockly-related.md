# Jockly-Interface

Es wurde testweise eine Partie über das Jocly Interface im Browser gespielt, um auszuprobieren wie das Interface von Jocly funktioniert und wie Partien exportiert werden.   
Link: https://aclap-dev.github.io/jocly/examples/browser/control.html?game=classic-chess

**Folgende Züge wurden gespielt:**  
weiß: e2 -> e3  
schwarz: e7 -> e5  
weiß: Bishop f1 -> c4  
schwarz: Knight b8 -> c6  
weiß: Queen d1 -> h5  
schwarz: Knight g8 -> f6  
weiß: Queen h5 -> f7 -> Checkmate  

Anschließend wurde die Partie exportiert. Dabei wird automatisch eine .json-Datei erzeugt:   
````
{  
  "playedMoves": [  
    {  
      "f": 12,  
      "t": 28,  
      "c": null,  
      "a": "",  
      "ept": 20,  
      "pr": 0,  
      "ck": false 
    },
    {
      "f": 52,
      "t": 36,
      "c": null,
      "a": "",
      "ept": 44,
      "pr": 2,
      "ck": false
    },
    {
      "f": 5,
      "t": 26,
      "c": null,
      "a": "B",
      "ck": false
    },
    {
      "f": 57,
      "t": 42,
      "c": null,
      "a": "N",
      "ck": false
    },
    {
      "f": 3,
      "t": 39,
      "c": null,
      "a": "Q",
      "ck": false
    },
    {
      "f": 62,
      "t": 45,
      "c": null,
      "a": "N",
      "ck": false
    },
    {
      "f": 39,
      "t": 53,
      "c": 21,
      "a": "Q",
      "ep": false,
      "ck": true
    }
  ],
  "game": "classic-chess"
}
````
Da wir die gespielten Züge serverseitig validieren müssen und die Züge in einer Datenbank persistiert werden sollen, ist es wichtig zu wissen, wofür die einzelnen Felder stehen. In der Dokumentation von Jocly konnten wir noch nichts dazu finden, aber die Felder lassen sich wie folgt interpretieren:  

``f`` = from square (numerischer Index für Startfeld), vermutlich wird das Schachbrett beginnend von unten links nach oben links chronologisch durchnummiert (d.h. a1 = 1, h1 = 8, h2 = 9, a2 = 16, usw.)   
  
``t`` = to square (numerischer Index für Zielfeld)   
  
``c`` = captured piece square oder captured piece id (nullable) — Feld, das gesetzt wird wenn eine Figur geschlagen wurde. Wie die Zahl zustandekommt, muss noch herausgefunden werden.  
  
``a`` = action/annotation — wird für Kennzeichnung der bewegten Figuren verwendet (``Q`` für Queen, ``B`` für Bishop, ``N`` für Knight, ``K`` für King, ``R`` für Rook, leer für pawn)  
  
``pr`` = piece/piece type (numerisch kodiert), nummeriert die Bauern durch  
  
``ept/ep`` = En-passant-related Value
  
``ck`` = check (Boolean) — ob der Zug Schach gesetzt hat  
