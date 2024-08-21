package site.leesoyeon.avalanche.shipping.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger logger = LoggerFactory.getLogger(StartupLogger.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("===============================================");
        logger.info("*     ❄️   *    *       *     ❄️    *      ️ ❄️");
        logger.info("   *           ❄️   *     *     *        *    ");
        logger.info(" *  /\\  PROJECT AVALANCHE SERVER STARTED!   *");
        logger.info("   /  \\  *      *     ❄️     *     *         ");
        logger.info(" *  ||  ❄️   *             *      ❄️    *    *");
        logger.info("    ||    SPRING BOOT POWERED SNOW SYSTEM  *  ");
        logger.info("   /||\\    *     *    *     *    *         ❄️");
        logger.info("===============================================");
    }
}