package com.ocpsoft.hatchling.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.jboss.forge.persistence.PaginationHelper;
import org.jboss.forge.persistence.PersistenceUtil;

import com.ocpsoft.hatchling.domain.Keyword;

@Named
@Stateful
@RequestScoped
public class FilterTrackBean extends PersistenceUtil
{
   private static final long serialVersionUID = 6866936433182018079L;

   @PersistenceContext
   private EntityManager manager;

   private List<Keyword> list = null;
   private Keyword filtertrack = new Keyword();
   private String id = "";
   private boolean editMode = false;
   private PaginationHelper<Keyword> pagination;

   public void load()
   {
      TypedQuery<Keyword> query = manager.createQuery("from Keyword k where k.label = :label", Keyword.class);
      query.setParameter("label", id);
      filtertrack = query.getSingleResult();
   }

   public String create()
   {
      filtertrack.setText(filtertrack.getText().replaceAll("\\s*,\\s*", ","));
      filtertrack.setCreatedOn(new Date());
      create(filtertrack);
      return "view?faces-redirect=true&id=" + filtertrack.getLabel();
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
      return "view?faces-redirect=true&id=" + filtertrack.getLabel();
   }

   public String getId()
   {
      return id;
   }

   public void setId(final String id)
   {
      this.id = id;
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

   public void setEditMode(final boolean editMode)
   {
      this.editMode = editMode;
   }

   public boolean isEditMode()
   {
      return editMode;
   }
}
