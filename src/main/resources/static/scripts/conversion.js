import { generateFen } from "./fen.js";

function joclyToObj(joclyMove, isWhite) {
    let move = {
        "source": null,
        "target": null,
        "promote": null,
        "castle": false,
        "capture": false,
    };

    joclyMove = joclyMove.replace("+", "");

    if (joclyMove.includes("=")) {
        let parts = joclyMove.split("=");
        joclyMove = parts[0];
        move.promote = parts[1].toLowerCase();
    }

    if (joclyMove == "O-O") {
        move.castle = true;
        if (isWhite) {
            move.source = "e1";
            move.target = "g1"
        } else {
            move.source = "e8";
            move.target = "g8";
        }
    } else if (joclyMove == "O-O-O") {
        move.castle = true;
        if (isWhite) {
            move.source = "e1";
            move.target = "b1";
        } else {
            move.source = "e8";
            move.target = "b8";
        }
    } else if (joclyMove.includes("-")) {
        let parts = joclyMove.split("-");
        let src = parts[0];
        let trgt = parts[1];
        if (src.length == 3) {
            src = src.substring(1);
        }
        move.source = src;
        move.target = trgt;
    } else if (joclyMove.includes("x")) {
        let parts = joclyMove.split("x");
        let src = parts[0];
        let trgt = parts[1];
        if (src.length == 3) {
            src = src.substring(1);
        }
        move.source = src;
        move.target = trgt;
        move.capture = true;
    }

    return move;
}

function objToJocly(obj) {
    if (obj.castle) {
        if (obj.target == "g1" || obj.target == "g8") {
            return "O-O";
        } else if (obj.target == "b1" || obj.target == "b8") {
            return "O-O-O";
        } else {
            return null;
        }
    }
    let seperator = obj.capture ? "x" : "-";
    let move = obj.source + seperator + obj.target;
    if (obj.promote) {
        move = move + "=" + obj.promote;
    }
    return move;
}

function gameToJoclyState(game) {
    return {
        "initialBoard": generateFen(game),
        "game": "classic-chess",
        "playedMoves": []
    };
}

export {joclyToObj, objToJocly, gameToJoclyState};