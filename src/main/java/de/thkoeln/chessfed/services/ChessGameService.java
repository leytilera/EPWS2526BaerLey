package de.thkoeln.chessfed.services;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.thkoeln.chessfed.exception.ResourceNotFoundException;
import de.thkoeln.chessfed.exception.InvalidMoveException;
import de.thkoeln.chessfed.model.Actor;
import de.thkoeln.chessfed.model.CastleState;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.ChessMove;
import de.thkoeln.chessfed.model.ChessPiece;
import de.thkoeln.chessfed.model.ChessPlayer;
import de.thkoeln.chessfed.model.FederatedObject;
import de.thkoeln.chessfed.model.IChessGameRepository;
import de.thkoeln.chessfed.model.IChessMoveRepository;
import de.thkoeln.chessfed.model.ObjectType;

@Service
public class ChessGameService implements IChessGameService {
    
    private IChessGameRepository gameRepository;
    private IChessMoveRepository moveRepository;
    private IFederationService federationService;
    private IRuleEngineService ruleEngine;
    private IChessBoardService boardService;

    @Autowired
    public ChessGameService(IChessGameRepository gameRepository, IChessMoveRepository moveRepository, IFederationService federationService, IRuleEngineService ruleEngine, IChessBoardService boardService) {
        this.gameRepository = gameRepository;
        this.moveRepository = moveRepository;
        this.federationService = federationService;
        this.ruleEngine = ruleEngine;
        this.boardService = boardService;
    }

    @Override
    public byte getFieldFlag(ChessPiece piece, ChessPlayer player) {
        if (piece == null) return 0;
        return piece.toField(player);
    }

    @Override
    public ChessPiece getPiece(byte fieldFlag) {
        return ChessPiece.fromField(fieldFlag);
    }

    @Override
    public ChessPlayer getPlayer(byte fieldFlag) {
        return ChessPlayer.fromField(fieldFlag);
    }

    @Override
    public int getFieldId(String fieldDescriptor) {
        return boardService.getFieldIndex(fieldDescriptor);
    }

    @Override
    public String getFieldDescriptor(int fieldId) {
        return boardService.getFieldDescriptor(fieldId);
    }

    @Override
    public int getFieldRowIndex(int fieldId) {
        return boardService.getFieldRowIndex(fieldId);
    }

    @Override
    public int getFieldColumnIndex(int fieldId) {
        return boardService.getFieldColumnIndex(fieldId);
    }

    @Override
    public int getFieldId(int rowIndex, int columnIndex) {
        return boardService.getFieldIndex(rowIndex, columnIndex);
    }

    @Override
    public ChessMove createMove(ChessGame game, int sourceFieldId, int targetFieldId) {
        ChessMove move = new ChessMove();
        move.setGame(game);
        move.setSourceField(sourceFieldId);
        move.setTargetField(targetFieldId);
        move.setMoveCount(game.getMoveCounter() + 1);
        move.setPlayer(game.getCurrentTurn());
        move.setCastle(false);
        move.setCapture(targetFieldId == game.getEnPassentField() || game.getFields()[targetFieldId] != 0);
        move.setPromote(null);
        return move;
    }

    @Override
    public List<ChessMove> getMoves(ChessGame game) {
        List<ChessMove> moves = moveRepository.getAllByGame(game);
        Collections.sort(moves, (ChessMove m1, ChessMove m2) -> m1.getMoveCount() - m2.getMoveCount());
        return moves;
    }

    @Override
    public void applyMove(ChessMove move, boolean force) throws InvalidMoveException {
        ChessPlayer winner = null;
        if (!force) {
            winner = ruleEngine.checkMove(move.getGame(), move);
        }
        ChessGame game = applyMoveUnchecked(move.getGame(), move);
        game.setMoveCounter(move.getMoveCount());
        if (winner != null) {
            game.setHasEnded(true);
            game.setCurrentTurn(winner);
        }
        if (move.getFederation() == null) {
            FederatedObject federatedObject = federationService.createFederatedObject(federationService.getBaseUrl() + "/games/" + game.getId() + "/moves/" + move.getMoveCount(), ObjectType.MOVE);
            move.setFederation(federatedObject);
        }
        gameRepository.save(game);
        moveRepository.save(move);
    }

