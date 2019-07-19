package sammie.com.truecrime;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Jitendra on 13/08/2018.
 */
public class MyDatabase extends SQLiteOpenHelper
{
    Context context;
    public MyDatabase(Context context)
    {
        super(context,"details_db",null,1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String qry = "create table user_details (name text,age integer,cno integer,email text,password text)";
        db.execSQL(qry);

        Toast.makeText(context, "Table Created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}