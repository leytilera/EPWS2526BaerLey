# POC-001 Server-to-Server-Communication/Client-to-Server Communication über ActivityPub

## Ziel
Dieses POC soll mehrere Aspekte der Server-to-Server und Client-to-Server Kommunikation über ActivityPub testen.

-----

## Vorbedingungen/Voraussetzungen:
Als erstes muss ActivityPub zur Entwicklungsumgebung hinzugefügt werden und es müssen mindestens 2 Test-Instanzen aufgesetzt werden. 

Zudem bietet es sich an, dieses POCs durchzuführen, wenn bereits eine Datenbank integriert wurde und eine persistente Speicherung der für dieses POC zu implemnetierenden Datenstrukturen möglich ist. Alternativ können die Datensätze auch lokal gespeichert werden. Für die zu testenden Funktionalitäten macht das keinen Unterschied, allerdings erlaubt es den Kontext dieses POCs noch stärker am späteren produktiven Projektkontext zu orientieren. 

## Kontext
Im Rahmen dieses POCs sollen folgende Aspekte der Server-zu-Server-Kommunikation getestet werden:
- Requests können von der Outbox eines Nutzers an die Inbox eines anderen Nutzers gesendet werden.
- Requests können von der Outbox eines Nutzers an die Onboxen mehrer Nutzer gesendet werden. 

Im Rahmen dieses POCs sollen folgende Aspekte der Client-zu-Server-Kommunikation getestet werden:
- Requests können auf dem Client erzeugt und an die Outbox auf dem Server gesendet werden.
- Requests können aus der Inbox auf dem Server vom Client aus abgerufen werden.

Zudem soll die Namensauflösung mithilfe von Webfinger geprüft werden, sowie das Nachrichten korrekt signiert und Signaturen korrekt überprüft werden.
Die Verwendung von Webfinger ist kein offizieller Standard, wird aber in der offiziellen Dokumentation von ActivityPub empfohlen und von vielen Implemnetierungen im Fediverse verwendet. Das gleiche gilt für die Signierung von Nachrichten mittels HTTP Signaturen. Auch dies ist kein Standard, wird aber empfohlen um Integrität und Authentizität sicherstellen. Da für uns Vertrauen in andere Instanzen und Fairplay eine wichtige Rolle spielt wäre es für uns auch sinnvoll, Nachrichten zu signieren. 

## Funktionsweise (Kurze Erklärung)
ActivityPub ist ein Federationsprotokoll, dass sowohl Server-to-Server als auch Client-to-Server Kommunikation ermöglicht. 
Nutzer werden über Actors repräsentiert, welche als JSON-Objecte formatiert werden. Jegliche Art von Interaktion (1:1 Kommunikation, Spielanfragen, das Übertragen von Spielzügen, das Anzeigen von Nutzerprofilen, ...) wird durch Activities repräsentiert, welche ebenfalls JSON-Objekte sind und über ``object`` den eigentlichen Inhalt beinhalten. Die Kommunikation erfolgt, indem Nutzer (Clients) ausgehende Nachrichten per POST Requests an ihre ``outbox`` senden und eingehende Nachrichten durch GET Requests an ihre ``inbox`` abfragen. Nachrichten in ``outbox`` werden automatisch vom Server an die Inboxen der angegebenen Ziele weitergeleitet. 

Eine ausführlichere Erläuterung der Funktionsweise von Activitypub kann hier nachgelesen werden: https://www.w3.org/TR/activitypub/#social-web-working-group

-----

## Implementierungen

### Zu definierende Datenstrukturen pro Instanz:

Actor JSON (Beispiel): 
````
{
  "@context": "https://www.w3.org/ns/activitystreams",
  "id": "https://instA.example/users/alice",
  "type": "Person",
  "preferredUsername": "alice",
  "name": "Alice Example",
  "inbox": "https://instA.example/inbox",
  "outbox": "https://instA.example/outbox",
  "publicKey": {
    "id": "https://inst.example/users/alice#main-key",
    "owner": "https://inst.example/users/alice",
    "publicKeyPem": "-----BEGIN PUBLIC KEY-----\n..."
  }
}
````
Datenstrukturen, um einen Nutzer zu repräsentieren. Die konkreten Werte können entweder dynamisch aus Datenbankfeldern erzeugt werden (falls Datenbank schon implementiert) oder im Rahmen des POCs statisch festgelegt werden. 
<br><br>    

