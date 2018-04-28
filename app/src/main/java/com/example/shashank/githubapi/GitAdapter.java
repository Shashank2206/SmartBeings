package com.example.shashank.githubapi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Shashank on 28/04/2018.
 */


public class GitAdapter extends RecyclerView.Adapter<GitAdapter.MyViewHolder> {

    private List<GitUser> gitUsers;
    private int rowLayout;
    private Context context;
    public GitAdapter(List<GitUser> gitUsers , int rowLayout , Context context) {
        super();
        this.gitUsers = gitUsers;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView gitUserName;
        public ImageView gitProfilePicture;

        public MyViewHolder(View view){
            super(view);
            gitUserName = view.findViewById(R.id.git_user);
            gitProfilePicture = view.findViewById(R.id.git_image);
        }
    }

    @Override
    public int getItemCount() {
        return gitUsers.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.git_profiles , parent , false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GitUser gitUser = gitUsers.get(position);
        holder.gitUserName.setText(gitUser.getUserName());
        Picasso.with(context)
                .load(gitUser.getImageUrl())
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .into(holder.gitProfilePicture);
    }

}
