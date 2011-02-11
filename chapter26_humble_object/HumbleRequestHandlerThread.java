public class HumbleRequestHandlerThread extends Thread implements Runnable {
    public RequestHandler requestHandler;
    
    public HumbleRequestHandlerThread() {
        super();
        requestHandler = new RequestHandlerImpl();
    }
    
    public void run() {
        requestHandler.initializeThread();
        try {
            processRequestsForever();
        } catch (InterruptedException ignore) {}
    }
    
    public boolean initializedSuccessfully() {
        return requestHandler.initializedSuccessfully();
    }
    
    Request nextMessage() throws InterruptedException {
        return Framework.takeRequest();
    }

    void putMsgOntoOutputQueue(Response response) throws InterruptedException {
        Framework.putResponse(response);
    }

    public void processRequestsForever() throws InterruptedException {
        Request request = nextMessage();
        do {
            Response response = requestHandler.processOneRequest(request);
            if (response != null) {
                putMsgOntoOutputQueue(response);
            }
            request = nextMessage();
        } while (request != null);
    }
}