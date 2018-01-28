if (-Not ($Env:SONAR_LOGIN)) {
    Write-Error -Category InvalidArgument -Message "Please set the SONAR_LOGIN environment variable"
    Exit 1
}

SonarQube.Scanner.MSBuild.exe begin /k:"BuzzStats" `
    /d:sonar.organization="ngeor-github" `
    /d:sonar.host.url="https://sonarcloud.io" `
    /d:sonar.login="$Env:SONAR_LOGIN" `
    /d:sonar.cs.opencover.reportsPaths="opencover.xml" `
    /d:sonar.coverage.exclusions="**/*Test.cs"

MSBuild.exe /t:Rebuild .\BuzzStats.sln
.\coverage.ps1 -project all

SonarQube.Scanner.MSBuild.exe end /d:sonar.login="$Env:SONAR_LOGIN"