Challenge Activity JSON (Beispiel):
````
{
  "@context":"https://www.w3.org/ns/activitystreams",
  "id":"https://instA.example/activities/ch-123",
  "type":"Create",
  "actor":"https://instA.example/users/alice",
  "to":["https://instB.example/users/ben"]
  "object":{
    "id":"https://instA.example/challenges/567",
    "type":"Challenge",
    "from":"https://instA.example/users/alice",
    "to": "https://instB.example/users/ben",
    "message":"Wollen wir Schach spielen?"
  },
  "published": "2025-12-05T12:00:00Z"
}
````
Datenstruktur, um eine Spielanfrage zu repräsentieren. Konkrete Werte entsprechend anpassen. 
<br><br>     
  
Accept Activity JSON (Beispiel):
````
{
  "@context": "https://www.w3.org/ns/activitystreams",
  "id": "https://instB.example/activities/acc-999",
  "type": "Accept",
  "actor": "https://instB.example/users/ben",
  "object": "https://instA.example/activities/ch-12345",             // referenziert ursprüngliche Activity-ID
  "to": ["https://instA.example/users/alice"],
  "published": "2025-12-05T12:01:00Z"
}
````
Datenstruktur, um eine Akzeptanz einer Spielanfrage zu repräsentieren. Konkrete Werte entsprechend anpassen. 
<br><br>  
  
### Zu definierende Endpunkte pro Instanz: 

````
GET /.well-known/webfinger?resource=acct:{user}@{host}
````
Endpunkt, welcher basierend auf der menschenlesbaren Adresse ``acct:{user}@{host}`` über Webfinger die technische Ressource (Actor URL) liefert.   
<br>     
  
````
GET /users/{username}
````
Endpunkt, welcher basierend auf der Actor URL die Actor JSON (``Content-Type application/activity+json``) liefert. Die Actor JSON enthält u.a. die Inbox des Nutzers, welche angibt wohin folgende Requests an diesen Nutzer gesendet werden müssen; sowie den public Key des Nutzers welcher für die Signierung benötigt wird.  
<br>      
  
````
POST /inbox
````
Endpunkt für Server-to-Server-Kommunikation, andere Server senden Activities hierhin.  
<br>      

````
POST /challenges         // oder POST /outbox
````
Endpunkt, über den der Client eine eigene Activity (z.B. Challenge) am eigenen Server erstellt. Der Server ist dann dafür zuständig, die Activity weiterzuleiten.
``(Content-Type: application/ld+json; profile="https://www.w3.org/ns/activitystreams")``  
<br>      

````
GET /users/{username}/inbox
````
Endpunkt, über den der Client lokal gespeicherte eingegangene Activities abruft (Activities, die an ihn gesendet wurden).  
<br>  

### Signierung von Nachrichten
Eine Anleitung, wie das Signieren und Verifizieren von Signaturen mittels cavage-12 HTTP Signatur funktioniert, ist hier zu finden: https://swicg.github.io/activitypub-http-signature/#basic-usage.

Mithilfe dieser Anleitung soll implementiert werden, dass Challenge- und Accept-Activities signiert werden können und die Signaturen von eingehenden Nachrichten überprüft werden können. 

-----

## Durchführung
Als Erstes müssen die notwendigen Datenstrukturen und Endpunkte implementiert werden (siehe oben).

Anschließend sollen drei zentrale Aspekte von Activityhub überprüft werden. Es empfiehlt sich, die folgenden Durchführungsschritte nacheinander und iterierend durchzuführen und jede Funktionalität in einer seperaten Funktion zu implementieren, um die Konzepte isolierend testen zu können. Es müssen aussagekräftige Logs an sinnvollen Stellen hinzugefügt werden, um die Abläufe nachzuvollziehen und Ergebnisse zu dokumentieren. 

### 1. Namensauflösung mit Webfinger
**Ablauf:**
1. Es wird basierend auf usernamen und hostnamen eines Nutzers ein Request an Webfinger gesendet und die Actor URL aus der Response extrahiert. 

**Exit-Kriterien:**
- Der Request liefert die korrekte Actor URL zurück, welche korrekt extrahiert werden konnte. 

