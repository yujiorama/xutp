public class RequestHandlerThread extends Thread {
    private boolean _initializationCompleted = false;
    private int _numberOfRequests = 0;
    
    public void run() {
        initializeThread();
        try {
            processRequestsForever();
        } catch (InterruptedException ignore) {}
    }
    
    public boolean initializedSuccessfully() {
        return _initializationCompleted;
    }

    public int getNumberOfRequestsCompleted() {
        return _numberOfRequests;
    }
    
    Request nextMessage() throws InterruptedException {
        return Framework.takeRequest();
    }
    
    private Response processOneRequest(Request request) {
        return Response.create(request.getContent());
    }
    
    void putMsgOntoOutputQueue(Response response) throws InterruptedException {
        Framework.putResponse(response);
    }
    
    private void initializeThread() {
        try {
            Thread.sleep(1000);
            _initializationCompleted = true;
        } catch (InterruptedException ignore) {}
    }
    
    void processRequestsForever() throws InterruptedException {
        Request request = nextMessage();
        do {
            Response response = processOneRequest(request);
            if (response != null) {
                _numberOfRequests++;
//                 System.out.println("number of requests:" + getNumberOfRequestsCompleted());
                putMsgOntoOutputQueue(response);
            }
            request = nextMessage();
        } while (request != null);
    }
}