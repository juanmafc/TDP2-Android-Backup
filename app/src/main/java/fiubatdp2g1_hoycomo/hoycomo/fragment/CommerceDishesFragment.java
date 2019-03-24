package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.activity.CommerceActivity;
import fiubatdp2g1_hoycomo.hoycomo.activity.adapter.DishAdapter;
import fiubatdp2g1_hoycomo.hoycomo.activity.adapter.SimpleSectionedRecyclerViewAdapter;
import fiubatdp2g1_hoycomo.hoycomo.interfaces.CommerceDishesLoadingListener;
import fiubatdp2g1_hoycomo.hoycomo.model.Dish;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;

public class CommerceDishesFragment extends Fragment implements CommerceDishesLoadingListener {

    private List<Dish> dishes = new ArrayList<Dish>();
    RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter dishAdapter;
    private RecyclerView dishesRecyclerView;
    private SimpleSectionedRecyclerViewAdapter mSectionedAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.dishesRecyclerView = new RecyclerView(getContext());
        this.layoutManager = new LinearLayoutManager(getContext());
        this.dishesRecyclerView.setLayoutManager(this.layoutManager);
        this.dishesRecyclerView.setHasFixedSize(true);
        this.dishesRecyclerView.setNestedScrollingEnabled(false);


        //Your RecyclerView.Adapter
        this.dishAdapter = new DishAdapter(this.dishes);
        this.mSectionedAdapter = new SimpleSectionedRecyclerViewAdapter(this.getActivity(), R.layout.dish_category_section, R.id.dish_category_text, this.dishAdapter);
        this.dishesRecyclerView.setAdapter(mSectionedAdapter);

        HoyComoDatabase.getDishesByCategoryForCommerce(this.getSelectedCommerceId(), this);


//        return inflater.inflate(R.layout.commerce_dishes, container, false);
        return this.dishesRecyclerView;
    }

    @Override
    public void updateDishesByCategory(HashMap<String, List<Dish>> dishesByCategory) {
        //Source: https://gist.github.com/gabrielemariotti/4c189fb1124df4556058

        //This is the code to provide a sectioned list
        List<SimpleSectionedRecyclerViewAdapter.Section> dishCategorySections = new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();
        for (String category : dishesByCategory.keySet())
        {
            //Sections
            dishCategorySections.add(new SimpleSectionedRecyclerViewAdapter.Section(this.dishes.size(), category));
            this.dishes.addAll( dishesByCategory.get(category) );

            //For each Dish in the current category, get and its options
            for (Dish dish : dishesByCategory.get(category) ){
                HoyComoDatabase.getDishOptions(this.getSelectedCommerceId(), dish);
            }
        }
        //Add your adapter to the sectionAdapter
        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[dishCategorySections.size()];
        this.mSectionedAdapter.setSections(dishCategorySections.toArray(dummy));
    }


    public String getSelectedCommerceId() {
        return CommerceActivity.getSelectedCommerce().getId();
    }
}