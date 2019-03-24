package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.activity.CommerceActivity;
import fiubatdp2g1_hoycomo.hoycomo.activity.ConfirmedOrderActivity;
import fiubatdp2g1_hoycomo.hoycomo.activity.OrderActivity;
import fiubatdp2g1_hoycomo.hoycomo.model.Commerce;
import fiubatdp2g1_hoycomo.hoycomo.model.CreditCardTextWatcher;
import fiubatdp2g1_hoycomo.hoycomo.model.DeliveryAddress;
import fiubatdp2g1_hoycomo.hoycomo.model.HoyComoUserProfile;
import fiubatdp2g1_hoycomo.hoycomo.service.Utilities;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;

public class ConfirmOrderFragment extends Fragment implements CommerceOutOfRangeDialogFragment.OnConfirmationListener {

    private DeliveryAddress deliveryAddress;
    private TextView deliveryAddressTextView;
    private TextView deliveryAddressDetailsTextView;
    private TextView commerceNameTextview;
    private TextView priceTextView;
    private ImageButton changeDeliveryAddressImagebutton;
    private RelativeLayout creditCardRelativelayout;

    private MaskedEditText creditCardExpirationDateEditText;
    private EditText creditCardNumberEditText;

    private boolean commerceOutOfRange;
    private ImageView creditCardImageView;

    private Spinner paymentMethodsSpinner;

    private PaymentMethod paymentMethod = PaymentMethod.NO_OPTION_SELECTED;
    private EditText creditCardOwnerEditText;
    private EditText creditCardValidationCodeEditText;
    private CreditCardBrand selectedCreditCardBrand = CreditCardBrand.UNKNOWN_BRAND;

    private enum PaymentMethod {
        CASH, CREDIT_CARD, NO_OPTION_SELECTED
    }

    private enum CreditCardBrand {
        UNKNOWN_BRAND,MASTERCARD, VISA, AMERICAN_EXPRESS

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.confirm_order_address, container, false);


        this.deliveryAddressTextView = rootView.findViewById(R.id.delivery_address_textview);
        this.deliveryAddressDetailsTextView = rootView.findViewById(R.id.delivery_address_details_textview);
        if ( this.deliveryAddress != null){
            this.deliveryAddressTextView.setText( this.deliveryAddress.getDeliveryAddressName() );
            this.deliveryAddressDetailsTextView.setText( this.deliveryAddress.getDeliveryAddressDetails() );
        }

