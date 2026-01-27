import { joclyToObj, objToJocly } from "./conversion.js";
import { initWebsocket } from "./websocket.js";

const state = {
    gameid: null,
    match: null,
    user: null,
    send: null
};

document.addEventListener("DOMContentLoaded", () => {
    state.gameid = window.location.hash.substring(1);
    const params = new URLSearchParams(window.location.search);
    state.user = params.get("user");
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
    state.send = await initWebsocket(onMessage, state.user);

    /*let init = {
        "initialBoard": "rnbqkbnr/pppp3p/5p2/4p1p1/6P1/5N1B/PPPPPP1P/RNBQK2R w KQkq e6 6 4",
        "game": "classic-chess",
        "playedMoves": []
    };*/
    
    state.match = await Jocly.createMatch("classic-chess");
    
    let element = document.querySelector("[data-js-board]");
    await state.match.attachElement(element);
    //await state.match.load(init);

    let startingTurn = await getCurrentTurn(state.gameid);

    if (startingTurn == state.user) {
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

async function getCurrentTurn(gameid) {
    let result = await fetch(`/api/games/${gameid}?user=${state.user}`).then((res) => res.json());
    return result.currentTurn;
}