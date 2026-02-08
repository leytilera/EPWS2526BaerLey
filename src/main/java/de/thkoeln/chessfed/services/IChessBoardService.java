package de.thkoeln.chessfed.services;

import de.thkoeln.chessfed.model.ChessGame;

public interface IChessBoardService {

    int getFieldIndex(String fieldDescriptor) throws IllegalArgumentException;

    String getFieldDescriptor(int fieldIndex) throws IllegalArgumentException;

    int getFieldRowIndex(int fieldIndex) throws IllegalArgumentException;

    int getFieldColumnIndex(int fieldIndex) throws IllegalArgumentException;

    int getFieldIndex(int rowIndex, int columnIndex) throws IllegalArgumentException;

    String generateFen(ChessGame game);

    ChessGame parseFen(String fen) throws IllegalArgumentException;

}
