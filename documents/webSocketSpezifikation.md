# WebSocket-Spezifikation

## Message Format

``` json
{
  "type": 0,
  "context": "<uuid>",
  "data": {}
}
```

- `type` enthält den Typ der Nachricht als Integer
- `context` enthält typabhängig eine UUID
- `data` enthält weitere typspezifische Daten

## Error

``` json
{
  "type": -1,
  "context": null,
  "data": {
    "error": "Error message"
  }
}
```

- Server -> Client
- `type`=`-1`
- `context` bleibt leer
- `data` enthält eine Fehlermeldung

## Create Game

``` json 
{
  "type": 0,
  "context": "<gameID>",
  "data": {}
}
```

- Server -> Client
- `type`=`0`
- `context` enthält die ID des neuen Spiels
- `data` bleibt leer

## Challenge invite

``` json
{
  "type": 1,
  "context": "<challengeID>",
  "data": {
    "source": "<actor URI>",
    "sourceHandle": "<username>@<domain>",
    "white": "<actor URI>"
  }
}
```

- Server -> Client
- `type`=`1`
- `context` enthält die ID der Challenge
- `data` enthält die URI sowie den Handle des Nutzers, der die Einladung erstellt hat, sowie optional die URI des Spielers, der Weiß spielen soll

## Challenge accept

``` json
{
  "type": 2,
  "context": "<challengeID>",
  "data": {}
}
```

- Client -> Server
- `type`=`2`
- `context` enthält die ID der Challenge
- `data` bleibt leer

## Move

``` json
{
  "type": 3,
  "context": "<gameID>",
  "data": {
    "source": "<source field>",
    "target": "<target field>",
    "promote": "<promotion piece>",
    "capture": false,
    "castle": false
  }
}
```

- Server <-> Client
- `type`=`3`
- `context` enthält die ID des Spiels
- `data` enthält Informationen über den Zug (wie ein Zug auch in der REST-API dargestellt wird)
