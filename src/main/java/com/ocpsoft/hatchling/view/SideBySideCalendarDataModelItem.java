/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.ocpsoft.hatchling.view;

import java.util.Date;

import org.richfaces.model.CalendarDataModelItem;

import com.ocpsoft.hatchling.util.DateUtils;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class SideBySideCalendarDataModelItem implements CalendarDataModelItem
{

   private final Date begin;
   private final Date end;
   private final Date date;
   private final int day;

   public SideBySideCalendarDataModelItem(final Date begin, final Date end, final Date date, final int day)
   {
      this.begin = begin;
      this.end = end;
      this.date = date;
      this.day = day;
   }

   @Override
   public boolean isEnabled()
   {
      return DateUtils.isSameDay(begin, date) || DateUtils.isSameDay(date, end)
               || (begin.before(date) && date.before(end));
   }

   @Override
   public boolean hasToolTip()
   {
      return false;
   }

   @Override
   public Object getToolTip()
   {
      return null;
   }

   @Override
   public String getStyleClass()
   {
      return isEnabled() ? "bold" : "disabled";
   }

   @Override
   public int getDay()
   {
      return day;
   }

   @Override
   public Object getData()
   {
      return date;
   }

}
