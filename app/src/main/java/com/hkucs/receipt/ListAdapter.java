package com.hkucs.receipt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    private List<Record> records;
    private LayoutInflater mInflater;
    private DatabaseHelper db;
    public ListAdapter(Context context, List<Record> records,DatabaseHelper db) {
        this.mInflater = LayoutInflater.from(context);
        this.records = records;
        this.db = db;
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int position) {
        return records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.listitem, null);
        TextView name = convertView.findViewById(R.id.new_name);
        TextView price = convertView.findViewById(R.id.price);
        TextView warranty = convertView.findViewById(R.id.warranty);
        TextView category = convertView.findViewById(R.id.category);
        TextView delete = convertView.findViewById(R.id.delete);


        Record record = records.get(position);
        name.setText(record.name);
        price.setText("price:"+record.price);
        warranty.setText("warranty:"+record.warranty);
        category.setText("category:"+record.category);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.delete(record.name);
                records.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

   public void setList(List<Record> records) {
       this.records = records;
       notifyDataSetChanged();
    }
}
