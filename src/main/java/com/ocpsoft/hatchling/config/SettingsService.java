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
package com.ocpsoft.hatchling.config;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.forge.persistence.PersistenceUtil;
import org.jboss.logging.Logger;
import org.jboss.seam.transaction.Transactional;

import com.ocpsoft.hatchling.domain.HatchlingConfig;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Named
@Transactional
public class SettingsService extends PersistenceUtil
{
   private static final long serialVersionUID = 352640474001730720L;

   Logger log = Logger.getLogger(SettingsService.class);

   @Inject
   private EntityManager manager;

   @Override
   protected EntityManager getEntityManager()
   {
      return manager;
   }

   public HatchlingConfig getConfig()
   {
      HatchlingConfig config = manager.find(HatchlingConfig.class, 1l);
      if (config == null)
      {
         config = new HatchlingConfig();
         save(config);
      }
      return config;
   }

   public void saveConfig(HatchlingConfig config)
   {
      manager.joinTransaction();
      save(config);
      manager.flush();
   }

}