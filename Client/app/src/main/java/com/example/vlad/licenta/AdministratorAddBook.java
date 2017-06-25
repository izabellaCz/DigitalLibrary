package com.example.vlad.licenta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import com.example.vlad.licenta.model.Author;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class AdministratorAddBook extends AppCompatActivity {

    private AutoCompleteTextView tv_title, tv_publisher, tv_total, tv_cover, tv_description;
    private Spinner spinner_author;
    private Button button_addBook, button_addAuthor, button_addCover;

    private static String addAuthorDialogResult = "";
    private static int spinnerPosition = -1;
    private static Activity adminAddBook;

    final private List<Author> authors = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator_add_book);

        adminAddBook = this;

        tv_title = (AutoCompleteTextView) findViewById(R.id.tv_AddTitle);
        tv_publisher = (AutoCompleteTextView) findViewById(R.id.tv_AddPublisher);
        tv_total = (AutoCompleteTextView) findViewById(R.id.tv_AddTotalBooks);
        tv_cover = (AutoCompleteTextView) findViewById(R.id.tv_BookCover);
        tv_description = (AutoCompleteTextView) findViewById(R.id.tv_AddDescription);

        spinner_author = (Spinner) findViewById(R.id.spinner_AddAuthor);
        spinner_author.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button_addBook = (Button) findViewById(R.id.button_admin_add_book);

        button_addAuthor = (Button) findViewById(R.id.button_addAuthor);
        button_addAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 CreateAlertDialog(adminAddBook);

            }
        });

        button_addCover = (Button) findViewById(R.id.button_addCover);
        button_addCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
            }
        });

        refreshAuthors();

    }

    private void refreshAuthors() {
        String url = ServerProperties.HOST;
        url += "/authors/list";
        ServerRequestGET<List<Author>> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructCollectionType(List.class, Author.class),
                new AsyncResponse<List<Author>>() {
                    @Override
                    public void actionCompleted(List<Author> obj) {
                        if (obj == null) obj = new ArrayList<>();
                        authors.clear();
                        authors.addAll(obj);
                        List<String> arraySpinner = new ArrayList<>();
                        arraySpinner.add("Choose Author");
                        for (Author author : obj) {
                            arraySpinner.add(author.getName());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arraySpinner);
                        spinner_author.setAdapter(adapter);
                    }
                });

        theServerRequest.execute();
    }

    private boolean CreateAlertDialog(final Activity activity)
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        final LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_add_author, null);
        dialogBuilder.setView(dialogView);

        final AutoCompleteTextView tvAddAuthor = (AutoCompleteTextView) dialogView.findViewById(R.id.tv_add_author_dialog);

        dialogBuilder.setTitle("Add new author");


        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int whichButton) {
                addAuthorDialogResult = tvAddAuthor.getText().toString();
                if (addAuthorDialogResult.compareTo("") != 0) {

                    String url = ServerProperties.HOST;
                    url += "/authors/add?name=";
                    url += addAuthorDialogResult;

                    ServerRequestGET<String> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructType(String.class),
                            new AsyncResponse<String>() {
                                @Override
                                public void actionCompleted(String res) {

                                    if(res != null && res.equals("1")) {
                                        ArrayAdapter<String> adapt = (ArrayAdapter<String>) spinner_author.getAdapter();
                                        adapt.add(addAuthorDialogResult);
                                        addAuthorDialogResult = "";

                                        refreshAuthors();
                                    }
                                    else
                                    {
                                        MiscFunctions.createToast(getApplicationContext(), "Error adding author!");
                                    }
                                }
                            });

                    theServerRequest.execute();


                }
            }
        });


        AlertDialog b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(false);
        b.show();

        return true;
    }

    public void addBook(View view) throws FileNotFoundException {

        if (tv_title.getText().toString().isEmpty()) {
            tv_title.setError("Please provide the book title.");
            return;
        }

        if (tv_publisher.getText().toString().isEmpty()) {
            tv_publisher.setError("Please provide the publisher.");
            return;
        }

        if (spinnerPosition == -1 || spinnerPosition == 0) {
            MiscFunctions.createToast(getApplicationContext(), "Please provide an author for the book.");
            return;
        }

        if (tv_description.getText().toString().isEmpty()) {
            tv_description.setError("Please provide the publisher.");
            return;
        }


        if (tv_total.getText().toString().isEmpty()) {
            tv_total.setError("Please provide the availability number (>=0).");
            return;
        }

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);

        Bitmap bmp;
        if (tv_cover.getText().toString().isEmpty())
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_book_image);
        else
            bmp = BitmapFactory.decodeFile(tv_cover.getText().toString());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

        ByteArrayResource byteArrayResource = new ByteArrayResource(stream.toByteArray()) {
            @Override
            public String getFilename() {
                return tv_cover.getText().toString();
            }
        };

        LinkedMultiValueMap<String, Object> multiPartRequest = new LinkedMultiValueMap<>();
        multiPartRequest.add("title", tv_title.getText().toString());
        multiPartRequest.add("publisher", tv_publisher.getText().toString());
        multiPartRequest.add("author", String.valueOf(authors.get(spinnerPosition - 1).getId()));
        multiPartRequest.add("description", tv_description.getText().toString());
        multiPartRequest.add("total", tv_total.getText().toString());
        multiPartRequest.add("cover", byteArrayResource);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multiPartRequest, header);

        String url = ServerProperties.HOST;
        url += "/library/add";
        ServerRequestPOST<String> serverRequestPOST = new ServerRequestPOST<>(url, TypeFactory.defaultInstance().constructType(String.class), requestEntity,
                new AsyncResponse<String>() {
                    @Override
                    public void actionCompleted(String obj) {
                        if ( obj != null && obj.compareTo("1") == 0 ) {
                            MiscFunctions.createToast(getApplicationContext(), "Book add: Success");
                            tv_title.setText("");
                            tv_description.setText("");
                            spinner_author.setSelection(0);
                            tv_publisher.setText("");
                            tv_total.setText("");
                            tv_cover.setText("");
                        }
                        else
                            MiscFunctions.createToast(getApplicationContext(), "Book add: Fail");
                    }
                });

        serverRequestPOST.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            tv_cover.setText(picturePath);
        }
    }
}
