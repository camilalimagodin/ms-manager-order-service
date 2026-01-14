package io.github.douglasdreer.order.adapter.exception;

/**
 * Exceção lançada quando há erro ao processar uma mensagem do broker de mensagens.
 * Esta exceção encapsula erros inesperados que ocorrem durante o consumo de mensagens.
 */
public class MessageProcessingException extends RuntimeException {

    public MessageProcessingException(String message) {
        super(message);
    }

    public MessageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
