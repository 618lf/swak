package com.swak.jwt;

import java.util.Date;

import com.swak.jwt.interfaces.Clock;

final class ClockImpl implements Clock {

    ClockImpl() {}

    @Override
    public Date getToday() {
        return new Date();
    }
}
