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
  * A Token implementation for byte size values (e.g., "10KB", "1.5MB").
  */
 public class ByteSize extends Token {
   private static final Pattern PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(B|KB|MB|GB|TB|PB)", Pattern.CASE_INSENSITIVE);
   
   private final double value;
   private final String unit;
   private final long bytes;
 
   /**
    * Constructor that parses a byte size string.
    *
    * @param value String representation of byte size (e.g., "10KB", "1.5MB")
    * @throws TokenException If the string doesn't match expected format
    */
   public ByteSize(String value) throws TokenException {
     super(value);
     
     Matcher matcher = PATTERN.matcher(value.trim());
     if (!matcher.matches()) {
       throw new TokenException(String.format("Invalid byte size format: '%s'", value));
     }
     
     this.value = Double.parseDouble(matcher.group(1));
     this.unit = matcher.group(2).toUpperCase();
     this.bytes = convertToBytes(this.value, this.unit);
   }
 
   /**
    * Converts the value to canonical bytes.
    *
    * @return Number of bytes
    */
   public long getBytes() {
     return bytes;
   }
 
   /**
    * Gets the value in kilobytes.
    *
    * @return Number of kilobytes
    */
   public double getKilobytes() {
     return bytes / 1024.0;
   }
 
   /**
    * Gets the value in megabytes.
    *
    * @return Number of megabytes
    */
   public double getMegabytes() {
     return bytes / (1024.0 * 1024.0);
   }
 
   /**
    * Gets the value in gigabytes.
    *
    * @return Number of gigabytes
    */
   public double getGigabytes() {
     return bytes / (1024.0 * 1024.0 * 1024.0);
   }
 
   /**
    * Gets the value in terabytes.
    *
    * @return Number of terabytes
    */
   public double getTerabytes() {
     return bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0);
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
    * @return Unit string (e.g., "KB", "MB")
    */
   public String getUnit() {
     return unit;
   }
 
   /**
    * Converts a value with a given unit to bytes.
    *
    * @param value Numeric value
    * @param unit Unit string (e.g., "KB", "MB")
    * @return Equivalent number of bytes
    */
   private long convertToBytes(double value, String unit) {
     switch (unit) {
       case "B":
         return (long) value;
       case "KB":
         return (long) (value * 1024);
       case "MB":
         return (long) (value * 1024 * 1024);
       case "GB":
         return (long) (value * 1024 * 1024 * 1024);
       case "TB":
         return (long) (value * 1024 * 1024 * 1024 * 1024);
       case "PB":
         return (long) (value * 1024 * 1024 * 1024 * 1024 * 1024);
       default:
         // This shouldn't happen due to regex validation
         return (long) value;
     }
   }
 }