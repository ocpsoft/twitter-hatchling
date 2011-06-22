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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
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
   private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

   @Inject
   private TwitterService twitterService;

   @Inject
   private SettingsService settings;

   private String leftParam = "";
   private String rightParam = "";

   private List<Keyword> techWords = new ArrayList<Keyword>();
   private Keyword left = new Keyword();
   private Keyword right = new Keyword();
   private Keyword lastLeft = new Keyword();
   private Keyword lastRight = new Keyword();

   private final int sampleSize = 5;
   private List<Tweet> leftTweets = new ArrayList<Tweet>();
   private List<Tweet> rightTweets = new ArrayList<Tweet>();

   private Map<Date, Long> leftTweetMap = new HashMap<Date, Long>();
   private Map<Date, Long> rightTweetMap = new HashMap<Date, Long>();

   private long leftTotal = 0;
   private long rightTotal = 0;
   private double leftAverage = 0;
   private double rightAverage = 0;

   private Date end = DateUtils.truncate(new Date(), Calendar.DATE);;
   private Date begin = DateUtils.addDays(end, -7);
   private Date lastEnd = new Date(0);
   private Date lastBegin = new Date(0);
   private List<Date> range = getDateRange();

   public void leftValueChanged(final ValueChangeEvent event) throws AbortProcessingException
   {
      System.out.println("Left: " + event.getOldValue() + " -> " + event.getNewValue());
   }

   public void rightValueChanged(final ValueChangeEvent event) throws AbortProcessingException
   {
      System.out.println("Right: " + event.getOldValue() + " -> " + event.getNewValue());
   }

   /*
    * Called by prettyfaces when viewing stats
    */
   public void init()
   {
      if (begin.after(end) || ((!end.before(DateUtils.truncate(new Date(), Calendar.DATE)))
               && !DateUtils.isSameDay(end, new Date())))
      {
         end = DateUtils.truncate(new Date(), Calendar.DATE);
         begin = DateUtils.addDays(end, -7);
         range = getDateRange();
      }

      if (techWords.isEmpty())
      {
         techWords = twitterService.getTrackKeywords();
         Collections.sort(techWords);
      }

      if (leftParam != null)
      {
         for (Keyword k : techWords)
         {
            if (k.getLabel().equalsIgnoreCase(leftParam))
            {
               if (!k.equals(left))
               {
                  setLeft(k);
               }
               break;
            }
         }
         leftParam = null;
      }
      if (rightParam != null)
      {
         for (Keyword k : techWords)
         {
            if (k.getLabel().equalsIgnoreCase(rightParam))
            {
               if (!k.equals(right))
               {
                  setRight(k);
               }
               break;
            }
         }
         rightParam = null;
      }

      if (!techWords.isEmpty())
      {

         if (left.equals(new Keyword()) || right.equals(new Keyword()))
         {
            HatchlingConfig config = settings.getConfig();
            if (left.equals(new Keyword()))
            {
               Keyword defaultLeft = config.getDefaultLeft();
               if ((defaultLeft != null) && techWords.contains(defaultLeft))
               {
                  setLeft(defaultLeft);
               }
               else
               {
                  setLeft(techWords.get(0));
               }
            }

            if (right.equals(new Keyword()))
            {
               Keyword defaultRight = config.getDefaultRight();
               if ((defaultRight != null) && techWords.contains(defaultRight))
               {
                  right = defaultRight;
               }
               else
               {
                  if (techWords.size() == 1)
                  {
                     setRight(techWords.get(0));
                  }
                  else if (techWords.size() > 1)
                  {
                     setRight(techWords.get(1));
                  }
               }
            }
         }
      }
   }

   public void updateChartData()
   {
      boolean updateLeft = false;
      boolean updateRight = false;
      if ((lastLeft.equals(new Keyword())) || (lastRight.equals(new Keyword()))
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

         if (updateLeft && (left != null) && (left.getLabel() != null))
         {
            leftTweetMap = new HashMap<Date, Long>();
            for (Date date : range)
            {
               leftTweetMap.put(date, 0l);
            }

            leftTweets = twitterService.getSampleTweets(Arrays.asList(left), begin, end, 0, 10);
            countTweets(left, leftTweetMap, stats);

            leftTotal = 0;
            leftAverage = 0;
            for (Long tweets : leftTweetMap.values())
            {
               leftTotal += tweets;
               leftAverage += tweets;
            }
            leftAverage = Math.round(leftAverage / leftTweetMap.values().size() * 100 / 100);
         }

         if (updateRight && (right != null) && (right.getLabel() != null))
         {
            rightTweetMap = new HashMap<Date, Long>();
            for (Date date : range)
            {
               rightTweetMap.put(date, 0l);
            }

            rightTweets = twitterService.getSampleTweets(Arrays.asList(right), begin, end, 0, 10);
            countTweets(right, rightTweetMap, stats);

            rightTotal = 0;
            rightAverage = 0;
            for (Long tweets : rightTweetMap.values())
            {
               rightTotal += tweets;
               rightAverage += tweets;
            }
            rightAverage = Math.round(rightAverage / rightTweetMap.values().size() * 100) / 100;
         }

         // update cache triggers
         lastLeft = left;
         lastRight = right;

         lastBegin = new Date(begin.getTime());
         lastEnd = new Date(end.getTime());
      }
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public Object getChartData()
   {
      updateChartData();

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
      data.put("left", left.getLabel());
      data.put("right", right.getLabel());
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

   /*
    * Getters & Setters
    */
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
      this.lastLeft = this.left;
      this.left = left;
   }

   public Keyword getRight()
   {
      return right;
   }

   public void setRight(final Keyword right)
   {
      this.lastRight = this.right;
      this.right = right;
   }

   public String getLeftLink() throws UnsupportedEncodingException
   {
      return left == null ? "" : URLEncoder.encode(left.getLabel().replaceAll("#", "%23"), "UTF-8");
   }

   public String getRightLink() throws UnsupportedEncodingException
   {
      return right == null ? "" : URLEncoder.encode(right.getLabel().replaceAll("#", "%23"), "UTF-8");
   }

   public Date getBegin()
   {
      return begin;
   }

   public void setBegin(final Date begin)
   {
      this.lastBegin = this.begin;
      this.begin = begin;
   }

   public String getBeginFormat()
   {
      return dateFormat.format(begin);
   }

   public Date getEnd()
   {
      return end;
   }

   public void setEnd(final Date end)
   {
      this.lastEnd = this.end;
      this.end = end;
   }

   public String getEndFormat()
   {
      return dateFormat.format(end);
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

   public Date getNow()
   {
      return new Date();
   }

   public int getSampleSize()
   {
      return sampleSize;
   }

   public long getLeftTotal()
   {
      return leftTotal;
   }

   public void setLeftTotal(final long leftTotal)
   {
      this.leftTotal = leftTotal;
   }

   public long getRightTotal()
   {
      return rightTotal;
   }

   public void setRightTotal(final long rightTotal)
   {
      this.rightTotal = rightTotal;
   }

   public double getLeftAverage()
   {
      return leftAverage;
   }

   public void setLeftAverage(final double leftAverage)
   {
      this.leftAverage = leftAverage;
   }

   public double getRightAverage()
   {
      return rightAverage;
   }

   public void setRightAverage(final double rightAverage)
   {
      this.rightAverage = rightAverage;
   }

   /**
    * PrettyFaces Parameters
    */
   public String getBeginParam()
   {
      return null;
   }

   public String getEndParam()
   {
      return null;
   }

   public void setBeginParam(final String beginParam)
   {
      if (beginParam != null)
      {
         try
         {
            setBegin(dateFormat.parse(beginParam));
         }
         catch (ParseException e)
         {
            // ignore
         }
      }
   }

   public void setEndParam(final String endParam)
   {
      if (endParam != null)
      {
         try
         {
            setEnd(dateFormat.parse(endParam));
         }
         catch (ParseException e)
         {
            // ignore
         }
      }
   }

   public String getLeftParam()
   {
      return leftParam;
   }

   public void setLeftParam(final String leftParam)
   {
      this.leftParam = leftParam;
   }

   public String getRightParam()
   {
      return rightParam;
   }

   public void setRightParam(final String rightParam)
   {
      this.rightParam = rightParam;
   }

}
