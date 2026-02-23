import { gameToJoclyState, joclyToObj, objToJocly } from "./conversion.js";
import { initWebsocket } from "./websocket.js";
import { API } from "./api.js";
import { parseActorUrlOrHandle, formatRawMoveToHistoryItem, applyPlayersInformation } from "./ui-helpers.js";

const state = {
    gameid: null,
    match: null,
    user: null,
    send: null,
    moves: []
};

/* Invitations */

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
    const statusEl = document.querySelector("[data-js-invite-status]")
    let inviteButton = document.querySelector("[data-js-invite]");
    inviteButton.addEventListener("click", () => {
        let input = document.querySelector("[data-js-opponent]");
        let opponent = input.value;
        input.value = "";
        API.createInvitation(opponent).then(result => {
            if (result) {
                if (statusEl) {
                    statusEl.textContent = `${opponent} was invited!`;
                } else {
                    alert(`${opponent} was invited!`);
                }
            } else {
                if (statusEl) {
                    statusEl.textContent = `Can't invite ${opponent}`;
                } else {
                    alert(`Can't invite ${opponent}`);
                }
            }
        });
    });
    let viewProfileButton = document.querySelector("[data-js-view-profile]");
    viewProfileButton.addEventListener("click", () => {
        let input = document.querySelector("[data-js-opponent]");
        let handle = input?.value?.trim();
        if (!handle) return;
        API.viewProfile(handle).then(result => {
            if (result) {
                window.location.href = `/users?acct=${encodeURIComponent(handle)}`;
            } else {
                if (statusEl) {
                    statusEl.textContent = `Can't find ${handle}`;
                } else {
                    alert(`Can't find ${handle}`);
                }
            }
        });
    });
    main();
});

async function addInvitation(id, challenge) {
    const list = document.querySelector("[data-js-invitations]");
    if (!list) return;

    if (list.querySelector(`[data-js-challenge="${id}"]`)) return;

    const li = document.createElement("li");
    li.className = "invitation-card";
    li.dataset.jsChallenge = id;

    const avatar = document.createElement("div");
    avatar.className = "invitation-avatar";
    const img = document.createElement("img");
    img.src = "/assets/Chessfed_Default_Avatar.jpg";
    img.alt = `${challenge.sourceHandle} avatar`;
    img.loading = "lazy";
    avatar.appendChild(img);
    avatar.setAttribute("aria-hidden", "true");

    const body = document.createElement("div");
    body.className = "invitation-body";

    const title = document.createElement("div");
    title.className = "invitation-title";
    title.textContent = `${challenge.sourceHandle} invited you to play`;
    title.style.cursor = "pointer";
    title.addEventListener("click", () => {
        window.location.href = `/users?acct=${encodeURIComponent(challenge.sourceHandle)}`;
    });

    const meta = document.createElement("div");
    meta.className = "invitation-meta";
    meta.textContent = `Variant: Classic Chess`;

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

/* Incoming messages & main */

async function onMessage(msg) {
    if (msg.type == 3) {
        let gameState = await API.getGameState(msg.context);
        addGameToListOrUpdate(gameState);
        if (msg.context == state.gameid) {
            let moveStr = objToJocly(msg.data);
            let move = await state.match.pickMove(moveStr);
            await state.match.playMove(move);
            const moves = await API.getMoves(state.gameid);
            renderMoveHistory(moves);
            gameState = await API.getGameState(state.gameid);
            if (gameState.finished) {
                showFinishedCard(gameState);
                return;
            }
            requestUserInput(state.send);
        }
    } else if (msg.type == 0) {
        const game = await API.getGameState(msg.context);
        addGameToListOrUpdate(game);
    } else if (msg.type == 1) {
        addInvitation(msg.context, msg.data);
    }
}

async function main() {
    state.user = await API.getUserData();
    state.send = await initWebsocket(onMessage);
    setupInvitationEvents();
    let games = await API.getGames();
    games.forEach(game => {
        addGameToListOrUpdate(game);
    });
    API.getInvitations().then(invitations => {
        invitations.forEach(invitation => {
            addInvitation(invitation.id, invitation);
        })
    })

    if (window.location.pathname !== "/play" && window.location.pathname !== "/") return;
    state.match = await Jocly.createMatch("classic-chess");

    let element = document.querySelector("[data-js-board]");
    await state.match.attachElement(element);
    await state.match.setViewOptions({ skin: "skin2dfull" });

    await loadGame();
}

/* Gameplay */

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

    // optimistic move history & turn update
    state.moves.push({
        source: msg.data.source,
        target: msg.data.target,
        promote: msg.data.promote ?? null,
        capture: msg.data.capture,
        castle: msg.data.castle
    })
    const game = await API.getGameState(state.gameid);
    game.yourTurn = false;
    addGameToListOrUpdate(game);
    // wait for server authority
    setTimeout(async () => {
        state.moves = await API.getMoves(state.gameid);
        const gameState = await API.getGameState(state.gameid);
        renderMoveHistory(state.moves);
        if (gameState.finished) {
            showFinishedCard(gameState);
        }
    }, 600);
}

