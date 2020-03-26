/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.swak.oss.aliyun.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;


public class DateUtil {

	private final static String TIME_ZONE = "GMT";
    private static final String RFC822_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String ISO8601_DATE_FORMAT_SMS = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String ALTERNATIVE_ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    // --------- 时间格式化 ----------
    
    public static Date parseRfc822Date(String dateString) throws ParseException {
        return getRfc822DateFormat().parse(dateString);
    }
    
    public static Date parseIso8601Date(String dateString) throws ParseException {
        try {
            return getIso8601DateFormat().parse(dateString);
        } catch (ParseException e) {
            return getAlternativeIso8601DateFormat().parse(dateString);
        }
    }

    public static String formatRfc822Date(Date date) {
        return getRfc822DateFormat().format(date);
    }
    
    public static String formatIso8601Date(Date date) {
        return getIso8601DateFormat().format(date);
    }

    public static String formatAlternativeIso8601Date(Date date) {
        return getAlternativeIso8601DateFormat().format(date);
    }
    
    public static String formatIso8601Date_sms(Date date) {
    	 return getIso8601DateFormat_sms().format(date);
    }

    // ----------- 时间格式化方式 --------------
    
    private static DateFormat getRfc822DateFormat() {
        SimpleDateFormat rfc822DateFormat = new SimpleDateFormat(RFC822_DATE_FORMAT, Locale.US);
        rfc822DateFormat.setTimeZone(new SimpleTimeZone(0, TIME_ZONE));

        return rfc822DateFormat;
    }
    
    private static DateFormat getIso8601DateFormat() {
        SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT, Locale.US);
        df.setTimeZone(new SimpleTimeZone(0, TIME_ZONE));
        return df;
    }
    
    private static DateFormat getIso8601DateFormat_sms() {
        SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT_SMS, Locale.US);
        df.setTimeZone(new SimpleTimeZone(0, TIME_ZONE));
        return df;
    }

    private static DateFormat getAlternativeIso8601DateFormat() {
        SimpleDateFormat df = new SimpleDateFormat(ALTERNATIVE_ISO8601_DATE_FORMAT, Locale.US);
        df.setTimeZone(new SimpleTimeZone(0, TIME_ZONE));
        return df;
    }
}
