import junit.framework.TestCase;

public class RequestHandlerThreadTest extends TestCase {
    private static final int TWO_SECONDS = 3000;
    
    void enqueRequest(Request req) {
    }
    
    Request makeSimpleRequest() {
        return null;
    }
    
    Response makeSimpleResponse() {
        return null;
    }
    
    Response getResponse() {
        return null;
    }
    
    void assertResponseEquals(Response expected, Response actual) {
        assertEquals(expected, actual);
    }
    
    public void testWasInitialized_Async() throws InterruptedException {
        // Setup
        RequestHandlerThread sut = new RequestHandlerThread();
        // Exercise
        sut.start();
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