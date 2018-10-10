package com.example.hp.googlebooks;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class BooksListCustomAdapter extends BaseAdapter
{
    List<Book> items;
    Context myContext;

    @Override
    public int getCount() {
        return items.size();
    }

    public BooksListCustomAdapter(List<Book> items, Context context) {
        this.items = items;
        this.myContext = context;
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Book>data) {
        items.addAll(data);
        this.notifyDataSetChanged();
    }

    @Override
    public Book getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View x = convertView;
        if (x == null) {
            x = View.inflate(myContext, R.layout.book_item, null);
        }
        TextView title = (TextView) x.findViewById(R.id.title);
        title.setText(items.get(position).getTitle());

        TextView authors = (TextView) x.findViewById(R.id.authors);
        authors.setText(items.get(position).getAuthors());

        TextView publishingDate = (TextView) x.findViewById(R.id.publishingDate);
        publishingDate.setText(items.get(position).getPublishingDate());


        return x;
    }


}
