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
    console.log(state.gameid);
});

async function onMessage(msg) {
    console.log(msg);
    if (msg.type == 3 && msg.context == state.gameid) {
        let moveStr = objToJocly(msg.data);
        let move = await state.match.pickMove(moveStr);
        await state.match.playMove(move);
        requestUserInput(state.send);
    }
}

async function main() {
    state.user = await getUserData().username;
    state.send = await initWebsocket(onMessage);
    
    state.match = await Jocly.createMatch("classic-chess");
    
    let element = document.querySelector("[data-js-board]");
    await state.match.attachElement(element);

    let gameState = await getGameState(state.gameid);
    let init = gameToJoclyState(gameState);
    await state.match.load(init);

    if (gameState.yourTurn) {
        await requestUserInput(state.send);
    }
    
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