    @Override
    public ChessGame createGame(Actor whitePlayer, Actor blackPlayer) {
        ChessGame game = new ChessGame();
        game.setWhitePlayer(whitePlayer);
        game.setBlackPlayer(blackPlayer);
        game.setCurrentTurn(ChessPlayer.WHITE);
        game.setMoveCounter(0);
        game.setHasEnded(false);
        game.setCastleState(new CastleState(true, true, true, true));
        game.setFields(setupBoard());
        FederatedObject federation = federationService.createFederatedObject(game.getId(), ObjectType.GAME);
        game.setFederation(federation);
        gameRepository.save(game);
        return game;
    }

    private byte[] setupBoard() {
        byte[] board = new byte[64];

        board[0] = getFieldFlag(ChessPiece.ROOK, ChessPlayer.WHITE);
        board[1] = getFieldFlag(ChessPiece.KNIGHT, ChessPlayer.WHITE);
        board[2] = getFieldFlag(ChessPiece.BISHOP, ChessPlayer.WHITE);
        board[3] = getFieldFlag(ChessPiece.QUEEN, ChessPlayer.WHITE);
        board[4] = getFieldFlag(ChessPiece.KING, ChessPlayer.WHITE);
        board[5] = getFieldFlag(ChessPiece.BISHOP, ChessPlayer.WHITE);
        board[6] = getFieldFlag(ChessPiece.KNIGHT, ChessPlayer.WHITE);
        board[7] = getFieldFlag(ChessPiece.ROOK, ChessPlayer.WHITE);

        board[8] = getFieldFlag(ChessPiece.PAWN, ChessPlayer.WHITE);
        board[9] = getFieldFlag(ChessPiece.PAWN, ChessPlayer.WHITE);
        board[10] = getFieldFlag(ChessPiece.PAWN, ChessPlayer.WHITE);
        board[11] = getFieldFlag(ChessPiece.PAWN, ChessPlayer.WHITE);
        board[12] = getFieldFlag(ChessPiece.PAWN, ChessPlayer.WHITE);
        board[13] = getFieldFlag(ChessPiece.PAWN, ChessPlayer.WHITE);
        board[14] = getFieldFlag(ChessPiece.PAWN, ChessPlayer.WHITE);
        board[15] = getFieldFlag(ChessPiece.PAWN, ChessPlayer.WHITE);

        board[48] = getFieldFlag(ChessPiece.PAWN, ChessPlayer.BLACK);
        board[49] = getFieldFlag(ChessPiece.PAWN, ChessPlayer.BLACK);
        board[50] = getFieldFlag(ChessPiece.PAWN, ChessPlayer.BLACK);
        board[51] = getFieldFlag(ChessPiece.PAWN, ChessPlayer.BLACK);
        board[52] = getFieldFlag(ChessPiece.PAWN, ChessPlayer.BLACK);
        board[53] = getFieldFlag(ChessPiece.PAWN, ChessPlayer.BLACK);
        board[54] = getFieldFlag(ChessPiece.PAWN, ChessPlayer.BLACK);
        board[55] = getFieldFlag(ChessPiece.PAWN, ChessPlayer.BLACK);

        board[56] = getFieldFlag(ChessPiece.ROOK, ChessPlayer.BLACK);
        board[57] = getFieldFlag(ChessPiece.KNIGHT, ChessPlayer.BLACK);
        board[58] = getFieldFlag(ChessPiece.BISHOP, ChessPlayer.BLACK);
        board[59] = getFieldFlag(ChessPiece.QUEEN, ChessPlayer.BLACK);
        board[60] = getFieldFlag(ChessPiece.KING, ChessPlayer.BLACK);
        board[61] = getFieldFlag(ChessPiece.BISHOP, ChessPlayer.BLACK);
        board[62] = getFieldFlag(ChessPiece.KNIGHT, ChessPlayer.BLACK);
        board[63] = getFieldFlag(ChessPiece.ROOK, ChessPlayer.BLACK);

        return board;
    }

