package com.ocpsoft.hatchling.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.forge.persistence.PaginationHelper;
import org.jboss.forge.persistence.PersistenceUtil;
import org.jboss.seam.transaction.Transactional;

import com.ocpsoft.hatchling.domain.Keyword;

@Named
@Transactional
@RequestScoped
public class FilterTrackBean extends PersistenceUtil
{
   @Inject
   private EntityManager manager;

   private List<Keyword> list = null;
   private Keyword filtertrack = new Keyword();
   private long id = 0;
   private PaginationHelper<Keyword> pagination;

   public void load()
   {
      filtertrack = findById(Keyword.class, id);
   }

   public String create()
   {
      filtertrack.setText(filtertrack.getText().replaceAll("\\s*,\\s*", ","));
      filtertrack.setCreatedOn(new Date());
      create(filtertrack);
      return "view?faces-redirect=true&id=" + filtertrack.getId();
   }

   public String delete()
   {
      delete(filtertrack);
      return "list?faces-redirect=true";
   }

   public String save()
   {
      filtertrack.setText(filtertrack.getText().replaceAll("\\s*,\\s*", ","));
      save(filtertrack);
      return "view?faces-redirect=true&id=" + filtertrack.getId();
   }

   public long getId()
   {
      return id;
   }

   public void setId(final long id)
   {
      this.id = id;
      if (id > 0)
      {
         load();
      }
   }

   public Keyword getFilterTrack()
   {
      return filtertrack;
   }

   public void setFilterTrack(final Keyword filtertrack)
   {
      this.filtertrack = filtertrack;
   }

   public List<Keyword> getList()
   {
      if (list == null)
      {
         list = getPagination().createPageDataModel();
      }
      return list;
   }

   public void setList(final List<Keyword> list)
   {
      this.list = list;
   }

   public PaginationHelper<Keyword> getPagination()
   {
      if (pagination == null)
      {
         pagination = new PaginationHelper<Keyword>(10)
         {
            @Override
            public int getItemsCount()
            {
               return count(Keyword.class);
            }

            @Override
            public List<Keyword> createPageDataModel()
            {
               return new ArrayList<Keyword>(findAll(Keyword.class, getPageFirstItem(), getPageSize()));
            }
         };
      }
      return pagination;
   }

   private void recreateModel()
   {
      list = null;
   }

   public String next()
   {
      getPagination().nextPage();
      recreateModel();
      return "list";
   }

   public String previous()
   {
      getPagination().previousPage();
      recreateModel();
      return "list";
   }

   @Override
   protected EntityManager getEntityManager()
   {
      return manager;
   }
}
