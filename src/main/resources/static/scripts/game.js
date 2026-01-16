/* CONSTANTS & GLOBALS */

const gameId = new URL(window.location.href).searchParams.get("gameId") || "defaultGame";
let ws = null;
let match = null;
const wsStatus = document.getElementById("wsStatus");

let myColor = null;
let turn = null;
let matchReady = false;
let userTurnInProgress = false;
let awaitingServerAck = false;
let syncInProgress = false;
let intentionalClose = false;

const moveHistory = []

let reconnectAttempts = 0;
let reconnectTimer = null;

let lastAppliedMoveIndex = -1;


/* JOCLY */
async function initJoclyNew() {
    if (typeof Jocly === 'undefined') {
        throw new Error("Jocly library is not loaded.");
    }

    if (match && matchReady) return;

    // abortUserTurnIfNeeded();

    match = await Jocly.createMatch("classic-chess");
    await match.attachElement(document.getElementById("board"));
    await match.setViewOptions({ sounds: false });

    matchReady = true;
    await setBoardPerspective();
}

async function setBoardPerspective() {
    if (!matchReady || !myColor) return;
    const perspective = (myColor === "WHITE") ? Jocly.PLAYER_A : Jocly.PLAYER_B;
    await match.setViewOptions({ viewAs: perspective });
}

function isMyTurn() {
    return myColor && turn && myColor === turn;
}

function abortUserTurnIfNeeded() {
    if (!matchReady) return;
    if (!userTurnInProgress) return;
    match.abortUserTurn().catch(() => { });
    userTurnInProgress = false;
}

function maybeStartUserTurn() {
    if (!matchReady || !ws) return;
    if (!myColor || !turn) return;
    if (awaitingServerAck) return;
    if (syncInProgress) return;

    if (!isMyTurn()) {
        abortUserTurnIfNeeded();
        return;
    }

    if (userTurnInProgress) return;
    startUserTurnLoop().catch((e) => console.error("userTurn loop error:", e));
}


async function startUserTurnLoop() {
    if (!isMyTurn()) return;
    if (userTurnInProgress) return;

    userTurnInProgress = true;

    try {
        const result = await match.userTurn();
        const moveString = await match.getMoveString(result.move);

        awaitingServerAck = true;
        ws.send(JSON.stringify({
            type: "MOVE_SUBMIT",
            move: moveString
        }));
        userTurnInProgress = false;
    } catch (e) {
        userTurnInProgress = false;

        if (String(e).includes("Aborted")) return;

        console.error("userTurn failed:", e);
    }
}


/* RENDERING */

function render() {
    document.getElementById("uiGameId").textContent = gameId;
    document.getElementById("uiColor").textContent = myColor ?? "—";
    document.getElementById("uiTurn").textContent = turn ?? "—";
    document.getElementById("uiLastIndex").textContent = String(lastAppliedMoveIndex);
}


/* DEBUG MOVE SENDING */

function sendMove() {
    const move = document.getElementById("moveInput").value;
    ws.send(JSON.stringify({ type: "MOVE_SUBMIT", move: move }));
}


/* WEBSOCKET */

