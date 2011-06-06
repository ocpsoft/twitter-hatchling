package com.ocpsoft.hatchling.twitter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.jboss.forge.persistence.PersistenceUtil;
import org.jboss.logging.Logger;
import org.jboss.seam.transaction.Transactional;

import com.ocpsoft.hatchling.domain.DayStats;
import com.ocpsoft.hatchling.domain.DayStatsKeyword;
import com.ocpsoft.hatchling.domain.Keyword;
import com.ocpsoft.hatchling.domain.Tweet;
import com.ocpsoft.hatchling.domain.TweetURL;
import com.ocpsoft.hatchling.util.DateUtils;

@Named
@Transactional
public class TwitterService extends PersistenceUtil
{
   Logger log = Logger.getLogger(TwitterService.class);

   @Inject
   private EntityManager manager;

   @Inject
   private KeywordParser keywordParser;

   private static final long serialVersionUID = -939943862688471632L;

   public long getTotalTweets()
   {
      return (Long) manager.createQuery("select count(t.id) from Tweet t").getSingleResult();
   }

   public List<Keyword> getTrackKeywords()
   {
      return findAll(Keyword.class);
   }

   public List<Tweet> getSampleTweets(final List<Keyword> keywords, final Date start, final Date end, final int begin,
            final int maxResults)
   {
      List<String> list = new ArrayList<String>();
      for (Keyword keyword : keywords)
      {
         list.add(keyword.getLabel());
      }

      List<Tweet> result = new ArrayList<Tweet>();

      TypedQuery<Tweet> query = manager
               .createQuery("FROM Tweet t " +
                        " WHERE " +
                        " t IN (SELECT t.id FROM Tweet t " +
                        " JOIN t.keywords k " +
                        " WHERE t.received > :start " +
                        " AND t.received < :end " +
                        " AND k.label in (:keywords)) " +
                        " ORDER BY t.received DESC", Tweet.class);

      query.setParameter("start", DateUtils.truncate(start, Calendar.DATE));
      query.setParameter("end", DateUtils.truncate(DateUtils.addDays(end, 1), Calendar.DATE));
      query.setParameter("keywords", list);

      if (begin > 0)
         query.setFirstResult(begin);
      if (maxResults > 0)
         query.setMaxResults(maxResults);

      List<Tweet> resultList = query.getResultList();

      for (Tweet tweet : resultList)
      {
         result.add(tweet);
      }

      return result;
   }

   public void saveNewTweets(List<Tweet> buffer)
   {
      manager.joinTransaction();
      /*
       * Insert new tweets, urls, and keywords
       */
      List<Keyword> trackKeywords = getTrackKeywords();
      buffer = keywordParser.assignKeywords(trackKeywords, buffer);

      Set<String> tweetedURLs = new HashSet<String>();
      for (Tweet tweet : buffer)
      {
         for (TweetURL url : tweet.getURLs())
         {
            tweetedURLs.add(url.getURL());
         }
      }

      if (!tweetedURLs.isEmpty())
      {
         TypedQuery<TweetURL> query = manager.createQuery("from TweetURL u where u.url in (:urls)",
                        TweetURL.class);
         query.setParameter("urls", tweetedURLs);
         List<TweetURL> existingTweetURLs = query.getResultList();

         for (Tweet tweet : buffer)
         {
            List<TweetURL> replacements = new ArrayList<TweetURL>();
            for (TweetURL url : tweet.getURLs())
            {
               if (existingTweetURLs.contains(url))
               {
                  replacements.add(url);
               }
               else
               {
                  existingTweetURLs.add(url);
               }
            }

            for (TweetURL url : replacements)
            {
               tweet.getURLs().remove(url);
               tweet.getURLs().add(existingTweetURLs.get(existingTweetURLs.lastIndexOf(url)));
            }
         }
      }

      for (Tweet tweet : buffer)
      {
         manager.persist(tweet);
      }
      manager.flush();
   }

