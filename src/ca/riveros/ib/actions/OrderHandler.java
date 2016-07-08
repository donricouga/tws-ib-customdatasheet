package ca.riveros.ib.actions;

import com.ib.client.OrderState;
import com.ib.client.OrderStatus;
import com.ib.controller.ApiController;

/**
 * Created by admin on 6/21/16.
 */
public class OrderHandler implements ApiController.IOrderHandler {

    @Override
    public void orderState(OrderState orderState) {
        System.out.println("Order State : " + orderState);
    }

    @Override
    public void orderStatus(OrderStatus status, double filled, double remaining, double avgFillPrice, long permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
        System.out.println("order status : " + status + " filled : " + filled);
    }

    @Override
    public void handle(int errorCode, String errorMsg) {
        System.out.println("ERROR : " + errorCode + " ERROR MSG : " + errorMsg);
    }
}
