package fiubatdp2g1_hoycomo.hoycomo.fragment;


import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.activity.CommerceActivity;
import fiubatdp2g1_hoycomo.hoycomo.activity.NavigationMenuActivity;
import fiubatdp2g1_hoycomo.hoycomo.activity.OrderActivity;
import fiubatdp2g1_hoycomo.hoycomo.model.HoyComoUserProfile;
import fiubatdp2g1_hoycomo.hoycomo.service.Utilities;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;

import static com.facebook.FacebookSdk.getApplicationContext;


public class CommerceStarsDialogFragment extends DialogFragment  {

    private View view;
    private EditText commentText;

    private int rating;
    private String comment;
    private String newOrderState;
    private String orderDeliveryTime;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.content_commerce_stars_dialog, container, false);

        this.initButtons();
        this.initRatingBar();
        this.commentText = (EditText) this.view.findViewById(R.id.rating_comment_text);

        return view;
    }


    public void setNewOrderState(String newOrderState, String orderDeliveryTime) {
        this.newOrderState = newOrderState;
        this.orderDeliveryTime = orderDeliveryTime;
    }

    public void initRatingBar() {
        RatingBar ratingBar = (RatingBar) this.view.findViewById(R.id.rating);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                CommerceStarsDialogFragment.this.rating = (int) rating;
            }
        });
    }

    private void initButtons() {
        Button mActionCancel = view.findViewById(R.id.cancel_options);
        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        Button mActionOk = view.findViewById(R.id.accept_options);
        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HoyComoDatabase.postRatingForCommerce(HoyComoUserProfile.getUserId(), CommerceActivity.getSelectedCommerceId(), CommerceStarsDialogFragment.this.rating, CommerceStarsDialogFragment.this.commentText.getText().toString(), "1");
                Toast.makeText(getApplicationContext(), "Opinión enviada" , Toast.LENGTH_LONG).show();

                String orderId = OrderActivity.getOrderId();


                HoyComoDatabase.changeOrderState(orderId, newOrderState, orderDeliveryTime);





                HoyComoUserProfile.removePendingOrder(orderId);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(CommerceStarsDialogFragment.this.getActivity());
                notificationManager.cancel(Utilities.stringToInt(orderId));

                getDialog().dismiss();

                Intent myIntent = new Intent(getActivity(), NavigationMenuActivity.class);
                startActivity(myIntent);
            }
        });
    }


}

