package com.shruti.anas;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class StudentsFrag extends Fragment implements View.OnClickListener {


    RecyclerView Students_vRV;
    ArrayList<Model_Students> arrStudents;
    Adapter_Students adapter_students;


    File filePath;
    String fileName = "720";
    String sheetName = "Science";

    public StudentsFrag(String fileName, String sheetName) {
        this.fileName = fileName;
        this.sheetName = sheetName;
    }

    FloatingActionButton Students_fbtn;
    Button Students_btnExcel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_students, container, false);


        Students_vRV = view.findViewById(R.id.Students_vRV);
        Students_fbtn = view.findViewById(R.id.Students_fbtn);
        Students_btnExcel = view.findViewById(R.id.Students_btnExcel);
        Students_vRV.setLayoutManager(new LinearLayoutManager(getActivity()));




        loadRecycler();








        adapter_students = new Adapter_Students(arrStudents);
        Students_vRV.setAdapter(adapter_students);

        //permission
        ActivityCompat.requestPermissions(
                getActivity()
                , new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }
                , PackageManager.PERMISSION_GRANTED);

        Students_btnExcel.setOnClickListener(this);

        Students_fbtn.setOnClickListener(this);

        return view;
    }




    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.Students_btnExcel:
                excel();
                break;
            case R.id.Students_fbtn:
                StudentAdder();
                break;
        }


    }

    private void excel() {
        filePath = new File(Environment.getExternalStorageDirectory() + "/" + fileName + ".xls");
        if (!filePath.exists()) {


            HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
            HSSFSheet hssfSheet = hssfWorkbook.createSheet(sheetName);

            int firstRow = -1;

            if (adapter_students.arrPresent.size() < 1) {
                Toast.makeText(getActivity(), "Select 1 atleast", Toast.LENGTH_SHORT).show();
            } else {
                for (Model_Students model : adapter_students.arrPresent) {
                    HSSFRow hssfRow = hssfSheet.createRow(++firstRow);
                    hssfRow.createCell(0).setCellValue(model.getName());
                    hssfRow.createCell(1).setCellValue(model.getUid());
                    hssfRow.createCell(2).setCellValue("Present");
                }
                for (Model_Students model : adapter_students.arrAbsent) {
                    HSSFRow hssfRow = hssfSheet.createRow(++firstRow);
                    hssfRow.createCell(0).setCellValue(model.getName());
                    hssfRow.createCell(1).setCellValue(model.getUid());
                    hssfRow.createCell(2).setCellValue("Absent");
                }
                try {
                    filePath.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                    hssfWorkbook.write(fileOutputStream);
                    Toast.makeText(getActivity(), fileName + " File Created", Toast.LENGTH_SHORT).show();

                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }



        else {
            try {
                FileInputStream fileInputStream = new FileInputStream(filePath);
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fileInputStream);
                HSSFSheet hssfSheet = null;
                int sheetCount = hssfWorkbook.getNumberOfSheets();

                for (int i=0;i<=sheetCount;i++){
                    if (hssfWorkbook.getSheetName(i).equals(sheetName)){
                        hssfSheet = hssfWorkbook.getSheet(sheetName);
                        break;
                    }
                    else {
                        hssfSheet = hssfWorkbook.createSheet(sheetName);
                        break;
                    }
                }

                int lastRow = hssfSheet.getLastRowNum();

                if (adapter_students.arrPresent.size()<1){
                    Toast.makeText(getActivity(),"Select atleast 1",Toast.LENGTH_SHORT);
                }
                else {
                    for (Model_Students model : adapter_students.arrPresent){
                        HSSFRow hssfRow = hssfSheet.createRow(++lastRow);
                        hssfRow.createCell(0).setCellValue(model.getName());
                        hssfRow.createCell(1).setCellValue(model.getUid());
                        hssfRow.createCell(2).setCellValue("Present");
                    }
                    for (Model_Students model : adapter_students.arrAbsent){
                        HSSFRow hssfRow = hssfSheet.createRow(++lastRow);
                        hssfRow.createCell(0).setCellValue(model.getName());
                        hssfRow.createCell(1).setCellValue(model.getUid());
                        hssfRow.createCell(2).setCellValue("Absent");
                    }

                    fileInputStream.close();

                    FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                    hssfWorkbook.write(fileOutputStream);
                    Toast.makeText(getActivity(), fileName+" File Updated", Toast.LENGTH_SHORT).show();
                    fileOutputStream.close();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void StudentAdder() {

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.student_dialog);

        EditText eName = dialog.findViewById(R.id.eName);
        EditText eUid = dialog.findViewById(R.id.eUid);
        Button btnAdd = dialog.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(v -> {

            String Name = eName.getText().toString();
            String Uid = eUid.getText().toString();

            if (Name.equals("") || Uid.equals("")){
                Toast.makeText(getActivity(), "Blank Field!", Toast.LENGTH_SHORT).show();
            }
            else {
                arrStudents.add(new Model_Students(Name,Uid));
                adapter_students.notifyItemInserted(arrStudents.size()-1);
                Students_vRV.scrollToPosition(arrStudents.size()-1);

                save();
                dialog.dismiss();
            }
        });

        dialog.show();
    }



    private void loadRecycler() {

        SharedPreferences sp = this.getActivity().getSharedPreferences("StudentsFragSP",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("StudentsSP",null);
        Type type = new TypeToken<ArrayList<Model_Students>>(){}.getType();
        arrStudents = gson.fromJson(json,type);

        if (arrStudents == null){
            arrStudents = new ArrayList<>();
            arrStudents.add(new Model_Students("Anas", "21BCS8965"));
            arrStudents.add(new Model_Students("Shruti", "21BCS9026"));
            arrStudents.add(new Model_Students("Nikhil", "21BCS8964"));
        }
    }

    private void save(){
        SharedPreferences sp = this.getActivity().getSharedPreferences("StudentsFragSP",MODE_PRIVATE);
        SharedPreferences.Editor sp_editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrStudents);
        sp_editor.putString("StudentsSP",json);
        sp_editor.apply();
        Toast.makeText(getActivity(), "File Saved", Toast.LENGTH_SHORT).show();
    }
}