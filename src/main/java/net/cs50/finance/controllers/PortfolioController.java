package net.cs50.finance.controllers;

import net.cs50.finance.models.Stock;
import net.cs50.finance.models.StockHolding;
import net.cs50.finance.models.StockLookupException;
import net.cs50.finance.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Chris Bay on 5/17/15.
 */
@Controller
public class PortfolioController extends AbstractFinanceController {

    @RequestMapping(value = "/portfolio")
    public String portfolio(HttpServletRequest request, Model model){

        // TODO - Implement portfolio display
        User user = this.getUserFromSession(request);

        ArrayList<HashMap<String, String>> portfolioData = new ArrayList<HashMap<String, String>>();

        Collection<StockHolding> holdings = user.getPortfolio().values();

        for (StockHolding holding : holdings) {

            HashMap<String, String> holdingData = new HashMap<String, String>();

            holdingData.put("stockShares", String.valueOf(holding.getSharesOwned()));

            Stock currentStock = null;
            try {
                currentStock = Stock.lookupStock(holding.getSymbol());
            } catch (StockLookupException e) {
                e.printStackTrace();
                return this.displayError("Portfolio error", model);
            }

            holdingData.put("stockName", currentStock.toString());

            String priceDisplay = "$" + String.format("%.2f", currentStock.getPrice());
            holdingData.put("stockPrice", priceDisplay);

            float totalValue = currentStock.getPrice() * holding.getSharesOwned();
            String totalValueDisplay = "$" + String.format("%.2f", totalValue);
            holdingData.put("totalValue", totalValueDisplay);

            portfolioData.add(holdingData);
        }

        model.addAttribute("portfolioData", portfolioData);
        model.addAttribute("cash", "$" + String.format("%.2f", user.getCash()));
        model.addAttribute("title", "Portfolio");
        model.addAttribute("portfolioNavClass", "active");

        return "portfolio";
    }

}