   public void updateStats(Date date)
   {
      // select count(u.id) AS c, u.url, u.id FROM urls AS u INNER JOIN tweets_urls AS t ON u.id = t.tweetURLs_id GROUP
      // BY u.id HAVING c > 20 ORDER BY c DESC LIMIT 200;

      date = DateUtils.truncate(date, Calendar.DATE);
      Date next = DateUtils.addDays(date, 1);

      Query dayTweetsQuery = manager.createQuery("SELECT COUNT(t.id) FROM Tweet t " +
                  " WHERE t.received >= :date AND t.received < :next ");
      dayTweetsQuery.setParameter("date", date);
      dayTweetsQuery.setParameter("next", next);
      long totalTweets = (Long) dayTweetsQuery.getSingleResult();

      DayStats day = getStats(date); // ensure stats exist
      day.setTotalTweets(totalTweets);
      manager.persist(day);

      /*
       * Update Keywords
       */
      List<Keyword> keywords = getTrackKeywords();

      TypedQuery<DayStatsKeyword> keywordStatsQuery = getEntityManager().createQuery(
                  "FROM DayStatsKeyword k WHERE k.keyword in (:keywords) AND k.day = :day", DayStatsKeyword.class);

      keywordStatsQuery.setParameter("keywords", keywords);
      keywordStatsQuery.setParameter("day", day);
      List<DayStatsKeyword> keywordStats = keywordStatsQuery.getResultList();

      for (Keyword keyword : keywords)
      {
         DayStatsKeyword keyStat = null;
         for (DayStatsKeyword dsk : keywordStats)
         {
            if (keyword.equals(dsk.getKeyword()))
            {
               keyStat = dsk;
            }
         }

         if (keyStat == null)
         {
            DayStatsKeyword temp = new DayStatsKeyword();
            temp.setDay(day);
            temp.setKeyword(keyword);
            temp.setNumTweets(0);
            day.getKeywords().add(temp);
            manager.persist(temp);
            keyStat = temp;
         }

         Query keyTweetsQuery = manager.createQuery("SELECT COUNT(k.id) FROM Tweet t " +
                     " LEFT JOIN t.keywords k" +
                     " WHERE t.received >= :date AND t.received < :next " +
                     " AND k = :keyword");
         keyTweetsQuery.setParameter("date", date);
         keyTweetsQuery.setParameter("next", next);
         keyTweetsQuery.setParameter("keyword", keyword);
         long keyTweets = (Long) keyTweetsQuery.getSingleResult();

         keyStat.setNumTweets(keyTweets);
         manager.persist(keyStat);
      }
   }

   public DayStats getStats(final Date date)
   {
      TypedQuery<DayStats> query = manager.createQuery("from DayStats d " +
               " where d.date = :date ",
               DayStats.class);
      query.setParameter("date", date);

      try
      {
         return query.getSingleResult();
      }
      catch (NoResultException e)
      {
         DayStats day = new DayStats();
         day.setDate(new Date());
         manager.persist(day);
         manager.flush();
         return day;
      }
   }

   public List<DayStats> getStats(final Date begin, final Date end)
   {
      TypedQuery<DayStats> query = manager.createQuery("FROM DayStats d " +
               " WHERE d.date >= :begin " +
               " AND d.date <= :end " +
               " ORDER BY d.date ASC", DayStats.class);

      query.setParameter("begin", begin);
      query.setParameter("end", end);

      List<DayStats> result = query.getResultList();
      return result;
   }

   public List<DayStatsKeyword> getStatsKeywords(final Keyword keyword, final Date begin, final Date end)
   {
      TypedQuery<DayStatsKeyword> query = manager.createQuery("FROM DayStatsKeyword d " +
               " WHERE d.day.date >= :begin " +
               " AND d.day.date <= :end " +
               " AND d.keyword.label = :keyword " +
               " ORDER BY d.day.date ASC", DayStatsKeyword.class);

      query.setParameter("begin", begin);
      query.setParameter("end", end);
      query.setParameter("keyword", keyword.getLabel());

      List<DayStatsKeyword> result = query.getResultList();
      return result;
   }

   @Override
   protected EntityManager getEntityManager()
   {
      return manager;
   }
}
