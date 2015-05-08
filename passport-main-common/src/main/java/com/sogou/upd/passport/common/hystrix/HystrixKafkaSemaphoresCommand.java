package com.sogou.upd.passport.common.hystrix;


import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.sogou.upd.passport.common.HystrixConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-4-9
 * Time: 下午3:22
 * To change this template use File | Settings | File Templates.
 */
public class HystrixKafkaSemaphoresCommand extends HystrixCommand<Void> {

    private String infoToLog;

    private static final Logger logger = LoggerFactory.getLogger("hystrixLogger");
    private static final Logger kafkaLogger = LoggerFactory.getLogger("userLoggerKafka");

    private static boolean requestCacheEnable = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_REQUEST_CACHE_ENABLED));
    private static boolean requestLogEnable = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_REQUEST_LOG_ENABLED));
    private static boolean breakerForceOpen = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_BREAKER_FORCE_OPEN));
    private static boolean breakerForceClose = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_BREAKER_FORCE_CLOSE));
    private static int errorThresholdPercentage = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_ERROR_THRESHOLD_PERCENTAGE));
    private static final int kafkaTimeout = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_KAFKA_TIMEOUT));
    private static final int kafkaRequestVolumeThreshold = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_KAFKA_REQUESTVOLUME_THRESHOLD));
    private static final int kafkaSemaphoreThreshold = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_KAFKA_SEMAPHORE_THRESHOLD));
    private static final int fallbackSemaphoreThreshold = Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_FALLBACK_SEMAPHORE_THRESHOLD));

    public HystrixKafkaSemaphoresCommand(String infoToLog) {

        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("HystrixKafka"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("KafkaHystrixSemaphoresCommand"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE) //信号隔离
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(kafkaSemaphoreThreshold)
                        .withRequestCacheEnabled(requestCacheEnable)
                        .withRequestLogEnabled(requestLogEnable)
                        .withCircuitBreakerForceOpen(breakerForceOpen)
                        .withCircuitBreakerForceClosed(breakerForceClose)
                        .withCircuitBreakerErrorThresholdPercentage(errorThresholdPercentage)
                        .withCircuitBreakerRequestVolumeThreshold(kafkaRequestVolumeThreshold)
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(fallbackSemaphoreThreshold)));

        this.infoToLog = infoToLog;

    }


    @Override
    protected Void run() throws Exception {
        kafkaLogger.info(infoToLog);
        return null;
    }

    @Override
    protected Void getFallback() {
        boolean isShortCircuited = isResponseShortCircuited();
        boolean isRejected = isResponseRejected();
        boolean isTimeout = isResponseTimedOut();
//        boolean isFailed = isFailedExecution();
        if (isTimeout) {
            logger.error("HystrixKafkaSemaphoresCommand fallback isTimeout");
        } else if (isRejected) {
            logger.error("HystrixKafkaSemaphoresCommand fallback isRejected");
        } else if (isShortCircuited) {
            logger.error("HystrixKafkaSemaphoresCommand fallback isShortCircuited");
        } else {
//            logger.error("HystrixKafkaSemaphoresCommand fallback unknown");
        }
        return null;
    }

}
