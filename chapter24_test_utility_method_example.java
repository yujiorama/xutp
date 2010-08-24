public class Chapter24Test1 extends TestCase {
    protected Customer findActiveCustomerWithDiscount(
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
        Invoice newInvoice = new Invoice(customer);
        registerTestObject(newInvoice);
        return newInvoice;
    }
    
    List testObjects;
    protected void registerTestObject(Object testObject) {
        testObjects.add(testObject);
    }
    
    private void deleteTestObjects() {
        Iterator i = testObjects.iterator();
        while (i.hasNext()) {
            try {
                deleteObject(i.next());
            } catch (RuntimeException e) {
                // Nothing to do; we just want to make sure
                // we continue on to the next object in the list.
            }
        }
    }

    public void testAddItemQuantity_sevelhralQuantity_v1(){
        final int QUANTITY = 5;
        final BigDecimal CUSTOMER_DISCOUNT = new BigDecimal("30");
        Customer customer = null;
        Product product = null;
        Invoice invoice = null;
        try {
            // Fixture Setup
            customer =
                findActiveCustomerWithDiscount(CUSTOMER_DISCOUNT);
            product = findCurrentProductWith3DigitPrice();
            invoice = createInvoice(customer);
            // Exercise SUT
            invoice.addItemQuantity(product, QUANTITY);
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
            deleteTestObjects();
        }
    }
}