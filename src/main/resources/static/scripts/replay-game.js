import { gameToJoclyState, objToJocly } from "./conversion.js";
import { API } from "./api.js";
import { parseActorUrlOrHandle, formatRawMoveToHistoryItem,applyPlayersInformation } from "./ui-helpers.js";

const state = {
    gameid: null,
    match: null,
    moves: [],
    joclyMoveStrings: [],
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

async function setUpGameReplayNavigation() {
    const replayButtons = document.querySelector("[data-js-replay-buttons]");
    if (!replayButtons) {
        return;
    }

    replayButtons.addEventListener("click", async (e) => {
        const btn = e.target.closest("button[data-action]");
        if (!btn) return;

        const action = btn.dataset.action;
        console.log("[replay] click", action);

        if (action === "first") {
            state.match.rollback(0);
            state.counter = 0;
            renderMoveHistory(state.moves, state.counter);
            return;
        }
        if (action === "previous") {
            if (state.counter == 0) return;
            state.match.rollback(-1);
            state.counter -= 1;
            renderMoveHistory(state.moves, state.counter);
            return;
        }
        if (action === "next") {
            if (state.counter == state.moves.length) return;
            state.match.applyMove(state.joclyMoveStrings[state.counter]);
            state.counter += 1;
            renderMoveHistory(state.moves, state.counter);
            return;
        }
        if (action === "last") {
            for (let i = state.counter; i < state.joclyMoveStrings.length; i++) {
                await state.match.applyMove(state.joclyMoveStrings[i]);
                state.counter += 1;
                renderMoveHistory(state.moves, state.counter);
            }
            return;
        };
    });
}

async function preloadAllMovesIntoJocly() {
    state.counter = 0;
    for (let i = 0; i < state.moves.length; i++) {
        const dto = state.moves[i];
        const moveString = dtoToJoclyString(dto);
        const move = await state.match.pickMove(moveString);
        await state.match.applyMove(move);
        state.joclyMoveStrings[i] = move;
        state.counter += 1;
    }
    renderMoveHistory(state.moves, state.counter);
}

function renderMoveHistory(moves, showUntil) {
    const moveHistory = document.getElementById("move-history");
    if (!moveHistory) return;

    if (state.joclyMoveStrings === 0) {
        moveHistory.innerHTML = "";
        return;
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

async function main() {
    state.gameid = window.location.hash.substring(1);
    if (!state.gameid) return;

    const gameState = await API.getGameState(state.gameid);
    applyPlayersInformation(gameState);
    state.moves = await API.getMoves(state.gameid);
    state.match = await Jocly.createMatch("classic-chess");
    const board = document.querySelector("[data-js-board]");
    await state.match.attachElement(board);
    await state.match.setViewOptions({ skin: "skin2dfull" });

    await preloadAllMovesIntoJocly();
    setUpGameReplayNavigation();
    await goTo(state.moves.length);
}

document.addEventListener("DOMContentLoaded", main);
window.addEventListener("hashchange", () => {
    location.reload();
});