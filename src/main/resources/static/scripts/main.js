import { gameToJoclyState, joclyToObj, objToJocly } from "./conversion.js";
import { initWebsocket } from "./websocket.js";
import { API } from "./api.js";

const state = {
    gameid: null,
    match: null,
    user: null,
    send: null
};

/* Helpers */

function getLocalpartFromHandle(handle) {
    if (!handle) return "unknown";
    let string = String(handle);

    if (string.startsWith("acct:")) string = string.slice(5)

    try {
        const url = new URL(string);
        const last = url.pathname.split("/").filter(Boolean).pop();
        return last || url.host || "Unknown";
    } catch (_) { }

    const at = string.indexOf("@");
    if (at > 0) return string.slice(0, at);

    return string;
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

/* Main functions */

function setupInvitationEvents() {
    const list = document.querySelector("[data-js-invitations]");
    if (!list) return;

    list.addEventListener("click", (e) => {
        const btn = e.target.closest("button[data-action]");
        if (!btn) return;

        const card = btn.closest("[data-js-challenge]");
        if (!card) return;

        const id = card.dataset.jsChallenge;
        const action = btn.dataset.action;

        if (action === "accept") {
            acceptInvite(id);
            card.remove();
        }

        if (action === "delete") {
            card.remove();
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    state.gameid = window.location.hash.substring(1);
    let button = document.querySelector("[data-js-invite]");
    button.addEventListener("click", () => {
        let input = document.querySelector("[data-js-opponent]");
        let opponent = input.value;
        input.value = "";
        API.createInvitation(opponent).then(result => {
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
        const moves = await API.getMoves(state.gameid);
        renderMoveHistory(moves);
        requestUserInput(state.send);
    } else if (msg.type == 0) {
        addGameToList(msg.context);
    } else if (msg.type == 1) {
        addInvitation(msg.context, msg.data);
    }
}

async function main() {
    state.user = await API.getUserData().username;
    state.send = await initWebsocket(onMessage);
    setupInvitationEvents();
    let games = await API.getGames();
    games.forEach(game => {
        addGameToList(game.id);
    });
    API.getInvitations().then(invitations => {
        invitations.forEach(invitation => {
            addInvitation(invitation.id, invitation);
        })
    })

    state.match = await Jocly.createMatch("classic-chess");

    let element = document.querySelector("[data-js-board]");
    await state.match.attachElement(element);
    await state.match.setViewOptions({ skin: "skin2dfull" });

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
    const moves = await API.getMoves(state.gameid);
    renderMoveHistory(moves);
}

async function loadGame() {
    if (!state.gameid) return;
    let gameState = await API.getGameState(state.gameid);
    applyPlayersInformation(gameState, state.user);
    let moves = await API.getMoves(state.gameid);
    renderMoveHistory(moves);

    let init = gameToJoclyState(gameState);
    await state.match.abortUserTurn();
    await state.match.load(init);

    if (gameState.yourTurn) {
        await requestUserInput(state.send);
    }
}

async function addGameToList(gameId) {
    let element = document.querySelector("[data-js-gamelist]");
    let entry = `<li><a href="/play#${gameId}">${gameId}</a></li>`;
    element.innerHTML += entry;
}

function applyPlayersInformation(gameDto, ownUsername) {
    // add later api request with handle for more accurate informations
    const whiteName = getLocalpartFromHandle(gameDto.white);
    const blackName = getLocalpartFromHandle(gameDto.black);

    // white is always at the bottom and black always at the top
    const elTop = document.getElementById("top-player-name");
    if (elTop) {
        elTop.textContent = blackName;
    }
    const elBottom = document.getElementById("bottom-player-name");
    if (elBottom) {
        elBottom.textContent = whiteName;
    }
}

function renderMoveHistory(moves) {
    const moveHistory = document.getElementById("move-history");
    if (!moveHistory) {
        return;
    }

    if (!Array.isArray(moves) || moves.length === 0) {
        moveHistory.innerHTML = "";
        return;
    }
    let html = "";
    for (let i = 0; i < moves.length; i += 2) {
        const moveNumber = Math.floor(i / 2) + 1;
        const whiteMove = moves[i] ?? null;
        const blackMove = moves[i + 1] ?? null;
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

async function addInvitation(id, challenge) {
    const list = document.querySelector("[data-js-invitations]");
    if (!list) return;

    if (list.querySelector(`[data-js-challenge="${id}"]`)) return;

    let white = "Random";
    if (challenge.white) {
        white = challenge.white === challenge.source ? challenge.sourceHandle : "You";
    }

    const li = document.createElement("li");
    li.className = "invitation-card";
    li.dataset.jsChallenge = id;

    const avatar = document.createElement("div");
    avatar.className = "invitation-avatar";
    avatar.setAttribute("aria-hidden", "true");

    const body = document.createElement("div");
    body.className = "invitation-body";

    const title = document.createElement("div");
    title.className = "invitation-title";
    title.textContent = `${challenge.sourceHandle} invited you to play`;

    const meta = document.createElement("div");
    meta.className = "invitation-meta";
    meta.textContent = `White Player: ${white}`;

    const actions = document.createElement("div");
    actions.className = "invitation-actions";

    const acceptBtn = document.createElement("button");
    acceptBtn.className = "btn";
    acceptBtn.type = "button";
    acceptBtn.dataset.action = "accept";
    acceptBtn.textContent = "Start Game";

    const deleteBtn = document.createElement("button");
    deleteBtn.className = "btn btn--ghost";
    deleteBtn.type = "button";
    deleteBtn.dataset.action = "delete";
    deleteBtn.textContent = "Delete";

    actions.append(acceptBtn, deleteBtn);
    body.append(title, meta, actions);
    li.append(avatar, body);
    list.prepend(li);

}

async function acceptInvite(challengeId) {
    let msg = {
        type: 2,
        context: challengeId
    };
    state.send(msg);
}