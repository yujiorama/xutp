public class Request {
    private final String content;

    Request(String content) {
        super();
        this.content = content;
    }

    public String getContent() {
        return content;
    }
    
    public static Request create(String content) {
        return new Request(content);
    }
}
