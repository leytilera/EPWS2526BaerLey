# ELO-Berechnung und Rating-Spezifikation

**Datum:** 30.10.2025  
**Version:** 1.0

## Auswahl und Kurzbegründung:
Wir haben uns dazu entschieden, für unser MVP die klassische Elo-Zahl als Messung der Spielstärke der Spieler*innen zu verwenden. Begründung:
- Etabliert und akzeptiert innerhalb der Schach-Community, wird von vielen Schachplattformen verwendet, gut nachvollziehbar für Spieler*innen
- leicht deterministisch und daher gut geeignet für ein verteiltes System 
- geringer Implementierungsaufwand und umsetzbar im Rahmen unseres Projekts

## Definition der Elo-Zahl
Die Elo-Zahl ist ein gängiger Wert um die Spielstärke von Schachspieler*innen anzugeben. Sie misst die relative Stärke von Spieler*innen im Vergleich zu anderen Spieler*innen und berechnet sich aus den vergangenen Partien, welche die Person gespielt hat. Umso höher der Elo-Wert, desto stärker ist der/die Spieler*in. 

Dafür wird zuerst das wahrscheinliche Ergebnis (Erwartungswert) eines Spiels basierend auf dem bisherigen Rating von Spieler A und Spieler B berechnet. Umso größer die Differenz zwischen den Elo-Zahlen der Spieler, desto höher ist die Wahrscheinlichkeit, dass ein Spieler gewinnt bzw. verliert. Erziehlt ein Spieler in einer Partie mehr Punkte, als ausgehend von seiner aktuellen Elo-Zahl zu erwarten war, gewinnt er Elo-Punkte dazu. Erziehlt er weniger Punkte als zu erwarten, verliert er Elo-Punkte.
Der Vorteil an dieser Berechnungsweise ist, dass schwächere Spieler bei einem Verlust gegen wesentlich stärkere Spieler kaum Punkte verlieren, und andersrum sehr viele Punkte gewinnen können, wenn sie gegen einen stärkeren Spieler gewinnen.

## Berechnung der Elo-Zahl
Basierend auf dem bisherigen Rating von Spieler A und Spieler B wird ein Erwartungswert berechnet welcher angibt, mit welcher Wahrscheinlichkeit der jeweilige Spieler die Partie gewinnt:

Erwartungswert von A gegen B:  
$$\[
E_A = \frac{1}{1 + 10^{(R_B - R_A)/400}}
\]$$

Erwartungswert von B gegen A:  
$$\[
E_B = \frac{1}{1 + 10^{(R_A - R_B)/400}}
\]$$

Dabei ist:  
$R_A$ = das aktuelle Rating von Spieler A und  
$R_B$ = das aktuelle Rating von Spieler B

Da immer $E_A + E_B = 1$ gelten muss, kann der Erwartungswert von B auch vereinfacht wie folgt berechnet werden:
 
Erwartungswert von B:  
$$\[
E_B = 1 - E_A
\]$$

Ausgehend vom berechneten Erwartungswert wird das neue Rating des Spielers wie folgt berechnet: 

R' = R + k * (S - E)

Dabei ist:  
R' = neue Elo-Zahl,  
R = bisherige Elo-Zahl,  
S = erzielte Punkte in der Partie,  
E = erwartete Punkte in der Partie (Erwartungswert),   
k = Koeffizient

Der Koeffizient k bestimmt, wie viele Punkte ein Spieler maximal dazu gewinnen/verlieren kann und wird der Erfahrung des Spielers angepasst.

Üblicherweise nimmt k die folgenden Werte an:  
k = 40 für neue Spieler mit weniger als 30 gewerteten Partien  
k = 20 für alle Spieler mit mindestens dreißig gewerteten Partien und einer maximalen Elo-Zahl von <2400  
k = 10 für alle Spieler die eine Elo-Zahl von =>2400 erreicht haben (selbst wenn ihre Elo-Zahl danach wieder unter den Wert fällt)  

Dadurch ergibt sich, dass Anfänger sich schnell verbessern können, während die Ratings von erfahrenen Spielern im oberen Reich stabilisiert werden und es nicht gravierend ist, mal ein Spiel zu verlieren.   

## Startrating und Grenzwerte  
**Default-Startrating**: es gibt keine offiziellen Vorgaben für die Vergabe des initialen Elo-Wertes, d.h. der Startwert ist frei konfigurierbar.   
**Rating-Grenzen:** optional, min. 100, max. 3000 (um Überläufe/unerwartete Werte zu verhindern). Die Grenzen sind aber ebenfalls Konfigurationsparameter. 

## Versionierung und Federations-Konsistenz
Da Instanzen den Algorithmus zur Berechnung verändern und Parameter konfigurieren können, muss sichergestellt werden, dass Ratings trotzdem fair und vergleichbar bleiben, d.h. zwei gegeneinander spielende Partein den gleichen Algorithmus zur Berechnung nutzen. 

Ein möglicher Lösungsansatz könnte sein:  
- Jeder Instanz wird beim Federation-Handshake die `ratingAlgorithmVersion` mitgeteilt (z. B. 'ELO-v1').  
- Wenn zwei Instanzen unterschiedliche Versionen haben, gilt eine der folgenden Policies (konfigurierbar & dokumentiert):  
  1. **Refuse cross-instance matches**: Matching nur möglich, wenn beide Parteien den gleichen Algorithmus zur Berechnung nutzen, oder  
  2. **Negotiate fallback**: Parteien einigen sich auf eine gemeinsame Rating-Algorithmus Version aus einem kompatiblen Set (z. B. beide unterstützen 'ELO-v1').  
- Alle Rating-Berechnungen werden mit `ratingAlgorithmVersion` und Timestamp protokolliert

## Implemnetierungsdetails
// TODO(), werden im weiteren Verlauf des Projekts noch spezifiziert 

Quellen:
https://www.chess.com/de/terms/elo  
https://de.wikipedia.org/wiki/Elo-Zahl

