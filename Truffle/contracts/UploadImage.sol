// Specifies the version of Solidity, using semantic versioning.
// Learn more: https://solidity.readthedocs.io/en/v0.5.10/layout-of-source-files.html#pragma
pragma solidity ^0.5.10;

//Author Alex Pearce 913987
//This contract handles image hash storage and upload/downlaod of hashes for verification.
contract UploadImage {

  struct Image {
    bytes32 hash;
    address uploaderAddress;
    string name;
  }

  //List of images
  Image[] images;

  //Adds an image hash to the list
  function addImage(bytes32 hash, string memory name) public
  {
    //Ignore duplicates.
    if (!isAlreadyUploaded(hash)) {
      Image memory image;
      image.uploaderAddress = msg.sender;
      image.hash = hash;
      image.name = name;
      images.push(image);
    }
    else {
      revert();
    }
  }

  // Returns the length of the images list
  function getLength() public view returns (uint)
  {
    return images.length;
  }

  //Returns the number of images the client has uploaded
  function getNumberOfUploadedImages() public view returns(uint) {
    uint count = 0;
    uint i;
    for(i = 0; i < images.length; i++)
    {
      if(images[i].uploaderAddress == msg.sender)
      {
        count++;
      }
    }

    return count;
  }

  //Searches the list of hashed images and returns a list of the hashes
  //the client calling the function has uploaded
  function getImageList() public view returns(bytes32[] memory)
  {
    address addr = msg.sender;
    uint length = getNumberOfUploadedImages();
    bytes32[] memory hashes = new bytes32[](length);

    uint i;
    uint j;
    for (i = 0; i < length; i++) {
      for (j = 0; j < images.length; j++) {
        if (images[j].uploaderAddress == addr) {
          hashes[i] = images[i].hash;
        }
      }
    }
    return hashes;
  }

  //returns the name of a file based on the hash
  function getImageNameFromHash(bytes32 hash) public view returns (string memory) {
    uint length = getNumberOfUploadedImages();
    uint i;
    uint j;
    for (i = 0; i < length; i++) {
      for (j = 0; j < images.length; j++) {
        if (images[j].hash == hash) {
          return images[j].name;
        }
      }
    }
  }

  //Returns true if the image has been uploaded
  function isAlreadyUploaded(bytes32 hash) public view returns (bool) {
    uint length = getNumberOfUploadedImages();
    uint i;
    uint j;
    for (i = 0; i < length; i++) {
      for (j = 0; j < images.length; j++) {
        if (images[j].hash == hash) {
          return true;
        }
      }
    }

   return false;
  }
}
