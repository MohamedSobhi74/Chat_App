package com.mohamedsobhi.chatapp.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.mohamedsobhi.chatapp.R;
import com.mohamedsobhi.chatapp.models.Message;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ItemViewHolder> {

    private final int CHAT_ME = 100;
    private final int CHAT_YOU = 200;


    private List<Message> messageList;

    private Context ctx;

    public ChatAdapter(Context ctx, List<Message> items) {
        this.messageList = items;
        this.ctx = ctx;
    }

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Message obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)



    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder vh;
        if (viewType == CHAT_ME) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_me, parent, false);
            vh = new ItemViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_you, parent, false);
            vh = new ItemViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        final Message m = messageList.get(position);
        holder.textContent.setText(m.getMessage());
        //vItem.text_time.setText(m.getDate());
        holder.lytParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, m, position);
                }
            }
        });

    }


    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.messageList.get(position).getSender().equals(FirebaseAuth.getInstance().getUid()) ? CHAT_ME : CHAT_YOU;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        @BindView(R.id.text_time)
        TextView textTime;
        @BindView(R.id.text_content)
        TextView textContent;
        @BindView(R.id.lyt_parent)
        LinearLayout lytParent;
        public ItemViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}