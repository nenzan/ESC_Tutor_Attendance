package id.compunerds.esctutorattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private Context context;
    private List<Siswa> siswaList;

    public RecyclerAdapter(Context mContext, List<Siswa> siswaList) {
        this.context = mContext;
        this.siswaList = siswaList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule_scan, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Siswa siswa = siswaList.get(position);
        holder.nama.setText(siswa.getNama());
        holder.tglMulai.setText(siswa.getTglMulai());
        holder.jamMulai.setText(siswa.getJamMulai());
    }

    @Override
    public int getItemCount() {
        return siswaList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nama, tglMulai, jamMulai;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nama = (TextView) itemView.findViewById(R.id.rvResultNama);
            tglMulai = (TextView) itemView.findViewById(R.id.rvTglScan);
            jamMulai = itemView.findViewById(R.id.rvJamScan);
        }
    }
}
