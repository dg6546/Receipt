package com.hkucs.receipt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.IOException;
import java.security.acl.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.lang.Object;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class NewFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private DatabaseHelper db;
    private Spinner spinner;
    private static final String[] paths = {"Day", "Week", "Month","Year"};
    private Calendar calendar;
    private EditText edittext;
    private Button takePhoto;
    private Button importPhoto;
    private ImageView imageView;
    private AutoCompleteTextView autocomplete;
    String[] arr;
    private final int GALLERY_REQUEST=5;
    private Uri image_uri;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new,container,false);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        takePhoto = (Button) view.findViewById(R.id.new_take_photo);
        importPhoto = (Button) view.findViewById(R.id.new_import);

        if (getArguments()!=null&& getArguments().getInt("position",-1)!=-1) {
            ArrayList<Record> RecordList = (ArrayList<Record>) getArguments().getSerializable("Record");
            int position = (int) getArguments().getInt("position");
//            System.out.println(position);
            EditText new_name = (EditText) view.findViewById(R.id.new_name);
            EditText new_date = (EditText) view.findViewById(R.id.new_purchase_date);
            EditText new_price = (EditText) view.findViewById(R.id.new_price);
            EditText new_warranty = (EditText) view.findViewById(R.id.new_warranty_period);
            AutoCompleteTextView new_category = (AutoCompleteTextView) view.findViewById(R.id.new_category);
            Spinner spinner = (Spinner) view.findViewById(R.id.spinner1);
            System.out.println(RecordList.get(position).name);
            new_name.setText(RecordList.get(position).name);
            new_date.setText(RecordList.get(position).date);
            new_price.setText(RecordList.get(position).price);
            new_warranty.setText(RecordList.get(position).warranty);
            new_category.setText(RecordList.get(position).category);

            Bitmap bitmap = BitmapFactory.decodeByteArray(RecordList.get(position).Image, 0, RecordList.get(position).Image.length);
            imageView.setImageBitmap(bitmap);
            importPhoto.setVisibility(View.GONE);
            takePhoto.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);

            String[] w = RecordList.get(position).warranty.split(" ", 2);
            String[] paths = {"Day", "Week", "Month", "Year"};
            int w2 = 0;
            for (int i = 0; i < 4; i++) {
                if (paths[i] == w[1]) {
                    w2 = i;
                    break;
                }
            }
        }
        spinner = (Spinner)view.findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item,paths);
        calendar = Calendar.getInstance();
        edittext= (EditText) view.findViewById(R.id.new_purchase_date);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
            private void updateLabel() {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                edittext.setText(sdf.format(calendar.getTime()));
            }

        };
        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        db = new DatabaseHelper(this.getActivity());
        SQLiteDatabase database = db.getReadableDatabase();
        String query = "Select Distinct Category from Receipt_table";
        try {
            Cursor cursor = database.rawQuery(query,null);
            arr = new String[cursor.getCount()];
            int i = 0;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                arr[i] = cursor.getString(0);
                i++;
                cursor.moveToNext();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        autocomplete = (AutoCompleteTextView)view.findViewById(R.id.new_category);
        ArrayAdapter<String> ad= new ArrayAdapter<String>(this.getActivity(),android.R.layout.select_dialog_item,arr);
        autocomplete.setThreshold(0);
        autocomplete.setAdapter(ad);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            //Take photo here
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_CODE);
                    }else{
                        //permission granted
                        openCamera();
                    }
                }else{
                    openCamera();
                }
            }
        });
        importPhoto.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }

        });
        return view;
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera");
        image_uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //new intent for camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
                case IMAGE_CAPTURE_CODE:
                    imageView.setImageURI(image_uri);
            }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }else{
                    Toast.makeText(getContext(),"permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

}
