import { gameToJoclyState, joclyToObj, objToJocly } from "./conversion.js";
import { initWebsocket } from "./websocket.js";

const state = {
    gameid: null,
    match: null,
    user: null,
    send: null
};

document.addEventListener("DOMContentLoaded", () => {
    state.gameid = window.location.hash.substring(1);
    let button = document.querySelector("[data-js-invite]");
    button.addEventListener("click", () => {
        let input = document.querySelector("[data-js-opponent]");
        let opponent = input.value;
        input.value = "";
        createInvitation(opponent).then(result => {
            if (result) {
                alert(`${opponent} was invited!`);
            } else {
                alert(`Can't invite ${opponent}`);
            }
        });
    });
    main();
});

window.addEventListener('hashchange', (event) => {
    event.preventDefault();
    state.gameid = window.location.hash.substring(1);
    loadGame();
});

async function onMessage(msg) {
    if (msg.type == 3 && msg.context == state.gameid) {
        let moveStr = objToJocly(msg.data);
        let move = await state.match.pickMove(moveStr);
        await state.match.playMove(move);
        requestUserInput(state.send);
    } else if (msg.type == 0) {
        addGameToList(msg.context);
    } else if (msg.type == 1) {
        addInvitation(msg.context, msg.data);
    }
}

async function main() {
    state.user = await getUserData().username;
    state.send = await initWebsocket(onMessage);
    let games = await getGames();
    games.forEach(game => {
        addGameToList(game.id);
    });
    getInvitations().then(invitations => {
        invitations.forEach(invitation => {
            addInvitation(invitation.id, invitation);
        })
    })

    state.match = await Jocly.createMatch("classic-chess");
    
    let element = document.querySelector("[data-js-board]");
    await state.match.attachElement(element);
    await state.match.setViewOptions({skin: "skin2dfull"});

    await loadGame();
    
}

async function requestUserInput(send) {
    let isWhite = await state.match.getTurn() == Jocly.PLAYER_A;
    let move = await state.match.userTurn();
    let msg = {
        type: 3,
        context: state.gameid
    };
    let moveStr = await state.match.getMoveString(move.move);
    msg.data = joclyToObj(moveStr, isWhite);
    send(msg);
}

async function getGameState(gameid) {
    return await fetch(`/api/games/${gameid}`).then((res) => res.json());
}

async function getUserData() {
    return await fetch(`/api/user`).then((res) => res.json());
}

async function getGames() {
    return await fetch(`/api/games`).then((res) => res.json());
}

async function loadGame() {
    if (!state.gameid) return;

    let gameState = await getGameState(state.gameid);
    let init = gameToJoclyState(gameState);
    await state.match.abortUserTurn();
    await state.match.load(init);

    if (gameState.yourTurn) {
        await requestUserInput(state.send);
    }
}

async function addGameToList(gameId) {
    let element = document.querySelector("[data-js-gamelist]");
    let entry = `<li><a href="#${gameId}">${gameId}</a></li>`;
    element.innerHTML += entry;
}

async function addInvitation(id, challenge) {
    let element = document.querySelector("[data-js-invitations]");
    let white = "Random";
    if (challenge.white) {
        white = challenge.white === challenge.source ? challenge.sourceHandle : "You";
    }
    let entry = `<li data-js-challenge="${id}">Invitation from ${challenge.sourceHandle}. White Player: ${white}</li>`;
    element.innerHTML += entry;
    let invite = element.querySelector(`[data-js-challenge="${id}"]`);
    invite.addEventListener("click", () => {
        acceptInvite(id);
        element.removeChild(invite);
    });
}

async function acceptInvite(challengeId) {
    let msg = {
        type: 2,
        context: challengeId
    };
    state.send(msg);
}

async function getInvitations() {
    return await fetch(`/api/challenges`).then((res) => res.json());
}

async function createInvitation(opponent) {
    let body = JSON.stringify({opponent: opponent});
    return await fetch(`/api/challenges`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: body
    }).then(res => res.ok);
}