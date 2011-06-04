package com.ocpsoft.hatchling.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.metawidget.inspector.annotation.UiHidden;

@Entity
@Table(name = "keywords")
public class Keyword implements Serializable, Comparable<Keyword>
{
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "id", updatable = false, nullable = false)
   private Long id = null;

   @Version
   @Column(name = "version")
   private int version = 0;

   @Column(unique = true, nullable = false)
   private String text;

   @UiHidden
   @Temporal(TemporalType.TIMESTAMP)
   private Date createdOn;

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

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((text == null) ? 0 : text.hashCode());
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
      Keyword other = (Keyword) obj;
      if (text == null)
      {
         if (other.text != null)
            return false;
      }
      else if (!text.equals(other.text))
         return false;
      return true;
   }

   public Date getCreatedOn()
   {
      return this.createdOn;
   }

   public void setCreatedOn(final Date createdOn)
   {
      this.createdOn = createdOn;
   }

   @Override
   public String toString()
   {
      return "Keyword [id=" + id + ", text=" + text + "]";
   }

   @Override
   public int compareTo(Keyword other)
   {
      if (this == other)
      {
         return 0;
      }
      else if (other == null)
      {
         return 1;
      }

      if (this.text == null && (other.getText() == null))
      {
         return 1;
      }
      else if (other.getText() == null)
      {
         return 1;
      }

      return text.toLowerCase().compareTo(other.getText().toLowerCase());

   }
}
