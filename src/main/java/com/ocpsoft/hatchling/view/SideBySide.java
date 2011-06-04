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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.hatchling.config.SettingsService;
import com.ocpsoft.hatchling.domain.DayStats;
import com.ocpsoft.hatchling.domain.DayStatsKeyword;
import com.ocpsoft.hatchling.domain.HatchlingConfig;
import com.ocpsoft.hatchling.domain.Keyword;
import com.ocpsoft.hatchling.domain.Tweet;
import com.ocpsoft.hatchling.twitter.TwitterService;
import com.ocpsoft.hatchling.util.DateUtils;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Named
@SessionScoped
public class SideBySide implements Serializable
{
   private static final long serialVersionUID = 6180121970302855698L;

   @Inject
   private TwitterService twitterService;

   @Inject
   private SettingsService settings;

   private Date lastBegin;
   private Date lastEnd;

   private List<Keyword> techWords = new ArrayList<Keyword>();
   private Keyword left;
   private Keyword right;
   private Keyword lastLeft;
   private Keyword lastRight;

   private Date begin;
   private Date end;

   private final int sampleSize = 5;
   private List<Tweet> leftTweets = new ArrayList<Tweet>();
   private List<Tweet> rightTweets = new ArrayList<Tweet>();

   private Map<Date, Long> leftTweetMap = new HashMap<Date, Long>();

   private Map<Date, Long> rightTweetMap = new HashMap<Date, Long>();

   private List<Date> range;

   public void leftValueChanged(final ValueChangeEvent event) throws AbortProcessingException
   {
   }

   public void rightValueChanged(final ValueChangeEvent event) throws AbortProcessingException
   {
   }

   /*
    * Called by prettyfaces when viewing stats
    */
   public void init()
   {
      if (techWords.isEmpty())
      {
         techWords = twitterService.getTrackKeywords();
         Collections.sort(techWords);
      }

      if (!techWords.isEmpty())
      {
         if (left == null || right == null)
         {
            HatchlingConfig config = settings.getConfig();
            if (left == null)
            {
               Keyword defaultLeft = config.getDefaultLeft();
               if (defaultLeft != null && techWords.contains(defaultLeft))
               {
                  left = defaultLeft;
               }
               else
               {
                  left = techWords.get(0);
               }
            }
            if (right == null)
            {
               Keyword defaultRight = config.getDefaultRight();
               if (defaultRight != null && techWords.contains(defaultRight))
               {
                  right = defaultRight;
               }
               else
               {
                  right = techWords.get(0);
                  if (techWords.size() > 1)
                  {
                     right = techWords.get(1);
                  }
               }
            }
         }
      }

      if ((begin == null) || (end == null))
      {
         end = DateUtils.truncate(new Date(), Calendar.DATE);
         begin = DateUtils.addDays(end, -7);
      }

      updateChartData();
   }

   public List<Keyword> getTechWords()
   {
      return techWords;
   }

   public void setTechWords(final List<Keyword> techWords)
   {
      this.techWords = techWords;
   }

   public Keyword getLeft()
   {
      return left;
   }

   public void setLeft(final Keyword left)
   {
      this.left = left;
      updateChartData();
   }

   public Keyword getRight()
   {
      return right;
   }

   public String getLeftLink()
   {
      return left == null ? "" : left.getText().replaceAll("#", "%23");
   }

   public String getRightLink()
   {
      return right == null ? "" : right.getText().replaceAll("#", "%23");
   }

   public void setRight(final Keyword right)
   {
      this.right = right;
      updateChartData();
   }

   public Date getBegin()
   {
      return begin;
   }

   public void setBegin(final Date begin)
   {
      this.begin = begin;
      updateChartData();
   }

   public Date getEnd()
   {
      return end;
   }

   public void setEnd(final Date end)
   {
      this.end = end;
      updateChartData();
   }

   private void updateChartData()
   {

      boolean updateLeft = false;
      boolean updateRight = false;
      if ((lastLeft == null) || (lastRight == null)
               || !(DateUtils.isSameDay(lastBegin, begin) && DateUtils.isSameDay(lastEnd, end)))
      {
         updateLeft = true;
         updateRight = true;
      }

      if (!left.equals(lastLeft))
         updateLeft = true;
      if (!right.equals(lastRight))
         updateRight = true;

      if (updateLeft || updateRight)
      {
         List<DayStats> stats = twitterService.getStats(begin, end);
         range = getDateRange();

         if (updateLeft)
         {
            leftTweetMap = new HashMap<Date, Long>();
            for (Date date : range)
            {
               leftTweetMap.put(date, 0l);
            }

            leftTweets = twitterService.getSampleTweets(Arrays.asList(left), begin, end, 0, 10);
            countTweets(left, leftTweetMap, stats);
         }

         if (updateRight)
         {
            rightTweetMap = new HashMap<Date, Long>();
            for (Date date : range)
            {
               rightTweetMap.put(date, 0l);
            }

            rightTweets = twitterService.getSampleTweets(Arrays.asList(right), begin, end, 0, 10);
            countTweets(right, rightTweetMap, stats);
         }
      }

      // update cache triggers
      lastLeft = left;
      lastRight = right;

      lastBegin = new Date(begin.getTime());
      lastEnd = new Date(end.getTime());
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public Object getChartData()
   {
      Map data = new HashMap();

      List rows = new ArrayList();
      SimpleDateFormat format = new SimpleDateFormat("MMM d");

      for (Date date : range)
      {
         rows.add(new Object[] { format.format(date), leftTweetMap.get(date), rightTweetMap.get(date) });
      }

      int increment = Math.round(range.size() / 5);
      if ((increment == 1) && (range.size() < 10))
      {
         increment = 2;
      }

      data.put("rows", rows);
      data.put("left", left.getText());
      data.put("right", right.getText());
      data.put("showTextEvery", increment);
      return data;
   }

   public void countTweets(final Keyword keyword, final Map<Date, Long> tweetCountMap, final List<DayStats> stats)
   {
      List<DayStatsKeyword> keywords = twitterService.getStatsKeywords(keyword, begin, end);
      for (DayStats day : stats)
      {
         for (DayStatsKeyword k : keywords)
         {
            if (day.equals(k.getDay()) && k.getKeyword().equals(keyword))
            {
               tweetCountMap.put(day.getDate(), k.getNumTweets());
            }
         }
      }
   }

   public List<Date> getDateRange()
   {
      List<Date> range = new ArrayList<Date>();
      Date temp = new Date(begin.getTime());
      while (temp.before(end) || DateUtils.isSameDay(temp, end))
      {
         range.add(new Date(temp.getTime()));
         temp = DateUtils.addDays(temp, 1);
      }
      return range;
   }

   public List<Tweet> getLeftTweetSample()
   {
      return extractSampleTweets(leftTweets);
   }

   public List<Tweet> getRightTweetSample()
   {
      return extractSampleTweets(rightTweets);
   }

   public void setLeftTweetSample(final List<Tweet> leftTweets)
   {
      this.leftTweets = leftTweets;
   }

   public void setRightTweetSample(final List<Tweet> rightTweets)
   {
      this.rightTweets = rightTweets;
   }

   public List<Tweet> extractSampleTweets(final List<Tweet> tweets)
   {
      if (tweets.size() > sampleSize)
      {
         List<Tweet> result = new ArrayList<Tweet>();
         for (int i = 0; i < sampleSize; i++)
         {
            result.add(tweets.get(i));
         }
         return result;
      }
      return tweets;
   }

   public Date getNow()
   {
      return new Date();
   }

   public int getSampleSize()
   {
      return sampleSize;
   }

}
