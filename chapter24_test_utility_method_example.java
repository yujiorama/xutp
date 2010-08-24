public class Chapter24Test1 extends TestCase {
    
    public void tearDown() {
        deleteTestObjects();
    }

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
    
    protected LineItem createLineItem(int quantity, BigDecimal discountPercentage,
                                      BigDecimal extendedPrice, Product product, Invoice invoice) {
        return new LineItem(quantity, discountPercentage, extendedPrice, product.getName(), invoice.getName());
    }
    
    protected void assertLineItemsEqual(String message, LineItem expected, LineItem actual) {
        // assertMessage(message)
        assertEquals("inv", expected.getInv(), actual.getInv());
        assertEquals("prod", expected.getProd(), actual.getProd());
        assertEquals("quant", expected.getQuantity(), actual.getQuantity());
        assertEquals("discount",
                     expected.getPercentDiscount(),
                     actual.getPercentDiscount());
        assertEquals("unit price",
                     expected.getUnitPrice(),
                     actual.getUnitPrice());
        assertEquals("extended",
                     expected.getExtendedPrice(),
                     actual.getExtendedPrice());
    }
    
    protected void assertContainsExactlyOneLineItem(Invoice invoice,
                                                    LineItem expected) {
        List lineItems = invoice.getLineItems();
        assertEquals("number of items", lineItems.size(), 1);
        LineItem actItem = (LineItem) lineItem.get(0);
        assertLineItemsEqual("", expected, actItem);
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
            final BigDecimal BASE_PRICE = product.getUnitPrice().
                multiply(new BigDecimal(QUANTITY));
            final BigDecimal EXTENDED_PRICE =
                BASE_PRICE.subtract(BASE_PRICE.multiply(
                                 CUSTOMER_DISCOUNT.movePointLeft(2)));
            LineItem expected =
                createLineItem(QUANTITY, CUSTOMER_DISCOUNT,
                               EXTENDED_PRICE, product, invoice);
            List lineItems = invoice.getLineItems();
            if (lineItems.size() == 1) {
                LineItem actItem = (LineItem) lineItem.get(0);
                assertLineItemsEqual("", expected, actItem);
            } else {
                assertTrue("Invoice should have 1 item", false);
            }
        } finally {
            // Teardown
        }
    }
}