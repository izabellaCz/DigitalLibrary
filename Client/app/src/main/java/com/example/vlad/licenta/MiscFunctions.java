package com.example.vlad.licenta;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vlad.licenta.model.Book;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


public class MiscFunctions {
    public static ImageView image;



    public static Bitmap QrGenerate(int currentUserId, Book book) {
        String text2Qr=Integer.toString(currentUserId).concat(",").concat(Integer.toString(book.getId())).concat(",").concat(book.getTitle()).concat(",").concat(book.getAuthor().getName());

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmaps = barcodeEncoder.createBitmap(bitMatrix);

            return bitmaps;

        }
        catch (WriterException e){
            e.printStackTrace();
            return null;
        }
    }

    public static int CreateRentAlertDialog(final Activity activity, final int userId, final int bookId, final String strDate)
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);

        dialogBuilder.setTitle("Rent Book");

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        dialogBuilder.setPositiveButton("Rent book", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String url = ServerProperties.HOST;
                url += "/library/rentBook?userId=";
                url += userId;
                url += "&bookId=";
                url += bookId;
                url += "&loanDate=";
                url += strDate;

                ServerRequestGET<String> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructType(String.class),
                        new AsyncResponse<String>() {
                            @Override
                            public void actionCompleted(String obj) {
                                MiscFunctions.CreateToast(activity.getApplicationContext(), "Book rent: Successful");
                            }
                        });

                theServerRequest.execute();
            }
        });

        return 0;
    }


    public static int CreateAlertDialog(final Activity activ, final Book book, final int isFavourite)
    {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activ);
        final LayoutInflater inflater = activ.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_book_details, null);
        dialogBuilder.setView(dialogView);

        final TextView tvDescription = (TextView) dialogView.findViewById(R.id.book_description_dialog);
        final ImageView imageBook = (ImageView) dialogView.findViewById(R.id.book_image_dialog);

        Bitmap bm;
        if (book.getCover() == null) {
            bm = BitmapFactory.decodeResource(activ.getResources(), R.drawable.default_book_image);
        } else {
            bm = BitmapFactory.decodeByteArray(book.getCover(), 0, book.getCover().length);
        }

        imageBook.setImageBitmap(bm);

        dialogBuilder.setTitle(book.getTitle());
        dialogBuilder.setMessage(book.getAuthor().getName());

        tvDescription.setText(book.getDescription());
        tvDescription.setMovementMethod(new ScrollingMovementMethod());

        dialogBuilder.setPositiveButton("Generate QR Code", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int currUserId = -1;

                if ( activ instanceof Client )
                    currUserId = ((Client) activ).getCurrentUser().getId();
                else
                    currUserId = ((Administrator)activ).getCurrentUser().getId();

                Intent intent = new Intent (activ, qrGeneratePage.class);
                intent.putExtra("bitmap", QrGenerate(currUserId, book));
                activ.startActivity(intent);
            }


        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        if ( isFavourite != -1 ) {
            dialogBuilder.setNeutralButton(isFavourite == 0 ? "Add To Fav" : "Remove from Fav", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String url = ServerProperties.HOST;
                    url += "/library";
                    url += isFavourite == 0 ? "/addFavourite" : "/removeFavourite";

                    if (activ instanceof Client)
                        url += "?userId=" + ((Client) activ).getCurrentUser().getId();
                    else
                        url += "?userId=" + ((Administrator) activ).getCurrentUser().getId();

                    url += "&bookId=" + book.getId();

                    ServerRequestGET<String> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructType(String.class),
                            new AsyncResponse<String>() {
                                @Override
                                public void actionCompleted(String obj) {
                                    if (obj != null && obj.compareTo("1") == 0)
                                        MiscFunctions.CreateToast(activ.getApplicationContext(), "Success");
                                    else
                                        MiscFunctions.CreateToast(activ.getApplicationContext(), "Failed");

                                }
                            });

                    theServerRequest.execute();
                }
            });
        }


        AlertDialog b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(false);
        b.show();



        return 0;
    }



    public static int CreateHistoryAlertDialog(Activity activ, Bitmap image, String title, String author, String description)
    {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activ);
        LayoutInflater inflater = activ.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_book_details, null);
        dialogBuilder.setView(dialogView);

        final TextView tvDescription = (TextView) dialogView.findViewById(R.id.book_description_dialog);
        final ImageView imageBook = (ImageView) dialogView.findViewById(R.id.book_image_dialog);

        tvDescription.setText(description);
        //  imageBook.setImageBitmap(image);
        //String asd =   dialogView.findViewById(R.id.book_author).toString();
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(author);
        /*
        dialogBuilder.setPositiveButton("Generate QR Code", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String text2Qr ="vlad are mere";
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try{
                    BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE,200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmaps = barcodeEncoder.createBitmap(bitMatrix);
                    imageBook.setImageBitmap(bitmaps);

                }
                catch (WriterException e){
                    e.printStackTrace();
                }
            }
        });
        */
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });


        AlertDialog b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(false);
        b.show();

        return 0;
    }

    public static int CreateToast(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        return 0;
    }

}
