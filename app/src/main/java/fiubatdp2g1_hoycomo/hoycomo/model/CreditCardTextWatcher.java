package fiubatdp2g1_hoycomo.hoycomo.model;

import android.text.Editable;
import android.text.TextWatcher;

import fiubatdp2g1_hoycomo.hoycomo.fragment.ConfirmOrderFragment;
import fiubatdp2g1_hoycomo.hoycomo.service.Utilities;

public class CreditCardTextWatcher implements TextWatcher {

    boolean editing = false;
    private ConfirmOrderFragment cardBrandListener;


    public String applyVisaFormat(String creditCardNumber) {

        int creditCardLenght = creditCardNumber.length();
        //creditCardNumber = 4582 4455 4412 87845
        if ( 16 < creditCardLenght ) {
            creditCardNumber = creditCardNumber.substring(0, 16);
        }
        if ( 12 <  creditCardLenght ) {
            creditCardNumber = creditCardNumber.substring(0, 12) + " " + creditCardNumber.substring(12, creditCardNumber.length());
        }
        if ( 8 <  creditCardLenght ) {
            creditCardNumber = creditCardNumber.substring(0, 8) + " " + creditCardNumber.substring(8, creditCardNumber.length());
        }
        if ( 4 <  creditCardLenght ) {
            creditCardNumber = creditCardNumber.substring(0, 4) + " " + creditCardNumber.substring(4, creditCardNumber.length());
        }
        return creditCardNumber;
    }

    private String applyMasterCardFormat(String creditCardNumber) {
        return this.applyVisaFormat(creditCardNumber);
    }


    public String applyAmericanExpressFormat(String creditCardNumber) {

        int creditCardLenght = creditCardNumber.length();

        if ( 15 < creditCardLenght ) {
            creditCardNumber = creditCardNumber.substring(0, 15);
        }
        if ( 10 <  creditCardLenght ) {
            creditCardNumber = creditCardNumber.substring(0, 10) + " " + creditCardNumber.substring(10, creditCardNumber.length());
        }
        if ( 4 <  creditCardLenght ) {
            creditCardNumber = creditCardNumber.substring(0, 4) + " " + creditCardNumber.substring(4, creditCardNumber.length());
        }
        return creditCardNumber;
    }

    private CharSequence applyNoBrandFormat(String creditCardNumber) {

        if ( 16 < creditCardNumber.length() ){
            creditCardNumber = creditCardNumber.substring(0, 16);
        }
        return creditCardNumber;
    }

    @Override
    public void afterTextChanged(Editable s) {

        if (!editing) {
            editing = true;

            String creditCardNumber = s.toString();
            creditCardNumber = creditCardNumber.replace(" ", "");
            //https://www.freeformatter.com/credit-card-number-generator-validator.html
            if (creditCardNumber.length() == 1) {
                if ( creditCardNumber.equals("4") ){
                    this.cardBrandListener.showVisa();
                    s.replace(0, s.length(), this.applyVisaFormat(creditCardNumber));
                }
                else {
                    this.cardBrandListener.showNoBrand();
                }
            }
            else if ( creditCardNumber.length() >= 2) {
                int firstTwoDigits = Utilities.stringToInt( creditCardNumber.substring(0, 2) );
                if ( ( 40 <= firstTwoDigits ) && ( firstTwoDigits <= 49) ){
                    this.cardBrandListener.showVisa();
                    s.replace(0, s.length(), this.applyVisaFormat(creditCardNumber));
                }
                else if ( ( 51 <= firstTwoDigits ) && ( firstTwoDigits <= 55) ){
                    this.cardBrandListener.showMastercard();
                    s.replace(0, s.length(), this.applyMasterCardFormat(creditCardNumber));
                }
                else if ( ( firstTwoDigits == 34 ) || ( firstTwoDigits == 37 ) ){
                    this.cardBrandListener.showAmericanExpress();
                    s.replace(0, s.length(), this.applyAmericanExpressFormat(creditCardNumber));
                }
                else {
                    this.cardBrandListener.showNoBrand();
                    s.replace(0, s.length(), this.applyNoBrandFormat(creditCardNumber) );
                }
            }
            else {
                this.cardBrandListener.showNoBrand();
            }
            editing = false;
        }
    }




    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public void addCardBrandListener(ConfirmOrderFragment cardBrandListener) {
        this.cardBrandListener = cardBrandListener;
    }
}
