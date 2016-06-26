package prashushi.farcon;

/**
 * Created by Dell User on 6/21/2016.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "dbA.FarCon";
    public static final String ITEMS_TABLE_NAME = "itemsA";
    public static final String ITEMS_ID = "item_id";
    public static final String ITEMS_NAME = "item_name";
    public static final String ITEMS_COST = "item_cost";
    public static final String ITEMS_QUANTITY = "item_qty";

    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(
                "create table if not exists "+ITEMS_TABLE_NAME +" " +
                        "(item_id integer primary key, item_name text,item_cost text,item_qty text)"
        );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table "+ITEMS_TABLE_NAME +" " +
                        "(item_id integer primary key, item_name text,item_cost text,item_qty text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+ITEMS_TABLE_NAME );
        onCreate(db);
    }

    public boolean insertItem  (String id, String name, String cost, String quantity)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("item_name", name);
        contentValues.put("item_cost", cost);
        contentValues.put("item_qty", quantity);
        contentValues.put("item_id", id);
        db.insert(ITEMS_TABLE_NAME , null, contentValues);
        System.out.println("Inserted id:"+id);
        return true;
    }


    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, ITEMS_TABLE_NAME);
    }

    public boolean updateItem (String id, String cost, String quantity)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("item_qty", quantity);
        contentValues.put("item_cost", cost);
        db.update(ITEMS_TABLE_NAME, contentValues, "item_id = ? ", new String[] {id } );

        if(quantity.compareTo("0")==0)
          deleteItems(id);
        return true;
    }

    public Integer deleteItems (String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ITEMS_TABLE_NAME, "item_id = ? ", new String[] { id });
    }

    public void deleteDB ()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+ITEMS_TABLE_NAME );
        db.execSQL(
                "create table "+ITEMS_TABLE_NAME +" " +
                        "(item_id integer primary key, item_name text,item_cost text,item_qty text)"
        );

    }

    public ArrayList<String> getItem(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ITEMS_TABLE_NAME +" where item_id='"+id+"'", null );
        ArrayList<String> array=new ArrayList<>();
        array.add(res.getString(res.getColumnIndex(ITEMS_NAME)));
        array.add(res.getString(res.getColumnIndex(ITEMS_COST)));
        array.add(res.getString(res.getColumnIndex(ITEMS_QUANTITY)));
        array.add(res.getString(res.getColumnIndex(ITEMS_ID)));
        res.close();
        return array;
    }

    public String getQuantity(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ITEMS_TABLE_NAME +" where item_id='"+id+"'", null );
        res.moveToFirst();
        String qty=""+res.getString(res.getColumnIndex(ITEMS_QUANTITY));
        res.close();
        return qty;
    }

    public Boolean ifPresent(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ITEMS_TABLE_NAME +" where item_id='"+id+"'", null );
        int n=res.getCount();
        res.close();
        if(n>0)
            return true;
        else
            return false;
    }

    public float getTotalCost(){
        float cost=0;
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ITEMS_TABLE_NAME , null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            cost+=Float.valueOf(res.getString(res.getColumnIndex(ITEMS_COST)))*Float.valueOf(res.getString(res.getColumnIndex(ITEMS_QUANTITY)));
            res.moveToNext();
        }
        res.close();
        return cost;
    }

    public JSONArray getAllItems()
    {
        JSONArray jsonArray=new JSONArray();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ITEMS_TABLE_NAME, null );
        res.moveToFirst();
        int i=0;
        while(!res.isAfterLast()){
            JSONObject obj = new JSONObject();
            try {
                obj.put("item_id",res.getString(res.getColumnIndex(ITEMS_ID) ));
            obj.put("item_name", res.getString(res.getColumnIndex(ITEMS_NAME)));
            obj.put("item_cost", res.getString(res.getColumnIndex(ITEMS_COST)));
            obj.put("item_qty", res.getString(res.getColumnIndex(ITEMS_QUANTITY)));
                jsonArray.put(i++, obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            res.moveToNext();
        }
        System.out.println("Array of length: "+jsonArray.length());
        res.close();
        return jsonArray;
    }
}