function initWebSocket() {
    if (ws && (ws.readyState === WebSocket.OPEN || ws.readyState === WebSocket.CONNECTING)) {
        intentionalClose = true;
        try {
            ws.close();
        } catch (e) {
            console.error("Error closing existing WebSocket:", e);
        }
    }

    ws = new WebSocket("ws://" + location.host + "/game?gameId=" + encodeURIComponent(gameId));

    ws.onopen = async () => {
        reconnectAttempts = 0;
        wsStatus.textContent = "WebSocket connected.";
        ws.send(JSON.stringify({ type: "SNAPSHOT_REQUEST" }));
    }

    ws.onclose = () => {
        wsStatus.textContent = "WebSocket disconnected.";
        abortUserTurnIfNeeded();

        if (intentionalClose) {
            intentionalClose = false;
            return;
        }

        scheduleReconnect();
    }

    ws.onerror = (error) => {
        wsStatus.textContent = "WebSocket error: " + error;
        console.error("WebSocket error:", error);
    }

    ws.onmessage = async (event) => {
        const message = JSON.parse(event.data);

        if (message.type === "JOINED") {
            myColor = message.color;
            turn = message.turn;
            render();
            await setBoardPerspective();
            maybeStartUserTurn();
        }
        else if (message.type === "SNAPSHOT") {
            await applySnapshot(message);
        }
        else if (message.type === "GAME_STATE") {
            turn = message.turn;
            render();
            maybeStartUserTurn();
        }
        else if (message.type === "MOVE_COMMITTED") {
            const playerColor = message.color;
            const moveString = message.move;
            const moveIndex = message.index;

            if (moveIndex <= lastAppliedMoveIndex) {
                console.log("Move already applied, skipping:", moveString);
                return;
            }

            pushMove(playerColor, moveString);

            if (playerColor !== myColor) {
                try {
                    const moveObject = await match.pickMove(moveString);
                    await match.applyMove(moveObject);
                } catch (error) {
                    console.error("Failed to apply opponent's move:", moveString, error);
                    alert("Error applying opponent's move: " + moveString);
                    ws.send(JSON.stringify({ type: "SNAPSHOT_REQUEST" }));
                    return
                }
            }

            lastAppliedMoveIndex = moveIndex;
            turn = message.turn;
            awaitingServerAck = false;

            render();
            maybeStartUserTurn();
        }
        else if (message.type === "MOVE_REJECTED") {
            console.log("Move rejected:", message.reason);
            alert("Rejected: " + message.reason);
            try {
                await match.rollback(-1);
            } catch (error) {
                console.error("Failed to rollback move after rejection:", error);
                ws.send(JSON.stringify({ type: "SNAPSHOT_REQUEST" }));
                return;
            }
            awaitingServerAck = false;
            maybeStartUserTurn();
        }
        else if (message.type === "ERROR") {
            console.log("Server error:", message.message);
            alert("Error: " + message.message);
        }
        else {
            console.log("Unknown message:", message);
        }
    }
}


/* RECONNECT LOGIC */

function scheduleReconnect() {
    if (reconnectTimer) return;

    reconnectAttempts++;
    const delay = Math.min(30000, Math.pow(2, reconnectAttempts) * 1000); // Exponential backoff up to 30s
    wsStatus.textContent = `WebSocket disconnected. Reconnecting in ${delay / 1000} seconds...`;
    reconnectTimer = setTimeout(() => {
        initWebSocket();
        reconnectTimer = null;
    }, delay);
}


async function applySnapshot(snapshot) {
    syncInProgress = true;
    awaitingServerAck = false;
    abortUserTurnIfNeeded();

    try {
        await initJoclyNew();
        resetMoveHistory();
        lastAppliedMoveIndex = -1;

        const moves = snapshot.moves;
        for (const moveEntry of moves) {
            // const playerColor = moveEntry.color;
            const moveString = moveEntry.move;
            const moveIndex = moveEntry.index;

            try {
                const moveObject = await match.pickMove(moveString);
                await match.applyMove(moveObject);
                pushMove(playerColor, moveString);
                lastAppliedMoveIndex = moveIndex;
            } catch (error) {
                console.error("Failed to apply move from snapshot:", moveString, error);
                alert("Error applying move from snapshot: " + moveString);
                return;
            }
        }
        turn = snapshot.turn;
        render();
        await setBoardPerspective();
    } finally {
        syncInProgress = false;
    }
    maybeStartUserTurn();
}


/* MOVE HISTORY */

const movesHistory = document.getElementById("movesHistory");

function renderMoveHistory() {
    movesHistory.innerHTML = "";

    for (let i = 0; i < moveHistory.length; i += 2) {
        const plyNumber = Math.floor(i / 2) + 1;
        const white = moveHistory[i]?.moveString ?? "";
        const black = moveHistory[i + 1]?.moveString ?? "";

        const row = document.createElement("div");
        row.className = "move-row";

        const number = document.createElement("div");
        number.className = "move-number";
        number.textContent = plyNumber + ".";

        const moveWhite = document.createElement("div");
        moveWhite.className = "move-cell" + (white ? "" : " move-cell--empty");
        moveWhite.textContent = white || "…";

        const moveBlack = document.createElement("div");
        moveBlack.className = "move-cell" + (black ? "" : " move-cell--empty");
        moveBlack.textContent = black || "…";
        row.append(number, moveWhite, moveBlack);
        movesHistory.appendChild(row);
    }
}

function pushMove(by, moveString) {
    moveHistory.push({ by, moveString });
    renderMoveHistory();
}

function resetMoveHistory() {
    moveHistory.length = 0;
    renderMoveHistory();
}


/* INITIALIZATION */

window.addEventListener("load", async () => {
    try {
        await fetch("/api/session/anonymous", {
            method: "POST",
            credentials: "include"
        });

        await initJoclyNew();

        initWebSocket();
    } catch (e) {
        console.error(e);
        alert(String(e));
    }
});