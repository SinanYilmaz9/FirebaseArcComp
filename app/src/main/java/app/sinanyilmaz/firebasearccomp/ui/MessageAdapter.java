package app.sinanyilmaz.firebasearccomp.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import java.util.List;

import app.sinanyilmaz.firebasearccomp.R;
import app.sinanyilmaz.firebasearccomp.databinding.MessageItemBinding;
import app.sinanyilmaz.firebasearccomp.model.Message;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final static String TAG = "Adapter";
    private List<? extends Message> mMessageList ;


    void setMessageList(final List<? extends Message> messageList){
        mMessageList = messageList;
        notifyDataSetChanged();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MessageItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.message_item,
                        parent,
                        false);

        return new MessageViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = mMessageList.get(position);
        holder.binding.setMessage(mMessageList.get(position));
        //holder.binding.name.setText(message.getUserName());
        if(message.getPhotoUrl() != null && !message.getPhotoUrl().isEmpty())
            Glide.with(holder.binding.photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .thumbnail(0.01f)
                    .into(holder.binding.photoImageView);
        holder.binding.executePendingBindings();
    }


    @Override
    public int getItemCount() {
        return mMessageList == null ? 0 : mMessageList.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        private final MessageItemBinding binding;

        public MessageViewHolder(MessageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }
}
