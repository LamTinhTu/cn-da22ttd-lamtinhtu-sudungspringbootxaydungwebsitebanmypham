# Test Twilio SMS Configuration
Write-Host "=== TWILIO SMS TEST ===" -ForegroundColor Cyan
Write-Host ""

# Read Twilio credentials from application-dev.properties
$propsFile = "src\main\resources\application-dev.properties"
if (Test-Path $propsFile) {
    $accountSid = (Select-String -Path $propsFile -Pattern "twilio.account-sid=(.+)" | ForEach-Object { $_.Matches.Groups[1].Value }).Trim()
    $authToken = (Select-String -Path $propsFile -Pattern "twilio.auth-token=(.+)" | ForEach-Object { $_.Matches.Groups[1].Value }).Trim()
    $twilioPhone = (Select-String -Path $propsFile -Pattern "twilio.phone-number=(.+)" | ForEach-Object { $_.Matches.Groups[1].Value }).Trim()
    
    Write-Host "Account SID: $accountSid" -ForegroundColor Yellow
    Write-Host "Auth Token: $($authToken.Substring(0, 8))..." -ForegroundColor Yellow
    Write-Host "Twilio Phone: $twilioPhone" -ForegroundColor Yellow
    Write-Host ""
    
    if ($twilioPhone -eq "+1XXXXXXXXXX") {
        Write-Host "ERROR: Twilio phone number chua duoc cap nhat!" -ForegroundColor Red
        Write-Host "Vui long cap nhat twilio.phone-number trong $propsFile" -ForegroundColor Red
        Write-Host "Lay so tu: https://console.twilio.com/us1/develop/phone-numbers/manage/incoming" -ForegroundColor Yellow
        exit 1
    }
    
    # Test Twilio API connection
    Write-Host "Dang kiem tra ket noi Twilio API..." -ForegroundColor Cyan
    $base64AuthInfo = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("${accountSid}:${authToken}"))
    $headers = @{
        Authorization = "Basic $base64AuthInfo"
    }
    
    try {
        $response = Invoke-RestMethod -Uri "https://api.twilio.com/2010-04-01/Accounts/$accountSid.json" -Headers $headers -Method Get
        Write-Host "Ket noi thanh cong!" -ForegroundColor Green
        Write-Host "Account Status: $($response.status)" -ForegroundColor Green
        Write-Host "Account Type: $($response.type)" -ForegroundColor Green
        Write-Host ""
        
        # Check verified numbers
        Write-Host "Kiem tra so dien thoai da verify..." -ForegroundColor Cyan
        $verifiedResponse = Invoke-RestMethod -Uri "https://api.twilio.com/2010-04-01/Accounts/$accountSid/OutgoingCallerIds.json" -Headers $headers -Method Get
        
        if ($verifiedResponse.outgoing_caller_ids.Count -gt 0) {
            Write-Host "Cac so da verify:" -ForegroundColor Green
            foreach ($caller in $verifiedResponse.outgoing_caller_ids) {
                Write-Host "  - $($caller.phone_number)" -ForegroundColor Green
            }
            
            if ($verifiedResponse.outgoing_caller_ids.phone_number -notcontains "+84339626863") {
                Write-Host ""
                Write-Host "CANH BAO: So +84339626863 CHUA duoc verify!" -ForegroundColor Yellow
                Write-Host "Vui long verify tai: https://console.twilio.com/us1/develop/phone-numbers/manage/verified" -ForegroundColor Yellow
            }
        } else {
            Write-Host "Chua co so dien thoai nao duoc verify!" -ForegroundColor Yellow
            Write-Host "Vui long verify so +84339626863 tai:" -ForegroundColor Yellow
            Write-Host "https://console.twilio.com/us1/develop/phone-numbers/manage/verified" -ForegroundColor Cyan
        }
        
    } catch {
        Write-Host "LOI: Khong the ket noi Twilio API!" -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor Red
        Write-Host ""
        Write-Host "Vui long kiem tra:" -ForegroundColor Yellow
        Write-Host "1. Account SID va Auth Token co dung khong?" -ForegroundColor Yellow
        Write-Host "2. Lay tai: https://console.twilio.com/us1/account/keys-credentials/api-keys" -ForegroundColor Cyan
    }
    
} else {
    Write-Host "ERROR: Khong tim thay file $propsFile" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== END TEST ===" -ForegroundColor Cyan
