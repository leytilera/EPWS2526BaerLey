# WebSocket-Spezifikation

## Challenges

### CHALLENGE_CREATE (Client -> Server)
```
{
  "type": "CHALLENGE_CREATE",
  "target": "<opponentActorUrl>",
  "params": {
    "gameVariant": "<variant>",
    "timeControl": "<timeControl>"
  }
}

```

### CHALLENGE_ACCEPT (Client -> Server)
```
{
  "type": "CHALLENGE_ACCEPT",
  "challengeId": "<uuid | url>"
}
```

### CHALLENGE_DECLINE (Client -> Server)
```
{
  "type": "CHALLENGE_DECLINE",
  "challengeId": "<uuid | url>"
}
```

### CHALLENGE_RECEIVED (Server -> Client)
```
{
  "type": "CHALLENGE_RECEIVED",
  "challengeId": "<uuid | url>",
  "from": "<actorUrl>",
  "params": {
    "gameVariant": "<variant>",
    "timeControl": "<timeControl>"
  },
  "fromProfile": {
    "username": "<username>",
    "elo": "<elo>",
    "avatarUrl": "<avatarUrl>"
  }
```

### CHALLENGE_ACCEPTED (Client -> Server)
```
{
  "type": "CHALLENGE_ACCEPTED",
  "challengeId": "<uuid | url>"
}
```

### CHALLENGE_DECLINED (Server -> Client)
```
{
  "type": "CHALLENGE_DECLINED",
  "challengeId": "<uuid | url>"
}
```

## Game

### GAME_CREATED (Server -> Client)
```
{
  "type": "GAME_CREATED",
  "challengeId": "<uuid | url>",
  "gameId": "<uuid>",
  "host": "<hostInstance>",
  "players": {
    "white": "<actorUrl>",
    "black": "<actorUrl>"
  },
  "yourColor": "BLACK || WHITE",
  "gameWsUrl": "<gameWsUrl>",
  "opponent": "<actorUrl>"
}
```

### SNAPSHPOT_REQUEST (Client -> Server)
```
{
  "type": "SNAPSHOT_REQUEST",
  "gameId": "<uuid>",
  "timestamp": "<timestamp>"
}
```

### SNAPSHOT (Server -> Client)
```
{
  "type": "SNAPSHOT",
  "gameId": "<uuid>",
  "timestamp": "<timestamp>",
  "players": {
    "white": "<actorUrl>",
    "black": "<actorUrl>"
  },
  "currentTurn": "<actorUrl>",
  "opponentProfile": {
    "username": "<username>",
    "elo": "<elo>",
    "avatarUrl": "<avatarUrl>"
  },
  "ply": "<currentPly>",
  "playedMoves": [],
  "lastMove": {},
  "finished": true | false,
  "winner": "<actorUrl>" // nur wenn finished = true
}
```

### MOVE_SUBMIT (Client -> Server)
```
{
  "type": "MOVE_SUBMIT",
  "gameId": "<uuid>",
  "timestamp": "<timestamp>",
  "expectedPly": "<plyOfCurrentMove>",
  "move": {}
}
```

### MOVE_ACCEPTED (Server -> Client)
```
{
  "type": "MOVE_ACCEPTED",
  "gameId": "<uuid>",
  "timestamp": "<timestamp>",
  "ply": "<currentPly>",
  "playedMoves": [],
  "turn": "<actorUrl>"
}
```

### MOVE_DECLINED (Server -> Client)
```
{
  "type": "MOVE_DECLINED",
  "gameId": "<uuid>",
  "timestamp": "<timestamp>",
  "ply": "<currentPly>",
  "playedMoves": [],
  "turn": "<actorUrl>",
  "reason": "<string>"
}
```

## Move Structure 

### Move
```
{
  "source": "<a1 .. h8>",
  "target": "<a1 .. h8>",
  "capture": true | false,
  "castle": true | false,
  "promotion": "<Q, R, B, N | null>",
  "isCheck": true | false,
  "raw": { /* Beispiel, optional als Fallback */
    "f": 12,
    "t": 28,
    "c": null,
    "a": "",
    "ept": 20,
    "pr": 0,
    "ck": false,
    "ep": false
  }
}
```
