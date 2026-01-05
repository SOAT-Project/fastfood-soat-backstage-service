package soat.fastfood.backstage.application.domain.exceptions;


import soat.fastfood.backstage.application.domain.validation.handler.Notification;

public class NotificationException extends DomainException {
    public NotificationException(final String aMessage, final Notification aNotification) {
        super(aMessage, aNotification.getErrors());
    }
}
