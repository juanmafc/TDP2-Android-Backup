package fiubatdp2g1_hoycomo.hoycomo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.activity.adapter.CommercePagerTabAdapter;
import fiubatdp2g1_hoycomo.hoycomo.fragment.CommerceDishesFragment;
import fiubatdp2g1_hoycomo.hoycomo.fragment.CommerceInformationFragment;
import fiubatdp2g1_hoycomo.hoycomo.fragment.OpinionsFragment;
import fiubatdp2g1_hoycomo.hoycomo.model.Commerce;
import fiubatdp2g1_hoycomo.hoycomo.model.Order;
import fiubatdp2g1_hoycomo.hoycomo.service.ImagesLoader;

public class CommerceActivity extends AppCompatActivity {


    enum ListContent {
        DISHES,
        OPINIONS
    }

    static private Commerce selectedCommerce = null;
    private CollapsingToolbarLayout collapsingToolbar;
    private CommercePagerTabAdapter commercePagerTabAdapter;
    private ViewPager viewPager;

    private ImageView showMapPagePagerImage;
    private ImageView showInformationPagePagerImage;
    private ImageView showStarsPagePagerImage;

    private ImageView showMapPagePagerImageUnselected;
    private ImageView showInformationPagePagerImageUnselected;
    private ImageView showStarsPagePagerImageUnselected;
    private ImageView showOrder;
    private ImageView showOrderInactive;
    private FragmentManager fragmentManager;
    private ListContent currentContent;

    public static void SetSelectedCommerce(Commerce selectedCommerce) {
        CommerceActivity.selectedCommerce = selectedCommerce;
        CommerceInformationFragment.selectedCommerce = selectedCommerce;

        //A new Order is created for this selectedCommerce
        OrderActivity.setOrder( new Order( selectedCommerce ) );
    }

    public static Commerce getSelectedCommerce() {
        return CommerceActivity.selectedCommerce;
    }

    public static String getSelectedCommerceId() {
        return CommerceActivity.getSelectedCommerce().getId();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commerce);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Hoy Como");
        
        this.fragmentManager = this.getSupportFragmentManager();
        currentContent = ListContent.DISHES;
        this.fragmentManager.beginTransaction().replace(R.id.commerce_content_list_linearlayout, new CommerceDishesFragment()).commit();

        ImageView commerceImage = findViewById(R.id.commerce_imageview);
        ImagesLoader.Load(CommerceActivity.selectedCommerce.getImageUrl(), commerceImage);

        this.showMapPagePagerImage = (ImageView) findViewById(R.id.imageView_pager_map);
        this.showInformationPagePagerImage = (ImageView) findViewById(R.id.imageView_pager_info);
        this.showStarsPagePagerImage = (ImageView) findViewById(R.id.imageView_pager_star);

        this.showMapPagePagerImageUnselected = (ImageView) findViewById(R.id.imageView_pager_map_unselected);
        this.showInformationPagePagerImageUnselected = (ImageView) findViewById(R.id.imageView_pager_info_unselected);
        this.showStarsPagePagerImageUnselected = (ImageView) findViewById(R.id.imageView_pager_star_unselected);

