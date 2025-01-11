package es.ua.eps.sqlite.providers

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import es.ua.eps.sqlite.sql.SQLManager

class UsersContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "es.ua.eps.usersprovider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/users")

        const val USERS = 1
        const val USERS_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "users", USERS)
            addURI(AUTHORITY, "users/#", USERS_ID)
        }
    }

    private lateinit var dbHelper: SQLManager

    override fun onCreate(): Boolean {
        dbHelper = SQLManager(context!!)
        return true
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            USERS -> "vnd.android.cursor.dir/$AUTHORITY.users"
            USERS_ID -> "vnd.android.cursor.item/$AUTHORITY.users"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val db = dbHelper.readableDatabase
        return when (uriMatcher.match(uri)) {
            USERS -> db.query(SQLManager.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            USERS_ID -> {
                val id = uri.lastPathSegment ?: throw IllegalArgumentException("Invalid ID")
                db.query(SQLManager.TABLE_NAME, projection, "_id = ?", arrayOf(id), null, null, sortOrder)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase
        return when (uriMatcher.match(uri)) {
            USERS -> {
                val id = db.insertOrThrow(SQLManager.TABLE_NAME, null, values)
                context?.contentResolver?.notifyChange(uri, null)
                Uri.withAppendedPath(CONTENT_URI, id.toString())
            }
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        return when (uriMatcher.match(uri)) {
            USERS -> db.update(SQLManager.TABLE_NAME, values, selection, selectionArgs)
            USERS_ID -> {
                val id = uri.lastPathSegment ?: throw IllegalArgumentException("Invalid ID")
                db.update(SQLManager.TABLE_NAME, values, "_id = ?", arrayOf(id))
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        return when (uriMatcher.match(uri)) {
            USERS -> db.delete(SQLManager.TABLE_NAME, selection, selectionArgs)
            USERS_ID -> {
                val id = uri.lastPathSegment ?: throw IllegalArgumentException("Invalid ID")
                db.delete(SQLManager.TABLE_NAME, "_id = ?", arrayOf(id))
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}
