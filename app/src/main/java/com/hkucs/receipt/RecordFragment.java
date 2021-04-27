package com.hkucs.receipt;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


class Record implements Serializable {
    String ID, name, date, price, warranty,spinner, category;
    byte[] Image;
}
class Record_s{
    String ID, name, date;
}
public class RecordFragment extends Fragment {
    DatabaseHelper db;
    private ArrayList<Record> RecordList;
    private ListAdapter adapter;

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_record,container,false);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        TextView searchButton = (TextView) view.findViewById(R.id.search_button);
        EditText searchContent = (EditText) view.findViewById(R.id.search_content);

        final Fragment n = new NewFragment();
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right);

                transaction.addToBackStack(null);
                transaction.replace(R.id.fragment_container, n).commit();
                ((MainActivity)getActivity()).add_vis();
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String searchStr = searchContent.getText().toString().trim().toLowerCase();
                ArrayList<Record> filter = new ArrayList<>();
                for (int i = 0; i < RecordList.size(); i++) {
                    Record record = RecordList.get(i);
                    if (record.name.toLowerCase().contains(searchStr)) {
                        filter.add(record);
                    }
                }
                adapter.setList(filter);
            }
        });


        RecordList = new ArrayList<>();
        ArrayList<String> RecordList_S = new ArrayList<>();
        db = new DatabaseHelper(this.getActivity());
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor c = database.rawQuery("SELECT * FROM Receipt_table", null);
        c.moveToFirst();
        while(!c.isAfterLast() ){
            Record r = new Record();
            r.ID = c.getString(0);
            r.name=c.getString(1);
            String rs =c.getString(1);
            r.date=c.getString(2);
            r.price=c.getString(3);
            r.Image=c.getBlob(4);
            r.warranty=c.getString(5);
            r.category=c.getString(6);
            RecordList.add(r);
            RecordList_S.add(rs);
            c.moveToNext();
        }
//        ArrayAdapter adapter = new ArrayAdapter<String>(this.getActivity(),R.layout.listview,RecordList_S);
        ListView listView = (ListView)view.findViewById(R.id.record_list);
         adapter = new ListAdapter(requireActivity(), RecordList,db);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //MySingletonClass singleton = null;
                //singleton.setValue(Long.toString(id));
                NewFragment nf =new NewFragment();
                Bundle args = new Bundle();
                args.putSerializable("Record",RecordList);
                args.putInt("position",position);
                //args.putString("current_id", Long.toString(id));
                nf.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(0,0,R.anim.enter_from_left,R.anim.exit_to_right).replace(R.id.fragment_container, nf).commit();
        }});

        return view;
    }

}
