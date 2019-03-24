package fiubatdp2g1_hoycomo.hoycomo.service.api;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fiubatdp2g1_hoycomo.hoycomo.interfaces.CategoriesLoadingListener;
import fiubatdp2g1_hoycomo.hoycomo.interfaces.CommerceDishesLoadingListener;
import fiubatdp2g1_hoycomo.hoycomo.interfaces.CommercesListLoadingListener;
import fiubatdp2g1_hoycomo.hoycomo.model.DeliveryAddress;
import fiubatdp2g1_hoycomo.hoycomo.model.Dish;
import fiubatdp2g1_hoycomo.hoycomo.model.DishOption;
import fiubatdp2g1_hoycomo.hoycomo.model.HoyComoUserProfile;
import fiubatdp2g1_hoycomo.hoycomo.model.Order;
import fiubatdp2g1_hoycomo.hoycomo.service.Utilities;

public class HoyComoDatabase {

    static private String HoyComoUrl = "https://aqueous-garden-11743.herokuapp.com";

    public static void getCommercesList(final CommercesListLoadingListener listener) {
        String commerceListUrl = HoyComoUrl + "/api/restaurants";
        HttpRequester.SendGETRequest(commerceListUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.updateCommercesList( HoyComoDatabaseParser.ParseCommercesListJsonObject(response) );
            }
        });
    }

    public static void getCategories(final CategoriesLoadingListener listener) {
        String categoriesUrl = HoyComoUrl + "/api/restaurants/categories";
        HttpRequester.SendGETRequest(categoriesUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.updateCategories(HoyComoDatabaseParser.ParseCategories(response));
            }
        });
    }

    public static void getDishesByCategoryForCommerce(String commerceId, final CommerceDishesLoadingListener listener) {
        String dishesUrl = HoyComoUrl + "/api/restaurants/" + commerceId + "/dishes";
        HttpRequester.SendGETRequest(dishesUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.updateDishesByCategory( HoyComoDatabaseParser.ParseDishesByCategory(response) );
            }
        });
    }

    public static void getAveragesOfACommerce(Response.Listener<JSONObject> listener) {
        String dishesUrl = HoyComoUrl + "/api/averages";
        HttpRequester.SendGETRequest(dishesUrl, null, listener);
    }

    public static void getDishOptions(String commerceId, final Dish dish) {
        String dishOptionsUrl = HoyComoUrl + "/api/restaurants/" + commerceId + "/dishes/" + dish.getId() + "/options";
        HttpRequester.SendGETRequest(dishOptionsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dish.setOptions(HoyComoDatabaseParser.ParseDishOptions(response));
            }
        });
    }

    public static void loginUser(String facebookId, String facebookToken, String firebaseToken, String name, Response.Listener<JSONObject> loginListener) {
        String loginUrl = HoyComoUrl + "/api/users/validate";

        JSONObject logingJsonObject = new JSONObject();
        try {
            logingJsonObject.put("facebookId", facebookId);
            logingJsonObject.put("facebookToken", facebookToken);
            logingJsonObject.put("firebaseToken", firebaseToken);
            logingJsonObject.put("user_name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpRequester.SendPOSTRequest(loginUrl, logingJsonObject, loginListener);
    }

    public static void sendOrder(String userID, String restaurantId, Order order, DeliveryAddress deliveryAddress, boolean commerceOutOfRange, String paymentType) {
        //TODO: send a POST message to the url

        String sendOrderUrl = HoyComoUrl + "/api/orders";

        JSONObject orderJsonObject = new JSONObject();
        try {
            //TODO: send the right state
            orderJsonObject.put("state", "waiting");
            orderJsonObject.put("price", order.getPrice());
            orderJsonObject.put("user_id", userID);
            orderJsonObject.put("restaurant_id", Utilities.stringToInt(restaurantId));
            orderJsonObject.put("time", order.getDate());

            JSONObject paymentJsonObject = new JSONObject();
            paymentJsonObject.put("payment_type", paymentType);
            JSONObject paymentDetailsJsonObject = new JSONObject();
            paymentJsonObject.put("details", paymentDetailsJsonObject);
            orderJsonObject.put("payment", paymentJsonObject);




            JSONArray dishesJsonArray = new JSONArray();

            for (Order.DishOrder dishOrder : order.getDisheOrders()) {
                JSONObject dishOrderJsonObject = new JSONObject();
                dishOrderJsonObject.put("dish_id", dishOrder.getDishId());

                JSONArray dishOptionsJsonObject = new JSONArray();
                for (DishOption dishOption : dishOrder.getDishOptions()) {
                    dishOptionsJsonObject.put(dishOption.getId());
                }
                dishOrderJsonObject.put("options", dishOptionsJsonObject);
                dishOrderJsonObject.put("quantity", dishOrder.getQuantity());
                dishesJsonArray.put(dishOrderJsonObject);
            }
            orderJsonObject.put("dishes", dishesJsonArray);

            orderJsonObject.put("username", HoyComoUserProfile.getUserName());


            JSONObject deliveryAddressJsonObject = new JSONObject();
            deliveryAddressJsonObject.put("addressName", deliveryAddress.getDeliveryAddressName());
            deliveryAddressJsonObject.put("addressDetails", deliveryAddress.getDeliveryAddressDetails());
            deliveryAddressJsonObject.put("latitude", deliveryAddress.getLatLng().latitude);
            deliveryAddressJsonObject.put("longitude",  deliveryAddress.getLatLng().longitude);
            deliveryAddressJsonObject.put("out_of_range",  commerceOutOfRange);
            orderJsonObject.put("address", deliveryAddressJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("JSON DEBUG", orderJsonObject.toString());
        HttpRequester.SendPOSTRequest(sendOrderUrl, orderJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("JSON DEBUG", response.toString());
            }
        });

    }

    public static void getOrders(Response.Listener<JSONObject> listener) {
        String ordersUrl = HoyComoUrl + "/api/orders";
        HttpRequester.SendGETRequest(ordersUrl, null, listener);
    }


    public static void getMaxDiscountPerCommerce(Response.Listener<JSONObject> listener) {
        String discountsUrl = HoyComoUrl + "/api/restaurants/discounts";
        HttpRequester.SendGETRequest(discountsUrl, null, listener);
    }

    public static void postRatingForCommerce(String userId, String commerceId, int rating, String comment, String orderId) {

        String starUrl = HoyComoUrl + "/api/stars";

        JSONObject starJsonObject = new JSONObject();
        try {
            starJsonObject.put("order_id", orderId);
            starJsonObject.put("time", Utilities.getCurrentDateAndTime() );

            starJsonObject.put("user_id", userId);
            starJsonObject.put("restaurant_id", commerceId);
            starJsonObject.put("value", rating);
            starJsonObject.put("comment", comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpRequester.SendPOSTRequest(starUrl, starJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("JSON DEBUG", response.toString());
            }
        });
    }

    public static void getListOfOpinionsForCommerce(String commerceId, Response.Listener<JSONObject> listener) {
        String commentsUrl = HoyComoUrl + "/api/comments/" + commerceId;
        HttpRequester.SendGETRequest(commentsUrl, null, listener);
    }

    public static void getCommerce(String commerceId, Response.Listener<JSONObject> listener) {
        String commerceUrl = HoyComoUrl + "/api/restaurants/" + commerceId;
        HttpRequester.SendGETRequest(commerceUrl, null, listener);
    }

    public static void getDish(String commerceId, String dishId, Response.Listener<JSONObject> listener) {
        String dishUrl = HoyComoUrl + "/api/restaurants/" + commerceId + "/dishes/" + dishId ;
        HttpRequester.SendGETRequest(dishUrl, null, listener);
    }

    public static void getDishOptions(String commerceId, String dishId, String optionId, Response.Listener<JSONObject> listener) {
        String dishOptionsUrl = HoyComoUrl + "/api/restaurants/" + commerceId + "/options/" + optionId;
        HttpRequester.SendGETRequest(dishOptionsUrl, null, listener);
    }

    public static void changeOrderState(String orderId, String state, String time) {
        String changeOrderStateUrl = HoyComoUrl + "/api/orders/" + orderId;

        JSONObject orderStateJsonObject = new JSONObject();
        try {
            orderStateJsonObject.put("state", state);
            orderStateJsonObject.put("update_time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpRequester.SendPUTRequest(changeOrderStateUrl, orderStateJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("JSON DEBUG", response.toString());
            }
        });
    }

    public static void getPriceRangeCategories(Response.Listener<JSONObject> listener) {
        String url = HoyComoUrl + "/api/priceranges/";
        HttpRequester.SendGETRequest(url, null, listener);
    }

    public static void getUserFavoritesCommercesList(String userId, Response.Listener<JSONObject> listener) {
        String commerceListUrl = HoyComoUrl + "/api/favorites/" + userId;
        HttpRequester.SendGETRequest(commerceListUrl, null, listener);
    }

    public static void getOneUserFavoritesCommerces(String userId, String commerceId, Response.Listener<JSONObject> listener) {
        String commerceListUrl = HoyComoUrl + "/api/favorites/" + userId + "/" + commerceId ;
        HttpRequester.SendGETRequest(commerceListUrl, null, listener);
    }

    public static void postUserFavoriteCommerce(String userId, String commerceId) {

        String favUrl = HoyComoUrl + "/api/favorites";

        JSONObject favJsonObject = new JSONObject();
        try {
            favJsonObject.put("user_id", userId);
            favJsonObject.put("restaurant_id", commerceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("JSON DEBUG - send", favJsonObject.toString());

        HttpRequester.SendPOSTRequest(favUrl, favJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("JSON DEBUG", response.toString());
            }
        });
    }


    public static void deleteUserFavoriteCommerce(String userId, String commerceId) {

        String favUrl = HoyComoUrl + "/api/favorites/" + userId + "/" + commerceId;

        JSONObject favJsonObject = new JSONObject();
        try {
            favJsonObject.put("user_id", userId);
            favJsonObject.put("restaurant_id", commerceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpRequester.SendDELETERequest(favUrl, favJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("JSON DEBUG", response.toString());
            }
        });
    }

    public static void sendMyAddress(DeliveryAddress myAddress, String userId) {
        String sendMyAddressUrl = HoyComoUrl + "/api/users/" + userId;

        JSONObject addressJsonObject = new JSONObject();
        try {

            JSONObject myAddressJsonObject = new JSONObject();
            myAddressJsonObject.put("addressName", myAddress.getDeliveryAddressName());
            myAddressJsonObject.put("addressDetails", myAddress.getDeliveryAddressDetails());
            myAddressJsonObject.put("latitude", myAddress.getLatLng().latitude);
            myAddressJsonObject.put("longitude", myAddress.getLatLng().longitude);

            addressJsonObject.put("address", myAddressJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequester.SendPUTRequest(sendMyAddressUrl, addressJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("JSON DEBUG", response.toString());
            }
        });

    }

    public static void getUserInformation(String userId, Response.Listener<JSONObject> listener) {
        String userUrl = HoyComoUrl + "/api/users/" + userId;
        HttpRequester.SendGETRequest(userUrl, null, listener);
    }

    public static void validateCreditCard(String creditCard, Response.Listener<JSONObject> listener) {
        String creditCardValidationUrl = HoyComoUrl + "/api/payment/validate?card=" + creditCard;
        HttpRequester.SendGETRequest(creditCardValidationUrl, null, listener);
    }
}
