package by.bsu.famcs.pricechecker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<ProductInfoData> values;
    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.basket_element, parent,false));
    }

    public RecyclerViewAdapter(List<ProductInfoData> values) {
        this.values = values;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.textViewListItemName.setText(values.get(position).getName());
        holder.spinnerListItemAmount.setSelection(values.get(position).getAmount());
        holder.textViewListItemTotalSum.setText(String.valueOf(values.get(position).getPrice() * values.get(position).getAmount()));
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @NonNull
        TextView textViewListItemName;
        Spinner spinnerListItemAmount;
        TextView textViewListItemTotalSum;
        Button buttonListItemCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewListItemName = itemView.findViewById(R.id.textViewListItemName);
            spinnerListItemAmount = itemView.findViewById(R.id.spinnerListItemAmount);
            textViewListItemTotalSum = itemView.findViewById(R.id.textViewListItemTotalSum);
            buttonListItemCancel = itemView.findViewById(R.id.buttonListItemCancel);
            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(itemView.getContext(), R.layout.simple_spinner_item, new Integer[]{1,2,3,4,5,6,7,8,9,10});
            adapter.setDropDownViewResource(R.layout.simple_spinner_item);
            spinnerListItemAmount.setAdapter(adapter);
        }
    }
}
