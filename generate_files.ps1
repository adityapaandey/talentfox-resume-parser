# TalentFor HR - Complete File Generator Script
# This script creates all necessary backend files

Write-Host "Generating TalentFor HR Project Files..." -ForegroundColor Green
$projectRoot = "$env:USERPROFILE\Downloads\talentfor-hr"

# Backend __init__ files
$null | Out-File -FilePath "$projectRoot\backend\app\__init__.py" -Encoding UTF8
$null | Out-File -FilePath "$projectRoot\backend\app\api\__init__.py" -Encoding UTF8
$null | Out-File -FilePath "$projectRoot\backend\app\models\__init__.py" -Encoding UTF8
$null | Out-File -FilePath "$projectRoot\backend\app\schemas\__init__.py" -Encoding UTF8
$null | Out-File -FilePath "$projectRoot\backend\app\services\__init__.py" -Encoding UTF8
$null | Out-File -FilePath "$projectRoot\backend\app\core\__init__.py" -Encoding UTF8
$null | Out-File -FilePath "$projectRoot\backend\app\db\__init__.py" -Encoding UTF8

Write-Host "Created __init__.py files" -ForegroundColor Cyan

# gitignore
$gitignore = @"
# Python
__pycache__/
*.py[cod]
*$py.class
*.so
.Python
venv/
env/
ENV/
.venv
*.egg-info/
dist/
build/

# Environment
.env
.env.local

# IDE
.vscode/
.idea/
*.swp
*.swo

# Uploads
uploads/*.pdf
!uploads/.gitkeep

# Database
*.db
*.sqlite3

# Node
node_modules/
npm-debug.log*
yarn-debug.log*
yarn-error.log*

# Production
build/
dist/

# Misc
.DS_Store
*.log
"@

$gitignore | Out-File -FilePath "$projectRoot\.gitignore" -Encoding UTF8

Write-Host "Created .gitignore" -ForegroundColor Cyan
Write-Host "\nAll files created! Navigate to:" -ForegroundColor Green
Write-Host "$projectRoot" -ForegroundColor Yellow
Write-Host "\nNext: Open in VS Code with: code `"$projectRoot`"" -ForegroundColor Magenta