    @Override
    public ChessGame getGame(UUID id) {
        return gameRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public ChessMove getMove(ChessGame game, int count) {
        return moveRepository.getByGameAndMoveCount(game, count).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public List<ChessGame> getGames(Actor player) {
        return gameRepository.getAllByWhitePlayerOrBlackPlayer(player, player);
    }

    @Override
    public ChessGame getGame(FederatedObject federatedObject) {
        return gameRepository.getByFederation(federatedObject).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public void addRemoteGame(ChessGame game) {
        gameRepository.save(game);
    }

    @Override
    public ChessMove getMove(FederatedObject federatedObject) {
        return moveRepository.getByFederation(federatedObject).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public void createGame(ChessGame customGame) {
        gameRepository.save(customGame);
    }

    private ChessGame applyMoveUnchecked(ChessGame currentState, ChessMove move) {
        byte[] fields = currentState.getFields();
        // Update castleing rights
        ChessPiece piece = ChessPiece.fromField(fields[move.getSourceField()]);
        if (piece == ChessPiece.KING) {
            if (move.getPlayer() == ChessPlayer.WHITE) {
                currentState.getCastleState().setWhiteShort(false);
                currentState.getCastleState().setWhiteLong(false);
            } else {
                currentState.getCastleState().setBlackShort(false);
                currentState.getCastleState().setBlackLong(false);
            }
        } else if (move.getSourceField() == 0 || move.getTargetField() == 0) {
            currentState.getCastleState().setWhiteLong(false);
        } else if (move.getSourceField() == 7 || move.getTargetField() == 7) {
            currentState.getCastleState().setWhiteShort(false);
        } else if (move.getSourceField() == 56 || move.getTargetField() == 56) {
            currentState.getCastleState().setBlackLong(false);
        } else if (move.getSourceField() == 63 || move.getTargetField() == 63) {
            currentState.getCastleState().setBlackShort(false);
        }
        // Move the actual Piece
        fields[move.getTargetField()] = fields[move.getSourceField()];
        fields[move.getSourceField()] = 0;
        // Process En Passant
        int row = boardService.getFieldRowIndex(move.getTargetField());
        int column = boardService.getFieldColumnIndex(move.getTargetField());
        if (move.isCapture() && move.getTargetField() == currentState.getEnPassentField()) {    
            int captureRow = row < 4 ? 3 : 4;
            int captureField = boardService.getFieldIndex(captureRow, column);
            fields[captureField] = 0;
            currentState.setEnPassentField(-1);
        } else if (ChessPiece.fromField(fields[move.getTargetField()]) == ChessPiece.PAWN && (row == 3 || row == 4) && (boardService.getFieldRowIndex(move.getSourceField()) == 1 || boardService.getFieldRowIndex(move.getSourceField()) == 6)) {
            int enPassantRow = row == 3 ? 2 : 5;
            int enPassantField = boardService.getFieldIndex(enPassantRow, column);
            currentState.setEnPassentField(enPassantField);
        } else {
            currentState.setEnPassentField(-1);
        }
        // Castleing
        if ((move.getSourceField() == 4 || move.getSourceField() == 60) && ChessPiece.fromField(fields[move.getTargetField()]) == ChessPiece.KING) {
            if (move.getTargetField() == 1) {
                fields[2] = fields[0];
                fields[0] = 0;
            } else if (move.getTargetField() == 6) {
                fields[5] = fields[7];
                fields[7] = 0;
            } else if (move.getTargetField() == 62) {
                fields[61] = fields[63];
                fields[63] = 0;
            } else if (move.getTargetField() == 57) {
                fields[58] = fields[56];
                fields[56] = 0;
            }
        }
        // Promotion
        if (piece == ChessPiece.PAWN && move.getPromote() != null) {
            fields[move.getTargetField()] = move.getPromote().toField(move.getPlayer());
        }
        // Set turn for next player
        currentState.setCurrentTurn(move.getPlayer().getOpponent());
        return currentState;
    }

}
