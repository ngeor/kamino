/// This file is autogenerated by a tool.
using System;

using EPiServer.Core;

namespace SampleProject
{

	public static class en
	{

		/// <summary>
		/// Gets a translated label. Original value: 'An error has occured.'.
		/// </summary>
		public static string error { get { return LanguageManager.Instance.Translate("/en/error"); } }

		/// <summary>
		/// Gets a translated label. Original value: 'Are you sure you want to {0}?'.
		/// </summary>
		public static string question { get { return LanguageManager.Instance.Translate("/en/question"); } }

		public static string questionFormat(params object[] args)
		{
			return string.Format(question, args);
		}
	}

}
