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

    }

    @Override
    public void orderStatus(int i, OrderStatus orderStatus, double v, double v1, double v2, long l, int i1, double v3, int i2, String s) {

    }

    @Override
    public void handle(int i, int i1, String s) {

    }
}
