package com.example.nitishkumar.inventoryappv2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.nitishkumar.inventoryappv2.data.BookContract.*;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = (TextView)view.findViewById(R.id.nameText);
        TextView priceTextView = (TextView)view.findViewById(R.id.priceText);
        TextView categoryTextView = (TextView)view.findViewById(R.id.categoryText);
        TextView quantityTextView = (TextView)view.findViewById(R.id.quantityText);
        Button saleButtonView = (Button)view.findViewById(R.id.saleButton);

        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int bookCategoryColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_CATEGORY);
        int bookPriceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int bookQuantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTIT);

        final int bookID = cursor.getInt(idColumnIndex);
        final String bookName = cursor.getString(bookNameColumnIndex);
        final String bookCategory = cursor.getString(bookCategoryColumnIndex);
        final int bookPrice = cursor.getInt(bookPriceColumnIndex);
        final int bookQuantity = cursor.getInt(bookQuantityColumnIndex);

        nameTextView.setText(bookName);
        priceTextView.setText(context.getString(R.string.rupees_symbol) + Integer.toString(bookPrice));
        categoryTextView.setText(bookCategory);
        quantityTextView.setText(context.getString(R.string.qty_head) + Integer.toString(bookQuantity));

        saleButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookQuantity > 0)
                {
                    int updatedQuantity = bookQuantity - 1;

                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_QUANTIT, updatedQuantity);

                    Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookID);
                    int rowAffected = context.getContentResolver().update(currentBookUri, values, null, null);

                    if (rowAffected == 0)
                    {
                        Toast.makeText(context, R.string.sale_failed, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(context, R.string.sale_success, Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(context, R.string.out_of_stock, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}