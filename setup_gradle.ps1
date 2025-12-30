$gradleVersion = "8.5"
$gradleUrl = "https://services.gradle.org/distributions/gradle-$gradleVersion-bin.zip"
$zipFile = "gradle-$gradleVersion-bin.zip"
$installDir = ".gradle_local"

Write-Host "Downloading Gradle $gradleVersion..."
Invoke-WebRequest -Uri $gradleUrl -OutFile $zipFile

Write-Host "Extracting Gradle..."
Expand-Archive -Path $zipFile -DestinationPath $installDir -Force

$gradleBin = "$PWD\$installDir\gradle-$gradleVersion\bin\gradle.bat"

Write-Host "Generating Gradle Wrapper..."
& $gradleBin wrapper --gradle-version $gradleVersion

Write-Host "Gradle Wrapper installed successfully!"
Remove-Item $zipFile
Remove-Item $installDir -Recurse -Force
