Write-Host "Deploying Truffle project:"
cd .\Truffle
truffle deploy
Write-Host "Creating java classes from smart contracts:"
cd ..
web3j truffle generate .\Truffle\build\contracts\MetaCoin.json -o .\Java\src\ -p sol
Write-Host "Done"

 