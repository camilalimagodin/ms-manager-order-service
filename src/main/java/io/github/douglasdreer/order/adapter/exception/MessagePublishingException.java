package io.github.douglasdreer.order.adapter.exception;

/**
 * Exceção lançada quando há erro ao publicar uma mensagem no broker de mensagens.
 * Esta exceção encapsula erros inesperados que ocorrem durante a publicação de mensagens.
 */
public class MessagePublishingException extends RuntimeException {

    public MessagePublishingException(String message) {
        super(message);
    }

    public MessagePublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}
