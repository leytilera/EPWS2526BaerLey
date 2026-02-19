import { gameToJoclyState, objToJocly } from "./conversion.js";
import { API } from "./api.js";

const state = {
    gameid: null,
    match: null,
    moves: [],
    counter: 0,
};

function dtoToJoclyString(dto) {
    const obj = {
        source: dto.source,
        target: dto.target,
        promote: dto.promote,
        capture: dto.capture,
        castle: dto.castle
    }
    return objToJocly(obj);
}

async function goTo(counter) {
    if (counter < 0) counter = 0;
    if (counter > state.moves.length) counter = state.moves.length;
    state.counter = counter;

    await state.match.rollback(state.counter);
    renderMoveHistory(state.moves, state.cursor);
}

function setUpGameReplayNavigation() {
    const replayButtons = document.querySelector("[data-js-replay-buttons]");
    if (!replayButtons)return;

    replayButtons.addEventListener("click", async (e) => {
        const btn = e.target.closest("button[data-action]");
        if (!btn) return;

        const action = btn.dataset.action;

        if (action === "first") return goTo(0);
        if (action === "previous") return goTo(state.counter - 1);
        if (action === "next") return goTo(state.cursor + 1);
        if (action === "last") return goTo(state.moves.length);
    });
}

async function preloadAllMovesIntoJocly {
    for (let i = 0; i < state.moves.length; i++) {
        const dto = state.moves[i];
        const moveString = dtoToJoclyString(dto);
    }
    const move = await state.match.pickMove(moveString);
    await state.match.playMove(move);
}

function renderMoveHistory(moves, ply = null) {
    const moveHistory = document.getElementById("move-history");
    if (!moveHistory) return;

    if (!Array.isArray(moves) || moves.length === 0) {
        moveHistory.innerHTML = "";
        return;
    }

    let showUntil;
    if (ply == null) {
        showUntil = moves.length;
    } else {
        if (ply < 0) {
            showUntil = 0;
        } else if (ply > moves.length) {
            showUntil = moves.length;
        } else {
            showUntil = ply;
        }
    }
    const visibleMoves = moves.slice(0, showUntil);

    let html = "";
    for (let i = 0; i < visibleMoves.length; i += 2) {
        const moveNumber = Math.floor(i / 2) + 1;
        const whiteMove = visibleMoves[i];
        const blackMove = visibleMoves[i + 1];

        const whiteMoveItem = whiteMove ? formatRawMoveToHistoryItem(whiteMove) : "";
        const blackMoveItem = blackMove ? formatRawMoveToHistoryItem(blackMove) : "";

        html += `
            <div class="move-row">
                <div class="move-number">${moveNumber}.</div>
                <div class="move-cell">${whiteMoveItem}</div>
                <div class="move-cell">${blackMoveItem}</div>
            </div>
        `;
    }
    moveHistory.innerHTML = html;
}

function formatRawMoveToHistoryItem(rawMove) {
    if (!rawMove) return;

    if (rawMove.castle) {
        return "O-O";
    }
    const seperator = rawMove.capture ? "x" : "-";
    let moveItem = `${rawMove.source}${seperator}${rawMove.target}`;
    if (rawMove.promote) {
        moveItem += `=${rawMove.promote}`;
    }

    return moveItem;
}

async function main() {
    state.gameid = window.location.hash.substring(1);
    if (!state.gameid) return;

    const gameState = await API.getGameState(state.gameid);
    if (!gameState.finished) return;

    state.moves = await API.getMoves(state.gameid);
    state.match = await Jocly.createMatch("classic-chess");
    const board = document.querySelector("[data-js-board]");
    await state.match.attachElement(board);
    await state.match.setViewOptions({ skin: "skin2dfull" });

    const init = gameToJoclyState(gameState);
    await state.match.load(init);
    await preloadAllMovesIntoJocly();
    setUpGameReplayNavigation();
    await goTo(state.moves.length);
}

document.addEventListener("DOMContentLoaded", main);
window.addEventListener("hashchange", () => {
    location.reload();
});