package fiubatdp2g1_hoycomo.hoycomo.interfaces;

import java.util.List;

import fiubatdp2g1_hoycomo.hoycomo.model.DishOption;

public interface DishOptionsLoadingListener {
    void updateDishesOptions(List<DishOption> dishOptions);
}
