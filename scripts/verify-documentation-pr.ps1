$ErrorActionPreference = 'Stop'

$stagedFiles = git diff --cached --name-only
$runtimeFiles = $stagedFiles | Where-Object { $_ -like 'src/*' -or $_ -like 'bin/*' -or $_ -like 'target/*' }

if ($runtimeFiles) {
    Write-Error "Runtime or build files are staged: $($runtimeFiles -join ', ')"
}

Write-Output 'Documentation-only staged file check passed.'
