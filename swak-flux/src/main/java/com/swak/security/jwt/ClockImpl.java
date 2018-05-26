package com.swak.security.jwt;

import java.util.Date;

import com.swak.security.jwt.interfaces.Clock;

final class ClockImpl implements Clock {

    ClockImpl() {}

    @Override
    public Date getToday() {
        return new Date();
    }
}