        this.showMapPagePagerImageUnselected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CommerceActivity.this.showMapPage(null);
            }
        });

        this.showInformationPagePagerImageUnselected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CommerceActivity.this.showInformationPage(null);
            }
        });

        this.showStarsPagePagerImageUnselected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CommerceActivity.this.showStarsPage(null);
            }
        });

        this.viewPager = findViewById(R.id.commerce_viewpager);
        this.commercePagerTabAdapter = new CommercePagerTabAdapter(getSupportFragmentManager());
        this.viewPager.setAdapter(this.commercePagerTabAdapter);

        this.setInitialPage();

        this.viewPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrollStateChanged(int state) {
                //Called when the scroll state changes.
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //This method will be invoked when the current page is scrolled,
                //either as part of a programmatically initiated smooth scroll
                //or a user initiated touch scroll.
            }

            @Override
            public void onPageSelected(int position) {
                //This method will be invoked when a new page becomes selected.
                switch (position) {
                    case 0:
                        CommerceActivity.this.setMapPageSelected();
                        break;
                    case 1:
                        CommerceActivity.this.setInformationPageSelected();
                        break;
                    case 2:
                        CommerceActivity.this.setStarsPageSelected();
                        break;
                }
                CommerceActivity.this.commercePagerTabAdapter.updateTab(position);
            }}
        );

        this.showOrderInactive = (ImageView) findViewById(R.id.imageView_order_inactive);
        this.showOrder = (ImageView) findViewById(R.id.imageView_order);
        this.showOrder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CommerceActivity.this.openOrderActivity(null);
            }
        });

        if (OrderActivity.getNumberOfDishes() > 0){
            this.activeOrder();
        }
        else {
            this.inactiveOrder();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        //onBackPressed();
        this.openNavigationMenuActivity();

        return true;
    }

    private void openNavigationMenuActivity() {
        Intent myIntent = new Intent(this, NavigationMenuActivity.class);
        this.startActivity(myIntent);
    }

    public void activeOrder() {
        this.showOrder.setVisibility(View.VISIBLE);
        this.showOrderInactive.setVisibility(View.INVISIBLE);
    }

    public void inactiveOrder() {
        this.showOrder.setVisibility(View.INVISIBLE);
        this.showOrderInactive.setVisibility(View.VISIBLE);
    }

    private void setInitialPage() {
        this.showInformationPage(null);
        this.setInformationPageSelected();
    }

    public void openOrderActivity(View goToOrderActivity) {
        Intent myIntent = new Intent(this, OrderActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(myIntent);
    }

    private void setMapPageSelected() {
        this.showMapPagePagerImage.setVisibility(View.VISIBLE);
        this.showInformationPagePagerImage.setVisibility(View.INVISIBLE);
        this.showStarsPagePagerImage.setVisibility(View.INVISIBLE);
        this.showMapPagePagerImageUnselected.setVisibility(View.INVISIBLE);
        this.showInformationPagePagerImageUnselected.setVisibility(View.VISIBLE);
        this.showStarsPagePagerImageUnselected.setVisibility(View.VISIBLE);

        this.setListContent(ListContent.DISHES);
    }

    private void setInformationPageSelected() {
        this.showMapPagePagerImage.setVisibility(View.INVISIBLE);
        this.showInformationPagePagerImage.setVisibility(View.VISIBLE);
        this.showStarsPagePagerImage.setVisibility(View.INVISIBLE);
        this.showMapPagePagerImageUnselected.setVisibility(View.VISIBLE);
        this.showInformationPagePagerImageUnselected.setVisibility(View.INVISIBLE);
        this.showStarsPagePagerImageUnselected.setVisibility(View.VISIBLE);

        this.setListContent(ListContent.DISHES);
    }

    private void setStarsPageSelected() {
        this.showMapPagePagerImage.setVisibility(View.INVISIBLE);
        this.showInformationPagePagerImage.setVisibility(View.INVISIBLE);
        this.showStarsPagePagerImage.setVisibility(View.VISIBLE);
        this.showMapPagePagerImageUnselected.setVisibility(View.VISIBLE);
        this.showInformationPagePagerImageUnselected.setVisibility(View.VISIBLE);
        this.showStarsPagePagerImageUnselected.setVisibility(View.INVISIBLE);


        this.setListContent(ListContent.OPINIONS);

    }


    public void setListContent(ListContent listContent) {
        if ( this.currentContent != listContent ){
            this.currentContent = listContent;
            Fragment currentFragment = null;
            switch (listContent){
                case DISHES:
                    currentFragment = new CommerceDishesFragment();
                    break;
                case OPINIONS:
                    currentFragment = new OpinionsFragment();
                    break;
            }
            this.fragmentManager.beginTransaction().replace(R.id.commerce_content_list_linearlayout, currentFragment ).commit();
        }
    }


    public void showMapPage(View pressedButton) {
        this.viewPager.setCurrentItem(0);
    }

    public void showInformationPage(View pressedButton) {
        this.viewPager.setCurrentItem(1);
    }

    public void showStarsPage(View pressedButton) {
        this.viewPager.setCurrentItem(2);
    }


    public void showCommentsActivity(View button) {
        Intent intent = new Intent(this, CommentsActivity.class);
        startActivity(intent);
    }

}