**Fail-Kriterien:**
- Der Request liefert keine oder eine falsche Actor URL zurück.
- Die Actor URL konnte nicht aus der Response extrahiert werden. 

**Fallback:**
Es wird auf Webfinger verzichtet und die Nutzernamen werden direkt in den Activities angegeben.

### 2. Client-zu-Server und Server-zu-Server-Kommunikation (1:1, noch ohne Signierung)
**Ablauf:**
1. Auf Instanz A wird eine Spielanfrage (Challenge Activity) erzeugt und per POST Request an die Inbox von Nutzer B auf Instanz B gesendet.
2. Auf Instanz B wird die Spielanfrage per GET Request aus der Inbox abgerufen. Anschließend wird eine Accept-Activity erzeugt und per POST Request zurück an Nutzer A gesendet.
3. Instanz A sollte anhand der ID die Antwort der ursprünglichen Spielanfrage zuordnen können. 

**Exit-Kriterien:**
- Die von Client A gesendete Spielanfrage kann korrekt auf Client B abgerufen werden und die von Client B gesendete Antwort kann erfolgreich empfangen und der ursprünglichen Anfrage zugeordnet werden, d.h. der Mechanismus vom Erzeugen und Übertragen von Activities funktioniert. 

**Fail-Kriterien:**
- Die Spielanfrage erreicht nicht Instanz B/Client B, erreicht den falschen Nutzer oder kann nicht verarbeitet werden.
- Die Antwort kann nicht der ursprünglichen Antwort zugeordnet werden.

**Fallback:**
- Keine, da diese Kommunikation die grundlegende Basis bildet und funktionieren muss. 

### 3. Signierung von Requests und Überprüfung von Signaturen

**Ablauf:**
1. Falls noch nicht geschehen, müssen Funktionen zum Signieren und der Überprüfung von Signaturen implemnetiert werden.
2. Gleicher Ablauf wie bei 1, allerdings werden ausgehende Requests signiert und bei eingehenden Signaturen wird die Signatur überprüft.
3. Negativ Test: es sollen unsignierte Requests bzw. falsch signierte Requests gesendet werden. 

**Exit-Kriterien:**
- Requests können korrekt signiert werden.
- Eine Überprüfung der Signaturen ist erfolgreich. Requests mit fehlenden oder fehlerhaften Signaturen werden abgelehnt. 

**Fail-Kriterien:**
- Das Signieren schlägt fehl.
- Eingehende Requests können nicht erfolgreich verifiziert werden. 

**Fallback:**
- Es wird ein anderer Mechanismus für Signierung verwendet oder eine andere Bibliotheken für das Codieren und Hashen von Nachrichten genutzt. Falls das auch nicht funktioniert, kann im Notfall auf Signaturen verzichtet werden.

### 4. Client-zu-Server und Server-zu-Server-Kommunikation (1:n)
**Ablauf:**
1. Auf Instanz A wird eine Spielanfrage erzeugt, aber diesmal wird kein konkreter Nutzer als Ziel angegeben, sondern die Spielanfrage soll öffentlich publiziert werden. Dafür können bei Bedarf zusätzliche Nutzer hinzugefügt werden, im Actor-Object eine Liste von Followern/Following hinzugefügt werden (``"followers": "https://instA.example/alice/followers/",
 "following": "https://instA.example/alice/following/"``) oder der Request an alle Nutzer gesendet werden (``"https://www.w3.org/ns/activitystreams#Public"``). Die Nachricht kann signiert werden. 
2. Es soll überprüft werden, ob ein anderer Nutzer die Nachricht empfangen und darauf reagieren kann. 

**Exit-Kriterien:**
- Das Veröffentlichen der Spielanfrage war erfolgreich und andere Nutzer können die Anfrage empfangen. 

**Fail-Kriterien:**
- Andere Nutzer können die Spielanfrage nicht empfangen. 

**Fallback:**
- Keine, da diese Kommunikation die grundlegende Basis bildet und funktionieren muss.  

-----

## Fazit und Folgen
Wenn dieses POCs erfolgreich war, zeigt dass das ActivityPub sich als Federationsprotokoll für unsere geplante federierte Schachplattform eignet und notwendige Funktionalitäten damit umsetzbar sind. 








