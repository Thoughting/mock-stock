package com.longone.broker.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("stockPositions")
public interface StockService extends RemoteService {
    StockPosition[] getStockPositions();
    String placeOrder(String code, int amount, boolean isBuy);
    String resetPassword(String password);
    DealLog[] getDealLogs();
}