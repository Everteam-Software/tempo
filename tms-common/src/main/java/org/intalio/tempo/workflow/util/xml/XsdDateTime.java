/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.intalio.tempo.workflow.util.xml;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Calendar capable for parsing/outputing xsd:dateTime strings, of form CCYY-MM-DDThh:mm:ss.ss
 */
public class XsdDateTime extends GregorianCalendar {

    private static final long serialVersionUID = 4394008655499855392L;

    static final int YEAR_MG = 1;

    static final int MONTH_MG = 2;

    static final int DAY_MG = 3;

    static final int HOUR_MG = 4;

    static final int MIN_MG = 5;

    static final int SEC_MG = 6;

    static final int TZ_MG = 7;

    static final int TZ_SIGN = 8;

    static final int TZ_HOUR_MG = 9;

    static final int TZ_MIN_MG = 10;

    static final Pattern PATTERN = Pattern
            .compile("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2}\\.?\\d*)(Z|(\\+|-)(\\d{2}):(\\d{2}))");

    public XsdDateTime(long time) {
        setTimeInMillis(time);
    }

    public XsdDateTime(Date date) {
        setTimeInMillis(date.getTime());
    }

    public XsdDateTime(String dateTime) {
        Matcher m = PATTERN.matcher(dateTime);
        if (m.matches()) {
            set(Integer.valueOf(m.group(YEAR_MG)), 
            //the following line has been modified by JAG to remove 1 as 
            //the month shown in the UI is one month later than it should be\
            //See bug WF-619
            Integer.valueOf(m.group(MONTH_MG))-1,
                    Integer.valueOf(m.group(DAY_MG)), Integer.valueOf(m.group(HOUR_MG)), Integer.valueOf(m
                            .group(MIN_MG)));
            BigDecimal sec = new BigDecimal(m.group(SEC_MG));
            set(SECOND, sec.intValue());
            BigDecimal fraction = sec.subtract(sec.setScale(0, BigDecimal.ROUND_DOWN));
            int millisec = fraction.movePointRight(3).intValue();
            set(Calendar.MILLISECOND, millisec);

            String tz = m.group(TZ_MG);
            if ("Z".equals(tz)) {
                setTimeZone(TimeZone.getTimeZone("GMT"));
            } else {
                int hr = Integer.valueOf(m.group(TZ_HOUR_MG));
                int min = Integer.valueOf(m.group(TZ_MIN_MG));
                boolean plus = "+".equals(m.group(TZ_SIGN));
                setTimeZone(new SimpleTimeZone((hr * 60 + min) * (plus ? 1 : -1) * 60 * 1000, ""));
            }
        } else {
            throw new IllegalArgumentException("Bad dateTime: " + dateTime);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(Integer.toString(get(Calendar.YEAR)));
        sb.append('-');
        //the following line has been modified by JAG to add 1 as 
        //the month shown in the UI is one month later than it should be
        //See bug WF-619
        formatNumber(sb, get(Calendar.MONTH)+1, 2);
        sb.append('-');
        formatNumber(sb, get(Calendar.DAY_OF_MONTH), 2);
        sb.append('T');
        formatNumber(sb, get(Calendar.HOUR_OF_DAY), 2);
        sb.append(':');
        formatNumber(sb, get(Calendar.MINUTE), 2);
        sb.append(':');
        formatNumber(sb, get(Calendar.SECOND), 2);
        int ms = get(Calendar.MILLISECOND);
        if (ms > 0) {
            String str = Double.toString(ms / 1000d);
            for (int i = 0; i < str.length(); ++i) {
                if (str.charAt(0) == '.')
                    break;
                str = str.substring(1);
            }
            sb.append(str);
        }

        int offsetInMinutes = (get(Calendar.ZONE_OFFSET) + get(Calendar.DST_OFFSET)) / (60 * 1000);
        if (offsetInMinutes == 0) {
            sb.append('Z');
        } else {
            if (offsetInMinutes < 0) {
                sb.append('-');
                offsetInMinutes *= -1;
            } else {
                sb.append('+');
            }
            formatNumber(sb, offsetInMinutes / 60, 2);
            sb.append(':');
            formatNumber(sb, offsetInMinutes % 60, 2);
        }
        return sb.toString();
    }

    private static void formatNumber(StringBuffer sb, int num, int digits) {
        String s = Integer.toString(num);
        for (int i = s.length(); i < digits; ++i) {
            sb.append('0');
        }
        sb.append(s);
    }

}
