publci interface RequestHandler {
    public boolean initializedSuccessfully();
    public int getNumberOfRequestsCompleted();
    public Response processOneRequest(Request request);
    public void initializeThread();
}
