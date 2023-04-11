package com.example.finalproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>{

    private RecyclerView.RecycledViewPool
            viewPool
            = new RecyclerView
            .RecycledViewPool();
    private ArrayList<header> data;
    Context context;

    int position;

    public HeaderAdapter(ArrayList<header> data, Context context){
        this.data = data;
        this.context = context;
    }

    @NonNull
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_layout, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        this.position = holder.getBindingAdapterPosition();
        header datalist = data.get(position);
        holder.textView.setText(datalist.getA_header());

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(
                holder.list.getContext(),
                LinearLayoutManager.VERTICAL,
                false);

        layoutManager
                .setInitialPrefetchItemCount(datalist.getContacts().size());

        MyAdapter ContactAdapter = new MyAdapter(datalist.getContacts(), context);
        holder.list.setLayoutManager(layoutManager);
        holder.list.setAdapter(ContactAdapter);
        holder.list.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public String getNum(int position){

        return data.get(this.position).getContacts().get(position).getPhoneNumber();
    }




    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        RecyclerView list;




        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.header_id);
            list = itemView.findViewById(R.id.child_view);

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBackMethod);
            itemTouchHelper.attachToRecyclerView(list);


        }

        ItemTouchHelper.SimpleCallback callBackMethod = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT
                | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();

                switch(direction){
                    case ItemTouchHelper.RIGHT:
                        String p_num = getNum(position);
                        makeCall(p_num);
                        Objects.requireNonNull(list.getAdapter()).notifyItemChanged(position);
                        break;

                    case ItemTouchHelper.LEFT:
                        p_num = getNum(position);
                        sendMessage(p_num);
                        Objects.requireNonNull(list.getAdapter()).notifyItemChanged(position);
                        break;

                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftLabel("Message")
                        .setSwipeLeftLabelColor(context.getResources().getColor(R.color.white))
                        .addSwipeLeftActionIcon(R.drawable.testmssg_icon)
                        .addSwipeLeftBackgroundColor(context.getResources().getColor(R.color.green))
                        .addSwipeLeftCornerRadius(TypedValue.COMPLEX_UNIT_SP, 25)
                        .addSwipeRightLabel("Call")
                        .setSwipeRightLabelColor(context.getResources().getColor(R.color.white))
                        .addSwipeRightActionIcon(R.drawable.call_icon)
                        .addSwipeRightBackgroundColor(context.getResources().getColor(R.color.green))
                        .addSwipeRightCornerRadius(TypedValue.COMPLEX_UNIT_SP, 25)
                        .create()
                        .decorate();



                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        private void sendMessage(String contactNumber) {

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + contactNumber));
            intent.putExtra("sms_body", "Enter your message");
            context.startActivity(intent);
        }

        private void makeCall(String contactNumber) {

            Intent callIntent = new Intent(Intent.ACTION_CALL);

            callIntent.setData(Uri.parse("tel:" + contactNumber));

            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.CALL_PHONE},  101);

                return;
            }


            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            context.startActivity(callIntent);
        }


    }
}
