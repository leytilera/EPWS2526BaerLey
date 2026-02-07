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
    }
}

async function main() {
    state.user = await getUserData().username;
    state.send = await initWebsocket(onMessage);
    let games = await getGames();
    games.forEach(game => {
        addGameToList(game.id);
    });

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