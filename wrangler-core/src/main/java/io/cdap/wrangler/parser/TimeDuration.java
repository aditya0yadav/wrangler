/*
 * Copyright © 2021 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

 package io.cdap.wrangler.api.parser;

 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 /**
  * A Token implementation for time duration values (e.g., "10ms", "1.5s").
  */
 public class TimeDuration extends Token {
   private static final Pattern PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(ms|s|m|min|h|d)", Pattern.CASE_INSENSITIVE);
   
   private final double value;
   private final String unit;
   private final long nanoseconds; // Using nanoseconds as the canonical unit
 
   /**
    * Constructor that parses a time duration string.
    *
    * @param value String representation of time duration (e.g., "10ms", "1.5s")
    * @throws TokenException If the string doesn't match expected format
    */
   public TimeDuration(String value) throws TokenException {
     super(value);
     
     Matcher matcher = PATTERN.matcher(value.trim());
     if (!matcher.matches()) {
       throw new TokenException(String.format("Invalid time duration format: '%s'", value));
     }
     
     this.value = Double.parseDouble(matcher.group(1));
     this.unit = matcher.group(2).toLowerCase();
     this.nanoseconds = convertToNanoseconds(this.value, this.unit);
   }
 
   /**
    * Gets the duration in nanoseconds.
    *
    * @return Number of nanoseconds
    */
   public long getNanoseconds() {
     return nanoseconds;
   }
 
   /**
    * Gets the duration in milliseconds.
    *
    * @return Number of milliseconds
    */
   public double getMilliseconds() {
     return nanoseconds / 1_000_000.0;
   }
 
   /**
    * Gets the duration in seconds.
    *
    * @return Number of seconds
    */
   public double getSeconds() {
     return nanoseconds / 1_000_000_000.0;
   }
 
   /**
    * Gets the duration in minutes.
    *
    * @return Number of minutes
    */
   public double getMinutes() {
     return nanoseconds / (60.0 * 1_000_000_000.0);
   }
 
   /**
    * Gets the duration in hours.
    *
    * @return Number of hours
    */
   public double getHours() {
     return nanoseconds / (3600.0 * 1_000_000_000.0);
   }
 
   /**
    * Gets the duration in days.
    *
    * @return Number of days
    */
   public double getDays() {
     return nanoseconds / (24.0 * 3600.0 * 1_000_000_000.0);
   }
 
   /**
    * Gets the original numeric value before conversion.
    *
    * @return Original numeric value
    */
   public double getValue() {
     return value;
   }
 
   /**
    * Gets the original unit string.
    *
    * @return Unit string (e.g., "ms", "s")
    */
   public String getUnit() {
     return unit;
   }
 
   /**
    * Converts a value with a given unit to nanoseconds.
    *
    * @param value Numeric value
    * @param unit Unit string (e.g., "ms", "s")
    * @return Equivalent number of nanoseconds
    */
   private long convertToNanoseconds(double value, String unit) {
     switch (unit) {
       case "ns":
         return (long) value;
       case "ms":
         return (long) (value * 1_000_000);
       case "s":
         return (long) (value * 1_000_000_000);
       case "m":
       case "min":
         return (long) (value * 60 * 1_000_000_000);
       case "h":
         return (long) (value * 3600 * 1_000_000_000);
       case "d":
         return (long) (value * 24 * 3600 * 1_000_000_000);
       default:
         // This shouldn't happen due to regex validation
         return (long) value;
     }
   }
 }