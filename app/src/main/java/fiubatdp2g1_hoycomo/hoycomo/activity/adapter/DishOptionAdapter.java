package fiubatdp2g1_hoycomo.hoycomo.activity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.model.DishOption;

public class DishOptionAdapter extends RecyclerView.Adapter<DishOptionAdapter.DishOptionViewHolder>{

    List<DishOption> dishOptions;
    private List<DishOptionViewHolder> viewHolders = new ArrayList<>();

    public List<DishOption> getSelectedOptions() {
        List<DishOption> selectedOptions = new ArrayList<>();

        for (DishOptionViewHolder viewHolder : this.viewHolders){
            if (viewHolder.dishOptionCheckBox.isChecked()) {
                selectedOptions.add(viewHolder.dishOption);
            }
        }

        return selectedOptions;
    }

    public static class DishOptionViewHolder extends RecyclerView.ViewHolder {
        public TextView dishOptionName;
        public TextView dishOptionDescription;
        public CheckBox dishOptionCheckBox;

        public DishOption dishOption;

        public DishOptionViewHolder(View itemView) {
            super(itemView);

            this.dishOptionName = itemView.findViewById(R.id.dish_option_name);
            this.dishOptionDescription = itemView.findViewById(R.id.dish_option_description);
            this.dishOptionCheckBox = itemView.findViewById(R.id.option_selected_checkbox);

            this.dishOptionCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DishOptionViewHolder.this.dishOption.selected(((CheckBox) view).isChecked());
                }
            });
        }

        public void bindToDishOption(DishOption dishOption) {
            this.dishOption = dishOption;
            this.dishOptionName.setText(this.dishOption.name + " ($" + this.dishOption.price + ")");
            this.dishOptionDescription.setText(this.dishOption.description);
        }
    }

    public DishOptionAdapter(List<DishOption> dishOptions){
        this.dishOptions = dishOptions;
    }


    @Override
    public DishOptionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dish_option, viewGroup, false);
        DishOptionViewHolder pvh = new DishOptionViewHolder(v);
        this.viewHolders.add(pvh);
        return pvh;
    }

    @Override
    public void onBindViewHolder(DishOptionViewHolder dishViewHolder, int i) {
        dishViewHolder.bindToDishOption(dishOptions.get(i));
    }

    @Override
    public int getItemCount() {
        return dishOptions.size();
    }

}