Write-Host "Deploying Truffle project:"
cd .\Truffle
truffle deploy
Write-Host "Creating java classes from smart contracts:"
cd ..
web3j generate truffle --truffle-json=.\Truffle\build\contracts\UploadImage.json -o .\Java\src\ -p sol
Write-Host "Done"

 