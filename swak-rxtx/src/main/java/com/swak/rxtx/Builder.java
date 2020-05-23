package com.swak.rxtx;

import com.swak.rxtx.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.Consumer;

/**
 * 构建起器
 *
 * @author lifeng
 */
@Getter
@Setter
@Accessors(chain = true)
public class Builder {
    private int works;
    private int heartbeatSeconds;
    private Consumer<Channel> channelInit;

    @SuppressWarnings("unchecked")
    public <T> T as() {
        return (T) this;
    }

    public Channels build() {
        return new Channels(works, heartbeatSeconds, channelInit);
    }
}

