package im.adamant.android.ui.messages_support.factories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.adamant.android.R;
import im.adamant.android.avatars.AvatarGenerator;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.ui.messages_support.entities.AdamantBasicMessage;
import im.adamant.android.ui.messages_support.holders.AbstractMessageViewHolder;
import im.adamant.android.ui.messages_support.holders.AdamantBasicMessageViewHolder;
import im.adamant.android.ui.messages_support.builders.AdamantBasicMessageBuilder;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;
import im.adamant.android.ui.messages_support.processors.AdamantBasicMessageProcessor;
import im.adamant.android.ui.messages_support.processors.MessageProcessor;

public class AdamantBasicMessageFactory implements MessageFactory<AdamantBasicMessage> {
    private AdamantAddressProcessor adamantAddressProcessor;
    private Encryptor encryptor;
    private AdamantApiWrapper api;
    private AvatarGenerator avatarGenerator;

    public AdamantBasicMessageFactory(
            AdamantAddressProcessor adamantAddressProcessor,
            Encryptor encryptor,
            AdamantApiWrapper api,
            AvatarGenerator avatarGenerator
    ) {
        this.adamantAddressProcessor = adamantAddressProcessor;
        this.avatarGenerator = avatarGenerator;
        this.encryptor = encryptor;
        this.api = api;
    }

    @Override
    public MessageBuilder<AdamantBasicMessage> getMessageBuilder() {
        return new AdamantBasicMessageBuilder();
    }

    @Override
    public AbstractMessageViewHolder getViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_adamant_basic_message, parent, false);
        return new AdamantBasicMessageViewHolder(parent.getContext(), v, adamantAddressProcessor, avatarGenerator);
    }

    @Override
    public MessageProcessor<AdamantBasicMessage> getMessageProcessor() {
        return new AdamantBasicMessageProcessor(encryptor, api);
    }
}
