package fiubatdp2g1_hoycomo.hoycomo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import fiubatdp2g1_hoycomo.hoycomo.R;

public class ConfirmedOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_order);
    }

    public void goToNAvigationMenu(View button) {
        Intent myIntent = new Intent(this, NavigationMenuActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
    }

    @Override
    public void onBackPressed() {
        this.goToNAvigationMenu(null);
    }
}
