package fiubatdp2g1_hoycomo.hoycomo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.activity.adapter.CategoryAdapter;
import fiubatdp2g1_hoycomo.hoycomo.activity.adapter.CommerceSearchTabAdapter;
import fiubatdp2g1_hoycomo.hoycomo.interfaces.CategoriesLoadingListener;
import fiubatdp2g1_hoycomo.hoycomo.model.Category;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;


public class CategorySearchActivity extends AppCompatActivity implements CategoriesLoadingListener {

    private static final String LOG_TAG = CategorySearchActivity.class.getSimpleName();

    public static CommerceSearchTabAdapter comerceSearchAdapter;

    ArrayList<Category> categoriesList = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    private CategoryAdapter categoryAdapter;
    private RecyclerView categoriesRecyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_search);
        setTitle("Categorias");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        HoyComoDatabase.getCategories(this);

        //this.mockCategories();
        this.categoriesRecyclerView = (RecyclerView) findViewById(R.id.categories_search_recycle_view);
        this.categoriesRecyclerView.setHasFixedSize(true);
        this.layoutManager = new LinearLayoutManager(getBaseContext());
        this.categoriesRecyclerView.setLayoutManager(this.layoutManager);
        this.categoryAdapter = new CategoryAdapter(this.categoriesList, comerceSearchAdapter, this);
        this.categoriesRecyclerView.setAdapter(categoryAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_categories, menu);

        MenuItem searchItem = menu.findItem(R.id.search_category);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Bucar categoría");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                categoryAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    categoryAdapter.getFilter().filter("");
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public void updateCategories(ArrayList<Category> categories) {
        Set<String> usedCategories = comerceSearchAdapter.getUsedCategories();
        //TODO: this could be moved to HoyComoDatabase
        for (Category category : categories) {
            if (usedCategories.contains(category.getName())) {
                this.categoriesList.add(category);
            }
        }
        this.sortAlphabetically();
        this.categoryAdapter.notifyDataSetChanged();
    }

    private void sortAlphabetically() {
        Collections.sort(this.categoriesList, new Comparator<Category>() {

            public String cleanString(String texto) {
                texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
                texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
                return texto;
            }

            @Override
            public int compare (Category s1, Category s2){
                String name1 = this.cleanString(s1.getName());
                String name2 = this.cleanString(s2.getName());
                return name1.toLowerCase().compareToIgnoreCase(name2);
            }
        });
    }

}
