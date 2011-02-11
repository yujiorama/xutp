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
    
    void processRequestsForever() {
        Request request = nextMessage();
        do {
            Response resonse = processOneRequest(request);
            if (response != null) {
                putMsgOntoOutputQueue(response);
            }
            request = nextMessage();
        } while (request != null);
    }
}