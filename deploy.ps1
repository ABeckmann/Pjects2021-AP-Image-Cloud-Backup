Write-Host "Deploying Truffle project:"
cd .\Truffle
truffle deploy
Write-Host "Creating java classes from smart contracts:"
cd ..
web3j truffle generate .\Truffle\build\contracts\MetaCoin.json -o .\Java\src\ -p sol
web3j truffle generate .\Truffle\build\contracts\ConvertLib.json -o .\Java\src\ -p sol
web3j truffle generate .\Truffle\build\contracts\Migrations.json -o .\Java\src\ -p sol
Write-Host "Done"

 