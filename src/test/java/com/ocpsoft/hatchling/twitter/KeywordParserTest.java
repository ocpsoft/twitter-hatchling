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
package com.ocpsoft.hatchling.twitter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ocpsoft.hatchling.domain.Keyword;
import com.ocpsoft.hatchling.domain.Tweet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class KeywordParserTest
{
   KeywordParser parser = new KeywordParser();

   @Test
   public void testAssignKeywords() throws Exception
   {
      Keyword test = new Keyword("Test", "#Test, test");
      Keyword match = new Keyword("Match", "MATCH");
      List<Keyword> tracks = Arrays.asList(test, match);
      List<Tweet> buffer = Arrays.asList(
               new Tweet("This is a #test tweet"),
               new Tweet("This is another Test tweet"),
               new Tweet("This will not match"),
               new Tweet("This tweet will match both tests"));

      List<Tweet> result = parser.assignKeywords(tracks, buffer);

      assertTrue(result.get(0).getKeywords().contains(test));
      assertFalse(result.get(0).getKeywords().contains(match));

      assertTrue(result.get(1).getKeywords().contains(test));
      assertFalse(result.get(1).getKeywords().contains(match));

      assertFalse(result.get(2).getKeywords().contains(test));
      assertTrue(result.get(2).getKeywords().contains(match));

      assertTrue(result.get(3).getKeywords().contains(match));
      assertTrue(result.get(3).getKeywords().contains(test));
   }

}
