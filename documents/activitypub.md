# ActivityPub Extension Types für Federated Chess

## Objects

### Challenge

``` json
{
    "@context": [
        "https://www.w3.org/ns/activitystreams",
        {
            "chessfed": "https://chessfed.tilera.xyz"
        }
    ],
    "id": "https://instA.example/challenges/567",
    "type": "chessfed:Challenge",
    "chessfed:white": "https://instB.example/users/ben",
    "published": "2025-12-05T12:00:00Z"
}
```

### Game

``` json
{
    "@context": [
        "https://www.w3.org/ns/activitystreams",
        {
            "chessfed": "https://chessfed.tilera.xyz"
        }
    ],
    "id": "https://instA.example/games/567",
    "type": "chessfed:Game",
    "published": "2025-12-05T12:00:00Z",
    "chessfed:white": "https://instB.example/users/ben",
    "chessfed:black": "https://instA.example/users/alice",
    "chessfed:finished": false,
    "chessfed:winner": null,
    "chessfed:castleState": {
        "whiteShort": true,
        "whiteLong": true,
        "blackShort": true,
        "blackLong": true
    },
    "chessfed:board": [
        ["r", "n", "b", "q", "k", "b", "n", "r"],
        ["p", null, "p", "p", "p", "p", "p", "p"],
        [null, null, null, null, null, null, null, null],
        [null, "p", null, null, null, null, null, null],
        [null, null, null, null, null, null, null, null],
        [null, null, null, null, null, null, null, null],
        [null, null, null, null, null, null, null, null],
        [null, null, null, null, null, null, null, null],
        ["P", "P", "P", "P", "P", "P", "P", "P"],
        ["R", "N", "B", "Q", "K", "B", "N", "R"]
    ],
    "chessfed:currentTurn": "https://instA.example/users/alice",
    "chessfed:enPassantField": "b2",
    "totalItems": 1,
    "items": [
        {
            "id": "https://instA.example/games/567/moves/1",
            "type": "chessfed:Move"
        }
    ]
}
```

### Move

``` json
{
    "@context": [
        "https://www.w3.org/ns/activitystreams",
        {
            "chessfed": "https://chessfed.tilera.xyz"
        }
    ],
    "id": "https://instA.example/games/567/moves/1",
    "type": "chessfed:Move",
    "published": "2025-12-05T12:00:00Z",
    "chessfed:source": "b1",
    "chessfed:target": "b3",
    "chessfed:capture": false,
    "chessfed:castle": false,
    "chessfed:promote": null
}
```

## Activities

### Play

``` json
{
    "@context": [
        "https://www.w3.org/ns/activitystreams",
        {
            "chessfed": "https://chessfed.tilera.xyz"
        }
    ],
    "id": "https://instA.example/activities/create-874",
    "type": "chessfed:Play",
    "actor": "https://instB.example/users/ben",
    "object": {
        "id": "https://instA.example/games/567/moves/1",
        "type": "chessfed:Move",
    },
    "target": {
        "id": "https://instA.example/games/567",
        "type": "chessfed:Game"
    }
}
```

## Verwendete ActivityStreams standard Activity Typen

### Invite Challenge

``` json
{
    "@context": [
        "https://www.w3.org/ns/activitystreams",
        {
            "chessfed": "https://chessfed.tilera.xyz"
        }
    ],
    "id": "https://instA.example/activities/ch-123",
    "type": "Invite",
    "actor": "https://instA.example/users/alice",
    "target": [
        "https://instB.example/users/ben"
    ],
    "object": {
        "id": "https://instA.example/challenges/567",
        "type": "chessfed:Challenge"
    }
}
```

### Accept Challenge

``` json
{
    "@context": [
        "https://www.w3.org/ns/activitystreams",
        {
            "chessfed": "https://chessfed.tilera.xyz"
        }
    ],
    "id": "https://instB.example/activities/acc-999",
    "type": "Accept",
    "actor": "https://instB.example/users/ben",
    "object": "https://instA.example/activities/ch-123"
}
```

### Create Game

``` json
{
    "@context": [
        "https://www.w3.org/ns/activitystreams",
        {
            "chessfed": "https://chessfed.tilera.xyz"
        }
    ],
    "id": "https://instA.example/activities/create-874",
    "type": "Create",
    "actor": "https://instA.example/instance",
    "object": {
        "id": "https://instA.example/games/567",
        "type": "chessfed:Game",
    }
}
```