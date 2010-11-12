package com.longone.broker.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AccountPanel extends VerticalPanel implements Initializable {

    private static final String[] HEADERS = {"初始资金", "可用资金", "股票市值", "总市值", "盈亏总额", "盈亏比例", "股票持仓比例"};
    private Grid grid = new Grid(2, HEADERS.length);
    private Button button = new Button("刷新");
    private StockServiceAsync stockSvc;

    public AccountPanel(StockServiceAsync stockSvc) {
        this.stockSvc = stockSvc;
        this.add(button);
        this.add(grid);
        button.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                button.setEnabled(false);
                loadData();
            }
        });
        createGridHeader();
    }

    private void createGridHeader() {
        for (int i = 0; i < HEADERS.length; i++) {
            grid.setWidget(0, i, new Label(HEADERS[i]));
        }
        grid.getRowFormatter().setStyleName(0, "watchListHeader");
        grid.addStyleName("watchList");
        grid.setCellPadding(6);
    }

    private void loadData() {
        // Set up the callback object.
        AsyncCallback<AccountInfo> callback = new AsyncCallback<AccountInfo>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
                GWT.log(caught.toString());
                button.setEnabled(true);
            }

            public void onSuccess(AccountInfo info) {
                if (info == null) {
                    Window.alert(MockStock.SESSION_TIMEOUT_MSG);
                    return;
                }
                populateGridData(info);
                button.setEnabled(true);
            }
        };
        // Make the call to the stock price service.
        stockSvc.getAccountInfo(callback);
    }

    private void populateGridData(AccountInfo info) {
        NumberFormat fmt = NumberFormat.getFormat("#,##0.00");
        grid.setWidget(1, 0, new Label(fmt.format(info.getIntialPrincipal())));
        grid.setWidget(1, 1, new Label(fmt.format(info.getLeftCapitical())));
        grid.setWidget(1, 2, new Label(fmt.format(info.getStockValue())));
        grid.setWidget(1, 3, new Label(fmt.format(info.getTotalValue())));
        grid.setWidget(1, 4, new Label(fmt.format(info.getProfit())));
        grid.setWidget(1, 5, new Label(fmt.format(info.getProfitPct()) + "%"));
        if (info.getProfit() > 0) {
            grid.getCellFormatter().removeStyleName(1, 4, "negativeChange");
            grid.getCellFormatter().addStyleName(1, 4, "positiveChange");
            grid.getCellFormatter().removeStyleName(1, 5, "negativeChange");
            grid.getCellFormatter().addStyleName(1, 5, "positiveChange");
        } else if (info.getProfit() < 0) {
            grid.getCellFormatter().removeStyleName(1, 4, "positiveChange");
            grid.getCellFormatter().addStyleName(1, 4, "negativeChange");
            grid.getCellFormatter().removeStyleName(1, 5, "positiveChange");
            grid.getCellFormatter().addStyleName(1, 5, "negativeChange");
        } else {
            grid.getCellFormatter().removeStyleName(1, 4, "positiveChange");
            grid.getCellFormatter().removeStyleName(1, 4, "negativeChange");
            grid.getCellFormatter().removeStyleName(1, 5, "positiveChange");
            grid.getCellFormatter().removeStyleName(1, 5, "negativeChange");
        }
        if (info.getTotalValue() != 0) {
            grid.setWidget(1, 6, new Label(fmt.format(100.0 * info.getStockValue() / info.getTotalValue()) + "%"));
        }
    }


    public void initialize() {
        loadData();
    }
}
