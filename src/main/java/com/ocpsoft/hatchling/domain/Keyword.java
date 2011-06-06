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
import javax.validation.constraints.Size;

import org.hibernate.annotations.Index;
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

   @Index(name = "labelIndex")
   @Column(unique = true, nullable = false)
   private String label;

   @Size(max = 60)
   @Column(unique = true, nullable = false, length = 60)
   private String text;

   @UiHidden
   @Temporal(TemporalType.TIMESTAMP)
   private Date createdOn;

   public Keyword()
   {
   }

   public Keyword(final String label, final String text)
   {
      this.label = label;
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

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((label == null) ? 0 : label.hashCode());
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
      if (label == null)
      {
         if (other.label != null)
            return false;
      }
      else if (!label.equals(other.label))
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
      return label;
   }

   @Override
   public int compareTo(final Keyword other)
   {
      if (this == other)
      {
         return 0;
      }
      else if (other == null)
      {
         return 1;
      }

      if ((this.text == null) && (other.getLabel() == null))
      {
         return 1;
      }
      else if (other.getLabel() == null)
      {
         return 1;
      }

      return text.toLowerCase().compareTo(other.getLabel().toLowerCase());

   }

   public String getLabel()
   {
      return label;
   }

   public void setLabel(final String label)
   {
      this.label = label;
   }
}