        Button confirmOrderButton = rootView.findViewById( R.id.confirm_order_button );
        confirmOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder();
            }
        });

        this.commerceNameTextview = rootView.findViewById(R.id.confirm_commerce_name_textview);
        this.commerceNameTextview.setText(CommerceActivity.getSelectedCommerce().getName());

        this.priceTextView = rootView.findViewById(R.id.confirm_price_textview);
        this.priceTextView.setText( "$" + Utilities.doubleToStringWithTwoDigits( OrderActivity.getOrder().getPrice() ));

        this.changeDeliveryAddressImagebutton = rootView.findViewById(R.id.change_delivery_address_imagebutton);
        this.changeDeliveryAddressImagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                DeliveryAddressForOrderConfigurationFragment configurationFragment  = new DeliveryAddressForOrderConfigurationFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.confirm_order_content, configurationFragment) //If there is a fragment inside content_nav_menu, it is replaced with this new Fragment
                        .addToBackStack(null) //So the user can use the back button
                        .commit(); //commit the transaction
            }
        });


        this.creditCardRelativelayout = rootView.findViewById(R.id.credit_card_relativelayout);

        this.paymentMethodsSpinner = rootView.findViewById(R.id.payment_methods_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1);
        adapter.add("Elegir modo de pago");
        adapter.add("Efectivo");
        adapter.add("Tarjeta");
        paymentMethodsSpinner.setAdapter(adapter);


        paymentMethodsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0 ) {
                    paymentMethod = PaymentMethod.NO_OPTION_SELECTED;
                    creditCardRelativelayout.setVisibility(View.GONE);
                }
                else if (position == 1 ) {
                    paymentMethod = PaymentMethod.CASH;
                    creditCardRelativelayout.setVisibility(View.GONE);
                }
                else if (position == 2 ) {
                    paymentMethod = PaymentMethod.CREDIT_CARD;
                    creditCardRelativelayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("PAYMENT", "NADA SELECCIONADO");
            }
        });



        this.creditCardOwnerEditText = rootView.findViewById(R.id.credit_card_owner_name_eddittext);

        this.creditCardImageView = rootView.findViewById(R.id.credit_card_imageview);
        this.creditCardExpirationDateEditText = rootView.findViewById(R.id.expiration_day_eddittext);
        this.creditCardNumberEditText = rootView.findViewById(R.id.credit_card_number_eddittext);
        this.creditCardValidationCodeEditText = rootView.findViewById(R.id.validation_code_eddittext);

        CreditCardTextWatcher creditCardWatcher = new CreditCardTextWatcher();
        creditCardWatcher.addCardBrandListener( this );
        this.creditCardNumberEditText.addTextChangedListener(creditCardWatcher);



        return rootView;
    }




    private void confirmOrder() {
        if (this.deliveryAddress != null) {
            this.commerceOutOfRange = !this.deliveryAddressIsInsideCommerceRange();
            if ( this.commerceOutOfRange ) {
                Log.e("DELIVERY", "COMMERCE OUT OF RANGE");
                CommerceOutOfRangeDialogFragment dialog = new CommerceOutOfRangeDialogFragment();
                dialog.setOnConfirmationListener(this);
                dialog.show(this.getActivity().getFragmentManager(), "CommerceOutOfRangeDialogFragment");
            }
            else {
                Log.e("DELIVERY", "COMMERCE IN RANGE");
                this.sendOrderConfirmation();
            }
        }
        else {
            Toast.makeText(this.getActivity(),"Por favor ingrese una dirección de entrega",Toast.LENGTH_SHORT).show();
        }


    }

    private boolean deliveryAddressIsInsideCommerceRange() {

        LatLng deliveryAddressLatLng = this.deliveryAddress.getLatLng();
        Commerce selectedCommerce = CommerceActivity.getSelectedCommerce();

        float deliveryDistance = Utilities.calculateDistanceInKm( deliveryAddressLatLng.latitude, deliveryAddressLatLng.longitude, selectedCommerce.getLatitude(), selectedCommerce.getLongitude() );

        return (deliveryDistance <= selectedCommerce.getDeliveryDistanceKm());
    }

    @Override
    public void sendOrderConfirmation() {

        if (this.paymentMethod == PaymentMethod.NO_OPTION_SELECTED) {
            Toast.makeText(getContext(), "Por favor seleccione un modo de pago", Toast.LENGTH_SHORT).show();
        }
        else if (this.paymentMethod == PaymentMethod.CASH){
            this.sendOrderToServer();
        }
        else if ( (this.paymentMethod == PaymentMethod.CREDIT_CARD) && (this.creditCardInformationIsValid()) ){
            HoyComoDatabase.validateCreditCard(this.creditCardNumberEditText.getText().toString().replace(" ", ""), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    boolean creditCardIsValidatedByService = false;

                    try {
                        creditCardIsValidatedByService = response.getBoolean("valid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (creditCardIsValidatedByService) {
                        ConfirmOrderFragment.this.sendOrderToServer();
                    }
                    else {
                        Toast.makeText(getContext(), "Los datos ingresados no corresponden a ningún usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    private boolean creditCardInformationIsValid() {
        boolean creditCardInformationIsValid = true;

        if ( this.selectedCreditCardBrand == CreditCardBrand.UNKNOWN_BRAND ) {
            Toast.makeText(getContext(), "Tipo de tarjeta no aceptado", Toast.LENGTH_SHORT).show();
            creditCardInformationIsValid = false;
        }
        else if ( this.creditCardInformationIsIncomplete() ) {
            Toast.makeText(getContext(), "Por favor complete todos los datos", Toast.LENGTH_SHORT).show();
            creditCardInformationIsValid = false;
        }
        else if ( this.creditCardIsExpired() ) {
            Toast.makeText(getContext(), "Fecha de vencimiento invalida", Toast.LENGTH_SHORT).show();
            creditCardInformationIsValid = false;
        }

        return creditCardInformationIsValid;
    }



    private boolean creditCardIsExpired() {

        String creditCardExpirationDateText = this.creditCardExpirationDateEditText.getText().toString();

        int month = Utilities.stringToInt( creditCardExpirationDateText.split("/")[0] );

        if ( (month == 0) || (month > 12)){
            return true;
        }

        return Utilities.dateHasPassed(creditCardExpirationDateText);
    }

    private boolean creditCardInformationIsIncomplete() {
        return ((this.creditCardOwnerEditText.getText().toString().length() == 0) ||
                ( this.creditCardNumberIsIncomplete()) ||
                (this.creditCardExpirationDateEditText.getText().toString().length() != 5) ||
                (this.creditCardValidationCodeEditText.getText().toString().length() != 3) );
    }


    private boolean creditCardNumberIsIncomplete() {
        return ( ( ( ( this.selectedCreditCardBrand == CreditCardBrand.VISA ) || (this.selectedCreditCardBrand == CreditCardBrand.MASTERCARD) ) &&  this.creditCardNumberEditText.getText().toString().length() != 19) ||
                ( ( this.selectedCreditCardBrand == CreditCardBrand.AMERICAN_EXPRESS ) &&  this.creditCardNumberEditText.getText().toString().length() != 17) );
    }

    private void sendOrderToServer() {
        HoyComoDatabase.getUserInformation(HoyComoUserProfile.getUserId(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if ( response.getBoolean("active") ) {

                        if (paymentMethod == PaymentMethod.CASH) {
                            HoyComoDatabase.sendOrder(HoyComoUserProfile.getUserId(), CommerceActivity.getSelectedCommerceId(), OrderActivity.getOrder(), deliveryAddress, commerceOutOfRange, "cash");
                        }
                        else if (paymentMethod == PaymentMethod.CREDIT_CARD) {
                            HoyComoDatabase.sendOrder(HoyComoUserProfile.getUserId(), CommerceActivity.getSelectedCommerceId(), OrderActivity.getOrder(), deliveryAddress, commerceOutOfRange, "credit card");
                        }

                        Intent myIntent = new Intent(getActivity(), ConfirmedOrderActivity.class);
                        startActivity(myIntent);
                    }
                    else {
                        Toast.makeText(getContext(), "Lo sentimos, tu usuario se encuentra bloqueado", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setDeliveryAddress(DeliveryAddress myAddress) {
        this.deliveryAddress = myAddress;
    }


    public void showVisa() {
        this.creditCardImageView.setImageResource(R.drawable.credit_card_visa);
        this.selectedCreditCardBrand = CreditCardBrand.VISA;
    }


    public void showMastercard() {
        this.creditCardImageView.setImageResource(R.drawable.credit_card_mastercard);
        this.selectedCreditCardBrand = CreditCardBrand.MASTERCARD;
    }

    public void showAmericanExpress() {
        this.creditCardImageView.setImageResource(R.drawable.credit_card_american_express);
        this.selectedCreditCardBrand = CreditCardBrand.AMERICAN_EXPRESS;
    }

    public void showNoBrand() {
        this.creditCardImageView.setImageResource(R.drawable.credit_card_unknow);
        this.selectedCreditCardBrand = CreditCardBrand.UNKNOWN_BRAND;
    }


}
