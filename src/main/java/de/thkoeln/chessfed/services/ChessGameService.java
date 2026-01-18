package de.thkoeln.chessfed.services;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.thkoeln.chessfed.exception.ResourceNotFoundException;
import de.thkoeln.chessfed.exception.InvalidMoveException;
import de.thkoeln.chessfed.model.Actor;
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
    private static char[] rowLookupTable = new char[]{'1', '2', '3', '4', '5', '6', '7', '8'};
    private static char[] columnLookupTable = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

    @Autowired
    public ChessGameService(IChessGameRepository gameRepository, IChessMoveRepository moveRepository, IFederationService federationService, IRuleEngineService ruleEngine) {
        this.gameRepository = gameRepository;
        this.moveRepository = moveRepository;
        this.federationService = federationService;
        this.ruleEngine = ruleEngine;
    }

    @Override
    public byte getFieldFlag(ChessPiece piece, ChessPlayer player) {
        if (player == ChessPlayer.NONE) return 0;
        int flag = player == ChessPlayer.WHITE ? (piece.ordinal() + 1) << 1 : (piece.ordinal() << 1) + 1;
        return (byte) (flag & 0xFF);
    }

    @Override
    public ChessPiece getPiece(byte fieldFlag) {
        if (fieldFlag == 0) return null;
        int flag = fieldFlag & 0xFF;
        boolean isWhite = (fieldFlag & 1) == 0;
        int pieceId = isWhite ? (flag >>> 1) - 1 : (flag - 1) >>> 1;
        if (pieceId < 0 || pieceId >= ChessPiece.values().length) throw new IllegalArgumentException("ALEC");
        return ChessPiece.values()[pieceId];
    }

    @Override
    public ChessPlayer getPlayer(byte fieldFlag) {
        if (fieldFlag == 0) return ChessPlayer.NONE;
        return (fieldFlag & 1) == 0 ? ChessPlayer.WHITE : ChessPlayer.BLACK;
    }

    @Override
    public int getFieldId(String fieldDescriptor) {
        if (fieldDescriptor == null || fieldDescriptor.chars().count() != 2) throw new IllegalArgumentException("ALEC");
        char[] parts = fieldDescriptor.toCharArray();
        int column = getArrayPosition(columnLookupTable, parts[0]);
        int row = getArrayPosition(rowLookupTable, parts[1]);
        if (column < 0 || row < 0) throw new IllegalArgumentException("ALEC");
        return getFieldId(row, column);
    }

    @Override
    public String getFieldDescriptor(int fieldId) {
        if (fieldId < 0 || fieldId > 63) throw new IllegalArgumentException("ALEC");
        int column = getFieldColumnIndex(fieldId);
        int row = getFieldRowIndex(fieldId);
        String desc = "" + columnLookupTable[column] + rowLookupTable[row];
        return desc;
    }

    private int getArrayPosition(char[] array, char c) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == c) return i;
        }
        return -1;
    }

    @Override
    public int getFieldRowIndex(int fieldId) {
        return fieldId / 8;
    }

    @Override
    public int getFieldColumnIndex(int fieldId) {
        return fieldId % 8;
    }

    @Override
    public int getFieldId(int rowIndex, int columnIndex) {
        return rowIndex * 8 + columnIndex;
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
    public void applyMove(ChessMove move) throws InvalidMoveException {
        ChessGame game = ruleEngine.applyMove(move.getGame(), move, this);
        game.setMoveCounter(move.getMoveCount());
        FederatedObject federatedObject = federationService.createFederatedObject(game.getFederation().getId() + "/moves/" + move.getMoveCount(), ObjectType.MOVE);
        move.setFederation(federatedObject);
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
        game.setCastleState((byte) 0);
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

}
