// --------------------------------------------------------------------------------
// <copyright file="IAboutPageView.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/01/24
// * Time: 07:30:21
// --------------------------------------------------------------------------------

using System;
using BuzzStats.Data;

namespace BuzzStats.Web.Mvp
{
    public interface IAboutPageView : IView
    {
        void SetEchoSucceeded(bool success);
        void SetUpTime(TimeSpan upTime);
        void SetMinMaxStats(MinMaxStats minMaxStats);
        void SetOldestCheckedStory(StorySummary oldestCheckedStory);
    }
}