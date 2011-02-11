public class RequestHandlerThreadTest extends TestCase {
    private static final int TWO_SECONDS = 3000;
    
    public void testWasInitialized_Async() throws InterruptedException {
        // Setup
        RequestHandlerThread sut = new RequestHandlerThread();
        // Exercise
        sut.star();
        // Verify
        Thread.sleep(TWO_SECONDS);
        assertTrue(sut.initializedSuccessfully());
    }
    
    public void testHandleOnRequest_Async() throws InterruptedException {
        // Setup
        RequestHandlerThread sut = new RequestHandlerThread();
        sut.start();
        // Exercise
        enqueRequest(makeSimpleRequest());
        // Verify
        Thread.sleep(TWO_SECONDS);
        assertEquals(1, sut.getNumberOfRequestsCompleted());
        assertResponseEquals(makeSimpleResponse(), getResponse());
    }
}