package com.example.simple_browser;

import android.content.Context;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class History_Adapter extends RecyclerView.Adapter<History_Adapter.HistoryViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private int position;
    private long id;
    private String title;
    private String link;
    private View v;


    private OnItemClickListener mListener;

    public interface OnItemClickListener {

        void onItemClick(int position);

    }

    public void setOnItemCLickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setItemId(long id) {
        this.id = id;
    }

    public long getItemId() {

        return this.id;

    }


    public History_Adapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;

    }


    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        private TextView title_textview;
        private TextView link_textview;
        private TextView date_textview;


        public HistoryViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            title_textview = itemView.findViewById(R.id.history_title);
            link_textview = itemView.findViewById(R.id.history_link);
            date_textview=itemView.findViewById(R.id.history_date);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {

                        int position = getAdapterPosition();

                        mCursor.moveToPosition(position);

                        setTitle(mCursor.getString(mCursor.getColumnIndex(History_class.History_entry.Coloumn_Title)));
                        setLink(mCursor.getString(mCursor.getColumnIndex(History_class.History_entry.Column_link)));

                    }

                    if (position != RecyclerView.NO_POSITION) {

                        if (listener != null) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });


            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                    menu.setHeaderTitle("Select any option");
                    menu.add(3, 12, 1, "Open");
                    menu.add(3, 13, 2, "Delete");
                    menu.add(3, 14, 3, "Share");


                }
            });

        }
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.history_list_layout, parent, false);
        return new HistoryViewHolder(view, mListener);

    }

    @Override
    public void onBindViewHolder(@NonNull final HistoryViewHolder holder, int position) {

        if (!mCursor.moveToPosition(position)) {
            return;

        }

        final String title = mCursor.getString((mCursor.getColumnIndex(History_class.History_entry.Coloumn_Title)));
        final String link = mCursor.getString(mCursor.getColumnIndex(History_class.History_entry.Column_link));
        String date=mCursor.getString(mCursor.getColumnIndex(History_class.History_entry.Column_date));
        final long id = mCursor.getLong(mCursor.getColumnIndex(History_class.History_entry._ID));

        holder.title_textview.setText(title);
        holder.link_textview.setText(link);
        holder.date_textview.setText(date);
        holder.itemView.setTag(id);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                setPosition(holder.getAdapterPosition());
                setItemId(id);
                setLink(link);
                setTitle(title);
                return false;
            }
        });


    }

    @Override
    public void onViewRecycled(@NonNull HistoryViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}
