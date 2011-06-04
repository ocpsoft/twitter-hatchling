package com.ocpsoft.hatchling.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

@Entity
@Table(name = "daystats", uniqueConstraints = {
         @UniqueConstraint(columnNames = { "date" })
 })
public class DayStats implements Serializable
{
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "id", updatable = false, nullable = false)
   private Long id = null;

   @Version
   @Column(name = "version")
   private int version = 0;

   @Column
   @Index(name = "numTweets")
   private long totalTweets;

   @Temporal(TemporalType.DATE)
   @Column(nullable = false, updatable = false)
   private Date date;

   @OneToMany(mappedBy = "day", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   private Set<DayStatsKeyword> keywords = new HashSet<DayStatsKeyword>();

   @OneToMany(mappedBy = "day", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   private Set<DayStatsURL> urls = new HashSet<DayStatsURL>();

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

   public long getTotalTweets()
   {
      return this.totalTweets;
   }

   public void setTotalTweets(final long totalTweets)
   {
      this.totalTweets = totalTweets;
   }

   public Date getDate()
   {
      return this.date;
   }

   public void setDate(final Date date)
   {
      this.date = date;
   }

   public Set<DayStatsKeyword> getKeywords()
   {
      return this.keywords;
   }

   public void setKeywords(final Set<DayStatsKeyword> keywords)
   {
      this.keywords = keywords;
   }

   public Set<DayStatsURL> getUrls()
   {
      return urls;
   }

   public void setUrls(final Set<DayStatsURL> urls)
   {
      this.urls = urls;
   }

   @Override
   public String toString()
   {
      return "DayStats [totalTweets=" + totalTweets + ", date=" + date + "]";
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((date == null) ? 0 : date.hashCode());
      return result;
   }

   @Override
   public boolean equals(final Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      DayStats other = (DayStats) obj;
      if (date == null)
      {
         if (other.date != null)
            return false;
      }
      else if (!date.equals(other.date))
         return false;
      return true;
   }
}
