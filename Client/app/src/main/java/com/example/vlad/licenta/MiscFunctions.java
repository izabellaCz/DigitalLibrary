package com.example.vlad.licenta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


public class MiscFunctions {
    public static ImageView image;



    public static Bitmap QrGenerate(String title,String author) {
        String text2Qr=title.concat(" ").concat(author);

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



    public static int CreateAlertDialog(final Activity activ, final Bitmap image, final String title, final String author, String description)
    {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activ);
        final LayoutInflater inflater = activ.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final TextView tvDescription = (TextView) dialogView.findViewById(R.id.book_description_dialog);
        //final ImageView imageBook = (ImageView) dialogView.findViewById(R.id.book_image_dialog);

        tvDescription.setText(description);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(author);

        dialogBuilder.setPositiveButton("Generate QR Code", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent (activ, qrGeneratePage.class);
                intent.putExtra("bitmap", QrGenerate(title, author));
                activ.startActivity(intent);
            }


        });
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



    public static int CreateHistoryAlertDialog(Activity activ, Bitmap image, String title, String author, String description)
    {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activ);
        LayoutInflater inflater = activ.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
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
