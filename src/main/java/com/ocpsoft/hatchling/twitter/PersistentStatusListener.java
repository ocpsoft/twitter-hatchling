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

import org.jboss.logging.Logger;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.URLEntity;

import com.ocpsoft.hatchling.domain.Tweet;
import com.ocpsoft.hatchling.domain.TweetURL;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PersistentStatusListener implements StatusListener
{
   private static final long serialVersionUID = -5644560161264580993L;
   Logger log = Logger.getLogger(PersistentStatusListener.class);
   private final TweetBuffer buffer;

   public PersistentStatusListener(final TweetBuffer buffer)
   {
      this.buffer = buffer;
   }

   @Override
   public void onStatus(final Status status)
   {
      Tweet t = new Tweet();
      t.setReceived(status.getCreatedAt());
      t.setText(status.getText());
      t.setScreenName(status.getUser().getScreenName());
      t.setUserName(status.getUser().getName());
      t.setUserProfileImageURL(status.getUser().getProfileImageURL());
      t.setProfileURL(null);
      t.setTweetId(status.getId());

      URLEntity[] urlEntities = status.getURLEntities();
      if (urlEntities != null)
      {
         for (URLEntity url : urlEntities)
         {
            TweetURL tweetURL = new TweetURL();
            if (url.getExpandedURL() != null)
            {
               tweetURL.setURL(url.getExpandedURL());
            }
            else if (url.getURL() != null)
            {
               tweetURL.setURL(url.getURL());
            }
            t.getURLs().add(tweetURL);
         }
      }

      buffer.add(t);
   }

   @Override
   public void onDeletionNotice(final StatusDeletionNotice statusDeletionNotice)
   {
   }

   @Override
   public void onTrackLimitationNotice(final int numberOfLimitedStatuses)
   {
   }

   @Override
   public void onException(final Exception ex)
   {
      ex.printStackTrace();
   }

   @Override
   public void onScrubGeo(final long arg0, final long arg1)
   {
   }

   @Override
   public void onStallWarning(StallWarning arg0)
   {
      // TODO Auto-generated method stub
      
   }
}