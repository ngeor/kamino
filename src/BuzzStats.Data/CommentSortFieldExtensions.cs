// --------------------------------------------------------------------------------
// <copyright file="CommentSortFieldExtensions.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 10:03 μμ
// --------------------------------------------------------------------------------

using NGSoftware.Common;

namespace BuzzStats.Data
{
    public static class CommentSortFieldExtensions
    {
        public static EnumSortExpression<CommentSortField> Asc(this CommentSortField field)
        {
            return EnumSortExpression<CommentSortField>.Asc(field);
        }

        public static EnumSortExpression<CommentSortField> Desc(this CommentSortField field)
        {
            return EnumSortExpression<CommentSortField>.Desc(field);
        }
    }
}
