package fiubatdp2g1_hoycomo.hoycomo.interfaces;


import java.util.HashMap;
import java.util.List;

import fiubatdp2g1_hoycomo.hoycomo.model.Dish;

public interface CommerceDishesLoadingListener {

    public void updateDishesByCategory(HashMap<String, List<Dish> > dishesByCategory);
}
