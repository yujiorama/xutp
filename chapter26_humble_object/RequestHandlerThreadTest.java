import junit.framework.TestCase;

public class RequestHandlerThreadTest extends TestCase {
    private static final int TWO_SECONDS = 2000;
    
    void enqueRequest(Request req) throws InterruptedException {
        Framework.putRequest(req);
    }
    
    Request makeSimpleRequest() {
        return Request.create("test");
    }
    
    Response makeSimpleResponse() {
        return Response.create("test");
    }
    
    Response getResponse() throws InterruptedException {
        return Framework.takeResponse();
    }
    
    void assertResponseEquals(Response expected, Response actual) {
        assertEquals(expected.getContent(), actual.getContent());
    }
    
    public void testWasInitialized_Async() throws InterruptedException {
        // Setup
        RequestHandlerThread sut = new RequestHandlerThread();
        // Exercise
        sut.start();
        // Verify
        Thread.sleep(TWO_SECONDS);
        assertTrue(sut.initializedSuccessfully());
        sut.interrupt();
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
        sut.interrupt();
    }
    
    public void testWasInitialized_Sync() throws InterruptedException {
        // Setup
        RequestHandlerThread sut = new RequestHandlerThread();
        // Exercise
        sut.initializeThread();
        // Verify
        assertTrue(sut.initializedSuccessfully());
        sut.interrupt();
    }
    
    public void testHandleOnRequest_Sync() throws InterruptedException {
        // Setup
        RequestHandlerThread sut = new RequestHandlerThread();
        // Exercise
        Response response = sut.processOneRequest(makeSimpleRequest());
        // Verify
        assertEquals(1, sut.getNumberOfRequestsCompleted());
        assertResponseEquals(makeSimpleResponse(), response);
    }
}