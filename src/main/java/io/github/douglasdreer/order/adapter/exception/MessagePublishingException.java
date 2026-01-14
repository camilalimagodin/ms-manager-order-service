package io.github.douglasdreer.order.adapter.exception;

/** Exceção para erros ao publicar mensagens no broker. */
public class MessagePublishingException extends RuntimeException {

    public MessagePublishingException(String message) {
        super(message);
    }

    public MessagePublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}
