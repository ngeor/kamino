// --------------------------------------------------------------------------------
// <copyright file="StorySortFieldExtensions.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 10:03 μμ
// --------------------------------------------------------------------------------

using NGSoftware.Common;

namespace BuzzStats.Data
{
    public static class StorySortFieldExtensions
    {
        public static EnumSortExpression<StorySortField> Asc(this StorySortField field)
        {
            return EnumSortExpression<StorySortField>.Asc(field);
        }

        public static EnumSortExpression<StorySortField> Desc(this StorySortField field)
        {
            return EnumSortExpression<StorySortField>.Desc(field);
        }
    }
}