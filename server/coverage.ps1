nuget install OpenCover -Version 4.6.519 -OutputDirectory packages
nuget install ReportGenerator -Version 2.5.9 -OutputDirectory packages
nuget install NUnit.ConsoleRunner -Version 3.7.0 -OutputDirectory packages

.\packages\OpenCover.4.6.519\tools\OpenCover.Console.exe `
	-register:user `
	-filter:"+[Buzz*]* -[*.UnitTests]*" `
	-target:.\packages\NUnit.ConsoleRunner.3.7.0\tools\nunit3-console.exe `
	-targetargs:".\BuzzStats.WebApi.UnitTests\bin\Debug\BuzzStats.WebApi.UnitTests.dll"

.\packages\ReportGenerator.2.5.9\tools\ReportGenerator.exe -reports:results.xml -targetdir:coveragereport
