$SRC=$Env:USERPROFILE
$DST="E:\"

function CopyAnyDir
{
  param([string]$SourceFolder, [string]$DestinationFolder)

  robocopy "$SourceFolder" "$DestinationFolder" `
    /S /COPY:DT /DCOPY:T /R:5 /W:5 /PURGE /XJ `
    /XD node_modules `
    /XF Thumbs.db desktop.ini
}

function CopyDir
{
  param([string]$Folder)
  CopyAnyDir "$SRC\$Folder" "$DST\$Folder"
}

CopyDir ".gnupg"
CopyDir ".ssh"
CopyDir "AndroidStudioProjects"
CopyDir "Archive"
CopyDir "Documents"
CopyDir "DOSBOX"
CopyDir "Google Drive"
CopyDir "Music"

$items = Get-ChildItem $SRC | Where-Object {$_.name -like "OneDrive*" }
foreach ($item in $items)
{
  CopyDir $item.Name
}

$items = Get-ChildItem $SRC | Where-Object {$_.name -like ".Idea*" }
foreach ($item in $items)
{
  CopyDir $item.Name
}

CopyDir "Pictures"
CopyDir "Projects"
CopyDir "vbox"
CopyDir "Videos"

robocopy "$SRC" "$DST" ".bash_aliases" ".bash_profile" ".bashrc" ".vimrc"

CopyAnyDir "C:\Windows\Fonts" "$DST\Fonts"
CopyAnyDir "C:\opt" "$DST\opt"
CopyAnyDir "C:\Users\ngeor\AppData\Roaming\Code\User" "$DST\VSCode"
