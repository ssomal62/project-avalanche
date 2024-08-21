package site.leesoyeon.avalanche.api.util;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class UserContext {
    public static final String CORRELATION_ID  = "correlation-id";
    public static final String AUTH_TOKEN      = "Authorization";
    public static final String USER_ID         = "User-Id";
    public static final String CLIENT_ID         = "Client-Id";

    private static final ThreadLocal<String> correlationId = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> authToken = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> userId = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> clientId = ThreadLocal.withInitial(() -> null);

    public String getCorrelationId() { return correlationId.get(); }
    public void setCorrelationId(String cid) { correlationId.set(cid); }

    public String getAuthToken() {return authToken.get(); }
    public void setAuthToken(String aToken) {authToken.set(aToken); }

    public String getUserId() { return userId.get(); }
    public void setUserId(String aUser) { userId.set(aUser); }

    public String getClientId() { return clientId.get(); }
    public void setClientId(String aClient) { clientId.set(aClient); }

    public HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CORRELATION_ID, getCorrelationId());
        return httpHeaders;
    }
}

