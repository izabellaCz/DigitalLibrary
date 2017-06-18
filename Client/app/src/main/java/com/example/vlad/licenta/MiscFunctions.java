package com.example.vlad.licenta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vlad.licenta.model.Book;
import com.example.vlad.licenta.model.QRTransaction;
import com.example.vlad.licenta.model.User;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MiscFunctions {
    public final static long MILLISECONDS_IN_DAY = 24 * 60 * 60 * 1000;
    public final static int LOAN_DAYS = 30;

    public static Bitmap qrGenerate(QRTransaction transactionType, int currentUserId, Book book) {
        String text2Qr = transactionType.toString() + "," +
                Integer.toString(currentUserId) + "," +
                Integer.toString(book.getId());

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

    public static void rentBook(final Activity activity, final String userId, final String bookId) {
        /**
         * Request ca sa nu punem si cover-ul in QR
         */
        String url = ServerProperties.HOST + "/library/getById?userId=" + userId
                + "&bookId=" + bookId;

        ServerRequestGET<Book> getBookById = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructType(Book.class),
                new AsyncResponse<Book>() {
                    @Override
                    public void actionCompleted(Book obj) {
                        if (obj != null) {
                            MiscFunctions.createRentAlertDialog(activity, userId, obj);
                        } else {
                            // TODO : treat case - book not found
                        }
                    }
                });

        getBookById.execute();
    }

    private static int createRentAlertDialog(final Activity activity, final String userId, final Book book) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        final LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_book_details, null);
        dialogBuilder.setView(dialogView);

        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        TextView tvAuthor = (TextView) dialogView.findViewById(R.id.tv_author);
        final TextView tvDescription = (TextView) dialogView.findViewById(R.id.book_description_dialog);
        final ImageView imageBook = (ImageView) dialogView.findViewById(R.id.book_image_dialog);

        final ImageButton addRemoveFavs = (ImageButton) dialogView.findViewById(R.id.ib_AddRemoveFav);
        addRemoveFavs.setVisibility(View.INVISIBLE);

        dialogBuilder.setTitle("Rent Book");

        Bitmap bm;
        if (book.getCover() == null) {
            bm = BitmapFactory.decodeResource(activity.getResources(), R.drawable.default_book_image);
        } else {
            bm = BitmapFactory.decodeByteArray(book.getCover(), 0, book.getCover().length);
        }

        imageBook.setImageBitmap(bm);

        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor().getName());

        tvDescription.setText(book.getDescription());
        tvDescription.setMovementMethod(new ScrollingMovementMethod());

        dialogBuilder.setPositiveButton("Approve Rent", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = ServerProperties.HOST;
                url += "/library/rentBook?userId=" + userId
                        + "&bookId=" + book.getId()
                        + "&approverId=" + ((Administrator) activity).getCurrentUser().getId()
                        + "&loanDate=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                ServerRequestGET<String> rentBookRequest = new ServerRequestGET<String>(url, TypeFactory.defaultInstance().constructType(String.class),
                        new AsyncResponse<String>() {
                            @Override
                            public void actionCompleted(String obj) {
                                if (obj != null && !obj.equals("-1")) {
                                    MiscFunctions.createToast(activity.getApplication(), "Book rent successful");
                                } else {
                                    MiscFunctions.createToast(activity.getApplication(), "Book rent unsuccessful");
                                }
                            }
                        });
                rentBookRequest.execute();

            }


        });

        dialogBuilder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(false);
        b.show();

        return 0;
    }

    public static int returnBook(final Activity activity, final String userId, final String bookId) {

        /**
         * Get ivCover from server
         */

        String url = ServerProperties.HOST + "/library/getLoanInfo" +
                "?userId=" + userId +
                "&bookId=" + bookId;
        ServerRequestGET<Book> getBook = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructType(Book.class),
                new AsyncResponse<Book>() {
                    @Override
                    public void actionCompleted(Book obj) {
                        if (obj != null && !obj.equals("-1")) {
                            MiscFunctions.createReturnAlertDialog(activity, obj);
                        } else {
                            MiscFunctions.createToast(activity.getApplicationContext(), "Invalid history data");
                        }
                    }
                });
        getBook.execute();


        return 0;
    }

    private static int createReturnAlertDialog(final Activity activity, final Book book) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        final LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_book_details, null);
        dialogBuilder.setView(dialogView);

        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        TextView tvAuthor = (TextView) dialogView.findViewById(R.id.tv_author);
        final TextView tvDescription = (TextView) dialogView.findViewById(R.id.book_description_dialog);
        final ImageView imageBook = (ImageView) dialogView.findViewById(R.id.book_image_dialog);

        TextView tvLoanDate = (TextView) dialogView.findViewById(R.id.loan_date_dialog);
        TextView tvReturnDays = (TextView) dialogView.findViewById(R.id.return_days_dialog);

        final ImageButton addRemoveFavs = (ImageButton) dialogView.findViewById(R.id.ib_AddRemoveFav);
        addRemoveFavs.setVisibility(View.INVISIBLE);

        dialogBuilder.setTitle("Return Book");

        Bitmap bm;
        if (book.getCover() == null) {
            bm = BitmapFactory.decodeResource(activity.getResources(), R.drawable.default_book_image);
        } else {
            bm = BitmapFactory.decodeByteArray(book.getCover(), 0, book.getCover().length);
        }

        imageBook.setImageBitmap(bm);

        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor().getName());

        tvDescription.setText(book.getDescription());
        tvDescription.setMovementMethod(new ScrollingMovementMethod());

        if (book.getHistory().getLoanDate() != null) {
            tvLoanDate.setText("Rented on: \n"
                    + book.getHistory().getLoanDate().toString().substring(0, 10));

            if (book.getHistory().getReturnDate() == null) {
                int days = (int) ((book.getHistory().getLoanDate().getTime() - new java.sql.Date(new Date().getTime()).getTime()) / MILLISECONDS_IN_DAY);
                days += LOAN_DAYS;
                tvReturnDays.setText("Days left: \n"
                        + String.valueOf(days));
                if (days <= 5) tvReturnDays.setTextColor(Color.rgb(255, 0, 0));
                else if (days <= 10) tvReturnDays.setTextColor(Color.rgb(255, 145, 0));
                else tvReturnDays.setTextColor(Color.rgb(0, 128, 0));
            } else {
                tvReturnDays.setText("Returned");
                tvReturnDays.setTextColor(Color.rgb(40, 60, 200));
            }
        }
        dialogBuilder.setPositiveButton("Approve Return", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = ServerProperties.HOST;
                url += "/library/returnBook?historyId=" + book.getHistory().getId()
                        + "&approverId=" + ((Administrator) activity).getCurrentUser().getId()
                        + "&returnDate=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                ServerRequestGET<String> returnBookRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructType(String.class),
                        new AsyncResponse<String>() {
                            @Override
                            public void actionCompleted(String obj) {
                                if (obj != null && !obj.equals("-1")) {
                                    MiscFunctions.createToast(activity.getApplication(), "Book return successful");
                                } else {
                                    MiscFunctions.createToast(activity.getApplication(), "Book return unsuccessful");
                                }
                            }
                        });
                returnBookRequest.execute();

            }


        });

        dialogBuilder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        return 0;
    }

    /**
     * When tapped on a book in the list
     */
    public static int createAlertDialog(final Activity activ, final Book book) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activ);
        final LayoutInflater inflater = activ.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_book_details, null);
        dialogBuilder.setView(dialogView);

        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        TextView tvAuthor = (TextView) dialogView.findViewById(R.id.tv_author);
        final TextView tvDescription = (TextView) dialogView.findViewById(R.id.book_description_dialog);
        final ImageView imageBook = (ImageView) dialogView.findViewById(R.id.book_image_dialog);
        TextView tvLoanDate = (TextView) dialogView.findViewById(R.id.loan_date_dialog);
        TextView tvReturnDays = (TextView) dialogView.findViewById(R.id.return_days_dialog);



        final ImageButton addRemoveFavs = (ImageButton) dialogView.findViewById(R.id.ib_AddRemoveFav);

        if (((LoggedInActivity)activ).getCurrentUser().getType().compareTo("ADMINISTRATOR") == 0 ) {
            addRemoveFavs.setVisibility(View.INVISIBLE);
        }
        else {
            if (book.isFavourite()) {
                addRemoveFavs.setImageBitmap(BitmapFactory.decodeResource(activ.getResources(), R.drawable.fav_minus_small));
            }
            addRemoveFavs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = ServerProperties.HOST;
                    url += "/library";
                    url += !book.isFavourite() ? "/addFavourite" : "/removeFavourite";

                    url += "?userId=" + ((LoggedInActivity)activ).getCurrentUser().getId();
                    url += "&bookId=" + book.getId();

                    ServerRequestGET<String> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructType(String.class),
                        new AsyncResponse<String>() {
                            @Override
                            public void actionCompleted(String obj) {
                                if (obj != null && obj.compareTo("1") == 0) {
                                    if ( book.isFavourite() ) {
                                        book.setFavourite(false);
                                        addRemoveFavs.setImageBitmap(BitmapFactory.decodeResource(activ.getResources(), R.drawable.fav_plus_small));
                                    } else {
                                        book.setFavourite(true);
                                        addRemoveFavs.setImageBitmap(BitmapFactory.decodeResource(activ.getResources(), R.drawable.fav_minus_small));
                                    }
                                    MiscFunctions.createToast(activ.getApplicationContext(), "Success");
                                }
                                else
                                    MiscFunctions.createToast(activ.getApplicationContext(), "Failed");

                            }
                        });

                        theServerRequest.execute();

                }
            });

            if (book.getHistory().getLoanDate() != null && book.getHistory().getReturnDate() == null) {
            /* to be returned */
                dialogBuilder.setPositiveButton("Return Book", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent (activ, qrGeneratePage.class);
                        intent.putExtra("bitmap", qrGenerate(QRTransaction.RETURN, ((LoggedInActivity) activ).getCurrentUser().getId(), book));
                        activ.startActivity(intent);
                    }
                });
            } else {

            /* to be rented */
                dialogBuilder.setPositiveButton("Rent Book", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent (activ, qrGeneratePage.class);
                        intent.putExtra("bitmap", qrGenerate(QRTransaction.RENT, ((LoggedInActivity) activ).getCurrentUser().getId(), book));
                        activ.startActivity(intent);
                    }
                });
            }
        }

        final Bitmap bm;
        if (book.getCover() == null) {
            bm = BitmapFactory.decodeResource(activ.getResources(), R.drawable.default_book_image);
        } else {
            bm = BitmapFactory.decodeByteArray(book.getCover(), 0, book.getCover().length);
        }

        imageBook.setImageBitmap(bm);

        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor().getName());

        tvDescription.setText(book.getDescription());
        tvDescription.setMovementMethod(new ScrollingMovementMethod());

        if (book.getHistory().getLoanDate() != null) {
            tvLoanDate.setText("Rented on: \n"
                    + book.getHistory().getLoanDate().toString().substring(0, 10));

            if (book.getHistory().getReturnDate() == null) {
                int days = (int) ((book.getHistory().getLoanDate().getTime() - new java.sql.Date(new Date().getTime()).getTime()) / MILLISECONDS_IN_DAY);
                days += LOAN_DAYS;
                tvReturnDays.setText("Days left: \n"
                        + String.valueOf(days));
                if (days <= 5) tvReturnDays.setTextColor(Color.rgb(255, 0, 0));
                else if (days <= 10) tvReturnDays.setTextColor(Color.rgb(255, 145, 0));
                else tvReturnDays.setTextColor(Color.rgb(0, 128, 0));
            } else {
                tvReturnDays.setText("Returned");
                tvReturnDays.setTextColor(Color.rgb(40, 60, 200));
            }
        }

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        if ( book.getHistory().getLoanDate() == null && book.getAvailable() == 0 ) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }

        return 0;
    }

    public static int createToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        return 0;
    }

}
