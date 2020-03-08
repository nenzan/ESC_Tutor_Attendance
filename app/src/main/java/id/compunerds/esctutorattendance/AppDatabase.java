package id.compunerds.esctutorattendance;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Siswa.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SiswaDao userDao();
}