async function loadGame() {
    if (!state.gameid) return;
    const finishedEl = document.getElementById("game-finished");
    if (finishedEl) finishedEl.innerHTML = "";

    let gameState = await API.getGameState(state.gameid);
    applyPlayersInformation(gameState, state.user);
    state.moves = await API.getMoves(state.gameid);
    renderMoveHistory(state.moves);

    let init = gameToJoclyState(gameState);
    await state.match.abortUserTurn();
    await state.match.load(init);

    if (gameState.finished) {
        showFinishedCard(gameState);
        return;
    }
    if (gameState.yourTurn) {
        await requestUserInput(state.send);
    }
}

async function addGameToListOrUpdate(game, finished = null) {
    let gameList = document.querySelector("[data-js-gamelist]");
    const white = parseActorUrlOrHandle(game.white);
    const black = parseActorUrlOrHandle(game.black);
    let turnLabel;
    if (finished) {
        turnLabel = finished;
    } else {
        turnLabel = game.yourTurn ? "Your turn" : "Wait for opponents turn";
    }
    let li = document.querySelector(`[data-gameid="${game.id}"]`);
    const alreadyExists = li;

    if (!alreadyExists) {
        li = document.createElement("li");
        li.className = "gamelist-card";
        li.dataset.gameid = game.id;
        li.addEventListener("click", () => {
            window.location.href = `/play#${game.id}`;
        });
    }
    li.innerHTML = `
        <div class="gamelist-card__header">
            <div class="gamelist-card__label">ID:</div>
            <div class="gamelist-card__id">${game.id}</div>
        </div>
        <div class="gamelist-card__body">
            <div class="gamelist-card__player">
                <span class="gamelist-card__avatar avatar--is-white" aria-hidden="true"></span>
                <span class="gamelist-card__name">${white.handle ?? white.localpart}</span>
            </div>
            <div class="gamelist-card__player">
                <span class="gamelist-card__avatar avatar--is-black" aria-hidden="true"></span>
                <span class="gamelist-card__name">${black.handle ?? black.localpart}</span>
            </div>
            <div class="gamelist-card__label">${turnLabel}</div>
        </div>
        `
    if (!alreadyExists) {
        gameList.prepend(li);
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

function showFinishedCard(gameDto) {
    let finished = document.getElementById("game-finished");
    if (!finished) return;
    finished.innerHTML = "";

    const white = parseActorUrlOrHandle(gameDto.white);
    const black = parseActorUrlOrHandle(gameDto.black);
    const winner = parseActorUrlOrHandle(gameDto.winner);

    // state.user.username contains handle
    let result;
    if (!winner.handle) {
        result = "Draw!";
    } else if (winner.handle == state.user.username) {
        result = "You won!";
    } else {
        result = `${winner.handle} won!`;
    }
    let avatarClass;
    if (winner.handle == white.handle) {
        avatarClass = "avatar--is-white";
    } else if (winner.handle == black.handle) {
        avatarClass = "avatar--is-black";
    } else {
        avatarClass = "avatar--is-blue";
    }
    
    const finishedCard = document.createElement("div");
    finishedCard.className = "game-finished-card";
    finishedCard.innerHTML = `
        <div class="game-finished-card__title">Game finished</div>
        <div class="game-finished-card__body">
            <span class="game-finished__avatar ${avatarClass}" aria-hidden="true"></span>
            <span class="game-finished__text">${result}</span>
        </div>
        <div class="game-finished__buttons">
            <a class="btn btn--replay" href="/replay#${state.gameid}">Replay Game</a>
        </div>
        `
    finished.appendChild(finishedCard);
    addGameToListOrUpdate(gameDto, result);
}

/* Event Listener */

window.addEventListener('hashchange', (event) => {
    event.preventDefault();
    state.gameid = window.location.hash.substring(1);
    loadGame();
});