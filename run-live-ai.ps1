$secureKey = Read-Host "Enter the replacement AI provider key" -AsSecureString
$pointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureKey)
try {
    $existing = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
    if ($existing) {
        Stop-Process -Id $existing.OwningProcess -Force
    }
    $env:AI_PROVIDER_ENABLED = "true"
    $env:AI_PROVIDER_API_KEY = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($pointer)
    $env:AI_PROVIDER_BASE_URL = "https://api-ap-southeast-1.modelarts-maas.com/openai/v1"
    $env:AI_PROVIDER_MODEL = "glm-5.1"
    Push-Location "$PSScriptRoot\backend"
    mvn spring-boot:run
} finally {
    if ($pointer -ne [IntPtr]::Zero) {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($pointer)
    }
    Remove-Item Env:AI_PROVIDER_API_KEY -ErrorAction SilentlyContinue
    Pop-Location
}
