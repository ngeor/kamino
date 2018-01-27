$UnitTestDirs = Get-ChildItem . | Where-Object { $_.PSIsContainer -and $_.Name.EndsWith("UnitTests")}
Remove-Item -Force .\opencover.xml
nuget install OpenCover -Version 4.6.519 -OutputDirectory packages
nuget install ReportGenerator -Version 2.5.9 -OutputDirectory packages

foreach ($dir in $UnitTestDirs) {
	.\packages\OpenCover.4.6.519\tools\OpenCover.Console.exe `
		-oldstyle `
		-output:opencover.xml `
		-mergeoutput `
		-register:user `
		-filter:"+[Buzz*]* -[*.UnitTests]*" `
		-target:"C:\Program Files\dotnet\dotnet.exe" `
		-targetargs:"test --no-build $dir" `
}

.\packages\ReportGenerator.2.5.9\tools\ReportGenerator.exe -reports:opencover.xml -targetdir:coverage
