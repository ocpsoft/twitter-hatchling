package com.ocpsoft.hatchling.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Index;

@Entity
@Table(name = "daystats_urls", uniqueConstraints = {
         @UniqueConstraint(columnNames = { "day_id", "tweetURL_id" })
 })
public class DayStatsURL implements java.io.Serializable
{
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "id", updatable = false, nullable = false)
   private Long id = null;

   @Version
   @Column(name = "version")
   private int version = 0;

   @Fetch(FetchMode.JOIN)
   @ManyToOne(fetch = FetchType.LAZY)
   private DayStats day = new DayStats();

   @Fetch(FetchMode.JOIN)
   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   private TweetURL tweetURL;

   @Column
   @Index(name = "numTweets")
   private int numTweets;

   public Long getId()
   {
      return id;
   }

   public void setId(final Long id)
   {
      this.id = id;
   }

   public int getVersion()
   {
      return version;
   }

   public void setVersion(final int version)
   {
      this.version = version;
   }

   public DayStats getDay()
   {
      return day;
   }

   public void setDay(final DayStats day)
   {
      this.day = day;
   }

   public TweetURL getTweetURL()
   {
      return tweetURL;
   }

   public void setTweetURL(final TweetURL tweetURL)
   {
      this.tweetURL = tweetURL;
   }

   public int getNumTweets()
   {
      return numTweets;
   }

   public void setNumTweets(final int numTweets)
   {
      this.numTweets = numTweets;
   }

   @Override
   public String toString()
   {
      return "DayStatsURL [day=" + day + ", tweetURL=" + tweetURL + ", numTweets=" + numTweets + "]";
   }

}
