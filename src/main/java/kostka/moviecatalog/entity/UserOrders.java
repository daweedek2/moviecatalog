package kostka.moviecatalog.entity;

import java.util.List;

public class UserOrders {
    private List<Order> orders;

    public UserOrders() {
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(final List<Order> orders) {
        this.orders = orders;
    }
}
