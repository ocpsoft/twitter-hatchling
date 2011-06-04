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
package com.ocpsoft.hatchling.twitter;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.jboss.seam.cron.api.scheduling.Every;
import org.jboss.seam.cron.api.scheduling.Interval;
import org.jboss.seam.cron.api.scheduling.Trigger;

import com.ocpsoft.hatchling.domain.Tweet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@ApplicationScoped
public class TweetBuffer
{
   private final Logger log = Logger.getLogger(TweetBuffer.class);

   @Inject
   private Instance<TwitterService> service;

   private final List<Tweet> buffer = new CopyOnWriteArrayList<Tweet>();

   private boolean dirty = false;

   public void flushTweets(@Observes @Every(nth = 10, value = Interval.SECOND) final Trigger trigger)
   {
      try
      {
         if (!buffer.isEmpty())
         {
            log.info("Flushing [" + buffer.size() + "] tweets");
            service.get().saveNewTweets(buffer);
            dirty = true;
         }
      }
      finally
      {
         buffer.clear();
      }
   }

   public void updateStats(@Observes @Every(nth = 5, value = Interval.MINUTE) final Trigger trigger)
   {
      try
      {
         if (dirty)
         {
            log.info("Updating stats.");
            service.get().updateStats(new Date());
         }
      }
      finally
      {
         dirty = false;
      }
   }

   public void add(final Tweet t)
   {
      buffer.add(t);
   }

}
