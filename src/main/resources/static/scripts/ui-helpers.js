/* Helpers */

export function parseActorUrlOrHandle(reference) {
    if (!reference) {
        return {
            localpart: "unknown",
            domain: null,
            handle: null,
            url: null
        }
    }
    let string = String(reference).trim();

    if (string.startsWith("acct:")) {
        string = string.slice(5)
    }

    try {
        const url = new URL(string);
        const parts = url.pathname.split("/").filter(Boolean);
        const localpart = parts.pop() ?? null;
        const domain = url.host ?? null;
        return {
            localpart: localpart,
            domain: domain,
            handle: localpart && domain ? `${localpart}@${domain}` : null,
            url: url.href
        }
    } catch (e) { }

    const atIndex = string.indexOf("@");
    if (atIndex > 0) {
        const localpart = string.slice(0, atIndex);
        const domain = string.slice(atIndex + 1);
        return {
            localpart: localpart,
            domain: domain,
            handle: string,
            url: null,
        }
    } else {
        return {
            localpart: string,
            domain: null,
            handle: null,
            url: null,
        }
    }
}

export function formatRawMoveToHistoryItem(rawMove) {
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

export function applyPlayersInformation(gameDto) {
    const white = parseActorUrlOrHandle(gameDto.white);
    const black = parseActorUrlOrHandle(gameDto.black);

    // white is always at the bottom and black always at the top
    const topNameEl = document.getElementById("top-player-name");
    const topStatusEl = document.getElementById("top-player-status");
    if (topNameEl) {
        topNameEl.textContent = black.localpart;
        if (black.handle) {
            topStatusEl.textContent = black.handle;
            topStatusEl.style.cursor = "pointer";
            topStatusEl.onclick = () =>
                window.location.href = `/users?acct=${encodeURIComponent(black.handle)}`;
        }
    }
    const bottomNameEl = document.getElementById("bottom-player-name");
    const bottomStatusEl = document.getElementById("bottom-player-status");
    if (bottomNameEl) {
        bottomNameEl.textContent = white.localpart;
        if (white.handle) {
            bottomStatusEl.textContent = white.handle
            bottomStatusEl.style.cursor = "pointer";
            bottomStatusEl.onclick = () =>
                window.location.href = `/users?acct=${encodeURIComponent(white.handle)}`;
        }
    }
}