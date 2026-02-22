async function getGameState(gameid) {
    return await fetch(`/api/games/${gameid}`).then((res) => res.json());
}

async function getUserData() {
    return await fetch('/api/user').then((res) => res.json());
}

async function getGames() {
    return await fetch('/api/games').then((res) => res.json());
}

async function getMoves(gameid) {
    return await fetch(`/api/games/${gameid}/moves`).then((res) => res.json());
}

async function getInvitations() {
    return await fetch('/api/challenges').then((res) => res.json());
}

async function createInvitation(opponent) {
    let body = JSON.stringify({ opponent: opponent });
    return await fetch('/api/challenges', {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: body
    }).then(res => res.ok);
}

async function viewProfile(handle) {
    return await fetch(`/api/users/${encodeURIComponent(handle)}`, {
        headers: { "Accept": "application/json" }
    }).then(res => res.ok);
}

let API = {
    getGameState,
    getUserData,
    getGames,
    getMoves,
    getInvitations,
    createInvitation,
    viewProfile
};

export { API };