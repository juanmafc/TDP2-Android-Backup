package fiubatdp2g1_hoycomo.hoycomo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.fragment.CommerceSearchFragment;

public class RatingsSearchActivity extends AppCompatActivity {

    private static final String LOG_TAG = RatingsSearchActivity.class.getSimpleName();
    public static CommerceSearchFragment comerceSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings_search);
        setTitle("Puntaje");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.initOptionsBehavior();
    }

    @Override
    public boolean onSupportNavigateUp() {
        comerceSearch.updateCommercesByRating(0.0);
        onBackPressed();
        return true;
    }

    private void initOptionsBehavior() {
        CardView cardView5 = findViewById(R.id.rating_5_cardview);
        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "5" , Toast.LENGTH_LONG).show();
                comerceSearch.updateCommercesByRating(5.0);
                onBackPressed();
            }
        });

        CardView cardView4 = findViewById(R.id.rating_4_cardview);
        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "4" , Toast.LENGTH_LONG).show();
                comerceSearch.updateCommercesByRating(4.0);
                onBackPressed();
            }
        });

        CardView cardView3 = findViewById(R.id.rating_3_cardview);
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "3" , Toast.LENGTH_LONG).show();
                comerceSearch.updateCommercesByRating(3.0);
                onBackPressed();
            }
        });

        CardView cardView2 = findViewById(R.id.rating_2_cardview);
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "2" , Toast.LENGTH_LONG).show();
                comerceSearch.updateCommercesByRating(2.0);
                onBackPressed();
            }
        });

        CardView cardView1 = findViewById(R.id.rating_1_cardview);
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "1" , Toast.LENGTH_LONG).show();
                comerceSearch.updateCommercesByRating(1.0);
                onBackPressed();
            }
        });
    }

}
