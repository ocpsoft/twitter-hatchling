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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import com.ocpsoft.hatchling.config.SettingsService;
import com.ocpsoft.hatchling.domain.HatchlingConfig;
import com.ocpsoft.hatchling.domain.Keyword;
import com.ocpsoft.hatchling.twitter.TwitterService;
import com.ocpsoft.hatchling.twitter.TwitterStreamHandler;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Model
public class AdminBean
{
   private Date date;

   @Inject
   private TwitterService service;

   @Inject
   private SettingsService settings;

   @Inject
   private TwitterStreamHandler streamHandler;

   private final List<Keyword> techWords = new ArrayList<Keyword>();

   private HatchlingConfig config;

   public void init()
   {
      config = settings.getConfig();

      if (techWords.isEmpty())
      {
         List<Keyword> keywords = service.getTrackKeywords();
         for (Keyword track : keywords)
         {
            if (!techWords.contains(track.getText()))
               techWords.add(track);

            Collections.sort(techWords);
         }
      }
   }

   public String startStream()
   {
      streamHandler.stream();
      return "pretty:";
   }

   public String stopStream()
   {
      streamHandler.stop();
      return "pretty:";
   }

   public String saveConfig()
   {
      Keyword defaultLeft = config.getDefaultLeft();
      if (defaultLeft != null)
      {
         config.setDefaultLeft(techWords.get(techWords.indexOf(defaultLeft)));
      }

      Keyword defaultRight = config.getDefaultRight();
      if (defaultRight != null)
      {
         config.setDefaultRight(techWords.get(techWords.indexOf(defaultRight)));
      }

      settings.saveConfig(config);
      return "pretty:";
   }

   public String updateStats()
   {
      service.updateStats(date);
      return "pretty:";
   }

   public Date getDate()
   {
      return date;
   }

   public void setDate(final Date date)
   {
      this.date = date;
   }

   public HatchlingConfig getConfig()
   {
      return config;
   }

   public void setConfig(HatchlingConfig config)
   {
      this.config = config;
   }

   public List<Keyword> getTechWords()
   {
      return techWords;
   }
}
