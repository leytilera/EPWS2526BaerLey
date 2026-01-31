function generateCastle(state) {
    let str = "";
    if (state.whiteShort) {
        str += "K";
    } 
    if (state.whiteLong) {
        str += "Q";
    }
    if (state.blackShort) {
        str += "k";
    }
    if (state.blackLong) {
        str += "q";
    }
    if (!str) {
        str = "-";
    }
    return str;
}

function parseCastle(str) {
    return {
        whiteShort: str.includes("K"),
        whiteLong: str.includes("Q"),
        blackShort: str.includes("k"),
        blackLong: str.includes("q")
    };
}

function generateBoard(board) {
    let str = "";
    for (let i = 7; i >= 0; i--) {
        let freeCounter = 0;
        for (let j = 0; j < 8; j++) {
            if (!board[i][j]) {
                freeCounter++;
            } else {
                if (freeCounter != 0) {
                    str += freeCounter;
                    freeCounter = 0;
                }
                str += board[i][j];
            }
            
        }
        if (freeCounter != 0) {
            str += freeCounter;
        }
        if (i != 0) {
            str += "/";
        }
    }
    return str;
}

function getTurn(current, white, black) {
    if (current === white) {
        return "w";
    } else if (current === black) {
        return "b";
    }
    return null;
}

function generateFen(game) {
    let parts = [];
    parts[0] = generateBoard(game.board);
    parts[1] = getTurn(game.currentTurn, game.white, game.black);
    parts[2] = generateCastle(game.castleState);
    parts[3] = game.enPassantField ? game.enPassantField : "-";
    parts[4] = "0";
    parts[5] = "1";
    return parts.join(" ");
}

export {generateFen};