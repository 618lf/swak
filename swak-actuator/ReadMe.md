# 系统监控
/actuator/beans      -- 系统实例化的bean以及其依赖关系
/actuator/heapDump   -- 
/actuator/threadDump -- 
/actuator/env/environment
/actuator/env/environmentEntry/{toMatch}
/actuator/loggers
/actuator/loggerLevels/{name}
/actuator/configureLogLevel/{name}?configuredLevel=DEBUG
/actuator/metrics/listNames
/actuator/metrics/metric/{requiredMetricName}
/actuator/sessions/getSession/{sessionId}
/actuator/sessions/deleteSession/{sessionId}
/actuator/mappings
/actuator/vertx/metricsNames -- 对 vertx 的监控