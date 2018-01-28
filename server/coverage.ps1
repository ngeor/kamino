param (
	[string]$project = "all"
)

nuget install OpenCover -Version 4.6.519 -OutputDirectory packages
nuget install ReportGenerator -Version 3.1.2 -OutputDirectory packages

if ($project -eq "all") {
	Remove-Item -Force .\opencover.xml
	$UnitTestDirs = Get-ChildItem . | Where-Object { $_.PSIsContainer -and $_.Name.EndsWith("UnitTests")}
	foreach ($dir in $UnitTestDirs) {
		.\packages\OpenCover.4.6.519\tools\OpenCover.Console.exe `
			-oldstyle `
			-output:opencover.xml `
			-mergeoutput `
			-register:user `
			-filter:"+[Buzz*]* -[*.UnitTests]*" `
			-target:"C:\Program Files\dotnet\dotnet.exe" `
			-targetargs:"test --no-build $dir"
	}
} else {
	.\packages\OpenCover.4.6.519\tools\OpenCover.Console.exe `
		-oldstyle `
		-output:opencover.xml `
		-mergeoutput `
		-register:user `
		-filter:"+[Buzz*]* -[*.UnitTests]*" `
		-target:"C:\Program Files\dotnet\dotnet.exe" `
		-targetargs:"test --no-build $project"
}

.\packages\ReportGenerator.3.1.2\tools\ReportGenerator.exe -reports:opencover.xml -targetdir:coverage
