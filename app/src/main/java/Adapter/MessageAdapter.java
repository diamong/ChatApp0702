package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.diamong.chatapp0702.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import Model.Chat;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageUrl;

    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageUrl) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if (viewType == MSG_TYPE_RIGHT) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, viewGroup, false);


            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, viewGroup, false);

            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder viewHolder, int i) {


        Chat chat = mChat.get(i);
        viewHolder.show_message.setText(chat.getMessage());

        if (imageUrl.equals("default")) {
            viewHolder.profile_image.setImageResource(R.drawable.ic_person_black_24dp);
        } else {
            Glide.with(mContext).load(imageUrl).into(viewHolder.profile_image);
        }

        if (i==mChat.size()-1){
            if (chat.isSeen()){
                viewHolder.txtIsSeen.setText("읽음");
            } else {
                viewHolder.txtIsSeen.setText("안읽음");
            }
        } else{
            viewHolder.txtIsSeen.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public ImageView profile_image;

        public TextView txtIsSeen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_img);
            txtIsSeen = itemView.findViewById(R.id.text_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
