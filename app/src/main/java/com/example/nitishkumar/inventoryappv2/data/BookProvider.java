package com.example.nitishkumar.inventoryappv2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.nitishkumar.inventoryappv2.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    private static final int BOOKS = 100;
    private static final int BOOKS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match)
        {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot Query unknoen URI : " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;

            case BOOKS_ID:
                return BookEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI : " + uri + "with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case BOOKS:
                return insertBook(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not suppoted for URI : " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues contentValues) {

        String name = contentValues.getAsString(BookEntry.COLUMN_BOOK_NAME);
        if (name == null)
        {
            throw new IllegalArgumentException("Book Requires a Name");
        }

        String category = contentValues.getAsString(BookEntry.COLUMN_BOOK_CATEGORY);
        if (category == null)
        {
            throw new IllegalArgumentException("Book Requires a Category");
        }

        Integer price = contentValues.getAsInteger(BookEntry.COLUMN_BOOK_PRICE);
        if (price != null && price < 0)
        {
            throw new IllegalArgumentException("Book Requires a valid Price");
        }

        Integer quantity = contentValues.getAsInteger(BookEntry.COLUMN_BOOK_QUANTIT);
        if (quantity != null && quantity < 0)
        {
            throw new IllegalArgumentException("Book Requires a valid Quantity");
        }

        String supplierName = contentValues.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
        if (supplierName == null)
        {
            throw new IllegalArgumentException("Book Requires a Supplier Name");
        }

        String supplierNo = contentValues.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NO);
        if (supplierNo == null)
        {
            throw new IllegalArgumentException("Book Requires a Supplier No");
        }

        String supplierEmail = contentValues.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_EMAIL);
        if (supplierEmail == null)
        {
            throw new IllegalArgumentException("Book Requires a Supplier Email");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(BookEntry.TABLE_NAME, null, contentValues);

        if (id == -1)
        {
            Toast.makeText(getContext(), "Failed to insert Row", Toast.LENGTH_SHORT).show();
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case BOOKS:
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is Not Supported For URI : " + uri);
        }

        if (rowsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);

            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBook(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not suppoted for URI : " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        if (contentValues.containsKey(BookEntry.COLUMN_BOOK_NAME))
        {
            String name = contentValues.getAsString(BookEntry.COLUMN_BOOK_NAME);
            if (name == null)
            {
                throw new IllegalArgumentException("Book requires a Name");
            }
        }

        if (contentValues.containsKey(BookEntry.COLUMN_BOOK_CATEGORY))
        {
            String category = contentValues.getAsString(BookEntry.COLUMN_BOOK_CATEGORY);
            if (category == null)
            {
                throw new IllegalArgumentException("Book Requires a Category");
            }
        }

        if (contentValues.containsKey(BookEntry.COLUMN_BOOK_PRICE))
        {
            Integer price = contentValues.getAsInteger(BookEntry.COLUMN_BOOK_PRICE);
            if (price != null && price < 0)
            {
                throw new IllegalArgumentException("Book Requires a valid Price");
            }
        }

        if (contentValues.containsKey(BookEntry.COLUMN_BOOK_QUANTIT))
        {
            Integer quantity = contentValues.getAsInteger(BookEntry.COLUMN_BOOK_QUANTIT);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Book Requires a valid Quantity");
            }
        }

        if (contentValues.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_NAME))
        {
            String supplierName = contentValues.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Book Requires a Supplier Name");
            }
        }

        if (contentValues.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_NO))
        {
            String supplierNo = contentValues.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NO);
            if (supplierNo == null) {
                throw new IllegalArgumentException("Book Requires a Supplier No");
            }
        }

        if (contentValues.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_EMAIL))
        {
            String supplierEmail = contentValues.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_EMAIL);
            if (supplierEmail == null) {
                throw new IllegalArgumentException("Book Requires a Supplier Email");
            }
        }

        if (contentValues.size() == 0)
        {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (rowsUpdated != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}