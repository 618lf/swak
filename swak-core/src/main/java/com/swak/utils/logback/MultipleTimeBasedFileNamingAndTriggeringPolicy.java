package com.swak.utils.logback;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;

/**
 * 自定义时间间隔
 *
 * @author: lifeng
 * @date: 2020/3/29 13:40
 */
@NoAutoStart
public class MultipleTimeBasedFileNamingAndTriggeringPolicy<E>
        extends DefaultTimeBasedFileNamingAndTriggeringPolicy<E> {

    /**
     * 这个用来指定时间间隔
     */
    private Integer multiple = 1;

    @Override
    protected void computeNextCheck() {
        nextCheck = rc.getEndOfNextNthPeriod(dateInCurrentPeriod, multiple).getTime();
    }

    public Integer getMultiple() {
        return multiple;
    }

    public void setMultiple(Integer multiple) {
        if (multiple > 1) {
            this.multiple = multiple;
        }
    }
}
