package by.bsu.famcs.pricechecker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
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
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.textViewListItemName.setText(values.get(position).getName());
        holder.spinnerListItemAmount.setSelection(values.get(position).getAmount()-1);
        holder.buttonListItemCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                values.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, values.size());
                ClassesRef.basket.notifySums();
            }
        });
        holder.spinnerListItemAmount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                holder.textViewListItemTotalSum.setText(
                        new DecimalFormat("#0.00").format(values.get(position).getPrice() * (selectedItemPosition + 1)));
                values.get(position).setAmount(selectedItemPosition + 1);
                ClassesRef.basket.notifySums();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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
