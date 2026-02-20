package com.lumen.inventory.integration.account;

import com.lumen.account.management.service.AccountManagementService;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Adapter for AccountManagementService interactions.
 */
@Component
public class AccountManagementAdapter {
    private final AccountManagementService accountManagementService;

    public AccountManagementAdapter(AccountManagementService accountManagementService) {
        this.accountManagementService = accountManagementService;
    }

    /**
     * Calls AccountManagementService to get BAN mapping.
     * @param ban Billing Account Number
     * @return "invoiceDisplayNumber|customerNumber" or "|" if not found
     */
    public String getBanMapping(String ban) {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-customer-number", ban);
        var response = accountManagementService.getBillingAccounts(
                headers,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
        if (response != null && response.getBillingAccounts() != null && !response.getBillingAccounts().isEmpty()) {
            var billingAccount = response.getBillingAccounts().get(0);
            String invoiceDisplayNumber = billingAccount.getId();
            String customerNumber = response.getCustomerNumber();
            return (invoiceDisplayNumber != null ? invoiceDisplayNumber : "") + "|" + (customerNumber != null ? customerNumber : "");
        }
        return "|";
    }
}
