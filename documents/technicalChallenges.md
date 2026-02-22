# Technische Herausforderungen für die Weiterentwicklung

## Timekeeping

Timekeeping, also Zeitbegrenzung für Spiele, ist in einer dezentralen Architektur eine besondere Herausforderung.
Es gäbe im Grunde 3 verschiedene Möglichkeiten, Timekeeping umzusetzen.
Möglichkeit 1 wäre, dass jede Instanz für den eigenen Spieler entscheidet, wann der entsprechende Zug gespielt wurde und den entsprechenden Zeitstempel an die andere Instanz übergibt, welche dann darauf vertraut, dass dieser korrekt ist.
Das Problem dabei wäre, dass die erste Instanz über den Zeitstempel lügen könnte und diesen vordatieren könnte, um dem eigenen Spieler einen Vorteil zu schaffen.
Möglichkeit 2 wäre, dass die jeweils andere Instanz über den Zeitpunkt des Zuges entscheidet, sobald sie diesen von der Instanz des Spielers erhalten hat.
Hier gibt es jedoch ein ähnliches Problem, da eine Instanz den Zug eines anderen Spielers verzögern könnte, um den eigenen Spieler zu bevorzugen.
Möglichkeit 3 wäre, dass eine bestimmte Instanz als "Single Point of Truth" über die genaue Zeit des Zuges entscheidet.
Dabei wäre es jedoch wichtig, dass dies keine der beiden Instanzen der Spieler ist, da dies sonst ein Vorteil für den Spieler wäre, dessen Instanz der "Single Point of Truth" ist.
Möglichkeit 3 wäre allerdings die Lösung des Timekeeping-Problems, wenn der "Single Point of Truth" gerecht gewählt wird.

## Single Point of Truth

Bei ActivityPub wird jedes Object (wie in unserem Fall zum Beispiel ein Schachspiel) durch eine URI identifiziert und gehört daher immer zu genau einer Instanz.
Daher ergibt es sich aus der Architektur von ActivityPub selbst, dass es für jedes Spiel einen Single Point of Truth gibt, welcher über den gültigen Spielzustand entscheidet.
Gibt es bei der Zugvalidierung Unstimmigkeiten zwischen den Instanzen, so entscheidet die Instanz, auf die die ID das Spiels verweist, über die Gültigkeit des Zuges, da diese über den Spielzustand entscheiden kann.
In der aktuellen Implementation ist dies die Instanz, auf der das Spiel, beziehungsweise die Einladung, erstellt wurde.
In kompetitiven Szenarien könnte dies ein Problem darstellen, da diese Instanz zu einem Spieler des entsprechenden Spiels gehört. 
Falls dieser Spieler die Instanz selbst kontrolliert, könnte er dadurch das Spiel manipulieren.
Eine Lösung dafür wäre, dass zu jedem föderierten Spiel immer 3 statt 2 Instanzen gehören: jeweils eine für jeden Spieler und eine Dritte für das Spiel selbt.
Dadurch würden die Entscheidungen über das Spiel durch eine von beiden Spielern unabhängige Instanz getroffen werden.
Die Herausforderung dabei wäre, dass sich beide Instanzen der Spieler auf eine Instanz für das Spiel einigen.
Um sicherzustellen, dass darüber zwischen den Instanzen ein Konsens herrscht, sollte entsprechende Entscheidung auf objektiven Parametern beruhen.
Möglich wäre es, dies auf dem Ruf von Instanzen zu basieren, wobei Instanzen bevorzugt werden, die in der Vergangenheit zuverlässig korrekte Entscheidungen getroffen haben.

## Matchmaking

Aktuell können neue Spiele nur gestartet werden, indem ein Spieler einen bestimmten anderen Spieler zu einem Spiel einläd.
Es sollte allerdings eine Möglichkeit geben, einen anderen Spieler "zufällig" zu finden, der gerade auch ein Spiel spielen möchte, und dabei sollte auch berücksichtigt werden, dass die Zuordnung möglichst gerecht ist und die Fähigkeiten der Spieler berücksichtigt.
Eine Möglichkeit dafür wäre ein zentrales System, welches das Matchmaking übernimmt.
Da dies jedoch das Ziel der dezentralität unterwandern würde und es durch die Möglichkeit eigene Instanzen zu hosten, sowieso jeder Instanz möglich wäre selbst zu entscheiden, welches System für das Matchmaking benutzt wird, sollte es in einer entsprechenden Architektur berücksichtigt werden, dass jeder seine eigene Matchmaking-Instanz aufsetzten könnte. 
Dies würde jedoch zu einer Fragmentierung des Netzwerks führen, da Spieler einer Instanz nur Spieler finden könnten, die die gleiche Matchmaking-Instanz benutzen.
Die Lösung dafür wäre, wenn jede Chessfed-Instanz mehrere Matchmaking-Instanzen verwenden könnte und bei Bedarf die erste Matchmaking-Instanz, die einen entsprechenden Spieler gefunden hat, benutzen und darauffolgende Angebote von anderen Matchmaking-Instanzen ablehnen.
Da die Matchmaking-Instanzen nun auch dezentral sind, ließe sich die Architektur jedoch auch so gestaltet, dass jede Chessfed-Instanz gleichzeitig auch eine Matchmaking-Instanz ist, wodurch eine entsprechende externe Anwendung für Matchmaking redundant wäre.
Alternativ könnte man auch einen Konsensmechanismus entwickeln, mit dem die Instanzen gemeinsam als dezentrales Netzwerk entscheiden, welche Spieler zu einem Match hinzugefügt werden.

## Rating

Damit beim Matchmaking die Fähigkeiten der Spieler berücksichtigt werden, muss es für Spieler ein Rating geben, welches auf den bereits gespielten Spielen basiert und auf allen Instanzen gleich berechnet wird. 
Dafür könnten [Elo-Zahlen](./eloCalculation.md) verwendet werden.
Trotz eines einheitlichen Berechnungsalgorithmus müsste es jedoch einen Konsenzmechanismus geben, mit dem die Instanzen entscheiden, welches Rating ein Spieler hat, da einzelne Instanzen sonst durch "Fake-Spiele" das Rating eines Spielers manipulieren könnten.

## Fazit

Zusammengefasst lässt sich sagen, dass es für die Lösung der größten Herausforderungen nötig wäre, einen Konsensmechanismus für das dezentrale Netzwerk aus Instanzen zu entwickeln, mit dem die Instanzen Entscheidungen treffen und die Entscheidungen bösartiger Instanzen ablehnen.