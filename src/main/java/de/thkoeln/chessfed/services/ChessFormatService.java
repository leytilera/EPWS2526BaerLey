package de.thkoeln.chessfed.services;

import org.springframework.stereotype.Service;

import de.thkoeln.chessfed.model.CastleState;
import de.thkoeln.chessfed.model.ChessGame;
import de.thkoeln.chessfed.model.ChessPiece;
import de.thkoeln.chessfed.model.ChessPlayer;

@Service
public class ChessFormatService implements IChessBoardService {

    private static char[] rowLookupTable = new char[]{'1', '2', '3', '4', '5', '6', '7', '8'};
    private static char[] columnLookupTable = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

    @Override
    public int getFieldIndex(String fieldDescriptor) {
        if (fieldDescriptor == null || fieldDescriptor.chars().count() != 2) throw new IllegalArgumentException("ALEC");
        char[] parts = fieldDescriptor.toCharArray();
        int column = getArrayPosition(columnLookupTable, parts[0]);
        int row = getArrayPosition(rowLookupTable, parts[1]);
        if (column < 0 || row < 0) throw new IllegalArgumentException("ALEC");
        return getFieldIndex(row, column);
    }

    @Override
    public String getFieldDescriptor(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex > 63) throw new IllegalArgumentException("ALEC");
        int column = getFieldColumnIndex(fieldIndex);
        int row = getFieldRowIndex(fieldIndex);
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
    public int getFieldRowIndex(int fieldIndex) {
        return fieldIndex / 8;
    }

    @Override
    public int getFieldColumnIndex(int fieldIndex) {
        return fieldIndex % 8;
    }

    @Override
    public int getFieldIndex(int rowIndex, int columnIndex) {
        return rowIndex * 8 + columnIndex;
    }

    @Override
    public String generateFen(ChessGame game) {
        String[] parts = new String[6];
        parts[0] = generateBoard(game.getFields());
        parts[1] = game.getCurrentTurn() == ChessPlayer.WHITE ? "w" : "b";
        StringBuilder castleState = new StringBuilder();
        if (game.getCastleState().isWhiteShort()) {
            castleState.append('K');
        }
        if (game.getCastleState().isWhiteLong()) {
            castleState.append('Q');
        }
        if (game.getCastleState().isBlackShort()) {
            castleState.append('k');
        }
        if (game.getCastleState().isBlackLong()) {
            castleState.append('q');
        }
        parts[2] = castleState.isEmpty() ? "-" : castleState.toString();
        parts[3] = game.getEnPassentField() == -1 ? "-" : getFieldDescriptor(game.getEnPassentField());
        parts[4] = "0";
        parts[5] = "1";
        
        return String.join(" ", parts);
    }

    @Override
    public ChessGame parseFen(String fen) {
        String[] parts = fen.split(" ");
        if (parts.length != 6) throw new IllegalArgumentException("ALEC");
        byte[] board = parseBoard(parts[0]);
        ChessPlayer currentTurn = "w".equals(parts[1]) ? ChessPlayer.WHITE : ChessPlayer.BLACK;
        CastleState castleState = new CastleState();
        if (parts[2].contains("K")) {
            castleState.setWhiteShort(true);
        }
        if (parts[2].contains("Q")) {
            castleState.setWhiteLong(true);
        }
        if (parts[2].contains("k")) {
            castleState.setBlackShort(true);
        }
        if (parts[2].contains("q")) {
            castleState.setBlackLong(true);
        }
        int enPassantField = parts[3].equals("-") ? -1 : getFieldIndex(parts[3]);
        ChessGame game = new ChessGame();
        game.setFields(board);
        game.setCurrentTurn(currentTurn);
        game.setCastleState(castleState);
        game.setEnPassentField(enPassantField);
        return game;
    }

    private String generateBoard(byte[] fields) {
        if (fields.length != 64) throw new IllegalArgumentException("ALEC");
        byte[][] board = new byte[8][8];

        int k = 0;
        for(int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = fields[k];
                k++;
            }
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int freeCounter = 0;
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 0) {
                    freeCounter++;
                } else {
                    if (freeCounter != 0) {
                        builder.append(freeCounter);
                        freeCounter = 0;
                    }
                    ChessPlayer player = ChessPlayer.fromField(board[i][j]);
                    ChessPiece piece = ChessPiece.fromField(board[i][j]);
                    String abbrev = piece.getAbbrev(player);
                    builder.append(abbrev);
                }
                if (j == 7 && freeCounter != 0) {
                    builder.append(freeCounter);
                    freeCounter = 0;
                }
            }
            if (i != 7) {
                builder.append('/');
            }
        }

        return builder.toString();
    }
    
    private byte[] parseBoard(String fenBoard) {
        String[] rows = fenBoard.split("/");
        if (rows.length != 8) throw new IllegalArgumentException("ALEC");
        byte[][] board = new byte[8][8];
        for (int i = 0; i < 8; i++) {
            String row = rows[i];
            char[] chars = row.toCharArray();
            int j = 0;
            for (char c : chars) {
                if (Character.isDigit(c)) {
                    int empty = Character.getNumericValue(c);
                    for (int k = 0; k < empty; k++) {
                        if (j >= 8) throw new IllegalArgumentException("ALEC");
                        board[i][j] = 0;
                        j++;
                    }
                } else {
                    if (j >= 8) throw new IllegalArgumentException("ALEC");
                    ChessPiece piece = ChessPiece.parse(String.valueOf(c));
                    ChessPlayer player = ChessPlayer.parse(String.valueOf(c));
                    if (piece == null) throw new IllegalArgumentException("ALEC");
                    board[i][j] = piece.toField(player);
                    j++;
                }
            }
        }

        byte[] res = new byte[64];

        int k = 0;
        for(int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                res[k] = board[i][j];
                k++;
            }
        }

        return res;
    }

}