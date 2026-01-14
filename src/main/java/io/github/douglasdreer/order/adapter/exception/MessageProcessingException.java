package io.github.douglasdreer.order.adapter.exception;

/** Exceção para erros ao processar mensagens do broker. */
public class MessageProcessingException extends RuntimeException {

    public MessageProcessingException(String message) {
        super(message);
    }

    public MessageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
