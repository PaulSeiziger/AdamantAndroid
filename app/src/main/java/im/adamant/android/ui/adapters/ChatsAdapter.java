package im.adamant.android.ui.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.ui.entities.Chat;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.ArrayList;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder>{
    private List<Chat> chats = new ArrayList<>();
    private SelectItemListener listener;
    private CompositeDisposable compositeDisposable;
    private Avatar avatar;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public Context context;

        public TextView chatView;
        public TextView lastMessageView;
        public RelativeTimeTextView dateView;
        public ImageView avatar;

        int avatarSize;

        public ViewHolder(Context context, View v, Avatar avatar) {
            super(v);

            this.context = context;

            chatView = v.findViewById(R.id.list_item_chat_name);
            lastMessageView = v.findViewById(R.id.list_item_chat_last_message);
            dateView = v.findViewById(R.id.list_item_chat_last_message_date);
            this.avatar = v.findViewById(R.id.list_item_chat_avatar);

            v.setOnClickListener(this);

           avatarSize = (int) context.getResources().getDimension(R.dimen.list_item_avatar_size);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            if((listener != null) && (position >= 0)) {
                listener.itemWasSelected(chats.get(position));
            }
        }

        public void bind(Chat chat) {
            chatView.setText(chat.getTitle());
            if (chat.getLastMessage() != null){
                lastMessageView.setText(chat.getLastMessage().getShortedMessage(context, 50));
                dateView.setReferenceTime(chat.getLastMessage().getTimestamp());
            } else {
                lastMessageView.setText("");
                dateView.setReferenceTime(System.currentTimeMillis());
            }

            if (chat.getAvatar() == null){
                if (chat.getCompanionPublicKey() != null){
                    Disposable avatarSubscription = ChatsAdapter.this.avatar
                            .build(chat.getCompanionPublicKey(), avatarSize)
                            .subscribe(
                                    pair -> {
                                        avatar.setImageBitmap(pair.second);
                                        chat.setAvatar(pair.second);
                                    },
                                    error -> {
                                        LoggerHelper.e("chatHolder", error.getMessage(), error);
                                    }
                            );
                    compositeDisposable.add(avatarSubscription);
                } else {
                    avatar.setImageResource(R.mipmap.ic_launcher_foreground);
                }
            } else {
                avatar.setImageBitmap(chat.getAvatar());
            }


        }
    }

    public interface SelectItemListener {
        void itemWasSelected(Chat chat);
    }

    public ChatsAdapter(
            List<Chat> chats,
            SelectItemListener listener,
            CompositeDisposable compositeDisposable,
            Avatar avatar
    ) {
        this.compositeDisposable = compositeDisposable;
        this.avatar = avatar;

        if (chats != null){
            this.chats = chats;
        }

        if (listener != null){
            this.listener = listener;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat, parent, false);

        return new ViewHolder(parent.getContext(), v, avatar);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        if (chat != null){
            holder.bind(chat);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void updateDataset(List<Chat> chats){
        if (chats != null){
            this.chats = chats;
        } else {
            this.chats.clear();
        }

        notifyDataSetChanged();
    }


    public void setListener(SelectItemListener listener) {
        this.listener = listener;
    }

    @Override
    protected void finalize() throws Throwable {
        if (compositeDisposable != null){
            compositeDisposable.dispose();
            compositeDisposable.clear();
        }
        super.finalize();
    }
}
