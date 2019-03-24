package fiubatdp2g1_hoycomo.hoycomo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import java.util.Stack;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.fragment.CommerceSearchFragment;
import fiubatdp2g1_hoycomo.hoycomo.fragment.FavoritesFragment;
import fiubatdp2g1_hoycomo.hoycomo.fragment.MyOrdersFragment;
import fiubatdp2g1_hoycomo.hoycomo.fragment.UserProfileAddressConfigurationFragment;
import fiubatdp2g1_hoycomo.hoycomo.model.HoyComoUserProfile;

public class NavigationMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;

    private Stack<Integer> fragmentStack = new Stack<>();


    private int currentItemId;
    private NavigationView navigationView;
    private CommerceSearchFragment commerceSearchFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation__menu);
        setTitle("Hoy Como");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        this.setSelectedItem(R.id.nav_commerce);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameText = (TextView) headerView.findViewById(R.id.menu_userName);
        userNameText.setText(HoyComoUserProfile.getUserName());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if ( (this.currentItemId == R.id.nav_commerce) && (this.commerceSearchFragment.filterIsApllied()) ){
                this.commerceSearchFragment.removeFilter();
            }
            else {
                this.fragmentStack.pop();

                if ( this.fragmentStack.empty() ){
                    this.finish();
                }
                else {
                    this.setSelectedItem( this.fragmentStack.peek() );
                    this.fragmentStack.pop();
                }
            }
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int selectedItemId = item.getItemId();

        if (this.currentItemId != selectedItemId ){
            this.currentItemId = selectedItemId;
            this.fragmentStack.push( this.currentItemId );

            if (selectedItemId == R.id.nav_commerce) {
                this.commerceSearchFragment = new CommerceSearchFragment();
                this.loadFragment( this.commerceSearchFragment );
            } else if (selectedItemId == R.id.nav_my_orders) {
                this.loadFragment(new MyOrdersFragment());
            } else if (selectedItemId == R.id.nav_favorites) {
                this.loadFragment(new FavoritesFragment());
            } else if (selectedItemId == R.id.nav_configuration) {
                this.loadFragment(new UserProfileAddressConfigurationFragment());
            } else if (selectedItemId == R.id.nav_logout) {
                this.logout();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        this.fragmentManager = this.getSupportFragmentManager();
        this.fragmentManager.beginTransaction()
                .replace(R.id.content_nav, fragment) //If there is a fragment inside content_nav_menu, it is replaced with this new Fragment
                //.addToBackStack(null) //So the user can use the back button
                .commit(); //commit the transaction
    }







    private void logout() {
        //TODO: Log out from Server
        //Log out from Facebook
        LoginManager.getInstance().logOut();
        //Go to main page
        Intent myIntent = new Intent(this, MainActivity.class);

        //This is used to clear all activities
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //All activities on top of the Main Activity are removed from the stack
        //myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(myIntent);
    }

    public Fragment getCurrentFragment() {
        return this.fragmentManager.findFragmentById(R.id.content_nav);
    }


    public void setSelectedItem(int selectedItemId) {
        navigationView.setCheckedItem( selectedItemId );
        onNavigationItemSelected(navigationView.getMenu().findItem( selectedItemId ));
    }
}
