package ru.practicum.shareit.exceptions;

public class BusinessLogicException extends Exception { // проверяемое искл поскольку насл от Exception, требует объяз обработки try/catch или throws
    public BusinessLogicException(String message) {
        super(message);
    }
}