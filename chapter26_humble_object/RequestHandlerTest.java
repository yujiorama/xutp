import junit.framework.TestCase;

public class RequestHandlerTest extends TestCase {
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
    
    public void testNotInitialized_Sync() {
        // Setup/Exercise
        RequestHandler sut = new RequestHandlerImpl();
        // Verify
        assertFalse("init", sut.initializedSuccessfully());
    }
    
    public void testWasInitialized_Sync() throws InterruptedException {
        // Setup
        RequestHandler sut = new RequestHandlerImpl();
        // Exercise
        sut.initializeThread();
        // Verify
        assertTrue(sut.initializedSuccessfully());
    }
    
    public void testHandleOnRequest_Sync() throws InterruptedException {
        // Setup
        RequestHandler sut = new RequestHandlerImpl();
        // Exercise
        Response response = sut.processOneRequest(makeSimpleRequest());
        // Verify
        assertEquals(1, sut.getNumberOfRequestsCompleted());
        assertResponseEquals(makeSimpleResponse(), response);
    }
    
    public void testConstroctor() {
        // Exercise
        HumbleRequestHandlerThread sut = new HumbleRequestHandlerThread();
        // Verify
        String actualDelegateClass = sut.requestHandler.getClass().getName();
        assertEquals(RequestHandlerImpl.class.getName(), actualDelegateClass);
    }
    
    static class RequestHandlerRecordingStub implements RequestHandler {
        public boolean initializedSuccessfully() {
            return true;
        }
        public int getNumberOfRequestsCompleted() {
            return 1;
        }
        public Response processOneRequest(Request request) {
            return null;
        }
        public void initializeThread() {
        }
    }
    
    public void testLogicCalled_Sync() throws InterruptedException {
        // Setup
        RequestHandlerRecordingStub mockHandler = new RequestHandlerRecordingStub();
        HumbleRequestHandlerThread sut = new HumbleRequestHandlerThread();
        // Mock Installation
        sut.setHandler(mockHandler);
        sut.start();
        // Exercise
        enqueRequest(makeSimpleRequest());
        // Verify
        Thread.sleep(TWO_SECONDS);
        assertTrue("init", mockHandler.initializedSuccessfully());
        assertEquals(1, mockHandler.getNumberOfRequestsCompleted());
    }
}