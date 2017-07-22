using System.Collections.Generic;
using BuzzStats.StorageWebApi.Entities;
using NHibernate.Mapping;

namespace BuzzStats.StorageWebApi
{
    public class CommentEntityHolder
    {
        private CommentEntity _entity;
        private IList<CommentEntityHolder> _children = new List<CommentEntityHolder>();

        public CommentEntity Entity
        {
            get { return _entity; }
            set
            {
                _entity = value;
                UpdateChildrenParentReferences();
            }
        }

        private void UpdateChildrenParentReferences()
        {
            foreach (var child in Children)
            {
                child.Entity.ParentComment = Entity;
            }
        }

        public IList<CommentEntityHolder> Children {
            get
            {
                return _children;
            }
            set
            {
                _children = value ?? new List<CommentEntityHolder>();
            }
        }
    }
}