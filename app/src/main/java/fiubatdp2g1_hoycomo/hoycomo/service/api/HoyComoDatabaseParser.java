package fiubatdp2g1_hoycomo.hoycomo.service.api;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fiubatdp2g1_hoycomo.hoycomo.model.Category;
import fiubatdp2g1_hoycomo.hoycomo.model.Commerce;
import fiubatdp2g1_hoycomo.hoycomo.model.DeliveryAddress;
import fiubatdp2g1_hoycomo.hoycomo.model.Dish;
import fiubatdp2g1_hoycomo.hoycomo.model.DishOption;
import fiubatdp2g1_hoycomo.hoycomo.model.OpenDay;
import fiubatdp2g1_hoycomo.hoycomo.model.Order;
import fiubatdp2g1_hoycomo.hoycomo.model.PriceRange;
import fiubatdp2g1_hoycomo.hoycomo.model.UserOpinion;

public class HoyComoDatabaseParser {

    public static ArrayList<Commerce> ParseCommercesListJsonObject(JSONObject commercesListJSonObject) {
        ArrayList<Commerce> commerceList = new ArrayList<>();
        try {
            JSONArray commerceListJsonArray = commercesListJSonObject.getJSONArray("restaurants");

            for (int i = 0; i < commerceListJsonArray.length(); i++) {

                JSONObject commerJsonObject = commerceListJsonArray.getJSONObject(i);

                Boolean isActive = commerJsonObject.getBoolean("active");
                if (isActive) {
                    commerceList.add( HoyComoDatabaseParser.ParseCommerce(commerJsonObject) );
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return commerceList;
    }

    public static Commerce ParseCommerce(JSONObject commerceJsonObject) {

        try {
            String commerceId = Integer.toString( commerceJsonObject.getInt("id") );
            String name = commerceJsonObject.getString("name");
            String telephone = commerceJsonObject.getString("telephone");
            Integer deliveryDistance = commerceJsonObject.getInt("distance");

            String imageUrl = commerceJsonObject.getString("image");

            JSONArray category = commerceJsonObject.getJSONArray("category");
            ArrayList<String> categories = new ArrayList<String>();
            for (int j = 0; j < category.length(); j++) {
                categories.add(category.getString(j));
            }

            ArrayList<String> dishCategories = new ArrayList<String>();
            if (!commerceJsonObject.isNull("dish_categories")){
                JSONArray dishCategory = commerceJsonObject.getJSONArray("dish_categories");
                for (int j = 0; j < dishCategory.length(); j++) {
                    dishCategories.add(dishCategory.getString(j));
                }
            }

            JSONObject addressObject = commerceJsonObject.getJSONObject("address");
            String address = addressObject.getString("address");
            double latitude = addressObject.getDouble("latitude");
            double longitude = addressObject.getDouble("longitude");

            ArrayList<OpenDay> openDays = new ArrayList<OpenDay>();
            JSONArray days = commerceJsonObject.getJSONArray("days");
            for (int j = 0; j < days.length(); j++) {
                JSONObject objectDays = days.getJSONObject(j);
                String day = objectDays.getString("day");
                String open = objectDays.getString("open");
                String close = objectDays.getString("close");
                openDays.add(new OpenDay(day, open, close));
            }

            Double stars = null;
            if (!commerceJsonObject.isNull("star")){
                stars = commerceJsonObject.getDouble("star");
            }


            Commerce commerce = new Commerce(commerceId, name, address, latitude, longitude, telephone, deliveryDistance, stars);
            commerce.setCategories(categories);
            commerce.setDishCategories(dishCategories);
            commerce.setOpenDays(openDays);
            commerce.setImageUrl(imageUrl);

            return commerce;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Category> ParseCategories(JSONObject categoriesJsonObject) {
        ArrayList<Category> categories = new ArrayList<>();
        try {
            JSONArray categoriesJsonArray = categoriesJsonObject.getJSONArray("categories");
            for (int i = 0; i < categoriesJsonArray.length(); i++) {
                categories.add(new Category(1, categoriesJsonArray.getString(i), 1));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public static ArrayList<Dish> ParseDishes(JSONObject dishesListJsonObject) {
        ArrayList<Dish> dishes = new ArrayList<Dish>();
        try {
            JSONArray dishesListJsonArray = dishesListJsonObject.getJSONArray("dishes");
            for (int i = 0; i < dishesListJsonArray.length(); i++) {
                JSONObject dishJsonObject = dishesListJsonArray.getJSONObject(i);
                boolean isActive = dishJsonObject.getBoolean("active");
                if( isActive ){
                    Dish dish = HoyComoDatabaseParser.ParseDish(dishJsonObject);
                    dishes.add(dish);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dishes ;
    }

    public static HashMap<String, List<Dish> > ParseDishesByCategory(JSONObject dishesListJsonObject) {

        HashMap<String, List<Dish> > dishesByCategory = new HashMap<>();

        try {
            JSONArray dishesListJsonArray = dishesListJsonObject.getJSONArray("dishes");

            for (int i = 0; i < dishesListJsonArray.length(); i++) {

                JSONObject dishJsonObject = dishesListJsonArray.getJSONObject(i);
                boolean isActive = dishJsonObject.getBoolean("active");
                if( isActive ){
                    Dish dish = HoyComoDatabaseParser.ParseDish(dishJsonObject);
                    HoyComoDatabaseParser.addDishToMap(dishesByCategory,dish.getCategory(), dish );
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dishesByCategory ;
    }

    public static Dish ParseDish(JSONObject dishJsonObject) {

        Dish dish = null;

        try {
            int dishId = dishJsonObject.getInt("id");
            String name = dishJsonObject.getString("name");
            String description = dishJsonObject.getString("description");
            double price =  dishJsonObject.getDouble("price");
            double discount = dishJsonObject.getDouble("discount");
            String imageUrl = dishJsonObject.getString("image" );
            String category = dishJsonObject.getString("category");

            dish = new Dish(dishId, name, description, price,imageUrl, category);
            dish.setActivate(true);
            dish.setDiscount(discount);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dish;
    }

    private static void addDishToMap(HashMap<String, List<Dish>> map, String key, Dish dish) {
        if ( map.containsKey( key ) ) {
            map.get(key).add(dish);
        }
        else {
            ArrayList<Dish> dishes = new ArrayList<Dish>();
            dishes.add( dish );
            map.put(key, dishes);
        }

    }

    public static List<DishOption> ParseDishOptions(JSONObject dishOptionsJsonObject) {
        List<DishOption> dishOptions = new ArrayList<>();
        try {
            JSONArray dishOptionsJsonArray = dishOptionsJsonObject.getJSONArray("options");


            for (int i = 0; i < dishOptionsJsonArray.length(); i++) {
                JSONObject dishOptionJsonObject  = dishOptionsJsonArray.getJSONObject(i);
                boolean isActive = dishOptionJsonObject.getBoolean("active");
                if ( isActive ){


                    dishOptions.add( HoyComoDatabaseParser.ParseDishOption(dishOptionJsonObject));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dishOptions;
    }

    public static DishOption ParseDishOption(JSONObject dishOptionJsonObject) {

        DishOption dishOption = null;

        try {
            int id = dishOptionJsonObject.getInt("option_id");
            String name = dishOptionJsonObject.getString("name");
            double price = dishOptionJsonObject.getDouble("price");
            String description = dishOptionJsonObject.getString("description");
            int optionType = dishOptionJsonObject.getInt("option_type_id");

            dishOption = new DishOption(id, name, description, price, optionType);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return dishOption;
    }

    public static HashMap<String, Double > ParseAverageOfCommerce(JSONObject averagesJsonObject) {
        HashMap<String,Double> commercesAvgs = new HashMap<>();
        try {
            JSONArray averages = averagesJsonObject.getJSONArray("averages");
            for (int i = 0; i < averages.length(); i++) {
                JSONObject avgObject = averages.getJSONObject(i);
                String cId = avgObject.getString("restaurant_id");
                Double avg = avgObject.getDouble("average");
                commercesAvgs.put(cId, avg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return commercesAvgs ;
    }

    public static HashMap<String, Integer> ParseMaxDiscountPerCommerce(JSONObject discountsJsonObject) {
        HashMap<String,Integer> commercesAvgs = new HashMap<>();
        try {
            JSONArray discounts = discountsJsonObject.getJSONArray("discounts");
            for (int i = 0; i < discounts.length(); i++) {
                JSONObject discObject = discounts.getJSONObject(i);
                String cId = discObject.getString("restaurant_id");
                Integer disc = discObject.getInt("maxDiscount");
                commercesAvgs.put(cId, disc);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return commercesAvgs ;
    }

    public static ArrayList<PriceRange> ParsePriceRangeCategories(JSONObject pricesRangesJsonObject) {
        ArrayList<PriceRange> priceRanges = new ArrayList<>();
        try {
            JSONObject r1 = pricesRangesJsonObject.getJSONObject("range1");
            priceRanges.add(new PriceRange("Range 1", r1.getInt("min"), r1.getInt("max")));
            JSONObject r2 = pricesRangesJsonObject.getJSONObject("range2");
            priceRanges.add(new PriceRange("Range 2", r2.getInt("min"), r2.getInt("max")));
            JSONObject r3 = pricesRangesJsonObject.getJSONObject("range3");
            priceRanges.add(new PriceRange("Range 3", r3.getInt("min"), r3.getInt("max")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return priceRanges ;
    }

    public static List<Order> ParseOrdersForUser(String userId, JSONObject ordersJsonObject) {
        List<Order> myOrders = new ArrayList<>();

        try {
            JSONArray ordersJsonArray = ordersJsonObject.getJSONArray("orders");

            for (int i = 0; i < ordersJsonArray.length(); i++) {

                JSONObject orderJsonObject = ordersJsonArray.getJSONObject(i);
                if (orderJsonObject.getString("user_id").compareTo( userId ) == 0) {

                    Order myOrder = HoyComoDatabaseParser.ParseOrder(orderJsonObject);
                    myOrders.add( myOrder );
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return myOrders;
    }

    public static Order ParseOrder(JSONObject orderJsonObject) {
        Order order = null;


        String orderId = null;
        try {
            orderId = orderJsonObject.getString("order_id");
            Double price = orderJsonObject.getDouble("price");
            String state = orderJsonObject.getString("state");
            String commerceId = orderJsonObject.getString("restaurant_id");
            String time = orderJsonObject.getString("time");

            String updateTime = null;
            if ( !orderJsonObject.isNull("update_time") ) {
                orderJsonObject.getString("update_time");
            }

            order = new Order(orderId, commerceId, state, price, time, updateTime);

            JSONArray dishesOrdersJsonArray = orderJsonObject.getJSONArray("dishes");
            for (int j = 0; j < dishesOrdersJsonArray.length(); j++) {
                JSONObject dishOrderJsonObject = dishesOrdersJsonArray.getJSONObject(j);
                String dishId = dishOrderJsonObject.getString("dish_id");
                int quantity = dishOrderJsonObject.getInt("quantity");

                List<String> optionIdList = new ArrayList<>();

                JSONArray optionsOrdersJsonArray = dishOrderJsonObject.getJSONArray("options");
                for (int k = 0; k < optionsOrdersJsonArray.length(); k++) {
                    String optionId = optionsOrdersJsonArray.getString(k);
                    optionIdList.add(optionId);
                }

                Order.DishOrder dishOrder = new Order.DishOrder(commerceId, dishId, optionIdList, quantity);
                order.addDishOrderWithoutAddingPrice(dishOrder);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return order;
    }

    public static List<UserOpinion> ParseOpinionList(JSONObject usersOpinionsListJsonObject) {
        List<UserOpinion> userOpinions = new ArrayList<>();
        try {
            JSONArray userOpinionsJsonArray = usersOpinionsListJsonObject.getJSONArray("stars");
            for (int i = 0; i < userOpinionsJsonArray.length(); i++ ){
                userOpinions.add( HoyComoDatabaseParser.ParseOpinion( userOpinionsJsonArray.getJSONObject(i)  ) );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userOpinions;
    }

    public static UserOpinion ParseOpinion(JSONObject userOpinionJsonObject) {
        UserOpinion userOpinion = null;
        try {
            int id = userOpinionJsonObject.getInt("star_id");
            String userId = userOpinionJsonObject.getString("user_id");
            String commerceId = userOpinionJsonObject.getString("restaurant_id");
            int stars =  userOpinionJsonObject.getInt("value");
            String comment = userOpinionJsonObject.getString("comment");
            String userName = userOpinionJsonObject.getString("user_name");
            String time = userOpinionJsonObject.getString("time");

            userOpinion = new UserOpinion(id, userId, stars, comment, userName, time );
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return userOpinion;
    }

    public static UserOpinion ParseMyOpinion(JSONObject usersOpinionsListJsonObject, String myUserId) {
        UserOpinion myOpinion = null;
        try {
            JSONArray userOpinionsJsonArray = usersOpinionsListJsonObject.getJSONArray("stars");
            int i = 0;
            while ( (myOpinion == null) || ( i < userOpinionsJsonArray.length() ) ){
                UserOpinion aUserOpinion = HoyComoDatabaseParser.ParseOpinion(userOpinionsJsonArray.getJSONObject(i));
                if (aUserOpinion.getUserId().equals(myUserId)) {
                    myOpinion = aUserOpinion;
                }
                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return myOpinion;
    }

    public static ArrayList<String> ParseFavorites(JSONObject favoritesListJSonObject) {
        ArrayList<String> commerceIdsList = new ArrayList<>();
        try {
            JSONArray commerceListJsonArray = favoritesListJSonObject.getJSONArray("favorites");
            for (int i = 0; i < commerceListJsonArray.length(); i++) {
                JSONObject commerJsonObject = commerceListJsonArray.getJSONObject(i);
                commerceIdsList.add(commerJsonObject.getString("restaurant_id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return commerceIdsList;
    }

    public static ArrayList<Commerce> ParseFavritesCommercesListJsonObject(JSONObject commercesListJSonObject) {
        ArrayList<Commerce> commerceList = new ArrayList<>();
        try {
            JSONArray commerceListJsonArray = commercesListJSonObject.getJSONArray("restaurants");

            for (int i = 0; i < commerceListJsonArray.length(); i++) {

                JSONObject commerJsonObject = commerceListJsonArray.getJSONObject(i);

                Boolean isActive = commerJsonObject.getBoolean("active");
                if (isActive) {
                    commerceList.add( HoyComoDatabaseParser.ParseCommerce(commerJsonObject) );
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return commerceList;
    }

    public static DeliveryAddress ParseAddress(JSONObject addressJsonObject) {
        DeliveryAddress deliveryAddress = null;
        try {
            String addressName = addressJsonObject.getString("addressName");
            String addressDetails = addressJsonObject.getString("addressDetails");
            double latitude = addressJsonObject.getDouble("latitude");
            double longitude = addressJsonObject.getDouble("longitude");

            deliveryAddress = new DeliveryAddress(addressName, addressDetails, new LatLng(latitude, longitude));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return deliveryAddress;
    }

}
