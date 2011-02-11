public class Response {
    private final String content;
    
    Response(String content) {
        super();
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
    
    public static Response create(String content) {
        return new Response(content);
    }
}
