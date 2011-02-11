import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

public class Framework {

    public static void putRequest(Request r) throws InterruptedException {
        Holder.INSTANCE.put(r);
    }
    
    public static Request takeRequest() throws InterruptedException {
        Request r = null;
        return Holder.INSTANCE.take(r);
    }

    public static void putResponse(Response r) throws InterruptedException {
        Holder.INSTANCE.put(r);
    }
    
    public static Response takeResponse() throws InterruptedException {
        Response r = null;
        return Holder.INSTANCE.take(r);
    }

    private static class Holder {
        private static final Framework INSTANCE = new Framework();
    }
    
    private final BlockingQueue<Request> requestQueue;
    private final BlockingQueue<Response> responseQueue;

    private Framework() {
        super();
        requestQueue = new ArrayBlockingQueue<Request>(10);
        responseQueue = new ArrayBlockingQueue<Response>(10);
    }
    
    void put(Request req) throws InterruptedException {
//         System.out.println(Thread.currentThread().getId() + ": put request");
        requestQueue.put(req);
    }
    void put(Response res) throws InterruptedException {
//         System.out.println(Thread.currentThread().getId() + ": put response");
        responseQueue.put(res);
    }
    Request take(Request req) throws InterruptedException {
//         System.out.println(Thread.currentThread().getId() + ": take request");
        return requestQueue.take();
    }
    Response take(Response res) throws InterruptedException {
//         System.out.println(Thread.currentThread().getId() + ": take response");
        return responseQueue.take();
    }
}
