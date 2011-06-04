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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.richfaces.model.CalendarDataModel;
import org.richfaces.model.CalendarDataModelItem;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class SideBySideCalendarModel implements CalendarDataModel
{
   private final Date begin;
   private final Date end;

   public SideBySideCalendarModel(final Date begin, final Date end)
   {
      this.begin = begin;
      this.end = end;
   }

   @Override
   public CalendarDataModelItem[] getData(final Date[] dateArray)
   {
      List<CalendarDataModelItem> result = new ArrayList<CalendarDataModelItem>();

      int day = 0;
      for (final Date date : dateArray)
      {
         result.add(new SideBySideCalendarDataModelItem(begin, end, date, day++));
      }

      return result.toArray(new CalendarDataModelItem[result.size()]);
   }

   @Override
   public Object getToolTip(final Date date)
   {
      return null;
   }
}
