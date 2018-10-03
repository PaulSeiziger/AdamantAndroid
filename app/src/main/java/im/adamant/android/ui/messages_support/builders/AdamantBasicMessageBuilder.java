package im.adamant.android.ui.messages_support.builders;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.ui.messages_support.entities.AdamantBasicMessage;
import im.adamant.android.ui.messages_support.SupportedMessageType;

public class AdamantBasicMessageBuilder implements MessageBuilder<AdamantBasicMessage> {
    @Override
    public AdamantBasicMessage build(Transaction transaction, String decryptedMessage, boolean isISayed, long date, String companionId) {
        AdamantBasicMessage message = new AdamantBasicMessage();
        message.setSupportedType(SupportedMessageType.ADAMANT_BASIC);
        message.setText(decryptedMessage);
        message.setiSay(isISayed);
        message.setDate(date);
        message.setCompanionId(companionId);

        if (transaction != null){
            message.setOwnerPublicKey(transaction.getSenderPublicKey());

            message.setProcessed(true);
            message.setTransactionId(transaction.getId());
        }

        return message;
    }
}
