package com.ocpsoft.hatchling.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "config")
public class HatchlingConfig implements Serializable
{
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "id", updatable = false, nullable = false)
   private Long id = null;

   @Version
   @Column(name = "version")
   private int version = 0;

   @ManyToOne
   private Keyword defaultLeft;
   @ManyToOne
   private Keyword defaultRight;

   @Column(length = 64)
   private String consumerKey;
   @Column(length = 64)
   private String consumerSecret;
   @Column(length = 64)
   private String accessToken;
   @Column(length = 64)
   private String accessTokenSecret;

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

   public String getConsumerKey()
   {
      return consumerKey;
   }

   public String getConsumerSecret()
   {
      return consumerSecret;
   }

   public String getAccessToken()
   {
      return accessToken;
   }

   public String getAccessTokenSecret()
   {
      return accessTokenSecret;
   }

   public void setConsumerKey(String consumerKey)
   {
      this.consumerKey = consumerKey;
   }

   public void setConsumerSecret(String consumerSecret)
   {
      this.consumerSecret = consumerSecret;
   }

   public void setAccessToken(String accessToken)
   {
      this.accessToken = accessToken;
   }

   public void setAccessTokenSecret(String accessTokenSecret)
   {
      this.accessTokenSecret = accessTokenSecret;
   }

   public Keyword getDefaultLeft()
   {
      return defaultLeft;
   }

   public void setDefaultLeft(Keyword defaultLeft)
   {
      this.defaultLeft = defaultLeft;
   }

   public Keyword getDefaultRight()
   {
      return defaultRight;
   }

   public void setDefaultRight(Keyword defaultRight)
   {
      this.defaultRight = defaultRight;
   }

}
