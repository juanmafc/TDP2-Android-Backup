package fiubatdp2g1_hoycomo.hoycomo.activity.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.model.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> implements Filterable {

    protected ArrayList<Category> categories;
    protected ArrayList<Category> filteredCategories;
    private CategoryAdapter.CategoryFilter comFilter;


    private final CommerceSearchTabAdapter commerceSearchAdapter;
    private Activity activity;


    public static class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Activity activity;
        private final CommerceSearchTabAdapter comerceSearchAdapter;
        public RelativeLayout rl;
        public TextView categoryName;
        public ImageView categoryPhoto;
        public Category category;

        CategoryViewHolder(View itemView, Activity activity, CommerceSearchTabAdapter commerceSearchAdapter) {
            super(itemView);
            rl = (RelativeLayout)itemView.findViewById(R.id.category_icon_layout);
            categoryName = (TextView)itemView.findViewById(R.id.category_icon_name);
//            categoryPhoto = (ImageView)itemView.findViewById(R.id.category_icon_image);
            rl.setOnClickListener(this);

            this.activity = activity;
            this.comerceSearchAdapter = commerceSearchAdapter;
        }

        @Override
        public void onClick(View v) {
            String categoryName = this.categoryName.getText().toString();
            Toast.makeText(rl.getContext(), categoryName,Toast.LENGTH_SHORT).show();

            comerceSearchAdapter.filterByCategoryAndUpdateFragments( categoryName );
            activity.finish();
        }

    }

    public CategoryAdapter(ArrayList<Category> categories, CommerceSearchTabAdapter commerceSearchAdapter, Activity currentActivity){
        this.categories = categories;
        this.filteredCategories = categories;

        this.activity = currentActivity;
        this.commerceSearchAdapter = commerceSearchAdapter;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_icon, viewGroup, false);
        CategoryViewHolder pvh = new CategoryViewHolder(v, this.activity, this.commerceSearchAdapter);
        return pvh;
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder categoryViewHolder, int i) {
        categoryViewHolder.category = categories.get(i);
        categoryViewHolder.categoryName.setText(categories.get(i).getName());
//        categoryViewHolder.categoryPhoto.setImageResource(categories.get(i).getPhotoId());
    }

    @Override
    public int getItemCount() {
        return this.categories.size();
    }

    @Override
    public Filter getFilter() {
        if (comFilter == null) {
            comFilter = new CategoryFilter();
        }

        return comFilter;
    }


    private class CategoryFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            filterResults.count = 0;
            filterResults.values = categories;
            if (charSequence != null && charSequence.length() > 0) {
                ArrayList<Category> tempList = new ArrayList<Category>();

                // search content in items list
                for (Category row : categories) {
                    if (row.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        tempList.add(row);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            }


            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if (filterResults.count == 0) {
                categories = filteredCategories;
                notifyDataSetChanged();
            }
            else {
                categories = (ArrayList<Category>) filterResults.values;
                notifyDataSetChanged();
            }
        }
    }
}


