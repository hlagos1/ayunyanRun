package lawtechthai.com.ayunyanRun;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private List<List_Data>list_data;

    public MyAdapter(List<List_Data> list_data) {
        this.list_data = list_data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_data,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        List_Data listData=list_data.get(position);
        holder.txtEquipmentID.setText(listData.getEquipmentID());
        holder.txtCertification.setText(listData.getCertification());
    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtEquipmentID, txtCertification;
        public ViewHolder(View itemView) {
            super(itemView);
            txtEquipmentID =(TextView)itemView.findViewById(R.id.txt_equipmentID);
            txtCertification =(TextView)itemView.findViewById(R.id.txt_certification);
        }
    }
}