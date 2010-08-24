public class Chapter24Test1 extends TestCase {
    proteted Customer findActiveCustomerWithDiscount(
                                                     BigDecimal percentDiscount) {
        return new Customer( 99, "John", "Doe",
                             percentDiscount,
                             billingAddress,
                             shippingAddress);
    }

    public void testAddItemQuantity_sevelhralQuantity_v1(){
        Address billingAddress = null;
        Address shippingAddress = null;
        Customer customer = null;
        Product product = null;
        Invoice invoice = null;
        try {
            // Fixture Setup
            billingAddress = new Address("1222 1st St SW",
                                         "Calgary", "Alberta",
                                         "T2N 2V2", "Canada");
            shippingAddress = new Address("1333 1st St SW",
                                          "Calgary", "Alberta",
                                          "T2N 2V2", "Canada");
            customer = new Customer( 99, "John", "Doe",
                                     new BigDecimal("30"),
                                     billingAddress,
                                     shippingAddress);
            product = new Product( 88, "SomeWidget",
                                   new BigDecimal("19.99"));
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