package fiubatdp2g1_hoycomo.hoycomo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.ArrayList;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.fragment.CommerceSearchFragment;
import fiubatdp2g1_hoycomo.hoycomo.model.PriceRange;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabaseParser;

public class PriceRangeSearchActivity extends AppCompatActivity {

    private static final String LOG_TAG = PriceRangeSearchActivity.class.getSimpleName();
    public static CommerceSearchFragment comerceSearch;
    private static final int MAX_AVG_PRICE = 1000000;
    private static ArrayList<PriceRange> ranges = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_range_search);
        setTitle("Rango de Precio");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.initOptionsBehavior();
    }

    @Override
    public boolean onSupportNavigateUp() {
        comerceSearch.updateCommercesByPriceRange(0, MAX_AVG_PRICE);
        onBackPressed();
        return true;
    }

    private void initOptionsBehavior() {

        HoyComoDatabase.getPriceRangeCategories(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ranges = HoyComoDatabaseParser.ParsePriceRangeCategories(response);

                CardView cardView1 = findViewById(R.id.price_range_1_cardview);
                TextView t1 = findViewById(R.id.text_price1);
                t1.setText("$" + String.valueOf(ranges.get(0).min) + "-$" + String.valueOf(ranges.get(0).max));
                cardView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "$" + ranges.get(0).min + "-$" + ranges.get(0).max, Toast.LENGTH_LONG).show();
                        comerceSearch.updateCommercesByPriceRange(ranges.get(0).min, ranges.get(0).max);
                        onBackPressed();
                    }
                });

                CardView cardView2 = findViewById(R.id.price_range_2_cardview);
                TextView t2 = findViewById(R.id.text_price2);
                t2.setText("$" + String.valueOf(ranges.get(1).min) + "-$" + String.valueOf(ranges.get(1).max));
                cardView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "$" + ranges.get(1).min + "-$" + ranges.get(1).max, Toast.LENGTH_LONG).show();
                        comerceSearch.updateCommercesByPriceRange(ranges.get(1).min, ranges.get(1).max);
                        onBackPressed();
                    }
                });

                CardView cardView3 = findViewById(R.id.price_range_3_cardview);
                TextView t3 = findViewById(R.id.text_price3);
                t3.setText("$" + String.valueOf(ranges.get(2).min) + " o más");
                cardView3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "$" + ranges.get(2).min + " o más", Toast.LENGTH_LONG).show();
                        comerceSearch.updateCommercesByPriceRange(ranges.get(0).min, MAX_AVG_PRICE);
                        onBackPressed();
                    }
                });

            }
        });

    }

}
