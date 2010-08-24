public class Chapter24Test1 extends TestCase {
    proteted Customer findActiveCustomerWithDiscount(
                                                     BigDecimal percentDiscount) {
        Address billingAddress = new Address("1222 1st St SW",
                                     "Calgary", "Alberta",
                                     "T2N 2V2", "Canada");
        Address shippingAddress = new Address("1333 1st St SW",
                                      "Calgary", "Alberta",
                                      "T2N 2V2", "Canada");
        return new Customer( 99, "John", "Doe",
                             percentDiscount,
                             billingAddress,
                             shippingAddress);
    }
    
    protected Product findCurrentProductWith3DigitPrice() {
        return new Product( 88, "SomeWidget",
                            new BigDecimal("19.99"));
    }
    
    protected Invoice createInvoice(Customer customer) {
        return new Invoice(customer);
    }

    public void testAddItemQuantity_sevelhralQuantity_v1(){
        final BigDecimal CUSTOMER_DISCOUNT = new BigDecimal("30");
        Customer customer = null;
        Product product = null;
        Invoice invoice = null;
        try {
            // Fixture Setup
            customer =
                findActiveCustomerWithDiscount(CUSTOMER_DISCOUNT);
            product = findCurrentProductWith3DigitPrice();
            invoice = new Invoice( customer );
            // Exercise SUT
            invoice.addItemQuantity( product, 5 );
            // Verify Outcome
            List lineItems = invoice.getLineItems();
            if (lineItems.size() == 1) {
                LineItem actItem = (LineItem) lineItem.get(0);
                assertEquals("inv", invoice, actItem.getInv());
                assertEquals("prod", product, actItem.getProd());
                assertEquals("quant", 5, actItem.getQuantity());
                assertEquals("discount",
                             new BigDecimal("30"),
                             actItem.getPercentDiscount());
                assertEquals("unit price",
                             new BigDecimal("19.99"),
                             actItem.getUnitPrice());
                assertEquals("extended",
                             new BigDecimal("69.96"),
                             actItem.getExtendedPrice());
            } else {
                assertTrue("Invoice should have 1 item", false);
            }
        } finally {
            // Teardown
            deleteObject(invoice);
            deleteObject(product);
            deleteObject(customer);
            deleteObject(billingAddress);
            deleteObject(shippingAddress);
        }
    }
}