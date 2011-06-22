package com.ocpsoft.hatchling.twitter;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletContext;

import org.jboss.seam.servlet.event.Destroyed;

import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.Authorization;
import twitter4j.auth.AuthorizationFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.ocpsoft.hatchling.config.SettingsService;
import com.ocpsoft.hatchling.domain.HatchlingConfig;

@Named
@Singleton
public class TwitterStreamHandler
{
   @Inject
   private TweetBuffer buffer;

   @Inject
   private SettingsService settings;

   @Inject
   private FilterLoader filterLoader;

   private PersistentStatusListener listener;

   private Authorization auth;
   private TwitterStream twitterStream;

   private boolean streaming = false;

   void shutdown(@Observes @Destroyed final ServletContext ctx)
   {
      stop();
   }

   public void stream()
   {
      HatchlingConfig conf = settings.getConfig();

      Configuration config = new ConfigurationBuilder()
               .setAsyncNumThreads(3)
               .setHttpConnectionTimeout(15000)
               .setHttpRetryIntervalSeconds(15000)
               .setHttpStreamingReadTimeout(150000)
               .setHttpReadTimeout(15000)
                .setOAuthConsumerKey(conf.getConsumerKey())
                .setOAuthConsumerSecret(conf.getConsumerSecret())
               .setOAuthAccessToken(conf.getAccessToken())
                .setOAuthAccessTokenSecret(conf.getAccessTokenSecret())
               .build();

      auth = AuthorizationFactory.getInstance(config);

      twitterStream = new TwitterStreamFactory(config).getInstance();

      listener = new PersistentStatusListener(buffer);
      twitterStream.addListener(listener);

      if (auth.isEnabled())
      {
         String[] filters = filterLoader.getFilters();
         if (filters.length == 0)
         {
            throw new RuntimeException("Must add Filters before streaming may begin.");
         }
         FilterQuery filter = new FilterQuery()
                     .track(filters)
                  .setIncludeEntities(true);

         twitterStream.filter(filter);
         streaming = true;
      }
   }

   public void stop()
   {
      if (twitterStream != null)
         twitterStream.cleanUp();
      streaming = false;
   }

   public Authorization getAuth()
   {
      return auth;
   }

   public void setAuth(final Authorization auth)
   {
      this.auth = auth;
   }

   public boolean isStreaming()
   {
      return streaming;
   }

   public void setStreaming(final boolean streaming)
   {
      this.streaming = streaming;
   }
}
