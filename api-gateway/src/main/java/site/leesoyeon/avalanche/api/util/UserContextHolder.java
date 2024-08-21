package site.leesoyeon.avalanche.api.util;

import org.springframework.util.Assert;

public class UserContextHolder {
    private static final ThreadLocal<UserContext> userContext = ThreadLocal.withInitial(UserContext::new);

    public static UserContext getContext() {
        return userContext.get();
    }

    public static void setContext(UserContext context) {
        Assert.notNull(context, "Null이 아닌 UserContext 인스턴스만 허용됩니다");
        userContext.set(context);
    }
}
