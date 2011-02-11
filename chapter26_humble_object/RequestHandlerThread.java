public class RequestHandlerThread extends Thread {
    private boolean _initializationCompleted = false;
    private int _numberOfRequests = 0;
    
    public void run() {
        initializeThread();
        processRequestsForever();
    }
    
    public boolean initializedSuccessfully() {
        return _initializationCompleted;
    }

    public int getNumberOfRequestsCompleted() {
        return _numberOfRequests;
    }
    
    Request nextMessage() {
        return null;
    }
    
    Response processOneRequest(Request request) {
        return null;
    }
    
    void putMsgOntoOutputQueue(Response response) {
    }
    
    void initializeThread() {
        try {
        Thread.sleep(1000);
        } catch (InterruptedException ignore) {}
        _initializationCompleted = true;
    }
    
    void processRequestsForever() {
        Request request = nextMessage();
        do {
            Response response = processOneRequest(request);
            if (response != null) {
                putMsgOntoOutputQueue(response);
            }
            request = nextMessage();
        } while (request != null);
    }
}