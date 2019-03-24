package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.activity.CommerceActivity;
import fiubatdp2g1_hoycomo.hoycomo.model.Commerce;

public class CommerceStartsFragment extends Fragment {

    private FragmentActivity myContext;
    private CommerceStartsFragment parentFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.myContext = (FragmentActivity) context;
//        this.parentFragment = (CommerceStartsFragment) this.myContext.getCurrentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View commerceStarsLayout = inflater.inflate(R.layout.content_commerce_stars, container, false);


        TextView commerceNameTextView = commerceStarsLayout.findViewById(R.id.commerce_name_textview);
        commerceNameTextView.setText(this.getCommerce().getName());

        TextView categoriesTextView = commerceStarsLayout.findViewById(R.id.commerce_categories_textview);
        categoriesTextView.setText(this.getCommerce().getCategoriesString(" - "));

        TextView commerceeRating = commerceStarsLayout.findViewById(R.id.stars_button_information);

        if (this.getCommerce().hasRating()){
            commerceeRating.setText( this.getCommerce().getRatingAsString() );
        }
        else {
            commerceeRating.setText( "-" );
        }

        return commerceStarsLayout;
    }

    public Commerce getCommerce() {
        return CommerceActivity.getSelectedCommerce();
    }

}
