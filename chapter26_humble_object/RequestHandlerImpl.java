public class RequestHandlerImpl implements RequestHandler {
    private boolean _initializationCompleted = false;
    private int _numberOfRequests = 0;

    public boolean initializedSuccessfully() {
        return _initializationCompleted;
    }

    public int getNumberOfRequestsCompleted() {
        return _numberOfRequests;
    }

    public Response processOneRequest(Request request) {
        _numberOfRequests++;
        //                 System.out.println("number of requests:" + getNumberOfRequestsCompleted());
        return Response.create(request.getContent());
    }

    public void initializeThread() {
        try {
            Thread.sleep(1000);
            _initializationCompleted = true;
        } catch (InterruptedException ignore) {}
    }
}
