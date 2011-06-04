package com.ocpsoft.hatchling.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

@Entity
@Table(name = "urls")
public class TweetURL implements java.io.Serializable
{
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "id", updatable = false, nullable = false)
   private Long id = null;

   @Version
   @Column(name = "version")
   private int version = 0;

   @Index(name = "urlIndex")
   @Column(unique = true, updatable = false, nullable = false)
   private String url;

   @ManyToMany(mappedBy = "tweetURLs")
   private Set<Tweet> tweets = new HashSet<Tweet>();

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

   public String getURL()
   {
      return this.url;
   }

   public void setURL(final String url)
   {
      this.url = url;
   }

   public Set<Tweet> getTweets()
   {
      return this.tweets;
   }

   public void setTweets(final Set<Tweet> tweets)
   {
      this.tweets = tweets;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((url == null) ? 0 : url.hashCode());
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
      TweetURL other = (TweetURL) obj;
      if (url == null)
      {
         if (other.url != null)
            return false;
      }
      else if (!url.equals(other.url))
         return false;
      return true;
   }

   @Override
   public String toString()
   {
      return "TweetURL [id= " + id + ", url=" + url + "]";
   }
}
