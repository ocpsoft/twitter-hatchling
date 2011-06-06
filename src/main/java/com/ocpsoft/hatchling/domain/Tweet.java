package com.ocpsoft.hatchling.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

@Entity
@Table(name = "tweets")
public class Tweet implements Serializable
{
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "id", updatable = false, nullable = false)
   private Long id = null;

   @Version
   @Column(name = "version")
   private int version = 0;

   @Column
   private String text;

   @Column
   private long tweetId;

   @Column
   private String screenName;

   @Column
   private String userName;

   @Column
   private String userProfileImageURL;

   @Column
   private String profileURL;

   @Index(name = "tweetReceivedIndex")
   @Temporal(TemporalType.TIMESTAMP)
   private Date received;

   @ManyToMany(cascade = CascadeType.ALL)
   private Set<Keyword> keywords = new HashSet<Keyword>();

   @ManyToMany(cascade = CascadeType.ALL)
   private Set<TweetURL> tweetURLs = new HashSet<TweetURL>();

   public Tweet()
   {
   }

   public Tweet(final String text)
   {
      this.text = text;
   }

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

   public String getText()
   {
      return this.text;
   }

   public void setText(final String text)
   {
      this.text = text;
   }

   public String getScreenName()
   {
      return this.screenName;
   }

   public void setScreenName(final String user)
   {
      this.screenName = user;
   }

   public Date getReceived()
   {
      return this.received;
   }

   public void setReceived(final Date received)
   {
      this.received = received;
   }

   public Set<Keyword> getKeywords()
   {
      return this.keywords;
   }

   public void setKeywords(final Set<Keyword> keywords)
   {
      this.keywords = keywords;
   }

   public String getUserName()
   {
      return this.userName;
   }

   public void setUserName(final String userName)
   {
      this.userName = userName;
   }

   public String getUserProfileImageURL()
   {
      return this.userProfileImageURL;
   }

   public void setUserProfileImageURL(final String userProfileImageURL)
   {
      this.userProfileImageURL = userProfileImageURL;
   }

   public String getProfileURL()
   {
      return profileURL;
   }

   public void setProfileURL(final String profileURL)
   {
      this.profileURL = profileURL;
   }

   public void setTweetId(final long tweetId)
   {
      this.tweetId = tweetId;
   }

   public long getTweetId()
   {
      return tweetId;
   }

   public Set<TweetURL> getURLs()
   {
      return this.tweetURLs;
   }

   public void setURLs(final Set<TweetURL> sharedURLs)
   {
      this.tweetURLs = sharedURLs;
   }

   @Override
   public String toString()
   {
      return "Tweet [text=" + text + ", tweetId=" + tweetId + ", screenName=" + screenName + ", received=" + received
               + "]";
   }
}
