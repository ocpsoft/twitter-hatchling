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
@Table(name = "daystats_keywords", uniqueConstraints = {
         @UniqueConstraint(columnNames = { "day_id", "keyword_id" })
 })
public class DayStatsKeyword implements java.io.Serializable
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
   private Keyword keyword;

   @Column
   @Index(name = "numTweets")
   private long numTweets;

   public Long getId()
   {
      return this.id;
   }

   public void setId(final Long id)
   {
      this.id = id;
   }

   public int getVersion()
   {
      return this.version;
   }

   public void setVersion(final int version)
   {
      this.version = version;
   }

   public DayStats getDay()
   {
      return this.day;
   }

   public void setDay(final DayStats day)
   {
      this.day = day;
   }

   public Keyword getKeyword()
   {
      return this.keyword;
   }

   public void setKeyword(final Keyword keyword)
   {
      this.keyword = keyword;
   }

   public long getNumTweets()
   {
      return numTweets;
   }

   public void setNumTweets(final long numTweets)
   {
      this.numTweets = numTweets;
   }

   @Override
   public String toString()
   {
      return "DayStatsKeyword [day=" + day + ", keyword=" + keyword + ", numTweets=" + numTweets + "]";
   }
}
