package ca.riveros.ib.actions;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.OrderStatus;
import com.ib.controller.ApiController;

/**
 * Created by admin on 6/21/16.
 */
public class LiveOrderHandler implements ApiController.ILiveOrderHandler {

    @Override
    public void openOrder(Contract contract, Order order, OrderState orderState) {
        System.out.println("RECEIVED OPEN ORDER ...");
    }

    @Override
    public void openOrderEnd() {
        System.out.println("OPEN ORDER END");
    }

    @Override
    public void orderStatus(int orderId, OrderStatus status, double filled, double remaining,
                            double avgFillPrice, long permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
        System.out.println("ORDER STATUS : " + status + " WHY HELD : " + whyHeld);
    }

    @Override
    public void handle(int orderId, int errorCode, String errorMsg) {
        System.out.println("ORDER ID " + orderId);
    }
}
