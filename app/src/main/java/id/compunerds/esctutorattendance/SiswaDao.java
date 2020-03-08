package id.compunerds.esctutorattendance;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SiswaDao {

    @Query("SELECT * FROM siswa")
    List<Siswa> getAll();

    @Query("SELECT * FROM siswa WHERE nama LIKE :nama ")
    Siswa findByName(String nama);

    @Insert
    void insertAll(Siswa... siswa);
}
