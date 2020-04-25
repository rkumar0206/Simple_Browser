package com.example.simple_browser;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookmarkADapter extends RecyclerView.Adapter<BookmarkADapter.BookmarkViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private int position;
    private long id;
    private String title;
    private String link;




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


    public BookmarkADapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;

    }


    public class BookmarkViewHolder extends RecyclerView.ViewHolder {

        private TextView title_textview;
        private TextView link_textview;
        private ImageView imageView;

        private BookmarkViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            title_textview = itemView.findViewById(R.id.bookmark_title);
            link_textview = itemView.findViewById(R.id.bookmark_link);
            imageView=itemView.findViewById(R.id.bookmark_imageview);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {

                        int position = getAdapterPosition();

                        mCursor.moveToPosition(position);

                        setTitle(mCursor.getString(mCursor.getColumnIndex(Bookmark_class.Bookmark_entry.Coloumn_Title)));
                        setLink(mCursor.getString(mCursor.getColumnIndex(Bookmark_class.Bookmark_entry.Column_link)));

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
                    menu.add(2, 8, 1, "Open");
                    menu.add(2, 9, 2, "Delete");
                    menu.add(2, 10, 3, "Share");
                    menu.add(2, 11, 4, "Edit");

                }
            });

        }
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.bookmarklayout, parent, false);
        return new BookmarkViewHolder(view, mListener);

    }

    @Override
    public void onBindViewHolder(@NonNull final BookmarkViewHolder holder, int position) {

        if (!mCursor.moveToPosition(position)) {
            return;

        }

        final String title = mCursor.getString((mCursor.getColumnIndex(Bookmark_class.Bookmark_entry.Coloumn_Title)));
        final String link = mCursor.getString(mCursor.getColumnIndex(Bookmark_class.Bookmark_entry.Column_link));
        final long id = mCursor.getLong(mCursor.getColumnIndex(Bookmark_class.Bookmark_entry._ID));

        DbBitmapUtility dbBitmapUtility=new DbBitmapUtility();

        Bitmap bitmap=dbBitmapUtility.getImage(mCursor.getBlob(mCursor.getColumnIndex(Bookmark_class.Bookmark_entry.Column_Image)));


        holder.title_textview.setText(title);
        holder.link_textview.setText(link);
        holder.imageView.setImageBitmap(bitmap);
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
    public void onViewRecycled(@NonNull BookmarkViewHolder holder) {
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
