package Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.diamong.chatapp0702.MessageActivity;
import com.diamong.chatapp0702.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import Model.Chat;
import Model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean ischat;
    String theLastMessage;


    public UserAdapter(Context mContext, List<User> mUsers, boolean ischat) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
    }

    /*public UserAdapter(Context mContext, List<User> mUsers) {
        this.mUsers = mUsers;
        this.mContext = mContext;

    }*/

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final User user = mUsers.get(i);
        viewHolder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default")) {
            viewHolder.profile_image.setImageResource(R.drawable.ic_person_black_24dp);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(viewHolder.profile_image);
        }

        if (ischat){
            LastMessage(user.getId(),viewHolder.lastMessage);
        } else {
            viewHolder.lastMessage.setVisibility(View.GONE);
        }

        if (ischat) {
            if (user.getStatus().equals("online")) {
                viewHolder.imageOn.setVisibility(View.VISIBLE);
                viewHolder.imageOff.setVisibility(View.GONE);
            } else {
                viewHolder.imageOn.setVisibility(View.GONE);
                viewHolder.imageOff.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.imageOn.setVisibility(View.GONE);
            viewHolder.imageOff.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;

        private ImageView imageOn;
        private ImageView imageOff;
        private TextView lastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_img);
            imageOn = itemView.findViewById(R.id.img_on);
            imageOff = itemView.findViewById(R.id.img_off);
            lastMessage = itemView.findViewById(R.id.text_last_message);
        }
    }

    private void LastMessage(final String userId, final TextView lastMessage) {

        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {

                        theLastMessage = chat.getMessage();

                    }
                }
                switch (theLastMessage) {
                    case "default":
                        lastMessage.setText("No message");
                        break;
                    default:
                        lastMessage.setText(theLastMessage);
                        break;
                }

                theLastMessage="default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
