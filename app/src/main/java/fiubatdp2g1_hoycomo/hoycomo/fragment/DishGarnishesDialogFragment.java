package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.activity.DishActivity;
import fiubatdp2g1_hoycomo.hoycomo.activity.adapter.DishGarnishAdapter;
import fiubatdp2g1_hoycomo.hoycomo.model.DishOption;

public class DishGarnishesDialogFragment extends DialogFragment  {

    private DishGarnishesDialogFragment.onGarnishesSelectedListener onOptionsSelectedListener;
    private RecyclerView dishesRecyclerView;
    private DishGarnishAdapter dishGarnishAdapter;
    private LinearLayoutManager layoutManager;

    public interface onGarnishesSelectedListener{
        void sendSelectedGarnishes(List<DishOption> selectedOptions);
    }

    private Button mActionOk;
    private Button mActionCancel;
    private List<DishOption> dishOptions = new ArrayList<>();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dish_options_dialog, container, false);

        mActionOk = view.findViewById(R.id.accept_options);
        mActionCancel = view.findViewById(R.id.cancel_options);


        this.dishOptions.addAll( DishActivity.getSelectedDish().getGarnishes() );

        this.dishesRecyclerView = view.findViewById(R.id.dish_options_recycler_view);
        this.layoutManager = new LinearLayoutManager(this.getActivity());
        this.dishesRecyclerView.setLayoutManager(this.layoutManager);

        this.dishGarnishAdapter = new DishGarnishAdapter(this.dishOptions);

        this.dishesRecyclerView.setAdapter(this.dishGarnishAdapter);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<DishOption> selectedOptions = new ArrayList<>();
                /* TODO: this only works for guarniciones (because they are unique), not for extras (can be multiple times)
                for (DishOption dishOption : DishOptionsDialogFragment.this.dishOptions) {
                    if (dishOption.isSelected()){
                        selectedOptions.add(dishOption);
                    }
                }
                */

                for (DishOption dishOption : DishGarnishesDialogFragment.this.dishGarnishAdapter.getSelectedOptions()) {
                    selectedOptions.add(dishOption);
                }

                DishGarnishesDialogFragment.this.onOptionsSelectedListener.sendSelectedGarnishes(selectedOptions);
                getDialog().dismiss();
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            this.onOptionsSelectedListener = (onGarnishesSelectedListener) getActivity();
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }
}
